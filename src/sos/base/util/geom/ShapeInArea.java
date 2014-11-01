package sos.base.util.geom;

import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;


public class ShapeInArea extends SOSShape{
	
	private static final long serialVersionUID = 1L;
	
	private final short areaIndex;
	public double positioningScore=0;
	public ShapeInArea(int[] apexes,Area area) {
		super(apexes);
		this.areaIndex = area.getAreaIndex();
	}
	
	public Area getArea(SOSWorldModel model) {
		return model.areas().get(areaIndex);
	}
	public Area getArea() {
		return SOSAgent.currentAgent().model().areas().get(areaIndex);
	}

	@Override
	public String toString() {
		return "[Shape in Area("+areaIndex+")x="+getCenterX()+"y="+getCenterY()+"]";
	}
}
