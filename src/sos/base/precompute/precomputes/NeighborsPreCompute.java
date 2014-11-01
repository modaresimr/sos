package sos.base.precompute.precomputes;

import java.util.HashMap;

import sos.base.SOSAgent;
import sos.base.SOSConstant;
import sos.base.entities.Building;
import sos.base.precompute.AbstractPreCompute;
import sos.base.precompute.PreComputeFile;
import sos.base.sosFireZone.util.Rnd;
import sos.base.sosFireZone.util.Wall;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

public class NeighborsPreCompute extends AbstractPreCompute {
	private SOSLoggerSystem nl = new SOSLoggerSystem(null, "FindingNeighborsLog", true, OutputType.File);

	@Override
	protected void compute() {
		initializeWalls();
		initializeBuildings();
	}

	public void initializeWalls() {
		Rnd.setSeed(23);
		//		Wall.debug.setBackgroundEntities(Color.gray,sosAgent.model().buildings());
		for (Building b : model().buildings()) {
			b.initializeWalls(model());
		}
		if (!SOSConstant.IS_CHALLENGE_RUNNING) {
			nl("All Walls are set ... see Building log .. ");
			nl("Walls >> ");
			for (Building b : model().buildings()) {
				nl(b + "number of walls " + b.walls().size());
				for (Wall w : b.walls()) {
					nl("> " + w);
				}
			}
		}
	}

	// Morteza2011***********************************************************************************************************************
	/** setting buildings connections *A2 */
	public void initializeBuildings() {

		if (!SOSConstant.IS_CHALLENGE_RUNNING)
			nl("Computing Neeighbors and ray values ....");
		initRayValues();
		for (Building b : model().buildings()) {
			b.setRealNeighbors();
			if (!SOSConstant.IS_CHALLENGE_RUNNING) {
				nl("Source Building: " + b + "====" + b.realNeighbors_Building());
			}
			b.freeResources();
		}
		//
		for (Building b : model().buildings()) {//Yoosef 
			for (Building n : b.neighbors_Building()) {
				if (!n.neighbors_Building().contains(b)) {
					n.neighbors_Building().add(b);
					n.neighbors_BuildValue().put(b.getBuildingIndex(), b.getNeighValue(n));
				}
			}
			for (Building n : b.realNeighbors_Building()) {
				if (!n.realNeighbors_Building().contains(b)) {
					n.realNeighbors_Building().add(b);
					n.real_neighbors_BuildValue().put(b.getBuildingIndex(), b.getNeighValue(n));
				}
			}

		}

		if (!SOSConstant.IS_CHALLENGE_RUNNING)
			nl("End of Neighbor computation ... ");

	}

	// Morteza2011***********************************************************************************************************************
	/** calculating them with a lower precision * A2 */
	public void initRayValues() {
		//		long start = System.currentTimeMillis();
		for (Building b : model().buildings()) {
			//			Wall.d=10000;
			//			Wall.count=1;
			b.initWallValues();

		}
		//		System.out.println("tm1"+	Wall.tm1);
		//		System.out.println("tm2"+Wall.tm2);
		//		System.out.println(Wall.d/Wall.count);
		//		nl.consoleInfo("initRayValues got:" + (System.currentTimeMillis() - start) + "ms");
	}

	// Morteza2011***********************************************************************************************************************
	public void nl(String s) {
		nl.logln(s);
	}

	@Override
	protected PreComputeFile getPreComputeFileContent() {
		NeighborPreComputeFile neighborContent = new NeighborPreComputeFile();
		for (Building b : model().buildings()) {
			neighborContent.setNeighbors(b);
		}
		return neighborContent;
	}

	@Override
	protected void setFromFile(PreComputeFile content) {
		NeighborPreComputeFile neighborContent = (NeighborPreComputeFile) content;
		for (Building b : model().buildings()) {
			b.setRayNeighbors(neighborContent.getNeighbors(b));
		}
		for (Building b : model().buildings()) {//Yoosef 
			for (Building n : b.neighbors_Building()) {
				if (!n.neighbors_Building().contains(b)) {
					n.neighbors_Building().add(b);
					n.neighbors_BuildValue().put(b.getBuildingIndex(), b.getNeighValue(n));
				}
			}
			for (Building n : b.realNeighbors_Building()) {
				if (!n.realNeighbors_Building().contains(b)) {
					n.realNeighbors_Building().add(b);
					n.real_neighbors_BuildValue().put(b.getBuildingIndex(), b.getNeighValue(n));
				}
			}

		}

	}

	private static class NeighborPreComputeFile implements PreComputeFile {
		private static final long serialVersionUID = -514152965451439346L;
		HashMap<Short, HashMap<Short, Float>> building_neighbor = new HashMap<Short, HashMap<Short, Float>>();

		public void setNeighbors(Building b) {
			building_neighbor.put(b.getBuildingIndex(), b.neighbors_BuildValue());
		}

		public HashMap<Short, Float> getNeighbors(Building b) {
			return building_neighbor.get(b.getBuildingIndex());
		}

		@Override
		public boolean isValid() {
			return building_neighbor.keySet().size() == SOSAgent.currentAgent().model().buildings().size();
		}
	}

}
