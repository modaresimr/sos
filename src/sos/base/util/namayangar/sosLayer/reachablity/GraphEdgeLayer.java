package sos.base.util.namayangar.sosLayer.reachablity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.worldGraph.Node;
import sos.base.worldGraph.WorldGraphEdge;
import sos.tools.GraphEdge;

public class GraphEdgeLayer extends SOSAbstractToolsLayer<GraphEdge> {
	
	public GraphEdgeLayer() {
		super(GraphEdge.class);
		setVisible(false);
	}
	@Override
	protected Shape render(GraphEdge e, Graphics2D g, ScreenTransform t) {
		Node start = ((SOSWorldModel) world).nodes().get(e.getHeadIndex());
		Node end = ((SOSWorldModel) world).nodes().get(e.getTailIndex());
		Shape line = new Line2D.Float(t.xToScreen(start.getPosition().getX()), t.yToScreen(start.getPosition().getY()),
				t.xToScreen(end.getPosition().getX()), t.yToScreen(end.getPosition().getY()));
		paintShape(e, line, g);
		return line;
	}
	
	protected void paintShape(GraphEdge e, Shape shape, Graphics2D g) {
		Line2D line = (Line2D) shape;

		if (e instanceof WorldGraphEdge)
			switch (e.getState()) {
			case Block:
				g.setColor(Color.RED);
				break;
			case Open:
				g.setColor(Color.green);
				break;
			case FoggyOpen:
				g.setColor(Color.white);
				break;
			case FoggyBlock:
				g.setColor(Color.gray);
				break;
			default:
				return;
			}
		if (e.haveTraffic()) {
			g.setColor(Color.PINK);
		}

		g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
	}
	@Override
	public int getZIndex() {
		return 10;
	}
	@Override
	protected void makeEntities() {
		setEntities(new ArrayList<GraphEdge>(((SOSWorldModel) world).graphEdges()));		
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
	public ArrayList<Pair<String, String>> sosInspect(GraphEdge graphEdge) {
		ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
		list.add(new Pair<String, String>("Index", graphEdge.getIndex()+""));
		list.add(new Pair<String, String>("Type", "GraphEdge"));
		list.add(new Pair<String, String>("HeadIndex", graphEdge.getHeadIndex()+""));
		list.add(new Pair<String, String>("TailIndex", graphEdge.getTailIndex()+""));
		list.add(new Pair<String, String>("Lenght", graphEdge.getLenght()+""));
		list.add(new Pair<String, String>("State", graphEdge.getState()+""));
		
		list.add(new Pair<String, String>("HaveTraffic", graphEdge.haveTraffic()+""));
		return list;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Base;
	}
}
