rem	                        Aliases for Debugger when a jar file in ~/Debugger


rem	 MEMORY is the number used to determine when to GC. Currently 400MB => 2m TSs
rem	 If your program needs lots of space & fewer TSs, make MEMORY < Xmx
rem	 If your program needs less  space & more TSs, make MEMORY == Xmx
rem	 Exceeding physical memory is not a major problem, though GC'ing a 2GB
rem	 virtual memory with 128MB physical is... ugly
rem	 MAX memory size is 2GB (31 bits). 400MB is generally a good size.

rem On current distributions (2004-?) the ClassLoader.jar file will NOT
rem exist and that bit of the command line will be ignored. The programmer
rem may install it herself:
rem java com.lambda.Debugger.InstrumentorForCL \Debugger


rem	If you save the debugger in a different location, change this line:
set DEBUGGER_HOME=c:\Debugger


set memSize=400000000
set ddHEAP_SIZE= -Xms%memSize% -Xmx%memSize% -DMEMORY=%memSize%
set ddPATH= -cp %DEBUGGER_HOME%\debugger.jar;%CLASSPATH% -Xbootclasspath/p:%DEBUGGER_HOME%\Debugger\ClassLoader.jar
set debugger=com.lambda.Debugger.Debugger


rem	 It is assumed (required!) that CLASSPATH is properly set.
rem	 If additional user flags are desired, set this env var:
set USER_FLAGS= 

rem	 debug              brings up a file chooser. This is only for programs not in jar files
rem	 debug MyProgram    starts the program & starts recording right away.
rem	 debugp MyProgram   just brings up the controller window so you can set flags.

java %ddHEAP_SIZE% %ddPATH% %USER_FLAGS% -DPAUSED -DDONT_START        	%debugger% %1 %2 %3 %4 %5 %6 %7 %8 %9

