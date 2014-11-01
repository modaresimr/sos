package sos.fire_v2.base.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.util.mapRecognition.MapRecognition.MapName;
import sos.fire_v2.base.worldmodel.FirePrecompute.Mode;
import sos.fire_v2.base.worldmodel.FireWorldModel;

public class BuildingBlock {
	private SOSWorldModel world;
	private ArrayList<Building> buildings = new ArrayList<Building>();
	private HashSet<Building> insideCoverBuildings = new HashSet<Building>();
	private HashSet<Building> outsideCoverBuildings = new HashSet<Building>();
	private ArrayList<BuildingBlock> neighbors = new ArrayList<BuildingBlock>();
	private int area = 0;
	private boolean isBurning;
	public float fieryArea = 0;
	public int fieryBuildingsNumber = 0;
	private int priority = 0;
	public boolean preExtinguished = false;
	public int ID = 0;
	private int addableDistance = 7000;
	private boolean sideIsland = false;
	private boolean importance;

	// Morteza2011***********************************************************************************************************************
	public BuildingBlock(SOSWorldModel world) {
		this.world = world;
		if (world instanceof FireWorldModel) {
			if (world.sosAgent().getMapInfo().getRealMapName() == MapName.VC || world.sosAgent().getMapInfo().getRealMapName() == MapName.Kobe)
				addableDistance = 7000;
			else if (world.sosAgent().getMapInfo().getRealMapName() == MapName.Berlin)
				addableDistance = 25000;
			else
				addableDistance = 15000;
		}
	}

	/** @author nima */
	public ArrayList<Building> makeByRoad(Mode mode, ArrayList<Building> allBuildings) {
		if (mode == Mode.Compute) {
			Building b = allBuildings.get(0);
			boolean mark[] = new boolean[world.buildings().size() + 10];
			Queue<Building> uncheckedNeighbors = new LinkedList<Building>();

			buildings.add(b);
			mark[b.getBuildingIndex()] = true;
			for (Building n : b.realNeighbors_Building()) {
				if (addableForRoad(b, n)) {
					uncheckedNeighbors.add(n);
				}
			}
			while (uncheckedNeighbors.size() > 0) {
				Building bu = uncheckedNeighbors.poll();
				if (mark[bu.getBuildingIndex()])
					continue;
				mark[bu.getBuildingIndex()] = true;
				buildings.add(bu);
				for (Building n : bu.realNeighbors_Building()) {
					if (!mark[n.getBuildingIndex()] && addableForRoad(bu, n)) {
						uncheckedNeighbors.add(n);
					}
				}
			}
			allBuildings.removeAll(buildings);
		} else if (mode == Mode.ReadFromFile) {
			buildings = allBuildings;
		}

		setBuildingBlockForBuildings();
		setBuildingBlockInsideCover();
		setBuildingBlockOutsideCover();
		setArea();
		return allBuildings;
	}

	/** @author nima */
	public ArrayList<Building> makeByDistance(Mode mode, ArrayList<Building> allBuildings) {
		if (mode == Mode.Compute) {
			Building b = allBuildings.get(0);
			boolean mark[] = new boolean[world.buildings().size() + 10];
			Queue<Building> uncheckedNeighbors = new LinkedList<Building>();

			buildings.add(b);
			mark[b.getBuildingIndex()] = true;
			for (Building n : b.realNeighbors_Building()) {
				if (addableForDistance(b, n)) {
					uncheckedNeighbors.add(n);
				}
			}
			while (uncheckedNeighbors.size() > 0) {
				Building bu = uncheckedNeighbors.poll();
				if (mark[bu.getBuildingIndex()])
					continue;
				mark[bu.getBuildingIndex()] = true;
				buildings.add(bu);
				for (Building n : bu.realNeighbors_Building()) {
					if (mark[n.getBuildingIndex()] == false && addableForDistance(bu, n)) {
						uncheckedNeighbors.add(n);
					}
				}
			}
			allBuildings.removeAll(buildings);
		} else if (mode == Mode.ReadFromFile) {
			buildings = allBuildings;
		}
		if (this.world instanceof FireWorldModel) {
			setIslandForBuildings();
			setIslandInsideCover();
			setIslandOutsideCover();
		}
		setArea();
		return allBuildings;
	}

	// Morteza2011***********************************************************************************************************************
	public ArrayList<Building> buildings() {
		return buildings;
	}

	// Morteza2011***********************************************************************************************************************
	private void setIslandForBuildings() {
		ID = Integer.MAX_VALUE;
		for (Building b : this.buildings) {
			if (b.getID().getValue() < ID)
				ID = b.getID().getValue();
			if (this.world instanceof FireWorldModel)
				b.getFireBuilding().setIsland(this);
		}
	}

	// Morteza2011***********************************************************************************************************************
	public void setBuildingBlockForBuildings() {
		ID = Integer.MAX_VALUE;
		for (Building b : this.buildings) {
			if (b.getID().getValue() < ID)
				ID = b.getID().getValue();
			b.getFireBuilding().setBuildingBlock(this);
		}
	}

	// Morteza2011***********************************************************************************************************************
	private void setArea() {
		for (Building b : buildings()) {
			area += b.getTotalArea();
		}
	}

	// Morteza2011***********************************************************************************************************************
	public int area() {
		return area;
	}

	// Morteza2011***********************************************************************************************************************
	public boolean addableForRoad(Building b, Building n) {
		if (!b.getFireBuilding().hasRoadBetweenThisAnd(n))
			return true;
		return false;
	}

	// Morteza2011***********************************************************************************************************************
	public boolean addableForDistance(Building b, Building n) {
		if (b.getWallDistanceTo(n) < addableDistance)
			return true;
		return false;
	}

	// Morteza2011***********************************************************************************************************************
	private void setBuildingBlockInsideCover() {
		insideCoverBuildings.clear();
		for (Building b : this.buildings) {
			if (b.getFireBuilding().hasANeighborThatAreIntersectedWithARoad()) {
				if (!insideCoverBuildings.contains(b))
					this.insideCoverBuildings.add(b);
			}
		}
	}

	// Morteza2011***********************************************************************************************************************
	private void setIslandInsideCover() {
		insideCoverBuildings.clear();
		for (Building b : this.buildings) {
			for (Building nn : b.realNeighbors_Building()) {
				FireBuilding n = nn.getFireBuilding();
				if (n.island() != this)
					if (!insideCoverBuildings.contains(b)) {
						this.insideCoverBuildings.add(b);
						b.setIsLandOutSide(true);//Yoosef
						break;
					}
			}
		}
	}

	// Morteza2011***********************************************************************************************************************
	public HashSet<Building> insideCoverBuildings() {
		return insideCoverBuildings;
	}

	// Morteza2011***********************************************************************************************************************
	private void setBuildingBlockOutsideCover() {
		for (Building b : this.buildings) {
			for (Building n : b.realNeighbors_Building()) {
				if (!buildings.contains(n) && !outsideCoverBuildings.contains(n)) {
					outsideCoverBuildings.add(n);
				}
			}
		}
	}

	// Morteza2011***********************************************************************************************************************
	private void setIslandOutsideCover() {
		for (Building b : this.buildings) {
			for (Building n : b.realNeighbors_Building()) {
				if (!buildings.contains(n) && !outsideCoverBuildings.contains(n)) {
					outsideCoverBuildings.add(n);
				}
			}
		}
	}

	// Morteza2011***********************************************************************************************************************
	public HashSet<Building> outsideCoverBuildings() {
		return outsideCoverBuildings;
	}

	// Morteza2011***********************************************************************************************************************
	public void setBuildingBlockNeighbors() {
		for (Building b : this.buildings) {
			for (Building n : b.realNeighbors_Building()) {
				if (n.getFireBuilding().buildingBlock() != b.getFireBuilding().buildingBlock()) {
					if (!neighbors.contains(n.getFireBuilding().buildingBlock()))
						this.neighbors.add(n.getFireBuilding().buildingBlock());
				}
			}
		}
	}

	// Morteza2011************************************
	public void setIslandNeighbors() {
		for (Building b : this.buildings) {
			for (Building n : b.realNeighbors_Building()) {
				if (n.getFireBuilding().island() != b.getFireBuilding().island()) {
					if (!neighbors.contains(n.getFireBuilding().island()))
						this.neighbors.add(n.getFireBuilding().island());
				}
			}
		}
	}

	// Morteza2011***********************************************************************************************************************
	public ArrayList<BuildingBlock> neighbors() {
		return neighbors;
	}

	// Morteza2011***********************************************************************************************************************
	private boolean updateBurning() {
		fieryArea = 0;
		fieryBuildingsNumber = 0;
		for (Building b : this.buildings()) {
			if (b.virtualData[0].isBurning() || b.virtualData[0].getFieryness() == 8) {
				fieryArea += b.getTotalArea();
				fieryBuildingsNumber++;
			}
		}
		if (((fieryArea / area()) > 0.6)) {
			return (isBurning = true);
		} else if (fieryBuildingsNumber / buildings().size() >= 0.6) {
			return (isBurning = true);
		} else {
			return (isBurning = false);
		}
	}

	// Morteza2011***********************************************************************************************************************
	public boolean isBurning() {
		return isBurning;
	}

	// Morteza2011***********************************************************************************************************************
	public void update() {
		updateBurning();
	}

	// Morteza2011***********************************************************************************************************************
	public boolean isFireNewInBuildingBlock() {
		// TODO Hesam 002: Should be Checked
		if (((double)(fieryBuildingsNumber)/(double)(buildings).size()) < 0.3)
			return true;
		if (fieryArea / area() < 0.2)
			return true;
		return false;
	}

	// Morteza2011***********************************************************************************************************************
	public boolean isFireNewInIsland() {
		// TODO Hesam 002: Should be Checked :::> Yoosef Checked
		//		num <= 5 || num * 100 / bb.buildings().size() < 12 && num <= 8
		if (fieryBuildingsNumber <= 5 && buildings.size() >= 20)
			return true;
		if (fieryArea / area() < 0.2)
			return true;
		return false;
	}

	// Morteza2011***********************************************************************************************************************
	public void resetPriority() {
		this.priority = 0;
	}

	// Morteza2011***********************************************************************************************************************
	public void addPriority(int priority) {
		this.priority += priority;
	}

	// Morteza2011***********************************************************************************************************************
	public int priority() {
		return priority;
	}

	// Morteza2011***********************************************************************************************************************
	@Override
	public String toString() {
		return "BuildingBlock: " + ID;
	}

	/**
	 * @author MasHouD
	 */
	public boolean isSideIsland() {
		return sideIsland;
	}

	/**
	 * @author MasHouD
	 */
	public void setSideIsland(boolean sideIsland) {
		this.sideIsland = sideIsland;
	}

	public void setImportanceI() {
		int num = 0;
		for (Building b : buildings) {
			if (!b.isMapSide()) {
				num++;
			}
		}
		if (num * 100 / buildings.size() > 40 && buildings.size() > 20)
			importance = true;
		else
			importance = false;
	}

	public void setImportanceB() {
		int num = 0;
		for (Building b : buildings) {
			if (!b.isMapSide()) {
				num++;
			}
		}
		if (num * 100 / buildings.size() > 50 && buildings.size() > 7)
			importance = true;
		else
			importance = false;
	}

	public boolean isImportant() {
		return importance;
	}
}
