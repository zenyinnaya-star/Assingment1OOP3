package managers;

import shapes.Shape3D;

public class ShapeResults {

    public void printResults(Shape3D[] shapes, int count,
                             String compareType, String compareLabel,
                             String sortName, long elapsedMs) {

        System.out.printf("%-20s %-35s %s: %.3f%n",
                "First element:", shapes[0].getClass().getName(),
                compareLabel, getValue(shapes[0], compareType));

        for (int i = 1000; i < count - 1; i += 1000) {
            System.out.printf("%-20s %-35s %s: %.3f%n",
                    i + "-th element:", shapes[i].getClass().getName(),
                    compareLabel, getValue(shapes[i], compareType));
        }

        System.out.printf("%-20s %-35s %s: %.3f%n",
                "Last element:", shapes[count - 1].getClass().getName(),
                compareLabel, getValue(shapes[count - 1], compareType));

        System.out.println(sortName + " run time was: " + elapsedMs + " ms");
    }

    private double getValue(Shape3D shape, String type) {
        switch (type) {
            case "v": return shape.getVolume();
            case "a": return shape.getSurfaceArea();
            default:  return shape.getHeight();
        }
    }
}

