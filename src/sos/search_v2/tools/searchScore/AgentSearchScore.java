package sos.search_v2.tools.searchScore;

import sos.base.SOSAgent;
import sos.base.entities.Human;

public abstract class AgentSearchScore {
	protected final SOSAgent<? extends Human> me;
	public static final int SEARCH_FILLTER_SCORE = -9999999;

	public AgentSearchScore(SOSAgent<? extends Human> me) {
		this.me = me;
	}

	public int getVisibleAreaCoef() {
		if (me.getMapInfo().isBigMap())
			return -40;
		if (me.getMapInfo().isMediumMap())
			return -25;
		return -10;
	}

	public int getHearingDistanceCoef() {
		if (me.getMapInfo().isBigMap())
			return -30;
		if (me.getMapInfo().isMediumMap())
			return -20;
		return -10;
	}

	public int getHearCoef() {
		if (me.getMapInfo().isBigMap())
			return 1000;
		if (me.getMapInfo().isMediumMap())
			return 700;
		return 400;
	}

	public int getBrokeNessCoef() {
		if (me.getMapInfo().isBigMap())
			return 20;
		if (me.getMapInfo().isMediumMap())
			return 15;
		return 10;
	}

	public int getBuildingReachabelityCoef() {
		if (me.getMapInfo().isBigMap())
			return -240;
		if (me.getMapInfo().isMediumMap())
			return -170;
		return -127;
	}

	public int getAreaReachabelityCoef() {
		if (me.getMapInfo().isBigMap())
			return 40;
		if (me.getMapInfo().isMediumMap())
			return 30;
		return 15;
	}

	public int getInSideVisibleBuilding() {
		if (me.getMapInfo().isBigMap())
			return -200;
		if (me.getMapInfo().isMediumMap())
			return -170;
		return -127;
	}

	public int getLastUpdatedCoef() {
		if (me.getMapInfo().isBigMap())
			return 120;
		if (me.getMapInfo().isMediumMap())
			return 90;
		return 60;
	}

	public int getUpdatedNeighbourCoef() {
		if (me.getMapInfo().isBigMap())
			return -20;
		if (me.getMapInfo().isMediumMap())
			return -12;
		return -6;
	}

	public abstract int getNoBrokeNessCoef();

	public int getCostCoef() {
		if (me.getMapInfo().isBigMap())
			return -2100;
		if (me.getMapInfo().isMediumMap())
			return -1600;
		return -800;
	}

	public int getDistanceToCenterOfClusterCoef() {
		if (me.getMapInfo().isBigMap())
			return -400;
		if (me.getMapInfo().isMediumMap())
			return -300;
		return -250;
	}

	public int getSpecialForFireCoef() {
		if (me.getMapInfo().isBigMap())
			return 600;
		return 400;
	}

	public SOSAgent<? extends Human> getMe() {
		return me;
	}

	public int getLastTargetCoef() {
		if (me.getMapInfo().isBigMap())
			return 400;
		else if (me.getMapInfo().isMediumMap()) {
			return 250;
		} else {
			return 150;
		}
	}

	public int getCivilianUpdateCoef() {
		return 20;
	}

	public int getHasBeenSeenCoef() {
		return -200;
	}

	public int getNotBeenSeenCoef() {
		return 100;
	}

	public int getMyClusterCoef() {
		return 90;
	}

	public int getLowTempertureEstimatedFiteSiteCoef() {
		return 30;
	}

	public int getHighTempertureEstimatedFiteSiteCoef() {
		return -500;
	}

	public int getOneCycleMoveCoef() {
		if (me.getMapInfo().isBigMap())
			return 500;
		else if (me.getMapInfo().isMediumMap()) {
			return 200;
		} else {
			return 100;
		}
	}

	public double getDistanceToFunctionCoef() {
		if (me.getMapInfo().isBigMap())
			return -2100;
		else if (me.getMapInfo().isMediumMap()) {
			return -1050;
		} else {
			return -600;
		}
	}

	public double getIsSearchedForFireAndCivilian() {
		if (me.getMapInfo().isBigMap())
			return -900;
		else if (me.getMapInfo().isMediumMap())
			return -700;
		else
			return -500;
	}

	public double getNoCivilianCoef() {
		return -400;
	}
}
