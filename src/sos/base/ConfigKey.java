package sos.base;

import rescuecore2.config.Config;

/**
 * @author Ivan
 */
public final class ConfigKey {
	public static final String REPAIR_DISTANCE_KEY = "clear.repair.distance";
	public static final String REPAIR_RATE_KEY = "clear.repair.rate";
	public static final String AGENT_TOTAL_PFS = "scenario.agents.pf";
	public static final String AGENT_TOTAL_ATS = "scenario.agents.at";
	public static final String AGENT_TOTAL_FBS = "scenario.agents.fb";
	public static final String AGENT_IGNORE_UNTIL = "kernel.agents.ignoreuntil";

	public static final String FIRE_MAX_WATER_KEY = "fire.tank.maximum";
	public static final String FIRE_MAX_DISTANCE_KEY = "fire.extinguish.max-distance";
	public static final String FIRE_MAX_POWER_KEY = "fire.extinguish.max-sum";

	/** for channels **/
	public static final String CHANNEL_VOICE = "voice";
	public static final String CHANNEL_RADIO = "radio";
	public static final String CHANNEL_PREFIX = "comms.channels.";

	public static final String CHANNEL_PLATOON_MAX = CHANNEL_PREFIX + "max.platoon";
	public static final String CHANNEL_CENTRE_MAX = CHANNEL_PREFIX + "max.centre";
	public static final String CHANNEL_COUNT = CHANNEL_PREFIX + "count";
	public static final String CHANNEL_TYPE_POSTFIX = ".type";
	// for voice only
	public static final String CHANNEL_RANGE_POSTFIX = ".range";
	public static final String CHANNEL_MESSAGE_SIZE_POSTFIX = ".messages.size";
	public static final String CHANNEL_MESSAGE_MAX_POSTFIX = ".messages.max";
	// for radio only
	public static final String CHANNEL_BANDWIDTH_POSTFIX = ".bandwidth";

	// Line of sight perception parameters
	public static final String PERCEPTION_TYPE_KEY = "kernel.perception";
	public static final String PERCEPTION_TYPE_LOS = "rescuecore2.standard.kernel.LineOfSightPerception";
	public static final String PERCEPTION_MAX_DISTANCE_KEY = "perception.los.max-distance";
	public static final String PERCEPTION_PRECISION_HP_KEY = "perception.los.precision.hp";
	public static final String PERCEPTION_PRECISION_DAMAGE_KEY = "perception.los.precision.damage";

	private ConfigKey() {
	}

	// TODO
	public static int getFireMaxWater() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(FIRE_MAX_WATER_KEY);
	}

	public static int getFireMaxDistance() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(FIRE_MAX_DISTANCE_KEY);
	}

	public static int getFireMaxPower() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(FIRE_MAX_POWER_KEY);
	}

	public static int getClearRepairDistance() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(REPAIR_DISTANCE_KEY, 10000);
	}

	public static int getClearRepairRate() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(REPAIR_RATE_KEY);
	}

	public static int getTotalPFs() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(AGENT_TOTAL_PFS);
	}

	public static int getTotalATs() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(AGENT_TOTAL_ATS);
	}

	public static int getTotalFBs() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(AGENT_TOTAL_FBS);
	}

	public static int getTotalAgents() {
		return getTotalATs() + getTotalFBs() + getTotalPFs();
	}

	public static int getIgnoreTime() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(AGENT_IGNORE_UNTIL);
	}

	public static int getChannelCount() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(CHANNEL_COUNT);
	}

	public static int getChannelPlatoonMax() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(CHANNEL_PLATOON_MAX);
	}

	public static int getChannelCentreMax() {
		Config c = SOSAgent.currentAgent().getConfig();
		return c.getIntValue(CHANNEL_CENTRE_MAX);
	}

	public static String getChannelType(Config c, int channelId) {
		return c.getValue(CHANNEL_PREFIX + channelId + CHANNEL_TYPE_POSTFIX);
	}

	/**
	 * for voice channel only
	 *
	 * @param c
	 * @param channelId
	 * @return Range of a voice message in mm
	 */
	public static int getChannelRange(Config c, int channelId) {
		return c.getIntValue(CHANNEL_PREFIX + channelId + CHANNEL_TYPE_POSTFIX);
	}

	/**
	 * for voice channel only
	 *
	 * @param c
	 * @param channelId
	 * @return Maximum size of a voice message
	 */
	public static int getChannelMessageSize(Config c, int channelId) {
		return c.getIntValue(CHANNEL_PREFIX + channelId + CHANNEL_MESSAGE_SIZE_POSTFIX);
	}

	/**
	 * for voice channel only
	 *
	 * @param c
	 * @param channelId
	 * @return Maximum number of voice messages each agent can utter each
	 *         timestep
	 */
	public static int getChannelMessageMax(Config c, int channelId) {
		return c.getIntValue(CHANNEL_PREFIX + channelId + CHANNEL_MESSAGE_MAX_POSTFIX);
	}

	/**
	 * for radio channel only
	 *
	 * @param c
	 * @param channelId
	 * @return the bandwidth of a radio channel
	 */
	public static int getChannelBandWidth(Config c, int channelId) {
		return c.getIntValue(CHANNEL_PREFIX + channelId + CHANNEL_BANDWIDTH_POSTFIX);
	}

	/**
	 * @param c
	 * @return 若是agent感知模型是LOS，则返回视野范围；否则返回0
	 */
	public static int getPerceptionMaxDistance() {
		Config c = SOSAgent.currentAgent().getConfig();
		if (c.getValue(PERCEPTION_TYPE_KEY).equals(PERCEPTION_TYPE_LOS)) {
			return c.getIntValue(PERCEPTION_MAX_DISTANCE_KEY);
		} else {
			return 0;
		}
	}

	/**
	 * @param c
	 * @return 若是agent感知模型是LOS，则返回damage的近似精度；否则返回0
	 */
	public static int getPerceptionPrecisionDamage() {
		Config c = SOSAgent.currentAgent().getConfig();
		if (c.getValue(PERCEPTION_TYPE_KEY).equals(PERCEPTION_TYPE_LOS)) {
			return c.getIntValue(PERCEPTION_PRECISION_DAMAGE_KEY);
		} else {
			return 1;
		}
	}

	/**
	 * @param c
	 * @return 若是agent感知模型是LOS，则返回hp的近似精度；否则返回0
	 */
	public static int getPerceptionPrecisionHp() {
		Config c = SOSAgent.currentAgent().getConfig();
		if (c.getValue(PERCEPTION_TYPE_KEY).equals(PERCEPTION_TYPE_LOS)) {
			return c.getIntValue(PERCEPTION_PRECISION_HP_KEY);
		} else {
			return 1;
		}
	}
}
