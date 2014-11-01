package sos.base.entities;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import sos.base.SOSConstant;
import sos.base.reachablity.CreateReachableParts;
import sos.base.reachablity.MergeBlockades;
import sos.base.reachablity.tools.EdgeElement;
import sos.base.reachablity.tools.SOSArea;
import sos.base.util.SOSGeometryTools;
import sos.base.util.blockadeEstimator.SOSBlockade;
import sos.tools.UnionFind;
import sos.tools.Utils;

/**
 * The Road object.
 */
public class Road extends Area {

	/* ///////////////////S.O.S instants////////////////// */
	protected short roadIndex;
	private ArrayList<SOSBlockade> middleBlockades = new ArrayList<SOSBlockade>();
	// ********************************************
	private boolean entrancePossibilityChecked = false; // Salim
	private boolean isEntrance;// Salim
	// ********************************************
	private Point2D positionBase = null; //Aramik


	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**
	 * Construct a Road object with entirely undefined property values.
	 *
	 * @param id
	 *            The ID of this entity.
	 */
	public Road(EntityID id) {
		super(id);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Road copy constructor.
	 *
	 * @param other
	 *            The Road to copy.
	 */
	public Road(Road other) {
		super(other);
	}

	// Please don't add any method here!!!!!!
	@Override
	protected Entity copyImpl() {
		return new Road(getID());
	}

	// Please don't add any method here!!!!!!
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.ROAD;
	}

	// Please don't add any method here!!!!!!
	@Override
	protected String getEntityName() {
		return "Road";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "Road[" + getID().getValue() + "]";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
		if(SOSConstant.IS_CHALLENGE_RUNNING)
			return "";
		try{
		if (isEdgesDefined() && getNeighbours().isEmpty())
			return "Road[" + getID().getValue() + "]";
		//		String a = " | neighbors=";
		//		String apex = " | apex=";
		//		String edges = " | edges=";
		String block = " | blockades=";
		String blockEntity = " | blockadesEntity=";
		//		if (isEdgesDefined())
		//			for (EntityID id : getNeighboursID())
		//				a = a.concat(id.getValue() + " : ");
		//		if (isEdgesDefined())
		//			for (Integer id : getApexList())
		//				apex = apex.concat(id + " : ");
		//		if (isEdgesDefined())
		//			for (Edge ed : getEdges())
		//				edges = edges.concat(ed + " : ");
		if (isBlockadesDefined())
			for (EntityID in : getBlockadesID())
				block += in.getValue() + " : ";
		else
			block += "Undefined!";
		if (isBlockadesDefined())
			for (Blockade in : getBlockades()){
				if(in ==null)
					getAgent().sosLogger.error(new NullPointerException("How blockade is null???"));
				else
					blockEntity += in.getID() + " : ";
			}

		else
			blockEntity += "Undefined!";
		//		return "Road[" + getID().getValue() + "] ,x=" + (isXDefined() ? getX() : "-") + " , y=" + (isYDefined() ? getY() : "-") + " , hasBeenSeen=" + hasBeenSeen() + a + apex + edges + block;
		return "Road[" + getID().getValue() + "] , hasBeenSeen=" + hasBeenSeen() + block + blockEntity;
		}catch(Exception e){
			getAgent().sosLogger.error("Exception in road full description:"+e.getMessage());
			return "";
		}
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public short getRoadIndex() {
		return roadIndex;
	}

	public void setRoadIndex(short index) {
		this.roadIndex = index;
	}

	public Point2D getPositionBase() {
		return positionBase;
	}

	public void setPositionBase(Point2D positionBase) {
		this.positionBase = positionBase;
	}

	/**
	 * @author Ali
	 */
	public java.awt.geom.Area getGeomArea() {
		return new java.awt.geom.Area(getShape());
	}

	/**
	 * @author Ali
	 *         after reachablity computed it return null collection
	 */
	public ArrayList<SOSBlockade> getMiddleBlockades() {
		return middleBlockades;
	}

	public void setMiddleBlockadesNull() {
		middleBlockades = null;
	}

	// ###################### {{ REACHABLITY }} ###################################
	/** Morteza2011 */
	private int extraDistanceForNewExpand = -10;
	/** Morteza2011 */
	private ArrayList<SOSArea> reachableParts;
	/** Morteza2011 */
	private SOSArea expandedArea = null;
	/** Morteza2011 */
	private ArrayList<ArrayList<EdgeElement>> reachableEdges = new ArrayList<ArrayList<EdgeElement>>();
	/** Morteza2011 */
	private ArrayList<SOSArea> mergedBlockades = null;
	/** Morteza2011 */
	private UnionFind unionForEdges;
	/** Morteza2011 */
	private UnionFind unionForReachablePartsAndEdges;
	/** Morteza2012 */
	private ArrayList<Blockade> neighborBlockades = new ArrayList<Blockade>();

	/** Morteza2012 *****************************************************************/
	public ArrayList<Blockade> getNeighborBlockades() {
		return neighborBlockades;
	}

	/** Morteza2011 *****************************************************************/
	public int getExtraDistanceForNewExpand() {
		extraDistanceForNewExpand += 10;
		if (extraDistanceForNewExpand > 60)
			extraDistanceForNewExpand = 0;
		return extraDistanceForNewExpand;
	}

	/** Morteza2011 *****************************************************************/
	public void setExpandedArea(SOSArea area) {
		expandedArea = area;
	}

	/** Morteza2011 *****************************************************************/
	public SOSArea getExpandedArea() {
		return expandedArea;
	}

	/** Morteza2011 *****************************************************************/
	public void setReachableParts() {
		reachableParts = CreateReachableParts.createReachableAreaParts(this, getExpandedArea(), mergedBlockades);
	}

	/** Morteza2011 *****************************************************************/
	public ArrayList<SOSArea> getReachableParts() {
		return reachableParts;
	}

	/** Morteza2011 *****************************************************************/
	public void setReachableEdges() {
		this.reachableEdges = CreateReachableParts.createReachableEdges(this);
	}

	/** Morteza2011 *****************************************************************/
	public ArrayList<ArrayList<EdgeElement>> getReachableEdges() {
		return reachableEdges;
	}

	/** Morteza2011 *****************************************************************/
	public void setMergedBlockades(ArrayList<SOSArea> blockasToMerge) {

		try {
			mergedBlockades = MergeBlockades.mergeBlockades(this, blockasToMerge);
		} catch (Exception e) {
			mergedBlockades = new ArrayList<SOSArea>();
			if (isBlockadesDefined()) {
				for (Blockade b : getBlockades()) {
					mergedBlockades.add(b.getExpandedBlock());
				}
			}
			e.printStackTrace();
		}
	}

	/** Morteza2011 *****************************************************************/
	public ArrayList<SOSArea> getMergedBlockades() {
		if (mergedBlockades == null)
			mergedBlockades = new ArrayList<SOSArea>();
		return mergedBlockades;
	}

	/** Morteza2011 *****************************************************************/
	public void setDisjonSetForEdges() {
		int n = 0;
		n = getEdges().size();
		unionForEdges = new UnionFind(n);
		for (ArrayList<EdgeElement> ee : reachableEdges) {
			for (int i = 0; i < ee.size(); i++) {
				for (int j = 0; j < ee.size(); j++) {
					unionForEdges.setUnion(ee.get(i).getEdge().getReachablityIndex(), ee.get(j).getEdge().getReachablityIndex());
				}
			}
		}

	}

	/** Morteza2011 *****************************************************************/
	public void setDisjonSetForReachablePartsAndEdges() {
		int n = 0;
		n = getEdges().size() + reachableParts.size();
		unionForReachablePartsAndEdges = new UnionFind(n);
		for (short i = 0; i < reachableEdges.size(); i++) {
			for (short j = 0; j < reachableEdges.get(i).size(); j++) {
				unionForReachablePartsAndEdges.setUnion(i, (short) (reachableEdges.get(i).get(j).getEdge().getReachablityIndex() + reachableParts.size()));
			}
		}
	}

	/** Morteza2011 *****************************************************************/
	public UnionFind getDisjiontSetForEdges() {
		return unionForEdges;
	}

	/** Morteza2011 *****************************************************************/
	public UnionFind getDisjiontSetForReachablePartsAndEdges() {
		return unionForReachablePartsAndEdges;
	}

	/** Morteza2011 *****************************************************************/
	public void setReachablityChanges() {
		extraDistanceForNewExpand = -10;
		ArrayList<SOSArea> blockasToMerge = new ArrayList<SOSArea>();
		for (Blockade b : getBlockades()) {
			blockasToMerge.add(b.getExpandedBlock());
		}
		for (Blockade b : neighborBlockades) {
			blockasToMerge.add(b.getExpandedBlock());
		}
		setMergedBlockades(blockasToMerge);
		setReachableParts();
		setReachableEdges();
		setDisjonSetForEdges();
		setDisjonSetForReachablePartsAndEdges();
		mergedBlockades.clear();
	}

	// ################### {{ END OF REACHABLITY }} ###############################

	// ***************************************************
	/**
	 * IF true it means that we have already have checked if this road is entrance or not.
	 *
	 * @return boolean
	 * @author Salim
	 */
	public boolean entrancePossibilityChecked() {
		return entrancePossibilityChecked;
	}

	/**
	 * sets the entrance possibility check flag true;
	 *
	 * @author Salim
	 */
	public void setEntrancePossibilityChecked() {
		entrancePossibilityChecked = true;
	}

	/**
	 * Returns true if the road is set to be and entrance road.<br>
	 * if not set it checks the possibility<br>
	 *
	 * @author Salim
	 * @return
	 */
	public boolean isEntrance() {
		if (entrancePossibilityChecked())
			return isEntrance;
		return checkEntrancePossibility();
	}

	public boolean isNeighbourWithBuilding() {
		for (Area nei : getNeighbours()) {
			if (nei instanceof Building)
				return true;
		}
		return false;
	}

	/**
	 * For current Road it checks if it is Entrance or not. <b> Entrance Road:<b> A road is called
	 * entrance if there is at least a building which is <br>
	 * connected to the rest of the city through this road and if we remove the building there is<br>
	 * no use for the road<br>
	 *
	 * @return
	 * @author Salim
	 */
	private boolean checkEntrancePossibility() {
		if (entrancePossibilityChecked())
			return isEntrance;
		// --------------------------------
		setEntrancePossibilityChecked();
		boolean hasBuildingNeighbor = false;
		for (Area nei : getNeighbours()) {
			if (nei instanceof Building) {
				hasBuildingNeighbor = true;
				break;
			}

		}
		// --------------------------------
		if (!hasBuildingNeighbor) {
			isEntrance = false;
			return false;
		}
		// --------------------------------
		if (getNeighborRoads().size() == 1) {
			isEntrance = true;
			return true;
		}
		// --------------------------------
		else if (getNeighborRoads().size() == 2) {
			if (sos.base.util.Utils.areNeighbour(getNeighborRoads().get(0), getNeighborRoads().get(1))) {
				isEntrance = true;
				return true;
			} else {
				ArrayList<Area> sameNeighbors = Utils.sameNeighbors(getNeighborRoads().get(0), getNeighborRoads().get(1));
				if (sameNeighbors.size() == 1) {
					isEntrance = false;
					return false;
				}
				for (Area area : sameNeighbors) {
					if (SOSGeometryTools.haveIntersectionInOnePoint(this, area)) {
						isEntrance = true;
						return true;
					}
				}
				isEntrance = false;
				return false;
			}
			// --------------------------------
		} else if (getNeighborRoads().size() == 3) {
			for (int j = 0; j < getNeighborRoads().size(); j++) {
				if ((sos.base.util.Utils.areNeighbour(getNeighborRoads().get(j), getNeighborRoads().get((j + 2) % 3))) && (sos.base.util.Utils.areNeighbour(getNeighborRoads().get(j), getNeighborRoads().get((j + 1) % 3)))) {
					isEntrance = true;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns only neighbors of the current road which are road themselves
	 *
	 * @return ArrayList<Road>
	 * @author Salim
	 */
	private ArrayList<Road> getNeighborRoads() {
		ArrayList<Road> neighborRoads = new ArrayList<Road>();// Salim
		for (Area a : getNeighbours()) {
			if (a instanceof Road) {
				neighborRoads.add((Road) a);
			}
		}
		return neighborRoads;
	}

	// ***********************************************************************

	private ArrayList<Building> visibleBuilding = new ArrayList<Building>();

	public ArrayList<Building> visibleBuilding() {//YOOSEF
		return visibleBuilding;
	}
	/* ////////////////////End of S.O.S/////////////////// */

}