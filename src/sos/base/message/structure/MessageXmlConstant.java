package sos.base.message.structure;
/**
 *
 * @author Ali
 * Edited by Aramik
 *
 */
public interface MessageXmlConstant {
	/**
	 * For header
	 */
	public static final String
	 	HEADER_OUCH					="Ouch",
	 	HEADER_HELP					="Help",
	 	HEADER_UNKNOWN_OUCH			="",


	 	HEADER_IM_HEALTHY_AND_CAN_ACT="Im healthy and can act",

	 	// search
		HEADER_SEARCHED_BUILDING 	="searched building",
		HEADER_SEARCHED_FOR_CIVILIAN="searched for civilian building",
		HEADER_NO_COMM_SEARCHED_FOR_CIVILIAN="no com searched for civilian building",
		HEADER_HEARED_CIVILIAN		="heared civilian",
		HEADER_ROAD_FIERYNESS_CHECK	="road fieryness check",
		HEADER_SEARCH_ZONE_CIVILIAN_SEARCH="search zone civilian search",

		//Road and Blockade messages
		HEADER_OPEN_ROAD = "open road",

		HEADER_ROAD_STATE 			= "road state",
		HEADER_AGENT_TO_EDGES_REACHABLITY_STATE="Agent To Edges Reachablity State",
	 	HEADER_NEW_BLOCKADE			="new blockade",
	 	HEADER_UPDATE_BLOCKADE		="update blockade",

	 	//Human Messages
	 	HEADER_SENSED_CIVILIAN		="sensed civilian",
		HEADER_SENSED_AGENT			="sensed agent",
		HEADER_LOW_SENSED_AGENT		="low sensed agent",
		HEADER_POSITION				="position",
	    HEADER_DEAD_AGENT			="dead agent",
	    HEADER_AGENT_STOCK			="AgentStock",

		//Building Messages
		HEADER_FIRE					="fire",
		HEADER_WATER				="water",
		// Building Messages
		HEADER_ZERO_BUILDING_BROKNESS = "zero building brokness",

	    //Ambulance Messages
	    HEADER_AMBULANCE_STATUS		="ambulance status",
	    HEADER_AMBULANCE_INFO		="ambulance info",
	    HEADER_AMBULANCE_ASSIGN		="ambulance assign",
	    HEADER_AMBULANCE_TASK_ACK	="ambulance task ack",
		HEADER_IGNORED_TARGET 		="ignored target",

	    //State and Type of policeForces
		HEADER_POLICE_INFO = "police info",

		// Fire Messages // Hesam 002
		HEADER_FIRE_ZONE = "firezone",
		HEADER_TARGET_FIRE_SITE = "target FireSite",
		HEADER_DISABLE_FIRE_SITE = "disable FireSite",
		HEADER_END_FIRE_SITE_TASK = "end task",
		HEADER_FIRE_NO_TASK="no task",
		//For Search
		HEADER_VISITED_AREA="visited area",

		HEADER_FIRE_SEARCHING = "searching fire",
		HEADER_CIVILIAN_SEARCHING = "searching civilian",
		HEADER_RANDOM_SEARCHING = "searching random",
		HEADER_NO_COMM_SEARCHING = "searching no comm",

		HEADER_LOWCOM_FIRE="LowComFireBuiding",
		HEADER_LOWCOM_CIVILIAN="LowComCivilian",
		
		HEADER_LOWCOM_1_CIVILIAN="LowComm1Civilian",
		HEADER_LOWCOM_2_CIVILIAN="LowComm2Civilian",
		HEADER_LOWCOM_MORE_CIVILIAN="LowComm3Civilian";


	/**
	 * For data
	 */
	public static final String
	 	//------------general
	 	DATA_ID								="id",
		DATA_TIME							="time",

		//------------index
		DATA_AREA_INDEX						="area index",
		DATA_BUILDING_INDEX					="building index",
		DATA_ROAD_INDEX						="road index",
		DATA_AGENT_INDEX					="agent index",
		DATA_BLOCKADE_INDEX					="blockade index",
		DATA_AMBULANCE_INDEX				="ambulance index",
		DATA_POLICE_INDEX					="police index",
		DATA_FIRE_INDEX						="fire index",
		//------------human
		DATA_HP								="hp",
		DATA_DAMAGE							="damage",
		DATA_BURIEDNESS						="buriedness",
		//------------building
		DATA_FIERYNESS						="fieryness",
		DATA_HEAT							="heat",
		//------------blockade
		DATA_BLOCKADE_SIZE					="blockade size",
		DATA_BLOCKADE_CLEARED_SIZE			="blockade cleared size",
		//------------position
		DATA_X								="x",
		DATA_Y								="y",
	 	//------------search
		DATA_SEARCH_STATUS 					="SearchStatus",
		DATA_SEARCH_TARGET					="search target",
		DATA_VISITED_START					="visited start",
		DATA_VISITED_END					="visited end",

		//------------ambulance
   		DATA_LONG_LIFE						="long life",
   		DATA_AT_STATE						="at state"	,
	   	DATA_ACK_TYPE						="ack type",
	   	DATA_FINISH_TIME					="finish time",
	   	DATA_NEED_HELP						="need help",
	   	DATA_CUSTOM_INDEX_BUILDING			="custom indx b",
	   	DATA_CUSTOM_INDEX_FIREBIRIGADE		="custom indx fb",
	   	//------------Fire Messages
	   	DATA_INFLAMMABLE					="inflammable",
	   	DATA_FIRE_SEARCHER                  ="fire searcher",
	   	DATA_IS_REALLY_UNREACHABLE			="isReallyUnReachable",
	   	DATA_IS_REALLY_REACHABLE			="isReallyReachable",
	   	DATA_VALID_CIVILIAN_COUNT			="valid civilian count",

	   	//LOW COMM
	   	DATA_BURIEDNESS_LEVEL				="BuriednessLevel",
	   	DATA_CIVILIAN_COUNT_LOSSY			="CivilianCounts losy",
	   	DATA_DEATH_TIME_LOSSY1				="death time lossy1",
	   	DATA_DEATH_TIME_LOSSY2				="death time lossy2",
	   	DATA_DEATH_TIME_LOSSY3				="death time lossy3",
		DATA_LOW_BURIEDNESS					="Lowburiedness";





}
