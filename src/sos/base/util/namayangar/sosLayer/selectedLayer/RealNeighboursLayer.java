package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class RealNeighboursLayer extends SOSAbstractSelectedComponent<Building> {

	public RealNeighboursLayer() {
		super(Building.class);
		setVisible(false);
	}
	@Override
	protected void paint(Building entity, Graphics2D g, ScreenTransform transform) {
		Shape shape;
		g.setColor(Color.blue);
		for (Building b : entity.realNeighbors_Building()) {
			shape = NamayangarUtils.transformShape(b, transform);
			g.draw(shape);
			NamayangarUtils.drawString(entity.getRealNeighValue(b)+"", g, transform, b);
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
