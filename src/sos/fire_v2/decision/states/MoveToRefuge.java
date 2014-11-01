package sos.fire_v2.decision.states;

import java.util.List;

import sos.base.entities.Refuge;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.decision.FireInformationModel;
import sos.fire_v2.decision.tasks.FireRestTask;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;
import sos.tools.decisionMaker.implementations.targets.EmptyTarget;
import sos.tools.decisionMaker.implementations.tasks.StandardMoveToListTask;

public class MoveToRefuge extends SOSIState<FireInformationModel> {
	private SOSLoggerSystem log;
	
	public MoveToRefuge(FireInformationModel infoModel) {
		super(infoModel);
		log = new SOSLoggerSystem(infoModel.self().me(), "MoveToHydrant", true, OutputType.File, true, true);
		infoModel.self().sosLogger.addToAllLogType(log);
	}
	

	@Override
	public SOSTask<?> decide(SOSEventPool eventPool) {
		
		if (((FireBrigadeAgent) infoModel.getAgent()).me().getWater() == 0) {
			moveToRefuge = true;
		}
		if (((FireBrigadeAgent) infoModel.getAgent()).me().getWater() >= AbstractFireBrigadeAgent.maxWater) {
			moveToRefuge = false;
		}

		if (moveToRefuge) {
			if (((FireBrigadeAgent) infoModel.getAgent()).me().getPosition() instanceof Refuge)
				return new FireRestTask(new EmptyTarget(), infoModel.getTime());
			else {
				if (((FireBrigadeAgent) infoModel.getAgent()).model().refuges().size() > 0)
					return new StandardMoveToListTask(infoModel.getModel().refuges(), infoModel.getTime());
			}
		}

		return null;
	}
	private boolean moveToRefuge=false;
		private void moveToRefuge() throws SOSActionException {
		//		if (me().getPositionArea() instanceof Refuge) {
		//			if (me().getWater() < maxWater)
		//				rest();
		//		}
	}

	
	@Override
	public void giveFeedbacks(List feedbacks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void skipped() {
		// TODO Auto-generated method stub

	}

	@Override
	public void overTaken() {
		// TODO Auto-generated method stub
		moveToRefuge = false;
	}

	@Override
	protected void handleEvent(SOSEvent sosEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "MoveToRefuge";
	}

	@Override
	public void taken() {
		super.taken();
		((FireBrigadeAgent) infoModel.getAgent()).FDK.lastState = getName();
	}
}
