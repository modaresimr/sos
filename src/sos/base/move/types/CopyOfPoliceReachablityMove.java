package sos.base.move.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.PoliceForce;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.move.MoveConstants;
import sos.base.reachablity.OtherReachablityTools;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.reachablity.tools.SOSArea;
import sos.base.util.SOSGeometryTools;
import sos.base.worldGraph.GraphWeight;
import sos.base.worldGraph.WorldGraph;
import sos.tools.GraphEdge;
import sos.tools.UnionFind;

public class CopyOfPoliceReachablityMove extends MoveType {
	public CopyOfPoliceReachablityMove(SOSAgent<? extends StandardEntity> me, WorldGraph graph) {
		super(me, graph, new GraphWeight((short) graph.getEdgesSize()));
	}

	@Override
	protected ArrayList<Integer> getOutsideNodes(Area area) {
		return getOutsideNodes(area, area.getX(), area.getY());
	}

	@Override
	protected ArrayList<Integer> getOutsideNodes(Area area, int x, int y) {
		ArrayList<Integer> result = new ArrayList<Integer>(5);
		boolean isInArea = false, flag = false;
		for (Edge ed : area.getPassableEdges()) {
			if (area instanceof Building)
				result.add((int) ed.getNodeIndex());
			else { // area is a road
				ReachablityState st = Reachablity.isReachable((Road) area, ed,new Point2D(x, y));
				if (st != ReachablityState.Close) {
					result.add((int) ed.getNodeIndex());
				} else {
					if (!flag) {
						flag = true;
						for (SOSArea ar : ((Road) area).getReachableParts())
							if (ar.getShape().contains(x, y)) {
								isInArea = true;
								break;
							}
						log().logln("agent is not reachable and point containing in any reachable part is " + isInArea);
					}
					if (!isInArea) {
						if (me.me() instanceof PoliceForce)
							continue;
						if (me.me() instanceof Human && ((Human) me.me()).getPositionArea() instanceof Road && ((Road) ((Human) me.me()).getPositionArea()).isBlockadesDefined()) {
							boolean fg = false;
							ArrayList<Blockade> blk = (ArrayList<Blockade>) ((Road) ((Human) me.me()).getPositionArea()).getBlockades();
							for (Blockade bk : blk) {
								if (bk.getShape().contains(((Human) me.me()).getPositionPoint().toGeomPoint())) {
									fg = true;
									break;
								}
							}
							if (!fg)
								result.add((int) ed.getNodeIndex());
						} else if (!(me.me() instanceof PoliceForce))
							result.add((int) ed.getNodeIndex());
					}
				}
			}
		}
		if (result.isEmpty()) { // special case = my policy is if there is no any open way we consider all of blocked entrances
			isLastPathSafe = false;
			log().logln("special case = my policy is if there is no any open way we consider all of blocked entrances");
			for (Edge ed : area.getPassableEdges())
				result.add((int) ed.getNodeIndex());
		}
		return result;
	}

	@Override
	protected ArrayList<Integer> getOutsideNodes(Human hu) {
		ArrayList<Integer> result = new ArrayList<Integer>(5);
		Area area = (Area) hu.getPosition();
		for (Edge ed : area.getPassableEdges()) {
			if (area instanceof Building)
				result.add((int) ed.getNodeIndex());
			else { // area is a road
				ReachablityState st = Reachablity.isReachableAgentToEdge(hu, (Road) area, ed);
				if (st != ReachablityState.Close) {
					result.add((int) ed.getNodeIndex());
				}
			}
		}
		if (result.isEmpty()) { // special case = my policy is if there is no any open way we consider all of blocked entrances
			isLastPathSafe = false;
			for (Edge ed : area.getPassableEdges())
				result.add((int) ed.getNodeIndex());
		}
		return result;
	}

	@Override
	protected boolean isInSameAreaWithMe(Pair<? extends Area, Point2D> pair) {
		if (pair.first().getAreaIndex() == ((Area) ((Human) me.me()).getPosition()).getAreaIndex()) {
			if (pair.first() instanceof Building)
				return true;
			ReachablityState result = Reachablity.isReachableAgentToPoint((Human) me.me(), (Road) ((Human) me.me()).getPosition(), pair.second());
			log().logln("isReachableAgentToPoint =" + result + " from " + ((Human) me.me()).getPosition() + " x=" + ((Human) me.me()).getX() + " y=" + ((Human) me.me()).getY() + " to " + pair.second());
			if (result != ReachablityState.Open) { // not reachable --> must check if the point is out of all reachable areas!!????
				boolean isInArea = false;
				for (SOSArea ar : ((Road) pair.first()).getReachableParts())
					if (ar.getShape().contains(pair.second().toGeomPoint())) {
						isInArea = true;
						break;
					}
				log().logln("agent is not reachable and point containing in any reachable part is " + isInArea);
				return !isInArea;
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean isInSameArea(Pair<? extends Area, Point2D> a, Pair<? extends Area, Point2D> b) {
		if (a.first().getAreaIndex() == b.first().getAreaIndex()) {
			if (a.first() instanceof Building)
				return true;
			ReachablityState result = Reachablity.isReachable((Road) a.first(), a.second(), b.second());
			if (result == ReachablityState.Close) {
				boolean isInArea = false;
				for (SOSArea ar : ((Road) a.first()).getReachableParts())
					if (ar.getShape().contains(a.second().toGeomPoint()) || ar.getShape().contains(b.second().toGeomPoint())) {
						isInArea = true;
						break;
					}
				log().logln("agent is not reachable and point containing in any reachable part is " + isInArea);
				return !isInArea;
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getCost(Area area, Point2D start, Point2D dst) {
		int dstlength = SOSGeometryTools.distance((int) start.getX(), (int) start.getY(), (int) dst.getX(), (int) dst.getY()) / DIVISION_UNIT;
		if (area instanceof Building)
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, dst, start);
		switch (rs) {
		case Close:
		case FoggyClose:
		case FoggyOpen:
			return UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		default:
			return dstlength;
		}
	}

	@Override
	protected int getCost(Area area, Edge ed, Point2D dst) {
		int dstlength = ((int) ed.getMidPoint().distance(dst)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building)
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, ed,dst);
		switch (rs) {
		case Close:
		case FoggyClose:
		case FoggyOpen:
			return UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		default:
			return dstlength;
		}
	}
	
	@Override
	protected int getCost(Area area, Point2D start, Edge ed) {
		int dstlength = ((int) ed.getMidPoint().distance(start)) / MoveConstants.DIVISION_UNIT;
		if (area instanceof Building)
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, start,ed);
		switch (rs) {
		case Close:
		case FoggyClose:
		case FoggyOpen:
			return UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		default:
			return dstlength;
		}
	}

	private ArrayList<Integer> getOutsideNodesReachabilityFalse(Area area, int x, int y) {
		//		boolean isInArea = false, flag = false;
		ArrayList<Integer> result = new ArrayList<Integer>(5);
		for (Edge ed : area.getPassableEdges()) {
			if (area instanceof Building) {
				result.add((int) ed.getNodeIndex());
			} else { // area is a road
				ReachablityState rs = Reachablity.isReachable((Road) area, ed,new Point2D(x, y));
				if (rs == ReachablityState.Open ) {
					result.add((int) ed.getNodeIndex());
				} else {
					int res = OtherReachablityTools.getReachablePart((Road) area, new Point2D(x, y));
					if (res != -1) {
						rs = Reachablity.getState((Road) area, ((Road) area).getDisjiontSetForReachablePartsAndEdges().inSameSet((short) res, (short) (ed.getReachablityIndex() + ((Road) area).getReachableParts().size())));
						if (rs == ReachablityState.Open) {
							result.add((int) ed.getNodeIndex());
						}
					}
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	private ArrayList<Integer> getOutsideNodesReachabilityTrue(Area area, int x, int y) {
		//		boolean isInArea = false, flag = false;
		ArrayList<Integer> result = new ArrayList<Integer>(5);
		for (Edge ed : area.getPassableEdges()) {
			if (area instanceof Building) {
				result.add((int) ed.getNodeIndex());
			} else { // area is a road
				if (Reachablity.isReachable((Road) area, ed,new Point2D(x, y)) != ReachablityState.Close) {
					result.add((int) ed.getNodeIndex());
				} else {
					// if (!flag) {
					// flag = true;
					// for (SOSArea ar : ((Road) area).getReachableParts())
					// if (ar.getShape().contains(x, y)) {
					// isInArea = true;
					// break;
					// }
					// log().logln("agent is not reachable and point containing in any reachable part is " + isInArea);
					// }
					// if (!isInArea) {
					// if (me.me() instanceof PoliceForce)
					// continue;
					// if (me.me() instanceof Human && ((Human) me.me()).getPositionArea() instanceof Road && ((Road) ((Human) me.me()).getPositionArea()).isBlockadesDefined()) {
					// boolean fg = false;
					// ArrayList<Blockade> blk = (ArrayList<Blockade>) ((Road) ((Human) me.me()).getPositionArea()).getBlockades();
					// for (Blockade bk : blk) {
					// if (bk.getShape().contains(((Human) me.me()).getPositionPoint().toGeomPoint())) {
					// fg = true;
					// break;
					// }
					// }
					// if (!fg)
					// result.add((int) ed.getNodeIndex());
					// } else if (!(me.me() instanceof PoliceForce))
					// result.add((int) ed.getNodeIndex());
					// }
					int res = OtherReachablityTools.getReachablePart((Road) area, new Point2D(x, y));
					if (res != -1) {
						ReachablityState rs = Reachablity.getState((Road) area, ((Road) area).getDisjiontSetForReachablePartsAndEdges().inSameSet((short) res, (short) (ed.getReachablityIndex() + ((Road) area).getReachableParts().size())));
						if (rs != ReachablityState.Close) {
							result.add((int) ed.getNodeIndex());
						}
					}

				}
			}
		}
		return result;
	}

	private ArrayList<Integer> getOutsideNodesReachability(Human hu) {
		ArrayList<Integer> result = new ArrayList<Integer>(5);
		Area area = (Area) hu.getPosition();
		for (Edge ed : area.getPassableEdges()) {
			if (area instanceof Building)
				result.add((int) ed.getNodeIndex());
			else { // area is a road
				if (Reachablity.isReachableAgentToEdge(hu, (Road) area, ed) == ReachablityState.Open) {
					result.add((int) ed.getNodeIndex());
				}
			}
		}
		return result;
	}

	public boolean isReachable(Area a, Area b, UnionFind union) {
		ArrayList<Integer> outsideNodes = getOutsideNodesReachabilityFalse(a, a.getX(), a.getY());
		if (outsideNodes.isEmpty())
			return a.getAreaIndex() == b.getAreaIndex();
		ArrayList<Integer> outsideNodesOfB = getOutsideNodesReachabilityFalse(b, b.getX(), b.getY());
		for (Integer alfa : outsideNodes)
			for (Integer beta : outsideNodesOfB)
				if (union.inSameSet(alfa.shortValue(), beta.shortValue()))
					return true;
		return false;
	}

	public boolean isReachable(Pair<? extends Area, Point2D> a, Pair<? extends Area, Point2D> b, UnionFind union) {
		ArrayList<Integer> outsideNodes = getOutsideNodesReachabilityFalse(a.first(), (int) a.second().getX(), (int) a.second().getY());
		ArrayList<Integer> outsideNodesOfB = getOutsideNodesReachabilityFalse(b.first(), (int) b.second().getX(), (int) b.second().getY());
		for (Integer alfa : outsideNodes)
			for (Integer beta : outsideNodesOfB)
				if (union.inSameSet(alfa.shortValue(), beta.shortValue()))
					return true;
		return false;
	}

	public boolean isReallyUnreachable(Collection<? extends Area> targets) {
		log().logln("isReallyUnreachable(Collection<? extends Area> targets) to " + targets);
		prepareDijkstraFromMe();
		ArrayList<Integer> outsideNodes = getOutsideNodesReachability((Human) me.me());
		if (outsideNodes.isEmpty())
			return !targets.contains(me.location());
		TreeSet<Integer> outsideNodesOfTargets = new TreeSet<Integer>();
		for (Area ar : targets)
			outsideNodesOfTargets.addAll(getOutsideNodesReachabilityFalse(ar, ar.getX(), ar.getY()));
		if (outsideNodesOfTargets.isEmpty())
			return true;
		long min = Long.MAX_VALUE;
		for (Integer index : outsideNodesOfTargets)
			if (dijkstra.getWeight(index) < min)
				min = dijkstra.getWeight(index);
		return min >= UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
	}

	public boolean isReallyReachableXY(Collection<Pair<? extends Area, Point2D>> targets) {
		log().logln("isReallyUnreachableXY(Collection<Pair<? extends Area, Point2D>> targets) to " + targets);
		prepareDijkstraFromMe();
		ArrayList<Integer> outsideNodes = getOutsideNodesReachability((Human) me.me());
		if (outsideNodes.isEmpty()) {
			for (Pair<? extends Area, Point2D> pr : targets) {
				if (me.location() instanceof Building && ((Area) me.location()).getAreaIndex() == pr.first().getAreaIndex())
					return false;
				else if (me.location() instanceof Road && ((Area) me.location()).getAreaIndex() == pr.first().getAreaIndex() 
						&& Reachablity.isReachableAgentToPoint((Human) me.me(), (Road) pr.first(), pr.second()) == ReachablityState.Open)
					return false;
			}
			return true;
		}
		TreeSet<Integer> outsideNodesOfTargets = new TreeSet<Integer>();
		for (Pair<? extends Area, Point2D> pr : targets)
			outsideNodesOfTargets.addAll(getOutsideNodesReachabilityFalse(pr.first(), (int) pr.second().getX(), (int) pr.second().getY()));
		if (outsideNodesOfTargets.isEmpty())
			return true;
		long min = Long.MAX_VALUE;
		for (Integer index : outsideNodesOfTargets)
			if (dijkstra.getWeight(index) < min)
				min = dijkstra.getWeight(index);
		return min >= UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
	}

	@Override
	public long getWeightTo(Area target, int x, int y) {
		log().logln("getWeightTo(Area a,int x,int y) targets) to " + target + " x=" + x + " y=" + y);
		prepareDijkstraFromMe();
		ArrayList<Integer> outsideNodesOfTarget = new ArrayList<Integer>();
		outsideNodesOfTarget.addAll(getOutsideNodesReachabilityFalse(target, x, y));
		if (outsideNodesOfTarget.isEmpty())
			return UNREACHABLE_COST;
		int[] costs = getCostsOfOutSidesFrom(x, y, outsideNodesOfTarget);
		int i = 0;
		long min = Long.MAX_VALUE;
		for (Integer node : outsideNodesOfTarget) {
			if (dijkstra.getWeight(node) + costs[i] < min) {
				min = dijkstra.getWeight(node) + costs[i];
			}
			i++;
		}
		return min/DIVISION_UNIT_FOR_GET;
	}

	@Override
	protected void updateWeigths() {
		for (short i = 0; i < weights.getSize(); ++i) {
			GraphEdge ge = me.model().graphEdges().get(i);
			int weight = 0;
			switch (ge.getState()) {
			case Open:
				weight = 1;
				break;
			case FoggyOpen:
			case FoggyBlock:
			default:
				weight = UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
				break;
			}
			if (weight == 0)
				weight = 1;
			((GraphWeight) weights).setWeight(i, weight);

		}
	}

	public boolean isReallyUnreachableTo(ArrayList<Edge> imReachableToEdges) {
		prepareDijkstraFromMe();
		int[] passableEdgeNodeIndexes = new int[imReachableToEdges.size()];
		for (int i = 0; i < imReachableToEdges.size(); i++) {
			passableEdgeNodeIndexes[i] = imReachableToEdges.get(i).getNodeIndex();
		}
		
		
		long min = Integer.MAX_VALUE;
		for (int i = 0; i < passableEdgeNodeIndexes.length; i++) {
//			Area target =me.model().areas().get(me.model().nodes().get(passableEdgeNodeIndexes[i]).getAreaIndex());
			if (dijkstra.getWeight(passableEdgeNodeIndexes[i])< min) {
				min = Math.min(min, dijkstra.getWeight(passableEdgeNodeIndexes[i]));
				if(min<MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING)
					return true;
			}
		}
		return false;
	}

	
}
