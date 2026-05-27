package shapes;

public abstract class Shape3D {
	protected String name;
	
public void shape3D(String name) {
	this.name=name;
}
public abstract double getVolume();



public abstract double getSurfaceArea();


public  abstract String getDimensions();



public abstract double getHeight();



public String getName(){
	return name;
}

	 
public void printInfo() {
	        System.out.println("Shape: " + name);
	        System.out.println("Dimensions: " + getDimensions());
	        System.out.println("Volume: " + getVolume());
	        System.out.println("Surface Area: " + getSurfaceArea());
	        System.out.println("---");
 }
@Override
public String toString() {
	return "shape3D{" +
            "name='" + name + '\'' +
            
            '}';
}
}



