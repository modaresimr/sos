package sos.police_v2.base;

import java.awt.Point;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.xml.soap.SOAPException;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.messages.AKClear;
import rescuecore2.standard.messages.AKClearArea;
import rescuecore2.worldmodel.EntityID;
import sos.base.PlatoonAgent;
import sos.base.SOSConstant.AgentType;
import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.entities.PoliceForce;
import sos.base.entities.StandardEntity;
import sos.base.move.Path;
import sos.base.move.types.PoliceMove;
import sos.base.util.SOSActionException;
import sos.base.util.information_stacker.act.ClearAction;
import sos.base.util.information_stacker.act.ClearAreaAction;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.base.clearablePointToReachable.ClearablePointToReachable;
import sos.police_v2.base.clearablePointToReachable.GeoDegreeClearPointToReachable;
import sos.police_v2.base.worldModel.PoliceWorldModel;
import sos.search_v2.agentSearch.PoliceSearch;

/**
 * SOS police force agent.
 * 
 * @author Salim
 */
public abstract class AbstractPoliceForceAgent extends PlatoonAgent<PoliceForce> {
	protected static final String DISTANCE_KEY = "clear.repair.distance";
	public SOSLoggerSystem log;
	public int clearDistance;
	public Point lastClearPoint;
	public static int clearWidth = 2000;
	public String lastState = "";
	public String lastCycleState = "";
	public ClearablePointToReachable clearableToPoint;
	public GeoDegreeClearPointToReachable degreeClearableToPoint;
	
	@Override
	protected void preCompute() {
		super.preCompute();
		log = sosLogger.agent;
		clearDistance = config.getIntValue(DISTANCE_KEY);
		log.trace("clear distance=" + clearDistance);
		lastClearPoint = new Point();
		long s = System.currentTimeMillis();
		
		
		newSearch = new PoliceSearch(this);
		log.consoleInfo("Police Search Precompute got:" + (System.currentTimeMillis() - s) + "ms");
	}

	@Override
	protected void prepareForThink() {
		super.prepareForThink();
		//		if(time()<FREEZE_TIME)
		//			informationStacker.addInfo(new CycleInformations(time(), new NoAct(), getLocation()));
		/*
		 * if(time()>2 && informationStacker.getInformations(1)==null&&informationStacker.getInformations(2)==null){//FOR IF RUNED FROM OTHER CYCLE
		 * for (int i = 0; i < time()-1; i++) {
		 * informationStacker.addInfo(time(), new NoAct(), me().getPositionPair());
		 * }
		 * }
		 */
	}

	/**
	 * Send a clear command to the kernel.
	 * 
	 * @param time
	 *            The current time.
	 * @param target
	 *            The target road.
	 * @throws SOSActionException
	 * @throws SOAPException
	 */
	public void clear(Blockade blockade) throws SOSActionException {
		if (blockade == null) {
			log.error("a null blockade pass to clear...????");
			return;
		}
		informationStacker.addInfo(model(), new ClearAction(blockade));
		send(new AKClear(getID(), model().time(), blockade.getID()));
		throw new SOSActionException("Clear " + blockade);
	}

	public void clear(Point point) throws SOSActionException {
		lastClearPoint = point;
		if (point == null) {
			log.error("a null point pass to clear...????");
			return;
		}
		informationStacker.addInfo(model(), new ClearAreaAction(point));
		PoliceForceAgent agent;
		int clearRang = 10000;
		if (me().getAgent() instanceof PoliceForceAgent) {
			agent = (PoliceForceAgent) me().getAgent();
			clearRang = agent.clearDistance + 2000;
		}
		float zavie = (float) Math.atan2(point.getY() - me().getY(), point.getX() - me().getX());
		send(new AKClearArea(getID(), model().time(), (int) (me().getX() + clearRang * Math.cos(zavie)), (int) (me().getY() + clearRang * Math.sin(zavie))));
		throw new SOSActionException("Clear " + point);
	}

	@Override
	@Deprecated
	public void rest() throws SOSActionException {
		log.error(new Error("Resting????"));
		super.rest();
	}

	public boolean isReachableTo(Pair<? extends Area, Point2D> b) {
		//		return move.isReachable(me().getPositionPair(), b);
		boolean isReachable = !move.isReallyUnreachableXYPolice(b);
		log.info("is reachable to " + b + "?" + isReachable);
		return isReachable;
	}

	public void move(Path path) throws SOSActionException {
		move.move(path, PoliceMove.class);
	}

	@Override
	@Deprecated
	public void randomWalk() throws SOSActionException {
		super.randomWalk();
	}

	@Override
	@Deprecated
	public void randomWalk(boolean doDummyRandomWalk) throws SOSActionException {
		super.randomWalk(doDummyRandomWalk);
	}

	@Override
	@Deprecated
	protected void dummyRandomWalk() throws SOSActionException {
		log.error("this should no called!");
		List<EntityID> entities = move.getBfs().getDummyRandomWalkPath().getIds();
		if (entities.isEmpty())
			return;
		Path movePath = move.getPathTo(Arrays.asList((Area) model().getEntity(entities.get(entities.size() - 1))), PoliceMove.class);
		move(movePath);
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.POLICE_FORCE);
	}

	@Override
	public String toString() {
		return me().toString();
	}

	@Override
	public PoliceForce me() {
		return super.me();
	}

	@Override
	protected PoliceWorldModel createWorldModel() {
		return new PoliceWorldModel(this);
	}

	@Override
	public PoliceWorldModel model() {
		return (PoliceWorldModel) super.model();
	}

	@Deprecated
	public Pair<Integer, Integer> getLocation() {
		return me().getLocation();
	}

	public Pair<? extends Area, Point2D> getPositionPair() {
		return me().getPositionPair();
	}

	public Point2D getPositionPoint() {
		return me().getPositionPoint();
	}

	public StandardEntity getPosition() {
		return me().getPosition();
	}

	@Override
	public AgentType type() {
		return AgentType.PoliceForce;
	}

	/**
	 * @author Ali
	 *         This method provide a global access to SOSAgent
	 * @param agentClass
	 *            is type of expected Agent
	 * @return
	 */
	public static PoliceForceAgent currentAgent() {
		return currentAgent(PoliceForceAgent.class);
	}

}