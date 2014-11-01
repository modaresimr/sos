package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import sos.base.entities.StandardEntity;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class LineOfSight extends SOSAbstractSelectedComponent<StandardEntity>{

	
	public LineOfSight() {
		super(StandardEntity.class);
		setVisible(false);
	}
	@Override
	protected void paint(StandardEntity selectedObj, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.blue);
		Shape shape = model().sosAgent().lineOfSightPerception.findVisibleShape(selectedObj);
		NamayangarUtils.drawShape(shape, g, transform);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
