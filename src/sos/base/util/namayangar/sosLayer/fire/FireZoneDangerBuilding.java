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
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class FireZoneDangerBuilding extends SOSAbstractToolsLayer<SOSEstimatedFireZone> {

	public FireZoneDangerBuilding() {
		super(SOSEstimatedFireZone.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getZIndex() {
		// TODO Auto-generated method stub
		return 1100;
	}

	@Override
	protected void makeEntities() {
		ArrayList<SOSEstimatedFireZone> target = new ArrayList<SOSEstimatedFireZone>();
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> pair : model().getFireSites()) {
			target.add(pair.second());
		}
		setEntities(target);
	}

	@Override
	protected Shape render(SOSEstimatedFireZone entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(new Color(0, 100, 100));
		for (Building building : entity.getDangerBuildingForIgnit()) {
			if (building != null)
				NamayangarUtils.drawEntity(building, g, transform);
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
		return model() instanceof FireWorldModel;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(SOSEstimatedFireZone entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LayerType getLayerType() {
		// TODO Auto-generated method stub
		return LayerType.Fire;
	}

}
