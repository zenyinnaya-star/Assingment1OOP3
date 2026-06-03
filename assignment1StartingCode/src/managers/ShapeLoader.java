package managers;

import shapes.*;
import java.io.*;

public class ShapeLoader {

    public ShapeRepository loadShapes(String filename) {
        System.out.println("\nReading from: " + filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            int count = Integer.parseInt(reader.readLine().trim());
            ShapeRepository repo = new ShapeRepository(count);

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                Shape3D shape = parseShape(line);

                if (shape != null) {
                    repo.add(shape);
                    System.out.println("  ✓ Added: " + shape.getName());
                }
            }

            System.out.println("  Loaded " + repo.size() + " shapes.");
            return repo;

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return new ShapeRepository(0);
        }
    }

    private Shape3D parseShape(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length < 3) {
            System.err.println("Invalid line: " + line);
            return null;
        }

        String type = parts[0];
        double v1, v2;

        try {
            v1 = Double.parseDouble(parts[1]);
            v2 = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid numeric values: " + line);
            return null;
        }

        switch (type.toLowerCase()) {
            case "cone":              return new Cone(type, v1, v2);
            case "cylinder":          return new Cylinder(type, v1, v2);
            case "pentagonalprism":   return new PentagonalPrism(type, v2, v1);
            case "pyramid":           return new Pyramid(type, v2, v1);
            case "squareprism":       return new SquarePrism(type, v2, v1);
            case "triangularprism":   return new TriangularPyramid(type, v2, v1);
            case "octagonalprism":    return new OctagonalPyramid(type, v2, v1);
            default:
                System.err.println("Unknown shape type: " + type);
                return null;
        }
    }
}
