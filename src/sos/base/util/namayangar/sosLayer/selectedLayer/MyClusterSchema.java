package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.Color;
import java.awt.Graphics2D;

import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.search_v2.tools.cluster.ClusterData;

public class MyClusterSchema extends SOSAbstractSelectedComponent<Human> {

	public MyClusterSchema() {
		super(Human.class);
		setVisible(true);
	}

	@Override
	protected void paint(Human selectedObj, Graphics2D g, ScreenTransform transform) {
		ClusterData cd = model().searchWorldModel.getClusterData(selectedObj);
		if(cd==null)
			return;

		g.setColor(Color.green);
		NamayangarUtils.drawShape(cd.getConvexShape(), g, transform);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

}
