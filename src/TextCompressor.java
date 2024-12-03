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

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, YOUR NAME HERE
 */
public class TextCompressor {

    private static final int LETTER_STATE = 0;
    private static final int CHAR_STATE = 1;
    private static final int LETTER_BITS = 6;
    private static final int LETTER_TO_CHAR_ESC = 28;
    private static final int CHAR_TO_LETTER_ESC = 'A';
    private static void compress() {

        // TODO: Complete the compress() method
//        while(!BinaryStdIn.isEmpty()){
//            char nextCharacter;
//            String nextWord = "";
//            do{
//                nextCharacter = BinaryStdIn.readChar();
//                nextWord += nextCharacter;
//            }while (nextCharacter != ' ');
//        }
        int state = LETTER_STATE;
        String text = BinaryStdIn.readString();
        int textLength = text.length();
        BinaryStdOut.write(textLength);
        for(int i = 0; i < textLength; i++){
            char nextCharacter = text.charAt(i);
            if(Character.isAlphabetic(nextCharacter)){
                state = LETTER_STATE;
            }
            else{
                state = CHAR_STATE;
            }
            if(state == LETTER_STATE){
                BinaryStdOut.write(nextCharacter - 'A', 6);
            }
            else if(state == CHAR_STATE){
                BinaryStdOut.write(CHAR_TO_LETTER_ESC, 6);
                BinaryStdOut.write(nextCharacter);
            }
        }
        BinaryStdOut.close();
    }

    private static void expand() {

        // TODO: Complete the expand() method
        int textLength = BinaryStdIn.readInt();
        for(int i = 0; i < textLength; i++){
            char nextCharacter = BinaryStdIn.readChar();
            if(nextCharacter == CHAR_TO_LETTER_ESC){
                i++;
                BinaryStdOut.write(nextCharacter);
            }
            else{
                BinaryStdOut.write();
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
