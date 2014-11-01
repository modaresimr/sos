package sos.base.worldGraph;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Edge;

/**
 * 
 * @author Aramik
 * 
 */
public class Node {
	private short index;
	private short areaIndex;
	private Edge relatedEdge;
	
	public Node(short index, short areaIndex, Edge relatedEdge) {
		this.index = index;
		this.areaIndex = areaIndex;
		this.relatedEdge = relatedEdge;
	}
	
	public short getIndex() {
		return index;
	}
	
	public void setIndex(short index) {
		this.index = index;
	}
	
	public short getAreaIndex() {
		return areaIndex;
	}
	
	public void setAreaIndex(short areaIndex) {
		this.areaIndex = areaIndex;
	}
	
	public Edge getRelatedEdge() {
		return relatedEdge;
	}
	
	public void setRelatedEdge(Edge relatedEdge) {
		this.relatedEdge = relatedEdge;
	}
	
	public Point2D getPosition() {
		return relatedEdge.getMidPoint();
	}
	
	public String fullDescription() {
		return "[Node index=" + index + " , areaIndex=" + areaIndex + " , relatedEdge=(" + relatedEdge.fullDescription() + ")]";
	}
	
	@Override
	public String toString() {
		return "[Node index=" + index + "]";
	}
}
