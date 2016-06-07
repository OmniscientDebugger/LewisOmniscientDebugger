/*                        RewriteString.java

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

// This is a really doppy way of doing this! (BUT IT WORKS)

public class RewriteString {
  static String programString = 
"package com.lambda.Debugger; \n" +
" \n" +
"import java.io.*; \n" +
"import java.util.*; \n" +
" \n" +
" \n" +
"/* \n" +
"  Read in a file and construct frequency tables for word order. Then use those table \n" +
"  to construct new \"sentences\". \n" +
"*/ \n" +
" \n" +
"public class Rewrite { \n" +
"    static String[] 		fileNames = {}; \n" +
"    static String		PERIOD = \".\"; \n" +
"    static int 			MAX = 5; \n" +
"    static int			inputIndex, len, nSentencesToGenerate = 100; \n" +
"    private static String 	input; \n" +
"    static Analysis		analysis = new Analysis(); \n" +
" \n" +
" \n" +
" \n" +
"    public static void main(String[] args) { \n" +
"	//	if (args.length > 0) nSentencesToGenerate = Integer.parseInt(args[0]); \n" +
"	if (args.length > 0) fileNames = args; \n" +
" \n" +
"	long start = System.currentTimeMillis(); \n" +
" \n" +
"	if (fileNames.length == 0) { \n" +
"	    String s = RewriteManuscript.manuscriptString; \n" +
"	    System.out.println(\"******** Reading and analyzing manuscript string ********\"); \n" +
"	    analyze(s); \n" +
"	} \n" +
"	     \n" +
" \n" +
"	for (int i = 0; i < fileNames.length; i++) { \n" +
"	    String s = readFile(fileNames[i]); \n" +
"	    System.out.println(\"******** Reading and analyzing file: \" \n" +
"			       +fileNames[i] + \"********\"); \n" +
"	    analyze(s); \n" +
"	} \n" +
"	analysis.calculateStatistics(); \n" +
"	System.out.println(\"******** Creating a new masterpiece of literature... *******\"); \n" +
"	generateSentences(); \n" +
" \n" +
"	long end = System.currentTimeMillis(); \n" +
"	long total = end-start; \n" +
"	System.out.println(\"\"+ total + \"ms\"); \n" +
" \n" +
"    } \n" +
" \n" +
" \n" +
"    public static String generateSentence() { \n" +
"	ArrayList al = new ArrayList(); \n" +
"	String s; \n" +
" \n" +
"	s = analysis.choose(al, 0); \n" +
"	al.add(s); \n" +
"	s = analysis.choose(al, 1); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 1); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 2); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 3); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 4); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 3); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
" 	s = analysis.choose(al, 3); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 1); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 4); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 4); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 3); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 2); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
"	s = analysis.choose(al, 2); \n" +
"	al.add(s); \n" +
"	if (s == PERIOD) return concat(al); \n" +
" \n" +
"	al.add(PERIOD); \n" +
"	return concat(al); \n" +
"    } \n" +
" \n" +
"    public static void generateSentences() { \n" +
"	for (int i = 0; i < nSentencesToGenerate; i++) { \n" +
"	    System.out.println(generateSentence()); \n" +
"	} \n" +
"    } \n" +
" \n" +
"    public static String readFile(String fileName) {		// returns one long string \n" +
"	try { \n" +
"	    StringBuffer sb = new StringBuffer(); \n" +
"	    BufferedReader br = new BufferedReader(new FileReader(fileName)); \n" +
"	    String line; \n" +
"	    while ((line = br.readLine()) != null) { \n" +
"		sb.append(\" \"); \n" +
"		sb.append(line); \n" +
"	    } \n" +
"	    return sb.toString(); \n" +
"	} \n" +
"	catch (IOException e) {System.out.println(e); return \"\";} \n" +
"    } \n" +
" \n" +
" \n" +
"    public static Analysis analyze(String s) {		// Build tables \n" +
"	input = s; \n" +
"	len = input.length(); \n" +
"	inputIndex = 0; \n" +
" \n" +
"	while (inputIndex < len) {analyzeNext();} \n" +
"	return analysis; \n" +
"    } \n" +
" \n" +
"    private static void analyzeNext() { \n" +
"	ArrayList previous = new ArrayList(); \n" +
"	String token = nextToken(); \n" +
"	analysis.add(\"\", token, 0);			// Insert 1st token directly. \n" +
"	previous.add(token); \n" +
" \n" +
"	while (true) { \n" +
"	    int nWords = Math.min(previous.size(), MAX); \n" +
"	    token = nextToken(); \n" +
"	    if (token == PERIOD) break; \n" +
" \n" +
"	    for (int j = 1; j <= nWords; j++) { \n" +
"		String s1 = concat(previous, j);	// ({\"a\", \"b\", \"c\"}, 2) -> \"b c\" \n" +
"		analysis.add(s1, token, j); \n" +
"	    } \n" +
" \n" +
"	    previous.add(token); \n" +
"	    if (previous.size() == MAX)  previous.remove(0); \n" +
"	} \n" +
" \n" +
" \n" +
"	int nWords = Math.min(previous.size(), MAX); \n" +
"	for (int j = 1; j < nWords; j++) { \n" +
"	    String s1 = concat(previous, j);		// ({\"a\", \"b\", \"c\"}, 2) -> \"b c\" \n" +
"	    analysis.add(s1, PERIOD, j); \n" +
"	} \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public static String concat(ArrayList al) { \n" +
"	return concat(al, al.size()); \n" +
"    } \n" +
" \n" +
"    public static String concat(ArrayList al, int n) {	// ({\"a\", \"b\", \"c\"}, 2) -> \"b c\" \n" +
"	//StringBuffer sb = new StringBuffer();		// ({\"a\", \"b\", \"c\"}, 7) -> \"a b c\" \n" +
"	String s = \"\";					// Easier to debug than SBs. \n" +
"	int size = al.size(); \n" +
"	if (size < n) n = size; \n" +
"	for (int i = size - n; i < size; i++) { \n" +
"	    String token = (String) al.get(i); \n" +
"	    //sb.append(token); \n" +
"	    s+=token; \n" +
"	    if ((i+2 < size) && !token.equals(\"\"))  s += \" \"; //sb.append(\" \"); \n" +
"	} \n" +
"	return s; //sb.toString(); \n" +
"    } \n" +
" \n" +
" \n" +
"    private static void skipWhiteSpace() { \n" +
"	while(true) { \n" +
"	    if (inputIndex >= len) return; \n" +
"	    char c = input.charAt(inputIndex); \n" +
"	    if (c != ' ') return; \n" +
"	    inputIndex++; \n" +
"	} \n" +
"    } \n" +
" \n" +
" \n" +
"    private static String nextToken() { \n" +
"	skipWhiteSpace(); \n" +
"	int nextSpace = input.indexOf(' ', inputIndex); \n" +
"	if (nextSpace == -1) { \n" +
"	    if (inputIndex >= len) return PERIOD; \n" +
"	    nextSpace = len; \n" +
"	} \n" +
"	String token = input.substring(inputIndex, nextSpace); \n" +
"	inputIndex = nextSpace+1; \n" +
"	if (token.equals(\".\")) return PERIOD; \n" +
"	return token.intern(); \n" +
"    } \n" +
" \n" +
"} \n" +
" \n" +
" \n" +
" \n" +
" \n" +
"class Analysis { \n" +
"    // maps[3]: {\"now is the\" -> <PrefixEntry>, ...} \n" +
"    // maps[2]: {\"is the\" -> <PrefixEntry>, ...}, etc. \n" +
" \n" +
"    Hashtable[] 		maps = new Hashtable[Rewrite.MAX];	 \n" +
"    int[] 			nEntries = new int[Rewrite.MAX];	 \n" +
"    public static Random 	random = new Random(); \n" +
"    String			markerWord = \"!MarkerWord!\";	// We need to show these words BOLD \n" +
"    String			markerWord2;			// Not used yet. \n" +
"    ArrayList			prefixList = new ArrayList(); \n" +
" \n" +
" \n" +
"    public Analysis() { \n" +
"	for (int i = 0; i < Rewrite.MAX; i++) maps[i] = new Hashtable(); \n" +
"    } \n" +
" \n" +
"    void add(String previous, String newWord, int n) {	//\"now is the\" -> <PrefixEntry\"time\"> \n" +
"	Hashtable hm = maps[n]; \n" +
"	nEntries[n]++; \n" +
"	PrefixEntry e = (PrefixEntry) hm.get(previous); \n" +
"	if (e == null) { \n" +
"	    e = new PrefixEntry(previous); \n" +
"	    hm.put(previous, e); \n" +
"	} \n" +
"	e.add(newWord); \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public String choose(int i) {				// Choose a starting word \n" +
"	Hashtable hm = maps[i]; \n" +
"	int ran = Math.abs(random.nextInt() % nEntries[i]); \n" +
"	int sum = 0; \n" +
" \n" +
"	Iterator iter = hm.keySet().iterator(); \n" +
"	while (iter.hasNext()) { \n" +
"	    String key = (String) iter.next(); \n" +
"	    PrefixEntry e = (PrefixEntry) hm.get(key); \n" +
"	    sum += e.nEntries; \n" +
"	    if (ran < sum) return e.prefix; \n" +
"	} \n" +
"	return \"\"; \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public String choose(ArrayList al, int i) {			// Choose next word based on \n" +
"	Hashtable hm = maps[i];					// previous i words in al \n" +
"	String prefix = Rewrite.concat(al, i); \n" +
"	PrefixEntry e = (PrefixEntry) hm.get(prefix); \n" +
" \n" +
"	if (e == null) return \"\"; \n" +
"	return e.choose(); \n" +
"    } \n" +
" \n" +
" \n" +
"    public void calculateStatistics() {				// Just a bunch of numbers  \n" +
"	for (int i = 0; i < Rewrite.MAX; i++) { \n" +
"	    markerWord = markerWord2; \n" +
"	    Hashtable hm = maps[i]; \n" +
"	    Iterator iter = hm.keySet().iterator(); \n" +
"	    while (iter.hasNext()) { \n" +
"		String key = (String) iter.next(); \n" +
"		PrefixEntry e = (PrefixEntry) hm.get(key); \n" +
"		e.calculateStatistics(this); \n" +
"	    } \n" +
"	} \n" +
"    } \n" +
" \n" +
" \n" +
"    public void inc(String s) { prefixList.add(s); } \n" +
"    public void getMarkerWord(WordEntry we) { we.set(markerWord); } \n" +
" \n" +
" \n" +
"    public void dump() { \n" +
"	System.out.println(this); \n" +
"	for (int i = 0; i < Rewrite.MAX; i++) { \n" +
"	    Hashtable hm = maps[i]; \n" +
"	    Iterator iter = hm.keySet().iterator(); \n" +
"	    while (iter.hasNext()) { \n" +
"		String key = (String) iter.next(); \n" +
"		PrefixEntry e = (PrefixEntry) hm.get(key); \n" +
"		e.dump(); \n" +
"	    } \n" +
"	    System.out.println(\"--------------------------------\"); \n" +
"	} \n" +
"    } \n" +
"} \n" +
" \n" +
" \n" +
" \n" +
" \n" +
" \n" +
" \n" +
"class PrefixEntry {				// Possible continuations based on previous n words. \n" +
" \n" +
"    String prefix;				// \"now is the\", \"is the\", \"the\", etc. \n" +
"    int nEntries = 0;				// Number of times this prefix appeared \n" +
"    Hashtable completions = new Hashtable();	// {\"time\" -> <WordEntry \"time\" 0 2>, ...} \n" +
" \n" +
"    public PrefixEntry(String s) {prefix = s;} \n" +
" \n" +
"    public void add(String word) { \n" +
"	WordEntry we = (WordEntry) completions.get(word); \n" +
"	if (we == null) { \n" +
"	    we = new WordEntry(word); \n" +
"	    completions.put(word, we); \n" +
"	} \n" +
"	we.inc(); \n" +
"	nEntries++; \n" +
"    } \n" +
" \n" +
" \n" +
"    public String choose() { \n" +
"	int ran = Math.abs(Analysis.random.nextInt() % nEntries); \n" +
"	Iterator iter = completions.keySet().iterator(); \n" +
"	int sum = 0; \n" +
" \n" +
"	while (iter.hasNext()) { \n" +
"	    String key = (String) iter.next(); \n" +
"	    WordEntry we = (WordEntry) completions.get(key); \n" +
"	    if (we == null) throw new NullPointerException(); \n" +
"	    sum += we.getnEntries(); \n" +
"	    if (ran <= sum) return we.word; \n" +
"	} \n" +
"	throw new NullPointerException(\"Cannot get here\"); \n" +
"    } \n" +
" \n" +
" \n" +
" \n" +
"    public void calculateStatistics(Analysis analysis) { \n" +
"	Iterator iter = completions.keySet().iterator(); \n" +
"	while (iter.hasNext()) { \n" +
"	    String key = (String) iter.next(); \n" +
"	    WordEntry we = (WordEntry) completions.get(key); \n" +
"	    we.calculateStatistics(analysis); \n" +
"	} \n" +
"    } \n" +
" \n" +
" \n" +
"    public void dump() { \n" +
"	System.out.println(this); \n" +
"	Iterator iter = completions.keySet().iterator(); \n" +
"	while (iter.hasNext()) { \n" +
"	    String key = (String) iter.next(); \n" +
"	    System.out.println(\"\t\" + key + \"\t\t\t\"+completions.get(key)); \n" +
"	} \n" +
"    } \n" +
" \n" +
"    public String toString() {return \"<PrefixEntry '\"+prefix+\"' \"+nEntries+\">\";} \n" +
" \n" +
"} \n" +
" \n" +
" \n" +
" \n" +
" \n" +
" \n" +
" \n" +
"class WordEntry {					// Hashtable entries for individual words \n" +
"    int nEntries = 0;					// no. of times encountered \n" +
"    String word = null; \n" +
" \n" +
" \n" +
"    public WordEntry(String s) {word = s;} \n" +
"    public void inc() {nEntries++;} \n" +
"    public void set(String s) {word = s;} \n" +
"    public int getnEntries() {return nEntries;} \n" +
" \n" +
"    public void calculateStatistics(Analysis analysis) {// Build frequency charts \n" +
"	if (word.startsWith(\"re\")) { \n" +
"	    analysis.inc(\"re\"); \n" +
"	} \n" +
"	if (word.startsWith(\"pre\")) { \n" +
"	    analysis.inc(\"pre\"); \n" +
"	} \n" +
"	if (word.startsWith(\"pro\")) { \n" +
"	    analysis.inc(\"pro\"); \n" +
"	    word = \"*\"+word+\"*\";			// Mark special words \n" +
"	} \n" +
"	if (word.length() > 4) { \n" +
"	    if (word.startsWith(\"de\") && word.endsWith(\"ed\")) { \n" +
"		analysis.inc(\"de-ed\"); \n" +
"		//word = word.upperCase(); \n" +
"	    } \n" +
"	    if (word.startsWith(\"Vine\")) { \n" +
"		analysis.inc(\"markerWord\"); \n" +
"		analysis.getMarkerWord(this); \n" +
"	    } \n" +
"	    if (word.startsWith(\"marker\")) { \n" +
"		analysis.inc(\"markerWord\"); \n" +
"		analysis.getMarkerWord(this); \n" +
"	    } \n" +
"	} \n" +
" \n" +
"    } \n" +
"    public String toString() {return \"<WordEntry '\"+word+\"' \"+nEntries+\">\";} \n" +
" \n" +
"} \n" +
"\n";
}
