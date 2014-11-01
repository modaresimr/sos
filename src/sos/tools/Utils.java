package sos.tools;

import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Edge;
import sos.base.entities.StandardEntity;
import sos.base.entities.StandardWorldModel;
import sos.base.move.MoveConstants;
import sos.base.move.Path;
import sos.base.util.SOSGeometryTools;

public class Utils {
	/**
	 * @author salim
	 */
	public static int AGENT_RADIUS = 500;

	/**
	 * @author Salim
	 */
	public static int isInNeighbours(Area e1, Area e2) {
		for (int i = 0; i < e1.getNeighboursID().size(); i++) {
			if (e1.getNeighboursID().get(i).equals(e2.getID()))
				return i;
		}
		return -1;
	}

	/**
	 * Return Area which both input areas have in their neighbor lists.
	 * 
	 * @param a
	 * @param b
	 * @return ArrayList<Area>
	 * @author Salim
	 */
	public static ArrayList<Area> sameNeighbors(Area a, Area b) {
		ArrayList<Area> results = new ArrayList<Area>();
		for (Area a1 : a.getNeighbours()) {
			for (Area a2 : b.getNeighbours()) {
				if (a1.getID().equals(a2.getID()))
					results.add(a1);
			}
		}
		return results;
	}

	public static double distance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
		// implemented by salim
		return distance(p1.first(), p1.second(), p2.first(), p2.second());
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		// implemented by salim
		return Math.sqrt((Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));

	}

	public static double distance(int x1, int y1, int x2, int y2) {
		// implemented by salim
		return Math.sqrt((Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));

	}

	public static List<EntityID> getEntitiesOfProperty(List<EntityID> list, SOSWorldModel world, String property) {
		// implemented by salim
		List<EntityID> results = new ArrayList<EntityID>();
		for (EntityID id : list) {
			Entity e = world.getEntity(id);
			if (e == null)
				continue;
			if (e.getProperty(property) == null)
				continue;
			results.add(id);
		}
		return results;
	}

	public static int max(int x, int y) {
		return x >= y ? x : y;
	}

	public static int min(int x, int y) {
		return x <= y ? x : y;
	}

	public static ArrayList<EntityID> getEntitiesOFType(StandardWorldModel model, List<EntityID> list, String urn) {
		// implemented by salim
		ArrayList<EntityID> results = new ArrayList<EntityID>();
		for (EntityID id : list) {
			if (model.getEntity(id).getURN().equals(urn)) {
				results.add(id);
			}
		}
		return results;
	}

	/**
	 * @author Salim
	 */
	public static ArrayList<Edge> invert(List<Edge> list) {
		// implemented by salim
		ArrayList<Edge> results = new ArrayList<Edge>(list.size());
		for (int i = list.size() - 1; i > -1; i--) {
			results.add(list.get(i));
		}
		return results;

	}

	public static int roundToTop(float f) {
		if (f > (int) f)
			return (int) (f + 1);
		return (int) f;
	}

	/**
	 * move cost to target by Hesam
	 */
	public static int getSampleTimeToTarget(Path path) {
		if (path.getLenght() == 0)
			return 0;
		return (path.getLenght() / MoveConstants.AVERAGE_MOVE_PER_CYCLE) + 1;

	}

	/**
	 * @author reyhaneh
	 */
	public static StandardEntity getNearestEntity(ArrayList<? extends StandardEntity> entities, Point2D myPlace) {

		int minDistance = Integer.MAX_VALUE;
		StandardEntity nearestEntity = null;
		
		for (StandardEntity entity : entities) {
			int distance = SOSGeometryTools.distance(entity.getAreaPosition().getPositionPoint(), myPlace);
			if (nearestEntity == null) {
				nearestEntity = entity;
				minDistance = distance;
				continue;
			}
			if (distance < minDistance) {
				nearestEntity = entity;
				minDistance = distance;
			}

		}
		return nearestEntity;
	}

}
