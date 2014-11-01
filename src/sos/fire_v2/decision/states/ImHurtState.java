package sos.fire_v2.decision.states;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.decision.FireInformationModel;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;
import sos.tools.decisionMaker.implementations.tasks.StandardMoveToListTask;

public class ImHurtState extends SOSIState<FireInformationModel> {

	public ImHurtState(FireInformationModel infoModel) {
		super(infoModel);
	}

	@Override
	public SOSTask<?> decide(SOSEventPool eventPool) {
		SOSLoggerSystem log = infoModel.getAgent().sosLogger.agent;
		log.info("[DamageState] is acting");
		if (infoModel.getModel().refuges().size() < 0) {
			log.debug("No Refuge found:(");
			return null;
		}
		if (amIGoingToDieSoon(4)) {
			log.warn("I'm going to die...  :(( ");
			Collection<Building> buildings = infoModel.getModel().getObjectsInRange((int) infoModel.getAgent().me().getPositionPoint().getX(), (int) infoModel.getAgent().me().getPositionPoint().getY(), 70000, Building.class);
			return new StandardMoveToListTask(new ArrayList<Area>(buildings), infoModel.getTime());
		}
		if (amIGoingToDieSoon(30)) {
			if (infoModel.getModel().refuges().size() > 0)
				return new StandardMoveToListTask(infoModel.getModel().refuges(), infoModel.getTime());
			else
				return null; //TODO vaghan chikar konim????
		}
		if (((Human) infoModel.getAgent().me()).getDamage() != 0) {
			if (((Human) infoModel.getAgent().me()).getDamage() > 50) {
				if (infoModel.getModel().refuges().size() > 0)
					return new StandardMoveToListTask(infoModel.getModel().refuges(), infoModel.getTime());
			}
			return null;
		}
		return null;
	}

	public boolean amIGoingToDieSoon(int deathTime) {
		if (((Human) infoModel.getAgent().me()).getDamage() == 0)
			return false;
		if (SimpleDeathTime.getEasyLifeTime(((Human) infoModel.getAgent().me()).getHP(), ((Human) infoModel.getAgent().me()).getDamage(), infoModel.getModel().time()) < deathTime)
			return true;
		return false;
	}

	@Override
	public void giveFeedbacks(List feedbacks) {
	}

	@Override
	public void skipped() {
	}

	@Override
	public void overTaken() {
	}

	@Override
	protected void handleEvent(SOSEvent sosEvent) {
	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
	}

	@Override
	public String getName() {
		return "ImHurtState";
	}

	@Override
	public void taken() {
		super.taken();
		((FireBrigadeAgent)infoModel.getAgent()).FDK.lastState=getName();
	}
}
