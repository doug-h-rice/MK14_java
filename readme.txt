/*
MK14 emulator ported to Java
Doug Rice 2018	13/05/2018

Status:	
	Does not run in real time yet

I brought one when they came out in about 1976 or 1977.
I had to wait all summer holidays for delivery.

Paul Robson wrote an emulator for DOS in 1998.


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
	Rom.java 		- contains SCIOS ROM code
	Cpu.java 		- contains CPU code ported form Paul Robson's code. JMP bug fixed.
	AnimateMK14.java	- Contains Main() plus Display and keyboard. 
