package sos.tools.decisionMaker.implementations.targets;

import sos.base.entities.Area;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;

/**
 *@author Salim
 */
public  class AreaTarget implements SOSITarget{

	private final Area area;

	public AreaTarget(Area area) {
		this.area = area;
	}

	public Area getArea() {
		return area;
	}
	


	
}
