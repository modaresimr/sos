package sos.police_v2.state.intrupt;

import java.awt.Point;

import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Road;
import sos.base.util.SOSActionException;
import sos.base.util.information_stacker.act.AbstractAction;
import sos.base.util.information_stacker.act.ClearAction;
import sos.police_v2.PoliceConstants;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

public class ClearEntrances extends PoliceAbstractIntruptState {

	public ClearEntrances(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);

	}
	@Override
	public void precompute() {
		
	}

	Area blockArea = null;

	@Override
	public boolean canMakeIntrupt() {
		if(PoliceConstants.IS_NEW_CLEAR)
			return false;
		//		ArrayList<Area> visible = agent.getVisibleEntities(Area.class);
		//		
		//		if()
		AbstractAction lastAct = agent.informationStacker.getInformations(1).getAct();
		if (lastAct instanceof ClearAction) {
			Area area = ((ClearAction) lastAct).getBlockade().getAreaPosition();
			if (area instanceof Road && PoliceUtils.isEntrance(area) && area.getBlockades().size() > 0) {
				blockArea = area;
				return true;

			}
		}
		return false;
	}

	

	@Override
	public void act() throws SOSActionException {
		log.debug("Clearing some special Entrances");
		Blockade best = chooseBestBlockade(blockArea.getBlockades());
		if (best != null)
			clear(new Point(best.getX(), best.getY()));
	}

}
