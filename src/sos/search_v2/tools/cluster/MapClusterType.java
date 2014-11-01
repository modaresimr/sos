package sos.search_v2.tools.cluster;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.util.sosLogger.SOSLoggerSystem;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public abstract class MapClusterType<E extends Human> {
	protected SOSAgent me;
	protected List<E> agents;
	protected HashMap<E, ClusterData> clusters;

	public MapClusterType(SOSAgent me, List<E> agents) {
		this.me = me;
		this.agents = agents;
		clusters = new HashMap<E, ClusterData>();
	}

	public HashMap<E, ClusterData> getClusterMap() {
		return clusters;
	}

	public abstract void startClustering(SOSWorldModel model);

	public HashSet<Building> getMyClusterBuildings() {
		return clusters.get(me.me()).getBuildings();//TODO if null
	}

	public HashSet<Building> getClusterBuildings(E human) {
		return clusters.get(human).getBuildings();//TODO if null
	}

	public ClusterData getMyCluster() {
		return clusters.get(me.me());//TODOD if null
	}

	public ClusterData getCluster(Human human) {
		return clusters.get(human);//TODOD if null
	}
	public abstract Collection<ClusterData> allClusters();
	
	protected SOSLoggerSystem log() {
		return me.sosLogger.search;
	}
}
