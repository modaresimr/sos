package sos.search_v2.tools;

import java.util.Collection;

import sos.base.util.geom.ShapeInArea;

public class SearchTask {
	private Collection<ShapeInArea> area;

	public SearchTask(Collection<ShapeInArea> targetArea) {
		super();
		this.setArea(targetArea);
	}

	public Collection<ShapeInArea> getArea() {
		return area;
	}

	public void setArea(Collection<ShapeInArea> area) {
		this.area = area;
	}

	@Override
	public String toString() {
		StringBuffer sd = new StringBuffer("Task[");
		for (ShapeInArea a : area) {
			sd.append(a.getArea()+",");
		}
		sd.append("]");
		return sd.toString();
	}
}
