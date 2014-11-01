package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Map.Entry;

import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class NeighboursLayer extends SOSAbstractSelectedComponent<Building> {

	public NeighboursLayer() {
		super(Building.class);
		setVisible(false);
	}
	@Override
	protected void paint(Building entity, Graphics2D g, ScreenTransform transform) {
		Shape shape;
		g.setColor(Color.yellow);
		for (Building b : entity.neighbors_Building()) {
			shape = NamayangarUtils.transformShape(b, transform);
			g.draw(shape);
			NamayangarUtils.drawString(entity.getNeighValue(b)+"", g, transform, b);
		}
		g.setColor(Color.GREEN);
		shape = NamayangarUtils.transformShape(entity, transform);
		g.fill(shape);
	}

	String getBuildValue(Building b, Building b2){
		for (Entry<Short, Float> buildValue : b.neighbors_BuildValue().entrySet()) {
			if(buildValue.getKey()==b2.getBuildingIndex())
				return buildValue.getValue()+"";
		}
		return "-";
	}
	@Override
	public boolean isValid() {
		return true;
	}

}
