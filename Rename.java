import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import java.io.File;

public class Rename {

    public static void printHelp() {
        System.out.println();
        System.out.println("(c) 2021 Eddie Kim, Revised May XX, 2021."); // edit this
        System.out.println("Usage: rename [-option argument1 argument2 ...]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("-f|file [filename] ...         :: file(s) to change.");
        System.out.println("-p|prefix [string] ...         :: rename [filename] so that it starts with [string].");
        System.out.println("-s|suffix [string] ...         :: rename [filename] so that it ends with [string].");
        System.out.println("-r|replace [str1] [str2]       :: rename [filename] by replacing all instances of [str1] with [str2].");
        System.out.println("-h|help                        :: print out this help and exit the program.");
    }


    public static void errorInvalidOption(String s) {
        System.out.println("Invalid option provided (" + s + "). Try 'rename -h' to see all valid options.");
    }
    public static void errorNoFileOption() {
        System.out.println("No file option ('-f' or '-file'). You must include at least 1 file to rename.");
    }
    public static void errorNoOptions() {
        System.out.println("No option provided (other than possibly '-f' or '-file'). Try 'rename -h' to see what options to include.");
    }
    public static void errorInsufficientValues(String s) {
        System.out.println("Incorrect number of values provided for " + s + ". Try 'rename -h' to see how many values you should include.");
    }
    public static void errorDuplicateOption(String s) {
        System.out.println("Duplicate option provided (" + s + "). Make sure you only include 1 option if necessary.");
    }
    public static void errorRenaming(String s) {
        System.out.println("Rename operation failed for the following file: " + s + ". Will still try to rename all other files if any...");
    }
    public static void errorTargetMissing(String theFile, String theTarget) {
        System.out.println("Replace operation failed - " + theFile + " does not contain " + theTarget + " in its name. " +
                "Please make sure the filename contains the replacement target.")
    }

    public static void printStuff(Map<Character, ArrayList<String>> values) {
        System.out.println("values    is " + values);
    }

    // 'start' MUST be the index of an option/flag
    public static int countValues(String args[], int start) {
        int i = 0;
        while (start + i + 1 < args.length && args[start + i + 1].charAt(0) != '-') {
            ++i;
        }
        return i;
    }

    // this function makes sure that:
    //   - there are no duplicate options
    //   - there are enough values specified for the given option. ( 2 for '-r', >=1 for everything else )
    public static void checkErrors(ArrayList<Character> encountered, String args[], int start) throws Exception {
        if (encountered.contains(args[start].charAt(1))) {
            errorDuplicateOption(args[start]);
            throw new Exception();
        }
        if ((( args[start].equals("-r") ||  args[start].equals("-replace")) && countValues(args, start) != 2) ||
             (!args[start].equals("-r") && !args[start].equals("-replace")  && countValues(args, start) == 0)) {
            errorInsufficientValues(args[start]);
            throw new Exception();
        }
        encountered.add(args[start].charAt(1));
    }

    // called after going through all options to see if anything is missing.
    public static void checkOptions(ArrayList<Character> encountered) throws Exception {
        if (!encountered.contains('p') && !encountered.contains('s') && !encountered.contains('r')) {
            errorNoOptions();
            throw new Exception();
        }
        if (!encountered.contains('f')) {
            errorNoFileOption();
            throw new Exception();
        }
    }

    // 'start' MUST be the index of an option/flag
    //   - collect the values following args[start]
    public static void collectValues(char opt, Map<Character, ArrayList<String>> values, String args[], int start) {
        int i = start + 1;
        while (i < args.length && args[i].charAt(0) != '-') {
            if ((args[start].equals("-f") || args[start].equals("-file")) && values.get(opt).contains(args[i])) {
                System.out.println("Duplicate file name spotted (" + args[i] + ")... will still try to rename it.");
            } else {
                values.get(opt).add(args[i]);
                printStuff(values);
            }
            ++i;
        }
    }

    // the 'op' parameter decides whether to prepend or append.
    public static void prependOrAppend(Map<Character, ArrayList<String>> values, char op) {
        int files = values.get('f').size();
        int snippets = values.get('p').size();
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < snippets; j++) {
                String toAdd = values.get(op).get(j);
                if (op == 'p') {
                    values.get('f').set(i, toAdd + values.get('f').get(i));
                } else if (op == 's') {
                    values.get('f').set(i, values.get('f').get(i) + toAdd);
                }
            }
        }
    }

    public static void replace(Map<Character, ArrayList<String>> values) {
        for (int i = 0; i < values.get('f').size(); i++) {
            String theFile = values.get('f').get(i);
            String theTarget = values.get('r').get(0);
            String theReplacement = values.get('r').get(1);
            if (!theFile.contains(theTarget)) {
                errorTargetMissing(theFile, theTarget);
                throw new Exception();
            }
            values.get('f').set(i, values.get('f').get(i).replaceAll(theTarget, theReplacement));
        }
    }

    // ------------------ main ---------------------
    public static void main(String args[]) {
        // check for help first. Then we can check for argument errors later without worrying about it.
        if (Arrays.asList(args).contains("-h") || Arrays.asList(args).contains("-help")) {
            printHelp();
            return;
        }

        // delete this
        System.out.print("args      is [");
        for (int i = 0; i < args.length - 1; i++) {
            System.out.print(args[i] + ", ");
        }
        System.out.println(args[args.length - 1] + "]");

        String options[] = {"-f", "-file", "-p", "-prefix", "-s", "-suffix", "-r", "-replace"};
        // keep track of all options given and preserve their order.
        ArrayList<Character> encountered = new ArrayList<Character>();
        Map<Character, ArrayList<String>> values = Map.ofEntries(
                entry('f', new ArrayList<String>()),
                entry('p', new ArrayList<String>()),
                entry('s', new ArrayList<String>()),
                entry('r', new ArrayList<String>(2))
        );

        int i = 0;
        while (i < args.length) {
            if (Arrays.asList(options).contains(args[i])) {
                try {
                    checkErrors(encountered, args, i);
                    System.out.println("encountered is " + encountered);
                } catch (Exception ex) {
                    return;
                }
                char opt = encountered.get(encountered.size() - 1);
                collectValues(opt, values, args, i);
                i += values.get(opt).size();
            } else if (args[i].charAt(0) == '-') {
                errorInvalidOption(args[i]);
                return;
            }
            ++i;
        }
        System.out.println("filenames is " + values.get('f'));
        System.out.println("prefixes  is " + values.get('p'));
        System.out.println("suffixes  is " + values.get('s'));
        System.out.println("toReplace is " + values.get('r'));


        try {
            checkOptions(encountered);
        } catch (Exception ex) {
            return;
        }

        encountered.remove(encountered.indexOf('f'));

        for (String s : values.get('f')) {
            File f = new File(s);
            if (!f.exists()) {
                System.out.println("File doesn't exist (" + s + "). Make sure you put in the correct name or path of the file.");
                return;
            }
        }
        // now, change the filenames.
        for (char c : encountered) {
            if (c == 'p' || c == 's') {
                prependOrAppend(values, c);
            } else { // c == 'r'
                try {
                    replace(values);
                } catch (Exception ex) {
                    return;
                }
            }
        }
        System.out.println("reached");
        printStuff(values);
    }
}






















