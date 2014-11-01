package sos.base.util.namayangar.sosLayer.search;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.search_v2.tools.cluster.ClusterData;

public class NotSearchedForCivlian extends SOSAbstractToolsLayer<ClusterData> {

	public NotSearchedForCivlian() {
		super(ClusterData.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 20;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().sosAgent().newSearch.getSearchWorld().getAllClusters());
	}

	@Override
	protected Shape render(ClusterData cluster, Graphics2D g, ScreenTransform transform) {
		int c = cluster.getIndex() * 3 + 1;
		if (cluster.equals(model().sosAgent().getMyClusterData()))
			c = 0;

		g.setColor(new Color(Math.abs(c * 23) % 255, Math.abs(17 * c) % 255, Math.abs(37 * c) % 255));

		Shape shape;
		if (cluster.isCoverer()) {
			shape = NamayangarUtils.transformShape(cluster.getConvexShape(), transform);
			g.draw(shape);
		} else {
			Area area = new Area();
			for (Building building : cluster.getBuildings())
				if (!building.isSearchedForCivilian()) {
					shape = NamayangarUtils.transformEntity(building, transform);
					area.add(new Area(shape));
					g.fill(shape);
				}
			shape = area;
		}

		return shape;
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
	public LayerType getLayerType() {
		return LayerType.Search;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(ClusterData entity) {
		return null;
	}

}
