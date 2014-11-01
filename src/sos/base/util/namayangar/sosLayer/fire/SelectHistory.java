package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class SelectHistory extends SOSAbstractToolsLayer<Building> {

	public SelectHistory() {
		super(Building.class);
		setVisible(true);
	}

	@Override
	public int getZIndex() {
		return 100001;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().buildings());
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		return new JScrollPane(((FireWorldModel) model()).owner().mySelectHistory, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	@Override
	public boolean isValid() {
		return model() instanceof FireWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		ArrayList<Pair<String, String>> inspect = new ArrayList<Pair<String, String>>();
		return inspect;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
