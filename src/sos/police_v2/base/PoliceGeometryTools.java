package sos.police_v2.base;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import rescuecore2.geometry.Point2D;

public class PoliceGeometryTools {
	public static Point2D getpoint2D(Point p) {
		return new Point2D(p.getX(), p.getY());
	}

	public static ArrayList<Point2D> getpoint2Ds(ArrayList<Point> p) {
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		for (Point point : p) {
			points.add(getpoint2D(point));
		}
		return points;
	}

	public static int[] getVertex2Ds(ArrayList<Point> p) {
		int[] points = new int[p.size() * 2];
		int i = 0;
		for (Point point : p) {

			points[i++] = point.x;
			points[i++] = point.y;
		}
		return points;
	}

	public static boolean haveIntersection(Shape shape, Path2D geopath) {
		Area area = new Area(shape);
		area.intersect(new Area(geopath));
		return area.isEmpty();
	}
}