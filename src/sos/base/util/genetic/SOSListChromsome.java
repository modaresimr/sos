package sos.base.util.genetic;

import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;
/**
 * 
 * @author Yoosef
 *	S.O.S Abstract Choromosome class for Genetic Algorithm
 * @param <T>
 */
public abstract class SOSListChromsome<T> extends AbstractListChromosome<T> {

	public SOSListChromsome(T[] representation) throws InvalidRepresentationException {
		super(representation);
	}

	@Override
	public List<T> getRepresentation() {
		return super.getRepresentation();
	}

	public T get(int index) {
		return getRepresentation().get(index);
	}

	public T set(int index, T element) {
		return getRepresentation().set(index, element);
	}

	public int getSize() {
		return getRepresentation().size();
	}

	@Override
	public abstract double fitness();

	@Override
	protected abstract void checkValidity(List<T> chromosomeRepresentation) throws InvalidRepresentationException;

	@Override
	public abstract AbstractListChromosome<T> newFixedLengthChromosome(List<T> chromosomeRepresentation);
	

}
