package sos.base.util.namayangar.sosLayer.ambulance;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Human;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class AmbulanceTask extends SOSAbstractToolsLayer<Human> {

	public AmbulanceTask() {
		super(Human.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 90;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().humans());
	}

	@Override
	protected Shape render(Human entity, Graphics2D g, ScreenTransform transform) {
		int x = transform.xToScreen(entity.getX());
		int y = transform.yToScreen(entity.getY());
		g.setColor(Color.yellow);
		g.drawString("DT:" + entity.getRescueInfo().getDeathTime(), x, y);
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
//		if (model() instanceof PoliceWorldModel || model() instanceof FireDisasterSpace)
//			setVisible(false);
//		else
//			setVisible(true);
		return true;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Human entity) {
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Ambulance;
	}
}
