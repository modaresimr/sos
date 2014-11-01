package sos.police_v2.base;

import rescuecore2.geometry.Point2D;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.worldGraph.WorldGraph;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceConstants;

public class PoliceMoveGraphWeight extends MoveGraphWeight {

	public PoliceMoveGraphWeight(SOSWorldModel model, WorldGraph graph) {
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
		return (getUnit(wge)*50);
	}

	@Override
	public int getBlockWeight(Area insideArea, WorldGraphEdge wge) {
//		if(model.time()<3) return (ge.getLenght() / 10);
//		if(ge instanceof WorldGraphEdge)
//		return (int) ((ge.getLenght() / 10)/(model.areas().get(((WorldGraphEdge)(ge)).getInsideAreaIndex()).policeArea.numberOfReachableTask*(1.5))) ;
		return (int) (getUnit(wge)*4.5);//300
	}

	@Override
	public int getFoggyBlockWeight(Area insideArea, WorldGraphEdge wge) {
//		if(model.time()<3) return (ge.getLenght() / 33);
//		if(ge instanceof WorldGraphEdge)
//		return (int) ((ge.getLenght() / 33)/(model.areas().get(((WorldGraphEdge)(ge)).getInsideAreaIndex()).policeArea.numberOfReachableTask*(1.5))) ;
		if(insideArea.getSOSGroundArea()< PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)//baraye inke tarjihan az road haye koochiki ke intrance khoone ha hastan nare
			return getBlockWeight(insideArea, wge);
		return (int) (getUnit(wge)*3.5);//400
	}

	@Override
	public int getFoggyOpenWeight(Area insideArea, WorldGraphEdge wge) {
//		if(model.time()<3) return (ge.getLenght() / 100);
//		if(ge instanceof WorldGraphEdge)
//		return (int) ((ge.getLenght() / 100)/(model.areas().get(((WorldGraphEdge)(ge)).getInsideAreaIndex()).policeArea.numberOfReachableTask*(1.5))) ;
		if(insideArea.getSOSGroundArea()<PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)//baraye inke tarjihan az road haye koochiki ke intrance khoone ha hastan nare
			return getBlockWeight(insideArea, wge);
		
		return (int) (getUnit(wge)*1.5);//700

	}
	@Override
	public int getOpenWeight(Area insideArea, WorldGraphEdge wge) {
//		if(model.time()<=3) return (ge.getLenght() / 1000);
//		if(ge instanceof WorldGraphEdge)
//		return (int) ((ge.getLenght() / 1000)/(model.areas().get(((WorldGraphEdge)(ge)).getInsideAreaIndex()).policeArea.numberOfReachableTask*(1.5))) ;
		if(insideArea.getSOSGroundArea()<PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)//baraye inke tarjihan az road haye koochiki ke intrance khoone ha hastan nare
			return getBlockWeight(insideArea, wge);
		return getUnit(wge);
	}
}
