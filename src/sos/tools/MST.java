package sos.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.worldGraph.GraphWeight;

/**
 * Run gets a Graph Object and Runs The MST (single source or multiSrc (with a SuperSource node)) !!!!!!!!!IMPORTANT!!!!!!!!!!!!!!!!!!!!!!!!!! you Can use A MST object multiple Times without initializing it ... But the First time or when the Graph is changed(number of nodes are changed) you shoould call setGraphSize()
 */
public class MST {
	
	private int parent[];
	private int minCost[];
	private int mark[];
	private int numberOfVertex, marker = 1;
	private SOSWorldModel model;
	
	public MST(int n, SOSWorldModel model) { // number of Graph Nodes
		setGraphSize(n);
		this.model = model;
	}
	
	public MST() {
	}
	
	/**
	 * Use This Method only if the Graph Size has Changed ... or its the First Time you need to Use this ...
	 */
	public void setGraphSize(int n) {
		parent = new int[n + 1];
		mark = new int[n + 1];
		minCost = new int[n + 1];
		this.numberOfVertex = n;
	}
	
	/**
	 * Run Single Src Dijkstra
	 */
	// TODO should improve this part ...
	public void Run(Graph graph, GraphWeight weight, int srcVertex) throws Exception {
		ArrayList<Integer> ar = new ArrayList<Integer>();
		ar.add(srcVertex);
		Run(graph, weight, ar);
	}
	
	/**
	 * Run multi Src Dijkstra
	 */
	public void Run(Graph graph, GraphWeight weight, ArrayList<Integer> srcVertexs) throws Exception {
		marker++;
		for (int i = 0; i < numberOfVertex; ++i) {
			minCost[i] = Integer.MAX_VALUE;
			parent[i] = -1;
		}
		PriorityQueue<Integer> PQ = new PriorityQueue<Integer>(100, new Cmp());
		for (Integer src : srcVertexs) {
			minCost[src] = 0;
			PQ.add(src);
		}
		while (PQ.size() != 0) {
			int node = (PQ.poll());
			if (mark[node] == marker)
				continue;
			else
				mark[node] = marker;
			for (Short ind : graph.getEdgesOf((short) node)) {
				GraphEdge e = model.graphEdges().get(ind);
				int childIndex = e.getNextNodeIndex((short) node);
				if (mark[childIndex] == marker)
					continue;
				int w = weight.getWeight(e.getIndex());
				if (w <= 0)
					throw new Exception();
				if (minCost[childIndex] > w) {
					minCost[childIndex] = w;
					parent[childIndex] = node;
					PQ.add(childIndex);
				}
			}
		}
	}
	/**
	 * Get Path From Des to Src ... both Src and Des are included in path
	 */
	public ArrayList<Pair<Integer, Integer>> getMST() {
		ArrayList<Pair<Integer, Integer>> ar = new ArrayList<Pair<Integer, Integer>>();
		for (int i = 0; i < numberOfVertex; ++i) {
			if (parent[i] != -1) {
				ar.add(new Pair<Integer, Integer>(i, parent[i]));
			}
		}
		return ar;
	}
	
	private class Cmp implements Comparator<Integer> {
		@Override
		public int compare(Integer a, Integer b) {
			if (minCost[a] > minCost[b])
				return 1;
			else if (minCost[a] == minCost[b])
				return 0;
			else
				return -1;
		}
	}
}
