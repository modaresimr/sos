package sos.police_v2.state.intrupt;

import sos.base.entities.Blockade;
import sos.base.entities.Road;
import sos.base.util.SOSActionException;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

public class LockInBlockadeState extends PoliceAbstractIntruptState {
	private Blockade target;

	public LockInBlockadeState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	@Override
	public boolean canMakeIntrupt() {
		if(agent.time()<4)
			return false;
		if (!(agent.me().getAreaPosition() instanceof Road))
			return false;
		target = null;
		for (Blockade blockade : agent.me().getAreaPosition().getBlockades()) {
			if (!PoliceUtils.isValid(blockade))
				continue;
			if (blockade.getShape().contains(agent.me().getX(), agent.me().getY())) {
				target = blockade;
				return true;
			}
		}
		return false;
	}

	@Override
	public void precompute() {

	}

	@Override
	public void act() throws SOSActionException {
		if (target != null)
			clear(target);
	}

}
