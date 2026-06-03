package shapes.utilities;

import shapes.Shape3D;
import java.util.Comparator;

/**
 * This class:
 * - Provides multiple sorting algorithms for Shape3D arrays
 * - Sorts shapes in descending order based on the given Comparator
 * - Contains only static methods (no objects of this class are created)
 */
public class SortUtility {
	
    /**
     * This method:
     * - Uses an external AI-based sorting algorithm
     * - Delegates sorting to AISortUtility.aiSort()
     *
     * @param shapes - array of Shape3D objects to sort
     * @param count - number of valid elements in the array
     * @param comparator - comparison rule used to determine ordering
     */
	public static void aiSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
		
		AISortUtility.aiSort(shapes, count, comparator);
	}


    /**
     * This method:
     * - Sorts the array using Bubble Sort
     * - Repeatedly compares adjacent elements
     * - Swaps elements when the left value is smaller than the right value
     *
     * @param shapes - array of Shape3D objects to sort
     * @param count - number of valid elements in the array
     * @param comparator - comparison rule used to determine ordering
     */
	public static void bubbleSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
		
		for(int i=0;i<count-1;i++) {
		
			for(int j =0; j< count -i-1; j++) {
			
				if (comparator.compare(shapes[j], shapes[j + 1]) < 0) {
				
					Shape3D temp= shapes[j];
					shapes[j] = shapes[j + 1];
                    shapes[j + 1] = temp;
				}

			}

		}
	}

    /**
     * This method:
     * - Sorts the array using QuickSort
     * - Recursively partitions the array around a pivot value
     * - Places larger values to the left and smaller values to the right
     *
     * @param shapes - array of Shape3D objects to sort
     * @param count - number of valid elements in the array
     * @param comparator - comparison rule used to determine ordering
     */
	public static void quickSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
        
		quickSortHelper(shapes, 0, count - 1, comparator);
	}
	
    /**
     * This method:
     * - Performs the recursive QuickSort steps
     * - Sorts the left and right partitions around the pivot
     *
     * @param shapes - array being sorted
     * @param left - starting index of the partition
     * @param right - ending index of the partition
     * @param comparator - comparison rule used to determine ordering
     */
	private static void quickSortHelper(Shape3D[] shapes, int i, int j, Comparator<Shape3D> comparator) {
		
		if (i < j) {
		
			int pivotIndex = partition(shapes, i, j, comparator);
			
			quickSortHelper(shapes, i, pivotIndex - 1, comparator);
			quickSortHelper(shapes, pivotIndex + 1, j, comparator);
		}
	}

    /**
     * This method:
     * - Partitions the array around a pivot element
     * - Moves all values greater than the pivot to the left side
     * - Moves all smaller values to the right side
     *
     * @param shapes - array being sorted
     * @param left - starting index of the partition
     * @param right - ending index (pivot position)
     * @param comparator - comparison rule used to determine ordering
     *
     * @return final index of the pivot element
     */
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
		return index;
	}

    /**
     * This method:
     * - Sorts the array using Insertion Sort
     * - Builds a sorted section one element at a time
     * - Shifts smaller values to the right to make space for larger ones
     *
     * @param shapes - array of Shape3D objects to sort
     * @param count - number of valid elements in the array
     * @param comparator - comparison rule used to determine ordering
     */
	public static void insertionSort(Shape3D[] shapes, int count, Comparator<Shape3D> comparator) {
        
		for(int i =1; i<count; i++) {
        
			Shape3D key = shapes[i];
            int j = i-1;
            
            while(j>=0 && comparator.compare(shapes[j], key) < 0) {
            
            	shapes[j+1] = shapes[j];
                j--;
            }
            
            shapes[j+1] = key;
            
		}
        
	}

    /**
     * This method:
     * - Sorts the array using Selection Sort
     * - Repeatedly finds the largest remaining value
     * - Places it at the front of the unsorted section
     *
     * @param shapes - array of Shape3D objects to sort
     * @param count - number of valid elements in the array
     * @param comparator - comparison rule used to determine ordering
     */
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
		}
    }
    /**
     * This method:
     * - Sorts the array using Merge Sort
     * - Recursively splits the array into halves
     * - Merges the halves back together in sorted order
     *
     * @param shapes - array of Shape3D objects to sort
     * @param count - number of valid elements in the array
     * @param comparator - comparison rule used to determine ordering
     */
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

	    /**
	     * This method:
	     * - Merges two sorted subarrays into one sorted array
	     * - Places larger values first according to the comparator
	     *
	     * @param shapes - destination array
	     * @param left - left sorted subarray
	     * @param right - right sorted subarray
	     * @param leftCount - number of elements in left array
	     * @param rightCount - number of elements in right array
	     * @param comparator - comparison rule used to determine ordering
	     */
		private static void merge(Shape3D[] shapes, Shape3D[] left, Shape3D[] right, int leftCount, int rightCount, Comparator<Shape3D> comparator) {
			
			int i = 0, j = 0, k = 0;
			
			while (i < leftCount && j < rightCount) {
				if (comparator.compare(left[i], right[j]) > 0) {
					shapes[k++] = left[i++];
					} 
				else 
					{
					shapes[k++] = right[j++];
					}
				}
			
			while (i < leftCount) 
			{
				shapes[k++] = left[i++];
			}
			
			while (j < rightCount) 
			{
				shapes[k++] = right[j++];
			}
		}
	}
