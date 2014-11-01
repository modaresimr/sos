package sos.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import sos.base.SOSConstant.GraphEdgeState;
import sos.base.SOSWorldModel;
import sos.base.move.MoveConstants;
import sos.police_v2.base.PoliceGraph;

/**
 * Run gets a Graph Object and Runs The Dijkstra From single and Multiple sources .. with methods getPathArray() & getCost You can Get the Results of The Dijkstra !!!!!!!!!IMPORTANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! you Can use the Dijkstra object multiple Times without initializing it ... But the First time or when the Graph is changed(number of nodes are changed) you shoould call setGraphSize()
 */
public class Dijkstra {

	private int parent[];
	private long minCost[];
	private int mark[];
	private int numberOfVertex, marker = 1;
	private SOSWorldModel model;

	public Dijkstra(int n, SOSWorldModel model) { // number of Graph Nodes
		setGraphSize(n);
		this.model = model;
	}

	public Dijkstra() {
	}

	/**
	 * Use This Method only if the Graph Size has Changed ... or its the First Time you need to Use this ...
	 */
	public void setGraphSize(int n) {
		parent = new int[n + 1];
		mark = new int[n + 1];
		minCost = new long[n + 1];
		this.numberOfVertex = n;
	}

	/**
	 * Run Single Src Dijkstra
	 */
	// TODO should improve this part ...
	public void Run(Graph graph, GraphWeightInterface weight, int srcVertex) throws Exception {
		ArrayList<Integer> ar = new ArrayList<Integer>();
		ar.add(srcVertex);
		Run(graph, weight, ar);
	}

	/**
	 * Run multi Src Dijkstra
	 */
	public void Run(Graph graph, GraphWeightInterface weight, ArrayList<Integer> srcVertexs) throws Exception {
		marker++;
		for (int i = 0; i < numberOfVertex; ++i) {
			minCost[i] = Long.MAX_VALUE / 2;
			parent[i] = -2;
		}
		PriorityQueue<Integer> PQ = new PriorityQueue<Integer>(100, new Cmp());
		for (Integer src : srcVertexs) {
			parent[src] = -1;
			minCost[src] = 0;
			PQ.add(src);
		}
		while (PQ.size() != 0) {
			int node = (PQ.poll());
			if (mark[node] == marker)
				continue;
			else
				mark[node] = marker;
			for (Short indx : graph.getEdgesOf((short) node)) {
				GraphEdge e = model.graphEdges().get(indx);
				int childIndex = e.getNextNodeIndex((short) node);
				if (mark[childIndex] == marker)
					continue;
				int w = weight.getWeight(e.getIndex());
				if (w <= 0 || minCost[node] + w < 0)
					throw new Exception("Negetive Cost");
				if (w >= MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING) {
					w = w +100- getLength(node);
				}
				if (minCost[childIndex] > minCost[node] + w) {
					minCost[childIndex] = minCost[node] + w;
					parent[childIndex] = node;
					PQ.add(childIndex);
				}
			}
		}
	}

	private int getLength(int desVertex) {
		if(parent[desVertex] == -2){
			System.out.println("why?"+ new Error().getStackTrace());
			return 10000;
		}
		if (parent[desVertex] == -1)
			return 1;

		return getLength(parent[desVertex]) + 1;
	}

	/**
	 * Added by aramik
	 */
	public void Run(Graph graph, GraphWeightInterface weight, ArrayList<Integer> srcVertexs, int[] srcCosts) throws Exception {
		marker++;
		for (int i = 0; i < numberOfVertex; ++i) {
			minCost[i] = Long.MAX_VALUE / 2;
		}
		PriorityQueue<Integer> PQ = new PriorityQueue<Integer>(100, new Cmp());
		for (int i = 0; i < srcVertexs.size(); ++i) {
			int src = srcVertexs.get(i);
			parent[src] = -1;
			minCost[src] = srcCosts[i];
			PQ.add(src);
		}
		while (PQ.size() != 0) {
			int node = (PQ.poll());
			if (mark[node] == marker)
				continue;
			else
				mark[node] = marker;
			for (Short indx : graph.getEdgesOf((short) node)) {
				GraphEdge e = model.graphEdges().get(indx);
				int childIndex = e.getNextNodeIndex((short) node);
				if (mark[childIndex] == marker)
					continue;
				int w = weight.getWeight(e.getIndex());
				if (w <= 0 || minCost[node] + w < 0)
					throw new Exception("Negetive Cost");
				if (minCost[childIndex] > minCost[node] + w) {
					minCost[childIndex] = minCost[node] + w;
					parent[childIndex] = node;
					PQ.add(childIndex);
				}
			}
		}
	}

	/**
	 * Run multi Src Dijkstra
	 *
	 * @author navid-it & Ali
	 */
	public void Run(PoliceGraph graph, List<Integer> srcVertexs, long[] firstCost) {
		marker++;
		for (int i = 0; i < numberOfVertex; ++i) {
			minCost[i] = Integer.MAX_VALUE;
		}
		PriorityQueue<Integer> PQ = new PriorityQueue<Integer>(100, new Cmp());
		for (Integer src : srcVertexs) {
			parent[src] = -1;
			minCost[src] = firstCost[src];
			PQ.add(src);
		}
		while (PQ.size() != 0) {
			int node = (PQ.poll());
			if (mark[node] == marker)
				continue;
			else
				mark[node] = marker;

			for (Short childIndex : graph.getConnectedNodesOf((short) node)) {
				if (mark[childIndex] == marker)
					continue;
				int w = (int) graph.weight(node, childIndex);

				if (w < 0) {
					System.err.println("-weight is negative in police dijkstra");
					w = 0;
				}
				if (minCost[childIndex] >= minCost[node] + w) {
					minCost[childIndex] = minCost[node] + w;
					parent[childIndex] = node;
					PQ.add((int) childIndex);
				}
			}
		}
	}

	/**
	 * Added by aramik
	 */
	public void RunForLenght(Graph graph, ArrayList<Integer> srcVertexs, ArrayList<Integer> srcCosts) throws Exception {
		marker++;
		for (int i = 0; i < numberOfVertex; ++i) {
			minCost[i] = Long.MAX_VALUE / 2;
		}
		PriorityQueue<Integer> PQ = new PriorityQueue<Integer>(100, new Cmp());
		for (int i = 0; i < srcVertexs.size(); ++i) {
			int src = srcVertexs.get(i);
			parent[src] = -1;
			minCost[src] = srcCosts.get(i);
			PQ.add(src);
		}
		while (PQ.size() != 0) {
			int node = (PQ.poll());
			if (mark[node] == marker)
				continue;
			else
				mark[node] = marker;
			for (Short indx : graph.getEdgesOf((short) node)) {
				GraphEdge e = model.graphEdges().get(indx);
				int childIndex = e.getNextNodeIndex((short) node);
				if (mark[childIndex] == marker)
					continue;
				int w = (e.getLenght() < MoveConstants.DIVISION_UNIT ? 1 : e.getLenght() / MoveConstants.DIVISION_UNIT);
				w = e.getState() == GraphEdgeState.Block ? MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING : w;
				if (w <= 0 || minCost[node] + w < 0)
					throw new Exception("Negetive Cost");
				if (minCost[childIndex] > minCost[node] + w) {
					minCost[childIndex] = minCost[node] + w;
					parent[childIndex] = node;
					PQ.add(childIndex);
				}
			}
		}
	}

	/**
	 * Get Path From Des to Src ... both Src and Des are included in path
	 */
	public ArrayList<Integer> getpathArray(int desVertex) {
		ArrayList<Integer> ar = new ArrayList<Integer>();
		if (parent[desVertex] != -1) {
			ar = getpathArray(parent[desVertex]);
		}
		ar.add(desVertex);
		return ar;
	}

	/**
	 * Get Cost From Des to Src ...
	 */
	public long getWeight(int desVertex) {
		if (minCost[desVertex] < 0)
			new Error("Cost is negetive....").printStackTrace();
		return minCost[desVertex];
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
