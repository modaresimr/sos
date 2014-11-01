package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;
import sos.fire_v2.base.worldmodel.FireWorldModel;
import sos.fire_v2.target.Tools;

public class FireZoneSpread extends SOSAbstractToolsLayer<SOSEstimatedFireZone> implements SelectedObjectListener {

	public FireZoneSpread() {
		super(SOSEstimatedFireZone.class);
		setVisible(false);
	}

	ArrayList<Building> bldgs = new ArrayList<Building>();

	@Override
	public int getZIndex() {
		return 20;
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
		try {
			if (bldgs.size() > 0) {
				SOSEstimatedFireZone es = bldgs.get(0).getSOSEstimateFireSite();
				if (es != null)
					setEntities(es);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected Shape render(SOSEstimatedFireZone site, Graphics2D g, ScreenTransform transform) {

		int SPREAD_ANGLE;

		if (((FireWorldModel) model()).getInnerOfMap().contains(site.getCenterX(), site.getCenterY()))
			SPREAD_ANGLE = 90;
		else
			SPREAD_ANGLE = 60;

		double x1, y1;
		Pair<Double, Double> spread = site.spread;
		x1 = spread.first();
		y1 = spread.second();

		double length = Math.sqrt(x1 * x1 + y1 * y1);

		for (Building b : model().buildings()) {
			double a3 = Tools.getAngleBetweenTwoVector(x1, y1, b.getX() - site.getCenterX(), b.getY() - site.getCenterY());
			int c = (int) (Math.abs(a3) / 30d) ;
				g.setColor(Color.black);
			NamayangarUtils.drawString("X="+c, g, transform, b.getX(),b.getY());
//			if (a3 > 2 * SPREAD_ANGLE && Tools.isBigFire(site)) {
//				g.setColor(Color.red);
//				NamayangarUtils.fillEntity(b, g, transform);
//			}
//			else {
//				g.setColor(new Color(Math.abs(c * 25) % 255, Math.abs(17 * c) % 255, Math.abs(34 * c) % 255));
//				NamayangarUtils.fillEntity(b, g, transform);
//			}
		}
		NamayangarUtils.drawString("Length=" + length, g, transform, site.getCenterX(), site.getCenterY());

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
	public ArrayList<Pair<String, String>> sosInspect(SOSEstimatedFireZone entity) {
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