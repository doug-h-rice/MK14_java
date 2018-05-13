/*
	File: AnimateMK14.java - Science Of Cambridge MK-14 emulator written in Java.

	Doug Rice May 2018, using C code by Paul Robson and others.

	
References:	
	http://www.java2s.com/Code/Java/2D-Graphics-GUI/HowtocreateAnimationPaintandthread.htm
	test to get timer, display and keyboard in preparation of a Java MK14 emulator.
	
	http://ccgi.dougrice.plus.com/cgi-bin/wiki.pl?MK14_Notes
	http://www.dougrice.plus.com/dev/seg_mk14.htm
	https://github.com/doug-h-rice
	
References:	
	http://s400081762.websitehome.co.uk/paulRobson/software.htm
	http://www.techlib.com/area_50/Readers/Karen/micro.htm#PIC14

Why?: 
	- I want an MK-14 emulator, as it was my first computer 40 years ago.
	- Not many other emulators expect you to enter HEX machine code using a calculator keyboard.
	
	- Try out Java2D programming.
	- try and work towards an Android App.

	
Files:
	Rom.java 			- contains SCIOS ROM code
	Cpu.java 			- contains CPU code ported form Paul Robson's code. JMP bug fixed.
	AnimateMK14.java	- Contains Main() plus Display and keyboard. 


	Line 83:  SEC: x.x imports
	Line 112: SEC: x.x Display
	Line 282: SEC: x.x User code eample - to save typing it in each time.
	Line 338: SEC: x.x main Object
	
	
Usage:
	build:
		javac Rom.java Cpu.java AnimateMK14.java
			
		"C:\Program Files\Java\jdk1.8.0_66\bin\javac"  Rom.java Cpu.java AnimateMK14.java -Xlint:deprecation
	run:
		java AnimateMK14 &
		

This Ports Paul Robson's CPU code.
I have rewritten the keyboard and display based on
http://www.java2s.com/Code/Java/2D-Graphics-GUI/HowtocreateAnimationPaintandthread.htm
 		
This emulator uses buffered keyboard and display writes.		
		
The MK-14 	used the SC/MP processor.
It had:
ROM:	512 byte monitor 
RAM: 	256 byte 
Display :- a 7 segment calulator LED display, a
Keybaord:	20 button keyboard before calutor keyboards used molded rubber and carbon pads.  

The memory map ( http://mymk14.co.uk/paulRobson/emulator.htm )

Science of Cambridge MK14 Memory Map 

Only the first 12 bits of the program counter are decoded 
000-1FF 	512 byte SCIOS ROM 	Decoded by 0xxx 
200-3FF 	ROM Shadow / Expansion RAM 	
400-5FF 	ROM Shadow / Expansion RAM 
600-7FF 	ROM Shadow / Expansion RAM 
800-87F 	128 bytes I/O chip RAM Decoded by 1xx0 
880-8FF 	I/O Ports Decoded by 1xx0 
900-9FF 	Keyboard & Display 	Decoded by 1x01 
A00-AFF 	I/O Port & RAM Shadow 
B00-BFF 	256 bytes RAM (Extended) / VDU RAM Decoded by 1011 
C00-CFF 	I/O Port & RAM Shadow 
D00-DFF 	Keyboard & Display Shadow 
E00-EFF 	I/O Port & RAM Shadow 
F00-FFF 	256 bytes RAM (Standard) / VDU RAM 	Decoded by 1111 

Keyboard Decoding 
On a READ the switch levels are read onto the bus via IC11 into bits D4-D7 
On a WRITE the bits D0-D7 are latched (sort of !) via IC9 and IC10 into the video display latches (D0=Segment A,D7 = Decimal Point) 
On any Access the lower 4 address bits are latched into IC12. These are decoded by IC13, dragging the appropriate line low to light the segments selected via IC9 and IC10 
IC11 is normally read as 1111xxxx - if a key is pressed when the corresponding line is pulled low via IC13 then bits will be read as zero - this indicates a key is being pressed. 
RAM Expansion 

	
		
*/ 

/*
====================================================================
SEC: x.x imports
=====================================================================
*/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import java.awt.geom.Line2D;
import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.geom.*;

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;




/*
====================================================================
SEC: x.x Display
=====================================================================
*/
/* put memory in Cpu */
/*


Memory has the: 
	ROM, 
	Memory Mapped I/O
	RAM
	
	The CPU addresses Memory.
	
	
class Memory {

	byte mem[] = new byte[4096];

	public byte read( int addr ){
		return mem[ addr ] ;
	};

	public void write( int addr , byte data ){
		mem[ addr ] = data;
	};

}
*/

/*
* we need an object for the display and keyboard
*
* The MK14 has a address ranges which write and read to the hardware.
* The host PC's keyboard and display need an interface as well.
*
* Some dual port memory is required.
* When the MK-14 writes to the display addresses store the segments data
*
* When the MK-14 reads from the  display addresses if a key is presseed the corresponding bits are stored.
* we need an array to track key presses when teh PS'c keyboard is pressed.
*
*/
class Display {
	
	byte dispMemWrite[] = new byte[16];
	byte dispMemRead[]  = new byte[16];
	
	int want;

	public int read( int addr ){
		/* map pressed keys to this array */
		return dispMemRead[ addr ] ;
//		return dispMemWrite[ addr ] ;
		
	};

	public void write( int addr , int data ){
		dispMemWrite[ addr ] = ( byte ) data;
	};

	public void setbit( int addr , int bit ){
		//dispMemWrite[ addr ] |= 1<< bit;
		dispMemRead[  addr ]   |= 1 << bit;
	};

	
	public void clearBit( int addr , int bit ){
		//dispMemWrite[ addr ] &= ~(1<< bit) ;
		dispMemRead[  addr ] &= ~(1<< bit) ;
	};

	
	public void addr_bit( int addr , int bit ){
		//dispMemWrite[ addr ] &= ~(1<< bit) ;
		//dispMemRead[  addr ] &= ~(1<< bit) ;
		if ( want ==1 ){	
			dispMemRead[  addr ]   |= 1 << bit;
		} else {
			dispMemRead[  addr ] &= ~(1<< bit) ;
		}
	};

	
	public byte read_pc( int addr ){
		/* map pressed keys to this array */
		return dispMemWrite[ addr ] ;
	};

	public void write_pc( int addr , byte data ){
		dispMemRead[ addr ] = data;
	};

	
	public void mapKey( int key , int action ){
		want = action;
/*
  * The Keyboard is also a challenge.
  * G = GO
  * M = MEM
  * T = TERM
  * Z = ABORT
  * Q or  / quites emulator 
  *  
  * G = GO		- run from displayed address
  * M = MEM 	- increment address and allow hex input
  * T = TERM 	- allow hex input
  * Z = ABORT 	- allow address input
  * 
  * R resets emulator 
  * 
  * =================================
  * Memory map 0D07 on left, 0D00 is on right
  * =================================
  * 0D00+   7  6  5  4  3  2  1  0
  * =================================
  * bit7    7  6  5  4  3  2  1  0
  * bit6                      9  8
  * bit5    T        Z  M  G
  * bit4    F  E        D  C  B  A
  * =================================
  * 0D00+   8  7  5  4  3  2  1  0
  * =================================
  *

*/	  
		
			/* 0..7 */
			if ( key == 48 ){ addr_bit( 0 , 7 );};		
			if ( key == 49 ){ addr_bit( 1 , 7 );};		
			if ( key == 50 ){ addr_bit( 2 , 7 );};		
			if ( key == 51 ){ addr_bit( 3 , 7 );};		
			if ( key == 52 ){ addr_bit( 4 , 7 );};		
			if ( key == 53 ){ addr_bit( 5 , 7 );};		
			if ( key == 54 ){ addr_bit( 6 , 7 );};		
			if ( key == 55 ){ addr_bit( 7 , 7 );};		

			/* 8..9 */
			if ( key == 56 ){ addr_bit( 0 , 6 );};		
			if ( key == 57 ){ addr_bit( 1 , 6 );};		
						
			/* G = 71	GO, 	M = 77  MEM,	T = 84	TERM, A = 65  ABORT , Q = 81  ABORT , Z = 90  ABORT */
			/* GO,MEM,TERM,ABORT */
			if ( key == 71 ){ addr_bit( 2 , 5 );};	// GO	- G
			if ( key == 77 ){ addr_bit( 3 , 5 );};	// MEM	- M
			if ( key == 84 ){ addr_bit( 7 , 5 );};	// TERM - T

			//if ( key == 65 ){ addr_bit( 4 , 5 );};	// ABORT - A 
			if ( key == 81 ){ addr_bit( 4 , 5 );};	// ABORT - Q
			if ( key == 90 ){ addr_bit( 4 , 5 );};	// ABORT - Z

			/* A..F */
			if ( key == 65 ){ addr_bit( 0 , 4 );};	// A
			if ( key == 66 ){ addr_bit( 1 , 4 );};	// B
			if ( key == 67 ){ addr_bit( 2 , 4 );};	// C
			if ( key == 68 ){ addr_bit( 3 , 4 );};	// D
			
			if ( key == 69 ){ addr_bit( 6 , 4 );};	// E
			if ( key == 70 ){ addr_bit( 7 , 4 );};	// F			

			/* Reset */
			/* addr_bit() sets resets bit */			
	};

}



/*
====================================================================
SEC: x.x User code eample - to save typing it in each time.
=====================================================================
*/
class Code {

int Code[] =
{/*
 *	 
 * Simple user program that set the FLAG LEDs
 * run from 0FE0
 */
 
0xC4,0x07,
0x07,
0x3f,

0xC4,0x06,
0x07,
0x3f,


0xC4,0x04,
0x07,
0x3f,


0xC4,0x00,
0x07,
0x3f,

0xC4,0x01,
0x07,
0x3f,

0xC4,0x03,
0x07,
0x3f,


0xC4,0x07,
0x07,
0x3f,

/* go back to 0F90 , by swaping P[0].low */
0xC4,0xCF,	
0x30,		/* XPAL 0 */

0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

};
}

/*
====================================================================
SEC: x.x main Object
=====================================================================
*/



public class AnimateMK14 extends JFrame  {

  private static int DELAY = 100;

  int count = 100;

  private int key = 0; 	
  private int key2 = 0; 	
 
 
  /* object for MK-14 */
  
  Display 	disp = new Display();
  Cpu 		cpu  = new Cpu();
  Rom		rom  = new Rom();  // Rom is loaded into cpu.Memory for now

  Code		code  = new Code();  // User program - for debugging
  
  Insets insets;

  /* http://www.java2s.com/Code/Java/2D-Graphics-GUI/HowtocreateAnimationPaintandthread.htm */

/*  
  Color colors[] = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
      Color.BLUE, Color.MAGENTA };
*/
      private class TAdapter extends KeyAdapter {
	  
/*
https://docs.oracle.com/javase/tutorial/uiswing/events/keylistener.html
*/
        @Override
        public void keyPressed(KeyEvent e) {
            // int 
			key = e.getKeyCode();
			key2 = '_';

			disp.mapKey(  key ,  0 );
			/* Reset */
			if ( key == 'R' ){ cpu.ResetCPU(  );   }	// RESET		
			if ( key == 'P' ){ 
			  //cpu.ResetCPU(  );   	// RESET		
	          System.out.println();	
			  for ( int count = 0x0F00 ; count < 4096 ; count++ ){
	            if ( ( count % 24 ) == 0 ){
	              System.out.println();	
  	              System.out.format("%04X : ", count  );
                }
	            System.out.format("%02X ", cpu.Memory[ count ] );
			  }	
	        }


			
		}
	  
        @Override
        public void keyReleased(KeyEvent e) {
            // int 
			key = e.getKeyCode();
			key2 = '/';			
			disp.mapKey(  key ,  1 );
			/* Reset */
			if ( key == 'R' ){ cpu.ResetCPU(  );   };	// RESET			
		}
    }	  
	  
  public void paint7seg(Graphics g, int offset, int val ) {

    /* run a digit timer to  impliment latency */
    if ( cpu.dispMemWriteTmr[ 9-offset] > 0 ){
		cpu.dispMemWriteTmr[ 9-offset] += -1;
		
   
    if (insets == null) {
      insets = getInsets();
    }
    // Calculate each time in case of resize
    int x = insets.left;
    int y = insets.top;
  
	int top    = 10+y;
	int middle = 30+y;
	int bottom = 50+y;
	
	int left   = 10+offset*35+x;
	int right  = 30+offset*35+x;
	
	//java.awt.Graphics.drawLine(int x1, int y1, int x2, int y2);
	// Segment map:-
	//    a
	//  f    b
	//    g
	//  e   c
	//    d
	//        h

	Graphics2D g2 = (Graphics2D)g;
	g2.setStroke(new BasicStroke(2.0f));
	g2.setColor(Color.RED);
	
    // conditionally draw each line of the 7 segment display	
	if ( (val & 1 )  >0 ) {	g2.drawLine(left  , top    , right , top    ); /* a */ }
	if ( (val & 64 ) >0 ) {	g2.drawLine(left  , middle , right , middle ); /* g */ }
	if ( (val & 8 )  >0 ) { g2.drawLine(left  , bottom , right , bottom ); /* d */ }
	
	if ( (val & 2 )  >0 ) { g2.drawLine(right , top    , right , middle ); /* b */ }
	if ( (val & 4 )  >0 ) { g2.drawLine(right , middle , right , bottom ); /* c */ }

	if ( (val & 32 ) >0 ) { g2.drawLine(left  , top    , left  , middle ); /* f */ }	
	if ( (val & 16 ) >0 ) { g2.drawLine(left  , middle , left  , bottom ); /* e */ }
	// dp
	if ( (val & 128 ) >0 ) {  
	    // draw an x for now
		g2.drawLine(right+2  , bottom+2 , right+4 , bottom+4 ); //h
		g2.drawLine(right+4  , bottom+2 , right+2 , bottom+4 ); //h
	}
	}
};	
 
	  
  public void paint(Graphics g) {
    super.paint(g);
    if (insets == null) {
      insets = getInsets();
    }
    // Calculate each time in case of resize
    int x = insets.left;
    int y = insets.top;

    int width  = getWidth()  - insets.left - insets.right;
    int height = getHeight() - insets.top  - insets.bottom;

/*	
    int start = 0;
    int steps = colors.length;
    int stepSize = 360 / steps;

	//int count;

*/	
	setBackground(Color.BLACK);

	  
	  g.setColor(Color.BLACK);
      // do something to test keyboard presses.	  
	  g.drawString("Flags - F2,F1,F0", 10+x, 70+y );  

	  g.drawString(
	  "Keys: M-Mem,  T-Term,  G-Go,  ZQ-Abort, 0..9,  A..F,  R-Reset ,P - dump " +
	  //"key: "+ key + " : " +'A' +" : " + (char) key+" : " + (char) key2 + " : " + 
	  String.format( "%c" , key ) + 
	  //":" + cpu.Ptr[ 0 ] + ":" + cpu.Acc + 
	  //":" + cpu.Ext + ":" + cpu.Stat  
	  "" , 10+x, 95+y ) ;
	
//    synchronized (colors) {
	{
      for ( int count = 0 ; count < 10 ; count++ ){
		disp.dispMemWrite[ count ] = cpu.dispMemWrite[ count ];
		cpu.dispMemRead[   count ] = (byte)( 0x0 ^ disp.dispMemRead[ count ] );
	  }

	  /* debug suggests 10 columns are scanned, but only 8 digits are equipped */
	  //paint7seg( g, 0, disp.read_pc(9) ); // no LED present on real device
	  paint7seg( g, 1, disp.read_pc(8) );	// no LED present on real device
	  paint7seg( g, 2, disp.read_pc(7) );
	  paint7seg( g, 3, disp.read_pc(6) );
	  paint7seg( g, 4, disp.read_pc(5) );
	  paint7seg( g, 5, disp.read_pc(4) );
	  paint7seg( g, 6, disp.read_pc(3) );
	  paint7seg( g, 7, disp.read_pc(2) );
	  paint7seg( g, 8, disp.read_pc(1) );
	  paint7seg( g, 9, disp.read_pc(0) );
	  
	  // paint7seg( g, 11, count );
	  
/*
The status register provides storage for arithmetic, control and software status flags. There are 3 'output' flags which connect directly to pins on the IC, called F0,F1 and F2. There are two input flags , Sense A and Sense B, also connecting to the IC. Sense A has a dual function, as the interrupt line. The other 3 flags are system status flags

Bit 	Function 	Notes
0 	F0 	Output Line
1 	F1 	Output Line
2 	F2 	Output Line
3 	IE 	Interrupt Enable (set to 1 when enabled)
4 	SA 	Input Line, causes interrupt when SA = 1 and IE = 1
5 	SB 	Input Line
6 	OV 	Overflow on Add, or Complement and Add instructions
7 	CY/L 	Carry / Link bit
*/

/* CY/L , OV, SB, SA,IE, F2, F1, f0 */

	  int w = 10;
	  int h = 10;
	  
	  int x2= 110 + x;
	  int y2= 60 + y;
	  

	  if ( ( cpu.Stat & 4 ) == 0 ) {  g.drawOval(x2,y2,w,h); } else { g.fillOval(x2,y2,w,h); };
	  x2 += 15;	
	  if ( ( cpu.Stat & 2 ) == 0 ) {  g.drawOval(x2,y2,w,h); } else { g.fillOval(x2,y2,w,h); };
	  x2 += 15;
	  if ( ( cpu.Stat & 1 ) == 0 ) {  g.drawOval(x2,y2,w,h); } else { g.fillOval(x2,y2,w,h); };
  

	  //Integer.valueOf(i).toString(). 
	  //Normal ways would be Integer.toString(i) or String.valueOf(i).
	}
  }

  public void go() {
	/*
	* Load ROM - 
	*	I have the original ---- -- rom 
	*	and the updates     0000 00 rom 
	*
	*/
	
	for( int count = 0; count < 512 ; count++){	  
	  cpu.Memory[ count  ] = (byte) rom.readROM( count , 1 ) ; // use 1 for  0000 00 ROM, use 0 for ---- -- ROM 
	}
	
	/* load 32 bytes of user's code */
	for( int count = 0; count < 32 ; count++){	  
	  cpu.Memory[ count +0x0FD0 ] = (byte) code.Code[ count ] ; 
	}
	
	for ( int count = 0 ; count < 10 ; count++ ){
	  //disp.write_pc( count , cpu.dispMemWrite[ count ] );
	  disp.dispMemWrite[ count ] = (byte) 0xff;
	  disp.dispMemRead[  count ] = (byte) 0xff;
	  cpu.dispMemWriteTmr[   count ] = 2;

	}

	
	//cpu.load_both_formats( "Clock.hex" );

    // add key	
	addKeyListener( new TAdapter() );

    cpu.ResetCPU();
	
    TimerTask task = new TimerTask() {

	public void run() {

/*	
	Color c = colors[0];
      synchronized (colors) {
        System.arraycopy(colors, 1, colors, 0, colors.length - 1);
        colors[colors.length - 1] = c;
      }
*/
		count ++;
        repaint();
		cpu.Execute( 2500 );
	   }
    };
	
    Timer timer = new Timer();
    timer.schedule(task, 0, DELAY);
  }

  public static void main(String args[]) {

	System.out.println("======================================================================");
	System.out.println( "MK-14 emulator");
	System.out.println("======================================================================");
	System.out.println();
	System.out.println( "java AnimateMK14 file " );
	System.out.println( "loads hex file in .ihx or .nas file format " );
	System.out.println( ".ihx    :180FDC00F6C40135C20031C100CA00C20131C100CA01C4FFCA0F925D84 " );
	System.out.println( ".nas    0F58 00 00 00 00 00 00 00 00 00 " );
	System.out.println();
	System.out.println( "Keys: M-Mem,  T-Term,  G-Go,  ZQ-Abort, 0..9,  A..F " );
	System.out.println( "Keys: R-Reset, P- dump memory  " );
	System.out.println();
	System.out.println("======================================================================");
	
	
/*	  
    Animate f = new Animate();
    f.setSize(500, 200);
    // f.show();
	f.setVisible(true);
    f.go();
*/

    AnimateMK14 f2 = new AnimateMK14();

	if ( args.length > 0 ){
      System.out.println( args.length+ " | " + args[0] ); 
	  f2.cpu.load_both_formats( args[0] );
	}
	
	f2.setTitle("Science Of Cambridge MK-14 emulator ");
    f2.setSize(450, 170);
    //f2.show();
	f2.setVisible(true);
    f2.go();
  }	
}
