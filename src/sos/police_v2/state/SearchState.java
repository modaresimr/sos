package sos.police_v2.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Building;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.util.SOSActionException;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.tools.SearchTask;

public class SearchState extends PoliceAbstractState {

	public SearchState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	@Override
	public void act() throws SOSActionException {
		log.info("acting as:" + this.getClass().getSimpleName());
		//		randomSearch();
		if(agent.isTimeToActFinished())
			randomSearch();
		SearchTask task = agent.newSearch.searchTask();
		handleTask(task);
		
		randomSearch();

		//handleTask(agent.mySearch.chooseStrategyAndSearch());
	}


	private void randomSearch() throws SOSActionException {
		log.info("policeRandomWalk");
		ArrayList<StandardEntity> result = new ArrayList<StandardEntity>();
		Collection<Road> roads = model().getObjectsInRange(agent.me(),(int) model().getBounds().getWidth()/5, Road.class);
		for (Road road : roads) {
			if (road.updatedtime() < 2)
				result.add(road);
		}
		log.debug("road that has not updated=" + result);
		if (result.isEmpty()) {
			Collection<Building> buildings= model().getObjectsInRange(agent.me(),(int) model().getBounds().getWidth()/5, Building.class);
			
			for (Building building : buildings) {
				if (!building.isSearchedForCivilian())
					result.add(building);
			}
			log.debug("unupdated roads are empty! building that has not updated=" + result);
		}
		StandardEntity dstEntity;
		if (result.isEmpty()) {
			log.debug("all entities are updated!!! now we are doing a dummy random walk");
			List<EntityID> a = agent.move.getBfs().getDummyRandomWalkPath().getIds();
			dstEntity = model().getEntity(a.get(a.size() - 1));
			result.add(dstEntity.getAreaPosition());
		}
		//		dstEntity = result.get(0);
		//		log.debug("Task "+dstEntity+" choosed...");
		if(!result.isEmpty())
			makeReachableTo(result.get(0));
	}

	public void handleTask(SearchTask task) throws SOSActionException {
		log.debug("Handling task " + task);
		if (task == null) {
			log.warn("Noting to do in Search???");
			return;
		} else {
			moveToShape(task.getArea());
		}
	}

	@Override
	public void precompute() {
		// TODO Auto-generated method stub
		
	}

}
