package managers;

import shapes.Shape3D;

public class ShapeRepository {
    private Shape3D[] shapes;
    private int count;

    public ShapeRepository(int capacity) {
        shapes = new Shape3D[capacity];
        count = 0;
    }

    public void add(Shape3D shape) {
        if (count < shapes.length) {
            shapes[count++] = shape;
        } else {
            System.err.println("Repository full — cannot add more shapes.");
        }
    }

    public Shape3D[] getAll() {
        return shapes;
    }

    public int size() {
        return count;
    }

    public void replaceAll(Shape3D[] newShapes) {
        this.shapes = newShapes;
        this.count = newShapes.length;
    }
}

