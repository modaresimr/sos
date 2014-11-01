package sos.base.util.namayangar.sosLayer.reachablity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Blockade;
import sos.base.entities.Road;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class ExpandedBlockdes extends SOSAbstractToolsLayer<Road>{

	public ExpandedBlockdes() {
		super(Road.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 100;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().roads());
	}

	@Override
	protected Shape render(Road road, Graphics2D g, ScreenTransform transform) {
		Color c = Color.YELLOW;//new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
		g.setColor(c);
		if(!road.isBlockadesDefined())
			return null;
		for (Blockade blockade : road.getBlockades()) {
			NamayangarUtils.drawShape(blockade.getExpandedBlock().getShape(), g, transform);
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
	public ArrayList<Pair<String, String>> sosInspect(Road entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Reachablity;
	}

}
