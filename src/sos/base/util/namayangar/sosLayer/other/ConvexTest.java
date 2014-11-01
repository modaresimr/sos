package sos.base.util.namayangar.sosLayer.other;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.sosFireZone.util.ConvexHull_arr_New;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.tools.LayerType;
import sos.police_v2.base.worldModel.PoliceWorldModel;

public class ConvexTest extends SOSAbstractToolsLayer<ConvexHull_arr_New> {

	public ConvexTest() {
		super(ConvexHull_arr_New.class);
	}
	@Override
	public int getZIndex() {
		return 100;
	}

	@Override
	protected void makeEntities() {
//		setEntities(((PoliceWorldModel)model()).convexes);
	}


	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return model() instanceof PoliceWorldModel;
	}

	@Override
	protected Shape render(ConvexHull_arr_New entity, Graphics2D g, ScreenTransform transform) {
		g.setColor(Color.white);
		g.draw(NamayangarUtils.transformShape(entity.getShape(), transform));
		return null;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(ConvexHull_arr_New entity) {
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Police;
	}
}
