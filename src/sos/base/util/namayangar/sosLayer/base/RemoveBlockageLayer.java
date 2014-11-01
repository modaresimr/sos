package sos.base.util.namayangar.sosLayer.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import rescuecore2.misc.collections.ArrayTools;
import rescuecore2.worldmodel.Property;
import sos.base.entities.Blockade;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.standard.view.StandardEntityViewLayer;
import sos.base.util.namayangar.tools.LayerType;

/**
 * A view layer that renders road blockages.
 */
public class RemoveBlockageLayer extends SOSAbstractToolsLayer<Blockade> {
	// private static final int BLOCK_SIZE = 3;
	// private static final int BLOCK_STROKE_WIDTH = 2;

	private static final Color REMOVED_BLOCK_COLOUR = Color.cyan;

	/**
	 * Construct a road blockage rendering layer.
	 */
	public RemoveBlockageLayer() {
		super(Blockade.class);
		setVisible(false);
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
		g.setColor(REMOVED_BLOCK_COLOUR);
			g.fill(shape);
		g.setColor(Color.white);
		int x1 = t.xToScreen(b.getCenteroid().getX());
		int y1 = t.yToScreen(b.getCenteroid().getY());
		g.drawArc(x1 - 1, y1 - 1, 2, 2, 0, 360);
		return shape;
	}

	@Override
	public int getZIndex() {
		return 2;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Blockade entity) {
		ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

		list.add(new Pair<String, String>("Id", entity.getID().toString()));
		String[] tmp = entity.getURN().split(":");
		String val = tmp[tmp.length - 1].substring(0, 1).toUpperCase() + tmp[tmp.length - 1].substring(1);
		list.add(new Pair<String, String>("Type", val));
		ArrayList<Property> props = new ArrayList<Property>();
		if (entity != null) {
			props.addAll(entity.getProperties());
			Collections.sort(props,StandardEntityViewLayer.PROPERTY_NAME_COMPARATOR);
		}
		for (Property prop : props) {
			tmp = prop.getURN().split(":");
			String nameTmp = tmp[tmp.length - 1].substring(0, 1).toUpperCase() + tmp[tmp.length - 1].substring(1);
			val = "";
			if (prop.isDefined()) {
				Object value = prop.getValue();
				val = value.toString();
				if (value.getClass().isArray()) {
					val = ArrayTools.convertArrayObjectToString(value);
				}

			} else {
				val = "Undefined";
			}
			list.add(new Pair<String, String>(nameTmp, val));
		}

		list.add(new Pair<String, String>("Update Time", entity.updatedtime()+""));
		return list;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().block.removedBlockades());
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
	public LayerType getLayerType() {
		return LayerType.Reachablity;
	}
}
