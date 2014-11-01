package sos.fire_v2.position;

import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.base.move.Move;
import sos.base.move.types.StandardMove;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class PositioningCostEvaluator {
	private FireWorldModel world;
	private FireBrigadeAgent me;
	private Move move;
	protected SOSAgent agent;
	private SOSLoggerSystem log;

	public PositioningCostEvaluator(FireBrigadeAgent fireBrigadeAgent, FireWorldModel model) {
		this.me = fireBrigadeAgent;
		this.world = model;
		move = me.move;
		log = new SOSLoggerSystem(world.me(), "PositioningCostEvaluator", true, OutputType.File, true);
		fireBrigadeAgent.sosLogger.addToAllLogType(log);
	}

	public long PositioningTime(Building b) {

		//		long upd=me.model().time() - b.updatedtime();
		long visibilitycost = move.getWeightToLowProcess(b.getFireBuilding().getExtinguishableArea().getExtinguishableSensibleArea(), StandardMove.class);
		long greedycost = move.getWeightToLowProcess(b.getFireBuilding().getExtinguishableArea().getRoadsShapeInArea(), StandardMove.class);
		int greedyTime = move.getMovingTimeFrom(greedycost);
		int visibilityTime = move.getMovingTimeFrom(visibilitycost);
		if (isGreedyPosition(b))
			return greedyTime;
		else
			return visibilityTime;
	}

	
	public boolean isGreedyPosition(Building b) {
		log.info("Building : " + b.getID());
		long upd = me.model().time() - b.updatedtime();
		if (me.getVisibleEntities(Building.class).contains(b)) {
			log.info(" selected building is in visible entities");
			log.info("Greedy position TRUE");
			return true;
		}
		if (upd < 5 && (sos.tools.Utils.distance(me.me().getX(), me.me().getY(), b.x(), b.y()) <= AbstractFireBrigadeAgent.maxDistance)) {
			log.info("update time is less than 5 and agent can extinguish");
			log.info("Greedy position TRUE");
			return true;
		}
		long visibilitycost = move.getWeightToLowProcess(b.getFireBuilding().getExtinguishableArea().getExtinguishableSensibleArea(), StandardMove.class);
		long greedycost = move.getWeightToLowProcess(b.getFireBuilding().getExtinguishableArea().getRoadsShapeInArea(), StandardMove.class);
		int greedyTime = move.getMovingTimeFrom(greedycost);
		if (greedyTime == 0)
			greedyTime = 1;
		int visibilityTime = move.getMovingTimeFrom(visibilitycost);

//		if (UnBurnedRoadSite(b) && visibilityTime < 4) {
//
//			log.info("Greedy position False");
//			return false;
//		}

		log.info("messageType= " + me.messageSystem.type);
		log.info("Visibility Cost = " + visibilitycost);
		log.info("Visibility time = " + visibilityTime);
		log.info("Greedy Cost = " + greedycost);
		log.info("Greedy Time = " + greedyTime);
		log.info("dUpd = " + upd);
		
		switch (me.messageSystem.type) {
		case LowComunication: {
			log.info("Greedy position " + lowCommunication(b, visibilityTime, greedyTime));

			return lowCommunication(b, visibilityTime, greedyTime);
		}
		case NoComunication: {
			log.info("Greedy position " + NoComunication(b, visibilityTime, greedyTime));
			return NoComunication(b, visibilityTime, greedyTime);
		}
		default: {
			log.info("Greedy position " + FullComunication(b, visibilityTime, greedyTime));
			return FullComunication(b, visibilityTime, greedyTime);
		}
		}

	}

	private boolean lowCommunication(Building b, long visibilityTime, long greedyTime) {
		long upd = me.model().time() - b.updatedtime();
		if (visibilityTime > 5 && upd < 10)
			return true;
		return false;
	}

	private boolean NoComunication(Building b, long visibilityTime, long greedyTime) {
		long upd = me.model().time() - b.updatedtime();
		if (upd > 10)
			return false;
		if (visibilityTime > upd)
			return true;
		return false;
	}

	private boolean FullComunication(Building b, long visibilityTime, long greedyTime) {
		long upd = me.model().time() - b.updatedtime();
		if (upd > 10)
			return false;
		
		if ((visibilityTime / greedyTime) > upd)
			return true;
		return false;
	}

	protected boolean UnBurnedRoadSite(Building b) {
		//	log.info("Building: " + b.getID());
		//log.info("Building block : " + b.getID());
		if (b.getFireBuilding().buildingBlock().isFireNewInBuildingBlock()) {
			log.info("unburned Road site  True");
			return true;
		}
		log.info("unburned Road site  False");
		return false;
	}

}
