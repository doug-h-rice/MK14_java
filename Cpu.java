/*
	File: Cpu.java see AnimateMK14.java - Science Of Cambridge MK-14 emulator written in Java.
	Doug Rice 2018, using C code by Paul Robson and others.
*/
  
import java.io.BufferedReader;
import java.io.FileReader;
//import java.io.IOException;
import java.io.*;

import java.lang.Integer ;

class Cpu {
	public byte dispMemWrite[] = new byte[16];
	public byte dispMemRead[]  = new byte[16];	
	public int lastDigit      = 0;	
	public byte dispMemWriteTmr[] = new byte[16];

	public byte[]  Memory = new byte[4096];				/* SC/MP Program Memory */

	
	
	/********************************************************************/
	/*	Load Memory from file functions    			  			        */
	/********************************************************************/

	/*
	 * .ihx format and .hex format
	:180FDC00F6C40135C20031C100CA00C20131C100CA01C4FFCA0F925D84
	:00000001FF
	 * 
	 *
	:len addr 00 xx xx xx .. check                                        
	:18  0FDC 00 F6 C4 01 35 C2 00    31 C1 00 CA 00 C2    01 31 C1 00 CA 01      C4 FF CA 0F 92 5D 84
	:18  0FDC 00 F6C40135C20031C100CA00C20131C100CA01C4FFCA0F925D84
	:0E  100E 00 6C6C6F20646F75670A00DDE5DD21 F4 
	 *  
	 *  *.nas format   addr datax8 00 BS BS  e.g. 
	0F58 00 00 00 00 00 00 00 00 00
	 *
	 */

    void hex_ihx( String s2 ) {

	  int i = 0;
	  int len, addr,	data,	check,count;    

      try  {
	    String tag = s2.substring( i , i+1 );
        if ( tag.equals( ":" ) ){ // get :
	      i +=1;
          len    = Integer.parseInt( s2.substring( i , i+2 ), 16 );      i += 2;
          addr   = Integer.parseInt( s2.substring( i , i+4 ), 16 );      i += 4;
          int tp = Integer.parseInt( s2.substring( i , i+2 ), 16 );      i += 2;
          //	  System.out.print( len + " | " + addr + " | " + tp + " | " );
	      for ( count = 0 ; count < len ; count++ ){	  
	        data =  Integer.parseInt( s2.substring( i  , i+2 ), 16 ) ;   i += 2;
	        Memory[ addr + count  ]  = ( byte )data;
	      }
	      check  = Integer.parseInt( s2.substring( i  , i+2 ), 16 );     i += 2;
	      } else {
	        /* *.nas format   addr datax8 00 BS BS  e.g. */
            /*0F58 00 00 00 00 00 00 00 00 00*/
            addr   = Integer.parseInt( s2.substring( i  , i+4 ), 16 );     i += 5;
	        for ( count = 0 ; count < 8 ; count++ ){	  
	        data =  Integer.parseInt( s2.substring( i  , i+2 ), 16 ) ; 	 i += 3;
	        Memory[ addr + count  ]  = ( byte )data;
	      }
	      check  = Integer.parseInt( s2.substring( i  , i+2 ), 16 );     i += 2;
        }
	    } catch ( NumberFormatException e)               { System.out.println("error NumberFormatException in " + s2 ); // e.printStackTrace();		//return -1;	
	    } catch ( StringIndexOutOfBoundsException siobe) { System.out.println("error StringIndexOutOfBoundsException in " + s2 ); // e.printStackTrace();		//return -1;	
	    }
  }
  
  //int load_both_formats(char *file);
  public int load_both_formats( String filename ){
	try  {
      FileReader fr     = new FileReader( filename );
      BufferedReader br = new BufferedReader(fr);
      String s;	
      while ((s = br.readLine()) != null) {		
        System.out.println(s);
	    hex_ihx( s  );
      }
      fr.close();	
	  for ( int count = 0x0F00 ; count < 4096 ; count++){
	    if ( ( count % 24 ) == 0 ){
	      System.out.println();	
  	      System.out.format("%04X : ", count  );
        }		
	    System.out.format("%02X ", this.Memory[ count ] );
	  }
	} catch (IOException i) { 			    //i.printStackTrace();		//return -1;
	  System.out.println("File name not known : " );
	  return -1;
	}
	System.out.println(" end of file load :" );
	return 0;
  };

	

	/********************************************************************/
	/*																	*/
	/*						Portable MK14 emulator in 'C'				*/
	/*																	*/
	/*								CPU Emulator						*/
	/*																	*/
	/*	                    Ported to Jave by Doug Rice 2018			*/
	/*																	*/
	/********************************************************************/

	//#include "scmp.h"

	int ReadMemory(int Address)
	{
		int n = Address & 0x0F00;
		if (n == 0x900 || n == 0xD00)			/* Handle I/O at 900 or D00 */
		{
			n = Address & 0x0F;					/* Digit select latch value */
			//SetDigitLatch(n,0);
			/* Digit select latch changed */
			//return(  n < 8 ? KeyStatus[n]:0xFF);	/* 0..7 are keys,8..F return FF */
			//System.out.println( "keyboard n:"+ n +" :"+Integer.toHexString( dispMemRead[ n ] & 0xFF  ) ); 
			return ( dispMemRead[ n ] & 0xFF );
		}
		else									/* Just return memory contents */
		return(  Memory[Address & 0xFFF] & 0xFF );

	}

/********************************************************************/
/*						Write a memory location						*/
/********************************************************************/

	void WriteMemory(int Address,int Data)
	{
		
		int n = Address & 0x0F00;				/* Find out which page */
		if (n == 0x900 || n == 0xD00)			/* Writing to I/O */
		{
		//SegmentLatch = Data;				/* Update segment latch */
			n = Address & 0xF;
			SetDigitLatch(n,1);					/* and digit latch */
			dispMemWrite[ n ] = ( byte ) Data ;
			//System.out.println( "display n:"+ n +" :"+Data ); 
		}
		else                                    /* Writing to memory - check it */
		{									/* isn't ROM */
			if (n >= 0x200)						/* Changed for expansion RAM */
				Memory[Address & 0xFFF] = ( byte ) Data;
		}		
}


/********************************************************************/
/*						Update the digit latch						*/
/********************************************************************/

void SetDigitLatch(int n,int Write)
{
	lastDigit = n;
	dispMemWriteTmr[ n ] = 4;
}

/********************************************************************/
/*					Latency Check / Keyboard Scan					*/
/********************************************************************/
	
/* This does the 12 bit ptr add. Basically there is no carry from bit   */
/* 11 into bit 12, so bits 12..15 are always unchanged					*/

//#define ADD12(Ptr,Ofs)	((((Ptr)+(Ofs)) & 0xFFF) | ((Ptr) & 0xF000))
//#define 
	int ADD12(int Ptr,int Ofs){
		return ( ( ( (Ptr) + (Ofs) ) & 0x0FFF) | ((Ptr) & 0xF000));
	}

/* There are two fetches. The second is more accurate but slower... the */
/* first doesn't increment the PC correctly (12 bit fashion). In		*/
/* practice the faster one is functionally equivalent					*/

//#define FETCH(Tgt) Tgt = Memory[(++Ptr[0]) & 0xFFF]

  int FETCH(  ){
	  Ptr[0]++;
	  return  Memory[ Ptr[0] & 0x0FFF ] & 0x00ff;
  }
  
  int FETCH2(  ){  
	  return  ( Memory[  ( Ptr[0]+1 ) & 0x0FFF ] ) & 0x00ff;
  }

  
/* #define FETCH(Tgt) { Ptr[0] = ADD12(Ptr[0],1);Tgt = Memory[Ptr[0]]; } */

	int Acc  = 0 ;
	int Ext  = 0 ;
	int Stat = 0 ;						/* SC/MP CPU Registers */
//	int Ptr[4];
	int Ptr[] = new int[4];
	long Cycles;							/* Cycle Count */

/*	
	static int Indexed(int);                
	
	/* Local prototypes */
/*
	static int AutoIndexed(int);
	static int BinAdd(int,int);
	static int DecAdd(int,int);
*/




	


	/********************************************************************/
	/*							Reset the CPU							*/
	/********************************************************************/

	void ResetCPU( )
	{
		Acc = Ext = Stat = 0;					/* Zero all registers */
		Cycles = 0L;
		Ptr[0] = Ptr[1] = Ptr[2] = Ptr[3] = 0;
	}

	/********************************************************************/
	/*					  Execute a block of code						*/
	/********************************************************************/

	//#define CYCLIMIT	(10000L)
    int CYCLIMIT	 = 10000;
	
	void BlockExecute()
	{
		Execute(8192);							/* Do opcodes until cyclimit */

		//if (CONKeyPressed(KEY_RESET))			/* Check for CPU Reset */
		//					ResetCPU();
	}

	/********************************************************************/
	/*			Execute a given number of instructions					*/
	/********************************************************************/

	//#define CYC(n)	Cycles+= (long)(n)		/* Bump the cycle counter */

	void CYC( int n ){
	  Cycles+= (n);
	}
	
											/* Shorthand for multiple case */
	//#define CAS4(n) case n: case n+1: case n+2: case n+3
	//#define CAS3(n) case n: case n+1: case n+2

	//#define CM(n)	((n) ^ 0xFF)      		/* 1's complement */

	int CM( int n ){
		return ((n) ^ 0xFF)&0xFF;
	}
	
	
	void Execute(int Count)
	{
//	register int Opcode;
//	register int Pointer;
	int Opcode;
	int Opcode2;
	
	int Pointer;
	int n;
	long l;
	
	while (Count-- > 0)
		{
		while (Cycles > CYCLIMIT)			/* Check for cycle limit */
			{
			//Latency();
			Cycles = Cycles - CYCLIMIT;
			//CONSynchronise(CYCLIMIT);
			}

		Opcode   = FETCH() &0x0FF;					/* Fetch the opcode, hack of the */
		Opcode2  = FETCH2()&0x0FF;					/* Fetch the opcode, hack of the */
		Pointer = Opcode & 3;				/* pointer reference */

		
if ( ( Ptr[0] > 0x11A7 ) ){
		System.out.println( 
		Count + 
		" Ptr[0]:" + Integer.toHexString( Ptr[0] ) +
		" Ptr[1]:" + Integer.toHexString( Ptr[1] ) +
		" Ptr[2]:" + Integer.toHexString( Ptr[2] ) +
		" Ptr[3]:" + Integer.toHexString( Ptr[3] ) +
		" op:"     + Integer.toHexString( Opcode ) +
		" "        + Integer.toHexString( Opcode2 ) +
		" acc:"    + Integer.toHexString( Acc )  +
		" ext:"    + Integer.toHexString( Ext ) 
		);
}

		switch(Opcode)						/* Pointer instructions first */
			{
											/* LD (Load) */
//			CAS4(0xC0):	Acc = ReadMemory(Indexed(Pointer));CYC(18);break;
			case 0xC0:	
			case 0xC1:	
			case 0xC2:	
			case 0xC3:	
						Acc = ReadMemory(Indexed(Pointer));CYC(18);break;
			case 0xC4:	Acc = FETCH();CYC(10);break;
			//CAS3(0xC5):
			case 0xC5:	
			case 0xC6:	
			case 0xC7:	
						Acc = ReadMemory(AutoIndexed(Pointer));CYC(18);break;
			case 0x40:	Acc = Ext;CYC(6);break;

											/* ST (Store) */
			//CAS4(0xC8):
			case 0xC8:
			case 0xC9:	
			case 0xCA:	
			case 0xCB:	
			
			
						WriteMemory(Indexed(Pointer),Acc);CYC(18);break;
			//CAS3(0xCD):
			case 0xCD:	
			case 0xCE:	
			case 0xCF:	
			
						WriteMemory(AutoIndexed(Pointer),Acc);CYC(18);break;

											/* AND (And) */
			//CAS4(0xD0):	
			case 0xD0:	
			case 0xD1:	
			case 0xD2:	
			case 0xD3:	

						Acc = Acc & ReadMemory(Indexed(Pointer));CYC(18);break;
			case 0xD4:	n = FETCH();Acc = Acc & n;CYC(10);break;
			//CAS3(0xD5):
			case 0xD5:	
			case 0xD6:	
			case 0xD7:	
			
						Acc = Acc & ReadMemory(AutoIndexed(Pointer));CYC(18);break;
			case 0x50:	Acc = Acc & Ext;CYC(6);break;

											/* OR (Or) */
			//CAS4(0xD8):
			case 0xD8:	
			case 0xD9:	
			case 0xDA:	
			case 0xDB:	
			
						Acc = Acc | ReadMemory(Indexed(Pointer));CYC(18);break;
			case 0xDC:	
						n=FETCH();Acc = Acc | n;CYC(10);break;
			//CAS3(0xDD):
			case 0xDD:	
			case 0xDE:	
			case 0xDF:	
			
						Acc = Acc | ReadMemory(AutoIndexed(Pointer));CYC(18);break;
			case 0x58:	Acc = Acc | Ext;CYC(6);break;

											/* XOR (Xor) */
			//CAS4(0xE0):
			case 0xE0:	
			case 0xE1:	
			case 0xE2:	
			case 0xE3:	
			
						Acc = Acc ^ ReadMemory(Indexed(Pointer));CYC(18);break;
			case 0xE4:	n = FETCH();Acc = Acc ^ n;CYC(10);break;
			//CAS3(0xE5):
			case 0xE5:	
			case 0xE6:	
			case 0xE7:	

			
						Acc = Acc ^ ReadMemory(AutoIndexed(Pointer));CYC(18);break;
			case 0x60:	Acc = Acc ^ Ext;CYC(6);break;

											/* DAD (Dec Add) */
			//CAS4(0xE8):
			case 0xE8:	
			case 0xE9:	
			case 0xEA:	
			case 0xEB:	

						Acc = DecAdd(Acc,ReadMemory(Indexed(Pointer)));CYC(23);break;
			case 0xEC:	n = FETCH();Acc = DecAdd(Acc,n);CYC(15);break;
			//CAS3(0xED):
			case 0xED:
			case 0xEE:	
			case 0xEF:	
			
						Acc = DecAdd(Acc,ReadMemory(AutoIndexed(Pointer)));CYC(23);break;
			case 0x68:	Acc = DecAdd(Acc,Ext);CYC(11);break;
						/* ADD (Add) */
			//CAS4(0xF0):
			case 0xF0:
			case 0xF1:
			case 0xF2:
			case 0xF3:
			
						Acc = BinAdd(Acc,ReadMemory(Indexed(Pointer)));CYC(19);break;
			case 0xF4:	
						n=FETCH();CYC(11);Acc = BinAdd(Acc,n);break;
			//CAS3(0xF5):
			case 0xF5:
			case 0xF6:
			case 0xF7:
						CYC(19);Acc = BinAdd(Acc,ReadMemory(AutoIndexed(Pointer)));break;
			case 0x70:	
				Acc = BinAdd(Acc,Ext);
				CYC(7);
				break;

						/* CAD (Comp Add) */
			//CAS4(0xF8):
			case 0xF8:
			case 0xF9:
			case 0xFA:
			case 0xFB:

			Acc = BinAdd(Acc,CM(ReadMemory(Indexed(Pointer))));CYC(20);break;
			case 0xFC:	
				n=FETCH();
				CYC(12);
				Acc = BinAdd(Acc,CM(n));break;
			//CAS3(0xFD):
			case 0xFD:
			case 0xFE:
			case 0xFF:

			Acc = BinAdd(Acc,CM(ReadMemory(AutoIndexed(Pointer))));CYC(20);break;
			case 0x78:	
				Acc = BinAdd(Acc,CM(Ext));
				CYC(8);break;

			//CAS4(0x30):
			case 0x30:
			case 0x31:
			case 0x32:
			case 0x33:
			/* XPAL */
				n = Ptr[Pointer];CYC(8);
				Ptr[Pointer] = (n & 0xFF00) | Acc;
				Acc = n & 0xFF;
				break;
			//CAS4(0x34):
			case 0x34:
			case 0x35:
			case 0x36:
			case 0x37:

			/* XPAH */
				n = Ptr[Pointer];CYC(8);
				Ptr[Pointer] = (n & 0xFF) | (Acc << 8);
				Acc = (n >> 8) & 0xFF;
				break;
			//CAS4(0x3C):
			case 0x3C:
			case 0x3D:
			case 0x3E:
			case 0x3F:

			/* XPPC */
				n = Ptr[Pointer];Ptr[Pointer] = Ptr[0];Ptr[0] = n;
				CYC(7);break;

			//CAS4(0x90):
			case 0x90:
			case 0x91:
			case 0x92:
			case 0x93:

			/* Jumps */
				CYC(11);
				Ptr[0] = IndexedJmp(Pointer);
				// System.out.println( "JMP Ptr[0]:" + Integer.toHexString( Ptr[0] ) + " :" + Pointer   );

				break;
			//CAS4(0x94):
			case 0x94:
			case 0x95:
			case 0x96:
			case 0x97:

				CYC(11);
				n = IndexedJmp(Pointer);
				if ((Acc & 0x80) == 0) Ptr[0] = n;
				break;
			//CAS4(0x98):
			case 0x98:
			case 0x99:
			case 0x9A:
			case 0x9B:

				CYC(11);
				n = IndexedJmp(Pointer);
				if (Acc == 0) Ptr[0] = n;
				break;
			//CAS4(0x9C):
			case 0x9C:
			case 0x9D:
			case 0x9E:
			case 0x9F:

				CYC(11);
				n = IndexedJmp(Pointer);
				if (Acc != 0) Ptr[0] = n;
				break;

			//CAS4(0xA8):		
			case 0xA8:
			case 0xA9:
			case 0xAA:
			case 0xAB:
			/* ILD and DLD */
				n = Indexed(Pointer);
				Acc = (ReadMemory(n)+1) & 0xFF;
				CYC(22);WriteMemory(n,Acc);break;

			//CAS4(0xB8):
			case 0xB8:
			case 0xB9:
			case 0xBA:
			case 0xBB:
				n = Indexed(Pointer);
				Acc = (ReadMemory(n)-1) & 0xFF;
				CYC(22);WriteMemory(n,Acc);break;

			case 0x8F:							/* DLY */
				n=FETCH();l = ((long)n) & 0xFFL;
				l = 514L * l + 13 + Acc; Acc = 0xFF;
				CYC(1);break;

			case 0x01:							/* XAE */
				n = Acc;Acc = Ext;Ext = n;break;
			case 0x19:							/* SIO */
				CYC(5);Ext = (Ext >> 1) & 0x7F;break;
			case 0x1C:							/* SR */
				CYC(5);Acc = (Acc >> 1) & 0x7F;break;
			case 0x1D:							/* SRL */
				Acc = (Acc >> 1) & 0x7F;
				CYC(5);Acc = Acc | (Stat & 0x80);break;
			case 0x1E:							/* RR */
				n = Acc;Acc = (Acc >> 1) & 0x7F;
				if ((n & 0x1)>0) Acc = Acc | 0x80;
				CYC(5);break;
			case 0x1F:							/* RRL */
				n = Acc;Acc = (Acc >> 1) & 0x7F;
				if ( (Stat & 0x80)>0) Acc = Acc | 0x80;
				Stat = Stat & 0x7F;
				if (( n & 0x1)>0) Stat = Stat | 0x80;
				CYC(5);break;

			case 0x00:							/* HALT */
				CYC(8);break;
			case 0x02:							/* CCL */
				Stat &= 0x7F;CYC(5);break;
			case 0x03:							/* SCL */
				Stat |= 0x80;CYC(5);break;
			case 0x04:                    		/* DINT */
				Stat &= 0xF7;CYC(6);break;
			case 0x05:							/* IEN */
				Stat |= 0x08;CYC(6);break;
			case 0x06:							/* CSA */
				Acc = Stat;CYC(5);break;
			case 0x07:                   		/* CAS */
				Stat = Acc & 0xCF;CYC(6);break;
			case 0x08:							/* NOP */
				break;
			}
		}
	}

	/********************************************************************/
	/*							  Decimal Add							*/
	/********************************************************************/

	int DecAdd(int v1,int v2)
	{
	int n1 = (v1 & 0xF) + (v2 & 0xF);			/* Add LSB */
	int n2 = (v1 & 0xF0) + (v2 & 0xF0);			/* Add MSB */
	if ( ( Stat & 0x80 ) > 0 ) n1++;						/* Add Carry */
	Stat = Stat & 0x7F;							/* Clear CYL */
	if (n1 > 0x09)								/* Digit 1 carry ? */
		{
		n1 = n1 - 0x0A;
		n2 = n2 + 0x10;
		}
	n1 = (n1 + n2);
	if (n1 > 0x99)								/* Digit 2 carry ? */
		{
		n1 = n1 - 0xA0;
		Stat = Stat | 0x80;
		}
	return(n1 & 0xFF);
	}

	/********************************************************************/
	/*							  Binary Add							*/
	/********************************************************************/

	//#define SGN(x) ((x) & 0x80)
	int SGN( int  x) {
		return ( x & 0x80 );
	}

	
	int BinAdd(int v1,int v2)
	{
		int n;
		n = v1 + v2 + (((Stat & 0x80)>0) ? 1 : 0);	/* Add v1,v2 and carry */
		Stat = Stat & 0x3F;						/* Clear CYL and OV */
		if ((n & 0x100)>0 ) Stat = Stat | 0x80;		/* Set CYL if required */
		if (SGN(v1) == SGN(v2) &&				/* Set OV if required */
					SGN(v1) != SGN(n)) Stat |= 0x40;
		return(n & 0xFF);
	}

	/********************************************************************/
	/*							Indexing Mode							*/
	/********************************************************************/

	int Indexed(int p)
	{
		int Offset;
//		Offset = FETCH() & 0x00FF;							/* Get offset */	
		Offset = FETCH() ;							/* Get offset */	
		if (Offset == 0x80) Offset = Ext;		/* Using 'E' register ? */
		if ( ( Offset & 0x80 ) > 0 ) Offset = Offset-256;	/* Sign extend */

		return( ADD12(Ptr[p],Offset) );			/* Return result */
	}
	
	
	/********************************************************************/
	/*							Indexing Mode							*/
	/********************************************************************/

	int IndexedJmp(int p)
	{
		int Offset;
//		Offset = FETCH() & 0x00FF;							/* Get offset */	
		Offset = FETCH() ;							/* Get offset */	
		//if (Offset == 0x80) Offset = Ext;		/* Using 'E' register ? */
		if ( ( Offset & 0x80 ) > 0 ) Offset = Offset-256;	/* Sign extend */

		return( ADD12(Ptr[p],Offset) );			/* Return result */
	}
	
	
	/********************************************************************/
	/*						  Auto-indexing mode						*/
	/********************************************************************/

	int AutoIndexed(int p)
	{
	int Offset,Address;
	Offset = FETCH();							/* Get offset */
	if (Offset == 0x80) Offset = Ext;		/* Using E ? */
	if ((Offset & 0x80)>0) Offset = Offset-256;	/* Sign extend */
	if (Offset < 0)							/* Pre decrement on -ve offset */
		Ptr[p] = ADD12(Ptr[p],Offset);
	Address = Ptr[p];						/* The address we're using */
	if (Offset > 0)							/* Post increment on +ve offset */
		Ptr[p] = ADD12(Ptr[p],Offset);
	return(Address);
	}
}

