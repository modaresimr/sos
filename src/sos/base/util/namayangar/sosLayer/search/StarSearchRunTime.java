package sos.base.util.namayangar.sosLayer.search;

 import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class StarSearchRunTime extends SOSAbstractToolsLayer<List<Building>> {

	public StarSearchRunTime() {
		super(null);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 5;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void makeEntities() {
		setEntities(model().buildings());
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	protected Shape render(List<Building> entity, Graphics2D g, ScreenTransform transform) {
//		g.setStroke(new BasicStroke(2));
//
//		g.setColor(Color.GRAY.darker());
//		Shape precomputeZone = starSearch().getPrecomputeAssigedZoneArea();
//		g.draw(NamayangarUtils.transformShape(precomputeZone.getBounds2D(), transform));
//
//		g.setColor(Color.BLUE.darker());
//		Shape currentZone = starSearch().getCurrentAssigenedZoneArea();
//		g.draw(NamayangarUtils.transformShape(currentZone.getBounds2D(), transform));
//
//		g.setColor(Color.LIGHT_GRAY);
//		Shape precomputeSubZone = starSearch().getPrecomputeAssigenedSubZoneArea();
//		g.draw(NamayangarUtils.transformShape(precomputeSubZone.getBounds2D(), transform));
//
//		g.setColor(Color.YELLOW);
//		Shape nextZone = starSearch().getNextAssigenedZoneArea();
//		g.draw(NamayangarUtils.transformShape(nextZone.getBounds2D(), transform));
//
//		g.setColor(Color.BLUE.darker().brighter());
//		ArrayList<Building> buildings = starSearch().getCurrentAssignedSubZone();
//		for (Building b : buildings) {
//			g.draw(NamayangarUtils.transformShape(b, transform));
//		}
//
//		g.setColor(Color.YELLOW.darker().brighter());
//		buildings = starSearch().getNextAssigenedSubZoneBuildings();
//		for (Building b : buildings) {
//			g.fill(NamayangarUtils.transformShape(b, transform));
//		}
//		g.setColor(Color.RED.brighter());
//		Road road = starSearch().getGatheringPointOfMap();
//		g.fill(NamayangarUtils.transformShape(road, transform));
//
//		g.setColor(Color.ORANGE.brighter());
//		road = starSearch().getGatheringPointOfZone();
//		g.fill(NamayangarUtils.transformShape(road, transform));

		return null;
	}
	@Override
	public ArrayList<Pair<String, String>> sosInspect(List<Building> entity) {
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}
}
