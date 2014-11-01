package sos.base.util.geom;

import java.awt.Polygon;
import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import sos.base.reachablity.tools.Utility;
import sos.base.util.SOSGeometryTools;

public class SOSShape extends Polygon {
	private static final long serialVersionUID = -2515226990006054470L;
//	private static ShapeDebugFrame debug = new ShapeDebugFrame();
	
	private int centerX;
	private int centerY;
	private boolean valid = true;
	
	private transient String errorReason;
	public SOSShape(int[] apexes) {
		super();
		if (apexes.length <= 4) {
			errorReason=("a shape with just 2 point???? what is the advantages?");
			valid = false;
		}

		int count = apexes.length / 2;
		int[] xs = new int[count];
		int[] ys = new int[count];
		for (int i = 0; i < count; ++i) {
			xs[i] = apexes[i * 2];
			ys[i] = apexes[i * 2 + 1];
		}

		this.xpoints = xs;
		this.ypoints = ys;
		this.npoints = count;

		if (apexes.length == 0) {
			errorReason=("sos shape has error in defination!!!!!!apexes.length should be >0");
			valid = false;
		} else
			computeCenter(apexes);

		if (!contains(centerX, centerY)) {
			errorReason=("[sos shape] center point not in shape!!!!!!");
			valid = false;
		}
		if (SOSGeometryTools.computeArea(apexes) < 100) {
			errorReason=("[sos shape]Too Small Area....=" + SOSGeometryTools.computeArea(apexes));
			valid = false;
		}

	}

	private void computeCenter(int[] apexes) {

		rescuecore2.geometry.Point2D p = SOSGeometryTools.computeCentroid(apexes);
		centerX = (int) p.getX();
		centerY = (int) p.getY();
		if (apexes.length < 6)
			return;
		try {
			Point2D point1 = null,point2= null,middlePoint = null;
			ArrayList<rescuecore2.geometry.Point2D> twoPointOutOfLine= null;
			if (!contains(centerX, centerY)) {//if the point is not inside the polygon
				///////////////////////finding a point inside polygon////////////////
				FOR:for (int i = 0; i < apexes.length - 2; i += 2) {
					point1 = new rescuecore2.geometry.Point2D(apexes[i], apexes[i + 1]);
					point2 = new rescuecore2.geometry.Point2D(apexes[i + 2], apexes[i + 3]);
					middlePoint = new rescuecore2.geometry.Point2D((apexes[i] + apexes[i + 2]) / 2, (apexes[i + 1] + apexes[i + 3]) / 2);
					twoPointOutOfLine =
							Utility.get2PointsAroundAPointOutOfLine(
									point1,
									point2,
									middlePoint, 2);
					for (rescuecore2.geometry.Point2D point2d : twoPointOutOfLine) {
						if (contains(point2d.getX(), point2d.getY())) {
							centerX = (int) point2d.getX();
							centerY = (int) point2d.getY();
							break FOR;
						}
					}

				}
			/*
			if (!contains(centerX, centerY)) {
				debug.show("SOS Shape", new ShapeDebugFrame.AWTShapeInfo(this, "apexes=" + Arrays.toString(apexes), Color.blue, false),
						new ShapeDebugFrame.Point2DShapeInfo(point1, "Point 1", Color.red, true),
						new ShapeDebugFrame.Point2DShapeInfo(point2, "Point 2", Color.green, true),
						new ShapeDebugFrame.Point2DShapeInfo(middlePoint, "middlePoint", Color.pink, true),
						new ShapeDebugFrame.Point2DShapeInfo(twoPointOutOfLine.get(0), "twoPointOutOfLine 1", Color.magenta, true),
						new ShapeDebugFrame.Point2DShapeInfo(twoPointOutOfLine.get(1), "twoPointOutOfLine 2", Color.black, true),
						new ShapeDebugFrame.DetailInfo("area:" + SOSGeometryTools.computeAreaUnsigned(apexes))
						);
			}
			*/
				///////////////////////////////////////////////////////////////////////
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(this.getClass().getSimpleName() + " have error" + e.getMessage());

		}

	}

	public boolean isValid() {
		return valid;
	}
	public String getErrorReason() {
		return errorReason;
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public rescuecore2.geometry.Point2D getCenterPoint() {
		return new rescuecore2.geometry.Point2D(getCenterX(), getCenterY());
	}

	public int[] getApexes() {
		int[] apexes = new int[npoints * 2];
		for (int i = 0; i < npoints; i++) {
			apexes[i * 2] = xpoints[i];
			apexes[i * 2 + 1] = ypoints[i];
		}

		return apexes;
	}

	@Override
	public String toString() {
		return "[Shape x=" + getCenterX() + "y=" + getCenterY() + "]";
	}
}
