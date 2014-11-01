package sos.tools.decisionMaker.implementations.tasks;

import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.targets.LastRescueTarget;

/**
 *@author Salim , reyhaneh
 */

public class RescueTask extends SOSTask<SOSITarget>{

	public RescueTask(Human human, int creatinTime) {
		super(new LastRescueTarget(human), creatinTime);
	}

	@Override
	public SOSITarget getTarget() {
		return target;
	}

	@Override
	public void execute(SOSAgent<? extends Human> agent) throws SOSActionException {
		((AmbulanceTeamAgent)agent).log().info("######## LAST RESCUE TASK ########");
		((AmbulanceTeamAgent)agent).rescue(((LastRescueTarget)target).getHuman());
	}

}
