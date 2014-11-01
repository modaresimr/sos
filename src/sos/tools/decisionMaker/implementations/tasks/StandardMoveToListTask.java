package sos.tools.decisionMaker.implementations.tasks;

import java.util.List;

import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Human;
import sos.base.move.types.StandardMove;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.commands.SOSITarget;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.targets.ListTarget;

/**
 * A simple task to move to a list of areas using StandardMove
 * 
 * @author Salim
 */
public class StandardMoveToListTask extends SOSTask<ListTarget<? extends Area>> {

	public StandardMoveToListTask(List<? extends Area> targets, int creatinTime) {
		super(new ListTarget<Area>(targets), creatinTime);
	}

	@Override
	public SOSITarget getTarget() {
		return target;
	}

	@Override
	public void execute(SOSAgent<? extends Human> agent) throws SOSActionException {
		agent.move.move(target, StandardMove.class);

	}

}
