package sos.police_v2.clearableBlockadeToReachable;

import java.util.ArrayList;

import sos.base.entities.Blockade;
import sos.base.move.Path;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.police_v2.PoliceForceAgent;

public abstract class ClearableBlockadeToReachable {


	protected final PoliceForceAgent agent;
	protected final SOSLoggerSystem log;
	
	public ClearableBlockadeToReachable(PoliceForceAgent policeForceAgent) {
		this.agent = policeForceAgent;
		log=agent.log;
	}

	public abstract ArrayList<Blockade> getBlockingBlockadeOfPath(Path path);

}
