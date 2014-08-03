import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import static edu.rice.hj.Module1.*;


/**
 * Sequential Smith-Waterman algorithm adapted for COMP 322 homework.
 */
public class SeqSparseParScoring {

    // scoring matrix
    /*
           _  A  C  G  T
        ------------------
        _ |-8 -2 -2 -2 -2
        A |-4  5  2  2  2
        C |-4  2  5  2  2
        G |-4  2  2  5  2
        T |-4  2  2  2  5
    */
    private final static int[][] M = new int[][]{{-8,-2,-2,-2,-2},
            {-4, 5, 2, 2, 2},
            {-4, 2, 5, 2, 2},
            {-4, 2, 2, 5, 2},
            {-4, 2, 2, 2, 5}};


    /**
     * Compute the solution matrix and return the time it took to do
     * the calculation.
     *
     * @param X       The first string to be compared.
     * @param Y       The second string to be compared.
     * @param xLength The length of the X string.
     * @param yLength The length of the Y string.
     * @param iter    The current iteration.
     * @return The total time it took to run the computation as a long.
     */
    public static long compute(String X, String Y, int xLength, int yLength, int iter) {

        //init row
        int[] top_row = new int[yLength+1];
        //second row under the upper row
        int[] current_row = new int[yLength+1];
        //holds all the elements for the zeroth column
        int[] zeroth_column = new int[xLength+1];
        for(int ii = 1; ii < yLength + 1; ii++){
            top_row[ii] = -2*ii;
        }
        //initialize the diagonal
        top_row[0] = 0;
        //fill in the zeroth column values
        for(int jj = 1; jj < xLength+1; jj++){
            zeroth_column[jj] = M[0][1]*jj;
        }


        long time = -System.nanoTime();

        forseq(1, xLength, (jj) -> {

            forseq(1, yLength, (i) -> {
                char XChar = X.charAt(jj - 1);
                char YChar = Y.charAt(i - 1);

                int diagScore = top_row[i - 1] + M[charMap(XChar)][charMap(YChar)];
                int topColScore = top_row[i] + M[charMap(XChar)][0];
                int leftRowScore = current_row[i - 1] + M[0][charMap(YChar)];
                current_row[i] = Math.max(diagScore, Math.max(leftRowScore, topColScore));

            });
            current_row[0] = zeroth_column[jj];
            System.arraycopy(current_row,0, top_row, 0, yLength);


        });

        //printMatrix(S);
        time += System.nanoTime();
        time = time / 1000000; // convert from nanoseconds to milliseconds
        System.out.println("  The score = " + current_row[yLength] + " in iteration " + (iter+1));
        System.out.println("  The execution time = " + time + " milliseconds in iteration " + (iter + 1));
        return time;
    }

    /**
     * Helper method for getting the index of character in scoring matrix.
     *
     * @param inputChar The character to search for.
     * @return The index of the requested character in the matrix.
     */
    public static int charMap(char inputChar) {
        int toBeReturned = -1;
        switch(inputChar){
            case '_': toBeReturned = 0; break;
            case 'A': toBeReturned = 1; break;
            case 'a': toBeReturned = 1; break;
            case 'C': toBeReturned = 2; break;
            case 'c': toBeReturned = 2; break;
            case 'G': toBeReturned = 3; break;
            case 'g': toBeReturned = 3; break;
            case 'T': toBeReturned = 4; break;
            case 't': toBeReturned = 4; break;
        }
        if(toBeReturned == -1)System.out.println("Error:Could not map character");
        return toBeReturned;
    }

    /**
     * Helper method for printing out a two dimensional int array,
     * can be useful for debugging.
     *
     * @param matrix The desired int matrix to be printed.
     */
    public static void printMatrix(int[][] matrix) {
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("--------------------------------");
    }


    /**
     * Read the sequence from a given text file.
     *
     * @param fileName The file name to read text from.
     * @return A string containing the sequence from the file.
     * @throws java.io.IOException if any.
     */
    public static String initString (String fileName) throws java.io.IOException {
        BufferedReader r = new BufferedReader(new FileReader(fileName));
        StringBuilder text = new StringBuilder();
        String line;
        while( (line = r.readLine()) != null){
            text.append(line);
        }
        return text.toString();
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */

    public static void main(String[] args){
        String filename1;
        String filename2;
        initializeHabanero();

        if (args.length == 2) {
            filename1 = args[0];
            filename2 = args[1];
        } else {
            System.out.println("Usage: ./SmithWaterman fileName1 fileName2");
            return;
        }

        try {
            final String X = initString(filename1);
            final String Y = initString(filename2);
            System.out.println("Size of input string 1 is " + X.length());
            System.out.println("Size of input string 2 is " + Y.length());


            int xLength = X.length();
            int yLength = Y.length();
            int numIter = 5;
            long[] times = new long[numIter];
            long execTime;
            long totalTime = 0;
            long minTime;
            for(int iter = 0; iter < numIter; iter++) {
                execTime = compute(X, Y, xLength, yLength, iter);
                totalTime += execTime;
                //ac.put(execTime);
                times[iter] = execTime;
            }
            Arrays.sort(times);
            System.out.println("Avg time of computation is " + totalTime/numIter);//totalTime //ac.get().intValue()
            System.out.println("Min time of computation is " + times[0] + " milliseconds ");

        } catch(Throwable e){
            System.err.println(e);
        }
        finalizeHabanero();
    }

}
