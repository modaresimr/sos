package sos.fire_v2.decision.tasks;


import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.targets.EmptyTarget;

/**
 * @author Salim , reyhaneh
 */

public class FireRestTask extends SOSTask<SOSITarget> {
	/**
	 * Contains explanation about the problem
	 */

	public FireRestTask(EmptyTarget target, int creatinTime) {
		super(target, creatinTime);
	}

	@Override
	public SOSITarget getTarget() {
		return target;
	}

	@Override
	public void execute(SOSAgent<? extends Human> agent) throws SOSActionException {
		agent.rest();
		
	}

}
