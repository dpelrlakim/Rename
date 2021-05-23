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

    // this function checks if:
    //   - there are duplicate options
    //   - there are no values specified for the given option.
    public static ArrayList<Character> checkErrors(ArrayList<String> encountered, String args[], int start) {
        if (encountered.contains(args[start].charAt(1))) {
            // error message (duplicate option) errorDupliateOption
            throw Throwable.Exception.RuntimeException.IllegalArgumentException;
        }
        if (start + 1 == args.length || args[start + 1].charAt(0) == '-') {
            // error message (no values) errorNoValues
            throw Throwable.Exception.RuntimeException.IllegalArgumentException;
        }
    }

    public static void checkOptions(ArrayList<String> e) {
        if (!e.contains('p') || !e.contains('s') || !e.contains('r')) {
            // error message (no options specified) errorNoOptions
            throw Throwable.Exception.RuntimeException.IllegalArgumentException;
        }
        if (!e.contains('f')) {
            // error message (no filename) errorNoFileOption
            throw Throwable.Exception.RuntimeException.IllegalArgumentException;
        }
    }

    public static void main(String args[]) {
        // check for help first. Then we can check for argument errors later without worrying about it.
        if (args.contains("-h") || args.contains("-help")) {
            printHelp();
            return;
        }

        ArrayList<String> options = new ArrayList<String> {"-f", "-filename", "-p", "-prefix", "-s", "-suffix", "-r", "-replace"};
        ArrayList<Character> encountered = new ArrayList<Character>();
        ArrayList<String> filenames = new ArrayList<String>();

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
                    while (i < args.length && args[i].charAt(0) != '-') {
                        if (filenames.contains(args[i])) {
                            Systems.out.println("Duplicate file name spotted (" + args[i] + ")... will still try to rename it.")
                        } else {
                            filenames.add(args[i]);
                        }
                        ++i;
                    }
                }
            } else if (args[i].charAt(0) == '-') {
                // invalid flag
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






















