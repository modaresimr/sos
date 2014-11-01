package sos.police_v2.base.clearablePointToReachable;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;
import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Blockade;
import sos.base.entities.Road;
import sos.base.move.Path;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame;
import sos.base.worldGraph.Node;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

public class GeoDegreeClearPointToReachable extends ClearablePointToReachable {

	private Node[] allMoveNodes;
	private Point me;
	private Path path;
	private Point target = null;
	private float oneDegreeInRadian = 0.017452778f;//(3.1415f) / 180;
//	static ShapeDebugFrame debug = new ShapeDebugFrame();
	ArrayList<ShapeDebugFrame.ShapeInfo> shapes;
	short selectedNodeIndex = 0;
	Point lastNode;

	public GeoDegreeClearPointToReachable(PoliceForceAgent forceAgent) {
		super(forceAgent);
//		debug.setBackgroundEntities(Color.gray, agent.model().areas(), agent.model().blockades());
	}

	@Override
	public Point nextPointToClear(Path path, boolean checkReachablity, boolean doClear) {
		this.path = path;
		lastNode = null;
		selectedNodeIndex = 0;
		allMoveNodes = path.getNodes();
		me = agent.me().getPositionPoint().toGeomPoint();
		shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(me.x, me.y), "Agent", Color.blue, true));
		selectTarget();
		if (isReachableToSelectedNode()) {
			logger.debug("reachable bood pass move bayad bezanim");
			//			debug.show(agent.me() + "", shapes);
			return null;
		}
		//		debug.show(agent.me() + "", shapes);
		logger.debug("before adding to rang=" + target);
		if (Point.distance(me.getX(), me.getY(), target.getX(), target.getY()) < agent.clearDistance - 1000)
			target = changeTargetRang(1000, target, me);
		logger.debug("after adding to rang =" + target);
		if (isAnyBlockadeInClearArea()) {
			logger.debug("bloackade to clear area bood pas clear act mifreste target=" + target);
			return target;
		}
		logger.debug("bloackade to clear area nabod pas null retrun mikonim ta move kone");
		return null;
	}

	private boolean isReachableToSelectedNode() {
		if (lastNode == null)
			return false;
		if (allMoveNodes == null)
			return false;
		shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(lastNode.x, lastNode.y), "LastNode", Color.BLACK, false));
		if (agent.me().getAreaPosition() instanceof Road) {
			/*
			 * Edge edge = allMoveNodes[0].getRelatedEdge();
			 * Road baseRoad = (Road) agent.me().getAreaPosition();
			 * if (Reachablity.isReachable(baseRoad, new Point2D(me.x, me.y), edge) == ReachablityState.Close) {
			 * shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, getPoint(allMoveNodes[0])), " NOT REACHABLE", new Color(100, 0, 0, 150), false, true));
			 * logger.info(me + " is reachable to " + getPoint(allMoveNodes[0]));
			 * return false;
			 * }
			 */
			ArrayList<Blockade> blockades = new ArrayList<Blockade>();
			setBlockadeInWayArea(me, getPoint(allMoveNodes[0]), blockades, path);
			if (blockades.size() > 0) {
				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, getPoint(allMoveNodes[0])), " NOT REACHABLE", new Color(100, 0, 0, 150), false, true));
				logger.info(me + " is reachable to " + getPoint(allMoveNodes[0]));
				return false;
			}
			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, getPoint(allMoveNodes[0])), " REACHABLE", new Color(0, 100, 100, 150), false, true));
			logger.info(me + " is NOT reachable to " + getPoint(allMoveNodes[0]));
		}
		for (int i = 1; i < allMoveNodes.length; i++) {
			Point node = getPoint(allMoveNodes[i]);
			Point last = getPoint(allMoveNodes[i - 1]);
			if (last.x == node.x && last.y == node.y)
				continue;
			ArrayList<Blockade> blockades = new ArrayList<Blockade>();
			setBlockadeInWayArea(last, node, blockades, path);
			if (blockades.size() > 0) {
				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(getPoint(allMoveNodes[i - 1]), getPoint(allMoveNodes[i])), " NOT REACHABLE", new Color(100, 0, 0, 150), false, true));
				logger.info(getPoint(allMoveNodes[i - 1]) + " is reachable to " + getPoint(allMoveNodes[i]));
				return false;
			}
			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(getPoint(allMoveNodes[i - 1]), getPoint(allMoveNodes[i])), "REACHABLE", new Color(0, 100, 100, 150), false, true));
			logger.info(getPoint(allMoveNodes[i - 1]) + " is NOT reachable to " + getPoint(allMoveNodes[i]));
			/*
			 * Edge endEdge = allMoveNodes[i].getRelatedEdge();
			 * Edge startEdge = allMoveNodes[i - 1].getRelatedEdge();
			 * sos.base.entities.Area area = agent.model().areas().get(endEdge.getMyAreaIndex());
			 * if (!(area instanceof Road))
			 * continue;
			 * Road baseRoad = (Road) area;
			 * if (Reachablity.isReachable(baseRoad, startEdge, endEdge) == ReachablityState.Close) {
			 * shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(getPoint(allMoveNodes[i - 1]), getPoint(allMoveNodes[i])), " NOT REACHABLE", new Color(100, 0, 0, 150), false, true));
			 * logger.info(getPoint(allMoveNodes[i - 1]) + " is reachable to " + getPoint(allMoveNodes[i]));
			 * return false;
			 * }
			 */
			if (node.x == lastNode.x && node.y == lastNode.y) {
				logger.debug(" got to last node that selected");
				break;
			}

		}

		return true;
	}

	private boolean isAnyBlockadeInClearArea() {
		ArrayList<Blockade> blockList = new ArrayList<Blockade>(agent.model().getBlockadesInRange(me.x, me.y, agent.clearDistance));
		if (isBlockadeInClearArea(blockList, agent.me(), target, agent.clearDistance, agent.clearWidth))
			return true;
		return false;
	}

	private void selectTarget() {
		ArrayList<Point> mustInAreaNodes = new ArrayList<Point>();
		if (allMoveNodes == null) {
			target = path.getDestination().second().toGeomPoint();
			if (!isInClearRang(me, target, agent.clearDistance))
				target = getInRangPoint(me, target, agent.clearDistance);
		} else {
			Point firstNode = getPoint(allMoveNodes[0]);
			if (!isInClearRang(me, firstNode, agent.clearDistance)) {
				logger.debug("first node to range nabod pass noghtei azash ke to rang bood ro target kardam");
				target = getInRangPoint(me, firstNode, agent.clearDistance);
				shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(target.x, target.y), "TARGET (first node out of rang)", Color.red, true));
				return;
			}
			logger.debug("first node to rang bood ");
			mustInAreaNodes.add(firstNode);
			target = getInRangPoint(me, firstNode, agent.clearDistance);
			for (int i = 1; i < allMoveNodes.length; i++) {
				logger.debug("---> check kardanne node e " + i + " om");
				Point nextNode = getPoint(allMoveNodes[i]);
				lastNode = nextNode;
				shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(nextNode.x, nextNode.y), "MUST" + i, Color.yellow, true));
				Point nextNodeInRange = getInRangPoint(me, nextNode, agent.clearDistance, 1000);
				//				shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(nextNodeInRange.x, nextNodeInRange.y), "MYSTinrange" + i, Color.orange, true));
				Point lastTarget = null;
				Point tempTarget = new Point(target);
				float baseDegree = (float) Math.atan2(target.getY() - me.getY(), target.getX() - me.getX());
				logger.info("base degree=" + baseDegree);
				float nextNodeDegree = (float) Math.atan2(nextNode.getY() - me.getY(), nextNode.getX() - me.getX());
				logger.info("next node degree=" + nextNodeDegree);
				float deltaDegree = nextNodeDegree - baseDegree;
				if (deltaDegree > Math.PI) {
					deltaDegree -= (Math.PI) * 2;
				} else if (deltaDegree < (Math.PI) * -1) {
					deltaDegree += (Math.PI) * 2;
				}
				//				System.err.println((Math.abs(deltaDegree) / oneDegreeInRadian) + "");
				FOR: for (int index = 0; index <= (Math.abs(deltaDegree) / oneDegreeInRadian) + 1; index++) {
					Area clearArea = PoliceUtils.getClearArea(agent.me(), tempTarget.x, tempTarget.y, agent.clearDistance, agent.clearWidth);
					for (Point checkPoint : mustInAreaNodes) {
						if (!clearArea.contains(checkPoint)) {
							logger.debug("target entekhab shod chon az in be bad noghato poshesh nemide noghteye entekhab shodeye koli target = " + target);
							//							for (Point point : mustInAreaNodes) {
							//								shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(point.x, point.y), "must in area", Color.yellow, true));
							//							}
							shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(target.x, target.y), "TARGET", Color.RED, true));
							if (lastTarget != null) {
								target = lastTarget;
								mustInAreaNodes.add(nextNode);
								break FOR;
							}

							return;
						}
						logger.debug("======== " + checkPoint + " to clear area bood");
					}
					logger.info("check bara next node >>>>> tempTarget= " + tempTarget + " nextNodeInRange= " + nextNodeInRange);
					if (clearArea.contains(nextNodeInRange)) {
						logger.debug("next Node toye clear area bood pass node badi ro check mikonim");
						lastTarget = tempTarget;
					}
					//					shapes.add(new ShapeDebugFrame.Point2DShapeInfo(new Point2D(target.x, target.y), "TARGET movaghat " + i, Color.green, true));
					target = tempTarget;
					tempTarget = getNextTempTarget(tempTarget, deltaDegree);
				}
				mustInAreaNodes.add(nextNode);
			}
		}
		selectedNodeIndex = (short) mustInAreaNodes.size();
	}

	private Point getNextTempTarget(Point tempTarget, float deltaDegree) {
		float baseDegree = (float) Math.atan2(tempTarget.getY() - me.getY(), tempTarget.getX() - me.getX());
		logger.debug("now Degree = " + baseDegree);
		if (deltaDegree > 0) {
			baseDegree += oneDegreeInRadian;
			Point result = new Point((int) (me.x + agent.clearDistance * Math.cos(baseDegree)), (int) (me.y + agent.clearDistance * Math.sin(baseDegree)));
			return result;
		} else if (deltaDegree < 0) {
			baseDegree -= oneDegreeInRadian;
			Point result = new Point((int) (me.x + agent.clearDistance * Math.cos(baseDegree)), (int) (me.y + agent.clearDistance * Math.sin(baseDegree)));
			return result;
		}
		return tempTarget;
	}
}
