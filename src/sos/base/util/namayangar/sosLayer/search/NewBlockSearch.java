package sos.base.util.namayangar.sosLayer.search;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Human;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.search_v2.tools.cluster.ClusterData;

public class NewBlockSearch extends SOSAbstractToolsLayer<Human> {

	public NewBlockSearch() {
		super(Human.class);

		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 5;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().agents());
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
	protected Shape render(Human entity, Graphics2D g, ScreenTransform transform) {
		ClusterData cd=model().searchWorldModel.getClusterData(entity);
		if(cd==null)
			return null;
		int c = cd.getIndex() *3;
		g.setColor(new Color(Math.abs(c * 23) % 255, Math.abs(17 * c) % 255, Math.abs(37 * c) % 255));
		Shape shape = NamayangarUtils.transformShape(cd.getConvexShape(), transform);
		g.draw(shape);
		g.setFont(new Font("serif", Font.ITALIC, 25));
		g.setColor(Color.red);
		NamayangarUtils.drawString("Count=" + cd.getBuildings().size(), g, transform, cd.getNearestBuildingToCenter());
		
		return shape;
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
