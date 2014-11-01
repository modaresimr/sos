package sos.tools.decisionMaker;

import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.tools.decisionMaker.definitions.SOSInformationModel;

public class SOSPlatoonAgentInformationModel extends SOSInformationModel {

	public SOSPlatoonAgentInformationModel(SOSAgent<? extends StandardEntity> agent) {
		super(agent);
	}

	public int getDamage() {
		return getHumanEntity().getDamage();
	}

	public int getHP() {
		return getHumanEntity().getHP();
	}

	public int getBurriedness() {
		return getHumanEntity().getBuriedness();
	}

	public Human getHumanEntity() {
		return (Human) getEntity();
	}

}
