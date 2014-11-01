package sos.base.move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import rescuecore2.misc.Pair;
import rescuecore2.standard.messages.AKMove;
import rescuecore2.worldmodel.EntityID;
import sos.base.PlatoonAgent;
import sos.base.SOSAgent;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.MessageConstants;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.move.types.DistanceMove;
import sos.base.move.types.MoveType;
import sos.base.move.types.PoliceMove;
import sos.base.move.types.PoliceReachablityMove;
import sos.base.move.types.SearchMove;
import sos.base.move.types.StandardMove;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.SOSActionException;
import sos.base.util.SOSGeometryTools;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.information_stacker.CycleInformations;
import sos.base.util.information_stacker.act.MoveAction;
import sos.base.util.information_stacker.act.StockMoveAction;
import sos.base.util.mapRecognition.MapRecognition.MapName;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.worldGraph.VirtualGraphEdge;
import sos.base.worldGraph.WorldGraph;
import sos.police_v2.PoliceForceAgent;
import sos.tools.GraphEdge;
import sos.tools.UnionFind;

/**
 * @author Aramik
 */
public class Move implements MoveConstants {

	private SOSAgent<? extends StandardEntity> me;
	private HashMap<Integer, MoveType> moves;// <class hashcode,Move type reference>
	private BFS bfs;
	// ----------traffic handling---------//
	private LinkedList<Pair<GraphEdge, Integer>> traffic;
	private int trafficRemainTime;

	// ----------Reachability handling---------//
	private UnionFind nodesUnion;

	public Move(SOSAgent<? extends StandardEntity> me, WorldGraph graph) {
		this.me = me;
		this.moves = new HashMap<Integer, MoveType>();
		this.bfs = new BFS(me);
		this.traffic = new LinkedList<Pair<GraphEdge, Integer>>();
		this.trafficRemainTime = (me instanceof PoliceForceAgent) ? 9 : 6;
		this.nodesUnion = new UnionFind(graph.getNumberOfNodes());
		for (GraphEdge ge : me.model().graphEdges())
			if (ge instanceof VirtualGraphEdge)
				ge.setState(GraphEdgeState.Open);
		// ------------------------------------- Adding move types here
		moves.put(PoliceReachablityMove.class.hashCode(), new PoliceReachablityMove(me, graph));

		if (me instanceof PoliceForceAgent) {
			moves.put(PoliceMove.class.hashCode(), new PoliceMove(me, graph));
			moves.put(DistanceMove.class.hashCode(), new DistanceMove(me, graph));
		} else
			moves.put(StandardMove.class.hashCode(), new StandardMove(me, graph));
		//		if (me instanceof FireBrigadeAgent)
		//			moves.put(FireMove.class.hashCode(), new FireMove(me, graph));
		moves.put(SearchMove.class.hashCode(), new SearchMove(me, graph));
		// ------------------------------------------------------------
		cycle();
	}

	/**
	 * should call each cycle to update weights
	 */
	public void cycle() {
		// update weights and ...
		for (MoveType mt : moves.values())
			mt.cycle();
		removeTemporaryTrafficBlocks();
	}

	private void removeTemporaryTrafficBlocks() {
		while (!traffic.isEmpty() && me.time() > traffic.getFirst().second() + this.trafficRemainTime) {
			traffic.getFirst().first().setFreeTraffic();
			log().info(" traffic removed " + traffic.getFirst().first());
			traffic.removeFirst();
		}
		if (me.location() instanceof Road) {
			//			inBuildingStoppedCount = 0;
			// isBuildingCenterPassed = false;
		}
	}

	// TODO add stopped time to 3 cycles
	private void checkTraffic() throws SOSActionException {
		log().info("Checking traffic");
		if (me.time() < 4) {
			log().debug("No Traffic! Beacuse time is less than 4");
			return;
		}
		CycleInformations cycleinfo1 = me.informationStacker.getInformations(1);
		CycleInformations cycleinfo2 = me.informationStacker.getInformations(2);
		CycleInformations cycleinfo3 = me.informationStacker.getInformations(3);
		if (cycleinfo1.getAct() instanceof MoveAction && cycleinfo2.getAct() instanceof MoveAction && cycleinfo3.getAct() instanceof MoveAction) {
			//		Path path1 = ((MoveAction) cycleinfo1.getAct()).getPath();
			Path path2 = ((MoveAction) cycleinfo2.getAct()).getPath();
			//		Path path3 = ((MoveAction) cycleinfo3.getAct()).getPath();
			log().info("Three(3) cycle move ....");
			log().debug("1 cycle ago position:" + cycleinfo1.getPositionPair());
			log().debug("2 cycle ago position:" + cycleinfo2.getPositionPair());
			log().debug("3 cycle ago position:" + cycleinfo3.getPositionPair());
			if (cycleinfo1.getPositionPair().first().equals(cycleinfo2.getPositionPair().first()) && cycleinfo1.getPositionPair().first().equals(cycleinfo3.getPositionPair().first())) {
				if (SOSGeometryTools.getDistance(cycleinfo1.getPositionPair().second(), cycleinfo2.getPositionPair().second()) < MoveConstants.TRAFFIC_CHECKING_DISTANCE && SOSGeometryTools.getDistance(cycleinfo1.getPositionPair().second(), cycleinfo3.getPositionPair().second()) < MoveConstants.TRAFFIC_CHECKING_DISTANCE) {
					if (path2.getEdges() != null && !path2.getEdges()[0].haveTraffic()) {
						this.traffic.add(new Pair<GraphEdge, Integer>(path2.getEdges()[0], me.time()));
						path2.getEdges()[0].setHaveTraffic();
						log().info("Traffic added to " + path2.getEdges()[0]);
					}
				}
			}
		}

	}

	int lastStockTime = 0;

	// TODO add stopped time to 3 cycles
	private void checkStock(Path path) throws SOSActionException {
		log().info("Checking stock");
		if (me.time() < 4) {
			log().debug("No Stock! Beacuse time is less than 4");
			return;
		}
		//		if(!(me instanceof PoliceForceAgent))
		//				check4CycleStock();
		//				check3CycleStock();
		if(me.time()-lastStockTime<10&& me.messageSystem.type==MessageConstants.Type.NoComunication&& lastStockPath!=null)
			sendStockMessage(lastStockPath);
		checkAlakiStock(path);
		check2CycleStock();
		if (me.getMapInfo().getRealMapName() == MapName.VC /* || me.getMapInfo().getRealMapName() == MapName.Kobe */)
			checkEarlyStock(path);
		lastStockTime = 0;

	}
	
	private Path lastStockPath=null;
	
	private void checkAlakiStock(Path path) throws SOSActionException {
	
		log().info("checking alaki stock");
		CycleInformations cycleinfo1 = me.informationStacker.getInformations(1);
		CycleInformations cycleinfo2 = me.informationStacker.getInformations(2);
		CycleInformations cycleinfo3 = me.informationStacker.getInformations(3);
		//Move Stock Move Stock Stock Stock
		log().trace("cycleinfo1:" + cycleinfo1);
		log().trace("cycleinfo2:" + cycleinfo2);
		log().trace("cycleinfo3:" + cycleinfo3);
		if (!(cycleinfo1.getAct() instanceof MoveAction) || cycleinfo1.getAct() instanceof StockMoveAction)
			return;
		if (/* !(cycleinfo2.getAct() instanceof MoveAction) || */!(cycleinfo2.getAct() instanceof StockMoveAction))
			return;
		if (!(cycleinfo3.getAct() instanceof MoveAction) || cycleinfo3.getAct() instanceof StockMoveAction)
			return;

		log().debug("cycleinfo1:" + cycleinfo1.getPositionPair() + "cycleinfo2:" + cycleinfo2.getPositionPair() + " cycleinfo3:" + cycleinfo3.getPositionPair());
		if (SOSGeometryTools.getDistance(cycleinfo1.getPositionPair().second(), cycleinfo2.getPositionPair().second()) < MoveConstants.TRAFFIC_CHECKING_DISTANCE
				&& SOSGeometryTools.getDistance(cycleinfo1.getPositionPair().second(), cycleinfo3.getPositionPair().second()) < MoveConstants.TRAFFIC_CHECKING_DISTANCE) {
			Path randomPath = bfs.getDummyRandomWalkPath();

			me.send(new AKMove(me.getID(), me.time(), randomPath.getIds(), randomPath.getDestination().second().getIntX(), randomPath.getDestination().second().getIntY()));
			sendStockMessage(path);
			me.informationStacker.addInfo(me.model(), new StockMoveAction(randomPath));
			lastStockTime=me.time();
			throw new SOSActionException("Move Stock Random Walk(" + randomPath + ")");
		}

	}

	private void sendStockMessage(Path path) {
		if (me instanceof PoliceForceAgent)
			return;

		if (!(me.me() instanceof Human))
			return;

		if (me.time() < 50)
			return;

		Human meEntity = (Human) me.me();

		if (meEntity.isBuriednessDefined() && meEntity.getBuriedness() > 0)
			return;

		if (meEntity.getImReachableToEdges().isEmpty())
			return;

		if (me.isTimeToActFinished())
			return;

		if (me.lastException != null)
			return;

		try {
//			if (me.getMyClusterData().isCoverer())
//				return;

			if (me.me().getAreaPosition().isBlockadesDefined() && me.me().getAreaPosition().getBlockadesID().isEmpty()) {
				boolean haveBlock = false;
				for (Area neighbour : me.me().getAreaPosition().getNeighbours()) {
					if (neighbour.isBlockadesDefined() && !neighbour.getBlockadesID().isEmpty())
						haveBlock = true;
				}
				if (!haveBlock)
					return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log().debug("Sending Stock Message");
		me.messageBlock = new MessageBlock(MessageXmlConstant.HEADER_AGENT_STOCK);
		me.messageBlock.addData(MessageXmlConstant.DATA_AREA_INDEX, me.me().getAreaPosition().getAreaIndex());
		me.messages.add(me.messageBlock);
		me.lowCommunicationMessages.add(me.messageBlock);
		me.sayMessages.add(me.messageBlock);
	}

	private void checkEarlyStock(Path path) throws SOSActionException {
		Area source = me.me().getAreaPosition();
		if (me.informationStacker.getInformations(1).getAct() instanceof MoveAction && me.informationStacker.getInformations(1).getPositionPair().first() instanceof Building) {
			log().debug("last act is move and current position is in building ==> don't check early stock");
			return;
		}
		if (me.informationStacker.getInformations(1).getAct() instanceof StockMoveAction) {
			log().debug("last act is stock move ==> don't check early stock");
			return;
		}

		if (source instanceof Building) {
			log().debug("early stock detected!!! agent is in " + source);
			ArrayList<EntityID> entityPath = new ArrayList<EntityID>();
			entityPath.add(source.getID());
			Area second = null;
			Area third = null;

			if (path.getIds().size() > 0 && !path.getIds().get(0).equals(source.getID())) {
				second = (Area) me.model().getEntity(path.getIds().get(0));
				if (path.getIds().size() > 1)
					third = (Area) me.model().getEntity(path.getIds().get(1));
			} else if (path.getIds().size() > 1 && path.getIds().get(0).equals(source.getID())) {
				second = (Area) me.model().getEntity(path.getIds().get(1));
				if (path.getIds().size() > 2)
					third = (Area) me.model().getEntity(path.getIds().get(2));
			} else {
				log().warn("move path size is 0 can't checkEarlyStock!!!");
				return;
			}
			if (second == null) {
				log().warn("second is null can't checkEarlyStock!!!");
				return;
			} else {
				ArrayList<Edge> edges = second.getEdgesTo(source);
				int minDistance = Integer.MAX_VALUE;
				for (Edge edge : edges) {
					minDistance = (int) Math.min(minDistance, SOSGeometryTools.distance(edge, me.me().getPositionPoint()));
				}
				//				Edge fedge = second.getEdgeTo(source);
				//				if (SOSGeometryTools.distance(fedge, me.me().getPositionPoint()) < MoveConstants.ENTRACE_DISTANCE_MM+1000)
				//					return;
				if (minDistance < MoveConstants.ENTRACE_DISTANCE_MM + 1000) {
					log().debug("I'm too neat to edge in building... no need to check early stock");
					return;
				}
				entityPath.add(second.getID());
			}
			if (third == null) {
				log().debug("Move path is too small!!! no need to early stock handle");
				return;
			} else {
				Edge ed = second.getEdgeTo(third);
				Point2D destXYAPointNearEdge;
				if (ed != null) {
					Line2D wallLine = ed.getLine();// new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());
					// ppp.add(new ShapeDebugFrame.Line2DShapeInfo(wallLine, "edge", Color.white.darker(), false, false));
					Vector2D offset;
					if (AliGeometryTools.havecorrectDirection(second)) {
						offset = wallLine.getDirection().getNormal().normalised().scale(15);
					} else {
						offset = wallLine.getDirection().getNormal().normalised().scale(-15);
					}
					destXYAPointNearEdge = ed.getMidPoint().plus(offset);
				} else {
					log().error("[Move]edge is null!!!!!some problem!!!!");
					destXYAPointNearEdge = second.getPositionPair().second();
				}
				log().debug("source:" + source + " dest area for stock:" + second + " dst point:" + destXYAPointNearEdge);
				me.send(new AKMove(me.getID(), me.time(), entityPath, (int) destXYAPointNearEdge.getX(), (int) destXYAPointNearEdge.getY()));
				path = new Path(null, null, entityPath, me.me().getPositionPair(), new Pair<Area, Point2D>(second, destXYAPointNearEdge), false);
				me.informationStacker.addInfo(me.model(), new StockMoveAction(path));
				throw new SOSActionException("Move Stock(" + source + "," + second + " " + destXYAPointNearEdge + ")");
			}

		}
	}

	private void check2CycleStock() throws SOSActionException {
		CycleInformations cycleinfo1 = me.informationStacker.getInformations(1);
		CycleInformations cycleinfo2 = me.informationStacker.getInformations(2);
		if (!(cycleinfo1.getAct() instanceof MoveAction) || cycleinfo1.getAct() instanceof StockMoveAction)
			return;
		if (!(cycleinfo2.getAct() instanceof MoveAction) || cycleinfo1.getAct() instanceof StockMoveAction)
			return;

		Path path1 = ((MoveAction) cycleinfo1.getAct()).getPath();
		//			Path path2 = ((MoveAction) cycleinfo2.getAct()).getPath();
		log().info("Two(2) cycle move ....");
		log().debug("current position:" + me.me().getAreaPosition());
		log().debug("1 cycle ago position:" + cycleinfo1.getPositionPair());
		log().debug("2 cycle ago position:" + cycleinfo2.getPositionPair());
		if (!cycleinfo1.getPositionPair().first().equals(cycleinfo2.getPositionPair().first()))
			return;
		if (!cycleinfo1.getPositionPair().first().equals(me.me().getAreaPosition()))
			return;
		if (SOSGeometryTools.getDistance(cycleinfo1.getPositionPair().second(), cycleinfo2.getPositionPair().second()) > MoveConstants.TRAFFIC_CHECKING_DISTANCE)
			return;

		////////////////Stock Occured
		log().debug("2 cycle stock occured!!!!!!!");

		ArrayList<EntityID> entityPath = new ArrayList<EntityID>();
		Area area = me.me().getAreaPosition();
		log().debug("Current Position:" + area);
		entityPath.add(area.getID());
		Edge ed;
		Area ne;
		if (path1.getIds().size() > 0 && !path1.getIds().get(0).equals(area.getID())) {
			ne = (Area) me.model().getEntity(path1.getIds().get(0));
			ed = area.getEdgeTo(ne);
		} else if (path1.getIds().size() > 1 && path1.getIds().get(0).equals(area.getID())) {
			ne = (Area) me.model().getEntity(path1.getIds().get(1));
			ed = area.getEdgeTo(ne);
		} else {
			ne = area.getNeighbours().get(0);
			ed = area.getEdgeTo(ne);
		}
		log().debug("ne:" + ne + " ed:" + ed + " last move path:" + path1);
		if (ed == null) {
			log().error("[Move]edge is null!!!!!some problem!!!!");
			ne = area.getNeighbours().get(0);
			ed = area.getEdgeTo(ne);
			log().debug("changed!!!! ne:" + ne + " ed:" + ed + " last move path:" + path1);
		}
		Point2D destXYAPointNearEdge;
		if (ed != null) {
			//			if (area instanceof Road) {
			Pair<Point2D, Point2D> points = getAPointInReachblePartEdges(area, ed);
			destXYAPointNearEdge = points.second();
			//			} else {
			//				Line2D wallLine = ed.getLine();// new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());
			//				// ppp.add(new ShapeDebugFrame.Line2DShapeInfo(wallLine, "edge", Color.white.darker(), false, false));
			//				Vector2D offset;
			//				if (lastStockTime < me.time() - 4) {
			//					if (AliGeometryTools.havecorrectDirection(area)) {
			//						offset = wallLine.getDirection().getNormal().normalised().scale(1500);
			//					} else {
			//						offset = wallLine.getDirection().getNormal().normalised().scale(-1500);
			//					}
			//				} else {
			//					if (!AliGeometryTools.havecorrectDirection(area)) {
			//						offset = wallLine.getDirection().getNormal().normalised().scale(1500);
			//					} else {
			//						offset = wallLine.getDirection().getNormal().normalised().scale(-1500);
			//					}
			//				}
			//				destXYAPointNearEdge = ed.getMidPoint().plus(offset);
			//			}
		} else {
			log().error("[Move]edge is null!!!!!some problem!!!!");
			destXYAPointNearEdge = area.getPositionPair().second();
		}
		log().debug("dest area for stock:" + area + " dst point:" + destXYAPointNearEdge);
		me.send(new AKMove(me.getID(), me.time(), entityPath, (int) destXYAPointNearEdge.getX(), (int) destXYAPointNearEdge.getY()));
		Path path = new Path(null, null, entityPath, me.me().getPositionPair(), new Pair<Area, Point2D>(area, destXYAPointNearEdge), false);
		me.informationStacker.addInfo(me.model(), new StockMoveAction(path));
		//		log().warn("Traffic Handeling should be change due to server doesn't support AKMotion");
		lastStockTime = me.time();
		throw new SOSActionException("Move Stock(" + area + " " + destXYAPointNearEdge + ")");
	}

	private Pair<Point2D, Point2D> getAPointInReachblePartEdges(Area area, Edge relatedEdge) {
		Point2D point = null;
		Edge rEdge = null;
		if (area instanceof Road) {
			Road road = (Road) area;
			FOR: for (SOSArea reachablePart : road.getReachableParts()) {
				for (Edge edge : reachablePart.getEdges()) {
					if (edge.getReachablityIndex() >= 0 && area.getEdges().get(edge.getReachablityIndex()).equals(relatedEdge)) {
						point = edge.getMidPoint();
						rEdge = edge;
						break FOR;
					}
				}
			}
		}
		if (point == null) {
			point = relatedEdge.getMidPoint();
			rEdge = relatedEdge;
		}
		ArrayList<Point2D> twoPointForCheckContain = Utility.get2PointsAroundAPointOutOfLine(rEdge.getStart(), rEdge.getEnd(), point, 10);
		ArrayList<Point2D> twoPoint = Utility.get2PointsAroundAPointOutOfLine(rEdge.getStart(), rEdge.getEnd(), point, 3000);
		if (area.getShape().contains(twoPointForCheckContain.get(0).toGeomPoint()))
			return new Pair<Point2D, Point2D>(twoPoint.get(0), twoPoint.get(1));
		else
			return new Pair<Point2D, Point2D>(twoPoint.get(1), twoPoint.get(0));

	}

	@SuppressWarnings("unused")
	private void check4CycleStock() throws SOSActionException {
		CycleInformations cycleinfo1 = me.informationStacker.getInformations(1);
		CycleInformations cycleinfo2 = me.informationStacker.getInformations(2);
		CycleInformations cycleinfo3 = me.informationStacker.getInformations(3);
		CycleInformations cycleinfo4 = me.informationStacker.getInformations(4);
		if (cycleinfo2.getAct() instanceof StockMoveAction)
			return;
		if (cycleinfo1.getAct() instanceof MoveAction && cycleinfo2.getAct() instanceof MoveAction && cycleinfo3.getAct() instanceof MoveAction && cycleinfo4.getAct() instanceof MoveAction) {
			//			Path path1 = ((MoveAction) cycleinfo1.getAct()).getPath();
			//			Path path2 = ((MoveAction) cycleinfo2.getAct()).getPath();
			//			Path path3 = ((MoveAction) cycleinfo3.getAct()).getPath();
			//			Path path4 = ((MoveAction) cycleinfo4.getAct()).getPath();
			log().debug("Four(4) cycle move ....");
			log().debug("1 cycle ago position:" + cycleinfo1.getPositionPair());
			log().debug("2 cycle ago position:" + cycleinfo2.getPositionPair());
			log().debug("3 cycle ago position:" + cycleinfo3.getPositionPair());
			log().debug("4 cycle ago position:" + cycleinfo4.getPositionPair());
			if (cycleinfo1.getPositionPair().first().equals(cycleinfo2.getPositionPair().first()) && cycleinfo1.getPositionPair().first().equals(cycleinfo3.getPositionPair().first()) && cycleinfo1.getPositionPair().first().equals(cycleinfo4.getPositionPair().first())) {
				if (SOSGeometryTools.getDistance(cycleinfo1.getPositionPair().second(), cycleinfo2.getPositionPair().second()) < MoveConstants.TRAFFIC_CHECKING_DISTANCE && SOSGeometryTools.getDistance(cycleinfo1.getPositionPair().second(), cycleinfo3.getPositionPair().second()) < MoveConstants.TRAFFIC_CHECKING_DISTANCE && SOSGeometryTools.getDistance(cycleinfo1.getPositionPair().second(), cycleinfo4.getPositionPair().second()) < MoveConstants.TRAFFIC_CHECKING_DISTANCE) {
					////////////////////DO TRAFFIC/////////////////////////////
					log().warn("4 cycle stock!--> do random walk");
					move(bfs.getDummyRandomWalkPath(), false);
				}
			}
		}

	}

	//**---------------------------------------------INTERFACE SEGMENT----------------------------------------------**//
	//********* STANDARD MOVE INTERFACE *********//
	public void moveStandard(Area destination) throws SOSActionException {
		move(destination, StandardMove.class);
	}

	public void moveStandard(Collection<? extends Area> destination) throws SOSActionException {
		move(destination, StandardMove.class);
	}

	public void moveStandardXY(Area destination, int x, int y) throws SOSActionException {
		moveXY(destination, x, y, StandardMove.class);
	}

	public void moveStandardXY(Collection<Pair<? extends Area, Point2D>> destinations) throws SOSActionException {
		moveXY(destinations, StandardMove.class);
	}

	//********* POLICE MOVE INTERFACE *********//
	public void movePolice(Area destination) throws SOSActionException {
		move(destination, PoliceMove.class);
	}

	public void movePolice(Collection<? extends Area> destination) throws SOSActionException {
		move(destination, PoliceMove.class);
	}

	public void movePoliceXY(Area destination, int x, int y) throws SOSActionException {
		moveXY(destination, x, y, PoliceMove.class);
	}

	public void movePoliceXY(Collection<Pair<? extends Area, Point2D>> destinations) throws SOSActionException {
		moveXY(destinations, PoliceMove.class);
	}

	public void moveToShape(Collection<ShapeInArea> des, Class<? extends MoveType> type) throws SOSActionException {

		if (!(me.me() instanceof Human)) {
			log().warn("can't use move in Center Agent " + me + "\n");
			return;
		}
		try {
			checkTraffic(); // TODO uncomment
			MoveType mt = moves.get(type.hashCode());
			if (mt != null) {
				Path path = mt.getPathToShape(des);
				log().debug("MOVE " + type.getSimpleName() + "\nTO : " + path + "\n");
				move(path);
			} else
				log().error(new Error("in move can not found type=" + type.getSimpleName()));

		} catch (SOSActionException e) {
			throw e;
		} catch (Exception er) {
			log().error(er);
			log().warn("using bfs for finding path");
			ArrayList<Area> goals = new ArrayList<Area>();

			for (ShapeInArea shapeInArea : des) {
				goals.add(shapeInArea.getArea(me.model()));
			}
			move(bfs.breadthFirstSearch((Area) me.location(), goals));
		}
		log().error("in move can not found type=" + type.getSimpleName());
		me.problemRest("in move can not found type=" + type.getSimpleName());
	}

	//********* GENERAL MOVE INTERFACE *********//
	public void move(Area destination, Class<? extends MoveType> type) throws SOSActionException {
		move(Collections.singleton(destination), type);
	}

	public void move(Collection<? extends Area> goals, Class<? extends MoveType> type) throws SOSActionException {
		if (!(me.me() instanceof Human)) {
			log().warn("can't use move in Center Agent " + me + "\n");
			return;
		}
		try {
			checkTraffic(); // TODO uncomment
			MoveType mt = moves.get(type.hashCode());

			if (mt != null) {
				Path path = mt.getPathTo(goals);
				log().debug("MOVE " + type.getSimpleName() + "\nTO : " + path + "\n");
				move(path);
			} else
				log().error(new Error("in move can not found type=" + type.getSimpleName()));
		} catch (SOSActionException ec) {
			throw ec;
		} catch (Exception er) {
			log().error(er);
			log().warn("using bfs for finding path");
			move(bfs.breadthFirstSearch((Area) me.location(), goals));
		}
		log().error("in move can not found type=" + type.getSimpleName());
		me.problemRest("in move can not found type=" + type.getSimpleName());
	}

	public void moveXY(Area destination, int x, int y, Class<? extends MoveType> type) throws SOSActionException {
		Pair<? extends Area, Point2D> pair = new Pair<Area, Point2D>(destination, new Point2D(x, y));
		ArrayList<Pair<? extends Area, Point2D>> arr = new ArrayList<Pair<? extends Area, Point2D>>();
		arr.add(pair);
		moveXY(arr, type);
	}

	public void moveXY(Collection<Pair<? extends Area, Point2D>> destinations, Class<? extends MoveType> type) throws SOSActionException {
		if (!(me.me() instanceof Human)) {
			log().warn("can't use move in Center Agent " + me + "\n");
			return;
		}
		try {
			checkTraffic();
			MoveType mt = moves.get(type.hashCode());
			if (mt != null) {
				Path path = mt.getPathToPoints(destinations);
				log().debug("MOVE XY" + type.getSimpleName() + "\nTO : " + path + "\n");
				move(path);
			} else
				log().error(new Error("in move can not found type=" + type.getSimpleName()));
		} catch (SOSActionException e) {
			throw e;
		} catch (Exception er) {
			log().error(er);
			log().warn("using bfs for finding path");
			move(bfs.breadthFirstSearchXY((Area) me.location(), destinations));
		}
		log().error("in move can not found type=" + type.getSimpleName());
		me.problemRest("in move can not found type=" + type.getSimpleName());
	}

	public void move(Path path, Class<? extends MoveType> type) throws SOSActionException {
		if (!(me.me() instanceof Human)) {
			log().warn("can't use move in Center Agent " + me + "\n");
			return;
		}
		MoveType mt = moves.get(type.hashCode());
		if (mt != null) {
			log().debug("MOVE " + type.getSimpleName() + "\nTO : " + path + "\n");
			move(path);
		} else
			log().error(new Error("in move can not found type=" + type.getSimpleName()));

		log().error("in move can not found type=" + type.getSimpleName());
		me.problemRest("in move can not found type=" + type.getSimpleName());
	}

	public void move(Path path) throws SOSActionException {
		move(path, true);
	}

	public void move(Path path, boolean checkStock) throws SOSActionException {
		if (!(me.me() instanceof Human)) {
			log().warn("can't use move in Center Agent " + me + "\n");
			return;
		}
		if (path == null) {
			log().error("move to null??....doing random walk");
			((PlatoonAgent<?>) me).randomWalk(true);
		}

		log().debug("MOVE TO : " + path + "\n");
		if (checkStock)
			checkStock(path);
		Point2D xy = path.getDestination().second();

		me.informationStacker.addInfo(me.model(), new MoveAction(path));
		me.send(new AKMove(me.getID(), me.model().time(), path.getIds(), (int) xy.getX(), (int) xy.getY()));
		throw new SOSActionException("MoveXY(" + path + ", " + xy + ")");
	}

	//*************************************** GET PATH *************************************************/
	public Path getPathTo(Collection<? extends Area> destinations, Class<? extends MoveType> type) {
		MoveType mt = moves.get(type.hashCode());
		if (mt != null) {
			Path path = mt.getPathTo(destinations);
			log().debug("Move.getPathTo " + type.getSimpleName() + "\nTO : " + path + "\n");
			return path;
		} else
			log().error(new Error("in Move.getPathTo can not found type=" + type.getSimpleName()));
		return null;
	}

	public Path getPathToPoints(Collection<Pair<? extends Area, Point2D>> destinations, Class<? extends MoveType> type) {
		MoveType mt = moves.get(type.hashCode());
		if (mt != null) {
			Path path = mt.getPathToPoints(destinations);
			log().debug("Move.getPathToPoints " + type.getSimpleName() + "\nTO : " + path + "\n");
			return path;
		} else
			log().error(new Error("in Move.getPathToPoints can not found type=" + type.getSimpleName()));
		return null;
	}

	public Path getPathFromTo(Area source, Area destination, Class<? extends MoveType> type) {
		return getPathFromTo(Collections.singleton(source), Collections.singleton(destination), type);
	}

	public Path getPathFromTo(Collection<? extends Area> sources, Collection<? extends Area> destinations, Class<? extends MoveType> type) {
		MoveType mt = moves.get(type.hashCode());
		if (mt != null) {
			Path path = mt.getPathFromTo(sources, destinations);
			log().debug("Move.getPathFromTo " + type.getSimpleName() + "\nTO : " + path + "\n");
			return path;
		} else
			log().error(new Error("in Move.getPathFromTo can not found type=" + type.getSimpleName()));
		return null;
	}

	public Path getPathFromPointsToPoints(Collection<Pair<? extends Area, Point2D>> sources, Collection<Pair<? extends Area, Point2D>> destinations, Class<? extends MoveType> type) {
		MoveType mt = moves.get(type.hashCode());
		if (mt != null) {
			Path path = mt.getPathFromPointsToPoints(sources, destinations);
			log().debug("Move.getPathFromPointsToPoints " + type.getSimpleName() + "\nTO : " + path + "\n");
			return path;
		} else
			log().error(new Error("in Move.getPathFromPointsToPoints can not found type=" + type.getSimpleName()));
		return null;
	}

	//*************************************** REACHABILITY  *************************************************/
	public boolean isReallyUnreachable(Collection<? extends Area> targets) {
		if (!(me.me() instanceof Human)) {
			log().warn("can't use isReallyUnreachableXY in Center Agent " + me + " " + Arrays.toString(new Error().getStackTrace()));
			return false;
		}
		StandardMove sm = (StandardMove) moves.get(StandardMove.class.hashCode());
		return sm.isReallyUnreachable(targets);
	}

	public boolean isReallyUnreachable(Area target) {
		return isReallyUnreachable(Collections.singletonList(target));
	}

	public boolean isReallyUnreachableXY(Collection<Pair<? extends Area, Point2D>> targets) {
		if (!(me.me() instanceof Human)) {
			log().warn("can't use isReallyUnreachableXY in Center Agent " + me + " " + Arrays.toString(new Error().getStackTrace()));
			return false;
		}
		StandardMove sm = (StandardMove) moves.get(StandardMove.class.hashCode());
		return sm.isReallyUnreachableXY(targets);
	}

	public boolean isReallyUnreachableXY(Area target, int x, int y) {
		Pair<? extends Area, Point2D> pair = new Pair<Area, Point2D>(target, new Point2D(x, y));
		ArrayList<Pair<? extends Area, Point2D>> arr = new ArrayList<Pair<? extends Area, Point2D>>();
		arr.add(pair);
		return isReallyUnreachableXY(arr);
	}

	public boolean isReallyUnreachableXY(Pair<? extends Area, Point2D> positionPair) {
		ArrayList<Pair<? extends Area, Point2D>> arr = new ArrayList<Pair<? extends Area, Point2D>>();
		arr.add(positionPair);
		return isReallyUnreachableXY(arr);
	}

	public boolean isReallyUnreachableXYPolice(Pair<? extends Area, Point2D> positionPair) {
		ArrayList<Pair<? extends Area, Point2D>> arr = new ArrayList<Pair<? extends Area, Point2D>>();
		arr.add(positionPair);
		return isReallyUnreachableXYPolice(arr);
	}

	public boolean isReallyUnreachableXYPolice(Collection<Pair<? extends Area, Point2D>> targets) {
		if (!(me.me() instanceof Human)) {
			log().warn("can't use isReallyUnreachableXY in Center Agent " + me + " " + Arrays.toString(new Error().getStackTrace()));
			return false;
		}
		PoliceReachablityMove pm = getMoveType(PoliceReachablityMove.class);
		return !pm.isReallyReachableXY(targets);
	}

	public boolean isReallyReacahble(Pair<? extends Area, Point2D> pair) {
		ArrayList<Pair<? extends Area, Point2D>> arr = new ArrayList<Pair<? extends Area, Point2D>>();
		arr.add(pair);
		return isReallyReacahble(arr);
	}

	public boolean isReallyReacahble(Collection<Pair<? extends Area, Point2D>> targets) {
		if (!(me.me() instanceof Human)) {
			log().warn("can't use isReallyUnreachableXY in Center Agent " + me + " " + Arrays.toString(new Error().getStackTrace()));
			return false;
		}
		PoliceReachablityMove pm = getMoveType(PoliceReachablityMove.class);
		return pm.isReallyReachableXY(targets);
	}

	//---------------------------------------------Default false Reachability---------------------------//
	//	public boolean isReachable(ArrayList<ShapeInArea> shapes) {
	//		return getWeightTo(shapes) < MoveConstants.UNREACHABLE_COST;
	//	}

	public boolean isReachable(Area a, Area b) {
		StandardMove sm = (StandardMove) moves.get(StandardMove.class.hashCode());
		return sm.isReachable(a, b, this.nodesUnion);
	}

	public boolean isReachable(Pair<? extends Area, Point2D> a, Pair<? extends Area, Point2D> b) {
		StandardMove sm = (StandardMove) moves.get(StandardMove.class.hashCode());
		return sm.isReachable(a, b, this.nodesUnion);
	}

	// **********************************************get Cost to targets*******************************/
	public long getWeightTo(ArrayList<ShapeInArea> targets, Class<? extends MoveType> type) {
		long weightTo = moves.get(type.hashCode()).getWeightTo(targets);
		if (weightTo < 0)
			log().error(new Error("why it become negetive here???"));
		return weightTo;
	}

	public long getWeightToLowProcess(List<ShapeInArea> targets, Class<? extends MoveType> type) {
		long min = Long.MAX_VALUE;
		MoveType movetype = moves.get(type.hashCode());
		for (ShapeInArea shapeInArea : targets) {
			if (shapeInArea.getArea(me.model()).equals(me.me().getAreaPosition())) {// source and destination is in same area
				if (shapeInArea.contains(((Human) me.me()).getX(), ((Human) me.me()).getY()))
				{
					log().debug("in shape--Yoosef");
					return 0;
				}
			}
			min = Math.min(min, movetype.getWeightToLowProcess(shapeInArea.getArea(me.model()), shapeInArea.getCenterX(), shapeInArea.getCenterY()));
		}
		if (min < 0)
			log().error(new Error("why it become negetive here???"));
		return min;
	}

	public long getWeightTo(Area target, int x, int y, Class<? extends MoveType> type) {
		long weightTo = moves.get(type.hashCode()).getWeightTo(target, x, y);
		if (weightTo < 0)
			log().error(new Error("why it become negetive here???"));
		return weightTo;
	}

	public long getWeightTo(Area target, Class<? extends MoveType> type) {
		long weightTo = moves.get(type.hashCode()).getWeightTo(target, target.getX(), target.getY());
		if (weightTo < 0)
			log().error(new Error("why it become negetive here???"));
		return weightTo;
	}

	// **********************************************get Cost to targets*******************************/
	public long[] getLenToTargets(Pair<? extends Area, Point2D> from, ArrayList<Pair<? extends Area, Point2D>> destinations) {
		StandardMove sm = (StandardMove) moves.get(StandardMove.class.hashCode());
		return sm.getLenToTargets(from, destinations);
	}

	public long[] getMMLenToTargets_notImportantPoint(Pair<? extends Area, Point2D> from, ArrayList<Pair<? extends Area, Point2D>> destinations) {
		StandardMove sm = (StandardMove) moves.get(StandardMove.class.hashCode());
		return sm.getMMLenToTargets_notImportantPoint(from, destinations);
	}

	// ****************************************get Reachable Areas from a source**********************/
	public Collection<? extends Area> getReachableAreasFrom(StandardEntity entity) {
		StandardMove sm = (StandardMove) moves.get(StandardMove.class.hashCode());
		return sm.getReachableAreasFrom(entity);
	}

	// ****************************************get Reachable Areas from a source**********************/
	public Collection<? extends Area> getFogyReachableAreasFrom(Pair<? extends Area, Point2D> from) {
		StandardMove sm = (StandardMove) moves.get(StandardMove.class.hashCode());
		return sm.getFogyReachableAreasFrom(from/* , this.nodesUnion */);
	}

	//*************************************** LOGGER *************************************************/
	private SOSLoggerSystem log() {
		return me.sosLogger.move;
	}

	//*************************************** ***** *************************************************/
	public BFS getBfs() {
		return bfs;
	}

	public UnionFind getNodesUnion() {
		return nodesUnion;
	}

	public Path getPathToShapes(Collection<ShapeInArea> des, Class<? extends MoveType> type) {
		try {
			MoveType mt = moves.get(type.hashCode());
			if (mt != null) {
				Path path = mt.getPathToShape(des);
				return path;

			} else {
				log().error(new Error("in move can not found type=" + type.getSimpleName()));
			}

		} catch (Exception er) {
			log().error(er);
			log().warn("using bfs for finding path");
			ArrayList<Area> goals = new ArrayList<Area>();

			for (ShapeInArea shapeInArea : des) {
				goals.add(shapeInArea.getArea(me.model()));
			}
			return getPathTo(goals, type);
		}
		return null;
	}

	public boolean isReallyUnreachable(ArrayList<ShapeInArea> targets) {
		return isReallyUnreachableShapes(targets);
	}

	public boolean isReallyUnreachableShapes(Collection<ShapeInArea> targets) {
		return moves.get(StandardMove.class.hashCode()).isReallyUnreachableTo(targets);
	}

	public boolean isReallyUnreachable(ShapeInArea sh) {
		if (sh.contains(me.me().getPositionPoint().toGeomPoint()))
			return false;
		long weightTo = moves.get(StandardMove.class.hashCode()).getWeightTo(sh);
		if (weightTo < 0)
			log().error(new Error("why it become negetive here???"));
		return weightTo > UNREACHABLE_COST;
	}

	@SuppressWarnings("unchecked")
	public <T extends MoveType> T getMoveType(Class<T> clazz) {
		return (T) moves.get(clazz.hashCode());
	}

	//********* MOVE SEND TO KERNEL INTERFACE *********//
	/*
	 * @SuppressWarnings("unused")
	 * private void move(List<EntityID> path, int destX, int destY) throws SOSActionException {
	 * if (path != null) {
	 * me.send(new AKMove(me.getID(), me.model().time(), path, destX, destY));
	 * } else
	 * log().error(new Error("Move to null??????"));
	 * throw new SOSActionException("MoveXY(" + path + ",x=" + destX + ",y=" + destY + ")");
	 * }
	 * private void move(List<EntityID> path, Point2D xy) throws SOSActionException {
	 * if (path != null) {
	 * me.send(new AKMove(me.getID(), me.model().time(), path, (int) xy.getX(), (int) xy.getY()));
	 * } else {
	 * log().error("Move to null??????");
	 * if (me instanceof PlatoonAgent<?>) {
	 * ((PlatoonAgent<?>) me).randomWalk();
	 * }
	 * }
	 * throw new SOSActionException("MoveXY(" + path + ", " + xy + ")");
	 * }
	 * @SuppressWarnings("unused")
	 * private void move(List<EntityID> path) throws SOSActionException {
	 * if (path != null) {
	 * me.send(new AKMove(me.getID(), me.model().time(), path));
	 * } else
	 * log().error(new Error("Move to null??????"));
	 * throw new SOSActionException("Move(" + path + ")");
	 * }
	 */

	//	public long getCostInMMToNodes(ArrayList<ShapeInArea> shapes,Class<? extends MoveType> type){
	//		return moves.get(type.hashCode()).getCostInMMToNodes(shapes);
	//	}
	public int getMovingTime(Path dst) {
		return getMovingTimeFromMM(dst.getLenght());
	}

	public int getMovingTimeFromMM(long lengthInMM) {
		return (int) ((double) lengthInMM / MoveConstants.AVERAGE_MOVE_PER_CYCLE + 0.5d);
	}

	public int getMovingTimeFrom(long length) {
		return getMovingTimeFromMM(length * MoveConstants.DIVISION_UNIT_FOR_GET);
	}
}
