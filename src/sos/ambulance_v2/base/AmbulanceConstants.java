package sos.ambulance_v2.base;

public class AmbulanceConstants {

	int SIMULATION_TIME = 1000;
	public static int STOCK_DISTANSE= 10000;
	public static int LONG_TIME = 300;
	public static final int VALID_DEATH_TIME_FOR_NO_REFUGE_MAP = 230;
//	public static final boolean LEGEACY_AMBULANCE = false;
	public static final int AVERAGE_MOVE_PER_CYCLE=25000;
	public static final int AVERAGE_MOVE_TO_TARGET = 10;
	public enum AmbulanceAction {
		MOVE, UNLOAD, LOAD, RESCUE, REST
	}
	public enum ATstates {
		MOVE_TO_TARGET(0), RESCUE(1), MOVE_TO_REFUGE(2), SEARCH(3);
		private final int messageIndex;

		ATstates(int messageIndex){
			this.messageIndex = messageIndex;

		}
		public int getMessageIndex() {
			return messageIndex;
		}

	}

	public enum Rescue_Emergency_status {
			MOVING_TO_TARGET, RESCUING, LOADING, MOVING_TO_ROAD, UNLOADING
	}

	public enum Rescue_Complete_status {
			MOVING_TO_TARGET, RESCUING, LOADING, MOVING_TO_ROAD, MOVING_TO_REFUGE, UNLOADING
	}

	public enum Rescue_Hearsing_status {
			MOVING_TO_TARGET, RESCUING, LOADING, MOVING_TO_ROAD, MOVING_TO_REFUGE, UNLOADING
	}

	public enum Rescue_Agent_status {
			MOVING_TO_TARGET, RESCUING
	}

	public enum Carry_to_Refuge_status {
			MOVING_TO_TARGET, LOADING, MOVING_TO_REFUGE, UNLOADING
	}

	public enum CivilianState {
			DEATH, CRITICAL, AVERAGE, HEALTHY
	}

}
