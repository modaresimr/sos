package sos.police_v2.state.intrupt;

import sos.police_v2.PoliceForceAgent;
import sos.police_v2.state.PoliceAbstractState;

public abstract class PoliceAbstractIntruptState extends PoliceAbstractState {
	
	public PoliceAbstractIntruptState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
		isFinished=true;
	}
	
	public abstract boolean canMakeIntrupt() ;

}
