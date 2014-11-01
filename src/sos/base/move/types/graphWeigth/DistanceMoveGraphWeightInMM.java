package sos.base.move.types.graphWeigth;

import rescuecore2.geometry.Point2D;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.worldGraph.WorldGraph;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.base.MoveGraphWeight;

public class DistanceMoveGraphWeightInMM extends MoveGraphWeight {

	public DistanceMoveGraphWeightInMM(SOSWorldModel model, WorldGraph graph) {
		super(model,graph);
	}

	/**
	 * getWeight from an edge(ed) of area to x , y
	 * @param destArea
	 * @param ed
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public int getWeightToXY(Area destArea, Edge edge, Point2D dst) {
		
		return (int) dst.distance(edge.getMidPoint());//TODO
	}

	@Override
	public int getWeightFromXYToXY(Area area, Point2D start, Point2D dst) {
		return (int) dst.distance(start);
	}

	@Override
	public int getWeightXY(Area area, Point2D start, Edge ed) {
		return (int) start.distance(ed.getMidPoint());
	}
	
	@Override
	public int getFiryWeight(Building insideArea, WorldGraphEdge wge) {
		return getOpenWeight(insideArea, wge)*100;
		}

	@Override
	public int getBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return getOpenWeight(insideArea, wge);
	}

	@Override
	public int getFoggyBlockWeight(Area insideArea, WorldGraphEdge wge) {
		return getOpenWeight(insideArea, wge);
		}

	@Override
	public int getFoggyOpenWeight(Area insideArea, WorldGraphEdge wge) {
		return getOpenWeight(insideArea, wge);

	}
	@Override
	public int getOpenWeight(Area insideArea, WorldGraphEdge wge) {
		return wge.getLenght() ;
	}


}
