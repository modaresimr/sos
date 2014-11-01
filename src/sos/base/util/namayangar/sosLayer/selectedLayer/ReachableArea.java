package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.entities.StandardEntity;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.police_v2.PoliceUtils;

public class ReachableArea extends SOSAbstractSelectedComponent<StandardEntity> {

	public ReachableArea() {
		super(StandardEntity.class);
	}
	@Override
	protected void paint(StandardEntity selectedObj, Graphics2D g, ScreenTransform transform) {
		Collection<Pair<? extends Area, Point2D>> areas = PoliceUtils.getReachableAreasPair(selectedObj);
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.green);
		for (Pair<? extends Area, Point2D> area : areas) {
			g.draw(NamayangarUtils.transformShape(area.first(), transform));
		}
		g.setStroke(new BasicStroke(1));
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
