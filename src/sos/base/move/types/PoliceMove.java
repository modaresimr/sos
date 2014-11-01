package sos.base.move.types;

import java.util.ArrayList;

import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.worldGraph.WorldGraph;
import sos.police_v2.base.PoliceMoveGraphWeight;

/**
 * @author Aramik
 * 
 */
public class PoliceMove extends NewMoveType {
	public PoliceMove(SOSAgent<? extends StandardEntity> me, WorldGraph graph) {
		super(me, graph,new PoliceMoveGraphWeight(me.model(),graph));
	}

	@Override
	protected ArrayList<Integer> getOutsideNodes(Human hu) {
		return getOutsideNodes((Area) hu.getPosition());
	}
	
}
