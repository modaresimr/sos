package sos.base.sosFireZone;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import sos.base.entities.Building;
import sos.base.sosFireZone.util.ConvexHull_arr_New;
import sos.fire_v2.FireBrigadeAgent;

public class SOSRealFireZone extends SOSAbstractFireZone {

	public SOSRealFireZone(short hashCode, SOSFireZoneManager manager) {
		super(hashCode, manager);
	}

	@Override
	public void addFieryBuilding(Building b) {
		b.setSOSRealFireSite(this);
		super.addFieryBuilding(b);
	}

	@Override
	public void update(int time) {
		convexed = null;
		convex = null;
		setOuterConvex(null);
		//		computeSafeAndOuter();
		long t1 = System.currentTimeMillis();
		//		for (int i = 0; i < outer.size(); i++) {
		//			if (outer.get(i).getFieryness() > 0) {
		//				fireLog.info("update site " + outer.get(i));
		updateSiteOuter();
		//			}
		//		}
		long t2 = System.currentTimeMillis();
		computeSafeAndOuter();
		setExtinguishable(getOuter().size() > 0);

		if (!isExtinguishable()) {
			if (time - extinguishedTime > DISABLE_TIME) {
				fireLog.info("DISABLE " + time + "   " + extinguishedTime);
				setDisable(true, time,true);
				//TODO message :D age messagesh behem nareside
			}
		} else {
			setDisable(false, -1,false);
		}
		//		fireLog.info("compute safe and outer " + (System.currentTimeMillis() - t2) + " ms ");
		//		fireLog.info("end update " + (System.currentTimeMillis() - t1) + " ms ");
		//		tekrariDare();
		//		if (time < 40)

	}

	@Override
	public void update(Building building) {
		fireLog.info("update buolding see " + building + "   " + building.virtualData[0].getTemperature() + "   " + building.getTemperature() + "  " + building.getFieryness());
		if (building.getFieryness() == 0 || (building.getFieryness() > 3)) {
			if (building.getFieryness() == 0) {
				allBuilding.remove(building);
				updateXY(building, -1);
				building.setSOSRealFireSite(null);
			}
			outer.remove(building);
			safeBuilding.add(building);
		}
	}

	//
	//	@Override
	//	protected void updateSiteOuter(Building b) {
	//		//		fireLog.info("barresi " + b);
	//		for (Building b2 : b.realNeighbors_Building()) {
	//			//			fireLog.info("vazeiaet " + b2 + "   " + b2.getFireSite() + "   " + b2.virtualData[0].isBurning());
	//			if ((b2.getSOSRealFireSite() == null || b2.getSOSRealFireSite().isDisable) && b2.getFieryness() > 0) {
	//				if (b2.getSOSRealFireSite() != null && b2.getSOSRealFireSite().isDisable) {
	//					if (b2.getSOSRealFireSite().equals(this)) {
	//						continue;
	//					} else {
	//						b2.getSOSRealFireSite().outer.remove(b2);
	//						b2.getSOSRealFireSite().allBuilding.remove(b2);
	//					}
	//				}
	//				addFieryBuilding(b2);
	//				//				fireLog.info(b2 + " added to this site " + this);
	//				b2.setSOSRealFireSite(this);
	//				updateSiteOuter(b2);
	//			}
	//		}
	//
	//	}

	@Override
	protected void updateSiteOuter() {
		Queue<Building> buildings = new LinkedList<Building>(allBuilding);

		boolean[] visited = new boolean[manager.model.buildings().size()];

		for (Building b : allBuilding) {
			visited[b.getBuildingIndex()] = true;
		}

		while (buildings.size() > 0) {
			Building from = buildings.poll();
			if ((from.getFieryness() > 0 && from.getSOSRealFireSite() == null)) {

				addFieryBuilding(from);
				from.setSOSRealFireSite(this);

			}

			for (Building to : from.realNeighbors_Building()) {
				if (visited[to.getBuildingIndex()] == false) {
					visited[to.getBuildingIndex()] = true;
					if ((to.getFieryness() > 0 && to.getSOSRealFireSite() == null)) {
						buildings.add(to);
					}
				}
			}
		}

	}

	@Override
	protected boolean isAddableToOuter(Building forOuter, Building forSafe) {
		//if ((!convex.getShape().contains(forSafe.getX(), forSafe.getY())) || (forSafe.getFireBuilding().island().isFireNewInIsland() || forOuter.getFireBuilding().island().isFireNewInIsland())) {
		if (forSafe.getFieryness() == 0)
			return true;
		if (forSafe.getFieryness() == 4) {
			if (forSafe.getGroundArea() > forOuter.getGroundArea() * 3) {
				if (forSafe.getTemperature() >= 40)
					return true;
				return false;
			} else {
				if (forSafe.getTemperature() > 20)
					return true;
				return false;
			}
		}
		//		}//
		return false;
	}

	@Override
	protected void computeSafeAndOuter() {
		safeBuilding.clear();
		outer.clear();

		burningBuildings.clear();
		fireLog.log("filterInConvexBuildings : \t");
		ArrayList<Building> tempOuter = new ArrayList<Building>();
		for (Building b : allBuilding) {
			if (!b.isBurning())
				continue;
			else {
				burningBuildings.add(b);
			}
			boolean temp = false;
			for (Building b2 : b.realNeighbors_Building()) {
				if (isAddableToOuter(b, b2)) {
					temp = true;
					//		addSafeBuilding(b2);
				}
			}
			if (temp) {
				//				if (!convex.getShape().contains(b.getX(), b.getY())) {
				tempOuter.add(b);
				//				}
			}
		}
		/////////////////////////////////////
		//if ((!convex.getShape().contains(forSafe.getX(), forSafe.getY())) || (forSafe.getFireBuilding().island().isFireNewInIsland() || forOuter.getFireBuilding().island().isFireNewInIsland())) {
		ConvexHull_arr_New convex = new ConvexHull_arr_New(tempOuter, true);
		convex = convex.getScaleConvex(0.9f);
		for (Building forOuter : tempOuter) {
			boolean temp = false;
			for (Building forSafe : forOuter.realNeighbors_Building()) {
				if ((!convex.getShape().contains(forSafe.getX(), forSafe.getY())) || checkIland(forOuter, forSafe)) {
					if (isAddableToOuter(forOuter, forSafe)) {
						temp = true;
						addSafeBuilding(forSafe);
					}
				}
			}

			if (temp) {
				//				if (!convex.getShape().contains(b.getX(), b.getY())) {
				outer.add(forOuter);
				//				}
			}
		}
		//		if (fireyBuilding.size() < 10)
		//			convex = convex.getScaleConvex(0f);
		//		else

	}

	@Override
	protected boolean checkIland(Building forOuter, Building forSafe) {
		if (!(forOuter.getAgent() instanceof FireBrigadeAgent))
			return false;
		return (forSafe.getFireBuilding().island().isFireNewInIsland() || forOuter.getFireBuilding().island().isFireNewInIsland());
	}

	@Override
	public void setDangerBuilding() {
		
	}
}
