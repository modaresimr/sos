package sos.fire_v2.decision.states;

import java.util.ArrayList;
import java.util.List;

import sos.base.entities.Building;
import sos.base.entities.FireBrigade;
import sos.base.entities.Refuge;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.base.tools.SOSFireProbability;
import sos.fire_v2.decision.FireInformationModel;
import sos.fire_v2.decision.tasks.ExtinguishTask;
import sos.fire_v2.decision.tasks.ShapeSearchTask;
import sos.fire_v2.target.Tools;
import sos.search_v2.tools.cluster.ClusterData;
import sos.tools.decisionMaker.definitions.commands.SOSTask;
import sos.tools.decisionMaker.implementations.stateBased.SOSEventPool;
import sos.tools.decisionMaker.implementations.stateBased.events.SOSEvent;
import sos.tools.decisionMaker.implementations.stateBased.states.SOSIState;
import sos.tools.decisionMaker.implementations.targets.ListTarget;

public class FireProbExtinguish extends SOSIState<FireInformationModel> {

	private SOSFireProbability fireProb;
	private SOSLoggerSystem log;

	public FireProbExtinguish(FireInformationModel infoModel) {
		super(infoModel);
		fireProb = infoModel.getAgent().fireProbabilityChecker;
		log = infoModel.extinguishFire;
	}

	@Override
	public SOSTask<?> decide(SOSEventPool eventPool) {
		if (infoModel.getLastSelectTime() == infoModel.getTime() - 1) {
			log.info("checkin last selected fire zone in this cycle");
			if (infoModel.getLastSelectedFireZone() != null) {
				if (infoModel.getLastSelectedFireZone().isExtinguishable())
					if (Tools.isMediumFire(infoModel.getLastSelectedFireZone()) || Tools.isSmallFire(infoModel.getLastSelectedFireZone())) {
						log.debug("last selected fire zone is small or medium continue last job");
						return null;
					}
			}
		}
		if (infoModel.getAgent().me().getAreaPosition() instanceof Refuge && ((FireBrigade) infoModel.getAgent().me()).getWater() < FireBrigadeAgent.maxWater / 2)
		{
			log.info("Im in Refuge and filling water");
			return null;
		}
		ArrayList<ClusterData> probBuilding = fireProb.getClusterProbabilisticFieryBuilding();

		log.info("Fire Prob Ex :> " + probBuilding);
		FireInformationModel inmodel = infoModel;

		Building best = null;

		ArrayList<Building> nearProb = getNeatProb(probBuilding);
		log.info("Near fire Prob :> " + nearProb);

		if (nearProb.isEmpty()) {
			log.debug("near prob size is zero state finished");
			return null;
		}

		ArrayList<Building> sensibleBuildings = getSensibleBuildings(nearProb);
		log.info("area we can see " + sensibleBuildings);
		if (sensibleBuildings.size() > 0) {
			int maxScore = 0;
			for (Building b : sensibleBuildings) {
				if (b.getValuSpecialForFire() > maxScore) {
					best = b;
					maxScore = b.getValuSpecialForFire();
				}
			}
			log.info("Best area for search " + best);
			ListTarget<ShapeInArea> x = new ListTarget<ShapeInArea>(best.getFireBuilding().getExtinguishableArea().getExtinguishableSensibleArea());

			return new ShapeSearchTask(x, inmodel.getTime());
		}

		ArrayList<Building> wateryBuilding = getWateryBuilding(nearProb);
		log.info("Watery building :> " + wateryBuilding);
		if (wateryBuilding.size() > 0) {

			SOSEstimatedFireZone es = createSite(wateryBuilding);
			log.debug("site created and task finished " + es);
			inmodel.setLastSelectedFireZone(es);
			return null;
		}
		else {
			if (((FireBrigadeAgent) infoModel.getAgent()).me().getWater() == 0) {
				log.debug("i have no water state finished");
				return null;
			}

			best = getBestByProiority(nearProb);
			log.info("Best Building for Extinguish " + best);
			if (best != null) {
				//				ShapeInArea pos = ((FireBrigadeAgent) inmodel.getAgent()).positioning.getPosition(best);
				//				log.info("Position " + pos);
				//				if (pos == null)
				//					return null;
				return new ExtinguishTask(best, null, inmodel.getTime());
			}
			return null;
		}
	}

	private SOSEstimatedFireZone createSite(ArrayList<Building> wateryBuilding) {
		return infoModel.getAgent().fireSiteManager.createUnknownFireZone(wateryBuilding);
	}

	private ArrayList<Building> getWateryBuilding(ArrayList<Building> nearProb) {
		ArrayList<Building> res = new ArrayList<Building>();
		for (Building b : nearProb) {
			if (b.virtualData[0].wasEverWatered)
				res.add(b);
		}
		return res;
	}

	private ArrayList<Building> getSensibleBuildings(ArrayList<Building> nearProb) {
		ArrayList<Building> res = new ArrayList<Building>();
		for (Building b : nearProb) {
			if (infoModel.getMove().isReallyUnreachable(b.getFireBuilding().getExtinguishableArea().getExtinguishableSensibleArea())) {
				continue;
			}
			res.add(b);
		}
		return res;
	}

	private ArrayList<Building> getNeatProb(ArrayList<ClusterData> probCluster) {
		ArrayList<Building> nearProb = new ArrayList<Building>();

		for (ClusterData cd : probCluster)
		{
			boolean add = false;
			for (Building b : cd.getBuildings()) {
				int maxDis = (int) Math.max(infoModel.getModel().getBounds().getWidth() / 5, 2 * AbstractFireBrigadeAgent.maxDistance);
				if (b.distance((FireBrigade) infoModel.getAgent().me()) < maxDis) {
					add = true;
					break;
				}
			}
			if (add)
				nearProb.addAll(cd.getBuildings());
		}
		return nearProb;
	}

	private Building getBestByProiority(ArrayList<Building> probBuilding) {
		Building best = null;
		int maxScore = 0;
		for (Building b : probBuilding) {
			if (b.getValuSpecialForFire() > maxScore && !b.virtualData[0].wasEverWatered) {
				best = b;
				maxScore = b.getValuSpecialForFire();
			}
		}

		return best;
	}

	@Override
	public void giveFeedbacks(List feedbacks) {
	}

	@Override
	public void skipped() {
	}

	@Override
	public void overTaken() {
	}

	@Override
	protected void handleEvent(SOSEvent sosEvent) {
	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
	}

	@Override
	public String getName() {
		return "FireProbExtinguish";
	}

	@Override
	public void taken() {
		super.taken();
		((FireBrigadeAgent) infoModel.getAgent()).FDK.lastState = getName();
	}
}
