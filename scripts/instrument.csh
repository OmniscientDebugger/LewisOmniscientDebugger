alias debugify java -Xms400100100 -Xmx400100100 -DMEMORY=400100100 -cp $HOME/Debugger:$CLASSPATH  com.lambda.Debugger.Debugify



setenv DIR1 "./accessibility ./swing ./swing/border ./swing/colorchooser ./swing/event ./swing/filechooser ./swing/plaf ./swing/plaf/basic ./swing/plaf/metal ./swing/plaf/multi ./swing/table ./swing/text ./swing/text/html ./swing/text/html/parser ./swing/text/rtf ./swing/tree ./swing/undo"



foreach dir ($DIR1)
  cd ~/Debugger/javax-org/$dir
  foreach f (*.java)
    sed -f ~/Debugger/scripts/instrumentCommands.sed $f > ~/Debugger/JAVAX/$dir/$f
  end
end
 

foreach dir ($DIR1)
(cd $dir; debugify *.class)
end
