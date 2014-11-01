package sos.base.util.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ListPopulation;

public class SOSSelectBest implements SOSSelectionPolicy {

	@Override
	public ListPopulation selectNextGeneration(ListPopulation population) {
		List<Chromosome> tmp = new ArrayList(population.getChromosomes());
		Collections.sort(tmp, new SelectComparator());

		SOSListPopulation result = new SOSListPopulation(population.getPopulationLimit()/2);

		for (int i = 0; i < tmp.size() / 2; i++) {
			result.addChromosome(tmp.get(i));
		}
		return result;
	}
}

/**
 * Sorts chromosomes in list descending
 * 
 * @author Salim
 */
class SelectComparator implements Comparator<Chromosome> {

	@Override
	public int compare(Chromosome c1, Chromosome c2) {

		if (c1.getFitness() > c2.getFitness()) {
			return -1;
		} else if (c1.getFitness() == c2.getFitness()) {
			return 0;
		} else
			return 1;

	}
}
