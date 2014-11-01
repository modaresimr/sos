package sos.base.entities;

//package sos.base.entities;
//
//import java.awt.Point;
//import java.awt.Polygon;
//import java.awt.Rectangle;
//import java.awt.Shape;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.PathIterator;
//import java.awt.geom.Rectangle2D;
//import java.util.ArrayList;
//import java.util.List;
//
//import rescuecore2.geometry.Point2D;
//import sos.tools.geom.Crossings;
//import sos.tools.geom.EvenOdd;
//
///**
// * The <code>Polygon</code> class encapsulates a description of a closed,
// * two-dimensional region within a coordinate space. This region is bounded by
// * an arbitrary number of line segments, each of which is one side of the
// * polygon. Internally, a polygon comprises of a list of {@code (x,y)}
// * coordinate pairs, where each pair defines a <i>vertex</i> of the polygon, and
// * two successive pairs are the endpoints of a line that is a side of the
// * polygon. The first and final pairs of {@code (x,y)} points are joined by a
// * line segment that closes the polygon. This <code>Polygon</code> is defined
// * with an even-odd winding rule. See
// * {@link java.awt.geom.PathIterator#WIND_EVEN_ODD WIND_EVEN_ODD} for a
// * definition of the even-odd winding rule. This class's hit-testing methods,
// * which include the <code>contains</code>, <code>intersects</code> and
// * <code>inside</code> methods, use the <i>insideness</i> definition described
// * in the {@link Shape} class comments.
// * 
// * @version 1.26, 07/24/98
// * @author Sami Shaio
// * @see Shape
// * @author Herb Jellinek
// * @since 1.0
// */
//public class SOSPolygon implements Shape, java.io.Serializable {
//
//	/**
//	 * The total number of points. The value of <code>npoints</code> represents
//	 * the number of valid points in this <code>Polygon</code> and might be less
//	 * than the number of elements in {@link #xpoints xpoints} or
//	 * {@link #ypoints ypoints}. This value can be NULL.
//	 * 
//	 * @serial
//	 * @see #addPoint(int, int)
//	 * @since 1.0
//	 */
//	public int npoints;
//
//	private List<Edge> edges;
//
//	/**
//	 * The bounds of this {@code Polygon}. This value can be null.
//	 * 
//	 * @serial
//	 * @see #getBoundingBox()
//	 * @see #getBounds()
//	 * @since 1.0
//	 */
//	protected Rectangle bounds;
//
//	/*
//	 * JDK 1.1 serialVersionUID
//	 */
//	private static final long serialVersionUID = -6460061437900069969L;
//
//	/*
//	 * Default length for xpoints and ypoints.
//	 */
//	@SuppressWarnings("unused")
//	private static final int MIN_LENGTH = 4;
//
//	/**
//	 * Creates an empty polygon.
//	 * 
//	 * @since 1.0
//	 */
//	public SOSPolygon() {
//		edges = new ArrayList<Edge>();
//		Polygon
//	}
//
//	/**
//	 * Constructs and initializes a <code>Polygon</code> from the specified
//	 * parameters.
//	 * 
//	 * @param xpoints
//	 *            an array of X coordinates
//	 * @param ypoints
//	 *            an array of Y coordinates
//	 * @param npoints
//	 *            the total number of points in the <code>Polygon</code>
//	 * @exception NegativeArraySizeException
//	 *                if the value of <code>npoints</code> is negative.
//	 * @exception IndexOutOfBoundsException
//	 *                if <code>npoints</code> is greater than the length of
//	 *                <code>xpoints</code> or the length of <code>ypoints</code>
//	 *                .
//	 * @exception NullPointerException
//	 *                if <code>xpoints</code> or <code>ypoints</code> is
//	 *                <code>null</code>.
//	 * @since 1.0
//	 */
//	public SOSPolygon(List<Edge> edges, int npoints) {
//		// Fix 4489009: should throw IndexOutofBoundsException instead
//		// of OutofMemoryException if npoints is huge and > {x,y}points.length
//		if (npoints > edges.size()) {
//			throw new IndexOutOfBoundsException("npoints > xpoints.length || "
//					+ "npoints > ypoints.length");
//		}
//		// Fix 6191114: should throw NegativeArraySizeException with
//		// negative npoints
//		if (npoints < 0) {
//			throw new NegativeArraySizeException("npoints < 0");
//		}
//		// Fix 6343431: Applet compatibility problems if arrays are not
//		// exactly npoints in length
//		this.npoints = npoints;
//		this.edges = edges;
//	}
//
//	/**
//	 * Resets this <code>Polygon</code> object to an empty polygon. The
//	 * coordinate arrays and the data in them are left untouched but the number
//	 * of points is reset to zero to mark the old vertex data as invalid and to
//	 * start accumulating new vertex data at the beginning. All
//	 * internally-cached data relating to the old vertices are discarded. Note
//	 * that since the coordinate arrays from before the reset are reused,
//	 * creating a new empty <code>Polygon</code> might be more memory efficient
//	 * than resetting the current one if the number of vertices in the new
//	 * polygon data is significantly smaller than the number of vertices in the
//	 * data from before the reset.
//	 * 
//	 * @see java.awt.Polygon#invalidate
//	 * @since 1.4
//	 */
//	public void reset() {
//		npoints = 0;
//		bounds = null;
//	}
//
//	/**
//	 * Invalidates or flushes any internally-cached data that depends on the
//	 * vertex coordinates of this <code>Polygon</code>. This method should be
//	 * called after any direct manipulation of the coordinates in the
//	 * <code>xpoints</code> or <code>ypoints</code> arrays to avoid inconsistent
//	 * results from methods such as <code>getBounds</code> or
//	 * <code>contains</code> that might cache data from earlier computations
//	 * relating to the vertex coordinates.
//	 * 
//	 * @see java.awt.Polygon#getBounds
//	 * @since 1.4
//	 */
//	public void invalidate() {
//		bounds = null;
//	}
//
//	/**
//	 * Translates the vertices of the <code>Polygon</code> by
//	 * <code>deltaX</code> along the x axis and by <code>deltaY</code> along the
//	 * y axis.
//	 * 
//	 * @param deltaX
//	 *            the amount to translate along the X axis
//	 * @param deltaY
//	 *            the amount to translate along the Y axis
//	 * @since 1.1
//	 */
//	public void translate(int deltaX, int deltaY) {
//		for (int i = 0; i < edges.size(); i++) {
//			edges.get(i).setEnd(
//					new Point2D(edges.get(i).getStartX() + deltaX, edges.get(i)
//							.getEndY() + deltaY));
//		}
//		if (bounds != null) {
//			bounds.translate(deltaX, deltaY);
//		}
//	}
//
//	/*
//	 * Calculates the bounding box of the points passed to the constructor. Sets
//	 * <code>bounds</code> to the result.
//	 * 
//	 * @param ArrayList<Edge>
//	 * 
//	 * @param npoints the total number of points
//	 */
//	void calculateBounds(List<Edge> edges) {
//		int boundsMinX = Integer.MAX_VALUE;
//		int boundsMinY = Integer.MAX_VALUE;
//		int boundsMaxX = Integer.MIN_VALUE;
//		int boundsMaxY = Integer.MIN_VALUE;
//		int i = 0;
//		while (i < npoints) {
//			int x = edges.get(i).getStartX();
//			boundsMinX = Math.min(boundsMinX, x);
//			boundsMaxX = Math.max(boundsMaxX, x);
//			int y = edges.get(i).getStartY();
//			boundsMinY = Math.min(boundsMinY, y);
//			boundsMaxY = Math.max(boundsMaxY, y);
//			i++;
//		}
//		bounds = new Rectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
//				boundsMaxY - boundsMinY);
//	}
//
//	/*
//	 * Calculates the bounding box of the points passed to the constructor. Sets
//	 * <code>bounds</code> to the result.
//	 * 
//	 * @param xpoints[] array of <i>x</i> coordinates
//	 * 
//	 * @param ypoints[] array of <i>y</i> coordinates
//	 * 
//	 * @param npoints the total number of points
//	 */
//	void calculateBounds(int xpoints[], int ypoints[], int npoints) {
//		int boundsMinX = Integer.MAX_VALUE;
//		int boundsMinY = Integer.MAX_VALUE;
//		int boundsMaxX = Integer.MIN_VALUE;
//		int boundsMaxY = Integer.MIN_VALUE;
//
//		for (int i = 0; i < npoints; i++) {
//			int x = xpoints[i];
//			boundsMinX = Math.min(boundsMinX, x);
//			boundsMaxX = Math.max(boundsMaxX, x);
//			int y = ypoints[i];
//			boundsMinY = Math.min(boundsMinY, y);
//			boundsMaxY = Math.max(boundsMaxY, y);
//		}
//		bounds = new Rectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
//				boundsMaxY - boundsMinY);
//	}
//
//	/*
//	 * Resizes the bounding box to accomodate the specified coordinates.
//	 * 
//	 * @param x,&nbsp;y the specified coordinates
//	 */
//	void updateBounds(int x, int y) {
//		if (x < bounds.x) {
//			bounds.width = bounds.width + (bounds.x - x);
//			bounds.x = x;
//		} else {
//			bounds.width = Math.max(bounds.width, x - bounds.x);
//			// bounds.x = bounds.x;
//		}
//
//		if (y < bounds.y) {
//			bounds.height = bounds.height + (bounds.y - y);
//			bounds.y = y;
//		} else {
//			bounds.height = Math.max(bounds.height, y - bounds.y);
//			// bounds.y = bounds.y;
//		}
//	}
//
//	@Override
//	public Rectangle getBounds() {
//		return getBoundingBox();
//	}
//
//	@Deprecated
//	public Rectangle getBoundingBox() {
//		if (npoints == 0) {
//			return new Rectangle();
//		}
//		if (bounds == null) {
//			calculateBounds(edges);
//		}
//		return bounds.getBounds();
//	}
//
//	public boolean contains(Point p) {
//		return contains(p.x, p.y);
//	}
//
//	public boolean contains(int x, int y) {
//		return contains((double) x, (double) y);
//	}
//
//	@Deprecated
//	public boolean inside(int x, int y) {
//		return contains((double) x, (double) y);
//	}
//
//	@Override
//	public Rectangle2D getBounds2D() {
//		return getBounds();
//	}
//
//	@Override
//	public boolean contains(double x, double y) {
//		if (npoints <= 2 || !getBoundingBox().contains(x, y)) {
//			return false;
//		}
//		int hits = 0;
//
//		int lastx = edges.get(edges.size() - 1).getEndX();
//		int lasty = edges.get(edges.size() - 1).getEndY();
//		int curx, cury;
//		// Walk the edges of the polygon
//		for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
//			curx = edges.get(i).getStartX();
//			cury = edges.get(i).getStartY();
//			if (cury == lasty) {
//				continue;
//			}
//
//			int leftx;
//			if (curx < lastx) {
//				if (x >= lastx) {
//					continue;
//				}
//				leftx = curx;
//			} else {
//				if (x >= curx) {
//					continue;
//				}
//				leftx = lastx;
//			}
//
//			double test1, test2;
//			if (cury < lasty) {
//				if (y < cury || y >= lasty) {
//					continue;
//				}
//				if (x < leftx) {
//					hits++;
//					continue;
//				}
//				test1 = x - curx;
//				test2 = y - cury;
//			} else {
//				if (y < lasty || y >= cury) {
//					continue;
//				}
//				if (x < leftx) {
//					hits++;
//					continue;
//				}
//				test1 = x - lastx;
//				test2 = y - lasty;
//			}
//
//			if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
//				hits++;
//			}
//		}
//
//		return ((hits & 1) != 0);
//	}
//
//	private Crossings getCrossings(double xlo, double ylo, double xhi,
//			double yhi) {
//		Crossings cross = new EvenOdd(xlo, ylo, xhi, yhi);
//		int lastx = edges.get(npoints - 1).getEndX();
//		int lasty = edges.get(npoints - 1).getEndY();
//		int curx, cury;
//		// Walk the edges of the polygon
//		for (int i = 0; i < npoints; i++) {
//			curx = edges.get(i).getStartX();
//			cury = edges.get(i).getStartY();
//			if (cross.accumulateLine(lastx, lasty, curx, cury)) {
//				return null;
//			}
//			lastx = curx;
//			lasty = cury;
//		}
//
//		return cross;
//	}
//
//	public boolean contains(Point2D p) {
//		return contains((int) p.getX(), (int) p.getY());
//	}
//
//	@Override
//	public boolean intersects(double x, double y, double w, double h) {
//		if (npoints <= 0 || !getBoundingBox().intersects(x, y, w, h)) {
//			return false;
//		}
//
//		Crossings cross = getCrossings(x, y, x + w, y + h);
//		return (cross == null || !cross.isEmpty());
//	}
//
//	@Override
//	public boolean intersects(Rectangle2D r) {
//		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
//	}
//
//	@Override
//	public boolean contains(double x, double y, double w, double h) {
//		if (npoints <= 0 || !getBoundingBox().intersects(x, y, w, h)) {
//			return false;
//		}
//
//		Crossings cross = getCrossings(x, y, x + w, y + h);
//		return (cross != null && cross.covers(y, y + h));
//	}
//
//	@Override
//	public boolean contains(Rectangle2D r) {
//		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
//	}
//
//	@Override
//	public PathIterator getPathIterator(AffineTransform at) {
//		return new PolygonPathIterator(this, at);
//	}
//
//	@Override
//	public PathIterator getPathIterator(AffineTransform at, double flatness) {
//		return getPathIterator(at);
//	}
//
//	class PolygonPathIterator implements PathIterator {
//		SOSPolygon poly;
//		AffineTransform transform;
//		int index;
//
//		public PolygonPathIterator(SOSPolygon pg, AffineTransform at) {
//			poly = pg;
//			transform = at;
//			if (pg.npoints == 0) {
//				// Prevent a spurious SEG_CLOSE segment
//				index = 1;
//			}
//		}
//
//		@Override
//		public int getWindingRule() {
//			return WIND_EVEN_ODD;
//		}
//
//		@Override
//		public boolean isDone() {
//			return index > poly.npoints;
//		}
//
//		@Override
//		public void next() {
//			index++;
//		}
//
//		@Override
//		public int currentSegment(float[] coords) {
//			if (index >= poly.npoints) {
//				return SEG_CLOSE;
//			}
//			coords[0] = poly.edges.get(index).getStartX();
//			coords[1] = poly.edges.get(index).getStartY();
//
//			if (transform != null) {
//				transform.transform(coords, 0, coords, 0, 1);
//			}
//			return (index == 0 ? SEG_MOVETO : SEG_LINETO);
//		}
//
//		@Override
//		public int currentSegment(double[] coords) {
//			if (index >= poly.npoints) {
//				return SEG_CLOSE;
//			}
//			coords[0] = poly.edges.get(index).getStartX();
//			coords[1] = poly.edges.get(index).getStartY();
//
//			if (transform != null) {
//				transform.transform(coords, 0, coords, 0, 1);
//			}
//			return (index == 0 ? SEG_MOVETO : SEG_LINETO);
//		}
//	}
//
//	@Override
//	public boolean contains(java.awt.geom.Point2D p) {
//		return false;
//	}
//}
/*
 * @(#)Polygon.java	1.57 06/02/24
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

 import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import sos.tools.geom.Crossings;
import sos.tools.geom.EvenOdd;

/**
 * The <code>Polygon</code> class encapsulates a description of a
 * closed, two-dimensional region within a coordinate space. This
 * region is bounded by an arbitrary number of line segments, each of
 * which is one side of the polygon. Internally, a polygon
 * comprises of a list of {@code (x,y)} coordinate pairs, where each pair defines a <i>vertex</i> of the
 * polygon, and two successive pairs are the endpoints of a
 * line that is a side of the polygon. The first and final
 * pairs of {@code (x,y)} points are joined by a line segment
 * that closes the polygon. This <code>Polygon</code> is defined with
 * an even-odd winding rule. See {@link java.awt.geom.PathIterator#WIND_EVEN_ODD WIND_EVEN_ODD} for a definition of the even-odd winding rule.
 * This class's hit-testing methods, which include the <code>contains</code>, <code>intersects</code> and <code>inside</code> methods, use the <i>insideness</i> definition described in the {@link Shape} class comments.
 * 
 * @version 1.26, 07/24/98
 * @author Sami Shaio
 * @see Shape
 * @author Herb Jellinek
 * @since 1.0
 */
public class SOSPolygon implements Shape, java.io.Serializable {

	/**
	 * The total number of points. The value of <code>npoints</code> represents the number of valid points in this <code>Polygon</code> and might be less than the number of elements in {@link #xpoints xpoints} or {@link #ypoints ypoints}.
	 * This value can be NULL.
	 * 
	 * @serial
	 * @see #addPoint(int, int)
	 * @since 1.0
	 */
	//	public int npoints;

	List<Edge> edges;

	/**
	 * The bounds of this {@code Polygon}.
	 * This value can be null.
	 * 
	 * @serial
	 * @see #getBoundingBox()
	 * @see #getBounds()
	 * @since 1.0
	 */
	protected Rectangle bounds;

	/*
	 * JDK 1.1 serialVersionUID
	 */
	private static final long serialVersionUID = -6460061437900069969L;

	/*
	 * Default length for xpoints and ypoints.
	 */
	//	private static final int MIN_LENGTH = 4;

	/**
	 * Creates an empty polygon.
	 * 
	 * @since 1.0
	 */
	public SOSPolygon() {
		edges = new ArrayList<Edge>();
	}

	/**
	 * Constructs and initializes a <code>Polygon</code> from the specified
	 * parameters.
	 * 
	 * @param xpoints
	 *            an array of X coordinates
	 * @param ypoints
	 *            an array of Y coordinates
	 * @param npoints
	 *            the total number of points in the <code>Polygon</code>
	 * @exception NegativeArraySizeException
	 *                if the value of <code>npoints</code> is negative.
	 * @exception IndexOutOfBoundsException
	 *                if <code>npoints</code> is
	 *                greater than the length of <code>xpoints</code> or the length of <code>ypoints</code>.
	 * @exception NullPointerException
	 *                if <code>xpoints</code> or <code>ypoints</code> is <code>null</code>.
	 * @since 1.0
	 */
	public SOSPolygon(List<Edge> edges) {
		this.edges = edges;
	}

	/**
	 * Resets this <code>Polygon</code> object to an empty polygon.
	 * The coordinate arrays and the data in them are left untouched
	 * but the number of points is reset to zero to mark the old
	 * vertex data as invalid and to start accumulating new vertex
	 * data at the beginning.
	 * All internally-cached data relating to the old vertices
	 * are discarded.
	 * Note that since the coordinate arrays from before the reset
	 * are reused, creating a new empty <code>Polygon</code> might
	 * be more memory efficient than resetting the current one if
	 * the number of vertices in the new polygon data is significantly
	 * smaller than the number of vertices in the data from before the
	 * reset.
	 * 
	 * @see java.awt.Polygon#invalidate
	 * @since 1.4
	 */
	public void reset() {
		//		npoints = 0;
		bounds = null;
	}

	/**
	 * Invalidates or flushes any internally-cached data that depends
	 * on the vertex coordinates of this <code>Polygon</code>.
	 * This method should be called after any direct manipulation
	 * of the coordinates in the <code>xpoints</code> or <code>ypoints</code> arrays to avoid inconsistent results
	 * from methods such as <code>getBounds</code> or <code>contains</code> that might cache data from earlier computations relating to
	 * the vertex coordinates.
	 * 
	 * @see java.awt.Polygon#getBounds
	 * @since 1.4
	 */
	public void invalidate() {
		bounds = null;
	}

	/**
	 * Translates the vertices of the <code>Polygon</code> by <code>deltaX</code> along the x axis and by <code>deltaY</code> along the y axis.
	 * 
	 * @param deltaX
	 *            the amount to translate along the X axis
	 * @param deltaY
	 *            the amount to translate along the Y axis
	 * @since 1.1
	 */
	public void translate(int deltaX, int deltaY) {
		if (edges == null) {
			return;
		}
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).setEnd(new rescuecore2.geometry.Point2D(edges.get(i).getEndX(), edges.get(i).getEndY()));
		}
		if (bounds != null) {
			bounds.translate(deltaX, deltaY);
		}
	}

	/*
	 * Calculates the bounding box of the points passed to the constructor.
	 * Sets <code>bounds</code> to the result.
	 * @param xpoints[] array of <i>x</i> coordinates
	 * @param ypoints[] array of <i>y</i> coordinates
	 * @param npoints the total number of points
	 */
	void calculateBounds(List<Edge> edges) {
		int boundsMinX = Integer.MAX_VALUE;
		int boundsMinY = Integer.MAX_VALUE;
		int boundsMaxX = Integer.MIN_VALUE;
		int boundsMaxY = Integer.MIN_VALUE;

		for (int i = 0; i < edges.size(); i++) {
			int x = edges.get(i).getStartX();
			boundsMinX = Math.min(boundsMinX, x);
			boundsMaxX = Math.max(boundsMaxX, x);
			int y = edges.get(i).getStartY();
			boundsMinY = Math.min(boundsMinY, y);
			boundsMaxY = Math.max(boundsMaxY, y);
		}
		bounds = new Rectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY);
	}

	/*
	 * Resizes the bounding box to accomodate the specified coordinates.
	 * @param x,&nbsp;y the specified coordinates
	 */
	void updateBounds(int x, int y) {
		if (x < bounds.x) {
			bounds.width = bounds.width + (bounds.x - x);
			bounds.x = x;
		} else {
			bounds.width = Math.max(bounds.width, x - bounds.x);
			// bounds.x = bounds.x;
		}

		if (y < bounds.y) {
			bounds.height = bounds.height + (bounds.y - y);
			bounds.y = y;
		} else {
			bounds.height = Math.max(bounds.height, y - bounds.y);
			// bounds.y = bounds.y;
		}
	}

	//	/**
	//	 * Appends the specified coordinates to this <code>Polygon</code>.
	//	 * <p>
	//	 * If an operation that calculates the bounding box of this <code>Polygon</code> has already been performed, such as <code>getBounds</code> or <code>contains</code>, then this method updates the bounding box.
	//	 * 
	//	 * @param x
	//	 *            the specified X coordinate
	//	 * @param y
	//	 *            the specified Y coordinate
	//	 * @see java.awt.Polygon#getBounds
	//	 * @see java.awt.Polygon#contains
	//	 * @since 1.0
	//	 */
	//	public void addPoint(int x, int y) {
	//		
	//	}

	/**
	 * Gets the bounding box of this <code>Polygon</code>.
	 * The bounding box is the smallest {@link Rectangle} whose
	 * sides are parallel to the x and y axes of the
	 * coordinate space, and can completely contain the <code>Polygon</code>.
	 * 
	 * @return a <code>Rectangle</code> that defines the bounds of this <code>Polygon</code>.
	 * @since 1.1
	 */
	@Override
	public Rectangle getBounds() {
		return getBoundingBox();
	}

	/**
	 * Returns the bounds of this <code>Polygon</code>.
	 * 
	 * @return the bounds of this <code>Polygon</code>.
	 * @deprecated As of JDK version 1.1,
	 *             replaced by <code>getBounds()</code>.
	 * @since 1.0
	 */
	@Deprecated
	public Rectangle getBoundingBox() {
		if (edges == null)
			return null;
		if (edges.size() == 0) {
			return new Rectangle();
		}
		if (bounds == null) {
			calculateBounds(edges);
		}
		return bounds.getBounds();
	}

	/**
	 * Determines whether the specified {@link Point} is inside this <code>Polygon</code>.
	 * 
	 * @param p
	 *            the specified <code>Point</code> to be tested
	 * @return <code>true</code> if the <code>Polygon</code> contains the <code>Point</code>; <code>false</code> otherwise.
	 * @see #contains(double, double)
	 * @since 1.0
	 */
	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}

	/**
	 * Determines whether the specified coordinates are inside this <code>Polygon</code>.
	 * <p>
	 * 
	 * @param x
	 *            the specified X coordinate to be tested
	 * @param y
	 *            the specified Y coordinate to be tested
	 * @return {@code true} if this {@code Polygon} contains
	 *         the specified coordinates {@code (x,y)}; {@code false} otherwise.
	 * @see #contains(double, double)
	 * @since 1.1
	 */
	public boolean contains(int x, int y) {
		return contains((double) x, (double) y);
	}

	/**
	 * Determines whether the specified coordinates are contained in this <code>Polygon</code>.
	 * 
	 * @param x
	 *            the specified X coordinate to be tested
	 * @param y
	 *            the specified Y coordinate to be tested
	 * @return {@code true} if this {@code Polygon} contains
	 *         the specified coordinates {@code (x,y)}; {@code false} otherwise.
	 * @see #contains(double, double)
	 * @deprecated As of JDK version 1.1,
	 *             replaced by <code>contains(int, int)</code>.
	 * @since 1.0
	 */
	@Deprecated
	public boolean inside(int x, int y) {
		return contains((double) x, (double) y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	@Override
	public Rectangle2D getBounds2D() {
		return getBounds();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	@Override
	public boolean contains(double x, double y) {
		if (edges == null)
			return false;
		if (edges.size() <= 2 || !getBoundingBox().contains(x, y)) {
			return false;
		}
		int hits = 0;

		int lastx = edges.get(edges.size() - 1).getStartX();
		int lasty = edges.get(edges.size() - 1).getStartY();
		int curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < edges.size(); lastx = curx, lasty = cury, i++) {
			curx = edges.get(i).getStartX();
			cury = edges.get(i).getStartY();

			if (cury == lasty) {
				continue;
			}

			int leftx;
			if (curx < lastx) {
				if (x >= lastx) {
					continue;
				}
				leftx = curx;
			} else {
				if (x >= curx) {
					continue;
				}
				leftx = lastx;
			}

			double test1, test2;
			if (cury < lasty) {
				if (y < cury || y >= lasty) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - curx;
				test2 = y - cury;
			} else {
				if (y < lasty || y >= cury) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - lastx;
				test2 = y - lasty;
			}

			if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
				hits++;
			}
		}

		return ((hits & 1) != 0);
	}

	private Crossings getCrossings(double xlo, double ylo, double xhi, double yhi) {
		if (edges == null)
			return null;
		Crossings cross = new EvenOdd(xlo, ylo, xhi, yhi);
		int lastx = edges.get(edges.size() - 1).getStartX();
		int lasty = edges.get(edges.size() - 1).getStartY();
		int curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < edges.size(); i++) {
			curx = edges.get(i).getStartX();
			cury = edges.get(i).getStartY();
			if (cross.accumulateLine(lastx, lasty, curx, cury)) {
				return null;
			}
			lastx = curx;
			lasty = cury;
		}

		return cross;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	@Override
	public boolean intersects(double x, double y, double w, double h) {
		if (edges.size() <= 0 || !getBoundingBox().intersects(x, y, w, h)) {
			return false;
		}

		Crossings cross = getCrossings(x, y, x + w, y + h);
		return (cross == null || !cross.isEmpty());
	}
//	public boolean intersectsNew(Rectangle r) {
//		if (edges.size() <= 0 || !r.intersects(getBoundingBox())) {
//			return false;
//		}
//
//		Crossings cross = getCrossings(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
//		return (cross == null || !cross.isEmpty());
//	}
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	@Override
	public boolean contains(double x, double y, double w, double h) {
		if (edges == null)
			return false;
		if (edges.size() <= 0 || !getBoundingBox().intersects(x, y, w, h)) {
			return false;
		}

		Crossings cross = getCrossings(x, y, x + w, y + h);
		return (cross != null && cross.covers(y, y + h));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public boolean isAValidShape() {
		return !(edges == null);
	}

	/**
	 * Returns an iterator object that iterates along the boundary of this <code>Polygon</code> and provides access to the geometry
	 * of the outline of this <code>Polygon</code>. An optional {@link AffineTransform} can be specified so that the coordinates
	 * returned in the iteration are transformed accordingly.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the
	 *            coordinates as they are returned in the iteration, or <code>null</code> if untransformed coordinates are desired
	 * @return a {@link PathIterator} object that provides access to the
	 *         geometry of this <code>Polygon</code>.
	 * @since 1.2
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new SOSPolygonPathIterator(this, at);
	}

	/**
	 * Returns an iterator object that iterates along the boundary of
	 * the <code>Shape</code> and provides access to the geometry of the
	 * outline of the <code>Shape</code>. Only SEG_MOVETO, SEG_LINETO, and
	 * SEG_CLOSE point types are returned by the iterator.
	 * Since polygons are already flat, the <code>flatness</code> parameter
	 * is ignored. An optional <code>AffineTransform</code> can be specified
	 * in which case the coordinates returned in the iteration are transformed
	 * accordingly.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the
	 *            coordinates as they are returned in the iteration, or <code>null</code> if untransformed coordinates are desired
	 * @param flatness
	 *            the maximum amount that the control points
	 *            for a given curve can vary from colinear before a subdivided
	 *            curve is replaced by a straight line connecting the
	 *            endpoints. Since polygons are already flat the <code>flatness</code> parameter is ignored.
	 * @return a <code>PathIterator</code> object that provides access to the <code>Shape</code> object's geometry.
	 * @since 1.2
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getPathIterator(at);
	}

	class SOSPolygonPathIterator implements PathIterator {
		SOSPolygon poly;
		AffineTransform transform;
		int index;

		public SOSPolygonPathIterator(SOSPolygon pg, AffineTransform at) {
			poly = pg;
			transform = at;
			if (poly.edges == null)
				index = 1;
			else {
				if (poly.edges.size() == 0) {
					// Prevent a spurious SEG_CLOSE segment
					index = 1;
				}
			}
		}

		/**
		 * Returns the winding rule for determining the interior of the
		 * path.
		 * 
		 * @return an integer representing the current winding rule.
		 * @see PathIterator#WIND_NON_ZERO
		 */
		@Override
		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}

		/**
		 * Tests if there are more points to read.
		 * 
		 * @return <code>true</code> if there are more points to read; <code>false</code> otherwise.
		 */
		@Override
		public boolean isDone() {
			if (edges == null)
				return true;
			return index > poly.edges.size();
		}

		/**
		 * Moves the iterator forwards, along the primary direction of
		 * traversal, to the next segment of the path when there are
		 * more points in that direction.
		 */
		@Override
		public void next() {
			index++;
		}

		/**
		 * Returns the coordinates and type of the current path segment in
		 * the iteration.
		 * The return value is the path segment type:
		 * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE.
		 * A <code>float</code> array of length 2 must be passed in and
		 * can be used to store the coordinates of the point(s).
		 * Each point is stored as a pair of <code>float</code> x,&nbsp;y
		 * coordinates. SEG_MOVETO and SEG_LINETO types return one
		 * point, and SEG_CLOSE does not return any points.
		 * 
		 * @param coords
		 *            a <code>float</code> array that specifies the
		 *            coordinates of the point(s)
		 * @return an integer representing the type and coordinates of the
		 *         current path segment.
		 * @see PathIterator#SEG_MOVETO
		 * @see PathIterator#SEG_LINETO
		 * @see PathIterator#SEG_CLOSE
		 */
		@Override
		public int currentSegment(float[] coords) {
			if (edges == null)
				return SEG_CLOSE;
			if (index >= poly.edges.size()) {
				return SEG_CLOSE;
			}
			coords[0] = poly.edges.get(index).getStartX();
			coords[1] = poly.edges.get(index).getStartY();
			if (transform != null) {
				transform.transform(coords, 0, coords, 0, 1);
			}
			return (index == 0 ? SEG_MOVETO : SEG_LINETO);
		}

		/**
		 * Returns the coordinates and type of the current path segment in
		 * the iteration.
		 * The return value is the path segment type:
		 * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE.
		 * A <code>double</code> array of length 2 must be passed in and
		 * can be used to store the coordinates of the point(s).
		 * Each point is stored as a pair of <code>double</code> x,&nbsp;y
		 * coordinates.
		 * SEG_MOVETO and SEG_LINETO types return one point,
		 * and SEG_CLOSE does not return any points.
		 * 
		 * @param coords
		 *            a <code>double</code> array that specifies the
		 *            coordinates of the point(s)
		 * @return an integer representing the type and coordinates of the
		 *         current path segment.
		 * @see PathIterator#SEG_MOVETO
		 * @see PathIterator#SEG_LINETO
		 * @see PathIterator#SEG_CLOSE
		 */
		@Override
		public int currentSegment(double[] coords) {
			if (edges == null)
				return SEG_CLOSE;
			if (index >= poly.edges.size()) {
				return SEG_CLOSE;
			}
			coords[0] = poly.edges.get(index).getStartX();
			coords[1] = poly.edges.get(index).getStartY();
			if (transform != null) {
				transform.transform(coords, 0, coords, 0, 1);
			}
			return (index == 0 ? SEG_MOVETO : SEG_LINETO);
		}
	}
//	public static void main(String[] args) {
//		ArrayList<Edge>edges=new ArrayList<Edge>();
//		edges.add(new Edge(new rescuecore2.geometry.Point2D(2,2),new rescuecore2.geometry.Point2D(100,100)));
//		edges.add(new Edge(new rescuecore2.geometry.Point2D(100,100),new rescuecore2.geometry.Point2D(100,50)));
//		edges.add(new Edge(new rescuecore2.geometry.Point2D(100,50),new rescuecore2.geometry.Point2D(2,22)));
//		edges.add(new Edge(new rescuecore2.geometry.Point2D(2,22),new rescuecore2.geometry.Point2D(2,2)));
////		SOSPolygon p=
////				new SOSPolygon(edges);
////		ShapeDebugFrame f=new ShapeDebugFrame();
////		f.setBackground( new ShapeDebugFrame.AWTShapeInfo(p, "Me2", Color.red, true));
////		edges=new ArrayList<Edge>();
////		edges.add(new Edge(new rescuecore2.geometry.Point2D(2,2),new rescuecore2.geometry.Point2D(100,100)));
////		edges.add(new Edge(new rescuecore2.geometry.Point2D(100,100),new rescuecore2.geometry.Point2D(100,50)));
////		edges.add(new Edge(new rescuecore2.geometry.Point2D(100,50),new rescuecore2.geometry.Point2D(2,22)));
////		p=new SOSPolygon(edges);
////		f.show("D",new ShapeDebugFrame.AWTShapeInfo(p, "Me2", Color.black, true));
//		
//	}
	
}
