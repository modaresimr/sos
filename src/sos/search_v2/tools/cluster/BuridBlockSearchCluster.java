package sos.search_v2.tools.cluster;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.FireBrigade;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.util.HungarianAlgorithm;
import sos.fire_v2.base.tools.FireStarCluster;
import sos.fire_v2.base.tools.FireStarZone;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.tools.SearchRegion;
import sos.search_v2.tools.genetic.GeneticClusterAssigner;
import sos.tools.Utils;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.google.code.ekmeans.EKmeans;

/**
 * @author Salim Malakouti
 * @param <E>
 */
public class BuridBlockSearchCluster<E extends Human> extends MapClusterType<E> {
	public static int KMEANS_ITERATIONS = 15; //Added By Salim
	public static int CLUSTER_SIZE = 6; //Added By Salim
	private ArrayList<ClusterData> allClusters = null;

	public BuridBlockSearchCluster(SOSAgent<E> me, List<E> agents) {
		super(me, agents);
	}

	@Override
	public void startClustering(SOSWorldModel model) {
		log().info("Starting cluster..." + this.getClass().getSimpleName());
//		if (isNoCommunication() && me instanceof FireBrigadeAgent) {
//			doStarClustering();
//			return;
//		}
		List<E> nonBuriyAgents = getNonbuiryAgents();
		List<E> buriyAgents = getBuiryAgents(nonBuriyAgents);
		allClusters = new ArrayList<ClusterData>(nonBuriyAgents.size() + buriyAgents.size());
		ArrayList<ClusterData> nonBuriyClusters = makeClusters(nonBuriyAgents.size());
		ArrayList<ClusterData> buriyClusters = makeClusters(buriyAgents.size());

		for (ClusterData clusterData : nonBuriyClusters) {
			allClusters.add(clusterData);
		}
		for (ClusterData clusterData : buriyClusters) {
			allClusters.add(clusterData);
			clusterData.setCoverer(true);
		}
		assign(nonBuriyAgents, nonBuriyClusters);
		assign(buriyAgents, buriyClusters);
	}

	private ArrayList<ClusterData> makeClustersWeka(int size) {
		Dataset[] blocks = wekaKmeans(size);
		ArrayList<SearchRegion> regions = getRegions(blocks);
		ArrayList<ClusterData> clusters = new ArrayList<ClusterData>();
		for (SearchRegion searchRegion : regions) {
			ClusterData clusterData = new ClusterData(searchRegion.getId(), searchRegion.getBuildings(), me, searchRegion.getId());
			clusters.add(clusterData);
		}
		return clusters;
	}

	private ArrayList<ClusterData> makeClustersEkmeans(int size) {
		int n = me.model().buildings().size(); // the number of data to cluster
		int k = size; // the number of cluster
		Random random = new Random(2);

		double[][] points = new double[n][2];
		for (int i = 0; i < n; i++) {
			Building b = me.model().buildings().get(i);
			Pair<Double, Double> bestpoint = getBuildingBestPoint(b);
			points[i][0] = bestpoint.first();
			points[i][1] = bestpoint.second();
		}
		double[][] centroids = new double[k][2];
		// lets create random centroids between 0 and 100 (in the same space as our points)
		for (int i = 0; i < k; i++) {
			centroids[i][0] = Math.abs(random.nextInt((int) me.model().getBounds().getWidth()));
			centroids[i][1] = Math.abs(random.nextInt((int) me.model().getBounds().getHeight()));
		}
		EKmeans eKmeans = new EKmeans(centroids, points);

		eKmeans.setIteration(60);
		eKmeans.setEqual(true);
		eKmeans.setDistanceFunction(EKmeans.MANHATTAN_DISTANCE_FUNCTION);
		eKmeans.run();
		HashSet<Building>[] clusters = new HashSet[k];

		int[] assignments = eKmeans.getAssignments();
		// here we just print the assignement to the console.
		for (int i = 0; i < n; i++) {
			int clusterIndex = assignments[i];
			if (clusters[clusterIndex] == null)
				clusters[clusterIndex] = new HashSet<Building>();
			clusters[clusterIndex].add(me.model().buildings().get(i));
		}
		ArrayList<ClusterData> result = new ArrayList<ClusterData>();
		int index = 0;
		for (HashSet<Building> cluster : clusters)
			result.add(new ClusterData(index, cluster, me, index++));
		return result;
	}

	private ArrayList<ClusterData> makeClusters(int size) {
//		return makeClustersEkmeans(size);
		return makeClustersWeka(size);
	}

	private void doStarClustering() {
		FireStarCluster fs = new FireStarCluster(me);
		List<E> unassignedFire = new ArrayList<E>();
		allClusters = new ArrayList<ClusterData>(unassignedFire.size());
		for (FireBrigade e : me.model().fireBrigades()) {
			unassignedFire.add((E) e);
		}
		int numberOfFireInEachCluster = 4;
		int numberOfCluster;
		if (unassignedFire.size() > 10) {
			numberOfCluster = unassignedFire.size() / (numberOfFireInEachCluster + 1) + 1;
		} else {
			numberOfFireInEachCluster = 1;
			numberOfCluster = unassignedFire.size() - 1;
		}

		fs.startClustering(numberOfCluster);
		int index = 0;
		for (int i = 0; i < numberOfFireInEachCluster; i++) {
			for (int j = 0; j < numberOfCluster; j++) {
				FireStarZone fsz = fs.getStarZones()[j];
				ClusterData cluster = new ClusterData(fsz.hashCode(), new HashSet<Building>(fsz.getZoneBuildings()), me, index++);
				if (i != 0)
					cluster.setCoverer(true);
				//				ClusterData cluster = new ClusterData(tmpclusters.get(j));
				allClusters.add(cluster);
			}
		}
		while (index != unassignedFire.size()) {
			FireStarZone fsz = fs.getStarZones()[0];
			ClusterData cluster = new ClusterData(fsz.hashCode(), new HashSet<Building>(fsz.getZoneBuildings()), me, index++);
			allClusters.add(cluster);
		}

		assign(unassignedFire, allClusters);
	}

	private void assign(List<E> assignAgents, ArrayList<ClusterData> clusters) {
		if (assignAgents.size() == 0)
			return;
		//Assigning
		//		if (isNoCommunication())
		//			assignCommunicationLess(assignAgents, clusters);
		//		else
		assignNormal(assignAgents, clusters);
	}

	private List<E> getBuiryAgents(List<E> nonBuriyAgents) {
		log().info("geting buiryAgents====non beriyAgents:" + nonBuriyAgents + "");
		ArrayList<E> clone = new ArrayList<E>(agents);
		clone.removeAll(nonBuriyAgents);
		log().debug("buiryAgents: " + clone);
		return clone;
	}

	private List<E> getNonbuiryAgents() {
		log().info("geting NonbuiryAgents");
		List<E> validAgents = getValidAgents(agents);
		List<E> result = new ArrayList<E>();
		for (E next : validAgents) {
			if (next.getAreaPosition() instanceof Building)
				continue;
			//enterance agents
			result.add(next);
		}

		log().debug("NonbuiryAgents: " + result);
		return result;

	}

	private void assignFiresNoComm(List<E> assignAgents, ArrayList<ClusterData> allClusters) {
		double cx = me.model().mapCenter().getX();
		double cy = me.model().mapCenter().getY();
		double mapMaxDistance = Math.max(Utils.distance(me.model().getWorldBounds().first().first(), 0, me.model().getWorldBounds().second().first(), 0) / 2, Utils.distance(0, me.model().getWorldBounds().first().second(), 0, me.model().getWorldBounds().second().second()) / 2);

		ArrayList<ClusterData> level1 = new ArrayList<ClusterData>();
		ArrayList<ClusterData> level2 = new ArrayList<ClusterData>();
		ArrayList<ClusterData> level3 = new ArrayList<ClusterData>();

		for (ClusterData cd : allClusters) {
			double d = Utils.distance(cd.getX(), cd.getY(), cx, cy);

			if (d < (((double) 2 / 5) * mapMaxDistance)) {
				level1.add(cd);
			} else if (d < (((double) 2 / 3) * mapMaxDistance)) {
				level2.add(cd);
			} else {
				double filterDistance = distanceToFilter(cd, allClusters);
				if (filterDistance > MIN_DISTANCE_THRESHOLD())
					continue;
				else
					level3.add(cd);
			}
		}
		//		int totalValidClusters = level1.size() + level2.size() + level3.size();
		int remFireBrigades = assignAgents.size();
		ArrayList<E> temp = new ArrayList<E>();
		temp.addAll(assignAgents);
		//-----------------------------round-1
		remFireBrigades = temp.size();
		if (remFireBrigades <= 0)
			return;
		for (ClusterData sr : level1) {
			assignFireNoComm(temp, sr);
		}
		//-----------------------------round-2
		remFireBrigades = temp.size();
		//		System.out.println("rem fires:" + remFireBrigades);
		if (remFireBrigades <= 0)
			return;

		for (ClusterData sr : level2) {
			assignFireNoComm(temp, sr);
		}

		//-----------------------------round-3
		remFireBrigades = temp.size();
		//		System.out.println("rem fires:" + remFireBrigades);
		while (remFireBrigades > 0) {
			for (ClusterData sr : level1) {
				assignFireNoComm(temp, sr);
			}
			for (ClusterData sr : level3) {
				assignFireNoComm(temp, sr);
			}
			for (ClusterData sr : level2) {
				assignFireNoComm(temp, sr);
			}

			remFireBrigades = temp.size();
			//			System.out.println("rem fires:" + remFireBrigades);
		}
	}

	private void assignFireNoComm(ArrayList<E> agents, ClusterData sr) {
		if (agents.size() == 0)
			return;
		E bestAgent = getBestAgent(sr, agents);
		clusters.put(bestAgent, sr);
		//		System.err.println("clusters: " + clusters.size() + " agents: " + agents.size());
	}

	private double distanceToFilter(ClusterData cd, Collection<ClusterData> values) {
		ArrayList<Pair<ClusterData, Double>> distances = new ArrayList<Pair<ClusterData, Double>>(values.size());
		for (ClusterData c : values) {
			double d = Utils.distance(c.getX(), c.getY(), cd.getX(), cd.getY());
			distances.add(new Pair<ClusterData, Double>(c, d));
		}
		Collections.sort(distances, new Comparator<Pair<ClusterData, Double>>() {

			@Override
			public int compare(Pair<ClusterData, Double> o1, Pair<ClusterData, Double> o2) {
				if (o1.second() < o2.second())
					return -1;
				if (o1.second() > o2.second())
					return 1;
				return 0;
			}
		});
		double maxDistance = -1;
		for (int i = 1; i < Math.min(distances.size() - 1, 4); i++) {
			double minDistance = Integer.MAX_VALUE;
			for (Building b : cd.getBuildings()) {
				for (Building b2 : distances.get(i).first().getBuildings()) {
					double d = Utils.distance(b.getX(), b.getY(), b2.getX(), b2.getY());
					if (d == 0)
						continue;
					if (d < minDistance)
						minDistance = d;
				}

			}
			if (minDistance > maxDistance)
				maxDistance += minDistance;
		}
		maxDistance = maxDistance / 3;
		return maxDistance;
	}

	private double MIN_DISTANCE_THRESHOLD() {
		if (me.model().me().getAgent().getMapInfo().isBigMap())
			return 60000;
		if (me.model().me().getAgent().getMapInfo().isMediumMap())
			return 25000;
		else
			return 10000;
	}

	private void assignCommunicationLess(List<E> assignAgents, ArrayList<ClusterData> allClusters) {
		//		if (me instanceof FireBrigadeAgent) {
		//			assignFiresNoComm(assignAgents,allClusters);
		//			return;
		//		}

		List<E> agents = assignAgents;

		while (agents.size() != 0) {
			for (int i = 0; i < allClusters.size(); i++) {
				if (agents.size() == 0)
					break;
				E bestAgent = getBestAgent(allClusters.get(i), agents);
				this.clusters.put(bestAgent, allClusters.get(i));
			}
		}
		assignSpecialTaskPoliceCluster(me.model());
		//		int num = getMaxForRegion(regions.size(), agents.size());
		//			for (SearchRegion sr : regions) {
		//			for (int i = 0; i < num; i++) {
		//				E bestAgent = getBestAgent(sr, agents);
		//				ClusterData cd = new ClusterData(sr.getId(), sr.getBuildings(), me, sr.getId());
		//				clusters.put(bestAgent, cd);
		//			}
		//		}
		//		//		System.err.println("clusters: " + clusters.size() + " agents: " + agents.size());
	}

	private E getBestAgent(ClusterData region, Collection<E> agents) {
		double bestD = Double.MAX_VALUE;
		E best = null;
		//		System.out.println("-------");
		//		System.out.println(agents);
		//		for (int i = agents.size() - 1; i > -1; i--) {
		//			double d = Utils.distance(region.getX(), region.getY(), agents.get(i).getX(), agents.get(i).getY());
		//			//			System.out.println(agents.get(i) + " " + d);
		//			if (d < bestD) {
		//				bestD = d;
		//				best = i;
		//			}
		//		}
		for (E e : agents) {
			double d = Utils.distance(region.getX(), region.getY(), e.getX(), e.getY());
			//			System.out.println(agents.get(i) + " " + d);
			if (d < bestD) {
				bestD = d;
				best = e;
			}
		}
		//		if (best == -1)
		//			System.out.println("");
		agents.remove(best);
		return best;

	}

	private void assignNormal(List<E> assignAgents, ArrayList<ClusterData> assignClusters) {
		//		for (int i = 0; i < agents.size(); i++) {
		//			ClusterData chosen = chooseNearestRegion(allClusters, agents.get(i));
		//			clusters.put(agents.get(i), chosen);
		//		}
		//		testGen(assignAgents, assignClusters);

		ArrayList<Pair<E, ClusterData>> assignList;
		//		assignList = simpleAssign(assignAgents, assignClusters);
		//		assignList = geneticAssign(assignAgents, assignClusters);
		assignList = hungarianAssign(assignAgents, assignClusters);
		for (Pair<E, ClusterData> agent_cluster : assignList) {
			clusters.put(agent_cluster.first(), agent_cluster.second());
		}
		//		assignSpecialTaskPoliceCluster(me.model());
	}

	/**
	 * This functions assign the middlest cluster to special task policeforce agents. <br>
	 * If there are more than one special task agent clusters will be assigned due to their distance to the center of map.<br>
	 * This means that special task agents will not have the same cluster at all. Important: number of special task agents should be smaller than all clusters
	 *
	 * @param model
	 */
	private void assignSpecialTaskPoliceCluster(final SOSWorldModel model) {
		if (!(model.sosAgent() instanceof PoliceForceAgent))
			return;

		List<ClusterData> tmp = new ArrayList<ClusterData>(allClusters);
		Collections.sort(tmp, new Comparator<ClusterData>() {

			@Override
			public int compare(ClusterData o1, ClusterData o2) {
				double d1 = model.mapCenter().distance(o1.getX(), o1.getY());
				double d2 = model.mapCenter().distance(o1.getX(), o1.getY());
				if (d1 > d2)
					return 1;
				else if (d1 == d2)
					return 0;
				else
					return -1;
			}
		});
		//		System.out.println(tmp);

	}

	private ArrayList<Pair<E, ClusterData>> simpleAssign(List<E> agents, ArrayList<ClusterData> allClusters) {
		ArrayList<Pair<E, ClusterData>> assignList = new ArrayList<Pair<E, ClusterData>>();
		ArrayList<ClusterData> clone = (ArrayList<ClusterData>) allClusters.clone();
		int sum2 = 0;
		for (int i = 0; i < agents.size(); i++) {
			ClusterData chosen = chooseNearestRegion(clone, agents.get(i));
			//			clusters.put(agents.get(i), chosen);
			assignList.add(new Pair<E, ClusterData>(agents.get(i), allClusters.get(i)));
			sum2 += Utils.distance(agents.get(i).getX(), agents.get(i).getY(), chosen.getX(), chosen.getY());
		}
		System.out.println("sum simle distance= " + sum2);
		return assignList;

	}

	private ArrayList<Pair<E, ClusterData>> geneticAssign(List<E> agents, ArrayList<ClusterData> allClusters) {
		ArrayList<Pair<E, ClusterData>> assignList = new ArrayList<Pair<E, ClusterData>>();
		GeneticClusterAssigner gca = new GeneticClusterAssigner(me.model(), (List<Human>) agents);
		int sum1 = 0;
		List<ClusterData> decision = gca.decide(allClusters);
		for (int i = 0; i < agents.size(); i++) {
			//			clusters.put(agents.get(i), chosen);
			//			clusters.put(agents.get(i), decision.get(i));
			assignList.add(new Pair<E, ClusterData>(agents.get(i), decision.get(i)));
			sum1 += Utils.distance(agents.get(i).getX(), agents.get(i).getY(), decision.get(i).getX(), decision.get(i).getY());
		}
		System.out.println("sum genetic distance= " + sum1);
		return assignList;
	}

	private void testGen(List<E> agents, ArrayList<ClusterData> allClusters) {
		if (agents.size() == 0)
			return;
		GeneticClusterAssigner gca = new GeneticClusterAssigner(me.model(), (List<Human>) agents);
		ArrayList<ClusterData> clone = (ArrayList<ClusterData>) allClusters.clone();
		int sum1 = 0;
		int sum2 = 0;
		for (int i = 0; i < agents.size(); i++) {
			ClusterData chosen = chooseNearestRegion(clone, agents.get(i));
			//			clusters.put(agents.get(i), chosen);
			sum2 += Utils.distance(agents.get(i).getX(), agents.get(i).getY(), chosen.getX(), chosen.getY());
		}

		List<ClusterData> decision = gca.decide(allClusters);
		for (int i = 0; i < agents.size(); i++) {
			//			clusters.put(agents.get(i), chosen);
			clusters.put(agents.get(i), decision.get(i));
			sum1 += Utils.distance(agents.get(i).getX(), agents.get(i).getY(), decision.get(i).getX(), decision.get(i).getY());
		}
		System.out.println("sum old distance= " + sum2);
		System.out.println("sum new distance= " + sum1);
	}

	private ArrayList<Pair<E, ClusterData>> hungarianAssign(List<E> agents, ArrayList<ClusterData> allClusters) {
		long t = System.currentTimeMillis();
		ArrayList<Pair<E, ClusterData>> assignList = new ArrayList<Pair<E, ClusterData>>();
		short size = (short) allClusters.size();
		if (agents.size() != size) {
			log().warn("tedade cluster ha bishtar az tedade agent hast !!!!!!");
		}
		double[][] cost = new double[size][size];

		for (int i = 0; i < size; i++) {
			//			CostFrom cf=new CostFrom(me.model(),Arrays.asList(allClusters.get(i).getNearestBuildingToCenter()));
			for (int j = 0; j < size; j++) {

				cost[i][j] = Point.distance(allClusters.get(i).getX(), allClusters.get(i).getY(), agents.get(j).getX(), agents.get(j).getY());
				//				cost[i][j] = cf.getCostTo(agents.get(j).getPositionPair());

			}
		}
		HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(cost);
		int[] result = hungarianAlgorithm.execute();

		int sum1 = 0;
		for (int index = 0; index < result.length; index++) {
			if (result[index] == -1) {
				log().warn("be cluster " + allClusters.get(index) + " be kasi assign nashode");
				continue;
			}
			E agent = agents.get(result[index]);
			ClusterData cluster = allClusters.get(index);
			log().debug("cluster =" + cluster + "be agent =" + agent + "assign shod");
			sum1 += Utils.distance(agent.getX(), agent.getY(), cluster.getX(), cluster.getY());
			assignList.add(new Pair<E, ClusterData>(agent, cluster));
		}
		//		System.out.println("sum hungrian distance= " + sum1+ " time:"+(System.currentTimeMillis()-t)+"ms");
		return assignList;
	}

	private int getMaxForRegion(int remainingRegions, int remainingAgents) {
		return (int) Math.ceil((remainingAgents / remainingRegions));

	}

	public Building[] getArray(List<Building> buildings) {
		Building[] arr = new Building[buildings.size()];
		for (int i = 0; i < buildings.size(); i++) {
			arr[i] = buildings.get(i);
		}
		return arr;
	}

	private Dataset[] wekaKmeans(int size) {
		if (size == 0)
			return null;
		Attribute at1 = new Attribute("x");
		Attribute at2 = new Attribute("y");
		FastVector fv = new FastVector(2);
		fv.addElement(at1);
		fv.addElement(at2);
		Instances instances = new Instances("Rel", fv, 2);

		Instance ins;
		for (Building b : me.model().buildings()) {
			ins = new Instance(2);
			Pair<Double, Double> p = getBuildingBestPoint(b);
			ins.setValue(at1, p.first());
			ins.setValue(at2, p.second());
			instances.add(ins);
			//			double[] points = new double[] { b.getX(), b.getY() };
			//			roadSet.add(new SOSDenseInstance(points, b));
		}
		// Clustering BuildingSet
		SimpleKMeans skm = new SimpleKMeans();
		skm.setPreserveInstancesOrder(true);
		try {
			int numValidAgents = size;

			skm.setNumClusters(numValidAgents);
			skm.setMaxIterations(KMEANS_ITERATIONS);
			skm.buildClusterer(instances);
			int[] assignments = skm.getAssignments();
			Dataset[] blocks = new Dataset[numValidAgents];
			for (int i = 0; i < blocks.length; i++) {
				blocks[i] = new DefaultDataset();
			}
			for (int i = 0; i < assignments.length; i++) {
				int a = assignments[i];
				Building b = me.model().buildings().get(i);
				blocks[a].add(new SOSDenseInstance(new double[] { b.getX(), b.getY() }, b));
			}
			return blocks;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public Road getEnteranceRoad(Building b, HashSet<Building> checked) {
		checked.add(b);
		for (Area neighbor : b.getNeighbours()) {
			if (neighbor instanceof Road)
				return (Road) neighbor;
		}
		for (Area neighbor : b.getNeighbours()) {
			if (checked.contains(neighbor))
				continue;
			return getEnteranceRoad((Building) neighbor, checked);
		}
		return null;
	}

	private int getNumValidAgents() {
		int numValidAgents = agents.size();
		if (isNoCommunication())//NoComunication
			numValidAgents = getNumValidAgentsForCommunicationLess();
		//		if(numValidAgents==0)
		//			throw new Error("Why numvalids:"+numValidAgents+"?");
		return Math.max(numValidAgents, 1);
	}

	private List<E> getValidAgents(List<E> agents) {
		log().info("geting ValidAgents from list " + agents);
		List<E> result = new ArrayList<E>(agents);
		if (isNoCommunication())//NoComunication
			getValidAgentsForCommunicationLess(result);
		log().debug("valid agents are:" + result);
		return result;
	}

	private void getValidAgentsForCommunicationLess(List<E> result) {
		//notting
	}

	public boolean isNoCommunication() {
		return me.messageSystem.type == Type.NoComunication;
	}

	private int getNumValidAgentsForCommunicationLess() {
		int valids = Integer.MAX_VALUE;
		//		if (me.model().fireBrigades().size() != 0)
		//			valids = Math.min(me.model().fireBrigades().size(), valids);
		if (me.model().policeForces().size() != 0)
			valids = Math.min(me.model().policeForces().size(), valids);
		if (me.model().ambulanceTeams().size() != 0)
			valids = Math.min(me.model().ambulanceTeams().size(), valids);
		//		System.err.println(valids);
		return valids;
	}

	private Pair<Double, Double> getBuildingBestPoint(Building b) {
		double cx = 0;
		double cy = 0;
		if (b.getPassableEdges().length == 0)
			return new Pair<Double, Double>((double) b.getX(), (double) b.getY());
		//		for (Edge e : b.getPassableEdges()) {
		//			cx += e.getMidPoint().getX();
		//			cy += e.getMidPoint().getY();
		//		}
		//		return new Pair<Double, Double>(cx / b.getPassableEdges().length, cy / b.getPassableEdges().length);
		Road road = getEnteranceRoad(b, new HashSet<Building>());
		if (road == null)
			return new Pair<Double, Double>((double) b.getX(), (double) b.getY());

		return new Pair<Double, Double>((double) road.getX(), (double) road.getY());
	}

	//	private List<Building> getBuildingNeigbours(Road road) {
	//		Queue<Area> possibles = new LinkedList<Area>();
	//		List<Building> results = new ArrayList<Building>();
	//		possibles.add(road);
	//		int index = 0;
	//		while (!possibles.isEmpty()) {
	//			if (index > 20)
	//				System.out.println(index);
	//			index++;
	//			Area current = possibles.remove();
	//			if (current instanceof Building && !results.contains(current))
	//				results.add((Building) current);
	//			for (Area a : current.getNeighbours()) {
	//				if (a instanceof Building && !results.contains(current))
	//					possibles.add(a);
	//			}
	//		}
	//		return results;
	//	}

	private ClusterData chooseNearestRegion(ArrayList<ClusterData> regions, E agent) { //Salim

		double minD = Double.MAX_VALUE;
		int best = -1;
		int index = 0;
		for (ClusterData region : regions) {
			double d = Utils.distance(region.getX(), region.getY(), agent.getX(), agent.getY());
			if (d < minD) {
				best = index;
				minD = d;
			}
			index++;
		}
		// returning best
		ClusterData result = regions.get(best);
			regions.remove(best);

		return result;
	}

	/**
	 * finds regions to be assigned to agents
	 *
	 * @return
	 */
	private ArrayList<SearchRegion> getRegions(Dataset[] blockSearchClusters) { //Salim
		ArrayList<SearchRegion> regions = new ArrayList<SearchRegion>();
		if (blockSearchClusters == null)
			return regions;
		int totalClusters = blockSearchClusters.length;
		int index = 0;
		for (Dataset d : blockSearchClusters) {
			if (d.size() == 0)
				continue;
			regions.add(new SearchRegion(d, index));
			index++;
		}
		int neededClusters = totalClusters - regions.size();
		if (neededClusters != 0) {
			splitClusters(neededClusters, regions);
		}
		return regions;
	}

	private void splitClusters(int neededClusters, ArrayList<SearchRegion> regions) {
		System.err.println("[ERROR] tell salim. tell salim(ho ba to hastam.. begoo dige)....");
		Collections.sort(regions, new Comparator<SearchRegion>() {

			@Override
			public int compare(SearchRegion o1, SearchRegion o2) {
				if (o1.getBuildings().size() > o2.getBuildings().size())
					return -1;
				if (o1.getBuildings().size() < o2.getBuildings().size())
					return 1;
				return 0;
			}
		});

		ArrayList<SearchRegion> tmp = new ArrayList<SearchRegion>(neededClusters);
		for (int i = neededClusters - 1; i > -1; i--) {
			tmp.add(regions.get(i));
			regions.remove(i);
		}
		for (SearchRegion sr : tmp) {
			Pair<SearchRegion, SearchRegion> p;
			try {
				p = split(sr, regions.size());
				regions.add(p.first());
				regions.add(p.second());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private Pair<SearchRegion, SearchRegion> split(SearchRegion sr, int regionSize) throws Exception {
		Attribute at1 = new Attribute("x");
		Attribute at2 = new Attribute("y");
		FastVector fv = new FastVector(2);
		fv.addElement(at1);
		fv.addElement(at2);
		Instances instances = new Instances("Rel", fv, 2);

		Instance ins;
		for (Building b : sr.getBuildings()) {
			ins = new Instance(2);
			Pair<Double, Double> p = getBuildingBestPoint(b);
			ins.setValue(at1, p.first());
			ins.setValue(at2, p.second());
			instances.add(ins);
		}
		SimpleKMeans skm = new SimpleKMeans();
		skm.setPreserveInstancesOrder(true);
		skm.setNumClusters(2);
		skm.setMaxIterations(KMEANS_ITERATIONS);
		skm.buildClusterer(instances);
		int[] assignments = skm.getAssignments();
		Dataset[] blocks = new Dataset[2];
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = new DefaultDataset();
		}
		for (int i = 0; i < assignments.length; i++) {
			int a = assignments[i];
			Building b = me.model().buildings().get(i);
			blocks[a].add(new SOSDenseInstance(new double[] { b.getX(), b.getY() }, b));
		}

		SearchRegion first = new SearchRegion(blocks[0], regionSize);
		SearchRegion second = new SearchRegion(blocks[1], regionSize + 1);

		return new Pair<SearchRegion, SearchRegion>(first, second);
	}

	@Override
	public Collection<ClusterData> allClusters() {
		return allClusters;
	}
}
