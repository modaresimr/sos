package sos.search_v2.worldModel;

import java.util.HashMap;

import rescuecore2.standard.entities.StandardEntityConstants.Fieryness;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.tools.searchScore.AgentSearchScore;

/**
 * @author Yoosef Golshahi
 */
public class SearchBuilding {
	private Building realBuilding = null;
	private double score;
	/**/private int lastFieryness = 0;//Salim
	public static int VALID_FIRE_SEARCH_PERIOD = 40; //FIXME
	HashMap<Civilian, Float> civ_prob = new HashMap<Civilian, Float>();

	public SearchBuilding(Building b) {
		this.realBuilding = b;
	}

	public Building getRealBuilding() {
		return realBuilding;
	}

	public boolean isHasBeenSeen() {
		return realBuilding.isSearchedForCivilian();
	}

	//////////////////////Priority///////////////////////////////////
	//	private float civProbability = 0;
	public boolean tar = false;
	private boolean specialForFire;
	private int validCivilianCountInLowCom = 0;//Ali
	private boolean isReallyUnReachableInLowCom = true;
	private int timeInLowComValidCivilianCount = 0;
	public StringBuilder reason = new StringBuilder();
	private boolean hasBeenSeenBySelf;

	public void addCivProbability(Civilian civilian, double score) {
		if (isHasBeenSeen()) {
			//			civProbability = 0;
			civ_prob.clear();
			return;
		}
		civ_prob.put(civilian, (float) score);
		float old = 0;
		if (civ_prob.containsKey(civilian))
			old += civ_prob.get(civilian);
		civ_prob.put(civilian, old + (float) score);

		//		civProbability += score;
		//
		//		if (isHasBeenSeen()) {
		//			civProbability = 0;
		//			return;
		//		}

	}

	public float getCivProbability() {
		if (isHasBeenSeen()) {
			//			civProbability = 0;
			civ_prob.clear();
			return 0;
		}
		float sum = 0;
		for (Civilian civ : civ_prob.keySet()) {
			if (civ.isUnkonwnCivilian())
				sum += civ_prob.get(civ);
		}
		return sum;
		//		if (isHasBeenSeen())
		//			civProbability = 0;
		//		return civProbability;
	}

	public float getNormalizedCivProbability() {
		if (getCivProbability() > 1)
			return 1;
		return getCivProbability();
	}

	public double getScore() {
		return score;
	}

	public void addScore(String reason, double score) {
		this.reason.append(reason + "  =  " + score + "\n");
		this.score += score;

	}

	public void setScore(double score, String reason) {
		this.reason.append(reason + "  =  " + score + "\n");
		this.score = score;

	}

	public void setScore(int score) {
		this.score = score;
		reason = new StringBuilder();
		tar = false;
	}

	public void setTarget(int time) {
		tar = true;
		reason.append("set as target time:" + time);
	}

	public int isSpecialForFire() {
		return getRealBuilding().getValuSpecialForFire();
	}

	@Override
	public String toString() {
		return "SearchBuilding[" + realBuilding.getID() + "]";
	}

	//ali
	public void setValidCivilianCountInLowCom(int validCivilianCountInLowCom, boolean isReallyUnReachable, int timeInLowComValidCivilianCount) {
		this.validCivilianCountInLowCom = validCivilianCountInLowCom;
		this.isReallyUnReachableInLowCom = isReallyUnReachable;
		this.timeInLowComValidCivilianCount = timeInLowComValidCivilianCount;
	}

	//ali
	public int getTimeInLowComValidCivilianCount() {
		return timeInLowComValidCivilianCount;
	}

	//ali
	public boolean isReallyUnReachableInLowCom(boolean checkAgain) {
		if (!isReallyUnReachableInLowCom)
			return false;

		if (checkAgain)
			return isReallyUnReachableInLowCom = realBuilding.model().sosAgent().move.isReallyUnreachableXYPolice(realBuilding.getPositionPair());

		return isReallyUnReachableInLowCom;
	}

	//ali
	public int getValidCivilianCountInLowCom() {
		return validCivilianCountInLowCom;
	}

	//Salim
	public int getLastFieryness() {
		return lastFieryness;
	}

	//Salim
	public void setLastFieryness() {
		if (getRealBuilding().isFierynessDefined())
			lastFieryness = getRealBuilding().getFieryness();
	}

	//Salim
	public boolean wasLastBurning() {
		return getRealBuilding().isFierynessDefined() && lastFieryness > 0 && lastFieryness < 4;
	}

	public boolean isSearchedForFire() { //Salim
		if (!isHasBeenSeen())
			return false;
		if (lastFieryness != realBuilding.getFieryness())
			return false;
		if (Math.max(realBuilding.getLastMsgTime(), realBuilding.getLastSenseTime()) > VALID_FIRE_SEARCH_PERIOD)
			return false;
		return true;
	}

	public boolean isSearchedFrom(int time) {
		if (getRealBuilding().getFieryness() > 0)//TODO check if it is right to apply
			return true;

		if (Math.max(realBuilding.getLastMsgTime(), realBuilding.getLastSenseTime()) > time)
			return false;

		return true;
	}

	/**
	 * Agent Based, It returns different results for different agent types and it is different to itHasBeenSeen and isSearchedForCivilian.
	 * 
	 * @return
	 */
	@Deprecated
	public boolean needsToBeSearchedForCivilian() {
		if (getRealBuilding().getAgent() instanceof PoliceForceAgent) {
			if (isHasBeenSeen())
				return false;
			if (getRealBuilding().isBrokennessDefined() && getRealBuilding().getBrokenness() == 0
			//					&& !getRealBuilding().getAgent().move.isReallyUnreachableXYPolice(new Pair<Area, Point2D>(this.getRealBuilding(), this.getRealBuilding().getPositionPoint()))
			)
				return false;
		} else {
			if (isHasBeenSeen())
				return false;

			if (getRealBuilding().isBrokennessDefined() && getRealBuilding().getBrokenness() == 0)
				return false;

			if (getRealBuilding().getAgent().move.isReallyUnreachable(this.getRealBuilding().getSearchAreas()))
				return false;
		}
		if (getRealBuilding().getFieryness() > 0)
			return false;
		return true;

	}

	public void setHasBeenSeenBySelf(boolean hasBeenSeenBySelf) {
		this.hasBeenSeenBySelf = hasBeenSeenBySelf;
	}

	public boolean isHasBeenSeenBySelf() {
		return hasBeenSeenBySelf;
	}

	public boolean scoreAndFilterSearchedForCivilian() {
		if (getRealBuilding().isFierynessDefined()
				&& getRealBuilding().getFierynessEnum() != Fieryness.UNBURNT
				&& getRealBuilding().getFierynessEnum() != Fieryness.WATER_DAMAGE) {
			addScore("FILTER:fiery", AgentSearchScore.SEARCH_FILLTER_SCORE);
			return false;
		}
		if (isHasBeenSeen()) {
			addScore("FILTER:has been seen:", AgentSearchScore.SEARCH_FILLTER_SCORE);
			return false;
		}
		if (getRealBuilding().getSOSRealFireSite() != null && !getRealBuilding().getSOSRealFireSite().isDisable()) {
			addScore("FILTER:not disable firezone", AgentSearchScore.SEARCH_FILLTER_SCORE);
			return false;
		}
		for (Building neighbour : getRealBuilding().realNeighbors_Building()) {
			if (neighbour.getSOSRealFireSite() != null && !neighbour.getSOSRealFireSite().isDisable()&&neighbour.isBurning()||neighbour.getFieryness()==8) {
				addScore("FILTER:not disable neighbour firezone", AgentSearchScore.SEARCH_FILLTER_SCORE);
				return false;
			}
		}

		if (getRealBuilding().getAgent() instanceof PoliceForceAgent) {
			if (getRealBuilding().isBrokennessDefined() && getRealBuilding().getBrokenness() == 0
			//					&& !getRealBuilding().getAgent().move.isReallyUnreachableXYPolice(new Pair<Area, Point2D>(this.getRealBuilding(), this.getRealBuilding().getPositionPoint()))
			) {
				addScore("FILTER:no brokness:", AgentSearchScore.SEARCH_FILLTER_SCORE);
				return false;
			}
		} else {
			if (getRealBuilding().isBrokennessDefined() && getRealBuilding().getBrokenness() == 0) {
				addScore("FILTER:no brokness:", AgentSearchScore.SEARCH_FILLTER_SCORE);
				return false;
			}

			//			if (getRealBuilding().getAgent().move.isReallyUnreachable(this.getRealBuilding().getSearchAreas())) {
			//				addScore("FILTER:unreachable:", AgentSearchScore.SEARCH_FILLTER_SCORE);
			//				return false;
			//			}
		}

		return true;

	}

}
