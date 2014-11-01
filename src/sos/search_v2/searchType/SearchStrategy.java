package sos.search_v2.searchType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Human;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.move.MoveConstants;
import sos.base.move.types.DistanceMove;
import sos.base.move.types.PoliceReachablityMove;
import sos.base.move.types.SearchMove;
import sos.base.util.SOSActionException;
import sos.base.util.geom.ShapeInArea;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.agentSearch.AgentSearch;
import sos.search_v2.agentSearch.AgentSearch.SearchType;
import sos.search_v2.tools.SearchTask;
import sos.search_v2.tools.SearchTools;
import sos.search_v2.tools.SearchUtils;
import sos.search_v2.tools.cluster.ClusterData;
import sos.search_v2.tools.searchScore.AgentSearchScore;
import sos.search_v2.worldModel.SearchBuilding;
import sos.search_v2.worldModel.SearchWorldModel;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public abstract class SearchStrategy<E extends Human> {
	protected SOSAgent<E> me;
	protected SearchWorldModel<E> searchWorld;
	protected ClusterData myClusterData;
	public SearchTools<E> searchTools;
	public Collection<ShapeInArea> targets = new ArrayList<ShapeInArea>();
	protected final AgentSearchScore scoreFunction;
	protected final AgentSearch<?> agentSearch;

	public SearchStrategy(SOSAgent<E> me, SearchWorldModel<E> searchWorld, AgentSearchScore scoreFunction, AgentSearch<?> agentSearch) {
		this.me = me;
		this.searchWorld = searchWorld;
		this.scoreFunction = scoreFunction;
		this.agentSearch = agentSearch;
		this.myClusterData = searchWorld.getClusterData();
		searchTools = new SearchTools<E>(me, searchWorld, scoreFunction);

	}

	public String log(String st) {
		me.sosLogger.search.info("                 " + st);
		return st;
	}

	public boolean notReachableAndNotPolice(Collection<ShapeInArea> targets) {
		return (!(me instanceof PoliceForceAgent)) && me.move.isReallyUnreachableShapes(targets);
	}

	public boolean isReachable(Area a) {
		if (me.location().getID().equals(a.getID()))
			return true;
		if (me instanceof PoliceForceAgent)
			return !me.move.isReallyUnreachableXYPolice(a.getPositionPair());
		return !me.move.isReallyUnreachable(a);
	}

	public boolean isReachable(ShapeInArea a) {
		return isReachable(Arrays.asList(a));
	}
	public boolean isReachable(List<ShapeInArea> a) {
		if (me instanceof PoliceForceAgent)
			return me.move.getWeightToLowProcess(a,PoliceReachablityMove.class)<MoveConstants.UNREACHABLE_COST;
		return !me.move.isReallyUnreachableShapes(a);
	}
	public long getWeightTo(List<ShapeInArea> a) {
		if (me instanceof PoliceForceAgent)
			return me.move.getWeightToLowProcess(a,DistanceMove.class);
		return me.move.getWeightToLowProcess(a,SearchMove.class);
	}
	public boolean isReachable(SearchBuilding b) {
		if (me.location().getID().equals(b.getRealBuilding().getID()))
			return true;
		if (me instanceof PoliceForceAgent)
			return !me.move.isReallyUnreachableXYPolice(new Pair<Area, Point2D>(b.getRealBuilding(), new Point2D(b.getRealBuilding().getX(), b.getRealBuilding().getY())));
		return !me.move.isReallyUnreachable(b.getRealBuilding());
	}

	public abstract SearchTask searchTask() throws SOSActionException;

	public void moveToShapes(Collection<ShapeInArea> targets) throws SOSActionException {
		this.targets = targets;
		me.move.moveToShape(targets, SearchMove.class);
	}

	protected long getWeightTo(SearchBuilding a) {
		return SearchUtils.getWeightTo(a.getRealBuilding(), me);
	}

	public abstract SearchType getType();


	public SearchWorldModel<?> getSearchWorldMode() {
		return me.model().searchWorldModel;
	}

	protected boolean isNoComm() {
		return me.messageSystem.type == Type.NoComunication;
	}

	public void preSearch() {

	}
}
