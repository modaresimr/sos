package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;

import sos.base.entities.Building;
import sos.base.entities.SOSPolygon;
import sos.base.util.geom.SOSShape;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

public class SensibleAreasLayer extends SOSAbstractSelectedComponent<Building> {

	public SensibleAreasLayer() {
		super(Building.class);
	}

	@Override
	protected void paint(Building entity, Graphics2D g, ScreenTransform transform) {
		SOSPolygon sh = new SOSPolygon(Arrays.asList(entity.fireSearchBuilding().setSensibleArea()));




		//		g.draw(new Ellipse2D.Float(transform.xToScreen(entity.getX() - 5), transform.yToScreen(entity.getY() - 5), 5.0f, 5.0f));
		g.setColor(Color.red);
		//		g.draw(NamayangarUtils.transformShape(entity.fireSearchBuilding().sensibleArea, transform));

		for (ShapeInArea sosArea : entity.fireSearchBuilding().sensibleAreasOfAreas()) {
			g.setColor(Color.GREEN);
			g.fill(NamayangarUtils.transformShape(sosArea, transform));
			//		SOSShape ss=new SOSShape(sosArea.getApexes());
			SOSShape ss = sosArea;
			//		g.drawString(""+Math.abs((int)SOSGeometryTools.computeAreaUnsigned(ss.getApexes())), transform.xToScreen(ss.getCenterX()), transform.yToScreen(ss.getCenterY()));
			g.fill(new Ellipse2D.Float(transform.xToScreen(ss.getCenterX()), transform.yToScreen(ss.getCenterY()), 2.0f, 2.0f));
		}
		g.setColor(Color.red);
		//		NamayangarUtils.drawShape(entity.fireSearchBuilding().sensibleArea, g, transform);
		g.setColor(Color.blue);
		g.draw(NamayangarUtils.transformShape(sh, transform));
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
