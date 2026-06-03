package shapes.utilities;

import shapes.Shape3D;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Comparator;

/**
 * This class:
 * - Provides AI-assisted sorting for Shape3D arrays
 * - Sends numeric values to an external Python script for sorting
 * - Receives a sorted index order and rearranges the array accordingly
 * - Falls back to Merge Sort if the AI script fails or returns invalid data
 */
public class AISortUtility {

    /**
     * This method:
     * - Attempts to locate the Python AI sorting script
     * - Checks several possible directory paths
     *
     * @return the absolute path to the Python script
     */
    private static String resolveScriptPath() {
        String userDir = System.getProperty("user.dir");
        File script = new File(userDir, "../Sorting_AI/sort_service.py");
        if (script.exists()) {
            return script.getAbsolutePath();
        }
        script = new File(userDir, "Sorting_AI/sort_service.py");
        if (script.exists()) {
            return script.getAbsolutePath();
        }
        script = new File(userDir, "../../Sorting_AI/sort_service.py");
        if (script.exists()) {
            return script.getAbsolutePath();
        }
        return "../Sorting_AI/sort_service.py";
    }

    /**
     * This method:
     * - Determines the working directory for running the Python script
     * - Ensures the script is executed from the correct folder
     *
     * @return the directory containing the AI sorting script
     */
    private static File resolveWorkingDir() {
        String userDir = System.getProperty("user.dir");
        File dir = new File(userDir, "../Sorting_AI");
        if (dir.exists()) return dir;
        dir = new File(userDir, "Sorting_AI");
        if (dir.exists()) return dir;
        dir = new File(userDir, "../../Sorting_AI");
        if (dir.exists()) return dir;
        return new File(userDir);
    }

    /**
     * This method:
     * - Performs AI-assisted sorting on the array
     * - Extracts numeric values based on the comparator type
     * - Sends values to the Python script in JSON format
     * - Receives a sorted index order and rearranges the array
     * - Falls back to Merge Sort if the AI script fails
     *
     * @param shapes     - array of Shape3D objects to sort
     * @param count      - number of valid elements in the array
     * @param comparator - determines which shape property is used for sorting
     */
    public static void aiSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
        if (count <= 1) return;

        double[] values = extractValues(shapes, count, comparator);
        String jsonInput = buildJson(values);
        String jsonOutput = callPythonScript(jsonInput);

        if (jsonOutput == null || jsonOutput.contains("\"error\"")) {
            System.err.println("AI Sort failed, falling back to merge sort");
            SortUtility.mergeSort(shapes, count, comparator);
            return;
        }

        int[] indices = parseIndices(jsonOutput);
        if (indices == null || indices.length != count) {
            System.err.println("AI Sort returned invalid result, falling back");
            SortUtility.mergeSort(shapes, count, comparator);
            return;
        }

        Shape3D[] temp = new Shape3D[count];
        for (int i = 0; i < count; i++) {
            temp[i] = shapes[indices[i]];
        }
        System.arraycopy(temp, 0, shapes, 0, count);
    }

    /**
     * This method:
     * - Extracts numeric values from each shape
     * - Uses height, volume, or surface area depending on comparator type
     *
     * @param shapes     - array of shapes
     * @param count      - number of valid elements
     * @param comparator - determines which property to extract
     *
     * @return array of numeric values used for AI sorting
     */
    private static double[] extractValues(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
        double[] values = new double[count];
        for (int i = 0; i < count; i++) {
            if (comparator instanceof HeightComparator) {
                values[i] = shapes[i].getHeight();
            } else if (comparator instanceof VolumeComparator) {
                values[i] = shapes[i].getVolume();
            } else if (comparator instanceof SurfaceAreaCompartor) {
                values[i] = shapes[i].getSurfaceArea();
            } else {
                values[i] = i; // fallback value
            }
        }
        return values;
    }

    /**
     * This method:
     * - Builds a JSON string containing the extracted values
     * - Specifies descending order for sorting
     *
     * @param values - numeric values extracted from shapes
     *
     * @return JSON string sent to the Python script
     */
    private static String buildJson(double[] values) {
        StringBuilder sb = new StringBuilder("{\"values\":[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(values[i]);
        }
        sb.append("],\"descending\":true}");
        return sb.toString();
    }

    /**
     * This method:
     * - Executes the external Python sorting script
     * - Sends JSON input through the script's standard input
     * - Reads the JSON output containing sorted indices
     *
     * @param jsonInput - JSON string containing values to sort
     *
     * @return JSON output from the Python script, or null on failure
     */
    private static String callPythonScript(String jsonInput) {
        try {
            String scriptPath = resolveScriptPath();
            File workDir = resolveWorkingDir();

            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath);
            pb.directory(workDir);
            pb.redirectErrorStream(false);
            Process process = pb.start();

            OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
            writer.write(jsonInput);
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String lastLine = null;
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }

            int exitCode = process.waitFor();
            if (exitCode != 0 || lastLine == null) {
                System.err.println("Python script error (exit " + exitCode + "): " + lastLine);
                return null;
            }

            return lastLine;
        } catch (Exception e) {
            System.err.println("Error calling AI sort: " + e.getMessage());
            return null;
        }
    }

    /**
     * This method:
     * - Extracts the "indices" array from the JSON output
     * - Converts the values into an integer array
     *
     * @param json - JSON string returned by the Python script
     *
     * @return array of sorted indices, or null if parsing fails
     */
    private static int[] parseIndices(String json) {
        try {
            String key = "\"indices\"";
            int start = json.indexOf(key);
            if (start < 0) return null;
            start = json.indexOf("[", start) + 1;
            int end = json.indexOf("]", start);
            if (end < 0) return null;

            String nums = json.substring(start, end).trim();
            if (nums.isEmpty()) return new int[0];

            String[] parts = nums.split(",");
            int[] indices = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                indices[i] = Integer.parseInt(parts[i].trim());
            }
            return indices;
        } catch (Exception e) {
            System.err.println("Error parsing AI result: " + e.getMessage());
            return null;
        }
    }
}
