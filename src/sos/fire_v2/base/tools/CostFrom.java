package sos.fire_v2.base.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.move.MoveConstants;
import sos.base.util.Triple;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.worldGraph.WorldGraph;
import sos.tools.Dijkstra;

public class CostFrom {
 
	private final Collection<?extends Area> src;
	private final SOSWorldModel model;
	private final Dijkstra dijkstra;
	private final FireMoveGraphWeight graphweight;
	private boolean finishedDijkstra=false;
	public CostFrom(SOSWorldModel model,Collection<?extends Area> src) {
		this.model = model;
		this.src = src;

		TreeSet<Integer> outsideNodes = new TreeSet<Integer>();
		for (Area s : src)
			outsideNodes.addAll(getOutsideNodes(s, s.getX(), s.getY()));
		log().debug("outsideNodes=" + outsideNodes);
		WorldGraph graph = model.getWorldGraph();
		graphweight=new FireMoveGraphWeight(model,graph);
		dijkstra=new Dijkstra(graph.getNumberOfNodes(), model);
		try {
			dijkstra.Run(graph, graphweight, new ArrayList<Integer>(outsideNodes));
			finishedDijkstra=true;
		} catch (Exception e) {
			finishedDijkstra=false;
		}
		
	}
	
	private SOSLoggerSystem log(){
		return model.sosAgent().sosLogger.agent;
	}
	public long getCostTo(Pair<? extends Area, Point2D> dst){
		log().debug("getPathTo sources=" + src + " ,destination=" + dst);
		if(!finishedDijkstra){
			log().error("BAYAD DOROST BESHE");
			return 10000;
		}
		ArrayList<Triple<Integer, Point2D, Integer>> costs = getOutsideNodesWithCosts(dst.first(),dst.second());
		Triple<Integer, Point2D, Long> mindst = getMinDestination(costs);
		if(mindst.third()<0)
			log().error(new Error("why it become negetive here???"));
		return mindst.third()/MoveConstants.DIVISION_UNIT_FOR_GET;
	}
	

	private Triple<Integer, Point2D, Long> getMinDestination(ArrayList<Triple<Integer, Point2D, Integer>> outsideNodesCosts) {
		long min = Long.MAX_VALUE;
		Triple<Integer, Point2D, Integer> minNode = null;
		for (Triple<Integer, Point2D, Integer> triple : outsideNodesCosts) {
			if (dijkstra.getWeight(triple.first()) + triple.third() < min) {
				min = (dijkstra.getWeight(triple.first()) + triple.third());
				minNode = triple;
			}
		}
		return new Triple<Integer, Point2D, Long>(minNode.first(), minNode.second(), min);
	}


	private ArrayList<Triple<Integer, Point2D, Integer>> getOutsideNodesWithCosts(Area area, Point2D point) {
		ArrayList<Triple<Integer, Point2D, Integer>> result = new ArrayList<Triple<Integer, Point2D, Integer>>(5);
		for (Edge ed : area.getPassableEdges()) {
			if (area instanceof Building) {
				result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), point, graphweight.getWeightToXY(area, ed, point)));
				//SOSGeometryTools.distance((int) p.getX(), (int) p.getY(), sosShape.getCenterX(), sosShape.getCenterY()) / DIVISION_UNIT));
			} else { // area is a road
				int minCostToReachableAreaFromThisEdge = graphweight.getWeightToXY(area, ed, point);
				result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), point, minCostToReachableAreaFromThisEdge));

			}
		}

		if (result.isEmpty()) { // special case = my policy is if there is no any open way we consider all of blocked entrances
			for (Edge ed : area.getPassableEdges()) {
				result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), point, graphweight.getWeightToXY(area, ed, point)));
				//SOSGeometryTools.distance((int) p.getX(), (int) p.getY(), sosShape.getCenterX(), sosShape.getCenterY()) / DIVISION_UNIT)
			}
		}
		return result;

	}

	protected ArrayList<Integer> getOutsideNodes(Area area) {
		return getOutsideNodes(area, area.getX(), area.getY());
	}

	protected ArrayList<Integer> getOutsideNodes(Area area, int x, int y) {
		ArrayList<Integer> result = new ArrayList<Integer>(5);
//		boolean isInArea = false, flag = false;
//		for (Edge ed : area.getPassableEdges()) {
//			if (area instanceof Building)
//				result.add((int) ed.getNodeIndex());
//			else { // area is a road
//				ReachablityState st = Reachablity.isReachable((Road) area, new Point2D(x, y), ed);
//				if (st != ReachablityState.Close) {
//					result.add((int) ed.getNodeIndex());
//				} else {
//					if (!flag) {
//						flag = true;
//						for (SOSArea ar : ((Road) area).getReachableParts())
//							if (ar.getShape().contains(x, y)) {
//								isInArea = true;
//								break;
//							}
//						log().logln("agent is not reachable and point containing in any reachable part is " + isInArea);
//					}
//					if (!isInArea) {
//						if (model.me() instanceof Human && ((Human) model.me()).getPositionArea() instanceof Road && ((Road) ((Human) model.me()).getPositionArea()).isBlockadesDefined()) {
//							boolean fg = false;
//							ArrayList<Blockade> blk = (ArrayList<Blockade>) ((Road) ((Human) model.me()).getPositionArea()).getBlockades();
//							for (Blockade bk : blk) {
//								if (bk.getShape().contains(((Human) model.me()).getPositionPoint().toGeomPoint())) {
//									fg = true;
//									break;
//								}
//							}
//							if (!fg)
//								result.add((int) ed.getNodeIndex());
//						} else if (!(model.me() instanceof PoliceForce))
//							result.add((int) ed.getNodeIndex());
//					}
//				}
//			}
//		}
		if (result.isEmpty()) { // special case = my policy is if there is no any open way we consider all of blocked entrances
			log().logln("special case = my policy is if there is no any open way we consider all of blocked entrances");
			for (Edge ed : area.getPassableEdges())
				result.add((int) ed.getNodeIndex());
		}
		return result;
	}

}
