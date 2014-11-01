package sos.base.move.types;

import sos.base.SOSAgent;
import sos.base.entities.StandardEntity;
import sos.base.move.types.graphWeigth.SearchMoveGraphWeight;
import sos.base.worldGraph.WorldGraph;

public class SearchMove extends NewMoveType {

	public SearchMove(SOSAgent<? extends StandardEntity> me, WorldGraph graph) {
		super(me, graph, new SearchMoveGraphWeight(me.model(),graph));
	}


}
