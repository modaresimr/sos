package sos.base.util.namayangar.sosLayer.fire;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import sos.base.entities.Building;
import sos.base.util.geom.RegularPolygon;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.selectedLayer.SOSAbstractSelectedComponent;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class ExtinguishableArea extends SOSAbstractSelectedComponent<Building> {

	public ExtinguishableArea() {
		super(Building.class);
	}

	@Override
	protected void paint(Building building, Graphics2D g, ScreenTransform transform) {
		RegularPolygon polygon = new RegularPolygon(building.getX(), building.getY(), FireBrigadeAgent.maxDistance, sos.fire_v2.base.tools.ExtinguishableArea.NUMBER_OF_VERTICES);
		g.setColor(Color.BLUE);
		NamayangarUtils.drawShape(polygon, g, transform);
		g.setColor(Color.red);
		for (ShapeInArea b : building.getFireBuilding().getExtinguishableArea().getBuildingsShapeInArea()) {
			NamayangarUtils.drawShape(b, g, transform);
		}
		g.setColor(Color.green);
		for (ShapeInArea b : building.getFireBuilding().getExtinguishableArea().getRoadsShapeInArea()) {
			NamayangarUtils.drawShape(b, g, transform);
		}
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.blue);
		for (ShapeInArea b : building.getFireBuilding().getExtinguishableArea().getExtinguishableSensibleArea()) {
			NamayangarUtils.drawShape(b, g, transform);
		}
		g.setStroke(new BasicStroke(1));
	}

	@Override
	public boolean isValid() {
		return model() instanceof FireWorldModel;
	}

}
