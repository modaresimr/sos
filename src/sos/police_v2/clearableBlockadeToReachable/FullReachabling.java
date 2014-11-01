package sos.police_v2.clearableBlockadeToReachable;

import java.util.ArrayList;

import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.move.Path;
import sos.police_v2.PoliceForceAgent;

public class FullReachabling extends ClearableBlockadeToReachable {

	public FullReachabling(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	@Override
	public ArrayList<Blockade> getBlockingBlockadeOfPath(Path path) {
		ArrayList<Blockade> blocks = new ArrayList<Blockade>();
		Area source = path.getSource().first();
		if (source.isBlockadesDefined())
			blocks.addAll(source.getBlockades());
		Area dst = path.getDestination().first();
		if (dst.isBlockadesDefined())
			blocks.addAll(dst.getBlockades());
		for (EntityID ei : path.getIds()) {
			StandardEntity a = agent.model().getEntity(ei);
			if (a instanceof Road && ((Road) a).isBlockadesDefined())
				blocks.addAll(((Road) a).getBlockades());
		}
		return blocks;
	}


}
