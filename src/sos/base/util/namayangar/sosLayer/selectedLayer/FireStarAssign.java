package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import sos.base.entities.FireBrigade;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.fire_v2.base.worldmodel.FireWorldModel;
import sos.search_v2.tools.cluster.ClusterData;

public class FireStarAssign extends SOSAbstractSelectedComponent<FireBrigade> {

	public FireStarAssign() {
		super(FireBrigade.class);
	}

	@Override
	protected void paint(FireBrigade fb, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.black);
		g.setFont(new Font("serif", Font.ITALIC, 35));
		g.setStroke(new BasicStroke(5));
		ClusterData cd = model().sosAgent().newSearch.getSearchWorld().getClusterData(fb);
		NamayangarUtils.drawLine(fb, cd.getBuildings().iterator().next(), g, transform);
	}

	@Override
	public boolean isValid() {
		return model() instanceof FireWorldModel;
	}

}
