package sos.base.util.genetic;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.genetics.ListPopulation;
import org.apache.commons.math3.genetics.Population;

public class SOSListPopulation extends ListPopulation {
	public SOSListPopulation(int populationLimit) throws NotPositiveException {
		super(populationLimit);
	}

	@Override
	public Population nextGeneration() {
		return new SOSListPopulation(getPopulationLimit());
	}


}
