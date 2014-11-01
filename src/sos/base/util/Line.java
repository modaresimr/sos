package sos.base.util;

import java.awt.Point;

import rescuecore2.geometry.Point2D;

public class Line {
	// Author: salim (salim.malakouti@gmail.com)
	private double m;
	private int x;
	private int y;
	private double lenght;
	private double c;

	public Line(int x1, int y1, int x2, int y2) {
		try {
			m = (y2 - y1) / (x2 - x1);
		} catch (ArithmeticException e) {
			m = Double.MAX_VALUE;
		}
		x = (x1 + x2);
		y = (y1 + y2);
		lenght = Utils.distance(x1, x2, y1, y2);
		c = y1 - m * x;
	}

	public Line(double incline, int x, int y) {
		this.x = x;
		this.y = y;
		m = incline;
		lenght = 0;
		c = y - m * x;
	}

	public double getIncline() {

		return m;
	}

	public double getPerpendicularIncline() {
		double n = 0;
		if (m == 0)
			m = 0.001;
		try {
			n = -(1 / m);
		} catch (ArithmeticException e) {
			// TODO: handle exception
		}

		return n;
	}

	public Point[] get2PointsAroundCenter(int distance) {
		// implemented by salim (salim.malakouti@gmail.com)
		Point[] points = new Point[2];
		double rx1 = (distance / (Math.pow((m * m + 1) + 1, 1 / 2) + x));
		double ry1 = m * rx1 + c;
		double rx2 = (-distance / (Math.pow((m * m + 1) + 1, 1 / 2) + x));
		double ry2 = m * rx2 + c;
		points[0] = new Point((int) rx1, (int) ry1);
		points[1] = new Point((int) rx2, (int) ry2);
		return points;

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getLength() {
		return (int) lenght;
	}

	public double getC() {
		return c;
	}

	public Point2D getIntersection(Line l) {
		if (getIncline() == l.getIncline())
			return null;
		return null;
	}

}
