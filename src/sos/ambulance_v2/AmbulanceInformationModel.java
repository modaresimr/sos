package sos.ambulance_v2;

import sos.tools.decisionMaker.definitions.SOSInformationModel;

/**
 * @author Salim
 */
public class AmbulanceInformationModel extends SOSInformationModel {

	public AmbulanceInformationModel(AmbulanceTeamAgent agent) {
		super(agent);
	}

	public AmbulanceTeamAgent getAmbulance() {
		return (AmbulanceTeamAgent) getAgent();
	}

}