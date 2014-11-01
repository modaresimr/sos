package sos.search_v2.tools;

import java.util.HashSet;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import sos.base.entities.Building;
import sos.search_v2.tools.cluster.SOSDenseInstance;

public class SearchRegion {
	private double x;
	private double y;
	private final Dataset dataset;
	private final int id;
	private HashSet<Building> buildings;

	public SearchRegion(Dataset dataset, int id) {
		buildings = new HashSet<Building>();
		this.dataset = dataset;
		this.id = id;
		for (Instance instance : dataset) {
			setY(getY() + ((SOSDenseInstance) instance).getBuilding().getY());
			setX(getX() + ((SOSDenseInstance) instance).getBuilding().getX());
			buildings.add(((SOSDenseInstance) instance).getBuilding());
		}
		setX(getX() / dataset.size());
		setY(getY() / dataset.size());
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean isEmpty() {
		return getDataset().size() == 0;
	}

	public HashSet<Building> getBuildings() {
		return buildings;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public int getId() {
		return id;
	}
	@Override
	public String toString() {
		return "SearchRegion [id:"+getId()+" size:"+buildings.size()+" ]";
	}

}