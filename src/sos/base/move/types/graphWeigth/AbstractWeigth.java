package sos.base.move.types.graphWeigth;

import sos.base.move.MoveConstants;
import sos.base.util.blockadeEstimator.BlockadeEstimator;
import sos.tools.GraphEdge;

public abstract class AbstractWeigth {

	public int getWeigth(GraphEdge ge) {
		if (ge.haveTraffic())
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;

		boolean isMiddleBlockBiggerThanReal = BlockadeEstimator.isActuallyMiddleBlockadesBiggerThanRealBlockades();
		if (isMiddleBlockBiggerThanReal) {
			switch (ge.getState()) {
			case FoggyOpen:
			case Open:
				return Math.max(1, getOpenWeight(ge));
			case FoggyBlock:
			case Block:
				return Math.max(1, getBlockWeight(ge));
			default:
				System.err.println("Error...Unknown graph edge state");
				return Math.max(1, ge.getLenght() / 333);
			}
		} else {

			switch (ge.getState()) {
			case FoggyOpen:
				return Math.max(1, getFoggyOpenWeight(ge));
			case FoggyBlock:
				return Math.max(1, getFoggyBlockWeight(ge));
			case Open:
				return Math.max(1, getOpenWeight(ge));
			case Block:
				return Math.max(1, getBlockWeight(ge));
			default:
				System.err.println("Error...Unknown graph edge state");
				return Math.max(1, ge.getLenght() / 333);
			}
		}
	}

	public abstract int getBlockWeight(GraphEdge ge);

	public abstract int getOpenWeight(GraphEdge ge);

	public abstract int getFoggyBlockWeight(GraphEdge ge);

	public abstract int getFoggyOpenWeight(GraphEdge ge);
}