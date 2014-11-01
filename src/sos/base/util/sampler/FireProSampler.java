package sos.base.util.sampler;

import java.sql.SQLException;
import java.util.Arrays;

import sos.LaunchAgents;
import sos.ambulance_v2.AmbulanceTeamAgent;
import sos.base.SOSConstant;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.StandardWorldModel;
import sos.base.move.types.PoliceMove;
import sos.base.move.types.StandardMove;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireBrigadeAgent;
import sos.police_v2.PoliceForceAgent;

public class FireProSampler extends SOSSampler {

	private final SOSWorldModel model;
	private SOSLoggerSystem logger;

	public FireProSampler(SOSWorldModel model) {
		this.model = model;
		logger = new SOSLoggerSystem(model.sosAgent().me(), "samples/", !SOSConstant.IS_CHALLENGE_RUNNING, OutputType.File);

	}

	/**
	 * This method run each cycle
	 * 
	 * @throws SQLException
	 */
	@Override
	protected void insert()  {
		for (Building b : model.buildings())
			try {
				insertBuilding(b);
			} catch (Exception e) {
//				e.printStackTrace();
			}
	}

	protected void insertBuilding(Building building) throws Exception {
		StandardWorldModel vModel = LaunchAgents.viewer.model();

		String query = "INSERT INTO sample VALUES(";

		query += vModel.time() + ",";

		query += vModel.buildings().get(building.getBuildingIndex()).getFieryness() + ",";

		query += vModel.buildings().get(building.getBuildingIndex()).getTemperature() + ",";

		query += model.time() + ",";

		query += building.updatedtime() + ",";

		query += building.getLastSenseTime() + ",";

		query += building.getTemperature() + ",";

		query += max_neigh_temp(building) + ",";

		query += min_neigh_temp(building) + ",";

		query += avg_neigh_temp(building) + ",";

		query += median_neigh_temp(building) + ",";

		query += building.getFloors() + ",";

		query += building.getGroundArea() + ",";

		query += distanceTOAgent(building) + ",";

		query += realDistanceTOAgent(building) + ",";

		query += model.getBounds().getWidth() + ",";

		query += model.getBounds().getHeight() + ",";

		query += ditance_TO_neigh_min(building) + ",";

		query += ditance_TO_neigh_max(building) + ",";

		query += ditance_TO_neigh_avg(building) + ",";

		query += ditance_TO_neigh_median(building) + ",";

		query += ditance_TO_nearest_agent(building) + ",";

		query += bandWidth() + ",";

		query += agentType() + ",";

		query += material(building) + ",";

		query += importance(building) + ",";

		query += building.getX() + ",";

		query += building.getY() + ",";

		query += model.mapCenter().getX() + ",";

		query += model.mapCenter().getY() + ",";

		query += model.getBounds().getWidth() * model.getBounds().getHeight() + ",";

		query += (building.updatedtime() > 3 ? 1 : 0) + ",";

		query += building.realNeighbors_Building().size() + ",";

		query += "\"" + model.sosAgent().messageSystem.type.name() + "\"";
		////////////////////////////

		query += ");";
		logger.log(query + "\n");

	}

	private String importance(Building building) {
		return building.getImportance() + "";
	}

	private String material(Building building) {
		return building.getBuildingAttributes() + "";
	}

	private String agentType() {
		if (model.sosAgent() instanceof FireBrigadeAgent)
			return 0 + "";
		else if (model.sosAgent() instanceof PoliceForceAgent)
			return 1 + "";
		else if (model.sosAgent() instanceof AmbulanceTeamAgent)
			return 2 + "";
		else
			return 3 + "";
	}

	private String bandWidth() {
		return model.sosAgent().messageSystem.getMessageConfig().getPlatoonSubScribeLimit() + "";
	}

	private String ditance_TO_nearest_agent(Building building) {
		int dis = Integer.MAX_VALUE;
		for (Human h : model.agents()) {
			int dis2 = building.distance(h);
			if (dis2 < dis)
				dis = dis2;
		}
		return dis + "";
	}

	private String ditance_TO_neigh_avg(Building building) {
		int sum = 0;
		for (Building b : building.realNeighbors_Building()) {
			sum += b.distance(building);
		}
		sum /= building.realNeighbors_Building().size();
		return sum + "";
	}

	private String ditance_TO_neigh_median(Building building) {
		int[] arr = new int[building.realNeighbors_Building().size()];

		for (int i = 0; i < arr.length; i++) {
			arr[i] = building.distance(building.realNeighbors_Building().get(i));
		}

		Arrays.sort(arr);

		return arr[(arr.length / 2)] + "";
	}

	private String ditance_TO_neigh_max(Building building) {
		int dis = Integer.MIN_VALUE;
		for (Building h : building.realNeighbors_Building()) {
			int dis2 = building.distance(h);
			if (dis2 > dis)
				dis = dis2;
		}
		return dis + "";
	}

	private String ditance_TO_neigh_min(Building building) {
		int dis = Integer.MAX_VALUE;
		for (Building h : building.realNeighbors_Building()) {
			int dis2 = building.distance(h);
			if (dis2 < dis)
				dis = dis2;
		}
		return dis + "";

	}

	private String realDistanceTOAgent(Building building) {
		if (model.sosAgent() instanceof PoliceForceAgent)
			return model.sosAgent().move.getWeightTo(building, PoliceMove.class) + "";
		return model.sosAgent().move.getWeightTo(building, StandardMove.class) + "";
	}

	private String distanceTOAgent(Building building) {
		return building.distance((Human) (model.sosAgent().me())) + "";
	}

	private String median_neigh_temp(Building building) {
		int[] arr = new int[building.realNeighbors_Building().size()];

		for (int i = 0; i < arr.length; i++) {
			arr[i] = (building.realNeighbors_Building().get(i)).getTemperature();
		}

		Arrays.sort(arr);

		return arr[(arr.length / 2)] + "";

	}

	private String avg_neigh_temp(Building building) {
		int sum = 0;

		for (Building h : building.realNeighbors_Building()) {
			sum += h.getTemperature();
		}

		sum = sum / building.realNeighbors_Building().size();
		return sum + "";
	}

	private String min_neigh_temp(Building building) throws Exception {
		int dis = Integer.MAX_VALUE;
		boolean changed=false;
		for (Building h : building.realNeighbors_Building()) {
			if (dis > h.getTemperature())
				dis = h.getTemperature();
			if(h.getTemperature()!=0)
				changed=true;
		}

		if(!changed)
			throw new Exception("Min  Temp is zero");

		return dis + "";
	}

	private String max_neigh_temp(Building building) throws Exception {
		int dis =0;

		for (Building h : building.realNeighbors_Building()) {
			if (dis < h.getTemperature())
				dis = h.getTemperature();
		}
		if(dis==0)
			throw new Exception("Max Temp is zero");

		return dis + "";
	}

}
