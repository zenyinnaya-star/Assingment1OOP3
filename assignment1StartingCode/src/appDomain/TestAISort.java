package appDomain;

import shapes.*;
import shapes.utilities.*;

public class TestAISort {
    public static void main(String[] args) {
        Shape3D[] shapes = new Shape3D[5];
        shapes[0] = new Cone("Cone", 1.0, 10.0);
        shapes[1] = new Cone("Cone", 1.0, 5.0);
        shapes[2] = new Cylinder("Cylinder", 1.0, 8.0);
        shapes[3] = new Pyramid("Pyramid", 1.0, 3.0);
        shapes[4] = new SquarePrism("SquarePrism", 1.0, 7.0);

        System.out.println("=== Before AI Sort (by height, descending) ===");
        for (int i = 0; i < shapes.length; i++) {
            System.out.println("  " + shapes[i].getName() + " height=" + shapes[i].getHeight());
        }

        SortUtility.aiSort(shapes, shapes.length, new HeightComparator());

        System.out.println("\n=== After AI Sort (by height, descending) ===");
        for (int i = 0; i < shapes.length; i++) {
            System.out.println("  " + shapes[i].getName() + " height=" + shapes[i].getHeight());
        }
    }
}
