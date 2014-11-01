package sos.police_v2.clearableBlockadeToReachable;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.move.Path;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.sosFireZone.util.Utill;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame.ShapeInfo;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.base.worldGraph.Node;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

public class GeoPathReachablity extends ClearableBlockadeToReachable {
	SOSLoggerSystem logger;

	public GeoPathReachablity(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
		logger = new SOSLoggerSystem(policeForceAgent.me(), "newClear", false, OutputType.File, false);
		policeForceAgent.sosLogger.addToAllLogType(logger);

	}

	/** Added by Hesam for new clear algoritm **/
	public Point nextPointToClear(Path path) {
		boolean isReachable = false;
//		debug.setBackgroundEntities(Color.gray, agent.model().areas(), agent.model().blockades());
		ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		ArrayList<Blockade> blocklist = new ArrayList<Blockade>();
		Point me = agent.me().getPositionPoint().toGeomPoint();
		Point p1 = me;
		Point p2 = null;
		Point select;
		Node[] allMoveNodes = path.getNodes();
		if (allMoveNodes == null) {
			p2 = path.getDestination().second().toGeomPoint();
			logger.debug("allMoveNodes == null so next node is destination point " + p2);
		} else {
			p2 = getPoint(allMoveNodes[0]);
			logger.debug("allMoveNodes != null so next node allMoveNodes[0]" + p2);
			/**
			 * TO Draw Path
			 */
			for (int i = 1; i < allMoveNodes.length; i++) {
				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(getPoint(allMoveNodes[i - 1]), getPoint(allMoveNodes[i])), "Move node " + i, Color.yellow, false, true));
				if (i == 4)
					break;
			}
		}
		if (p2 != null) {
			Point inRangeTarget = p2;
			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "Target (before check of distance)", Color.red, false, true));
			if (Point.distance(me.getX(), me.getY(), p2.getX(), p2.getY()) > agent.clearDistance - agent.clearWidth - 5) {
				float zavie = (float) Math.atan2(p2.getY() - me.getY(), p2.getX() - me.getX());
				int rang = agent.clearDistance - agent.clearWidth;
				logger.warn("clear range =" + rang);
				inRangeTarget = new Point((int) (me.x + rang * Math.cos(zavie)), (int) (me.y + rang * Math.sin(zavie)));
				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, inRangeTarget), "Target (after check of distance and selected)", Color.green, false, true));

			}
			Line2D l = new Line2D.Double(p1, inRangeTarget);
			Collection<Road> roads = getCheckingRoads(l, path);
			logger.debug("roads in line= " + roads);
			addToBlockadeList(blocklist, roads, l);
			logger.debug("block in way =" + blocklist);

			if (blocklist.size() > 0) {
				for (Blockade blockade : blocklist)
					for (Edge e : blockade.getExpandedBlock().getEdges()) {
						shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(e.getStart(), e.getEnd()), "blockade edge", Color.orange, false, true));
					}
				//				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, inRangeTarget), "Target (first)", Color.red, false, true));
				//				log.warn(Point.distance(me.getX(), me.getY(), p2.getX(), p2.getY()) + " == " + (agent.clearDistance - agent.clearWidth - 100));
				if (agent.me().getAreaPosition() instanceof Road) {
					Road myRoad = (Road) agent.me().getAreaPosition();
					if (allMoveNodes == null) {

						if (Reachablity.isReachable(myRoad, new Point2D(p1.getX(), p1.getY()), new Point2D(p2.getX(), p2.getY())) == ReachablityState.Open) {
							shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "reachable points(1)", Color.magenta, false, true));
							//							debug.show("reachable bood velesh kardam", shapes);
							isReachable = true;
						}
					} else {
						if (inRangeTarget == p2) {

							if (Reachablity.isReachable(myRoad, new Point2D(p1.getX(), p1.getY()), allMoveNodes[0].getRelatedEdge()) == ReachablityState.Open) {
								shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "reachable points(2)", Color.magenta, false, true));
								shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(allMoveNodes[0].getRelatedEdge().getStart(), allMoveNodes[0].getRelatedEdge().getEnd()), "select edge", Color.green, false, true));
								//								debug.show("reachable bood velesh kardam", shapes);
								isReachable = true;
							}
						} else {
							if (Reachablity.isReachable(myRoad, new Point2D(p1.getX(), p1.getY()), new Point2D(inRangeTarget.getX(), inRangeTarget.getY())) == ReachablityState.Open) {
								shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, inRangeTarget), "reachable points(2.5 last point not in range)", Color.magenta, false, true));
								shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(allMoveNodes[0].getRelatedEdge().getStart(), allMoveNodes[0].getRelatedEdge().getEnd()), "select edge", Color.green, false, true));
								//								debug.show("reachable bood velesh kardam ta onje ke to range", shapes);
								isReachable = true;
							}
						}
					}

				} else {
					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "reachable points(3)", Color.magenta, false, true));
//					debug.show("reachable bood velesh kardam", shapes);
					isReachable = true;
				}
				if (isReachable) {
					logger.warn("first node is reachable");
					shapes.clear();
				} else {
//					debug.show("first node " + agent.model().time(), shapes);
					log.debug("return p2 " + p2);
					return p2;
				}
			}
		} else {
			//p2 is null
			logger.warn("in first select of node p2 was null why?");
		}
		for (int i = 1; i < allMoveNodes.length; i++) {
			p1 = p2;
			p2 = getPoint(allMoveNodes[i]);
			select = null;
			Line2D l = new Line2D.Double(p1, p2);
			Collection<Road> roads = getCheckingRoads(l, path);
			addToBlockadeList(blocklist, roads, l);
			if (blocklist.size() > 0) {
				//				logger.warn(" p1 in :" + agent.model().areas().get(allMoveNodes[i - 1].getAreaIndex()) + " p2 in :" + agent.model().areas().get(allMoveNodes[i].getAreaIndex()));
				select = givePointToClearLine(me, p1, p2, shapes);
				if (select == null) {
					break;
				}
				if (Point.distance(me.getX(), me.getY(), select.getX(), select.getY()) >= agent.clearDistance - agent.clearWidth) {
					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, select), "out off range", Color.magenta, false, true));
//					debug.show("2 piunt is farr", shapes);
					break;
				}
				Road baseRoad = null;
				Area area = agent.model().areas().get(allMoveNodes[i - 1].getAreaIndex());
				if (area instanceof Road) {
					baseRoad = (Road) area;
					if (Reachablity.isReachable(baseRoad, allMoveNodes[i - 1].getRelatedEdge(), allMoveNodes[i].getRelatedEdge()) == ReachablityState.Open) {
						shapes.clear();
						shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "is reachable", new Color(0, 100, 100, 150), false, true));
						continue;
					}
				}

				log.warn("secound point select " + select);
				return select;
			}
		}
		return null;
	}

//	static ShapeDebugFrame debug = new ShapeDebugFrame();

	private Point givePointToClearLine(Point me, Point p1, Point p2, ArrayList<ShapeInfo> shapes2) {
//		debug.setBackgroundEntities(Color.gray, agent.model().areas(), agent.model().blockades());
		ArrayList<ShapeDebugFrame.ShapeInfo> shapes = shapes2;
		Point near, far;
		int nearDis, farDis;
		if (Point.distance(me.getX(), me.getY(), p1.getX(), p1.getY()) > Point.distance(me.getX(), me.getY(), p2.getX(), p2.getY())) {
			near = p2;
			far = p1;
		} else {
			near = p1;
			far = p2;
		}
		nearDis = (int) Point.distance(me.getX(), me.getY(), near.getX(), near.getY());
		farDis = (int) Point.distance(me.getX(), me.getY(), far.getX(), far.getY());
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, near), "near", Color.red, false, true));
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, far), "far " + farDis, Color.red, false, true));
		float zavie1 = (float) Math.atan2(far.getY() - me.getY(), far.getX() - me.getX());
		Point mirror = new Point((int) (me.getX() + nearDis * Math.cos(zavie1)), (int) (me.getY() + nearDis * Math.sin(zavie1)));
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, mirror), "mirror", Color.cyan, false, true));
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(mirror, near), "near-mirror", Color.pink, false, true));
		Point between = new Point((mirror.x + near.x) / 2, (mirror.y + near.y) / 2);
		float zavie2 = (float) Math.atan2(between.getY() - me.getY(), between.getX() - me.getX());
		farDis = (int) (farDis * Math.cos(zavie1 - zavie2));
		Point target = new Point((int) (me.getX() + farDis * Math.cos(zavie2)), (int) (me.getY() + farDis * Math.sin(zavie2)));
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, target), "Target " + farDis, Color.green, false, true));
		/**
		 * IF Clear Width not enough to clear 2 point
		 */
		if (Point.distance(far.getX(), far.getY(), target.getX(), target.getY()) > (agent.clearWidth) - 4) {
			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(far, target), "nemishe chon bishtar az width" + farDis, Color.red, false, true));
//			debug.show("width is not enough to clear this 2 point from here", shapes);
			return null;
		}
		/**
		 * **/
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(far, target), "mishe clear kard 2 tasho az inja " + farDis, Color.green, false, true));
//		debug.show("secound node in range", shapes);
		return target;
	}

	@Override
	public ArrayList<Blockade> getBlockingBlockadeOfPath(Path path) {
		ArrayList<Blockade> blocklist = new ArrayList<Blockade>();
		findGeoPathInMove(path, blocklist);
		return blocklist;
	}

	public Point getPoint(Node node) {
		return node.getPosition().toGeomPoint();
	}

	public void findGeoPathInMove(Path path, ArrayList<Blockade> blocklist) {
		//				debug.setBackgroundEntities(Color.gray,agent.model().areas());
		Point p1 = agent.me().getPositionPoint().toGeomPoint();

		//				addToIfItIsGood(selectedRoads,path.getSource().first());

		ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		Node[] allMoveNodes = path.getNodes();
		ArrayList<rescuecore2.geometry.Line2D> lines = new ArrayList<rescuecore2.geometry.Line2D>();
		//				position.add(path.getSource().first());
		if (allMoveNodes == null) {
			Point p2 = path.getDestination().second().toGeomPoint();
			Line2D l = new Line2D.Double(p1, p2);
			Collection<Road> roads = getCheckingRoads(l, path);
			logger.debug("roads in line= " + roads);
			addToBlockadeList(blocklist, roads, l);
			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(l.getP1().getX(), l.getP1().getY(), l.getP2().getX(), l.getP2().getY()), "null", Color.red, false, true));

		} else {
			for (int i = 0; i < allMoveNodes.length; i++) {

				Point p2 = getPoint(allMoveNodes[i]);
				////								debug.setBackground(shapes);
				////								debug.show("",new ShapeDebugFrame.Point2DShapeInfo(new Point2D(p1.x, p1.y), "p1", Color.black, true),
				//										new ShapeDebugFrame.Point2DShapeInfo(new Point2D(p2.x, p2.y), "p2", Color.white, true),
				//										new ShapeDebugFrame.Line2DShapeInfo(lines, "current", Color.red, false, true));
				Line2D l = new Line2D.Double(p1, p2);
				lines.add(new rescuecore2.geometry.Line2D(p1, p2));
				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), i + "", Color.red, false, true));
				Collection<Road> roads = getCheckingRoads(l, path);

				addToBlockadeList(blocklist, roads, l);
				p1 = p2;
			}
			Point p2 = path.getDestination().second().toGeomPoint();
			Line2D l = new Line2D.Double(p1, p2);
			//			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1,p2), "end",Color.blue, false, true));
			Collection<Road> roads = getCheckingRoads(l, path);
			addToBlockadeList(blocklist, roads, l);

		}
		for (Blockade block : blocklist) {
			shapes.add(new ShapeDebugFrame.AWTShapeInfo(block.getShape(), block + "", Color.BLACK, true));
		}
		//				debug.setBackgroundEntities(Color.gray,agent.model().areas());
		//				debug.setBackgroundEntities(Color.white,agent.model().blockades());
		//				debug.show("", shapes);
	}

	private Collection<Road> getCheckingRoads(Line2D l, Path path) {
		Collection<Road> roads = agent.model().getObjectsInRectangle(l.getBounds(), Road.class);

		if (path.getSource().first() instanceof Road)
			roads.add((Road) path.getSource().first());
		if (path.getDestination().first() instanceof Road)
			roads.add((Road) path.getDestination().first());

		return roads;
	}

	private void addToBlockadeList(ArrayList<Blockade> blocklist, Collection<Road> roads, Line2D l) {
		for (Road road : roads)
			addToBlockadeList(blocklist, road, l);

	}

	private void addToBlockadeList(ArrayList<Blockade> blocklist, Area road, Line2D l) {
		if (!road.isBlockadesDefined())
			return;
		if (road.getLastSenseTime() < agent.time() - 1)
			return;

		for (Blockade block : road.getBlockades()) {
			if (PoliceUtils.isValid(block) && haveIntersect(l, block))
				blocklist.add(block);
		}
	}

	private boolean haveIntersect(Line2D l, Blockade blockade) {
		for (Edge e : blockade.getExpandedBlock().getEdges()) {
			Point2D p = Utill.intersectLowProcess((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2(), e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY());
			if (p != null)
				return true;
		}
		//		if (SOSGeometryTools.haveIntersection(blockade.getExpandedBlock().getShape(), l))
		//			return true;
		return false;
	}
}
