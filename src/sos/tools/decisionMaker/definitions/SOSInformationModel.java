package sos.tools.decisionMaker.definitions;

import rescuecore2.geometry.Point2D;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.StandardEntity;
import sos.base.move.Move;
import sos.base.util.information_stacker.InformationStacker;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.search_v2.agentSearch.AgentSearch;
import sos.search_v2.tools.cluster.ClusterData;

/**
 * Its just an interface class to simplify decision codes
 * 
 * @author Salim
 */
public class SOSInformationModel {
	/**
	 * agent instance
	 */
	private SOSAgent<? extends StandardEntity> agent;

	public SOSInformationModel(SOSAgent<? extends StandardEntity> agent) {
		this.agent = agent;
	}

	public SOSAgent<? extends StandardEntity> getAgent() {
		return agent;
	}

	public SOSWorldModel getModel() {
		return agent.model();
	}

	public Move getMove() {
		return agent.move;
	}

	public AgentSearch<?> getSearch() {
		return agent.newSearch;
	}

	public InformationStacker getInfoStacker() {
		return agent.informationStacker;
	}

	public StandardEntity getEntity() {
		return agent.me();
	}
	public AmbulanceTeam getATEntity() {
		return (AmbulanceTeam)agent.me();
	}

	public ClusterData getMyCluster() {
		return agent.getMyClusterData();
	}

	public int getTime() {
		return getModel().time();
	}

	public Point2D getPositionPoint() {
		return getEntity().getPositionPoint();
	}

	public SOSLoggerSystem getLog() {
		return getAgent().sosLogger.agent;
	}

}
