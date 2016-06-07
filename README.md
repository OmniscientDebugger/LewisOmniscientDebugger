Thank you for downloading the Omniscient Debugger (the ODB), the Java debugger that lets you go
"backwards in time" so you can examine your program's execution.

=== Maven/Gradle ===

<dependency>
  <groupId>com.neuronrobotics</groupId>
  <artifactId>LewisOmniscientDebugger</artifactId>
  <version>1.6</version>
</dependency>

compile group: 'com.neuronrobotics', name: 'LewisOmniscientDebugger', version: '1.6'

=== Contact Info / Website ===

There are no ODB mailing lists. For support, patches / other
contributions, bugs, and feature requests, please email me at
bil@lambdacs.com -- I enjoy hearing your feedback.

Also please visit the website at: 

http://omniscientdebugger.github.io/

=== System Requirements ===

ODB 1.4 works on code complied for JDK 1.3 and 1.4. ODB 1.5 works on code compiled for JDK 1.5. 
I have not tested it on 1.6. If you have, please let me know.

=== Usage ===

If you normally run your program on UNIX like this:

% java com.lambda.tests.TestMyArrayList

You can run the debugger like this:

% java -cp LewisOmniscientDebugger-1.5.jar:$CLASSPATH com.lambda.Debugger.Debugger com.lambda.tests.TestMyArrayList

There are alias files and .BAT files that allow you to type this:

% debug bomberman.Bomberman

I often run it from Elipse by making com.lambda.Debugger.Debugger the program to run
and making bomberman.Bomberman the argument to it.

=== Tutorial ===

Please try the demo programs that ship with the ODB. Just run the ODB with no
command line arguments.

Please also watch the ODB video "Debugging Backwards in Time"
available at:

https://www.youtube.com/watch?v=xpI8hIgOyko

The research paper can be found at: 

http://www.cs.kent.edu/~farrell/mc08/lectures/progs/pthreads/Lewis-Berg/odb/AADEBUG_Mar_03.pdf

I am looking for a good short Flash tutorial, with voice annotation,
of how to use the ODB. I would like to put it on my website. If you create
one, please email me.

=== Manual ===

See docs/ODBUserManual.html. Enjoy!



   --Bil Lewis <bil@lambdacs.com>
   18 Feb 2007

