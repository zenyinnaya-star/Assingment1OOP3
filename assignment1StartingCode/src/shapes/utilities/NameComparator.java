package shapes.utilities;

import java.util.Comparator;
import shapes.Shape3D;

/**
 * This class:
 * - Compares two Shape3D objects based on their name
 * - Sorts in ascending alphabetical order (A → Z)
 */
public class NameComparator implements Comparator<Shape3D> {

    /**
     * This method:
     * - Compares the names of two shapes alphabetically
     * - Ignores uppercase/lowercase differences
     * - Returns a positive value if shape1's name comes after shape2's name
     * - Returns a negative value if shape1's name comes before shape2's name
     * - Returns zero if both names are identical
     *
     * @param shape1 - the first shape being compared
     * @param shape2 - the second shape being compared
     *
     * @return comparison result based on alphabetical order
     */
    @Override
    public int compare(Shape3D shape1, Shape3D shape2) {
        return shape1.getName().compareToIgnoreCase(shape2.getName());
    }
}
