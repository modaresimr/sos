package sos.tools.lists;
import java.util.Arrays;

import rescuecore2.worldmodel.EntityID;

/**
 * 
 * @author Ali
 * @editor Salim
 * 
 */
public class IDList {
	public transient int[] elementData;
	private int size;
	
	public IDList(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		this.elementData = new int[initialCapacity];
	}
	
	public IDList() {
		this(10);
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public boolean contains(EntityID o) {
		return indexOf(o) >= 0;
	}
	
	public int indexOf(EntityID o) {
		for (int i = 0; i < size; i++)
			if (o.getValue() == elementData[i])
				return i;
		return -1;
	}
	
	public int lastIndexOf(EntityID o) {
		for (int i = size - 1; i >= 0; i--)
			if (o.getValue() == elementData[i])
				return i;
		return -1;
	}
	
	public EntityID get(int index) {
		return new EntityID(elementData[index]);
	}
	
	public boolean add(EntityID e) {
		ensureCapacity(size + 1); // Increments modCount!!
		elementData[size++] = e.getValue();
		return true;
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = Arrays.copyOf(elementData, newCapacity);
		}
	}
	
	public void clear() {
		size = 0;
	}
}
