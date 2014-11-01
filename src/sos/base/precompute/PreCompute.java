package sos.base.precompute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import rescuecore2.geometry.Point2D;
import sos.LaunchAgents;
import sos.base.CenterAgent;
import sos.base.SOSAgent;
import sos.base.SOSConstant;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.SOSLineOfSightPerception;
import sos.base.Updater;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.message.MessageBuffer;
import sos.base.message.ReadXml;
import sos.base.message.structure.MessageConstants.ChannelSystemType;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.message.system.MessageHandler;
import sos.base.message.system.MessageSystem;
import sos.base.move.Move;
import sos.base.precompute.precomputes.FireSearchBuildingsPreCompute;
import sos.base.precompute.precomputes.MiddleBlockadePreCompute;
import sos.base.precompute.precomputes.NeighborsPreCompute;
import sos.base.precompute.precomputes.SearchAreaPrecompute;
import sos.base.reachablity.ExpandArea;
import sos.base.reachablity.Reachablity;
import sos.base.reachablity.tools.SOSArea;
import sos.base.sosFireEstimator.SOSFireEstimator;
import sos.base.sosFireEstimator.SOSFireEstimatorWorldModel;
import sos.base.sosFireZone.SOSFireZoneManager;
import sos.base.sosFireZone.util.Rnd;
import sos.base.update.No_Comm;
import sos.base.util.SOSGeometryTools;
import sos.base.util.TimeNamayangar;
import sos.base.util.Utils;
import sos.base.util.blockadeEstimator.BlockadeEstimator;
import sos.base.util.blockadeEstimator.SOSBlockade;
import sos.base.util.mapRecognition.MapRecognition;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.worldGraph.Node;
import sos.base.worldGraph.VirtualGraphEdge;
import sos.base.worldGraph.WorldGraph;
import sos.base.worldGraph.WorldGraphEdge;
import sos.police_v2.PoliceForceAgent;
import sos.tools.GraphEdge;

public class PreCompute<E extends StandardEntity> {
	private final SOSAgent<E> sosAgent;
	private final SOSLoggerSystem preComputeLog;

	private static final double numberOfProcess = 14d;
	private static int processStep = (int) (100 / numberOfProcess);
	private int pCount = 0;
	private MapRecognition mr;//Salim
	TimeNamayangar time = new TimeNamayangar("", false, false);

	public PreCompute(SOSAgent<E> sosAgent) {
		this.sosAgent = sosAgent;
		preComputeLog = sosAgent.sosLogger.preComputeLog;
	}

	public static String getPreComputeFile(String type) {
		return "SOSFiles/" + type +
				SOSAgent.currentAgent().getMapInfo().getMapName() + ".sos";
	}

	public void init() {
		Rnd.setSeed(23);
		time.start();
		processGUI();
		sosAgent.lineOfSightPerception = new SOSLineOfSightPerception<E>(sosAgent); // Ali
		mapVerifierPrecompute();

		mapRecognizer();//salim
		messagePrecompute();
		if (!sosAgent.messageSystem.getMine().isUnusedAgent()) {//The agent isn't useful!!!(it occurred for unusful centers)

			if (sosAgent instanceof CenterAgent && sosAgent.messageSystem.type == Type.NoComunication)
				return;
		}
		findMaxBlockadesForBuilding();//get 1m in VC//3mb in berlin
		setReachablityPrecomputes();//get 4.5m in VC//5mb in berlin
		setNeighbours();//nima//2mb in berlin
		setFireSearchBuildings();//nima//get 4m in VC//2mb in berlin
		//		setFireSearchBuildingsAndLightNeighbors();//nima
		computeRoadsBasePoint();
		computeEdgesNewProperties();
		createWorldGraphNodes();
		createWorldGraphEdges();
		createVirtualGraphEdges();
		setSearchPrecomputes();
		movePrecompute();
		updatePrecompute();
		setFireEstimatorPrecompute();//got 7 mb in berlin

	}

	public void setFireEstimatorPrecompute() {
		splashLogStart("FireEstimatorPrecompute");
		sosAgent.fireSiteManager = new SOSFireZoneManager(sosAgent.model(), sosAgent, SOSConstant.AgentType.FireBrigade);
		sosAgent.model().setFireSites(sosAgent.fireSiteManager.getFireSites());
		sosAgent.model().estimatedModel = new SOSFireEstimatorWorldModel(sosAgent.model());
		sosAgent.fireEstimator = new SOSFireEstimator(sosAgent.model(), sosAgent.model().estimatedModel, sosAgent.fireSiteManager.getFireSites(), 0, (short) 0);
		splashLogEnd("FireEstimatorPrecompute");
	}

	public void mapVerifierPrecompute() {
		//		splashLogStart("MapVerifierPrecompute");

		//		if (!SOSConstant.IS_CHALLENGE_RUNNING)
		//		new MapVerifier(sosAgent);

		//		splashLogEnd("MapVerifierPrecompute");
	}

	/**
	 * Checks if the map is a KNOWN map and we have the files
	 *
	 * @author Salim
	 */
	public void mapRecognizer() {
		splashLogStart("MapRecognition");
		mr = new MapRecognition();
		mr.initialize(sosAgent.model());
		sosAgent.setMapInfo(mr.verifyMap(this.sosAgent.model()));
		sosAgent.sosLogger.base.consoleInfo("[Map Recognintion]::: " + sosAgent.getMapInfo());
		splashLogEnd("MapRecognition");
	}

	public void messagePrecompute() {
		splashLogStart("messagePrecompute");

		sosAgent.messageSystem = new MessageSystem(sosAgent); // Ali
		sosAgent.messages = new MessageBuffer(ChannelSystemType.Normal, sosAgent.messageSystem); // Ali
		sosAgent.sayMessages = new MessageBuffer(ChannelSystemType.Voice, sosAgent.messageSystem); // Ali
		sosAgent.lowCommunicationMessages = new MessageBuffer(ChannelSystemType.Low, sosAgent.messageSystem); // Ali
		new ReadXml(sosAgent.model()); // Ali
		sosAgent.messageHandler = new MessageHandler(sosAgent); // Ali
		sosAgent.noCommunicationMessageSelector = new No_Comm(sosAgent);
		splashLogEnd("messagePrecompute");
	}

	public void updatePrecompute() {
		splashLogStart("updatePrecompute");

		sosAgent.updater = new Updater(sosAgent);

		splashLogEnd("updatePrecompute");
	}

	public void movePrecompute() {
		splashLogStart("movePrecompute");

		sosAgent.model().setWorldGraph(new WorldGraph(sosAgent.model().nodes().size(), sosAgent.model().graphEdges()));
		sosAgent.move = new Move(sosAgent, sosAgent.model().getWorldGraph());
		sosAgent.move.cycle();// Aramik
		splashLogEnd("movePrecompute");
	}

	/** @author Morteza2012 *******************************************************/
	public void setSearchPrecompute() {
		for (Building b : sosAgent.model().buildings()) {
			b.fireSearchBuilding().SetSearchArea();
		}
	}

	/** @author Morteza2012 *******************************************************/
	public void setFireSearchBuildings() {
		splashLogStart("setFireSearchBuildings");
		new FireSearchBuildingsPreCompute().execute();
		splashLogEnd("setFireSearchBuildings");
	}

	public void setSearchPrecomputes() {
		splashLogStart("setSearchPrecomputes");
		new SearchAreaPrecompute().execute();
		splashLogEnd("setSearchPrecomputes");
	}

	/*
	 * * Aramik
	 * base point in roads are used for setting exact agent position by message
	 */
	public void computeRoadsBasePoint() {
		splashLogStart("computeRoadsBasePoint");

		// int x_distance=Integer.MIN_VALUE;
		// int y_distance=Integer.MIN_VALUE;
		for (Road rd : sosAgent.model().roads()) {
			int min_x = Integer.MAX_VALUE, min_y = Integer.MAX_VALUE;
			// int max_x=Integer.MIN_VALUE,max_y=Integer.MIN_VALUE;
			for (int i = 0; i < rd.getApexList().length; i += 2) {
				min_x = min_x > rd.getApexList()[i] ? rd.getApexList()[i] : min_x;
				min_y = min_y > rd.getApexList()[i + 1] ? rd.getApexList()[i + 1] : min_y;
				// max_x=max_x<rd.getApexList()[i]?rd.getApexList()[i]:max_x;
				// max_y=max_y<rd.getApexList()[i+1]?rd.getApexList()[i+1]:max_y;
			}
			rd.setPositionBase(new Point2D(min_x, min_y));
			// int dis=max_x-min_x;
			// x_distance=x_distance<dis?dis:x_distance;
			// dis=max_y-min_y;
			// y_distance=y_distance<dis?dis:y_distance;
		}
		// System.out.println("x distance ="+x_distance+" y distance="+y_distance);
		splashLogEnd("computeRoadsBasePoint");
	}

	/**
	 * Aramik
	 */
	public void computeEdgesNewProperties() {
		splashLogStart("computeEdgesNewProperties");
		preComputeLog.logln("\n--------- Enter --> computeEdgesNewProperties() ---------");
		TreeSet<Edge> edgesSet = new TreeSet<Edge>(new EdgeComparator());// for set Twin property in edges
		for (Area area : sosAgent.model().areas()) {
			byte index = 0;
			Edge[] passableEdges = null;
			int[] distances = null;
			for (Edge edge : area.getEdges()) {
				edge.setMyAreaIndex(area.getAreaIndex());
				if (edge.isPassable()) {
					edge.setPassabilityEdgeIndex(index++);
					edgesSet.add(edge);
				}
			}
			passableEdges = new Edge[index];
			distances = new int[index];
			index = 0;
			for (Edge edge : area.getEdges()) {
				if (edge.isPassable()) {
					passableEdges[index] = edge;
					distances[index++] = getCentersDistanceOf(area, edge);
				}
			}
			area.setPassableEdges(passableEdges);
			area.setDistanceOfpassableEdgesFromAreaCenter(distances);
		}
		//		// ---------START DEBUGGING SECTION
		//		if (!SOSConstant.IS_CHALLENGE_RUNNING) {
		//			for (Area area : sosAgent.model().areas()) {
		//				preComputeLog.logln(area + " +++++++++++ passable Edges:");
		//				for (Edge edge : area.getPassableEdges())
		//					preComputeLog.logln(edge.fullDescription());
		//			}
		//		}
		//		// ---------END OF DEBUGGING SECTION
		splashLogEnd("computeEdgesNewProperties");
	}

	/**
	 * Aramik
	 *
	 * @param area
	 * @param edge
	 * @return
	 */
	private int getCentersDistanceOf(Area area, Edge edge) {
		return SOSGeometryTools.distance((edge.getStartX() + edge.getEndX()) / 2, (edge.getStartY() + edge.getEndY()) / 2, area.getX(), area.getY());
	}

	/**
	 * Aramik
	 *
	 * @param ed1
	 * @param ed2
	 * @return
	 */
	private int getCentersDistanceOf(Edge ed1, Edge ed2) {
		return SOSGeometryTools.distance((ed1.getStartX() + ed1.getEndX()) / 2, (ed1.getStartY() + ed1.getEndY()) / 2, (ed2.getStartX() + ed2.getEndX()) / 2, (ed2.getStartY() + ed2.getEndY()) / 2);
	}

	/**
	 * Aramik
	 */
	public void createWorldGraphNodes() {

		splashLogStart("createWorldGraphNodes");
		preComputeLog.logln("\n--------- Enter --> createWorldGraphNodes() ---------");
		short index = 0;
		for (Area area : sosAgent.model().areas())
			for (Edge edge : area.getPassableEdges()) {
				Node node = new Node(index, area.getAreaIndex(), edge);
				sosAgent.model().nodes().add(node);
				edge.setNodeIndex(index++);
			}
		// ---------START DEBUGGING SECTION
		//		if (!SOSConstant.IS_CHALLENGE_RUNNING) {
		//			preComputeLog.logln("Nodes Properties -->");
		//			for (Node node : sosAgent.model().nodes()) {
		//				preComputeLog.logln(node.fullDescription());
		//			}
		//		}
		//		// ---------END OF DEBUGGING SECTION
		splashLogEnd("createWorldGraphNodes");
	}

	/**
	 * Aramik
	 *
	 * @param ed1
	 * @param ed2
	 * @param area
	 * @return
	 */
	private int getLenghtOfWorldGraphEdge(Edge ed1, Edge ed2, Area area) {
		int lenA = area.getDistanceOfpassableEdgesFromAreaCenter()[ed1.getPassabilityEdgeIndex()];
		int lenB = area.getDistanceOfpassableEdgesFromAreaCenter()[ed2.getPassabilityEdgeIndex()];
		return (lenA + lenB + getCentersDistanceOf(ed1, ed2)) / 2;
	}

	/**
	 * Aramik
	 */
	public void createWorldGraphEdges() {
		splashLogStart("createWorldGraphEdges");
		preComputeLog.logln("\n--------- Enter --> createWorldGraphEdges() ---------");
		short index = 0;
		for (Area area : sosAgent.model().areas()) {
			int len = area.getPassableEdges().length;
			area.setWorldgraphEdgesSize((short) ((len * (len - 1)) / 2));
			short[] graphEdges = new short[(len * (len - 1)) / 2 + len];
			Arrays.fill(graphEdges, (short) -1);
			int size = 0;
			for (int i = 0; i < len; ++i) {
				for (int k = i + 1; k < len; ++k) {
					Edge ed1 = area.getPassableEdges()[i], ed2 = area.getPassableEdges()[k];
					GraphEdgeState st = null;
					if (area instanceof Building)
						st = GraphEdgeState.Open;
					else if ((!(sosAgent instanceof PoliceForceAgent))
							&& BlockadeEstimator.doFastPrecompute(sosAgent.getMapInfo().getRealMapName())) {
						if (Math.random() > 0.5d)
							st = GraphEdgeState.FoggyBlock;
						else
							st = GraphEdgeState.FoggyOpen;
					} else
						st = Utils.convertReachabilityStatesToGraphEdgeStates(Reachablity.isReachable((Road) area, ed1, ed2));

					WorldGraphEdge wge = new WorldGraphEdge(index++, area.getAreaIndex(), ed1.getNodeIndex(), ed2.getNodeIndex(), getLenghtOfWorldGraphEdge(ed1, ed2, area), st);
					sosAgent.model().graphEdges().add(wge);
					graphEdges[size++] = wge.getIndex();
				}
			}
			area.setGraphEdges(graphEdges);
		}
		splashLogEnd("createWorldGraphEdges");
	}

	/**
	 * Aramik
	 */
	public void createVirtualGraphEdges() {
		splashLogStart("createVirtualGraphEdges");
		preComputeLog.logln("\n--------- Enter --> createVirtualGraphEdges() ---------");
		short index = (short) sosAgent.model().graphEdges().size();
		boolean[] flags = new boolean[sosAgent.model().nodes().size()];
		Arrays.fill(flags, false);
		for (Node node : sosAgent.model().nodes())
			if (!flags[node.getIndex()]) {
				VirtualGraphEdge vge = new VirtualGraphEdge(index++, node.getIndex(), node.getRelatedEdge().getTwin().getNodeIndex());
				sosAgent.model().graphEdges().add(vge);
				Area a = sosAgent.model().areas().get(node.getAreaIndex());
				int ind = 0;
				while (a.getGraphEdges()[ind] != -1)
					ind++;
				a.getGraphEdges()[ind] = vge.getIndex();
				a = sosAgent.model().areas().get(node.getRelatedEdge().getTwin().getMyAreaIndex());
				ind = 0;
				while (a.getGraphEdges()[ind] != -1)
					ind++;
				a.getGraphEdges()[ind] = vge.getIndex();
				flags[node.getIndex()] = true;
				flags[node.getRelatedEdge().getTwin().getNodeIndex()] = true;
			}
		// ---------START DEBUGGING SECTION
		if (SOSConstant.IS_CHALLENGE_RUNNING) {
			preComputeLog.logln("GraphEdges Properties -->");
			for (GraphEdge ge : sosAgent.model().graphEdges()) {
				preComputeLog.logln(ge.fullDescription());
			}
		}
		// ---------END OF DEBUGGING SECTION
		splashLogEnd("createVirtualGraphEdges");
	}

	/**
	 * @author Aramik
	 */
	public final class EdgeComparator implements java.util.Comparator<Edge>, java.io.Serializable {
		private static final long serialVersionUID = -123456789123525L;

		@Override
		public int compare(Edge ro1, Edge ro2) {
			int x1 = (ro1.getStartX() + ro1.getEndX()) / 2;
			int x2 = (ro2.getStartX() + ro2.getEndX()) / 2;
			if (x1 > x2)
				return 1;
			else if (x2 > x1)
				return -1;
			int y1 = (ro1.getStartY() + ro1.getEndY()) / 2;
			int y2 = (ro2.getStartY() + ro2.getEndY()) / 2;
			if (y1 > y2)
				return 1;
			else if (y2 > y1)
				return -1;
			ro1.setPassabilityTwin(ro2);
			ro2.setPassabilityTwin(ro1);
			return 0;
		}
	}

	// private SOSWorldModel model() {
	// return sosAgent.model();
	//
	// }
	// ###################### {{ REACHABLITY }} ###################################

	// Morteza2011**********************setRoadsExpandArea*************************
	public void setReachablityIndexForRoads() {
		for (Area area : sosAgent.model().roads()) {
			for (short i = 0; i < area.getEdges().size(); i++) {
				area.getEdges().get(i).setReachablityIndex(i);
			}
		}
	}

	// Morteza2011**********************setRoadsExpandArea*************************
	public void setRoadsExpandArea() {
		for (int i = 0; i < sosAgent.model().roads().size(); i++) {
			sosAgent.model().roads().get(i).setExpandedArea(ExpandArea.expandArea(sosAgent.model().roads().get(i)));
		}
	}

	public void verifyEdgesOfAllAreas() {
		for (int i = 0; i < sosAgent.model().roads().size(); i++) {
			sosAgent.model().roads().get(i).setEdges(verifyEdgesForExpand(sosAgent.model().roads().get(i).getEdges()));
		}
		for (int i = 0; i < sosAgent.model().buildings().size(); i++) {
			sosAgent.model().buildings().get(i).setEdges(verifyEdgesForExpand(sosAgent.model().buildings().get(i).getEdges()));
		}
	}

	public static ArrayList<Edge> verifyEdgesForExpand(List<Edge> RealEdges) {
		ArrayList<Edge> edges = new ArrayList<Edge>(RealEdges);
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).getStart().equals(edges.get(i).getEnd())) {
				// System.out.println("removed for zero length: " + edges.get(i));
				edges.remove(i);
				i--;
				continue;
			}
			for (int j = i + 1; j < edges.size(); j++) {
				if (edges.get(i).edgeEquals(edges.get(j))) {
					// System.out.println("removed for repeat: " + edges.get(i));
					edges.remove(j);
					i--;
					break;
				}
			}
		}
		return edges;
	}

	// Morteza2011*****************************************************************
	public void ComputeReachablityByMaxBlockades() {

		for (Road r : sosAgent.model().roads()) {
			try {
				ArrayList<SOSArea> blocksToMerge = new ArrayList<SOSArea>();
				for (SOSBlockade b : r.getMiddleBlockades()) {
					blocksToMerge.add(b.getExpandedBlock());
				}
				r.setMergedBlockades(blocksToMerge);
				r.setReachableParts();
				r.setReachableEdges();
				r.setDisjonSetForEdges();
				r.setDisjonSetForReachablePartsAndEdges();
				//			sosAgent.model().block.removeMiddleBlockadesOfRoad(r);
			} catch (Exception e) {
				preComputeLog.error(r, e);
			}
		}

	}

	// Morteza2011***************setReachablityPrecomputes*************************
	public void setReachablityPrecomputes() {
		splashLogStart("setReachablityPrecomputes");

		verifyEdgesOfAllAreas();
		setReachablityIndexForRoads();
		setRoadsExpandArea();
		ComputeReachablityByMaxBlockades();
		for (Road r : sosAgent.model().roads()) {
			r.getMergedBlockades().clear();
			sosAgent.model().block.removeMiddleBlockadesOfRoad(r);
		}

		//		int num=0;
		//		for (Road r : sosAgent.model().roads()) {
		//			if(r.getMergedBlockades().size()==1&&!Utility.hasIntersect(r.getMergedBlockades().get(0), new SOSArea(r.getEdges())))
		//				num++;
		//		}
		//		System.out.println(num);
		splashLogEnd("setReachablityPrecomputes");
	}

	// ################### {{ END OF REACHABLITY }} ###############################

	// #################### {{ START OF FINDING NEIGHBOURS }} ###############################
	// this section changed by Yoosef

	/**
	 * @author nima
	 *         changed by Yoosef
	 */
	@SuppressWarnings({ "unchecked" })
	public void setNeighbours() {
		splashLogStart("setNeighbours");
		new NeighborsPreCompute().execute();
		splashLogEnd("setNeighbours");
		//		System.err.println(new Error().getStackTrace()[0]);
		//		System.exit(0);
	}

	/** @author nima */
	public void makeNeighborsFile(File file) {
		//		if (SOSConstant.IS_CHALLENGE_RUNNING)
		//			return;
		try {
			file.getParentFile().mkdirs();
			FileOutputStream fileOuputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOuputStream);

			for (Building b : sosAgent.model().buildings()) {
				objectOutputStream.writeObject(b.neighbors_BuildValue());
			}

			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** @author nima */
	//	public void setNearBuildings() {
	//		TimeNamayangar tm = new TimeNamayangar();
	//		tm.start();
	//
	//		List<Building> buildings = sosAgent.model().buildings();
	//		int distance = 50000;// Kobe & VC
	//		if (buildings.size() > 1300)
	//			distance = 200000;// Paris & Berlin
	//		int distance2 = distance*distance;
	//		//		for (Building first : buildings) {
	//		//			Collection<Building> nearBuildings = sosAgent.model().getBuildingsInRange(first.getX(), first.getY(), distance);
	//		//			for (Building second : nearBuildings) {
	//		//				first.getNearBuildings().add(second);
	//		//				second.getNearBuildings().add(first);
	//		//			}
	//		//		}
	//
	//		for (int i = 0; i < buildings.size(); i++) {
	//			Building first = buildings.get(i);
	//			Collection<Building> blist = sosAgent.model().getObjectsInRange(first, distance, Building.class);
	//			for (Building second : blist) {
	//				if(second.getBuildingIndex()<i+1)
	//					continue;
	////				if (first.distance2(second) < distance2)
	//				{
	//					first.getNearBuildings().add(second);
	//					second.getNearBuildings().add(first);
	//				}
	//			}
	//		}
	//
	//		if (!SOSConstant.IS_CHALLENGE_RUNNING) {
	//			for (Building b : sosAgent.model().buildings()) {
	//				nl("Source Building: " + b);
	//				nl("nearBuildings >> BuildingsWithDisatanceLessThan50Meter: " + b.getNearBuildings());
	//			}
	//			nl("-------------------------------------------------------------------");
	//		}
	//		tm.finish();
	//		preComputeLog.consoleDebug("setNearBuildings:" + tm);
	//	}

	// Morteza2011********************************************************************************************************************

	// ################### {{ END OF FINDING NEIGHBOURS }} ###############################

	// *********#############************############ SOS Blockade
	/**
	 * @author Ali
	 */
	public void findMaxBlockadesForBuilding() {
		splashLogStart("findMaxBlockadesForBuilding");
		// ConsoleProcessBar consoleProcessBar = new ConsoleProcessBar(50, sosAgent.model().buildings().size());
		// File maxBlocksFile = new File("SOSFiles/BlockadeEstimator/" + sosAgent.model().buildings().size() + "/maxBlocks.sos");
		// File middleBlocksFile = new File("SOSFiles/BlockadeEstimator/" + sosAgent.model().buildings().size() + "/middleBlocks.sos");
		// if (maxBlocksFile.exists() && maxBlocksFile.exists()) {
		// sosAgent.blockadeEstimator.readMaxBlockadeFromFile(maxBlocksFile);
		// sosAgent.blockadeEstimator.readMiddleBlockadeFromFile(middleBlocksFile);
		// } else {
		// for (Building building : sosAgent.model().buildings()) {
		// // preComputeLog.log(consoleProcessBar.progress(), true);
		// sosAgent.blockadeEstimator.findBuildingMayCreateBlockadesOnRoad(building);
		// sosAgent.blockadeEstimator.createMiddleBlockades(building);
		// }
		// if (!SOSConstant.IS_CHALLENGE_RUNNING) {
		// sosAgent.blockadeEstimator.writeMaxBlockadeToFile(maxBlocksFile);
		// sosAgent.blockadeEstimator.writeMiddleBlockadeToFile(middleBlocksFile);
		// }
		// }
		new MiddleBlockadePreCompute().execute();
		splashLogEnd("findMaxBlockadesForBuilding");
	}

	// /**
	// * @author Ali
	// */
	// public void findRoadNearBuildings() {
	// preComputeLog.consoleInfo("findRoadNearBuildings...");
	// String oldText = LaunchAgents.splash.getText();
	// LaunchAgents.splash.setText(oldText + " || findRoadNearBuildings");
	// long t = System.currentTimeMillis();
	// ConsoleProcessBar consoleProcessBar = new ConsoleProcessBar(50, sosAgent.model().roads().size());
	// for (Road road : sosAgent.model().roads()) {
	// // preComputeLog.debug(i + " : " + road + ", ");
	// preComputeLog.log(consoleProcessBar.progress(), true);
	// LaunchAgents.splash.setProgressStep(consoleProcessBar.getCurrentPercent());
	// road.findNearBuildings();
	// }
	// preComputeLog.consoleDebug("findRoadNearBuildings time usage=" + (System.currentTimeMillis() - t) + " ms");
	// LaunchAgents.splash.setText(oldText);
	// }

	String oldText;
	TimeNamayangar tmStart;

	public void splashLogStart(String string) {
		//		preComputeLog.consoleInfo(string+"...");
		if (LaunchAgents.splash != null) {
			oldText = LaunchAgents.splash.getText();
			LaunchAgents.splash.setText(oldText + " || " + string);
		}
		tmStart = new TimeNamayangar();
	}

	public void splashLogEnd(String string) {
		if (LaunchAgents.splash != null)
			LaunchAgents.splash.setText(oldText);
		preComputeLog.consoleDebug(time + string + tmStart);
		processGUI();
	}

	public void processGUI() {
		if (LaunchAgents.splash != null)
			LaunchAgents.splash.setProgressStep(processStep * (pCount++));
	}
}
