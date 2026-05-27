package shapes.utilities;
import shapes.Shape3D;
import java.util.Comparator;

public class SurfaceAreaCompartor implements Comparator<Shape3D>{
	@Override
	public int compare(Shape3D shape1,Shape3D shape2) {
	return Double.compare(shape2.getSurfaceArea(), shape1.getSurfaceArea());
	}
}
