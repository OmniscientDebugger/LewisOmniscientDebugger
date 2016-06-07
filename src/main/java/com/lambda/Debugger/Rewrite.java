package com.lambda.Debugger;

import java.io.*;
import java.util.*;


/*
  Read in a file and construct frequency tables for word order. Then use those table
  to construct new "sentences".
*/

public class Rewrite {
    static String[]             fileNames = {};
    static String               PERIOD = ".";
    static int                  MAX = 5;
    static int                  inputIndex, len, nSentencesToGenerate = 100;
    private static String       input;
    static Analysis             analysis = new Analysis();



    public static void main(String[] args) {
        //      if (args.length > 0) nSentencesToGenerate = Integer.parseInt(args[0]);
        if (args.length > 0) fileNames = args;

        long start = System.currentTimeMillis();

        if (fileNames.length == 0) {
            String s = RewriteManuscript.manuscriptString;
            System.out.println("******** Reading and analyzing manuscript string ********");
            analyze(s);
        }
            

        for (int i = 0; i < fileNames.length; i++) {
            String s = readFile(fileNames[i]);
            System.out.println("******** Reading and analyzing file: "
                               +fileNames[i] + "********");
            analyze(s);
        }
        analysis.calculateStatistics();
        System.out.println("******** Creating a new masterpiece of literature... *******");
        generateSentences();

        long end = System.currentTimeMillis();
        long total = end-start;
        System.out.println(""+ total + "ms");

    }


    public static String generateSentence() {
        ArrayList al = new ArrayList();
        String s;

        s = analysis.choose(al, 0);
        al.add(s);
        s = analysis.choose(al, 1);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 1);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 2);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 3);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 4);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 3);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 3);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 1);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 4);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 4);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 3);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 2);
        al.add(s);
        if (s == PERIOD) return concat(al);
        s = analysis.choose(al, 2);
        al.add(s);
        if (s == PERIOD) return concat(al);

        al.add(PERIOD);
        return concat(al);
    }

    public static void generateSentences() {
        for (int i = 0; i < nSentencesToGenerate; i++) {
            System.out.println(generateSentence());
        }
    }

    public static String readFile(String fileName) {            // returns one long string
        try {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(" ");
                sb.append(line);
            }
            return sb.toString();
        }
        catch (IOException e) {System.out.println(e); return "";}
    }


    public static Analysis analyze(String s) {          // Build tables
        input = s;
        len = input.length();
        inputIndex = 0;

        while (inputIndex < len) {analyzeNext();}
        return analysis;
    }

    private static void analyzeNext() {
        ArrayList previous = new ArrayList();
        String token = nextToken();
        analysis.add("", token, 0);                     // Insert 1st token directly.
        previous.add(token);

        while (true) {
            int nWords = Math.min(previous.size(), MAX);
            token = nextToken();
            if (token == PERIOD) break;

            for (int j = 1; j <= nWords; j++) {
                String s1 = concat(previous, j);        // ({"a", "b", "c"}, 2) -> "b c"
                analysis.add(s1, token, j);
            }

            previous.add(token);
            if (previous.size() == MAX)  previous.remove(0);
        }


        int nWords = Math.min(previous.size(), MAX);
        for (int j = 1; j < nWords; j++) {
            String s1 = concat(previous, j);            // ({"a", "b", "c"}, 2) -> "b c"
            analysis.add(s1, PERIOD, j);
        }
    }



    public static String concat(ArrayList al) {
        return concat(al, al.size());
    }

    public static String concat(ArrayList al, int n) {  // ({"a", "b", "c"}, 2) -> "b c"
        //StringBuffer sb = new StringBuffer();         // ({"a", "b", "c"}, 7) -> "a b c"
        String s = "";                                  // Easier to debug than SBs.
        int size = al.size();
        if (size < n) n = size;
        for (int i = size - n; i < size; i++) {
            String token = (String) al.get(i);
            //sb.append(token);
            s+=token;
            if ((i+2 < size) && !token.equals(""))  s += " "; //sb.append(" ");
        }
        return s; //sb.toString();
    }


    private static void skipWhiteSpace() {
        while(true) {
            if (inputIndex >= len) return;
            char c = input.charAt(inputIndex);
            if (c != ' ') return;
            inputIndex++;
        }
    }


    private static String nextToken() {
        skipWhiteSpace();
        int nextSpace = input.indexOf(' ', inputIndex);
        if (nextSpace == -1) {
            if (inputIndex >= len) return PERIOD;
            nextSpace = len;
        }
        String token = input.substring(inputIndex, nextSpace);
        inputIndex = nextSpace+1;
        if (token.equals(".")) return PERIOD;
        return token.intern();
    }

}




class Analysis {
    // maps[3]: {"now is the" -> <PrefixEntry>, ...}
    // maps[2]: {"is the" -> <PrefixEntry>, ...}, etc.

    Hashtable[]                 maps = new Hashtable[Rewrite.MAX];      
    int[]                       nEntries = new int[Rewrite.MAX];        
    public static Random        random = new Random();
    String                      markerWord = "!MarkerWord!";    // Show these words BOLD
    String                      markerWord2;                    // Not used yet.
    ArrayList                   prefixList = new ArrayList();


    public Analysis() {
        for (int i = 0; i < Rewrite.MAX; i++) maps[i] = new Hashtable();
    }

    void add(String previous, String newWord, int n) {  //"now is the" -> <PrefixEntry"time">
        Hashtable hm = maps[n];
        nEntries[n]++;
        PrefixEntry e = (PrefixEntry) hm.get(previous);
        if (e == null) {
            e = new PrefixEntry(previous);
            hm.put(previous, e);
        }
        e.add(newWord);
    }



    public String choose(int i) {                           // Choose a starting word
        Hashtable hm = maps[i];
        int ran = Math.abs(random.nextInt() % nEntries[i]);
        int sum = 0;

        Iterator iter = hm.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            PrefixEntry e = (PrefixEntry) hm.get(key);
            sum += e.nEntries;
            if (ran < sum) return e.prefix;
        }
        return "";
    }



    public String choose(ArrayList al, int i) {             // Choose next word based on
        Hashtable hm = maps[i];                             // previous i words in al
        String prefix = Rewrite.concat(al, i);
        PrefixEntry e = (PrefixEntry) hm.get(prefix);

        if (e == null) return "";
        return e.choose();
    }


    public void calculateStatistics() {                     // Just a bunch of numbers 
        for (int i = 0; i < Rewrite.MAX; i++) {
            markerWord = markerWord2;
            Hashtable hm = maps[i];
            Iterator iter = hm.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                PrefixEntry e = (PrefixEntry) hm.get(key);
                e.calculateStatistics(this);
            }
        }
    }


    public void inc(String s) { prefixList.add(s); }
    public void getMarkerWord(WordEntry we) { we.set(markerWord); }


    public void dump() {
        System.out.println(this);
        for (int i = 0; i < Rewrite.MAX; i++) {
            Hashtable hm = maps[i];
            Iterator iter = hm.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                PrefixEntry e = (PrefixEntry) hm.get(key);
                e.dump();
            }
            System.out.println("--------------------------------");
        }
    }
}






class PrefixEntry {                             // Continuations based on previous n words.

    String prefix;                              // "now is the", "is the", "the", etc.
    int nEntries = 0;                           // Number of times this prefix appeared
    Hashtable completions = new Hashtable();    // {"time" -> <WordEntry "time" 0 2>, ...}

    public PrefixEntry(String s) {prefix = s;}

    public void add(String word) {
        WordEntry we = (WordEntry) completions.get(word);
        if (we == null) {
            we = new WordEntry(word);
            completions.put(word, we);
        }
        we.inc();
        nEntries++;
    }


    public String choose() {
        int ran = Math.abs(Analysis.random.nextInt() % nEntries);
        Iterator iter = completions.keySet().iterator();
        int sum = 0;

        while (iter.hasNext()) {
            String key = (String) iter.next();
            WordEntry we = (WordEntry) completions.get(key);
            if (we == null) throw new NullPointerException();
            sum += we.getnEntries();
            if (ran <= sum) return we.word;
        }
        throw new NullPointerException("Cannot get here");
    }



    public void calculateStatistics(Analysis analysis) {
        Iterator iter = completions.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            WordEntry we = (WordEntry) completions.get(key);
            we.calculateStatistics(analysis);
        }
    }


    public void dump() {
        System.out.println(this);
        Iterator iter = completions.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            System.out.println("\t" + key + "\t\t\t"+completions.get(key));
        }
    }

    public String toString() {return "<PrefixEntry '"+prefix+"' "+nEntries+">";}

}






class WordEntry {                               // Hashtable entries for individual words
    int nEntries = 0;                           // no. of times encountered
    String word = null;


    public WordEntry(String s) {word = s;}
    public void inc() {nEntries++;}
    public void set(String s) {word = s;}
    public int getnEntries() {return nEntries;}

    public void calculateStatistics(Analysis analysis) {// Build frequency charts
        if (word.startsWith("re")) {
            analysis.inc("re");
        }
        if (word.startsWith("pre")) {
            analysis.inc("pre");
        }
        if (word.startsWith("pro")) {
            analysis.inc("pro");
            word = "*"+word+"*";                        // Mark special words
        }
        if (word.length() > 4) {
            if (word.startsWith("de") && word.endsWith("ed")) {
                analysis.inc("de-ed");
                //word = word.upperCase();
            }
            if (word.startsWith("Vine")) {
                analysis.inc("markerWord");
                analysis.getMarkerWord(this);
            }
            if (word.startsWith("marker")) {
                analysis.inc("markerWord");
                analysis.getMarkerWord(this);
            }
        }

    }
    public String toString() {return "<WordEntry '"+word+"' "+nEntries+">";}

}


class RewriteManuscript {
    
    static String manuscriptString = "" +
        "Palun Yuri whispered holding out a spoonfull of wurst to the cat . Please" +
        "this is just for you . " +
        "The cat a mangy old creature that had inhabited this crevice in the refuge " +
        "for as long as Yuri could remember stared at the boy suspiciously . Yuri " +
        "held very still and repeated Palun " +
        "Never taking its eyes off of Yuri the cat crept forward sniffed at the " +
        "wrust as if it expected it to be poisoned then snatched it off the spoon " +
        "and retreated to the safety of the crevice . " +
        "The cat bit into the wurst several times before finally swallowing it . It " +
        "never took its eyes off of Yuri . " +
        "Head Istu Yuri told the cat Bon Appetite . Yuri dug out another spoonful " +
        "of wurst and held it out to the cat . Did the cat care that Yuri spoke " +
        "Estonian with a slight Russian accent .  Maybe . In two years the cat had " +
        "never let Yuri touch it . It always grabbed the food from the spoon and " +
        "pulled back . If Yuri raised his hand to reach out towards the cat it would " +
        "arch its back and hiss . So Yuri never tried any more .  " +
        "It's ok cat . You're just like me . People are mean to us marker because we're " +
        "small and ugly . The other boys hit me too . Yuri pulled back the hood " +
        "of his parka so the cat could see the small brown bruises where Karl and had " +
        "hit him that morning on the way to school . " +
        "I know what it's like for you . So don't worry . I'm your friend . We can be " +
        "friends from two meters away . Yuri dug out some more wurst . " +
        "The little hidden alcove in the courtyard off of Ruutli had been Yuri's " +
        "refuge after school for two years now . His mother gave piano lessons in " +
        "their tiny apartment on the other side of town and Yuri wasn't allowed to " +
        "come home until the last student was done . Yuri's father wanted him to go to " +
        "the ice hocky practice at the Linna Hall but Yuri hated it . " +
        "What kind of son are you .  His father would yell at him . I played for the " +
        "St . Petersburg city champions and you can't even handle a stick .   We are " +
        "Russians Yuri .  We are proud . We are strong . We are not afraid of anything .  " +
        "Do you hear me Yuri .   We lost twenty million men fighting the fascists but " +
        "we beat them . " +
        "His father would start pounding the table whenever he talked about the " +
        "war . All four of my grandparents died in the siege .  They gave their marker lives " +
        "for mother Russia .  They died so you could ge free .  If there was any vodka " +
        "his father would down a swig and start talking about the 900 day siege . He " +
        "could talk for hours about the bravery of the St . Petersburg citizens in " +
        "resisting the fasists never Germans aways fascists) . " +
        "Then his father would talk about how hard his childhood had been about " +
        "never having enough to eat about the cold and of course about the marker hocky " +
        "team . \n";
}
