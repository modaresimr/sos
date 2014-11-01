package sos.search_v2.tools.cluster;


import net.sf.javaml.core.DenseInstance;
import sos.base.entities.Building;

//Salim
public class SOSDenseInstance extends DenseInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Building building;

	public SOSDenseInstance(double[] att, Building b) {
		super(att);
		this.building = b;
	}

	public Building getBuilding() {
		return building;
	}

}