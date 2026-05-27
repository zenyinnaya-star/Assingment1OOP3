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

import shapes.Cone;
import shapes.Cylinder;
import shapes.OctagonalPyramid;
import shapes.PentagonalPrism;
import shapes.Pyramid;
import shapes.SquarePrism;
import shapes.TriangularPyramid;

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
		AppDriver driver = new AppDriver(10000);
		
		String[] files = {"Shape1.txt", "Shape2.txt", "Shape3.txt"};
		
		for (String filename : files) {
			driver.readShapesFromFile(filename);
		}
		
		// Display results
		driver.displayAllShapes();
	}

	private void displayAllShapes() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Reads shapes from a file and adds them to the array
	 */
	public void readShapesFromFile(String filename) {
		System.out.println("\nReading from: " + filename);
		int startCount = shapeCount;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
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
			
			System.out.println("  Loaded " + (shapeCount - startCount) + " shapes from " + filename);
			
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

			} else if (type.equalsIgnoreCase("octagonalpyramid")) {
				// Format: OctagonalPyramid height edgeLength
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
	
}