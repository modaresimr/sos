package sos.police_v2.state.intrupt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.entities.Building;
import sos.base.entities.GasStation;
import sos.base.entities.Road;
import sos.base.util.SOSActionException;
import sos.police_v2.PoliceForceAgent;

public class DamagedState extends PoliceAbstractIntruptState {

	private static final int GAS_RANG = 50000;
	private ArrayList<GasStation> expGasStation;

	public DamagedState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}

	@Override
	public void precompute() {

	}

	@Override
	public void act() throws SOSActionException {
		log.info("[DamageState] is acting");
		if (expGasStation != null && expGasStation.size() > 0) {
			Road safe = findSafeRoad(expGasStation);
			if (safe != null) {
				//log.warn("FARAR BE ROAD =" + safe);
				move(safe);
			}
		}
		if (agent.model().refuges().size() < 0) {
			log.debug("No Refuge found:(");
			return;
		}
		if (amIGoingToDieSoon(4)) {
			//log.warn("I'm going to die...  :(( ");
			Collection<Building> buildings = model().getObjectsInRange((int) agent.me().getPositionPoint().getX(), (int) agent.me().getPositionPoint().getY(), 70000, Building.class);
			move(buildings);///TODO jaii bere ke trafic nashe 
		}
		if (amIGoingToDieSoon(30)) {
			if (model().refuges().size() > 0)
				move(agent.model().refuges());
			else
				return; //TODO vaghan chikar konim????
		}
		if (agent.me().getDamage() != 0) {
			if (agent.me().getDamage() > 50) {
				if (model().refuges().size() > 0)
					move(agent.model().refuges());
			}
			return;
		}
		//log.warn("chera rafte too damage state?????");
	}

	private void runAwayFromGasstaion() {
		//log.warn("runAwayFromGasstaion called=>");
		ArrayList<GasStation> inRangGasStations = getInRangGasStaion();
		expGasStation = inRangGasStations;

	}

	private Road findSafeRoad(ArrayList<GasStation> inRangGasStations) {
		//log.warn("findSafeRoad called=>");
		Collection<Road> roadList = model().getObjectsInRange(agent.getID(), GAS_RANG + 2000, Road.class);
		//log.warn("all road list= " + roadList);
		for (Road select : roadList) {
			boolean isSafe = true;
			for (GasStation station : inRangGasStations)
				if (Point.distance(select.getX(), select.getY(), station.getX(), station.getY()) < GAS_RANG)
					isSafe = false;
			//log.warn(select + " is safe= " + isSafe);
			if (isSafe && isReachableTo(select))
				return select;
		}
		for (Road select : roadList) {
			boolean isSafe = true;
			for (GasStation station : inRangGasStations)
				if (Point.distance(select.getX(), select.getY(), station.getX(), station.getY()) < GAS_RANG)
					isSafe = false;
			//log.warn(select + "()()()()() is safe= " + isSafe);
			if (isSafe)
				return select;
		}
		return null;
	}

	private ArrayList<GasStation> getInRangGasStaion() {
		//log.warn("getInRangGasStaion called=>");
		ArrayList<GasStation> result = new ArrayList<GasStation>();
		for (GasStation station : model().GasStations()) {
			if (Point.distance(agent.me().getX(), agent.me().getY(), station.getX(), station.getY()) < GAS_RANG)
				if (isGoingToEXPGas(station))
					result.add(station);
		}
		return result;
	}

	private boolean isGoingToEXPGas(GasStation station) {
		//log.warn("isGoingToEXPGas called=>");
		if (station.getTemperature() > 30 && (station.getFieryness() == 0 || station.getFieryness() == 4))
			return true;
		return false;
	}

	public boolean amIGoingToDieSoon(int deathTime) {
		if (agent.me().getDamage() == 0)
			return false;
		if (SimpleDeathTime.getEasyLifeTime(agent.me().getHP(), agent.me().getDamage(), agent.model().time()) < deathTime)
			return true;
		return false;
	}

	@Override
	public boolean canMakeIntrupt() {
		boolean result = false;
		runAwayFromGasstaion();
		if (expGasStation != null && expGasStation.size() > 0) {
			return true;
		}
		result = amIGoingToDieSoon(30);
		if (agent.me().getDamage() != 0)
			result = true;
		log.trace(this + " can make intrupt?" + result);
		return result;
	}
}
