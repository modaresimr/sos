package sos.base.precompute.precomputes;

import java.util.ArrayList;
import java.util.HashMap;

import sos.base.entities.Building;
import sos.base.precompute.AbstractPreCompute;
import sos.base.precompute.PreComputeFile;
import sos.base.util.FireSearchBuilding;
import sos.base.util.geom.ShapeInArea;

public class FireSearchBuildingsPreCompute extends AbstractPreCompute{
	@Override
	public void compute() {
		setNewFireSearchBuilding();
		setSensibleAreasOfRoads();
	}
	
	/** @author Ali
	 * @param model *******************************************************/
	public void setSensibleAreasOfRoads() {
		for (Building b : model().buildings())
			b.fireSearchBuilding().setSensibleAreasOfAreas();
	}


	private void setNewFireSearchBuilding(){
		for (Building b : model().buildings())
			b.setFireSearchBuilding(new FireSearchBuilding(b));
	}
		
	@Override
	protected void setFromFile(PreComputeFile content) {
		setNewFireSearchBuilding();
		SensibleAreaPreComputeFile firesearchContent = ((SensibleAreaPreComputeFile)content);
		for (Building building : model().buildings()) {
			building.fireSearchBuilding().setSensibleAreasOfRoads(firesearchContent.getSensibleAreas(building));
		}
	}
	
	@Override
	protected PreComputeFile getPreComputeFileContent() {
		SensibleAreaPreComputeFile sensibleContent =new SensibleAreaPreComputeFile();
		for (Building building : model().buildings()) {
			sensibleContent.setSensibleAreas(building,building.fireSearchBuilding().sensibleAreasOfAreas());
		}
		return sensibleContent;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	private static class SensibleAreaPreComputeFile implements PreComputeFile{
		private static final long serialVersionUID = -3600732519302083892L;
		private HashMap<Short, ArrayList<ShapeInArea>> building_SensibleAreas=new HashMap<Short, ArrayList<ShapeInArea>>(); 
		
		@Override
		public boolean isValid() {
			return model().buildings().size()==building_SensibleAreas.size();
		}

		public void setSensibleAreas(Building building, ArrayList<ShapeInArea> sensibleAreasOfAreas) {
			building_SensibleAreas.put(building.getBuildingIndex(),sensibleAreasOfAreas);
		}

		public ArrayList<ShapeInArea> getSensibleAreas(Building building) {
			return building_SensibleAreas.get(building.getBuildingIndex());
		}
	}
	
}
