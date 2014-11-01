package sos.base.sosFireZone.util;

import java.awt.Point;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Building;
import sos.base.entities.Edge;

public class Utill {

	// Angeh2010***************************************************************************************************************
	public static Point getRndPoint(Point a, Point b) {
		float[] mb = getAffineFunction(a.x, a.y, b.x, b.y);
		float dx = (Math.max((float) a.x, (float) b.x) - Math.min((float) a.x, (float) b.x));
		dx *= Rnd.get01();
		dx += Math.min((float) a.x, (float) b.x);
		if (mb == null) {
			// vertical line
			int p = Math.max(a.y, b.y) - Math.min(a.y, b.y);
			p = (int) (p * Math.random());
			p = p + Math.min(a.y, b.y);
			return new Point(a.x, p);
		}
		float y = mb[0] * dx + mb[1];
		Point rtv = new Point((int) dx, (int) y);
		return rtv;
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
	public static Point getRndPoint(Point a, double length, Building owner) {
		double angel = Rnd.get01() * 2d * Math.PI;

		double sin = Math.sin(angel);
		double cos = Math.cos(angel);
		double checkx = sin * 100 + a.x;
		double checky = cos * 100 + a.y;
		double x, y;
		if (owner.getShape().contains(checkx, checky)) {
			//			return null;
			x = -sin * length;
			y = -cos * length;
		} else {
			x = sin * length;
			y = cos * length;
		}

		return new Point((int) x + a.x, (int) y + a.y);
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
		if (!(inBounds(points[0], points[1], points[2], points[3], crossing[0], crossing[1])
		&& inBounds(points[4], points[5], points[6], points[7], crossing[0], crossing[1])))
			return null;
		return crossing;
	}

	// Angeh2010***************************************************************************************************************
	public static final int distance(int x1, int y1, int x2, int y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	// Angeh2010***************************************************************************************************************
	public static int max(int x, int y) {
		return x >= y ? x : y;
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

	public static float ACCURACY = 10f;

	// Angeh2010***************************************************************************************************************
	public static boolean inBounds(float bx1, float by1, float bx2, float by2, float x, float y) {

		if (bx1 < bx2) {
			if (x < bx1 - ACCURACY || x > bx2 + ACCURACY) {
				//				debug.show(""
				//						,new ShapeDebugFrame.Line2DShapeInfo(new Line2D(bx1,by1, bx2-bx1,by2-by1), "l1", Color.blue, false, true),
				//
				//						new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "intersection", Color.BLACK, true),
				//								new ShapeDebugFrame.DetailInfo("inbound1?:"+false)
				//
				//						);
				return false;
			}
		} else {
			if (x > bx1 + ACCURACY || x < bx2 - ACCURACY) {
				//				debug.show(""
				//						,new ShapeDebugFrame.Line2DShapeInfo(new Line2D(bx1,by1, bx2-bx1,by2-by1), "l1", Color.blue, false, true),
				//
				//						new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "intersection", Color.BLACK, true),
				//								new ShapeDebugFrame.DetailInfo("inbound2?:"+false)
				//
				//						);
				return false;
			}
		}
		if (by1 < by2) {
			if (y < by1 - ACCURACY || y > by2 + ACCURACY) {
				//				debug.show(""
				//						,new ShapeDebugFrame.Line2DShapeInfo(new Line2D(bx1,by1, bx2-bx1,by2-by1), "l1", Color.blue, false, true),
				//
				//						new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "intersection", Color.BLACK, true),
				//								new ShapeDebugFrame.DetailInfo("inbound3?:"+false)
				//
				//						);
				return false;
			}
		} else {
			if (y > by1 + ACCURACY || y < by2 - ACCURACY) {
				//				debug.show(""
				//						,new ShapeDebugFrame.Line2DShapeInfo(new Line2D(bx1,by1, bx2-bx1,by2-by1), "l1", Color.red, false, true),
				//
				//						new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "intersection", Color.BLACK, true),
				//								new ShapeDebugFrame.DetailInfo("inbound4?:"+false)
				//
				//						);
				return false;
			}
		}
		//		debug.show(""
		//				,new ShapeDebugFrame.Line2DShapeInfo(new Line2D(bx1,by1, bx2-bx1,by2-by1), "l1", Color.green, false, true),
		//
		//				new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "intersection", Color.BLACK, true),
		//						new ShapeDebugFrame.DetailInfo("inbound5?:"+true)
		//
		//				);
		return true;
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

	/**
	 * @author Ali
	 *         m=shibe khate aval
	 *         c=adade sabete khat e aval
	 * @param line1p1
	 * @param line1p2
	 * @param line2p1
	 * @param line2p2
	 * @param line1m
	 * @param line1c
	 * @return
	 */

	public static Point intersectLowProcess(Point line1p1, Point line1p2, Point line2p1, Point line2p2, double line1m, double line1c) {
		boolean direction1 = line1m * line2p1.x + line1c - line2p1.y > 0;
		boolean direction2 = line1m * line2p2.x + line1c - line2p2.y > 0;
		Point p = null;
		if (direction1 != direction2) {
			p = intersect(line1p1, line1p2, line2p1, line2p2);
			//						p=intersectionZJU(line1p1, line1p2, line2p1, line2p2);
			//			debug.show("", new ShapeDebugFrame.Line2DShapeInfo(new Line2D(line1p1, line1p2), "l1", Color.blue, false, true),
			//					new ShapeDebugFrame.Line2DShapeInfo(new Line2D(line2p1, line2p2), "l2", Color.red, false, true),
			//					p==null?new ShapeDebugFrame.DetailInfo("intersection:null"):new ShapeDebugFrame.Point2DShapeInfo(new Point2D(p.x, p.y), "intersection", Color.BLACK, true),
			//							new ShapeDebugFrame.DetailInfo("direction1:"+direction1),
			//							new ShapeDebugFrame.DetailInfo("direction2:"+direction2)
			//					);
		}

		return p;
	}

	/**
	 *have bug
	 */
	@Deprecated
	public static Point2D intersectLowProcess(int line1p1x, int line1p1y, int line1p2x, int line1p2y, int line2p1x, int line2p1y, float line2p2x, float line2p2y) {
		float[] rv = intersect(new float[] { line1p1x, line1p1y, line1p2x, line1p2y, line2p1x, line2p1y, line2p2x, line2p2y });
		if (rv == null)
			return null;
		return new Point2D(rv[0], rv[1]);
	}

	//	static ShapeDebugFrame debug=new ShapeDebugFrame();
	public static Point intersectLowProcess(Point line1p1, Point line1p2, Edge e, double line1m, double line1c) {
		boolean direction1 = line1m * e.getStartX() + line1c - e.getStartY() > 0;
		boolean direction2 = line1m * e.getEndX() + line1c - e.getEndY() > 0;
		if (direction1 != direction2) {
			return intersect(line1p1, line1p2, e);
			//			return intersectionZJU(line1p1, line1p2, line2p1, line2p2);
		}
		return null;
	}

	public static Point intersect(Point a, Point b, Edge e) {
		float[] rv = intersect(new float[] { a.x, a.y, b.x, b.y, e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY() });
		if (rv == null)
			return null;
		return new Point((int) rv[0], (int) rv[1]);
	}

	/**
	 * @author Ali
	 * @param x1
	 * @param p1
	 *            .y
	 * @param p2
	 *            .x
	 * @param p2
	 *            .y
	 * @param p3
	 *            .x
	 * @param p3
	 *            .y
	 * @param p4
	 *            .x
	 * @param p4
	 *            .y
	 * @return
	 */
	public static Point intersectionZJU(Point p1, Point p2, Point p3, Point p4) {
		int d = ((p4.y - p3.y)) * (p2.x - p1.x) - ((p4.x - p3.x)) * (p2.y - p1.y);
		if (d == 0)
			return null; // parallel lines
		double a = (((p4.x - p3.x)) * (p1.y - p3.y) - ((p4.y - p3.y)) * (p1.x - p3.x)) / (double) d;
		int x = p1.x + (int) (a * (p2.x - p1.x));
		int y = p1.y + (int) (a * (p2.y - p1.y));
		// check they are within the bounds of the lines
		if (x >= Math.min(p1.x, p2.x) && x <= Math.max(p1.x, p2.x) && x <= Math.max(p3.x, p4.x) && x >= Math.min(p3.x, p4.x) && y >= Math.min(p1.y, p2.y) && y <= Math.max(p1.y, p2.y) && y <= Math.max(p3.y, p4.y) && y >= Math.min(p3.y, p4.y)) // they
			// cross!!
			return new Point(x, y);
		return null;
	}

	public static Point intersectLowProcess(Point line1p1, Point line1p2, Point line2p1, Point line2p2, double line1m, double line1c, Building jbuilding) {
		boolean direction1 = line1m * line2p1.x + line1c - line2p1.y > 0;
		boolean direction2 = line1m * line2p2.x + line1c - line2p2.y > 0;
		Point p = null;
		//		debug.setBackground(new ShapeDebugFrame.AWTShapeInfo(jbuilding.getShape(), jbuilding+"", Color.gray, false));
		//		if(jbuilding.getID().getValue()==53177||jbuilding.getID().getValue()==53256)
		if (direction1 != direction2) {
			p = intersect(line1p1, line1p2, line2p1, line2p2);
			//						p=intersectionZJU(line1p1, line1p2, line2p1, line2p2);
			//			debug.show(""
			//					,new ShapeDebugFrame.Line2DShapeInfo(new Line2D(line1p1, line1p2), "l1", Color.blue, false, true),
			//					new ShapeDebugFrame.Line2DShapeInfo(new Line2D(line2p1, line2p2), "l2", Color.red, false, true),
			//					p==null?new ShapeDebugFrame.DetailInfo("intersection:null"):new ShapeDebugFrame.Point2DShapeInfo(new Point2D(p.x, p.y), "intersection", Color.BLACK, true),
			//							new ShapeDebugFrame.DetailInfo("direction1:"+direction1),
			//							new ShapeDebugFrame.DetailInfo("direction2:"+direction2)
			//					);
		}

		return p;
	}

}