package sos.base.reachablity.tools;

import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import sos.base.entities.Edge;
import sos.base.entities.SOSPolygon;
import sos.base.sosFireZone.util.Utill;

public class Utility {

	// Morteza2011*****************************************************************
	public static ArrayList<Point2D> get2PointsAroundAPointOnLine(Point2D start, Point2D end, Point2D test, int distance) {
		double x1 = start.getX();
		double y1 = start.getY();
		double x2 = end.getX();
		double y2 = end.getY();
		ArrayList<Point2D> finalPoints = new ArrayList<Point2D>(2);
		double m1;
		double dx = x2 - x1;
		double dy = y2 - y1;
		if (dx != 0)
			m1 = dy / dx;
		else
			m1 = 100000000;
		double dx2 = Math.sqrt(distance * distance / (m1 * m1 + 1));
		double dy2 = Math.sqrt(m1 * m1 * distance * distance / (m1 * m1 + 1));
		Point2D[] points = new Point2D[2];
		if (m1 < 0) {
			points[0] = new Point2D(test.getX() - dx2, test.getY() + dy2);
			points[1] = new Point2D(test.getX() + dx2, test.getY() - dy2);
		} else {
			points[0] = new Point2D(test.getX() + dx2, test.getY() + dy2);
			points[1] = new Point2D(test.getX() - dx2, test.getY() - dy2);
		}

		finalPoints.add(points[0]);
		finalPoints.add(points[1]);
		return finalPoints;
	}

	// Morteza2011*****************************************************************
	public static ArrayList<Point2D> get2PointsAroundAPointOutOfLine(Point2D start, Point2D end, Point2D test, int distance) {
		double x1 = start.getX();
		double y1 = start.getY();
		double x2 = end.getX();
		double y2 = end.getY();
		ArrayList<Point2D> finalPoints = new ArrayList<Point2D>(2);
		double m1;
		double dx = x2 - x1;
		double dy = y2 - y1;
		if (dx != 0)
			m1 = dy / dx;
		else
			m1 = 100000000;
		double m2;
		if (m1 != 0)
			m2 = -1 / m1;
		else
			m2 = 1000000;
		double dx2 = Math.sqrt(distance * distance / (m2 * m2 + 1));
		double dy2 = Math.sqrt(m2 * m2 * distance * distance / (m2 * m2 + 1));
		Point2D[] points = new Point2D[2];
		if (m1 > 0) {
			points[0] = new Point2D(test.getX() - dx2, test.getY() + dy2);
			points[1] = new Point2D(test.getX() + dx2, test.getY() - dy2);
		} else {
			points[0] = new Point2D(test.getX() + dx2, test.getY() + dy2);
			points[1] = new Point2D(test.getX() - dx2, test.getY() - dy2);
		}

		finalPoints.add(points[0]);
		finalPoints.add(points[1]);
		return finalPoints;
	}

	// Morteza2011*****************************************************************
	public static Point2D getIntersect(Edge edge1, Edge edge2) {
		Point2D point = GeometryTools2D.getIntersectionPoint(edge1.getLine(), edge2.getLine());
		if (point != null) {
			if (((int) point.getX() <= edge1.getStart().getX() && (int) point.getX() >= edge1.getEndX()) || ((int) point.getX() >= edge1.getStart().getX() && (int) point.getX() <= edge1.getEndX())) {
				if (((int) point.getY() <= edge1.getStartY() && (int) point.getY() >= edge1.getEndY()) || ((int) point.getY() >= edge1.getStartY() && (int) point.getY() <= edge1.getEndY())) {
					if (((int) point.getX() <= edge2.getStart().getX() && (int) point.getX() >= edge2.getEndX()) || ((int) point.getX() >= edge2.getStart().getX() && (int) point.getX() <= edge2.getEnd().getX())) {
						if (((int) point.getY() <= edge2.getStartY() && (int) point.getY() >= edge2.getEndY()) || ((int) point.getY() >= edge2.getStartY() && (int) point.getY() <= edge2.getEndY())) {
							return point;
						}
					}
				}
			}
		}
		return null;
	}

	// Morteza2011*****************************************************************
	public static double getSmallerAngleBetweenTwoLines(Line2D l1, Line2D l2) {
		double al = GeometryTools2D.getLeftAngleBetweenLines(l1, l2);
		double ar = GeometryTools2D.getRightAngleBetweenLines(l1, l2);
		if (al > ar)
			return ar;
		else
			return al;
	}

	/**
	 * @author Salim
	 * @param xs
	 * @param ys
	 * @return
	 */
	public static Shape newShape(int[] xs, int[] ys) {
		return new Polygon(xs, ys, xs.length);
	}

	// Morteza2011*****************************************************************
	public static Shape newShape(ArrayList<Edge> edges) {
		//		int count = edges.size();
		//		int[] xs = new int[count];
		//		int[] ys = new int[count];
		//		for (int i = 0; i < edges.size(); i++) {
		//			xs[i] = (int) edges.get(i).getStart().getX();
		//			ys[i] = (int) edges.get(i).getStart().getY();
		//		}
		//		Shape shape = new Polygon(xs, ys, count);
		//		return shape;
		return new SOSPolygon(edges);
	}

	// Morteza2011*****************************************************************
	public static boolean hasIntersect(SOSArea area1, SOSArea area2) {
		ArrayList<Edge> edgeList1 = area1.getEdges();
		ArrayList<Edge> edgeList2 = area2.getEdges();
		for (int i = 0; i < edgeList1.size(); i++) {
			for (int j = 0; j < edgeList2.size(); j++) {
				if (getIntersect(edgeList1.get(i), edgeList2.get(j)) != null) {
					return true;
				}
			}
		}
		return false;
	}

	// Morteza2011*****************************************************************
	public static double getAngle(Edge e1, Edge e2) {
		double RAngle = GeometryTools2D.getRightAngleBetweenLines(e1.getLine(), e2.getLine());
		double LAngle = GeometryTools2D.getLeftAngleBetweenLines(e1.getLine(), e2.getLine());
		double angle = Math.min(RAngle, LAngle);
		return angle;
	}

	// Morteza2011*****************************************************************
	public static Edge newEdge(ArrayList<Point2D> points, int start, int end, short index) {
		Edge e = new Edge(points.get(start), points.get(end));
		e.setReachablityIndex(index);
		return e;
	}

	// Morteza2012*****************************************************************
	public static int distanceToSOSArea(Point2D p, SOSArea rp) {
		int minDistance = Integer.MAX_VALUE;
		for (Edge e : rp.getEdges()) {
			Point2D po = GeometryTools2D.getClosestPointOnSegment(e.getLine(), p);
			minDistance = Utill.min(minDistance, Utill.distance(p.getIntX(), p.getIntY(), po.getIntX(), po.getIntY()));
		}
		return minDistance;
	}
}
