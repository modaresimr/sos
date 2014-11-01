package sos.fire_v2.target.building ; 

import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import rescuecore2.misc.Pair;
import sos.base.SOSConstant;
import sos.base.entities.Building;
import sos.base.entities.Center;
import sos.base.entities.FireBrigade;
import sos.base.entities.Refuge;
import sos.base.move.types.StandardMove;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSFireZoneManager;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.base.tools.BuildingBlock;
import sos.fire_v2.base.tools.FireUtill;
import sos.fire_v2.base.worldmodel.FireWorldModel;
public abstract class SOSTargetChooser2012 implements SelectBuildingConstants {
	/////////////////COnstatnts////////////////////////////
	public static short DISTANCE_PRIORITY = -10;
	public static double SPREAD_ANGLE = 60;
	public static short PRE_EX_MINIMUM_TEMPERATURE = 0;
	//	public static short PRE_EX_LARGE_BUILDING_AREA_NEAR_SMALL_BUILDING = 500;
	//	public static short PRE_EX_LARGE_BUILDING_TEMPERATURE_NEAR_SMALL_BUILDING = 10;
	public static short PRE_EX_LARGE_BUILDING_NEAR_SMALL_BUILDING_AREA = 10;
	public static short PRE_EX_MINIMUM_AREA = 10;
	///////////////////////////////////////////////////////
	protected FireWorldModel model;
	protected SOSLoggerSystem log;
	protected int NEAR_BUILDING_MOVE_WEIGHT = 30;
	protected ArrayList<Building> extraBuildings = new ArrayList<Building>();
	protected ArrayList<Building> bs = new ArrayList<Building>();
	protected ArrayList<Building> ns = new ArrayList<Building>();

	public SOSTargetChooser2012(SOSFireZoneManager fireSiteManager, FireWorldModel model) {
		this.model = model;
		log = new SOSLoggerSystem(model.owner().me(), "SOSTargetChooser/", true, OutputType.File, true);
		setConstants();
	}

	protected void setConstants() {

	}

	public Building getBestTarget(SOSEstimatedFireZone site) {
		Building best = null;
		long t1 = System.currentTimeMillis();
		log.logln("---------------------- " + model.time() + "-------------------------------------");
		log.logln("Target From " + site + site.getSize());
		bs.clear();
		ns.clear();
		//		ConvexHull_arr_New conv = site.getOuterConvex().getScaleConvex(0.7f);
		//		for (Building b : site.getOuter()) {
		//			if (site.getFires().size() > 10) {
		//				if (!conv.getShape().contains(b.getX(), b.getY())) {
		//					bs.add(b);
		//				}
		//			} else {
		//				bs.add(b);
		//			}
		//		}
		//		for (Building b : bs) {
		//			for (Building n : b.realNeighbors_Building()) {
		//				int fn = n.virtualData[0].getFieryness();
		//				if (fn == 0 || fn > 3 && fn < 7)
		//					if (!(n instanceof Center || n instanceof Refuge))
		//						if (!ns.contains(n)) {
		//							if (site.getFires().size() > 10) {
		//								if (!conv.getShape().contains(n.getX(), n.getY()))
		//									ns.add(n);
		//							} else
		//								ns.add(n);
		//						}
		//			}
		//		}
		bs.addAll(site.getOuter());
		ns.addAll(site.getSafeBuilding());
		log.logln("bs: " + bs);
		log.logln("ns: " + ns);
		log.info("time1---> " + (System.currentTimeMillis() - t1));
		best = getExtinguishTarget(site);
		log.info("time2---> " + (System.currentTimeMillis() - t1));

		return best;
	}

	protected Building getExtinguishTarget(SOSEstimatedFireZone site) {
		Building best = null;
		reset();
		setFilters(site);
		computs();
		printIDs();
		setPriority(site);
		bs.addAll(ns);
		Collections.sort(bs, new Comparator<Building>() {

			@Override
			public int compare(Building o1, Building o2) {
				return o2.priority() - o1.priority();
			}
		});
		if (bs.size() > 0) {
			best = bs.get(0);
			log.logln("Selected large" + best + " with score: " + best.priority());
		}
		//		if (best != null ) {
		//			log.warn("select null because all of bulidngs dont have conditions");
		//			return null;
		//		}
		return best;
	}

	protected abstract void setPriority(SOSEstimatedFireZone site);

	protected void EX_E_setPriorityForNeutral(Building b, int i) {
		if (b.virtualData[0].getTemperature() < 5)
			b.addPriority(i, "Neutral");
	}

	protected void EX_EP_setPriorityForBigBuilding(Building b) {
		if (b.getGroundArea() > 3000) {
			if (b.virtualData[0].getTemperature() > 100)
				b.addPriority(-b.getGroundArea() * 2, "BigArea");
			if (b.virtualData[0].getTemperature() < 50)
				if (b.distance(model.owner().me()) < AbstractFireBrigadeAgent.maxDistance)
					b.addPriority(1300, "BigAreaMinTemp");

		}
	}

	protected void EX_P_setPriorityForBigBuilding(Building b) {
		if (b.virtualData[0].getTemperature() > PRE_EX_MINIMUM_TEMPERATURE) {
			if (b.distance(model.owner().me()) < AbstractFireBrigadeAgent.maxDistance)
				b.addPriority(b.getGroundArea() * 2, "area");
			else
				b.addPriority(b.getGroundArea() / 4, "area");

		}
		if (b.virtualData[0].getTemperature() > 3 * PRE_EX_MINIMUM_TEMPERATURE) {
			if (b.getGroundArea() > 2500)
				if (b.distance(model.owner().me()) < AbstractFireBrigadeAgent.maxDistance)
					b.addPriority(10000, "area");
				else
					b.addPriority(1200, "area");
		}

	}

	protected void EX_E_setPriorityForFireNess(Building b, int i) {
		ScoreConstant constant = new ScoreConstant();
		switch (b.virtualData[0].getFieryness()) {
		case 0:
			b.addPriority(i * constant.fireness[0], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		case 1:
			b.addPriority(i * constant.fireness[1], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		case 2:
			b.addPriority(i * constant.fireness[2], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		case 3:
			b.addPriority(i * constant.fireness[3], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		case 4:
			b.addPriority(i * constant.fireness[4], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		case 5:
			b.addPriority(i * constant.fireness[5], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		case 6:
			b.addPriority(i * constant.fireness[6], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		case 7:
			b.addPriority(i * constant.fireness[7], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		case 8:
			b.addPriority(i * constant.fireness[8], "FireNess=" + b.virtualData[0].getFieryness());
			break;
		}
	}

	protected void EX_E_setPriorityForUnburnedNeighbours(Building b, int i) {
		int num = 0;
		for (Building n : b.realNeighbors_Building()) {
			if (n.virtualData[0].getFieryness() == 0 || n.virtualData[0].getFieryness() == 4) {
				num++;
			}
		}
		b.addPriority(num * i, "UnBurned Neighbours");
	}

	protected boolean isLarge(SOSEstimatedFireZone site) {
		//TODO 
		return false;
	}

	protected void EX_EP_setPriorityForDistance(Building b, int priority) {
		b.addPriority(model.sosAgent().move.getMovingTimeFrom(b.getFireBuilding().getExtinguishableArea().getCostToCustom()) * priority, "Distance");
	}

	protected void reset() {
		for (Building b : bs) {
			b.resetPriority();
		}
		for (Building n : ns) {
			n.resetPriority();
		}
	}

	protected void setFilters(SOSEstimatedFireZone fireSite) {
		filterUnReachableForExitnguish();
		filterInConvexed(fireSite);
		//		if (strategy == 0) {
		//			filterRefugesAndCenters();
		//			filterForBuildingsWithZiroTemprature();
		//			filterPreExtinguishInConvexBuildings(fireSite);
		//			Pre_FilterBuildingInMapSideBuildings();
		//		} else
		//			filterInConvexBuildings(fireSite);
		//	}
	}

	private void filterInConvexed(SOSEstimatedFireZone fireSite) {
		if (bs.size() > 10) {
			Shape convex;
			if (bs.size() < 25)
				convex = fireSite.getConvex().getScaleConvex(0.6f).getShape();
			else
				convex = fireSite.getConvex().getScaleConvex(0.8f).getShape();
			Building building;
			for (Iterator<Building> iterator = bs.iterator(); iterator.hasNext();) {
				building = iterator.next();
				if (convex.contains(building.getX(), building.getY()))
					iterator.remove();
			}
			for (Iterator<Building> iterator = ns.iterator(); iterator.hasNext();) {
				building = iterator.next();
				if (convex.contains(building.getX(), building.getY()))
					iterator.remove();
			}

		}

	}

	protected void filterRefugesAndCenters() {
		for (Iterator<Building> iterator = bs.iterator(); iterator.hasNext();) {
			Building building = iterator.next();
			if ((building instanceof Refuge || building instanceof Center) && !building.isBurning())
				iterator.remove();
		}
		for (Iterator<Building> iterator = ns.iterator(); iterator.hasNext();) {
			Building building = iterator.next();
			if ((building instanceof Refuge || building instanceof Center) && !building.isBurning())
				iterator.remove();
		}

	}

	protected void filterUnReachableForExitnguish() {
		log.log("filterUnReachableForExitnguish : \t");

		for (Iterator<Building> iterator = bs.iterator(); iterator.hasNext();) {
			Building b = iterator.next();
			if (b.getFireBuilding().getExtinguishableArea().isReallyUnReachableCustom()) {
				b.addPriority(-999, "UnReachable");
				log.log(b.getID().getValue() + " \t");
				iterator.remove();
				continue;
			}
			if (b.getFireBuilding().getExtinguishableArea().getBuildingsShapeInArea().isEmpty() && b.getFireBuilding().getExtinguishableArea().getRoadsShapeInArea().isEmpty()) {
				log.error("why it come here????" + b + " both ExtinguishableBuildings and ExtinguishableRoads are empty");
				b.addPriority(-999, "No Extinguishable area");
				iterator.remove();
			}
		}
		for (Iterator<Building> iterator = ns.iterator(); iterator.hasNext();) {
			Building b = iterator.next();
			if (b.getFireBuilding().getExtinguishableArea().isReallyUnReachableCustom()) {
				log.log(b.getID().getValue() + " \t");
				b.addPriority(-999, "UnReachable");
				iterator.remove();
				continue;
			}
			if (b.getFireBuilding().getExtinguishableArea().getBuildingsShapeInArea().isEmpty() && b.getFireBuilding().getExtinguishableArea().getRoadsShapeInArea().isEmpty()) {
				log.error("why it come here????" + b + " both ExtinguishableBuildings and ExtinguishableRoads are empty");
				b.addPriority(-999, "No Extinguishable area");
				iterator.remove();
			}
		}
		log.logln("");
	}

	/**
	 * CHECKED : OK
	 * TESTED ://TODO
	 * 
	 * @param b
	 *            safeBuilding for PreExtinguish
	 * @param priority
	 */
	protected void EX_P_setPreExtinguishProrityForLargBuildingsNearSmallFireBuilding(Building b, int priority) {
		if (b.getTotalArea() < PRE_EX_MINIMUM_AREA)
			return;//			b.addPriority(-priority * 1000, "min area");
		if (b.virtualData[0].getTemperature() < PRE_EX_MINIMUM_TEMPERATURE)
			return;

		int num = 0;
		for (Building n : b.realNeighbors_Building()) {
			if (n.virtualData[0].isBurning() && n.getTotalArea() < PRE_EX_LARGE_BUILDING_NEAR_SMALL_BUILDING_AREA) {
				num++;
			}

		}
		if (num > 2)
			b.addPriority(priority, "PRE_EX_ LARGE_BUILDING_NEAR_SMALL_FIRE_BUILDING");
		//		log("Pre_Large            ");
	}

	protected void EX_P_setPriorityForCriticalTempratureBuildings(Building n, int priority) {
		//		extraBuildings.clear();
		//		for (Building n : ns) {

		if (n.virtualData[0].getTemperature() > 30 && n.virtualData[0].getTemperature() < 50)
			if (!n.isMapSide()) {//TODO
				//					if (FireUtill.distance((FireBrigade) model.me(), n) < AbstractFireBrigadeAgent.maxDistance)
				//					if (!extraBuildings.contains(n)) {
				n.addPriority(priority, "CRITICAL_TEMPERATURE (over 30)");
				//						extraBuildings.add(n);
				//				}
				//				}
				if (n.virtualData[0].getTemperature() > 40)
					if (model.sosAgent().getVisibleEntities(Building.class).contains(n))
						if (n.distance(model.owner().me()) < AbstractFireBrigadeAgent.maxDistance) {
							n.addPriority(priority, "CRITICAL_TEMPERATURE (over 40)");
							if (n.getGroundArea() > 3000)
								n.addPriority(priority, "CRITICAL_TEMPERATURE (over 40 for big building)");

						}
			}
	}

	protected void EX_E_setPriorityForCenters(int priority) {
		for (Center c : model.centers()) {
			if (model.owner().messageSystem.isUsefulCenter(c))
				if ((bs.contains(c) || ns.contains(c)) && c.virtualData[0].getTemperature() >= 10)
					c.addPriority(priority, "CENETER");
		}

	}

	// Morteza2012********************************************************************************************
	protected void EX_E_setPriorityForBuildingNotInMapSideBuildings(Building b, int priority) {
		if (!b.isMapSide()) {
			b.addPriority(priority, "MAP_SIDE");
		}
		//		log("NotInMapSide         ");
	}

	// Morteza2012********************************************************************************************
	protected void EX_E_setPriorityForSpread(int priority, SOSEstimatedFireZone site) {

		double x1, y1;
		Pair<Double, Double> spread = site.spread;
		x1 = spread.first();
		y1 = spread.second();
		double length = Math.sqrt(x1 * x1 + y1 * y1);
		priority = (int) (priority * length);
		for (Building b : bs) {
			double a3 = getAngleBetweenTwoVector(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY());
			int x = (int) (Math.abs(a3) / 30d);
			b.addPriority((priority / (x + 1)), "SPREAD");
			if (a3 > 2 * SPREAD_ANGLE) {
				b.addPriority(-priority, "FILTER_SPREAD");
			}
			////////////////////////////

			if (Math.abs(a3) < SPREAD_ANGLE) {
				if (isRighTurn(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY())) {
					if (model.owner().getID().getValue() % 2 == 0) {
						b.addPriority(Math.min(priority / 2, 1000), "Group1");
					}
				} else {
					if (model.owner().getID().getValue() % 2 == 1) {
						b.addPriority(Math.min(priority / 2, 1000), "Group2");
					}
				}
				//			b.addPriority((priority / (x+1)), "SPREAD");
			}

			/////////////////

		}

		for (Building b : ns) {
			double a3 = getAngleBetweenTwoVector(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY());
			//			if (Math.abs(a3) < SPREAD_ANGLE) {
			int x = (int) (Math.abs(a3) / 30d);
			b.addPriority((priority / ((x + 1))), "SPREAD");

			//			b.addPriority(priority, "SPREAD");
			//			}
			if (a3 > 2 * SPREAD_ANGLE) {
				b.addPriority(-priority * 10, "FILTER_SPREAD");
			}

			if (Math.abs(a3) < SPREAD_ANGLE) {
				if (isRighTurn(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY())) {
					if (model.owner().getID().getValue() % 2 == 0) {
						b.addPriority(Math.min(priority / 2, 1000), "Group1");
					}
				} else {
					if (model.owner().getID().getValue() % 2 == 1) {
						b.addPriority(Math.min(priority / 2, 1000), "Group2");
					}
				}
				//			b.addPriority((priority / (x+1)), "SPREAD");
			}

		}

	}

	private double getAngleBetweenTwoVector(double d1x, double d1y, double d2x, double d2y) {
		double length1 = Math.hypot(d1x, d1y);
		double length2 = Math.hypot(d2x, d2y);
		//		d1x = d1x * 1 / length1;
		//		d1y = d1y * 1 / length1;
		//		d2x = d2x * 1 / length2;
		//		d2y = d2y * 1 / length2;
		double cos = (d1x * d2x + d1y * d2y) / (length1 * length2);

		if (cos > 1) {
			cos = 1;
		}
		double angle = Math.toDegrees(Math.acos(cos));
		return angle;

	}

	// Morteza2012********************************************************************************************
	protected void EX_E_setPriorityForSpreadForLargeFireSites(int priority, SOSEstimatedFireZone site) {
		double x1, y1, x2, y2, x3, y3;
		Pair<Double, Double> spread = site.spread;
		x1 = spread.first();
		y1 = spread.second();
		x2 = .5 * x1 + .85 * y1;
		y2 = .5 * y1 - .85 * x1;
		x3 = .5 * x1 - .85 * y1;
		y3 = .5 * y1 + .85 * x1;
		int cx = site.getCenterX(), cy = site.getCenterY();
		int[] xp = { cx, (int) (cx + x2 * 1000000), (int) (cx + x3 * 1000000) };
		int[] yp = { cy, (int) (cy + y2 * 1000000), (int) (cy + y3 * 1000000) };
		Shape sh = new Polygon(xp, yp, 3);
		for (Building b : bs) {
			if (sh.contains(b.x(), b.y())) {
				b.addPriority(priority, "SPREAD LARGE");
			}
		}
		log("Spread priority      ");
	}

	public boolean isBigMap() {
		if (model.owner().getMapInfo().isBigMap() || model.owner().getMapInfo().isMediumMap())
			return true;
		return false;
	}

	protected void EX_E_setPriorityForBuildingsInNewRoadSites(int priority, Building b) {

		for (BuildingBlock bb : model.buildingBlocks()) {
			if (bb.isFireNewInBuildingBlock())
				if (bb.buildings().contains(b))
					b.addPriority(priority, "new Road Site");
		}
		log("InNewRoadSites       ");
	}

	protected void EX_P_setPriorityForUnBurnedIsLands(Building b, int priority) {
		//		log("IslandCovers         ");
		if (b.getTotalArea() < PRE_EX_MINIMUM_AREA)
			return;
		if (b.virtualData[0].getTemperature() < 10)
			return;
		if (b.virtualData[0].getFieryness() != 0)
			return;
		//		if (is.size() <= 1)
		//			return;
		if (!b.getFireBuilding().island().isFireNewInIsland())
			return;
		if (!b.getFireBuilding().island().insideCoverBuildings().contains(b))
			return;
		if (b.virtualData[0].getTemperature() < 20)
			priority /= 2;
		if (b.getFireBuilding().island().isImportant())
			b.addPriority(priority, "PRIORITY_FOR_UNBURNED_ISLAND");
		else
			b.addPriority(priority / 2, "PRIORITY_FOR_UNBURNED_ISLAND");

	}

	protected void EX_P_setPriorityForUnBurnedRoadSites(Building b, int priority) {
		//		log("IslandCovers         ");
		if (b.getTotalArea() < PRE_EX_MINIMUM_AREA)
			return;
		if (b.virtualData[0].getTemperature() < 10)
			return;
		if (b.virtualData[0].getFieryness() != 0)
			return;
		//		if (is.size() <= 1)
		//			return;
		if (!b.getFireBuilding().buildingBlock().isFireNewInBuildingBlock())
			return;
		if (!b.getFireBuilding().buildingBlock().insideCoverBuildings().contains(b))
			return;
		if (b.virtualData[0].getTemperature() < 20)
			priority /= 2;
		if (b.getFireBuilding().buildingBlock().isImportant())
			b.addPriority(priority, "PRIORITY_FOR_UNBURNED_ISLAND");
		else
			b.addPriority(priority / 2, "PRIORITY_FOR_UNBURNED_ISLAND");

	}

	// Morteza2012********************************************************************************************
	protected void EX_E_setPriorityForEarlyIgnitedBuildings(Building b, int priority) {
		//		log("EarlyIgnitedBuildings");
		if (b.virtualData[0].isExtinguishableInOneCycle(AbstractFireBrigadeAgent.maxPower)) {
			b.addPriority(priority, "EARLY IGNITED");
		}
	}

	protected void EX_E_setPriorityForBuildingsInNewIslands(int priority, SOSEstimatedFireZone site, Building b) {
		for (BuildingBlock bb : model.islands()) {
			if (bb.isFireNewInBuildingBlock())
				if (bb.buildings().contains(b)) {
					if (FireUtill.distance(model.owner().me(), b) > AbstractFireBrigadeAgent.maxDistance && model.owner().move.getWeightTo(b.fireSearchBuilding().sensibleAreasOfAreas(), StandardMove.class) > NEAR_BUILDING_MOVE_WEIGHT) {
						short nearestAgents = 0;
						for (FireBrigade fb : site.getAssignedAgent()) {
							if (FireUtill.distance(fb, b) < AbstractFireBrigadeAgent.maxDistance)
								nearestAgents++;
						}
						if (nearestAgents < 5) {
							b.addPriority(priority, "NEW ISLAND");
						}
					} else {
						b.addPriority(priority, "NEW ISLAND");
					}
					if ((!SOSConstant.IS_CHALLENGE_RUNNING) && model.islands().lastIndexOf(bb) != model.islands().indexOf(bb))
					{
						log.error(new Error("islands added repeatedly"));
					}
					break;
				}
		}
	}

	// Morteza2012********************************************************************************************
	protected void computs() {
		model.updateBuildingBlocks();
		//		computeIslandsPercentage();
		//		computeRoadSitePercentage();
	}

	//	// Morteza2012********************************************************************************************
	//	protected void computeIslandsPercentage() {
	//		log.logln("ComputeIslandPercents : ");
	//		for (Building b : bs) {
	//			if (!is.contains(b.getFireBuilding().island())) {
	//				is.add(b.getFireBuilding().island());
	//			}
	//		}
	//		for (Building b : ns) {
	//			if (!is.contains(b.getFireBuilding().island())) {
	//				is.add(b.getFireBuilding().island());
	//			}
	//		}
	//		for (BuildingBlock bb : is) {
	//			int num = 0;
	//			for (Building b : bb.buildings()) {
	//				int fn = b.virtualData[0].getFieryness();
	//				if (fn > 0 && fn < 4 || fn == 8)
	//					num++;
	//			}
	//			bb.setPercent(num);
	//			log.logln(bb.buildings().get(0) + " : " + bb.percent() + " >> isNew: " + ((num <= 5 || num * 100 / bb.buildings().size() < 12 && num <= 8)) + " >> IsImportant : " + bb.isImportant());
	//		}
	//	}

	// Morteza2012********************************************************************************************
	//	protected void computeRoadSitePercentage() {
	//		for (Building b : bs) {
	//			if (!rs.contains(b.getFireBuilding().buildingBlock())) {
	//				rs.add(b.getFireBuilding().buildingBlock());
	//			}
	//		}
	//		for (Building b : ns) {
	//			if (!rs.contains(b.getFireBuilding().buildingBlock())) {
	//				rs.add(b.getFireBuilding().buildingBlock());
	//			}
	//		}
	//		for (BuildingBlock bb : rs) {
	//			int num = 0;
	//			for (Building b : bb.buildings()) {
	//				int fn = b.virtualData[0].getFieryness();
	//				if (fn > 0 && fn < 4 || fn == 8)
	//					num++;
	//			}
	//			bb.setPercent(num);
	//		}
	//	}

	// Morteza2012********************************************************************************************
	protected void printIDs() {
		String s = "                        \t";
		for (Building b : bs) {
			s += b.getID().getValue();
			for (int i = 1; i <= 5; i *= 10) {
				if (b.getID().getValue() / i == 0)
					s += " ";
			}
			s += " \t\t";
		}
		for (Building b : ns) {
			s += b.getID().getValue();
			for (int i = 1; i <= 5; i *= 10) {
				if (b.getID().getValue() / i == 0)
					s += " ";
			}
			s += " \t\t";
		}
		log.logln(s);
	}

	// Morteza2012********************************************************************************************
	protected void log(String reason) {
		String s = reason + " : \t";
		for (Building b : bs) {
			s += "B:";
			s += b;
			s += b.priority();
			s += " \t";
		}
		for (Building b : ns) {
			s += "N:";
			s += b;
			s += b.priority();
			s += " \t";
		}
		log.logln(s);
	}

	public static boolean isRighTurn(double x1, double y1, double x2, double y2) {
		double t = (x1 * y2) - (y1 * x2);
		return t < 0;
	}

	//	if (isRightTurn(first, second)) {
	//		// CHECKSTYLE:OFF:MagicNumber
	//		return 360.0 - angle;
	//		// CHECKSTYLE:ON:MagicNumber
	//	} else {
	//		return angle;
	//	}
	protected void EX_EP_setPriorityForFireBrigadeGroup(int priority, SOSEstimatedFireZone site) {

		double x1, y1;
		Pair<Double, Double> spread = site.spread;
		x1 = spread.first();
		y1 = spread.second();
		for (Building b : bs) {
			double a3 = getAngleBetweenTwoVector(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY());
			if (Math.abs(a3) < SPREAD_ANGLE) {
				if (isRighTurn(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY())) {
					if (model.owner().getID().getValue() % 2 == 0) {
						b.addPriority(priority, "Group1");
					}
				} else {
					if (model.owner().getID().getValue() % 2 == 1) {
						b.addPriority(priority, "Group1");
					}
				}
				//			b.addPriority((priority / (x+1)), "SPREAD");
			}
		}

		for (Building b : ns) {
			double a3 = getAngleBetweenTwoVector(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY());
			if (Math.abs(a3) < SPREAD_ANGLE) {
				if (isRighTurn(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY())) {
					if (model.owner().getID().getValue() % 2 == 0) {
						b.addPriority(priority, "Group1");
					}
				} else {
					if (model.owner().getID().getValue() % 2 == 1) {
						b.addPriority(priority, "Group1");
					}
				}
				//			b.addPriority((priority / (x+1)), "SPREAD");
			}
		}

	}

}
