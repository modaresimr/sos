package sos.tools;

public abstract class Graph {
	
	// AngehA2*************************************************************************************************************
	private short NumberOfNodes;
	
	public Graph(int n) {
		setNumberOfNodes((short) n);
	}
	
	// AngehA2*************************************************************************************************************
	public void setNumberOfNodes(short n) {
		NumberOfNodes = n;
	}
	
	// AngehA2*************************************************************************************************************
	public short getNumberOfNodes() {
		return NumberOfNodes;
	}
	
	// AngehA2*************************************************************************************************************
	/**
	 * return all edges that are being started from Node(srcIndex)
	 */
	public abstract short[] getEdgesOf(short srcIndex);
	
	// AngehA2*************************************************************************************************************
	/**
	 * returning weight of edge starting from "u" , and ending with "v" 0,1 if the Graph is not weighted ...
	 */
	public abstract double weight(int u, int v);
	
	// ARAMIK*************************************************************************************************************
	/**
	 * returning Edge Index between u and v nodes
	 */
	public abstract short edgeIndexBetween(int u, int v);
	
}
