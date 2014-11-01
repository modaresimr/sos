package sos.base.util.namayangar.sosLayer.fire;

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
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class MapSides extends SOSAbstractToolsLayer<Building> {

	public MapSides() {
		super(Building.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		// TODO Auto-generated method stub
		return 34;
	}

	@Override
	protected void makeEntities() {
		if (model() instanceof FireWorldModel)
			setEntities(model().buildings());
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return model() instanceof FireWorldModel;
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		Shape shape = NamayangarUtils.transformShape(entity, transform);
		g.setColor(Color.green);
		if (entity.isMapSide()) {
			g.fill(shape);
			return shape;
		}
		return null;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
