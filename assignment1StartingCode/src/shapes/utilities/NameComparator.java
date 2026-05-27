package shapes.utilities;

import java.util.Comparator;

import shapes.Shape3D;

public class NameComparator implements Comparator<Shape3D> {
    @Override
    public int compare(Shape3D shape1, Shape3D shape2) {
        return shape1.getName().compareToIgnoreCase(shape2.getName());
    }
}