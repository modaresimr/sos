package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;

import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.search_v2.tools.cluster.ClusterData;

public class MyCluster extends SOSAbstractSelectedComponent<Human> {

	public MyCluster() {
		super(Human.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void paint(Human selectedObj, Graphics2D g, ScreenTransform transform) {
		if(selectedObj instanceof Civilian)
			return;
		ClusterData cd = model().searchWorldModel.getClusterData(selectedObj);

		g.setColor(Color.green);
		for (Building b : cd.getBuildings()) {
			NamayangarUtils.drawEntity(b, g, transform);
		}
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

}
