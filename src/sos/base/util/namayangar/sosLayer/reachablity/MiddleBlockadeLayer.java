package sos.base.util.namayangar.sosLayer.reachablity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.entities.Road;
import sos.base.util.blockadeEstimator.SOSBlockade;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;

public class MiddleBlockadeLayer extends SOSAbstractToolsLayer<SOSBlockade> {
	
	
	
	public MiddleBlockadeLayer() {
		super(SOSBlockade.class);
		setVisible(false);
	}

	@Override
	protected Shape render(SOSBlockade b, Graphics2D g, ScreenTransform t) {
		int[] apexes = b.getApexes();
		int count = apexes.length / 2;
		int[] xs = new int[count];
		int[] ys = new int[count];
		for (int i = 0; i < count; ++i) {
			xs[i] = t.xToScreen(apexes[i * 2]);
			ys[i] = t.yToScreen(apexes[(i * 2) + 1]);
		}

		Polygon shape = new Polygon(xs, ys, count);
		paintShape(b, shape, g);
//		g.setColor(Color.white);
//		int x1 = t.xToScreen(b.getCenteroid().getX());
//		int y1 = t.yToScreen(b.getCenteroid().getY());
//		g.drawArc(x1 - 1, y1 - 1, 2, 2, 0, 360);
		return shape;
	}
	
	protected void paintShape(SOSBlockade b, Shape shape, Graphics2D g) {
			g.setColor(Color.darkGray);
		
		if (b.getRepairCost() > 0) {
			g.fill(shape);
			g.setColor(Color.black);
			g.draw(shape);
		}

	}

	@Override
	public int getZIndex() {
		return 2;
	}

	@Override
	protected void makeEntities() {
		setEntities(new ArrayList<SOSBlockade>());
		// for (Entry<Building, ArrayList<SOSBlockade>> b : ((SOSWorldModel) world).block.middleBlockades().entrySet()) {
		// entities.addAll(b.getValue());
		// }
		for (Road r : ((SOSWorldModel) world).roads()) {
			if(r.getMiddleBlockades()!=null)
			for (SOSBlockade b : r.getMiddleBlockades()) {
				getEntities().add(b);
			}
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
	public ArrayList<Pair<String, String>> sosInspect(SOSBlockade entity) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Base;
	}
}
