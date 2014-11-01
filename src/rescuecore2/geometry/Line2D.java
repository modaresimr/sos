package rescuecore2.geometry;

import static rescuecore2.geometry.GeometryTools2D.nearlyZero;

import java.awt.Point;

/**
 * A line segment in 2D space. Lines are immutable.
 */
public class Line2D {
	private Point2D origin;
	private Point2D end;

	/**
	 * Create a new line segment.
	 *
	 * @param origin
	 *            The origin of the line.
	 * @param direction
	 *            The direction of the line.
	 */
	public Line2D(Point2D origin, Vector2D direction) {
		this.origin = origin;
		this.end = origin.plus(direction);
	}

	/**
	 * Create a new line segment.
	 *
	 * @param origin
	 *            The origin of the line.
	 * @param end
	 *            The end of the line.
	 */
	public Line2D(Point2D origin, Point2D end) {
		this.origin = origin;
		this.end = end;
	}

	/**
	 * Create a new line segment.
	 *
	 * @param x
	 *            The x coordinate of the origin.
	 * @param y
	 *            The y coordinate of the origin.
	 * @param dx
	 *            The x component of the direction.
	 * @param dy
	 *            The y component of the direction.
	 */
	public Line2D(double x, double y, double dx, double dy) {
		this(new Point2D(x, y), new Vector2D(dx, dy));
	}

	public Line2D(Point line1p1, Point line1p2) {
		this(new Point2D(line1p1.getX(), line1p1.getY()),new Point2D(line1p2.getX(), line1p2.getY()));
	}

	public Line2D(java.awt.geom.Line2D l) {
		this(l.getX1(), l.getY1(), l.getX2()-l.getX1(), l.getY2()-l.getY1());
	}

	/**
	 * Get a point along this line.
	 *
	 * @param t
	 *            The distance along the direction vector to create the point.
	 * @return A new Point2D.
	 */
	public Point2D getPoint(double t) {
		return origin.translate(t * getDirection().getX(), t * getDirection().getY());
	}

	/**
	 * Get the origin of this line segment.
	 *
	 * @return The origin.
	 */
	public Point2D getOrigin() {
		return origin;
	}

	/**
	 * Get the endpoint of this line segment.
	 *
	 * @return The endpoint.
	 */
	public Point2D getEndPoint() {
		return end;
	}

	/**
	 * Get the direction of this line segment.
	 *
	 * @return The direction vector.
	 */
	public Vector2D getDirection() {
		//        return direction;
		return end.minus(origin);
	}

	@Override
	public String toString() {
		return "Line from " + origin + " towards " + end + " (direction = " + getDirection() + ")";
	}

	/**
	 * Find out how far along this line the intersection point with another line is.
	 *
	 * @param other
	 *            The other line.
	 * @return How far along this line (in terms of this line's direction vector) the intersection point is, or NaN if the lines are parallel.
	 */
	public double getIntersection(Line2D other) {
		double bxax = getDirection().getX();
		double dycy = other.getDirection().getY();
		double byay = getDirection().getY();
		double dxcx = other.getDirection().getX();
		double cxax = other.origin.getX() - origin.getX();
		double cyay = other.origin.getY() - origin.getY();
		double d = (bxax * dycy) - (byay * dxcx);
		double t = (cxax * dycy) - (cyay * dxcx);
		if (nearlyZero(d)) {
			// d is close to zero: lines are parallel so no intersection
			return Double.NaN;
		}
		return t / d;
	}

	public double getLength() {
		return GeometryTools2D.getDistance(origin, end);
	}

	/**
	 * Ehsan
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof Line2D) {
			Line2D l = (Line2D) obj;
			if ((l.origin.equals(origin) && l.end.equals(end)) || (l.end.equals(origin) && l.origin.equals(end))) {
				return true;
			}
		}
		return false;
	}

	/*
	 * @author Salim
	 */
	public void switchHeadAndTail() {
		Point2D p = origin;
		origin = end;
		end = p;
	}

	public void setEnd(Point2D end) {
		this.end = end;
	}

	public void setOrigin(Point2D origin) {
		this.origin = origin;
	}
}
