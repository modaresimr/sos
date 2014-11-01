package sos.ambulance_v2.decision;

import java.util.Comparator;

import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.entities.VirtualCivilian;
import sos.base.util.SOSGeometryTools;
import sos.search_v2.tools.cluster.ClusterData;
public class AmbComparator{
	
}
//***********************************************************************************************************
//***********************************************************************************************************
class DeathTimeComparator implements Comparator<Human> {
	@Override
	public int compare(Human h1, Human h2) {
		if (h1.getRescueInfo().getDeathTime() > h2.getRescueInfo().getDeathTime())
			return 1;
		else if (h1.getRescueInfo().getDeathTime() < h2.getRescueInfo().getDeathTime())
			return -1;
		return 0;
	}
}

//***********************************************************************************************************
//***********************************************************************************************************
class PriorityComparator implements Comparator<Human> {

	@Override
	public int compare(Human h1, Human h2) {
		if (h1.getRescueInfo().getRescuePriority() < h2.getRescueInfo().getRescuePriority())
			return 1;
		if (h1.getRescueInfo().getRescuePriority() > h2.getRescueInfo().getRescuePriority())
			return -1;
		if (h1.getBuriedness() < h2.getBuriedness())
			return 1;
		if (h1.getBuriedness() > h2.getBuriedness())
			return -1;
		return 0;
	}
}

//***********************************************************************************************************
//***********************************************************************************************************
class VirtualCivilianPriorityComparator implements Comparator<VirtualCivilian> {

	@Override
	public int compare(VirtualCivilian c1, VirtualCivilian c2) {
		if (c1.getRescuePriority() < c2.getRescuePriority())
			return 1;
		if (c1.getRescuePriority() > c2.getRescuePriority())
			return -1;
		if (c1.getBuridness() < c2.getBuridness())
			return 1;
		if (c1.getBuridness() > c2.getBuridness())
			return -1;
		return 0;
	}
}
//***********************************************************************************************************
//***********************************************************************************************************
class CostComparator implements Comparator<Pair<Human,Float>> {

	@Override
	public int compare(Pair<Human,Float> h1, Pair<Human,Float> h2) {//Target cost
		if (h1.second() < h2.second())
			return -1;
		else if (h1.second()> h2.second())
			return 1;

		return 0;
	}
}
//***********************************************************************************************************
//***********************************************************************************************************
class VirtualCivilianCostComparator implements Comparator<Pair<VirtualCivilian,Float>> {

	@Override
	public int compare(Pair<VirtualCivilian,Float> h1, Pair<VirtualCivilian,Float> h2) {//Target cost
		if (h1.second() < h2.second())
			return -1;
		else if (h1.second()> h2.second())
			return 1;

		return 0;
	}
}
//***********************************************************************************************************
//***********************************************************************************************************
class DistanceComparator implements Comparator<ClusterData> {

	private SOSAgent<? extends StandardEntity> agent = null;
	public DistanceComparator( SOSAgent<? extends StandardEntity> agent){
		this.agent=agent;
	}
	@Override
	public int compare(ClusterData o1, ClusterData o2) {
		double o1s = SOSGeometryTools.distance(agent.me().getPositionPoint().getX(),agent.me().getPositionPoint().getY(), o1.getX(), o1.getY());
		double o2s = SOSGeometryTools.distance(agent.me().getPositionPoint().getX(), agent.me().getPositionPoint().getY(), o2.getX(), o2.getY());
		if (o1s > o2s)
			return 1; //o2s
		if (o1s < o2s)
			return -1; //o1s
		return 0;
	}
	
}

//***********************************************************************************************************
//***********************************************************************************************************
class DistanceToMyClusterComparator implements Comparator<ClusterData> {

	private ClusterData myCluster = null;
	public DistanceToMyClusterComparator(ClusterData myCluster){
		this.myCluster=myCluster;
	}
	@Override
	public int compare(ClusterData o1, ClusterData o2) {
		double o1s = SOSGeometryTools.distance(myCluster.getX(),myCluster.getY(), o1.getX(), o1.getY());
		double o2s = SOSGeometryTools.distance(myCluster.getX(), myCluster.getY(), o2.getX(), o2.getY());
		if (o1s > o2s)
			return 1; //o2s
		if (o1s < o2s)
			return -1; //o1s
		return 0;
	}
	
}
//***********************************************************************************************************
//***********************************************************************************************************
class clusterComparitor implements Comparator<AmbulanceTeam> {

	private Human target = null;
	public clusterComparitor( Human target){
		this.target=target;
	}
	@Override
	public int compare(AmbulanceTeam o1, AmbulanceTeam o2) {
		ClusterData o1Cluster = o1.model().searchWorldModel.getClusterData(o1);
		ClusterData o2Cluster = o2.model().searchWorldModel.getClusterData(o2);
		double o1s = SOSGeometryTools.distance(target.getPositionPoint().getX(),target.getPositionPoint().getY(), o1Cluster.getX(), o1Cluster.getY());
		double o2s = SOSGeometryTools.distance(target.getPositionPoint().getX(), target.getPositionPoint().getY(), o2Cluster.getX(), o2Cluster.getY());
		
		if (o1s > o2s)
			return 1; //o2s
		if (o1s < o2s)
			return -1; //o1s
		return 0;
	}
	
}
//***********************************************************************************************************
//***********************************************************************************************************
class IdComparator implements Comparator<Human> {

	@Override
	public int compare(Human h1, Human h2) {
		if (h1.getID().getValue() < h2.getID().getValue())
			return 1;
		else if (h1.getID().getValue() > h2.getID().getValue())
			return -1;

		return 0;
	}
}
