package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class ImReachableToEdges extends SOSAbstractSelectedComponent<Human>{

	public ImReachableToEdges() {
		super(Human.class);
	}
	@Override
	protected void paint(Human selectedObj, Graphics2D g, ScreenTransform transform) {
		for (Edge e : selectedObj.getImReachableToEdges()) {
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.green);
			NamayangarUtils.drawLine(e.getStartX(), e.getStartY(),e.getEndX(), e.getEndY(), g, transform);
			g.setStroke(new BasicStroke(1));
		}
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
