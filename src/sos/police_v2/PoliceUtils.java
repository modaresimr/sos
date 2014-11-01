package sos.police_v2;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.entities.VirtualCivilian;
import sos.base.move.Path;
import sos.base.move.types.PoliceMove;
import sos.base.reachablity.PoliceReachablity;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.worldGraph.WorldGraphEdge;
import sos.search_v2.tools.cluster.ClusterData;

public class PoliceUtils {

	public static int getWeight(StandardEntity source, StandardEntity destination) {
		System.err.println("khoooob nist");
		Path path = source.getAgent().move.getPathFromTo(source.getAreaPosition(), destination.getAreaPosition(), PoliceMove.class);
		return path.getLenght();//getDistance(source, destination);
	}

	/**
	 * @author Ali
	 */
	public static ArrayList<Pair<? extends Area, Point2D>> getReachableAreasPair(StandardEntity entity) {
		if (entity == null) {
			System.err.println("The pased entity is null in PoliceUtils.getReachableAreas????WHY??");
			return new ArrayList<Pair<? extends Area, Point2D>>();
		}
		ArrayList<Pair<? extends Area, Point2D>> allAreas = new ArrayList<Pair<? extends Area, Point2D>>();
		allAreas.add(entity.getPositionPair());
		if (entity instanceof Human) {
			for (Edge ed : ((Human) entity).getImReachableToEdges()) {
				Area target = entity.model().areas().get(ed.getMyAreaIndex());
				allAreas.add(new Pair<Area, Point2D>(target, ed.getMidPoint()));
			}
		}

		//		Collection<? extends Area> reachableAreas = getReachableAreas(entity);
		//		if (reachableAreas.isEmpty())
		//			allAreas.add(entity.getPositionPair());
		//		else
		//			for (Area area : reachableAreas)
		//				allAreas.add(area.getPositionPair());

		return allAreas;
	}

	//	/**
	//	 * @author Ali
	//	 */
	//	public static Collection<? extends Area> getReachableAreas(StandardEntity entity) {
	//		Collection<? extends Area> reachables;
	//		if (entity == null) {
	//			System.err.println("The pased entity is null in PoliceUtils.getReachableAreas????WHY??");
	//			return new ArrayList<Area>();
	//		}
	//		SOSLoggerSystem log = entity.getAgent().sosLogger.agent;
	//		if (entity.getPositionPair()!=null) {
	//			reachables = entity.getAgent().move.getReachableAreasFrom(entity);
	//			//			setNumberOFReachableTask(reachables); FIXME
	//			return reachables;
	//		}else{
	//			log.error(entity + " has undefined position ");
	//			return null;
	//		}
	//	}

	/**
	 * @author Ali
	 */
	public static Collection<? extends Area> getFogyReachableAreas(StandardEntity entity) {
		Collection<? extends Area> reachables;
		if (entity == null) {
			System.err.println("The pased entity is null in PoliceUtils.getReachableAreas????WHY??");
			return new ArrayList<Area>();
		}
		SOSLoggerSystem log = entity.getAgent().sosLogger.agent;
		if (entity instanceof Area) {
			Area area = ((Area) entity);
			reachables = entity.getAgent().move.getFogyReachableAreasFrom(area.getPositionPair());
			//			setNumberOFReachableTask(reachables); FIXME
			return reachables;
		} else if (entity instanceof Human) {
			Pair<? extends Area, Point2D> pos = ((Human) entity).getPositionPair();
			if (pos != null) {
				reachables = entity.getAgent().move.getFogyReachableAreasFrom(pos);
				//				setNumberOFReachableTask(reachables); FIXME
				return reachables;
			}
			log.error(entity + " has undefined position ");
			return null;
		} else {
			log.error("Not Handled Yet for type(" + entity.getClass() + ")");
			return new ArrayList<Area>();
		}
	}

	/*
	 * private static void setNumberOFReachableTask(Collection<? extends Area> reachables) {
	 * for (Area area : reachables) {
	 * if(area.policeArea.lastCheckedTime!=area.model().time()){
	 * area.policeArea.lastCheckedTime=(short) area.model().time();
	 * area.policeArea.numberOfReachableTask=1;
	 * }else{
	 * area.policeArea.numberOfReachableTask++;
	 * }
	 * }
	 * }
	 */
	//	public static int getWeight(Task<?> source, Task<?> destination) {
	//		return getWeight(source.getAreaPosition(), destination.getAreaPosition());
	//	}

	public static int getDistance(StandardEntity source, StandardEntity goal) {
		return getDistance(source.getLocation().first(), source.getLocation().second(), goal.getLocation().first(), goal.getLocation().second());

	}

	public static int getDistance(Point2D source, Point2D destination) {
		return getDistance((int) source.getX(), (int) source.getY(), (int) destination.getX(), (int) destination.getY());
	}

	public static int getDistance(int x1, int y1, int x2, int y2) {

		//TODO Make it better
		double dx = x1 - x2;
		double dy = y1 - y2;
		return (int) (Math.sqrt(dx * dx + dy * dy) / PoliceConstants.DISTANCE_UNIT);
	}

	public ArrayList<WorldGraphEdge> aStarPathFinding(StandardEntity source, StandardEntity goal) {
		ArrayList<WorldGraphEdge> result = new ArrayList<WorldGraphEdge>();

		return result;
	}

	public int getHeuristic(StandardEntity source, StandardEntity goal) {
		return getDistance(source, goal);
	}

	public static boolean isValid(Blockade b) {
		if (b == null) {
			System.err.println("null blockade passed to isValid");
			return false;
		}
		SOSLoggerSystem log = b.getAgent().sosLogger.agent;
		PoliceForceAgent policeForceAgent = (PoliceForceAgent) b.getAgent();
		double blockadeDistance = getBlockadeDistance(b);
		// implemented by salim
		// if (Utils.distance(b.getLocation(), getLocation()) > clearDistance+)
		if (b.getLastSenseTime() < policeForceAgent.time() - 2) {
			log.debug(b + " is invalid! it is seen in " + b.getLastSenseTime());
			return false;
		}
		if (blockadeDistance >= policeForceAgent.clearDistance) {
			log.debug(b + "is invalid because clear distance :" + policeForceAgent.clearDistance + "  my distance=" + blockadeDistance);
			return false;
		}
		// --------------------------
		return true;
	}

	public static boolean isValidCivilian(Civilian civ, SOSAgent<?> owner, boolean checkReachablity) {
		if (owner == null) {
			System.err.println("Owner is null!!!! why?????????");
		}
		SOSLoggerSystem log = owner.sosLogger.agent;
		if (civ == null) {
			log.debug(" civ is not valid beacuse it is null");
			return false;
		}
		if (civ.isUnkonwnCivilian()) {
			log.debug(civ + " is not valid beacuse it is UnknownCivilian");
			return false;
		}
		if (!civ.isPositionDefined()) {
			log.debug(civ + " is not valid beacuse it hasn't a defined position");
			return false;
		}
		if (!(civ.getAreaPosition() instanceof Building)) {
			log.debug(civ + " is not valid beacuse it isn't in building");
			return false;

		}

		if ((civ.getAreaPosition() instanceof Refuge)) {
			log.debug(civ + " is not valid beacuse it isn't in building");
			return false;
		}

		if (civ.isReallyReachable(false) || (checkReachablity && !owner.move.isReallyUnreachableXYPolice(civ.getPositionPair()))) {
			log.debug(civ + " is not valid beacuse it is Reacable...");
			return false;
		} else if (!checkReachablity)
			log.debug("Reachablity not checked!");

		if (civ.getHP() == 0) {
			log.debug(civ + " is not valid beacuse it is dead...");
			return false;
		}
		if (civ.getDamage() > 200) {
			log.debug(civ + " is not valid beacuse it's damage is bigger that 200...");
			return false;
		}
		if (civ.getRescueInfo().getDeathTime() - owner.model().time() < civ.getBuriedness() / 3 + 5) {
			int dieCycle = civ.getRescueInfo().getDeathTime();
			log.debug(civ + " is not valid beacuse it will die soon...dieCycle= " + dieCycle);
			return false;
		}

		log.debug(civ + " is valid!");
		return true;
	}

	/**
	 * This method added by Hesam akbary
	 */
	public static boolean isValidCivilianWithOutDeadTimeCheck(Civilian civ, SOSAgent<?> owner, boolean checkReachablity) {
		if (owner == null) {
			System.err.println("Owner is null!!!! why?????????");
		}
		SOSLoggerSystem log = owner.sosLogger.agent;
		if (civ == null) {
			log.debug(" civ is not valid beacuse it is null");
			return false;
		}
		if (civ.isUnkonwnCivilian()) {
			log.debug(civ + " is not valid beacuse it is UnknownCivilian");
			return false;
		}
		if (!civ.isPositionDefined()) {
			log.debug(civ + " is not valid beacuse it hasn't a defined position");
			return false;
		}
		if (!(civ.getAreaPosition() instanceof Building)) {
			log.debug(civ + " is not valid beacuse it isn't in building");
			return false;

		}

		if ((civ.getAreaPosition() instanceof Refuge)) {
			log.debug(civ + " is not valid beacuse it isn't in building");
			return false;
		}

		if (civ.isReallyReachable(false) || (checkReachablity && !owner.move.isReallyUnreachableXYPolice(civ.getPositionPair()))) {
			log.debug(civ + " is not valid beacuse it is Reacable...");
			return false;
		} else if (!checkReachablity)
			log.debug("Reachablity not checked!");

		if (civ.getHP() == 0) {
			log.debug(civ + " is not valid beacuse it is dead...");
			return false;
		}
		if (civ.getDamage() > 200) {
			log.debug(civ + " is not valid beacuse it's damage is bigger that 200...");
			return false;
		}
		if (civ.getRescueInfo().getDeathTime() + 20 < civ.getAgent().model().time()) {
			log.debug(civ + " is not because its must die 20 cycle before");
			return false;
		}

		log.debug(civ + " is valid!");
		return true;
	}

	/**
	 * This method added by Hesam akbary
	 */
	public static boolean isValidHealtyCivilian(Civilian civ, PoliceForceAgent owner) {
		if (owner == null) {
			System.err.println("Owner is null!!!! why?????????");
		}
		SOSLoggerSystem log = owner.log;
		if (civ == null) {
			log.debug("NULL CIVILIAN CALLED TO CHECK ");
			return false;
		}
		if (civ.isUnkonwnCivilian()) {
			log.debug(civ + " is not valid beacuse it is UnknownCivilian");
			return false;
		}
		if (!civ.isPositionDefined()) {
			log.debug(civ + " is not valid beacuse it hasn't a defined position");
			return false;
		}
		if (civ.isReallyReachable(false) || (owner.isReachableTo(civ.getPositionPair()))) {
			log.debug(civ + " is not valid beacuse it is Reacable...");
			return false;
		}
		if (civ.getDamage() != 0) {
			log.debug(civ + " is not valid because got damage");
			return false;
		}
		if (civ.getBuriedness() != 0) {
			log.debug(civ + " is not valid because got buriedness");
			return false;
		}
		if (!(civ.getAreaPosition() instanceof Building)) {
			log.debug(civ + "is not in building now");
			return false;
		}

		log.debug(civ + " is valid Healty !");
		return true;
	}

	public static double getBlockadeDistance(Blockade b) {
		if (b == null) {
			System.err.println("passed null blockade to getBlockadeDistance");
			return Double.MAX_VALUE;
		}
		PoliceForceAgent policeForceAgent = (PoliceForceAgent) b.getAgent();
		Point2D agentLocation = policeForceAgent.getPositionPoint();

		double bestDistance = Double.MAX_VALUE;
		List<Line2D> blockadelines = GeometryTools2D.pointsToLines(GeometryTools2D.vertexArrayToPoints(b.getApexes()), true);
		for (Line2D line : blockadelines) {
			Point2D closest = GeometryTools2D.getClosestPointOnSegment(line, agentLocation);
			double distance = GeometryTools2D.getDistance(agentLocation, closest);
			if (bestDistance > distance) {
				bestDistance = distance;
			}
		}

		//		return SOSGeometryTools.distance(b.getEdges(), policeForceAgent.me().getPositionPoint());
		return bestDistance;

	}

	public static int getDistance(Pair<Integer, Integer> location, Pair<Integer, Integer> location2) {

		return getDistance(location.first(), location.second(), location2.first(), location2.second());
	}

	public static ArrayList<Blockade> getClearableBlockades(Road blockArea, Point2D point1, Point2D point2) {
		SOSLoggerSystem log = blockArea.getAgent().sosLogger.agent;
		log.debug("getClearableBlockades(Road " + blockArea + ", Point2D" + point1 + ", Point2D " + point2);
		ArrayList<Blockade> clearableBlockades = PoliceReachablity.clearableBlockades(blockArea, point1, point2);
		if (clearableBlockades.isEmpty() && Reachablity.isReachable(blockArea, point1, point2) == ReachablityState.Close) {
			blockArea.getAgent().sosLogger.warn("there is an error in communication between police reachablity and reachablity!!! it should bo solve!!!now using police another reachablity util!!! ");
			Edge clearAreaEdge = new Edge(point1, point2);
			if (blockArea.isBlockadesDefined() && blockArea.getLastSenseTime() > blockArea.model().time() - 2) {
				for (Blockade blockade : blockArea.getBlockades()) {
					if (haveIntersect(blockade.getExpandedBlock(), clearAreaEdge)) {
						clearableBlockades.add(blockade);
					}
				}
			}
		}
		return clearableBlockades;
	}

	/*
	 * public static ArrayList<Blockade> getClearableBlockades(Road blockArea, Point2D point, Edge edge) {
	 * SOSLoggerSystem log = blockArea.getAgent().sosLogger.agent;
	 * log.debug("getClearableBlockades(Road "+blockArea+", Point2D"+point+", EDGE "+edge);
	 * ArrayList<Blockade> clearableBlockades = PoliceReachablity.clearableBlockades(blockArea, point, edge);
	 * if (clearableBlockades.isEmpty() && Reachablity.isReachable(blockArea, point, edge) == ReachablityState.Close) {
	 * blockArea.getAgent().sosLogger.warn("there is an error in communication between police reachablity and reachablity!!! it should bo solve!!!now using police another reachablity util!!! ");
	 * Edge clearAreaEdge = new Edge(point, edge.getMidPoint());
	 * if (blockArea.isBlockadesDefined() && blockArea.getLastSenseTime() > blockArea.model().time() - 2) {
	 * for (Blockade blockade : blockArea.getBlockades()) {
	 * if (haveIntersect(blockade.getExpandedBlock(), clearAreaEdge)) {
	 * clearableBlockades.add(blockade);
	 * }
	 * }
	 * }
	 * }
	 * return clearableBlockades;
	 * }
	 * public static ArrayList<Blockade> getClearableBlockades(Road blockArea, Edge edge, Point2D point) {
	 * SOSLoggerSystem log = blockArea.getAgent().sosLogger.agent;
	 * log.debug("getClearableBlockades(Road "+blockArea+", Edge"+edge+", Point "+point);
	 * ArrayList<Blockade> clearableBlockades = PoliceReachablity.clearableBlockades(blockArea, edge, point);
	 * if (clearableBlockades.isEmpty() && Reachablity.isReachable(blockArea, point, edge) == ReachablityState.Close) {
	 * blockArea.getAgent().sosLogger.warn("there is an error in communication between police reachablity and reachablity!!! it should bo solve!!!now using police another reachablity util!!! ");
	 * Edge clearAreaEdge = new Edge(point, edge.getMidPoint());
	 * if (blockArea.isBlockadesDefined() && blockArea.getLastSenseTime() > blockArea.model().time() - 2) {
	 * for (Blockade blockade : blockArea.getBlockades()) {
	 * if (haveIntersect(blockade.getExpandedBlock(), clearAreaEdge)) {
	 * clearableBlockades.add(blockade);
	 * }
	 * }
	 * }
	 * }
	 * return clearableBlockades;
	 * }
	 * public static ArrayList<Blockade> getClearableBlockades(Road blockArea, GraphEdge blockEdge) {
	 * Edge e1 = blockArea.model().nodes().get(blockEdge.getHeadIndex()).getRelatedEdge();
	 * Edge e2 = blockArea.model().nodes().get(blockEdge.getTailIndex()).getRelatedEdge();
	 * SOSLoggerSystem log = blockArea.getAgent().sosLogger.agent;
	 * log.debug("getClearableBlockades(Road "+blockArea+", Edge"+e1+", edge "+e2);
	 * ArrayList<Blockade> clearableBlockades = PoliceReachablity.clearableBlockades(blockArea, e1, e2);
	 * if (clearableBlockades.isEmpty() && (Reachablity.isReachable(blockArea, e1, e2) == ReachablityState.Close || blockEdge.getState() == GraphEdgeState.Block)) {
	 * blockArea.getAgent().sosLogger.warn("there is an error in communication between police reachablity and reachablity!!! it should bo solve!!!now using police another reachablity util!!! ");
	 * Edge clearAreaEdge = new Edge(e1.getMidPoint(), e2.getMidPoint());
	 * if (blockArea.isBlockadesDefined() && blockArea.getLastSenseTime() > blockArea.model().time() - 2) {
	 * for (Blockade blockade : blockArea.getBlockades()) {
	 * if (haveIntersect(blockade.getExpandedBlock(), clearAreaEdge)) {
	 * blockArea.getAgent().sosLogger.agent.debug("the block edge "+ blockEdge+" has intersect with "+blockade);
	 * clearableBlockades.add(blockade);
	 * }
	 * }
	 * }
	 * }
	 * return clearableBlockades;
	 * }
	 */
	private static boolean haveIntersect(SOSArea expandedBlock, Edge clearAreaEdge) {
		java.awt.geom.Area area = new java.awt.geom.Area(expandedBlock.getShape());
		area.intersect(new java.awt.geom.Area(clearAreaEdge.getShape()));
		boolean haveIntersectGeom = !area.isEmpty();
		boolean haveIntersectotherReachablity = false;
		for (Edge e : expandedBlock.getEdges()) {
			if (Utility.getIntersect(e, clearAreaEdge) != null) {
				haveIntersectotherReachablity = true;
				break;
			}
		}
		if (haveIntersectotherReachablity != haveIntersectGeom)
			System.err.println("IntersectotherReachablity:" + haveIntersectotherReachablity + " but IntersectGeom:" + haveIntersectGeom);
		return haveIntersectotherReachablity;
	}

	public static boolean isEntrance(Area area) {
		if (area instanceof Building)
			return false;
		for (Area nei : area.getNeighbours()) {
			if (nei instanceof Building)
				return true;
		}
		return false;
	}

	/**
	 * Added By Hesam Akbary
	 */

	public static boolean isValidInRoadCivilian(Civilian civ, PoliceForceAgent owner) {
		if (owner == null) {
			System.err.println("Owner is null!!!! why?????????");
		}
		SOSLoggerSystem log = owner.log;
		if (civ == null) {
			log.debug("NULL CIVILIAN CALLED TO CHECK ");
			return false;
		}
		if (civ.isUnkonwnCivilian()) {
			log.debug(civ + " is not valid beacuse it is UnknownCivilian");
			return false;
		}
		if (!civ.isPositionDefined()) {
			log.debug(civ + " is not valid beacuse it hasn't a defined position");
			return false;
		}
		if (civ.getDamage() != 0) {
			log.debug(civ + " is not valid because got damage");
			return false;
		}
		if (civ.getBuriedness() != 0) {
			log.debug(civ + " is not valid because got buriedness");
			return false;
		}
		if (!(civ.getAreaPosition() instanceof Road)) {
			log.debug(civ + "is not in Road now");
			return false;
		}

		log.debug(civ + " is valid Healty In Road !");
		return true;
	}

	/**
	 * Added by Hesam Akbary
	 */
	public static ArrayList<ClusterData> getNeighberCluster(SOSWorldModel model, ClusterData select) {
		//		System.err.println(" getNeighberCluster TO POLICE UTILS CHECK Nashode hanoz ");
		ArrayList<ClusterData> result = new ArrayList<ClusterData>();
		for (ClusterData cluster : model.searchWorldModel.getAllClusters()) {
			if (cluster.equals(select))
				continue;
			if (cluster.isCoverer())
				continue;
			if (isNeighberCluster(select, cluster))
				result.add(cluster);
		}
		return result;
	}

	/**
	 * Added by Hesam Akbary
	 */
	private static boolean isNeighberCluster(ClusterData select, ClusterData cluster) {
		long distance = (long) Point.distance(select.getX(), select.getY(), cluster.getX(), cluster.getY());
		Rectangle rect1 = select.getBounds();
		long d1 = (long) Point.distance(rect1.getWidth(), rect1.getHeight(), 0, 0);
		d1 = d1 / 2;
		Rectangle rect2 = cluster.getBounds();
		long d2 = (long) Point.distance(rect2.getWidth(), rect2.getHeight(), 0, 0);
		d2 = d2 / 2;
		return distance <= (d1 + d2) * 0.85f;
	}

	/**
	 * Added by Hesam Akbary
	 */
	public static java.awt.geom.Area getClearArea(Human agent, int targetX, int targetY,
			int clearLength, int clearRad) {
		clearLength = clearLength - 200;
		clearRad = clearRad - 200;
		Vector2D agentToTarget = new Vector2D(targetX - agent.getX(), targetY
				- agent.getY());

		if (agentToTarget.getLength() > clearLength)
			agentToTarget = agentToTarget.normalised().scale(clearLength);

		Vector2D backAgent = (new Vector2D(agent.getX(), agent.getY()))
				.add(agentToTarget.normalised().scale(-450));
		Line2D line = new Line2D(backAgent.getX(), backAgent.getY(),
				agentToTarget.getX(), agentToTarget.getY());

		Vector2D dir = agentToTarget.normalised().scale(clearRad);
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

	public static java.awt.geom.Area getClearAreaByPoint(Point agent, int targetX, int targetY,
			int clearLength, int clearRad) {
		Vector2D agentToTarget = new Vector2D(targetX - agent.getX(), targetY
				- agent.getY());

		if (agentToTarget.getLength() > clearLength)
			agentToTarget = agentToTarget.normalised().scale(clearLength);

		Vector2D backAgent = (new Vector2D(agent.getX(), agent.getY()))
				.add(agentToTarget.normalised().scale(-450));
		Line2D line = new Line2D(backAgent.getX(), backAgent.getY(),
				agentToTarget.getX(), agentToTarget.getY());

		Vector2D dir = agentToTarget.normalised().scale(clearRad);
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

	public static double surface(java.awt.geom.Area area) {
		PathIterator iter = area.getPathIterator(null);

		double sum_all = 0;
		while (!iter.isDone()) {
			List<double[]> points = new ArrayList<double[]>();
			while (!iter.isDone()) {
				double point[] = new double[2];
				int type = iter.currentSegment(point);
				iter.next();
				if (type == PathIterator.SEG_CLOSE) {
					if (points.size() > 0)
						points.add(points.get(0));
					break;
				}
				points.add(point);
			}

			double sum = 0;
			for (int i = 0; i < points.size() - 1; i++)
				sum += points.get(i)[0] * points.get(i + 1)[1]
						- points.get(i)[1] * points.get(i + 1)[0];

			sum_all += Math.abs(sum) / 2;
		}

		return sum_all;
	}

	public static boolean isValidVirtualCivilian(VirtualCivilian target, PoliceForceAgent agent, boolean b) {
		SOSLoggerSystem log = agent.log;
		if (target == null) {
			log.info("vir civilian is null why? ");
			return false;
		}
		if (target.getPosition() == null) {
			log.info(target+"vir civilian position is null why ? ");
			return false;
		}
		if (b)
			if (target.isReallyReachable() || agent.isReachableTo(target.getPosition().getPositionPair())) {
				log.info(target+"vir civilian is not valid because it is reachable");
				return false;
			}
		if (target.getDeathTime() < agent.model().time()) {
			log.info(target+"vir civilian is not valid because it must die now");
			return false;
		}
		if (target.getDeathTime() - agent.model().time() < (target.getBuridness() / 3)) {
			log.info(target+"vir civilian is not valid because not have engoh time it will die soon");
			return false;
		}
		if (target.getPosition() instanceof Refuge) {
			log.info(target+"vir civilian is not valid because it is in refuge");
			return false;
		}
		if (!(target.getPosition() instanceof Building)) {
			log.info(target+"vir civilian is not valid because it is noy in building ");
			return false;
		}
		return true;
	}
}
