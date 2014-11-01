package sos.base.reachablity;

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
import sos.base.reachablity.tools.RoadGraph;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.sosLogger.SOSLoggerSystem;

public class CreateRoadGraph {
	static SOSLoggerSystem log(StandardEntity entity) {
		return entity.getAgent().sosLogger.reachablity_RoadGraph;
	}

	// Morteza2011*****************************************************************
	public static RoadGraph createRoadGraph(StandardEntity entity, SOSArea area, ArrayList<SOSArea> blockades) {
		Pair<ArrayList<Edge>, ArrayList<Edge>> splitedEdges = splitForGraph(area, blockades);
		ArrayList<Edge> removedExtraEdges = removeExtraEdgesForGraph(((Road) entity), splitedEdges.first(), splitedEdges.second(), area, blockades);
		RoadGraph roadGraph = addToGraph((Road) entity, removedExtraEdges);
		//		log(entity).logln("ID: " + entity.getID().getValue() + " " + roadGraph);
		//		for (Edge edge : removedExtraEdges) {
		//			log(entity).logln(edge);
		//		}
		return roadGraph;
	}

	// Morteza2011*****************************************************************
	private static Pair<ArrayList<Edge>, ArrayList<Edge>> splitForGraph(SOSArea area, List<SOSArea> blockades) {
		ArrayList<ArrayList<Pair<Point2D, Point2D>>> blockPoints;
		/*
		 * instead of keeping the ArrayLists each in blockade(SOSArea) I keep
		 * them in the scope of the function so it would not occupy the meory
		 * Salim on March 27 2011
		 */
		ArrayList<ArrayList<ArrayList<Pair<Point2D, Point2D>>>> blockadePoints = new ArrayList<ArrayList<ArrayList<Pair<Point2D, Point2D>>>>();
		/* changes end */
		for (SOSArea b : blockades) {
			blockPoints = new ArrayList<ArrayList<Pair<Point2D, Point2D>>>(b.getEdges().size());
			for (short i = 0; i < b.getEdges().size(); i++) {
				blockPoints.add(new ArrayList<Pair<Point2D, Point2D>>());
				blockPoints.get(i).add(new Pair<Point2D, Point2D>(b.getEdges().get(i).getStart(), b.getEdges().get(i).getStart()));
				blockPoints.get(i).add(new Pair<Point2D, Point2D>(b.getEdges().get(i).getStart(), b.getEdges().get(i).getEnd()));
			}
			blockadePoints.add(blockPoints);
		}
		ArrayList<ArrayList<Pair<Point2D, Point2D>>> areaPoints = new ArrayList<ArrayList<Pair<Point2D, Point2D>>>();
		for (short i = 0; i < area.getEdges().size(); i++) {
			areaPoints.add(new ArrayList<Pair<Point2D, Point2D>>());
			areaPoints.get(i).add(new Pair<Point2D, Point2D>(area.getEdges().get(i).getStart(), area.getEdges().get(i).getStart()));
			areaPoints.get(i).add(new Pair<Point2D, Point2D>(area.getEdges().get(i).getStart(), area.getEdges().get(i).getEnd()));
		}
		for (short i = 0; i < area.getEdges().size(); i++) {
			int index = -1;
			for (SOSArea b : blockades) {
				index++;
				for (short j = 0; j < b.getEdges().size(); j++) {
					Point2D intersect = Utility.getIntersect(area.getEdges().get(i), b.getEdges().get(j));
					if (intersect != null) {
						areaPoints.get(i).add(new Pair<Point2D, Point2D>(area.getEdges().get(i).getStart(), intersect));
						blockadePoints.get(index).get(j).add(new Pair<Point2D, Point2D>(b.getEdges().get(j).getStart(), intersect));
					}
				}
			}
		}
		for (short i = 0; i < area.getEdges().size(); i++) {
			sortPoints(areaPoints.get(i));
		}
		for (int index = 0; index < blockades.size(); index++) {
			for (short i = 0; i < blockadePoints.get(index).size(); i++) {
				sortPoints(blockadePoints.get(index).get(i));
			}
		}
		ArrayList<Edge> areaEdges = new ArrayList<Edge>();
		ArrayList<Edge> blockEdges = new ArrayList<Edge>();
		for (short i = 0; i < areaPoints.size(); i++) {
			for (short j = 0; j < areaPoints.get(i).size() - 1; j++) {
				Edge e = new Edge(areaPoints.get(i).get(j).second(), areaPoints.get(i).get(j + 1).second());
				e.setReachablityIndex(area.getEdges().get(i).getReachablityIndex());
				areaEdges.add(e);
			}
		}
		for (short k = 0; k < blockades.size(); k++) {
			for (short i = 0; i < blockadePoints.get(k).size(); i++) {
				for (short j = 0; j < blockadePoints.get(k).get(i).size() - 1; j++) {
					Edge e = new Edge(blockadePoints.get(k).get(i).get(j).second(), blockadePoints.get(k).get(i).get(j + 1).second());
					e.setReachablityIndex((short) (-k - 2));
					blockEdges.add(e);
				}
			}
		}
		return new Pair<ArrayList<Edge>, ArrayList<Edge>>(areaEdges, blockEdges);
	}

	// Morteza2011*****************************************************************
	private static void sortPoints(ArrayList<Pair<Point2D, Point2D>> points) {
		Comparator<Pair<Point2D, Point2D>> pointComparator = new Comparator<Pair<Point2D, Point2D>>() {
			@Override
			public int compare(Pair<Point2D, Point2D> p1, Pair<Point2D, Point2D> p2) {
				if (GeometryTools2D.getDistance(p1.first(), p1.second()) < GeometryTools2D.getDistance(p2.first(), p2.second()))
					return -1;
				return 1;
			}
		};
		Collections.sort(points, pointComparator);
	}

	// Morteza2011*****************************************************************
	private static ArrayList<Edge> removeExtraEdgesForGraph(Road r, ArrayList<Edge> areaEdges, ArrayList<Edge> blockadesEdges, SOSArea area, ArrayList<SOSArea> blockades) {
		ArrayList<Edge> allEdges = new ArrayList<Edge>();
		//		r.removedEdges.clear();
		for (short i = 0; i < blockadesEdges.size(); i++) {
			if (area.getShape().contains(blockadesEdges.get(i).getMidPoint().getX(), blockadesEdges.get(i).getMidPoint().getY()))
				allEdges.add(blockadesEdges.get(i));
			//			else
			//				r.removedEdges.add(blockadesEdges.get(i));
		}
		for (short i = 0; i < areaEdges.size(); i++) {
			boolean contain = false;
			for (short j = 0; j < blockades.size(); j++) {
				if (blockades.get(j).getShape().contains(areaEdges.get(i).getMidPoint().getX(), areaEdges.get(i).getMidPoint().getY())) {
					contain = true;
					break;
				}
			}
			if (!contain)
				allEdges.add(areaEdges.get(i));
			//			else
			//				r.removedEdges.add(areaEdges.get(i));
		}
		//		r.removedEdges.clear();
		//		r.removedEdges.addAll(allEdges);
		return allEdges;
	}

	// Morteza2011*****************************************************************
	private static RoadGraph addToGraph(Road r, ArrayList<Edge> edges) {
		//		ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();

		ArrayList<Point2D> points = new ArrayList<Point2D>();
		ArrayList<ArrayList<Pair<Short, Short>>> graph = new ArrayList<ArrayList<Pair<Short, Short>>>();
		//		for (int i=0;i<edges.size();i++) {
		//			Edge e=edges.get(i);
		//			if(e.getStart().distanceSq(e.getEnd())<10){
		//				for (Edge e1 : edges) {
		//					if(e1.getStart().isEqualTo(e.getEnd()))
		//						e1.setStart(e.getStart());
		//					if(e1.getEnd().isEqualTo(e.getEnd()))
		//						e1.setEnd(e.getStart());
		//				}
		//				edges.remove(i--);
		//			}
		//		}
		//		if (r.model().time() > 5) {
		//			for (Edge e : edges) {
		//				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(e.getLine(), "", Color.gray, false, false));
		//			}
		//			debug.setBackground(shapes);
		//			shapes.clear();
		//		}
		for (Edge edge : edges) {
			short start = -1, end = -1;
			for (short i = 0; i < points.size(); i++) {
				if (edge.getStart().isEqualTo1(points.get(i)))
					start = i;
				if (edge.getEnd().isEqualTo1(points.get(i)))
					end = i;
			}
			if (start == -1) {
				graph.add(new ArrayList<Pair<Short, Short>>());
				start = (short) points.size();
				points.add(edge.getStart());
				//				if (r.getID().getValue() == 6351 && r.model().time() > 5) {
				//					shapes.add(new ShapeDebugFrame.Point2DShapeInfo(edge.getStart(), "", Color.BLUE, true));
				//					debug.show("", shapes);
				//				}
				//			} else if (r.model().time() > 5 && r.getID().getValue() == 6351) {
				//				shapes.add(new ShapeDebugFrame.Point2DShapeInfo(edge.getStart(), "", Color.RED, true));
				//				debug.show("", shapes);
			}
			if (end == -1) {
				graph.add(new ArrayList<Pair<Short, Short>>());
				end = (short) points.size();
				points.add(edge.getEnd());
				//				if (r.getID().getValue() == 6351 && r.model().time() > 5) {
				//					shapes.add(new ShapeDebugFrame.Point2DShapeInfo(edge.getEnd(), "", Color.BLUE, true));
				//					debug.show("", shapes);
				//				}
				//			}
				//			else if (r.getID().getValue() == 6351 && r.model().time() > 5) {
				//				shapes.add(new ShapeDebugFrame.Point2DShapeInfo(edge.getEnd(), "", Color.RED, true));
				//				debug.show("", shapes);
			}
			graph.get(start).add(new Pair<Short, Short>(end, edge.getReachablityIndex()));
			graph.get(end).add(new Pair<Short, Short>(start, edge.getReachablityIndex()));
			//			if (r.getID().getValue() == 6351 && r.model().time() > 5) {
			//				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(edge.getLine(), edge.toString(), Color.pink, false, false));
			//				debug.show("", shapes);
			//			}
		}
		RoadGraph rg = new RoadGraph(graph, points);
//		if (SOSConstant.CREATE_BASE_LOGS) {
//			if (r.model().time() > 5) {
//				log(r).logln("--------------------------------------------------------\nRoad: " + r + "\n");
//				log(r).logln(rg);
//				for (Edge e : edges) {
//					log(r).logln(e);
//				}
//			}
//		}
		return rg;
	}

	//	public static ShapeDebugFrame debug = new ShapeDebugFrame();
}
