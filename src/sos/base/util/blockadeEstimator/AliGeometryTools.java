package sos.base.util.blockadeEstimator;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.reachablity.tools.SOSArea;

public class AliGeometryTools {
	// static ShapeDebugFrame debug = new ShapeDebugFrame();

	public static List<Line2D> getLines(java.awt.geom.Area area) {
		return GeometryTools2D.pointsToLines(GeometryTools2D.vertexArrayToPoints(getApexes(area)), true);
	}
	
	public static double getClosestDistanceToArea(java.awt.geom.Area area, Point2D point) {
		List<Line2D> lines = getLines(area);
		double minDistance = Double.MAX_VALUE;
		for (Line2D line2d : lines) {
			minDistance = Math.min(minDistance, getClosestDistanceOnSegement(line2d, point));
		}
		return minDistance;
	}
	
	public static double getClosestDistanceOnSegement(Line2D line2d, Point2D point) {
		return GeometryTools2D.getDistance(GeometryTools2D.getClosestPointOnSegment(line2d, point), point);
	}

	public static double getClosestDistance(List<Line2D> lines, Point2D point) {
		double min = Double.MAX_VALUE;
		for (Line2D l : lines) {
			min = Math.min(min, getClosestDistanceOnSegement(l, point));
		}
		return min;
	}
	
	// public static double getClosestDistance(Line2D line2d, Point2D point) {
	// return GeometryTools2D.getDistance(GeometryTools2D.getClosestPoint(line2d, point), point);
	// }

	public static double getClosestDistance(java.awt.geom.Area area1, java.awt.geom.Area area2) {

		List<Line2D> lines1 = getLines(area1);
		List<Line2D> lines2 = getLines(area2);
		double minDistance = getClosestDistance(lines1, lines2);
		/*
		 * debug.show("getClosestDistance", new AWTShapeInfo(area1, "area1", Color.red, false),
		 * new AWTShapeInfo(area2, "area2", Color.blue, false),
		 * new ShapeDebugFrame.Line2DShapeInfo(lines1, "line1", Color.black, false, true),
		 * new ShapeDebugFrame.Line2DShapeInfo(lines2, "line2", Color.green, false, true),
		 * new ShapeDebugFrame.DetailInfo("distance between area1-area2: " + minDistance)
		 * );
		 */

		return minDistance;
	}
	
	public static double getClosestDistance(List<Line2D> lines1, List<Line2D> lines2) {
		double minDistance = Integer.MAX_VALUE;
		for (Line2D l1 : lines1) {
			for (Line2D l2 : lines2) {
				minDistance = Math.min(minDistance, getClosestDistance(l1, l2));
			}
		}
		
		/*
		 * debug.show("getClosestDistance",
		 * new ShapeDebugFrame.Line2DShapeInfo(lines1, "line1", Color.white, false, true),
		 * new ShapeDebugFrame.Line2DShapeInfo(lines2, "line2", Color.green, false, true),
		 * new ShapeDebugFrame.DetailInfo("distance between lines1-lines2: " + minDistance)
		 * );
		 */

		return minDistance;
		
	}
	
	public static double getClosestDistance(Area area1, List<Point2D> points) {
		List<Line2D> lines1 = getLines(area1);
		double minDistance = Integer.MAX_VALUE;
		for (Line2D l1 : lines1) {
			for (Point2D p2 : points) {
				minDistance = Math.min(minDistance, getClosestDistanceOnSegement(l1, p2));
			}
		}
		/*
		 * debug.show("getClosestDistance", new AWTShapeInfo(area1, "area1", Color.red, false),
		 * new ShapeDebugFrame.Line2DShapeInfo(lines1, "line1", Color.black, false, true),
		 * // new ShapeDebugFrame.Point2DShapeInfo(lines2, "line2", Color.green, false, true),
		 * new ShapeDebugFrame.DetailInfo("distance between area1-area2: " + minDistance)
		 * );
		 */
		return minDistance;
		
	}

	
	public static double getClosestDistance(Line2D line1, Line2D line2) {
		Point2D p11 = line1.getOrigin();
		Point2D p12 = line1.getEndPoint();
		
		Point2D p21 = line2.getOrigin();
		Point2D p22 = line2.getEndPoint();
		double minDistance = Double.MAX_VALUE;
		if (GeometryTools2D.getSegmentIntersectionPoint(line1, line2) == null) {
			minDistance = Math.min(minDistance, getClosestDistanceOnSegement(line2, p11));
			minDistance = Math.min(minDistance, getClosestDistanceOnSegement(line2, p12));
			minDistance = Math.min(minDistance, getClosestDistanceOnSegement(line1, p21));
			minDistance = Math.min(minDistance, getClosestDistanceOnSegement(line1, p22));
		} else
			minDistance = 0;
		
		/*
		 * debug.show("getClosestDistance",
		 * new ShapeDebugFrame.Line2DShapeInfo(line1, "line1", Color.black, false, true),
		 * new ShapeDebugFrame.Line2DShapeInfo(line2, "line2", Color.green, false, true),
		 * new ShapeDebugFrame.Point2DShapeInfo(p11, "p11" + p11, Color.red, true),
		 * new ShapeDebugFrame.Point2DShapeInfo(p12, "p12" + p12, Color.blue, true),
		 * new ShapeDebugFrame.Point2DShapeInfo(p21, "p21" + p21, Color.yellow, true),
		 * new ShapeDebugFrame.Point2DShapeInfo(p22, "p22" + p22, Color.white, true),
		 * new ShapeDebugFrame.Point2DShapeInfo(GeometryTools2D.getSegmentIntersectionPoint(line1, line2), "IntersectionPoint(line1, line2)" + GeometryTools2D.getSegmentIntersectionPoint(line1, line2), Color.MAGENTA, true),
		 * new ShapeDebugFrame.DetailInfo("distance between p11-line2: " + getClosestDistance(line2, p11)),
		 * new ShapeDebugFrame.DetailInfo("distance between p12-line2: " + getClosestDistance(line2, p12)),
		 * new ShapeDebugFrame.DetailInfo("distance between p21-line1: " + getClosestDistance(line1, p21)),
		 * new ShapeDebugFrame.DetailInfo("distance between p22-line1: " + getClosestDistance(line1, p22)),
		 * new ShapeDebugFrame.DetailInfo("distance between line1-line2: " + minDistance)
		 * );
		 */

		return minDistance;
	}
	
	public static int[] getApexes_old(java.awt.geom.Area area) {
		// Logger.debug("getApexes");
		List<Integer> apexes = new ArrayList<Integer>();
		// CHECKSTYLE:OFF:MagicNumber
		PathIterator it = area.getPathIterator(null, 100);
		double[] d = new double[6];
		int moveX = 0;
		int moveY = 0;
		int lastX = 0;
		int lastY = 0;
		boolean finished = false;
		while (!finished && !it.isDone()) {
			int x = 0;
			int y = 0;
			switch (it.currentSegment(d)) {
			case PathIterator.SEG_MOVETO:
				x = (int) d[0];
				y = (int) d[1];
				moveX = x;
				moveY = y;
				// Logger.debug("Move to " + x + ", " + y);
				break;
			case PathIterator.SEG_LINETO:
				x = (int) d[0];
				y = (int) d[1];
				// Logger.debug("Line to " + x + ", " + y);
				if (x == moveX && y == moveY) {
					finished = true;
				}
				break;
			case PathIterator.SEG_QUADTO:
				x = (int) d[2];
				y = (int) d[3];
				// Logger.debug("Quad to " + x + ", " + y);
				if (x == moveX && y == moveY) {
					finished = true;
				}
				break;
			case PathIterator.SEG_CUBICTO:
				x = (int) d[4];
				y = (int) d[5];
				// Logger.debug("Cubic to " + x + ", " + y);
				if (x == moveX && y == moveY) {
					finished = true;
				}
				break;
			case PathIterator.SEG_CLOSE:
				// Logger.debug("Close");
				finished = true;
				break;
			default:
				throw new RuntimeException("Unexpected result from PathIterator.currentSegment: " + it.currentSegment(d));
			}
			// Logger.debug(x + ", " + y);
			if (!finished && (x != lastX || y != lastY)) {
				apexes.add(x);
				apexes.add(y);
			}
			lastX = x;
			lastY = y;
			it.next();
		}
		// CHECKSTYLE:ON:MagicNumber
		int[] result = new int[apexes.size()];
		int i = 0;
		for (Integer next : apexes) {
			result[i++] = next;
		}
		return result;
	}
	
	public static int[] getApexes(Shape area) {

		List<Integer> apexes = new ArrayList<Integer>();
		// CHECKSTYLE:OFF:MagicNumber
		PathIterator it = area.getPathIterator(null, 100);
		double[] d = new double[6];
		int moveX = 0;
		int moveY = 0;
		int lastX = 0;
		int lastY = 0;
		boolean finished = false;
		while (/* !finished && */!it.isDone()) {
			int x = -1;
			int y = -1;
			switch (it.currentSegment(d)) {
			case PathIterator.SEG_MOVETO:
				x = (int) d[0];
				y = (int) d[1];
				moveX = x;
				moveY = y;
				// Logger.debug("Move to " + x + ", " + y);
				break;
			case PathIterator.SEG_LINETO:
				x = (int) d[0];
				y = (int) d[1];
				// Logger.debug("Line to " + x + ", " + y);
				if (x == moveX && y == moveY) {
					// finished = true;
				}
				break;
			case PathIterator.SEG_QUADTO:
				x = (int) d[2];
				y = (int) d[3];
				// Logger.debug("Quad to " + x + ", " + y);
				if (x == moveX && y == moveY) {
					// finished = true;
				}
				break;
			case PathIterator.SEG_CUBICTO:
				x = (int) d[4];
				y = (int) d[5];
				// Logger.debug("Cubic to " + x + ", " + y);
				if (x == moveX && y == moveY) {
					// finished = true;
				}
				break;
			case PathIterator.SEG_CLOSE:
				// Logger.debug("Close");
				finished = true;
				break;
			default:
				throw new RuntimeException("Unexpected result from PathIterator.currentSegment: " + it.currentSegment(d));
			}
			// Logger.debug(x + ", " + y);
			if (!finished && (x != lastX || y != lastY) && (x != -1 && y != -1)) {
				apexes.add(x);
				apexes.add(y);
			}
			lastX = x;
			lastY = y;
			it.next();
		}
		// CHECKSTYLE:ON:MagicNumber
		int[] result = new int[apexes.size()];
		int i = 0;
		for (Integer next : apexes) {
			result[i++] = next;
		}
		return result;
	}

	
	public static List<java.awt.geom.Area> fix(java.awt.geom.Area area) {
		List<java.awt.geom.Area> result = new ArrayList<java.awt.geom.Area>();
		if (area.isSingular()) {
			result.add(area);
			return result;
		}
		PathIterator it = area.getPathIterator(null);
		Path2D current = null;
		// CHECKSTYLE:OFF:MagicNumber
		double[] d = new double[6];
		while (!it.isDone()) {
			switch (it.currentSegment(d)) {
			case PathIterator.SEG_MOVETO:
				if (current != null) {
					result.add(new java.awt.geom.Area(current));
				}
				current = new Path2D.Double();
				current.moveTo(d[0], d[1]);
				break;
			case PathIterator.SEG_LINETO:
				current.lineTo(d[0], d[1]);
				break;
			case PathIterator.SEG_QUADTO:
				current.quadTo(d[0], d[1], d[2], d[3]);
				break;
			case PathIterator.SEG_CUBICTO:
				current.curveTo(d[0], d[1], d[2], d[3], d[4], d[5]);
				break;
			case PathIterator.SEG_CLOSE:
				current.closePath();
				break;
			default:
				throw new RuntimeException("Unexpected result from PathIterator.currentSegment: " + it.currentSegment(d));
			}
			it.next();
		}
		// CHECKSTYLE:ON:MagicNumber
		if (current != null) {
			result.add(new java.awt.geom.Area(current));
		}
		return result;
	}
	
	public static java.awt.geom.Area areaToGeomArea(sos.base.entities.Area area) {
		Path2D result = new Path2D.Double();
		Iterator<Edge> it = area.getEdges().iterator();
		Edge e = it.next();
		result.moveTo(e.getStartX(), e.getStartY());
		result.lineTo(e.getEndX(), e.getEndY());
		while (it.hasNext()) {
			e = it.next();
			result.lineTo(e.getEndX(), e.getEndY());
		}
		return new java.awt.geom.Area(result);
	}
	
	public static java.awt.geom.Area sosareaToGeomArea(SOSArea sosArea) {
		Path2D result = new Path2D.Double();
		Iterator<Edge> it = sosArea.getEdges().iterator();
		Edge e = it.next();
		result.moveTo(e.getStartX(), e.getStartY());
		result.lineTo(e.getEndX(), e.getEndY());
		while (it.hasNext()) {
			e = it.next();
			result.lineTo(e.getEndX(), e.getEndY());
		}
		return new java.awt.geom.Area(result);
	}
	public static java.awt.geom.Area sosBlockadeToArea(SOSBlockade sosBlock) {
		Path2D result = new Path2D.Double();
		int[] apexes = sosBlock.getApexes();
		result.moveTo(apexes[0], apexes[1]);
		for (int i = 2; i < apexes.length; i += 2) {
			result.lineTo(apexes[i], apexes[i + 1]);
		}
		result.closePath();
		
		return new java.awt.geom.Area(result);
	}

	public static java.awt.geom.Area blockadeToArea(Blockade b) {
		Path2D result = new Path2D.Double();
		int[] apexes = b.getApexes();
		result.moveTo(apexes[0], apexes[1]);
		for (int i = 2; i < apexes.length; i += 2) {
			result.lineTo(apexes[i], apexes[i + 1]);
		}
		result.closePath();
		return new java.awt.geom.Area(result);
	}

	public static boolean havecorrectDirection(sos.base.entities.Area b) {
		Edge edge = b.getEdges().get(0);
		Line2D wallLine = new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());
		Vector2D wallDirection = wallLine.getDirection();
		Vector2D offset = wallDirection.getNormal().normalised().scale(-10);
		Point2D first = wallLine.getOrigin().plus(offset);
		Point2D second = wallLine.getEndPoint().plus(offset);
		Point2D centerPoint = new Point2D((first.getX() + second.getX()) / 2, (first.getY() + second.getY()) / 2);
		boolean direction = !b.getShape().contains(centerPoint.toGeomPoint());
		/*
		 * debug.show("Collapsed building",
		 * new ShapeDebugFrame.AWTShapeInfo(b.getShape(), "Original building area", Color.RED, true),
		 * new ShapeDebugFrame.Line2DShapeInfo(wallLine, "Wall edge", Color.WHITE, true, true),
		 * new ShapeDebugFrame.Point2DShapeInfo(first, "first: " + first, Color.BLUE, true),
		 * new ShapeDebugFrame.Point2DShapeInfo(second, "second: " + second, Color.RED, true),
		 * new ShapeDebugFrame.Point2DShapeInfo(centerPoint, "centerPoint: " + centerPoint, Color.LIGHT_GRAY, true),
		 * new ShapeDebugFrame.DetailInfo("direction: " + direction)
		 * );
		 */
		return direction;
		
	}
	
	public static double getCosOfAngleBetweenVectors(Vector2D first, Vector2D second) {
		Vector2D v1 = first.normalised();
		Vector2D v2 = second.normalised();
		double cos = v1.dot(v2);
		if (cos > 1) {
			cos = 1;
		}
		if (cos < -1) {
			cos = -1;
		}
		return cos;
	}
	
	public static boolean haveAccordance(Line2D l1, Line2D l2) {
		Double Threshold = (0.00001);
		return haveAccordance(l1, l2, Threshold);
		
	}
	
	/**
	 * @author Ali
	 * @param base
	 * @param containLine
	 * @param threshold
	 * @param road
	 * @param blockade
	 * @return
	 */
	public static boolean haveAccordance(Line2D base, Line2D containLine, double threshold) {
		double cos = AliGeometryTools.getCosOfAngleBetweenVectors(base.getDirection(), containLine.getDirection());
		
		if (Math.acos(Math.abs(cos)) > 0.017)// are they parallel?(threshold=the angle less than 1 degree)
			return false;
		if (areEqual(getClosestDistanceOnSegement(base, containLine.getEndPoint()), 0, threshold)
					|| areEqual(getClosestDistanceOnSegement(base, containLine.getOrigin()), 0, threshold)
					|| areEqual(getClosestDistanceOnSegement(containLine, base.getEndPoint()), 0, threshold)
					|| areEqual(getClosestDistanceOnSegement(containLine, base.getEndPoint()), 0, threshold))
			return true;
		return false;

		/*
		 * debug.setBackground(new ShapeDebugFrame.AWTShapeInfo(road.getShape(), road.toString(), Color.BLUE, false),
		 * new ShapeDebugFrame.AWTShapeInfo(blockade.getShape(), blockade.toString(), Color.GREEN, false));
		 * debug.show("haveAccordance",
		 * new ShapeDebugFrame.Line2DShapeInfo(base, "base", Color.white, true, true),
		 * new ShapeDebugFrame.Line2DShapeInfo(containLine, "containL	ine", Color.black, false, true),
		 * new ShapeDebugFrame.DetailInfo("haveAccordance?     " + b),
		 * new ShapeDebugFrame.DetailInfo("cos of angle between too line: " + cos),
		 * new ShapeDebugFrame.DetailInfo("d[base, containLine.getEndPoint]: " + AliGeometryTools.getClosestDistanceOnSegement(base, containLine.getEndPoint())),
		 * new ShapeDebugFrame.DetailInfo("d[base, containLine.getOrigin]: " + AliGeometryTools.getClosestDistanceOnSegement(base, containLine.getOrigin())),
		 * new ShapeDebugFrame.DetailInfo("d[containLine, base.getEndPoint]: " + AliGeometryTools.getClosestDistanceOnSegement(containLine, base.getEndPoint())),
		 * new ShapeDebugFrame.DetailInfo("d[containLine, base.getOrigin]: " + AliGeometryTools.getClosestDistanceOnSegement(containLine, base.getOrigin()))
		 * );
		 */

	}

	public static boolean areEqual(double d1, double d2, double threshold) {
		double d = d1 - d2;
		return d > -threshold && d < threshold;
	}

	public static boolean containsAll(Area maxBlockade, List<Point2D> vertexArrayToPoints) {
		for (Point2D point2d : vertexArrayToPoints) {
			if (!maxBlockade.contains(point2d.toGeomPoint()))
				return false;
		}
		return true;
	}

	public static boolean areEqual(Point2D p1, Point2D p2, int threshold) {
		return areEqual(p1.getX(), p2.getX(), threshold) && areEqual(p1.getY(), p2.getY(), threshold);
	}
	
	public static double getEqualityThreshold(Point2D p1, Point2D p2) {
		return Math.max(computeThreshold(p1.getX(), p2.getX()), computeThreshold(p1.getY(), p2.getY()));
	}
	
	public static double computeThreshold(Double... d) {
		List<Double> dlist = Arrays.asList(d);
		double min = Double.MAX_VALUE;
		double max = 0;
		for (Double d1 : dlist) {
			min = Math.min(min, d1);
			max = Math.max(max, d1);
		}
		return max - min;
	}

	/**
	 * it mean then line intersect with the base on segment!
	 * 
	 * @param base
	 * @param line
	 * @return
	 */
	public static Point2D getIntersectionPoint_Ali_Define(Line2D base, Line2D line) {
		double t1 = base.getIntersection(line);
		double t2 = line.getIntersection(base);
		if (Double.isNaN(t1) || Double.isNaN(t2) || t2 < 0 || t2 > 1) {
			return null;
		}
		return base.getPoint(t1);
	}

	public static boolean areEqual(int[] apexes1, int[] apexes2, int threshold) {
		if (apexes1 == null || apexes2 == null)
			return false;
			
		if (apexes1.length != apexes2.length)
			return false;

		for (int i = 0; i < apexes1.length; i++) {
			if (!areEqual(apexes1[i], apexes2[i], threshold))
				return false;
		}

		return true;
	}
	
	public static int getClosestDistance(Collection<Point2D> blockEdgesPoints, Collection<Point2D> vertexArrayToPoints) {
		int minData=Integer.MAX_VALUE;
		for (Point2D point2dA : vertexArrayToPoints) {
			for (Point2D point2dB : blockEdgesPoints) {
				minData=(int) Math.min(GeometryTools2D.getDistance(point2dA, point2dB), minData);
			}
		}
		return minData;
	}

	public static Polygon getShape(int[] allApexes) {
		int count = allApexes.length / 2;
		int[] xs = new int[count];
		int[] ys = new int[count];
		for (int i = 0; i < count; ++i) {
			xs[i] = allApexes[i * 2];
			ys[i] = allApexes[i * 2 + 1];
		}
		return new Polygon(xs, ys, count);
	}
	public static double getClosestDistance(Collection<Point2D> maxBlockadeEdges, Point2D centeroid) {
		return getClosestDistance(maxBlockadeEdges, Collections.singleton(centeroid));
	}
	
	public static List<Edge> getEdges(int[] allApexes) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (int i = 0; i < allApexes.length; i += 2) {
			edges.add(new Edge(new Point2D(allApexes[i], allApexes[i + 1]),
					new Point2D(allApexes[(i + 2) % allApexes.length], allApexes[((i + 3)) % allApexes.length])));
		}
		return edges;
	}
	
	public static List<Line2D> getLines(int[] allApexes) {
		ArrayList<Line2D> edges = new ArrayList<Line2D>();
		for (int i = 0; i < allApexes.length; i += 2) {
			edges.add(new Line2D(new Point2D(allApexes[i], allApexes[i + 1]), 
					new Point2D(allApexes[(i + 2) % allApexes.length], allApexes[((i + 3)) % allApexes.length])));
		}
		return edges;
	}
	
	public static List<Edge> getEdges(List<Point2D> points) {
		List<Edge> result = new ArrayList<Edge>();
		for (int i = 0; i < points.size(); i++) {
			result.add(new Edge(points.get(i), points.get((i + 1) % points.size())));
		}
		return result;
	}
	
	public static int[] getApexes(List<Edge> e) {
			int[] apexList = new int[e.size() * 2];
			int i = 0;
			for (Edge next : e) {
				apexList[i++] = next.getStartX();
				apexList[i++] = next.getStartY();
			}
			return apexList;
	}
	
	public static double getAngleInRadian(Vector2D first,Vector2D second){
		Vector2D v1 = first.normalised();
		Vector2D v2 = second.normalised();
		double cos = v1.dot(v2);
		if (cos > 1) {
			cos = 1;
		}
		double angle = Math.acos(cos);
		
		return angle;
	}
	
}
