/*                        Defaults.java

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;



public class Defaults {
    static VectorD dontRecord = new VectorD();
    static VectorD dontInstrument = new VectorD();
    static VectorD didntInstrument = new VectorD();
    //    static VectorD instrumentOnly = new VectorD();
    static VectorD instrumentOnlyPackages = new VectorD();
    static String defaultsFile = ".debuggerDefaults";

    static String MAX_MEMORY =			"MaxTimeStamps:";
    static String START_ON_LINE =		"StartOnLine:";
    static String STOP_ON_LINE =		"StopOnLine:";
    static String START_ON_OUTPUT =		"StartOnOutput:";
    static String STOP_ON_OUTPUT =		"StopOnOutput:";
    static String INSTRUMENT_ONLY =		"OnlyInstrument:";
    static String DIDNT_INSTRUMENT =		"DidntInstrument:";
    static String DONT_INSTRUMENT_METHOD =	"DontInstrumentMethod:";
    static String DONT_RECORD_METHOD =		"DontRecordMethod:";
    static String SOURCE_DIRECTORY =		"SourceDirectory:";
    static String USER_SELECTED_FIELD =		"UserSelectedField:";
    //    static String CUTOFF_DEPTH =		"CutoffDepth:";
    static String START_PATTERN =		"StartPattern:";
    static String STOP_PATTERN =		"StopPattern:";
    static String DONT_EITHER =			"DontEither:";
    static String SPECIAL_FORMATTER =			"SpecialFormatter:";

    //    static String DONT_RECORD_IMMEDIATELY =	"DontRecordImmediately:";
    //    static String INSTRUMENT_VIA_CLASSLOADER =	"InstrumentViaClassloader:";
    //    static String STOP_WHEN_MAIN_EXITS =	"StopWhenMainExits:";
    //    static String PAUSE_PROGRAM_ON_STOP =	"PauseProgramOnStop:";
    //    static String START_RUNNING_IMMEDIATELY =	"StartRunningImmediately:";
    //    static String DEBUG_DEBUGGER =		"DebugDebugger:";

    static private boolean firstRun = false;
    static private boolean alreadyRead = false;

    public static boolean readDefaults() {
	if (alreadyRead) return firstRun;
	alreadyRead = true;
	try {
		if (!"com.lambda".startsWith("com")) SpecialFormatters.add("lambda.Debugger.SpecialTimeStampFormatter");
	    BufferedReader br = new BufferedReader(new FileReader(defaultsFile));
	    Debugger.println("Reading .debuggerDefaults file...");
	    String line;
	    while ((line = br.readLine()) != null) {
		//System.out.println(line);
		int len = line.length();
		if (line.equals("")) continue;		// ignore blank lines
		if (line.startsWith("#")) continue;		// ignore comment lines
		int firstSpace = line.indexOf(' ');
		int firstTab = line.indexOf('\t');
		if (firstSpace == -1) firstSpace = firstTab;
		if ((firstTab > 0) && (firstTab < firstSpace)) firstSpace = firstTab;
		if (firstSpace == -1) firstSpace = len;

		String category = line.substring(0, firstSpace).intern();
		String value="";
		if (len > firstSpace) value = line.substring(firstSpace+1, len);
		value = value.trim();
		if (value.length() == 0) continue;
		if (category == MAX_MEMORY) 			{setMemory(value); continue;}
		if (category == START_ON_LINE) 			{setStartLine(value); continue;}
		if (category == STOP_ON_LINE) 			{setStopLine(value); continue;}
		if (category == START_ON_OUTPUT) 		{setStartOutput(value); continue;}
		if (category == STOP_ON_OUTPUT) 		{setStopOutput(value); continue;}
		if (category == INSTRUMENT_ONLY) 		{addInstrumentOnly(value); continue;}
		if (category == DIDNT_INSTRUMENT)		{addDidntInstrument(value); continue;}
		if (category == DONT_INSTRUMENT_METHOD) 	{addDontInstrumentMethod(value); continue;}
		if (category == DONT_RECORD_METHOD) 		{addDontRecord(value); continue;}
		if (category == DONT_EITHER)	 		{addDontEither(value); continue;}
		if (category == SOURCE_DIRECTORY) 		{addSourceDirectory(value); continue;}
		if (category == USER_SELECTED_FIELD) 		{addUserSelectedField(value); continue;}
		// if (category == CUTOFF_DEPTH) 		{setCutoff(value); continue;}
		if (category == START_PATTERN) 			{setStartPattern(value); continue;}
		if (category == STOP_PATTERN) 			{setStopPattern(value); continue;}
		if (category == SPECIAL_FORMATTER)	 	{addFormatter(value); continue;}
		// if (category == DONT_RECORD_IMMEDIATELY) 	{setStartImmediately(value); continue;}
		// if (category == START_RUNNING_IMMEDIATELY) 	{setStartRunning(value); continue;}
		// if (category == INSTRUMENT_VIA_CLASSLOADER) 	{setInstrument(value); continue;}
		// if (category == STOP_WHEN_MAIN_EXITS) 	{setStopOnExit(value); continue;}
		// if (category == PAUSE_PROGRAM_ON_STOP)	{setPauseOnStop(value); continue;}
		// if (category == DEBUG_DEBUGGER) 		{setDebug(value); continue;}
		throw new DebuggerException("Unrecognized category in defaults: " +  line);
	    }
	    br.close();
	    if (dontInstrument.size() > 2) Debugger.println("Not instrumenting "+ (dontInstrument.size()-2) + " extra methods");
	    if (dontRecord.size() > 5) Debugger.println("Not recording "+ (dontRecord.size()-5) + " extra methods");
	    return false;
	}
	catch (FileNotFoundException e) {
	    initializeDefaults();
	    writeDefaults();
	    firstRun=true;
	    return true;
	}
	catch (Exception e) {
	    Debugger.println("Problem loading defaults file: "+e + ". Aborting load.");
	    D.println("");
	    return false;
	}
    }

    static private void initializeDefaults(){
	Debugger.println("Creating new .debuggerDefaults file...");
	String[] hide_ts = {"*", "toString"};
	String[] hide_vo = {"*", "valueOf"};
	String[] hide_sb0 = {"java.lang.StringBuilder", "*"};
    String[] hide_sb1 = {"java.lang.StringBuffer", "*"};
	String[] hide_in = {"*", "new"};
	String[] hide_oi = {"java.lang.Object", "new"};
	String[] hide_cl = {"*", "<clinit>"};

	dontRecord.add(hide_ts);
	dontRecord.add(hide_vo);
    dontRecord.add(hide_sb0);
    dontRecord.add(hide_sb1);
	dontRecord.add(hide_oi);
	//dontRecord.add(hide_in);
	//dontRecord.add(hide_cl);
	dontInstrument.add(hide_ts);
	dontInstrument.add(hide_vo);
	addUserSelectedField("\"com.lambda.Debugger.DemoThing name\"");
	//	dontInstrument.add(hide_cl);
	//dontInstrument.add(hide_in);
    }

    static private void setStartLine(String value){		// "com.lambda.Debugger.Demo:Demo.java:33"
	String s =getString(value);
	Debugger.println("Recording will start on: "+s);
	//	SourceLine.setStartLine(s);
	//	D.CUTOFF_DEPTH=D.DONT_START;
    }
    static private void setStopLine(String value){		// "com.lambda.Debugger.Demo:Demo.java:33"
	String s =getString(value);
	Debugger.println("Recording will stop on: "+s);
	//	SourceLine.setStopLine(s);
    }
    static private void setStartOutput(String value){		// "Start recording when the program prints this"
	String s =getString(value);
	Debugger.println("Recording will start on output: \""+s+"\"");
	//	D.setStartString(s);
	//	D.CUTOFF_DEPTH=D.DONT_START;
    }
    static private void setStopOutput(String value){		// "Stop recording when the program prints this"
	String s =getString(value);
	Debugger.println("Recording will start on output: \""+s+"\"");
	//	D.setStopString(s);
    }
    static private void addInstrumentOnly(String value){	// "com.foo"
	instrumentOnlyPackages.add(getString(value));
    }
    static private void addDidntInstrument(String value){	// "com.foo.UnparentedToo"
	didntInstrument.add(getString(value));
    }
    static private void addDontInstrumentMethod(String value){	// "com.foo.UnparentedToo frob"
	dontInstrument.add(getStrings(value));
    }
    static private void addDontRecord(String value){		// "com.foo.UnparentedToo frob"
	dontRecord.add(getStrings(value));
    }
    static private void addDontEither(String value){		// "com.foo.UnparentedToo frob"
	String[] s = getStrings(value);
	dontRecord.add(s);
	dontInstrument.add(s);
    }
    static private void addSourceDirectory(String value){	// "/Users/billewis/org/apache/bcel/"
	SourceFileFinder.sourceDirectories.add(getString(value));
    }
    static private void addUserSelectedField(String value){	// "com.lambda.Debugger.DemoThing name"
	String s[] = getStrings(value);
	ClassInformation.addUserSelectedField(s[0], s[1]);
    }
    static private void setCutoff(String value){		// 0
	int i = getInt(value);
	if (i > 0) {
	    //	    Debugger.println("Only calls less than "+i+" deep will be recorded (and no assignments).");
	    Debugger.PAUSED=true;
	    //D.CUTOFF_DEPTH = i;
	}
    }
    static private void setStartPattern(String value){		// 0
	EventInterface.setStartPatternString(value.substring(1, value.length()-1));		// Strip off opening ""
    }
    static private void setStopPattern(String value){		// 0
	EventInterface.setStopPatternString(value.substring(1, value.length()-1));		// Strip off opening ""
    }
    static private void addFormatter(String value){	
	SpecialFormatters.add(value);
    }
    static private void setMemory(String value){		// 0
	TimeStamp.setMax(getInt(value));
    }
    static private void setStartImmediately(String value){	// true
	Debugger.PAUSED = getBoolean(value);
    }
    static private void setStartRunning(String value){		// true
	Debugger.START = getBoolean(value);
    }
    static private void setInstrument(String value){		// true
	Debugger.INSTRUMENT = getBoolean(value);
    }
    /*
    static private void setStopOnExit(String value){		// true
	Debugger.SHOW = getBoolean(value);
    }
    static private void setPauseOnStop(String value){		// true
	Debugger.PAUSE_ON_STOP = getBoolean(value);
    }
    static private void setDebug(String value){		// true
	Debugger.DEBUG_DEBUGGER = getBoolean(value);
    }
*/

    static private boolean getBoolean(String value){
	if (value.length() < 4) throw new DebuggerException("Not a boolean: " +  value);
	String s = value.substring(0, 4);
	if (s.equals("true")) return true;
	if (s.equals("fals")) return false;
	throw new DebuggerException("Not a boolean: " +  value);
    }

    static private int getInt(String value){
	if (value.length() < 1) throw new DebuggerException("Not a number: " +  value);
	int firstSpace = value.indexOf(' ');
	int firstTab = value.indexOf('\t');
	if ((firstTab > 0) && (firstTab < firstSpace)) firstSpace = firstTab;
	if (firstSpace != -1) value=value.substring(0, firstSpace);
	int i = Integer.parseInt(value);
	return i;
    }

    static private String getString(String value) {
	if (value.length() < 1) throw new DebuggerException("Not a quoted string: " +  value);
	if (value.charAt(0) != '"') throw new DebuggerException("Not a quoted string: " +  value);
	int lastQuote = value.indexOf('"', 1);
	if (lastQuote == -1)  throw new DebuggerException("Not a quoted string: " +  value);
	String s = value.substring(1, lastQuote);
	return s;
    }


    static private String[] getStrings(String value) {
	if (value.length() < 1) throw new DebuggerException("Not a quoted string: " +  value);
	if (value.charAt(0) != '"') throw new DebuggerException("Not a quoted string: " +  value);

	int firstSpace = value.indexOf(' ');
	int firstTab = value.indexOf('\t');
	if ((firstTab > 0) && (firstTab < firstSpace)) firstSpace = firstTab;
	if (firstSpace == -1) throw new DebuggerException("Not a quoted string: " +  value);

	String s1 = value.substring(1, firstSpace);
	int lastQuote = value.indexOf('"', 1);
	if (lastQuote == -1)  throw new DebuggerException("Not a quoted string: " +  value);
	String s2 = value.substring(firstSpace, lastQuote);
	s2 = s2.trim();
	return new String[] {s1, s2};
    }

    static public void writeDefaults(){
	try {
	    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(defaultsFile)));
	    w.write("# ODB Defaults "+ Debugger.version +" -- You may edit by hand. See Manual for details\n\n");
	    w.write("#                        Class & method names must be complete. '*' must be freestanding.\n");
	    w.write("# DidntInstrument:       This is informative only. (You may change to InstrumentOnly.)\n");
	    w.write("# DontInstrumentMethod:  These methods won't be instrumented (but may be recorded).\n");
	    w.write("# DontRecordMethod:      These methods won't be recorded (ie, from the calling method).\n");
	    w.write("# DontEither:            These methods won't be recorded or instrumented.\n");
	    w.write("# MaxTimeStamps:         This is overridded by the command line argument, hence seldom used.\n");
	    w.write("# StartPattern:          Recording will start when this pattern is matched.\n");
	    w.write("# StopPattern:           Recording will stop when this pattern is matched (no restarts!).\n");
	    w.write("# SourceDirectory:       If sources can't be found normally, look here.\n");
	    w.write("# OnlyInstrument:        Only classes which match this prefix will be instrumented.\n");
	    w.write("# OnlyInstrument:        \"\" means default package only. No entry means everything.\n");
	    w.write("# UserSelectedField:     This instance variable (a final String) will be appended to the display string\n");
	    w.write("# UserSelectedField:     \"com.lambda.Thing name\"  ->   <Thing_23 John>\n");
	    w.write("# SpecialFormatter:      com.lambda.Debugger.SpecialTimeStampFormatter\n");
	    w.write("\n");
	    writeDefaultInt(w, MAX_MEMORY, TimeStamp.MAX_TIMESTAMPS);
	    /*	    if (D.CUTOFF_DEPTH == D.DONT_START)
		writeDefaultInt(w, CUTOFF_DEPTH, 0);
	    else
		writeDefaultInt(w, CUTOFF_DEPTH, D.CUTOFF_DEPTH);
	    */
	    writeDefaultString(w, START_PATTERN, EventInterface.getStartPatternString());
	    writeDefaultString(w, STOP_PATTERN, EventInterface.getStopPatternString());
	    //	    writeDefaultString(w, START_ON_LINE, D.startLine);
	    //	    writeDefaultString(w, STOP_ON_LINE, D.stopLine);
	    //	    writeDefaultString(w, START_ON_OUTPUT, D.startString);
	    //	    writeDefaultString(w, STOP_ON_OUTPUT, D.stopString);
	    writeDefaultStrings(w, SOURCE_DIRECTORY, SourceFileFinder.sourceDirectories);
	    writeDefaultStrings(w, INSTRUMENT_ONLY, instrumentOnlyPackages);
	    writeDefaultStrings(w, DIDNT_INSTRUMENT, didntInstrument);
	    //	    writeDefaultBoolean(w, DEBUG_DEBUGGER, Debugger.DEBUG_DEBUGGER);
	    //	    writeDefaultBoolean(w, DONT_RECORD_IMMEDIATELY, Debugger.PAUSED);
	    //	    writeDefaultBoolean(w, INSTRUMENT_VIA_CLASSLOADER, Debugger.INSTRUMENT);
	    //	    writeDefaultBoolean(w, STOP_WHEN_MAIN_EXITS, Debugger.SHOW);
	    //	    writeDefaultBoolean(w, PAUSE_PROGRAM_ON_STOP, Debugger.PAUSE_ON_STOP);
	    //	    writeDefaultBoolean(w, START_RUNNING_IMMEDIATELY, Debugger.START);
	    writeDefaultStringPairs(w, DONT_INSTRUMENT_METHOD, ClassObjectFilter.getFilteredMethods());
	    writeDefaultStringPairs(w, DONT_INSTRUMENT_METHOD, dontInstrument);
	    writeDefaultStringPairs(w, DONT_RECORD_METHOD, ClassObjectFilter.getFilteredMethods());
	    writeDefaultStringPairs(w, DONT_RECORD_METHOD, dontRecord);
	    writeDefaultStringPairs(w, USER_SELECTED_FIELD, ClassInformation.getUserSelectedFields());
	    w.close();
	}
	catch (IOException e) {
	    Debugger.message("Could not save file " + defaultsFile, true);
	    return;
	}
    }

    private static void writeDefaultBoolean(BufferedWriter w, String s, boolean z) throws IOException {
	if (s.length() < 16) s+="\t";
	if (s.length() < 24) s+="\t";
	w.write(s+"" + z + "\n");
    }


    private static void writeDefaultInt(BufferedWriter w, String s, int i) throws IOException {
	if (s.length() < 16) s+="\t";
	if (s.length() < 24) s+="\t";
	w.write(s+"" + i + "\n");
    }


    private static void writeDefaultString(BufferedWriter w, String s, String value) throws IOException {
	if (s.length() < 16) s+="\t";
	if (s.length() < 24) s+="\t";
	if (value == null)
	    w.write(s+"\n");
	else
	    w.write(s + "\"" + value +"\"\n");
    }

    private static void writeDefaultStrings(BufferedWriter w, String s, VectorD v) throws IOException {
	if (v == null) {w.write(s+"\n"); return;}
	int len = v.size();
	if (s.length() < 16) s+="\t";
	if (s.length() < 24) s+="\t";
	if (len == 0) w.write(s+"\n");
	for (int i = 0; i < len; i++) {
	    String ss = (String) v.elementAt(i);
	    w.write(s + "\"" + ss  +"\"\n");
	}
    }

    private static void writeDefaultStringPairs(BufferedWriter w, String s, VectorD v) throws IOException {
	if (v == null) {w.write(s+"\n"); return;}
	int len = v.size();
	if (s.length() < 16) s+="\t";
	if (s.length() < 24) s+="\t";
	if (len == 0) w.write(s+"\n");
	for (int i = 0; i < len; i++) {
	    String[] ss = (String[]) v.elementAt(i);
	    w.write(s + "\"" + ss[0] + "\t " + ss[1]  +"\"\n");
	}
    }

    public static void addIOP(String packageName) {
	instrumentOnlyPackages.add(packageName);
	writeDefaults();
    }
}
