<H2>doug-h-rice/MK14_java</h2>

A java Science of Cambridge MK-14 port. 

MK14 emulator ported to Java
Doug Rice 2018	13/05/2018

<h2>Status:</h2>	

Does not run in real time yet

I brought a real MK-14 when they came out in about 1976 or 1977. 
I had to wait all summer holidays for delivery.

<h2>History</H2>
Paul Robson wrote an emulator for DOS in 1998. This has been ported to SDL1.2 and SDL2 and runs on a Raspberry Pi and Linux. This version is for Java, as that is more common on PCs. You need the Java JRE to run this and JDK to build the code. 

The Cpu.java is a port from his CPU.C. The JMP bug has been fixed.

Rom.java has both versions of the Monitor.

AnimateMK14 started as Animate.java, which is worth understanding.

References:<BR>
	http://www.java2s.com/Code/Java/2D-Graphics-GUI/HowtocreateAnimationPaintandthread.htm <BR>
	http://ccgi.dougrice.plus.com/cgi-bin/wiki.pl?MK14_Notes <BR>
	http://www.dougrice.plus.com/dev/seg_mk14.htm <BR>
	https://github.com/doug-h-rice <BR>


References:<BR>	
	http://s400081762.websitehome.co.uk/paulRobson/software.htm <BR>
	http://www.techlib.com/area_50/Readers/Karen/micro.htm#PIC14 <BR>

Why?:
<pre>
	- I want an MK-14 emulator, as it was my first computer 40 years ago.
	- Paul Robson's Emulator needs DOSBOX and Turbo C to build it.
	- Java is more common than SDL2
	- There is a zip file of compiled java classes if jdk is not installed and javac is unavailable.
	- My JavaScript emulator is a bit slow
	- Converting Cpu.c to C# and using Mono was going nowhere.
	
	- Not many other emulators expect you to enter HEX machine code using a calculator keyboard.
	
	- Try out Java2D programming.
	- try and work towards an Android App.
	- Learn a bit about Java and Java2d.
</pre>
	
Files:
<pre>

	Rom.java 		- contains SCIOS ROM code
	Cpu.java 		- contains CPU code ported form Paul Robson's code. JMP bug fixed.
	AnimateMK14.java	- Contains Main() plus Display and keyboard. 
</pre>

For examples, see elsewhere on my git hub and Other people's pages.

	
We need to build the MK14:
	It has a PCB - This connects the CPU to the Memory map, RAM, ROM, Keyboard, Display and Crystal.
	keyboard - assemble - Ensure no keys are pressed. When we run the GUI, track key presses in another memory.  
	display  - Assemble - When the digit is written to, the segments light up, so we need to repaint GUI periodically.  
	You plug the ROM in - load ROM into the Memory Map that the CPU can access.
	You plug the RAM in - We can add a function to preload the RAM to save typing!!
	You plug the CPU in - The PCB connects it to memory and teh 
	Insert the Crystal  - The CPU has a clock that runs the program counter
	Do a visual check
	Power up 			- Start the timer that makes the CPU run some instructions and repaint the GUI.
	
Press keys and play!
	
	
