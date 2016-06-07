/*                        DebuggerCommand.java

  Copyright 2003, Bil Lewis

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA   
*/

package com.lambda.Debugger;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DebuggerCommand implements Serializable, Runnable {
    private static boolean		recordCommands = false;
    protected Class			clazz;
    protected String			command;
    protected int			index = -1;				// -1 -> "NO INDEX USED FOR THIS COMMAND"
    protected static DebuggerCommandHistory    previousCommandHistory, previousPreviousCommandHistory;



  public DebuggerCommand(Class c, String cmd, int index) {
    clazz = c;
    command = cmd;
    this.index = index;
  }

  /*
  public DebuggerCommand(Class c, String cmd, int index, int value) {
    clazz = c;
    command = cmd;
    this.index = index;
    intValue = value;
  }
  */

  public DebuggerCommand(Class c, String cmd) {
    clazz = c;
    command = cmd;
  }

  public DebuggerCommand() {
  }


  public void run() {    // Run only in Swing main thread.
    replayRecording();
  }



  public static void startRecording() {
    recordCommands=true;
    DebuggerCommandHistoryList.reset();
    Debugger.message("Recording commands.", false);
  }
  public static void stopRecording() {
    recordCommands=false;
    DebuggerCommandHistoryList.writeHistory();
  }
  public static void replayRecording() {
    if (recordCommands) stopRecording();
    DebuggerCommandHistoryList.readHistory();
    DebuggerCommandHistoryList.replayRecording();
  }


  public void execute() {
    if (command == null) throw new DebuggerException("Cannot execute null command");
    DebuggerCommandHistory dch = new DebuggerCommandHistory(this);
    //D.println("DebuggerCommand.execute( " + clazz +"."+command+"() )");

    try {executeNow();}
    catch (Exception e) {
      System.out.println("Exception in DebuggerCommand:\n");
      e.printStackTrace();
    }
    previousCommandHistory = dch;
    if (recordCommands) dch.recordResult();
  }


  public void executeNow() {

    if (clazz == CodeActionListener.class) {
      if (command.equals("firstLine")) CodeActionListener.firstLine();
      else if (command.equals("previousLineThisFunction")) CodeActionListener.previousLineThisFunction();
      else if (command.equals("previousLineAnyFunction")) CodeActionListener.previousLineAnyFunction();
      else if (command.equals("nextLineAnyFunction")) CodeActionListener.nextLineAnyFunction();
      else if (command.equals("nextLineThisFunction")) CodeActionListener.nextLineThisFunction();
      else if (command.equals("lastLine")) CodeActionListener.lastLine();
      else if (command.equals("previousIteration")) CodeActionListener.previousIteration();
      else if (command.equals("nextIteration")) CodeActionListener.nextIteration();
      else if (command.equals("upToCaller")) CodeActionListener.upToCaller();
      else if (command.equals("returnToCaller")) CodeActionListener.returnToCaller();
      else if (command.equals("firstLineB")) CodeActionListener.firstLineB();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == FilterMenuActionListener.class) {
      if (command.equals("filterOutMethodClass")) FilterMenuActionListener.filterOutMethodClass();
      else if (command.equals("unfilter")) FilterMenuActionListener.unfilter();
      else if (command.equals("filterOutClass")) FilterMenuActionListener.filterOutClass();
      else if (command.equals("saveFilters")) {Defaults.writeDefaults(); Debugger.message("Wrote defaults", false);}
      else if (command.equals("filterIn")) FilterMenuActionListener.filterIn();
      else if (command.equals("filter1")) FilterMenuActionListener.filter1();
      else if (command.equals("filter2")) FilterMenuActionListener.filter2();
      else if (command.equals("filter3")) FilterMenuActionListener.filter3();
      else if (command.equals("filter4")) FilterMenuActionListener.filter4();
      else if (command.equals("filter5")) FilterMenuActionListener.filter5();
      else if (command.equals("filter6")) FilterMenuActionListener.filter6();
      else if (command.equals("filter7")) FilterMenuActionListener.filter7();
      else if (command.equals("filter8")) FilterMenuActionListener.filter8();
      else if (command.equals("filter9")) FilterMenuActionListener.filter9();
      else if (command.equals("filterOutMethod")) FilterMenuActionListener.filterOutMethod();
      else if (command.equals("filterOutMethodInternals")) FilterMenuActionListener.filterOutMethodInternals();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }   

    if (clazz == ThreadActionListener.class) {
      if (command.equals("first")) ThreadActionListener.first();
      else if (command.equals("previous")) ThreadActionListener.previous();
      else if (command.equals("next")) ThreadActionListener.next();
      else if (command.equals("last")) ThreadActionListener.last();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == DebuggerActionListener.class) {
      if (command.equals("first")) DebuggerActionListener.first();
      else if (command.equals("previous")) DebuggerActionListener.previous();
      else if (command.equals("next")) DebuggerActionListener.next();
      else if (command.equals("last")) DebuggerActionListener.last();
      else if (command.equals("previousRevert")) DebuggerActionListener.previousRevert();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == LocalsActionListener.class) {
      if (command.equals("first")) LocalsActionListener.first(index);
      else if (command.equals("previous")) LocalsActionListener.previous(index);
      else if (command.equals("next")) LocalsActionListener.next(index);
      else if (command.equals("last")) LocalsActionListener.last(index);
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == ThisActionListener.class) {// UNUSED!
      if (command.equals("first")) ThisActionListener.first();
      else if (command.equals("previous")) ThisActionListener.previous();
      else if (command.equals("next")) ThisActionListener.next();
      else if (command.equals("last")) ThisActionListener.last();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == TraceActionListener.class) {
      if (command.equals("first")) TraceActionListener.first();
      else if (command.equals("previous")) TraceActionListener.previous();
      else if (command.equals("next")) TraceActionListener.next();
      else if (command.equals("last")) TraceActionListener.last();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == ObjectActionListener.class) {
      if (command.equals("first")) ObjectActionListener.first(index);
      else if (command.equals("previous")) ObjectActionListener.previous(index);
      else if (command.equals("next")) ObjectActionListener.next(index);
      else if (command.equals("last")) ObjectActionListener.last(index);
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == TTYActionListener.class) {
      if (command.equals("first")) TTYActionListener.first();
      else if (command.equals("previous")) TTYActionListener.previous();
      else if (command.equals("next")) TTYActionListener.next();
      else if (command.equals("last")) TTYActionListener.last();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == ObjectsMenuActionListener.class) {
      if (command.equals("copyClass")) ObjectsMenuActionListener.copyClass();
      else if (command.equals("remove")) ObjectsMenuActionListener.remove();
      else if (command.equals("expand")) ObjectsMenuActionListener.expand();
      else if (command.equals("select")) ObjectsMenuActionListener.select();
      else if (command.equals("selectLocal")) ObjectsMenuActionListener.selectLocal();
      else if (command.equals("retain")) ObjectsMenuActionListener.retain();
      else if (command.equals("set")) ObjectsMenuActionListener.set();
      else if (command.equals("abort")) ObjectsMenuActionListener.abort();
      else if (command.equals("copy")) ObjectsMenuActionListener.copy();
      else if (command.equals("showAll")) ObjectsMenuActionListener.showAll();
      else if (command.equals("restore")) ObjectsMenuActionListener.restore();
      else if (command.equals("add")) ObjectsMenuActionListener.add();
      else if (command.equals("hex")) ObjectsMenuActionListener.hex();
      else if (command.equals("print")) ObjectsMenuActionListener.print();
      else if (command.equals("tostring")) ObjectsMenuActionListener.tostring();
      else if (command.equals("input")) ObjectsMenuActionListener.input();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == CodeListener.class) {
      if (command.equals("selectCodeLine")) CodeListener.selectCodeLine(index);
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == FileMenuActionListener.class) {
      if (command.equals("choose")) FileMenuActionListener.choose();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == HelpMenuActionListener.class) {
      if (command.equals("ghelp")) HelpMenuActionListener.ghelp();
	  if (command.equals("dhelp")) HelpMenuActionListener.dhelp();
      else if (command.equals("fhelp")) HelpMenuActionListener.fhelp();
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }
    if (clazz == StackListener.class) {
      if (command.equals("select")) StackListener.select(index);
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == TTYListener.class) {
      if (command.equals("select")) TTYListener.select(index);
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == TraceListener.class) {
      if (command.equals("select")) TraceListener.select(index);
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }

    if (clazz == ThreadListener.class) {
      if (command.equals("select")) ThreadListener.select(index);
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }
 
    if (clazz == TimeSliderListener.class) {
      if (command.equals("slider")) TimeSliderListener.slider(index);
      else System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }
 
    if (clazz == DebugMenuActionListener.class) {
	if (command.equals("repeat")) {
	    if (previousCommandHistory == null) {
		System.out.println("No previous command to repeat");
		return;
	    }
	    if (previousCommandHistory.command.command.equals("repeat")) {
		previousCommandHistory = previousPreviousCommandHistory;
	    }
	    System.out.println("executing command: " + previousCommandHistory.command.command);
	    previousCommandHistory.execute();
	    previousPreviousCommandHistory = previousCommandHistory;
	}
      else
	  System.out.println("IMPOSSIBLE: DebuggerCommand.execute() no such command: " + clazz+"."+command);
    }
    
  }

}




 class DebuggerCommandHistory implements Serializable {

   final DebuggerCommand 		command;
   private int  			end;
   private String			fileLine, message;


   public DebuggerCommandHistory(DebuggerCommand dc) {
     command = dc;
   }

   public void execute() {
       command.execute();
   }

     
   public void recordResult() {
     DebuggerCommandHistoryList.add(this);
     end = TimeStamp.currentTime().time;
     message = Debugger.getMessage();
   }


   public boolean replay(int i) {
     boolean failed = false;
     command.execute();

     if (TimeStamp.currentTime().time != end) {
       if (command.index == -1)
	 System.out.println("Replay Error["+i+"]: " + command.clazz +"." + command.command
			    + "() \nexpected: "+end+ "\n     got: " +TimeStamp.currentTime().time);
       else
	 System.out.println("Replay Error["+i+"]: " + command.clazz +"." + command.command
			    + "(" + command.index + ") \nexpected: "+end+ "\n     got: " +TimeStamp.currentTime().time);	 
       failed = true;
     }
     if (!message.equals(Debugger.getMessage())) {
       if (command.index == -1)
       System.out.println("Replay Error["+i+"]: " + command.clazz +"." + command.command
			  + " \n    expected: "+message+ "\n         got: " +Debugger.getMessage());
       else
       System.out.println("Replay Error["+i+"]: " + command.clazz +"." + command.command
			  + "(" + command.index + ") \n    expected: "+message+ "\n         got: " +Debugger.getMessage());
       failed = true;
     }

     return failed;
   }

 }



 class DebuggerCommandHistoryList implements Serializable {
   private static DebuggerCommandHistoryList 	commandHistoryList = new DebuggerCommandHistoryList();
   private DebuggerCommandHistory[] 		commandHistory = new DebuggerCommandHistory[1000];
   private int 					index = 0;


   public static void add(DebuggerCommandHistory dch) {
     commandHistoryList.addCommand(dch);
   }

   public void addCommand(DebuggerCommandHistory dch) {
     commandHistory[index] = dch;
     index++;
   }

   public static void replayRecording() {
     if (commandHistoryList == null) return;
     commandHistoryList.replayRecordings();
   }

   public void replayRecordings() {
     boolean failed = false;

     if (index == 0) {
       Debugger.message("No recorded commands.", true);
       return;
     }

     for (int i=0; i < index; i++) {
       if (commandHistory[i].replay(i)) {
	 Debugger.message(" Command " + i + " failed.", true);
	 return;
       }
     }
     Debugger.message("All "+index+" commands succeeded.", false);
   }

   

   public static void writeHistory() {
     try {
       ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Debugger.programName+ ".debuggerCommands"));
       oos.writeObject(commandHistoryList);
       oos.close();
       Debugger.message("Saved history to "+Debugger.programName+ ".debuggerCommands.", false);
     }
     catch (IOException e) {Debugger.message("Couldn't save history " + e, true);}
   }
     

   
   public static void readHistory() {
	    try {
	      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Debugger.programName+ ".debuggerCommands"));
	      commandHistoryList = (DebuggerCommandHistoryList)ois.readObject();
	      ois.close();
	    }
	    catch (IOException e) {Debugger.message("Couldn't read history " + e, true);}
	    catch (ClassNotFoundException e) {Debugger.message("Couldn't read history " + e, true);}
   }

   public static void reset() {
     commandHistoryList.index = 0;
   }
   
 }
