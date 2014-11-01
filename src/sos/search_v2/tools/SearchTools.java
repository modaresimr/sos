package sos.search_v2.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.base.entities.StandardEntity;
import sos.base.move.MoveConstants;
import sos.base.move.types.DistanceMove;
import sos.base.move.types.PoliceReachablityMove;
import sos.base.move.types.SearchMove;
import sos.base.util.geom.ShapeInArea;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.tools.searchScore.AgentSearchScore;
import sos.search_v2.worldModel.SearchBuilding;
import sos.search_v2.worldModel.SearchWorldModel;

public class SearchTools<E extends Human> {
	private static final int FIRE_PROBABLITY_DISTANCE = 50000;//FIXME tune it in large maps
	private static final int FIRE_PROBABLITY_TIME = 5; //FIXME tune for big and small maps
	private SOSAgent<E> me;
	private SearchWorldModel<E> searchWorld;
	private AgentSearchScore scoreFunction;

	public SearchTools(SOSAgent<E> me, SearchWorldModel<E> searchWorld, AgentSearchScore scoreFunction) {
		this.me = me;
		this.searchWorld = searchWorld;
		this.scoreFunction = scoreFunction;
	}

	public ArrayList<Building> getVisibleBuilding() {
		//		ArrayList<Building> res = new ArrayList<Building>();
		//		for (StandardEntity e : me.getVisibleEntities()) {
		//			if (e instanceof Building)
		//				res.add((Building) e);
		//		}
		return me.getVisibleEntities(Building.class);
	}

	public ArrayList<Building> getInSideVisibleBuilding() {
		ArrayList<Building> res = new ArrayList<Building>();
		for (Building e : getVisibleBuilding()) {
			for (ShapeInArea sh : e.getSearchAreas()) {
				if (sh.getArea(me.model()).equals(me.me().getAreaPosition())) {
					if (sh.contains(me.me().getX(), me.me().getY())) {//tush ro mibinam
						res.add(e);
					}
				}
			}
		}
		return res;
	}

	public Collection<Building> getBuildingsInHearDistance() {
		Collection<Building> buildingsInRange = me.model().getObjectsInRange(me.me().getX(), me.me().getY(), me.messageSystem.getMessageConfig().voiceChannels().get(0).getRange(), Building.class);
		return buildingsInRange;
	}

	public ArrayList<Building> getUpdatedBuilding() {
		ArrayList<Building> res = new ArrayList<Building>();
		for (Building b : me.model().buildings()) {
			if (b.updatedtime() > 5) {
				res.add(b);
			}
		}
		return res;
	}

	public HashSet<Building> getUpdatedBuildingNeighbour() {
		HashSet<Building> res = new HashSet<Building>();
		for (Building b : me.model().buildings()) {
			if (me.model().time() - b.updatedtime() < 5) {
				for (Building b2 : b.realNeighbors_Building()) {
					if (!res.contains(b2))
						res.add(b2);
				}
			}
		}
		return res;
	}

	//	public ArrayList<Civilian> getCivilian(SearchBuilding b) {
	//		ArrayList<Civilian> res = new ArrayList<Civilian>();
	//		Collection<Civilian> cives = me.model().civilians()/* me.model().getObjectsInRectangle(b.getRealBuilding().getShape().getBounds(), Civilian.class) */;
	//		for (Civilian c : cives)
	//			if (c.getPosition() != null) {
	//				if (c.getPosition() == b.getRealBuilding())
	//					res.add(c);
	//			}
	//		//		ArrayList<Civilian> res2 = new ArrayList<Civilian>();
	//		//		Collection<Civilian> cives2 = me.model().civilians();
	//		//		for (Civilian c : cives2)
	//		//			if (c.isPositionDefined() && c.getPosition().equals(b.getRealBuilding()))
	//		//				res.add(c);
	//		//		if(res2.size()!=res.size())
	//		//			System.err.println("DDDD");
	//		return res;
	//	}

	public void log(String st) {
		me.sosLogger.search.info("IN SearchTools  ::;:::;;;:;;>   " + st);
	}

	public ArrayList<Building> getBuildingWithFireProbability() { //FIXME BUG:all agents will gather in one place. 
		ArrayList<Building> res =me.fireProbabilityChecker.getProbabilisticFieryBuilding();/// new ArrayList<Building>();
		log("fire probability"+res);
//		for (Building b : me.model().buildings()) {
//			boolean t = haveFireProbability(b);
//			if (t) {
//				log("fire probability " + b);
//				ArrayList<Building> firePro = addNearFireProbability(b);
//				for (Building temp : firePro) {
//					if (res.contains(temp)) {
//						searchWorld.getSearchBuilding(temp).setSpecialForFire(true);
//					} else {
//						res.add(temp);
//						searchWorld.getSearchBuilding(temp).setSpecialForFire(false);
//					}
//				}
//			}
//		}
		return res;
	}

	private ArrayList<Building> addNearFireProbability(Building b) {
		ArrayList<Building> res = new ArrayList<Building>();
		ArrayList<Building> invalid = new ArrayList<Building>();
		for (Building near : b.realNeighbors_Building()) {
			if (me.model().time() - near.updatedtime() > 3 && b.updatedtime() > near.updatedtime() + 3 && (!near.virtualData[0].isBurning() && !(near.virtualData[0].getFieryness() >= 7) && near.getEstimator() == null)) {
				res.add(near);
				log("add fire building " + near);
			} else {
				invalid.add(near);
				log("invalid fire building " + near);
			}
		}

		for (Building k : invalid) {
			//			if (k.getTotalArea() > b.getTotalArea() * 4 && b.virtualData[0].getTemperature() < 10)
			//				continue;
			////			//			if (k.getTotalArea()* 10 > b.getTotalArea() )
			//				continue;

			for (Building k2 : k.realNeighbors_Building()) {
				//				if (k.getTotalArea() > k2.getTotalArea() * 4 && k.virtualData[0].getTemperature() < 10)
				//				if(k2.getFireBuilding().island()!=b.getFireBuilding().island())
				//					res.remove(k2);

				if (!(me.model().time() - k2.updatedtime() > 3 && b.updatedtime() > k2.updatedtime() + 3 && (!k2.virtualData[0].isBurning() && !(k2.virtualData[0].getFieryness() >= 7) && !isNearFire(k2)))) {
					res.remove(k2);
					log("remove kardam " + k2);
				}

			}
			//			res.removeAll(k.realNeighbors_Building());
		}
		return res;
	}

	private boolean haveFireProbability(Building b) {//FIXME YOUSEF rewrite
		if ((int) b.virtualData[0].getTemperature() == 0)
			return false;
		if (b.virtualData[0].getFieryness() >= 7 || b.virtualData[0].isBurning())
			return false;
		// Yousef mikhaste bege agar too chahar ccle ghab dide boodam, hata agar damasham bishtar az 0 hast va agar nazdik fire nist probable nist
		if ((me.model().time() - b.updatedtime() < FIRE_PROBABLITY_TIME))
			return false;
		if (isNearFire(b))
			return false;
		//		if (!b.haveFireProbability()) TODO should be added
		//			return false;
		return true;
	}

	private boolean isNearFire(Building b) { //TODO add temperature increase
		Collection<Building> objectsInRange = me.model().getObjectsInRange(b.getX(), b.getY(), FIRE_PROBABLITY_DISTANCE, Building.class);
		for (Building building : objectsInRange) {
			if (building.getSOSEstimateFireSite() != null) {
				return true;
			}
		}
		return false;
	}

	public PriorityQueue<Building> getCivilianSearchArea() {

		PriorityQueue<Building> toSearch = new PriorityQueue<Building>(50, new Comparator<Building>() {
			@Override
			public int compare(Building o1, Building o2) {
				return (int) (searchWorld.getSearchBuilding(o2).getScore() - searchWorld.getSearchBuilding(o1).getScore());
			}
		});

		for (Building b : searchWorld.getClusterData().getBuildings()) {
			SearchBuilding temp = searchWorld.getSearchBuilding(b);
			if (!temp.needsToBeSearchedForCivilian())
				continue;

			long cost = getWeightTo(searchWorld.getSearchBuilding(b));
			if (!(me instanceof PoliceForceAgent) && cost >= MoveConstants.UNREACHABLE_COST)
				continue;
			temp.setScore(0);
			temp.addScore("Move Cost", SearchUtils.decimalScaleCost(cost, me.getMapInfo(),me) * scoreFunction.getCostCoef());
			toSearch.add(b);
		}
		return toSearch;
	}

	public SearchBuilding getBestCivilianSearchArea(Collection<Building> input, boolean addRandom) {
		log("Choosing Building to search in my cluster");
		return getBestCivilianTargetIn(input,addRandom);
	}

	public SearchBuilding getBestOtherCivilianSearchArea() {
		Collection<Building> nearSearch = me.model().getObjectsInRange(me.me(), (int) me.model().getBounds().getWidth() / 8, Building.class);
		SearchBuilding best = null;
		best = getBestCivilianTargetIn(nearSearch,true);
		if (best == null) {
			Collection<Building> farSearch = me.model().getObjectsInRange(me.me(), (int) me.model().getBounds().getWidth() / 4, Building.class);
			farSearch.removeAll(nearSearch);
			best = getBestCivilianTargetIn(farSearch,true);
			if (me.time() > 120) {
				if (best == null) {
					Collection<Building> farSearch2 = me.model().getObjectsInRange(me.me(), (int) me.model().getBounds().getWidth() / 3, Building.class);
					farSearch2.removeAll(nearSearch);
					farSearch2.removeAll(farSearch);
					best = getBestCivilianTargetIn(farSearch2,true);
				}
			}
		}

		return best;
	}

	private SearchBuilding getBestCivilianTargetIn(Collection<Building> nearSearch, boolean addRandom) {
		SearchBuilding best = null;
		for (Building b : nearSearch) {
			SearchBuilding searchB = searchWorld.getSearchBuilding(b);
			searchB.setScore(0);
			if(b instanceof Refuge){
				searchB.setScore(AgentSearchScore.SEARCH_FILLTER_SCORE,"is Refuge");
				continue;
			}
//			if(b instanceof Center){
//				searchB.setScore(0,"is Center");
//				continue;
//			}
			
			if (scoreForCivilianSearch(searchB,addRandom)) {
				if (best == null || searchB.getScore() > best.getScore()) {
					best = searchB;
				}
			}
		}
		return best;
	}

	//	public PriorityQueue<Building> getOtherCivilianSearchArea() {
	//		PriorityQueue<Building> toSearch = new PriorityQueue<Building>(50, new Comparator<Building>() {
	//			@Override
	//			public int compare(Building o1, Building o2) {
	//				return (int) (searchWorld.getSearchBuilding(o2).getScore() - searchWorld.getSearchBuilding(o1).getScore());
	//			}
	//		});
	//		for (Building b : searchWorld.model().buildings()) {
	//			SearchBuilding temp = searchWorld.getSearchBuilding(b);
	//			if (scoreForCivilianSearch(temp))
	//				toSearch.add(b);
	//		}
	//		return toSearch;
	//	}

	public boolean scoreForCivilianSearch(SearchBuilding b,boolean addRandom) {

		b.setScore(0);
		b.addScore("Search for civilian Started...", 0);
		if (!b.scoreAndFilterSearchedForCivilian()) {
			return false;
		}
//		if (!b.needsToBeSearchedForCivilian()) {
//			b.addScore("FILTER:not needsToBeSearchedForCivilian:", AgentSearchScore.SEARCH_FILLTER_SCORE);
//			return false;
//		}

//		if (b.getRealBuilding()/* .virtualData[0] */.isBurning()) {
//			b.addScore("FILTER:firey Buildings:", AgentSearchScore.SEARCH_FILLTER_SCORE);
//			return false;
//		}
		//b.addScore("civilian probability", scoreFunction.getHearScore() * b.getCivProbability());

//		if (b.getRealBuilding().isBrokennessDefined() && b.getRealBuilding().getBrokenness() == 0 && !(me instanceof PoliceForceAgent)) {
//			b.addScore("FILTER:No Brokeness", AgentSearchScore.SEARCH_FILLTER_SCORE);
//			return false;
//		}
		long cost = getWeightTo(b);
		//		if(cost!=0)
//		System.out.println("cost: "+cost+" id: "+b.getRealBuilding().getID()+" score:"+SearchUtils.decimalScaleCost(cost, me.getMapInfo()) * scoreFunction.getCostCoef());

		if (!(me instanceof PoliceForceAgent) && cost >= MoveConstants.UNREACHABLE_COST) {
			b.addScore("FILTER:Unreachable:", AgentSearchScore.SEARCH_FILLTER_SCORE);
			return false;
		}
		if(addRandom){
			cost=(long) (cost *(Math.random() * 25 + 75) / 100);
		}
		double decimalScale = SearchUtils.decimalScaleCost(cost, me.getMapInfo(),me);
		int costCoef = scoreFunction.getCostCoef();	
		b.addScore("Move Cost(cost:"+cost+",decimalScale:"+decimalScale+",coef:"+costCoef+")",  decimalScale*costCoef);
		if (me instanceof PoliceForceAgent ) {
			if(!me.move.getMoveType(PoliceReachablityMove.class).isReallyUnreachableTo(b.getRealBuilding().getSearchAreas()))
				b.addScore("Negative Score:Reachable:", 300);
		}
		return true;
	}

	public int getWeightIncreaseCoef(SOSAgent<? extends StandardEntity> me) {
		if (me.getMapInfo().isBigMap())
			return 3;
		else
			return 2;
	}

	protected long getWeightTo(SearchBuilding a) {
		if (me instanceof PoliceForceAgent) {
			return me.move.getWeightTo(a.getRealBuilding().getSearchAreas(), DistanceMove.class);
		} else
			return me.move.getWeightTo(a.getRealBuilding().getSearchAreas(), SearchMove.class);
	}

}
