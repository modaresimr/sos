package sos.base.util.namayangar.sosLayer.police;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

public class ClearTargetPoint extends SOSAbstractToolsLayer<Point> {
	PoliceForceAgent agent;
	public ClearTargetPoint() {
		super(Point.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE-1;
	}

	@Override
	protected void makeEntities() {
		setEntities(((PoliceForceAgent) (model().me().getAgent())).lastClearPoint);
	}

	@Override
	protected Shape render(Point entity, Graphics2D g, ScreenTransform transform) {
		agent=((PoliceForceAgent) (model().me().getAgent()));
		g.setColor(Color.cyan);
		NamayangarUtils.paintPoint2D(new Point2D(entity.getX(), entity.getY()), transform, g);
		g.setColor(Color.blue);
		NamayangarUtils.drawShape(PoliceUtils.getClearArea(agent.me(), entity.x, entity.y, agent.clearDistance,agent.clearWidth), g, transform);
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {

		return (model().me().getAgent() instanceof PoliceForceAgent);
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Point entity) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Police;
	}

}
