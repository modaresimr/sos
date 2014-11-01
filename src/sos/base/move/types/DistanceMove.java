package sos.base.move.types;

import sos.base.SOSAgent;
import sos.base.entities.StandardEntity;
import sos.base.move.types.graphWeigth.DistanceMoveGraphWeightInMM;
import sos.base.worldGraph.WorldGraph;

/**
 * @author Aramik
 */
public class DistanceMove extends NewMoveType {
	public DistanceMove(SOSAgent<? extends StandardEntity> me, WorldGraph graph) {
		super(me, graph, new DistanceMoveGraphWeightInMM(me.model(),graph));
	}
}
