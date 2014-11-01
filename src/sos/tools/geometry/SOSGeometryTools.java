package sos.tools.geometry;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import sos.base.entities.Area;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.SOSPolygon;
import sos.tools.Utils;

public final class SOSGeometryTools {
	public static int AGENT_RADIUS = 500;

	/**
	 * This function returns an instance of Class Shape which can be used in all java Geometric Classes for example later in this class this function is used to check if to Areas do intersect or not.
	 * 
	 * @return Shape
	 */

	public static Shape getShape(Area a) {
		// implemented by salim
		//		int[] allApexes = a.getApexList();
		//		int count = allApexes.length / 2;
		//		int[] xs = new int[count];
		//		int[] ys = new int[count];
		//		for (int i = 0; i < count; ++i) {
		//			xs[i] = allApexes[i * 2];
		//			ys[i] = allApexes[i * 2 + 1];
		//		}
		//		return shape;
		return new SOSPolygon(a.getEdges());
	}

	//	public static Shape getShape(int[] allApexes) {
	//		// implemented by salim
	////		int count = allApexes.length / 2;
	////		int[] xs = new int[count];
	////		int[] ys = new int[count];
	////		for (int i = 0; i < count; ++i) {
	////			xs[i] = allApexes[i * 2];
	////			ys[i] = allApexes[i * 2 + 1];
	////		}
	////		Shape shape = new Polygon(xs, ys, count);
	////		return shape;
	//		return new SOSPolygon(edges, npoints)
	//	}

	/**
	 * This function is Used to see if two Areas have intersection or not.
	 * 
	 * @return boolean
	 */
	public static boolean haveIntersection(Area a, Area b) {
		// implemented by salim
		Shape sa = getShape(a);
		Shape sb = getShape(b);
		java.awt.geom.Area a1 = new java.awt.geom.Area(sa);
		java.awt.geom.Area a2 = new java.awt.geom.Area(sb);
		a1.intersect(a2);
		return (!a1.isEmpty());
	}

	/**
	 * This function is Used to see if two Areas have intersection or not.
	 * 
	 * @return boolean
	 */
	public static boolean haveIntersection(Area a, Shape b) {
		// implemented by salim
		Shape sa = getShape(a);
		java.awt.geom.Area a1 = new java.awt.geom.Area(sa);
		java.awt.geom.Area a2 = new java.awt.geom.Area(b);
		a1.intersect(a2);
		return (!a1.isEmpty());
	}

	/**
	 * This function is Used to see if two Areas have intersection or not.
	 * 
	 * @return boolean
	 */
	public static boolean haveIntersection(Shape a, Shape b) {
		// implemented by salim
		java.awt.geom.Area a1 = new java.awt.geom.Area(a);
		java.awt.geom.Area a2 = new java.awt.geom.Area(b);
		a1.intersect(a2);
		return (!a1.isEmpty());

	}

	/**
	 * finds the intersection of a line and the Line which contains the point and is perpendicular to the first line.
	 * 
	 * @param l1
	 *            is instance of sos.utils.geometry.Line
	 * @param l2
	 *            is instance of sos.utils.geometry.Line
	 * @return Point if there is a intersection and returns null if they are parallel
	 */
	public static Point2D getIntersection(int[] apexesOfLine, Point2D point) {
		// implemented by salim
		Line l = new Line(apexesOfLine[0], apexesOfLine[1], apexesOfLine[2], apexesOfLine[3]);
		Line l2 = new Line(l.getPerpendicularIncline(), (int) point.getX(), (int) point.getY());
		float[] p;
		if (l2.getIncline() == Double.MAX_VALUE) {
			p = intersect(new float[] { apexesOfLine[0], apexesOfLine[1], apexesOfLine[2], apexesOfLine[3] }, null);
		} else {
			p = intersect(new float[] { apexesOfLine[0], apexesOfLine[1], apexesOfLine[2], apexesOfLine[3] }, new float[] { (float) l2.getIncline(), (float) l2.getC() });
		}
		if (p == null)
			return null;
		return new Point2D(p[0], p[1]);
	}

	@SuppressWarnings("unchecked")
	public static List<Edge>[] getEdgesBetween(Edge e1, Edge e2, List<Edge> list) {
		// implemented by salim
		List<Edge> list1 = new ArrayList<Edge>();
		List<Edge> list2 = new ArrayList<Edge>();
		ArrayList<Edge> remained = new ArrayList<Edge>();
		boolean found = false;
		int place = 0;
		for (Edge e : list) {
			if (e1.equals(e) || e2.equals(e)) {
				if (found) {
					remained = Utils.invert(remained);
					place = remained.size();
					found = false;
				} else
					found = true;
				continue;
			}
			if (found) {
				list1.add(e);
			} else {
				remained.add(place, e);

			}
		}

		for (Edge e : remained) {

			if (e1.equals(e) || e2.equals(e)) {
				if (found)
					found = false;
				continue;
			}
			if (found) {
				list1.add(e);
			} else {
				list2.add(e);
			}
		}
		@SuppressWarnings("rawtypes")
		List[] result = new List[] { list1, list2 };
		return result;
	}

	public static float[] getAffineFunction(float x1, float y1, float x2, float y2) {
		if (x1 == x2)
			return null;
		float m = (y1 - y2) / (x1 - x2);
		float b = y1 - m * x1;
		return new float[] { m, b };
	}

	public static float[] intersect(float[] points) {
		float[] l1 = getAffineFunction(points[0], points[1], points[2], points[3]);
		float[] l2 = getAffineFunction(points[4], points[5], points[6], points[7]);
		float[] crossing;
		if (l1 == null && l2 == null) {
			return null;
		} else if (l1 == null && l2 != null) {
			crossing = intersect(l2[0], l2[1], points[0]);
		} else if (l1 != null && l2 == null) {
			crossing = intersect(l1[0], l1[1], points[4]);
		} else {
			crossing = intersect(l1[0], l1[1], l2[0], l2[1]);
		}
		if (crossing == null) {
			return null;
		}
		if (!(inBounds(points[0], points[1], points[2], points[3], crossing[0], crossing[1]) && inBounds(points[4], points[5], points[6], points[7], crossing[0], crossing[1])))
			return null;
		return crossing;
	}

	public static float[] intersect(float[] points, float[] line) {
		float[] l1 = getAffineFunction(points[0], points[1], points[2], points[3]);
		float[] l2 = line;
		float[] crossing;
		if (l1 == null && l2 == null) {
			return null;
		} else if (l1 == null && l2 != null) {
			crossing = intersect(l2[0], l2[1], points[0]);
		} else if (l1 != null && l2 == null) {
			crossing = intersect(l1[0], l1[1], points[4]);
		} else {
			crossing = intersect(l1[0], l1[1], l2[0], l2[1]);
		}
		if (crossing == null) {
			return null;
		}
		if (!(inBounds(points[0], points[1], points[2], points[3], crossing[0], crossing[1])))
			return null;
		return crossing;
	}

	// ***********************************************************************************************Test:A2-09********************
	public static float[] intersect(float m1, float b1, float m2, float b2) {
		if (m1 == m2) {
			return null;
		}
		float x = (b2 - b1) / (m1 - m2);
		float y = m1 * x + b1;
		return new float[] { x, y };
	}

	// ***********************************************************************************************Test:A2-09********************
	public static float[] intersect(float m1, float b1, float x) {
		return new float[] { x, m1 * x + b1 };
	}

	// ***********************************************************************************************Test:A2-09********************
	public static boolean inBounds(float bx1, float by1, float bx2, float by2, float x, float y) {
		if (bx1 < bx2) {
			if (x < bx1 || x > bx2)
				return false;
		} else {
			if (x > bx1 || x < bx2)
				return false;
		}
		if (by1 < by2) {
			if (y < by1 || y > by2)
				return false;
		} else {
			if (y > by1 || y < by2)
				return false;
		}
		return true;
	}

	/**
	 * @author Salim
	 */
	public static double distance(Area se1, Area se2) {
		double min = Double.MAX_VALUE;

		double minD = Double.MAX_VALUE;
		for (Edge e2 : se2.getEdges()) {
			double d = distance(e2, new Point2D(se1.getX(), se1.getY()));
			if (d < minD)
				minD = d;
		}
		if (minD < min)
			min = minD;

		return min;
	}

	/**
	 * @author Salim
	 */
	public static double distance(Edge e, Point2D p) {
		double d = Double.MAX_VALUE;
		if (e == null || p == null)
			return d;
		Point2D closest = GeometryTools2D.getClosestPointOnSegment(e.getLine(), p);
		return GeometryTools2D.getDistance(closest, p);
	}

	/**
	 * @author Salim
	 */
	public static double distance(Edge e1, Edge e2) {
		double d1 = distance(e1, e2.getStart());
		double d2 = distance(e1, e2.getEnd());
		double d3 = distance(e1, new Point2D((e2.getEndX() + e2.getStartX()) / 2, (e2.getEndY() + e2.getStartY()) / 2));
		double d4 = distance(e2, e1.getStart());
		double d5 = distance(e2, e1.getEnd());
		double d6 = distance(e2, new Point2D((e1.getEndX() + e1.getStartX()) / 2, (e1.getEndY() + e1.getStartY()) / 2));
		d1 = Math.min(d1, d2);
		d2 = Math.min(d3, d4);
		d3 = Math.min(d5, d6);
		d1 = Math.min(d1, d2);
		d1 = Math.min(d1, d3);
		return d1;
	}

	/**
	 * @author Salim
	 */
	public static double distance(Area a, Point2D p) {
		return distance(a.getEdges(), p);
	}

	/**
	 * @author Salim
	 */
	public static double distance(List<Edge> edges, Point2D p) {
		double min = Double.MAX_VALUE;
		for (Edge e : edges) {
			double d = distance(e, p);
			if (d < min)
				min = d;
		}
		return min;
	}

	public static double distance(Human me, Area position) {

		double minD = Double.MAX_VALUE;
		for (Edge e2 : position.getEdges()) {
			double d = distance(e2, me.getPositionPair().second());
			if (d < minD)
				minD = d;
		}

		return minD;

	}

}
