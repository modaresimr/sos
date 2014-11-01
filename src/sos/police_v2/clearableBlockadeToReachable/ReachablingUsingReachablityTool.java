package sos.police_v2.clearableBlockadeToReachable;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.move.Path;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.Reachablity.ReachablityState;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.worldGraph.Node;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;
import sos.tools.GraphEdge;
import sos.tools.geometry.SOSGeometryTools;

public class ReachablingUsingReachablityTool extends ClearableBlockadeToReachable {

	public ReachablingUsingReachablityTool(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	public ArrayList<Blockade> getBlockingBlockadeOfPathWithGoodReachablity(Path path) {
		log.info("========================getBlockingBlockade path:" + path);
		ArrayList<Blockade> blockingBlock = new ArrayList<Blockade>();

		if (path.getEdges() == null || path.getNodes() == null || path.getNodes().length == 0) {
			blockingBlock.addAll(getBlockingBlockadeInNullEdges(path));
			return blockingBlock;
		}
		Pair<? extends Area, Point2D> source = path.getSource();
		Pair<? extends Area, Point2D> dest = path.getDestination();

		Node[] nodes = path.getNodes();
		Pair<Point2D, Point2D> lastPoint = null;
		lastPoint = getAPointInReachblePartEdges(source.first(), nodes[0].getRelatedEdge());
		////////////////////////////////////////////////////FROM STARTPOINT TO EDGE//////////////////////////////
		if (source.first() instanceof Road && source.first().isBlockadesDefined()) {
			log.info("Finding blockades source:" + source.first() + " from startpoint to edge(it is " + Reachablity.isReachable((Road) source.first(), source.second(), lastPoint.first()) + ")");
			ArrayList<Blockade> clearableBlockades = PoliceUtils.getClearableBlockades((Road) source.first(), source.second(), lastPoint.first());
			for (Blockade blockade : clearableBlockades) {
				if (PoliceUtils.isValid(blockade))
					blockingBlock.add(blockade);
			}
			log.debug("Find clearable block:" + clearableBlockades + " from startpoint to edge");
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////Between Edges/////////////////////////////////////////////////
		ArrayList<Road> visibleRoads = agent.getVisibleEntities(Road.class);
		if (blockingBlock.size() == 0) {
			log.info("Finding blockades between edges ");
			for (GraphEdge graphEdge : path.getEdges()) {
				Area position = null;
				if (graphEdge instanceof WorldGraphEdge) {
					position = agent.model().areas().get(((WorldGraphEdge) graphEdge).getInsideAreaIndex());
					if (!position.isBlockadesDefined())
						return blockingBlock;

					if (SOSGeometryTools.distance(agent.me(), position) < agent.VIEW_DISTANCE) {
						if (lastPoint == null) {
							log.error("lastPoint is null");
							return (ArrayList<Blockade>) position.getBlockades();
						}

						Edge e1 = position.model().nodes().get(graphEdge.getHeadIndex()).getRelatedEdge();
						Edge e2 = position.model().nodes().get(graphEdge.getTailIndex()).getRelatedEdge();

						Pair<Point2D, Point2D> endPoint = null;
						if (SOSGeometryTools.distance(e1, lastPoint.second()) < SOSGeometryTools.distance(e2, lastPoint.second())) {
							endPoint = getAPointInReachblePartEdges(position, e2);
						} else
							endPoint = getAPointInReachblePartEdges(position, e1);

						if (position instanceof Road 
								&& position.isBlockadesDefined() 
								&& visibleRoads.contains(position) 
								&& Reachablity.isReachable((Road) position, lastPoint.second(), endPoint.first()) != ReachablityState.Open) {
							Road blockArea = (Road) position;
							GraphEdge blockEdge = graphEdge;
							log.debug("edge " + blockEdge + " is block! last point is:" + lastPoint);

							ArrayList<Blockade> clearableBlockades = PoliceUtils.getClearableBlockades(blockArea, lastPoint.second(), endPoint.first());
							for (Blockade blockade : clearableBlockades) {
								if (PoliceUtils.isValid(blockade))
									blockingBlock.add(blockade);
							}
							log.debug("found blocking block:" + clearableBlockades + " between edges...valid blocks:" + blockingBlock);
							if (blockingBlock.size() != 0)
								break;
						}
						lastPoint = endPoint;
					}

				}
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////FIND BLOCKADES FROM LAST EDGE TO END POINT///////////////////////////////
		if (blockingBlock.size() == 0 && dest.first() instanceof Road && dest.first().getLastSenseTime() > agent.time() - 2) {
			log.info("finding blockades from latest edge to end point ");
			if (lastPoint == null) {
				Pair<Point2D, Point2D> tmplastPoint = getAPointInReachblePartEdges(dest.first(), nodes[nodes.length - 1].getRelatedEdge());
				lastPoint = new Pair<Point2D, Point2D>(tmplastPoint.second(), tmplastPoint.first());
				log.error("why oomad inja??");
			}
			ArrayList<Blockade> clearableBlockades = PoliceUtils.getClearableBlockades((Road) dest.first(), lastPoint.second(), dest.second());
			for (Blockade blockade : clearableBlockades) {
				if (PoliceUtils.isValid(blockade))
					blockingBlock.add(blockade);
			}
			log.debug("find blocking block:" + clearableBlockades + " from latest edge to end point");
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		log.info("valid blocking blockades (" + blockingBlock + ") is clearable now in clear distance(" + agent.clearDistance + ")");
		return blockingBlock;
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
		ArrayList<Point2D> twoPoint = Utility.get2PointsAroundAPointOutOfLine(rEdge.getStart(), rEdge.getEnd(), point, 10);
		if (area.getShape().contains(twoPoint.get(0).toGeomPoint()))
			return new Pair<Point2D, Point2D>(twoPoint.get(0), twoPoint.get(1));
		else
			return new Pair<Point2D, Point2D>(twoPoint.get(1), twoPoint.get(0));

	}

	//TODO edge bug dare
	@Override
	public ArrayList<Blockade> getBlockingBlockadeOfPath(Path path) {
		return getBlockingBlockadeOfPathWithGoodReachablity(path);
		/*
		 * ArrayList<Blockade> blockingBlock=new ArrayList<Blockade>();
		 * if (path.getEdges() == null || path.getNodes() == null || path.getNodes().length == 0) {
		 * blockingBlock.addAll(getBlockingBlockadeInNullEdges(path));
		 * return blockingBlock;
		 * }
		 * Pair<? extends Area, Point2D> source = path.getSource();
		 * Pair<? extends Area, Point2D> dest = path.getDestination();
		 * Road blockArea = null;
		 * GraphEdge blockEdge = null;
		 * Node[] nodes = path.getNodes();
		 * if(source.first() instanceof Road){
		 * log.info("finding blockades from startpoint to edge(it is "+Reachablity.isReachable((Road) source.first(), source.second(), nodes[0].getRelatedEdge())+")");
		 * ArrayList<Blockade> clearableBlockades = PoliceUtils.getClearableBlockades((Road) source.first(), source.second(), nodes[0].getRelatedEdge());
		 * for (Blockade blockade : clearableBlockades) {
		 * if(PoliceUtils.isValid(blockade))
		 * blockingBlock.add(blockade);
		 * }
		 * log.debug("find clearable block:"+clearableBlockades+" from startpoint to edge");
		 * }
		 * if (blockingBlock.size() == 0) {
		 * log.info("finding blockades between edges ");
		 * for (GraphEdge graphEdge : path.getEdges()) {
		 * Area position = null;
		 * if (graphEdge instanceof WorldGraphEdge)
		 * position = agent.model().areas().get(((WorldGraphEdge) graphEdge).getInsideAreaIndex());
		 * if (position != null) {
		 * if (SOSGeometryTools.distance(agent.me(), position) < agent.VIEW_DISTANCE) {
		 * if (graphEdge.getState() == GraphEdgeState.Block&&position instanceof Road &&
		 * position.isBlockadesDefined() &&
		 * position.getLastSenseTime()>=agent.time()-2) {
		 * blockArea = (Road) position;
		 * blockEdge = graphEdge;
		 * break;
		 * }
		 * }
		 * }
		 * }
		 * if (blockArea != null) {
		 * log.debug("edge "+blockEdge +"("+blockEdge.getIndex()+") is block!");
		 * ArrayList<Blockade> clearableBlockades = PoliceUtils.getClearableBlockades(blockArea,blockEdge);
		 * for (Blockade blockade : clearableBlockades) {
		 * // if(PoliceUtils.isValid(blockade))
		 * blockingBlock.add(blockade);
		 * }
		 * log.debug("find blocking block:"+clearableBlockades+" between edges");
		 * }
		 * }
		 * if (blockingBlock.size() == 0&& dest.first() instanceof Road && dest.first().getLastSenseTime()>agent.time()-2) {
		 * log.info("finding blockades from latest edge to end point ");
		 * ArrayList<Blockade> clearableBlockades = PoliceUtils.getClearableBlockades((Road) dest.first(), nodes[nodes.length - 1].getRelatedEdge(),dest.second());
		 * for (Blockade blockade : clearableBlockades) {
		 * if(PoliceUtils.isValid(blockade))
		 * blockingBlock.add(blockade);
		 * }
		 * log.debug("find blocking block:"+clearableBlockades+" from latest edge to end point");
		 * }
		 * log.info("valid blocking blockades ("+blockingBlock+") is clearable now in clear distance(" + agent.clearDistance+")");
		 * return blockingBlock;
		 */
	}

	private ArrayList<Blockade> getBlockingBlockadeInNullEdges(Path path) {
		log.debug("makeReachableInNullEdges path=" + path);
		Pair<? extends Area, Point2D> source = path.getSource();
		Pair<? extends Area, Point2D> dest = path.getDestination();

		if (!source.first().equals(dest.first()) || !(source.first() instanceof Road)) {
			log.error("How can the edges be null and source and destination are not in the same road????");
			return new ArrayList<Blockade>();
		}
		if (source.first() instanceof Road) {
			ArrayList<Blockade> clearableBlocks = PoliceUtils.getClearableBlockades((Road) source.first(), source.second(), dest.second());

			log.debug("Clearable Blocks:" + clearableBlocks);
			return clearableBlocks;
		}

		log.error("How can the edges be null and source and destination are not in the same road????");
		return new ArrayList<Blockade>();

	}

	/*
	 * private void makeReachable2(Path path) throws SOSActionException {
	 * if (path.getEdges() == null) {
	 * log.info(" I am on the right position " + path);
	 * return;
	 * }
	 * long start = System.currentTimeMillis();
	 * log.info("making reachable to " + path);
	 * ArrayList<EntityID> ids = path.getIds();
	 * System.out.println("path ==== " + path);
	 * int blockEntityIndex = 0;
	 * Blockade bestBlockade = null;
	 * for (int i = 0; i < ids.size(); i++) {
	 * if (policeForceAgent.move.isReallyUnreachable((Area) (model()
	 * .getEntity(ids.get(i))))) {
	 * blockEntityIndex = i;
	 * continue;
	 * }
	 * }
	 * log.info(" block entity index of path " + blockEntityIndex);
	 * if (blockEntityIndex < ids.size() - 1) {
	 * log.info(" block Entitiy index < path.size =" + ids.size());
	 * bestBlockade = chooseBestBlockade(PoliceReachablity
	 * .clearableBlockades(
	 * (Road) policeForceAgent.location(),
	 * new Point2D(policeForceAgent.me().getX(),
	 * policeForceAgent.me().getY()),
	 * ((Area) (model().getEntity(ids
	 * .get(blockEntityIndex)))).getEdgeTo(
	 * ((Area) (model().getEntity(ids
	 * .get(blockEntityIndex + 1)))))
	 * .getMidPoint()));
	 * } else {
	 * log.info(" block is here " + blockEntityIndex);
	 * bestBlockade = chooseBestBlockade(PoliceReachablity
	 * .clearableBlockades(
	 * (Road) policeForceAgent.getPosition(),
	 * new Point2D(policeForceAgent.getLocation().first(),
	 * policeForceAgent.getLocation().second()),
	 * ((Area) (model().getEntity(ids.get(0)))).getEdgeTo(
	 * ((Area) (model().getEntity(ids.get(1)))))
	 * .getMidPoint()));
	 * }
	 * if (bestBlockade == null) {
	 * log.info(" best blockade is still null so we can move now ");
	 * long finish = System.currentTimeMillis();
	 * System.out.println(" he moved on path in  " + (finish - start)
	 * + "  ms");
	 * policeForceAgent.move(ids);
	 * } else if (sos.base.util.SOSGeometryTools.distance(new Point2D(
	 * policeForceAgent.me().getX(), policeForceAgent.me().getY()),
	 * bestBlockade.getCenteroid()) <policeForceAgent.clearDistance ) {
	 * long finish = System.currentTimeMillis();
	 * System.out.println(" he cleared a blockade in  " + (finish - start)
	 * + "  ms");
	 * clear(bestBlockade);
	 * }
	 * log.info(" police force is now moving in this path " + path.getIds());
	 * long finish = System.currentTimeMillis();
	 * System.out
	 * .println(" he moved on path in  " + (finish - start) + "  ms");
	 * policeForceAgent.move(ids);
	 * }
	 */

}
