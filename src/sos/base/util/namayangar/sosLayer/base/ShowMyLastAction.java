package sos.base.util.namayangar.sosLayer.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Human;
import sos.base.move.Path;
import sos.base.util.information_stacker.CycleInformations;
import sos.base.util.information_stacker.act.AbstractAction;
import sos.base.util.information_stacker.act.ClearAction;
import sos.base.util.information_stacker.act.ExtinguishAct;
import sos.base.util.information_stacker.act.LoadAction;
import sos.base.util.information_stacker.act.MoveAction;
import sos.base.util.information_stacker.act.RescueAction;
import sos.base.util.information_stacker.act.StockMoveAction;
import sos.base.util.information_stacker.act.UnloadAction;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.worldGraph.Node;

public class ShowMyLastAction extends SOSAbstractToolsLayer<Human> {

	public ShowMyLastAction() {
		super(Human.class);
	}

	@Override
	public int getZIndex() {
		// TODO Auto-generated method stub
		return 1000;
	}

	@Override
	protected void makeEntities() {
		setEntities((Human) model().me());
	}

	@Override
	protected Shape render(Human entity, Graphics2D g, ScreenTransform transform) {
		if (model().sosAgent().informationStacker.hasInformation(1))
			return renderAct(model().sosAgent().informationStacker.getInformations(1).getAct(), g, transform);

		return null;
	}

	private Shape renderAct(AbstractAction act, Graphics2D g, ScreenTransform t) {
		g.setStroke(new BasicStroke(2));
		Shape shape = null;
		if (act instanceof MoveAction) {
			shape = findGeoPathInMove(((MoveAction) act).getPath(), t);
			if (act instanceof StockMoveAction)
				g.setColor(Color.red);
			else
				g.setColor(Color.yellow);
		} else if (act instanceof ClearAction) {
			shape = NamayangarUtils.transformEntity(((ClearAction) act).getBlockade(), t);
			g.setColor(Color.magenta);
		}
		else if (act instanceof ExtinguishAct) {
			shape = NamayangarUtils.transformEntity(((ExtinguishAct) act).getTarget(), t);
			g.setColor(Color.blue);
		}
		else if (act instanceof RescueAction) {
			shape = NamayangarUtils.transformEntity(((RescueAction) act).getHuman(), t);
			g.setColor(Color.white);
		}
		else if (act instanceof LoadAction) {
			shape = NamayangarUtils.transformEntity(((LoadAction) act).getHuman(), t);
			g.setColor(Color.white);
		}
		else if (act instanceof UnloadAction) {
			shape = NamayangarUtils.transformEntity(model().me().getAreaPosition(), t);
			g.setColor(Color.white);
		} else {
			shape = NamayangarUtils.transformEntity(model().me().getAreaPosition(), t);
			g.setStroke(new BasicStroke(4));
			g.setColor(Color.red);
		}
		g.draw(shape);
		g.setStroke(new BasicStroke(1));
		return shape;
	}

	public Shape findGeoPathInMove(Path path, ScreenTransform t) {
		Path2D path2d = new Path2D.Double();

		Point p1 = getTransformedPoint(path.getSource().second().toGeomPoint(), t);
		path2d.moveTo(p1.x, p1.y);

		Node[] allMoveNodes = path.getNodes();

		if (allMoveNodes == null) {
			Point p2 = getTransformedPoint(path.getDestination().second().toGeomPoint(), t);
			path2d.lineTo(p2.x, p2.y);
		} else {
			for (int i = 0; i < allMoveNodes.length; i++) {

				Point p2 = getTransformedPoint(getPoint(allMoveNodes[i]), t);
				path2d.lineTo(p2.x, p2.y);
				p1 = p2;
				path2d.moveTo(p1.x, p1.y);
			}
			Point p2 = getTransformedPoint(path.getDestination().second().toGeomPoint(), t);
			path2d.lineTo(p2.x, p2.y);
		}
		return path2d;
	}

	private Point getTransformedPoint(Point point, ScreenTransform t) {
		return new Point(t.xToScreen(point.x), t.yToScreen(point.y));
	}

	public Point getPoint(Node node) {
		return node.getPosition().toGeomPoint();
	}

	//	public Shape findGeoPathInMove(Path path, ScreenTransform t) {
	//
	//		Path2D geopath = new Path2D.Double();
	//		geopath.moveTo(path.getSource().second().getX(),path.getSource().second().getY());
	//
	//		GraphEdge[] allMoveEdges = path.getEdges();
	//		//		position.add(path.getSource().first());
	//		if (allMoveEdges == null) {
	//			geopath.lineTo(path.getDestination().second().getX(), path.getDestination().second().getY());
	//		} else {
	//			Node lastNode=null;
	//			for (int i = 0; i < allMoveEdges.length; i++) {
	//				short tail;
	//				short head;
	//				//				if (isInEdge(edge.getInsideAreaIndex(),edge.getHeadIndex())) {
	//				head = allMoveEdges[i].getHeadIndex();
	//				tail = allMoveEdges[i].getTailIndex();
	//				//				} else {
	//				//					tail = edge.getHeadIndex();
	//				//					head = edge.getTailIndex();
	//				//				}
	//				Node start = model().nodes().get(head);
	//				Node end = model().nodes().get(tail);
	//				if(lastNode==end){
	//					end= start;
	//					start=lastNode;
	//				}
	//				lastNode=end;
	//				if (allMoveEdges[i] instanceof WorldGraphEdge) {
	//					geopath.moveTo(start.getPosition().getX(), start.getPosition().getY());
	//					geopath.lineTo(end.getPosition().getX(), end.getPosition().getY());
	//				}
	//			}
	//			geopath.lineTo(path.getDestination().second().getX(), path.getDestination().second().getY());
	//		}
	//		return NamayangarUtils.transformShape(geopath, t);
	//
	//	}

	//	private boolean isInEdge(short headIndex) {
	//
	//		for (int i = 0; i < model().me().getAreaPosition().getPassableEdges().length; i++) {
	//			Edge ed = model().me().getAreaPosition().getPassableEdges()[i];
	//			if (headIndex == ed.getNodeIndex())
	//				return true;
	//		}
	//		return false;
	//	}

	//	public Shape getGeoPath(Path movepath, ScreenTransform t) {
	//
	//		List<EntityID> path = movepath.getIds();
	//		Path2D.Double path2d = new Path2D.Double();
	//
	//		if (path.isEmpty()) {
	//			return null;
	//		}
	//		Iterator<EntityID> it = path.iterator();
	//		StandardEntity first = model().getEntity(it.next());
	//		Pair<Integer, Integer> firstLocation = first.getLocation();
	//		int startX = t.xToScreen(firstLocation.first());
	//		int startY = t.yToScreen(firstLocation.second());
	//		path2d.moveTo(startX, startY);
	//		while (it.hasNext()) {
	//			StandardEntity next = model().getEntity(it.next());
	//			if (!it.hasNext() && (movepath.getDestination().second().getIntX() >= 0 && movepath.getDestination().second().getIntY() >= 0))
	//				break;
	//			Pair<Integer, Integer> nextLocation = next.getLocation();
	//			int nextX = t.xToScreen(nextLocation.first());
	//			int nextY = t.yToScreen(nextLocation.second());
	//			path2d.lineTo(nextX, nextY);
	//			//			g.drawLine(startX, startY, nextX, nextY);
	//			// Draw an arrow partway along the length
	//			//			DrawingTools.drawArrowHeads(startX, startY, nextX, nextY, g);
	//			//			startX = nextX;
	//			//			startY = nextY;
	//		}
	//		if (movepath.getDestination().second().getIntX() >= 0 || movepath.getDestination().second().getIntY() >= 0) {
	//
	//			int nextX = t.xToScreen(movepath.getDestination().second().getIntX());
	//			int nextY = t.yToScreen(movepath.getDestination().second().getIntY());
	//			path2d.lineTo(nextX, nextY);
	//			//			g.drawLine(startX, startY, nextX, nextY);
	//			// Draw an arrow partway along the length
	//			//			DrawingTools.drawArrowHeads(startX, startY, nextX, nextY, g);
	//		}
	//
	//		return path2d;
	//
	//	}

	@Override
	public JComponent getGUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		return model().me() instanceof Human;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Human entity) {
		ArrayList<Pair<String, String>> p = new ArrayList<Pair<String, String>>();
		if (model().sosAgent().informationStacker.hasInformation(1)) {
			CycleInformations info = model().sosAgent().informationStacker.getInformations(1);
			p.add(new Pair<String, String>("Position", info.getPositionPair() + ""));
			p.add(new Pair<String, String>("Act", info.getAct() + ""));
			p.add(new Pair<String, String>("Time", info.time() + ""));
		}
		return p;
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.None;
	}
}
