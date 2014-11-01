package sos.police_v2.state;

import sos.base.entities.Building;
import sos.base.entities.Refuge;
import sos.base.util.SOSActionException;
import sos.police_v2.PoliceForceAgent;

public class TestState extends PoliceAbstractState {

	public TestState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}


	@Override
	public void act() throws SOSActionException {
		if(!agent.model().refuges().isEmpty()){
			if(agent.me().getPositionArea()instanceof Refuge)
				agent.problemRest("Noting to do");
			move(agent.model().refuges());
		}else if(agent.me().getPositionArea()instanceof Building)
			move(agent.model().roads());
		
		agent.problemRest("Noting to do");
	}

	@Override
	public void precompute() {
		// TODO Auto-generated method stub
		
	}


}
