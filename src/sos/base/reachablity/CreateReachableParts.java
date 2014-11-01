package sos.base.reachablity;

import java.util.ArrayList;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.reachablity.tools.EdgeElement;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.base.reachablity.tools.RoadGraph;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.sosLogger.SOSLoggerSystem;

public class CreateReachableParts {
	static SOSLoggerSystem log(StandardEntity entity) {
		return entity.getAgent().sosLogger.reachablity_ReachablePart;
	}

	// Morteza2011*****************************************************************
	public static ArrayList<SOSArea> createReachableAreaParts(StandardEntity entity, SOSArea area, ArrayList<SOSArea> blockades) {
		RoadGraph roadGraph = CreateRoadGraph.createRoadGraph(entity, area, blockades);
		ArrayList<Point2D> points = roadGraph.getPoints();
		ArrayList<ArrayList<Pair<Short, Short>>> graph = roadGraph.getGraph();
		ArrayList<SOSArea> reachableParts = new ArrayList<SOSArea>();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		short index;
		while ((index = graphHasElement(graph)) > -1) {
			short current = index, next, number;
			while (true) {
				if (graph.get(current).size() == 0)
					break;
				next = graph.get(current).get(0).first();
				number = graph.get(current).get(0).second();
				graph.get(current).remove(0);
				for (short i = 0; i < graph.get(next).size(); i++) {
					if (graph.get(next).get(i).first() == current) {
						graph.get(next).remove(i);
						break;
					}
				}
				edges.add(Utility.newEdge(points, current, next, number));
				current = next;
			}
			reachableParts.add(new SOSArea(edges, area.getID()));
			edges.clear();
		}
		for (int j = 0; j < reachableParts.size(); j++) {
			for (int i = 0; i < reachableParts.size(); i++) {
				if (i == j)
					continue;
				if (reachableParts.get(j).getShape().contains(reachableParts.get(i).getEdges().get(0).getStartX(), reachableParts.get(i).getEdges().get(0).getStartY())) {
					reachableParts.get(j).addHole(reachableParts.get(i));
					reachableParts.remove(reachableParts.get(i));
					if (j > i)
						j--;
					i--;
				}
			}
		}
		//		log(entity).logln("ID: "+entity.getID());
		//		int i=0;
		//		for (SOSArea rp : reachableParts) {
		//			log(entity).logln("rp"+(i++)+": "+rp.getEdges());
		//		}
		return reachableParts;
	}

	// Morteza2011*****************************************************************
	private static short graphHasElement(ArrayList<ArrayList<Pair<Short, Short>>> graph) {
		for (short i = 0; i < graph.size(); i++) {
			if (graph.get(i).size() > 0) {
				return i;
			}
		}
		return -1;
	}

	// Morteza2011*****************************************************************
	public static ArrayList<ArrayList<EdgeElement>> createReachableEdges(Road area) {
		ArrayList<SOSArea> reachableParts = area.getReachableParts();
		ArrayList<ArrayList<EdgeElement>> reachableEdges = new ArrayList<ArrayList<EdgeElement>>();
		for (short i = 0; i < reachableParts.size(); i++) {
			ArrayList<Edge> reachablePartEdges = new ArrayList<Edge>();
			reachablePartEdges.add(reachableParts.get(i).getEdges().get(reachableParts.get(i).getEdges().size() - 1));
			reachablePartEdges.addAll(reachableParts.get(i).getEdges());
			reachablePartEdges.add(reachablePartEdges.get(1));
			ArrayList<EdgeElement> edgeElemetList = new ArrayList<EdgeElement>();
			for (short j = 1; j < reachablePartEdges.size() - 1; j++) {
				if (reachablePartEdges.get(j).getReachablityIndex() >= 0) {
					if (area.getEdges().get(reachablePartEdges.get(j).getReachablityIndex()).isPassable()) {
						Edge e1 = reachablePartEdges.get(j);
						Edge e2 = reachablePartEdges.get(j - 1);
						Edge e3 = reachablePartEdges.get(j + 1);
						Point2D start = e1.getStart();
						Point2D end = e1.getEnd();
						Point2D mid = e1.getMidPoint();
						if (e2.getReachablityIndex() < -1 || e2.getReachablityIndex() >= 0 && Utility.getAngle(e2, e1) > 30 && !area.getEdges().get(e2.getReachablityIndex()).isPassable()) {
							ArrayList<Point2D> p = Utility.get2PointsAroundAPointOnLine(start, end, start, ReachablityConstants.AGENT_WIDTH / 2);
							if (GeometryTools2D.getDistance(p.get(0), mid) > GeometryTools2D.getDistance(p.get(1), mid))
								start = p.get(0);
							else
								start = p.get(1);
							if (GeometryTools2D.getDistance(start, area.getEdges().get(e1.getReachablityIndex()).getStart()) < ReachablityConstants.AGENT_WIDTH / 2)
								start = area.getEdges().get(e1.getReachablityIndex()).getStart();
						}
						if (e3.getReachablityIndex() < -1 || e3.getReachablityIndex() >= 0 && Utility.getAngle(e1, e3) > 30 && !area.getEdges().get(e3.getReachablityIndex()).isPassable()) {
							ArrayList<Point2D> p = Utility.get2PointsAroundAPointOnLine(start, end, end, ReachablityConstants.AGENT_WIDTH / 2);
							if (GeometryTools2D.getDistance(p.get(0), mid) > GeometryTools2D.getDistance(p.get(1), mid))
								end = p.get(0);
							else
								end = p.get(1);
							if (GeometryTools2D.getDistance(end, area.getEdges().get(e1.getReachablityIndex()).getEnd()) < ReachablityConstants.AGENT_WIDTH / 2)
								end = area.getEdges().get(e1.getReachablityIndex()).getEnd();
						}
						EdgeElement ee = new EdgeElement(area.getEdges().get(e1.getReachablityIndex()), start, end);
						edgeElemetList.add(ee);
					}
				}
			}
			reachableEdges.add(edgeElemetList);
		}
		return reachableEdges;
	}

}
