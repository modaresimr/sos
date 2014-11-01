package sos.police_v2.base;

import sos.tools.Graph;

public abstract class PoliceGraph extends Graph {

	public PoliceGraph(int n) {
		super(n);
	}

	public abstract short[] getConnectedNodesOf(short srcIndex);
	
	@Override
	public abstract double weight(int source, int destination) ;
}
