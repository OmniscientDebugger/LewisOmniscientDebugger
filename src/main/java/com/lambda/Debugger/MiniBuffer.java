/*                        MiniBuffer.java

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


import java.awt.Toolkit;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;


public class MiniBuffer implements DocumentListener {
    private static JTextArea 		miniBuffer;
    private static int			selectedLine, startLine;
    private static MethodLine		currentMethodLine;		
    public static boolean		forward = true, recursive=false;
    private static String		searchString = "", expression = "<Demo_0>.sort(6, 16)", objectString = "";

    final static String			DISPLAY = "Display Object: ", INPUT = "Input Object: ", EVALUATE = "Evaluate Expression: ";
    final static String			FGET = "fget: ", ISEARCH = "I-search: ", CDATA = "cdata: ";


    public static String getText() {return miniBuffer.getText();}

    public static void add(Object o) {
	String s = miniBuffer.getText();    
	String os = TimeStamp.trimToLength(o, 50);
	miniBuffer.setText(s+os);
    }
	

    public static void initialize(JTextArea mb) {
	DocumentListener listener = new MiniBuffer();
	miniBuffer = mb;
	Document d = miniBuffer.getDocument();
	d.addDocumentListener(listener);
    }

    public static boolean wantsInput() {
	String s = miniBuffer.getText();    
	if ( (s.startsWith(EVALUATE)) || (s.startsWith(INPUT)) ||(s.startsWith(DISPLAY)) ||
	     (s.startsWith(FGET)) || (s.startsWith(ISEARCH)) ||
	     (s.startsWith(CDATA)) ) return true;
	return false;
    }


    public static void copyValue(String value) {
	Completion.createCompletionTable();
	recursive=true;
	String s = miniBuffer.getText();    
	if (wantsInput()) {
	    s = s + value;
	    miniBuffer.setText(s);
	    miniBuffer.requestFocus();
	}
	else {
	    if (Debugger.mainTimeLine) {
		Debugger.message("Switch to secondary time line before altering variable values", true);
		return;
	    }
	    miniBuffer.setText(INPUT+value);
	    miniBuffer.requestFocus();
	}
	recursive=false;
    }



    public static void message(String msg, boolean beep) {
	recursive=true;
//	if (msg.length() > 140) msg = msg.substring(0, 135)+"...";
	msg = Misc.replace(msg, "\n", "");
	miniBuffer.setText(msg);
	if (beep) Toolkit.getDefaultToolkit().beep();
	recursive=false;
    }

    public static void messageLong(String msg, boolean beep) {
	recursive=true;
	miniBuffer.setText(msg);
	if (beep) Toolkit.getDefaultToolkit().beep();
	TimeStamp ts = TimeStamp.currentTime();
	Debugger.TSLabel.setText("asdf");					// Triggers a resize of minibuffer for some odd reason.
	Debugger.updateTSLabel(ts);						// (Size of label changed? So???)
	recursive=false;
    }


    public static void abort() {
	recursive=true;
	Debugger.TracePList.updateUI();						// It may have scrolled while searching
	messageLong("Aborted", true);
	recursive=false;
    }

    public static void evaluateExpressionInitialize() {
	recursive=true;
	String s = miniBuffer.getText();
	if (s.startsWith(EVALUATE)) {
	    if (s.length() == 21)
		miniBuffer.setText(EVALUATE+ expression);
	    else
		Toolkit.getDefaultToolkit().beep();
	}
	else {
	    Completion.createCompletionTable();
	    miniBuffer.setText(EVALUATE);
	    miniBuffer.requestFocus();
	}
	recursive=false;
    }



    public static void completeExpression() {					// Only called by insertUpdate()
	recursive=true;
	String s = miniBuffer.getText();
	if (s.endsWith("\t")) s = s.substring(0, s.length()-1);			// Remove TAB	

	if (!s.startsWith(EVALUATE)) {
	    Debugger.message("Not evaluating expression!?", true);			// Never happen?
	    recursive=false;
	    return;
	}

	s = s.substring(21, s.length());

	expression = Completion.completeCall(s);
	miniBuffer.setText(EVALUATE+expression);
	miniBuffer.requestFocus();
	recursive=false;
    }


    public static void evaluateExpression() {					// Only called by insertUpdate()
	recursive=true;
	ParsePair pp;
	String s = miniBuffer.getText();
	if (s.endsWith("\n")) s = s.substring(0, s.length()-1);			// Remove CR

	if (!s.startsWith(EVALUATE)) {
	    Debugger.message("Not evaluating expression!?", true);			// Never happen?
	    recursive=false;
	    return;
	}

	expression = s.substring(21, s.length());

	try {pp = Completion.parse(expression);}
	catch (CompletionException ce) {
	    //System.out.println(ce.message);	
	    Debugger.message(ce.message, true);
	    recursive=false;
	    return;
	}
	if (pp == null) {
	    Debugger.message("Invalid Expression: "+expression, true);
	    recursive=false;
	    return;
	}

	Object obj = pp.obj;
	if (obj instanceof ObjectCompletionPair) obj = ((ObjectCompletionPair)obj).obj;
	//Completion.printTable();

	Object returnValue = Debugger.runAlternate(obj, pp.method, pp.objects);
	String returnString;
	if (returnValue instanceof String)
	    returnString = (String) returnValue;
	else if ((returnValue instanceof ShadowPrimitive) || (returnValue instanceof Integer))
	    returnString = returnValue.toString();
	else
	    returnString = Shadow.get(returnValue).printString();
	miniBuffer.setText(expression + " -> " + returnString);
	Debugger.println(expression + " -> " + returnString);
	return;
    }



    public static void inputObjectAndDisplay() {
	recursive=true;
	Completion.createCompletionTable();
	miniBuffer.setText(DISPLAY);
	miniBuffer.requestFocus();
	recursive=false;
    }

    public static void inputValue() {
	recursive=true;
	Completion.createCompletionTable();
	miniBuffer.setText(INPUT);
	miniBuffer.requestFocus();
	recursive=false;
    }

    
    private static void completeValue() {						// Only called by insertUpdate()
	boolean isInput = false, isDisplay = false;
	recursive=true;
	String s = miniBuffer.getText();
	if (s.endsWith("\t")) s = s.substring(0, s.length()-1);			// Remove TAB

	if (s.startsWith(INPUT)) isInput = true;
	if (s.startsWith(DISPLAY)) isDisplay = true;
	if (!(isInput || isDisplay)) {
	    Debugger.message("Not inputting value", true);
	    recursive=false;
	    return;
	}
	String sub = s.substring((isInput ? 14 : 16));
	String completion = Completion.completeObject(sub);
	if (isInput)
	    miniBuffer.setText(INPUT+completion);
	else
	    miniBuffer.setText(DISPLAY+completion);
	miniBuffer.requestFocus();
	recursive=false;
    }


    public static void completedValue() {						// Called by insertUpdate() & endSearch()
	boolean isInput = false, isDisplay = false;
	recursive=true;
	String s = miniBuffer.getText();
	if (s.endsWith("\n")) s = s.substring(0, s.length()-1);			// Remove CR

	if (s.startsWith(INPUT)) isInput = true;
	if (s.startsWith(DISPLAY)) isDisplay = true;
	if (!(isInput || isDisplay)) {
	    Debugger.message("Not inputting value", true);				// Never happen if called by insertUpdate()
	}
	String sub = s.substring((isInput ? 14 : 16));
	try {
	    Object o = Completion.completedObject(sub);
	    if (s.startsWith(INPUT)) {
		miniBuffer.setText("Value set");
		ObjectPane.set(o);
	    }
	    else {
		miniBuffer.setText("");
		ObjectPane.add(o);
	    }
	}
	catch (CompletionException ce) {						// Incomplete/Impossible input
	    sub = s.substring((isInput ? 14 : 16));
	    String completion = Completion.completeObject(sub);
	    if (isInput) 
		miniBuffer.setText(INPUT+completion);
	    else
		miniBuffer.setText(DISPLAY+completion);
	    Toolkit.getDefaultToolkit().beep();
	    miniBuffer.requestFocus();
	}
	Debugger.revert();
	recursive=false;
    }



    public static void search() {
	forward = true;
	searchHelper();
    }

    public static void rsearch() {
	forward = false;
	String s = miniBuffer.getText();
   
	if (s.startsWith(ISEARCH)) {searchHelper(); return;}
	if (s.startsWith(FGET)) {rFGet(); return;}
    	if (s.startsWith(CDATA)) {rCdata(); return;}
    }

    public static void endSearch() {				// Callable from insertUpdate() below, or TraceMenu "End search"
	recursive=true;
	String s = miniBuffer.getText();
   
	if ( s.startsWith(ISEARCH) && (currentMethodLine != null) ) Debugger.revert(currentMethodLine.lookupTS()); 
	else if (s.startsWith(INPUT)) completedValue();			// Never happen?
	else if (s.startsWith(DISPLAY)) completedValue();			// Never happen?
	else if (s.startsWith(EVALUATE)) evaluateExpression();	// Never happen?
	else Debugger.message("Minibuffer not active?!", true);
	recursive=false;
    }


// **************** FGet ****************

    public static void countFGet() {
	String s = miniBuffer.getText();
	s = s.substring(6, s.length());
	try{EventInterface.fget(s, true, true);}
	catch (DebuggerException e) {
	    Debugger.println(e.getMessage());
	    Debugger.message(e.getMessage(), true);
	}
	//System.out.println("cdata() pattern: "+s);
    }


    public static void beginFGet() { beginFGet(true); }

    public static void beginFGet(boolean forward) {
	recursive=true;
	String previous = EventInterface.previousPattern();
	String s = miniBuffer.getText();
	//    System.out.println("S: \""+s+"\"");
	if (s.equals(FGET)) {
	    if (previous.equals("")) {
		Toolkit.getDefaultToolkit().beep();
		recursive=false;
		return;
	    }
	    s += previous;
	    miniBuffer.setText(s);
	    recursive=false;
	    return;
	}
	if (s.startsWith(FGET)) {
	    //	previousFGet = s.substring(6, s.length());
	    //	System.out.println("refgettig: \""+s+"\"");
	    s=s.substring(6, s.length());
	    try{EventInterface.fget(s, forward, false);}
	    catch (DebuggerException e) {
		Debugger.println(e.getMessage());
		Debugger.message(e.getMessage(), true);
	    }
	    //System.out.println("fget() pattern: "+s);
	    recursive=false;
	    return;
	}	
	miniBuffer.setText(FGET);
	miniBuffer.requestFocus();
	recursive=false;
    }

    public static void rFGet() {
	beginFGet(false);
    }

    public static void endFGet() {				// Callable from insertUpdate() below, or TraceMenu "End search"
	recursive=true;
	String s = miniBuffer.getText();
	//    System.out.println("endFGet " + s);
	Debugger.revert();					//message("Aborted.", false);
	recursive=false;
    }



    public static void countMatches() {
	String s = miniBuffer.getText();
	if (s.startsWith(CDATA)) { countCdata(); return; }
	if (s.startsWith(FGET)) { countFGet(); return; }
	return;
    }
	
	


// **************** Cdata ****************

    public static void countCdata() {
	String s = miniBuffer.getText();
	s = s.substring(7, s.length());
	try{EventInterface.cdata(s, true, true);}
	catch (DebuggerException e) {
	    Debugger.println(e.getMessage());
	    Debugger.message(e.getMessage(), true);
	}
	//System.out.println("cdata() pattern: "+s);
    }


    public static void beginCdata() { beginCdata(true); }

    public static void beginCdata(boolean forward) {
	recursive=true;
	String previous = EventInterface.previousCdataPattern();
	String s = miniBuffer.getText();
	//    System.out.println("S: \""+s+"\"");
	if (s.equals(CDATA)) {
	    if (previous.equals("")) {
		Toolkit.getDefaultToolkit().beep();
		recursive=false;
		return;
	    }
	    s += previous;
	    miniBuffer.setText(s);
	    recursive=false;
	    return;
	}
	if (s.startsWith(CDATA)) {
	    s=s.substring(7, s.length());
	    try{EventInterface.cdata(s, forward, false);}
	    catch (DebuggerException e) {
		Debugger.println(e.getMessage());
		Debugger.message(e.getMessage(), true);
	    }
	    //System.out.println("cdata() pattern: "+s);
	    recursive=false;
	    return;
	}	
	miniBuffer.setText(CDATA);
	miniBuffer.requestFocus();
	recursive=false;
    }

    public static void rCdata() {
	beginCdata(false);
    }

    public static void endCdata() {				// Callable from insertUpdate() below, or TraceMenu "End search"
	recursive=true;
	String s = miniBuffer.getText();
	//    System.out.println("endCdata " + s);
	Debugger.revert();					//message("Aborted.", false);
	recursive=false;
    }

// **************** Cdata ****************



    private static void searchHelper() {
	recursive=true;
	String s = miniBuffer.getText();
	if (s.startsWith(ISEARCH)) {
	    if (s.length() == 10) {
		miniBuffer.setText(ISEARCH+searchString);
		updateSearch();
	    }
	    else {
		startLine=selectedLine;
		if (forward)
		    startLine++;
		else
		    startLine--;
		updateSearch();
	    }
	}
	else {
	    miniBuffer.setText(ISEARCH);
	    miniBuffer.requestFocus();
	    int i = Debugger.TracePList.getSelectedIndex();
	    if (i > -1)
		startLine=selectedLine = i;
	    else
		startLine=selectedLine = 0;
	}
	recursive=false;
    }

    public void removeUpdate(DocumentEvent e) {
	//    System.out.println("removeUpdate " + e);
	if (recursive) return;    
	String s = miniBuffer.getText();
	if (s.startsWith(ISEARCH)) updateSearch();
    }



    public void insertUpdate(DocumentEvent e) {
	if (recursive) return;    
	//System.out.println("insertUpdate " + e);
	String s = miniBuffer.getText();

    
	if (s.endsWith("\t")) {							// Auto-complete
	    if (s.startsWith(ISEARCH)) {
		updateSearch();
	    }
	    if (s.startsWith(INPUT) || s.startsWith(DISPLAY)) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    completeValue();
			} });
	    }
	    else if (s.startsWith(EVALUATE)) {
		SwingUtilities.invokeLater(new Runnable() { public void run() {completeExpression();} });
	    }
	    return;
	}


	if (s.endsWith("\n")) {							// CR == done with whatever
	    if (s.startsWith(ISEARCH)) {
		SwingUtilities.invokeLater(new Runnable() { public void run() { endSearch(); } });
	    }
	    if (s.startsWith(FGET)) {
		SwingUtilities.invokeLater(new Runnable() { public void run() { endFGet(); } });
	    }
	    if (s.startsWith(FGET)) {
		SwingUtilities.invokeLater(new Runnable() { public void run() { endFGet(); } });
	    }
	    else if  (s.startsWith(EVALUATE)) {	
		SwingUtilities.invokeLater(new Runnable() { public void run() {evaluateExpression();} });
	    }    
	    else if  (s.startsWith(INPUT)) {				// ?Useful?
		SwingUtilities.invokeLater(new Runnable() { public void run() {completedValue();} });
	    }    
	    else if  (s.startsWith(DISPLAY)) {				// ?Useful?
		SwingUtilities.invokeLater(new Runnable() { public void run() {completedValue();} });
	    }    
	    recursive=false;  
	    return;
	}

	if (s.startsWith(ISEARCH)) updateSearch();
	recursive=false;
    }

    public void changedUpdate(DocumentEvent e) {					// Never called?
	recursive=true;
	//System.out.println("changedUpdate " + e);
	String s = miniBuffer.getText();
	if (s.startsWith(ISEARCH)) updateSearch();
	recursive=false;
    }


    private static void updateSearch() {
	recursive=true;
	String s = miniBuffer.getText();
	if (!s.startsWith(ISEARCH)) {
	    //System.out.println("Not searching? IMPOSSIBLE!");
	    recursive=false;
	    return;
	}
	String ss = s.substring(10);
	//System.out.println("Searching for "+searchString);
	MethodLine tl = TraceLine.search(startLine, ss, forward);
	if (tl == null) {
	    Toolkit.getDefaultToolkit().beep();
	    recursive=false;
	    return;
	}
	currentMethodLine = tl;
	selectedLine = tl.filteredIndex;
	//Debugger.reverting = true;
	//Debugger.updateTracePane(tl.lookupTS());
	Debugger.revert(tl.lookupTS(), false);
	startLine = tl.filteredIndex;
	//Debugger.reverting = false;
	recursive=false;
	if (ss.equals("")) return;
	searchString = ss;
    }

}
