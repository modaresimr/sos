package sos.ambulance_v2.base;

import sos.base.CenterActivity;
import sos.base.SOSAgent;
import sos.base.SOSConstant.AgentType;

/**
 * SOS centre agent.
 */
public class AbstractAmbulanceCenterActivity extends CenterActivity {
	
	public AbstractAmbulanceCenterActivity(SOSAgent<?> sosAgent) {
		super(sosAgent);
	}
	
	public AgentType type() {
		return AgentType.AmbulanceCenter;
	}

}