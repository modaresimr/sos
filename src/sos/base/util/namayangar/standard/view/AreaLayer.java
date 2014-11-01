package sos.base.util.namayangar.standard.view;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.entities.Edge;
import sos.base.util.namayangar.misc.gui.ScreenTransform;

/**
 * A view layer that renders areas.
 * 
 * @param <E>
 *            The subclass of Area that this layer knows how to draw.
 */
public abstract class AreaLayer<E extends Area> extends StandardEntityViewLayer<E> {
	/**
	 * Construct an area view layer.
	 * 
	 * @param clazz
	 *            The subclass of Area this can render.
	 */
	protected AreaLayer(Class<E> clazz) {
		super(clazz);
	}

	@Override
	public Shape render(E area, Graphics2D g, ScreenTransform t) {
		List<Edge> edges = area.getEdges();
		if (edges.isEmpty()) {
			return null;
		}
		int count = edges.size();
		int[] xs = new int[count];
		int[] ys = new int[count];
		int i = 0;
		for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
			Edge e = it.next();
			xs[i] = t.xToScreen(e.getStartX());
			ys[i] = t.yToScreen(e.getStartY());
			++i;
		}
		Polygon shape = new Polygon(xs, ys, count);
		paintShape(area, shape, g);
		for (Edge edge : edges) {
			paintEdge(edge, g, t);
		}
		return shape;
	}

	/**
	 * Paint an individual edge.
	 * 
	 * @param e
	 *            The edge to paint.
	 * @param g
	 *            The graphics to paint on.
	 * @param t
	 *            The screen transform.
	 */
	protected void paintEdge(Edge e, Graphics2D g, ScreenTransform t) {
	}

	/**
	 * Paint the overall shape.
	 * 
	 * @param area
	 *            The area.
	 * @param p
	 *            The overall polygon.
	 * @param g
	 *            The graphics to paint on.
	 */
	protected void paintShape(E area, Polygon p, Graphics2D g) {
	}
	@Override
	public ArrayList<Pair<String,String>> sosInspect(E entity) {
		ArrayList<Pair<String, String>> list=super.sosInspect(entity);
		list.add(new Pair<String, String>("Area Index", ""+entity.getAreaIndex()));
		list.add(new Pair<String, String>("Neighbours", ""+entity.getNeighbours()));
		return list;
	}
}
