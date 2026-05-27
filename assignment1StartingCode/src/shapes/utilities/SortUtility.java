package shapes.utilities;
import shapes.Shape3D;
import java.util.Comparator;









public class SortUtility {
	public static void bubbleSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
		for(int i=0;i<count-1;i++) {
			for(int j =0; j< count -i-1; j++) {
				if (comparator.compare(shapes[j], shapes[j + 1]) < 0) {
					Shape3D temp= shapes[j];
					shapes[j] = shapes[j + 1];
                    shapes[j + 1] = temp;
                    timer();

	
	
                    
                    
			
				 }
				
			}
	
		}
	}
	

	public static void quickSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
        quickSortHelper(shapes, 0, count - 1, comparator);
        
		
	}
	private static void quickSortHelper(Shape3D[] shapes, int i, int j, Comparator<Shape3D> comparator) {
		if (i < j) {
			int pivotIndex = partition(shapes, i, j, comparator);
			quickSortHelper(shapes, i, pivotIndex - 1, comparator);
			quickSortHelper(shapes, pivotIndex + 1, j, comparator);
		}
		
	}
		// TODO Auto-generated method stub
	private static int partition(Shape3D[] shapes, int i, int j, Comparator<Shape3D> comparator) {
		Shape3D pivot = shapes[j];
		int index = i;
		for (int k = i; k < j; k++) {
			if (comparator.compare(shapes[k], pivot) > 0) {
				Shape3D temp = shapes[index];
				shapes[index] = shapes[k];
				shapes[k] = temp;
				index++;
			}
		}
		Shape3D temp = shapes[index];
		shapes[index] = shapes[j];
		shapes[j] = temp;
		timer();
		return index;
		
		
	}

	public static void insertionSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
        for(int i =1; i<count; i++) {
            Shape3D key = shapes[i];
            int j = i-1;
            while(j>=0 && comparator.compare(shapes[j], key) < 0) {
                shapes[j+1] = shapes[j];
                j--;
            }
            shapes[j+1] = key;
            timer();}
            
        }
        
	public static void selectionSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
		for (int i = 0; i < count - 1; i++) {
			int maxIndex = i;
			for (int j = i + 1; j < count; j++) {
				if (comparator.compare(shapes[j], shapes[maxIndex]) > 0) {
					maxIndex = j;
				}
			}
			Shape3D temp = shapes[maxIndex];
			shapes[maxIndex] = shapes[i];
			shapes[i] = temp;
			timer();
			
			
		}
		
        
           }

			public static void mergeSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
				if (count < 2) {
					return;
				}
				int mid = count / 2;
				Shape3D[] left = new Shape3D[mid];
				Shape3D[] right = new Shape3D[count - mid];
				for (int i = 0; i < mid; i++) {
					left[i] = shapes[i];
				}
				for (int i = mid; i < count; i++) {
					right[i - mid] = shapes[i];
				}
				mergeSort(left, mid, comparator);
				mergeSort(right, count - mid, comparator);
				merge(shapes, left, right, mid, count - mid, comparator);
			}

			private static void merge(Shape3D[] shapes, Shape3D[] left, Shape3D[] right, int leftCount, int rightCount,
					Comparator<Shape3D> comparator) {
				int i = 0, j = 0, k = 0;
				while (i < leftCount && j < rightCount) {
					if (comparator.compare(left[i], right[j]) > 0) {
						shapes[k++] = left[i++];
					} else {
						shapes[k++] = right[j++];
					}
				}
				while (i < leftCount) {
					shapes[k++] = left[i++];
				}
				while (j < rightCount) {
					shapes[k++] = right[j++];
				}
				timer();
			}
	public static void timer() {
	    long startTime = System.nanoTime();
//to time it after each swap or comparison, call this method to calculate and print the execution time
	    long endTime = System.nanoTime();
	    long duration = endTime - startTime;
	    System.out.println("Execution time: " + duration + " milliseconds");
	}
	
	}

		
	
	
