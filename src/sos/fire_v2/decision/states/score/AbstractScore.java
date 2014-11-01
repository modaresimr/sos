package sos.fire_v2.decision.states.score;

import java.util.List;

import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.position.PositioningCostEvaluator;

public abstract class AbstractScore {

	protected SOSAgent<?> agent;
	protected PositioningCostEvaluator positioningcostEvaluator;
	public AbstractScore(SOSAgent<?> agent) {
		this.agent = agent;
		positioningcostEvaluator = ((FireBrigadeAgent) agent).positioning.getPositioningCostEvaluator();
	}
	public abstract int getScore(Building b);

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	public abstract List<Pair<String,String>> getAditionalLogs(Building b);
}
