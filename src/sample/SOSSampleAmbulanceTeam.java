package sample;

 import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import rescuecore2.log.Logger;
import rescuecore2.standard.entities.StandardEntityURN;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.move.Path;
import sos.base.util.DistanceSorter;
import sos.base.util.SOSActionException;


/**
   A sample ambulance team agent.
 */
public class SOSSampleAmbulanceTeam extends SOSAbstractSampleAgent<AmbulanceTeam> {
//    private Collection<EntityID> unexploredBuildings;

    @Override
    public String toString() {
        return "Sample ambulance team";
    }

    @Override
    protected void postConnect() throws Exception {
        super.postConnect();
//        model().indexClass(StandardEntityURN.CIVILIAN, StandardEntityURN.FIRE_BRIGADE, StandardEntityURN.POLICE_FORCE, StandardEntityURN.AMBULANCE_TEAM, StandardEntityURN.REFUGE, StandardEntityURN.BUILDING);
    }

    @Override
    protected void think() throws SOSActionException {
        // Am I transporting a civilian to a refuge?
        if (someoneOnBoard()) {
            // Am I at a refuge?
            if (location() instanceof Refuge) {
                // Unload!
                Logger.info("Unloading");
                sendUnload();
                return;
            }
            else {
                // Move to a refuge
                Path path = search.breadthFirstSearch(me().getAreaPosition(), model().refuges());
                if (path != null) {
                    Logger.info("Moving to refuge");
                    move(path);
                    return;
                }
                // What do I do now? Might as well carry on and see if we can dig someone else out.
                Logger.debug("Failed to plan path to refuge");
            }
        }
        // Go through targets (sorted by distance) and check for things we can do
        for (Human next : getTargets()) {
            if (next.getPosition().equals(location())) {
                // Targets in the same place might need rescueing or loading
                if ((next instanceof Civilian) && next.getBuriedness() == 0 && !(location() instanceof Refuge)) {
                    // Load
                    Logger.info("Loading " + next);
                    sendLoad(next);
                    return;
                }
                if (next.getBuriedness() > 0) {
                    // Rescue
                    Logger.info("Rescueing " + next);
                    sendRescue(next);
                    return;
                }
            }
            else {
                // Try to move to the target
                Path path = search.breadthFirstSearch(me().getAreaPosition(), next.getAreaPosition());
                if (path != null) {
                    Logger.info("Moving to target");
                    move(path);
                    return;
                }
            }
        }

        randomWalk();

    }

    @Override
    protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
        return EnumSet.of(StandardEntityURN.AMBULANCE_TEAM);
    }

    private boolean someoneOnBoard() {
        for (Civilian next : model().civilians()) {
            if (next.isPositionDefined()&&next.getPosition().equals(me())) {
                Logger.debug(next + " is on board");
                return true;
            }
        }
        return false;
    }

    private List<Human> getTargets() {
        List<Human> targets = new ArrayList<Human>();
        for (Human h: model().humans()) {
            if (h == me()) {
                continue;
            }
            if (h.isHPDefined()
                && h.isBuriednessDefined()
                && h.isDamageDefined()
                && h.isPositionDefined()
                && h.getHP() > 0
                && (h.getBuriedness() > 0 || h.getDamage() > 0)) {
                targets.add(h);
            }
        }
        Collections.sort(targets, new DistanceSorter(location(), model()));
        return targets;
    }

//    private void updateUnexploredBuildings(ChangeSet changed) {
//        for (EntityID next : changed.getChangedEntities()) {
//            unexploredBuildings.remove(next);
//        }
//    }


}
