package appDomain;

import shapes.Shape3D;
import shapes.utilities.HeightComparator;
import shapes.utilities.SurfaceAreaCompartor;
import shapes.utilities.VolumeComparator;

import java.util.Comparator;

import managers.ShapeLoader;
import managers.ShapeRepository;
import managers.ShapeResults;
import managers.SortController;

/**
 * This class:
 * - Acts as the main entry point for the sorting application
 * - Reads command-line arguments for file name, comparison type, and sort type
 * - Loads shapes from a file into a repository
 * - Selects the correct comparator and sorting algorithm
 * - Measures sorting time and prints summary results
 */
public class AppDriver {

    /**
     * This method:
     * - Parses command-line arguments
     * - Loads shape data from a file
     * - Chooses the correct comparator based on user input
     * - Selects and executes the sorting algorithm
     * - Prints the first, last, and periodic elements of the sorted list
     *
     * @param args - command-line arguments:
     *             → -f<filename> : path to the shape file
     *             → -t<h|v|a>    : comparison type (height, volume, base area)
     *             → -s<b|s|i|m|q|z> : sorting algorithm
     */
    public static void main(String[] args) {

        // -------------------------------
        // 1. Parse command-line arguments
        // -------------------------------
        String filename = null;
        String compareType = null;
        String sortType = null;

        for (String arg : args) {
            String flag = arg.substring(0, 2).toLowerCase();
            String value = arg.length() > 2 ? arg.substring(2) : "";

            switch (flag) {
                case "-f": filename = value; break;
                case "-t": compareType = value.toLowerCase(); break;
                case "-s": sortType = value.toLowerCase(); break;
                default:
                    System.err.println("Unknown argument: " + arg);
                    printUsage();
                    return;
            }
        }

        if (filename == null || compareType == null || sortType == null) {
            System.err.println("Error: Missing required argument(s).");
            printUsage();
            return;
        }

        if (!compareType.matches("[hva]")) {
            System.err.println("Error: -t must be h (height), v (volume), or a (base area).");
            printUsage();
            return;
        }

        if (!sortType.matches("[bsimqz]")) {
            System.err.println("Error: -s must be b, s, i, m, q, or z.");
            printUsage();
            return;
        }

        // -------------------------------
        // 2. Load shapes from file
        // -------------------------------
        ShapeLoader reader = new ShapeLoader();
        ShapeRepository repo = reader.loadShapes(filename);

        if (repo.size() == 0) {
            System.err.println("No shapes loaded. Exiting.");
            return;
        }

        // -------------------------------
        // 3. Choose comparator
        // -------------------------------
        Comparator<Shape3D> comparator;
        String compareLabel;

        switch (compareType) {
            case "v":
                comparator = new VolumeComparator();
                compareLabel = "Volume";
                break;
            case "a":
                comparator = new SurfaceAreaCompartor();
                compareLabel = "Base Area";
                break;
            default:
                comparator = new HeightComparator();
                compareLabel = "Height";
                break;
        }

        // -------------------------------
        // 4. Choose sort name
        // -------------------------------
        String sortName;
        switch (sortType) {
            case "b": sortName = "Bubble Sort"; break;
            case "s": sortName = "Selection Sort"; break;
            case "i": sortName = "Insertion Sort"; break;
            case "m": sortName = "Merge Sort"; break;
            case "q": sortName = "Quick Sort"; break;
            default:  sortName = "AI Sort"; break;
        }

        // -------------------------------
        // 5. Perform sorting + timing
        // -------------------------------
        SortController sorter = new SortController();

        long start = System.nanoTime();
        sorter.sort(repo.getAll(), repo.size(), comparator, sortType);
        long end = System.nanoTime();

        long elapsedMs = (end - start) / 1_000_000;

        // -------------------------------
        // 6. Display results
        // -------------------------------
        ShapeResults reporter = new ShapeResults();
        reporter.printResults(repo.getAll(), repo.size(),
                compareType, compareLabel, sortName, elapsedMs);
    }

    /**
     * This method:
     * - Prints instructions for correct command-line usage
     * - Helps the user understand required flags and valid values
     */
    private static void printUsage() {
        System.out.println("Usage: java -jar Sort.jar -f<file> -t<h|v|a> -s<b|s|i|m|q|z>");
        System.out.println("  -f  File name/path (e.g. -fres\\shapes1.txt)");
        System.out.println("  -t  Compare type: h=height, v=volume, a=base area");
        System.out.println("  -s  Sort type:    b=bubble, s=selection, i=insertion, m=merge, q=quick, z=AI sort");
    }
}
