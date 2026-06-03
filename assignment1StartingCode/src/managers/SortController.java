package managers;

import shapes.Shape3D;
import shapes.utilities.SortUtility;
import java.util.Comparator;

/**
 * This class:
 * - Selects which sorting algorithm to use
 * - Calls the appropriate SortUtility method based on the user's input
 * - Acts as a controller between the UI and the sorting logic
 */
public class SortController {

    /**
     * This method:
     * - Sorts the given array of Shape3D objects
     * - Uses the sort type code to determine which algorithm to run
     * - Supports:
     *   → b = Bubble Sort
     *   → s = Selection Sort
     *   → i = Insertion Sort
     *   → m = Merge Sort
     *   → q = Quick Sort
     *   → z = AI Sort
     * - Prints an error message if the sort type is unknown
     *
     * @param shapes - array of Shape3D objects to sort
     * @param count  - number of valid elements in the array
     * @param comp   - comparator defining how shapes are compared
     * @param type   - single-letter code representing the sorting algorithm
     */
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
