package sos.police_v2.base;

import rescuecore2.geometry.Point2D;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.move.MoveConstants;
import sos.base.worldGraph.GraphWeight;
import sos.base.worldGraph.WorldGraph;
import sos.base.worldGraph.WorldGraphEdge;
import sos.tools.GraphEdge;
public abstract class MoveGraphWeight extends GraphWeight {

	private final SOSWorldModel model;

	public MoveGraphWeight(SOSWorldModel model, WorldGraph graph) {
		super((short)graph.getEdgesSize());
		this.model = model;
		updateWeigths();
	}

//	@Override
//	public int getWeight(short index) {
//		GraphEdge ge = model.graphEdges().get(index);
//		return getWeight(ge);
//	}

//	@Override
//	public short getSize() {
//		return (short) model.getWorldGraph().getEdgesSize();
//	}

	public int getWeight(GraphEdge ge) {

		if (ge.haveTraffic())
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;

		if (ge instanceof WorldGraphEdge) {
			WorldGraphEdge wge = (WorldGraphEdge) ge;
			Area insideArea = model().areas().get(wge.getInsideAreaIndex());
			if (insideArea instanceof Building) {
				return Math.max(1, getFiryWeight((Building) insideArea, wge));
			}
			switch (ge.getState()) {
			case FoggyOpen:
				return Math.max(1, getFoggyOpenWeight(insideArea, wge));
			case FoggyBlock:
				return Math.max(1, getFoggyBlockWeight(insideArea, wge));
			case Open:
				return Math.max(1, getOpenWeight(insideArea, wge));
			case Block:
				return Math.max(1, getBlockWeight(insideArea, wge));
			default:
				System.err.println("Error...Unknown graph edge state");
//				return Math.max(1, ge.getLenght() / 333);
			}
		}
		return 1;

	}

	/**
	 * if a building is on fire the wge should get cost
	 * 
	 * @param insideArea
	 * @param wge
	 * @return
	 */
	public abstract int getFiryWeight(Building insideArea, WorldGraphEdge wge);

	/**
	 * getWeight from an edge(ed) of area to x , y
	 * 
	 * @param destArea
	 * @param ed
	 * @param x
	 * @param y
	 * @return
	 */
	public abstract int getWeightToXY(Area destArea, Edge edge, Point2D dst);
	/**
	 * getWeight from an x,y of area to x2 , y2
	 * 
	 * @param destArea
	 * @param ed
	 * @param x
	 * @param y
	 * @return
	 */

	public abstract int getWeightFromXYToXY(Area area, Point2D start, Point2D dst) ;

	public abstract int getBlockWeight(Area insideArea, WorldGraphEdge wge);

	public abstract int getOpenWeight(Area insideArea, WorldGraphEdge wge);

	public abstract int getFoggyBlockWeight(Area insideArea, WorldGraphEdge wge);

	public abstract int getFoggyOpenWeight(Area insideArea, WorldGraphEdge wge);

	public abstract int getWeightXY(Area area, Point2D start, Edge ed);
	
	public void updateWeigths() {
		for (short i = 0; i < getSize(); ++i) {
			GraphEdge ge = model().graphEdges().get(i);
			setWeight(i, getWeight(ge));

		}
	}

	public SOSWorldModel model() {
		return model;
	}

	public int getUnit(WorldGraphEdge wge){
		return wge.getLenght() /MoveConstants.DIVISION_UNIT;
	}

	
}
