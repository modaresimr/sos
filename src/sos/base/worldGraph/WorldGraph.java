package sos.base.worldGraph;

import java.util.ArrayList;
import java.util.Vector;

import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.tools.Graph;
import sos.tools.GraphEdge;

/**
 * 
 * @author Aramik
 * 
 */
public class WorldGraph extends Graph {
	private short[][] graph;
	private GraphWeight gw;
	private ArrayList<GraphEdge> edges;
	private SOSLoggerSystem log;
	
	public WorldGraph(int n, ArrayList<GraphEdge> edges, GraphWeight weights) {
		this(n, edges);
		gw = weights;
	}
	
	public WorldGraph(int n, ArrayList<GraphEdge> edges) {
		super(n);
		graph = new short[n][];
		this.edges = edges;
		log = new SOSLoggerSystem(null, "WorldGraph", true, OutputType.File);
		addEdges(edges);
	}
	
	private void addEdges(ArrayList<GraphEdge> edges) {
		log.logln("\n--------- Enter --> addEdges(ArrayList<GraphEdge> edges) ---------");
		@SuppressWarnings("unchecked")
		Vector<Short>[] adjs = new Vector[getNumberOfNodes()];
		for (int i = 0; i < getNumberOfNodes(); i++)
			adjs[i] = new Vector<Short>();
		for (GraphEdge ge : edges) {
			adjs[ge.getHeadIndex()].add(ge.getIndex());
			adjs[ge.getTailIndex()].add(ge.getIndex());
		}
		for (int i = 0; i < getNumberOfNodes(); i++) {
			graph[i] = new short[adjs[i].size()];
			int index = 0;
			for (Short r : adjs[i]) {
				graph[i][index++] = r;
			}
		}
		// ---------START DEBUGGING SECTION
		log.logln("WorldGraph of lenght=" + getNumberOfNodes() + "-->");
		for (int i = 0; i < graph.length; i++) {
			log.log(i + "\t-->");
			for (int k = 0; k < graph[i].length; k++) {
				log.log(graph[i][k] + "\t");
			}
			log.logln("");
		}
		// ---------END OF DEBUGGING SECTION
	}
	
	public void setWeights(GraphWeight weights) {
		this.gw = weights;
	}
	
	public GraphWeight getWeighs() {
		return gw;
	}
	
	@Override
	public short[] getEdgesOf(short srcIndex) {
		return graph[srcIndex];
	}
	
	@Override
	public double weight(int u, int v) {
		short edgeIndex = edgeIndexBetween(u, v);
		if (edgeIndex >= 0)
			return gw.getWeight(edgeIndex);
		log.error("weight(int u, int v) in WORLDGRAPH did not found an edge between " + u + " and " + v + "\n");
		return 0;
	}
	
	@Override
	public short edgeIndexBetween(int u, int v) {
		int min, next;
		if (graph[u].length > graph[v].length) {
			min = v;
			next = u;
		} else {
			min = u;
			next = v;
		}
		for (short edgeIndex : graph[min]) {
			GraphEdge ge = edges.get(edgeIndex);
			if ((ge.getHeadIndex() == next && ge.getTailIndex() == min) || (ge.getHeadIndex() == min && ge.getTailIndex() == next))
				return edgeIndex;
		}
		log.error("edgeIndexBetween(int u, int v) in WORLDGRAPH did not found an edge between " + u + " and " + v + "\n");
		return -1;
	}
	
	public int getEdgesSize() {
		return edges.size();
	}
	
}
