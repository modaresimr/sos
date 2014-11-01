package sos.base.move.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.CenterAgent;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.move.Move;
import sos.base.move.MoveConstants;
import sos.base.move.Path;
import sos.base.reachablity.Reachablity;
import sos.base.util.Triple;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.worldGraph.Node;
import sos.base.worldGraph.WorldGraph;
import sos.tools.Dijkstra;
import sos.tools.GraphEdge;
import sos.tools.GraphWeightInterface;

/**
 * @author Aramik
 */
public abstract class MoveType implements MoveConstants {
	protected WorldGraph wg;
	protected GraphWeightInterface weights;
	protected Dijkstra dijkstra;
	protected SOSAgent<? extends StandardEntity> me;
	protected short lastDijkstraRunTimeOnME = 1;
	protected boolean isLastPathSafe = true;

	// -------------------------------------------------Constructor-------------------------------------------------//
	public MoveType(SOSAgent<? extends StandardEntity> me, WorldGraph graph, GraphWeightInterface weight) {
		this.me = me;
		this.wg = graph;
		this.weights = weight;
		this.dijkstra = new Dijkstra(graph.getNumberOfNodes(), me.model());
		updateWeigths();
	}

	// -------------------------------------------------ABSTRACTS-------------------------------------------------//
	protected abstract boolean isInSameAreaWithMe(Pair<? extends Area, Point2D> pair);

	protected abstract boolean isInSameArea(Pair<? extends Area, Point2D> alfa, Pair<? extends Area, Point2D> beta);

	protected abstract void updateWeigths();

	protected abstract ArrayList<Integer> getOutsideNodes(Area area);

	protected abstract ArrayList<Integer> getOutsideNodes(Area area, int x, int y);

	protected abstract ArrayList<Integer> getOutsideNodes(Human hu);

	private ArrayList<Triple<Integer, Point2D, Integer>> getOutsideNodesWithCosts(Area area, Point2D point) {
		ArrayList<Triple<Integer, Point2D, Integer>> result = new ArrayList<Triple<Integer, Point2D, Integer>>(5);
		for (Edge ed : area.getPassableEdges())
			result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), point, getCost(area, ed, point)));
		if (result.isEmpty()) { // special case = my policy is if there is no any open way we consider all of blocked entrances
			log().warn("no outside node????? why?");
			isLastPathSafe = false;
			for (Edge ed : area.getPassableEdges()) {
				result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), point, getCost(area, ed, point)));
				//SOSGeometryTools.distance((int) p.getX(), (int) p.getY(), sosShape.getCenterX(), sosShape.getCenterY()) / DIVISION_UNIT)
			}
		}
		return result;

	}

	// -------------------------------------------------CYCLE-------------------------------------------------//
	public void cycle() {
//		updateWeigths();
		isLastPathSafe = true;
	}

	// -------------------------------------------------Prepare Dijkstra-------------------------------------------------//
	protected void prepareDijkstraFromMe() {
		if (lastDijkstraRunTimeOnME < me.time()) {
			log().debug(this.getClass().getSimpleName() + ":prepareDijkstraFromMe");
			log().debug(this.getClass().getSimpleName() + ":lastTime =" + lastDijkstraRunTimeOnME);
			if (!(me instanceof CenterAgent)) {
				try {
					updateWeigths();
					ArrayList<Integer> outsideNodes = getOutsideNodes((Human) me.me());
					log().debug(this.getClass().getSimpleName() + ":Outside Nodes from Area=" + me.location() + " x=" + ((Human) me.me()).getX() + " y=" + ((Human) me.me()).getY() + "  is:\n" + outsideNodes);
					int[] costs = getCostsOfOutSidesFromMe(outsideNodes);
					log().debug(this.getClass().getSimpleName() + ":Outside Costs =", costs);
					long t1 = System.currentTimeMillis();
					this.dijkstra.Run(wg, weights, outsideNodes, costs);
					me.sosLogger.base.debug("performing Dijkstra took: " + (System.currentTimeMillis() - t1) + " ms");
					log().debug(this.getClass().getSimpleName() + ":Run Dijkstra");
					lastDijkstraRunTimeOnME = (short) me.time();
				} catch (Exception e) {
					log().error(e);
				}
			}
		}
	}

	private int[] getCostsOfOutSidesFromMe(ArrayList<Integer> outsides) {
		int[] costs = new int[outsides.size()];
		int index = 0;
		for (Integer out : outsides) {
			Node node = me.model().nodes().get(out);
			//			Point2D point = node.getRelatedEdge().getMidPoint();
			costs[index++] = getCost(me.model().areas().get(node.getAreaIndex()), me.me().getPositionPoint(), node.getRelatedEdge());
			//SOSGeometryTools.distance(x, y, (int) point.getX(), (int) point.getY()) / DIVISION_UNIT;
		}
		return costs;
	}

	// -------------------------------------------------OUTSIDE COSTS-------------------------------------------------//
	protected int[] getCostsOfOutSidesFrom(int x, int y, Collection<Integer> outsides) {
		int[] costs = new int[outsides.size()];
		int index = 0;
		for (Integer out : outsides) {
			Node node = me.model().nodes().get(out);
			//			Point2D point = node.getRelatedEdge().getMidPoint();
			costs[index++] = getCost(me.model().areas().get(node.getAreaIndex()), node.getRelatedEdge(), new Point2D(x, y));
			//SOSGeometryTools.distance(x, y, (int) point.getX(), (int) point.getY()) / DIVISION_UNIT;
		}
		return costs;
	}

	protected int[] getCostsOfOutSides(Collection<Integer> outsides) {
		int[] costs = new int[outsides.size()];
		int index = 0;
		for (Integer out : outsides) {
			Point2D point = me.model().nodes().get(out).getRelatedEdge().getMidPoint();
			Area area = me.model().areas().get(me.model().nodes().get(out).getAreaIndex());
			costs[index++] = getCost(area, area.getPositionPoint(), point);
		}
		return costs;
	}

	// -------------------------------------------------GET EDGES-------------------------------------------------//
	protected GraphEdge[] getEdgesByNodes(Node[] nodes) {
		GraphEdge[] pathEdges = new GraphEdge[nodes.length - 1];
		for (int i = 0, k = 1; k < nodes.length; ++i, ++k) {
			int edgeIndex = wg.edgeIndexBetween(nodes[i].getIndex(), nodes[k].getIndex());
			pathEdges[i] = me.model().graphEdges().get(edgeIndex);
		}
		return pathEdges;
	}

	// -------------------------------------------------GET MIN-------------------------------------------------//
	protected int getMinDestination(Collection<Integer> outsideNodes, int[] costs) {
		long min = Long.MAX_VALUE;
		int minNode = -1;
		int i = 0;
		for (Integer node : outsideNodes) {
			if (dijkstra.getWeight(node) + costs[i] < min) {
				min = dijkstra.getWeight(node) + costs[i];
				minNode = node;
			}
			i++;
		}
		return minNode;
	}

	// -------------------------------------------------GET ID-------------------------------------------------//
	protected ArrayList<EntityID> getAreaIds(Node[] nodes) {
		ArrayList<EntityID> pathIDs = new ArrayList<EntityID>(nodes.length / 2);
		ArrayList<Short> indexes = new ArrayList<Short>(nodes.length / 2);
		if (nodes.length < 2) {
			log().warn(this.getClass().getSimpleName() + ":in MoveType--> getAreaIds(ArrayList<Node> nodes) sizeof nodes=" + nodes.length + "\n");
			pathIDs.add(me.model().areas().get(nodes[0].getAreaIndex()).getID());
		}
		int base_index = 0;
		for (int k = 1; k < nodes.length; ++k) {
			if (nodes[base_index].getAreaIndex() != nodes[k].getAreaIndex()) {
				indexes.add(nodes[base_index].getAreaIndex());
				base_index = k;
			}
		}
		if (indexes.get(indexes.size() - 1) != nodes[nodes.length - 1].getAreaIndex())
			indexes.add(nodes[nodes.length - 1].getAreaIndex());
		for (Short indx : indexes)
			pathIDs.add(me.model().areas().get(indx).getID());
		return pathIDs;
	}

	// -------------------------------------------------LOGGING-------------------------------------------------//
	protected SOSLoggerSystem log() {
		return me.sosLogger.move;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++ MOVE ++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * should use only in Human Agents
	 *
	 * @param destinations
	 * @return
	 */
	public Path getPathTo(Collection<? extends Area> destinations) {
		prepareDijkstraFromMe();
		// -----------------------getting outside nodes and checking if source==destination
		log().debug(this.getClass().getSimpleName() + ":getPathTo=" + destinations);
		TreeSet<Integer> outsideNodes = new TreeSet<Integer>();
		for (Area ar : destinations) {
			outsideNodes.addAll(getOutsideNodes(ar));
			if (ar.getAreaIndex() == ((Area) ((Human) me.me()).getPosition()).getAreaIndex()) {// source and destination is in same area
				log().debug(this.getClass().getSimpleName() + ":source==destination");
				ArrayList<EntityID> ids = new ArrayList<EntityID>();
				ids.add(ar.getID());
				return new Path(null, null, ids, new Pair<Area, Point2D>(ar, new Point2D(((Human) me.me()).getX(), ((Human) me.me()).getY())), new Pair<Area, Point2D>(ar, new Point2D(ar.getX(), ar.getY())), isLastPathSafe);
			}
		}
		log().debug(this.getClass().getSimpleName() + ":outsideNodes=" + outsideNodes);
		// ------------------------get costs of outside nodes of destinations
		//		ArrayList<Integer> costs = getCostsOfOutSidesFrom(((Human) me.me()).getX(), ((Human) me.me()).getY(), outsideNodes);//TODO check this
		int[] costs = getCostsOfOutSides(outsideNodes);
		log().debug(this.getClass().getSimpleName() + ":outsideCosts=" + costs);
		// ------------------------get minimum cost destination
		int dest = getMinDestination(outsideNodes, costs);
		log().debug(this.getClass().getSimpleName() + ":Min destination=" + dest);
		// ------------------------get reverse nodes index of path
		ArrayList<Integer> pathNodesIndx = dijkstra.getpathArray(dest);
		log().debug(this.getClass().getSimpleName() + ":nodes index=" + pathNodesIndx);
		// ------------------------reversing nodes array to get right path
		Node[] pathNodes = new Node[pathNodesIndx.size()];
		for (int k = 0; k < pathNodesIndx.size(); ++k)
			pathNodes[k] = me.model().nodes().get(pathNodesIndx.get(k));
		log().debug(this.getClass().getSimpleName() + ":path Nodes =", pathNodes);
		// ------------------------get graphEdges between nodes
		GraphEdge[] pathEdges = getEdgesByNodes(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path Edges=", pathEdges);
		// ------------------------get Area IDS that will pass to kernel
		ArrayList<EntityID> ids = getAreaIds(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path IDs=" + ids);
		Area destination_Area = me.model().areas().get(me.model().nodes().get(dest).getAreaIndex());
		return new Path(pathEdges, pathNodes, ids, new Pair<Area, Point2D>((Area) ((Human) me.me()).getPosition(), new Point2D(me.me().getLocation().first(), me.me().getLocation().second())), new Pair<Area, Point2D>(destination_Area, new Point2D(destination_Area.getX(), destination_Area.getY())), isLastPathSafe);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++ MOVE ++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * should use only in Human Agents IMPORTNAT --> notice that area's in destination should be unique (unacceptable input sample <area_A,Point(x1,y1)>,<area_A,Point(x2,y2)> && Point(x1,y1)!=Point(x2,y2)
	 *
	 * @param destinations
	 * @return
	 */
	public Path getPathToShape(Collection<ShapeInArea> destinations) {
		if (destinations.size() == 0)
			log().error("why the size of array is zero?", new Error());
		prepareDijkstraFromMe();
		log().debug(this.getClass().getSimpleName() + ":getPathToPoints=" + destinations);
		Path path = null;
		long minWeigthPath = Long.MAX_VALUE;
		Collection<ShapeInArea> newDest = new ArrayList<ShapeInArea>();
		// -----------------------getting outside nodes and checking if source==destination
		for (ShapeInArea sosshape : destinations) {
			if (!sosshape.isValid())
				log().error(new Error("[move]sos shape is not valid..." + sosshape.getErrorReason()));
			if (sosshape.getArea(me.model()).equals(me.me().getAreaPosition())) {// source and destination is in same area
				log().debug("source==destination");
				Point2D dstpoint = null;
				if (sosshape.getArea(me.model()) instanceof Road) {
					ArrayList<Point2D> dstshape = Reachablity.getReachablePoints((Road) sosshape.getArea(me.model()), me.me().getPositionPair().second(), sosshape);
					int mindst = Integer.MAX_VALUE;
					for (Point2D shapePoint : dstshape) {
						int tmpdis = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), shapePoint);
						if (mindst > tmpdis) {
							mindst = tmpdis;
							dstpoint = shapePoint;
						}
					}
				}
				if (dstpoint == null)
					dstpoint = sosshape.getCenterPoint();
				int cost = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), dstpoint);
				if (cost < minWeigthPath) {
					minWeigthPath = cost;

					ArrayList<EntityID> ids = new ArrayList<EntityID>();
					ids.add(sosshape.getArea(me.model()).getID());
					path = new Path(null, null, ids, me.me().getPositionPair(), new Pair<Area, Point2D>(sosshape.getArea(me.model()), dstpoint), isLastPathSafe);
				}
			} else
				newDest.add(sosshape);

		}
		log().debug(this.getClass().getSimpleName() + ":Destination where is not in current location=" + newDest);
		if (newDest.size() == 0)
			return path;

		// ------------------------get minimum cost destination
		//////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		Triple<Integer, Point2D, Long> dest_cost = getMinDestination(newDest);
		log().debug(this.getClass().getSimpleName() + ":Min destination=" + dest_cost);
		if (dest_cost.third() > minWeigthPath)
			return path;
		// ------------------------get reverse nodes index of path
		ArrayList<Integer> pathNodesIndx = dijkstra.getpathArray(dest_cost.first());
		log().debug(this.getClass().getSimpleName() + ":nodes index=" + pathNodesIndx);
		// ------------------------reversing nodes array to get right path
		Node[] pathNodes = new Node[pathNodesIndx.size()];
		for (int k = 0; k < pathNodesIndx.size(); ++k)
			pathNodes[k] = me.model().nodes().get(pathNodesIndx.get(k));
		log().debug(this.getClass().getSimpleName() + ":path Nodes =", pathNodes);
		// ------------------------get graphEdges between nodes
		GraphEdge[] pathEdges = getEdgesByNodes(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path Edges =", pathEdges);
		// ------------------------get Area IDS that will pass to kernel
		ArrayList<EntityID> ids = getAreaIds(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path IDs =" + ids);
		Area destination_Area = me.model().areas().get(me.model().nodes().get(dest_cost.first()).getAreaIndex());
		Point2D xy = dest_cost.second();

		if (xy == null)
			log().error(this.getClass().getSimpleName() + ":XY is null in getPathToPoints(Collection<Pair<Area, Point2D>> destinations)\n");
		return new Path(pathEdges, pathNodes, ids, new Pair<Area, Point2D>((Area) ((Human) me.me()).getPosition(), new Point2D(((Human) me.me()).getX(), ((Human) me.me()).getY())), new Pair<Area, Point2D>(destination_Area, xy), isLastPathSafe);
	}

	private Triple<Integer, Point2D, Long> getMinDestination(Collection<ShapeInArea> newDest) {
		// ------------------------getting outside nodes of destinations and costs
		long min = Long.MAX_VALUE;
		Triple<Integer, Point2D, Integer> minNode = null;
		for (ShapeInArea shape : newDest) {
			Area area = shape.getArea(me.model());

			Pair<Point2D, Integer> tmpNode;
			for (Edge edge : area.getPassableEdges()) {
				if (dijkstra.getWeight(edge.getNodeIndex()) < min) {
					tmpNode = getMinOutsideNodeWithCost(edge, shape);
					if (tmpNode != null) {
						if (dijkstra.getWeight(edge.getNodeIndex()) + tmpNode.second() < min) {
							min = dijkstra.getWeight(edge.getNodeIndex()) + tmpNode.second();
							minNode = new Triple<Integer, Point2D, Integer>((int) edge.getNodeIndex(), tmpNode.first(), tmpNode.second());
						}
					}
				}
			}
		}
		return new Triple<Integer, Point2D, Long>(minNode.first(), minNode.second(), min);
	}

	private boolean haveADestinationWithCostLessThan(Collection<ShapeInArea> newDest, long cost) {
		// ------------------------getting outside nodes of destinations and costs
		for (ShapeInArea shape : newDest) {
			Area area = shape.getArea(me.model());
			Pair<Point2D, Integer> tmpNode;
			for (Edge edge : area.getPassableEdges()) {
				if (dijkstra.getWeight(edge.getNodeIndex()) < cost) {
					tmpNode = getMinOutsideNodeWithCost(edge, shape);
					if (tmpNode != null) {
						if (dijkstra.getWeight(edge.getNodeIndex()) + tmpNode.second() < cost) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	protected boolean haveADestinationWithCostLessThan(Collection<Pair<? extends Area, Point2D>> newDest, int maxcost) {
		log().trace("checking have a Destination from " + newDest + " WithCostless than " + maxcost);
		for (Pair<? extends Area, Point2D> dst : newDest) {
			for (Edge edge : dst.first().getPassableEdges()) {
				log().trace("Weight to node[" + edge.getNodeIndex() + "]=" + dijkstra.getWeight(edge.getNodeIndex()));
				if (dijkstra.getWeight(edge.getNodeIndex()) < maxcost) {
					Pair<Point2D, Integer> tmpNode = getMinOutsideNodeWithCost(edge, dst);
					log().trace("Weight to node[" + edge.getNodeIndex() + "]+cost outside=" + (dijkstra.getWeight(edge.getNodeIndex()) + tmpNode.second()));
					if (tmpNode != null && dijkstra.getWeight(edge.getNodeIndex()) + tmpNode.second() < maxcost) {
						return true;

					}
				}
			}
		}
		return false;

	}

	private Pair<Point2D, Integer> getMinOutsideNodeWithCost(Edge ed, ShapeInArea shape) {
		Area area = shape.getArea(me.model());
		if (area instanceof Building || !area.isBlockadesDefined()) {
			return new Pair<Point2D, Integer>(shape.getCenterPoint(), getCost(area, ed, shape.getCenterPoint()));
		} else { // area is a road
			ArrayList<Point2D> reachablePoints = Reachablity.getReachablePoints((Road) area, ed, shape);
			//			reachablePoints.add(((Human) me.me()).getPositionPoint());//Added by Yoosef my position is reachable for me
			int minCostToReachableAreaFromThisEdge = Integer.MAX_VALUE;
			Point2D minCostPointToReachableAreaFromThisEdge = null;
			for (Point2D reachPoint : reachablePoints) {
				int costTmp = getCost(area, ed, reachPoint);
				if (costTmp < minCostToReachableAreaFromThisEdge) {
					minCostPointToReachableAreaFromThisEdge = reachPoint;
					minCostToReachableAreaFromThisEdge = costTmp;
				}
			}
			if (reachablePoints.size() > 0) {
				return new Pair<Point2D, Integer>(minCostPointToReachableAreaFromThisEdge, minCostToReachableAreaFromThisEdge);
			}

		}

		return new Pair<Point2D, Integer>(shape.getCenterPoint(), getCost(shape.getArea(me.model()), ed, shape.getCenterPoint()));
	}

	private Pair<Point2D, Integer> getMinOutsideNodeWithCost(Edge ed, Pair<? extends Area, Point2D> pair) {
		return new Pair<Point2D, Integer>(pair.second(), getCost(pair.first(), ed, pair.second()));
	}

	private Triple<Integer, Point2D, Long> getMinDestination(ArrayList<Triple<Integer, Point2D, Integer>> outsideNodesCosts) {
		long min = Long.MAX_VALUE;
		Triple<Integer, Point2D, Integer> minNode = null;
		for (Triple<Integer, Point2D, Integer> triple : outsideNodesCosts) {
			if (dijkstra.getWeight(triple.first()) + triple.third() < min) {
				min = dijkstra.getWeight(triple.first()) + triple.third();
				minNode = triple;
			}
		}

		return new Triple<Integer, Point2D, Long>(minNode.first(), minNode.second(), min);
	}

	//	static ShapeDebugFrame debug=new ShapeDebugFrame();
	private ArrayList<Triple<Integer, Point2D, Integer>> getOutsideNodesWithCosts(ShapeInArea sosShape) {
		Area area = sosShape.getArea(me.model());
		ArrayList<Triple<Integer, Point2D, Integer>> result = new ArrayList<Triple<Integer, Point2D, Integer>>(5);
		for (Edge ed : area.getPassableEdges()) {
			if (area instanceof Building) {
				result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), sosShape.getCenterPoint(), getCost(area, ed, sosShape.getCenterPoint())));
			} else { // area is a road
				ArrayList<Point2D> reachablePoints = Reachablity.getReachablePoints((Road) area, ed, sosShape);
				int minCostToReachableAreaFromThisEdge = Integer.MAX_VALUE;
				Point2D minCostPointToReachableAreaFromThisEdge = null;
				for (Point2D reachPoint : reachablePoints) {
					int costTmp = getCost(area, ed, reachPoint);
					if (costTmp < minCostToReachableAreaFromThisEdge) {
						minCostPointToReachableAreaFromThisEdge = reachPoint;
						minCostToReachableAreaFromThisEdge = costTmp;
					}
				}
				if (reachablePoints.size() > 0) {
					result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), minCostPointToReachableAreaFromThisEdge, minCostToReachableAreaFromThisEdge));
				}
			}
		}

		if (result.isEmpty()) { // special case = my policy is if there is no any open way we consider all of blocked entrances
			isLastPathSafe = false;
			for (Edge ed : area.getPassableEdges()) {
				result.add(new Triple<Integer, Point2D, Integer>((int) ed.getNodeIndex(), sosShape.getCenterPoint(), getCost(area, ed, sosShape.getCenterPoint())));
			}
		}
		return result;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++ MOVE ++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * should use only in Human Agents IMPORTNAT --> notice that area's in destination should be unique (unacceptable input sample <area_A,Point(x1,y1)>,<area_A,Point(x2,y2)> && Point(x1,y1)!=Point(x2,y2)
	 *
	 * @param destinations
	 * @return
	 */
	public Path getPathToPoints(Collection<Pair<? extends Area, Point2D>> destinations) {
		prepareDijkstraFromMe();
		log().debug(this.getClass().getSimpleName() + ":getPathToPoints=" + destinations);
		// -----------------------getting outside nodes and checking if source==destination
		Path path = null;
		long minWeigthPath = Long.MAX_VALUE;
		Collection<Pair<? extends Area, Point2D>> newDest = new ArrayList<Pair<? extends Area, Point2D>>();
		for (Pair<? extends Area, Point2D> pair : destinations) {
			//			if (isInSameAreaWithMe(pair)) {// source and destination is in same area
			if (pair.first().equals(me.me().getAreaPosition())) {
				int cost = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), pair.second());
				if (cost < minWeigthPath) {
					minWeigthPath = cost;
					ArrayList<EntityID> ids = new ArrayList<EntityID>();
					log().debug("source==destination");
					ids.add(pair.first().getID());
					path = new Path(null, null, ids, me.me().getPositionPair(), pair, isLastPathSafe);
				}
			} else {
				newDest.add(pair);
			}
		}
		if (newDest.size() == 0)
			return path;
		else {
			log().debug("newDsts:" + newDest);

		}
		// ------------------------getting outside nodes of destinations and costs
		////////////////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		ArrayList<Triple<Integer, Point2D, Integer>> outsideNodesCosts = new ArrayList<Triple<Integer, Point2D, Integer>>();
		for (Pair<? extends Area, Point2D> s : newDest) {
			ArrayList<Triple<Integer, Point2D, Integer>> temp = getOutsideNodesWithCosts(s.first(), s.second());
			outsideNodesCosts.addAll(temp);
		}
		log().debug(this.getClass().getSimpleName() + ":outsideNodesCosts of destinations=" + outsideNodesCosts);
		// ------------------------get minimum cost destination
		//////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		Triple<Integer, Point2D, Long> dest_cost = getMinDestination(outsideNodesCosts);
		log().debug(this.getClass().getSimpleName() + ":Min destination=" + dest_cost);
		if (dest_cost.third() > minWeigthPath)
			return path;
		// ------------------------get reverse nodes index of path
		ArrayList<Integer> pathNodesIndx = dijkstra.getpathArray(dest_cost.first());
		log().debug(this.getClass().getSimpleName() + ":nodes index=" + pathNodesIndx);
		// ------------------------reversing nodes array to get right path
		Node[] pathNodes = new Node[pathNodesIndx.size()];
		for (int k = 0; k < pathNodesIndx.size(); ++k)
			pathNodes[k] = me.model().nodes().get(pathNodesIndx.get(k));
		log().debug(this.getClass().getSimpleName() + ":path Nodes =" + pathNodes);
		// ------------------------get graphEdges between nodes
		GraphEdge[] pathEdges = getEdgesByNodes(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path Edges =" + pathEdges);
		// ------------------------get Area IDS that will pass to kernel
		ArrayList<EntityID> ids = getAreaIds(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path IDs =" + ids);
		Area destination_Area = me.model().areas().get(me.model().nodes().get(dest_cost.first()).getAreaIndex());
		Point2D xy = dest_cost.second();
		if (xy == null)
			log().error(this.getClass().getSimpleName() + ":XY is null in getPathToPoints(Collection<Pair<Area, Point2D>> destinations)\n");
		return new Path(pathEdges, pathNodes, ids, me.me().getPositionPair(), new Pair<Area, Point2D>(destination_Area, xy), isLastPathSafe);
	}

	protected abstract int getCost(Area area, Point2D start, Point2D dst);

	protected abstract int getCost(Area area, Edge ed, Point2D dst);

	protected abstract int getCost(Area area, Point2D start, Edge ed);

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++ MOVE ++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public Path getPathFromTo(Collection<? extends Area> sources, Collection<? extends Area> destinations) {
		log().debug(this.getClass().getSimpleName() + ":getPathFromTo source=" + sources + " ,destination=" + destinations);
		// ------------------------checking if source==destination
		for (Area s : sources)
			for (Area d : destinations)
				if (s.getAreaIndex() == d.getAreaIndex()) {
					log().debug(this.getClass().getSimpleName() + ":source==destination");
					ArrayList<EntityID> ids = new ArrayList<EntityID>();
					ids.add(s.getID());
					return new Path(null, null, ids, new Pair<Area, Point2D>(s, new Point2D(((Human) me.me()).getX(), ((Human) me.me()).getY())), new Pair<Area, Point2D>(s, new Point2D(s.getX(), s.getY())), isLastPathSafe);
				}
		// ------------------------getting outside nodes of sources
		TreeSet<Integer> outsideNodes = new TreeSet<Integer>();
		for (Area s : sources)
			outsideNodes.addAll(getOutsideNodes(s, s.getX(), s.getY()));
		log().debug(this.getClass().getSimpleName() + ":outsideNodes=" + outsideNodes);
		// ------------------------getting cost of outside nodes of sources
		int[] costs = getCostsOfOutSides(outsideNodes);
		log().debug(this.getClass().getSimpleName() + ":outsideCosts=" + costs);
		// ------------------------run dijkstra
		try {
			log().debug(this.getClass().getSimpleName() + ":Run Dijkstra");
			this.dijkstra.Run(wg, weights, new ArrayList<Integer>(outsideNodes), costs);
			lastDijkstraRunTimeOnME = (short) (me.time() - 1);
		} catch (Exception e) {
			log().error(e);
		}
		// ------------------------getting outside nodes of destinations
		outsideNodes = new TreeSet<Integer>();
		for (Area s : destinations)
			outsideNodes.addAll(getOutsideNodes(s, s.getX(), s.getY()));
		log().debug(this.getClass().getSimpleName() + ":outsideNodes of destinations=" + outsideNodes);
		// ------------------------get costs of outside nodes of destinations
		costs = getCostsOfOutSides(outsideNodes);
		log().debug(this.getClass().getSimpleName() + ":outside costs of destination=" + costs);
		// ------------------------get minimum cost destination
		int dest = getMinDestination(outsideNodes, costs);
		log().debug(this.getClass().getSimpleName() + ":Min destination=" + dest);
		// ------------------------get reverse nodes index of path
		ArrayList<Integer> pathNodesIndx = dijkstra.getpathArray(dest);
		log().debug(this.getClass().getSimpleName() + ":nodes index=" + pathNodesIndx);
		// ------------------------reversing nodes array to get right path
		Node[] pathNodes = new Node[pathNodesIndx.size()];
		for (int k = 0; k < pathNodesIndx.size(); ++k)
			pathNodes[k] = me.model().nodes().get(pathNodesIndx.get(k));
		log().debug(this.getClass().getSimpleName() + ":path Nodes =" + pathNodes);
		// ------------------------get graphEdges between nodes
		GraphEdge[] pathEdges = getEdgesByNodes(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path Edges =" + pathEdges);
		// ------------------------get Area IDS that will pass to kernel
		ArrayList<EntityID> ids = getAreaIds(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path IDs =" + ids);
		Area destination_Area = me.model().areas().get(me.model().nodes().get(dest).getAreaIndex());
		Area source_Area = me.model().areas().get(pathNodes[0].getAreaIndex());
		return new Path(pathEdges, pathNodes, ids, new Pair<Area, Point2D>(source_Area, new Point2D(source_Area.getX(), source_Area.getY())), new Pair<Area, Point2D>(destination_Area, new Point2D(destination_Area.getX(), destination_Area.getY())), isLastPathSafe);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++ MOVE ++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public Path getPathFromPointsToPoints(Collection<Pair<? extends Area, Point2D>> sources, Collection<Pair<? extends Area, Point2D>> destinations) {
		log().debug("getPathFromPointsToPoints source=" + sources + " ,destination=" + destinations);
		// ------------------------checking if source==destination
		for (Pair<? extends Area, Point2D> s : sources)
			for (Pair<? extends Area, Point2D> d : destinations)
				if (isInSameArea(s, d)) {
					ArrayList<EntityID> ids = new ArrayList<EntityID>();
					log().debug(this.getClass().getSimpleName() + ":source==destination");
					ids.add(s.first().getID());
					return new Path(null, null, ids, s, d, isLastPathSafe);
				}
		// ------------------------getting outside nodes of sources and costs
		HashMap<Integer, Integer> outsideNodesCosts = new HashMap<Integer, Integer>();
		for (Pair<? extends Area, Point2D> s : sources) {
			ArrayList<Triple<Integer, Point2D, Integer>> temp = getOutsideNodesWithCosts(s.first(), s.second());
			for (Triple<Integer, Point2D, Integer> p : temp)
				outsideNodesCosts.put(p.first(), p.third());
		}
		log().debug(this.getClass().getSimpleName() + ":outsideNodesCosts=" + outsideNodesCosts);
		// ------------------------run dijkstra
		int[] temp_costs = new int[outsideNodesCosts.size()];
		int index = 0;
		for (Integer val : outsideNodesCosts.values())
			temp_costs[index++] = val;
		try {
			log().debug(this.getClass().getSimpleName() + ":Run Dijkstra");
			this.dijkstra.Run(wg, weights, new ArrayList<Integer>(outsideNodesCosts.keySet()), temp_costs);
			lastDijkstraRunTimeOnME = (short) (me.time() - 1);
		} catch (Exception e) {
			log().error(e);
		}
		// ------------------------getting outside nodes of destinations and costs
		outsideNodesCosts = new HashMap<Integer, Integer>();
		for (Pair<? extends Area, Point2D> s : destinations) {
			ArrayList<Triple<Integer, Point2D, Integer>> temp = getOutsideNodesWithCosts(s.first(), s.second());
			for (Triple<Integer, Point2D, Integer> p : temp)
				outsideNodesCosts.put(p.first(), p.third());
		}
		log().debug(this.getClass().getSimpleName() + ":outsideNodesCosts of destinations=" + outsideNodesCosts);
		// ------------------------get minimum cost destination
		temp_costs = new int[outsideNodesCosts.size()];
		index = 0;
		for (Integer val : outsideNodesCosts.values())
			temp_costs[index++] = val;
		int dest = getMinDestination(outsideNodesCosts.keySet(), temp_costs);
		log().debug(this.getClass().getSimpleName() + ":Min destination=" + dest);
		// ------------------------get reverse nodes index of path
		ArrayList<Integer> pathNodesIndx = dijkstra.getpathArray(dest);
		log().debug(this.getClass().getSimpleName() + ":path Nodes Index =" + pathNodesIndx);
		// ------------------------reversing nodes array to get right path
		Node[] pathNodes = new Node[pathNodesIndx.size()];
		for (int k = 0; k < pathNodesIndx.size(); ++k)
			pathNodes[k] = me.model().nodes().get(pathNodesIndx.get(k));
		log().debug(this.getClass().getSimpleName() + ":path Nodes =" + pathNodes);
		// ------------------------get graphEdges between nodes
		GraphEdge[] pathEdges = getEdgesByNodes(pathNodes);
		log().debug(this.getClass().getSimpleName() + ":path Nodes =" + pathEdges);
		// ------------------------get Area IDS that will pass to kernel
		ArrayList<EntityID> ids = getAreaIds(pathNodes);
		log().debug("path IDs =" + ids);
		Area destination_Area = me.model().areas().get(me.model().nodes().get(dest).getAreaIndex());
		Point2D xy_d = null;
		for (Pair<? extends Area, Point2D> pair : destinations) { // I assumed that there is no area with different X and Y s in destinations
			if (destination_Area.getAreaIndex() == pair.first().getAreaIndex()) {
				xy_d = pair.second();
				break;
			}
		}
		if (xy_d == null)
			log().error(this.getClass().getSimpleName() + ":XY is null in getPathToPoints(Collection<Pair<Area, Point2D>> destinations)\n");
		Area source_Area = me.model().areas().get(pathNodes[0].getAreaIndex());
		Point2D xy_s = null;
		for (Pair<? extends Area, Point2D> pair : sources) { // I assumed that there is no area with different X and Y s in destinations
			if (source_Area.getAreaIndex() == pair.first().getAreaIndex()) {
				xy_s = pair.second();
				break;
			}
		}
		if (xy_s == null)
			log().error(this.getClass().getSimpleName() + ":XY is null in getPathToPoints(Collection<Pair<Area, Point2D>> sources)\n");
		return new Path(pathEdges, pathNodes, ids, new Pair<Area, Point2D>(source_Area, xy_s), new Pair<Area, Point2D>(destination_Area, xy_d), isLastPathSafe);
	}

	public long getWeightTo(Area target, int x, int y) {
		prepareDijkstraFromMe();
		log().info("calculating weight to " + target + " x=" + x + " y=" + y);
		long weight = Long.MAX_VALUE;
		for (Edge ed : target.getPassableEdges()) {
			int cost = getCost(target, ed, new Point2D(x, y));
			log().trace("cost from edge" + ed + " to x=" + x + " y=" + y);
			if (dijkstra.getWeight(ed.getNodeIndex()) + cost < weight) {
				weight = dijkstra.getWeight(ed.getNodeIndex()) + cost;
				log().trace("weight node(" + ed.getNodeIndex() + ") +cost(" + cost + ") =" + weight);
			}
		}
		return weight / Move.DIVISION_UNIT_FOR_GET;

	}

	public long getWeightTo(ShapeInArea sosshape) {
		if (sosshape.contains(((Human) me.me()).getX(), ((Human) me.me()).getY()))
		{
			log().debug("in shape -- Yoosef");
			return 0;
		}
		prepareDijkstraFromMe();
		log().debug(this.getClass().getSimpleName() + ":getWeightTo=" + sosshape);
		//		Path path = null;
		long minWeigthPath = Long.MAX_VALUE;
		Collection<ShapeInArea> newDest = new ArrayList<ShapeInArea>();
		// -----------------------getting outside nodes and checking if source==destination
		if (sosshape.getArea(me.model()).equals(me.me().getAreaPosition())) {// source and destination is in same area
			log().debug("source==destination");
			Point2D dstpoint = null;
			if (sosshape.getArea(me.model()) instanceof Road) {
				ArrayList<Point2D> dstshape = Reachablity.getReachablePoints((Road) sosshape.getArea(me.model()), me.me().getPositionPair().second(), sosshape);

				int mindst = Integer.MAX_VALUE;

				for (Point2D shapePoint : dstshape) {

					int tmpdis = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), shapePoint);

					if (mindst > tmpdis) {
						mindst = tmpdis;
						dstpoint = shapePoint;
					}
				}
			}
			if (dstpoint == null)
				dstpoint = sosshape.getCenterPoint();
			int cost = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), dstpoint);
			if (cost < minWeigthPath) {
				minWeigthPath = cost;

				ArrayList<EntityID> ids = new ArrayList<EntityID>();
				ids.add(sosshape.getArea(me.model()).getID());
				//				path = new Path(null, null, ids, me.me().getPositionPair(), new Pair<Area, Point2D>(sosshape.getArea(me.model()), dstpoint), isLastPathSafe);
				//				path.setCost(cost/DIVISION_UNIT_FOR_GET);
			}
		} else
			newDest.add(sosshape);

		if (newDest.size() == 0)
			return minWeigthPath / DIVISION_UNIT_FOR_GET;

		// ------------------------getting outside nodes of destinations and costs
		/////////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		ArrayList<Triple<Integer, Point2D, Integer>> outsideNodesCosts = new ArrayList<Triple<Integer, Point2D, Integer>>();
		for (ShapeInArea sosShape : newDest) {
			outsideNodesCosts.addAll(getOutsideNodesWithCosts(sosShape));
		}
		if (outsideNodesCosts.size() == 0) {
			log().error("[move]outsideNodesCosts shouldn't have zero size!!!!");
		}
		log().debug(this.getClass().getSimpleName() + ":outsideNodesCosts of destinations=" + outsideNodesCosts);
		// ------------------------get minimum cost destination
		//////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		Triple<Integer, Point2D, Long> dest_cost = getMinDestination(outsideNodesCosts);
		log().debug(this.getClass().getSimpleName() + ":Min destination=" + dest_cost);
		if (dest_cost.third() > minWeigthPath)
			return minWeigthPath / DIVISION_UNIT_FOR_GET;
		return dest_cost.third() / DIVISION_UNIT_FOR_GET;

	}

	public long getWeightTo(Collection<ShapeInArea> destinations) {
		prepareDijkstraFromMe();
		log().debug(this.getClass().getSimpleName() + ":getPathToPoints=" + destinations);
		//		Path path = null;
		long minWeigthPath = Long.MAX_VALUE;
		Collection<ShapeInArea> newDest = new ArrayList<ShapeInArea>();
		// -----------------------getting outside nodes and checking if source==destination
		for (ShapeInArea sosshape : destinations) {

			if (sosshape.getArea(me.model()).equals(me.me().getAreaPosition())) {// source and destination is in same area
				if (sosshape.contains(((Human) me.me()).getX(), ((Human) me.me()).getY()))
				{
					log().debug("in shape--Yoosef");
					return 0;
				}
				log().debug("source==destination");
				Point2D dstpoint = null;
				if (sosshape.getArea(me.model()) instanceof Road) {
					ArrayList<Point2D> dstshape = Reachablity.getReachablePoints((Road) sosshape.getArea(me.model()), me.me().getPositionPair().second(), sosshape);
					dstshape.add(((Human) me.me()).getPositionPoint());//Yoosef my position is reachable
					int mindst = Integer.MAX_VALUE;
					for (Point2D shapePoint : dstshape) {
						int tmpdis = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), shapePoint);
						if (mindst > tmpdis) {
							mindst = tmpdis;
							dstpoint = shapePoint;
						}
					}
					//					}
				}
				if (dstpoint == null)
					dstpoint = sosshape.getCenterPoint();
				int cost = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), dstpoint);
				if (cost < minWeigthPath) {
					minWeigthPath = cost;

					ArrayList<EntityID> ids = new ArrayList<EntityID>();
					ids.add(sosshape.getArea(me.model()).getID());
					//					path = new Path(null, null, ids, me.me().getPositionPair(), new Pair<Area, Point2D>(sosshape.getArea(me.model()), dstpoint), isLastPathSafe);
				}
			} else
				newDest.add(sosshape);

		}
		log().debug(this.getClass().getSimpleName() + ":Destination where is not in current location=" + newDest);
		if (newDest.size() == 0)
			return minWeigthPath / DIVISION_UNIT_FOR_GET;

		// ------------------------get minimum cost destination
		//////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		Triple<Integer, Point2D, Long> dest_cost = getMinDestination(newDest);
		log().debug(this.getClass().getSimpleName() + ":Min destination=" + dest_cost);
		if (dest_cost.third() > minWeigthPath)
			return minWeigthPath / DIVISION_UNIT_FOR_GET;
		return dest_cost.third() / DIVISION_UNIT_FOR_GET;
	}

	public boolean isReallyUnreachableTo(Collection<ShapeInArea> destinations) {
		for (ShapeInArea shapeInArea : destinations) {
			if(shapeInArea.contains(me.me().getPositionPoint().toGeomPoint()))
				return false;
		}
		prepareDijkstraFromMe();
		//		log().debug(this.getClass().getSimpleName() + ":isReallyUnreachableTo=" + destinations);
		Collection<ShapeInArea> newDest = new ArrayList<ShapeInArea>();
		// -----------------------getting outside nodes and checking if source==destination
		for (ShapeInArea sosshape : destinations) {
			
			if (sosshape.getArea(me.model()).equals(me.me().getAreaPosition())) {// source and destination is in same area
				log().debug("source==destination");
				Point2D dstpoint = null;
				if (sosshape.getArea(me.model()) instanceof Road) {
					ArrayList<Point2D> dstshape = Reachablity.getReachablePoints((Road) sosshape.getArea(me.model()), me.me().getPositionPair().second(), sosshape);
					int mindst = Integer.MAX_VALUE;
					for (Point2D shapePoint : dstshape) {
						int tmpdis = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), shapePoint);
						if (mindst > tmpdis) {
							mindst = tmpdis;
							dstpoint = shapePoint;
						}
					}
				} else {
					dstpoint = sosshape.getCenterPoint();
				}
				if (dstpoint == null)
					return false;

				int cost = getCost(me.me().getPositionPair().first(), me.me().getPositionPair().second(), dstpoint);
				if (cost < MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING) {
					log().debug(this.getClass().getSimpleName() + ":isReallyUnreachableTo=" + destinations + ":::::" + false);
					return false;
				}
			} else
				newDest.add(sosshape);

		}
		if (newDest.size() == 0) {
			log().debug(this.getClass().getSimpleName() + ":isReallyUnreachableTo=" + destinations + ":::::" + true);
			return true;
		}

		// ------------------------getting outside nodes of destinations and costs
		/////////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		// ------------------------get minimum cost destination
		//////<node,    point,   cost>=Triple<Integer, Point2D, Integer>
		if (haveADestinationWithCostLessThan(newDest, MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING)) {
			log().debug(this.getClass().getSimpleName() + ":isReallyUnreachableTo=" + destinations + ":::::" + false);
			return false;
		}
		log().debug(this.getClass().getSimpleName() + ":isReallyUnreachableTo=" + destinations + ":::::" + true);
		return true;
	}

	public long getWeightToLowProcess(Area target, int x, int y) {
		log().logln("getWeightToLowProcess(Area " + target + ")to x,y !!!!!");
		prepareDijkstraFromMe();
		int[] outsideNodesOfTarget = target.getPassableEdgeNodeIndexes();
		if (outsideNodesOfTarget.length == 0)
			return UNREACHABLE_COST;
		long min = Long.MAX_VALUE;
		for (int i = 0; i < outsideNodesOfTarget.length; i++) {
			int widthTo = getCost(target, target.getEdges().get(i), new Point2D(x, y));
			if (dijkstra.getWeight(outsideNodesOfTarget[i]) + widthTo < min) {
				min = dijkstra.getWeight(outsideNodesOfTarget[i]) + widthTo;

			}
		}
		return min / DIVISION_UNIT_FOR_GET;
	}
	//	public long getCostInMMToNodes(ArrayList<ShapeInArea> shapes) {
	//		return 0;
	//	}

}
