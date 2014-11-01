package sos.police_v2.base.clearablePointToReachable;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.move.Path;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame.ShapeInfo;
import sos.base.worldGraph.Node;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

public class GeoClearPointReachablity extends ClearablePointToReachable {

//	static ShapeDebugFrame debug = new ShapeDebugFrame();
	short index;
	boolean isReachable;
	Node[] allMoveNodes;
	Point me;
	Point p1;
	Point p2;
	Point select = null;
	ArrayList<ShapeDebugFrame.ShapeInfo> shapes;
	boolean doClear = false;
	private Pair<ArrayList<Blockade>, Pair<Integer, Point>> lastThinkResult;
	private Pair<ArrayList<Blockade>, Pair<Integer, Point>> lastTwoThinkResult;
	private ArrayList<Blockade> blocklist;

	public GeoClearPointReachablity(PoliceForceAgent forceAgent) {
		super(forceAgent);
		lastThinkResult = new Pair<ArrayList<Blockade>, Pair<Integer, Point>>(new ArrayList<Blockade>(), new Pair<Integer, Point>(0, agent.me().getPositionPoint().toGeomPoint()));

	}

	@Override
	public Point nextPointToClear(Path path, boolean checkReachablity, boolean doClear) {
		logger.debug("++++++++++++++++++++++CHECK REACHABLITY=" + checkReachablity + "++++++++++++++++++" + new Error().getStackTrace()[1]);
		this.doClear = doClear;
		index = 0;
		isReachable = false;
		allMoveNodes = path.getNodes();
		me = agent.me().getPositionPoint().toGeomPoint();
		p1 = me;
		p2 = null;
		select = null;
		blocklist = new ArrayList<Blockade>();
		/***/
//		debug.setBackgroundEntities(Color.gray, agent.model().areas(), agent.model().blockades());
		shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();
		/***********/

		Point result = checkDestination(blocklist, path, checkReachablity);
		if (result != null)
			if (doClear)
				if (notSameAsLast()) {
					lastTwoThinkResult = lastThinkResult;
					lastThinkResult = new Pair<ArrayList<Blockade>, Pair<Integer, Point>>(blocklist, new Pair<Integer, Point>(getCostOfBlockades(blocklist), me));
					logger.debug("..::..::..JAVABE NAHAYI : " + result);
					return result;
				}
				else
					return null;
		logger.info("----------------------checkDestinationWithOutPath is passed------------------");
		result = checkFirstEdge(blocklist, path, checkReachablity);
		if (result != null)
			if (doClear)
				if (notSameAsLast()) {
					lastTwoThinkResult = lastThinkResult;
					lastThinkResult = new Pair<ArrayList<Blockade>, Pair<Integer, Point>>(blocklist, new Pair<Integer, Point>(getCostOfBlockades(blocklist), me));
					logger.debug("..::..::..JAVABE NAHAYI : " + result);
					return result;
				}
				else
					return null;
		logger.info("---------------------First edgh skiped got to check others-------------------");
		if (allMoveNodes != null) {
			result = checkNextEdges(blocklist, path, checkReachablity);
			if (result != null)
				if (doClear)
					if (notSameAsLast()) {
						lastTwoThinkResult = lastThinkResult;
						lastThinkResult = new Pair<ArrayList<Blockade>, Pair<Integer, Point>>(blocklist, new Pair<Integer, Point>(getCostOfBlockades(blocklist), me));
						logger.debug("..::..::..JAVABE NAHAYI : " + result);
						return result;
					}
					else
						return null;
		}
		return null;
	}

	private boolean notSameAsLast() {
		if (lastTwoThinkResult == null)
			return true;
		if (lastTwoThinkResult.second().first() != getCostOfBlockades(blocklist))
			return true;
		if (lastTwoThinkResult.second().second() != me)
			return true;
		for (Blockade blockade : blocklist)
			if (!lastTwoThinkResult.first().contains(blockade))
				return true;
		for (Blockade blockade : lastTwoThinkResult.first())
			if (!blocklist.contains(blockade))
				return true;
		logger.warn("think mese ghabl bod ba check kardan cost bloacked handel kardam \n now:" + blocklist + "\n last:" + lastThinkResult.first());
		return false;
	}

	private int getCostOfBlockades(ArrayList<Blockade> blockades) {
		int sum = 0;
		for (Blockade blockade : blockades) {
			sum += blockade.getRepairCost();
		}
		return sum;
	}

	private Point checkDestination(ArrayList<Blockade> blocklist, Path path, boolean checkReachablity) {
		p2 = path.getDestination().second().toGeomPoint();
		if (Point.distance(me.getX(), me.getY(), p2.getX(), me.getY()) < agent.clearDistance - agent.clearWidth) {
			Line2D l = new Line2D.Double(me, p2);
			Collection<Road> roads = getCheckingRoads(l, path);
			if (isAreaAllInRoads(getMoveWayArea(me, p2), roads)) {

				setBlockadeInWayArea(p1, p2, blocklist, path);

				if (blocklist.size() > 0) {
					logger.debug("Destination is in clear rang and all of line is in roads so clear it");
					if (isBlockadeInClearArea(blocklist, agent.me(), p2, agent.clearDistance, agent.clearWidth))
						return p2;
					else
						logger.debug("!!! clear area bloackade nadasht pass nabayad clear kone");
				}
			}
		}
		return null;
	}

	/******************************************************************************************
	 *** baraye inke peyda kone age jayi ke vasade o mitone edghaye dg ro clear kone clear kone**
	 ********************************************************************************************/
	private Point checkNextEdges(ArrayList<Blockade> blocklist, Path path, boolean checkReachablity) {
		for (int i = index + 1; i < allMoveNodes.length; i++) {
			p1 = p2;
			p2 = getPoint(allMoveNodes[i]);
			if (!isInClearRang(me, p1, agent.clearDistance) && !isInClearRang(me, p2, agent.clearDistance)) {
				logger.debug("Both nodes are out of range so cant clear it");
				continue;
			}
			logger.debug("checking to " + i + " node and distance from me =" + Point.distance(me.getX(), me.getY(), p2.getX(), p2.getY()));
			select = null;
			setBlockadeInWayArea(p1, p2, blocklist, path);
			logger.debug("block way= " + blocklist);
			if (blocklist.size() > 0) {
				//				logger.warn(" p1 in :" + agent.model().areas().get(allMoveNodes[i - 1].getAreaIndex()) + " p2 in :" + agent.model().areas().get(allMoveNodes[i].getAreaIndex()));
				select = givePointToClearLine(me, p1, p2, shapes, true);
				if (select == null) {
					logger.debug("select null dade");
					return null;
				}
				if (Point.distance(me.getX(), me.getY(), select.getX(), select.getY()) >= agent.clearDistance - agent.clearWidth) {
					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, select), "out off range", Color.magenta, false, true));
					logger.debug("selected target to clear is farr so find a new near point of this line");
					Point inRangeTarget = getInRangPoint(me, select);
					////debug.show("2 piunt is farr", shapes);
					if (!isBlockadeInClearArea(blocklist, agent.me(), inRangeTarget, agent.clearDistance, agent.clearWidth)) {
						logger.debug("blockadi  to cleararea nist pass bayad null bedim ke move bezanim");
						return null;
					}
					return inRangeTarget;
				}
				Road baseRoad = null;
				Area area = agent.model().areas().get(allMoveNodes[i - 1].getAreaIndex());
				logger.debug("reachable check is in area" + area);
				if (area instanceof Road) {
					baseRoad = (Road) area;
					if (checkReachablity && Reachablity.isReachable(baseRoad, allMoveNodes[i - 1].getRelatedEdge(), allMoveNodes[i].getRelatedEdge()) == ReachablityState.Open) {
						shapes.clear();
						shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "is reachable", new Color(0, 100, 100, 150), false, true));
						logger.debug("skip node beacuse its reachable");
						continue;
					}
				}
				if (!isBlockadeInClearArea(blocklist, agent.me(), select, agent.clearDistance, agent.clearWidth)) {
					logger.debug("NO block in selected clear area so skip it");
					return null;
				}

				logger.debug("secound point select " + select);
				return select;
			}
		}
		return getLastDestinationOfPath(blocklist, path, checkReachablity);
	}

	/***************************************************************************************
	 * Baraye CHeck kardane Destination path
	 ***************************************************************************************/
	private Point getLastDestinationOfPath(ArrayList<Blockade> blocklist, Path path, boolean checkReachablity) {
		logger.info("----------------------------------CHECKING DESTINATION-----------------------------------");
		p1 = p2;
		p2 = path.getDestination().second().toGeomPoint();
		if (!isInClearRang(me, p1, agent.clearDistance) && !isInClearRang(me, p2, agent.clearDistance)) {
			logger.debug("Both nodes are out of range so cant clear it");
			return null;
		}
		logger.debug(" checking Destination to clear and distance from me =" + Point.distance(me.getX(), me.getY(), p2.getX(), p2.getY()));
		select = null;
		setBlockadeInWayArea(p1, p2, blocklist, path);
		logger.debug("block way= " + blocklist);
		if (blocklist.size() > 0) {
			//				logger.warn(" p1 in :" + agent.model().areas().get(allMoveNodes[i - 1].getAreaIndex()) + " p2 in :" + agent.model().areas().get(allMoveNodes[i].getAreaIndex()));
			select = givePointToClearLine(me, p1, p2, shapes, true);
			if (select == null) {
				logger.debug("select null dade");
				return null;
			}
			logger.debug("find select=" + select);
			if (Point.distance(me.getX(), me.getY(), select.getX(), select.getY()) >= agent.clearDistance - agent.clearWidth) {
				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, select), "out off range", Color.magenta, false, true));
				logger.debug("selected target to clear is farr so find a new near point of this line");
				Point inRangeTarget = getInRangPoint(me, select);
				////debug.show("2 piunt is farr", shapes);
				if (!isBlockadeInClearArea(blocklist, agent.me(), inRangeTarget, agent.clearDistance, agent.clearWidth)) {
					logger.debug("blockadi  to cleararea nist pass bayad null bedim ke move bezanim");
					return null;
				}
				return inRangeTarget;
			}
			Road baseRoad = null;
			Area area = path.getDestination().first();
			logger.debug("reachable check is in area" + area);
			if (area instanceof Road) {
				baseRoad = (Road) area;
				if (checkReachablity && Reachablity.isReachable(baseRoad, new Point2D(p1.getX(), p1.getY()), new Point2D(p2.getX(), p2.getY())) == ReachablityState.Open) {
					shapes.clear();
					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "is reachable (destination)", new Color(0, 100, 100, 150), false, true));
					logger.debug("skip Destination beacuse its reachable");
					return null;
				}
			}
			if (!isBlockadeInClearArea(blocklist, agent.me(), select, agent.clearDistance, agent.clearWidth)) {
				logger.debug("NO block in selected clear area so skip it");
				return null;
			}

			logger.debug("secound point select " + select);
			if (isBlockadeInClearArea(blocklist, agent.me(), select, agent.clearDistance, agent.clearWidth))
				return select;
			else
				logger.debug(" clear area bloackadenadasht pass nabayad clear kone");
		}

		return null;
	}

	/**
	 * Baraye inke avalin node move ro az onja ke vasadim ro peyda kone o clear kone
	 ***********************************************************************/
	private Point checkFirstEdge(ArrayList<Blockade> blocklist, Path path, boolean checkReachablity) {
		if (allMoveNodes == null) {
			p2 = path.getDestination().second().toGeomPoint();
			logger.debug("allMoveNodes == null so next node is destination point " + p2);
		} else {
			p2 = getPoint(allMoveNodes[index]);
			logger.debug("allMoveNodes != null so next node allMoveNodes[0]" + p2);
			while (p2.x == me.x && p2.y == me.y && allMoveNodes.length > index + 1) {
				index++;
				p2 = getPoint(allMoveNodes[index]);
				logger.debug("p2 ro khodam bod avazesh kardam be " + index + " omin node " + p2);
			}

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
			logger.debug("checking to " + index + " node and distance from me =" + Point.distance(me.getX(), me.getY(), p2.getX(), p2.getY()));
			Point inRangeTarget = p2;
			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "Target (before check of distance)", Color.red, false, true));
			if (Point.distance(me.getX(), me.getY(), p2.getX(), p2.getY()) > agent.clearDistance - agent.clearWidth - 5) {
				inRangeTarget = getInRangPoint(me, p2);
				shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, inRangeTarget), "Target (after check of distance and selected)", Color.green, false, true));
			}
			setBlockadeInWayArea(p1, inRangeTarget, blocklist, path);
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

						if (checkReachablity && Reachablity.isReachable(myRoad, new Point2D(p1.getX(), p1.getY()), new Point2D(p2.getX(), p2.getY())) == ReachablityState.Open) {
							shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "reachable points(1)", Color.magenta, false, true));
							//debug.show("reachable bood velesh kardam", shapes);
							isReachable = true;
						}
					} else {
						if (inRangeTarget == p2) {

							if (checkReachablity && Reachablity.isReachable(myRoad, new Point2D(p1.getX(), p1.getY()), allMoveNodes[index].getRelatedEdge()) == ReachablityState.Open) {
								shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "reachable points(2)", Color.magenta, false, true));
								shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(allMoveNodes[index].getRelatedEdge().getStart(), allMoveNodes[index].getRelatedEdge().getEnd()), "select edge", Color.green, false, true));
								//debug.show("reachable bood velesh kardam", shapes);
								logger.debug("when p2==inRangeTarget reachablilty says this is reachable me=" + me + " edge=" + allMoveNodes[index].getRelatedEdge());
								isReachable = true;
							}
						} else {
							if (checkReachablity && Reachablity.isReachable(myRoad, new Point2D(p1.getX(), p1.getY()), new Point2D(inRangeTarget.getX(), inRangeTarget.getY())) == ReachablityState.Open) {
								shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, inRangeTarget), "reachable points(2.5 last point not in range)", Color.magenta, false, true));
								shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(allMoveNodes[index].getRelatedEdge().getStart(), allMoveNodes[index].getRelatedEdge().getEnd()), "select edge", Color.green, false, true));
								//debug.show("reachable bood velesh kardam ta onje ke to range", shapes);
								logger.debug("when p2!=inRangeTarget reachablilty says this is reachable ");
								isReachable = true;
							}
						}
					}

				} else if (checkReachablity) {
					logger.debug("to road nabodam ke blockade dashte bashe");
					shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(p1, p2), "reachable points(3)", Color.magenta, false, true));
					//debug.show("reachable bood velesh kardam", shapes);
					isReachable = true;
				}
				if (isReachable) {
					logger.debug("first node is reachable");
					shapes.clear();
				} else {
					Point better = getBetterPointToClearTwoFirstEdgh(blocklist, path, inRangeTarget);
					if (better != null)
						return better;
					//debug.show("first node " + agent.model().time(), shapes);
					//					if (!isBlockadeInClearArea(blocklist, agent.me(), p2, agent.clearDistance, agent.clearWidth)){
					//						return null;
					//					}
					logger.debug("return p2 " + p2);
					return p2;
				}
			}
		} else {
			//p2 is null
			logger.warn("in first select of node p2 was null why?");
		}
		return null;
	}

	/**
	 * baraye inke agar jayi vasade o va ba clear kardane edge badi edge aval ham clear mishe pass edghe badio clear kone
	 ********************************************************************************************************************/
	private Point getBetterPointToClearTwoFirstEdgh(ArrayList<Blockade> blocklist, Path path, Point inRangeTarget) {
		logger.info("------------------------check better point-----------------------");
		if (allMoveNodes != null && allMoveNodes.length > index + 1)
			if (p2 == inRangeTarget) {
				logger.debug("check konim bebinim age mishe ba clear kardane edge badi edghe avalo clear kard ke ono clear konim ta inam clear beshe ");
				blocklist.clear();
				index++;
				Point t1 = p2;
				Point t2 = getPoint(allMoveNodes[index]);
				while (t1.x == t2.x && t1.y == t2.y && allMoveNodes.length > index + 1) {
					index++;
					t2 = getPoint(allMoveNodes[index]);
					logger.debug("t2 ro khodesh bood avazesh kardam be node " + index + "om   " + t2);
				}
				logger.debug("node1= " + t1 + " node2=" + t2 + "line lenth=" + Point.distance(t1.getX(), t1.getY(), t2.getX(), t2.getY()));
				logger.debug("road hayi ke baraye entekhabe dovom to rah hastan baraye clear behtar");
				setBlockadeInWayArea(t1, t2, blocklist, path);
				logger.debug("block way= " + blocklist);
				if (blocklist.size() > 0) {
					select = givePointToClearLine(me, t1, t2, shapes, true);
					if (select != null) {
						Road baseRoad = null;
						Area area = agent.model().areas().get(allMoveNodes[index].getAreaIndex());
						if (area instanceof Road) {
							baseRoad = (Road) area;
							if (allMoveNodes.length > index + 1) {
								//								if (Reachablity.isReachable(baseRoad, allMoveNodes[index].getRelatedEdge(), allMoveNodes[index + 1].getRelatedEdge()) != ReachablityState.Open) {
								if (Point.distance(me.getX(), me.getY(), select.getX(), select.getY()) < agent.clearDistance - agent.clearWidth) {
									logger.debug("noghteye behtar entekhab shod baraye clear kardan ta inke clear aval ro anjam bede" + select);
									shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(me, select), "jaygozin baraye clear aval", Color.BLACK, false, true));
									//debug.show("noghteye jaygozin bejaye claear kardane edge aval", shapes);
									return select;
								} else {
									logger.debug("noghteye jaygozin entekhab shod vali in rang nabod");
									Point te = getInRangPoint(me, select);
									//debug.show("2 piunt is farr", shapes);
									if (!isBlockadeInClearArea(blocklist, agent.me(), te, agent.clearDistance, agent.clearWidth)) {
										logger.debug("blockadi  to cleararea nist pass bayad null bedim ke move bezanim");
										return null;
									}
									return te;
								}
								//								} else {
								//									logger.debug("edghe dovom reachable bood pass nemikhast ke jaygozin konam");
							}
						} else {
							logger.debug("to road nist edgh");
						}
					} else {
						logger.debug("select null return shode");
					}
				}
			}
		return null;
	}

	/***
	 * baraye peyda kardane noghteye 3omi ke ba clear kardanesh 2 noghteye dg ke dar rastaye agent nist clear mishe
	 **********************************************************************************************/
	private Point givePointToClearLine(Point me, Point po1, Point po2, ArrayList<ShapeInfo> shapes2, boolean checkWithAreaIfCantFind) {
//		debug.setBackgroundEntities(Color.gray, agent.model().areas(), agent.model().blockades());
		ArrayList<ShapeDebugFrame.ShapeInfo> shapes = shapes2;
		Point near, far;
		int nearDis, farDis;
		if (Point.distance(me.getX(), me.getY(), po1.getX(), po1.getY()) > Point.distance(me.getX(), me.getY(), po2.getX(), po2.getY())) {
			near = po2;
			far = po1;
		} else {
			near = po1;
			far = po2;
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
			//debug.show("width is not enough to clear this 2 point from here", shapes);
			if (checkWithAreaIfCantFind) {
				logger.debug("find point to clear with givePointToClearLineWithArea");
				return givePointToClearLineWithArea(me, target, near, far);
			}
			return null;
		}
		/**
		 * **/
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(far, target), "mishe clear kard 2 tasho az inja " + farDis, Color.green, false, true));
		//debug.show("secound node in range", shapes);
		return target;
	}

	/**
	 * Clear area ro check mikone bebine mishe clear kard jori ke 2 ta nobghte tosh biofte
	 ************************************************************************************************/
	private Point givePointToClearLineWithArea(Point me2, Point target, Point near, Point far) {
		Point tt1 = near;
		Point tt2 = far;
		float z1 = (float) Math.atan2(tt1.getY() - me.getY(), tt1.getX() - me.getX());
		float z2 = (float) Math.atan2(tt2.getY() - me.getY(), tt2.getX() - me.getX());
		float deltaZ = z1 - z2;
		//		float base = (float) Math.atan2(target.getY() - me.getY(), target.getX() - me.getX());
		if (!isInClearRang(me, tt1, agent.clearDistance - agent.clearWidth - 1000))
			tt1 = getInRangPoint(me2, near, agent.clearDistance - agent.clearWidth - 1000);
		if (!isInClearRang(me, tt2, agent.clearDistance - agent.clearWidth - 1000))
			tt2 = getInRangPoint(me2, far, agent.clearDistance - agent.clearWidth - 1000);
		int rang = agent.clearDistance;
		Point target2 = new Point(0, 0);
		if (deltaZ >= 0) {
			logger.info("deltaz mosbat bood");
			for (float plus = 0; plus <= deltaZ; plus += 0.05f) {
				target2.setLocation(me.getX() + rang * Math.cos(z2 + plus), me.getY() + rang * Math.sin(z2 + plus));
				java.awt.geom.Area clearArea = PoliceUtils.getClearArea(agent.me(), target2.x, target2.y, agent.clearDistance, agent.clearWidth);
				logger.debug("tt1 contain=" + clearArea.contains(tt1.getX(), tt1.getY()) + "  tt2 contain=" + clearArea.contains(tt2.getX(), tt2.getY()));
				if (clearArea.contains(tt1.getX(), tt1.getY()) && clearArea.contains(tt2.getX(), tt2.getY())) {
					logger.debug("noghteye clear ba area peyda shod " + target2);
					return target2;
				}
			}
		} else {
			logger.info("deltaz manfi bood");
			deltaZ = Math.abs(deltaZ);
			for (float plus = 0; plus <= deltaZ; plus += 0.05f) {
				target2.setLocation(me.getX() + rang * Math.cos(z2 - plus), me.getY() + rang * Math.sin(z2 - plus));
				java.awt.geom.Area clearArea = PoliceUtils.getClearArea(agent.me(), target2.x, target2.y, agent.clearDistance, agent.clearWidth);
				logger.debug("tt1 contain=" + clearArea.contains(tt1.getX(), tt1.getY()) + "  tt2 contain=" + clearArea.contains(tt2.getX(), tt2.getY()));
				if (clearArea.contains(tt1.getX(), tt1.getY()) && clearArea.contains(tt2.getX(), tt2.getY())) {
					logger.debug("noghteye clear ba area peyda shod " + target2);
					return target2;
				}
			}
		}
		//			target2.setLocation(me.getX() + rang * Math.cos(base - plus), me.getY() + rang * Math.sin(base - plus));
		//			clearArea = PoliceUtils.getClearArea(agent.me(), target2.x, target2.y, agent.clearDistance, agent.clearWidth);
		//			logger.debug("tt1 contain=" + clearArea.contains(tt1.getX(), tt1.getY()) + "  tt2 contain=" + clearArea.contains(tt2.getX(), tt2.getY()));
		//			if (clearArea.contains(tt1.getX(), tt1.getY()) && clearArea.contains(tt2.getX(), tt2.getY())) {
		//				logger.debug("noghteye clear ba area peyda shod " + target2);
		//				return target2;
		//			}
		logger.debug("checkArea ham natonest noghtaro peyda kone :|");
		return null;
	}

}
