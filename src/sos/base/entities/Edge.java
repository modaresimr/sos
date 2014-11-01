package sos.base.entities;

import java.awt.Shape;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.worldmodel.EntityID;

/**
 * An edge is a line segment with an optional neighbouring entity. Edges without neighbours are impassable, edges with neighbours are passable.
 */
public class Edge implements ShapeableObject {
	/* ///////////////////S.O.S instants////////////////// */
	private short myAreaIndex = -1;
	private byte passabilityEdgeIndex = -1;
	private Edge passabilityTwin = null;
	private short nodeIndex = -1;
	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**/private Line2D line;
	/**/private int neighbour = -1;

	/**
	 * Constuct an impassable Edge.
	 *
	 * @param startX
	 *            The X coordinate of the first endpoint.
	 * @param startY
	 *            The Y coordinate of the first endpoint.
	 * @param endX
	 *            The X coordinate of the second endpoint.
	 * @param endY
	 *            The Y coordinate of the second endpoint.
	 */
	public Edge(int startX, int startY, int endX, int endY) {
		this(new Point2D(startX, startY), new Point2D(endX, endY), null);
	}

	/**
	 * Constuct an impassable Edge.
	 *
	 * @param start
	 *            The first endpoint coordinates.
	 * @param end
	 *            The second endpoint coordinates.
	 */
	public Edge(Point2D start, Point2D end) {
		this(start, end, null);
	}

	/**
	 * Constuct an Edge. If the neighbour is null then this edge is impassable; if it is non-null then this edge is passable.
	 *
	 * @param startX
	 *            The X coordinate of the first endpoint.
	 * @param startY
	 *            The Y coordinate of the first endpoint.
	 * @param endX
	 *            The X coordinate of the second endpoint.
	 * @param endY
	 *            The Y coordinate of the second endpoint.
	 * @param neighbour
	 *            The ID of the neighbour on the other side of this edge. This may be null to indicate an impassable edge.
	 */
	public Edge(int startX, int startY, int endX, int endY, EntityID neighbour) {
		this(new Point2D(startX, startY), new Point2D(endX, endY), neighbour);
	}

	/**
	 * Constuct an Edge. If the neighbour is null then this edge is impassable; if it is non-null then this edge is passable.
	 *
	 * @param start
	 *            The first endpoint coordinates.
	 * @param end
	 *            The second endpoint coordinates.
	 * @param neighbour
	 *            The ID of the neighbour on the other side of this edge. This may be null to indicate an impassable edge.
	 */
	public Edge(Point2D start, Point2D end, EntityID neighbour) {
		line = new Line2D(start, end);
		if (neighbour != null)
			this.neighbour = neighbour.getValue();
	}

	/**
	 * Get the X coordinate of the first endpoint.
	 *
	 * @return The X coordinate of the first endpoint.
	 */
	public int getStartX() {
		//		return (int) start.getX();
		return (int) line.getOrigin().getX();
	}

	/**
	 * Get the Y coordinate of the first endpoint.
	 *
	 * @return The Y coordinate of the first endpoint.
	 */
	public int getStartY() {
		//		return (int) start.getY();
		return (int) line.getOrigin().getY();
	}

	/**
	 * Get the X coordinate of the second endpoint.
	 *
	 * @return The X coordinate of the second endpoint.
	 */
	public int getEndX() {
		//		return (int) end.getX();
		return (int) line.getEndPoint().getX();
	}

	/**
	 * Get the Y coordinate of the second endpoint.
	 *
	 * @return The Y coordinate of the second endpoint.
	 */
	public int getEndY() {
		//		return (int) end.getY();
		return (int) line.getEndPoint().getY();
	}

	/**
	 * Get the start point.
	 *
	 * @return The start point.
	 */
	public Point2D getStart() {
		//		return start;
		return line.getOrigin();

	}

	/**
	 * Get the end point.
	 *
	 * @return The end point.
	 */
	public Point2D getEnd() {
		return line.getEndPoint();
	}

	/**
	 * Get the ID of the neighbour.
	 *
	 * @return The ID of the neighbour or null if this edge is impassable.
	 */
	public EntityID getNeighbour() {
		return new EntityID(neighbour);
	}

	/**
	 * Find out if this edge is passable or not.
	 *
	 * @return True iff the neighbor is non-null.
	 */
	public boolean isPassable() {
		return neighbour != -1;
	}

	/**
	 * Get a line representing this edge.
	 *
	 * @return A Line2D representing this edge.
	 */
	public Line2D getLine() {
		return line;
	}

	@Override
	public String toString() {
		return "Edge from " + getStart() + " to " + line.getEndPoint() + " (" + (neighbour == -1 ? "impassable" : "neighbour: " + neighbour) + ")";
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public String fullDescription() {
		return "[Edge AreaIndex=" + myAreaIndex + " , passabilityIndex=" + passabilityEdgeIndex + " , neighbor=" + neighbour + " , twin=" + passabilityTwin + "]";
	}

	// ############################{{ REACHABLITY }}#######################################
	private short ReachablityIndex = 0; // Morteza2011

	// Morteza2011*****************************************************************
	public Point2D getMidPoint() {
		return new Point2D((getStart().getX() + getEndX()) / 2, (getStart().getY() + getEndY()) / 2);
	}

	// Morteza2011*****************************************************************
	public boolean edgeEquals(Edge edge) {
		return (edge.getStart().equals(getStart()) && edge.getEnd().equals(getEnd())) || (edge.getStart().equals(getEnd()) && edge.getEnd().equals(getStart()));
	}

	// Morteza2011*****************************************************************
	public void setEnd(Point2D end) {
		line.setEnd(end);
	}

	// Morteza2011*****************************************************************
	public void setReachablityIndex(short reachablityIndex) {
		this.ReachablityIndex = reachablityIndex;
	}

	// Morteza2011*****************************************************************
	public short getReachablityIndex() {
		return ReachablityIndex;
	}

	// Morteza2012*****************************************************************
	public double length() {
		return GeometryTools2D.getDistance(getStart(), getEnd());
	}

	/** Nima2010 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof Edge) {
			Edge edge = (Edge) obj;
			if (edge.getStart().equals(getStart()) && edge.getEnd().equals(getEnd()))
				return true;
			if (edge.getStart().equals(getEnd()) && edge.getEnd().equals(getStart()))
				return true;
		}
		return false;

	}

	// ############################{{ END OF REACHABLITY }}##########################
	public void setMyAreaIndex(short myAreaIndex) {
		this.myAreaIndex = myAreaIndex;
	}

	public short getMyAreaIndex() {
		return myAreaIndex;
	}

	public void setPassabilityEdgeIndex(byte passabilityEdgeIndex) {
		this.passabilityEdgeIndex = passabilityEdgeIndex;
	}

	public byte getPassabilityEdgeIndex() {
		return passabilityEdgeIndex;
	}

	public void setPassabilityTwin(Edge passabilityTwin) {
		this.passabilityTwin = passabilityTwin;
	}

	public Edge getTwin() {
		return this.passabilityTwin;
	}

	public short getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(short nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	// ******************************Map Verifier************************************
	public void setNeighbour(EntityID neighbour) {
		if (neighbour != null)
			this.neighbour = neighbour.getValue();
		else
			this.neighbour = -1;
	}

	public void switchHeadAndTail() {
		line.switchHeadAndTail();
	}

	public void setStart(Point2D p) {
		line.setOrigin(p);
	}

	// ******************************End Map Verifier*************************

	@Override
	public Shape getShape() {
		java.awt.geom.Line2D shape = new java.awt.geom.Line2D.Double(line.getOrigin().toGeomPoint(), line.getEndPoint().toGeomPoint());
		return shape;
	}
	/* ////////////////////End of S.O.S/////////////////// */

}