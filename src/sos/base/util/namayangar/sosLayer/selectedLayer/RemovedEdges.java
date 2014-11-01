package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;

import sos.base.entities.Road;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class RemovedEdges extends SOSAbstractSelectedComponent<Road> {

	public RemovedEdges() {
		super(Road.class);
	}

	@Override
	protected void paint(Road r, Graphics2D g, ScreenTransform t) {
		g.setColor(Color.RED);
//		for (Edge e : r.removedEdges) {
//			g.drawLine(t.xToScreen(e.getStartX()), t.yToScreen(e.getStartY()), t.xToScreen(e.getEndX()), t.yToScreen(e.getEndY()));
//		}

	}

	@Override
	public boolean isValid() {
		return true;
	}

}
