package sos.police_v2;

public class PoliceConstants {

	public static enum Value {
		Refuge(4), FireBrigade(6), AmbulanceTeam(5), PoliceForceInBuilding(8), PoliceForce(0), Others(1), PoliceForSpecialTasks(10), StarSearchGatheringRoad(5);

		private int id;

		Value(int id) {
			this.id = id;
		}

		public void setValue(int id) {
			this.id = id;
		}

		public int getValue() {
			return id;
		}
	}

	public static enum States {
		Precompute, Rechabling, Plow
	}

	public static int STANDARD_OF_MAP;
	public static final int DEFAULT_JOB_DONE_FOR_POLICE_IN_BUILDING = 550;
	public static final double moveDistance = 200000;
	public static final int RANGE_OF_FIRE_UPDATE = 50000;
	public static final int DEFAULT_NEGATIVE_VALUE = -50;

	public static final int DISTANCE_UNIT = 1000;
	public static final int STOCK_DISTANCE = 2;//meter /*2000/DISTANCE_UNIT*/;
	public static final double VERY_SMALL_ROAD_GROUND_IN_MM = 8 * 1000 * 1000;

	public static final boolean IS_NEW_CLEAR =true;
	public static final boolean NEW_CLEAR_MOVE = true;

}
