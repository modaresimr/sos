package sos.base.util.namayangar.sosLayer.search;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class SearchOtherCivilianArea extends SOSAbstractToolsLayer<Building> {

	public SearchOtherCivilianArea() {
		super(Building.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 10;
	}

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
		return model().me() instanceof Human;
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		if(entity.isSearchedForCivilian())
			return null;
		g.setColor(Color.orange);
		Shape shape = NamayangarUtils.transformShape(entity, transform);
		g.fill(shape);
		return null;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}
}
