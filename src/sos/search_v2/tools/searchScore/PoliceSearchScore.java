package sos.search_v2.tools.searchScore;

import sos.base.SOSAgent;
import sos.base.entities.Human;

public class PoliceSearchScore extends AgentSearchScore {

	public PoliceSearchScore(SOSAgent<? extends Human> me) {
		super(me);
	}

	@Override
	public int getBuildingReachabelityCoef() {
		if (me.getMapInfo().isBigMap())
			return 80;
		if (me.getMapInfo().isMediumMap())
			return 50;
		return 27;
	}

	@Override
	public int getAreaReachabelityCoef() {
		if (me.getMapInfo().isBigMap())
			return 20;
		if (me.getMapInfo().isMediumMap())
			return 10;
		return 0;
	}

	@Override
	public int getNoBrokeNessCoef() {
		if (me.getMapInfo().isBigMap())
			return 100;
		if (me.getMapInfo().isMediumMap())
			return 80;
		return 50;
	}

}
