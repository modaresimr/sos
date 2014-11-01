package sos.search_v2.tools.genetic;

import java.util.List;

import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import sos.base.entities.Human;
import sos.base.util.genetic.SOSListChromsome;
import sos.search_v2.tools.cluster.ClusterData;
import sos.tools.Utils;

/**
 * S.O.S chromosome class for running genetic on assigning agents to clusters.
 * 
 * @author Salim
 */
public class GCAChromosome extends SOSListChromsome<ClusterData> {

	private final List<? extends Human> agents;

	public GCAChromosome(ClusterData[] representation, List<? extends Human> agents) throws InvalidRepresentationException {
		super(representation);
		this.agents = agents;
	}

	/**
	 * @author Yoosef
	 */
	@Override
	public double fitness() {
		List<ClusterData> representation = getRepresentation();
		if (representation == null)
			throw new Error("Chromosome is null in fitness function");
		int sumDistances = 0;
		for (int i = 0; i < agents.size(); i++) {
			Human fb = agents.get(i);
			ClusterData gen = representation.get(i);
			sumDistances += Utils.distance(gen.getX(), gen.getY(), fb.getX(), fb.getY());
		}
		return 1f/(sumDistances+1);
	}

	/**
	 * CHecks validity by checking occurances of ClusterDatas in lsit. No clsuter could be repeated
	 */
	@Override
	protected void checkValidity(List<ClusterData> data) throws InvalidRepresentationException {
		boolean[] flags = new boolean[data.size()];
		for (ClusterData clusterData : data) {
			if (flags[clusterData.getIndex()]) {
				System.out.println("not valid chera ? "+data);
				throw new InvalidRepresentationException(new DummyLocalizable(" Not valid Data in GCAChromosome"), data);
			} else {
				flags[clusterData.getIndex()] = true;
			}
		}
	}

	/**
	 * this function simply changes the input to an [] and returns an instance of GCAChromosome
	 */
	@Override
	public GCAChromosome newFixedLengthChromosome(List<ClusterData> data) {
		ClusterData[] tmp = new ClusterData[data.size()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = data.get(i);
		}
		return new GCAChromosome(tmp, agents);
	}

}
