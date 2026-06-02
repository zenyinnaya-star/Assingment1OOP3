package shapes;

import java.util.Comparator;

public class Cylinder extends Shape3D implements Comparator<Shape3D> {
    private double radius;
    private double height;

    public Cylinder(String name, double radius, double height) {
    	super(name);  // Fixed: pass name to parent class
        this.radius = radius;
        this.height = height;
    }

    @Override
    public double getVolume() {
        return Math.PI * Math.pow(radius, 2) * height;
    }

    @Override
    public double getSurfaceArea() {
        return 2 * Math.PI * radius * height + 2 * Math.PI * Math.pow(radius, 2);
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