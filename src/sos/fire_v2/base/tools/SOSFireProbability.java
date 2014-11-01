package sos.fire_v2.base.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.base.entities.StandardEntity;
import sos.base.util.SOSGeometryTools;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.search_v2.tools.cluster.ClusterData;

public class SOSFireProbability {
	private SOSAgent agent;
	SOSLoggerSystem log;

	public SOSFireProbability(SOSAgent agent) {
		this.agent = agent;
		log = new SOSLoggerSystem((StandardEntity) agent.me(), "SOSFireProbability/", true, OutputType.File, true);
		agent.sosLogger.addToAllLogType(log);
	}

	ArrayList<Building> res = new ArrayList<Building>();
	ArrayList<ClusterData> resCluster = new ArrayList<ClusterData>();
	int time = -1;

	public ArrayList<ClusterData> getClusterProbabilisticFieryBuilding() {
		if (agent.model().time() == time)
			return resCluster;
		getProbabilisticFieryBuilding();
		return resCluster;
	}

	public ArrayList<Building> getProbabilisticFieryBuilding() {
		if (agent.model().time() == time)
			return res;
		time = agent.model().time();
		res.clear();
		resCluster.clear();
		log.info("new fire probability computer");

		ArrayList<Building> probabilisticBuilding = getProbabilisticBuilding();
		log.info("Probabilistic buildings :::> " + probabilisticBuilding);

		ArrayList<ArrayList<Building>> regions = getRegions(probabilisticBuilding);

		removeCheckedBuildings(regions);

		int index = 0;

		for (ArrayList<Building> region : regions) {
			HashSet<Building> x = new HashSet<Building>();
			for (Building b : getBestProb(region))
				if (b.getValuSpecialForFire() > 0) {
					res.add(b);
					x.add(b);
				}
			if (x.size() > 0)
			{
				ClusterData cd = new ClusterData(x, index++);
				resCluster.add(cd);
			}
		}

		log.info("result ::> " + res);

		return res;
	}

	public static double distance(Building b1, Building b2) {
		double distance = Integer.MAX_VALUE;
		for (int i = 0; i < b1.getApexes().length / 2; i += 2) {
			for (int j = 0; j < b2.getApexes().length / 2; j += 2) {
				double dis = SOSGeometryTools.distance(b1.getApexes()[i], b1.getApexes()[i + 1], b2.getApexes()[j], b2.getApexes()[j + 1]);
				if (dis < distance)
					distance = dis;
			}
		}
		return distance;
	}

	private ArrayList<Building> getBestProb(ArrayList<Building> region) {

		for (Building b : region) {

			if (hasFireProbability(b)) {

				addProbScore(region, b);

			} else {

				if (agent.model().time() - b.updatedtime() < Math.min(7, agent.model().time() / 2) && b.isTemperatureDefined() && b.getTemperature() == 0)
				{
					for (Building neigh : b.realNeighbors_Building()) {
						double t = 0;
						double c = 0;
						for (Building n2 : neigh.realNeighbors_Building())
						{
							if (n2.getTemperature() > 0)
								c -= neigh.getRealNeighValue(n2);
							else
								c += neigh.getRealNeighValue(n2);
						}
						t = (b.getRealNeighValue(neigh) + 0.001) * 100d / (Math.max(1, distance(b, neigh) / 1000)) * 3;
						if (c > 0)
							neigh.setSpecialForFire(neigh.getValuSpecialForFire() - (int) (Math.ceil(t * c)), "negative center=" + b + "  t" + t + "  c" + c);
					}
				}

				if (agent.model().time() == b.updatedtime()) {
					if (b.getTemperature() == 0) {
						for (Building neigh : b.realNeighbors_Building()) {
							if (neigh.getGroundArea() > 1.7 * b.getGroundArea())
								neigh.setSpecialForFire(neigh.getValuSpecialForFire() - 50, "negative value update in big building=" + b + "   " + (-50));
						}

					}
				}

			}
		}

		return region;
	}

	private int temperatureScore(Building b) {
		int tmp = (int) Math.ceil(Math.min(b.getTemperature(), 120) / 1d);
		if (b.getTemperature() > 120)
			tmp = 10;
		return (int) Math.pow(tmp, 4);
	}

	private void addProbScore(ArrayList<Building> region, Building center) {
		for (Building neigh : center.realNeighbors_Building()) {
			neigh.setSpecialForFire(neigh.getValuSpecialForFire() - 100000, " -100  " + center);
			//////////////////////////////////////
			if (neigh.updatedtime() >= center.updatedtime()) {
				log.info("\t\t\t Neigh is update " + neigh);
				continue;
			}
			if (neigh.updatedtime() >= center.getTemperatureIncreasedTime()) {
				log.info("\t\t\t Neigh is update after sensed temp " + neigh + "    NeighUpdTime=" + neigh.updatedtime() + "\t increaseTime=" + center.getTemperatureIncreasedTime());
				continue;
			}
			/////////////////////////////////
			if (agent.model().time() - neigh.updatedtime() < Math.min(8, agent.model().time() / 2))
				continue;

			if (center.updatedtime() < neigh.updatedtime() + 3)
				continue;

			if (!region.contains(neigh))
				continue;

			neigh.setSpecialForFire(neigh.getValuSpecialForFire() + 100000, "Add 100 score ");

			double t = temperatureScore(center);

			t *= (center.getRealNeighValue(neigh) + 0.001) * 100d / ((Math.max(1, distance(center, neigh) / 1000)));
			if (center.getTemperature() < 3)
				t += center.getNeighValue(neigh) * 200 * temperatureScore(center);

			double c = 1;
			for (Building n2 : neigh.realNeighbors_Building())
			{
				if (n2.getTemperature() > 0)
					c -= neigh.getNeighValue(n2);
				else
					c += neigh.getRealNeighValue(n2);
			}

			neigh.setSpecialForFire(neigh.getValuSpecialForFire() + (int) (Math.ceil(t * c)), " temp = " + temperatureScore(center) + " distance and neigh = " + t + "  c " + c + " " + center);

			//			if (!res.contains(neigh))
			//				res.add(neigh);

		}

	}

	private void removeCheckedBuildings(ArrayList<ArrayList<Building>> regions) {

	}

	private ArrayList<ArrayList<Building>> getRegions(ArrayList<Building> probabilisticBuilding) {

		ArrayList<ArrayList<Building>> regions = new ArrayList<ArrayList<Building>>();

		while (probabilisticBuilding.size() > 0) {
			ArrayList<Building> region = bfs(probabilisticBuilding);
			log.info("region ::>  " + region);
			if (region != null)
				regions.add(region);
		}
		return regions;
	}

	private ArrayList<Building> bfs(ArrayList<Building> probabilisticBuilding) {
		ArrayList<Building> searched = new ArrayList<Building>();

		Queue<Building> queue = new LinkedList<Building>();
		Building start = probabilisticBuilding.remove(0);

		queue.add(start);
		searched.add(start);

		while (queue.size() > 0) {

			Building b = queue.poll();
			for (Building neigh : b.realNeighbors_Building()) {
				boolean removed = probabilisticBuilding.remove(neigh);
				if (removed) {
					queue.add(neigh);
					searched.add(neigh);
				}
			}
		}

		return searched;
	}

	private ArrayList<Building> getProbabilisticBuilding() {

		ArrayList<Building> probabilisticBuildings = new ArrayList<Building>();
		boolean[] checked = new boolean[agent.model().buildings().size()];

		for (Building b : agent.model().buildings()) {
			b.setSpecialForFire(0, "reset");
			boolean fireProbability = hasFireProbability(b);

			if (fireProbability) {
				log.info("Fire Prob  " + b);
				if (!checked[b.getBuildingIndex()]) {
					b.setSpecialForFire(0, "reset");
					probabilisticBuildings.add(b);
					checked[b.getBuildingIndex()] = true;
					log.info("\t\t aded " + b);
				}

				for (Building neigh : b.realNeighbors_Building()) {
					if (!checked[neigh.getBuildingIndex()]) {//&& hasFireProbability(neigh)
						neigh.setSpecialForFire(0, "reset");
						probabilisticBuildings.add(neigh);
						checked[neigh.getBuildingIndex()] = true;
						log.info("\t\t aded " + neigh);
					}
				}

			}
		}

		return probabilisticBuildings;
	}

	public void log(String st) {
		log.info(st);
	}

	private boolean hasFireProbability(Building b) {
		if (b.getEstimator() != null || b.getSOSEstimateFireSite() != null)
			return false;

		if ((int) b.virtualData[0].getTemperature() == 0)
			return false;

		if (b.getFieryness() > 0 && b.getFieryness() != 4)
			return false;

		//		if (b.getFieryness() == 4 && agent instanceof FireBrigadeAgent)
		//			return false;

		for (Building near : b.neighbors_Building()) {
			if ((near.virtualData[0].getFieryness() > 0 && near.virtualData[0].getFieryness() != 4) || (near.getFieryness() > 0 && near.getFieryness() != 4))
				return false;
		}

		return true;
	}

}
