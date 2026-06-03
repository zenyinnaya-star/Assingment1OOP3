package shapes.utilities;
import shapes.Shape3D;
import java.util.Comparator;

/**
 * This class:
 * - Compares two Shape3D objects based on their surface area
 * - Sorts in descending order (larger surface area comes first)
 */
public class SurfaceAreaCompartor implements Comparator<Shape3D>{
	
    /**
     * This method:
     * - Compares the surface area of two shapes
     * - Returns a positive value if shape2 has a larger surface area than shape1
     * - Returns a negative value if shape1 has a larger surface area than shape2
     * - Returns zero if both shapes have equal surface area
     *
     * @param shape1 - the first shape being compared
     * @param shape2 - the second shape being compared
     *
     * @return comparison result based on descending surface area
     */
	@Override
	public int compare(Shape3D shape1,Shape3D shape2) {
	return Double.compare(shape2.getSurfaceArea(), shape1.getSurfaceArea());
	}
}
