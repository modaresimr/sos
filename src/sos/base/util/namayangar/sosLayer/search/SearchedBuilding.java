package sos.base.util.namayangar.sosLayer.search;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class SearchedBuilding extends SOSAbstractToolsLayer<sos.search_v2.worldModel.SearchBuilding> {
	public SearchedBuilding() {
		super(sos.search_v2.worldModel.SearchBuilding.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 10;
	}

	@Override
	protected void makeEntities() {
		setEntities(( model()).searchWorldModel.getSearchBuildings());
	}

	@Override
	protected Shape render(sos.search_v2.worldModel.SearchBuilding entity, Graphics2D g, ScreenTransform transform) {
		Shape shape = NamayangarUtils.transformShape(entity.getRealBuilding(), transform);
		if (entity.isHasBeenSeen()) {
			g.setColor(Color.red);
			g.fill(shape);
		}
		return shape;
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
	public ArrayList<Pair<String, String>> sosInspect(sos.search_v2.worldModel.SearchBuilding entity) {
		ArrayList<Pair<String, String>> inspect = new ArrayList<Pair<String, String>>();
		return inspect;
	}
	
	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}

}
