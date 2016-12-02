import java.lang.Math;

/**
 * a BinaryHeap with generic type
 * @author vivianle
 *
 * @param <T> generic type
 */
public class BinaryHeapA<T extends Comparable<T>> implements BinaryHeap<T> {
	
	/** array to hold the heap **/
	protected Comparable[] internalArray;
	
	/** keep track of the heap size (different from the capacity) **/
	protected int heapSize;
	
	/**
	 * Constructor to initialize the instance variables
	 * @param A a Comparable array
	 */
	public BinaryHeapA(Comparable[] A) {
		this.internalArray = A;
		this.heapSize = this.internalArray.length;
	}

	/**
	 * get the index of the parent node
	 * @param i index of current node
	 * @return index of parent node
	 */
	public int PARENT(int i) {
		return (int)Math.floor(i/2);
	}
	
	/**
	 * get the index of the left child of the node
	 * @param i index of node in array
	 * @return index of left child in array
	 */
	public int LEFT(int i) {
		return 2*i;
	}
	
	/**
	 * get the index of right child of the node
	 * @param i index of node in array
	 * @return index of right child in array
	 */
	public int RIGHT(int i) {
		return 2*i+1;
	}
	
	/**
	 * get the value of node index
	 * @param index of node
	 * @return a Comparable
	 */
	public Comparable getNodeValue(int index) {
		// if index out of range
		if ((index < 1) || (index > this.heapSize))
			return null;
		return internalArray[index-1];
	}
	
	/**
	 * exchange values at 2 node index
	 * @param index1 to exchange
	 * @param index2 to exchange
	 */
	public void exchangeValues(int index1, int index2) {
		
		// if index out of range, return
		if ((index1 < 1) || (index1 > this.internalArray.length) || (index2 < 1) || (index2 > this.internalArray.length))
			return;
		else {
			// exchange values
			Comparable key = internalArray[index1-1];
			internalArray[index1-1] = internalArray[index2-1];
			internalArray[index2-1] = key;
		}
	}
	
	/**
	 * modify the array to create a Max-Heap
	 */
	public void buildMaxHeap() {
		
		// iterate backwards through the array from the middle index
		// max-heapify the array at index i
		for (int i = (int)Math.floor((this.internalArray.length)/2); i >= 1; i--)
			this.MAX_HEAPIFY(i);
	}
	
	/**
	 * modify the value at index i and its children to
	 * satisfy the max heap requirements
	 * @param i index being checked
	 */
	public void MAX_HEAPIFY(int i) {
		
		// get the right and left child indexes of index i
		int l = LEFT(i);
		int r = RIGHT(i);
		
		// keep track of the largest value
		int largest;
		
		// if index of left child is in array and left child is larger than i
		// l becomes the largest
		if ((l <= this.heapSize) && (getNodeValue(l).compareTo(getNodeValue(i)) > 0))
			largest = l;
		else
			largest = i;
		
		// if index of right child is in array and right child is larger than
		// the current largest, r becomes the largest
		if ((r <= this.heapSize) && (getNodeValue(r).compareTo(getNodeValue(largest)) > 0))
			largest = r;
		
		// if the largest is no longer i, exchange value at i and largest
		if (largest != i) {
			this.exchangeValues(i, largest);
			
			// call max_heapify at largest to adjust array
			// according to the change of i
			MAX_HEAPIFY(largest);
		}
	}
	
	/**
	 * remove and return the maximum of heap
	 * @return the maximum
	 */
	public Comparable heapExtractMaximum() {
		
		// if the heap is empty, return null
		if (this.heapSize < 1)
			return null;
		else {
			// the maximum is in the first node
			Comparable max = getNodeValue(1);
			
			// set value of first node to be the value of last node
			this.internalArray[0] = this.getNodeValue(heapSize);
			
			// decrement heapsize to remove 1 node and call max_heapify
			this.setHeapSize(this.getHeapSize() - 1);
			MAX_HEAPIFY(1);
			
			return max;
		}
	}
	
	/**
	 * increase value of a particular node
	 * @param i index of particular node
	 * @param value new value of index i
	 */
	public void heapIncreaseValue(int i, Comparable value) {
		
		// if i is not out of range
		if ((this.heapSize >= i) && (i > 0)) {
			
			// compare if new value is bigger than the current one
			if (value.compareTo(getNodeValue(i)) < 0)
				return;

			// update value at i
			this.internalArray[i-1] = value;
			
			// check if value at index i is greater than its parents
			while ((i > 1) && (getNodeValue(PARENT(i)).compareTo(getNodeValue(i)) < 0)) {
				
				// is yes, exchange i and its parent, update i to continue checking
				exchangeValues(i, PARENT(i));
				i = PARENT(i);
			}
		}
	}
	
	/**
	 * insert a value in the heap
	 * @param value to insert in
	 */
	public void heapInsert(Comparable value) {
		
		// increment size of heap
		this.heapSize = this.heapSize+1;
		
		// if array out of range, create new longer array
		if (this.heapSize > this.internalArray.length) {
			Comparable[] newArray = new Comparable[this.heapSize];
			
			// copy all values from old array and update internalArray
			for (int i = 0; i < this.internalArray.length; i++)
				newArray[i] = this.internalArray[i];
			this.internalArray = newArray;
		}
		
		// set the last node value to -infinity
		this.internalArray[this.heapSize-1] = value;
	}
	
	/**
	 * insert a value in the heap
	 * @param value to insert in
	 */
	public void maxHeapInsert(Comparable value) {
		
		// increment size of heap
		this.heapSize = this.heapSize+1;
		
		// if array out of range, create new longer array
		if (this.heapSize > this.internalArray.length) {
			Comparable[] newArray = new Comparable[this.heapSize];
			
			// copy all values from old array and update internalArray
			for (int i = 0; i < this.internalArray.length; i++)
				newArray[i] = this.internalArray[i];
			this.internalArray = newArray;
		}
		
		// set the last node value to -infinity
		this.internalArray[this.heapSize-1] = value;
		
		// increase value of last node to ensure its a max heap
		this.heapIncreaseValue(this.heapSize, value);
	}
	
	/**
	 * get the maximum of heap
	 * @return the maximum
	 */
	public Comparable heapMaximum() {
		return getNodeValue(1);
	}
	
	/**
	 * get the heap size
	 * @return the heapsize
	 */
	public int getHeapSize() {
		return this.heapSize;
	}
	
	/**
	 * set the heapsize
	 * @param i new heapsize
	 */
	public void setHeapSize(int i) {
		this.heapSize = i;
	}
	
	/**
	 * sort the heap
	 */
	public void heapSort() {
		// build a max-heap from the array
		this.buildMaxHeap();
		// iterate through the array
		for (int i = this.heapSize; i >= 2; i--) {
			// exchange the value at index i and the first index
			// as this is the largest value up to heapsize
			this.exchangeValues(1, i);
			
			// decrease heapsize and max_heapify the array from first index
			this.setHeapSize(this.getHeapSize() - 1);
			this.MAX_HEAPIFY(1);
		}
	}
	
	/**
	 * get the internal array
	 * @return the internal array
	 */
	public Comparable[] getInternalArray() {
		return this.internalArray;
	}
}