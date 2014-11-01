package sos.tools.lists;
import java.util.Arrays;

/**
 * 
 * @author Ali
 * @editor Salim
 * 
 */
public class LongList {
	public transient long[] elementData;
	private int size;
	
	public LongList(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		this.elementData = new long[initialCapacity];
	}
	
	public LongList() {
		this(10);
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public boolean contains(long o) {
		return indexOf(o) >= 0;
	}
	
	public int indexOf(long o) {
		for (int i = 0; i < size; i++)
			if (o == elementData[i])
				return i;
		return -1;
	}
	
	public int lastIndexOf(long o) {
		for (int i = size - 1; i >= 0; i--)
			if (o == elementData[i])
				return i;
		return -1;
	}
	
	public long get(int index) {
		return elementData[index];
	}
	
	public boolean add(long e) {
		ensureCapacity(size + 1); // Increments modCount!!
		elementData[size++] = e;
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
