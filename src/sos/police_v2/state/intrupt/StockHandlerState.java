package sos.police_v2.state.intrupt;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import rescuecore2.worldmodel.EntityID;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.move.Path;
import sos.base.util.SOSActionException;
import sos.base.util.information_stacker.act.MoveAction;
import sos.base.util.information_stacker.act.StockMoveAction;
import sos.base.worldGraph.Node;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceConstants;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;
import sos.tools.GraphEdge;
import sos.tools.geometry.SOSGeometryTools;

public class StockHandlerState extends PoliceAbstractIntruptState {
	short index;
	boolean isReachable;
	Node[] allMove;
	Point me;
	Point p1;
	Point p2;
	Point select = null;

	public StockHandlerState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	@Override
	public void precompute() {
		isFinished = true;
	}

	public boolean checkIfStocked() {
		log.info(this + " Checking if stock");
		if (agent.time() < agent.FREEZE_TIME) {
			return false;
		}
		if (agent.informationStacker.getInformations(1).getAct() instanceof MoveAction && !(agent.informationStacker.getInformations(1).getAct() instanceof StockMoveAction)) {
			int lastLocationDistance = PoliceUtils.getDistance(agent.me().getPositionPair().second(), agent.informationStacker.getInformations(1).getPositionPair().second());
			log.info("lastLocationDistance:" + lastLocationDistance);
			if (lastLocationDistance < PoliceConstants.STOCK_DISTANCE) {
				//				log.warn("I'm Now Stocked!!!!!!");
				isFinished = false;
				return true;
			}
		}
		//		log.debug("I'm NOT stock");
		return false;
	}

	public ArrayList<Blockade> findStockedBlock() {
		Path path = agent.informationStacker.getLastMovePath();

		GraphEdge[] allMoveEdges = path.getEdges();
		log.info("making reachable to " + path);
		if (allMoveEdges == null) {
			log.info(" I am on the right position and the path is null " + path);
			return null;
		}
		Area blockArea = null;
		Area position = null;
		GraphEdge blockEdge = null;
		for (GraphEdge graphEdge : allMoveEdges) {
			if (graphEdge instanceof WorldGraphEdge) {
				position = model().areas().get(((WorldGraphEdge) graphEdge).getInsideAreaIndex());
			}
			if (position == null)
				position = getAreaOfAGraphEdge(graphEdge);
			if (position == null) {
				for (EntityID id : path.getIds()) {
					for (short myEdge : model().getEntity(id).getAreaPosition().getGraphEdges()) {
						if (myEdge == graphEdge.getIndex())
							position = (Area) model().getEntity(id);
						break;
					}
					if (position != null)
						break;
				}
			}

			if (position != null) {
				if (SOSGeometryTools.distance(agent.location(), position) < PoliceConstants.moveDistance) {
					if (isRealyBlock(graphEdge, position)) {
						blockArea = position;
						blockEdge = graphEdge;
						break;
					}
				}
			} else
				log.consoleDebug(" in graph edge virtual graph edge nis... bara hamin area position nulle ");
		}
		if (blockArea != null) {
			ArrayList<Blockade> myBlockades = new ArrayList<Blockade>();
			if (blockArea.isBlockadesDefined() && blockArea.getBlockades().size() > 0)
				for (Blockade blockade : blockArea.getBlockades()) {
					if (haveIntersect(blockEdge, blockade))
						myBlockades.add(blockade);
				}
			return myBlockades;
		} else
			log.consoleDebug("hich blockade E dar 200 metri peida nakarde");

		return null;
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

	public ArrayList<Blockade> findStockBlockade2() {

		ArrayList<Area> position = new ArrayList<Area>();
		Path2D geopath = findGeoPathInMove(position);

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

	private Area getAreaOfAGraphEdge(GraphEdge graphEdge) {
		Area position = null;
		for (Area area : agent.model().getObjectsInRange(agent.me(), 30000, Area.class)) {
			for (short myEdge : area.getGraphEdges()) {
				if (myEdge == graphEdge.getIndex())
					position = area;
				break;
			}
			if (position != null)
				break;
		}
		return position;
	}

	private boolean haveIntersect(GraphEdge graphEdge, Blockade blockade) {
		Path2D geoGraph = new Path2D.Double();
		geoGraph.moveTo(model().nodes().get(graphEdge.getHeadIndex()).getPosition().getX(), model().nodes().get(graphEdge.getHeadIndex()).getPosition().getY());
		geoGraph.lineTo(model().nodes().get(graphEdge.getTailIndex()).getPosition().getX(), model().nodes().get(graphEdge.getTailIndex()).getPosition().getY());
		geoGraph.closePath();
		if (SOSGeometryTools.haveIntersection(blockade.getShape(), geoGraph))
			return true;
		return false;
	}

	private boolean isRealyBlock(GraphEdge graphEdge, Area position) {
		Path2D geoGraph = new Path2D.Double();
		geoGraph.moveTo(model().nodes().get(graphEdge.getHeadIndex()).getPosition().getX(), model().nodes().get(graphEdge.getHeadIndex()).getPosition().getY());
		geoGraph.lineTo(model().nodes().get(graphEdge.getTailIndex()).getPosition().getX(), model().nodes().get(graphEdge.getTailIndex()).getPosition().getY());
		geoGraph.closePath();
		if (graphEdge.getState() == GraphEdgeState.Block)
			return true;
		if (graphEdge.getState() == GraphEdgeState.Open) {
			if (position.isBlockadesDefined() && position.getBlockades().size() > 0)
				for (Blockade blockade : position.getBlockades()) {
					if (SOSGeometryTools.haveIntersection(blockade.getShape(), geoGraph))
						return true;
				}
		}
		return false;
	}

	//	ShapeDebugFrame debug = new ShapeDebugFrame();

	@Override
	public void act() throws SOSActionException {
		log.warn("I'm Stock now!acting as stock...");
		isFinished = false;
		if (!PoliceConstants.IS_NEW_CLEAR) {
			ArrayList<Blockade> stockdBlock = findStockBlockade2();
			Blockade best = chooseBestBlockade(stockdBlock);
			log.debug("bestblock" + best);

			if (best != null)
				clear(new Point(best.getX(), best.getY()));
			else
				isFinished = true;
		} else {

			/*****************************
			 *** Edited By Hesam Akbary****
			 *****************************/
			Path path = agent.informationStacker.getLastMovePath();
			index = 0;
			isReachable = false;
			allMove = path.getNodes();
			select = null;
			Point result = agent.degreeClearableToPoint.nextPointToClear(path, false, true);
			log.debug("point baraye clear kardane =" + result);
			if (result != null)
				clear(result);
			else
				isFinished = true;
		}
	}

	@Override
	public boolean isFinished() {
		if (isFinished)
			return isFinished;

		if (findStockBlockade2().isEmpty())
			return isFinished = true;
		return false;

	}

	@Override
	public boolean canMakeIntrupt() {
		boolean result = checkIfStocked() | (!isFinished());
		log.trace(this + " can make intrupt?" + result);
		return result;

	}

}
