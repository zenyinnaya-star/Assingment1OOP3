package appDomain;

import shapes.Shape3D;
/**
 * <p>
 * This application driver code is designed to be used as a basis for the
 * Complexity and Sorting assignment that will be developed in the CPRG304
 * class at SAIT. The implementors of this applications will be required
 * to add all the correct functionality.
 * </p>
 */
import java.io.*;
import java.util.Comparator;

import shapes.Cone;
import shapes.Cylinder;
import shapes.OctagonalPyramid;
import shapes.PentagonalPrism;
import shapes.Pyramid;
import shapes.SquarePrism;
import shapes.TriangularPyramid;
import shapes.utilities.HeightComparator;
import shapes.utilities.SortUtility;
import shapes.utilities.SurfaceAreaCompartor;
import shapes.utilities.VolumeComparator;

/**
 * Application Driver for reading and storing 3D shapes from text files
 */
public class AppDriver {
	private Shape3D[] shapes;
	private int shapeCount = 0;

	public AppDriver(int capacity) {
		shapes = new Shape3D[capacity];
	}

	/**
	 *  The main method is the entry point of the application.
	 *
	 *  @param args The input to control the execution of the application.
	 */
	public static void main(String[] args) {
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

		AppDriver driver = new AppDriver(0);
		driver.readShapesFromFile(filename);

		if (driver.shapeCount == 0) {
			System.err.println("No shapes loaded. Exiting.");
			return;
		}

		Comparator<Shape3D> comparator;
		switch (compareType) {
		    case "v":  comparator = new VolumeComparator();      break;
		    case "a":  comparator = new SurfaceAreaCompartor();  break;
		    default:   comparator = new HeightComparator();      break;
		}

		String compareLabel;
		switch (compareType) {
		    case "v":  compareLabel = "Volume";     break;
		    case "a":  compareLabel = "Base Area";  break;
		    default:   compareLabel = "Height";     break;
		}

		String sortName;
		switch (sortType) {
		    case "b":  sortName = "Bubble Sort";     break;
		    case "s":  sortName = "Selection Sort";  break;
		    case "i":  sortName = "Insertion Sort";  break;
		    case "m":  sortName = "Merge Sort";      break;
		    case "q":  sortName = "Quick Sort";      break;
		    default:   sortName = "AI Sort";         break;
		}

		// Time only the sort — not file reading or output
		long start = System.nanoTime();

		switch (sortType) {
			case "b": SortUtility.bubbleSort   (driver.shapes, driver.shapeCount, comparator); break;
			case "s": SortUtility.selectionSort(driver.shapes, driver.shapeCount, comparator); break;
			case "i": SortUtility.insertionSort(driver.shapes, driver.shapeCount, comparator); break;
			case "m": SortUtility.mergeSort    (driver.shapes, driver.shapeCount, comparator); break;
			case "q": SortUtility.quickSort    (driver.shapes, driver.shapeCount, comparator); break;
			case "z": SortUtility.aiSort       (driver.shapes, driver.shapeCount, comparator); break;
		}

		long end = System.nanoTime();
		long elapsedMs = (end - start) / 1_000_000;

		// Display results
		driver.displayResults(compareType, compareLabel, sortName, elapsedMs);
	}

	/**
	 * Reads shapes from a file and adds them to the array
	 */
	public void readShapesFromFile(String filename) {
		System.out.println("\nReading from: " + filename);

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

			// First line contains the number of shapes in the file
			int count = Integer.parseInt(reader.readLine().trim());
			shapes = new Shape3D[count];
			shapeCount = 0;

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}

				// Parse the line and create a shape
				Shape3D shape = parseShape(line);

				if (shape != null) {
					addShape(shape);  // Add to array
					System.out.println("  ✓ Added: " + shape.getName());
				}
			}

			System.out.println("  Loaded " + shapeCount + " shapes from " + filename);

		} catch (IOException e) {
			System.err.println("Error reading " + filename + ": " + e.getMessage());
		}
	}



	private Shape3D parseShape(String line) {
		String[] parts = line.split("\\s+");

		// Need at least 3 parts: Type, Value1, Value2
		if (parts.length < 3) {
			System.err.println("Invalid line format (needs 3 values): " + line);
			return null;
		}

		String type = parts[0].trim();
		double value1 = 0;
		double value2 = 0;

		try {
			value1 = Double.parseDouble(parts[1].trim());
			value2 = Double.parseDouble(parts[2].trim());
		} catch (NumberFormatException e) {
			System.err.println("Error parsing numeric values: " + line);
			return null;
		}

		try {
			if (type.equalsIgnoreCase("cone")) {
				// Format: Cone height radius

				double height = value1;
				double radius = value2;
				return new Cone(type, height, radius);

			} else if (type.equalsIgnoreCase("cylinder")) {
				// Format: Cylinder height radius

				double height = value1;
				double radius = value2;
				return new Cylinder(type, height, radius);

			} else if (type.equalsIgnoreCase("pentagonalprism")) {
				// Format: PentagonalPrism height edgeLength
				double height = value1;
				double edgeLength = value2;
				return new PentagonalPrism(type, edgeLength, height);

			} else if (type.equalsIgnoreCase("pyramid")) {
				// Format: Pyramid height edgeLength

				double height = value1;
				double edgeLength = value2;
				return new Pyramid(type, edgeLength, height);

			} else if (type.equalsIgnoreCase("squareprism")) {
				// Format: SquarePrism height edgeLength

				double height = value1;
				double edgeLength = value2;
				return new SquarePrism(type, edgeLength,  height);

			} else if (type.equalsIgnoreCase("triangularprism")) {
				// Format: TriangularPrism height edgeLength
				double height = value1;
				double edgeLength = value2;
				return new TriangularPyramid(type, edgeLength, height);

			} else if (type.equalsIgnoreCase("octagonalprism")) {
				// Format: OctagonalPrism height edgeLength
				double height = value1;
				double edgeLength = value2;
				return new OctagonalPyramid(type, edgeLength, height);

			} else {
				System.err.println("Unknown shape type: " + type);
			}

		} catch (Exception e) {
			System.err.println("Error creating shape: " + line + " - " + e.getMessage());
		}

		return null;
	}

	/**
	 * Add a shape to the array
	 */
	public void addShape(Shape3D shape) {
		if (shapeCount < shapes.length) {
			shapes[shapeCount] = shape;
			shapeCount++;
		} else {
			System.err.println("Array is full! Cannot add more shapes.");
		}
	}

	/**
	 * Display all shapes stored in the array
	 */
	private void displayResults(String compareType, String compareLabel, String sortName, long elapsedMs) {
		System.out.printf("%-20s %-35s %s: %.3f%n",
				"First element is:", shapes[0].getClass().getName(),
				compareLabel, getCompareValue(shapes[0], compareType));

		for (int i = 1000; i < shapeCount - 1; i += 1000) {
			System.out.printf("%-20s %-35s %s: %.3f%n",
					i + "-th element:", shapes[i].getClass().getName(),
					compareLabel, getCompareValue(shapes[i], compareType));
		}

		System.out.printf("%-20s %-35s %s: %.3f%n",
				"Last element is:", shapes[shapeCount - 1].getClass().getName(),
				compareLabel, getCompareValue(shapes[shapeCount - 1], compareType));

		System.out.println(sortName + " run time was: " + elapsedMs + " milliseconds");
	}

	private static double getCompareValue(Shape3D shape, String type) {
	    switch (type) {
	        case "v":  return shape.getVolume();
	        case "a":  return shape.getSurfaceArea();
	        default:   return shape.getHeight();
	    }
	}

	private static void printUsage() {
		System.out.println("Usage: java -jar Sort.jar -f<file> -t<h|v|a> -s<b|s|i|m|q|z>");
		System.out.println("  -f  File name/path (e.g. -fres\\shapes1.txt)");
		System.out.println("  -t  Compare type: h=height, v=volume, a=base area");
		System.out.println("  -s  Sort type:    b=bubble, s=selection, i=insertion, m=merge, q=quick, z=AI sort");
	}
}
