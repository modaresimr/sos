package sample;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import rescuecore2.log.Logger;
import rescuecore2.standard.entities.StandardEntityURN;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.FireBrigade;
import sos.base.entities.Refuge;
import sos.base.entities.StandardEntity;
import sos.base.move.Path;
import sos.base.util.DistanceSorter;
import sos.base.util.SOSActionException;


/**
   A sample fire brigade agent.
 */
public class SOSSampleFireBrigade extends SOSAbstractSampleAgent<FireBrigade> {
    private static final String MAX_WATER_KEY = "fire.tank.maximum";
    private static final String MAX_DISTANCE_KEY = "fire.extinguish.max-distance";
    private static final String MAX_POWER_KEY = "fire.extinguish.max-sum";

    private int maxWater;
    private int maxDistance;
    private int maxPower;

    @Override
    public String toString() {
        return "Sample fire brigade";
    }

    @Override
    protected void postConnect() throws Exception {
        super.postConnect();
//        model().indexClass(StandardEntityURN.BUILDING, StandardEntityURN.REFUGE);
        maxWater = config.getIntValue(MAX_WATER_KEY);
        maxDistance = config.getIntValue(MAX_DISTANCE_KEY);
        maxPower = config.getIntValue(MAX_POWER_KEY);
        Logger.info("Sample fire brigade connected: max extinguish distance = " + maxDistance + ", max power = " + maxPower + ", max tank = " + maxWater);
    }

    @Override
	protected void think() throws SOSActionException {
        FireBrigade me = me();
        // Are we currently filling with water?
        if (me.isWaterDefined() && me.getWater() < maxWater && location() instanceof Refuge) {
            Logger.info("Filling with water at " + location());
            sendRest();
        }
        // Are we out of water?
        if (me.isWaterDefined() && me.getWater() == 0) {
            // Head for a refuge
            Path path = search.breadthFirstSearch(me().getAreaPosition(), model().refuges());
            if (path != null) {
                Logger.info("Moving to refuge");
                move(path);
                return;
            }
            else {
                Logger.debug("Couldn't plan a path to a refuge.");
                randomWalk();
                return;
            }
        }
        // Find all buildings that are on fire
        Collection<Building> all = getBurningBuildings();
        // Can we extinguish any right now?
        for (Building next : all) {
            if (model().getDistance(getID(), next.getID()) <= maxDistance) {
                Logger.info("Extinguishing " + next);
                sendExtinguish(next, maxPower);
//                sendSpeak(time, 1, ("Extinguishing " + next).getBytes());
                return;
            }
        }
        // Plan a path to a fire
        for (Building next : all) {
            Path path = planPathToFire(next);
            if (path != null) {
                Logger.info("Moving to target");
                move(path);
                return;
            }
        }
        Logger.debug("Couldn't plan a path to a fire.");
        randomWalk();
    }

    @Override
    protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
        return EnumSet.of(StandardEntityURN.FIRE_BRIGADE);
    }

    private List<Building> getBurningBuildings() {
        Collection<Building> e = model().buildings();
        List<Building> result = new ArrayList<Building>();
        for (StandardEntity next : e) {
            if (next instanceof Building) {
                Building b = (Building)next;
                if (b.isOnFire()) {
                    result.add(b);
                }
            }
        }
        // Sort by distance
        Collections.sort(result, new DistanceSorter(location(), model()));
        return result;
    }

    private Path planPathToFire(Building target) {
        // Try to get to anything within maxDistance of the target
        Collection<Area> targets = model().getObjectsInRange(target.getID(), maxDistance,Area.class);
        if (targets.isEmpty()) {
            return null;
        }
        return search.breadthFirstSearch(me().getAreaPosition(), target);
    }

}
