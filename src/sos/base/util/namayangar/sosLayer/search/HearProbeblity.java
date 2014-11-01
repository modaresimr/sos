package sos.base.util.namayangar.sosLayer.search;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.search_v2.worldModel.SearchBuilding;

public class HearProbeblity extends SOSAbstractToolsLayer<SearchBuilding> {

	public HearProbeblity() {
		super(SearchBuilding.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 50;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().sosAgent().newSearch.getSearchWorld().getSearchBuildings());
	}

	@Override
	protected Shape render(SearchBuilding entity, Graphics2D g, ScreenTransform transform) {
		if(entity.getCivProbability()>0){
			g.setColor(Color.green);
			NamayangarUtils.fillEntity(entity.getRealBuilding(), g, transform);
			g.setColor(Color.red);
			NamayangarUtils.drawString((int)(entity.getCivProbability()*1000)+"", g, transform, entity.getRealBuilding());
		}
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(SearchBuilding entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LayerType getLayerType() {
		// TODO Auto-generated method stub
		return LayerType.Search;
	}

}
