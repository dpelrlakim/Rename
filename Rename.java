import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import java.io.File;

public class Rename {
    public static final String ERROR = "Error: ";
    public static final String FAIL = "Fail. ";

    public static void printHelp() {
        System.out.println();
        System.out.println("(c) 2021 Eddie Kim, Revised May 25, 2021.");
        System.out.println("Usage: rename [-option argument1 argument2 ...]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("-f|file [filename] ...    :: file(s) to change.");
        System.out.println("-p|prefix [string] ...    :: rename [filename] so that it starts with [string].");
        System.out.println("-s|suffix [string] ...    :: rename [filename] so that it ends with [string].");
        System.out.println("-r|replace [str1] [str2]  :: rename [filename] by replacing all instances of [str1] with [str2].");
        System.out.println("-h|help                   :: print out this help and exit the program.");
    }


    // error messages
    public static void errorInvalidOption(String s) {
        System.out.println(ERROR + "Invalid option provided (" + s + "). Try 'rename -h' to see all valid options.");
    }
    public static void errorNoFileOption() {
        System.out.println(ERROR + "No file option ('-f' or '-file'). You must include at least 1 file to rename.");
    }
    public static void errorNoOptions() {
        System.out.println(ERROR + "No option provided (other than possibly '-f' or '-file'). Try 'rename -h' to see what options to include.");
    }
    public static void errorInsufficientValues(String s) {
        System.out.println(ERROR + "Incorrect number of values provided for " + s + ". Try 'rename -h' to see how many values you should include.");
    }
    public static void errorDuplicateOption(String s) {
        System.out.println(ERROR + "Duplicate option provided (" + s + "). Make sure you only include 1 option if necessary.");
    }
    public static void errorTargetMissing(String theFile, String theTarget) {
        System.out.println(ERROR + "Replace operation failed - " + theFile + " does not contain " + theTarget + " in its name. " +
                "Please make sure the filename contains the replacement target.");
    }

    // for debugging purposes (not used in the submission)
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
            }
            ++i;
        }
    }

    // the 'op' parameter decides whether to prepend or append.
    public static void prependOrAppend(Map<Character, ArrayList<String>> values, char op) {
        int files = values.get('f').size();
        int snippets = values.get(op).size();
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

    public static void replace(Map<Character, ArrayList<String>> values) throws Exception {
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

        String options[] = {"-f", "-file", "-p", "-prefix", "-s", "-suffix", "-r", "-replace"};
        // keep track of all options given and preserve their order.
        ArrayList<Character> encountered = new ArrayList<Character>();
        Map<Character, ArrayList<String>> values = Map.ofEntries(
                entry('f', new ArrayList<String>()),
                entry('p', new ArrayList<String>()),
                entry('s', new ArrayList<String>()),
                entry('r', new ArrayList<String>(2))
        );

        // parsing
        int index = 0;
        while (index < args.length) {
            if (Arrays.asList(options).contains(args[index])) {
                try {
                    checkErrors(encountered, args, index);
                    // System.out.println("encountered is " + encountered); // delete this-------------
                } catch (Exception ex) {
                    return;
                }
                char opt = encountered.get(encountered.size() - 1);
                collectValues(opt, values, args, index);
                index += values.get(opt).size();
            } else if (args[index].charAt(0) == '-') {
                errorInvalidOption(args[index]);
                return;
            }
            ++index;
        }

        // check for errors (i.e. not enough options)
        try {
            checkOptions(encountered);
        } catch (Exception ex) {
            return;
        }

        ArrayList<File> files = new ArrayList<File>();

        // check to see if all files exist; add to the arraylist 'files' if they do.
        for (String s : values.get('f')) {
            File f = new File(s);
            if (!f.exists()) {
                System.out.println("File doesn't exist (" + s + "). Make sure you put in the correct name or path of the file.");
                return;
            }
            files.add(f);
        }

        //printStuff(values);

        encountered.remove(encountered.indexOf('f'));
        // coming up with file names and storing them in values.get('f')
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

        //printStuff(values);

        for (int i = 0; i < values.get('f').size(); i++) {
            File theFile = files.get(i);
            File newFile = new File(values.get('f').get(i));
            System.out.print("Renaming " + theFile.getPath() + " to " + newFile.getPath() + " ... ");
            if (newFile.exists()) {
                System.out.print(FAIL + newFile.getName() + " already exists. Please pick a different name for " + theFile.getName());
                continue;
            }
            try {
                // the renameTo() methods only returns false if the file doesn't exist, and we checked that before. (2 for-loops ago)
                theFile.renameTo(new File(newFile.getPath()));
                System.out.println("Success!");
            } catch (SecurityException se) {
                System.out.println(FAIL + "You do not have permission to write to the file.");
            } catch (NullPointerException npe) {
                System.out.println(FAIL + "The parameter target is null.");
            }
        }
    }
}






















