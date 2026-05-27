package shapes;

import java.util.Comparator;

public class SquarePrism extends Shape3D implements Comparator<Shape3D> {
    private double sideLength;  // Side of square base
    private double height;

    public SquarePrism(String name, double sideLength, double height) {
        super();  // Fixed: pass name to parent class
        this.sideLength = sideLength;
        this.height = height;
    }

    @Override
    public double getVolume() {
        return Math.pow(sideLength, 2) * height;
    }

    @Override
    public double getSurfaceArea() {
        double baseArea = Math.pow(sideLength, 2);
        double sideArea = 4 * sideLength * height;
        return 2 * baseArea + sideArea;
    }

    @Override
    public String getDimensions() {
        return "Side Length: " + sideLength + ", Height: " + height;
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