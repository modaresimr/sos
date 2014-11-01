package sos.police_v2.state.intrupt;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.move.Path;
import sos.base.util.SOSActionException;
import sos.base.util.information_stacker.CycleInformations;
import sos.base.util.information_stacker.act.MoveAction;
import sos.base.util.information_stacker.act.StockMoveAction;
import sos.base.worldGraph.Node;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceConstants;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;
import sos.tools.GraphEdge;
import sos.tools.geometry.SOSGeometryTools;

public class HeavyStockedHandlerState extends PoliceAbstractIntruptState {

	public HeavyStockedHandlerState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	@Override
	public void precompute() {

	}

	@Override
	public void act() throws SOSActionException {
		log.info("acting as " + this);
		if (PoliceConstants.IS_NEW_CLEAR) {
			log.warn("HeavyStock with New Clear");
			isFinished = false;
			ArrayList<Blockade> stockdBlock = findStockBlockade2();
			Blockade best = chooseBestBlockade(stockdBlock);
			log.debug("bestblock" + best);
			if (best != null)
				clear(new Point(best.getX(), best.getY()));
			else
				isFinished = true;
		}
		log.warn("HeavyStock with old clear");
		clearNearestBlockade();
	
		log.warn("dige har kari kardam natounetam rad sham");
		//		Path path = agent.informationStacker.getLastMovePath();
		//		if(path.getIds().size()>0)
		//		move(model().getEntity(path.getIds().get(0)).getAreaPosition());

	}

	@Override
	public boolean isFinished() {
		if (isFinished)
			return isFinished;

		if (findStockBlockade2().isEmpty())
			return isFinished = true;
		return false;

	}

	public ArrayList<Blockade> findStockBlockade2() {

		ArrayList<Area> position = new ArrayList<Area>();
		Path2D geopath = findGeoPathInMove(position);
		log.debug("list of area in path = " + position);
		ArrayList<Blockade> list = new ArrayList<Blockade>();
		for (Area ar : position) {
			if (ar.isBlockadesDefined())
				for (Blockade blockade : ar.getBlockades()) {
					if (SOSGeometryTools.haveIntersection(blockade.getShape(), geopath)) {
						if (PoliceUtils.isValid(blockade))
							list.add(blockade);
					}
				}
		}

		//		/////////debug///////////////
		//		ArrayList<ShapeInfo> backs = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		//		for (Blockade b : model().blockades()) {
		//			backs.add(new ShapeDebugFrame.AWTShapeInfo(b.getShape(), b + "", Color.black, false));
		//		}
		//		debug.setBackground(backs);
		//		ArrayList<ShapeInfo> show = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		//		for (Area b : position) {
		//			show.add(new ShapeDebugFrame.AWTShapeInfo(b.getShape(), b + "", Color.red.darker(), true));
		//		}
		//		for (Blockade b : list) {
		//			show.add(new ShapeDebugFrame.AWTShapeInfo(b.getShape(), "target: " + b, Color.green, true));
		//		}
		//		show.add(new ShapeDebugFrame.AWTShapeInfo(geopath, "path", Color.white, false));
		//		debug.show("geopath", show);
		//		///////////////////////////////////////////////////////
		return list;

	}

	public Path2D findGeoPathInMove(ArrayList<Area> position) {

		Path2D geopath = new Path2D.Double();
		geopath.moveTo(agent.me().getX(), agent.me().getY());
		Path path = agent.informationStacker.getLastMovePath();

		GraphEdge[] allMoveEdges = path.getEdges();
		position.add(path.getSource().first());
		if (allMoveEdges == null) {
			geopath.lineTo(path.getDestination().second().getX(), path.getDestination().second().getY());
		} else {
			int index = 2;
			for (int i = 0; i < allMoveEdges.length; i++) {

				GraphEdge edge = allMoveEdges[i];
				short tail;
				short head;
				if (isInEdge(edge.getHeadIndex())) {
					head = edge.getHeadIndex();
					tail = edge.getTailIndex();
				} else {
					tail = edge.getHeadIndex();
					head = edge.getTailIndex();
				}
				Node start = model().nodes().get(head);
				Node end = model().nodes().get(tail);

				geopath.lineTo(start.getPosition().getX(), start.getPosition().getY());
				geopath.lineTo(end.getPosition().getX(), end.getPosition().getY());

				if (edge instanceof WorldGraphEdge) {
					Area area = model().areas().get(((WorldGraphEdge) edge).getInsideAreaIndex());
					if (area.getLastSenseTime() == agent.time())
						position.add(area);
					index--;
					if (index == 0)
						break;
					//					break;
				}
			}
			if (position.size() <= 2) {
				if (path.getDestination().first().getLastSenseTime() == agent.time())
					position.add(path.getDestination().first());
				geopath.lineTo(path.getDestination().second().getX(), path.getDestination().second().getY());
			}
		}
		return geopath;

	}

	private boolean isInEdge(short headIndex) {

		for (int i = 0; i < agent.me().getAreaPosition().getPassableEdges().length; i++) {
			Edge ed = agent.me().getAreaPosition().getPassableEdges()[i];
			if (headIndex == ed.getNodeIndex())
				return true;
		}
		return false;
	}

	@Override
	public boolean canMakeIntrupt() {
		boolean result = false;
		CycleInformations last = agent.informationStacker.getInformations(1);
		CycleInformations twoCycleAgo = agent.informationStacker.getInformations(2);
		if (last.getAct() instanceof MoveAction && !(last.getAct() instanceof StockMoveAction)) {
			if (twoCycleAgo.getAct() instanceof MoveAction && !(twoCycleAgo.getAct() instanceof StockMoveAction)) {
				log.info(this + "Checking if heavystock");
				int distanceLast = PoliceUtils.getDistance(agent.getPositionPair().second(), last.getPositionPair().second());
				int distanceTwoCycleAgo = PoliceUtils.getDistance(agent.getPositionPair().second(), twoCycleAgo.getPositionPair().second());

				log.trace("distanceToLastLocation:" + distanceLast + " distanceTo2CycleAgoLocation:" + distanceTwoCycleAgo);
				if (distanceLast < PoliceConstants.STOCK_DISTANCE && distanceTwoCycleAgo < PoliceConstants.STOCK_DISTANCE) {
					result = true;
				}
			} else if (twoCycleAgo.getAct() instanceof StockMoveAction) {
				log.info(this + "Checking if heavystock when twoCycleAgo.getAct() instanceof StockMoveAction");
				int distanceLast = PoliceUtils.getDistance(agent.getPositionPair().second(), last.getPositionPair().second());
				log.trace("distanceToLastLocation:" + distanceLast);
				if (distanceLast < PoliceConstants.STOCK_DISTANCE) {
					result = true;
				}
			}
		} else if (last.getAct() instanceof StockMoveAction) {
			log.info(this + "heavystock ! last.getAct() instanceof StockMoveAction");
			result = true;
		}
		log.trace(this + " can make intrupt?" + result);
		return result;
	}
}
