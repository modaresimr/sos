package rescuecore2.geometry;

import sos.base.util.blockadeEstimator.AliGeometryTools;

/**
 * A point in 2D space. Points are immutable.
 */
public class Point2D {
	public int reachablityIndex = -1;
	private double x;
	private double y;
	public double ReachablityEdgeIndex = 0;

	/**
	 * Create a new Point2D.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 */
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the X coordinate.
	 * 
	 * @return The X coordinate.
	 */
	public double getX() {
		return x;
	}

	public int getIntX() {
		return (int) x;
	}

	public int getIntY() {
		return (int) y;
	}

	/**
	 * Get the Y coordinate.
	 * 
	 * @return The Y coordinate.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Set the X and Y coordinations
	 * 
	 * @param x
	 * @param y
	 */
	public void setCoordinations(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new Point2D that is a translation of this point.
	 * 
	 * @param dx
	 *            The x translation.
	 * @param dy
	 *            The y translation.
	 * @return A new Point2D.
	 */
	public Point2D translate(double dx, double dy) {
		return new Point2D(x + dx, y + dy);
	}

	/**
	 * Create a vector by subtracting a point from this point.
	 * 
	 * @param p
	 *            The Point2D to subtract from this one.
	 * @return A new Vector2D that represents the vector from the other point to
	 *         this one.
	 */
	public Vector2D minus(Point2D p) {
		return new Vector2D(this.x - p.x, this.y - p.y);
	}

	/**
	 * Create a Point2D by adding a vector to this point.
	 * 
	 * @param v
	 *            The Vector2D to add.
	 * @return A new Point2D.
	 */
	public Point2D plus(Vector2D v) {
		return new Point2D(this.x + v.getX(), this.y + v.getY());
	}

	@Override
	public String toString() {
		return "Point2D[" + x + " , " + y + "]";
	}

	/**
	 * Ehsan && Edited By Nima
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof Point2D) {
			Point2D p = (Point2D) obj;
			if ((Math.abs(p.x - x) < .1) && (Math.abs(p.y - y) < .1)) {
				return true;
			}
		}
		return false;
	}

	// Nima**********************************************************************************************************
	public boolean isEqualTo(Point2D p) {
		if ((Math.abs(p.x - x) < .01) && (Math.abs(p.y - y) < .01)) {
			return true;
		}
		return false;
	}

	public boolean isEqualTo1(Point2D p) {
		if ((Math.abs(p.x - x) < 1) && (Math.abs(p.y - y) < 1)) {
			return true;
		}
		return false;
	}

	// ////////////////////////////////////SOS////////////////////////////////
	/**
	 * @author Ali
	 */
	public java.awt.Point toGeomPoint() {
		return new java.awt.Point((int) x, (int) y);
	}

	/**
	 * @author Ali
	 */
	public boolean equalsWith(Point2D p, double threshold) {
		if (AliGeometryTools.areEqual(getX(), p.getX(), threshold)
				&& AliGeometryTools.areEqual(getY(), p.getY(), threshold))
			return true;
		return false;
	}

	/**
	 * @author Ali
	 */
	public int hashId() {
		long bits = java.lang.Double.doubleToLongBits(getX()) >> 5;
		bits ^= (java.lang.Double.doubleToLongBits(getY()) * 31) >> 5;
		return (((int) bits) ^ ((int) (bits >> (32 - 5))));
	}

	public double distanceSq(Point2D start) {
		double dx = getX() - start.getX();
		double dy = getY() - start.getY();
		return (dx * dx + dy * dy);
	}

	public double distance(Point2D start) {
		double dx = getX() - start.getX();
		double dy = getY() - start.getY();
		return  Math.hypot(dx, dy);
	}

}