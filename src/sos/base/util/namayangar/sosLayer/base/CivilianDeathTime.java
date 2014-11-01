package sos.base.util.namayangar.sosLayer.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Civilian;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class CivilianDeathTime extends SOSAbstractToolsLayer<Civilian> {

	public CivilianDeathTime() {
		super(Civilian.class);
	}

	@Override
	public int getZIndex() {
		return 1000;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().civilians());
	}

	@Override
	protected Shape render(Civilian entity, Graphics2D g, ScreenTransform transform) {
		if (entity != null)
			if (entity.isPositionDefined()) {

				int deadtime = entity.getRescueInfo().getDeathTime();
				int time = model().time();
				if (deadtime - time > 70)
					g.setColor(Color.green);
				else if (deadtime - time > 40)
					g.setColor(Color.yellow);
				else if (deadtime - time > 0)
					g.setColor(Color.red);
				else
					g.setColor(Color.black);
				String valid = "";
				if (entity.getRescueInfo().getDeathTime() - model().time() < entity.getBuriedness() / 3 + 5) {
					valid = "✞";
				}
				NamayangarUtils.drawString(deadtime + valid, g, transform, entity);
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
		return true;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Civilian entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Base;
	}

}
