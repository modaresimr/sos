package sos.search_v2.tools.cluster;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.fire_v2.FireBrigadeAgent;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.tools.SearchUtils;
import sos.tools.Utils;

/**
 * @author Yoosef 
 * @param <E>
 */
public class NoCommunicationCluster<E extends Human> extends MapClusterType<E> {

	public NoCommunicationCluster(SOSAgent<E> me, List<E> agents) {
		super(me, agents);
	}

	@Override
	public void startClustering(SOSWorldModel model) {
		cluster();

		if (model.sosAgent() instanceof FireBrigadeAgent) {
			fireAssign();
		}
		if (model.sosAgent() instanceof AmbulanceTeamAgent) {
			ambulanceAssign();
		}
		if (model.sosAgent() instanceof PoliceForceAgent) {
			policeAssign();
		}
	}

	private StarZone[] startZones;
	public static int STAR_ZONES = 13;//8+4+1
	public double FIRST_CENTERAL_CIRCLE_RADIUS = 0;
	public double SECOND_CENTERAL_CIRCLE_RADIUS = 0;
	public double CX;
	public double CY;

	private void cluster() {
		setStartZones(new StarZone[STAR_ZONES]);

		double maxD = getMaxDistance(me.model().mapCenter());
		FIRST_CENTERAL_CIRCLE_RADIUS = Math.max(100000, maxD / 7d);
		SECOND_CENTERAL_CIRCLE_RADIUS = Math.max(FIRST_CENTERAL_CIRCLE_RADIUS + 200000, maxD * 2 / 3);

		for (Building b : me.model().buildings()) {
			getStartZones()[getZoneIndex(b)].getZoneBuildings().add(b);
		}

	}

	private int getZoneIndex(Building b) {
		double distanceTOCenter = Utils.distance(CX, CY, b.getX(), b.getY());
		if (distanceTOCenter <= FIRST_CENTERAL_CIRCLE_RADIUS) {
			return 0;
		}
		float taleAngle = 360 / 8;
		int starZoneIndex = ((int) (SearchUtils.getAngle(b, (int) CX, (int) CY) / taleAngle));

		if (distanceTOCenter <= SECOND_CENTERAL_CIRCLE_RADIUS) {
			return starZoneIndex + 1;
		} else {
			return (int) (1 + 8 + Math.ceil(starZoneIndex / 2d));
		}
	}

	public double getMaxDistance(Point point) {
		CX = point.getX();
		CY = point.getY();
		double maxDistance = -1;
		for (Building b : me.model().buildings()) {
			double distance = Utils.distance(b.getX(), b.getY(), point.getX(), point.getY());
			if (distance > maxDistance)
				maxDistance = distance;
		}
		return maxDistance;
	}

	private void ambulanceAssign() {
	}

	private void policeAssign() {
	}

	private void fireAssign() {
		ArrayList<E> fbs = new ArrayList<E>(agents);
		for (int i = 0; i < getStartZones().length; i++) {
			if (fbs.size() > 0) {
				E bestAgent = getBestAgent(getStartZones()[i], fbs);
				ClusterData cd = new ClusterData(i, getStartZones()[i].getZoneBuildings(), me, i);
				clusters.put(bestAgent, cd);
			}
		}
	}

	public StarZone[] getStartZones() {
		return startZones;
	}

	private E getBestAgent(StarZone region, ArrayList<E> agents) {
		double bestD = Double.MAX_VALUE;
		int best = -1;
		for (int i = agents.size() - 1; i > -1; i--) {
			double d = Utils.distance(region.getZoneBuildings().iterator().next().getX(), region.getZoneBuildings().iterator().next().getY(), agents.get(i).getX(), agents.get(i).getY());
			if (d < bestD) {
				bestD = d;
				best = i;
			}
		}
		E result = agents.get(best);
		agents.remove(best);
		return result;

	}

	public void setStartZones(StarZone[] startZones) {
		this.startZones = startZones;
		for (int i = 0; i < startZones.length; i++) {
			startZones[i] = new StarZone(1, -1, -1, i);
		}
	}

	@Override
	public Collection<ClusterData> allClusters() {
		return null;
	}


}
