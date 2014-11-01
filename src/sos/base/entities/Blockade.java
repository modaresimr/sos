package sos.base.entities;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.entities.StandardPropertyURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.EntityListener;
import rescuecore2.worldmodel.Property;
import rescuecore2.worldmodel.properties.EntityRefProperty;
import rescuecore2.worldmodel.properties.IntArrayProperty;
import rescuecore2.worldmodel.properties.IntProperty;
import sos.base.reachablity.ExpandBlockade;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.blockadeEstimator.BlockadeInterface;
import sos.base.util.geom.ShapeInArea;

/**
 * A blockade.
 */
public class Blockade extends StandardEntity implements BlockadeInterface, ShapeableObject {
	/* ///////////////////S.O.S instants////////////////// */
	private Road position;
	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**/private IntProperty x;
	/**/private IntProperty y;
	/**/private EntityRefProperty positionID;
	/**/private IntArrayProperty apexes;
	/**/private IntProperty repairCost;
	/**/private Shape shape;

	/**
	 * Construct a Blockade object with entirely undefined property values.
	 *
	 * @param id
	 *            The ID of this entity.
	 */
	public Blockade(EntityID id) {
		super(id);
		x = new IntProperty(StandardPropertyURN.X);
		y = new IntProperty(StandardPropertyURN.Y);
		positionID = new EntityRefProperty(StandardPropertyURN.POSITION);
		apexes = new IntArrayProperty(StandardPropertyURN.APEXES);
		repairCost = new IntProperty(StandardPropertyURN.REPAIR_COST);
		registerProperties(x, y, positionID, apexes, repairCost);
		shape = null;
		addEntityListener(new ApexesListener());
	}

	// Please don't add any method here!!!!!!
	/**
	 * Blockade copy constructor.
	 *
	 * @param other
	 *            The Blockade to copy.
	 */
	public Blockade(Blockade other) {
		super(other);
		x = new IntProperty(other.x);
		y = new IntProperty(other.y);
		positionID = new EntityRefProperty(other.positionID);
		apexes = new IntArrayProperty(other.apexes);
		repairCost = new IntProperty(other.repairCost);
		registerProperties(x, y, positionID, apexes, repairCost);
		shape = null;
		addEntityListener(new ApexesListener());
	}

	// Please don't add any method here!!!!!!
	@Override
	public Pair<Integer, Integer> getLocation() {
		if (!x.isDefined() || !y.isDefined()) {
			return null;
		}
		return new Pair<Integer, Integer>(x.getValue(), y.getValue());
	}

	// Please don't add any method here!!!!!!
	@Override
	protected Entity copyImpl() {
		return new Blockade(getID());
	}

	// Please don't add any method here!!!!!!
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.BLOCKADE;
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
		case POSITION:
			return positionID;
		case APEXES:
			return apexes;
		case REPAIR_COST:
			return repairCost;
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
	@Override
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
	 * Undefined the X property.
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
	@Override
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
	 * Get the apexes property.
	 *
	 * @return The apexes property.
	 */
	public IntArrayProperty getApexesProperty() {
		return apexes;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the apexes of this area.
	 *
	 * @return The apexes.
	 */
	@Override
	public int[] getApexes() {
		return apexes.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the apexes.
	 *
	 * @param apexes
	 *            The new apexes.
	 */
	@Override
	public void setApexes(int[] apexes) {
		this.apexes.setValue(apexes);
		shape = null;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the apexes property has been defined.
	 *
	 * @return True if the apexes property has been defined, false otherwise.
	 */
	public boolean isApexesDefined() {
		return apexes.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the apexes property.
	 */
	public void undefineApexes() {
		apexes.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the position property.
	 *
	 * @return The position property.
	 */
	public EntityRefProperty getPositionProperty() {
		return positionID;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the ID of position of this blockade.
	 *
	 * @return The position.
	 */
	public EntityID getPositionID() {
		return positionID.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the ID of position of this blockade.
	 *
	 * @return The position.
	 */
	@Override
	public Road getPosition() {
		return position == null ? position = (Road) standardModel().getEntity(positionID.getValue()) : position;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the position.
	 *
	 * @param position
	 *            The new position.
	 */
	public void setPosition(EntityID position) {
		this.positionID.setValue(position);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the position property has been defined.
	 *
	 * @return True if the position property has been defined, false otherwise.
	 */
	public boolean isPositionDefined() {
		return positionID.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the position property.
	 */
	public void undefinePosition() {
		positionID.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the repair cost property.
	 *
	 * @return The repair cost property.
	 */
	public IntProperty getRepairCostProperty() {
		return repairCost;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the repair cost of this blockade.
	 *
	 * @return The repair cost.
	 */
	@Override
	public int getRepairCost() {
		return repairCost.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the repair cost.
	 *
	 * @param cost
	 *            The new repair cost.
	 */
	public void setRepairCost(int cost) {
		this.repairCost.setValue(cost);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the repair cost property has been defined.
	 *
	 * @return True if the repair cost property has been defined, false otherwise.
	 */
	public boolean isRepairCostDefined() {
		return repairCost.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefined the repair cost property.
	 */
	public void undefineRepairCost() {
		repairCost.undefine();
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
			shape = new ShapeInArea(getApexes(),position);
		}
		return shape;
	}

	// Please don't add any method here!!!!!!
	private class ApexesListener implements EntityListener {
		@Override
		public void propertyChanged(Entity e, Property p, Object oldValue, Object newValue) {
			if (p == apexes) {
				shape = null;
				edges = null;
			}
		}
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "Blockade (" + getID() + ")";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
		ArrayList<Integer> t = new ArrayList<Integer>();
		if (isApexesDefined())
			for (Integer j : getApexes())
				t.add(j);
		return "Blockade[" + getID().getValue() + "] ,x=" + (isXDefined() ? getX() : "-") + " , y=" + (isYDefined() ? getY() : "-") + " , cost=" + (isRepairCostDefined() ? getRepairCost() : "-") + " , position=" + (isPositionDefined() ? getPositionID().getValue() : "-") + t;
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	// *******#######*******#######******* SOSBlockade *******#######*******#######*******
	private Point2D centeroid;

	// ////////END OF OLD REACHABLITY//////////////
	/**
	 * @author Ali
	 */
	@Override
	public Point2D getCenteroid() {
		if (centeroid == null) {
			centeroid = GeometryTools2D.computeCentroid(GeometryTools2D.vertexArrayToPoints(getApexes()));
		}
		return centeroid;
	}

	// ########################## {{ REACHABLITY }} ################################
	private List<Edge> edges;// Morteza2011
	private SOSArea expandedBlock = null;

	// Morteza2011*****************************************************************
	public List<Edge> getEdges() {
		// implemented by Navid-IT
		if (edges == null) {
			edges = AliGeometryTools.getEdges(getApexes());
		}
		return edges;
	}

	// Morteza2011*****************************************************************
	public void setExpandedBlock(boolean isNewBlockade) {
		SOSArea area = new SOSArea(getEdges());
		ArrayList<Blockade> b = new ArrayList<Blockade>();
		b.add(this);
		area.setReachablityBlockades(b);
		if (isNewBlockade)
			expandedBlock = ExpandBlockade.expandBlock(area, ReachablityConstants.BLOCK_EXPAND_WIDTH+getPosition().getExtraDistanceForNewExpand());
		else
			expandedBlock = ExpandBlockade.expandBlock(area, ReachablityConstants.BLOCK_EXPAND_WIDTH);
	}

	// Morteza2011*****************************************************************
	public SOSArea getExpandedBlock() {
		if (expandedBlock == null)
			setExpandedBlock(true);
		return expandedBlock;
	}

	// Morteza2012*****************************************************************
	public ArrayList<Road> removed() {
		ArrayList<Road> rs = new ArrayList<Road>();
		for (Area a : position.getNeighbours()) {
			if (a instanceof Road) {
				rs.add((Road) a);
				((Road) a).getNeighborBlockades().remove(this);
			}
		}
		rs.add(position);
		return rs;
	}

	// Morteza2012*****************************************************************
	public ArrayList<Road> checkNeighborRoads() {
		ArrayList<Road> rs = new ArrayList<Road>();
		for (Area a : position.getNeighbours()) {
			if (a instanceof Road) {
				Road r = ((Road) a);
				if (Utility.hasIntersect(expandedBlock, r.getExpandedArea())) {
					if (!r.getNeighborBlockades().contains(this)) {
						rs.add(r);
						r.getNeighborBlockades().remove(this);
						r.getNeighborBlockades().add(this);
					}
				} else if (r.getNeighborBlockades().contains(this)) {
					r.getNeighborBlockades().remove(this);
					rs.add(r);
				}
			}
		}
		rs.add(position);
		return rs;
	}
	// ################### {{ END OF REACHABLITY }} ##################################

}