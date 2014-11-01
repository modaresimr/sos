package sos.base.util.namayangar.sosLayer.selectedLayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import sos.base.entities.Area;
import sos.base.entities.StandardEntity;
import sos.base.move.Path;
import sos.base.move.types.DistanceMove;
import sos.base.move.types.MoveType;
import sos.base.move.types.PoliceMove;
import sos.base.move.types.PoliceReachablityMove;
import sos.base.move.types.SearchMove;
import sos.base.move.types.StandardMove;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.worldGraph.Node;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.base.worldModel.PoliceWorldModel;
import sos.tools.GraphEdge;

public class ShowMoveCost extends SOSAbstractSelectedComponent<StandardEntity> {

	public ShowMoveCost() {
		super(StandardEntity.class);
	}

	@Override
	protected void paint(StandardEntity selectedObj, Graphics2D g, ScreenTransform transform) {
		drawMoveCosts(selectedObj, g, transform);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	private void drawMoveCosts(StandardEntity selectedObject, Graphics2D g, ScreenTransform transform) {

		Area area = selectedObject.getAreaPosition();
		Class<? extends MoveType> moveClass = getMove(moveList.getSelectedItem()+"");

		long weight = model().sosAgent().move.getWeightTo(area, area.getX(), area.getY(), moveClass);
		drawMovePath(area, moveClass, g, transform);
		g.setColor(Color.orange);
		int x = transform.xToScreen(area.getX());
		int y = transform.yToScreen(area.getY());
		g.setFont(new Font("Tahoma", Font.BOLD, 12));
		g.drawString("" + weight, x, y);

	}

	private void drawMovePath(Area areaTo, Class<? extends MoveType> moveClass, Graphics2D g, ScreenTransform transform) {
		ArrayList<Area> a = new ArrayList<Area>();
		a.add(areaTo);
		Path path = model().sosAgent().move.getPathTo(a, moveClass);
		if (path != null && path.getEdges() != null)
			for (GraphEdge ge : path.getEdges()) {
				Line2D line = (Line2D) renderGraphEdge(ge, g, transform);
				if (ge instanceof WorldGraphEdge)
					switch (ge.getState()) {
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
				if (ge.haveTraffic()) {
					g.setColor(Color.PINK);
				}
				g.setStroke(new BasicStroke(2));
				g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
				g.setStroke(new BasicStroke(1));
			}
	}

	private Shape renderGraphEdge(GraphEdge e, Graphics2D g, ScreenTransform t) {
		Node start = model().nodes().get(e.getHeadIndex());
		Node end = model().nodes().get(e.getTailIndex());
		Shape line = new Line2D.Float(t.xToScreen(start.getPosition().getX()), t.yToScreen(start.getPosition().getY()), t.xToScreen(end.getPosition().getX()), t.yToScreen(end.getPosition().getY()));
		return line;
	}

	JComboBox moveList;
	private Vector<Class<? extends MoveType>> moveVector;

	@Override
	public JComponent getGui() {
		if (moveList == null) {
			moveVector = new Vector<Class<? extends MoveType>>();

			if (model() instanceof PoliceWorldModel) {
				moveVector.add(PoliceMove.class);
				moveVector.add(DistanceMove.class);
			} else {
				moveVector.add(StandardMove.class);
			}
			moveVector.add(PoliceReachablityMove.class);
			moveVector.add(SearchMove.class);

			Vector<String> s=new Vector<String>();
			for (Class<? extends MoveType> mv : moveVector) {
				s.add(mv.getSimpleName());
			}
			moveList = new JComboBox(s);
//			moveList.addActionListener(new ActionListener() {
//				
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					get
//				}
//			});
		}
		return moveList;
	}

	public Class<? extends MoveType> getMove(String name) {
		for (Class<? extends MoveType> class1 : moveVector) {
			if (class1.getSimpleName().equals(name))
				return class1;
		}
		return null;
	}
}
