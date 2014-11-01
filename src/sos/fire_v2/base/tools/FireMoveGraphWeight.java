package sos.fire_v2.base.tools;

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

public class FireMoveGraphWeight extends MoveGraphWeight {

	public FireMoveGraphWeight(SOSWorldModel model, WorldGraph graph) {
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

		int dstlength = ((int) dst.distance(edge.getMidPoint()))/ MoveConstants.DIVISION_UNIT;
		if (area instanceof Building||!area.isBlockadesDefined())
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, edge, dst);
		switch (rs) {
		case Close:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
			return dstlength * 5;
		case FoggyOpen:
			return dstlength * 2;
		default:
			return dstlength;
		}
	}
	@Override
	public int getWeightXY(Area area, Point2D start, Edge ed) {
		int dstlength = ((int) start.distance(ed.getMidPoint()))/ MoveConstants.DIVISION_UNIT;
		if (area instanceof Building||!area.isBlockadesDefined())
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, start,ed);
		switch (rs) {
		case Close:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
			return dstlength * 5;
		case FoggyOpen:
			return dstlength * 2;
		default:
			return dstlength;
		}
	}

	@Override
	public int getWeightFromXYToXY(Area area, Point2D start,Point2D end) {
		int dstlength = ((int)start.distance(end)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building||!area.isBlockadesDefined())
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area,start,end);
		switch (rs) {
		case Close:
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
			return dstlength * 5;
		case FoggyOpen:
			return dstlength * 2;
		default:
			return dstlength;
		}
	}

	@Override
	public int getFiryWeight(Building insideArea, WorldGraphEdge wge) {
		return getFoggyOpenWeight(insideArea, wge);
	}

	@Override
	public int getBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
	}

	@Override
	public int getFoggyBlockWeight(Area insideArea, WorldGraphEdge wge) {
//		if (((FireBrigadeAgent) model().sosAgent()).targetFireZone() != null) {
//			SOSFireZone fz = ((FireBrigadeAgent) model().sosAgent()).targetFireZone().generalFireZone();
//			if (fz != null)
//				if (fz.convexHull.isInConvex(fz.convex, new Point(insideArea.getX(), insideArea.getY())))
//					//				return (wge.getLenght() / MoveConstants.DIVISION_UNIT) * 13;
//					return MoveConstants.UNREACHABLE_COST;
//		}
//		//		return MoveConstants.UNREACHABLE_COST;
//		return (wge.getLenght() / MoveConstants.DIVISION_UNIT) * 5;
		return (wge.getLenght() / MoveConstants.DIVISION_UNIT) * 5;
	}

	@Override
	public int getFoggyOpenWeight(Area insideArea, WorldGraphEdge wge) {
//		if (((FireBrigadeAgent) model().sosAgent()).targetFireZone() != null) {
//			SOSFireZone fz = ((FireBrigadeAgent) model().sosAgent()).targetFireZone().generalFireZone();
//			if (fz != null)
//				if (fz.convexHull.isInConvex(fz.convex, new Point(insideArea.getX(), insideArea.getY())))
//					//				return (wge.getLenght() / MoveConstants.DIVISION_UNIT) * 10;
//					return MoveConstants.UNREACHABLE_COST;
//		}
//		//		return MoveConstants.UNREACHABLE_COST;
//		return (wge.getLenght() / MoveConstants.DIVISION_UNIT) * 2;
		return (wge.getLenght() / MoveConstants.DIVISION_UNIT) * 2;
	}

	@Override
	public int getOpenWeight(Area insideArea, WorldGraphEdge wge) {
//		if (((FireBrigadeAgent) model().sosAgent()).targetFireZone() != null) {
//			SOSFireZone fz = ((FireBrigadeAgent) model().sosAgent()).targetFireZone().generalFireZone();
//			if (fz != null)
//				if (fz.convexHull.isInConvex(fz.convex, new Point(insideArea.getX(), insideArea.getY())))
//					//				return (wge.getLenght() / MoveConstants.DIVISION_UNIT) * 8;
//					return MoveConstants.UNREACHABLE_COST;
//		}
//		//		return MoveConstants.UNREACHABLE_COST;
//		return (wge.getLenght() / MoveConstants.DIVISION_UNIT);
		if(insideArea.getSOSGroundArea()<4000000)
			return getBlockWeight(insideArea, wge);

		return (wge.getLenght() / MoveConstants.DIVISION_UNIT);
	}

//	@Override
//	public FireWorldModel model() {
//		return (FireWorldModel) super.model();
//	}


}
