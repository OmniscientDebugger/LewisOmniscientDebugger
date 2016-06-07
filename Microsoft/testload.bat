rem	                        Aliases for Debugger when a jar file in ~/Debugger


rem	 This is for testing a series of .class files to see if the verifier likes them

rem	If you save the debugger in a different location, change this line:
set DEBUGGER_HOME=c:\Debugger

set ddPATH= -cp %DEBUGGER_HOME%\debugger.jar;%CLASSPATH%


java %ddPATH% com.lambda.Debugger.TestLoadClass %1 %2 %3 %4 %5 %6 %7 %8 %9

