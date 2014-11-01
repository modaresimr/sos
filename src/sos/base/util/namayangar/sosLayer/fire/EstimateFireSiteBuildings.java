package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.sosFireZone.SOSAbstractFireZone;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSRealFireZone;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class EstimateFireSiteBuildings extends SOSAbstractToolsLayer<SOSAbstractFireZone> {
	private Color[] colors = { Color.red, Color.blue, Color.black, Color.cyan, Color.green, Color.orange, Color.yellow };

	public EstimateFireSiteBuildings() {
		super(SOSAbstractFireZone.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 10;
	}

	@Override
	protected void makeEntities() {
		ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> x = (model()).getFireSites();
		ArrayList<SOSAbstractFireZone> sss = new ArrayList<SOSAbstractFireZone>();
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> paf : x)
			sss.add(paf.second());
		setEntities(sss);
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return model().me() instanceof Human;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(SOSAbstractFireZone entity) {
		return null;
	}

	@Override
	protected Shape render(SOSAbstractFireZone entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(colors[entity.getIndex() % 6]);
		for (Building b : (entity).getAllBuildings()) {
			Shape shape = NamayangarUtils.transformShape(b, transform);
			g.fill(shape);
		}
		g.setColor(Color.yellow);
		g.drawString("ID =  " + entity.getIndex() + " (isDisable=" + entity.isDisable() + "+isEx=" + entity.isExtinguishable() + "isEst=" + entity.isEstimating() + ") ", transform.xToScreen(entity.getCenterX()), transform.yToScreen(entity.getCenterY()));
		NamayangarUtils.drawShape(entity.getConvex().getShape(), g, transform);
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
