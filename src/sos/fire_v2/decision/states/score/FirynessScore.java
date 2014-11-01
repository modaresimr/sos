package sos.fire_v2.decision.states.score;

import java.util.ArrayList;
import java.util.List;

import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Building;

public class FirynessScore extends AbstractScore{

	private int priority;
	public FirynessScore(SOSAgent<?> agent) {
		super(agent);
		priority=1000;
	}

	@Override
	public int getScore(Building b) {
		if(b.isBurning())
			return (3-b.getFieryness())*priority;
		return 0;
	}
	@Override
	public List<Pair<String, String>> getAditionalLogs(Building b) {
		ArrayList<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
		if(b.isFierynessDefined())
			result.add(new Pair<String, String>("Firyness", b.getFieryness()+""));
		else
			result.add(new Pair<String, String>("Firyness", null));
		return result;
	}

}
