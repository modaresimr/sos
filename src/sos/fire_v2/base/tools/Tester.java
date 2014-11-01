package sos.fire_v2.base.tools;


public class Tester {
	/*
	FireBrigadeAgent agent;
	FireDisasterSpace world;
	boolean suburb = false;
	boolean sensibleArea = false;
	boolean sensibleAreasOfRoads = false;
	boolean extinguishableAreas = false;
	boolean neighbors = false;
	boolean buildingBlock = false;
	boolean islands = false;
	boolean buildingBlockCoverBuildings = true;
	boolean islandCoverBuildings = false;
	boolean FZ = false;
	boolean allSensable = false;

	public Tester(FireBrigadeAgent agent) {
		this.agent = agent;
		this.world = agent.model();
	}

	public void checkBuildingsToUpdate(ArrayList<Building> Bs) {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		List<Edge> list0 = new ArrayList<Edge>();
		for (Building b : Bs) {
			list0.addAll(b.getEdges());
		}
		testList.add(list0);
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	public void checkEdges(ArrayList<Edge> edges) {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		testList.add(edges);
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checkSuburbBuildings() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		List<Edge> list0 = new ArrayList<Edge>();
		List<Edge> list1 = new ArrayList<Edge>();
		List<Edge> list2 = new ArrayList<Edge>();
		for (FireBuilding b : world.getFireBuildings()) {
			if (b.isInSuburb() && b.numberOfSuburbDirections() == 0) {
				list0.addAll(b.building().getEdges());
			} else if (b.isInSuburb() && b.numberOfSuburbDirections() == 1) {
				list1.addAll(b.building().getEdges());
			} else if (b.isInSuburb())
				list2.addAll(b.building().getEdges());
		}
		testList.add(list0);
		testList.add(list1);
		testList.add(list2);
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checkBuildingBlocks() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (BuildingBlock rs : world.buildingBlocks()) {
			List<Edge> list = new ArrayList<Edge>();
			for (Building b : rs.buildings()) {
				list.addAll(b.getEdges());
			}
			testList.add(list);
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checkBuildingBlocksCoverBuildings() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (BuildingBlock bb : world.buildingBlocks()) {
			List<Edge> list = new ArrayList<Edge>();
			for (Building b : bb.insideCoverBuildings()) {
				list.addAll(b.getEdges());
			}
			testList.add(list);
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checkIslandsCoverBuildings() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (BuildingBlock bb : world.islands()) {
			List<Edge> list = new ArrayList<Edge>();
			for (Building b : bb.insideCoverBuildings()) {
				list.addAll(b.getEdges());
			}
			testList.add(list);
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checkFireZones() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		for (FireZone fz : world.fireZones()) {
			List<Edge> e = new ArrayList<Edge>();
			for (Building b : fz.buildings()) {
				e.addAll(b.getEdges());
			}
			testList.add(e);
		}

		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checksensibleAreasOfRoads() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		// for (FireBuilding b : agent.Fworld.FBuildings()) {
		FireBuilding b = world.getFireBuildings().get(agent.model().time() - 3);
		List<Edge> e = new ArrayList<Edge>();
		for (SOSArea a : b.building().fireSearchBuilding().sensibleAreasOfRoads()) {
			e.addAll(a.getEdges());
		}
		testList.add(e);
		testList.add(b.building().getEdges());
		// }
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checksensibleAreas() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		// for (FireBuilding b : agent.Fworld.FBuildings()) {
		FireBuilding b = agent.model().getFireBuildings().get(agent.model().time() - 3);
		List<Edge> e = new ArrayList<Edge>();
		e.addAll(b.building().fireSearchBuilding().sensibleArea().getEdges());
		testList.add(b.building().getEdges());
		testList.add(e);
		// }
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checkAllSensable() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		// for (FireBuilding b : agent.Fworld.FBuildings()) {
		FireBuilding b = agent.model().getFireBuildings().get(agent.model().time() - 3);
		List<Edge> e = new ArrayList<Edge>();
		for (SOSArea a : b.building().fireSearchBuilding().sensibleAreasOfRoads()) {
			e.addAll(a.getEdges());
		}
		testList.add(b.building().fireSearchBuilding().sensibleArea().getEdges());
		testList.add(e);
		testList.add(b.building().getEdges());
		// }
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	private void checkIslands() {
		ArrayList<List<Edge>> testList = new ArrayList<List<Edge>>();
		List<Edge> e;
		for (BuildingBlock rs : world.islands()) {
			e = new ArrayList<Edge>();
			for (Building b : rs.buildings()) {
				e.addAll(b.getEdges());
			}
			testList.add(e);
		}
		if (agent.getWorldModelViewer() != null && agent.getWorldModelViewer().viewer != null)
			agent.getWorldModelViewer().viewer.reachEdgesLayer.paintThisEdges(testList);
	}

	public void test() {
		try {
			if (suburb)
				checkSuburbBuildings();
			if (buildingBlock)
				checkBuildingBlocks();
			if (buildingBlockCoverBuildings)
				checkBuildingBlocksCoverBuildings();
			if (islandCoverBuildings)
				checkIslandsCoverBuildings();
			if (FZ)
				checkFireZones();
			if (sensibleArea)
				checksensibleAreas();
			if (sensibleAreasOfRoads)
				checksensibleAreasOfRoads();
			if (allSensable)
				checkAllSensable();
			if (islands)
				checkIslands();
		} catch (Exception e) {
		}
	}
	*/
}
