package shapes.utilities;

import java.util.Comparator;
import shapes.Shape3D;

/**
 * This class:
 * - Compares two Shape3D objects based on their height
 * - Sorts in descending order (taller shapes come first)
 */
public class HeightComparator implements Comparator<Shape3D> {

    /**
     * This method:
     * - Compares the height of two shapes
     * - Returns a positive value if shape2 is taller than shape1
     * - Returns a negative value if shape1 is taller than shape2
     * - Returns zero if both shapes have equal height
     *
     * @param shape1 - the first shape being compared
     * @param shape2 - the second shape being compared
     *
     * @return comparison result based on descending height
     */
    @Override
    public int compare(Shape3D shape1, Shape3D shape2) {
        return Double.compare(shape2.getHeight(), shape1.getHeight());
    }
}
