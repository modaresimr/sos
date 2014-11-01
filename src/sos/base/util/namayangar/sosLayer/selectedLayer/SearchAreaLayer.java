package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.search_v2.tools.SOSAreaTools;

public class SearchAreaLayer extends SOSAbstractSelectedComponent<Building> {

	public SearchAreaLayer() {
		super(Building.class);
		setVisible(true);
	}
	@Override
	protected void paint(Building selectedObj, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.green);
		for (ShapeInArea area : selectedObj.getSearchAreas()) {
			g.draw(NamayangarUtils.transformShape(area, transform));
		}
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.red);
		Point2D center = new Point2D(selectedObj.getX(), selectedObj.getY());
		Pair<Point2D, Point2D> points = null;
//		ArrayList<Line2D> entrancesEdge = selectedObj.entrances();
		Edge[] entrancesEdge = selectedObj.getPassableEdges();
		for (int i = 0; i < entrancesEdge.length; i++) {
			Line2D edgeLine = entrancesEdge[i].getLine();
			NamayangarUtils.drawLine(edgeLine.getOrigin().getIntX(), edgeLine.getOrigin().getIntY(),edgeLine.getEndPoint().getIntX(),edgeLine.getEndPoint().getIntY(), g, transform);
			// _______________________________________
			points = SOSAreaTools.get2PointsOnParallelLine(edgeLine, center);
			// _______________________________________
			NamayangarUtils.paintPoint2D(points.first(), transform, g);
			if(!selectedObj.getShape().contains(points.first().toGeomPoint()))
				System.out.println("??????1");
			NamayangarUtils.paintPoint2D(points.second(), transform, g);
			if(!selectedObj.getShape().contains(points.second().toGeomPoint()))
				System.out.println("??????2");
			
		}

	}

	@Override
	public boolean isValid() {
		return true;
	}

}
