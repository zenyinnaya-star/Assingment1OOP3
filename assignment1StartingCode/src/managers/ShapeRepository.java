package managers;

import shapes.Shape3D;

/**
 * This class:
 * - Stores Shape3D objects inside a fixed‑size array
 * - Tracks how many shapes have been added
 * - Provides access to the stored shapes
 * - Allows replacing the entire collection when needed
 */
public class ShapeRepository {
	
    private Shape3D[] shapes;
    private int count;

    /**
     * This constructor:
     * - Creates an internal array with the given capacity
     * - Initializes the shape counter to zero
     *
     * @param capacity - maximum number of shapes the repository can hold
     */
    public ShapeRepository(int capacity) {
        shapes = new Shape3D[capacity];
        count = 0;
    }

    /**
     * This method:
     * - Adds a new Shape3D object to the repository
     * - Stores the shape at the next available index
     * - Prints an error message if the repository is already full
     *
     * @param shape - the Shape3D object to add
     */
    public void add(Shape3D shape) {
    	
        if (count < shapes.length) {
            shapes[count++] = shape;
        } 
        else {
            System.err.println("Repository full — cannot add more shapes.");
        }
    }

    /**
     * This method:
     * - Returns the internal array of shapes
     * - Does not create a copy (direct access for performance)
     *
     * @return array containing all stored Shape3D objects
     */
    public Shape3D[] getAll() {
        return shapes;
    }

    /**
     * This method:
     * - Returns the number of shapes currently stored
     *
     * @return number of valid shapes in the repository
     */
    public int size() {
        return count;
    }

    /**
     * This method:
     * - Replaces the entire internal array with a new one
     * - Updates the count to match the new array length
     *
     * @param newShapes - array of Shape3D objects to replace the current collection
     */
    public void replaceAll(Shape3D[] newShapes) {
        this.shapes = newShapes;
        this.count = newShapes.length;
    }
}

