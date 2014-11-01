package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSRealFireZone;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class EstimateFireSiteOuter extends SOSAbstractToolsLayer<SOSEstimatedFireZone> {
	private Color[] colors = { Color.red, Color.blue, Color.yellow, Color.green, Color.orange };

	public EstimateFireSiteOuter() {
		super(SOSEstimatedFireZone.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 11;
	}

	@Override
	protected void makeEntities() {
		ArrayList<SOSEstimatedFireZone> arr = new ArrayList<SOSEstimatedFireZone>();
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> x : ((model().me().getAgent())).fireSiteManager.getFireSites())
			arr.add(x.second());
		setEntities(arr);
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
	public ArrayList<Pair<String, String>> sosInspect(SOSEstimatedFireZone entity) {
		return null;
	}

	@Override
	protected Shape render(SOSEstimatedFireZone entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(new Color((entity.getIndex() * 20) % 256, (entity.getIndex() * 30) % 256, (entity.getIndex() * 40) % 256));
		for (Building b : entity.getOuter())
		{
			Shape shape = NamayangarUtils.transformShape(b, transform);
			g.fill(shape);
		}
		g.setColor(Color.yellow);
		g.drawString("ID =  " + entity.getIndex() + " (isDisable=" + entity.isDisable() + "+isEx=" + entity.isExtinguishable() + ") ", transform.xToScreen(entity.getCenterX()), transform.yToScreen(entity.getCenterY()));

		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
