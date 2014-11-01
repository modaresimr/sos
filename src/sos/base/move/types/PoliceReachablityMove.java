package sos.base.move.types;

import java.util.ArrayList;
import java.util.Collection;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.move.MoveConstants;
import sos.base.move.types.graphWeigth.PoliceReachablityMoveGraph;
import sos.base.worldGraph.WorldGraph;

public class PoliceReachablityMove extends NewMoveType {
	public PoliceReachablityMove(SOSAgent<? extends StandardEntity> me, WorldGraph graph) {
		super(me, graph, new PoliceReachablityMoveGraph(me.model(),graph));
	}

	@Override
	protected ArrayList<Integer> getOutsideNodes(Human hu) {
		return getOutsideNodes((Area) hu.getPosition());
	}
	public boolean isReallyReachableTo(ArrayList<Edge> imReachableToEdges) {
		prepareDijkstraFromMe();
		int[] passableEdgeNodeIndexes = new int[imReachableToEdges.size()];
		for (int i = 0; i < imReachableToEdges.size(); i++) {
			passableEdgeNodeIndexes[i] = imReachableToEdges.get(i).getNodeIndex();
		}

		long min = Long.MAX_VALUE;
		for (int i = 0; i < passableEdgeNodeIndexes.length; i++) {
			if (dijkstra.getWeight(passableEdgeNodeIndexes[i]) < min) {
				min = Math.min(min, dijkstra.getWeight(passableEdgeNodeIndexes[i]));
				if (min < MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING)
					return true;
			}
		}
		return false;
	}

	public boolean isReallyReachableXY(Collection<Pair<? extends Area, Point2D>> targets) {
		prepareDijkstraFromMe();
		log().debug("");
		log().debug("$$$$$$$$$");
		log().debug("");
		Collection<Pair<? extends Area, Point2D>> newDest = new ArrayList<Pair<? extends Area, Point2D>>();
		// -----------------------getting outside nodes and checking if source==destination
		for (Pair<? extends Area, Point2D> dest : targets) {
			if (dest.first().equals(me.me().getAreaPosition())) {// source and destination is in same area
				log().debug("source==destination");
				if (dest.first() instanceof Road) {
					int tmpdis = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), dest.second());
					if (tmpdis < MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING) {
						log().debug(this.getClass().getSimpleName() + ":1isReallyReachableTo=" + targets + ":::::" + true);
						return true;
					}
				} else {
					log().debug(this.getClass().getSimpleName() + ":2isReallyReachableTo=" + targets + ":::::" + true);
					return true;
				}
				log().debug(this.getClass().getSimpleName() + ":3isReallyReachableTo=" + targets + ":::::" + false);
				return false;

			} else
				newDest.add(dest);

		}
		if (newDest.size() == 0) {
			log().debug(this.getClass().getSimpleName() + ":4isReallyReachableTo=" + targets + ":::::" + false);
			return false;
		}

		// ------------------------getting outside nodes of destinations and costs
		/////////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		// ------------------------get minimum cost destination
		//////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		if (haveADestinationWithCostLessThan(newDest, MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING)) {
			log().debug(this.getClass().getSimpleName() + ":5isReallyReachableTo=" + targets + ":::::" + true);
			return true;
		}
		log().debug(this.getClass().getSimpleName() + ":6isReallyReachableTo=" + targets + ":::::" + false);
		return false;
	}

}
