package sos.base.util.namayangar.sosLayer.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.base.clearablePointToReachable.GeoClearPointReachablity;
import sos.police_v2.base.worldModel.PoliceWorldModel;

public class MoveAreaWithLastClearTarget extends SOSAbstractToolsLayer<Point> {

	public MoveAreaWithLastClearTarget() {
		super(Point.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 1100;
	}

	@Override
	protected void makeEntities() {
		setEntities(((PoliceForceAgent) (model().me().getAgent())).lastClearPoint);
	}

	@Override
	protected Shape render(Point entity, Graphics2D g, ScreenTransform transform) {
		if (entity != null) {
			Point me = model().me().getPositionPoint().toGeomPoint();
			Area moveArea = GeoClearPointReachablity.getMoveWayArea(me, entity);
			g.setColor(new Color(100, 100, 0, 100));
			NamayangarUtils.fillShape(moveArea, g, transform);
			g.setColor(Color.cyan);
			NamayangarUtils.paintPoint2D(entity.getX(), entity.getY(), transform, g);
		}
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {

		return model() instanceof PoliceWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Point entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LayerType getLayerType() {
		// TODO Auto-generated method stub
		return LayerType.Police;
	}

}
