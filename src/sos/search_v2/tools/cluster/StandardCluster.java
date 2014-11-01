package sos.search_v2.tools.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Human;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public class StandardCluster<E extends Human> extends MapClusterType<E> {

	public StandardCluster(SOSAgent<E> me, List<E> agents) {
		super(me, agents);
	}

	@Override
	public void startClustering(SOSWorldModel model) {
		ArrayList<Building> buildings = new ArrayList<Building>();
		buildings.addAll(model.buildings());
		int COUNT = agents.size();
		int index=0;
		for (int i = 0; i < COUNT; i++) {
			Building max = getMaxXY(buildings);
			HashSet<Building> clusterBuilding = new HashSet<Building>();
			ClusterData cd;
			if (i + 1 == COUNT) {
				clusterBuilding.addAll(buildings);
				cd = new ClusterData(i, clusterBuilding,me,index);
			} else {
				clusterBuilding.addAll(getBestBuildingForMe(max, buildings, model.buildings().size() / COUNT));
				cd = new ClusterData(i, clusterBuilding,me,index);
			}
			clusters.put(agents.get(i), cd);
			index++;
		}
	}

	private ArrayList<Building> getBestBuildingForMe(Building building, ArrayList<Building> buildings, int numberOfBuildingToAssign) {
		ArrayList<Building> result = new ArrayList<Building>();
		for (int j = 0; j < numberOfBuildingToAssign; j++) {
			if (buildings.size() > 0) {
				Building b = getNear(building, buildings);
				buildings.remove(b);
				result.add(b);
			} else
				break;
		}
		return result;
	}

	private Building getNear(Building building, ArrayList<Building> buildings) {
		double max = Integer.MAX_VALUE;
		Building best = null;
		for (Building b2 : buildings) {
			if (building.distance(b2) < max) {
				max = building.distance(b2);
				best = b2;
			}
		}
		return best;
	}

	private Building getMaxXY(ArrayList<Building> availableBuildings) {
		double max = Integer.MIN_VALUE;
		Building best = null;
		for (Building b : availableBuildings) {
			if (b.x() + b.y() > max)
				best = b;
		}
		return best;
	}

	@Override
	public Collection<ClusterData> allClusters() {
		// TODO Auto-generated method stub
		return null;
	}

}
