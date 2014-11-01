package sos.fire_v2.decision.tasks;

import sos.base.entities.Building;
import sos.base.util.geom.ShapeInArea;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;

public class BuildingTarget implements SOSITarget {
	private  Building building;
	private ShapeInArea position;

	public BuildingTarget(Building building, ShapeInArea position) {
		this.building = building;
		this.position = position;
	}

	public Building getTarget() {
		return building;
	}

	public ShapeInArea getPosition() {
		return position;
	}
}
