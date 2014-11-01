package sos.search_v2.agentSearch;

import java.util.ArrayList;
import java.util.Collection;

import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.message.structure.channel.VoiceChannel;
import sos.base.move.types.SearchMove;
import sos.base.util.SOSActionException;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.searchType.SearchStrategy;
import sos.search_v2.searchType.SearchStrategyChooser;
import sos.search_v2.tools.SearchTask;
import sos.search_v2.tools.cluster.MapClusterType;
import sos.search_v2.tools.searchScore.AgentSearchScore;
import sos.search_v2.worldModel.SearchBuilding;
import sos.search_v2.worldModel.SearchWorldModel;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public abstract class AgentSearch<E extends Human> implements MessageXmlConstant {
	protected SOSAgent<E> me;
	public SearchStrategyChooser<E> strategyChooser;
	private SearchWorldModel<E> searchWorld;
	public SearchType searchType = SearchType.None;
	protected ArrayList<SearchStrategy<?>> searchTypes;

	public static final double CIV_HEAR_BASE_PROB_SCORE = 5;

	public enum SearchType {
		None, CombinedSearch, CivilianSearch, FireSearch, BlockSearch, StarSearch, CivilianUpdateSearch, ClusterCombinedSearch, DummySearch,NoCommGatheringSearch;
	}

	public AgentSearch(SOSAgent<E> me, MapClusterType<E> clusterType, Class<? extends AgentSearchScore> score) {
		this.me = me;
		searchWorld=new SearchWorldModel<E>(me.model());
		me.model().searchWorldModel=searchWorld;
		searchWorld.cluster(clusterType);
		strategyChooser = new SearchStrategyChooser<E>(me, this, searchWorld, score);
		//Salim
		searchTypes = new ArrayList<SearchStrategy<?>>();
		initSearchOrder();
	}
	
	public abstract void initSearchOrder();

	public void preSearch() {
		long start = System.currentTimeMillis();
		log("presearch started!");
		for (SearchStrategy<?> ss : searchTypes) {
			ss.preSearch();
		}
		log("presearch finished time:" + (System.currentTimeMillis() - start) + "ms");
	}

	public void search() throws SOSActionException {
		//		preSearch();
		doActs(searchTypes.toArray(new SearchStrategy<?>[0]));
		moveToASafeArea();
	}

	public void doActs(SearchStrategy<?>... all) throws SOSActionException {
		if (me instanceof PoliceForceAgent)
			log().error(new Error(me + " cant use this function"));
		SearchTask task = doTaskActs(all);
		if (task != null)
			moveToShapes(task.getArea());
	}

	public void moveToShapes(Collection<ShapeInArea> targets) throws SOSActionException {
		me.move.moveToShape(targets, SearchMove.class);
	}

	public SearchTask doTaskActs(SearchStrategy<?>... all) {
		//		if (!(me instanceof PoliceForceAgent))
		//			throw new Error(me + " cant use this function");
		me.sosLogger.act.info("=========Search==========" + all);
		for (SearchStrategy<?> ss : all) {
			long start = System.currentTimeMillis();
			log("search " + ss.getClass().getSimpleName() + " started!");
			try {
				searchType = ss.getType();
				SearchTask st = ss.searchTask();
				if (st != null) {
					me.abstractStateLogger.logln(me.time()+":"+ss.getClass().getSimpleName()+"\t\t\t : target="+st+"\t\t\t :" + (System.currentTimeMillis() - start) + "ms");
					me.sosLogger.act.info("search finished" + ss.getClass().getSimpleName() + " return:" + st + " time:" + (System.currentTimeMillis() - start) + "ms");
					log("finished search " + ss.getClass().getSimpleName() + " return:" + st + " time:" + (System.currentTimeMillis() - start) + "ms");
					return st;
				}
			} catch (Exception e) {
				me.sosLogger.error(e);
			}
			me.sosLogger.act.info("search do nothing " + ss.getClass().getSimpleName() + " time:" + (System.currentTimeMillis() - start) + "ms");
			log("search " + ss.getClass().getSimpleName() + " time:" + (System.currentTimeMillis() - start) + "ms");
		}
		return null;
	}

	public SearchTask searchTask() {
		//		preSearch();
		return doTaskActs(searchTypes.toArray(new SearchStrategy<?>[0]));
	}

	public void moveToASafeArea() throws SOSActionException {
		if (!me.model().refuges().isEmpty()) {
			if (me.me().getPositionArea() instanceof Refuge)
				me.rest();
			me.move.moveStandard(me.model().refuges());
		} else if (me.me().getPositionArea() instanceof Building)
			me.move.moveStandard(me.model().roads());
		me.rest();
	}

	protected SearchStrategy<E> getStrategy() {
		return strategyChooser.getBestStrategy();

	}

	public SOSLoggerSystem log() {
		return me.sosLogger.search;
	}

	public void log(String st) {
		me.sosLogger.search.info(st);
	}

	public void hear(String header, DataArrayList data, StandardEntity sender, Channel channel) {
		if (sender instanceof Civilian) {
			me.sosLogger.search.info("say shenidam " + sender);
			if (!((Civilian) sender).isPositionDefined()) {
				if (channel instanceof VoiceChannel) {
					Collection<Building> buildingsInRange = me.model().getObjectsInRange(me.me().getX(), me.me().getY(), ((VoiceChannel) channel).getRange(), Building.class);//TODO
					SearchBuilding temp;
					for (Building b : buildingsInRange) {
						temp = getSearchWorld().getSearchBuildings().get(b.getBuildingIndex());
						try{
						temp.addCivProbability((Civilian)sender,CIV_HEAR_BASE_PROB_SCORE / buildingsInRange.size());
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					me.sosLogger.search.warn(channel + " is not voice channel");
				}
			}
		}
	}


	public SearchWorldModel<E> getSearchWorld() {
		return searchWorld;
	}

}
