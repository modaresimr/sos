package sos.base.move.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.SOSConstant.GraphEdgeState;
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
import sos.base.worldGraph.Node;
import sos.base.worldGraph.WorldGraph;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceConstants;
import sos.tools.GraphEdge;
import sos.tools.UnionFind;

/** @author Aramik */
public class StandardMove extends MoveType {

	public StandardMove(SOSAgent<? extends StandardEntity> me, WorldGraph graph) {
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
			if (a.first() instanceof Building || !a.first().isBlockadesDefined())
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
		if (area instanceof Building || !area.isBlockadesDefined())
			return dstlength;

		ReachablityState rs = Reachablity.isReachable((Road) area, dst, start);
		switch (rs) {
		case Close:
			return UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
			return dstlength * 5;
		case FoggyOpen:
			return dstlength * 2;
		default:
			return dstlength;
		}
	}

	@Override
	protected int getCost(Area area, Edge ed, Point2D dst) {
		int dstlength = (int) dst.distance(ed.getMidPoint());
		if (area instanceof Building || !area.isBlockadesDefined())
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, ed, dst);
		switch (rs) {
		case Close:
			return UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
			return dstlength * 5;
		case FoggyOpen:
			return dstlength * 2;
		default:
			return dstlength;
		}
	}

	@Override
	protected int getCost(Area area, Point2D start, Edge ed) {
		int dstlength = (int) start.distance(ed.getMidPoint());
		if (area instanceof Building || !area.isBlockadesDefined())
			return dstlength;
		ReachablityState rs = Reachablity.isReachable((Road) area, start, ed);
		switch (rs) {
		case Close:
			return UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		case FoggyClose:
			return dstlength * 5;
		case FoggyOpen:
			return dstlength * 2;
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
				if (Reachablity.isReachable((Road) area, ed,new Point2D(x, y)) == ReachablityState.Open) {
					result.add((int) ed.getNodeIndex());
				} else {
					int res = OtherReachablityTools.getReachablePart((Road) area, new Point2D(x, y));
					if (res != -1) {
						ReachablityState rs = Reachablity.getState((Road) area, ((Road) area).getDisjiontSetForReachablePartsAndEdges().inSameSet((short) res, (short) (ed.getReachablityIndex() + ((Road) area).getReachableParts().size())));
						if (rs == ReachablityState.Open) {
							result.add((int) ed.getNodeIndex());
						}
					}
				}
			}
		}
		return result;
	}

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
				if (Reachablity.isReachableAgentToEdge(hu, (Road) area, ed) != ReachablityState.Close) {
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
			outsideNodesOfTargets.addAll(getOutsideNodesReachabilityTrue(ar, ar.getX(), ar.getY()));
		if (outsideNodesOfTargets.isEmpty())
			return true;
		long min = Long.MAX_VALUE;
		for (Integer index : outsideNodesOfTargets)
			if (dijkstra.getWeight(index) < min)
				min = dijkstra.getWeight(index);
		return min >= UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
	}

	public boolean isReallyUnreachableXY(Collection<Pair<? extends Area, Point2D>> targets) {
		log().logln("isReallyUnreachableXY(Collection<Pair<? extends Area, Point2D>> targets) to " + targets);
		prepareDijkstraFromMe();
		ArrayList<Integer> outsideNodes = getOutsideNodesReachability((Human) me.me());
		if (outsideNodes.isEmpty()) {
			for (Pair<? extends Area, Point2D> pr : targets) {
				if (me.location() instanceof Building && ((Area) me.location()).getAreaIndex() == pr.first().getAreaIndex())
					return false;
				else if (me.location() instanceof Road && ((Area) me.location()).getAreaIndex() == pr.first().getAreaIndex() && Reachablity.isReachableAgentToPoint((Human) me.me(), (Road) pr.first(), pr.second()) == ReachablityState.Open)
					return false;
			}
			return true;
		}
		TreeSet<Integer> outsideNodesOfTargets = new TreeSet<Integer>();
		for (Pair<? extends Area, Point2D> pr : targets)
			outsideNodesOfTargets.addAll(getOutsideNodesReachabilityTrue(pr.first(), (int) pr.second().getX(), (int) pr.second().getY()));
		if (outsideNodesOfTargets.isEmpty())
			return true;
		long min = Long.MAX_VALUE;
		for (Integer index : outsideNodesOfTargets)
			if (dijkstra.getWeight(index) < min)
				min = dijkstra.getWeight(index);
		return min >= UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
	}

	protected ArrayList<Pair<Integer, Integer>> getOutsideNodesWithCostsForLen(Area area, Point2D point) {
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>(5);
		boolean isInArea = false, flag = false;
		for (Edge ed : area.getPassableEdges()) {
			Point2D p = ed.getMidPoint();
			if (area instanceof Building) {
				result.add(new Pair<Integer, Integer>((int) ed.getNodeIndex(), SOSGeometryTools.distance(p, point) / DIVISION_UNIT));
			} else { // area is a road
				ReachablityState rs = Reachablity.isReachable((Road) area, ed,point);
				if (rs != ReachablityState.Close) {
					result.add(new Pair<Integer, Integer>((int) ed.getNodeIndex(), SOSGeometryTools.distance(p, point) / DIVISION_UNIT));
				} else {
					if (!flag) {
						flag = true;
						for (SOSArea ar : ((Road) area).getReachableParts())
							if (ar.getShape().contains(point.toGeomPoint())) {
								isInArea = true;
								break;
							}
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
								result.add(new Pair<Integer, Integer>((int) ed.getNodeIndex(), SOSGeometryTools.distance(p, point) / DIVISION_UNIT));
						} else if (!(me.me() instanceof PoliceForce))
							result.add(new Pair<Integer, Integer>((int) ed.getNodeIndex(), SOSGeometryTools.distance(p, point) / DIVISION_UNIT));
					}
				}
			}
		}
		return result;
	}

	protected long getMinLenght(Collection<Integer> outsideNodes, ArrayList<Integer> costs) {
		long min = Long.MAX_VALUE;
		int i = 0;
		for (Integer node : outsideNodes) {
			if (dijkstra.getWeight(node) + costs.get(i) < min) {
				min = dijkstra.getWeight(node) + costs.get(i);
			}
			i++;
		}
		return min;
	}

	@Override
	public long getWeightTo(Area target, int x, int y) {
		log().logln("getWeightTo(Area a,int x,int y) targets) to " + target + " x=" + x + " y=" + y);
		prepareDijkstraFromMe();
		ArrayList<Integer> outsideNodesOfTarget = new ArrayList<Integer>();
		outsideNodesOfTarget.addAll(getOutsideNodesReachabilityTrue(target, x, y));
		if (outsideNodesOfTarget.isEmpty())
			return UNREACHABLE_COST;
		int[] costs = getCostsOfOutSidesFrom(x, y, outsideNodesOfTarget);
		int i = 0;
		long min = Long.MAX_VALUE;
		for (int node : outsideNodesOfTarget) {
			if (dijkstra.getWeight(node) + costs[i] < min) {
				min = dijkstra.getWeight(node) + costs[i];
			}
			i++;
		}

		return min / DIVISION_UNIT_FOR_GET;
	}

	@Override
	public long getWeightToLowProcess(Area target, int x, int y) {
		log().logln("getWeightToLowProcess(Area a)(every point of area) targets) to x,y not checked!!!!!");
		prepareDijkstraFromMe();
		int[] outsideNodesOfTarget = target.getPassableEdgeNodeIndexes();
		if (outsideNodesOfTarget.length == 0)
			return UNREACHABLE_COST;
		long min = Long.MAX_VALUE;
		for (int i = 0; i < outsideNodesOfTarget.length; i++) {
			if (dijkstra.getWeight(outsideNodesOfTarget[i]) < min) {
				min = Math.min(min, dijkstra.getWeight(outsideNodesOfTarget[i]));

			}
		}
		return min / DIVISION_UNIT_FOR_GET;
	}

	public long[] getMMLenToTargets_notImportantPoint(Pair<? extends Area, Point2D> from, ArrayList<Pair<? extends Area, Point2D>> destinations) {
		log().logln("getLenToTargets " + from + " targets" + destinations);
		long[] result = new long[destinations.size()];
		Arrays.fill(result, -1);
		int count = 0;
		for (int i = 0; i < destinations.size(); i++)
			if (isInSameArea(from, destinations.get(i))) {
				result[i] = SOSGeometryTools.distance(from.second(), destinations.get(i).second());
				count++;
			}
		if (count == result.length)
			return result;
		// ------------------------getting outside nodes of sources and costs
		HashMap<Integer, Integer> outsideNodesCosts = new HashMap<Integer, Integer>();
		for (Edge ed : from.first().getPassableEdges()) {
			outsideNodesCosts.put((int) ed.getNodeIndex(), 1);
		}
		log().debug("outsideNodesCosts=" + outsideNodesCosts);
		if (outsideNodesCosts.isEmpty()) {
			for (int i = 0; i < result.length; i++)
				if (result[i] == -1)
					result[i] = MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		}
		try {
			log().debug("Run Dijkstra RunForLenght");
			this.dijkstra.RunForLenght(wg, new ArrayList<Integer>(outsideNodesCosts.keySet()), new ArrayList<Integer>(outsideNodesCosts.values()));
			lastDijkstraRunTimeOnME = (short) (me.time() - 1);
		} catch (Exception e) {
			log().error(e);
		}
		// ------------------------getting outside nodes of destinations and costs

		for (int i = 0; i < destinations.size(); i++) {
			if (result[i] != -1)
				continue;
			outsideNodesCosts = new HashMap<Integer, Integer>();
			for (Edge ed : destinations.get(i).first().getPassableEdges())
				outsideNodesCosts.put((int) ed.getNodeIndex(), 1);
			result[i] = getMinLenght(outsideNodesCosts.keySet(), new ArrayList<Integer>(outsideNodesCosts.values()));
		}
		return result;
	}

	public long[] getLenToTargets(Pair<? extends Area, Point2D> from, ArrayList<Pair<? extends Area, Point2D>> destinations) {
		log().logln("getLenToTargets " + from + " targets" + destinations);
		long[] result = new long[destinations.size()];
		Arrays.fill(result, -1);
		int count = 0;
		for (int i = 0; i < destinations.size(); i++)
			if (isInSameArea(from, destinations.get(i))) {
				result[i] = SOSGeometryTools.distance(from.second(), destinations.get(i).second()) / DIVISION_UNIT_FOR_GET;
				count++;
			}
		if (count == result.length)
			return result;
		// ------------------------getting outside nodes of sources and costs
		HashMap<Integer, Integer> outsideNodesCosts = new HashMap<Integer, Integer>();
		ArrayList<Pair<Integer, Integer>> temp = getOutsideNodesWithCostsForLen(from.first(), from.second());
		for (Pair<Integer, Integer> p : temp)
			outsideNodesCosts.put(p.first(), p.second());
		log().debug("outsideNodesCosts=" + temp);
		if (temp.isEmpty()) {
			for (int i = 0; i < result.length; i++)
				if (result[i] == -1)
					result[i] = Integer.MAX_VALUE;
		}
		try {
			log().debug("Run Dijkstra RunForLenght");
			this.dijkstra.RunForLenght(wg, new ArrayList<Integer>(outsideNodesCosts.keySet()), new ArrayList<Integer>(outsideNodesCosts.values()));
			lastDijkstraRunTimeOnME = (short) (me.time() - 1);
		} catch (Exception e) {
			log().error(e);
		}
		// ------------------------getting outside nodes of destinations and costs

		for (int i = 0; i < destinations.size(); i++) {
			if (result[i] != -1)
				continue;
			outsideNodesCosts = new HashMap<Integer, Integer>();
			temp = getOutsideNodesWithCostsForLen(destinations.get(i).first(), destinations.get(i).second());
			if (temp.isEmpty()) {
				result[i] = Integer.MAX_VALUE;
				continue;
			}
			for (Pair<Integer, Integer> p : temp)
				outsideNodesCosts.put(p.first(), p.second());
			result[i] = getMinLenght(outsideNodesCosts.keySet(), new ArrayList<Integer>(outsideNodesCosts.values())) / DIVISION_UNIT_FOR_GET;
		}
		return result;
	}

	//*************************************Added in IranOpen2011*********************************//
	public Collection<? extends Area> getReachableAreasFrom(Pair<? extends Area, Point2D> from, UnionFind union) {
		List<Area> result = new ArrayList<Area>();
		boolean[] ar_signed = new boolean[me.model().areas().size()];
		LinkedList<Short> queue = new LinkedList<Short>();
		//add own area for some detailed reasons 
		ar_signed[from.first().getAreaIndex()] = true;
		//		result.add(from.first());
		for (Area nei : from.first().getNeighbours()) {
			ar_signed[nei.getAreaIndex()] = true;
			queue.add(nei.getAreaIndex());
		}
		while (!queue.isEmpty()) {
			short first = queue.pollFirst();
			Area first_ar = me.model().areas().get(first);
			if (isReachable(from, new Pair<Area, Point2D>(first_ar, new Point2D(first_ar.getX(), first_ar.getY())), union))
				result.add(first_ar);
			else
				continue;
			for (Area nei : me.model().areas().get(first).getNeighbours()) {
				if (ar_signed[nei.getAreaIndex()])
					continue;
				ar_signed[nei.getAreaIndex()] = true;
				queue.add(nei.getAreaIndex());
			}
		}
		return result;
	}

	/********************************************************************************/
	/**
	 * @author Ali
	 */
	public Collection<? extends Area> getReachableAreasFrom(StandardEntity entity) {
		ArrayList<Area> result = new ArrayList<Area>();
		boolean[] ar_signed = new boolean[me.model().nodes().size()];

		Area currentArea = entity.getAreaPosition();
		ArrayList<Node> startNodes = new ArrayList<Node>();
		if (entity instanceof Human) {
			for (Edge ed : ((Human) entity).getImReachableToEdges()) {
				startNodes.add(me.model().nodes().get(ed.getNodeIndex()));
			}
		}
		for (Edge edge : currentArea.getPassableEdges()) {
			Node relatedNode = me.model().nodes().get(edge.getNodeIndex());
			if (currentArea instanceof Building) {
				startNodes.add(relatedNode);
				ar_signed[relatedNode.getIndex()] = true;
				if (!result.contains(currentArea))//TODO
					result.add(currentArea);
			}
			if (currentArea instanceof Road) {
				if (Reachablity.isReachable((Road) currentArea, entity.getPositionPoint(), edge) == ReachablityState.Open) {
					startNodes.add(relatedNode);
					ar_signed[relatedNode.getIndex()] = true;
					if (!result.contains(currentArea))//TODO
						result.add(currentArea);
				}
			}
		}
		getReachableAreasFrom(startNodes, ar_signed, result);
		return result;
	}

	/********************************************************************************/
	/**
	 * @author Ali
	 */
	public Collection<? extends Area> getFogyReachableAreasFrom(Pair<? extends Area, Point2D> from) {
		ArrayList<Area> result = new ArrayList<Area>();
		boolean[] ar_signed = new boolean[me.model().nodes().size()];

		Area currentArea = from.first();
		ArrayList<Node> startNodes = new ArrayList<Node>();
		for (Edge edge : currentArea.getPassableEdges()) {
			Node relatedNode = me.model().nodes().get(edge.getNodeIndex());
			if (currentArea instanceof Building) {
				startNodes.add(relatedNode);
				ar_signed[relatedNode.getIndex()] = true;
				if (!result.contains(currentArea))//TODO
					result.add(currentArea);
			}
			if (currentArea instanceof Road) {
				if (Reachablity.isReachable((Road) currentArea, from.second(), edge) != ReachablityState.Close) {
					startNodes.add(relatedNode);
					ar_signed[relatedNode.getIndex()] = true;
					if (!result.contains(currentArea))//TODO
						result.add(currentArea);
				}
			}
		}
		getReachableAreasFrom(startNodes, ar_signed, result);
		return result;
	}

	/********************************************************************************/
	/**
	 * @author Ali
	 */
	public void getReachableAreasFrom(ArrayList<Node> from, boolean[] ar_signed, Collection<Area> result) {
		if (from.size() == 0)
			return;
		ArrayList<Node> newNodes = new ArrayList<Node>();
		for (Node node : from) {
			short[] wge = me.model().getWorldGraph().getEdgesOf(node.getIndex());
			for (short s : wge) {
				GraphEdge graphEdge = me.model().graphEdges().get(s);

				if (graphEdge.getState() == GraphEdgeState.Open) {
					if (graphEdge instanceof WorldGraphEdge) {
						Area insideArea = me.model().areas().get(((WorldGraphEdge) graphEdge).getInsideAreaIndex());
						if (!result.contains(insideArea))//TODO
							result.add(insideArea);
					}
					Node headNode = me.model().nodes().get(graphEdge.getHeadIndex());
					if (!ar_signed[headNode.getIndex()]) {
						ar_signed[headNode.getIndex()] = true;
						newNodes.add(headNode);
					}

					Node tailNode = me.model().nodes().get(graphEdge.getTailIndex());
					if (!ar_signed[tailNode.getIndex()]) {
						ar_signed[tailNode.getIndex()] = true;
						newNodes.add(tailNode);
					}
				}
			}
		}
		getReachableAreasFrom(newNodes, ar_signed, result);

	}

	@Override
	protected void updateWeigths() {
		for (short i = 0; i < weights.getSize(); ++i) {
			GraphEdge ge = me.model().graphEdges().get(i);
			int weight = 0;
			if (ge.getState() == GraphEdgeState.FoggyOpen && !ge.haveTraffic()) {
				weight = (ge.getLenght() / DIVISION_UNIT) * 2;
			} else if (ge.getState() == GraphEdgeState.FoggyBlock && !ge.haveTraffic()) {
				weight = (ge.getLenght() / DIVISION_UNIT) * 5;
			} else if (ge.getState() == GraphEdgeState.Open && !ge.haveTraffic()) {
				weight = (ge.getLenght() / DIVISION_UNIT);
			} else {
				weight = UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
			}
			if (weight == 0)
				weight = 1;
			if (ge instanceof WorldGraphEdge) {
				WorldGraphEdge wge = (WorldGraphEdge) ge;
				Area insideArea = me.model().areas().get(wge.getInsideAreaIndex());

				if (insideArea.getSOSGroundArea() < PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM) {
					weight *= 100;
				} else if (insideArea instanceof Building && ((Building) insideArea).isBurning()) {
					weight *= 100;
				}
			}
			((GraphWeight) weights).setWeight(i, weight);

		}
	}

}
