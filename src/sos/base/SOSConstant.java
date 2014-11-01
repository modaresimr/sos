package sos.base;

import java.text.NumberFormat;

import rescuecore2.config.Config;

public class SOSConstant {

	private static Config config;

	public static void setConfig(Config config){
		SOSConstant.config = config;
		if (!SOSConstant.IS_CHALLENGE_RUNNING)
			SOSConstant.IS_CHALLENGE_RUNNING = config.getBooleanValue("sos.IS_CHALLENGE_RUNNING", true);
		if (!SOSConstant.NO_ANY_LOG)
			SOSConstant.NO_ANY_LOG = config.getBooleanValue("sos.NO_ANY_LOG", true);
		if(SOSConstant.IS_CHALLENGE_RUNNING)
			CREATE_BASE_LOGS=false;
	}

	public static final boolean WORLD_MODEL_NAMAYANGAR = true; // Ali
	public static final boolean SHOW_SOS_STARTING_DIALOG = true; // Ali
	public static final boolean SHOW_SOS_STARTING_SPLASH = true; // Ali
	public static final boolean TIME_OUT_EXCEPTION = true; // Ali
	public static boolean IS_CHALLENGE_RUNNING =false; // Ali
	public static boolean NO_ANY_LOG = false; // Ali
	public static boolean DONT_CREATE_LOGS = false;//Ali
	public static boolean XML_LOGGING = false; // Ali
	public static boolean CREATE_BASE_LOGS = false;//Ali

	//Search Sampling
	public static final boolean SEARCH_SAMPLING = false; //Salim
	public static String MYSQL_URL = "jdbc:mysql://localhost/sos_search";//Salim
	public static String MYSQL_USER = "root";//Salim
	public static String MYSQL_PASS = "salmalkk";//Salim
	//YOOSEF
	public static final boolean SAMPLING = false;

	public static final int MOVE_DISTANCE_PER_CYCLE = 35000;// TODO check and modify

	public enum AgentType { // Ali
		FireBrigade, FireStation, PoliceForce, PoliceOffice, AmbulanceTeam, AmbulanceCenter, Center
	}

	public enum logType { // Ali
		Agent, MessageTransmit, MessageContent, WorldModel, Base, Move, Act, Search, Reachablity,NoComunication
	}

	// old logger constants
	public enum ModeType {
		Light, Medium, Heavy
	}

	public enum SystemType {
		Agent, Msg, Traffic, GIS, Monitoring, ZoneAnalizer, GlobalStrategyChooser, Voronoi, LocalStrategyChooser, MessageSend, MessageReceive, RoadSite, Iland, Base, Message, GraphUsage, AmbulanceDecision, Clustering, MapChecking, RoadSiteIland, Building, Sampling, Process, WorldPrint, AT, Update
	}

	public enum GraphEdgeState {
		Open, Block, FoggyOpen, FoggyBlock
	}

	public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(); // Ali
	public static final int PASSWORD = 234;
	public static final boolean DISABLE_FIRE_ESTIMATION = false;

	public static boolean isCreatingPreComputeFiles(){
		return config.getBooleanValue("sos.create-precompue-files",false);
	}

	public static Config getConfig() {
		return config;
	}


}
