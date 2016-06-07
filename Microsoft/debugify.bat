rem	                        Aliases for Debugger when a jar file in ~/Debugger


rem	If you save the debugger in a different location, change this line:
set DEBUGGER_HOME=c:\Debugger


set memSize=80000000
set ddHEAP_SIZE= -Xms%memSize% -Xmx%memSize% -DMEMORY=%memSize%
set ddPATH= -cp %DEBUGGER_HOME%\debugger.jar;%CLASSPATH%
set debugger=com.lambda.Debugger.Debugify


rem	 It is assumed (required!) that CLASSPATH is properly set.
rem	 If additional user flags are desired, set this env var:
set USER_FLAGS= 

rem	debugify *.class   	Inserts instrumentation into the specified files.
rem				You can then run the debugger with the DONT_INSTRUMENT flag.

java %ddHEAP_SIZE% %ddPATH% %USER_FLAGS% -DPAUSED -DDONT_START        	%debugger% %1 %2 %3 %4 %5 %6 %7 %8 %9

