package sos.base.util;

import java.util.Arrays;
import java.util.Collection;

public class IntList {
	private transient int[] elementData;
    private int size;
	@SuppressWarnings("unused")
	private int modCount;
	
	public IntList(int initialCapacity) {
		this.elementData = new int[initialCapacity];
    }

	public IntList() {
	this(10);
    }

	public IntList(Collection<Integer> c) {
		this(c.size());
		for (Integer integer : c) {
			add(integer);
		}
    }
    public void trimToSize() {
	modCount++;
	int oldCapacity = elementData.length;
	if (size < oldCapacity) {
            elementData = Arrays.copyOf(elementData, size);
	}
    }

    public void ensureCapacity(int minCapacity) {
	modCount++;
	int oldCapacity = elementData.length;
	if (minCapacity > oldCapacity) {
			// int oldData[] = elementData;
	    int newCapacity = (oldCapacity * 3)/2 + 1;
    	    if (newCapacity < minCapacity)
		newCapacity = minCapacity;
            // minCapacity is usually close to size, so this is a win:
            elementData = Arrays.copyOf(elementData, newCapacity);
	}
    }

    public int size() {
	return size;
    }

    public boolean isEmpty() {
	return size == 0;
    }

	public boolean contains(int o) {
	return indexOf(o) >= 0;
    }

	public int indexOf(int o) {
	    for (int i = 0; i < size; i++)
			if (o == elementData[i])
		    return i;
	return -1;
    }

	public int lastIndexOf(int o) {
	    for (int i = size-1; i >= 0; i--)
			if (o == elementData[i])
		    return i;
	return -1;
    }

	@Override
	public IntList clone() {
		IntList v = new IntList();
	    v.elementData = Arrays.copyOf(elementData, size);
	    v.modCount = 0;
	    return v;
    }

	public int[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

	public int get(int index) {
		// RangeCheck(index);
		return elementData[index];
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
	public int set(int index, int element) {
	RangeCheck(index);

		int oldValue = elementData[index];
	elementData[index] = element;
	return oldValue;
    }

	public boolean add(int e) {
	ensureCapacity(size + 1);  // Increments modCount!!
	elementData[size++] = e;
	return true;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
	public void add(int index, int element) {
	ensureCapacity(size+1);  // Increments modCount!!
		System.arraycopy(elementData, index, elementData, index + 1, size - index);
	elementData[index] = element;
	size++;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
	public int remove(int index) {
		// RangeCheck(index);

	modCount++;
		int oldValue = elementData[index];

	int numMoved = size - index - 1;
	if (numMoved > 0)
	    System.arraycopy(elementData, index+1, elementData, index,
			     numMoved);
		elementData[--size] = 0; // Let gc do its work

	return oldValue;
    }

	public boolean removeData(int o) {
	    for (int index = 0; index < size; index++)
			if (o == elementData[index]) {
		    fastRemove(index);
		    return true;
		}
	return false;
    }

    /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     */
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
		elementData[--size] = 0; // Let gc do its work
    }

    public void clear() {
	modCount++;

	// Let gc do its work
	for (int i = 0; i < size; i++)
			elementData[i] = 0;

	size = 0;
    }

	public boolean addAll(Collection<Integer> c) {
		for (int integer : c) {
			add(integer);
		}
		return true;
    }

	public boolean addAll(int index, Collection<Integer> c) {
	if (index > size || index < 0)
	    throw new IndexOutOfBoundsException(
		"Index: " + index + ", Size: " + size);

	Object[] a = c.toArray();
	int numNew = a.length;
	ensureCapacity(size + numNew);  // Increments modCount

	int numMoved = size - index;
	if (numMoved > 0)
	    System.arraycopy(elementData, index, elementData, index + numNew,
			     numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
	size += numNew;
	return numNew != 0;
    }

    protected void removeRange(int fromIndex, int toIndex) {
	modCount++;
	int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                         numMoved);

	// Let gc do its work
	int newSize = size - (toIndex-fromIndex);
	while (size != newSize)
			elementData[--size] = 0;
    }

    private void RangeCheck(int index) {
	if (index >= size)
	    throw new IndexOutOfBoundsException(
		"Index: "+index+", Size: "+size);
    }

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int a : elementData) {
			sb.append(a + ",");
			
		}
		return sb.toString();
	}
}
