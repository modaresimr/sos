/**
 * 
 */
package sos.base.move;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.entities.Area;
import sos.base.worldGraph.Node;
import sos.tools.GraphEdge;

/**
 * @author Aramik
 * 
 */
public class Path {
	private GraphEdge[] edges;
	private Node[] nodes;
	private ArrayList<EntityID> ids;
	private int lenght;
	private Pair<? extends Area, Point2D> source;
	private Pair<? extends Area, Point2D> destination;
	private boolean isPathSafe;
	private int cost;
	
	public Path(GraphEdge[] edges, Node[] nodes, ArrayList<EntityID> ids, Pair<? extends Area, Point2D> source, Pair<? extends Area, Point2D> destination, boolean safety) {
		this.edges = edges;
		this.nodes = nodes;
		this.ids = ids;
		this.source = source;
		this.destination = destination;
		this.isPathSafe=safety;
		calculateLenght();
	}
	
	private void calculateLenght() {
		this.lenght = 0;
		if (edges != null) {
			for (GraphEdge ed : edges) {
				this.lenght += ed.getLenght();
				if (isPathSafe && ed.getState() == GraphEdgeState.Block)
					isPathSafe = false;
			}
		}
		if (nodes == null)
			this.lenght += distance((int) source.second().getX(), (int) source.second().getY(), (int) destination.second().getX(), (int) destination.second().getY());
		else {
			this.lenght += distance((int) source.second().getX(), (int) source.second().getY(), (int) nodes[0].getPosition().getX(), (int) nodes[0].getPosition().getY());
			this.lenght += distance((int) destination.second().getX(), (int) destination.second().getY(), (int) nodes[nodes.length - 1].getPosition().getX(), (int) nodes[nodes.length - 1].getPosition().getY());
		}
		if(this.lenght<0){
			System.err.println("path "+this);
			new Error("Overflow...").printStackTrace();
		}
	}
	public boolean isPathSafe() {
		return isPathSafe;
	}
	private int distance(int x1, int y1, int x2, int y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return (int) Math.hypot(dx, dy);
	}
	
	public Node[] getNodes() {
		return nodes;
	}
	
	public Pair<? extends Area, Point2D> getDestination() {
		return destination;
	}
	
	public GraphEdge[] getEdges() {
		return edges;
	}
	
	public Pair<? extends Area, Point2D> getSource() {
		return source;
	}
	
	public ArrayList<EntityID> getIds() {
		return ids;
	}
	
	public int getLenght() {
		return lenght;
	}
	
	public void setDestination(Pair<? extends Area, Point2D> destination) {
		this.destination = destination;
	}
	
	@Override
	public String toString() {
		return "[Path SOURCE=" + source + " DESTINATION=" + destination + " NODES=" + nodes + " EDGES=" + edges + " IDS=" + ids + " LENGHT=" + lenght;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getCost() {
		return cost;
	}
	
}
