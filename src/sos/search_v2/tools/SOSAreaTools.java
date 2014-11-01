package sos.search_v2.tools;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSConstant;
import sos.base.entities.Edge;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.SOSActionException;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.tools.geometry.Line;

public class SOSAreaTools {
	public static SOSLoggerSystem logI = new SOSLoggerSystem(null, "Reachablity/Intersect", SOSConstant.CREATE_BASE_LOGS, OutputType.File);// Morteza2011
	
	public static Pair<Point2D, Point2D> get2PointsOnParallelLine(Line2D entrance, Point2D center) {
		Line l = new Line((int) entrance.getOrigin().getX(), (int) entrance.getOrigin().getY(), (int) entrance.getEndPoint().getX(), (int) entrance.getEndPoint().getY());
		l = new Line(l.getIncline(), (int) center.getX(), (int) center.getY());
		List<Point2D> list = Utility.get2PointsAroundAPointOnLine(center, l.getPoint(center.getX() * 2), center, 300);//TODO in 300 dorost nist
		return new Pair<Point2D, Point2D>(list.get(0), list.get(1));
	}
	
	public static java.awt.geom.Point2D getPoint(Pair<Integer, Integer> p) {
		Point2D d = new Point2D(p.first(), p.second());
		
		return d.toGeomPoint();
	}
	
	// Morteza2011*****************************************************************
	public static SOSArea intersect(SOSArea area1, SOSArea area2) {
		// final checked
		logI.setFullLoggingLevel();
		ArrayList<Edge> edgeList1 = new ArrayList<Edge>(area1.getEdges());
		ArrayList<Edge> edgeList2 = new ArrayList<Edge>(area2.getEdges());
		Pair<ArrayList<Edge>, ArrayList<Edge>> newEdges = splitEdges(edgeList1, edgeList2);
		removeExtraEdges(newEdges.first(), area2.getShape());
		removeExtraEdges(newEdges.second(), area1.getShape());
		try {
			ArrayList<SOSArea> sortedEdges = sortEdges(newEdges.first(), newEdges.second(), false);
			SOSArea finalMergedArea;
			if (sortedEdges.size() > 1)
				finalMergedArea = removeHoles(sortedEdges);
			else if (sortedEdges.size() == 1)
				finalMergedArea = sortedEdges.get(0);
			else {
				return reIntersect(area1, area2);
			}
			finalMergedArea.setEdges(verifyEdgesAfterMerge(finalMergedArea.getEdges()));
			return finalMergedArea;
		} catch (SOSActionException e) {
			return reIntersect(area1, area2);
		}
	}
	
	// Morteza2011*****************************************************************
	private static SOSArea reIntersect(SOSArea area1, SOSArea area2) {
		SOSArea mergedarea = new SOSArea();
		Area a1 = new Area(area1.getShape());
		Area a2 = new Area(area2.getShape());
		a1.intersect(a2);
		int[] app = AliGeometryTools.getApexes(a1);
		int next = 0;
		ArrayList<Edge> finalEdges = new ArrayList<Edge>();
		for (int i = 0; i < app.length; i += 2) {
			next = i + 2;
			if (next == app.length)
				next = 0;
			finalEdges.add(new Edge(new Point2D(app[i], app[i + 1]), new Point2D(app[next], app[next + 1])));
		}
		mergedarea = new SOSArea(finalEdges, area1.getID());
		return mergedarea;
	}
	
	// Morteza2011*****************************************************************
	private static ArrayList<Edge> verifyEdgesAfterMerge(List<Edge> realEdges) {
		ArrayList<Edge> edges = new ArrayList<Edge>(realEdges);
		for (short i = 0; i < edges.size(); i++) {
			double length = GeometryTools2D.getDistance(edges.get(i).getStart(), edges.get(i).getEnd());
			if (length < 100) {
				if (i > 0) {
					edges.get(i - 1).setEnd(edges.get(i).getEnd());
				} else {
					edges.get(edges.size() - 1).setEnd(edges.get(i).getEnd());
				}
				edges.remove(i--);
				continue;
			}
			for (short j = (short) (i + 1); j < edges.size(); j++) {
				if (edges.get(i).edgeEquals(edges.get(j))) {
					edges.remove(j--);
				}
			}
		}
		return edges;
	}
	
	// Morteza2011*****************************************************************
	private static void setEqualDirection(ArrayList<Edge> edgeList) {
		ArrayList<Edge> newEdges = new ArrayList<Edge>();
		for (short k = (short) (edgeList.size() - 1); k >= 0; k--) {
			Point2D p = edgeList.get(k).getStart();
			edgeList.get(k).setStart(edgeList.get(k).getEnd());
			edgeList.get(k).setEnd(p);
			newEdges.add(edgeList.get(k));
		}
		edgeList = newEdges;
	}

	
	// Morteza2011*****************************************************************
	private static Pair<ArrayList<Edge>, ArrayList<Edge>> splitEdges(ArrayList<Edge> edgeList1, List<Edge> edgeList2) {
		ArrayList<ArrayList<Pair<Point2D, Point2D>>> pointList1 = new ArrayList<ArrayList<Pair<Point2D, Point2D>>>();
		ArrayList<ArrayList<Pair<Point2D, Point2D>>> pointList2 = new ArrayList<ArrayList<Pair<Point2D, Point2D>>>();
		for (short i = 0; i < edgeList1.size(); i++) {
			pointList1.add(new ArrayList<Pair<Point2D, Point2D>>());
			pointList1.get(i).add(new Pair<Point2D, Point2D>(edgeList1.get(i).getStart(), edgeList1.get(i).getStart()));
			pointList1.get(i).add(new Pair<Point2D, Point2D>(edgeList1.get(i).getStart(), edgeList1.get(i).getEnd()));
		}
		for (short i = 0; i < edgeList2.size(); i++) {
			pointList2.add(new ArrayList<Pair<Point2D, Point2D>>());
			pointList2.get(i).add(new Pair<Point2D, Point2D>(edgeList2.get(i).getStart(), edgeList2.get(i).getStart()));
			pointList2.get(i).add(new Pair<Point2D, Point2D>(edgeList2.get(i).getStart(), edgeList2.get(i).getEnd()));
		}
		for (short i = 0; i < edgeList1.size(); i++) {
			for (short j = 0; j < edgeList2.size(); j++) {
				Point2D intersect = Utility.getIntersect(edgeList1.get(i), edgeList2.get(j));
				if (intersect != null) {
					pointList1.get(i).add(new Pair<Point2D, Point2D>(edgeList1.get(i).getStart(), intersect));
					pointList2.get(j).add(new Pair<Point2D, Point2D>(edgeList2.get(j).getStart(), intersect));
				}
			}
		}
		for (short i = 0; i < edgeList1.size(); i++) {
			sortPoints(pointList1.get(i));
		}
		for (short i = 0; i < edgeList2.size(); i++) {
			sortPoints(pointList2.get(i));
		}
		ArrayList<Edge> newEdges1 = new ArrayList<Edge>();
		ArrayList<Edge> newEdges2 = new ArrayList<Edge>();
		for (short i = 0; i < pointList1.size(); i++) {
			for (short j = 0; j < pointList1.get(i).size() - 1; j++) {
				Edge e = new Edge(pointList1.get(i).get(j).second(), pointList1.get(i).get(j + 1).second());
				newEdges1.add(e);
			}
		}
		for (short i = 0; i < pointList2.size(); i++) {
			for (short j = 0; j < pointList2.get(i).size() - 1; j++) {
				Edge e = new Edge(pointList2.get(i).get(j).second(), pointList2.get(i).get(j + 1).second());
				newEdges2.add(e);
			}
		}
		return new Pair<ArrayList<Edge>, ArrayList<Edge>>(newEdges1, newEdges2);
	}
	
	// Morteza2011*****************************************************************
	private static void sortPoints(ArrayList<Pair<Point2D, Point2D>> points) {
		Comparator<Pair<Point2D, Point2D>> pointComparator = new Comparator<Pair<Point2D, Point2D>>() {
			@Override
			public int compare(Pair<Point2D, Point2D> p1, Pair<Point2D, Point2D> p2) {
				if (GeometryTools2D.getDistance(p1.first(), p1.second()) < GeometryTools2D.getDistance(p2.first(), p2.second()))
					return -1;
				else if (GeometryTools2D.getDistance(p1.first(), p1.second()) > GeometryTools2D.getDistance(p2.first(), p2.second()))
					return 1;
				return 0;
			}
		};
		Collections.sort(points, pointComparator);
	}
	
	// Morteza2011*****************************************************************
	private static void removeExtraEdges(ArrayList<Edge> edges, Shape shape) {
		for (short i = 0; i < edges.size(); i++) {
			if (!shape.contains(edges.get(i).getMidPoint().getX(), edges.get(i).getMidPoint().getY())) {
				edges.remove(i--);
			}
		}
	}
	
	
	// Morteza2011*****************************************************************
	private static ArrayList<SOSArea> sortEdges(ArrayList<Edge> removedExtraEdgesBlock1, ArrayList<Edge> removedExtraEdgesBlock2, boolean setDirection) throws SOSActionException {
		ArrayList<SOSArea> areaList = new ArrayList<SOSArea>();
		ArrayList<ArrayList<Edge>> edgeList = new ArrayList<ArrayList<Edge>>(2);
		ArrayList<Edge> sortedEdges = new ArrayList<Edge>();
		edgeList.add(new ArrayList<Edge>(removedExtraEdgesBlock1));
		edgeList.add(new ArrayList<Edge>(removedExtraEdgesBlock2));
		short index = 0;
		short[] indexes = { 0, 0 };
		boolean firstTime = true;
		while (indexes[0] < edgeList.get(0).size() || indexes[1] < edgeList.get(1).size()) {
			Point2D start, end;
			if (indexes[0] < edgeList.get(0).size()) {
				start = edgeList.get(0).get(indexes[0]).getStart();
				end = edgeList.get(0).get(indexes[0]).getEnd();
				sortedEdges.add(edgeList.get(0).get(indexes[0]));
				indexes[0]++;
				index = 0;
			} else {
				start = edgeList.get(1).get(indexes[1]).getStart();
				end = edgeList.get(1).get(indexes[1]).getEnd();
				sortedEdges.add(edgeList.get(1).get(indexes[1]));
				indexes[1]++;
				index = 1;
			}
			while (!end.isEqualTo(start)) {
				if (indexes[index] < edgeList.get(index).size() && edgeList.get(index).get(indexes[index]).getStart().equals(end)) {
					sortedEdges.add(edgeList.get(index).get(indexes[index]));
					indexes[index]++;
				} else if (indexes[1 - index] < edgeList.get(1 - index).size() && edgeList.get(1 - index).get(indexes[1 - index]).getStart().equals(end)) {
					firstTime = false;
					index = (short) (1 - index);
					sortedEdges.add(edgeList.get(index).get(indexes[index]));
					indexes[index]++;
				} else {
					boolean found = false;
					index = (short) (1 - index);
					short listSize = (short) (edgeList.get(index).size() - indexes[index]);
					for (short i = 0; i < listSize; i++) {
						if (!edgeList.get(index).get(indexes[index]).getStart().equals(end)) {
							edgeList.get(index).add(edgeList.get(index).get(indexes[index]));
							indexes[index]++;
						} else {
							found = true;
							firstTime = false;
							break;
						}
					}
					if (!found) {
						if (areaList.size() == 0) {
							if (firstTime && !setDirection) {
								ArrayList<Edge> newList = new ArrayList<Edge>(removedExtraEdgesBlock2);
								setEqualDirection(newList);
								try {
									return sortEdges(removedExtraEdgesBlock1, newList, true);
								} catch (SOSActionException e) {
									throw new SOSActionException("need ReExpand!!!");
								}
							} else
								throw new SOSActionException("need ReExpand!!!");
						} else
							return areaList;
					}
					sortedEdges.add(edgeList.get(index).get(indexes[index]));
					indexes[index]++;
				}
				end = sortedEdges.get(sortedEdges.size() - 1).getEnd();
			}
			SOSArea area = new SOSArea(sortedEdges);
			if (sortedEdges.size() > 2)
				areaList.add(area);
			sortedEdges.clear();
		}
		if (edgeList.get(0).size() != indexes[0]) {
			logI.warn("Sorting has been completed but 1List has elements yet!!!");
		}
		if (edgeList.get(1).size() != indexes[1]) {
			logI.warn("Sorting has been completed but 1List has elements yet!!!");
		}
		return areaList;
	}
	
	// Morteza2011*****************************************************************
	private static SOSArea removeHoles(ArrayList<SOSArea> areaParts) {
		if (areaParts.size() == 1)
			return areaParts.get(0);
		for (short i = 0; i < areaParts.size(); i++) {
			for (short j = 0; j < areaParts.size(); j++) {
				if (i == j)
					continue;
				if (areaParts.get(i).getShape().contains(areaParts.get(j).getEdges().get(0).getMidPoint().getX(), areaParts.get(j).getEdges().get(0).getMidPoint().getY())) {
					if (j > i)
						j--;
					areaParts.remove(i--);
				}
			}
		}
		return areaParts.get(0);
	}
}

	

