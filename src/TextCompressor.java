/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/


import java.util.ArrayList;

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, YOUR NAME HERE
 */
public class TextCompressor {

    private static final int EOF = 256;
    private static final int CODE_LENGTH = 12;
    private static final int POSSIBLE_CODES = (int)Math.pow(2, CODE_LENGTH);

    private static void compress() {
        TST codes = new TST();
        String file = BinaryStdIn.readString();
        int fileLength = file.length();
        String prefix;
        int codeNumber = EOF + 1;
        int index = 0;
        for(int i = 0; i < 256; i++){
            codes.insert("" + (char)i, i);
        }
        while(index < fileLength){
            prefix = codes.getLongestPrefix(file, index);
            BinaryStdOut.write(codes.lookup(prefix), CODE_LENGTH);
            if(index + prefix.length() + 1 < fileLength){
                if(codeNumber < POSSIBLE_CODES) {
                    codes.insert(prefix + file.charAt(index + prefix.length()), codeNumber);
                    codeNumber++;
                }
            }
            index += prefix.length();
        }
        BinaryStdOut.write(EOF, CODE_LENGTH);
        BinaryStdOut.close();
    }

    private static void expand() {
        String[] prefixes = new String[POSSIBLE_CODES];
        int codeNumber = EOF + 1;
        for(int i = 0; i < 256; i++){
            prefixes[i] = "" + (char)i;
        }
        int currentCode;
        int lookaheadCode = BinaryStdIn.readInt(CODE_LENGTH);
        while(true){
            currentCode = lookaheadCode;
            String prefix = prefixes[currentCode];
            BinaryStdOut.write(prefix);
            //System.out.print(prefix);
            String lookaheadPrefix;
            lookaheadCode = BinaryStdIn.readInt(CODE_LENGTH);
            if(lookaheadCode == EOF){
                break;
            }
            if(codeNumber < POSSIBLE_CODES){
                if(prefixes[lookaheadCode] == null){
                    prefixes[codeNumber] = prefix + prefix.charAt(0);
                    codeNumber++;

                }
                else {
                    lookaheadPrefix = prefixes[lookaheadCode];
                    prefixes[codeNumber] = prefix + lookaheadPrefix.charAt(0);
                    codeNumber++;
                }
            }
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
