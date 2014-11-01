package sos.base.util.namayangar.sosLayer.search;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class IsSearchedForCivilian extends SOSAbstractToolsLayer<Building> {

	public IsSearchedForCivilian() {
		super(Building.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 6;
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
		return true;
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		if(!entity.isSearchedForCivilian())
			return null;
		g.setColor(Color.green.darker().darker());
		Shape shape = NamayangarUtils.transformShape(entity, transform);
		g.setStroke(new BasicStroke(3));
		NamayangarUtils.drawString("T:"+entity.getLastSearchedForCivilianTime(), g, transform, entity);
		g.draw(shape);
		g.setStroke(new BasicStroke(1));
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
