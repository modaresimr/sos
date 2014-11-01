package sos.ambulance_v2.tools;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.util.Triple;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.worldGraph.WorldGraph;
import sos.tools.Dijkstra;

public class CostFromInMM {
 
//	private final Area src;
	private final SOSWorldModel model;
	private final Dijkstra dijkstra;
	private final DistanceBlockMoveGraphWeightInMM graphweight;
	private boolean finishedDijkstra=false;
	public CostFromInMM(SOSWorldModel model,Area src) {
		this.model = model;
//		this.src = src;

		WorldGraph graph = model.getWorldGraph();
		graphweight=new DistanceBlockMoveGraphWeightInMM(model,graph);
		dijkstra=new Dijkstra(graph.getNumberOfNodes(), model);
		try {
			dijkstra.Run(graph, graphweight, getOutsideNodes(src));
			finishedDijkstra=true;
		} catch (Exception e) {
			finishedDijkstra=false;
		}
		
	}
	
	private SOSLoggerSystem log(){
		return model.sosAgent().sosLogger.agent;
	}
	public long getCostTo(Area dst){
//		log().debug("getPathTo sources=" + src + " ,destination=" + dst);
		if(!finishedDijkstra){
			log().error("BAYAD DOROST BESHE");
			return 10000;
		}
		ArrayList<Triple<Integer, Point2D, Integer>> costs = getOutsideNodesWithCosts(dst);
		Triple<Integer, Point2D, Long> mindst = getMinDestination(costs);
		if(mindst.third()<0)
			log().error(new Error("why it become negetive here???"));
		return mindst.third();
	}
	
	

	public long getCostTo(Pair<? extends Area, Point2D> dst){
//		log().debug("getPathTo sources=" + src + " ,destination=" + dst);
		if(!finishedDijkstra){
			log().error("BAYAD DOROST BESHE");
			return 10000;
		}
		ArrayList<Triple<Integer, Point2D, Integer>> costs = getOutsideNodesWithCosts(dst.first(),dst.second());
		Triple<Integer, Point2D, Long> mindst = getMinDestination(costs);
		if(mindst.third()<0)
			log().error(new Error("why it become negetive here???"));
		return mindst.third();
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

	private ArrayList<Triple<Integer, Point2D, Integer>> getOutsideNodesWithCosts(Area area) {
		ArrayList<Triple<Integer, Point2D, Integer>> result = new ArrayList<Triple<Integer, Point2D, Integer>>(5);
		for (Edge ed : area.getPassableEdges()) {
				result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), area.getPositionPoint(), 1));
		}
		return result;
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
			for (Edge ed : area.getPassableEdges())
				result.add((int) ed.getNodeIndex());
		return result;
	}

}
