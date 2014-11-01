package sos.search_v2.searchType;

import java.lang.reflect.InvocationTargetException;

import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Human;
import sos.base.util.geom.ShapeInArea;
import sos.search_v2.agentSearch.AgentSearch;
import sos.search_v2.tools.searchScore.AgentSearchScore;
import sos.search_v2.worldModel.SearchWorldModel;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public class SearchStrategyChooser<E extends Human> {
	public Area target;
	public ShapeInArea shapeTarget;
	public DummySearch<E> dummySearch;
	public SearchStrategy<?> noCommGathering;

	public SearchStrategyChooser(SOSAgent<E> me, AgentSearch<?> agentSearch, SearchWorldModel<E> searchWorld, Class<? extends AgentSearchScore> scoreClass) {

		try {
			AgentSearchScore score = scoreClass.getConstructor(new Class[] { SOSAgent.class }).newInstance(me);
//			starSearch = new StarSearch<E>(me, searchWorld, StarSearchType.RJS_THRESHOLD, score, agentSearch);

			dummySearch=new DummySearch<E>(me, searchWorld, score, agentSearch);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public SearchStrategy<E> getBestStrategy() {
		return dummySearch;//TODO
	}
}
