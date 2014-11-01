package sos.search_v2.tools;

import net.sf.javaml.core.DenseInstance;


public class Clusterance extends DenseInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Integer index;

	public Clusterance(double[] att, Integer index) {
		super(att);
		this.index = index;
	}

	public Integer getIndex() {
		return index;
	}

}