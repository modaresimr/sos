package sos.fire_v2.target.building;

 import sos.base.entities.Building;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSFireZoneManager;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class SOSTargetChooser2012_SmallMap extends SOSTargetChooser2012 implements SelectBuildingConstants {

	public SOSTargetChooser2012_SmallMap(SOSFireZoneManager fireSiteManager, FireWorldModel model) {
		super(fireSiteManager, model);
	}

	@Override
	protected void setConstants() {
		super.setConstants();
		DISTANCE_PRIORITY = -100;
		if (model.sosAgent().messageSystem.type == Type.NoComunication)
			DISTANCE_PRIORITY = -100 * 5;

		PRE_EX_MINIMUM_TEMPERATURE = 10;
		//		PRE_EX_LARGE_BUILDING_AREA_NEAR_SMALL_BUILDING = 100;
		//		PRE_EX_LARGE_BUILDING_TEMPERATURE_NEAR_SMALL_BUILDING = 10;
		PRE_EX_LARGE_BUILDING_NEAR_SMALL_BUILDING_AREA = 100;
		PRE_EX_MINIMUM_AREA = 100;
	}

	/**
	 * Yoosef
	 * 
	 * @param site
	 */
	@Override
	protected void setPriority(SOSEstimatedFireZone site) {
		if (model.getInnerOfMap().contains(site.getCenterX(), site.getCenterY()))
			SPREAD_ANGLE = 90;
		else
			SPREAD_ANGLE = 60;
		///////FILTERS////////////
		filterRefugesAndCenters();
		////////SPREAD//////////
		//						EX_E_setPriorityForSpreadForLargeFireSites(20000, site);
		long t1 = System.currentTimeMillis();
		EX_E_setPriorityForSpread(Math.min(10000, site.getAllBuildings().size() * 20), site);
		log("time for spread " + (System.currentTimeMillis() - t1));
		//////////////ILAND_ROADSITE////////////////
		//		log(" time Road site " + (System.currentTimeMillis() - t1));
		//		log("time for new iland" + (System.currentTimeMillis() - t1));
		////////////////////////
		//select center if burning
		EX_E_setPriorityForCenters(1000000);
		for (Building b : bs) {
			EX_E_setPriorityForBuildingsInNewIslands(1000, site, b);
			EX_E_setPriorityForBuildingsInNewRoadSites(400, b);
			EX_EP_setPriorityForDistance(b, DISTANCE_PRIORITY);
			EX_E_setPriorityForEarlyIgnitedBuildings(b, 110);
			EX_E_setPriorityForBuildingNotInMapSideBuildings(b, 1000);
			EX_E_setPriorityForUnburnedNeighbours(b, 140);
			EX_E_setPriorityForFireNess(b, 20);
			EX_E_setPriorityForNeutral(b, -100000);

			if (isLarge(site)) {
			}
		}
		log("\n\n time after for1   " + (System.currentTimeMillis() - t1) + "      size " + bs.size());

		for (Building n : ns) {
			EX_E_setPriorityForBuildingsInNewIslands(1000, site, n);
			EX_E_setPriorityForBuildingsInNewRoadSites(400, n);
			EX_EP_setPriorityForDistance(n, DISTANCE_PRIORITY);
			EX_E_setPriorityForEarlyIgnitedBuildings(n, 110);
			EX_E_setPriorityForBuildingNotInMapSideBuildings(n, 1000);
			EX_P_setPreExtinguishProrityForLargBuildingsNearSmallFireBuilding(n, 200);//checked
			EX_P_setPriorityForCriticalTempratureBuildings(n, 250);
			EX_P_setPriorityForUnBurnedIsLands(n, 2000);
			EX_E_setPriorityForNeutral(n, -100000);

		}
		log("time after for2   " + (System.currentTimeMillis() - t1) + "   size " + ns.size());

	}
}