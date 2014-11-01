package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;

import sos.base.entities.StandardEntity;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class IsReachable extends SOSAbstractSelectedComponent<StandardEntity> {

	public IsReachable() {
		super(StandardEntity.class);
	}

	@Override
	protected void paint(StandardEntity selectedObj, Graphics2D g, ScreenTransform transform) {
		boolean b = model().sosAgent().move.isReallyUnreachableXYPolice(selectedObj.getPositionPair());
		if (!b)
			g.setColor(Color.green);
		else
			g.setColor(Color.red);
		NamayangarUtils.fillEntity(selectedObj, g, transform);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
