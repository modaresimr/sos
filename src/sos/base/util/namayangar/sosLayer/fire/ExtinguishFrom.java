package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.SOSGeometryTools;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.geom.RegularPolygon;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.base.tools.ExtinguishableArea;
import sos.fire_v2.base.worldmodel.FireWorldModel;
import sos.police_v2.PoliceConstants;

public class ExtinguishFrom extends SOSAbstractToolsLayer<Building> implements SelectedObjectListener {

	public ExtinguishFrom() {
		super(Building.class);
		setVisible(false);
	}

	ArrayList<Building> bldgs = new ArrayList<Building>();

	@Override
	public int getZIndex() {
		// TODO Auto-generated method stub
		return 110;
	}

	@Override
	public void objectSelected(SOSSelectedObj sso) {
		if (sso == null) {
			bldgs.clear();

		} else {
			bldgs.clear();
			if (sso.getObject() instanceof Building) {
				bldgs.add((Building) sso.getObject());
			}
		}
	}

	@Override
	protected void makeEntities() {
		setEntities(bldgs);
	}

	@Override
	protected Shape render(Building building, Graphics2D g, ScreenTransform transform) {
		int x = transform.xToScreen(building.getX());
		int y = transform.yToScreen(building.getY());

		int radius = AbstractFireBrigadeAgent.maxDistance;
		double agentX = x;
		double agentY = y;
		double ellipseX1 = agentX - radius;
		double ellipseY1 = agentY + radius;
		double ellipseX2 = agentX + radius;
		double ellipseY2 = agentY - radius;

		int x1 = transform.xToScreen(ellipseX1);
		int y1 = transform.yToScreen(ellipseY1);
		int x2 = transform.xToScreen(ellipseX2);
		int y2 = transform.yToScreen(ellipseY2);
		int ellipseWidth = x2 - x1;
		int ellipseHeight = y2 - y1;

		Shape shape = new Ellipse2D.Double(x - ellipseWidth / 2, y - ellipseHeight / 2, ellipseWidth, ellipseHeight);
		//		paintShape(entity, shape, g);
		g.setColor(Color.red);
		g.draw(shape);
		///////////////////////////////////////////////////
//		long t1=System.nanoTime();
		RegularPolygon polygon = new RegularPolygon(building.getX(), building.getY(), FireBrigadeAgent.maxDistance, ExtinguishableArea.NUMBER_OF_VERTICES);
		Area polygonArea = new Area(polygon);
		ArrayList<ShapeInArea> extisense=new ArrayList<ShapeInArea>();
		for (ShapeInArea shapeInArea : building.fireSearchBuilding().sensibleAreasOfAreas()) {
			if (polygon.contains(shapeInArea.getApexes())) {
				extisense.add(shapeInArea);
			} else {
				Area area = new Area(shapeInArea);
				area.intersect(polygonArea);
				int[] apex = AliGeometryTools.getApexes(area);
				if (apex.length >= 6 && SOSGeometryTools.computeArea(apex) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM * 2)
					extisense.add(new ShapeInArea(apex, shapeInArea.getArea(model())));

			}
		}
//		System.out.println((System.nanoTime()-t1));
		//a1.intersect(rhs)
		g.setColor(Color.black);
		for (ShapeInArea shapeInArea : extisense) {
			Shape sh=NamayangarUtils.transformShape(shapeInArea, transform);
			g.fill(sh);
		}
		//////////////////////////////////

		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return model() instanceof FireWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		return null;
	}

	@Override
	public void preCompute() {
		getViewer().getViewerFrame().addSelectedObjectListener(this);
		super.preCompute();
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}