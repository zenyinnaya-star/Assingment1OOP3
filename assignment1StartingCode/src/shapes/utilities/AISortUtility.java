package shapes.utilities;

import shapes.Shape3D;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Comparator;

public class AISortUtility {

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
                values[i] = i;
            }
        }
        return values;
    }

    private static String buildJson(double[] values) {
        StringBuilder sb = new StringBuilder("{\"values\":[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(values[i]);
        }
        sb.append("],\"descending\":true}");
        return sb.toString();
    }

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
