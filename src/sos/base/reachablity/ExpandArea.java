package sos.base.reachablity;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import sos.base.entities.Area;
import sos.base.entities.Edge;
import sos.base.entities.StandardEntity;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.sosLogger.SOSLoggerSystem;

public class ExpandArea {

	static SOSLoggerSystem log(StandardEntity entity) {
		return entity.getAgent().sosLogger.reachablity_ExpandArea;
	}
	
	// Morteza2011*****************************************************************
	public static SOSArea expandArea(Area area) {
		ArrayList<Edge> finalExpandedEdges = null;
		ArrayList<Edge> firstLevelExpandedEdges = null;
		firstLevelExpandedEdges = setFirstLevelExpandedEdges(area,area.getEdges(), area.getShape());
		finalExpandedEdges = setIntersectPointsAndCreateNewEdges(firstLevelExpandedEdges);
		return new SOSArea(finalExpandedEdges, area.getID().getValue());
	}

	// Morteza2011*****************************************************************
	private static ArrayList<Edge> setFirstLevelExpandedEdges(Area area,List<Edge> areaEdge, Shape shape) {
		ArrayList<Edge> firstLevelExpandedEdges = new ArrayList<Edge>();
		ArrayList<Point2D> startPoints = null;
		ArrayList<Point2D> endPoints = null;
		ArrayList<Point2D> midPoints = null;
		for (short i = 0; i < areaEdge.size(); i++) {
			Edge edge = areaEdge.get(i);
			Edge newEdge = null;
			if (!edge.isPassable()) {
				startPoints = Utility.get2PointsAroundAPointOutOfLine(edge.getStart(), edge.getEnd(), edge.getStart(), ReachablityConstants.AREA_EXPAND_WIDTH);
				endPoints = Utility.get2PointsAroundAPointOutOfLine(edge.getStart(), edge.getEnd(), edge.getEnd(), ReachablityConstants.AREA_EXPAND_WIDTH);
				midPoints = Utility.get2PointsAroundAPointOutOfLine(edge.getStart(), edge.getEnd(), edge.getMidPoint(), ReachablityConstants.TEST_DISTANCE);
				if (shape.contains(midPoints.get(0).getX(), midPoints.get(0).getY())) {
					newEdge = new Edge(startPoints.get(0), endPoints.get(0));
					newEdge.setReachablityIndex(edge.getReachablityIndex());
				} else if (shape.contains(midPoints.get(1).getX(), midPoints.get(1).getY())) {
					newEdge = new Edge(startPoints.get(1), endPoints.get(1));
					newEdge.setReachablityIndex(edge.getReachablityIndex());
				} else {
					log(area).warn("CONTAINED NON OF POINTS !!!");
					newEdge = new Edge(edge.getStart(), edge.getEnd());
					newEdge.setReachablityIndex(edge.getReachablityIndex());
				}
			} else {
				newEdge = new Edge(edge.getStart(), edge.getEnd());
				newEdge.setReachablityIndex(edge.getReachablityIndex());
				newEdge.setNeighbour(edge.getNeighbour());
			}
			firstLevelExpandedEdges.add(newEdge);
		}
		return firstLevelExpandedEdges;
	}

	// Morteza2011*****************************************************************
	private static ArrayList<Edge> setIntersectPointsAndCreateNewEdges(ArrayList<Edge> firstLevelExpandedEdges) {
		firstLevelExpandedEdges.add(firstLevelExpandedEdges.get(0));
		for (short i = 0; i < firstLevelExpandedEdges.size() - 1; i++) {
			Edge e1 = firstLevelExpandedEdges.get(i);
			Edge e2 = firstLevelExpandedEdges.get(i + 1);
			if (e1.isPassable() && e2.isPassable()) {
				// do nothing
			} else if (e1.isPassable() || e2.isPassable()) {
				double angle = Utility.getAngle(e1, e2);
				if (angle > 30) {
					Point2D ilp = GeometryTools2D.getIntersectionPoint(e1.getLine(), e2.getLine());
					if (ilp != null) {
						e1.setEnd(ilp);
						e2.setStart(ilp);
					} else {
						e1.setEnd(e2.getStart());
					}
				} else {
					Edge newEdge = new Edge(e1.getEnd(), e2.getStart());
					newEdge.setReachablityIndex((short) (-1));
					firstLevelExpandedEdges.add(i + 1, newEdge);
					i++;
				}
			} else {
				Point2D iep = Utility.getIntersect(e1, e2);
				if (iep != null) {
					e1.setEnd(iep);
					e2.setStart(iep);
				} else {
					if (GeometryTools2D.getDistance(e1.getEnd(), e2.getStart()) < 20) {
						e1.setEnd(e2.getStart());
					} else {
						Point2D ilp = GeometryTools2D.getIntersectionPoint(e1.getLine(), e2.getLine());
						if (ilp != null) {
							e1.setEnd(ilp);
							e2.setStart(ilp);
						} else {
							e1.setEnd(e2.getStart());
						}
					}
				}
			}
		}
		firstLevelExpandedEdges.remove(firstLevelExpandedEdges.size() - 1);
		return firstLevelExpandedEdges;
	}
}
