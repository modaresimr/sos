package sos.police_v2.state;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.move.MoveConstants;
import sos.base.move.Path;
import sos.base.move.types.PoliceMove;
import sos.base.move.types.PoliceReachablityMove;
import sos.base.util.SOSActionException;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.police_v2.PoliceConstants;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;
import sos.police_v2.base.worldModel.PoliceWorldModel;
import sos.police_v2.clearableBlockadeToReachable.FullReachabling;
import sos.police_v2.clearableBlockadeToReachable.GeoPathReachablity;
import sos.police_v2.clearableBlockadeToReachable.ModifiedPathToMov;
import sos.police_v2.clearableBlockadeToReachable.ReachablingUsingReachablityTool;

public abstract class PoliceAbstractState {

	protected final GeoPathReachablity reachableWithEdge;
	protected final FullReachabling clearableBlockadeToFullReachable;
	protected final ReachablingUsingReachablityTool reachablityTool;
	public ModifiedPathToMov modifiedPathToMove;
	protected final PoliceForceAgent agent;
	protected boolean isFinished = false;
	public final SOSLoggerSystem log;

	public PoliceAbstractState(PoliceForceAgent policeForceAgent) {
		this.agent = policeForceAgent;
		log = policeForceAgent.log;
		reachablityTool = new ReachablingUsingReachablityTool(policeForceAgent);
		reachableWithEdge = new GeoPathReachablity(policeForceAgent);
		clearableBlockadeToFullReachable = new FullReachabling(policeForceAgent);
		modifiedPathToMove = new ModifiedPathToMov(policeForceAgent);

	}
	public abstract void precompute();
	public abstract void act() throws SOSActionException;

	public boolean isFinished() {
		return isFinished;
	}

	protected PoliceWorldModel model() {
		return agent.model();
	}

	public void move(Path path) throws SOSActionException {
		log.info("moving to path: " + path);
		if (path == null) {
			log.error("path is null!!!");
			moveToARandomArea();
		}
		justMakeReachable(path);
		
		if(PoliceConstants.NEW_CLEAR_MOVE){
			Path newpath = modifiedPathToMove.getModifiedPathToMove(path);
			agent.move(newpath);
		}
		else
			agent.move(path);
	}

	
	private void moveWithGoodReachablity(Path path) throws SOSActionException {
		log.info("moving with good reachablity to path: " + path);
		justMakeReachableWithGoodReachablity(path);
		agent.move(path);
	}

	public void clearNearestBlockade() throws SOSActionException {
		log.info("clearing NearestBlockade");
		PriorityQueue<Blockade> blockadesInRange = model().getBlockadesInRange(agent.me().getX(), agent.me().getY(), agent.clearDistance);
		log.debug("Blockades in Range=" + blockadesInRange);
		Blockade selectedBlock = null;
		if (!blockadesInRange.isEmpty())
			selectedBlock = blockadesInRange.remove();
		log.debug("best blockade:" + selectedBlock);
		if (selectedBlock != null)
			clear(selectedBlock);

	}

	///////////////////////////basic actions//////////////////////////////////////////////////////////////////////////////
	protected void move(Area destination) throws SOSActionException {
		move(Collections.singleton(destination));
	}

	public void makeReachableTo(List<? extends StandardEntity> targets) throws SOSActionException {
		log.info("makeReachableTo " + targets);
		ArrayList<Pair<? extends Area, Point2D>> reachableAreas = new ArrayList<Pair<? extends Area, Point2D>>();
		for (StandardEntity entity : targets) {
			if (entity == null) {
				log.error("A null entity passed to makeReachableTo!!! WHY?");
				continue;
			}
			if (entity.getPositionPair() == null) {
				log.error("A null position entity(" + entity + ") passed to makeReachableTo!!! WHY?");
				continue;
			}
			if (!isReachableTo(entity)) {
				reachableAreas.addAll(PoliceUtils.getReachableAreasPair(entity));
			}
		}
		log.debug("reachableAreas = " + reachableAreas);
		if (!reachableAreas.isEmpty()) {
			moveToPoint(reachableAreas);
		}
	}

	public void makeReachableTo(StandardEntity... targets) throws SOSActionException {
		makeReachableTo(Arrays.asList(targets));
	}

	protected void move(Collection<? extends Area> destination) throws SOSActionException {
		log.info("moving to: " + destination);
		if (destination.isEmpty()) {
			log.error("Move to empty destination????? random move....");
			moveToARandomArea();
		}

		Path path = agent.move.getPathTo(destination, PoliceMove.class);
		move(path);
	}

	public void moveToARandomArea() throws SOSActionException {

		Road randomDST = model().roads().get((int) (Math.random() * (model().roads().size() - 1)));
		ArrayList<Area> area = new ArrayList<Area>();
		area.add(randomDST);
		Path path = agent.move.getPathTo(area, PoliceMove.class);
		move(path);

	}

	protected void moveToPoint(Pair<? extends Area, Point2D> destinations) throws SOSActionException {
		ArrayList<Pair<? extends Area, Point2D>> dests = new ArrayList<Pair<? extends Area, Point2D>>();
		dests.add(destinations);
		moveToPoint(dests);
	}

	protected void moveToPoint(Collection<Pair<? extends Area, Point2D>> destinations) throws SOSActionException {

		log.info("moving to: " + destinations);
		if (destinations.isEmpty()) {
			log.error("Move to empty destination????? random move....");
			moveToARandomArea();
		}

		Path path = agent.move.getPathToPoints(destinations, PoliceMove.class);
		move(path);
	}

	@SuppressWarnings("unused")
	private void moveToPointWithGoodReachablity(ArrayList<Pair<? extends Area, Point2D>> destinations) throws SOSActionException {
		log.info("moving to: " + destinations);
		if (destinations.isEmpty()) {
			log.error("Move to empty destination????? random move....");
			moveToARandomArea();
		}

		Path path = agent.move.getPathToPoints(destinations, PoliceMove.class);
		moveWithGoodReachablity(path);
	}

	protected void moveXY(Area destination, int x, int y) throws SOSActionException {
		log.info("moving to: " + destination + " x:" + x + " y:" + y);
		Pair<? extends Area, Point2D> dest = new Pair<Area, Point2D>(destination, new Point2D(x, y));
		ArrayList<Pair<? extends Area, Point2D>> dests = new ArrayList<Pair<? extends Area, Point2D>>();
		dests.add(dest);
		Path path = agent.move.getPathToPoints(dests, PoliceMove.class);
		move(path);
	}

	public void moveToShape(Collection<ShapeInArea> area) throws SOSActionException {
		log.info("moving to shape in areas: " + area);
		Path path = agent.move.getPathToShapes(area, PoliceMove.class);
		move(path);
	}

	protected void clear(Blockade blockade) throws SOSActionException {
		log.info("clearing " + blockade);
		agent.clear(blockade);
	}

	protected void clear(Point point) throws SOSActionException {
		agent.clear(point);
	}

	private void justMakeReachableWithGoodReachablity(Path path) throws SOSActionException {
		log.info("making reachable with good reachablity to " + path);

		ArrayList<Blockade> blocks = reachablityTool.getBlockingBlockadeOfPath(path);
		log.debug("clearableBlockadeToReachablebyReachablityTool is " + blocks);
		ArrayList<Blockade> blockadeEdge = reachableWithEdge.getBlockingBlockadeOfPath(path);
		blocks.addAll(blockadeEdge);

		log.debug("clearableBlockadeToReachableWithEdge is " + blocks);

		Blockade clearBlock = chooseBestBlockade(blocks);

		if (clearBlock != null)
			clear(clearBlock);
		else if (clearBlock == null && !blocks.isEmpty()) {
			log.debug("No blockade is in range but the path is close!!! so move to close area");
			ArrayList<Pair<? extends Area, Point2D>> dest = new ArrayList<Pair<? extends Area, Point2D>>();
			for (Blockade blockade : blocks) {
				dest.add(blockade.getPositionPair());
			}
			path = agent.move.getPathToPoints(dest, PoliceMove.class);
			agent.move(path);
		} else
			log.debug("No Blockade found---> we can move");

	}

	private void justMakeReachable(Path path) throws SOSActionException {
		log.info("making reachable to " + path);
		if (!PoliceConstants.IS_NEW_CLEAR) {

			ArrayList<Blockade> blocks = new ArrayList<Blockade>();

			ArrayList<Blockade> blockadeEdge = reachableWithEdge.getBlockingBlockadeOfPath(path);
			blocks.addAll(blockadeEdge);
			//
			if (blocks.isEmpty()) {
				blocks.addAll(reachablityTool.getBlockingBlockadeOfPath(path));
				log.debug("clearableBlockadeToReachablebyReachablityTool is " + blocks);
				//	ArrayList<Blockade> blockadeEdge = clearableBlockadeToFullReachable.getBlockingBlockadeOfPath(path);
			}
			log.debug("clearableBlockadeToReachableWithEdge is " + blocks);

			Blockade clearBlock = chooseBestBlockade(blocks);

			if (clearBlock != null)
				clear(clearBlock);
			else
				log.debug("No Blockade found---> we can move");
		} else {
			Point next = agent.degreeClearableToPoint.nextPointToClear(path, true, true);
			if (next != null) {
				//				log.warn("clear at" + next);
				clear(next);
			}
		}
	}

	protected Blockade chooseFirstBlockade(List<Blockade> list) {
		log.debug("Choosing first blockade from list(" + list + ")");
		if (list == null) {
			log.warn("clearableBlockades is null!!! it shouldn't be happened!");
			return null;
		}
		for (Blockade blockade : list) {
			if (PoliceUtils.isValid(blockade)) {
				log.info(" first valid blockade is " + blockade);
				return blockade;
			}
		}
		log.info(" can't choos a blockade ");
		return null;
	}

	protected Blockade chooseBestBlockade(List<Blockade> list) {
		log.debug("Choosing best blockade from list(" + list + ")");
		if (list == null) {
			log.warn("clearableBlockades is null!!! it shouldn't be happened!");
			return null;
		}
		Blockade best = null;
		double bestValue = 0;
		for (Blockade blockade : list) {
			if (PoliceUtils.isValid(blockade)) {
				int value = computeBlockadeValue(blockade);
				if (value > bestValue) {
					bestValue = value;
					best = blockade;
				}
			}
		}
		log.info(" best blockade is " + best);
		return best;
	}

	private int computeBlockadeValue(Blockade blockade) {
		return (int) (1000000 / Math.max(agent.me().getPositionPoint().distance(blockade.getCenteroid()), 1));
		//return 100000 / blockade.getRepairCost();
		//TODO
		// moteallegh be kodam sakhteman hastand.(CV)

	}

	//	public boolean isReachable(Pair<? extends Area, Point2D> a, Pair<? extends Area, Point2D> b) {
	//		return agent.move.isReachable(a, b);
	//	}

	public boolean isReachableTo(StandardEntity se) {
		if (se instanceof Human) {
			Human hum = (Human) se;
			if (agent.getVisibleEntities(Human.class).contains(hum) && hum.isPositionDefined() && hum.getAreaPosition().isBlockadesDefined()) {
				for (Blockade blockade : hum.getAreaPosition().getBlockades()) {
					if (blockade.getShape().contains(hum.getPositionPoint().toGeomPoint()))
						return false;
				}
			}
			if(!hum.getImReachableToEdges().isEmpty())
				return agent.move.getMoveType(PoliceReachablityMove.class).isReallyReachableTo(hum.getImReachableToEdges());

		}
		return isReachableTo(se.getPositionPair());
	}

	public boolean isReachableTo(Pair<? extends Area, Point2D> b) {
		return agent.isReachableTo(b);
	}

	//	public boolean isReallyUnReachableTo(Pair<? extends Area, Point2D> positionPair) {
	//		return agent.move.isReallyUnreachableXY(positionPair.first(), (int)positionPair.second().getX(), (int)positionPair.second().getY());
	//	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	public Pair<Area, Point2D> getEntrancePoint(Area dest) {
		if (dest == null) {
			log.error("why try to move to " + dest + "?????");
			return null;
		}
		Path path = agent.move.getPathTo(Collections.singleton(dest), PoliceMove.class);
		ArrayList<EntityID> pathArray = path.getIds();
		if (pathArray.isEmpty()) {
			log.error("path is empty. why????");
			return null;
		}

		Edge inComingEdge;
		if (pathArray.size() == 1) {
			inComingEdge = dest.getEdgeTo(agent.location());
		} else {
			inComingEdge = dest.getEdgeTo(pathArray.get(pathArray.size() - 2));
		}
		if (inComingEdge == null)
			return new Pair<Area, Point2D>(dest, dest.getPositionPoint());
		Line2D wallLine = inComingEdge.getLine();// new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());

		Vector2D offset;
		if (AliGeometryTools.havecorrectDirection(dest)) {
			offset = wallLine.getDirection().getNormal().normalised().scale(10);
		} else {
			offset = wallLine.getDirection().getNormal().normalised().scale(-10);
		}
		Point2D destXY = inComingEdge.getMidPoint().plus(offset);
		return new Pair<Area, Point2D>(dest, destXY);
	}

	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {

	}

	public int getTimeToTarget(Path path) {
		if (path.getLenght() < agent.clearDistance)
			return 0;
		return ((path.getLenght() - agent.clearDistance) / MoveConstants.AVERAGE_MOVE_PER_CYCLE) + 1;
	}

}