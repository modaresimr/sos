package sos.base.move.types;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.worldGraph.WorldGraph;
import sos.police_v2.base.MoveGraphWeight;

public abstract class NewMoveType extends MoveType {

	public NewMoveType(SOSAgent<? extends StandardEntity> me, WorldGraph graph, MoveGraphWeight weight) {
		super(me, graph, weight);
	}
	@Override
	protected ArrayList<Integer> getOutsideNodes(Area area) {
		ArrayList<Integer> result = new ArrayList<Integer>(5);
		for (Edge ed : area.getPassableEdges())
			result.add((int) ed.getNodeIndex());
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
	protected ArrayList<Integer> getOutsideNodes(Area area, int x, int y) {
		return getOutsideNodes(area);
	}

	@Override
	protected int getCost(Area area, Edge ed, Point2D dst) {
		return weigths().getWeightToXY(area, ed, dst);
	}

	@Override
	protected int getCost(Area area, Point2D start,Edge ed) {
		return weigths().getWeightXY(area, start, ed);
	}

	@Override
	protected int getCost(Area area, Point2D start, Point2D dst) {
		return weigths().getWeightFromXYToXY(area, start,dst);
	}

	@Override
	protected boolean isInSameAreaWithMe(Pair<? extends Area, Point2D> pair) {
		return (pair.first().getAreaIndex() == ((Area) ((Human) me.me()).getPosition()).getAreaIndex());
	}

	@Override
	protected boolean isInSameArea(Pair<? extends Area, Point2D> a, Pair<? extends Area, Point2D> b) {
		return (a.first().getAreaIndex() == b.first().getAreaIndex());
	}

	@Override
	protected void updateWeigths() {
		weigths().updateWeigths();

	}

	public MoveGraphWeight weigths() {
		return ((MoveGraphWeight) weights);
	}

}
