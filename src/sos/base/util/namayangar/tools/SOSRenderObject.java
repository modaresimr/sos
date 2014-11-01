package sos.base.util.namayangar.tools;

import java.awt.Shape;

import sos.base.util.namayangar.view.RenderedObject;
import sos.base.util.namayangar.view.ViewLayer;

public class SOSRenderObject extends RenderedObject{

	private final ViewLayer sosLayer;

	public SOSRenderObject(Object object, Shape shape,ViewLayer sosLayer) {
		super(object, shape);
		this.sosLayer = sosLayer;
	}
	
	public ViewLayer getLayer() {
		return sosLayer;
	}

}
