package sos.police_v2.clearableBlockadeToReachable;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.move.Path;
import sos.base.sosFireZone.util.Utill;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame;
import sos.base.util.namayangar.misc.gui.ShapeDebugFrame.ShapeInfo;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.base.worldGraph.Node;
import sos.police_v2.PoliceForceAgent;

public class ModifiedPathToMov {

	protected final GeoPathReachablity reachableWithEdge;
	private SOSLoggerSystem log;
	private PoliceForceAgent agent;
//	private static ShapeDebugFrame debug = new ShapeDebugFrame();

	public ModifiedPathToMov(PoliceForceAgent policeForceAgent) {
		this.agent = policeForceAgent;
		reachableWithEdge = new GeoPathReachablity(policeForceAgent);
		log = new SOSLoggerSystem(policeForceAgent.me(), "MiladPath", false, OutputType.File, true, true);
		policeForceAgent.sosLogger.addToAllLogType(log);

	}

	public Point getPoint(Node node) {
		return node.getPosition().toGeomPoint();
	}

	private ArrayList<ShapeInfo> getPathShape(Path path, Color color) {
		if (path.getNodes() == null || path.getNodes().length < 1)
			return null;
		ArrayList<ShapeDebugFrame.ShapeInfo> shapes = new ArrayList<ShapeDebugFrame.ShapeInfo>();

		Node[] allMoveNodes = path.getNodes();
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(
				agent.getPositionPoint().toGeomPoint()
				, getPoint(allMoveNodes[0]))
				, "final", color, false, true));

		for (int i = 1; i < allMoveNodes.length; i++) {
			shapes.add(new ShapeDebugFrame.Line2DShapeInfo(
					new rescuecore2.geometry.Line2D(getPoint(allMoveNodes[i - 1]),
							getPoint(allMoveNodes[i])), "Move node " + i, color, false, true));
			if (i == 10)
				break;
		}
		shapes.add(new ShapeDebugFrame.Line2DShapeInfo(new rescuecore2.geometry.Line2D(
				getPoint(allMoveNodes[allMoveNodes.length - 1])
				, path.getDestination().second().toGeomPoint())
				, "final", color, false, true));
		return shapes;
	}

	public Path getModifiedPathToMove(Path path) {
		log.debug("varede Modifiedpath shod : ba path :" + path + "\n");
		Node[] allMoveNodes = path.getNodes();
		if (allMoveNodes == null) {
			log.debug("***************all move node hasj null bode : ********\n");
			return path;
		}
		log.debug("the path all move nodes size is    :   " + allMoveNodes.length + "\n");
		//		ArrayList<Blockade> blockadeEdge = reachableWithEdge.getBlockingBlockadeOfPath(path);
//		debug.setBackgroundEntities(Color.gray, agent.model().areas(), agent.model().blockades());

		Point lastp = agent.me().getPositionPoint().toGeomPoint();
		ArrayList<EntityID> ids = new ArrayList<EntityID>();
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < allMoveNodes.length; i++) {

			log.debug("varede FOR e asli shod :  ba index   :" + i + "\n");
			Area area = agent.model().areas().get(allMoveNodes[i].getAreaIndex());
			if (ids.isEmpty() || !ids.get(ids.size() - 1).equals(area.getID()))
				ids.add(area.getID());

			Point p2 = allMoveNodes[i].getPosition().toGeomPoint();
			if (!area.isBlockadesDefined())
				return path;
			if (agent.time() - area.getLastSenseTime() > 50)
				return path;

			if (area instanceof Road) {

				ArrayList<Blockade> blocks = new ArrayList<Blockade>(area.getBlockades());
				log.debug("blockade list for " + area + " is  :  " + blocks + "\n");
				Line2D l = new Line2D(lastp, p2);
				for (Blockade block : blocks) {
					log.debug("varede FOR2 dovomi shod baraye dadane noghte\n");
					Point2D bestp = getNearestPointThatHasIntersect(l, block);
					if (bestp != null) {
						log.info("***********************************" + bestp + "\n");
						Path newPath = new Path(null, nodes.isEmpty() ? null : nodes.toArray(new Node[0]), ids, agent.me().getPositionPair(), new Pair<Area, Point2D>(area, bestp), false);
						//						if(nodes.size()<3)
//						debug.show("Move", getPathShape(path, Color.yellow),
//								getPathShape(newPath, Color.BLUE),
//								ShapeDebugFrame.convertToShapeList(Color.black, blocks),
//								Arrays.asList(new ShapeDebugFrame.Point2DShapeInfo(bestp, "Best", Color.RED, true)));
						log.debug("PAth jadid is : " + newPath);
						return newPath;
					}

				}
			}
			//	if(path2 == null)
			lastp = p2;
			nodes.add(allMoveNodes[i]);
			//		if(path2 != null)
			//		break;
		}

		///////////////////////////
		Area area = path.getDestination().first();
		if (ids.isEmpty() || !ids.get(ids.size() - 1).equals(area.getID()))
			ids.add(area.getID());

		Point p2 = path.getDestination().second().toGeomPoint();
		if (!area.isBlockadesDefined())
			return path;
		if (agent.time() - area.getLastSenseTime() > 50)
			return path;

		if (area instanceof Road) {

			ArrayList<Blockade> blocks = new ArrayList<Blockade>(area.getBlockades());
			log.debug("blockade list for " + area + " is  :  " + blocks + "\n");
			Line2D l = new Line2D(lastp, p2);
			for (Blockade block : blocks) {
				log.debug("varede FOR2 dovomi shod baraye dadane noghte\n");
				Point2D bestp = getNearestPointThatHasIntersect(l, block);
				if (bestp != null) {
					log.info("***********************************" + bestp + "\n");
					Path newPath = new Path(null, null, ids, agent.me().getPositionPair(), new Pair<Area, Point2D>(area, bestp), false);
					log.debug("PAth jadid is : " + newPath);
					return newPath;
				}

			}
		}
		////////////////////////
		return path;

	}

	private Collection<Road> getCheckingRoads(Line2D l, Path path) {
		Collection<Road> roads = agent.model().getObjectsInRectangle(new Rectangle(l.getOrigin().getIntX(), l.getOrigin().getIntY(), l.getEndPoint().getIntX(), l.getEndPoint().getIntY()), Road.class);

		if (path.getSource().first() instanceof Road)
			roads.add((Road) path.getSource().first());
		if (path.getDestination().first() instanceof Road)
			roads.add((Road) path.getDestination().first());

		return roads;
	}

	private boolean haveIntersect(Line2D l, Blockade blockade) {
		int midx = (l.getOrigin().getIntX()+l.getEndPoint().getIntX())/2;
		int midy = (l.getOrigin().getIntY()+l.getEndPoint().getIntY())/2;
		
		log.debug("checking intersect with "+blockade);
		for (Edge e1 : blockade.getEdges()) {
			Point2D p1 = GeometryTools2D.getSegmentIntersectionPoint(l, e1.getLine());
//			if(blockade.getPositionID().equals(31109))
//			debug.show("point intersection", new ShapeDebugFrame.Line2DShapeInfo(l, "l", Color.WHITE, true, false),new ShapeDebugFrame.Line2DShapeInfo(e1.getLine(), "edge", Color.green, true, false),
//					new ShapeDebugFrame.Point2DShapeInfo(p1, "intersection", Color.pink, true)
//					);
//			
			if (p1 != null)
				return true;
			if(blockade.getShape().contains(midx,midy))
				return true;
		}
		return false;
	}

	private Point2D getNearestPointThatHasIntersect(Line2D l, Blockade blockade) {
		Point2D target = null;
		double minDist = Integer.MAX_VALUE;
		//		int x1 = l.getOrigin().getIntX();
		//		int y1 = l.getOrigin().getIntY();
		//		int x2 = l.getEndPoint().getIntX();
		//		int y2 = l.getEndPoint().getIntY();
		Point2D lastp = l.getOrigin();
		if (haveIntersect(l, blockade)) {
			target=l.getOrigin();
			for (Edge e : blockade.getExpandedBlock().getEdges()) {
				Point2D p = GeometryTools2D.getSegmentIntersectionPoint(l, e.getLine());
				if (p == null)
					continue;

				double dis = Point.distance(p.getX(), p.getY(), lastp.getX(), lastp.getY());
				if (dis < minDist) {
					target = p;
					minDist = dis;
				}
			}
		}
		log.debug("Target E ke getNearestPointThatHasIntersect barmigardone : " + target + "\n");
		return target;
	}

	private boolean haveIntersect(java.awt.geom.Line2D l, Blockade blockade) {
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