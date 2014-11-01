package sos.base.util.namayangar.sosLayer.search;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.search_v2.tools.cluster.ClusterData;

public class BlockSearch extends SOSAbstractToolsLayer<ClusterData> {

	public BlockSearch() {
		super(ClusterData.class);

		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 5;
	}

	@Override
	protected void makeEntities() {

		ArrayList<ClusterData> entities = new ArrayList<ClusterData>();

		for (ClusterData cd : model().sosAgent().newSearch.getSearchWorld().getAllClusters()) {

			entities.add(cd);
		}

		setEntities(entities);
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
	protected Shape render(ClusterData entity, Graphics2D g, ScreenTransform transform) {
		int c = entity.getIndex() + (int) (Math.random() * 100);
		g.setColor(new Color(Math.abs(c * 23) % 255, Math.abs(17 * c) % 255, Math.abs(37 * c) % 255));
		int area = 0;
		for (Building b : entity.getBuildings()) {
			Shape shape = NamayangarUtils.transformShape(b, transform);
			g.fill(shape);
			area++;
		}
		g.setFont(new Font("serif", Font.ITALIC, 25));
		g.setColor(Color.red);
		NamayangarUtils.drawString("Count=" + area, g, transform, entity.getNearestBuildingToCenter());
		return null;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(ClusterData entity) {
		return null;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}

}
