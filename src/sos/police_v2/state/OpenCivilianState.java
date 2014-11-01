package sos.police_v2.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.entities.Civilian;
import sos.base.util.SOSActionException;
import sos.police_v2.PoliceConstants;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

public class OpenCivilianState extends PoliceAbstractState {

	private Civilian assignCivil;
	private List<Civilian> validCivilians = new ArrayList<Civilian>();

	public OpenCivilianState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	@Override
	public void precompute() {
	}

	@Override
	public void act() throws SOSActionException {
		log.info("acting as:" + this);
		log.debug("assigned cilvilian=" + assignCivil);
		if (agent.getLastCycleState() != null && !(agent.getLastCycleState() instanceof OpenCivilianState)) {
			log.debug("-------> cycle ghabl dashte kare dg mikarde dobare assign mikone hala");
			assignCivil = assignNewCivilian();
		}
		if (assignCivil != null && missionComplete(assignCivil)) {
			log.info("missoin complete!!" + assignCivil + " opened!");
			assignCivil.setIsReallyReachable(true);
			assignCivil = null;
		}
		if (!PoliceUtils.isValidCivilian(assignCivil, agent, true)) {
			assignCivil = assignNewCivilian();
		}
		log.debug("current assigned civilian is:" + assignCivil);
		if (assignCivil != null) {
			Pair<Area, Point2D> ep = getEntrancePoint(assignCivil.getAreaPosition());
			if (ep == null) {
				log.error("how?????");
				assignCivil = null;
				return;
			}
			moveToPoint(ep);//TODO TOO BUILDING MAGHSADESH NABASHE;)
		}

	}

	private boolean missionComplete(Civilian assignCivil) {
		if (agent.location().equals(assignCivil.getPosition()))
			return true;
		if (assignCivil.isReallyReachable(true))
			return true;
		if (assignCivil.getPosition().isReallyReachable(true))
			return true;
		if (agent.location().getNeighbours().contains(assignCivil.getPosition()) && agent.location().isBlockadesDefined() && agent.location().getBlockades().isEmpty())
			return true;
		if (isReallyOpen(assignCivil))
			return true;
		return false;
	}

	private boolean isReallyOpen(Civilian assignCivil2) {
		return false;
	}

	private Civilian assignNewCivilian() {
		validCivilians.clear();
		for (Civilian civ : model().civilians()) {
			if(!PoliceUtils.isValidCivilian(civ, agent, true))
				continue;
			if(agent.me().distance(civ.getAreaPosition())<model().getDiagonalOfMap()/10)
				validCivilians.add(civ);
		}

		Civilian best = getBestUnreachableCivilians(validCivilians);

		return best;
	}

	/*
	 * private PriorityQueue<Civilian> getUnreachableCivilians() {
	 * PriorityQueue<Civilian> unreachableCivilians = new PriorityQueue<Civilian>(model().civilians().size(),new DistanceComparator());
	 * for (Civilian civilian : model().civilians()) {
	 * if(isValidCivilian(civilian))
	 * unreachableCivilians.add(civilian);
	 * }
	 * return unreachableCivilians;
	 * }
	 */
	private Civilian getBestUnreachableCivilians(Collection<Civilian> cives) {
		log.info("getBestUnreachableCivilians");
		Civilian best = null;
		DistanceComparator comparator = new DistanceComparator();
		for (Civilian civilian : cives) {
			if (PoliceUtils.isValidCivilian(civilian, agent, true)) {
				if (best == null || comparator.compare(best, civilian) < 0)
					best = civilian;
			}

		}
		//		log.info("BestUnreachableCivilian is:" + best);
		return best;
	}

	@SuppressWarnings("unused")
	private Collection<Civilian> getCivilianInRange(int d) {
		ArrayList<Civilian> result = new ArrayList<Civilian>();
		for (Civilian civilian : model().civilians()) {
			if (civilian.isPositionDefined())
				if (PoliceUtils.getDistance(agent.me(), civilian) < d / PoliceConstants.DISTANCE_UNIT)
					result.add(civilian);
		}
		log.trace("civilians in range(" + d + ")=" + result);
		return result;
	}

	private class DistanceComparator implements java.util.Comparator<Civilian> {
		HashMap<Civilian, Integer> civ_cost = new HashMap<Civilian, Integer>();

		@Override
		public int compare(Civilian c1, Civilian c2) {
			Integer d1 = civ_cost.get(c1);
			if (d1 == null) {
				d1 = (int) (PoliceUtils.getDistance(agent.me(), c2) * (Math.random() * 25 + 75) / 100);
				civ_cost.put(c1, d1);
			}
			Integer d2 = civ_cost.get(c2);
			if (d2 == null) {
				d2 = (int) (PoliceUtils.getDistance(agent.me(), c2) * (Math.random() * 25 + 75) / 100);
				civ_cost.put(c2, d2);
			}

			double score1 = 1d;
			double score2 = 1d;

			double finalScore1 = score1 / (d1);
			double finalScore2 = score2 / (d2);
			if (finalScore2 < finalScore1)
				return -1;
			if (finalScore2 > finalScore1)
				return 1;

			return 0;
		}
	}

}