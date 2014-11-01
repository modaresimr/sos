package sos.base.util.namayangar.standard.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.ArrayList;

import rescuecore2.misc.Pair;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.police_v2.PoliceConstants;

/**
 * A view layer that renders roads.
 */
public class RoadLayer extends AreaLayer<Road> {
	private static final Color ROAD_EDGE_COLOUR = Color.GRAY.darker();
	private static final Color ROAD_SHAPE_COLOUR = new Color(185, 185, 185);
	
	private static final Stroke WALL_STROKE = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	private static final Stroke ENTRANCE_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	
	/**
	 * Construct a road rendering layer.
	 */
	public RoadLayer() {
		super(Road.class);
	}
	
	@Override
	public String getName() {
		return "Roads";
	}
	
	@Override
	protected void paintShape(Road rd, Polygon shape, Graphics2D g) {
		g.setColor(ROAD_SHAPE_COLOUR);
		/*
		 * if(rd.getState()==RoadState.ALLOPEN)
		 * g.setColor(Color.WHITE);
		 * else if(rd.getState()==RoadState.FOGGY)
		 * g.setColor(Color.GRAY);
		 * else if(rd.getState()==RoadState.PARTLYOPEN)
		 * g.setColor(Color.green);
		 * else
		 * g.setColor(Color.BLACK);
		 */
		g.fill(shape);
	}
	
	@Override
	protected void paintEdge(Edge e, Graphics2D g, ScreenTransform t) {
		g.setColor(ROAD_EDGE_COLOUR);
		g.setStroke(e.isPassable() ? ENTRANCE_STROKE : WALL_STROKE);
		g.drawLine(t.xToScreen(e.getStartX()),
							t.yToScreen(e.getStartY()),
							t.xToScreen(e.getEndX()),
							t.yToScreen(e.getEndY()));
	}
	@Override
	public int getZIndex() {
		return 1;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Road entity) {
		ArrayList<Pair<String, String>> list = super.sosInspect(entity);
		list.add(new Pair<String, String>("Road Index", (int)entity.getRoadIndex()+""));
		list.add(new Pair<String, String>("ground area unsigned", (int)entity.getSOSGroundArea()+""));
		list.add(new Pair<String, String>("is intrance", entity.isEntrance()+""));
		list.add(new Pair<String, String>("isNeighbourWithBuilding", entity.isNeighbourWithBuilding()+""));
		list.add(new Pair<String, String>("is Too Small", (entity.getSOSGroundArea()<PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)+""));
		return list;}
}
