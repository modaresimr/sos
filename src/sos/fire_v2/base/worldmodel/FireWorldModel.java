package sos.fire_v2.base.worldmodel;

import java.awt.Shape;
import java.util.ArrayList;

import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.entities.Center;
import sos.base.entities.StandardEntity;
import sos.base.sosFireZone.util.ConvexHull_arr_New;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.tools.BuildingBlock;
import sos.fire_v2.base.tools.FireBuilding;

public class FireWorldModel extends SOSWorldModel {
	public enum SystemType {
		World, Agent, Building, FireZone, BuildingBlock
	}

	private Shape mapInBound;
	ConvexHull_arr_New convex = null;

	private FireBrigadeAgent owner;
	private ArrayList<FireBuilding> fireBuildings;
	private ArrayList<BuildingBlock> buildingBlocks = new ArrayList<BuildingBlock>();
	private ArrayList<BuildingBlock> island = new ArrayList<BuildingBlock>();
	public SOSLoggerSystem wl = null;
	public SOSLoggerSystem al = null;
	public SOSLoggerSystem fzl = null;
	public SOSLoggerSystem bl = null;

	private int avarageAreaOfBuildings = 0;
	private double mapArea = 0;
	private double burntOrBurningArea = 0;

	/** Indicates that {@link Center}s are flammable or not */
	private boolean centerInflammable = true; // Hesam 002
	private boolean centerChecked = false; // Hesam 002
	private int bigBuildingArea=0;
	// Morteza2011**************************************************************************************************************
	public FireWorldModel(SOSAgent<? extends StandardEntity> sosAgent) {
		super(sosAgent);
	}

	@Override
	public void precompute() {
		super.precompute();
		setBigBuildingArea();
	
	}
	public int getBigBuildingArea() {
		return bigBuildingArea;
	}
	private void setBigBuildingArea(){
		double sumOfarea=0;
		double ave;
		for (Building b:buildings()){
			sumOfarea+=b.getTotalArea();
		}
		ave=(sumOfarea/buildings().size());
		bigBuildingArea= (int) (2*ave);
		
	}
	// Morteza2011**************************************************************************************************************
	public void setOwner(FireBrigadeAgent fb) {
		owner = fb;
	}

	// Morteza2011**************************************************************************************************************
	public FireBrigadeAgent owner() {
		return owner;
	}

	// Morteza2011**************************************************************************************************************
	public void setFireBuildings(ArrayList<FireBuilding> fbuildings) {
		fireBuildings = fbuildings;
	}

	// Morteza2011**************************************************************************************************************
	public ArrayList<FireBuilding> getFireBuildings() {
		return fireBuildings;
	}

	// Morteza2011**************************************************************************************************************
	public ArrayList<BuildingBlock> buildingBlocks() {
		return buildingBlocks;
	}

	/** @author Nima */
	public ArrayList<BuildingBlock> islands() {
		return island;
	}

	// Morteza2011**************************************************************************************************************
	public void updateBuildingBlocks() {
		for (BuildingBlock bb : buildingBlocks) {
			bb.update();
		}
	}

	// Morteza2011**************************************************************************************************************
	public void updateIslands() {
		for (BuildingBlock bb : island) {
			bb.update();
		}
	}

	// Morteza2011**************************************************************************************************************
	public void al(String s) {
		al.logln(s);
	}

	// Morteza2011**************************************************************************************************************
	public void setAvarageAreaOfBuildings(int avarageAreaOfBuildings) {
		this.avarageAreaOfBuildings = avarageAreaOfBuildings;
	}

	// Morteza2011**************************************************************************************************************
	public int avarageAreaOfBuildings() {
		return avarageAreaOfBuildings;
	}

	/**
	 * @author Hesam 002
	 * @param centerInflammable
	 *            the centerInflammable to set
	 */
	public void setCenterInflammable(boolean centerInflammable) {
		this.centerInflammable = centerInflammable;
	}

	/**
	 * @author Hesam 002
	 * @return the centerInflammable
	 */
	public boolean isCenterInflammable() {
		return centerInflammable;
	}

	/**
	 * @author Hesam 002
	 * @param centerChecked
	 *            the centerChecked to set
	 */
	public void setCenterChecked(boolean centerChecked) {
		this.centerChecked = centerChecked;
	}

	/**
	 * @author Hesam 002
	 * @return the centerChecked
	 */
	public boolean isCenterChecked() {
		return centerChecked;
	}

	public void setMapArea(double mapArea) {
		this.mapArea = mapArea;
	}

	public double mapArea() {
		return mapArea;
	}

	public void setBurntOrBurningArea(double burntOrBurningArea) {
		this.burntOrBurningArea = burntOrBurningArea;
	}

	public double burntOrBurningArea() {
		return burntOrBurningArea;
	}

	/***********************************************************************************************/
	/***************************************           **************************************************/
	/*********************************** MasHouD **********************************************/
	/****************************************      ***************************************************/
	/**********************************************************************************************/
	private FireBrigadeAgent me;

	public void setMe(FireBrigadeAgent me) {
		this.me = me;
	}
	public FireBrigadeAgent getMe() {
		return me;
	}

	public Shape getInnerOfMap() {
		return mapInBound;
	}

	public void setMapSides() {
		mapInBound = null;
		if (convex == null)
			setConvex();
		mapInBound = convex.getScaleConvex(0.7f).getShape();
		for(Building b:buildings()){
			if(!mapInBound.contains(b.getX(),b.getY())){
				b.setMapSide(true);
			}
		}
		mapInBound = convex.getScaleConvex(0.7f).getShape();
	}

	public ConvexHull_arr_New getConvex() {
		return convex;
	}

	public void setConvex() {
		convex = new ConvexHull_arr_New(buildings());

	}

	public void setSideIslands() {
		for (BuildingBlock island : islands()) {
			island.setSideIsland(false);
			for (Building b : island.insideCoverBuildings()) {
				if (b.isMapSide()) {
					island.setSideIsland(true);
					break;
				}
			}
		}
	}

}