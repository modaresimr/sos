package sos.base.util.genetic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;
import org.apache.commons.math3.random.RandomGenerator;

public class ExchangeMutation<T> implements MutationPolicy {
	private final float rate;
	private RandomGenerator random;

	public ExchangeMutation(float rate, RandomGenerator random) {
		this.rate = rate;
		this.random = random;
	}

	/**
	 * mutate the chromosome<br>
	 * This function chooses two gene and exchanges them. This exchange process is taken place N times. Which N is equal to length of chromozone *1/2* rate. Rate is a number between 0 and 1 and 1/2 is because each exchange applies on two genes.
	 */
	@Override
	public Chromosome mutate(Chromosome c) throws MathIllegalArgumentException {
		if (!(c instanceof SOSListChromsome))
			throw new MathIllegalArgumentException(new DummyLocalizable(" Could not mutate " + c + " by ExchangeMutation class!!"), c);
		SOSListChromsome<T> slc = (SOSListChromsome<T>) c;
		int length = slc.getSize();
		int mutationSize = (int) (length * rate) / 2;// devided by 2 because each mutation involves two genes
		List<T> representation = new ArrayList<T>(slc.getRepresentation());
		int N = representation.size();
		int x = -1;
		int y = -1;

		while (mutationSize > 0) {

			x = (Math.abs(random.nextInt()) % N);
			while ((y = (Math.abs(random.nextInt()) % N)) == x)
				;
			T temp = representation.get(x);
			representation.set(x, representation.get(y));
			representation.set(y, temp);

			mutationSize--;
		}
		return slc.newFixedLengthChromosome(representation);
	}
}
