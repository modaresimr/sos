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
import sos.fire_v2.base.worldmodel.FireWorldModel;
import sos.police_v2.base.MoveGraphWeight;

public class SearchMoveGraphWeight extends MoveGraphWeight {

	public SearchMoveGraphWeight(SOSWorldModel model, WorldGraph graph) {
		super(model,graph);

	}

	@Override
	public int getFiryWeight(Building insideArea, WorldGraphEdge wge) {
		if(model() instanceof FireWorldModel)
			return getFoggyBlockWeight(insideArea, wge)*10;
		
		return getFoggyBlockWeight(insideArea, wge)*100;
	}

	@Override
	public int getWeightToXY(Area area, Edge edge, Point2D dst) {
		int dstlength = ((int) edge.getMidPoint().distance(dst)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building||!area.isBlockadesDefined())
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, edge,dst);
		switch (rs) {
		case Close:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
//			return dstlength * 2;
		case FoggyOpen:
		case Open:
		default:
			return dstlength;
		}

	}
	@Override
	public int getWeightXY(Area area, Point2D start, Edge ed) {
		int dstlength = ((int) ed.getMidPoint().distance(start)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building||!area.isBlockadesDefined())
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, start,ed);
		switch (rs) {
		case Close:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
//			return dstlength * 2;
		case FoggyOpen:
		case Open:
		default:
			return dstlength;
		}

	}
	@Override
	public int getWeightFromXYToXY(Area area, Point2D start,Point2D end) {
		int dstlength = ((int)start.distance(end)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building||!area.isBlockadesDefined())
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, start,end);
		switch (rs) {
		case Close:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
//			return dstlength * 2;
		case FoggyOpen:
		case Open:
		default:
			return dstlength;
		}
	}

	@Override
	public int getBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
	}

	@Override
	public int getOpenWeight(Area insideArea, WorldGraphEdge wge) {
//		if (insideArea.getSOSGroundArea() < PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)
//			return getUnit(wge) * 10;

		return getUnit(wge);
	}

	@Override
	public int getFoggyBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return getFoggyOpenWeight(insideArea, wge);
	}

	@Override
	public int getFoggyOpenWeight(Area insideArea, WorldGraphEdge wge) {
		return getOpenWeight(insideArea, wge);
	}

	

}
