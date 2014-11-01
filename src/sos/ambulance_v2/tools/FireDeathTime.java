package sos.ambulance_v2.tools;

import java.util.TreeSet;

import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.StandardEntity;

/**
 * Created by IntelliJ IDEA.
 * User: ara
 * To change this template use File | Settings | File Templates.
 */
public class FireDeathTime {

	public  FireDeathTime() {
	}

	// LoggingSystem lg = new LoggingSystem(Constants.ModeType.Medium, Constants.OutputType.File, null, Constants.SystemType.Traffic, false);
	private static TreeSet<Building> getFieryBuildings(SOSAgent<? extends StandardEntity> sosAgent) {
		return sosAgent.model().fieryBuildings();
	}

	public static int getFireDeathTime(Human hm) {

		if (!hm.isPositionDefined() || hm.getPosition() instanceof Refuge || !(hm.getPosition() instanceof Building))
			return 1000;

		if (getFieryBuildings(hm.getAgent()).isEmpty())
			return 1000;
		if (!(hm instanceof Civilian) && hm.getBuriedness() == 0)
			return 1000;
		Building civPosition = (Building) hm.getPosition();
		int receiveFireTime = getFireRecievedTime(hm);
		int extra = hm.getRescueInfo().estimatedHp() / (500 + hm.getRescueInfo().estimatedDamage());
		//        extra -= 2; comment by Ali
		//        if (extra < 0)
		//            extra = 0;
		if (civPosition.isOnFire()) {
			return extra + hm.getAgent().time();
		}
		return receiveFireTime + extra;
	}

	public static int getFireDeathTime(Human hm, int timeOfFireRecieve) {
		if (!hm.isPositionDefined() || hm.getPosition() instanceof Refuge || !(hm.getPosition() instanceof Building))
			return 1000;
		if (getFieryBuildings(hm.getAgent()).isEmpty())
			return 1000;
		if (!(hm instanceof Civilian) && hm.getBuriedness() == 0)
			return 1000;
		Building civPosition = (Building) hm.getPosition();
		int extra = hm.getRescueInfo().estimatedHp() / (250 + hm.getRescueInfo().estimatedDamage());
		extra -= 2;
		if (extra < 0)
			extra = 0;
		if (civPosition.isOnFire()) {
			return extra + hm.getAgent().time();
		}
		return timeOfFireRecieve + extra;
	}

	public static int getFireRecievedTime(Human hm) {
		if (!hm.isPositionDefined() || hm.getPosition() instanceof Refuge || !(hm.getPosition() instanceof Building))
			return 1000;
		if (getFieryBuildings(hm.getAgent()).isEmpty())
			return 1000;
		if (!(hm instanceof Civilian) && hm.getBuriedness() == 0)
			return 1000;

		Building near = getNearestFire((Building) hm.getPosition()); // TODO delete
		int dist = near.distance((Building) hm.getPosition());
		return dist / 500 + hm.getAgent().time();
	}

	public static Building getNearestFire(Building from) {
		Building nearest = null;
		for (Building a : getFieryBuildings(from.getAgent())) {
			if (a.isOnFire()) {
				if (nearest == null) {
					nearest = a;
				} else {
					nearest = a.distance(from) < nearest.distance(from) ? a : nearest;
				}
			}
		}
		return nearest;
	}

}
