rem
rem
rem

rem java AnimateMK14 
echo "batch file to run Java examples "

"C:\Program Files\Java\jdk1.8.0_66\bin\javac"  Rom.java -Xlint:deprecation
"C:\Program Files\Java\jdk1.8.0_66\bin\javac"  Cpu.java -Xlint:deprecation
"C:\Program Files\Java\jdk1.8.0_66\bin\javac"  AnimateMK14.java -Xlint:deprecation

java AnimateMK14

java AnimateMK14 Babbage.hex
java AnimateMK14 examples\Clock.hex
pause
exit

rem 

java AnimateMK14 examples\Babbage.hex
java AnimateMK14 examples\test5.hex
java AnimateMK14 examples\message.hex
java AnimateMK14 examples\duck_shoot.ihx
