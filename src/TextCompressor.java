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

    private static final int EOF_CHARACTER = 256;
    private static final int CODE_LENGTH = 12;
    private static final int POSSIBLE_CODES = (int)Math.pow(2, CODE_LENGTH);

    private static void compress() {
        // TST for fast lookup of codes
        TST codes = new TST();
        String file = BinaryStdIn.readString();
        int fileLength = file.length();
        String prefix;
        // Set the first new code to right after the EOF character
        int codeNumber = EOF_CHARACTER + 1;
        // Insert all single character prefixes into TST
        for(int i = 0; i < 256; i++){
            codes.insert("" + (char)i, i);
        }
        int index = 0;
        // Loop through file compressing into codes
        while(index < fileLength){
            // Set the prefix to the longest possible prefix starting at index
            prefix = codes.getLongestPrefix(file, index);
            // Write the prefix code to file
            BinaryStdOut.write(codes.lookup(prefix), CODE_LENGTH);
            // If making a new code doesn't exceed file length
            if(index + prefix.length() + 1 < fileLength){
                // If there are still availible codes
                if(codeNumber < POSSIBLE_CODES) {
                    // Create a new prefix by adding the next letter to the current prefix
                    codes.insert(prefix + file.charAt(index + prefix.length()), codeNumber);
                    codeNumber++;
                }
            }
            // Advance index to skip over the rest of the prefix
            index += prefix.length();
        }
        // Write the EOF character at the end of the compressed file
        BinaryStdOut.write(EOF_CHARACTER, CODE_LENGTH);
        BinaryStdOut.close();
    }

    private static void expand() {
        // Array for constant time lookup of prefixes
        String[] prefixes = new String[POSSIBLE_CODES];
        // Set the first new code to one more than the EOF character
        int codeNumber = EOF_CHARACTER + 1;
        // Insert all one character prefixes into the array
        for(int i = 0; i < 256; i++){
            prefixes[i] = "" + (char)i;
        }
        int currentCode;
        int lookaheadCode = BinaryStdIn.readInt(CODE_LENGTH);
        String lookaheadPrefix;
        // Loop until reaching the EOF character
        while(true){
            // Advance currentCode to lookaheadCode
            currentCode = lookaheadCode;
            // Lookup the prefix and write it to the expanded file
            String prefix = prefixes[currentCode];
            BinaryStdOut.write(prefix);
            // Read in the lookahead code
            lookaheadCode = BinaryStdIn.readInt(CODE_LENGTH);
            // Breaks from the loop if the next character is the EOF character
            if(lookaheadCode == EOF_CHARACTER){
                break;
            }
            // If there are more available codes
            if(codeNumber < POSSIBLE_CODES){
                // Edge case for if the lookahead prefix hasn't been built yet
                if(prefixes[lookaheadCode] == null){
                    // Because the lookahead prefix is built from the original prefix, the first
                    // character of the original prefix is added to create the lookahead prefix
                    prefixes[codeNumber] = prefix + prefix.charAt(0);
                    codeNumber++;

                }
                else {
                    // Build the next prefix/code pair from the lookahead and current prefixes
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
