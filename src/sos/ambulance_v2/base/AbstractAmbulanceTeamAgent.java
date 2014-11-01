package sos.ambulance_v2.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.messages.AKLoad;
import rescuecore2.standard.messages.AKRescue;
import rescuecore2.standard.messages.AKUnload;
import rescuecore2.worldmodel.EntityID;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.PlatoonAgent;
import sos.base.SOSConstant.AgentType;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.move.MoveConstants;
import sos.base.move.Path;
import sos.base.move.types.StandardMove;
import sos.base.util.SOSActionException;
import sos.base.util.SOSGeometryTools;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.information_stacker.act.LoadAction;
import sos.base.util.information_stacker.act.RescueAction;
import sos.base.util.information_stacker.act.UnloadAction;
import sos.base.util.sosLogger.SOSLoggerSystem;

/**
 * SOS ambulance team agent.
 */
public abstract class AbstractAmbulanceTeamAgent extends PlatoonAgent<AmbulanceTeam> {
	public Human getLoadingInjured() {
		for (Civilian next : getVisibleEntities(Civilian.class)) {
			if (next.isPositionDefined() && next.getPosition().equals(me())) {
				return next;
			}
		}
		return null;
	}

	public boolean isLoadingInjured() {
		return getLoadingInjured() != null;
	}

	public Human underMissionTarget() {
		return me().getWork().getTarget();
	}

	public boolean canSeeTheCenterOfBuilding(Point2D p1, Area area) {
		return (SOSGeometryTools.getDistance(p1, area.getPositionPoint()) < VIEW_DISTANCE);
	}

	@Override
	protected void prepareForThink() {
		super.prepareForThink();
	}

	public boolean isFull() {
		return isLoadingInjured();
	}

	/***********************************************************/

	/***********************************************************/
	/**
	 * Send a rescue command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param target
	 *            The target human.
	 * @throws SOSActionException
	 */
	public void rescue(Human target) throws SOSActionException {
		sosLogger.debug("Rescuing " + target);
		informationStacker.addInfo(model(), new RescueAction(target));
		send(new AKRescue(getID(), model().time(), target.getID()));
		throw new SOSActionException("Rescue(" + target + ")");
	}

	/**
	 * Send a load command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @param target
	 *            The target human.
	 * @throws SOSActionException
	 */
	public void load(Human target) throws SOSActionException {
		sosLogger.debug("Loading " + target);
		//		m_loadingInjured=target;

		informationStacker.addInfo(model(), new LoadAction(target));
		send(new AKLoad(getID(), model().time(), target.getID()));
		throw new SOSActionException("Load(" + target + ")");
	}

	/**
	 * Send an unload command to the kernel.
	 *
	 * @param time
	 *            The current time.
	 * @throws SOSActionException
	 */
	public void unload() throws SOSActionException {
		sosLogger.debug("Unloading ");
		informationStacker.addInfo(model(), new UnloadAction());
		send(new AKUnload(getID(), time()));
		throw new SOSActionException("Unload");
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.AMBULANCE_TEAM);
	}

	@Override
	public String toString() {
		return me().toString();
	}

	@Override
	public AgentType type() {
		return AgentType.AmbulanceTeam;
	}

	@Override
	public AmbulanceTeam me() {
		return super.me();
	}


	private void moveToEntranceOfArea(Area dest) throws SOSActionException {
		if (dest == null) {
			sosLogger.agent.error("why try to move to " + dest + "?????");
			randomWalk(true);
		}

		Path path = move.getPathTo(Collections.singleton(dest), StandardMove.class);
		ArrayList<EntityID> pathArray = path.getIds();
		if (pathArray.isEmpty()) {
			sosLogger.agent.error("path is empty. why????");
			move.moveStandard(dest);
		}

		Edge inComingEdge;
		if (pathArray.size() == 1) {
			inComingEdge = dest.getEdgeTo(location());
		} else {
			inComingEdge = dest.getEdgeTo(pathArray.get(pathArray.size() - 2));
		}
		Line2D wallLine = inComingEdge.getLine();// new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());

		Vector2D offset;
		if (AliGeometryTools.havecorrectDirection(dest)) {
			offset = wallLine.getDirection().getNormal().normalised().scale(MoveConstants.ENTRACE_DISTANCE_MM);
		} else {
			offset = wallLine.getDirection().getNormal().normalised().scale(-1 * MoveConstants.ENTRACE_DISTANCE_MM);
		}
		Point2D destXY = inComingEdge.getMidPoint().plus(offset);
		path.setDestination(new Pair<Area, Point2D>(dest, destXY));
		if (dest.getShape().contains(destXY.toGeomPoint()) && (dest instanceof Refuge || canSeeTheCenterOfBuilding(destXY, dest)))
			move.move(path);
		else
			move.moveStandard(dest);
	}

	public SOSLoggerSystem log() {
		return sosLogger.agent;
	}

	public void moveToRefuges() throws SOSActionException {
		if (model().refuges().isEmpty()) {
			log().error("Move to refuge in no refuge map????");
			return;
		}
		Refuge bestRefuge = findBestRefuge();
		moveToRefuge(bestRefuge);
	}

	//-------------------------------------------------------------------------------------
	private Refuge findBestRefuge() {
		Refuge minRef = null;
		long minCost = Long.MAX_VALUE;
		for (Refuge ref : model().refuges()) {
			long w = move.getWeightTo(ref, ref.getX(), ref.getY(), StandardMove.class);
			if (w < minCost) {
				minCost = w;
				minRef = ref;
			}
		}
		return minRef;
	}

	public void moveToRefuge(Refuge bestRefuge) throws SOSActionException {
		moveToEntranceOfArea(bestRefuge); // Transporting Move
	}

	public void moveTo(Human target) throws SOSActionException {
		if (target == null || !target.isPositionDefined()) {
			sosLogger.agent.error("why try to move to " + target + "?????");
			randomWalk(true);
		}
		if (target.getPosition() instanceof Area) {
			Area dest = (Area) target.getPosition();
			if (location().equals(dest)) {
				sosLogger.agent.error("You are on " + dest + " why you want to move there????");
				move.moveStandard(dest);
			}
			//			move.moveStandard(dest);
			moveToEntranceOfArea(dest);

		}
		sosLogger.agent.error("Why you are moving to an agent that is in an ambulance???");
	}

	@Override
	public void randomWalk(boolean doDummyRandomWalk) throws SOSActionException {
		List<Area> result = new ArrayList<Area>();

		for (Building building : model().buildings()) {
			if (building.updatedtime() < 2)
				for (Area area : building.getNeighbours()) {
					if (!move.isReallyUnreachable(area))
						result.add(area);
				}
		}

		if (result.isEmpty())
			super.randomWalk(true);
		move.moveStandard(result);
	}

	/**
	 * @author Ali
	 *         This method provide a global access to SOSAgent
	 * @param agentClass
	 *            is type of expected Agent
	 * @return
	 */
	public static AmbulanceTeamAgent currentAgent() {
		return currentAgent(AmbulanceTeamAgent.class);
	}

	public void finishTasksState() throws SOSActionException {
		lastState = " search ";
		search();

		lastState = " randomWalk ";
		randomWalk(true);

	}

}