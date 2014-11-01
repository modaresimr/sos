package sos.police_v2.base;

import rescuecore2.geometry.Point2D;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.worldGraph.WorldGraph;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceConstants;
import sos.police_v2.PoliceUtils;

public class PoliceTaskGraphWeight extends MoveGraphWeight {

	public PoliceTaskGraphWeight(SOSWorldModel model, WorldGraph graph) {
		super(model,graph);
	}

	@Override
	public int getBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return getFoggyBlockWeight(insideArea, wge);//chon nadarim block to precompute
	}

	@Override
	public int getFoggyBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return getOpenWeight(insideArea, wge) * 4;
	}

	@Override
	public int getFoggyOpenWeight(Area insideArea, WorldGraphEdge wge) {
		return getOpenWeight(insideArea, wge);
	}

	@Override
	public int getOpenWeight(Area insideArea, WorldGraphEdge wge) {

		return (wge.getLenght() / PoliceConstants.DISTANCE_UNIT /** jd */
		);
	}

	@Override
	public int getFiryWeight(Building insideArea, WorldGraphEdge wge) {
		return getOpenWeight(insideArea, wge);
	}

	@Override
	public int getWeightToXY(Area destArea, Edge edge, Point2D dst) {
		
		return PoliceUtils.getDistance(edge.getMidPoint().getIntX(),edge.getMidPoint().getIntY(),dst.getIntX(),dst.getIntY());
	}

	@Override
	public int getWeightFromXYToXY(Area area, Point2D start, Point2D dst) {
		return PoliceUtils.getDistance(start.getIntX(),start.getIntY(),dst.getIntX(),dst.getIntY());
	}

	@Override
	public int getWeightXY(Area area, Point2D start, Edge ed) {
		return PoliceUtils.getDistance(ed.getMidPoint().getIntX(),ed.getMidPoint().getIntY(),start.getIntX(),start.getIntY());
	}
	


}
