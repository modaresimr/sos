package sos.police_v2.base;

import sos.base.CenterActivity;
import sos.base.SOSAgent;
import sos.base.SOSConstant.AgentType;

/**
 * SOS centre agent.
 */
public class AbstractPoliceOfficeActivity extends CenterActivity {
	public AbstractPoliceOfficeActivity(SOSAgent<?> sosAgent) {
		super(sosAgent);
	}
	
	@Override
	public String toString() {
		return "PoliceOfficeActivity";
	}
	
	public AgentType type() {
		return AgentType.FireStation;
	}

}