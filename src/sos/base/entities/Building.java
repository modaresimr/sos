package sos.base.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardEntityConstants;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.entities.StandardPropertyURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.Property;
import rescuecore2.worldmodel.properties.BooleanProperty;
import rescuecore2.worldmodel.properties.IntProperty;
import sos.base.SOSConstant;
import sos.base.SOSWorldModel;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.sosFireEstimator.VirtualData;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSRealFireZone;
import sos.base.sosFireZone.util.Wall;
import sos.base.util.FireSearchBuilding;
import sos.base.util.IntList;
import sos.base.util.SOSGeometryTools;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.blockadeEstimator.BlockadeEstimator;
import sos.base.util.blockadeEstimator.SOSBlockade;
import sos.base.util.geom.ShapeInArea;
import sos.fire_v2.base.tools.FireBuilding;
import sos.fire_v2.base.tools.FireBuildingPP;
import sos.police_v2.PoliceConstants;
import sos.search_v2.tools.SOSAreaTools;
import sos.tools.Utils;

import com.infomatiq.jsi.Rectangle;

/**
 * The Building object.
 */
public class Building extends Area {
	/* ///////////////////S.O.S instants////////////////// */
	protected short index;
	public VirtualData virtualData[] = new VirtualData[1];//added by Yoosef
	public FireBuildingPP fireProperties;//Yoosef
	public ArrayList<Pair<String, Integer>> scoreData;//Yoosef

	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**
	 * Fieryness levels that indicate burning.
	 */
	/**/public static final EnumSet<StandardEntityConstants.Fieryness> BURNING = EnumSet.of(StandardEntityConstants.Fieryness.HEATING, StandardEntityConstants.Fieryness.BURNING, StandardEntityConstants.Fieryness.INFERNO);
	/**/private ArrayList<ShapeInArea> searchAreas;
	/**/private IntProperty floors;
	/**/private BooleanProperty ignition;
	/**/private IntProperty fieryness;
	/**/private IntProperty brokenness;
	/**/private IntProperty code;
	/**/private IntProperty attributes;
	/**/private IntProperty groundArea;
	/**/private IntProperty totalArea;
	/**/private IntProperty temperature;
	/**/private IntProperty importance;

	/**
	 * Construct a Building object with entirely undefined property values.
	 *
	 * @param id
	 *            The ID of this entity.
	 */
	public Building(EntityID id) {
		super(id);
		floors = new IntProperty(StandardPropertyURN.FLOORS);
		ignition = new BooleanProperty(StandardPropertyURN.IGNITION);
		fieryness = new IntProperty(StandardPropertyURN.FIERYNESS);
		brokenness = new IntProperty(StandardPropertyURN.BROKENNESS);
		code = new IntProperty(StandardPropertyURN.BUILDING_CODE);
		attributes = new IntProperty(StandardPropertyURN.BUILDING_ATTRIBUTES);
		groundArea = new IntProperty(StandardPropertyURN.BUILDING_AREA_GROUND);
		totalArea = new IntProperty(StandardPropertyURN.BUILDING_AREA_TOTAL);
		temperature = new IntProperty(StandardPropertyURN.TEMPERATURE);
		importance = new IntProperty(StandardPropertyURN.IMPORTANCE);
		registerProperties(floors, ignition, fieryness, brokenness, code, attributes, groundArea, totalArea, temperature, importance);

		if (!SOSConstant.IS_CHALLENGE_RUNNING) {
			scoreData = new ArrayList<Pair<String, Integer>>();
			fireProperties = new FireBuildingPP();
		}
	}

	// Please don't add any method here!!!!!!
	/**
	 * Building copy constructor.
	 *
	 * @param other
	 *            The Building to copy.
	 */
	public Building(Building other) {
		super(other);
		floors = new IntProperty(other.floors);
		ignition = new BooleanProperty(other.ignition);
		fieryness = new IntProperty(other.fieryness);
		brokenness = new IntProperty(other.brokenness);
		code = new IntProperty(other.code);
		attributes = new IntProperty(other.attributes);
		groundArea = new IntProperty(other.groundArea);
		totalArea = new IntProperty(other.totalArea);
		temperature = new IntProperty(other.temperature);
		importance = new IntProperty(other.importance);
		registerProperties(floors, ignition, fieryness, brokenness, code, attributes, groundArea, totalArea, temperature, importance);
		if (!SOSConstant.IS_CHALLENGE_RUNNING) {
			scoreData = new ArrayList<Pair<String, Integer>>();
			fireProperties = new FireBuildingPP();
		}
	}

	// Please don't add any method here!!!!!!
	@Override
	protected Entity copyImpl() {
		return new Building(getID());
	}

	// Please don't add any method here!!!!!!
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.BUILDING;
	}

	// Please don't add any method here!!!!!!
	@Override
	protected String getEntityName() {
		return "Building";
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
		case FLOORS:
			return floors;
		case IGNITION:
			return ignition;
		case FIERYNESS:
			return fieryness;
		case BROKENNESS:
			return brokenness;
		case BUILDING_CODE:
			return code;
		case BUILDING_ATTRIBUTES:
			return attributes;
		case BUILDING_AREA_GROUND:
			return groundArea;
		case BUILDING_AREA_TOTAL:
			return totalArea;
		case TEMPERATURE:
			return temperature;
		case IMPORTANCE:
			return importance;
		default:
			return super.getProperty(urn);
		}
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the floors property.
	 *
	 * @return The floors property.
	 */
	public IntProperty getFloorsProperty() {
		return floors;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the number of floors in this building.
	 *
	 * @return The number of floors.
	 */
	public int getFloors() {
		return floors.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the number of floors in this building.
	 *
	 * @param floors
	 *            The new number of floors.
	 */
	public void setFloors(int floors) {
		this.floors.setValue(floors);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the floors property has been defined.
	 *
	 * @return True if the floors property has been defined, false otherwise.
	 */
	public boolean isFloorsDefined() {
		return floors.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the floors property.
	 */
	public void undefineFloors() {
		floors.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the ignition property.
	 *
	 * @return The ignition property.
	 */
	public BooleanProperty getIgnitionProperty() {
		return ignition;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the value of the ignition property.
	 *
	 * @return The ignition property value.
	 */
	public boolean getIgnition() {
		return ignition.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the ignition property.
	 *
	 * @param ignition
	 *            The new ignition value.
	 */
	public void setIgnition(boolean ignition) {
		this.ignition.setValue(ignition);
		virtualData[0].setIgnition(true);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the ignition property has been defined.
	 *
	 * @return True if the ignition property has been defined, false otherwise.
	 */
	public boolean isIgnitionDefined() {
		return ignition.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the ingition property.
	 */
	public void undefineIgnition() {
		ignition.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the fieryness property.
	 *
	 * @return The fieryness property.
	 */
	public IntProperty getFierynessProperty() {
		return fieryness;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the fieryness of this building.
	 *
	 * @return The fieryness property value.
	 */
	public int getFieryness() {
		return fieryness.getValue();
	}

	// DON'T ADD ANY method HEAR!!!!!!
	/**
	 * Get the fieryness of this building as an enum constant. If fieryness is not defined then return null.
	 *
	 * @return The fieryness property value as a Fieryness enum, or null if fieryness is undefined.
	 */
	public StandardEntityConstants.Fieryness getFierynessEnum() {
		if (!fieryness.isDefined()) {
			return null;
		}//TODO
		return StandardEntityConstants.Fieryness.values()[fieryness.getValue()];
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the fieryness of this building.
	 *
	 * @param fieryness
	 *            The new fieryness value.
	 */
	public void setFieryness(int fieryness) {
		this.fieryness.setValue(fieryness);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the fieryness property has been defined.
	 *
	 * @return True if the fieryness property has been defined, false otherwise.
	 */
	public boolean isFierynessDefined() {
		return fieryness.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the fieryness property.
	 */
	public void undefineFieryness() {
		fieryness.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the brokenness property.
	 *
	 * @return The brokenness property.
	 */
	public IntProperty getBrokennessProperty() {
		return brokenness;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the brokenness of this building.
	 *
	 * @return The brokenness value.
	 */
	public int getBrokenness() {
		return brokenness.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the brokenness of this building.
	 *
	 * @param brokenness
	 *            The new brokenness.
	 */
	public void setBrokenness(int brokenness) {
		this.brokenness.setValue(brokenness);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the brokenness property has been defined.
	 *
	 * @return True if the brokenness property has been defined, false otherwise.
	 */
	public boolean isBrokennessDefined() {
		return brokenness.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the brokenness property.
	 */
	public void undefineBrokenness() {
		brokenness.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the building code property.
	 *
	 * @return The building code property.
	 */
	public IntProperty getBuildingCodeProperty() {
		return code;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the building code of this building.
	 *
	 * @return The building code.
	 */
	public int getBuildingCode() {
		return code.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the building code of this building as an enum constant. If building code is not defined then return null.
	 *
	 * @return The building code property value as a BuildingCode enum, or null if building code is undefined.
	 */
	public StandardEntityConstants.BuildingCode getBuildingCodeEnum() {
		if (!code.isDefined()) {
			return null;
		}
		return StandardEntityConstants.BuildingCode.values()[code.getValue()];
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the building code of this building.
	 *
	 * @param newCode
	 *            The new building code.
	 */
	public void setBuildingCode(int newCode) {
		this.code.setValue(newCode);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the building code has been defined.
	 *
	 * @return True if the building code has been defined, false otherwise.
	 */
	public boolean isBuildingCodeDefined() {
		return code.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the building code.
	 */
	public void undefineBuildingCode() {
		code.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the building attributes property.
	 *
	 * @return The building attributes property.
	 */
	public IntProperty getBuildingAttributesProperty() {
		return attributes;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the building attributes of this building.
	 *
	 * @return The building attributes.
	 */
	public int getBuildingAttributes() {
		return attributes.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the building attributes of this building.
	 *
	 * @param newAttributes
	 *            The new building attributes.
	 */
	public void setBuildingAttributes(int newAttributes) {
		this.attributes.setValue(newAttributes);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the building attributes property has been defined.
	 *
	 * @return True if the building attributes property has been defined, false otherwise.
	 */
	public boolean isBuildingAttributesDefined() {
		return attributes.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the building attributes.
	 */
	public void undefineBuildingAttributes() {
		attributes.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the ground area property.
	 *
	 * @return The ground area property.
	 */
	public IntProperty getGroundAreaProperty() {
		return groundArea;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the ground area of this building.
	 *
	 * @return The ground area.
	 */
	public int getGroundArea() {
		return groundArea.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the ground area of this building.
	 *
	 * @param ground
	 *            The new ground area.
	 */
	public void setGroundArea(int ground) {
		this.groundArea.setValue(ground);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the ground area property has been defined.
	 *
	 * @return True if the ground area property has been defined, false otherwise.
	 */
	public boolean isGroundAreaDefined() {
		return groundArea.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the ground area.
	 */
	public void undefineGroundArea() {
		groundArea.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the total area property.
	 *
	 * @return The total area property.
	 */
	public IntProperty getTotalAreaProperty() {
		return totalArea;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the total area of this building.
	 *
	 * @return The total area.
	 */
	public int getTotalArea() {
		return totalArea.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the total area of this building.
	 *
	 * @param total
	 *            The new total area.
	 */
	public void setTotalArea(int total) {
		this.totalArea.setValue(total);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the total area property has been defined.
	 *
	 * @return True if the total area property has been defined, false otherwise.
	 */
	public boolean isTotalAreaDefined() {
		return totalArea.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the total area property.
	 */
	public void undefineTotalArea() {
		totalArea.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the temperature property.
	 *
	 * @return The temperature property.
	 */
	public IntProperty getTemperatureProperty() {
		return temperature;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the temperature of this building.
	 *
	 * @return The temperature.
	 */
	public int getTemperature() {
		return temperature.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the temperature of this building.
	 *
	 * @param temperature
	 *            The new temperature.
	 */
	public void setTemperature(int temperature) {
		this.temperature.setValue(temperature);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the temperature property has been defined.
	 *
	 * @return True if the temperature property has been defined, false otherwise.
	 */
	public boolean isTemperatureDefined() {
		return temperature.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the temperature property.
	 */
	public void undefineTemperature() {
		temperature.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the building importance property.
	 *
	 * @return The importance property.
	 */
	public IntProperty getImportanceProperty() {
		return importance;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the importance of this building.
	 *
	 * @return The importance.
	 */
	public int getImportance() {
		return importance.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the importance of this building.
	 *
	 * @param importance
	 *            The new importance.
	 */
	public void setImportance(int importance) {
		this.importance.setValue(importance);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the importance property has been defined.
	 *
	 * @return True if the importance property has been defined, false otherwise.
	 */
	public boolean isImportanceDefined() {
		return importance.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the importance property.
	 */
	public void undefineImportance() {
		importance.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if this building is on fire.
	 *
	 * @return True if this buildings fieriness indicates that it is burning.
	 */
	public boolean isOnFire() {
		if (!fieryness.isDefined()) {
			return false;
		}
		return (fieryness.getValue() > 0 && fieryness.getValue() < 4);
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
		if(SOSConstant.IS_CHALLENGE_RUNNING)
			return "";
		//		if (isEdgesDefined() && getNeighbours().isEmpty())
		//			return "Building[" + getID().getValue() + "]";
		//		String a = " , neighbors=";
		//		if (isEdgesDefined())
		//			for (EntityID id : getNeighboursID())
		//				a = a.concat(id.getValue() + " : ");
		return "Building[" + getID().getValue() + "] ,brokness=" + (isBrokennessDefined() ? getBrokenness() : "-") + " , atrib=" + (isBuildingAttributesDefined() ? getBuildingAttributes() : "-") + " , code=" + (isBuildingCodeDefined() ? getBuildingCode() : "-") + " , fiery=" + (isFierynessDefined() ? getFieryness() : "-") + " , floors=" + (isFloorsDefined() ? getFloors() : "-") + " , areaGround=" + (isGroundAreaDefined() ? getGroundArea() : "-") + " , importance=" + (isImportanceDefined() ? getImportance() : "-") + " , temperature=" + (isTemperatureDefined() ? getTemperature() : "-") + " , totalArea=" + (isTotalAreaDefined() ? getTotalArea() : "-") + " , ignition=" + (isIgnitionDefined() ? getIgnition() : "-");
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "Building[" + getID().getValue() + "]";
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public short getBuildingIndex() {
		return index;
	}

	public void setBuildingIndex(short index) {
		this.index = index;
	}

	/**
	 * @author Ali
	 * @return
	 */
	public java.awt.geom.Area getMaxBlockadeArea() {
		//		if (maxBlockadeArea == null)
		java.awt.geom.Area maxBlockadeArea = BlockadeEstimator.expandBuildingForCollapsingBlockade(this, BlockadeEstimator.getMaximumBlockadeSize(this));
		if (!maxBlockadeArea.isSingular()) {
			List<java.awt.geom.Area> areas = AliGeometryTools.fix(maxBlockadeArea);
			for (java.awt.geom.Area area : areas) {
				SOSBlockade sosBlock = new SOSBlockade(area, null);
				if (sosBlock.getApexes().length >= 2 && sosBlock.getRepairCost() > 0) {
					maxBlockadeArea = area;
				}
			}
			if (!maxBlockadeArea.isSingular())
				model().log().error("Creating Max Blockade has error for " + this);
		}
		return maxBlockadeArea;
	}

	/****************************************************************************/
	/************************ START OF FIRE ZONE ********************************/
	/****************************************************************************/
	@Override
	protected void firePropertyChanged(Property p, Object oldValue, Object newValue) {
		super.firePropertyChanged(p, oldValue, newValue);
		if (p == temperature) {
			int oldT = (Integer) oldValue;
			int newT = (Integer) oldValue;
			if (newT > oldT)
				setTempIncreasedTime(model().time());
		}
	};

	private HashMap<Short, Float> real_neighbors_BuildValue = new HashMap<Short, Float>();// Yoosef2013
	private HashMap<Short, Float> neighbors_BuildValue = new HashMap<Short, Float>();// Yoosef2013
	private Collection<Wall> walls; // Morteza2011

	private ArrayList<Building> realNeighbors_Building = new ArrayList<Building>();// Morteza2011
	private ArrayList<Building> Neighbors_Building = new ArrayList<Building>();// Morteza2011

	public double totalWallArea;// Morteza2011
	private FireSearchBuilding fireSearchBuilding;
	private FireBuilding fireBuilding;

	// Morteza2011*****************************************************************************************************************************
	public ArrayList<Building> realNeighbors_Building() {
		return realNeighbors_Building;
	}

	public ArrayList<Building> neighbors_Building() {
		return Neighbors_Building;
	}

	// AngehA2&Morteza2011********************************************************************************************************************
	public Collection<Wall> walls() {
		return walls;
	}

	// AngehA2&Morteza2011********************************************************************************************************************
	public void setWalls(Collection<Wall> walls) {
		this.walls = walls;
	}

	/** radiating Rays *A2 */
	public void initWallValues() {
		//		ArrayList<ShapeDebugFrame.AWTShapeInfo> back = new ArrayList<ShapeDebugFrame.AWTShapeInfo>();
		//		for (Building b : model().buildings()) {
		//			back.add(new AWTShapeInfo(b.getShape(), b.toString(), Color.gray, false));
		//		}
		//		debug.setBackground(back);
		int totalRays = 0;
		//		HashMap<Building, Integer> connectedBuildings = new HashMap<Building, Integer>();
		byte[] connectedBuildings = new byte[model().buildings().size()];
		for (Iterator<Wall> w = walls.iterator(); w.hasNext();) {
			Wall wall = w.next();
			wall.findHits(model(), connectedBuildings);
			totalRays += wall.rays;
		}

		float base = totalRays;
		for (int i = 0; i < connectedBuildings.length; i++) {
			int value = connectedBuildings[i];
			if (connectedBuildings[i] == 0)
				continue;
			float f = value / base;
			this.neighbors_BuildValue().put(model().buildings().get(i).getBuildingIndex(), f);

		}
	}

	// AngehA2&Morteza2011*************************************************************************************************************
	/** sets more reliable neighbors * A2 */
	public boolean isRealNeighbor(Building b, float f) {//Yoosef
		//		this.neighbors_BuildValue().put(b.getBuildingIndex(), f);
		if (this.distance(b) < 30000 && f > 0.01) {
			return true;
		} else if (this.distance(b) < 35000 && f > 0.015) {
			return true;
		} else if (f > 0.02) {
			return true;
		} else {
			return false;
			//System.err.println("[suspicious event] : [ not added to nghbrs becaus of connected values ]" + this + " :: " + b + " :: " + f);
		}
	}

	// AngehA2&Morteza2011*********************************************************************************************************
	/** makes wall for the building and calculates the total area of the B walls */
	public void initializeWalls(SOSWorldModel world) {
		if (walls != null)
			return;

		totalWallArea = 0;
		walls = new ArrayList<Wall>(getEdges().size());
		int fx = getApexList()[0];
		int fy = getApexList()[1];
		int lx = fx;
		int ly = fy;

		for (int n = 2; n < getApexList().length; n++) {
			int tx = getApexList()[n];
			int ty = getApexList()[++n];
			Wall w = new Wall(lx, ly, tx, ty, this);
			if (w.validate()) {
				walls.add(w);
				totalWallArea += w.length * 1000 * VirtualData.FLOOR_HEIGHT;
			}
			//			apexes.add(new Point(tx, ty));
			lx = tx;
			ly = ty;
		}
		Wall w = new Wall(lx, ly, fx, fy, this);
		walls.add(w);
		//		apexes.remove(new Point(lx, ly));
		//		apexes.add(new Point(lx, ly));
		totalWallArea = totalWallArea / 1000000d;
	}

	// AngehA2&Morteza2011&nima**********************************************************************************************************
	/** sets Building's exact neighbors * A2 */
	public void setRealNeighbors() {
		if (!realNeighbors_Building.isEmpty())
			System.out.println("D");
		for (Entry<Short, Float> bv : neighbors_BuildValue().entrySet()) {
			Building neigh = model().buildings().get(bv.getKey());
			float f = bv.getValue();
			if (isRealNeighbor(neigh, f)) {
				realNeighbors_Building.add(neigh);
				real_neighbors_BuildValue.put(bv.getKey(), bv.getValue());
			}
			Neighbors_Building.add(neigh);
		}
	}

	// Morteza2011&nima*****************************************************************************************************************************
	public void setRayNeighbors(HashMap<Short, Float> neighbors_BuildValue) {
		this.neighbors_BuildValue = neighbors_BuildValue;
		setRealNeighbors();
	}

	// AngehA2&Morteza2011**********************************************************************************************************
	public int distance(Building b) {
		return (int) Math.sqrt((double) (b.x() - x()) * (b.x() - x()) + (double) (b.y() - y()) * (b.y() - y()));
	}

	public int distance2(Building b) {
		return (b.x() - x()) * (b.x() - x()) + (b.y() - y()) * (b.y() - y());
	}

	/** @author Hesam 002 */
	public int distance(Human f) {
		return (int) Math.sqrt((double) (f.getX() - x()) * (f.getX() - x()) + (double) (f.getY() - y()) * (f.getY() - y()));
	}

	//	/** @author nima */
	//	public ArrayList<Building> getNearBuildings() {
	//		return nearBuildings;
	//	}

	// AngehA2&Morteza2011*********************************************************************************************************
	public int x() {
		return this.getX();
	}

	// AngehA2&Morteza2011********************************************************************************************************
	public int y() {
		return this.getY();
	}

	public float getWallDistanceTo(Building n) {
		Point2D p1 = null;
		Point2D p2 = null;
		float dis = 0;
		Edge edge = new Edge(new Point2D(getX(), getY()), new Point2D(n.getX(), n.getY()));
		for (Edge e : getEdges()) {
			p1 = Utility.getIntersect(e, edge);
			if (p1 != null)
				break;
		}
		for (Edge e : n.getEdges()) {
			p2 = Utility.getIntersect(e, edge);
			if (p2 != null)
				break;
		}
		if (p1 != null && p2 != null)
			dis = (float) GeometryTools2D.getDistance(p1, p2);
		else
			System.err.println("wall distance null");
		return dis;
	}

	// AngehA2********************************************************************************************************************
	public void freeResources() {
		//		apexes = null;
		walls.clear();
		walls = null;
	}

	/****************************************************************************/
	/************************ END OF FIRE ZONE ********************************/
	/****************************************************************************/
	// Navid-IT&Salim**************************************************************************************************************
	/** find the best areas that you can see in the building from it */
	public ArrayList<ShapeInArea> findSightArea(ArrayList<Area> areas) throws Exception {
		ArrayList<ShapeInArea> searchAreas = new ArrayList<ShapeInArea>();

		Point2D center = new Point2D(getX(), getY());
		Pair<Point2D, Point2D> points = null;

		//		List<Line2D> entrancesEdge = entrances();
		//		for (int i = 0; i < entrancesEdge.size(); i++) {
		//			Line2D edgeLine = entrancesEdge.get(i);
		Edge[] entrancesEdge = getPassableEdges();
		for (int i = 0; i < entrancesEdge.length; i++) {
			Line2D edgeLine = entrancesEdge[i].getLine();
			if (SOSGeometryTools.distance(entrancesEdge[i], new Point2D(getX(), getY())) > model().sosAgent().VIEW_DISTANCE)
				continue;
			// _______________________________________
			points = SOSAreaTools.get2PointsOnParallelLine(edgeLine, center);
			// _______________________________________
			makeAreas(points.first(), edgeLine, center, areas, searchAreas);
			makeAreas(points.second(), edgeLine, center, areas, searchAreas);

		}
		return searchAreas;
	}

	/** make the preliminary area that should be intersected by the other objects */
	private void makeAreas(Point2D point, Line2D edgeLine, Point2D center, List<Area> areas, ArrayList<ShapeInArea> searchAreas) throws Exception {
		Point2D oo = null;
		Point2D ee = null;
		Pair<Line2D, Line2D> lines = getProperLines(point, edgeLine, center);
		Line2D po = lines.first();
		Line2D ce = lines.second();
		// _______________________________________
		List<Point2D> oo0 = Utility.get2PointsAroundAPointOnLine(po.getOrigin(), po.getEndPoint(), po.getEndPoint(), model().sosAgent().VIEW_DISTANCE);// FIXME should be gotten from config
		List<Point2D> ee0 = Utility.get2PointsAroundAPointOnLine(ce.getOrigin(), ce.getEndPoint(), ce.getEndPoint(), model().sosAgent().VIEW_DISTANCE);// FIXME

		if (Utils.distance(oo0.get(0).getX(), oo0.get(0).getY(), po.getEndPoint().getX(), po.getEndPoint().getY()) < Utils.distance(oo0.get(0).getX(), oo0.get(0).getY(), po.getOrigin().getX(), po.getOrigin().getY())) {
			oo = oo0.get(0);
		} else {
			oo = oo0.get(1);
		}
		if (Utils.distance(ee0.get(0).getX(), ee0.get(0).getY(), ce.getEndPoint().getX(), ce.getEndPoint().getY()) < Utils.distance(ee0.get(0).getX(), ee0.get(0).getY(), ce.getOrigin().getX(), ce.getOrigin().getY())) {
			ee = ee0.get(0);
		} else {
			ee = ee0.get(1);
		}

		//		center-oo
		//		point-ee
		oo = doIntersectWithBuildings(center, oo);
		ee = doIntersectWithBuildings(point, ee);
		for (Area area : areas) {
			SOSArea ab;
			ab = new SOSArea(edgeMaker(center, point, oo, ee));
			SOSArea sosarea;
			if (area instanceof Road)
				sosarea = ((Road) area).getExpandedArea();
			else
				sosarea = new SOSArea(area.getEdges());

			if (Utility.hasIntersect(ab, sosarea)) {
				ab = SOSAreaTools.intersect(sosarea, ab);// FIXME age kond bood sefreh kon
				if (ab.getEdges().size() != 0) {
					int[] apx = ab.getApexes();
					if (apx.length >= 6 && (SOSGeometryTools.computeArea(apx) > 100000 || area instanceof Building && SOSGeometryTools.computeArea(apx) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM))
						searchAreas.add(new ShapeInArea(apx, area));
				}
			}
		}

	}

	private Point2D doIntersectWithBuildings(Point2D start, Point2D end) {

		//					debug.show("", line,new ShapeDebugFrame.Point2DShapeInfo(new Point2D(end.x, end.y), "end", Color.red, true),
		//							new ShapeDebugFrame.Point2DShapeInfo(new Point2D(start.x, start.y), "start", Color.blue, true));
		Rectangle rect = new Rectangle(start.getIntX(), start.getIntY(), end.getIntX(), end.getIntY());
		//		Collection<Building> jlist = world.getObjectsInRectangle(rect, Building.class);
		IntList indexlist = model().getBuildingIndexInRectangle(rect);

		//					jlist.remove(owner);
		//					ArrayList<Building> jlist=new ArrayList<Building>();
		//					for (Building jbuilding : jlist) {
		boolean[] bd = new boolean[model().buildings().size()];
		bd[getBuildingIndex()] = true;
		double minDist = Double.MAX_VALUE;
		Point2D minCross = end;
		for (int ii = 0; ii < indexlist.size(); ii++) {
			int bindex = indexlist.get(ii);

			if (bd[bindex])
				continue;
			//						if (!owner.getNearBuildings().contains(jbuilding))
			//							continue;

			bd[bindex] = true;
			Building building = model().buildings().get(bindex);
			//			jlist.add(building);
			for (Edge other : building.getEdges()) {
				Point2D cross = GeometryTools2D.getSegmentIntersectionPoint(new Line2D(start, end), other.getLine());
//				Point2D cross = Utill.intersectLowProcess(start.getIntX(), start.getIntY(), end.getIntX(), end.getIntY(), other.getStartX(), other.getStartY(), other.getEndX(), other.getEndY());
				if (cross != null) {
					double d = cross.distanceSq(start);
					if (d < minDist) {
						minDist = d;
						minCross = cross;
					}
				}
			}
		}
		return minCross;
	}

	/** find the entrances of building...(straight entrances) */
	public ArrayList<Line2D> entrances() {
		ArrayList<Line2D> entrances = new ArrayList<Line2D>();
		for (int i = 0; i < getNeighboursID().size(); i++) {
			entrances.add(straghtEntrance(getEdesTo(getNeighboursID().get(i), this)));
		}
		return entrances;
	}

	/** make four edges that make inclose shape by these four points */
	private List<Edge> edgeMaker(Point2D first, Point2D second, Point2D third, Point2D forth) {
		List<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(first, second));
		edges.add(new Edge(second, third));
		edges.add(new Edge(third, forth));
		edges.add(new Edge(forth, first));
		return edges;
	}

	/** find the edge between two object */
	private ArrayList<Line2D> getEdesTo(EntityID id, Area a) {
		ArrayList<Line2D> results = new ArrayList<Line2D>();
		for (Edge edge : a.getEdges()) {
			if (edge.isPassable()) {
				if (edge.getNeighbour().equals(id)) {
					results.add(edge.getLine());
				}
			}
		}
		return results;

	}

	/**
	 * this function get some edges and return straight line from origin to end implemented and designed by Navid-IT
	 */
	private Line2D straghtEntrance(ArrayList<Line2D> edges) {
		List<Point2D> points = new ArrayList<Point2D>();
		for (int i = 0; i < edges.size(); i++) {
			points.add(edges.get(i).getOrigin());
			points.add(edges.get(i).getEndPoint());
		}
		double maxSize = 0;
		Point2D maxPoint1 = null;
		Point2D maxPoint2 = null;
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < points.size(); j++) {
				if (Utils.distance(points.get(i).getX(), points.get(i).getY(), points.get(j).getX(), points.get(j).getY()) > maxSize) {
					maxSize = Utils.distance(points.get(i).getX(), points.get(i).getY(), points.get(j).getX(), points.get(j).getY());
					maxPoint1 = points.get(i);
					maxPoint2 = points.get(j);
				}

			}
		}

		return new Line2D(maxPoint1, maxPoint2);
	}

	/** get the line that is proper by the entrances */
	private Pair<Line2D, Line2D> getProperLines(Point2D point, Line2D edgeLine, Point2D center) {
		if (Utils.distance(point.getX(), point.getY(), edgeLine.getEndPoint().getX(), edgeLine.getEndPoint().getY()) < Utils.distance(point.getX(), point.getY(), edgeLine.getOrigin().getX(), edgeLine.getOrigin().getY())) {
			return new Pair<Line2D, Line2D>(new Line2D(point, edgeLine.getEndPoint()), new Line2D(center, edgeLine.getOrigin()));

		} else {
			return new Pair<Line2D, Line2D>(new Line2D(center, edgeLine.getEndPoint()), new Line2D(point, edgeLine.getOrigin()));
		}

	}

	public void setSearchAreas(ArrayList<ShapeInArea> searchAreas) {
		this.searchAreas = searchAreas;
	}

	public ArrayList<ShapeInArea> getSearchAreas() {
		if (searchAreas == null)
			searchAreas = new ArrayList<ShapeInArea>();
		return searchAreas;
	}

	/** get the roads and objects that should be intersect by the main area */
	public ArrayList<Area> getRoadsInSight() {
		ArrayList<Area> result = new ArrayList<Area>();
		for (Area area : getNeighbours()) {
			if (area instanceof Road) {
				result.add(area);
				for (Area a : area.getNeighbours()) {
					if (a instanceof Road) {
						result.add(a);
					}
				}
			}
		}
		return result;
	}

	public ArrayList<Area> getAreasInSight() {
		ArrayList<Area> result = new ArrayList<Area>();
		for (Area area : getNeighbours()) {
			result.add(area);
			for (Area a : area.getNeighbours()) {
				result.add(a);
			}
		}
		return result;
	}

	public void setFireSearchBuilding(FireSearchBuilding fireSearchBuilding) {
		this.fireSearchBuilding = fireSearchBuilding;
	}

	public FireSearchBuilding fireSearchBuilding() {
		return fireSearchBuilding;
	}

	public void setFireBuilding(FireBuilding fireBuilding) {
		this.fireBuilding = fireBuilding;
	}

	public FireBuilding getFireBuilding() {
		return fireBuilding;
	}

	public HashMap<Short, Float> real_neighbors_BuildValue() {
		return real_neighbors_BuildValue;
	}

	public HashMap<Short, Float> neighbors_BuildValue() {
		return neighbors_BuildValue;
	}

	public boolean isBurning() {
		return isFierynessDefined() && getFieryness() > 0 && getFieryness() < 4;
	}

	public boolean isEitherFieryOrBurnt() {
		return isBurning() || getFieryness() == 8 || getFieryness() == 7;
	}

	public boolean isIsLandOutSide() {
		return isIsLandOutSide;
	}

	public void setIsLandOutSide(boolean isIsLandOutSide2) {
		this.isIsLandOutSide = isIsLandOutSide2;
	}

	private boolean isIsLandOutSide = false;
	private int searchedForCivilianTime = -1;//ali
	private int priority;

	/**
	 * @author Ali
	 * @param searchedForCivilian
	 */
	public void setSearchedForCivilian(int searchedForCivilianTime) {
		this.searchedForCivilianTime = searchedForCivilianTime;
	}

	/**
	 * @author Ali
	 * @param searchedForCivilian
	 */
	public boolean isSearchedForCivilian() {
		return searchedForCivilianTime != -1;
	}

	public int getLastSearchedForCivilianTime() {
		return searchedForCivilianTime;
	}

	public void addPriority(int priority, String reason) {
		if (!SOSConstant.IS_CHALLENGE_RUNNING)
			scoreData.add(new Pair<String, Integer>(reason, priority));
		this.priority += priority;
	}


	public void resetPriority() {
		if (!SOSConstant.IS_CHALLENGE_RUNNING) {
			scoreData.clear();
			scoreData.add(new Pair<String, Integer>("Time", getAgent().model().time()));
		}
		this.priority = 0;
	}

	public int priority() {
		return this.priority;
	}

	//ali
	public int getPermiout() {
		int edgeLenght = 0;
		for (Edge e : getEdges()) {
			edgeLenght += SOSGeometryTools.distance(e.getEnd(), e.getStart());
		}
		return edgeLenght;
	}

	//ali
	public ArrayList<Civilian> getCivilians() {
		ArrayList<Civilian> res = new ArrayList<Civilian>();

		Collection<Civilian> cives = standardModel().civilians()/* model().getObjectsInRectangle(getShape().getBounds(), Civilian.class) */;
		for (Civilian c : cives)
			if (c.isPositionDefined() && c.getPosition() == this)
				res.add(c);

		//		ArrayList<Civilian> res2 = new ArrayList<Civilian>();
		//		Collection<Civilian> cives2 = model().civilians();
		//		for (Civilian c : cives2)
		//			if (c.isPositionDefined() && c.getPosition().equals(this))
		//				res.add(c);
		//		if(res2.size()!=res.size())
		//			System.err.println("DDDD");

		return res;
	}

	private boolean isMapSide = false;

	public void setMapSide(boolean b) {
		isMapSide = b;
	}

	public boolean isMapSide() {
		return isMapSide;
	}

	public float getNeighValue(Building k) {
		float bv = neighbors_BuildValue.get(k.getBuildingIndex());
		return bv;
	}

	public float getRealNeighValue(Building k) {
		float bv = real_neighbors_BuildValue.get(k.getBuildingIndex());
		return bv;
	}

	public int[] getApexes() {
		int[] apexList = new int[getEdges().size() * 2];
		int i = 0;
		for (Edge next : getEdges()) {
			apexList[i++] = next.getStartX();
			apexList[i++] = next.getStartY();
		}
		return apexList;
	}

	//////////////////////Yoosef 2013

	private int valueSpecialForFire = 0;

	public String ss = "";

	public void setSpecialForFire(int b, String s) {
		valueSpecialForFire = b;
		if (!SOSConstant.IS_CHALLENGE_RUNNING) {
			if (s.equals("reset"))
				ss = "";
			ss += "\n" + s;
		}
	}

	public int getValuSpecialForFire() {
		return valueSpecialForFire;
	}

	private SOSEstimatedFireZone estimator;//in fireSite hast ke man be onvane buildinghaye estimator tush hastam

	public SOSEstimatedFireZone getEstimator() {
		return estimator;
	}

	public void addToEstimator(SOSEstimatedFireZone f) {
		estimator = f;
	}

	private int tempIncreasedTime = 999999;

	public int getTemperatureIncreasedTime() {
		return tempIncreasedTime;
	}

	public void setTempIncreasedTime(int tempIncreasedTime) {
		this.tempIncreasedTime = tempIncreasedTime;
	}

	/*
	 * Hesam
	 */

	private SOSRealFireZone realFireSite;
	private SOSEstimatedFireZone estimateFireSite;

	public void setSOSRealFireSite(SOSRealFireZone sosRealFireSite) {
		this.realFireSite = sosRealFireSite;
	}

	public SOSRealFireZone getSOSRealFireSite() {
		return realFireSite;
	}

	public void setSOSEstimateFireSite(SOSEstimatedFireZone estimateFireSite) {
		this.estimateFireSite = estimateFireSite;
	}

	public SOSEstimatedFireZone getSOSEstimateFireSite() {
		return estimateFireSite;
	}

	/* ////////////////////End of S.O.S/////////////////// */

	public int getTempIncreasedTime() {
		return tempIncreasedTime;
	}

}