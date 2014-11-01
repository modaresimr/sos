package sos.base.sosFireZone;

import java.util.ArrayList;

import sos.base.SOSConstant;
import sos.base.entities.Building;
import sos.base.sosFireZone.util.ConvexHull_arr_New;
import sos.base.sosFireZone.util.Mergable;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

/**
 * @author Yoosef
 */
public class SOSFireZoneMerger implements Mergable {

	private final SOSFireZoneManager manager;
	private SOSLoggerSystem log;

	public SOSFireZoneMerger(SOSFireZoneManager manager) {
		this.manager = manager;
		log = new SOSLoggerSystem(manager.me.me(), "SOSFireSite/SOSFireSiteManager/Merger", SOSConstant.CREATE_BASE_LOGS, OutputType.File, true);
		manager.me.sosLogger.addToAllLogType(log);
	}


	@Override
	public boolean isMergable(SOSAbstractFireZone f1, SOSAbstractFireZone f2) {
		if (f1.isDisable || f2.isDisable)
			return false;
		//
		if (f1.manager.model.time() % 5 != (manager.me.getID().getValue() % 5))
			return false;

		int borderDistance = getDistance(f1, f2);
		int centerDistance = (int) sos.tools.Utils.distance(f1.getCenterX(), f1.getCenterY(), f2.getCenterX(), f2.getCenterY());
		if (borderDistance > centerDistance / 2) {
			//					fireLog.warn("not enoygh close fireSites");
			return false;
		}
		ConvexHull_arr_New mergedconvex = new ConvexHull_arr_New(f1.getUsefullConvex(), f2.getUsefullConvex());
		ArrayList<Building> middleBuilding = new ArrayList<Building>();
		ArrayList<Building> site1Building = new ArrayList<Building>(f1.getAllBuildings());
		ArrayList<Building> site2Building = new ArrayList<Building>(f2.getAllBuildings());
		for (Building b : f1.manager.model.buildings()) {
			if (mergedconvex.contains(b.getX(), b.getY()))
				if ((!f1.getUsefullConvex().contains(b.getX(), b.getY())) && (!f2.getUsefullConvex().contains(b.getX(), b.getY())))
					middleBuilding.add(b);
				else if (f1.getUsefullConvex().contains(b.getX(), b.getY()) && (!site1Building.contains(b)))
					site1Building.add(b);
				else if (f2.getUsefullConvex().contains(b.getX(), b.getY()) && (!site2Building.contains(b)))
					site2Building.add(b);
		}

		float gradient = (float) Math.atan2(f1.centerY - f2.centerY, f1.centerX - f2.centerX);
		float jazb = getBuildingsGravity(site1Building, site2Building, gradient, 6);
		float daf1 = getBuildingsGravity(site1Building, middleBuilding, gradient, 1);
		float daf2 = getBuildingsGravity(middleBuilding, site2Building, gradient, 1);
		if ((jazb - daf1 - daf2) > 0) {
			//			fireLog.warn("fire site "+f1.getHashCode()+" and "+f2.getHashCode()+" is merged now");
			return true;
		}
		return false;

	}

	protected int getDistance(SOSAbstractFireZone fs, SOSAbstractFireZone sosFireSite) {
		int distance = Integer.MAX_VALUE;
		//		fireLog.info("calculate distance    \n f1 " + fs.allBuilding + " \n  f2 " + sosFireSite.allBuilding);
		for (Building b : fs.outer) {
			for (Building b2 : sosFireSite.outer) {
				if (distance > sos.tools.Utils.distance(b.x(), b.y(), b2.x(), b2.y())) {
					distance = (int) sos.tools.Utils.distance(b.x(), b.y(), b2.x(), b2.y());
				}
			}
		}
		return distance;
	}

	public float getBuildingsGravity(ArrayList<Building> l1, ArrayList<Building> l2, float gradient, float load) {
		/**
		 * Baraye be dast ovordane mizane jazb ya dafe 2 ta liste building hathte zaviye khast
		 */
		float result = 0;
		float temp;
		float gradient2;
		float min = Float.MAX_VALUE;
		float max = 0;
		for (Building b1 : l1)
			for (Building b2 : l2) {
				if (b1.equals(b2))
					continue;
				temp = (float) ((b1.getGroundArea() * b2.getGroundArea()) / (Math.max(Math.pow(sos.tools.Utils.distance(b1.getX(), b1.getY(), b2.getX(), b2.getY()), 2), 1)));
				gradient2 = (float) Math.atan2((b1.getY() - b2.getY()), (b1.getX() - b2.getX()));
				temp = (float) (Math.cos(gradient2 - gradient) * temp);
				//				System.out.println("gradient2="+gradient2+"  delta="+(gradient2-gradient)+"  cos="+Math.cos(gradient2-gradient));
				//				System.out.println(("b1=" + b1.getID() + "  b2=" + b2.getID() + "   force= " + temp+"  b1.g="+b1.getGroundArea()+"  b2.g="+b2.getGroundArea()+"  distance="+(Math.pow(sos.tools.Utils.distance(b1.getX(), b1.getY(), b2.getX(), b2.getY()), 2))    ));
				min = Math.min(min, temp);
				max = Math.max(max, temp);
				result += temp;
			}
		//		fireLog.warn("min=" + min + "  max=" + max + " force=" + result + "  avg=" + (result / index) + "  index=" + index);
		return result * load;
	}

}
