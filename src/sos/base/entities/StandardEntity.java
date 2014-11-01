package sos.base.entities;

import java.util.HashSet;

import rescuecore2.components.AbstractComponent;
import rescuecore2.components.AbstractViewer;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.AbstractEntity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.Property;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.move.Move;
import sos.base.move.types.SearchMove;
import sos.base.util.geom.ShapeInArea;
import sos.police_v2.PoliceForceAgent;

/**
 * Abstract base class for all standard entities.
 */
public abstract class StandardEntity extends AbstractEntity implements ShapeableObject {
	/* ///////////////////S.O.S instants////////////////// */
	private int lastSenseTime = 1;
	private int lastMsgTime = 1;
	private boolean isReallyReachable = false;
	private transient SOSAgent<? extends StandardEntity> agent;
	//	private boolean notValid;
	private int lastReachableTime;

	//	public int tempPoliceTaskWeight=0;
	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**
	 * Construct a StandardEntity with entirely undefined property values.
	 * 
	 * @param id
	 *            The ID of this entity.
	 */
	protected StandardEntity(EntityID id) {
		super(id);

	}

	// Please don't add any method here!!!!!!
	/**
	 * StandardEntity copy constructor.
	 * 
	 * @param other
	 *            The StandardEntity to copy.
	 */
	protected StandardEntity(StandardEntity other) {
		super(other);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the location of this entity.
	 * 
	 * @param world
	 *            The world model to look up for entity references.
	 * @return The coordinates of this entity, or null if the location cannot be determined.
	 */
	public Pair<Integer, Integer> getLocation() {
		return null;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the URN of this entity type as an instanceof StandardEntityURN.
	 * 
	 * @return A StandardEntityURN.
	 */
	public abstract StandardEntityURN getStandardURN();

	// Please don't add any method here!!!!!!
	@Override
	public final String getURN() {
		return getStandardURN().toString();
	}

	// Please don't add any method here!!!!!!
	public SOSAgent<? extends StandardEntity> getAgent() {
		if (agent == null)
			agent = SOSAgent.currentAgent();
		return agent;
	}

	// Please don't add any method here!!!!!!
	public SOSWorldModel model() {
		return getAgent().model();
	}

	@SuppressWarnings("unchecked")
	public StandardWorldModel standardModel() {
		if (agent != null)
			return agent.model();
		if (AbstractComponent.currentComponent() instanceof SOSAgent)
			return model();
		else if (AbstractComponent.currentComponent() instanceof AbstractViewer)
		{
			return ((AbstractViewer<StandardWorldModel>) AbstractComponent.currentComponent()).model();
		}
		return null;
	}

	// Please don't add any method here!!!!!!
	public String fullDescription() {
		return toString();
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public void setLastSenseTime(int time) {
		this.lastSenseTime = time;
	}

	public int getLastSenseTime() {
		return this.lastSenseTime;
	}

	public void setLastMsgTime(int time) {
		this.lastMsgTime = time;
	}

	public int getLastMsgTime() {
		return this.lastMsgTime;
	}

	public boolean isLastKnowledgeFromMsg() {
		return lastMsgTime > lastSenseTime;
	}

	public int updatedtime() {
		return Math.max(lastMsgTime, lastSenseTime);
	}

	public boolean hasBeenSeen() {
		return lastSenseTime > 1;
	}

	@Override
	protected void firePropertyChanged(Property p, Object oldValue, Object newValue) {
		super.firePropertyChanged(p, oldValue, newValue);
		// setLastSenseTime(model().time());
	}

	/**
	 * @author Ali
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null)
			getAgent().sosLogger.agent.error("You can't check equality of a StatndEntity and null", new Error("You can't check equality of a StatndEntity and null"));

		if (!(o instanceof StandardEntity))
			getAgent().sosLogger.agent.error("You can't check equality of a StatndEntity and " + o.getClass(), new Error("You can't check equality of a StatndEntity and " + o.getClass()));
		return super.equals(o);
	}

	/**
	 * @author Ali
	 *         please override it if change is needed!!!!
	 */
	public int getMessageWeightForSending() {
		return 1;
	}

	/**
	 * @author Ali
	 *         please override it if change is needed!!!!
	 */
	public int getMessageMiddleManPriority_forPolice() {
		return 1;
	}

	/* ////////////////////End of S.O.S/////////////////// */

	public Area getAreaPosition() {//ali
		if (this instanceof Human)
			return ((Human) this).getPositionArea();
		if (this instanceof Area)
			return ((Area) this);
		if (this instanceof Blockade)
			return ((Blockade) this).getPosition();
		return null;
	}

	public Pair<? extends Area, Point2D> getPositionPair() {//ali
		if (this instanceof Human)
			return ((Human) this).getPositionPair();

		return new Pair<Area, Point2D>(this.getAreaPosition(), new Point2D(this.getLocation().first(), this.getLocation().second()));
	}

	public Point2D getPositionPoint() {// ali
		if (this instanceof Human)
			return ((Human) this).getPositionPoint();

		return new Point2D(this.getLocation().first(), this.getLocation().second());

	}

	public void setIsReallyReachable(boolean isReallyReachable) {
		if ((!this.isReallyReachable) && isReallyReachable)
			lastReachableTime = model().time();
		else if (!isReallyReachable)
			lastReachableTime = 1000;

		this.isReallyReachable = isReallyReachable;
	}

	public boolean isReallyReachable(boolean checkAgainIfUnReachable) {

		HashSet<StandardEntity> visibleEntities = getAgent().getVisibleEntitiesHash(StandardEntity.class);
		boolean forceCheck = false;
		if (visibleEntities.contains(this.getAreaPosition()))
			forceCheck = true;
		for (Area neighbor : this.getAreaPosition().getNeighbours()) {
			if (visibleEntities.contains(neighbor))
				forceCheck = true;
		}

		if ((!isReallyReachable && checkAgainIfUnReachable) || forceCheck) {
			boolean oldReachablity = isReallyReachable;
			if ((!(getAgent() instanceof PoliceForceAgent)) && this instanceof Civilian && getAreaPosition() instanceof Road) {//check reachablity of civilian in road with checking the whole road because for loading it needs to only be in the road place
				isReallyReachable = !model().sosAgent().move.isReallyUnreachable(new ShapeInArea(getAreaPosition().getApexList(), getAreaPosition()));
			} else
				isReallyReachable = model().sosAgent().move.isReallyReacahble(getPositionPair());

			if (oldReachablity && !isReallyReachable) {
				boolean isReallyUnreachable;
				if (getAgent() instanceof PoliceForceAgent)
					isReallyUnreachable = model().sosAgent().move.isReallyUnreachableXYPolice(getPositionPair());
				else
					isReallyUnreachable = model().sosAgent().move.isReallyUnreachableXY(getPositionPair());
				if (!isReallyUnreachable)
					isReallyReachable = true;
			}
			if (isReallyReachable) {
				if (!oldReachablity)
					lastReachableTime = model().time();
			} else
				lastReachableTime = 1000;
		}
		return isReallyReachable;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	public int getLastReachableTime() {
		return lastReachableTime;
	}

	public boolean isReallyReachableSearch() {//Salim
		if (getAgent() instanceof PoliceForceAgent)
			return !(model().sosAgent().move.getWeightTo(getPositionPair().first(), getPositionPair().second().getIntX(), getPositionPair().second().getIntY(), SearchMove.class) > Move.UNREACHABLE_COST);
		else
			return isReallyReachable(true);
	}

	@Override
	public EntityID getID() {
		//		if (notValid) {
		//			Error error = new Error("don't use get id for invalid objects...");
		//			for (StackTraceElement trace : error.getStackTrace()) {
		//				if (trace.getClass().isInstance(StandardEntity.class) &&
		//						!(trace.getMethodName().contains("fullDescription") || trace.getMethodName().contains("toString"))) {
		//					error.printStackTrace();
		//				}
		//			}
		//		}
		return super.getID();
	}
}