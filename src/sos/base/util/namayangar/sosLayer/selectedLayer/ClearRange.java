package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import sos.base.entities.PoliceForce;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.base.worldModel.PoliceWorldModel;

public class ClearRange extends SOSAbstractSelectedComponent<PoliceForce> {

	public ClearRange() {
		super(PoliceForce.class);
	}
	@Override
	protected void paint(PoliceForce entity, Graphics2D g, ScreenTransform t) {
		PoliceForceAgent agent = (PoliceForceAgent) entity.getAgent();
		double ellipseX1 = entity.getPositionPoint().getX()- agent.clearDistance;
		double ellipseY1 = entity.getPositionPoint().getY()+ agent.clearDistance;
		double ellipseX2 = entity.getPositionPoint().getX()+ agent.clearDistance;
		double ellipseY2 = entity.getPositionPoint().getY()- agent.clearDistance;
		
		int x=t.xToScreen(entity.getPositionPoint().getX());
		int y=t.yToScreen(entity.getPositionPoint().getY());
		int x1 = t.xToScreen(ellipseX1);
		int y1 = t.yToScreen(ellipseY1);
		int x2 = t.xToScreen(ellipseX2);
		int y2 = t.yToScreen(ellipseY2);
		int ellipseWidth = x2 - x1;
		int ellipseHeight = y2 - y1;

		Ellipse2D.Double shape = new Ellipse2D.Double(x - ellipseWidth / 2, y - ellipseHeight / 2, ellipseWidth, ellipseHeight);
		g.setColor(Color.yellow);
		g.draw(shape);

	}

	@Override
	public boolean isValid() {
		return model() instanceof PoliceWorldModel;
	}

}
