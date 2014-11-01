package sos.base.sosFireZone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.FireBrigade;
import sos.base.entities.Refuge;
import sos.base.sosFireZone.util.ConvexHull_arr_New;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.tools.Utils;

public class SOSEstimatedFireZone extends SOSAbstractFireZone {
	/**
	 * the buildings that estimate by this fire zone
	 */
	protected ArrayList<Building> estimatorBuildings;
	/**
	 * fire zone score for select
	 */
	public double score = 0;//TODO
	public boolean isReachableInInterupt = false;

	public SOSEstimatedFireZone(short index, SOSFireZoneManager manager) {
		super(index, manager);
		estimatorBuildings = new ArrayList<Building>();
	}

	@Override
	public void update(int time) {
		convexed = null;//TODO
		convex = null;//TODO
		setOuterConvex(null);//TODO

		long t1 = System.currentTimeMillis();

		updateSiteOuter();

		fireLog.info("update site outer " + (System.currentTimeMillis() - t1) + " ms ");

		long t2 = System.currentTimeMillis();
		computeSafeAndOuter();

		fireLog.info("compute safe and  outer " + (System.currentTimeMillis() - t2) + " ms ");

		fireLog.info("outer  ::>  " + outer);
		fireLog.info("allBuilding ::>  " + allBuilding);
		fireLog.info("safe Building ::>  " + safeBuilding);

		setExtinguishable(getOuter().size() > 0);

		if (!isExtinguishable()) {
			if (time - extinguishedTime > DISABLE_TIME) {
				fireLog.info("DISABLE " + time + "   " + extinguishedTime);
				setDisable(true, time,true);
			}
		} else {
			setDisable(false, -1,false);
		}

		if (manager.me instanceof FireBrigadeAgent)
			computeSpread();//spread

		fireLog.info("end update " + (System.currentTimeMillis() - t1) + " ms ");

		fireLog.info("All building " + allBuilding);
		fireLog.info("safe building " + safeBuilding);
		fireLog.info("outer building " + outer);

		//		if (!SOSConstant.IS_CHALLENGE_RUNNING)
		//			fireLog.warn("Fire zone (" + this + ") update in Time= " + (System.currentTimeMillis() - t1) + " ms ");
	}

	@Override
	public void addFieryBuilding(Building b) {
		b.setSOSEstimateFireSite(this);
		super.addFieryBuilding(b);
		addEstimatorBuilding(b);
	}

	public void addEstimatorBuilding(Building b) {
		if (b.getEstimator() == null) {
			estimatorBuildings.add(b);
			b.addToEstimator(this);
		}

		int distance = 70000;// Kobe & VC
		if (manager.me.getMapInfo().isBigMap() || manager.me.getMapInfo().isMediumMap())
			distance = 200000;

		Collection<Building> buildingInRange = manager.me.model().getObjectsInRange(b, distance, Building.class);
		for (Building b2 : buildingInRange) {
			if (b2.getEstimator() == null) {
				estimatorBuildings.add(b2);
				b2.addToEstimator(this);
			}
		}
	}

	@Override
	public void update(Building building) {
		if (building.virtualData[0].getFieryness() == 0 || (building.virtualData[0].getFieryness() > 3)) {
			if (building.virtualData[0].getFieryness() == 0) {
				allBuilding.remove(building);
				updateXY(building, -1);
				building.setSOSEstimateFireSite(null);
			}
			outer.remove(building);
			safeBuilding.add(building);
		}
	}

	//	@Override
	//	protected void updateSiteOuter(Building b) {//TODO BFS
	//		for (Building b2 : b.realNeighbors_Building()) {
	//
	//			if (b2.virtualData[0].getFieryness() > 0 && (b2.getSOSEstimateFireSite() == null || b2.getSOSEstimateFireSite().isDisable)) {
	//
	//				if (b2.getSOSEstimateFireSite() != null && b2.getSOSEstimateFireSite().isDisable) {
	//
	//					if (b2.getSOSEstimateFireSite().equals(this)) {
	//
	//						System.out.println("age in chapppppppppppppp sho be Yoosef begin number 1 !!!! ");
	//						continue;
	//
	//					} else {
	//
	//						b2.getSOSEstimateFireSite().outer.remove(b2);
	//						b2.getSOSEstimateFireSite().allBuilding.remove(b2);
	//						b2.getSOSEstimateFireSite().estimatorBuildings.remove(b2);
	//						b2.getSOSEstimateFireSite().safeBuilding.remove(b2);
	//						System.out.println("age in chapppppppppppppp sho be Yoosef begin number 2!!!! ");
	//						//TODO update x,y etc.
	//
	//					}
	//				}
	//
	//				addFieryBuilding(b2);
	//				b2.setSOSEstimateFireSite(this);
	//				updateSiteOuter(b2);
	//			}
	//		}
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
			if ((from.virtualData[0].getFieryness() > 0 && from.getSOSEstimateFireSite() == null)) {

				addFieryBuilding(from);
				from.setSOSEstimateFireSite(this);

			}

			for (Building to : from.realNeighbors_Building()) {
				if (visited[to.getBuildingIndex()] == false) {
					visited[to.getBuildingIndex()] = true;
					if ((to.virtualData[0].getFieryness() > 0 && to.getSOSEstimateFireSite() == null)) {
						buildings.add(to);
					}
				}
			}
		}

	}

	@Override
	protected boolean isAddableToOuter(Building forOuter, Building forSafe) {
		if (forSafe.virtualData[0].getFieryness() == 0)
			return true;

		if (forSafe.virtualData[0].getFieryness() == 4) {
			if (forSafe.virtualData[0].getTemperature() > 20)
				return true;

		}
		return false;
	}

	@Override
	protected void computeSafeAndOuter() {

		safeBuilding.clear();
		outer.clear();
		burningBuildings.clear();

		ArrayList<Building> tempOuter = new ArrayList<Building>();

		for (Building b : allBuilding) {
			if (b.virtualData[0].isBurning())
				burningBuildings.add(b);
			else
				continue;
			boolean temp = false;

			for (Building b2 : b.realNeighbors_Building()) {
				if (isAddableToOuter(b, b2)) {
					temp = true;
					break;
				}
			}
			if (temp) {
				tempOuter.add(b);
			}
		}
		//
		//		ConvexHull_arr_New convex = new ConvexHull_arr_New(tempOuter, true);
		//		convex = convex.getScaleConvex(0.5f);
		//
		//		for (Building forOuter : tempOuter) {
		//
		//			boolean temp = false;
		//
		//			for (Building forSafe : forOuter.realNeighbors_Building()) {
		//
		//				if ((!convex.getShape().contains(forSafe.getX(), forSafe.getY())) || checkIland(forOuter, forSafe)) {
		//
		//					if (isAddableToOuter(forOuter, forSafe)) {
		//
		//						temp = true;
		//						addSafeBuilding(forSafe);
		//
		//					}
		//				}
		//			}
		//
		//			if (temp) {
		//				outer.add(forOuter);
		//			}
		//		}
		outer.addAll(tempOuter);
		safeBuilding.clear();
		for (Building b : outer) {
			for (Building neigh : b.realNeighbors_Building()) {
				if (isAddableToOuter(b, neigh)) {
					if (!safeBuilding.contains(neigh))
						safeBuilding.add(neigh);
				}
			}
		}
	}

	@Override
	protected boolean checkIland(Building forOuter, Building forSafe) {
		if (!(manager.me instanceof FireBrigadeAgent))
			return false;
		return (forSafe.getFireBuilding().island().isFireNewInIsland() || forOuter.getFireBuilding().island().isFireNewInIsland());
	}

	@Override
	public void setDangerBuilding() {//TODO
		dangerBuilding = new ArrayList<Building>();
		ArrayList<Building> danger = new ArrayList<Building>();

		for (Building b : allBuilding) {
			for (Building b2 : b.realNeighbors_Building()) {
				if (b2.getSOSEstimateFireSite() == null) {
					if (!dangerBuilding.contains(b2))
						dangerBuilding.add(b2);
				}
			}
		}
		//		fireLog.info("pre danger" + danger);
		//		for (Building b : danger) {
		//			//			for (Building b2 : b.realNeighbors_Building()) {
		//			//				if (b2.getSOSEstimateFireSite() == null)
		//			//					if (!dangerBuilding.contains(b2))
		//			//						dangerBuilding.add(b2);
		//			//			}
		//			//			if (!dangerBuilding.contains(b))
		//			dangerBuilding.add(b);
		//
		//		}
		fireLog.info("danger building " + dangerBuilding);

	}

	public ArrayList<Building> getEstimatorBuilding() {
		return estimatorBuildings;
	}

	/////////////////?Fire Agent

	public Pair<Double, Double> spread = new Pair<Double, Double>(0.0, 0.0);

	public void computeSpread() {
		double x = 0, y = 0, num = 1;
		for (Building b : manager.me.model().buildings()) {
			int fn = b.virtualData[0].getFieryness();
			if (fn == 0 || fn > 3 && fn <= 7) {
				if (b.getX() > getCenterX()) {
					x++;
				} else {
					x--;
				}
				if (b.getY() > getCenterY()) {
					y++;
				} else {
					y--;
				}
			} else {//if (fn >= 1 && fn <= 3) {
				if (b.getX() > getCenterX()) {
					x -= 1;
				} else {
					x += 1;
				}
				if (b.getY() > getCenterY()) {
					y -= 1;
				} else {
					y += 1;
				}
			}
			num++;
		}
		for (Civilian civ : manager.me.model().civilians()) {
			if (civ.getAreaPosition() instanceof Building) {
				if (!(civ.getAreaPosition() instanceof Refuge)) {
					if (civ.isAlive()) {
						if (!civ.isDamageDefined() || (civ.isDamageDefined() && civ.getDamage() > 0)) {
							if (civ.getX() > getCenterX()) {
								x += 1;
							} else {
								x -= 1;
							}
							if (civ.getY() > getCenterY()) {
								y += 1;
							} else {
								y -= 1;
							}
							num++;
						}
					}
				}
			}

		}

		spread = new Pair<Double, Double>(x / num, y / num);
	}

	public int getNumberOfAgentToExtinguish() {//TODO
		if (allBuilding.size() == 0)
			return 0;
		if (isDisable())
			return 0;
		if (!isExtinguishable())
			return 1;
		//		return 2 + (int) Math.ceil(getOuterArea() / 200d);
		if (getArea() < 220)
			return 1;//TODO check ISOLE for this site
		//		ArrayList<Building> cvb = getConvexedBuilding((float) 0.7);
		int size = getOuter().size();
		//		for (Building b : getOuter()) {
		//			if (getOuter().contains(b)) {
		//				size++;
		//			}
		//		}
		if (size == 0)
			return 0;
		if (AbstractFireBrigadeAgent.maxPower <= 600) {
			if (size <= 3) {
				return 3;
			}
			if (size <= 10) {
				return 6;
			}
			if (size <= 20) {
				return 8;
			}
			if (size <= 30) {
				return 12;
			}
			return 16;

		} else {
			if (size <= 3) {
				return 2;
			}
			if (size <= 10) {
				return 4;
			}
			if (size <= 20) {
				return 7;
			}
			if (size <= 30) {
				return 10;
			}
			return 15;

		}
		//
		//		if (size <= 5) {
		//			//			return 1 * k;
		//			return 3 * k;
		//		}
		//		if (size <= 10) {
		//			return 6 * k;
		//		}
		//		if (size <= 15) {
		//			return 9 * k;
		//		}
		//		return 15 * k;
	}

	public int getArea() {
		int area = 0;
		for (Building b : getAllBuildings()) {
			area += b.getGroundArea();
		}
		return area;
	}

	public int getOuterArea() {
		int area = 0;
		for (Building b : getOuter()) {
			area += b.getGroundArea();
		}
		return area;
	}

	private ArrayList<FireBrigade> assignedAgent = new ArrayList<FireBrigade>();

	public int getSizeAssignedAgent() {
		return assignedAgent.size();
	}

	public ArrayList<FireBrigade> getAssignedAgent() {
		return assignedAgent;
	}

	public void setAssignedAgnet(FireBrigade f) {
		if (!assignedAgent.contains(f))
			assignedAgent.add(f);

	}

	public int getNumberOfAgentNeed() {
		return getNumberOfAgentToExtinguish() - getSizeAssignedAgent();
	}

	public void updateInsideFireSite() {//TODO
		//		if (manager.me.getMapInfo().getRealMapName() == MapName.Paris || manager.me.getMapInfo().getRealMapName() == MapName.Berlin || manager.me.getMapInfo().getRealMapName() == MapName.Big)//TODO ISTANBUL
		//			return;
		if (getAllBuildings().size() <= 1)
			return;
		ConvexHull_arr_New convex2 = getConvex();
		for (Building b2 : getAllBuildings()) {
			for (Building b : b2.realNeighbors_Building())
				if (!b.isTemperatureDefined() && b.virtualData[0].getTemperature() < 10) {
					if (convex2.contains(b.getX(), b.getY())) {
						b.virtualData[0].artificialFire(1);//setTemprature(50);
					}
				}
		}
	}

	public String fullDescription() {
		return "No Description!!!";
	}

	public int distance(int x, int y) {
		int dis = Integer.MAX_VALUE;
		int dis2 = 0;

		for (Building out : getOuter()) {
			dis2 = (int) Utils.distance(x, y, out.getX(), out.getY());
			if (dis2 < dis) {
				dis = dis2;
			}
		}
		return dis;
	}

}
