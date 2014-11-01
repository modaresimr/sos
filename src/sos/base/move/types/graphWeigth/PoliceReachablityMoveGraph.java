package sos.base.move.types.graphWeigth;

import rescuecore2.geometry.Point2D;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.move.MoveConstants;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.worldGraph.WorldGraph;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.base.MoveGraphWeight;

public class PoliceReachablityMoveGraph extends MoveGraphWeight {
	public PoliceReachablityMoveGraph(SOSWorldModel model, WorldGraph graph) {
		super(model,graph);
	}

	/**
	 * getWeight from an edge(ed) of area to x , y
	 * 
	 * @param destArea
	 * @param ed
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public int getWeightToXY(Area area, Edge edge, Point2D dst) {
		int dstlength = ((int) edge.getMidPoint().distance(dst)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building)
			return dstlength;
	
		if(!area.isBlockadesDefined())
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		
		ReachablityState rs = Reachablity.isReachable((Road) area, edge,dst);
		switch (rs) {
		case Open:
			return dstlength;

		case Close:
		case FoggyClose:
		case FoggyOpen:
		default:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		}

	}

	@Override
	public int getWeightFromXYToXY(Area area, Point2D start,Point2D end) {
		int dstlength = ((int)start.distance(end)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building)
			return dstlength;
		if(!area.isBlockadesDefined())
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		
		ReachablityState rs = Reachablity.isReachable((Road) area, start,end);
		switch (rs) {
		case Open:
			return dstlength;
		case Close:
		case FoggyClose:
		case FoggyOpen:
		default:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		}
	}

	@Override
	public int getFiryWeight(Building insideArea, WorldGraphEdge wge) {
		return getUnit(wge);
	}

	@Override
	public int getBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
	}

	@Override
	public int getFoggyBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return getBlockWeight(insideArea, wge);
	}

	@Override
	public int getFoggyOpenWeight(Area insideArea, WorldGraphEdge wge) {
		return getBlockWeight(insideArea, wge);
	}

	@Override
	public int getOpenWeight(Area insideArea, WorldGraphEdge wge) {
		return getUnit(wge);
	}

	@Override
	public int getWeightXY(Area area, Point2D start, Edge ed) {
		int dstlength = ((int) ed.getMidPoint().distance(start)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building)
			return dstlength;
	
		if(!area.isBlockadesDefined())
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		
		ReachablityState rs = Reachablity.isReachable((Road) area, start,ed);
		switch (rs) {
		case Open:
			return dstlength;

		case Close:
		case FoggyClose:
		case FoggyOpen:
		default:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		}

	}

}
