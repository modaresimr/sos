package sos.search_v2.tools.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import rescuecore2.misc.Pair;
import sos.base.SOSAgent;
import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.search_v2.tools.SearchRegion;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class CustomCluster {
	public static int KMEANS_ITERATIONS = 15;

	public static List<ClusterData> DoCluster(SOSAgent<? extends Human> sosagent, Collection<Building> buildings, int clusterCount) {
		return makeClustersWeka(sosagent, new ArrayList<Building>(buildings), clusterCount);
	}

	private static ArrayList<ClusterData> makeClustersWeka(SOSAgent<? extends Human> sosagent, List<Building> buildings, int clusterCount) {
		Dataset[] blocks = wekaKmeans(buildings, clusterCount);
		ArrayList<SearchRegion> regions = getRegions(blocks);
		ArrayList<ClusterData> clusters = new ArrayList<ClusterData>();
		for (SearchRegion searchRegion : regions) {
			ClusterData clusterData = new ClusterData(searchRegion.getId(), searchRegion.getBuildings(), sosagent, searchRegion.getId());
			clusters.add(clusterData);
		}
		return clusters;
	}

	private static ArrayList<SearchRegion> getRegions(Dataset[] blockSearchClusters) { //Salim
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
		if (neededClusters != 0)
			System.err.println("WHAT HAPPEN??????");
//			splitClusters(neededClusters, regions);
		return regions;
	}

	private static Dataset[] wekaKmeans(List<Building> buildings, int clusterCount) {
		if (clusterCount == 0)
			return null;
		Attribute at1 = new Attribute("x");
		Attribute at2 = new Attribute("y");
		FastVector fv = new FastVector(2);
		fv.addElement(at1);
		fv.addElement(at2);
		Instances instances = new Instances("Rel", fv, 2);

		Instance ins;
		for (Building b : buildings) {
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
			int numValidAgents = clusterCount;

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
				Building b = buildings.get(i);
				blocks[a].add(new SOSDenseInstance(new double[] { b.getX(), b.getY() }, b));
			}
			return blocks;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static Pair<Double, Double> getBuildingBestPoint(Building b) {
//		double cx = 0;
//		double cy = 0;
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

	public static Road getEnteranceRoad(Building b, HashSet<Building> checked) {
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

}
