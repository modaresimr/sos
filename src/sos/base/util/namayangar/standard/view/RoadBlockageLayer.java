package sos.base.util.namayangar.standard.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;

import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.entities.Blockade;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

/**
 * A view layer that renders road blockages.
 */
public class RoadBlockageLayer extends StandardEntityViewLayer<Blockade> {
	// private static final int BLOCK_SIZE = 3;
	// private static final int BLOCK_STROKE_WIDTH = 2;

	private static final Color COLOUR = Color.black;

	/**
	 * Construct a road blockage rendering layer.
	 */
	public RoadBlockageLayer() {
		super(Blockade.class);
		setVisible(true);
	}

	@Override
	public String getName() {
		return "Blockades";
	}

	@Override
	public Shape render(Blockade b, Graphics2D g, ScreenTransform t) {
		int[] apexes = b.getApexes();
		int count = apexes.length / 2;
		int[] xs = new int[count];
		int[] ys = new int[count];
		for (int i = 0; i < count; ++i) {
			xs[i] = t.xToScreen(apexes[i * 2]);
			ys[i] = t.yToScreen(apexes[(i * 2) + 1]);
		}
		Polygon shape = new Polygon(xs, ys, count);
		g.draw(shape);
		g.setColor(COLOUR);
		if (!((SOSWorldModel) world).block.removedBlockades().contains(b)){
//			g.setColor(REMOVED_BLOCK_COLOUR);
//			g.draw(shape);

//		}else{
			g.fill(shape);
		}
		g.setColor(Color.white);
		int x1 = t.xToScreen(b.getCenteroid().getX());
		int y1 = t.yToScreen(b.getCenteroid().getY());
		g.drawArc(x1 - 1, y1 - 1, 2, 2, 0, 360);
		if (!((SOSWorldModel) world).block.removedBlockades().contains(b)&&
				!b.getPosition().getBlockadesID().contains(b.getID())){
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(3));
			g.draw(shape);
		}
		return shape;
	}

	@Override
	public int getZIndex() {
		return 3;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Blockade entity) {
		ArrayList<Pair<String, String>> list = super.sosInspect(entity);
		return list;
	}
}
