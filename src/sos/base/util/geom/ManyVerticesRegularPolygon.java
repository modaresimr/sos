package sos.base.util.geom;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Area;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame.ShapeInfo;

/**
 * @author http://java-sl.com/downloads.html
 */
public class ManyVerticesRegularPolygon extends Polygon {
	final int centerX;
	final int centerY;
	final int radious2;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ManyVerticesRegularPolygon(int x, int y, int r, int vertexCount) {
		this(x, y, r, vertexCount, 0);
		if (vertexCount < 30)
			System.err.println("too low vertices used in ManyVerticesRegularPolygon!!!");

	}

	public ManyVerticesRegularPolygon(int x, int y, int r, int vertexCount, double startAngle) {
		super(getXCoordinates(x, y, r, vertexCount, startAngle), getYCoordinates(x, y, r, vertexCount, startAngle), vertexCount);
		this.centerX = x;
		this.centerY = y;
		this.radious2 = (r>>10) * (r>>10);

	}

	protected static int[] getXCoordinates(int x, int y, int r, int vertexCount, double startAngle) {
		int res[] = new int[vertexCount];
		double addAngle = 2 * Math.PI / vertexCount;
		double angle = startAngle;
		for (int i = 0; i < vertexCount; i++) {
			res[i] = (int) Math.round(r * Math.cos(angle)) + x;
			angle += addAngle;
		}
		return res;
	}

	protected static int[] getYCoordinates(int x, int y, int r, int vertexCount, double startAngle) {
		int res[] = new int[vertexCount];
		double addAngle = 2 * Math.PI / vertexCount;
		double angle = startAngle;
		for (int i = 0; i < vertexCount; i++) {
			res[i] = (int) Math.round(r * Math.sin(angle)) + y;
			angle += addAngle;
		}
		return res;
	}

	@Override
	public boolean contains(int x, int y) {
		return ((this.centerX - x)>>10) * ((this.centerX - x)>>10) + ((this.centerY - y)>>10) * ((this.centerY - y)>>10) < radious2;
		//		return SOSGeometryTools.distance(this.x, this.y, x, y) < r;
	}

	@Override
	public boolean contains(double x, double y) {
		return (this.centerX - x)/1024 * (this.centerX - x)/1024 + (this.centerY - y)/1024 * (this.centerY - y)/1024 < radious2;
		//		return SOSGeometryTools.distance(this.x, this.y, x, y) < r;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return contains(x, y) && contains(x + w, y) && contains(x, y + h) && contains(x + w, y + h);
	}

	public boolean contains(Polygon r) {
		for (int i = 0; i < r.npoints; i++) {
			if (!contains(r.xpoints[i], r.ypoints[i]))
				return false;
		}
		return true;
	}

	public boolean contains(int[] apexList) {
		for (int i = 0; i < apexList.length; i += 2) {
			if (!contains(apexList[i], apexList[i + 1]))
				return false;
		}
		return true;
	}

	public boolean contains(Area area) {
		//		if(!contains(area.getShape().getBounds()))
		//			return false;
		return contains(area.getApexList());
	}

	public int[] getApexes() {
		int[] apexes = new int[npoints * 2];
		for (int i = 0; i < npoints; i++) {
			apexes[i * 2] = xpoints[i];
			apexes[i * 2 + 1] = ypoints[i];
		}
		return apexes;
	}

	public boolean contains(int[] apexList, ShapeDebugFrame debug) {
		for (int i = 0; i < apexList.length; i += 2) {
			ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeInfo>( debug.getShapes());
			shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(apexList[i], apexList[i + 1]),new Point2D(apexList[i], apexList[i + 1])+"", Color.black, true));
			shapes.add(new ShapeDebugFrame.DetailInfo("containt? "+contains(apexList[i], apexList[i + 1])));
			debug.show("",shapes );
			if (!contains(apexList[i], apexList[i + 1],debug))
				return false;
		}
		return true;
	}
	public boolean contains(int x, int y,ShapeDebugFrame debug) {
		ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeInfo>( debug.getShapes());
		shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(centerX, centerY),"center: "+new Point2D(centerX, centerY), Color.white, true));
		shapes.add(new ShapeDebugFrame.DetailInfo("r2= "+radious2));
		shapes.add(new ShapeDebugFrame.DetailInfo("x2= "+(this.centerX - x) * (long)(this.centerX - x)));
		shapes.add(new ShapeDebugFrame.DetailInfo("y2= "+(this.centerY - y) * (long)(this.centerY - y)));
		shapes.add(new ShapeDebugFrame.DetailInfo("x2+y2= "+((this.centerX - x) * (long)(this.centerX - x) + (this.centerY - y) * (long)(this.centerY - y))));
		debug.show("",shapes );
		return (this.centerX - x) * (long)(this.centerX - x) + (this.centerY - y) * (long)(this.centerY - y) < radious2;
		//		return SOSGeometryTools.distance(this.x, this.y, x, y) < r;
	}

}
