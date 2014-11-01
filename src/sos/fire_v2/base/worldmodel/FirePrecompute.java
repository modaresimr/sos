package sos.fire_v2.base.worldmodel;

import java.util.ArrayList;

import sos.base.entities.Building;
import sos.base.entities.Road;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.tools.BuildingBlock;
import sos.fire_v2.base.tools.FireBuilding;
import sos.fire_v2.base.worldmodel.FireWorldModel.SystemType;

public class FirePrecompute {

	public enum Mode {
		Compute, ReadFromFile
	};

	private FireWorldModel world;
	private FireBrigadeAgent owner;

	// Morteza2011************************************************************************************************************
	public FirePrecompute(FireWorldModel world) {
		this.world = world;
		this.owner = world.owner();
	}

	/** @author Hesam 002 & Morteza2011 & nima */
	public void execute() {
		long FPTimeS = System.currentTimeMillis();
		world.al = new SOSLoggerSystem(owner.me(), "Agent/FireBrigade/" + SystemType.Agent.toString(), true, OutputType.File, true);
		world.al.consoleInfo("Start of Fire PreCompute...");
		long startTime = System.currentTimeMillis();
		world.wl = new SOSLoggerSystem(owner.me(), "Agent/FireBrigade/" + SystemType.World.toString(), true, OutputType.File, true);
		world.fzl = new SOSLoggerSystem(owner.me(), "Agent/FireBrigade/" + SystemType.FireZone.toString(), true, OutputType.File, true);
		world.bl = new SOSLoggerSystem(owner.me(), "Agent/FireBrigade/" + SystemType.Building.toString(), true, OutputType.File, true);

		setFireBuildings();
		world.al.consoleInfo("setFireBuildings got: " + (System.currentTimeMillis() - startTime) + "ms");
		startTime = System.currentTimeMillis();
		setExtinguishableAreas();
		world.al.consoleInfo("setExtinguishableArea: " + (System.currentTimeMillis() - startTime) + "ms");
		startTime = System.currentTimeMillis();
		makeBuildingBlocks(); // nima
		world.al.consoleInfo("makeBuildingBlocks got: " + (System.currentTimeMillis() - startTime) + "ms");
		startTime = System.currentTimeMillis();
		makeIslands(); // nima
		world.al.consoleInfo("makeIslands got: " + (System.currentTimeMillis() - startTime) + "ms");
		startTime = System.currentTimeMillis();
		world.al.consoleInfo("Fire PreComput Time: " + (System.currentTimeMillis() - FPTimeS) + "ms ------------------------");
		setVisiblityForRoads();

	}

	private void setExtinguishableAreas() {

		for (Building b : world.buildings()) {
			b.getFireBuilding().getExtinguishableArea();
			//			b.getFireBuilding().getExtinguishableBuildingsDistanceShapeInArea();
			//			if (b.getFireBuilding().getExtinguishableArea().canExtinguish.isEmpty() && b.getFireBuilding().getExtinguishableRoadsDistanceShapeInArea().isEmpty()) {
			//				world.al.error("why it come here????" + b + " both ExtinguishableBuildings and ExtinguishableRoads are empty");
			//			}
		}
	}

	private void setVisiblityForRoads() {
		for (Building b : world.buildings()) {
			
			for (ShapeInArea a : b.fireSearchBuilding().sensibleAreasOfAreas())
				if (a.getArea(world) instanceof Road)
					((Road) a.getArea(world)).visibleBuilding().add(b);
		}

	}
	// Morteza2011**************************************************************************************************************
	private void setFireBuildings() {
		ArrayList<FireBuilding> fireBuildings = new ArrayList<FireBuilding>();
		for (Building b : world.buildings()) {
			FireBuilding fb = new FireBuilding(world, b);
			b.setFireBuilding(fb);
			fireBuildings.add(fb);
		}
		world.setFireBuildings(fireBuildings);
	}

	// Morteza2011**************************************************************************************************************
	private void makeBuildingBlocks() {
		ArrayList<Building> allBuildings = new ArrayList<Building>(world.buildings());
		while (allBuildings.size() > 0) {
			BuildingBlock bb = new BuildingBlock(world);
			allBuildings = bb.makeByRoad(Mode.Compute, allBuildings);
			if (bb.buildings().size() > 0)
				world.buildingBlocks().add(bb);
		}
		for (BuildingBlock bb : world.buildingBlocks())
			bb.setBuildingBlockNeighbors();
		for (BuildingBlock bb : world.buildingBlocks())
			bb.setImportanceB();
	}

	// Morteza2011**************************************************************************************************************
	private void makeIslands() {
		ArrayList<Building> allBuildings = new ArrayList<Building>(world.buildings());
		while (allBuildings.size() > 0) {
			BuildingBlock bb = new BuildingBlock(world);
			allBuildings = bb.makeByDistance(Mode.Compute, allBuildings);
			if (bb.buildings().size() > 0) {
				world.islands().add(bb);
			}
		}
		for (BuildingBlock bb : world.islands())
			bb.setIslandNeighbors();
		for (BuildingBlock bb : world.islands())
			bb.setImportanceI();
	}

}
