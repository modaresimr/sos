package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Graphics2D;

import sos.base.entities.Road;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class MergedBlockadeLayer extends SOSAbstractSelectedComponent<Road> {

	public MergedBlockadeLayer() {
		super(Road.class);
	}
	@Override
	protected void paint(Road entity, Graphics2D g, ScreenTransform transform) {
//		for (SOSArea a : entity.getMergedBlockades()) {
//			Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
//			g.setColor(c);
//			g.draw(NamayangarUtils.transformShape(a, transform));
//		}
	}

	@Override
	public boolean isValid() {
		return true;
	}

	
}
