package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.entities.StandardEntity;
import sos.base.entities.StandardWorldModel;
import sos.base.move.Path;

/**
 * SOS search class.
 */
public final class SampleBFS {
	private static final int RANDOM_WALK_LENGTH = 30;// Ali
	private final StandardWorldModel model;

	/**
	 * Construct a new SOSSearch.
	 * The world model to search.
	 */
	public SampleBFS(StandardWorldModel standardWorldModel) {
		this.model = standardWorldModel;
	}

	/**
	 * Do a breadth first search from one location to the closest (in terms of
	 * number of nodes) of a set of goals.
	 * 
	 * @param start
	 *            The location we start at.
	 * @param goals
	 *            The set of possible goals.
	 * @return The path from start to one of the goals, or null if no path can
	 *         be found.
	 */
	public Path breadthFirstSearch(Area start, Area... goals) {
		return breadthFirstSearch(start, Arrays.asList(goals));
	}

	public Path breadthFirstSearchXY(Area start, Collection<Pair<? extends Area, Point2D>> dests) {
		LinkedList<Area> list = new LinkedList<Area>();
		for (Pair<? extends Area, Point2D> p : dests)
			list.add(p.first());
		return breadthFirstSearch(start, list);
	}

	/**
	 * Do a breadth first search from one location to the closest (in terms of
	 * number of nodes) of a set of goals.
	 * 
	 * @param start
	 *            The location we start at.
	 * @param goals
	 *            The set of possible goals.
	 * @return The path from start to one of the goals, or null if no path can
	 *         be found.
	 */
	public Path breadthFirstSearch(Area start, Collection<? extends Area> goals) {
		LinkedList<Area> open = new LinkedList<Area>();
		Map<Area, Area> ancestors = new HashMap<Area, Area>();
		open.add(start);
		Area next = null;
		boolean found = false;
		ancestors.put(start, null);
		do {
			next = open.remove(0);
			List<Area> neighbours = next.getNeighbours();
			if (neighbours.isEmpty()) {
				continue;
			}
			for (Area neighbour : neighbours) {
				if (isGoal(neighbour, goals)) {
					ancestors.put(neighbour, next);
					next = neighbour;
					found = true;
					break;
				} else {
					if (!ancestors.containsKey(neighbour) /*
														   * && !(neighbour
														   * instanceof Building)
														   */) {
						open.add(neighbour);
						ancestors.put(neighbour, next);
					}
				}
			}
		} while (!found && !open.isEmpty());
		if (!found) {
			// No path
			System.err.println("BFS Can not find any path---> dummyRandomWalking...");
			return getDummyRandomWalkPath(start);
//			return null;
		}
		// Walk back from goal to start
		Area current = next;
		ArrayList<EntityID> path = new ArrayList<EntityID>();
		// Logger.debug("Building path");
		// Logger.debug("Goal found: " + current);
		do {
			path.add(0, current.getID());
			current = ancestors.get(current);
			// Logger.debug("Parent node: " + current);
			if (current == null) {
				throw new RuntimeException("Found a node with no ancestor! Something is broken.");
			}
		} while (current != start);
		// Logger.debug("Final path: " + path);
		// int indexOfMe = path.lastIndexOf(start.getID());
		// if (indexOfMe > 0) {
		// System.out.println("old " + path + "ssss" + start);
		// path = path.subList(0, indexOfMe);
		// // System.out.println("new " + path);
		// }

		return new Path(null, null, path, model.getEntity(path.get(0)).getPositionPair(), model.getEntity(path.get(path.size() - 1)).getPositionPair(), false);
	}

	/**
	 * Get the neighbours of an entity.
	 * 
	 * @param e
	 *            The entity to look up.
	 * @return All neighbours of that entity.
	 */
	public Collection<StandardEntity> findNeighbours(StandardEntity e) {
		Collection<StandardEntity> result = new ArrayList<StandardEntity>();
		if (e instanceof Area) {
			Area a = (Area) e;
			for (Area next : a.getNeighbours()) {
				result.add(next);
			}
		}
		return result;
	}

	private boolean isGoal(Area e, Collection<? extends Area> test) {
		for (Area next : test) {
			if (next.getID().equals(e.getID())) {
				return true;
			}
		}
		return false;
	}

	public Path getDummyRandomWalkPath(Area locationFrom) {
		ArrayList<EntityID> result = new ArrayList<EntityID>(RANDOM_WALK_LENGTH);
		Set<StandardEntity> seen = new HashSet<StandardEntity>();
		Area current = locationFrom;
		for (int i = 0; i < RANDOM_WALK_LENGTH; ++i) {
			result.add(current.getID());
			seen.add(current);
			List<Area> neighbours = new ArrayList<Area>(current.getNeighbours());
			Collections.shuffle(neighbours);
			boolean found = false;
			for (Area next : neighbours) {
				if (seen.contains(next)) {
					continue;
				}
				current = next;
				found = true;
				break;
			}
			if (!found) {
				// We reached a dead-end.
				break;
			}
		}
		return new Path(null, null, result, model.getEntity(result.get(0)).getPositionPair(), model.getEntity(result.get(result.size() - 1)).getPositionPair(), false);
		
	}
}