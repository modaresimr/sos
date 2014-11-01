package sos.tools.decisionMaker.implementations.tasks;

import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.targets.EmptyTarget;

/**
 * @author Salim , reyhaneh
 */

public class RestTask extends SOSTask<SOSITarget> {
	/**
	 * Contains explanation about the problem
	 */
	private String problem;

	public RestTask(EmptyTarget target, int creatinTime, String problem) {
		super(target, creatinTime);
		this.problem = problem;
	}

	@Override
	public SOSITarget getTarget() {
		return target;
	}

	@Override
	public void execute(SOSAgent<? extends Human> agent) throws SOSActionException {
		agent.problemRest(problem);
		
	}

}
