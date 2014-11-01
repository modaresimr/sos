package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSRealFireZone;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class CustomConvexOfFireZone extends SOSAbstractToolsLayer<SOSEstimatedFireZone> {
	private Color[] colors = { Color.red, Color.blue, Color.yellow, Color.green, Color.orange };

	public CustomConvexOfFireZone() {
		super(SOSEstimatedFireZone.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 16;
	}

	@Override
	protected void makeEntities() {
		ArrayList<SOSEstimatedFireZone> zones = new ArrayList<SOSEstimatedFireZone>();
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> temp : ((model().me().getAgent())).fireSiteManager.getFireSites())
			zones.add(temp.second());
		setEntities(zones);
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	protected Shape render(SOSEstimatedFireZone entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.cyan);
		Shape shape = NamayangarUtils.transformShape(entity.getConvex().getScaleConvex(1.3f).getShape(), transform);
		g.draw(shape);
		g.setColor(Color.pink);
		shape = NamayangarUtils.transformShape(entity.getConvex().getScaleConvex(0.9f).getShape(), transform);
		g.draw(shape);
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Base;
	}
	
	@Override
	public ArrayList<Pair<String, String>> sosInspect(SOSEstimatedFireZone entity) {
		// TODO Auto-generated method stub
		return null;
	}
}
