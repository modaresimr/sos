package sos.base.util;

import java.util.ArrayList;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.reachablity.IntersectAreas;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.sosFireZone.util.Utill;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.geom.ShapeInArea;

import com.infomatiq.jsi.Rectangle;

public class FireSearchBuilding {
	private final Building building;
	public final ArrayList<Road> nearRoads = new ArrayList<Road>();// AngehA2
	private ArrayList<ShapeInArea> sensibleAreasOfAreas;
	public static final double[] cos = { 1.0, 0.9961946980917455, 0.984807753012208, 0.9659258262890683, 0.9396926207859084, 0.9063077870366499, 0.8660254037844387, 0.8191520442889918, 0.766044443118978, 0.7071067811865476, 0.6427876096865394, 0.5735764363510462, 0.5000000000000001, 0.42261826174069944, 0.3420201433256688, 0.25881904510252074, 0.17364817766693041, 0.08715574274765814, 6.123233995736766E-17, -0.08715574274765824, -0.1736481776669303, -0.25881904510252085, -0.3420201433256685, -0.42261826174069933, -0.4999999999999998, -0.5735764363510462, -0.6427876096865394, -0.7071067811865475, -0.7660444431189779, -0.8191520442889919, -0.8660254037844387, -0.9063077870366499, -0.9396926207859083, -0.9659258262890683, -0.984807753012208, -0.9961946980917455, -1.0, -0.9961946980917455, -0.984807753012208, -0.9659258262890684, -0.9396926207859084, -0.90630778703665, -0.8660254037844386, -0.819152044288992, -0.7660444431189783, -0.7071067811865477, -0.6427876096865395, -0.5735764363510456, -0.5000000000000004, -0.42261826174069994, -0.34202014332566855, -0.25881904510252063, -0.17364817766693033, -0.08715574274765825, -1.8369701987210297E-16, 0.08715574274765789, 0.17364817766692997, 0.2588190451025203, 0.342020143325669, 0.42261826174069883, 0.5000000000000001, 0.573576436351046, 0.6427876096865393, 0.7071067811865474, 0.7660444431189778, 0.8191520442889916, 0.8660254037844388, 0.9063077870366497, 0.9396926207859084, 0.9659258262890683, 0.9848077530122079, 0.9961946980917455, };
	public static final double[] sin = { 0.000000000000001, 0.08715574274765817, 0.17364817766693033, 0.25881904510252074, 0.3420201433256687, 0.42261826174069944, 0.49999999999999994, 0.573576436351046, 0.6427876096865393, 0.7071067811865475, 0.766044443118978, 0.8191520442889917, 0.8660254037844386, 0.9063077870366499, 0.9396926207859083, 0.9659258262890683, 0.984807753012208, 0.9961946980917455, 1.0, 0.9961946980917455, 0.984807753012208, 0.9659258262890683, 0.9396926207859084, 0.90630778703665, 0.8660254037844387, 0.8191520442889917, 0.766044443118978, 0.7071067811865476, 0.6427876096865395, 0.5735764363510459, 0.49999999999999994, 0.4226182617406995, 0.3420201433256689, 0.2588190451025206, 0.17364817766693028, 0.08715574274765864, 1.2246467991473532E-16, -0.08715574274765794, -0.17364817766693047, -0.25881904510252035, -0.34202014332566866, -0.4226182617406993, -0.5000000000000001, -0.5735764363510458, -0.6427876096865389, -0.7071067811865475, -0.7660444431189779, -0.8191520442889921, -0.8660254037844385, -0.9063077870366497, -0.9396926207859084, -0.9659258262890683, -0.984807753012208, -0.9961946980917455, -1.0, -0.9961946980917455, -0.9848077530122081, -0.9659258262890684, -0.9396926207859083, -0.9063077870366503, -0.8660254037844386, -0.8191520442889918, -0.7660444431189781, -0.7071067811865477, -0.6427876096865396, -0.5735764363510465, -0.49999999999999967, -0.4226182617407, -0.3420201433256686, -0.2588190451025207, -0.17364817766693127, -0.08715574274765832, };
//	private SOSArea verifiedSensibleArea;
	private double senseDis;
	private int checkDistance;

	public static long sensableTime = 0;
	public static long roadTime = 0;
	public static long buildingTime = 0;

//		public static ShapeDebugFrame debug = new ShapeDebugFrame();

	public FireSearchBuilding(Building b) {
		this.building = b;

		senseDis = building().getAgent().getConfig().getIntValue("perception.los.max-distance");
		if(b.model().sosAgent().getMapInfo().isBigMap()||b.model().sosAgent().getMapInfo().isMediumMap())
			checkDistance = 100000;
		else
			checkDistance = 40000;

		setNearRoads();
	}

	public Building building() {
		return building;
	}

	private void setNearRoads() {

		for (Road r : this.building().model().getObjectsInRange(x(), y(), checkDistance, Road.class))
			nearRoads.add(r);
	}

	public ArrayList<Road> nearRoads() {
		return nearRoads;
	}

	/** @author Morteza2012 ************************************************************/
	public Edge[] setSensibleArea() {

		ArrayList<Point2D> points = new ArrayList<Point2D>();
		int allNum = 72;
		for (int i = 0; i < allNum; i++) {
			double x = this.x() + (1000000) * cos[i];
			double y = this.y() + (1000000) * sin[i];
			double maxDistance = Double.MIN_VALUE;
			Edge selfEdge = null;
			Point2D selfP = null;
			for (Edge e : building().getEdges()) {
				Point2D p = getSegmentIntersection(e, new Edge(new Point2D(x(), y()), new Point2D(x, y)));
				//								Point2D p = Utill.intersectLowProcess(e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY(), x(), y(), (float) x, (float) y);
				if (p != null) {
					double dist = distance(p.getX(), p.getY(), x(), y());
					if (dist > maxDistance) {
						maxDistance = dist;
						selfEdge = e;
						selfP = p;
					}
				}
			}
			if (maxDistance == Double.MIN_VALUE)
			{
				//				continue;
				//				System.err.println(building + " ba ray be tool e 1,000,000 khodesh ro ghat nakard!!!!");
				maxDistance = 0;
			}
			double dis = senseDis + maxDistance;
			x = this.x() + dis * cos[i];
			y = this.y() + dis * sin[i];
			Edge e = new Edge(new Point2D(x(), y()), new Point2D(x, y));
			Rectangle rect = new Rectangle(x(), y(), (float) x, (float) y);
			IntList ns = building.model().getBuildingIndexInRectangle(rect);
//			if(building.getID().getValue()==49342){
//
//				debug.setBackgroundEntities(Color.gray, building.model().buildings());
//			}
			loop: for (int b = 0; b < ns.size(); b++) {
				Building bu = building.model().buildings().get(ns.get(b));
				if (bu.getID().getValue() == building.getID().getValue())
					continue;
				for (Edge edge : bu.getEdges()) {
					if (edge.isPassable())
						continue;
					if (selfEdge != null && selfEdge.edgeEquals(edge)) {
						e.setEnd(selfP);
						break loop;
					}
					Point2D p =GeometryTools2D.getSegmentIntersectionPoint(e.getLine(), edge.getLine());

//					Point2D p = Utill.intersectLowProcess(e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY(), edge.getStartX(), edge.getStartY(), edge.getEndX(), edge.getEndY());
//					if(building.getID().getValue()==49342){
//						debug.show("", new ShapeDebugFrame.Line2DShapeInfo(e.getLine(), "e", Color.black, false, true),
//								new ShapeDebugFrame.Line2DShapeInfo(edge.getLine(), "edge", Color.red, false, true),
//								new ShapeDebugFrame.Point2DShapeInfo(p, "p", Color.white, true)
//								);
//					}
					if (p != null) {
						e.setEnd(p);
					}
				}
			}
			points.add(e.getEnd());
		}
		ArrayList<Edge> finalEdges = new ArrayList<Edge>();
		points.add(points.get(0));
		for (int i = 0; i < points.size() - 1; i++) {
			Edge newEdge = new Edge(points.get(i), points.get(i + 1));
			finalEdges.add(newEdge);
		}

		return finalEdges.toArray(new Edge[4]);
	}
//	static ShapeDebugFrame debug=new ShapeDebugFrame();
//	/** @author nima */
//	public SOSArea verifiedSensibleArea() {
//		return verifiedSensibleArea;
//	}

	/** @author nima */
	/*
	 * private SOSShape verifySensibleArea(SOSArea sensibleArea) {
	 * if (true) {
	 * int[] apexes = AliGeometryTools.getApexes(sensibleArea.getEdges());
	 * if (apexes.length >= 6)
	 * return new SOSShape(apexes);
	 * else {
	 * System.out.println("this may cause problem!");
	 * return null;
	 * }
	 * }
	 * // ShapeDebugFrame debug = new ShapeDebugFrame();
	 * // debug.setAutozoomEnabled(false);
	 * // ArrayList<ShapeInfo> backs = new ArrayList<ShapeDebugFrame.ShapeInfo>();
	 * // for (Building b : building.getNearBuildings()) {
	 * // backs.add(new ShapeDebugFrame.AWTShapeInfo(b.getShape(), b + "", Color.black, false));
	 * // }
	 * // backs.add(new ShapeDebugFrame.AWTShapeInfo(building.getShape(), building + "", Color.red, false));
	 * // debug.setBackground(backs);
	 * // ArrayList<ShapeInfo> show = new ArrayList<ShapeDebugFrame.ShapeInfo>();
	 * // show.add(new ShapeDebugFrame.AWTShapeInfo(sensibleArea.getShape(), "target: ", Color.blue, false));
	 * ArrayList<Edge> inputEdges = new ArrayList<Edge>(sensibleArea.getEdges());
	 * ArrayList<Pair<Point2D, Building>> addableApexes = new ArrayList<Pair<Point2D, Building>>();
	 * Collection<Building> bin = building.model().getObjectsInRange(building, checkDistance, Building.class);
	 * for (Building b : bin) {
	 * for (Edge edge : b.getEdges()) {
	 * if (edge.isPassable())
	 * continue;
	 * if (sensibleArea.getShape().contains(edge.getStartX(), edge.getStartY())) {
	 * addableApexes.add(new Pair<Point2D, Building>(edge.getStart(), b));
	 * // System.err.println("addable apexes : ");
	 * // System.err.print(" building : " + b);
	 * // System.err.print("    x     : " + edge.getStartX());
	 * // System.err.print("    y     : " + edge.getStartY());
	 * // System.err.println("");
	 * }
	 * }
	 * }
	 * Building b = this.building();
	 * for (Pair<Point2D, Building> p : addableApexes) {
	 * Edge e = null;
	 * SOSArea area = null;
	 * for (int i = 0; i < inputEdges.size(); i++) {
	 * e = inputEdges.get(i);
	 * ArrayList<Edge> edges = new ArrayList<Edge>();
	 * edges.add(new Edge(b.center().x, b.center().y, e.getStartX(), e.getStartY()));
	 * edges.add(e);
	 * edges.add(new Edge(e.getEndX(), e.getEndY(), b.center().x, b.center().y));
	 * area = new SOSArea(edges);
	 * // show.clear();
	 * if (area.getShape().contains(p.first().getX(), p.first().getY())) {
	 * boolean isOnEdge = false;
	 * for (Edge edge : p.second().getEdges()) {
	 * if (Utility.getIntersect(edge, e) != null) {
	 * isOnEdge = true;
	 * break;
	 * }
	 * }
	 * if (!isOnEdge)
	 * continue;
	 * // System.err.println("addable apex with index " + addableApexes.indexOf(p) + " must be inserted between apexes of edge" + i);
	 * int startX = e.getStartX();
	 * int startY = e.getStartY();
	 * int endX = e.getEndX();
	 * int endY = e.getEndY();
	 * // System.err.println("removed : " + i + " : " + inputEdges.get(i));
	 * inputEdges.remove(i);
	 * inputEdges.add(i, new Edge(startX, startY, (int) p.first().getX(), (int) p.first().getY()));
	 * inputEdges.add(i + 1, new Edge((int) p.first().getX(), (int) p.first().getY(), endX, endY));
	 * // System.err.println("added : " + i + " : " + inputEdges.get(i));
	 * // System.err.println("added : " + (i + 1) + " : " + inputEdges.get(i + 1));
	 * // show.add(new ShapeDebugFrame.Line2DShapeInfo((new Edge(e.getStartX(), e.getStartY(), (int) p.first().getX(), (int) p.first().getY())).getLine(), "newLine", Color.BLUE, true, false));
	 * // show.add(new ShapeDebugFrame.Line2DShapeInfo((new Edge((int) p.first().getX(), (int) p.first().getY(), e.getEndX(), e.getEndY())).getLine(), "newLine", Color.BLUE, true, false));
	 * // debug.show("i : " + i, show);
	 * break;
	 * }
	 * // show.add(new ShapeDebugFrame.AWTShapeInfo((new SOSArea(inputEdges)).getShape(), "target: ", Color.red, false));
	 * // show.add(new ShapeDebugFrame.AWTShapeInfo(area.getShape(), "shape", Color.orange, false));
	 * // debug.show("i : " + i, show);
	 * }
	 * }
	 * // for (Edge e : inputEdges)
	 * // System.err.println(inputEdges.indexOf(e) + " : " + e);
	 * int[] apexes = AliGeometryTools.getApexes(inputEdges);
	 * if (apexes.length >= 6)
	 * return new SOSShape(apexes);
	 * else {
	 * System.out.println("this may cause problem!");
	 * return null;
	 * }
	 * }
	 */

	/** @author nima */
	/*
	 * public void setSensibleAreaAndNeighbors_Building() {
	 * ArrayList<Point2D> points = new ArrayList<Point2D>();
	 * int allNum = 72;
	 * for (int i = 0; i < allNum; i++) {
	 * double dis = building().getAgent().getConfig().getIntValue("perception.los.max-distance");
	 * double x = this.x() + (100000) * Math.cos(Math.PI * i * (360 / allNum) / 180);
	 * double y = this.y() + (100000) * Math.sin(Math.PI * i * (360 / allNum) / 180);
	 * double minDistance = Double.MAX_VALUE;
	 * for (Edge e : building().getEdges()) {
	 * Point2D p = getSegmentIntersection(e, new Edge(new Point2D(x(), y()), new Point2D(x, y)));
	 * if (p != null) {
	 * double dist = distance(p.getX(), p.getY(), x(), y());
	 * if (dist < minDistance)
	 * minDistance = dist;
	 * }
	 * }
	 * if (minDistance == Double.MAX_VALUE)
	 * continue;
	 * dis = dis + minDistance;
	 * x = this.x() + dis * Math.cos(Math.PI * i * (360 / allNum) / 180);
	 * y = this.y() + dis * Math.sin(Math.PI * i * (360 / allNum) / 180);
	 * Edge e = new Edge(new Point2D(x(), y()), new Point2D(x, y));
	 * minDistance = Double.MAX_VALUE;
	 * short indexOfNeighbor = -1;
	 * for (Building b : building().getNearBuildings()) {
	 * for (Edge edge : b.getEdges()) {
	 * Point2D p = getSegmentIntersection(edge, e);
	 * if (p != null) {
	 * e.setEnd(p);
	 * double dist = distance(p.getX(), p.getY(), x(), y());
	 * if (dist < minDistance) {
	 * minDistance = dist;
	 * indexOfNeighbor = b.getIndex();
	 * }
	 * }
	 * }
	 * }
	 * if (indexOfNeighbor > -1)
	 * this.building().realNeighbors_Building().add(this.building().model().buildings().get(indexOfNeighbor));
	 * points.add(e.getEnd());
	 * }
	 * ArrayList<Edge> finalEdges = new ArrayList<Edge>();
	 * points.add(points.get(0));
	 * for (int i = 0; i < points.size() - 1; i++) {
	 * Edge newEdge = new Edge(points.get(i), points.get(i + 1));
	 * finalEdges.add(newEdge);
	 * }
	 * int[] apexes = AliGeometryTools.getApexes(finalEdges);
	 * if(apexes.length>=6)
	 * sensibleArea = new SOSShape(apexes);
	 * else
	 * System.out.println("it may cause problem");
	 * }
	 */

	public void setSensibleAreasOfAreas() {
		long s = System.currentTimeMillis();
		Edge[] sensableArea = setSensibleArea();
		sensableTime += System.currentTimeMillis() - s;
		sensibleAreasOfAreas = new ArrayList<ShapeInArea>();
		SOSArea[] a = new SOSArea[4];
		Point2D center = new Point2D(building.x(), building.y());
		Edge e1, e2;
		for (int j = 0; j < a.length; j++) {
			e1 = new Edge(sensableArea[17 + 18 * j].getEnd(), center);
			e2 = new Edge(center, sensableArea[18 * j].getStart());
			ArrayList<Edge> edges = new ArrayList<Edge>();
			for (int i = 18 * j; i < 18 * (j + 1); i++) {
				edges.add(sensableArea[i]);
			}
			edges.add(e1);
			edges.add(e2);
			int minX, maxX, minY, maxY;
			minX = maxX = sensableArea[18 * j].getStartX();
			minY = maxY = sensableArea[18 * j].getStartY();
			for (Edge edge : edges) {
				if (edge.getEndX() < minX)
					minX = edge.getEndX();
				if (edge.getEndY() < minY)
					minY = edge.getEndY();
				if (edge.getEndX() > maxX)
					maxX = edge.getEndX();
				if (edge.getEndY() > maxY)
					maxY = edge.getEndY();
			}
			a[j] = new SOSArea(edges);
			IntList roads = building.model().getRoadIndexInRectangle(new Rectangle(minX, minY, maxX, maxY));
			//			Collection<Road> roadsInRange = building.model().getObjectsInRectangle(a[j].getBounds(), sos.base.entities.Road.class);
			int[] intersectArea;
			for (int r1 = 0; r1 < roads.size(); r1++) {
				Road r = building.model().roads().get(roads.get(r1));
				s = System.currentTimeMillis();
				short x = 0;
				for (Edge e : r.getEdges()) {
					if (a[j].getShape().contains(e.getStart().getX(), e.getStart().getY()))
						x++;
					else
						x--;
				}
				if (x == r.getEdges().size()) {
					if (r.getExpandedArea().getApexes().length >= 6) {
						ShapeInArea shape = new ShapeInArea(r.getExpandedArea().getApexes(), r);
						if(shape.isValid())
							sensibleAreasOfAreas.add(shape);
					}
				}
				//			else if (x == -1 * r.getExpandedArea().getEdges().size()) {
				//				continue;
				//			}
				else {
					intersectArea = IntersectAreas.intersect(r.getExpandedArea(), a[j]);
//					java.awt.geom.Area area1 = new java.awt.geom.Area(r.getExpandedArea().getShape());
//					java.awt.geom.Area area2 = new java.awt.geom.Area( a[j].getShape());
//					area1.intersect(area2);
//					ShapeInArea shape = new ShapeInArea(AliGeometryTools.getApexes(area1), r);
					if (intersectArea.length >= 6) {
						ShapeInArea shape = new ShapeInArea(intersectArea, r);
						if(shape.isValid())
							sensibleAreasOfAreas.add(shape);
					}
				}
				roadTime += System.currentTimeMillis() - s;
			}
			//			if (sensibleAreasOfAreas.isEmpty()) {
			//			s = System.currentTimeMillis();
			//			for (Area b : building.getNeighbours()) {
			//				if (!(b instanceof Building))
			//					continue;
			//				intersectArea = IntersectAreas.intersect(new SOSArea(b.getEdges()), a[j]);
			//				if (intersectArea.length >= 6)
			//					sensibleAreasOfAreas.add(new ShapeInArea(intersectArea, b));
			//				//				}
			//				inRanges.add(b);
			//			}
			//			buildingTime += System.currentTimeMillis() - s;
			//			}
		}
		if (sensibleAreasOfAreas.isEmpty())
			for (int j = 0; j < a.length; j++) {
				int[] intersectArea;
				s = System.currentTimeMillis();
				for (Area b : building.getNeighbours()) {
					if (!(b instanceof Building))
						continue;
					intersectArea = IntersectAreas.intersect(new SOSArea(b.getEdges()), a[j]);
					if (intersectArea.length >= 6){
						ShapeInArea shape = new ShapeInArea(intersectArea, b);
						if(shape.isValid())
							sensibleAreasOfAreas.add(shape);
					}
					//				}
				}
				buildingTime += System.currentTimeMillis() - s;
				//			}
			}
		if (sensibleAreasOfAreas.isEmpty() && building.getApexes().length >= 6)
			sensibleAreasOfAreas.add(new ShapeInArea(building.getApexes(), building));

	}

	public void setSensibleAreasOfRoads(ArrayList<ShapeInArea> areas) {
		sensibleAreasOfAreas = areas;
	}

	public ArrayList<ShapeInArea> sensibleAreasOfAreas() { // FIXME what the fuck?
		if (sensibleAreasOfAreas == null)
		//FIXME FIXME FIXME XXX chera senseble area az search area sakhte mishe????????!!!!!
		/*
		 * if (building.getSearchAreas() != null && building.getSearchAreas().size() > 0) {
		 * ArrayList<ShapeInArea> as = new ArrayList<ShapeInArea>();
		 * for (ShapeInArea a : building.getSearchAreas()) {
		 * as.add(new ShapeInArea(a.getApexes(), a.getArea()));
		 * }
		 * setSensibleAreasOfRoads(as);
		 * } else
		 */{
			setSensibleAreasOfAreas();
		}
		return sensibleAreasOfAreas;
	}

	// Morteza2011_implementedByAngehA2********************************************
	public int x() {
		return building().getX();
	}

	// Morteza2011_implementedByAngehA2********************************************
	public int y() {
		return building().getY();
	}

	// Morteza2011*****************************************************************
	public static Point2D getSegmentIntersection(Edge edge1, Edge edge2) {
		Point2D point = GeometryTools2D.getIntersectionPoint(edge1.getLine(), edge2.getLine());
		if (point != null) {
			if (((int) point.getX() <= edge1.getStartX() && (int) point.getX() >= edge1.getEndX()) || ((int) point.getX() >= edge1.getStartX() && (int) point.getX() <= edge1.getEndX())) {
				if (((int) point.getY() <= edge1.getStartY() && (int) point.getY() >= edge1.getEndY()) || ((int) point.getY() >= edge1.getStartY() && (int) point.getY() <= edge1.getEndY())) {
					if (((int) point.getX() <= edge2.getStartX() && (int) point.getX() >= edge2.getEndX()) || ((int) point.getX() >= edge2.getStartX() && (int) point.getX() <= edge2.getEndX())) {
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
	public static double distance(double x1, double y1, double x2, double y2) {
		return java.lang.Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	// Morteza2012*****************************************************************
	public void SetSearchArea() {
		//	public static ShapeDebugFrame debug = new ShapeDebugFrame();
		//		ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		//		debug.setBackgroundEntities(Color.gray, building.model().buildings());
		Point2D center = new Point2D(building.x(), building.y());
		int rayNum = 20;
		for (Edge e : building.getEdges()) {
			if (e.isPassable())
			{
				ArrayList<Edge> edges = new ArrayList<Edge>();
				for (int i = 0; i < rayNum; i++) {
					int dx = e.getStartX() + (e.getEndX() - e.getStartX()) * i / (rayNum - 1) - building.x();
					int dy = e.getStartY() + (e.getEndY() - e.getStartY()) * i / (rayNum - 1) - building.y();
					int dis = Utill.distance(e.getStartX() + (e.getEndX() - e.getStartX()) / 4, e.getStartY() + (e.getEndY() - e.getStartY()) / 4, center.getIntX(), center.getIntY());
					dx *= senseDis;
					dx /= dis;
					dy *= senseDis;
					dy /= dis;
					Edge e1 = new Edge(center, new Point2D(center.getX() + dx, center.getY() + dy));
					boolean[] bus = new boolean[building.model().buildings().size()];
					bus[building.getBuildingIndex()] = true;
					IntList bs = building.model().getBuildingIndexInRectangle(new Rectangle(e1.getStartX(), e1.getStartY(), e1.getEndX(), e1.getEndY()));
					for (int b = 0; b < bs.size(); b++) {
						if (bus[bs.get(b)])
							continue;
						bus[bs.get(b)] = true;
						for (Edge ed : building.model().buildings().get(bs.get(b)).getEdges()) {
							if (ed.isPassable())
								continue;
							Point2D intersect = Utility.getIntersect(ed, e1);
							if (intersect != null) {
								e1.setEnd(intersect);
							}
						}
					}
					if (edges.size() > 0) {
						edges.add(new Edge(edges.get(edges.size() - 1).getEnd(), e1.getEnd()));
						if (edges.size() == rayNum)
							edges.add(new Edge(e1.getEnd(), e1.getStart()));
					} else
						edges.add(e1);
					//					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(e1.getLine(), "e1", Color.pink, false, true));
				}
				SOSArea a = new SOSArea(edges);
				//				shapes.add(new ShapeDebugFrame.AWTShapeInfo(a.getShape(), "", Color.blue, false));
				//					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(e1.getLine(), "e1", Color.pink, false, true));
				//					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(e2.getLine(), "e2", Color.green, false, true));
				//					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(e3.getLine(), "e3", Color.red, false, true));
				//					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(e4.getLine(), "e4", Color.orange, false, true));
				//					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(e5.getLine(), "e5", Color.cyan, false, true));

				IntList rs = building.model().getRoadIndexInRectangle(a.getBounds());
				for (int r1 = 0; r1 < rs.size(); r1++) {
					Road r = building.model().roads().get(rs.get(r1));
					java.awt.geom.Area ar1 = new java.awt.geom.Area(r.getExpandedArea().getShape());
					java.awt.geom.Area ar2 = new java.awt.geom.Area(a.getShape());
					ar2.intersect(ar1);
					int[] ai = AliGeometryTools.getApexes(ar2);
					//					int[] ai = IntersectAreas.intersect(r.getExpandedArea(), a);
					if (ai.length > 4){
						ShapeInArea shape = new ShapeInArea(ai, r);
						if(shape.isValid())
						building.getSearchAreas().add(shape);
					}
				}
				if (building.model().getEntity(e.getNeighbour()) instanceof Building) {
					Building b = (Building) building.model().getEntity(e.getNeighbour());
					//					int[] ai = IntersectAreas.intersect(new SOSArea(b.getEdges()), a);
					java.awt.geom.Area ar1 = new java.awt.geom.Area(b.getShape());
					java.awt.geom.Area ar2 = new java.awt.geom.Area(a.getShape());
					ar2.intersect(ar1);
					int[] ai = AliGeometryTools.getApexes(ar2);
					if (ai.length > 4){
						ShapeInArea shape = new ShapeInArea(ai, b);
						if(shape.isValid())
							building.getSearchAreas().add(shape);
					}
				}

				//				for (ShapeInArea s : building.getSearchAreas()) {
				//					shapes.add(new ShapeDebugFrame.AWTShapeInfo(s, "", Color.green, true));
				//				}
				//				if(building.getID().getValue()==49621)
				//				debug.show(building + "", shapes);
			}
		}
		building.getSearchAreas().add(new ShapeInArea(building.getApexes(), building));

	}
}
