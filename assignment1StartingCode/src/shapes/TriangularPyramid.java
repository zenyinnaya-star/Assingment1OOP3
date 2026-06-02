package shapes;

import java.util.Comparator;

public class TriangularPyramid extends Shape3D implements Comparator<Shape3D> {
    private double baseArea;  // Area of triangular base
    private double height;

    public TriangularPyramid(String name, double baseArea, double height) {
    	super(name);  // Fixed: pass name to parent class
        this.baseArea = baseArea;
        this.height = height;
    }

    @Override
    public double getVolume() {
        return (1.0 / 3.0) * baseArea * height;
    }

    @Override
    public double getSurfaceArea() {
        // Base area + 3 triangular sides
        // Note: This is an approximation. For exact calculation, 
        // you would need the side lengths of the base triangle and slant heights
        return baseArea + (3 * baseArea);
    }

    @Override
    public String getDimensions() {
        return "Base Area: " + baseArea + ", Height: " + height;
    }

    @Override
    public double getHeight() {
        return height;  // Fixed: was returning 0
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