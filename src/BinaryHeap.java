/**
 * a BinaryHeap with generic type
 * @author vivianle
 *
 * @param <T> generic type
 */
public interface BinaryHeap<T extends Comparable<T>> {
	
	/**
	 * get the index of the left child of the node
	 * @param i index of node in array
	 * @return index of left child in array
	 */
	public int LEFT(int i);
	
	/**
	 * get the index of right child of the node
	 * @param i index of node in array
	 * @return index of right child in array
	 */
	public int RIGHT(int i);
	
	
	/**
	 * get the index of the parent node
	 * @param i index of current node
	 * @return index of parent node
	 */
	public int PARENT(int i);
	
	
	/**
	 * modify the array to create a Max-Heap
	 */
	public void buildMaxHeap();
	
	/**
	 * modify the value at index i and its children to
	 * satisfy the max heap requirements
	 * @param i index being checked
	 */
	public void MAX_HEAPIFY(int i);
	
	/**
	 * get the value of node index
	 * @param index of node
	 * @return a Comparable
	 */
	public Comparable getNodeValue(int index);
	
	/**
	 * exchange values at 2 node index
	 * @param index1 to exchange
	 * @param index2 to exchange
	 */
	public void exchangeValues(int index1, int index2);
	
	/**
	 * remove and return the maximum of heap
	 * @return the maximum
	 */
	public Comparable heapExtractMaximum();
	
	/**
	 * increase value of a particular node
	 * @param i index of particular node
	 * @param value new value of index i
	 */
	public void heapIncreaseValue(int i, Comparable value);
	
	/**
	 * insert a value in the heap
	 * @param value to insert in
	 */
	public void heapInsert(Comparable value);
	
	/**
	 * insert a value in the heap
	 * @param value to insert in
	 */
	public void maxHeapInsert(Comparable value);
	
	/**
	 * get the maximum of heap
	 * @return the maximum
	 */
	public Comparable heapMaximum();
	
	/**
	 * get the heap size
	 * @return the heapsize
	 */
	public int getHeapSize();
	
	/**
	 * sort the heap
	 */
	public void heapSort();
	
	/**
	 * set the heapsize
	 * @param i new heapsize
	 */
	public void setHeapSize(int i);
	
	/**
	 * get the internal array
	 * @return the internal array
	 */
	public Comparable[] getInternalArray();
}