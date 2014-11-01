package sos.base.precompute.precomputes;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;

import sos.base.SOSAgent;
import sos.base.entities.Road;
import sos.base.precompute.AbstractPreCompute;
import sos.base.precompute.PreComputeFile;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.blockadeEstimator.BlockadeEstimator;
import sos.base.util.blockadeEstimator.SOSBlockade;
import sos.base.util.geom.SOSShape;

public class MiddleBlockadePreCompute extends AbstractPreCompute {

	@Override
	protected void compute() {
		agent().blockadeEstimator = new BlockadeEstimator(model());
		agent().blockadeEstimator.preCompute();
	}

	@Override
	protected PreComputeFile getPreComputeFileContent() {
		PreComputeBlockadeEstimator preComputeContent = new PreComputeBlockadeEstimator();
		for (Road road : model().roads()) {
			ArrayList<Shape> blockadeShapes = new ArrayList<Shape>();
			for (SOSBlockade middleBlockade : road.getMiddleBlockades()) {
				blockadeShapes.add(middleBlockade.getShape());
			}
			preComputeContent.setRoadBlockades(road, blockadeShapes);
		}

		return preComputeContent;
	}

	@Override
	protected void setFromFile(PreComputeFile content) {
		PreComputeBlockadeEstimator preComputeContent = (PreComputeBlockadeEstimator) content;
		for (Road road : model().roads()) {
			ArrayList<Shape> blockadeShapes = preComputeContent.getRoadBlockades(road);
			for (Shape shape : blockadeShapes) {
				int[] apexes;
				if (shape instanceof SOSShape)
					apexes = ((SOSShape) shape).getApexes();
				else
					apexes = AliGeometryTools.getApexes(shape);

				model().block.newMiddleBlockade(new SOSBlockade(apexes, road));
			}
		}
	}

	private static class PreComputeBlockadeEstimator implements PreComputeFile {
		private static final long serialVersionUID = -8662974518041692828L;

		HashMap<Short, ArrayList<Shape>> all = new HashMap<Short, ArrayList<Shape>>();

		public ArrayList<Shape> getRoadBlockades(Road road) {
			return all.get(road.getRoadIndex());
		}

		public void setRoadBlockades(Road road, ArrayList<Shape> road_blockades) {
			all.put(road.getRoadIndex(), road_blockades);
		}

		@Override
		public boolean isValid() {
			return all.keySet().size() == SOSAgent.currentAgent().model().roads().size();
		}

	}

}
