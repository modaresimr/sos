package sos.tools.geometry;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.tools.Utils;

/**
 * 
 * @author Salim
 * 
 */
public class Line {
	
	private double m;
	private int x;
	private int y;
	private double length;
	private double c;
	
	public Line(int x1, int y1, int x2, int y2) {
		try {
			m = (y2 - y1) / (x2 - x1);
		} catch (ArithmeticException e) {
			m = Double.MAX_VALUE;
		}
		x = (x1 + x2);
		y = (y1 + y2);
		length = Utils.distance(x1, y1, x2, y2);
		c = y1 - m * x;
	}
	
	public Line(double incline, int x, int y) {
		this.x = x;
		this.y = y;
		m = incline;
		length = 0;
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
	
	public Pair<Double, Double> getRandomPoint() {
		double x = (Math.random() * 1000);
		return new Pair<Double, Double>(x, getYOF(x));
	}
	
	public double getYOF(double x) {
		double res = m * x + c;
		if(Double.isNaN(res))
			return Integer.MAX_VALUE;
		return res;
	}
	
	public Point2D getPoint(double x) {
		return new Point2D(x, getYOF(x));
	}
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getLength() {
		return (int) length;
	}
	
	public double getC() {
		return c;
	}
	
}
