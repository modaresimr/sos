package sos.tools.lists;
import rescuecore2.geometry.Point2D;
import sos.base.util.blockadeEstimator.AliGeometryTools;

/**
 * 
 * @author Ali
 * @edited Salim
 * 
 */
public class Point2DList {
	DoubleList heads;
	DoubleList tails;
	private int size;
	
	public Point2DList(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		createLists(initialCapacity);
	}
	
	public void createLists(int initial) {
		heads = new DoubleList(initial);
		tails = new DoubleList(initial);
	}
	
	public Point2DList() {
		this(10);
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public boolean contains(Point2D o) {
		return indexOf(o) >= 0;
	}
	
	public int indexOf(Point2D o) {
		for (int i = 0; i < size; i++) {
			if (isEqual(o, new Point2D(heads.get(i), tails.get(i))))
				return i;
		}
		
		return -1;
	}
	
	public boolean isEqual(Point2D p1, Point2D p2) {
		if (p1.getX() - p2.getX() > 0.000000001)
			if (p2.getY() - p2.getY() > 0.000000001)
				return true;
		return false;
	}
	
	public int lastIndexOf(Point2D p) {
		for (int i = size - 1; i >= 0; i--)
			if (AliGeometryTools.areEqual(p.getX(), heads.get(i), 0.0000000001))
				return i;
		return -1;
	}
	
	public Point2D get(int index) {
		return new Point2D(heads.get(index), tails.get(index));
	}
	
	public boolean add(Point2D point) {
		ensureCapacity(size + 1); // Increments modCount!!
		heads.add(point.getX());
		tails.add(point.getY());
		return true;
	}
	
	public void ensureCapacity(int minCapacity) {
		heads.ensureCapacity(minCapacity);
		tails.ensureCapacity(minCapacity);
	}
	
	public void clear() {
		size = 0;
	}
}
