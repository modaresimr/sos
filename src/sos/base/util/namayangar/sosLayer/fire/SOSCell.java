package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;

public class SOSCell extends SOSAbstractToolsLayer<Building> implements SelectedObjectListener {
	public SOSCell() {
		super(Building.class);
		setVisible(false);
	}


	@Override
	public int getZIndex() {
		return 200;
	}

	@Override
	public void objectSelected(SOSSelectedObj sso) {

	}

	@Override
	protected void makeEntities() {
		ArrayList<Building> b = new ArrayList<Building>();
		b.add(model().buildings().get(0));
		setEntities(b);
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.red);
		for (int x = 0; x < model().estimatedModel.getAirTemp().length; x++)
			for (int y = 0; y < model().estimatedModel.getAirTemp()[0].length; y++) {
//				g.drawString(((int) model().estimatedModel.getAirTemp()[x][y]) + "", transform.xToScreen(model().estimatedModel.worldCells[x][y].getxCenter()), transform.yToScreen(model().estimatedModel.worldCells[x][y].getyCenter()));
			}
		return null;
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
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.None;
	}
}
