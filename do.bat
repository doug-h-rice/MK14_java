rem
rem "batch file to build and run Java examples "
rem download jre to run precompiled class files
rem download jdk to build java files

rem java AnimateMK14 
echo "batch file to run Java examples "
//java AnimateMK14 examples\decimal.hex

rem build emulator and run it
rem edit to point to your JDK files



"C:\Program Files\Java\jdk1.8.0_66\bin\javac"  Rom.java -Xlint:deprecation
"C:\Program Files\Java\jdk1.8.0_66\bin\javac"  Cpu.java -Xlint:deprecation
"C:\Program Files\Java\jdk1.8.0_66\bin\javac"  AnimateMK14.java -Xlint:deprecation

java AnimateMK14


rem ---------------------------------------------
rem start it loading an example 
rem other examples in zip file or elsewhere on the web
rem http://ccgi.dougrice.plus.com/cgi-bin/wiki.pl?MK14_Notes
rem http://www.dougrice.plus.com/dev/seg_mk14.htm
rem ---------------------------------------------
java AnimateMK14 message.hex
java AnimateMK14 Clock.hex
java AnimateMK14 examples\decimal.hex
java AnimateMK14 examples\Babbage.hex
java AnimateMK14 examples\Clock.hex
java AnimateMK14 examples\duck_shoot.ihx
pause
exit

rem 

java AnimateMK14 examples\Babbage.hex
java AnimateMK14 examples\test5.hex
java AnimateMK14 examples\message.hex
java AnimateMK14 examples\duck_shoot.ihx
