import java.util.ArrayList;

public class Rename {

    public static void printHelp() {
        System.out.println("(c) 2021 Eddie Kim, Revised May XX, 2021.");
        System.out.println("Usage: rename [-option argument1 argument2 ...]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("-f|file [filename]          :: file(s) to change.");
        System.out.println("-p|prefix [string]          :: rename [filename] so that it starts with [string].");
        System.out.println("-s|suffix [string]          :: rename [filename] so that it ends with [string].");
        System.out.println("-r|replace [str1] [str2]    :: rename [filename] by replacing all instances of [str1] with [str2].");
        System.out.println("-h|help                     :: print out this help and exit the program.");
    }


    public static void errorInvalidOption(String s) {
        System.out.println("Invalid option provided (" + s + "). Try 'rename -h' to see all valid options.");
    }
    public static void errorNoFileOption() {
        System.out.println("No file option ('-f' or '-file'). You must include at least 1 file to rename.");
    }
    public static void errorNoOptions() {
        System.out.println("No option provided (excluding '-f' or '-file'). See 'rename -h' to see what options to include.");
    }
    public static void errorInsufficientValues(String s) {
        System.out.println("Not enough values provided for " + s + ". Try 'rename -h' to see what values you should include.");
    }
    public static void errorDuplicateOption(String s) {
        System.out.println("Duplicate option provided (" + s + "). Make sure you only include 1 option if necessary.");
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
    //   - there are enough values specified for the given option. ( >=2 for '-r', >=1 for everything else )
    public static ArrayList<Character> checkErrors(ArrayList<String> encountered, String args[], int start) {
        if (encountered.contains(args[start].charAt(1))) {
            errorDuplicateOption(args[start]);
            throw Throwable.Exception.RuntimeException.IllegalArgumentException;
        }
        if (((args[start] == "-r" || args[start] == "-replace") && countValues(args, start) != 2) ||
             (args[start] != "-r" && args[start] != "-replace"  && countValues(args, start) == 0)) {
            errorInsufficientValues(args[start]);
            throw Throwable.Exception.RuntimeException.IllegalArgumentException;
        }
        return encountered;
    }

    // called after going through all options to see if anything is missing.
    public static void checkOptions(ArrayList<String> e) {
        if (!e.contains('p') || !e.contains('s') || !e.contains('r')) {
            errorNoOptions();
            throw Throwable.Exception.RuntimeException.IllegalArgumentException;
        }
        if (!e.contains('f')) {
            errorNoFileOption();
            throw Throwable.Exception.RuntimeException.IllegalArgumentException;
        }
    }

    // 'start' MUST be the index of an option/flag
    //   - collect the values following args[start]
    public static ArrayList<String> collectArguments(ArrayList<String> values, String args[], int i) {
        ++i;
        while (i < args.length && args[i].charAt(0) != '-') {
            if ((args[i].equals("-f") || args[i].equals("-file")) && values.contains(args[i])) {
                Systems.out.println("Duplicate file name spotted (" + args[i] + ")... will still try to rename it.");
            } else {
                values.add(args[i]);
            }
            ++i;
        }
        return values;
    }

    // ------------------ main ---------------------
    public static void main(String args[]) {
        // check for help first. Then we can check for argument errors later without worrying about it.
        if (args.contains("-h") || args.contains("-help")) {
            printHelp();
            return;
        }

        ArrayList<String> options = new ArrayList<String> {"-f", "-filename", "-p", "-prefix", "-s", "-suffix", "-r", "-replace"};
        ArrayList<Character> encountered = new ArrayList<Character>();
        ArrayList<String> filenames = new ArrayList<String>();
        ArrayList<String> prefixes = new ArrayList<String>();
        ArrayList<String> suffixes = new ArrayList<String>();
        ArrayList<String> toReplace = new ArrayList<String>(2);

        int i = 0;
        while (i < args.length) {
            if (options.contains(args[i])) {
                try {
                    encountered = checkErrors(encountered, args, i);
                } catch (Exception ex) {
                    return;
                }
                ++i;
                if (args[i].equals("-f") || args[i].equals("-file")) {
                    filenames = collectArguments(filenames, args, i);
                    i += filenames.length;
                } else if (args[i].equals("-p") || args[i].equals("-prefix")) {
                    prefixes = collectArguments(prefixes, args, i);
                    i += prefixes.length;
                } else if (args[i].equals("-s") || args[i].equals("-suffix")) {
                    suffixes = collectArguments(suffixes, args, i);
                    i += suffixes.length;
                } else { // args[i].equals("-r") || args[i].equals("-replace"))
                    toReplace = collectArguments(toReplace, args, i);
                    i += toReplace.length;
                }
            } else if (args[i].charAt(0) == '-') {
                errorInvalidOption(args[i]);
                return;
            }
            ++i;
        }

        try {
            checkOptions(encountered);
        } catch (Exception ex) {
            return;
        }

        // now, change the filenames.
    }
}






















