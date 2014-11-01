package sos.base.util.namayangar.standard.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import rescuecore2.config.Config;
import rescuecore2.misc.Pair;
import sos.base.entities.Human;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

/**
 * A view layer that renders position history.
 */
public class PositionHistoryLayer extends StandardEntityViewLayer<Human> {
	private static final Color PATH_COLOUR = Color.RED;
	
	/**
	 * Construct a position history layer.
	 */
	public PositionHistoryLayer() {
		super(Human.class);
	}
	
	@Override
	public void initialise(Config config) {
	}
	
	@Override
	public String getName() {
		return "Position history";
	}
	
	@Override
	public Shape render(Human h, Graphics2D g, ScreenTransform t) {
		if (!h.isPositionHistoryDefined()) {
			return null;
		}
		int[] history = h.getPositionHistory();
		// CHECKSTYLE:OFF:MagicNumber
		if (history.length < 4) {
			return null;
		}
		// CHECKSTYLE:ON:MagicNumber
		g.setColor(PATH_COLOUR);
		int x = t.xToScreen(history[0]);
		int y = t.yToScreen(history[1]);
		for (int i = 2; i < history.length; i += 2) {
			int x2 = t.xToScreen(history[i]);
			int y2 = t.yToScreen(history[i + 1]);
			g.drawLine(x, y, x2, y2);
			x = x2;
			y = y2;
		}
		return null;
	}
	@Override
	public int getZIndex() {
		return 6;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Human entity) {
		// TODO Auto-generated method stub
		return null;
	}
}
