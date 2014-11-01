package sos.base.util.namayangar.sosLayer.fire;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.fire_v2.base.tools.BuildingBlock;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class RoadSiteViewer extends SOSAbstractToolsLayer<BuildingBlock> {

	public RoadSiteViewer() {
		super(BuildingBlock.class);
		setVisible(false);
	}
	@Override
	public int getZIndex() {
		return 12;
	}

	@Override
	protected void makeEntities() {
		if (model() instanceof FireWorldModel)
			setEntities(((FireWorldModel) model()).buildingBlocks());

	}

	@Override
	protected Shape render(BuildingBlock entity, Graphics2D g, ScreenTransform transform) {

		if (model() instanceof FireWorldModel) {
			int c = ((FireWorldModel) model()).buildingBlocks().indexOf(entity);
			g.setColor(new Color(Math.abs(c * 26) % 255, Math.abs(17 * c) % 255, Math.abs(34 * c) % 255));
			Shape shape;
			for (Building b : entity.buildings()) {
				shape = NamayangarUtils.transformShape(b, transform);
				g.fill(shape);
			}
		}
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
	public ArrayList<Pair<String, String>> sosInspect(BuildingBlock entity) {
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Fire;
	}
}
