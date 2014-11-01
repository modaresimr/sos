package sos.search_v2.worldModel;

import java.util.ArrayList;
import java.util.Collection;

import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.search_v2.tools.cluster.ClusterData;
import sos.search_v2.tools.cluster.MapClusterType;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public class SearchWorldModel<E extends Human> {
	private SOSWorldModel model;
	private MapClusterType<E> mapClusterType;
	

	public MapClusterType<E> getMapClusterType() {
		return mapClusterType;
	}

	private ArrayList<SearchBuilding> searchBuildings = new ArrayList<SearchBuilding>();
	private ArrayList<SearchRoad> searchRoads = new ArrayList<SearchRoad>();

	public ArrayList<SearchBuilding> getSearchBuildings() {
		return searchBuildings;
	}

	public SearchBuilding getSearchBuilding(Building b) {
		return getSearchBuildings().get(b.getBuildingIndex());
	}
	//Salim
	public SearchRoad getSearchRoad(Road r) {
		return getSearchRoads().get(r.getRoadIndex());
	}

	public ArrayList<SearchRoad> getSearchRoads() {
		return searchRoads;
	}

	public SearchWorldModel(SOSWorldModel model) {
		this.model = model;
		createSearchBuildings();
		createSearchRoads();
	}

	public void createSearchBuildings() {
		for (Building b : model.buildings()) {
			searchBuildings.add(new SearchBuilding(b));
		}
	}

	public void createSearchRoads() {
		for (Road r : model.roads()) {
			searchRoads.add(new SearchRoad(r));
		}
	}

	public void cluster(MapClusterType<E> clusterType) {
		this.mapClusterType = clusterType;
		clusterType.startClustering(model);
	}

	public ClusterData getClusterData() {
		return mapClusterType.getMyCluster();
	}

	public ClusterData getClusterData(Human h) {
		return mapClusterType.getCluster(h);
	}


	public SOSWorldModel model() {
		return model;
	}
	public Collection<ClusterData> getAllClusters(){
		return mapClusterType.allClusters();
	}
}
