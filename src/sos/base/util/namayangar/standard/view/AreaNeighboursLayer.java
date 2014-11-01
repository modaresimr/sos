package sos.base.util.namayangar.standard.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;

import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.entities.Edge;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

/**
 * A view layer that renders area neighbours.
 */
public class AreaNeighboursLayer extends StandardEntityViewLayer<Area> {
	
	private static final Color NEIGHBOUR_COLOUR = Color.blue;
	private static final Stroke NEIGHBOUR_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	
	/**
	 * Construct an area neighbours view layer.
	 */
	public AreaNeighboursLayer() {
		super(Area.class);
		setVisible(false);

	}
	
	@Override
	public String getName() {
		return "Neighbours";
	}
	
	@Override
	public Shape render(Area area, Graphics2D g, ScreenTransform t) {
		g.setColor(NEIGHBOUR_COLOUR);
		g.setStroke(NEIGHBOUR_STROKE);
		for (Edge edge : area.getEdges()) {
			EntityID neighbour = edge.getNeighbour();
			if (neighbour != null) {
				Area a = (Area) world.getEntity(neighbour);
				if (a != null) {
					g.drawLine(t.xToScreen(area.getX()), t.yToScreen(area.getY()), t.xToScreen(a.getX()), t.yToScreen(a.getY()));
				}
			}
		}
		return null;
	}

	@Override
	public int getZIndex() {
		return 3;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Area entity) {
		// TODO Auto-generated method stub
		return null;
	}
}
