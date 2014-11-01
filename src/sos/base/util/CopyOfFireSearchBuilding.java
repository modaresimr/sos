package sos.base.util;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.sosFireZone.util.Utill;
import sos.base.sosFireZone.util.Wall;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.geom.SOSShape;
import sos.base.util.geom.ShapeInArea;
import sos.police_v2.PoliceConstants;

import com.infomatiq.jsi.Rectangle;

public class CopyOfFireSearchBuilding {
	private final Building building;
	public final ArrayList<Road> nearRoads = new ArrayList<Road>();// AngehA2
	private ArrayList<ShapeInArea> sensibleAreasOfAreas;
	public static final double[] sin={0.0,0.08715574274765817,0.17364817766693033,0.25881904510252074,0.3420201433256687,0.42261826174069944,0.49999999999999994,0.573576436351046,0.6427876096865393,0.7071067811865475,0.766044443118978,0.8191520442889917,0.8660254037844386,0.9063077870366499,0.9396926207859083,0.9659258262890683,0.984807753012208,0.9961946980917455,1.0,0.9961946980917455,0.984807753012208,0.9659258262890683,0.9396926207859084,0.90630778703665,0.8660254037844387,0.8191520442889917,0.766044443118978,0.7071067811865476,0.6427876096865395,0.5735764363510459,0.49999999999999994,0.4226182617406995,0.3420201433256689,0.2588190451025206,0.17364817766693028,0.08715574274765864,1.2246467991473532E-16,-0.08715574274765794,-0.17364817766693047,-0.25881904510252035,-0.34202014332566866,-0.4226182617406993,-0.5000000000000001,-0.5735764363510458,-0.6427876096865389,-0.7071067811865475,-0.7660444431189779,-0.8191520442889921,-0.8660254037844385,-0.9063077870366497,-0.9396926207859084,-0.9659258262890683,-0.984807753012208,-0.9961946980917455,-1.0,-0.9961946980917455,-0.9848077530122081,-0.9659258262890684,-0.9396926207859083,-0.9063077870366503,-0.8660254037844386,-0.8191520442889918,-0.7660444431189781,-0.7071067811865477,-0.6427876096865396,-0.5735764363510465,-0.49999999999999967,-0.4226182617407,-0.3420201433256686,-0.2588190451025207,-0.17364817766693127,-0.08715574274765832};
	public static final double[] cos={1.0,0.9961946980917455,0.984807753012208,0.9659258262890683,0.9396926207859084,0.9063077870366499,0.8660254037844387,0.8191520442889918,0.766044443118978,0.7071067811865476,0.6427876096865394,0.5735764363510462,0.5000000000000001,0.42261826174069944,0.3420201433256688,0.25881904510252074,0.17364817766693041,0.08715574274765814,6.123233995736766E-17,-0.08715574274765824,-0.1736481776669303,-0.25881904510252085,-0.3420201433256685,-0.42261826174069933,-0.4999999999999998,-0.5735764363510462,-0.6427876096865394,-0.7071067811865475,-0.7660444431189779,-0.8191520442889919,-0.8660254037844387,-0.9063077870366499,-0.9396926207859083,-0.9659258262890683,-0.984807753012208,-0.9961946980917455,-1.0,-0.9961946980917455,-0.984807753012208,-0.9659258262890684,-0.9396926207859084,-0.90630778703665,-0.8660254037844386,-0.819152044288992,-0.7660444431189783,-0.7071067811865477,-0.6427876096865395,-0.5735764363510456,-0.5000000000000004,-0.42261826174069994,-0.34202014332566855,-0.25881904510252063,-0.17364817766693033,-0.08715574274765825,-1.8369701987210297E-16,0.08715574274765789,0.17364817766692997,0.2588190451025203,0.342020143325669,0.42261826174069883,0.5000000000000001,0.573576436351046,0.6427876096865393,0.7071067811865474,0.7660444431189778,0.8191520442889916,0.8660254037844388,0.9063077870366497,0.9396926207859084,0.9659258262890683,0.9848077530122079,0.9961946980917455};

	private SOSArea verifiedSensibleArea;
	private double senseDis;
	private int checkDistance;
	public static long sensableAreaTime = 0;

	public CopyOfFireSearchBuilding(Building b) {
		this.building = b;

		senseDis = building().getAgent().getConfig().getIntValue("perception.los.max-distance");

		switch (b.model().sosAgent().getMapInfo().getRealMapName()) {

		case Berlin:
		case Big:
		case Unknown:
		case Medium:
		case Paris:
		case Istanbul:
			checkDistance = 100000;
			break;
		case Kobe:
		case Small:
		case VC:
		default:
			checkDistance = 40000;
			break;
		}

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

	//	public void setSensibleArea(SOSShape sosShape) {
	//		sensibleArea = sosShape;
	//	}

	//	public SOSShape sensibleArea() {
	//		return sensibleArea;
	//	}

	/**
	 * @author nima
	 * @return
	 */
	private SOSShape setSensibleArea() {
		long t = System.currentTimeMillis();
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		byte allNum = 72;
		double[] sin = new double[72];
		double[] cos = new double[72];
		for (int i = 0; i < allNum; i++) {
			double x = this.x() + (100000) * cos[i];
			double y = this.y() + (100000) * sin[i];
			int maxDistance = Integer.MIN_VALUE;
			for (Edge e : building().getEdges()) {
				Point2D p = Utill.intersectLowProcess(e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY(), x(), y(), (float) x, (float) y);
				//						Point2D p = getSegmentIntersection(e, new Edge(new Point2D(x(), y()), new Point2D(x, y)));
				if (p != null) {
					int dist = (int) distance(p.getX(), p.getY(), x(), y());
					if (dist > maxDistance)
						maxDistance = dist;
				}
			}
			if (maxDistance == Integer.MIN_VALUE)
				continue;
			double dis = senseDis + maxDistance;
			x = this.x() + dis * cos[i];
			y = this.y() + dis * sin[i];
			Edge e = new Edge(new Point2D(x(), y()), new Point2D(x, y));
			int num = (((int) dis - 1) / Wall.SAMPLE_DISTANCE) + 1;
			int dx = (int) x - x();
			int dy = (int) y - y();
			int startX = x();
			int startY = y();
			int endX = x() + dx / num;
			int endY = x() + dx / num;
			boolean[] bd = new boolean[building.model().buildings().size()];
			bd[building.getBuildingIndex()] = true;
			boolean intersected = false;
			for (int j = 0; j < num; j++) {
				Rectangle rect = new Rectangle(startX, startY, endX, endY);
				Collection<Building> bs = building.model().getObjectsInRectangle(rect, Building.class);
				for (Building b : bs) {
					if (bd[b.getBuildingIndex()])
						continue;
					bd[b.getBuildingIndex()] = true;
					for (Edge edge : b.getEdges()) {
						if (edge.isPassable())
							continue;
						Point2D p = Utill.intersectLowProcess(e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY(), edge.getStartX(), edge.getStartY(), edge.getEndX(), edge.getEndY());
						//							Point2D p = getSegmentIntersection(edge, e);
						if (p != null) {
							e.setEnd(p);
						}
					}
				}
				if (intersected)
					break;
				startX += dx / num;
				startY += dy / num;
				endX += dx / num;
				endY += dy / num;
			}
			points.add(e.getEnd());
		}
		ArrayList<Edge> finalEdges = new ArrayList<Edge>();
		points.add(points.get(0));
		for (int i = 0; i < points.size() - 1; i++) {
			Edge newEdge = new Edge(points.get(i), points.get(i + 1));
			finalEdges.add(newEdge);
		}
		sensableAreaTime += (System.currentTimeMillis() - t);
		//		sensibleArea = new SOSArea(finalEdges);
		//		verifiedSensibleArea = verifySensibleArea(sensibleArea);
		return verifySensibleArea(new SOSArea(finalEdges));//TODO verify bug dare bayad dorost beshe!!!
		//		sensibleArea = new SOSShape(new SOSArea(finalEdges).getApexes());
		//		return verifySensibleArea(new SOSArea(building.sensibleAreasEdges));//TODO verify bug dare bayad dorost beshe!!!
	}

	/** @author nima */
	public SOSArea verifiedSensibleArea() {
		return verifiedSensibleArea;
	}

	/** @author nima */
	private SOSShape verifySensibleArea(SOSArea sensibleArea) {
		if (true) {
			int[] apexes = AliGeometryTools.getApexes(sensibleArea.getEdges());
			if (apexes.length >= 6){
				SOSShape shape = new SOSShape(apexes);
				if(shape.isValid())
					return shape;
				
			}
			
				System.err.println("this may cause problem!");
				return null;
			
		}
		//		ShapeDebugFrame debug = new ShapeDebugFrame();
		//		debug.setAutozoomEnabled(false);
		//		ArrayList<ShapeInfo> backs = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		//		for (Building b : building.getNearBuildings()) {
		//			backs.add(new ShapeDebugFrame.AWTShapeInfo(b.getShape(), b + "", Color.black, false));
		//		}
		//		backs.add(new ShapeDebugFrame.AWTShapeInfo(building.getShape(), building + "", Color.red, false));
		//		debug.setBackground(backs);
		//		ArrayList<ShapeInfo> show = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		//		show.add(new ShapeDebugFrame.AWTShapeInfo(sensibleArea.getShape(), "target: ", Color.blue, false));
		ArrayList<Edge> inputEdges = new ArrayList<Edge>(sensibleArea.getEdges());
		ArrayList<Pair<Point2D, Building>> addableApexes = new ArrayList<Pair<Point2D, Building>>();
		Collection<Building> bin = building.model().getObjectsInRange(building, checkDistance, Building.class);
		for (Building b : bin) {
			for (Edge edge : b.getEdges()) {
				if (edge.isPassable())
					continue;
				if (sensibleArea.getShape().contains(edge.getStartX(), edge.getStartY())) {
					addableApexes.add(new Pair<Point2D, Building>(edge.getStart(), b));
					//					System.err.println("addable apexes : ");
					//					System.err.print(" building : " + b);
					//					System.err.print("    x     : " + edge.getStartX());
					//					System.err.print("    y     : " + edge.getStartY());
					//					System.err.println("");
				}
			}
		}
		Building b = this.building();
		for (Pair<Point2D, Building> p : addableApexes) {
			Edge e = null;
			SOSArea area = null;
			for (int i = 0; i < inputEdges.size(); i++) {
				e = inputEdges.get(i);
				ArrayList<Edge> edges = new ArrayList<Edge>();
				edges.add(new Edge(b.x(), b.y(), e.getStartX(), e.getStartY()));
				edges.add(e);
				edges.add(new Edge(e.getEndX(), e.getEndY(), b.x(), b.y()));
				area = new SOSArea(edges);
				//				show.clear();
				if (area.getShape().contains(p.first().getX(), p.first().getY())) {
					boolean isOnEdge = false;
					for (Edge edge : p.second().getEdges()) {
						if (Utility.getIntersect(edge, e) != null) {
							isOnEdge = true;
							break;
						}
					}
					if (!isOnEdge)
						continue;
					//					System.err.println("addable apex with index " + addableApexes.indexOf(p) + " must be inserted between apexes of edge" + i);
					int startX = e.getStartX();
					int startY = e.getStartY();
					int endX = e.getEndX();
					int endY = e.getEndY();
					//					System.err.println("removed : " + i + " : " + inputEdges.get(i));
					inputEdges.remove(i);
					inputEdges.add(i, new Edge(startX, startY, (int) p.first().getX(), (int) p.first().getY()));
					inputEdges.add(i + 1, new Edge((int) p.first().getX(), (int) p.first().getY(), endX, endY));
					//					System.err.println("added : " + i + " : " + inputEdges.get(i));
					//					System.err.println("added : " + (i + 1) + " : " + inputEdges.get(i + 1));
					//						show.add(new ShapeDebugFrame.Line2DShapeInfo((new Edge(e.getStartX(), e.getStartY(), (int) p.first().getX(), (int) p.first().getY())).getLine(), "newLine", Color.BLUE, true, false));
					//						show.add(new ShapeDebugFrame.Line2DShapeInfo((new Edge((int) p.first().getX(), (int) p.first().getY(), e.getEndX(), e.getEndY())).getLine(), "newLine", Color.BLUE, true, false));
					//						debug.show("i : " + i, show);
					break;
				}
				//				show.add(new ShapeDebugFrame.AWTShapeInfo((new SOSArea(inputEdges)).getShape(), "target: ", Color.red, false));
				//				show.add(new ShapeDebugFrame.AWTShapeInfo(area.getShape(), "shape", Color.orange, false));
				//				debug.show("i : " + i, show);
			}

		}
		//		for (Edge e : inputEdges)
		//			System.err.println(inputEdges.indexOf(e) + " : " + e);
		int[] apexes = AliGeometryTools.getApexes(inputEdges);
		if (apexes.length >= 6)
			return new SOSShape(apexes);
		else {
			System.out.println("this may cause problem!");
			return null;
		}
	}

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

	public SOSShape sensibleArea;

	public void setSensibleAreasOfAreas() {
		sensibleArea = setSensibleArea();
		sensibleAreasOfAreas = new ArrayList<ShapeInArea>();

		Collection<Road> roadsInRange = building.model().getObjectsInRectangle(sensibleArea.getBounds(), sos.base.entities.Road.class);
		for (sos.base.entities.Area areaEntity : roadsInRange) {
			java.awt.geom.Area intersectArea = new java.awt.geom.Area(areaEntity.getShape());
			java.awt.geom.Area sensibleArea_Area = new java.awt.geom.Area(sensibleArea);
			intersectArea.intersect(sensibleArea_Area);
			List<java.awt.geom.Area> splitedIntersectArea = AliGeometryTools.fix(intersectArea);
			for (Area area : splitedIntersectArea) {
				int[] apexes = AliGeometryTools.getApexes(area);
				if (apexes.length >= 6 && SOSGeometryTools.computeArea(apexes) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM * 2)
					sensibleAreasOfAreas.add(new ShapeInArea(apexes, areaEntity));
			}
		}
		if (sensibleAreasOfAreas.isEmpty()) {
			Collection<Building> buildingInRange = building.model().getObjectsInRectangle(sensibleArea.getBounds(), sos.base.entities.Building.class);
			for (sos.base.entities.Area areaEntity : buildingInRange) {
				java.awt.geom.Area intersectArea = new java.awt.geom.Area(areaEntity.getShape());
				java.awt.geom.Area sensibleArea_Area = new java.awt.geom.Area(sensibleArea);
				intersectArea.intersect(sensibleArea_Area);
				List<java.awt.geom.Area> splitedIntersectArea = AliGeometryTools.fix(intersectArea);
				for (Area area : splitedIntersectArea) {
					int[] apexes = AliGeometryTools.getApexes(area);
					if (apexes.length >= 6 && SOSGeometryTools.computeArea(apexes) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)
						sensibleAreasOfAreas.add(new ShapeInArea(apexes, areaEntity));
				}
			}
		}
		if (sensibleAreasOfAreas.isEmpty())
			sensibleAreasOfAreas.add(new ShapeInArea(building.getApexList(), building));

		//		sensibleArea = null;

		/*
		 * for (Road r : nearRoads) {
		 * SOSArea a = new SOSArea(r.getEdges(), r.getID().getValue());
		 * boolean hasIntersect = false;
		 * loop: for (Edge e1 : r.getEdges()) {
		 * for (Edge e2 : sensibleArea.getEdges()) {
		 * if (getSegmentIntersection(e1, e2) != null) {
		 * hasIntersect = true;
		 * SOSArea intersect = IntersectAreas.intersect(a, sensibleArea);
		 * intersect.setRoad(r);
		 * intersect.setID(r.getID().getValue());
		 * sensibleAreasOfRoads.add(intersect);
		 * break loop;
		 * }
		 * }
		 * }
		 * if (!hasIntersect)
		 * if (sensibleArea.getShape().contains(r.getEdges().get(0).getStart().getX(), r.getEdges().get(0).getStart().getY())) {
		 * a.setRoad(r);
		 * sensibleAreasOfRoads.add(a);
		 * }
		 * }
		 */
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

}
