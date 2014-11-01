package sos.fire_v2.decision.states.score;

import java.util.List;

import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Building;

public class DistanceScore extends AbstractScore{

	private int priority;

	public DistanceScore(SOSAgent<?> agent) {
		super(agent);
		priority=100;
	}

	@Override
	public int getScore(Building b) {
		return (int) (positioningcostEvaluator.PositioningTime(b)*priority);
	}

	@Override
	public List<Pair<String, String>> getAditionalLogs(Building b) {
		return null;
	}

}
