package sos.search_v2.tools.genetic;

import java.util.Iterator;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.Population;

public class GCAPopulation implements Population {

	@Override
	public Iterator<Chromosome> iterator() {
		return null;
	}

	@Override
	public void addChromosome(Chromosome arg0) throws NumberIsTooLargeException {
		// TODO Auto-generated method stub

	}

	@Override
	public Chromosome getFittestChromosome() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPopulationLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPopulationSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Population nextGeneration() {
		return null;
	}

}
