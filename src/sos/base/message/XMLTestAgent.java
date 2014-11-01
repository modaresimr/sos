package sos.base.message;

import java.util.Arrays;
import java.util.EnumSet;

import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.EntityID;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.SOSAgent;
import sos.base.SOSConstant.AgentType;
import sos.base.SOSWorldModel;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.StandardEntity;
import sos.base.util.SOSActionException;
import sos.base.util.sosLogger.SOSLogger;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

/**
 * @author Ali
 */
public class XMLTestAgent extends SOSAgent<StandardEntity> {

	@Override
	public AgentType type() {
		return AgentType.AmbulanceTeam;
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return null;
	}

	public XMLTestAgent() {
		AmbulanceTeam a = new AmbulanceTeam(new EntityID(10));
		setModel(new SOSWorldModel(new AmbulanceTeamAgent()));
		model().addEntities(Arrays.asList(a));
		sosLogger = new SOSLogger(a, true, OutputType.Both);
		//		postConnect();
	}

	@Override
	protected void thinkAfterExceptionOccured() throws SOSActionException {
		// TODO Auto-generated method stub

	}

}
