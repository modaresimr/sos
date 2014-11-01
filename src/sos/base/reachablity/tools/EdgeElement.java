package sos.base.reachablity.tools;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Edge;

public class EdgeElement {
	private Edge edge;// Morteza2011
	private Point2D start;// Morteza2011
	private Point2D end;// Morteza2011
	
	// Morteza2011*****************************************************************
	
	public EdgeElement(Edge edge, Point2D start, Point2D end) {
		this.edge = edge;
		this.start = start;
		this.end = end;
	}
	
	// Morteza2011*****************************************************************
	
	public Edge getEdge() {
		return edge;
	}
	
	// Morteza2011*****************************************************************
	
	public Point2D getStart() {
		return start;
	}
	
	// Morteza2011*****************************************************************
	
	public Point2D getEnd() {
		return end;
	}
	
	// Morteza2011*****************************************************************
	
	@Override
	public String toString() {
		return "EdgeElement: from: " + start + " to: " + end + " by Edge: " + edge;
	}

}
