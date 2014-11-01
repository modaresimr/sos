package sos.fire_v2.target;

import java.util.ArrayList;
import java.util.List;

import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSRealFireZone;

public class SOSFireZoneSelector extends SOSSelectTarget<SOSEstimatedFireZone> {

	public SOSFireZoneSelector(SOSAgent<?> agent) {
		super(agent);
	}

	@Override
	public SOSEstimatedFireZone getBestTarget(List<SOSEstimatedFireZone> validTarget) {

		double max = Integer.MIN_VALUE;

		SOSEstimatedFireZone best = null;

		for (SOSEstimatedFireZone e : validTarget) {
			if (e.score != 0 && e.score > max) {
				best = e;
				max = e.score;
			}
		}

		log.info("get best Target " + best);

		return best;
	}

	@Override
	public void reset(List<SOSEstimatedFireZone> validTarget) {
		for (SOSEstimatedFireZone e : validTarget) {
			e.score = 0;
		}
	}

	@Override
	public void setPriority(List<SOSEstimatedFireZone> validTarget) {
		log.info("Set Priority");
		for (SOSEstimatedFireZone fz : validTarget) {
			boolean temp = needMe(fz);
			log.info("Priority For " + fz + "       Added " + temp);
			if (temp) 
				distanceScore(fz);
			
		}
	}

	private boolean needMe(SOSEstimatedFireZone fz) {
		return true;
	}

	private void distanceScore(SOSEstimatedFireZone fz) {
		int dis = fz.distance(((Human) agent.me()).getX(), ((Human) agent.me()).getY());
		fz.score += -1 * dis / 1000;
		log.info("Distance Score " + fz + "  " + -1 * dis / 1000);

	}

	@Override
	public void preCompute() {
	}

	@Override
	public List<SOSEstimatedFireZone> getValidTask(Object link) {
		log.info("Valid Target ");

		ArrayList<SOSEstimatedFireZone> firezones = new ArrayList<SOSEstimatedFireZone>();
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> x : agent.fireSiteManager.getFireSites()) {
			log.info("\tChecking  " + x.second());
			if (x.second().isDisable()) {
				log.info("\t\t Fire Zone is Disable ");
				continue;
			}
			firezones.add(x.second());
		}

		log.info("finished validTarget ::> " + firezones);
		return firezones;
	}

}
