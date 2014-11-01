package sos.base.util.namayangar.sosLayer.reachablity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Road;
import sos.base.reachablity.tools.SOSArea;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class ReachablityParts extends SOSAbstractToolsLayer<Road> {

	public ReachablityParts() {
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
		for (SOSArea reachablePart : road.getReachableParts()) {

			Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
			g.setColor(c);
			NamayangarUtils.drawShape(reachablePart.getShape(), g, transform);
			g.setColor(Color.red);
			if (reachablePart.getHoles() != null)
				for (SOSArea hole : reachablePart.getHoles()) {
					NamayangarUtils.drawShape(hole.getShape(), g, transform);
				}
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
