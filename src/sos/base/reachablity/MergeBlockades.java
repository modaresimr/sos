package sos.base.reachablity;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.reachablity.tools.ReachablityException;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.sosLogger.SOSLoggerSystem;

public class MergeBlockades {
	static SOSLoggerSystem log(StandardEntity entity) {
		return entity.getAgent().sosLogger.reachablity_Merge;
	}

	// Morteza2011*****************************************************************
	public static ArrayList<SOSArea> mergeBlockades(Road road, ArrayList<SOSArea> blockades) {
		//log(road).logln("RoadID:" + road.getID());
		//		for (SOSArea b : blockades) {
		//log(road).logln(b.getReachablityBlockades());
		//		}
		for (short i = 0; i < blockades.size(); i++) {
			for (short j = (short) (i + 1); j < blockades.size(); j++) {
				if (Utility.hasIntersect(blockades.get(i), blockades.get(j))) {
					SOSArea a1 = blockades.get(i);
					SOSArea a2 = blockades.get(j);
					SOSArea a3;
					try {
						a3 = merge(road, a1, a2, (short) (0));
					} catch (Exception e) {
						a3 = reMerge(a1, a2);
						e.printStackTrace();
					}

					blockades.remove(j--);
					if (!a3.getEdges().isEmpty()) {
						blockades.set(i, a3);
					}
					i--;
					break;
				} else {
					try {
						if (blockades.get(i).getShape().contains(blockades.get(j).getEdges().get(0).getStart().getX(), blockades.get(j).getEdges().get(0).getStart().getY())) {
							//log(road).logln(blockades.get(j)+" has included in "+blockades.get(i));
							blockades.get(i).addReachablityBlockades(blockades.get(j).getReachablityBlockades());
							blockades.remove(j--);
						} else {
							if (blockades.get(j).getShape().contains(blockades.get(i).getEdges().get(0).getStart().getX(), blockades.get(i).getEdges().get(0).getStart().getY())) {
								//log(road).logln(blockades.get(i)+" has included in "+blockades.get(j));
								blockades.get(j).addReachablityBlockades(blockades.get(i).getReachablityBlockades());
								blockades.remove(i--);
								break;
							}
							//else
							//log(road).logln("intersect:no and included:no!");

						}
					} catch (Exception e) {
						try {
							e.printStackTrace();
							SOSArea a1 = blockades.get(i);
							SOSArea a2 = blockades.get(j);
							SOSArea a3;
							a3 = reMerge(a1, a2);
							blockades.remove(j--);
							if (!a3.getEdges().isEmpty()) {
								blockades.set(i, a3);
							}
							i--;
							break;
						} catch (Exception e1) {

						}
					}
				}
			}
		}
		return blockades;
	}

	// Morteza2011*****************************************************************
	public static SOSArea merge(StandardEntity a, SOSArea blockade1, SOSArea blockade2, short mergedNumber) {

		ArrayList<Edge> edgeList1 = new ArrayList<Edge>(blockade1.getEdges());
		ArrayList<Edge> edgeList2 = new ArrayList<Edge>(blockade2.getEdges());
		Pair<ArrayList<Edge>, ArrayList<Edge>> newEdges = splitEdges(edgeList1, edgeList2);
		removeExtraEdges(newEdges.first(), blockade2.getShape());
		removeExtraEdges(newEdges.second(), blockade1.getShape());
		try {
			ArrayList<SOSArea> sortedEdges = sortEdges(a, newEdges.first(), newEdges.second(), false);
			SOSArea finalMergedArea;
			if (sortedEdges.size() > 1)
				finalMergedArea = removeHoles(sortedEdges);
			else if (sortedEdges.size() == 1)
				finalMergedArea = sortedEdges.get(0);
			else {
				return reMerge(blockade1, blockade2);
			}
			finalMergedArea.setEdges(verifyEdgesAfterMerge(finalMergedArea.getEdges()));
			finalMergedArea.setReachablityBlockades(blockade1.getReachablityBlockades());
			finalMergedArea.addReachablityBlockades(blockade2.getReachablityBlockades());
			return finalMergedArea;
		} catch (ReachablityException e) {
		}
		return reMerge(blockade1, blockade2);
	}

	// Morteza2011*****************************************************************
	public static SOSArea reMerge(SOSArea blockade1, SOSArea blockade2) {
		java.awt.geom.Area a1 = new Area(blockade1.getShape());
		java.awt.geom.Area a2 = new Area(blockade2.getShape());
		a1.add(a2);
		int[] apexes = AliGeometryTools.getApexes(a1);
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (int i = 0; i < apexes.length / 2 - 1; i++) {
			edges.add(new Edge(apexes[i * 2], apexes[i * 2 + 1], apexes[i * 2 + 2], apexes[i * 2 + 3]));
		}
		SOSArea finalArea = new SOSArea(edges);
		finalArea.addReachablityBlockades(blockade1.getReachablityBlockades());
		finalArea.addReachablityBlockades(blockade2.getReachablityBlockades());
		return finalArea;
	}

	// Morteza2011*****************************************************************
	public static ArrayList<Edge> verifyEdgesAfterMerge(List<Edge> realEdges) {
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
	public static Pair<ArrayList<Edge>, ArrayList<Edge>> splitEdges(ArrayList<Edge> edgeList1, List<Edge> edgeList2) {
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
			if (shape.contains(edges.get(i).getMidPoint().getX(), edges.get(i).getMidPoint().getY())) {
				edges.remove(i--);
			}
		}
	}

	// Morteza2011*****************************************************************
	public static ArrayList<SOSArea> sortEdges(StandardEntity a, ArrayList<Edge> removedExtraEdgesBlock1, ArrayList<Edge> removedExtraEdgesBlock2, boolean setDirection) throws ReachablityException {
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
									return sortEdges(a, removedExtraEdgesBlock1, newList, true);
								} catch (ReachablityException e) {
									throw new ReachablityException("need ReExpand!!!");
								}
							} else
								throw new ReachablityException("need ReExpand!!!");
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
			log(a).warn("Sorting has been completed but 1List has elements yet!!!");
		}
		if (edgeList.get(1).size() != indexes[1]) {
			log(a).warn("Sorting has been completed but 1List has elements yet!!!");
		}
		return areaList;
	}

	// Morteza2011*****************************************************************
	public static SOSArea removeHoles(ArrayList<SOSArea> areaParts) {
		if (areaParts.size() == 1)
			return areaParts.get(0);
		for (short i = 0; i < areaParts.size(); i++) {
			for (short j = 0; j < areaParts.size(); j++) {
				if (i == j)
					continue;
				if (areaParts.get(i).getShape().contains(areaParts.get(j).getEdges().get(0).getMidPoint().getX(), areaParts.get(j).getEdges().get(0).getMidPoint().getY())) {
					if (j < i)
						i--;
					areaParts.remove(j--);
				}
			}
		}
		return areaParts.get(0);
	}
}
