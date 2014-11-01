package sos.fire_v2.decision.tasks;

import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.util.SOSActionException;
import sos.base.util.geom.ShapeInArea;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;
import sos.tools.decisionMaker.definitions.commands.SOSTask;

public class ExtinguishTask extends SOSTask<SOSITarget> {

	public ExtinguishTask(Building target, ShapeInArea postition, int creatinTime) {
		super(new BuildingTarget(target, postition), creatinTime);
	}

	@Override
	public BuildingTarget getTarget() {
		return (BuildingTarget) target;
	}

	@Override
	public void execute(SOSAgent<? extends Human> agent) throws SOSActionException {
		Building b = ((BuildingTarget) target).getTarget();
		((FireBrigadeAgent) agent).positioning.newPsitioning(b);
		((FireBrigadeAgent) agent).extinguish(b, getEnoughWater(b, (FireBrigadeAgent) agent));
	}

	public int getEnoughWater(Building b, FireBrigadeAgent agent) {
		return Math.min(AbstractFireBrigadeAgent.maxPower, agent.me().getWater());
	}

}
