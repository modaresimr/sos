package sos.base.util.genetic;

import org.apache.commons.math3.genetics.ListPopulation;

/**
 * This is only used in SOSGenerationLibrary and is used to select the nextgeneration.
 * 
 * @author Salim
 */
public interface SOSSelectionPolicy {
	/**
	 * Select next generation
	 * 
	 * @param population
	 * @return
	 */
	public ListPopulation selectNextGeneration(ListPopulation population);
}
