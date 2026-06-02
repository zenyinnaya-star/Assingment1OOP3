package shapes;
import java.util.Comparator;


public class OctagonalPyramid extends Shape3D implements Comparator<Shape3D> {
    private double sideLength;
    private double height;

    public OctagonalPyramid(String name, String color, double sideLength, double height) {
    	super(name);
        this.sideLength = sideLength;
        this.height = height;
    }

    public OctagonalPyramid(String name, double baseSide, double height2) {
        super(name);
        this.sideLength = baseSide;
        this.height = height2;
    }
	@Override
    public double getVolume() {
        double baseArea = 2 * (1 + Math.sqrt(2)) * Math.pow(sideLength, 2);
        return (1.0 / 3.0) * baseArea * height;
    }

    @Override
    public double getSurfaceArea() {
        double baseArea = 2 * (1 + Math.sqrt(2)) * Math.pow(sideLength, 2);

        double apothem = sideLength / (2 * Math.tan(Math.PI / 8));
        double slantHeight = Math.sqrt(Math.pow(height, 2) + Math.pow(apothem, 2));

        double lateralArea = 8 * (0.5 * sideLength * slantHeight);

        return baseArea + lateralArea;
    }

    @Override
    public String getDimensions() {
        return "Side Length: " + sideLength + ", Height: " + height;
    }

    @Override
    public double getHeight() {
        return height;
    }

    /**
     * Compares two Shape3D objects by height, volume, and surface area.
     * Primary: Height | Secondary: Volume | Tertiary: Surface Area
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return a negative integer if the first object is less than the second object,
     *         zero if the first object is equal to the second object,
     *         or a positive integer if the first object is greater than the second object
     */
    @Override
    public int compare(Shape3D o1, Shape3D o2) {
        // First: Compare by height
        int heightComparison = Double.compare(o1.getHeight(), o2.getHeight());
        if (heightComparison != 0) {
            return heightComparison;
        }

        // Second: If heights are equal, compare by volume
        int volumeComparison = Double.compare(o1.getVolume(), o2.getVolume());
        if (volumeComparison != 0) {
            return volumeComparison;
        }

        
        return Double.compare(o1.getSurfaceArea(), o2.getSurfaceArea());
    }
}