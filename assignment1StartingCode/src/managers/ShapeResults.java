package managers;

import shapes.Shape3D;

/**
 * This class:
 * - Displays selected results from a sorted Shape3D array
 * - Prints the first, last, and every 1000th element
 * - Shows the comparison value used for sorting (height, volume, or surface area)
 * - Reports the total runtime of the sorting algorithm
 */
public class ShapeResults {

    /**
     * This method:
     * - Prints summary information about the sorted shapes
     * - Displays:
     *   → The first element in the sorted array
     *   → Every 1000th element (if applicable)
     *   → The last element in the sorted array
     * - Shows the comparison value used for sorting (height, volume, or surface area)
     * - Prints the total sorting time in milliseconds
     *
     * @param shapes       - array of sorted Shape3D objects
     * @param count        - number of valid elements in the array
     * @param compareType  - code representing the comparison type ("h", "v", or "a")
     * @param compareLabel - readable label for the comparison type
     * @param sortName     - name of the sorting algorithm used
     * @param elapsedMs    - total runtime of the sort in milliseconds
     */
    public void printResults(Shape3D[] shapes, int count, String compareType, String compareLabel, String sortName, long elapsedMs) {

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

    /**
     * This method:
     * - Retrieves the comparison value for a given shape
     * - Returns:
     *   → Volume if compareType = "v"
     *   → Surface area if compareType = "a"
     *   → Height for all other cases
     *
     * @param shape - the shape whose value is being retrieved
     * @param type  - comparison type code ("h", "v", or "a")
     *
     * @return numeric value used for sorting
     */
    private double getValue(Shape3D shape, String type) {
        switch (type) {
            
        case "v": return shape.getVolume();
        
        case "a": return shape.getSurfaceArea();
        
        default:  return shape.getHeight();
        }
    }
}
