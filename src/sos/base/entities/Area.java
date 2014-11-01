package sos.base.entities;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.EdgeListProperty;
import rescuecore2.standard.entities.StandardPropertyURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.EntityListener;
import rescuecore2.worldmodel.Property;
import rescuecore2.worldmodel.properties.EntityRefListProperty;
import rescuecore2.worldmodel.properties.IntProperty;
import sos.base.util.SOSGeometryTools;
import sos.base.util.geom.ShapeInArea;
import sos.tools.map.MapVerifier;

/**
 * The Area object.
 */
public abstract class Area extends StandardEntity implements ShapeableObject {
	/* ///////////////////S.O.S instants////////////////// */
	/** JUST USE FOR POLICE */
	protected short areaIndex;// aramik
	public boolean correctApexList = true; // salim
	protected Edge[] passableEdges = null;// aramik
	protected int[] distanceOfpassableEdgesFromAreaCenter = null;// aramik
	protected short[] graphEdges = null; // Aramik including WorldGraphEdges and VirtualGraphEdges
	protected short worldgraphEdgesSize = -1;
	private double groundArea = -1;//ALI
	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**/private IntProperty x;
	/**/private IntProperty y;
	/**/private EdgeListProperty edges;
	/**/private EntityRefListProperty blockadesID;
	/**/private List<Blockade> blockades;
	/**/private Shape shape;
	/**/private int[] apexList;
	/**/private List<EntityID> neighboursID;
	/**/private List<Area> neighbours;
	private int[] passableEdgeNodeIndexes = null;

	// DON'T ADD ANY instant or method HEAR!!!!!!
	/**
	 * Construct a subclass of Area with entirely undefined property values.
	 *
	 * @param id
	 *            The ID of this entity.
	 */
	protected Area(EntityID id) {
		super(id);
		x = new IntProperty(StandardPropertyURN.X);
		y = new IntProperty(StandardPropertyURN.Y);
		edges = new EdgeListProperty(StandardPropertyURN.EDGES);
		blockadesID = new EntityRefListProperty(StandardPropertyURN.BLOCKADES);
		registerProperties(x, y, edges, blockadesID);
		shape = null;
		apexList = null;
		neighboursID = null;
		addEntityListener(new EdgesListener());
		addEntityListener(new BlockadeListener());
	}

	// Please don't add any method here!!!!!!
	/**
	 * Area copy constructor.
	 *
	 * @param other
	 *            The Area to copy.
	 */
	protected Area(Area other) {
		super(other);
		x = new IntProperty(other.x);
		y = new IntProperty(other.y);
		edges = new EdgeListProperty(other.edges);
		blockadesID = new EntityRefListProperty(other.blockadesID);
		registerProperties(x, y, edges, blockadesID);
		shape = null;
		apexList = null;
		neighboursID = null;
		addEntityListener(new EdgesListener());
	}

	// Please don't add any method here!!!!!!
	@Override
	public Pair<Integer, Integer> getLocation() {
		return new Pair<Integer, Integer>(x.getValue(), y.getValue());
	}

	// Please don't add any method here!!!!!!
	@Override
	public Property getProperty(String urn) {
		StandardPropertyURN type;
		try {
			type = StandardPropertyURN.fromString(urn);
		} catch (IllegalArgumentException e) {
			return super.getProperty(urn);
		}
		switch (type) {
		case X:
			return x;
		case Y:
			return y;
		case EDGES:
			return edges;
		case BLOCKADES:
			return blockadesID;
		default:
			return super.getProperty(urn);
		}

	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the X property.
	 *
	 * @return The X property.
	 */
	public IntProperty getXProperty() {
		return x;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the X coordinate.
	 *
	 * @return The X coordinate.
	 */
	public int getX() {
		return x.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the X coordinate.
	 *
	 * @param x
	 *            The new X coordinate.
	 */
	public void setX(int x) {
		this.x.setValue(x);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the X property has been defined.
	 *
	 * @return True if the X property has been defined, false otherwise.
	 */
	public boolean isXDefined() {
		return x.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the X property.
	 */
	public void undefineX() {
		x.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the Y property.
	 *
	 * @return The Y property.
	 */
	public IntProperty getYProperty() {
		return y;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the Y coordinate.
	 *
	 * @return The Y coordinate.
	 */
	public int getY() {
		return y.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the Y coordinate.
	 *
	 * @param y
	 *            The new y coordinate.
	 */
	public void setY(int y) {
		this.y.setValue(y);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the Y property has been defined.
	 *
	 * @return True if the Y property has been defined, false otherwise.
	 */
	public boolean isYDefined() {
		return y.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the Y property.
	 */
	public void undefineY() {
		y.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the edges property.
	 *
	 * @return The edges property.
	 */
	public EdgeListProperty getEdgesProperty() {
		return edges;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the edges of this area.
	 *
	 * @return The edges.
	 */
	public List<Edge> getEdges() {
		return edges.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the edges.
	 *
	 * @param edges
	 *            The new edges.
	 */
	public void setEdges(List<Edge> edges) {
		this.edges.setEdges(edges);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the edges property has been defined.
	 *
	 * @return True if the edges property has been defined, false otherwise.
	 */
	public boolean isEdgesDefined() {
		return edges.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the edges property.
	 */
	public void undefineEdges() {
		edges.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the blockades property.
	 *
	 * @return The blockades property.
	 */
	public EntityRefListProperty getBlockadesProperty() {
		return blockadesID;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the blockades in this area.
	 *
	 * @return The blockades.
	 */
	public List<EntityID> getBlockadesID() {
		return blockadesID.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the blockades in this area.
	 *
	 * @return The blockades.
	 */
	public List<Blockade> getBlockades() {
		if (isBlockadesDefined() && blockades == null) {
			blockades = new ArrayList<Blockade>();
			for (EntityID entityID : getBlockadesID()) {
				blockades.add((Blockade) standardModel().getEntity(entityID));

			}
		}

		return blockades;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the blockades in this area.
	 *
	 * @param blockades
	 *            The new blockades.
	 */
	public void setBlockades(List<EntityID> blockades) {
		this.blockadesID.setValue(blockades);
		blockades = null;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the blockades property has been defined.
	 *
	 * @return True if the blockades property has been defined, false otherwise.
	 */
	public boolean isBlockadesDefined() {
		return blockadesID.isDefined();
	}

//	// Please don't add any method here!!!!!!
//	/**
//	 * Undefine the blockades property.
//	 */
//	public void undefineBlockades() {
//		blockadesID.undefine();
//	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the neighbours of this area.
	 *
	 * @return The neighbours.
	 */
	public List<EntityID> getNeighboursID() {
		if (neighboursID == null) {
			neighboursID = new ArrayList<EntityID>();
			for (Edge next : edges.getValue()) {
				if (next.isPassable()) {
					neighboursID.add(next.getNeighbour());
				}
			}
		}
		return neighboursID;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the neighbours of this area.
	 *
	 * @return The neighbours.
	 */
	public List<Area> getNeighbours() {
		if (neighbours == null) {
			neighbours = new ArrayList<Area>();
			for (Edge next : edges.getValue()) {
				if (next.isPassable()) {
					neighbours.add((Area) standardModel().getEntity(next.getNeighbour()));
				}
			}
		}
		return neighbours;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the edge that crosses to a particular neighbour.
	 *
	 * @param neighbour
	 *            The neighbour ID.
	 * @return The edge that crosses to the given neighbour, or null if no edges border that neighbour.
	 */
	public Edge getEdgeTo(EntityID neighbour) {
		for (Edge next : getEdges()) {
			if (next.getNeighbour() != null)
				if (neighbour.equals(next.getNeighbour())) {
					return next;
				}
		}
		return null;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the edge that crosses to a particular neighbour.
	 *
	 * @param neighbour
	 *            The neighbour ID.
	 * @return The edge that crosses to the given neighbour, or null if no edges border that neighbour.
	 */
	public Edge getEdgeTo(Area neighbour) {
		for (Edge next : getEdges()) {
			if (neighbour.getID().equals(next.getNeighbour())) {
				return next;
			}
		}
		return null;
	}
	public ArrayList<Edge> getEdgesTo(Area neighbour) {
		ArrayList<Edge> edges=new ArrayList<Edge>();
		for (Edge next : getEdges()) {
			if (neighbour.getID().equals(next.getNeighbour())) {
				edges.add(next);
			}
		}
		return edges;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the list of apexes for this area.
	 *
	 * @return The list of apexes.
	 */
	public int[] getApexList() {
		if (apexList == null) {
			List<Edge> e = getEdges();
			apexList = new int[e.size() * 2];
			int i = 0;
			for (Edge next : e) {
				apexList[i++] = next.getStartX();
				apexList[i++] = next.getStartY();
			}
		}
		return apexList;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get this area as a Java Shape object.
	 *
	 * @return A Shape describing this area.
	 */
	@Override
	public Shape getShape() {
		if (shape == null) {
			shape=new ShapeInArea(getApexList(), this);
		}
		return shape;
	}

	// Please don't add any method here!!!!!!
	private class EdgesListener implements EntityListener {
		@Override
		public void propertyChanged(Entity e, Property p, Object oldValue, Object newValue) {
			if (p == edges) {
				shape = null;
				apexList = null;
				neighboursID = null;
			}
		}
	}

	private class BlockadeListener implements EntityListener {
		@Override
		public void propertyChanged(Entity e, Property p, Object oldValue, Object newValue) {
			if (p == blockadesID) {
				blockades=null;
			}
		}
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "Area[" + getID().getValue() + "]";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
//		String a = " | neighbors=";
//		String apex = " | apex=";
//		String edges = " | edges=";
//		if (isEdgesDefined())
//			for (EntityID id : getNeighboursID()) {
//				a = a.concat(id.getValue() + " : ");
//			}
//		else a+="-";
//		if (isEdgesDefined())
//			for (Integer id : getApexList())
//				apex = apex.concat(id + " : ");
//		if (isEdgesDefined())
//			for (Edge ed : getEdges())
//				edges = edges.concat(ed + " : ");
//		return "Area[" + getID().getValue() + "] ,x=" + (isXDefined() ? getX() : "-") + " , y=" + (isYDefined() ? getY() : "-") + a + apex + edges;
		return "Area[" + getID().getValue() + "]";
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */

	public void setAreaIndex(short areaIndex) {
		this.areaIndex = areaIndex;
	}

	public short getAreaIndex() {
		return areaIndex;
	}

	public Edge[] getPassableEdges() {
		return passableEdges;
	}

	public int[] getPassableEdgeNodeIndexes() {
		if (passableEdgeNodeIndexes == null) {
			passableEdgeNodeIndexes = new int[getPassableEdges().length];
			for (int i = 0; i < getPassableEdges().length; i++) {
				passableEdgeNodeIndexes[i] = getPassableEdges()[i].getNodeIndex();
			}
		}
		return passableEdgeNodeIndexes;
	}

	public int[] getDistanceOfpassableEdgesFromAreaCenter() {
		return distanceOfpassableEdgesFromAreaCenter;
	}

	public short[] getGraphEdges() {
		return graphEdges;
	}

	public int getWorldGraphEdgesSize() {
		return this.worldgraphEdgesSize;
	}

	public void setWorldgraphEdgesSize(short worldgraphEdgesSize) {
		this.worldgraphEdgesSize = worldgraphEdgesSize;
	}

	public void setDistanceOfpassableEdgesFromAreaCenter(int[] distanceOfpassableEdgesFromAreaCenter) {
		this.distanceOfpassableEdgesFromAreaCenter = distanceOfpassableEdgesFromAreaCenter;
	}

	public void setGraphEdges(short[] graphEdges) {
		this.graphEdges = graphEdges;
	}

	public void setPassableEdges(Edge[] passableEdges) {
		this.passableEdges = passableEdges;
	}

	// ******************************Map Verifier************************************
	public void setNewApexList(ArrayList<Integer> apexList) {
		if (apexList.size() < 4)
			throw new Error("ApexeList is too small");
		this.apexList = new int[apexList.size()];
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (int i = 0; i < apexList.size() - 2; i += 2) {
			edges.add(new Edge(new Point2D(apexList.get(i), apexList.get(i + 1)), new Point2D(apexList.get(i + 1), apexList.get(i + 2))));
			this.apexList[i] = apexList.get(i);
			this.apexList[i + 1] = apexList.get(i + 1);
		}
		this.apexList[apexList.size() - 2] = apexList.get(apexList.size() - 2);
		this.apexList[apexList.size() - 1] = apexList.get(apexList.size() - 1);
		this.edges = new EdgeListProperty(StandardPropertyURN.APEXES);
		edges.add(new Edge(new Point2D(apexList.get(apexList.size() - 2), apexList.get(apexList.size() - 1)), new Point2D(apexList.get(0), apexList.get(1))));
		neighbours = null;
		neighboursID = null;
	}

	public void setNewEdges(List<Edge> newEdges) {
		edges = new EdgeListProperty(StandardPropertyURN.EDGES, newEdges);
		apexList = null;
		neighbours = null;
		neighboursID = null;
	}

	public int addNeighbor(Edge e, EntityID id) {
		for (Edge edge : getEdges()) {
			if (edge.edgeEquals(e)) {
				if (e.isPassable()) {
					if (e.getNeighbour().getValue() == id.getValue()) {
						return MapVerifier.NEIGHBOR_WAS_CORRECT;
					} else {
						return id.getValue();
					}
				} else {
					e.setNeighbour(id);
					neighbours = null;
					neighboursID = null;
					return MapVerifier.NEIGHBOR_SET;
				}
			}
		}
		return MapVerifier.NEIGHBOR_SAME_EDGE_WAS_NOT_FOUND;
	}

	public void setCorrectApexList(boolean b) {
		correctApexList = b;
	}

	public boolean hasCorrectApexList() {
		return correctApexList;
	}

	// ******************************End Map Verifier*************************
	///ALI
	public double getSOSGroundArea() {
		if (groundArea < 0) {
			groundArea = SOSGeometryTools.computeArea(getApexList());
		}
		return groundArea;
	}
	/* ////////////////////End of S.O.S/////////////////// */

}
