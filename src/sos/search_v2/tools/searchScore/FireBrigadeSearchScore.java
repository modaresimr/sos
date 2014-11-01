package sos.search_v2.tools.searchScore;

import sos.base.SOSAgent;
import sos.base.entities.Human;

public class FireBrigadeSearchScore extends AgentSearchScore {
	public FireBrigadeSearchScore(SOSAgent<? extends Human> me) {
		super(me);
	}





	@Override
	public int getNoBrokeNessCoef() {
		if (me.getMapInfo().isBigMap())
			return -150;
		if (me.getMapInfo().isMediumMap())
			return -120;
		return -100;
	}

}
