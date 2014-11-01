package sos.base;

import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import rescuecore2.misc.Pair;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;

/**
 * @author Ali
 * @param <E>
 */
public class SOSLineOfSightPerception<E extends StandardEntity> {
	private final SOSAgent<E> sosAgent;
	private static final IntersectionSorter INTERSECTION_SORTER = new IntersectionSorter();
	private int lastGetVisibleFromMeTime = -1;
	private Shape lastGetVisibleFromMeShape = null;

	public SOSLineOfSightPerception(SOSAgent<E> sosAgent) {
		this.sosAgent = sosAgent;
	}

	public Collection<StandardEntity> getVisibleToCenterEntities(StandardEntity from) {
		Pair<Integer, Integer> location = from.getLocation();
		if (location != null) {
			Point2D point = new Point2D(location.first(), location.second());
			Collection<StandardEntity> nearby = model().getObjectsInRange(location.first(), location.second(), sosAgent.VIEW_DISTANCE, StandardEntity.class);
			return findVisibleToCenter(from, point, nearby);
		}
		return new HashSet<StandardEntity>();

	}

	public Collection<StandardEntity> getVisibleEntities(StandardEntity from) {
		Pair<Integer, Integer> location = from.getLocation();
		if (location != null) {
			Point2D point = new Point2D(location.first(), location.second());
			Collection<StandardEntity> nearby = model().getObjectsInRange(location.first(), location.second(), sosAgent.VIEW_DISTANCE, StandardEntity.class);
			return findVisible(from, point, nearby);
		}
		return new HashSet<StandardEntity>();

	}


	public Collection<StandardEntity> getVisibleEntities(StandardEntity from, Collection<StandardEntity> nearby) {
		Pair<Integer, Integer> location = from.getLocation();
		if (location != null) {
			Point2D point = new Point2D(location.first(), location.second());
			return findVisible(from, point, nearby);
		}
		return new HashSet<StandardEntity>();
	}

	private Collection<StandardEntity> findVisible(StandardEntity agentEntity, Point2D location, Collection<StandardEntity> nearby) {
		Collection<LineInfo> lines = getAllLines(nearby);
		int rayCount = 72;
		double dAngle = Math.PI * 2 / rayCount;
		// CHECKSTYLE:ON:MagicNumber
		Collection<StandardEntity> result = new HashSet<StandardEntity>();
		for (int i = 0; i < rayCount; ++i) {
			double angle = i * dAngle;
			Vector2D vector = new Vector2D(Math.sin(angle), Math.cos(angle)).scale(sosAgent.VIEW_DISTANCE);
			Ray ray = new Ray(new Line2D(location, vector), lines);
			for (LineInfo hit : ray.getLinesHit()) {
				StandardEntity e = hit.getEntity();
				result.add(e);
			}
		}
		// Now look for humans
		for (StandardEntity next : nearby) {
			if (next instanceof Human) {
				Human h = (Human) next;
				if (canSee(agentEntity, location, h, lines)) {
					result.add(h);
				}
			}
		}
		// Add self
		result.add(agentEntity);
		return result;
	}

	public Shape getThisCycleVisibleShapeFromMe() {
		if (lastGetVisibleFromMeTime == sosAgent.time())
			return lastGetVisibleFromMeShape;
		lastGetVisibleFromMeTime = sosAgent.time();
		Collection<StandardEntity> nearby = model().getObjectsInRange(sosAgent.me().getLocation().first(), sosAgent.me().getLocation().second(), sosAgent.VIEW_DISTANCE, StandardEntity.class);
		return lastGetVisibleFromMeShape = findVisibleShape(sosAgent.me(), sosAgent.me().getPositionPair().second(), nearby,72*3);
	}

	public Shape findVisibleShape(StandardEntity agentEntity) {
		Collection<StandardEntity> nearby = model().getObjectsInRange(agentEntity.getLocation().first(), agentEntity.getLocation().second(), sosAgent.VIEW_DISTANCE, StandardEntity.class);
		return findVisibleShape(agentEntity, agentEntity.getPositionPair().second(), nearby);
	}

	private Shape findVisibleShape(StandardEntity agentEntity, Point2D location, Collection<StandardEntity> nearby) {
		return findVisibleShape(agentEntity, location, nearby,72);
	}
	private Shape findVisibleShape(StandardEntity agentEntity, Point2D location, Collection<StandardEntity> nearby,int rayCount) {
		Collection<LineInfo> lines = getAllLines(nearby);
		
		double dAngle = Math.PI * 2 / rayCount;
		// CHECKSTYLE:ON:MagicNumber
		int[] xs = new int[rayCount];
		int[] ys = new int[rayCount];
		for (int i = 0; i < rayCount; ++i) {
			double angle = i * dAngle;
			Vector2D vector = new Vector2D(Math.sin(angle), Math.cos(angle)).scale(sosAgent.VIEW_DISTANCE);
			Ray ray = new Ray(new Line2D(location, vector), lines);

			Point2D p = ray.getRay().getPoint(ray.getVisibleLength());
			xs[i] = p.getIntX();
			ys[i] = p.getIntY();

		}

		return new Polygon(xs, ys, rayCount);
	}

	private Collection<StandardEntity> findVisibleToCenter(StandardEntity agentEntity, Point2D location, Collection<StandardEntity> nearby) {

		Collection<LineInfo> lines = getAllLines(nearby);
		int rayCount = 72;
		double dAngle = Math.PI * 2 / rayCount;
		// CHECKSTYLE:ON:MagicNumber
		Collection<StandardEntity> result = new HashSet<StandardEntity>();
		for (int i = 0; i < rayCount; ++i) {
			double angle = i * dAngle;
			Vector2D vector = new Vector2D(Math.sin(angle), Math.cos(angle)).scale(sosAgent.VIEW_DISTANCE);
			Ray ray = new Ray(new Line2D(location, vector), lines);
			for (LineInfo hit : ray.getLinesHit()) {
				StandardEntity e = hit.getEntity();
				if (ray.getVisibleLength() > distance(e.getLocation().first(), e.getLocation().second(), (int) location.getX(), (int) location.getY()) || (agentEntity instanceof Human && ((Human) agentEntity).getPosition().equals(e)))// FIXME if be on a area it should be use
					result.add(e);
			}
		}
		// Now look for humans
		for (StandardEntity next : nearby) {
			if (next instanceof Human) {
				Human h = (Human) next;
				if (canSee(agentEntity, location, h, lines)) {
					result.add(h);
				}
			}
		}
		// Add self
		result.add(agentEntity);
		return result;
	}

	private boolean canSee(StandardEntity agent, Point2D location, Human h, Collection<LineInfo> lines) {
		if (h.isXDefined() && h.isYDefined()) {
			int x = h.getX();
			int y = h.getY();
			Point2D humanLocation = new Point2D(x, y);
			Ray ray = new Ray(new Line2D(location, humanLocation), lines);
			if (ray.getVisibleLength() >= 1) {
				return true;
			}
		} else if (h.isPositionDefined()) {
			if (h.getPosition().equals(agent)) {
				return true;
			}
			StandardEntity e = model().getEntity(h.getPositionID());
			if (e instanceof AmbulanceTeam) {
				return canSee(agent, location, (Human) e, lines);
			}
		}
		return false;
	}

	private Collection<LineInfo> getAllLines(Collection<StandardEntity> entities) {
		Collection<LineInfo> result = new HashSet<LineInfo>();
		for (StandardEntity next : entities) {
			if (next instanceof Building) {
				for (Edge edge : ((Building) next).getEdges()) {
					Line2D line = edge.getLine();
					result.add(new LineInfo(line, next, !edge.isPassable()));
				}
			}
			if (next instanceof Road) {
				for (Edge edge : ((Road) next).getEdges()) {
					Line2D line = edge.getLine();
					result.add(new LineInfo(line, next, false));
				}
			} else if (next instanceof Blockade) {
				int[] apexes = ((Blockade) next).getApexes();
				List<Point2D> points = GeometryTools2D.vertexArrayToPoints(apexes);
				List<Line2D> lines = GeometryTools2D.pointsToLines(points, true);
				for (Line2D line : lines) {
					result.add(new LineInfo(line, next, false));
				}
			} else {
				continue;
			}
		}
		return result;
	}

	public static class Ray {
		/** The ray itself. */
		private Line2D ray;
		/** The visible length of the ray. */
		private double length;
		/** List of lines hit in order. */
		private List<LineInfo> hit;

		public Ray(Line2D ray, Collection<LineInfo> otherLines) {
			this.ray = ray;
			List<Pair<LineInfo, Double>> intersections = new ArrayList<Pair<LineInfo, Double>>();
			// Find intersections with other lines
			for (LineInfo other : otherLines) {
				double d1 = ray.getIntersection(other.getLine());
				double d2 = other.getLine().getIntersection(ray);
				if (d2 >= 0 && d2 <= 1 && d1 > 0 && d1 <= 1) {
					intersections.add(new Pair<LineInfo, Double>(other, d1));
				}
			}
			Collections.sort(intersections, INTERSECTION_SORTER);
			hit = new ArrayList<LineInfo>();
			length = 1;
			for (Pair<LineInfo, Double> next : intersections) {
				LineInfo l = next.first();
				hit.add(l);
				if (l.isBlocking()) {
					length = next.second();
					break;
				}
			}
		}

		public Line2D getRay() {
			return ray;
		}

		public double getVisibleLength() {
			return length;
		}

		public List<LineInfo> getLinesHit() {
			return Collections.unmodifiableList(hit);
		}
	}

	private static class LineInfo {
		private Line2D line;
		private StandardEntity entity;
		private boolean blocking;

		public LineInfo(Line2D line, StandardEntity entity, boolean blocking) {
			this.line = line;
			this.entity = entity;
			this.blocking = blocking;
		}

		public Line2D getLine() {
			return line;
		}

		public StandardEntity getEntity() {
			return entity;
		}

		public boolean isBlocking() {
			return blocking;
		}
	}

	private static class IntersectionSorter implements Comparator<Pair<LineInfo, Double>>, java.io.Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Pair<LineInfo, Double> a, Pair<LineInfo, Double> b) {
			double d1 = a.second();
			double d2 = b.second();
			if (d1 < d2) {
				return -1;
			}
			if (d1 > d2) {
				return 1;
			}
			return 0;
		}
	}

	private SOSWorldModel model() {
		return sosAgent.model();
	}

	private int distance(int x1, int y1, int x2, int y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return (int) Math.hypot(dx, dy);
	}
}
