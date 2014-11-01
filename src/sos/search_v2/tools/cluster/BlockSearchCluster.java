package sos.search_v2.tools.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.message.structure.MessageConstants.Type;
import sos.police_v2.PoliceForceAgent;
import sos.search_v2.tools.SearchRegion;
import sos.search_v2.tools.genetic.GeneticClusterAssigner;
import sos.tools.Utils;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Salim Malakouti
 * @param <E>
 */
public class BlockSearchCluster<E extends Human> extends MapClusterType<E> {
	public static int KMEANS_ITERATIONS = 15; //Added By Salim
	private ArrayList<ClusterData> allClusters = null;

	public BlockSearchCluster(SOSAgent<E> me, List<E> agents) {
		super(me, agents);
	}

	@Override
	public void startClustering(SOSWorldModel model) {
		Dataset[] blocks = wekaKmeans();
		ArrayList<SearchRegion> regions = getRegions(blocks);

		allClusters = new ArrayList<ClusterData>(regions.size());
		for (int i = 0; i < regions.size(); i++) {
			allClusters.add(new ClusterData(regions.get(i).getId(), regions.get(i).getBuildings(), me, regions.get(i).getId()));
		}
		ArrayList<ClusterData> tmp = new ArrayList<ClusterData>();
		tmp.addAll(allClusters());
		//Assigning
		if (isNoCommunication())
			assignCommunicationLess(tmp);
		else
			assignNormal(tmp, model);

	}

	private void assignFiresNoComm(ArrayList<ClusterData> allClusters) {
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
		int remFireBrigades = agents.size();
		ArrayList<E> temp = new ArrayList<E>();
		temp.addAll(this.agents);
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

	private void assignCommunicationLess(ArrayList<ClusterData> allClusters) {
		//		if (me instanceof FireBrigadeAgent) {
		//			assignFiresNoComm(allClusters);
		//			return;
		//		}

		List<E> agents = getValidAgents(this.agents);

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

	private E getBestAgent(ClusterData region, List<E> agents) {
		double bestD = Double.MAX_VALUE;
		int best = -1;
		//		System.out.println("-------");
		//		System.out.println(agents);
		for (int i = agents.size() - 1; i > -1; i--) {
			double d = Utils.distance(region.getX(), region.getY(), agents.get(i).getX(), agents.get(i).getY());
			//			System.out.println(agents.get(i) + " " + d);
			if (d < bestD) {
				bestD = d;
				best = i;
			}
		}
		//		if (best == -1)
		//			System.out.println("");
		E result = agents.get(best);
		agents.remove(best);
		return result;

	}

	private void assignNormal(ArrayList<ClusterData> allClusters, SOSWorldModel model) {
		//		for (int i = 0; i < agents.size(); i++) {
		//			ClusterData chosen = chooseNearestRegion(allClusters, agents.get(i));
		//			clusters.put(agents.get(i), chosen);
		//		}
		testGen(allClusters);
		assignSpecialTaskPoliceCluster(model);
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

	private void testGen(ArrayList<ClusterData> allClusters) {
		List<E> agents = getValidAgents(this.agents);
		GeneticClusterAssigner gca = new GeneticClusterAssigner(me.model(), (List<Human>) agents);
		ArrayList<ClusterData> clone = (ArrayList<ClusterData>) allClusters.clone();
		int sum1 = 0;
		int sum2 = 0;
		for (int i = 0; i < agents.size(); i++) {
			ClusterData chosen = chooseNearestRegion(clone, agents.get(i));
			//			clusters.put(agents.get(i), chosen);
			sum2 += Utils.distance(agents.get(i).getX(), agents.get(i).getY(), chosen.getX(), chosen.getY());
		}
		System.out.println("sum old distance= " + sum2);

		List<ClusterData> decision = gca.decide(allClusters);
		for (int i = 0; i < agents.size(); i++) {
			//			clusters.put(agents.get(i), chosen);
			clusters.put(agents.get(i), decision.get(i));
			sum1 += Utils.distance(agents.get(i).getX(), agents.get(i).getY(), decision.get(i).getX(), decision.get(i).getY());
		}
		System.out.println("sum old distance= " + sum2);
		System.out.println("sum new distance= " + sum1);
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

	private Dataset[] wekaKmeans() {
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
			int numValidAgents = getNumValidAgents();

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

	private int getNumValidAgents() {
		int numValidAgents = agents.size();
		if (isNoCommunication())//NoComunication
			numValidAgents = getNumValidAgentsForCommunicationLess();
		//		if(numValidAgents==0)
		//			throw new Error("Why numvalids:"+numValidAgents+"?");
		return Math.max(numValidAgents, 1);
	}

	private List<E> getValidAgents(List<E> agents) {
		List<E> result = new ArrayList<E>(agents);
		if (isNoCommunication())//NoComunication
			getValidAgentsForCommunicationLess(result);
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
		for (Edge e : b.getPassableEdges()) {
			cx += e.getMidPoint().getX();
			cy += e.getMidPoint().getY();
		}
		return new Pair<Double, Double>(cx / b.getPassableEdges().length, cy / b.getPassableEdges().length);
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
