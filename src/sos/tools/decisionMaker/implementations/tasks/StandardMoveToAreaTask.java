package sos.tools.decisionMaker.implementations.tasks;

import java.util.Collections;

import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.move.types.StandardMove;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.targets.AreaTarget;
/**
 * 
 * @author Salim
 *
 */
public class StandardMoveToAreaTask extends SOSTask<AreaTarget>{

	public StandardMoveToAreaTask(AreaTarget target, int creatinTime) {
		super(target, creatinTime);
	}

	@Override
	public SOSITarget getTarget() {
		return target;
	}

	@Override
	public  void execute(SOSAgent<? extends Human> agent) throws SOSActionException{
		agent.move.move(Collections.singleton(target.getArea()), StandardMove.class);
	}

}
