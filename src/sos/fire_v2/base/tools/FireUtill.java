package sos.fire_v2.base.tools;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import sos.base.SOSConstant;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.FireBrigade;
import sos.base.entities.Human;

public class FireUtill {

	private static Rectangle rect = new Rectangle(0, 0, 0, 0);
	public static double PI = 3.1415;

	// Angeh2010***************************************************************************************************************
	public static int dx(Point a, Point b) {
		return Math.abs(a.x - b.x);
	}

	// Angeh2010***************************************************************************************************************
	public static int dy(Point a, Point b) {
		return Math.abs(a.y - b.y);
	}

	// Angeh2010***************************************************************************************************************
	public static double vectorAngle(double x, double y) {
		double r = java.lang.Math.atan2(y, x);
		if (r < 0) {
			r += 2 * PI;
		}
		return r;
	}

	// Angeh2010***************************************************************************************************************
	public static int vectorAngleD(Point start, Point end) {
		return (int) Math.toDegrees(vectorAngle(end.x - start.x, end.y - start.y));
	}

	// Angeh2010***************************************************************************************************************
	public static double angleDiff(int angle1, int angle2) {
		double diff = Math.abs(angle1 - angle2);
		if (diff <= 180) {
			return diff;
		} else {
			return 360 - diff;
		}
	}

	// Angeh2010***************************************************************************************************************
	public static int position(Point p, Point center) {
		if (p.x >= center.x) {
			if (p.y >= center.y) {
				return 1;
			} else {
				return 2;
			}
		} else {
			if (p.y >= center.y) {
				return 4;
			} else {
				return 3;
			}
		}
	}

	// Angeh2010***************************************************************************************************************
	public static float[] getAffineFunction(float x1, float y1, float x2, float y2) {
		if (x1 == x2)
			return null;
		float m = (y1 - y2) / (x1 - x2);
		float b = y1 - m * x1;
		return new float[] { m, b };
	}

	// Angeh2010***************************************************************************************************************
	public static float getAffineFunctionM(float x1, float y1, float x2, float y2) {
		if (x1 == x2) {
			return 999;
		}
		return (y1 - y2) / (x1 - x2);
		// float b=y1-m*x1;
		// return new float[]{m,b};
	}

	// Angeh2010***************************************************************************************************************
	public static float getAffineFunctionB(float x1, float y1, float x2, float y2) {
		if (x1 == x2) {
			return 999;
		}
		float m = (y1 - y2) / (x1 - x2);
		return y1 - m * x1;
		// return new float[]{m,b};
	}

	// Angeh2010***************************************************************************************************************
	public static Point intersect(Point a, Point b, Point c, Point d) {
		float[] rv = intersect(new float[] { a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y });
		if (rv == null)
			return null;
		return new Point((int) rv[0], (int) rv[1]);
	}

	// Angeh2010***************************************************************************************************************
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

	// Angeh2010***************************************************************************************************************
	public static final int distance(Area from, Area to) {
		float dx = from.getX() - to.getX();
		float dy = from.getY() - to.getY();
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	// Angeh2010***************************************************************************************************************
	public static final int distance(int x1, int y1, int x2, int y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	// Angeh2010***************************************************************************************************************
	public static final double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	// Angeh2010***************************************************************************************************************
	public static final float distancef(int x1, int y1, int x2, int y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	// Angeh2010***************************************************************************************************************
	public static final int distance(Point p1, Point p2) {
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	// Nima***************************************************************************************************************
	public static final int distance(Point2D p1, Point2D p2) {
		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	// Angeh2010***************************************************************************************************************
	public static final float distancef(Point p1, Point p2) {
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	// Nima***************************************************************************************************************
	public static double distance(Point2D p, Line2D line) {
		double dis1 = GeometryTools2D.getDistance(p, line.getOrigin());
		double dis2 = GeometryTools2D.getDistance(p, line.getEndPoint());
		Point2D A = null, B = null;
		if (dis1 < dis2) {
			A = line.getOrigin();
			B = line.getEndPoint();
		} else {
			A = line.getEndPoint();
			B = line.getOrigin();
		}
		double AB = GeometryTools2D.getDistance(A, B);
		double AC = GeometryTools2D.getDistance(A, p);

		double AB_dot_AC = (B.getX() - A.getX()) * (p.getX() - A.getX()) + (B.getY() - A.getY()) * (p.getY() - A.getY());

		double alpha = Math.acos(AB_dot_AC / (AB * AC));

		if (alpha > Math.PI / 2) {
			return AC;
		} else {
			double product = AB * AC * Math.sin(alpha);
			return product;
		}
	}

	// Angeh2010***************************************************************************************************************
	public static final int delta(int a, int b) {
		return (a - b);
	}

	// Angeh2010***************************************************************************************************************
	public static int max(int x, int y) {
		return x >= y ? x : y;
	}

	// Angeh2010***************************************************************************************************************
	public static long min(long x, long y) {
		return x <= y ? x : y;
	}

	// Angeh2010***************************************************************************************************************
	public static int min(int x, int y) {
		return x <= y ? x : y;
	}

	// Angeh2010***************************************************************************************************************
	public static float[] intersect(float m1, float b1, float m2, float b2) {
		if (m1 == m2) {
			return null;
		}
		float x = (b2 - b1) / (m1 - m2);
		float y = m1 * x + b1;
		return new float[] { x, y };
	}

	// Angeh2010***************************************************************************************************************
	public static float[] intersect(float m1, float b1, float x) {
		return new float[] { x, m1 * x + b1 };
	}

	// Angeh2010***************************************************************************************************************
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

	// Angeh2010***************************************************************************************************************
	public static Object randomChoice(Collection<Object> col, Random random) {
		if (col.isEmpty())
			throw new Error("the col must not be empty.");
		Iterator<Object> it = col.iterator();
		for (int i = random.nextInt(col.size()); i > 0; i--)
			it.next();
		return it.next();
	}

	// Angeh2010***************************************************************************************************************
	public static boolean boundingTest(Polygon p, int x, int y, int w, int h) {
		rect.setBounds(x, y, w, h);
		return p.intersects(rect);
	}

	// Angeh2010***************************************************************************************************************
	public static int percent(float x1, float y1, float width, float height, Polygon p) {
		int counter = 0;
		double dx = width / 10;
		double dy = height / 10;
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++) {
				if (p.contains(dx * i + x1, dy * j + y1))
					counter++;
			}
		return counter;
	}

	// Angeh2010***************************************************************************************************************
	public static ArrayList<Object> eshterak(ArrayList<Object>[] ar) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (ArrayList<Object> ari : ar) {
			if (ari == ar[0]) {
				result.addAll(ari);
			} else
				result.retainAll(ari);
		}
		return result;
	}

	// Angeh2010***************************************************************************************************************
	public static ArrayList<Object> eshterak(ArrayList<Building> arrayList, ArrayList<Building> arrayList2) {
		ArrayList<Object> result = new ArrayList<Object>();
		result.addAll(arrayList);
		result.retainAll(arrayList2);
		return result;
	}

	// Angeh2010***************************************************************************************************************
	public static ArrayList<Object> arrayToArrayList(Object[] ob) {
		ArrayList<Object> ar = new ArrayList<Object>();
		for (Object o : ob) {
			ar.add(o);
		}
		return ar;
	}

	// Angeh2010***************************************************************************************************************
	public static Point intersectionZJU(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
		long d = ((long) (y4 - y3)) * (x2 - x1) - ((long) (x4 - x3)) * (y2 - y1);
		if (d == 0)
			return new Point(-1, -1); // parallel lines
		double a = (((long) (x4 - x3)) * (y1 - y3) - ((long) (y4 - y3)) * (x1 - x3)) * 1.0 / d;
		int x = x1 + (int) (a * (x2 - x1));
		int y = y1 + (int) (a * (y2 - y1));
		// check they are within the bounds of the lines
		if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && x <= Math.max(x3, x4) && x >= Math.min(x3, x4) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2) && y <= Math.max(y3, y4) && y >= Math.min(y3, y4)) // they
			// cross!!
			return new Point(x, y);
		return new Point(-1, -1);
	}

	// Morteza2011***************************************************************************************************************
	public static double distance(FireBrigade fb, FireBuilding b) {
		return distance(fb.getX(), fb.getY(), b.building().getX(), b.building().getY());
	}

	// Morteza2011***************************************************************************************************************
	public static double distance(FireBrigade fb, Area a) {
		return distance(fb.getX(), fb.getY(), a.getX(), a.getY());
	}

	public static double distance(FireBrigade fb, Human hu) {
		return distance(hu.getX(), hu.getY(), fb.getX(), fb.getY());
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

	/**
	 * Partitions the array for quick sort
	 * 
	 * @author Hesam 002
	 */
	public static int partition(int arr[], int left, int right) {
		int i = left, j = right;
		int tmp;
		int pivot = arr[(left + right) / 2];

		while (i <= j) {
			while (arr[i] < pivot)
				i++;
			while (arr[j] > pivot)
				j--;
			if (i <= j) {
				tmp = arr[i];
				arr[i] = arr[j];
				arr[j] = tmp;
				i++;
				j--;
			}
		}
		return i;
	}

	/**
	 * Does a quick sort on the given array
	 * 
	 * @author Hesam 002
	 */

	public static void quickSort(short[] fireZoneInfo, int left, int right) {
		int index = partition(fireZoneInfo, left, right);
		if (left < index - 1)
			quickSort(fireZoneInfo, left, index - 1);
		if (index < right)
			quickSort(fireZoneInfo, index, right);
	}

	/**
	 * Partitions the array for quick sort
	 * 
	 * @author Hesam 002
	 */
	public static int partition(short arr[], int left, int right) {
		int i = left, j = right;
		short tmp;
		int pivot = arr[(left + right) / 2];

		while (i <= j) {
			while (arr[i] < pivot)
				i++;
			while (arr[j] > pivot)
				j--;
			if (i <= j) {
				tmp = arr[i];
				arr[i] = arr[j];
				arr[j] = tmp;
				i++;
				j--;
			}
		}
		return i;
	}

	/**
	 * Does a quick sort on the given array
	 * 
	 * @author Hesam 002
	 */

	/**
	 * @author Hesam 002
	 * @param from
	 * @param arrayList
	 * @return The Area With minimum distance
	 */
	public static Area distanceMin(Area from, ArrayList<? extends Area> arrayList) {
		int min = Integer.MAX_VALUE;
		int dis;
		Area result = null;
		for (Area to : arrayList) {
			dis = FireUtill.distance(from, to);
			if (min > dis) {
				min = dis;
				result = to;
			}
		}
		return result;
	}

	/**
	 * @author Hesam 002
	 * @param from
	 * @param to
	 * @return Dummy Move Time
	 */
	public static int getDummyMoveTime(Area from, Area to) {
		int dis = FireUtill.distance(from, to);
		double twoRadical = 1.41421356237;
		int cycle = (int) Math.round(twoRadical * dis) / SOSConstant.MOVE_DISTANCE_PER_CYCLE + 1;
		return cycle;
	}

	/**
	 * a simple method to find life time added by Aramik
	 * 
	 * @param hp
	 * @param dmg
	 * @param time
	 * @return
	 */
	public static int getEasyLifeTime(int hp, int dmg, int time) {
		if (dmg <= 0)
			return Integer.MAX_VALUE;
		if (hp <= 0)
			return 0;
		double alpha = 0;
		double newAlpha = 0.01;
		while (java.lang.Math.abs(alpha - newAlpha) > 1E-10) {

			alpha = newAlpha;
			double tmp = java.lang.Math.exp(-alpha * time);
			newAlpha = ((alpha * time + 1) * tmp - 1) / (time * tmp - (double) (10000 - hp) / dmg);
		}

		if (alpha > 0)
			return (int) (java.lang.Math.ceil((7.0 / 8) * java.lang.Math.log(alpha * hp / dmg + 1) / alpha));
		else
			return hp / dmg;
	}

}