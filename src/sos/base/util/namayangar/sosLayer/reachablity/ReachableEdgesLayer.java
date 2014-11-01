package sos.base.util.namayangar.sosLayer.reachablity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Road;
import sos.base.reachablity.tools.SOSArea;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class ReachableEdgesLayer extends SOSAbstractToolsLayer<SOSArea> {

	public int countOfReachableSet = 0;
	public ArrayList<Color> colors = new ArrayList<Color>();

	public ReachableEdgesLayer() {
		super(SOSArea.class);
		setVisible(false);
	}

	@Override
	protected Shape render(SOSArea r, Graphics2D g, ScreenTransform transform) {
		Shape shape = NamayangarUtils.transformShape(r.getShape(), transform);
		Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
		g.setColor(c);
		g.draw(shape);

		g.setColor(Color.red);
		if (r.getHoles() != null)
			for (SOSArea h : r.getHoles()) {
				NamayangarUtils.drawShape(h.getShape(), g, transform);
				g.draw(h.getShape());
			}

		return shape;
	}


	@Override
	public int getZIndex() {
		return 1;
	}

	@Override
	protected void makeEntities() {
		setEntities(new ArrayList<SOSArea>());
		if (model().sosAgent().model().sosAgent().getVisibleEntities(Road.class).isEmpty())
			return;
		for (Road r : model().sosAgent().getVisibleEntities(Road.class)) {
			getEntities().addAll(r.getReachableParts());
//			getEntities().addAll(r.getMergedBlockades());
		}

	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(SOSArea entity) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Reachablity;
	}
}
