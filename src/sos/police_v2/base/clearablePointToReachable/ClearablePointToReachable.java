package sos.police_v2.base.clearablePointToReachable;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.PoliceForce;
import sos.base.entities.Road;
import sos.base.move.Path;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.base.sosFireZone.util.Utill;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.base.worldGraph.Node;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

public abstract class ClearablePointToReachable {
	PoliceForceAgent agent;
	SOSLoggerSystem logger;

	public ClearablePointToReachable(PoliceForceAgent forceAgent) {
		agent = forceAgent;
		logger = new SOSLoggerSystem(forceAgent.me(), "newClear", false, OutputType.File, true);
		forceAgent.sosLogger.addToAllLogType(logger);
	}

	public abstract Point nextPointToClear(Path path, boolean checkReachablity, boolean doClear);

	protected boolean isBlockadeInClearArea(ArrayList<Blockade> blocklist, PoliceForce force, Point target, int clearRang, int clearWidth) {
		boolean result = false;
		java.awt.geom.Area clearArea = PoliceUtils.getClearArea(force, target.x, target.y, clearRang, clearWidth);
		for (Blockade block : blocklist) {
			java.awt.geom.Area select = new java.awt.geom.Area(block.getShape());
			select.intersect(clearArea);
			if (!select.isEmpty()) {
				logger.debug(block + "  has intersect with clear area ");
				result = true;
			}
		}
		return result;
	}

	protected Point changeTargetRang(int plusRang, Point target, Point me) {
		float zavie = (float) Math.atan2(target.getY() - me.getY(), target.getX() - me.getX());
		int rang = (int) (Point.distance(me.getX(), me.getY(), target.getX(), target.getY()) + plusRang);
		Point RangeTarget = new Point((int) (me.x + rang * Math.cos(zavie)), (int) (me.y + rang * Math.sin(zavie)));
		return RangeTarget;

	}

	private void addToBlockadeList(ArrayList<Blockade> blocklist, Collection<Road> roads, Line2D l) {
		for (Road road : roads)
			addToBlockadeList(blocklist, road, l);

	}

	private void addToBlockadeList(ArrayList<Blockade> blocklist, Area road, Line2D l) {
		logger.debug("line baraye intersect tolesh =" + Point.distance(l.getX1(), l.getY1(), l.getX2(), l.getY2()) + " ---- " + l.getP1() + " ---- " + l.getP2());
		if (!road.isBlockadesDefined())
			return;
		if (road.getLastSenseTime() < agent.time() - 1)
			return;
		logger.debug(road + " have this blockade => ");
		for (Blockade block : road.getBlockades()) {
			logger.debug(block + "-->" + " isvalid =" + PoliceUtils.isValid(block) + "    have Intersect =" + haveIntersect(l, block));
			if (PoliceUtils.isValid(block) && haveIntersect(l, block))
				if (!blocklist.contains(block))
					blocklist.add(block);
		}
	}

	protected boolean isAreaAllInRoads(java.awt.geom.Area area, Collection<Road> roads) {
		java.awt.geom.Area temp = new java.awt.geom.Area(area);
		for (Road road : roads) {
			temp.subtract(new java.awt.geom.Area(road.getShape()));
		}
		//		logger.warn(roads + "====" + temp.isEmpty());
		return temp.isEmpty();
	}

	private boolean haveIntersect(Line2D l, Blockade blockade) {
		if (blockade.getShape().contains(l.getP1()) || blockade.getShape().contains(l.getP2()))
			return true;
		for (Edge e : blockade.getExpandedBlock().getEdges()) {
			Point2D p = Utill.intersectLowProcess((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2(), e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY());
			if (p != null)
				return true;

			if (GeometryTools2D.getSegmentIntersectionPoint(e.getLine(), new rescuecore2.geometry.Line2D(l)) != null) {
				logger.warn("intersectLowProcess says have not intersect but GeometryTools2D says have intersect...");
				return true;
			}
		}
		return false;
	}

	protected void setBlockadeInLine(Point a, Point b, ArrayList<Blockade> blockades, Path path) {
		blockades.clear();
		Line2D l = new Line2D.Double(a, b);
		Collection<Road> roads = getCheckingRoads(l, path);
		logger.debug("Notting select for next point so for second point road in line(" + l + ") = " + roads);
		logger.debug("roads in line= " + roads);
		addToBlockadeList(blockades, roads, l);
	}

	protected void setBlockadeInWayArea(Point start, Point end, ArrayList<Blockade> blockades, Path path) {
		blockades.clear();
		java.awt.geom.Area moveArea = getMoveWayArea(start, end);
		Line2D l = new Line2D.Double(start, end);
		Collection<Road> roads = getCheckingRoads(l, path);
		addToBlockadeList(blockades, roads, moveArea);
	}

	private void addToBlockadeList(ArrayList<Blockade> blockades, Collection<Road> roads, java.awt.geom.Area moveArea) {
		for (Road road : roads)
			addToBlockadeList(blockades, road, moveArea);
	}

	private void addToBlockadeList(ArrayList<Blockade> blockades, Road road, java.awt.geom.Area moveArea) {
		logger.info("addToBlockadeList by area called");
		if (!road.isBlockadesDefined())
			return;
		if (road.getLastSenseTime() < agent.time() - 1)
			return;
		logger.debug(road + " have this blockade => ");
		for (Blockade block : road.getBlockades()) {
			logger.debug(block + "-->" + " isvalid =" + PoliceUtils.isValid(block) + "    have Intersect =" + haveIntersect(moveArea, block));
			if (PoliceUtils.isValid(block) && haveIntersect(moveArea, block))
				if (!blockades.contains(block))
					blockades.add(block);
		}
	}

	private boolean haveIntersect(java.awt.geom.Area moveArea, Blockade block) {
		java.awt.geom.Area temp = new java.awt.geom.Area(block.getShape());
		temp.intersect(moveArea);
		return (!temp.isEmpty());
	}

	protected Collection<Road> getCheckingRoads(Line2D l, Path path) {
		Collection<Road> roads = agent.model().getObjectsInRectangle(l.getBounds(), Road.class);

		if (path.getSource().first() instanceof Road)
			roads.add((Road) path.getSource().first());
		if (path.getDestination().first() instanceof Road)
			roads.add((Road) path.getDestination().first());

		return roads;
	}

	public static Point getPoint(Node node) {
		return node.getPosition().toGeomPoint();
	}

	protected Point getInRangPoint(Point me, Point target) {
		float zavie = (float) Math.atan2(target.getY() - me.getY(), target.getX() - me.getX());
		int rang = agent.clearDistance - agent.clearWidth;
		Point inRangeTarget = new Point((int) (me.x + rang * Math.cos(zavie)), (int) (me.y + rang * Math.sin(zavie)));
		return inRangeTarget;
	}

	protected Point getInRangPoint(Point me, Point target, int rang) {
		float zavie = (float) Math.atan2(target.getY() - me.getY(), target.getX() - me.getX());
		Point inRangeTarget = new Point((int) (me.x + rang * Math.cos(zavie)), (int) (me.y + rang * Math.sin(zavie)));
		return inRangeTarget;
	}

	protected Point getInRangPoint(Point me, Point target, int rang, int subtract) {
		float zavie = (float) Math.atan2(target.getY() - me.getY(), target.getX() - me.getX());
		Point inRangeTarget = new Point((int) (me.x + (rang - subtract) * Math.cos(zavie)), (int) (me.y + (rang - subtract) * Math.sin(zavie)));
		return inRangeTarget;
	}

	protected boolean isInClearRang(Point me, Point target, int rang) {
		if (Point.distance(me.getX(), me.getY(), target.getX(), target.getY()) < rang)
			return true;
		return false;
	}

	public static java.awt.geom.Area getMoveWayArea(Point t1, Point t2) {
		Vector2D startToEnd = new Vector2D(t1.getX() - t2.getX(), t1.getY() - t2.getY());
		rescuecore2.geometry.Line2D line = new rescuecore2.geometry.Line2D(new Point2D(t2.getX(), t2.getY()), startToEnd);
		Vector2D dir = startToEnd.normalised().scale(ReachablityConstants.AGENT_WIDTH / 2);
		Vector2D perpend1 = new Vector2D(-dir.getY(), dir.getX());
		Vector2D perpend2 = new Vector2D(dir.getY(), -dir.getX());
		Point2D points[] = new Point2D[] {
				line.getOrigin().plus(perpend1),
				line.getEndPoint().plus(perpend1),
				line.getEndPoint().plus(perpend2),
				line.getOrigin().plus(perpend2) };
		int[] xPoints = new int[points.length];
		int[] yPoints = new int[points.length];
		for (int i = 0; i < points.length; i++) {
			xPoints[i] = (int) points[i].getX();
			yPoints[i] = (int) points[i].getY();
		}
		return new java.awt.geom.Area(new Polygon(xPoints, yPoints, points.length));
	}

}
