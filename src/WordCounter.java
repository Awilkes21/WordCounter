import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Put a short phrase describing the program here.
 *
 * @author Andrew Wilkes
 *
 */
public final class WordCounter {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private WordCounter() {
    }

    private static class Order implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }

    /**
     * generates html to be output to a file
     *
     * @param fileName
     *            the given {@code String} with the output file name
     * @param wordMap
     *            the given {@code wordMap} with each word and number of
     *            instances
     * @param keySequence
     *            the given {@code Sequence} containing every key in the map
     *            alphabetically
     */
    private static void generateOutputHtml(String fileName,
            Map<String, Integer> wordMap, Sequence<String> keySequence) {
        SimpleWriter out = new SimpleWriter1L(fileName);
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Words Counted</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Words Counted</h2>");
        out.println("<hr>");
        out.println("<table border='1'>");
        out.println("<tbody>");
        out.println("<tr>");
        out.println("<th>Words</th>");
        out.println("<th>Counts</th>");
        out.println("</tr>");
        for (int i = 0; i < keySequence.length(); i++) {
            String currentKey = keySequence.entry(i);

            int currentValue = wordMap.value(currentKey);
            out.println("<tr>");
            out.println("<td>" + currentKey + "</td>");
            out.println("<td>" + currentValue + "</td>");
            out.println("</tr>");
        }
        out.println("</body>");
        out.print("</html>");
        out.close();
    }

    /**
     * sorts a sequence full of each key in the map
     *
     * @param wordMap
     *            the given {@code Map} containing each word and its number of
     *            instances
     * @param keySequence
     *            the given {@code Sequence} containing each key
     * @param order
     *            the comparator for lexigraphics
     */
    private static void createSortedSequence(Map<String, Integer> wordMap,
            Sequence<String> keySequence, Comparator<String> order) {
        Sequence<String> tempSequence = new Sequence1L<>();
        // add all of the keys to a sequence
        for (Map.Pair<String, Integer> currentPair : wordMap) {
            tempSequence.add(tempSequence.length(), currentPair.key());
        }

        while (tempSequence.length() > 0) {
            // add the keys in lexigraphical order
            keySequence.add(keySequence.length(),
                    removeFront(tempSequence, order));
        }
    }

    /**
     * removes the highest key lexigraphically
     *
     * @param sequence
     *            the given {@code Sequence} with all of the keys
     * @param order
     *            the comparator used for lexigraphy
     * @return the lowest key lexigraphically
     */
    private static String removeFront(Sequence<String> sequence,
            Comparator<String> order) {
        String first = sequence.remove(0);

        if (sequence.length() > 0) {
            String second = removeFront(sequence, order);

            // check where each string is lexigraphically
            if (order.compare(first, second) > 0) {
                // add first back
                sequence.add(sequence.length(), first);

                first = second;
            } else {
                // add second back
                sequence.add(sequence.length(), second);
            }
        }
        return first;
    }

    /**
     * checks if the given word has already been added to the map and adds 1 if
     * it already exists
     *
     * @param currentWord
     *            the given {@code String} with a singular word to be added
     * @param wordMap
     *            the given {@code Map} to add words and word counts to
     */
    private static void checkIfAlreadyKey(String currentWord,
            Map<String, Integer> wordMap) {
        if (!wordMap.hasKey(currentWord)) {
            // create a new pair with a value of 1
            wordMap.add(currentWord, 1);
        } else {
            // increment the associated value by 1
            wordMap.replaceValue(currentWord, wordMap.value(currentWord) + 1);
        }
    }

    /**
     * gets the individual words from a given line from a text file
     *
     * @param line
     *            the given {@code String} line from a text file
     * @param separatorSet
     *            the given {@code Set} full of separators
     * @param wordMap
     *            the given {@code Map} to add words and their counts to
     */
    private static void fillMapWithWords(String line,
            Set<Character> separatorSet, Map<String, Integer> wordMap) {
        String word = "";
        for (int i = 0; i < line.length(); i++) {
            if (!separatorSet.contains(line.charAt(i))) {
                // add character to the word if it is not a separator
                word += line.charAt(i);
            } else if (!word.equals("")) {
                // at this point in the loop, a full word should be present in the word variable
                // make case insensitive
                word = word.toLowerCase();
                // check if this word has been added yet to the map
                checkIfAlreadyKey(word, wordMap);
                word = "";
            }
        }
    }

    /**
     * reads each line of the input file
     *
     * @param fileName
     *            the given {@code String} file name to read from
     * @param separator
     *            the {@code Set} full of separators
     * @param wordMap
     *            the given {@code Map} to be edited
     */
    private static void readFile(String fileName, Set<Character> separator,
            Map<String, Integer> wordMap) {
        SimpleReader in = new SimpleReader1L(fileName);
        while (!in.atEOS()) {
            // get one line from the text file
            String currentLine = in.nextLine();
            // get all of the words from that line and add them to the map
            fillMapWithWords(currentLine, separator, wordMap);
        }
        in.close();

    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateElements(String str, Set<Character> charSet) {
        // loop through the length of the string
        for (int i = 0; i < str.length(); i++) {
            // check if the set has the current character of the string
            if (!charSet.contains(str.charAt(i))) {
                // add the character to the set
                charSet.add(str.charAt(i));
            }
        }
        charSet.add('\r');
        charSet.add('\n');
        charSet.add('\0');
        charSet.add(' ');
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        // create the map where the key is the word and the value is the number of occurences
        Map<String, Integer> wordMap = new Map1L<>();

        // create sequence for sorted keys
        Sequence<String> keySequence = new Sequence1L<>();

        // get the path/name of the input file
        out.print("Enter the path to the input file: ");
        String inputFileName = in.nextLine();

        // get the path/name of the file to output
        out.print("Enter the path for the output file: ");
        String outputFileName = in.nextLine();

        // generate set of separators
        Set<Character> separatorSet = new Set1L<>();
        generateElements(".,!?:;-", separatorSet);

        // begin to traverse the file
        readFile(inputFileName, separatorSet, wordMap);

        Comparator<String> order = new Order();

        // sort the keys in a sequence
        createSortedSequence(wordMap, keySequence, order);
        // create the output html
        generateOutputHtml(outputFileName, wordMap, keySequence);

        out.print("The file has been created successfully");
        in.close();
        out.close();
    }

}
