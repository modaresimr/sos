package sos.fire_v2.target;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import sos.base.entities.Building;
import sos.base.entities.FireBrigade;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.fire_v2.base.tools.FireStarCluster;

public class Tools {

	public static boolean isSmallFire(SOSEstimatedFireZone fz) {
		if (fz.getOuter().size() < 4)
			return true;
		return false;
	}

	public static boolean isMediumFire(SOSEstimatedFireZone fz) {
		if (fz.getOuter().size() < 15 && !isSmallFire(fz))
			return true;
		return false;
	}

	public static boolean isBigFire(SOSEstimatedFireZone fz) {
		if (!isMediumFire(fz) && !isSmallFire(fz))
			return true;
		return false;
	}


	public static double getAngleBetweenTwoVector(double d1x, double d1y, double d2x, double d2y) {
		double length1 = Math.hypot(d1x, d1y);
		double length2 = Math.hypot(d2x, d2y);
		double cos = (d1x * d2x + d1y * d2y) / (length1 * length2);
		if (cos > 1) {
			cos = 1;
		}
		double angle = Math.toDegrees(Math.acos(cos));
		return angle;
	}


	public static ArrayList<SOSEstimatedFireZone> getFireZonesInDistance(List<SOSEstimatedFireZone> validFireZones, int distance, Human me) {
		ArrayList<SOSEstimatedFireZone> result = new ArrayList<SOSEstimatedFireZone>();
		for (SOSEstimatedFireZone f : validFireZones) {
			if (f.distance(me.getX(), me.getY()) < distance)
				result.add(f);
		}
		return result;
	}

	public static Comparator<SOSEstimatedFireZone> FIRE_ZONE_DISTANCE_COMPARATOR = new Comparator<SOSEstimatedFireZone>() {
		@Override
		public int compare(SOSEstimatedFireZone o1, SOSEstimatedFireZone o2) {
			int x = ((Human) o1.manager.me.me()).getX();
			int y = ((Human) o1.manager.me.me()).getY();
			return o1.distance(x, y) - o2.distance(x, y);
		}
	};

	public static ArrayList<FireBrigade> getOutSideFire(ArrayList<FireBrigade> fire) {
		ArrayList<FireBrigade> res = new ArrayList<FireBrigade>();
		for (FireBrigade fb : fire) {
			if (!(fb.getPositionArea() instanceof Building) || fb.getPositionArea() instanceof Refuge)
				res.add(fb);
		}
		return res;
	}

	public static int getNumberOfAgentInSideBuilding(ArrayList<FireBrigade> available) {
		int res = 0;
		for (FireBrigade fb : available) {
			if (fb.getAreaPosition() instanceof Building) {
				res++;
			}
		}

		return res;
	}

	public static HashMap<Integer, ArrayList<SOSEstimatedFireZone>> getFireSiteByLocation(ArrayList<SOSEstimatedFireZone> fireZones, FireStarCluster startCluster) {
		HashMap<Integer, ArrayList<SOSEstimatedFireZone>> hash = new HashMap<Integer, ArrayList<SOSEstimatedFireZone>>();

		for (int j = 0; j < startCluster.getStarZones().length; j++) {
			ArrayList<SOSEstimatedFireZone> arr = new ArrayList<SOSEstimatedFireZone>();
			hash.put(j, arr);
			for (int i = 0; i < fireZones.size(); i++) {
				SOSEstimatedFireZone es = fireZones.get(i);
				for (Building b : es.getAllBuildings()) {
					if (startCluster.getStarZones()[j].getZoneBuildings().contains(b)) {
						arr.add(es);
						break;
					}
				}

			}
		}
		return hash;
	}
	public static ArrayList<Integer> getFireSiteLocation(SOSEstimatedFireZone fireZone, FireStarCluster startCluster) {
		ArrayList<Integer> res=new ArrayList<Integer>();

		for (int j = 0; j < startCluster.getStarZones().length; j++) {
				for (Building b : fireZone.getAllBuildings()) {
					if (startCluster.getStarZones()[j].getZoneBuildings().contains(b)) {
						res.add(j);
						break;
					}
				}

			}
		return res;
	}

}
