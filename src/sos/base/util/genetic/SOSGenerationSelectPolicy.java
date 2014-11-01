package sos.base.util.genetic;

import org.apache.commons.math3.genetics.Population;

public interface SOSGenerationSelectPolicy {
	/**
	 * This function is used in SOSGeneticAlgorithm to select the next
	 * 
	 * @param population
	 * @return
	 */
	public Population selectNextGeneration(Population population);
}
