package sos.fire_v2.base;

import sos.base.CenterActivity;
import sos.base.SOSAgent;
import sos.base.SOSConstant.AgentType;

/**
 * SOS center agent.
 */
public class AbstractFireStationActivity extends CenterActivity {

	public AbstractFireStationActivity(SOSAgent<?> sosAgent) {
		super(sosAgent);
	}

	@Override
	public String toString() {
		return "FireStationActivity[" + sosAgent.getID().getValue() + "]";
	}

	public AgentType type() {
		return AgentType.FireStation;
	}

}