package managers;

import shapes.Shape3D;
import shapes.utilities.SortUtility;
import java.util.Comparator;

public class SortController {

    public void sort(Shape3D[] shapes, int count, Comparator<Shape3D> comp, String type) {
        switch (type) {
            case "b": SortUtility.bubbleSort(shapes, count, comp); break;
            case "s": SortUtility.selectionSort(shapes, count, comp); break;
            case "i": SortUtility.insertionSort(shapes, count, comp); break;
            case "m": SortUtility.mergeSort(shapes, count, comp); break;
            case "q": SortUtility.quickSort(shapes, count, comp); break;
            case "z": SortUtility.aiSort(shapes, count, comp); break;
            default:
                System.err.println("Unknown sort type: " + type);
        }
    }
}

