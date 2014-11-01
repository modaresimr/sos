package sos.base.util.namayangar.tools;

import java.util.ArrayList;

import sos.base.util.namayangar.view.ViewLayer;

public class SOSSelectedObj {

	ArrayList<ViewLayer> layers=new ArrayList<ViewLayer>();
	private final Object object;
	public SOSSelectedObj(Object object) {
		this.object = object;
	}
	public void addLayer(ViewLayer layer) {
		layers.add(layer);
	}
	public Object getObject() {
		return object;
	}
	public ArrayList<ViewLayer> getLayers() {
		return layers;
	}
}
