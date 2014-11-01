package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class NearBuildingLayer extends SOSAbstractSelectedComponent<Building> {

	public NearBuildingLayer() {
		super(Building.class);
//		setVisible(true);
	}
	@Override
	protected void paint(Building entity, Graphics2D g, ScreenTransform transform) {
		Shape shape;
		g.setColor(Color.blue);
		for (Building  b : model().getObjectsInRange(entity, 200000, Building.class)) {
			shape = NamayangarUtils.transformShape(b, transform);
			g.draw(shape);
		}
		g.setColor(Color.GREEN);
		shape = NamayangarUtils.transformShape(entity, transform);
		g.fill(shape);


	}

	@Override
	public boolean isValid() {
		return true;
	}

}
