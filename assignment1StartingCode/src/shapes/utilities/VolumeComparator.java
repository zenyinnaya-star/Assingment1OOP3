package shapes.utilities;

import java.util.Comparator;

import shapes.Shape3D;

public class VolumeComparator implements Comparator<Shape3D> {
    @Override
    public int compare(Shape3D shape1, Shape3D shape2) {
        
        return Double.compare(shape2.getVolume(), shape1.getVolume());
    }
}