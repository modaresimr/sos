package sos.fire_v2.target;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.SOSConstant;
import sos.base.entities.Building;
import sos.base.entities.Center;
import sos.base.entities.GasStation;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.move.MoveConstants;
import sos.base.move.types.StandardMove;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.sosLogger.TableLogger;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.base.tools.FireStarCluster;
import sos.fire_v2.decision.states.score.AbstractScore;
import sos.fire_v2.decision.states.score.DistanceScore;
import sos.fire_v2.decision.states.score.FirynessScore;

public class SOSBuildingSelector extends SOSSelectTarget<Building> {

	public FireStarCluster starCluster;
	private List<AbstractScore> scores = new ArrayList<AbstractScore>();

	public SOSBuildingSelector(SOSAgent<?> agent){
		super(agent);
		initiateScores();
	}

	private void initiateScores() {
		scores.add(new FirynessScore(agent));
		scores.add(new DistanceScore(agent));
	}

	@Override
	public void preCompute() {

	}

	@Override
	public Building getBestTarget(List<Building> validTarget) {
		double max = Integer.MIN_VALUE;
		Building best = null;

		for (Building e : validTarget) {
			if (e.priority() > max) {
				best = e;
				max = e.priority();
			}
		}
		log.info("get best Target " + best);

		return best;
	}

	@Override
	public void reset(List<Building> validTarget) {
		for (Building b : validTarget)
			b.resetPriority();
	}

	TableLogger tablelog;

	@Override
	public void setPriority(List<Building> validTarget) {
		tablelog = new TableLogger(30);
		tablelog.setPrintNull(false);
		tablelog.addColumn("Result");

		for (Building b : validTarget) {
			for (AbstractScore score : scores) {
				List<Pair<String, String>> adlog = score.getAditionalLogs(b);
				if (adlog != null)
					for (Pair<String, String> pair : adlog)
						tablelog.addScore(b.toString(), pair.first(), pair.second());

				addPriority(b, score.getScore(b), score.toString());
			}
		}
		for (Building b : validTarget)
			tablelog.addScore(b.toString(), "Result", b.priority());
		log.logln("\n" + tablelog.getTablarResult("Result"));
	}

	@Override
	public List<Building> getValidTask(Object link) {
		SOSEstimatedFireZone site = (SOSEstimatedFireZone) link;
		ArrayList<Building> res = new ArrayList<Building>();

		log.logln("Target From " + site + site.getSize());
		log.logln("bs: " + site.getOuter());
		log.logln("ns: " + site.getSafeBuilding());

		res.addAll(site.getOuter());
		res.addAll(site.getSafeBuilding());
		if (!SOSConstant.IS_CHALLENGE_RUNNING)
			reset(res);
		filterNeutral(res);
		filterRefugesAndCenters(res);
		filterUnReachableForExitnguish(res);

		return res;
	}

	private boolean isReachable(ArrayList<ShapeInArea> shapes, Building building) {

		ArrayList<ShapeInArea> temp = new ArrayList<ShapeInArea>();
		for (ShapeInArea sh : shapes) {
			//			log.info("\t\t Shape" + sh + "   Area : " + sh.getArea());
			long cost = 0;
			if (sh.getArea() instanceof Building && ((Building) sh.getArea()).virtualData[0].isBurning())
			{
				//				log.info("\t\t\t burning building filter");
				continue;

			}
			if (((Human) agent.me()).getAreaPosition().equals(sh.getArea()) && canExtinguish(building)) {
				cost = 0;
			} else {
				temp.clear();
				temp.add(sh);
				cost = agent.move.getWeightTo(temp, StandardMove.class);
				if (cost >= MoveConstants.UNREACHABLE_COST) {
					//					log.info("\t\t\t Unreachable");
					continue;
				}
			}
			if (cost < MoveConstants.UNREACHABLE_COST)
				return true;
		}
		return false;
	}

	private boolean canExtinguish(Building building) {
		return (sos.tools.Utils.distance(((Human) agent.me()).getX(), ((Human) agent.me()).getY(), building.x(), building.y()) <= AbstractFireBrigadeAgent.maxDistance);
	}

	protected void filterUnReachableForExitnguish(ArrayList<Building> buildings) {
		log.log("filterUnReachableForExitnguish : \t");

		for (Iterator<Building> iterator = buildings.iterator(); iterator.hasNext();) {
			Building b = iterator.next();

			boolean reachable = isReachable(b.getFireBuilding().getExtinguishableArea().getRoadsShapeInArea(), b);
			if (reachable)
				continue;
			reachable = isReachable(b.getFireBuilding().getExtinguishableArea().getBuildingsShapeInArea(), b);
			if (reachable)
				continue;
			log.log(b.getID().getValue() + " \t");
			b.addPriority(0, "Filter Unreachable 1");
			iterator.remove();

		}
		log.logln("");
	}

	protected void filterRefugesAndCenters(ArrayList<Building> buildings) {
		for (Iterator<Building> iterator = buildings.iterator(); iterator.hasNext();) {
			Building building = iterator.next();
			if ((building instanceof Refuge || building instanceof Center) && !building.isBurning())
			{
				building.resetPriority();
				building.addPriority(0, "Filter Ref Center");
				iterator.remove();
			}
		}

	}

	protected void filterNeutral(ArrayList<Building> buildings) {
		for (Iterator<Building> iterator = buildings.iterator(); iterator.hasNext();) {
			Building building = iterator.next();
			if (building instanceof GasStation)
			{
				if (building.virtualData[0].getTemperature() < 5)
				{
					building.resetPriority();
					building.addPriority(0, "Filter Neutral");
					iterator.remove();
				}
				continue;
			}
			if (building.virtualData[0].getTemperature() < 35)//bekhatere inke olaviyat migire va too filter spread kharrab mikone karo
			{
				building.resetPriority();
				building.addPriority(0, "Filter Neutral");
				iterator.remove();
				continue;
			}
		}
	}

	private void addPriority(Building b, int priority, String Comment) {
		tablelog.addScore(b.toString(), Comment, priority);
		b.addPriority(priority, Comment);
	}
}
