package sos.base.reachablity.tools;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSConstant;

public class RoadGraph {
	private ArrayList<ArrayList<Pair<Short, Short>>> graph;// Morteza 2011
	private ArrayList<Point2D> points;// Morteza 2011

	// Morteza2011*****************************************************************
	public RoadGraph(ArrayList<ArrayList<Pair<Short, Short>>> graph, ArrayList<Point2D> points) {
		this.graph = graph;
		this.points = points;
	}

	// Morteza2011*****************************************************************
	public ArrayList<Point2D> getPoints() {
		return points;
	}

	// Morteza2011*****************************************************************
	public ArrayList<ArrayList<Pair<Short, Short>>> getGraph() {
		return graph;
	}

	// Morteza2012*****************************************************************
	@Override
	public String toString() {
		String s = "RoadGraph:";
		if(!SOSConstant.CREATE_BASE_LOGS)
			return s;
		for (ArrayList<Pair<Short, Short>> a : graph) {
			s += a + "\n";
		}
		for (Point2D p : points) {
			s += p + "\n";
		}
		return s;
	}
}