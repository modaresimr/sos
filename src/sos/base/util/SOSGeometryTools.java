package sos.base.util;

import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.SOSPolygon;
import sos.base.sosFireZone.util.Utill;

public final class SOSGeometryTools {
	public static int AGENT_RADIUS = 500;

	/**
	 * This function returns an instance of Class Shape which can be used in all
	 * java Geometric Classes for example later in this class this function is
	 * used to check if to Areas do intersect or not.
	 *
	 * @return Shape
	 */

	public static Shape getShape(Area a) {
		return new SOSPolygon(a.getEdges());
	}

	/**
	 * This function is Used to see if two Areas have intersection or not.
	 *
	 * @return boolean
	 */
	public static boolean haveIntersection(Area a, Area b) {
		// implemented by salim (salim.malakouti@gmail.com)
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
		// implemented by salim (salim.malakouti@gmail.com)
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
		// implemented by salim (salim.malakouti@gmail.com)
		java.awt.geom.Area a1 = new java.awt.geom.Area(a);
		java.awt.geom.Area a2 = new java.awt.geom.Area(b);
		a1.intersect(a2);
		return (!a1.isEmpty());

	}

	/**
	 * finds the intersection of a line and the Line which contains the point
	 * and is perpendicular to the first line.
	 *
	 * @param l1
	 *            is instance of sos.utils.geometry.Line
	 * @param l2
	 *            is instance of sos.utils.geometry.Line
	 * @return Point if there is a intersection and returns null if they are
	 *         parallel
	 */
	public static Point2D getIntersection(int[] apexesOfLine, Point2D point) {
		// implemented by salim(salim.malakouti@gmail)
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
		// implemented by salim(salim.malakouti@gmail)
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

	public static double distance(Area se1, Area se2) {
		double minD = Double.MAX_VALUE;
		for (Edge e1 : se1.getEdges()) {
			for (Edge e2 : se2.getEdges()) {
				double d = distance(e2, e1);
				if (d < minD)
					minD = d;
			}
		}
		return minD;
	}

	public static double distance(Edge e, List<Line2D> edges) {
		double minD = Double.MAX_VALUE;
		for (Line2D e2 : edges) {
			double d = distance(e, new Edge(e2.getOrigin(), e2.getEndPoint()));// AliGeometryTools.getClosestDistance(e.getLine(), e2);
			minD = Math.min(d, minD);
		}
		return minD;
	}

	public static double distance(Edge e, Point2D p) {
		double d = Double.MAX_VALUE;
		if (e == null || p == null)
			return d;
		Point2D p2 = getIntersection(new int[] { e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY() }, p);
		if (p2 == null) {
			double d2 = GeometryTools2D.getDistance(p, e.getStart());
			double d3 = GeometryTools2D.getDistance(p, e.getEnd());
			d = Math.min(d2, d3);
		} else {
			d = GeometryTools2D.getDistance(p, p2);
		}
		return d;
	}

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

	public static double distance(Area a, Point2D p) {
		double min = Double.MAX_VALUE;
		for (Edge e : a.getEdges()) {
			double d = distance(e, p);
			if (d < min) {
				min = d;
			}
		}
		return min;
	}

	/**
	 * Checks if two area have intersection only and only in one point.
	 *
	 * @param a
	 * @param b
	 * @return boolean: true if there is only and only one point int which they hit each other
	 * @author Salim
	 */
	public static boolean haveIntersectionInOnePoint(Area a, Area b) {
		int num = 0;
		// System.out.println("----------------------");
		for (Edge e : a.getEdges()) {
			// System.out.println(e);
			Line2D l = e.getLine();
			for (int i = 0; i < b.getApexList().length; i += 2) {
				Point2D p = new Point2D(b.getApexList()[i], b.getApexList()[i + 1]);
				// System.out.println(p);
				if (GeometryTools2D.contains(l, p)) {
					//					System.out.println("!!");
					num++;
				}
			}
		}
		if (num == 2)
			return true;
		return false;
	}

	//Aramik
	public static int distance(int x1, int y1, int x2, int y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return (int) Math.hypot(dx, dy);
	}
	//Aramik
	public static double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return  Math.hypot(dx, dy);
	}

	public static int getDistance(Point2D source, Point2D destination) {
		return distance((int) source.getX(), (int) source.getY(), (int) destination.getX(), (int) destination.getY());
	}

	public static int distance(Point2D a, Point2D b) {
		double dx = a.getX() - b.getX();
		double dy = a.getY() - b.getY();
		return (int) Math.hypot(dx, dy);
	}

	/*
	 * @author Ali from GeometryTools2D form rescuecore2
	 */
	public static double computeAreaUnsigned(int[] apexes) {
		if(apexes.length<6)
			return 0;
		int indexLast = 0;
		int indexFirst = indexLast;
		double sum = 0;
		for (int indexNext = 2; indexNext < apexes.length; indexNext += 2) {
			double lastX = apexes[indexLast];
			double lastY = apexes[indexLast + 1];
			double nextX = apexes[indexNext];
			double nextY = apexes[indexNext + 1];
			sum += (lastX * nextY) - (nextX * lastY);
			indexLast = indexNext;
		}
		double lastX = apexes[indexLast];
		double lastY = apexes[indexLast + 1];
		double nextX = apexes[indexFirst];
		double nextY = apexes[indexFirst + 1];
		sum += (lastX * nextY) - (nextX * lastY);
		sum /= 2.0;

		return sum;
	}

	public static double computeArea(int[] apexes) {
		return Math.abs(computeAreaUnsigned(apexes));
	}

	/**
	 * @author Ali from GeometryTools2D form rescuecore2
	 *         Compute the centroid of a simple polygon.
	 * @param vertices
	 *            The vertices of the polygon.
	 * @return
	 * @return The centroid.
	 */
	public static Point2D computeCentroid(int[] apexes) {
		double area = computeAreaUnsigned(apexes);
		int indexLast = 0;
		int indexFirst = indexLast;
		double xSum = 0;
		double ySum = 0;
		for (int indexNext = 0; indexNext < apexes.length; indexNext += 2) {
			double lastX = apexes[indexLast];
			double lastY = apexes[indexLast + 1];
			double nextX = apexes[indexNext];
			double nextY = apexes[indexNext + 1];
			xSum += (lastX + nextX) * ((lastX * nextY) - (nextX * lastY));
			ySum += (lastY + nextY) * ((lastX * nextY) - (nextX * lastY));
			indexLast = indexNext;
		}
		double lastX = apexes[indexLast];
		double lastY = apexes[indexLast + 1];
		double nextX = apexes[indexFirst];
		double nextY = apexes[indexFirst + 1];
		xSum += (lastX + nextX) * ((lastX * nextY) - (nextX * lastY));
		ySum += (lastY + nextY) * ((lastX * nextY) - (nextX * lastY));
		// CHECKSTYLE:OFF:MagicNumber
		xSum /= 6.0 * area;
		ySum /= 6.0 * area;
		return new Point2D(xSum, ySum);
	}

	public static boolean haveIntersectionLowProcessLine(Building building, Point start, Point end) {
		//		Rectangle r=new Rectangle(start);
		//		r.add(end);
		//		if(!r.intersects(building.getShape().getBounds()))
		//			return false;
		double m = (start.getY() - end.getY()) / (start.getX() - end.getX());
		double c = m * (-start.getX()) + start.getY();
		return haveIntersectionLowProcess(building, m, c);

	}
	public static boolean haveIntersectionLowProcessOnSegment(Building building, Point start, Point end) {
		//		Rectangle r=new Rectangle(start);
		//		r.add(end);
		//		if(!r.intersects(building.getShape().getBounds()))
		//			return false;
		double m = (start.getY() - end.getY()) / (start.getX() - end.getX());
		double c = m * (-start.getX()) + start.getY();
		return haveIntersectionLowProcess(building, m, c);

	}

	public static boolean haveIntersectionLowProcess(Building building, double m, double c) {
		//		Rectangle r=new Rectangle(start);
		//		r.add(end);
		//		if(!r.intersects(building.getShape().getBounds()))
		//			return false;
		int firstx = building.getApexList()[0];
		int firsty = building.getApexList()[1];
		boolean lastDirection = m * firstx + c - firsty > 0;
		for (int i = 2; i < building.getApexList().length; i += 2) {
			int x = building.getApexList()[i];
			int y = building.getApexList()[i + 1];
			boolean direction = m * x + c - y > 0;///y0=m*x+c==>direction=y0-y
			if (lastDirection != direction)
				return true;
		}
		return false;

	}

	public static Point intersect(Point p1, Point p2, Point p3, Point p4) {
		double makhraj = (p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y - p1.y);
		if (makhraj < .1 && makhraj > -.1)
			return null;
		double ua = ((p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x)) / makhraj;
		int x = (int) (p1.x + ua * (p2.x - p1.x));
		int y = (int) (p1.y + ua * (p2.y - p1.y));
		Point point = new Point(x, y);
		if (!(Utill.inBounds(p1.x, p1.y, p2.x, p2.y, x, y) && Utill.inBounds(p3.x, p3.y, p4.x, p4.y, x, y)))
			return null;
		return point;
	}

	public static double distance2(double x, double y, int x2, int y2) {
		double dx = (x-x2);
		double dy = (y-y2);
		return dx*dx+dy*dy;
	}
//	static ShapeDebugFrame debug = new ShapeDebugFrame();
}
