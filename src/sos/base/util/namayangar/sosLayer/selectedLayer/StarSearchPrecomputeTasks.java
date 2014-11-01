package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Human;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class StarSearchPrecomputeTasks extends SOSAbstractToolsLayer<Human> {

	public StarSearchPrecomputeTasks() {
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
		//		setEntities(search().strategyChooser.starSearch.getStarZones()[0].getSubZones()[search().strategyChooser.starSearch.getPreComputeAssignedSubZone()]);
		setEntities(model().agents());

	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	protected Shape render(Human h, Graphics2D g, ScreenTransform transform) {
		//		int c = model().sosAgent().newSearch.getBlockSearchClusters().indexOf(entity);
//		g.setColor(Color.red.darker().darker());
//		for (Building b : h.getAgent().newSearch.strategyChooser.starSearch.getStarZones()[h.getAgent().newSearch.strategyChooser.starSearch.getPreComputeAssignedZone()].getSubZones()[h.getAgent().newSearch.strategyChooser.starSearch.getPreComputeAssignedSubZone()]) {
//			Shape shape = NamayangarUtils.transformShape(b, transform);
//			g.fill(shape);
//		}
		return h.getShape();
	}


	@Override
	public ArrayList<Pair<String, String>> sosInspect(Human entity) {
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}
}
