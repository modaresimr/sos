package sos.base.util.namayangar.sosLayer.search;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.search_v2.tools.cluster.ClusterData;

public class SearchCluster extends SOSAbstractToolsLayer<Human> {

	public SearchCluster() {
		super(Human.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 5;
	}

	@Override
	protected void makeEntities() {
		setEntities(new ArrayList<Human>(model().searchWorldModel.getMapClusterType().getClusterMap().keySet()));
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
	protected Shape render(Human hum, Graphics2D g, ScreenTransform transform) {
		int c = model().humans().indexOf(hum) + 1;
		if (hum == model().me())
			c = 0;
		g.setColor(new Color(Math.abs(c * 25) % 255, Math.abs(17 * c) % 255, Math.abs(34 * c) % 255));
		HashMap<ClusterData, Integer> assigns = countAssings();
		ClusterData cd = model().searchWorldModel.getMapClusterType().getClusterMap().get(hum);
		for (Building b : cd.getBuildings()) {
			Shape shape = NamayangarUtils.transformShape(b, transform);
			g.fill(shape);

		}
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 10, 20));
		NamayangarUtils.drawString("Assigns:" + assigns.get(cd), g, transform, (int) cd.getX(), (int) cd.getY());
		///////////////////////////////////
		int x1 = hum.getX();
		int y1 = hum.getY();
		int x2 = (int) cd.getX();
		int y2 = (int) cd.getY();
		
		NamayangarUtils.drawLine(x1, y1, x2, y2, g, transform);

		g.drawArc(transform.xToScreen(x2), transform.yToScreen(y2), 10, 10, 0, 360);
		
		return NamayangarUtils.transformLine(x1, y1, x2, y2, transform);
	}

	public HashMap<ClusterData, Integer> countAssings() {
		HashMap<ClusterData, Integer> map = new HashMap<ClusterData, Integer>();
		for (ClusterData cd : model().searchWorldModel.getMapClusterType().getClusterMap().values()) {
			Integer current = map.get(cd);
			if (current == null) {
				map.put(cd, 1);
			} else {
				map.remove(cd);
				map.put(cd, current + 1);
			}
		}
		return map;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Human entity) {
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}
}
