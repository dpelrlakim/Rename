import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println("No option provided (other than possibly '-f' or '-file'). See 'rename -h' to see what options to include.");
    }
    public static void errorInsufficientValues(String s) {
        System.out.println("Incorrect number of values provided for " + s + ". Try 'rename -h' to see how many values you should include.");
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
    //   - there are enough values specified for the given option. ( 2 for '-r', >=1 for everything else )
    public static Character checkErrors(ArrayList<Character> encountered, String args[], int start) throws Exception {
        if (encountered.contains(args[start].charAt(1))) {
            errorDuplicateOption(args[start]);
            throw new Exception();
        }
        if ((( args[start].equals("-r") ||  args[start].equals("-replace")) && countValues(args, start) != 2) ||
             (!args[start].equals("-r") && !args[start].equals("-replace")  && countValues(args, start) == 0)) {
            errorInsufficientValues(args[start]);
            throw new Exception();
        }
        return args[start].charAt(1);
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
    public static ArrayList<String> collectValues(ArrayList<String> values, String args[], int i) {
        int start = i;
        ++i;
        while (i < args.length && args[i].charAt(0) != '-') {
            if ((args[start].equals("-f") || args[start].equals("-file")) && values.contains(args[i])) {
                System.out.println("Duplicate file name spotted (" + args[i] + ")... will still try to rename it.");
            } else {
                values.add(args[i]);
                System.out.println("values is " + values);
            }
            ++i;
        }
        return values;
    }

    // ------------------ main ---------------------
    public static void main(String args[]) {
        // check for help first. Then we can check for argument errors later without worrying about it.
        if (Arrays.asList(args).contains("-h") || Arrays.asList(args).contains("-help")) {
            printHelp();
            return;
        }

        // delete this
        System.out.print("args is [");
        for (int i = 0; i < args.length - 1; i++) {
            System.out.print(args[i] + ", ");
        }
        System.out.println(args[args.length - 1] + "]");

        String options[] = {"-f", "-filename", "-p", "-prefix", "-s", "-suffix", "-r", "-replace"};
        ArrayList<Character> encountered = new ArrayList<Character>();
        ArrayList<String> filenames = new ArrayList<String>();
        ArrayList<String> prefixes = new ArrayList<String>();
        ArrayList<String> suffixes = new ArrayList<String>();
        ArrayList<String> toReplace = new ArrayList<String>(2);

        int i = 0;
        while (i < args.length) {
            if (Arrays.asList(options).contains(args[i])) {
                try {
                    encountered.add(checkErrors(encountered, args, i));
                    System.out.println("encountered is " + encountered);
                } catch (Exception ex) {
                    return;
                }
                if (encountered.get(encountered.size() - 1) == 'f') {
                    filenames = collectValues(filenames, args, i);
                    i += filenames.size();
                } else if (encountered.get(encountered.size() - 1) == 'p') {
                    prefixes = collectValues(prefixes, args, i);
                    i += prefixes.size();
                } else if (encountered.get(encountered.size() - 1) == 's') {
                    suffixes = collectValues(suffixes, args, i);
                    i += suffixes.size();
                } else if (encountered.get(encountered.size() - 1) == 'r') {
                    toReplace = collectValues(toReplace, args, i);
                    i += toReplace.size();
                }
            } else if (args[i].charAt(0) == '-') {
                errorInvalidOption(args[i]);
                return;
            }
            ++i;
        }
        System.out.println("filenames is " + filenames);
        System.out.println("prefixes is " + prefixes);
        System.out.println("suffixes is " + suffixes);
        System.out.println("toReplace is " + toReplace);


        try {
            checkOptions(encountered);
        } catch (Exception ex) {
            return;
        }
        System.out.println("reached");

        // now, change the filenames.
    }
}






















