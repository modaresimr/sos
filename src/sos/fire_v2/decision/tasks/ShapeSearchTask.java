package sos.fire_v2.decision.tasks;

import java.util.ArrayList;

import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.move.types.StandardMove;
import sos.base.util.SOSActionException;
import sos.base.util.geom.ShapeInArea;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.targets.ListTarget;

public class ShapeSearchTask extends SOSTask<ListTarget<ShapeInArea>> {

	public ShapeSearchTask(ArrayList<ShapeInArea> searchTask, int time) {
		super(new ListTarget<ShapeInArea>(searchTask), time);
	}

	@Override
	public ListTarget<ShapeInArea> getTarget() {
		return target;
	}

	@Override
	public void execute(SOSAgent<? extends Human> agent) throws SOSActionException {
		agent.move.moveToShape(getTarget(), StandardMove.class);
	}

}
