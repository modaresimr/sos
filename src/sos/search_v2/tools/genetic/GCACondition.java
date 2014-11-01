package sos.search_v2.tools.genetic;

import java.util.List;

import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;

import sos.base.entities.Human;
import sos.base.util.genetic.SOSGeneticAlgorithm;

public class GCACondition implements StoppingCondition {

	private final SOSGeneticAlgorithm geneticAlg;
	private final List<Human> agents;
	private int maxGeneration;

	public GCACondition(SOSGeneticAlgorithm ga, List<Human> agents, int maxGeneration) {
		this.geneticAlg = ga;
		this.agents = agents;
		this.maxGeneration = maxGeneration;
	}

	@Override
	public boolean isSatisfied(Population population) {

		return geneticAlg.getGenerationsEvolved() > maxGeneration;
	}

}
