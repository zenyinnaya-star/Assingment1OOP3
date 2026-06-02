package shapes;
import java.util.Comparator;

public class PentagonalPrism extends Shape3D implements Comparator<Shape3D> {
    private double baseLength;  // Length of pentagonal base edge
    private double height;

    public PentagonalPrism(String name, double baseLength, double height) {
    	super(name);
        this.baseLength = baseLength;
        this.height = height;
    }

    @Override
    public double getVolume() {
        double pentagonArea = (Math.pow(baseLength, 2) * Math.sqrt(25 + 10 * Math.sqrt(5))) / 4;
        return pentagonArea * height;
    }

    @Override
    public double getSurfaceArea() {
        double pentagonArea = (Math.pow(baseLength, 2) * Math.sqrt(25 + 10 * Math.sqrt(5))) / 4;
        double lateralArea = 5 * baseLength * height;
        return 2 * pentagonArea + lateralArea;
    }

    @Override
    public String getDimensions() {
        return "Base Length: " + baseLength + ", Height: " + height;
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

        // Third: If volumes are equal, compare by surface area
        return Double.compare(o1.getSurfaceArea(), o2.getSurfaceArea());
    }
}
