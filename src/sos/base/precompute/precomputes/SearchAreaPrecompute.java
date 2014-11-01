package sos.base.precompute.precomputes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import sos.base.entities.Area;
import sos.base.entities.Building;
import sos.base.precompute.AbstractPreCompute;
import sos.base.precompute.PreComputeFile;
import sos.base.util.SOSGeometryTools;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.geom.ShapeInArea;
import sos.police_v2.PoliceConstants;

public class SearchAreaPrecompute extends AbstractPreCompute {

	@Override
	protected void compute() {
		for (Building b : model().buildings()) {
			try {
				ArrayList<Area> roadInSight = b.getRoadsInSight();
				b.setSearchAreas(b.findSightArea(roadInSight));
				if (b.getSearchAreas().isEmpty()) {
					java.awt.geom.Area shape = new java.awt.geom.Area(model().sosAgent().lineOfSightPerception.findVisibleShape(b));

					Collection<Area> ve = model().getObjectsInRectangle(shape.getBounds(), Area.class);
					for (Area area : ve) {
						java.awt.geom.Area f = new java.awt.geom.Area(area.getShape());
						f.intersect(shape);
						List<java.awt.geom.Area> a = AliGeometryTools.fix(f);
						for (java.awt.geom.Area area2 : a) {
							int[] apx = AliGeometryTools.getApexes(area2);
							if (apx.length >= 6 && (SOSGeometryTools.computeArea(apx) > 100000 || area instanceof Building && SOSGeometryTools.computeArea(apx) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)) {
								ShapeInArea s = new ShapeInArea(b.getApexList(), b);
								if (s.isValid())
									b.getSearchAreas().add(s);
							}
						}
					}
				}
				if (b.getSearchAreas().isEmpty()) {
					model().sosAgent().sosLogger.warn("search area is empty");
					b.getSearchAreas().add(new ShapeInArea(b.getApexList(), b));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}

	@Override
	protected PreComputeFile getPreComputeFileContent() {
		SearchAreaPreComputeFile content = new SearchAreaPreComputeFile();
		for (Building b : model().buildings()) {
			content.setSearchAreas(b,b.getSearchAreas());
		}
		return content;
	}

	@Override
	protected void setFromFile(PreComputeFile content) {
		SearchAreaPreComputeFile searchContent = (SearchAreaPreComputeFile) content;
		for (Building b : model().buildings()) {
			b.setSearchAreas(searchContent.getSearchAreas(b));
		}	
	}

	private static class SearchAreaPreComputeFile implements PreComputeFile{
		private static final long serialVersionUID = 530985347690949532L;
		private HashMap<Short, ArrayList<ShapeInArea>> building_SearchAreas=new HashMap<Short, ArrayList<ShapeInArea>>(); 
		
		@Override
		public boolean isValid() {
			return model().buildings().size()==building_SearchAreas.size();
		}

		public void setSearchAreas(Building building, ArrayList<ShapeInArea> sensibleAreasOfAreas) {
			building_SearchAreas.put(building.getBuildingIndex(),sensibleAreasOfAreas);
		}

		public ArrayList<ShapeInArea> getSearchAreas(Building building) {
			return building_SearchAreas.get(building.getBuildingIndex());
		}
		
	}
}
