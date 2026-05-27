package shapes;
import java.util.Comparator;

public class Cone extends Shape3D implements Comparator<Shape3D> {
    private double radius;
    private double height;

    public Cone(String name, double radius, double height) {
        super();  // Fixed: pass name to parent
        this.radius = radius;
        this.height = height;
    }

    @Override
    public double getVolume() {
        return (1.0 / 3.0) * Math.PI * Math.pow(radius, 2) * height;
    }

    @Override
    public double getSurfaceArea() {
        double slantHeight = Math.sqrt(Math.pow(radius, 2) + Math.pow(height, 2));
        return Math.PI * radius * slantHeight + Math.PI * Math.pow(radius, 2);
    }

    @Override
    public String getDimensions() {
        return "Radius: " + radius + ", Height: " + height;
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

        
        int volumeComparison = Double.compare(o1.getVolume(), o2.getVolume());
        if (volumeComparison != 0) {
            return volumeComparison;
        }

        
        return Double.compare(o1.getSurfaceArea(), o2.getSurfaceArea());
    }
}