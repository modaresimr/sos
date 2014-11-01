package sos.fire_v2.base.tools;

import java.awt.geom.Area;
import java.util.ArrayList;

import sos.base.entities.Building;
import sos.base.entities.Road;
import sos.base.move.MoveConstants;
import sos.base.move.types.StandardMove;
import sos.base.util.IntList;
import sos.base.util.SOSGeometryTools;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.geom.RegularPolygon;
import sos.base.util.geom.ShapeInArea;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.worldmodel.FireWorldModel;
import sos.police_v2.PoliceConstants;

import com.infomatiq.jsi.Rectangle;

public class ExtinguishableArea {
	public static int NUMBER_OF_VERTICES = 18;
	private ArrayList<ShapeInArea> extinguishableRoadsDistanceShapeInArea = null;
	//	private ArrayList<Road> extinguishableRoads=new ArrayList<Road>();
	private ArrayList<ShapeInArea> extinguishableBuildingsDistanceShapeInArea = null;
	//	private ArrayList<Building> extinguishableBuildings=new ArrayList<Building>();
	private final Building building;
	public FireWorldModel world;

	public ExtinguishableArea(Building building, FireWorldModel world) {
		this.building = building;
		this.world = world;
		getBuildingsShapeInArea();
		getRoadsShapeInArea();
		checkValidity();
	}

	private void checkValidity() {
		for (ShapeInArea sia : getBuildingsShapeInArea()) {
			if (!sia.isValid())
				System.err.println("ERROR:" + sia.getErrorReason());
		}
		for (ShapeInArea sia : getRoadsShapeInArea()) {
			if (!sia.isValid())
				System.err.println("ERROR:" + sia.getErrorReason());
		}
	}

	public ArrayList<ShapeInArea> getBuildingsShapeInArea() {
		if (extinguishableBuildingsDistanceShapeInArea == null) {
			extinguishableBuildingsDistanceShapeInArea = new ArrayList<ShapeInArea>();
			RegularPolygon polygon = new RegularPolygon(building.getX(), building.getY(), FireBrigadeAgent.maxDistance, NUMBER_OF_VERTICES);
			Area polygonArea = new Area(polygon);
			int minX, maxX, minY, maxY;
			minX = (int) polygon.getBounds().getMinX();
			minY = (int) polygon.getBounds().getMinY();
			maxX = (int) polygon.getBounds().getMaxX();
			maxY = (int) polygon.getBounds().getMaxY();

			IntList bs = building.model().getBuildingIndexInRectangle(new Rectangle(minX, minY, maxX, maxY));
			for (int bu = 0; bu < bs.size(); bu++) {
				Building build = building.model().buildings().get(bs.get(bu));
				if (polygon.contains(build)) {
					if (build.getShape() instanceof ShapeInArea) {
						extinguishableBuildingsDistanceShapeInArea.add((ShapeInArea) build.getShape());
					} else {
						System.err.println("area.getShape() is not instance of ShapeInArea");
						extinguishableBuildingsDistanceShapeInArea.add(new ShapeInArea(build.getApexList(), build));
					}
				} else {
					//					SOSArea sosarea = new SOSArea(build.getEdges());
					////					if (Utility.hasIntersect(polygonSOSArea, sosarea)) {
					//						sosarea  = SOSAreaTools.intersect(sosarea, polygonSOSArea);// FIXME age kond bood sefreh kon
					//						if (sosarea .getEdges().size() != 0) {
					//							int[] apx = sosarea .getApexes();
					//							if (apx.length >= 6 && SOSGeometryTools.computeArea(apx) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)
					//								extinguishableBuildingsDistanceShapeInArea.add(new ShapeInArea(apx, build));
					//						}
					////					}
					Area area = new Area(build.getShape());
					area.intersect(polygonArea);
					int[] apex = AliGeometryTools.getApexes(area);
					if (apex.length >= 6 && SOSGeometryTools.computeArea(apex) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM * 2) {
						ShapeInArea shape = new ShapeInArea(apex, build);
						if (shape.isValid())
							extinguishableBuildingsDistanceShapeInArea.add(shape);
					}

				}
			}
		}
		return extinguishableBuildingsDistanceShapeInArea;

	}

	public ArrayList<ShapeInArea> getRoadsShapeInArea() {
		if (extinguishableRoadsDistanceShapeInArea == null) {
			extinguishableRoadsDistanceShapeInArea = new ArrayList<ShapeInArea>();
			RegularPolygon polygon = new RegularPolygon(building.getX(), building.getY(), FireBrigadeAgent.maxDistance, NUMBER_OF_VERTICES);
			Area polygonArea = new Area(polygon);
			//			SOSArea polygonSOSArea = new SOSArea(AliGeometryTools.getEdges(polygon.getApexes()), polygon);
			int minX, maxX, minY, maxY;
			minX = (int) polygon.getBounds().getMinX();
			minY = (int) polygon.getBounds().getMinY();
			maxX = (int) polygon.getBounds().getMaxX();
			maxY = (int) polygon.getBounds().getMaxY();

			IntList rs = building.model().getRoadIndexInRectangle(new Rectangle(minX, minY, maxX, maxY));
			for (int rd = 0; rd < rs.size(); rd++) {
				Road road = building.model().roads().get(rs.get(rd));
				//				debug.show("", new ShapeDebugFrame.AWTShapeInfo(road.getShape(),road+"",Color.blue,false),
				//						new ShapeDebugFrame.AWTShapeInfo(polygon,"polygon",Color.white,false),new ShapeDebugFrame.DetailInfo("contain?"+polygon.contains(road)));
				//				if (polygon.contains(road.getApexList(),debug))
				if (polygon.contains(road.getApexList())) {
					//					extinguishableRoadsDistanceShapeInArea.add(new ShapeInArea(road.getApexList(), road));
					//					extinguishableRoads.add(road);
					if (road.getShape() instanceof ShapeInArea) {
						extinguishableRoadsDistanceShapeInArea.add((ShapeInArea) road.getShape());
					} else {
						System.err.println("area.getShape() is not instance of ShapeInArea");
						extinguishableRoadsDistanceShapeInArea.add(new ShapeInArea(road.getApexList(), road));
					}
				} else {
					//					SOSArea sosarea = road.getExpandedArea();
					////					if (Utility.hasIntersect(polygonSOSArea, sosarea)) {
					//					sosarea= SOSAreaTools.intersect(sosarea, polygonSOSArea);// FIXME age kond bood sefreh kon
					//						if (sosarea.getEdges().size() != 0) {
					//							int[] apx = sosarea.getApexes();
					//							if (apx.length >= 6 && SOSGeometryTools.computeArea(apx) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM)
					//								extinguishableRoadsDistanceShapeInArea.add(new ShapeInArea(apx, road));
					//						}
					//					}
					Area area = new Area(road.getExpandedArea().getShape());
					area.intersect(polygonArea);
					int[] apex = AliGeometryTools.getApexes(area);
					if (apex.length >= 6 && SOSGeometryTools.computeArea(apex) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM) {
						ShapeInArea shape = new ShapeInArea(apex, road);
						if (shape.isValid())
							extinguishableRoadsDistanceShapeInArea.add(shape);
					}
				}
			}
		}
		return extinguishableRoadsDistanceShapeInArea;

	}

	public ArrayList<ShapeInArea> getExtinguishableSensibleArea() {
		RegularPolygon polygon = new RegularPolygon(building.getX(), building.getY(), FireBrigadeAgent.maxDistance, ExtinguishableArea.NUMBER_OF_VERTICES);
		Area polygonArea = new Area(polygon);
		ArrayList<ShapeInArea> extisense = new ArrayList<ShapeInArea>();
		for (ShapeInArea shapeInArea : building.fireSearchBuilding().sensibleAreasOfAreas()) {
			if (polygon.contains(shapeInArea.getApexes())) {
				extisense.add(shapeInArea);
			} else {
				Area area = new Area(shapeInArea);
				area.intersect(polygonArea);
				int[] apex = AliGeometryTools.getApexes(area);
				if (apex.length >= 6 && SOSGeometryTools.computeArea(apex) > PoliceConstants.VERY_SMALL_ROAD_GROUND_IN_MM * 2) {
					ShapeInArea shape = new ShapeInArea(apex, shapeInArea.getArea(building.model()));
					if (shape.isValid())
						extisense.add(shape);
				}

			}
		}
		return extisense;
	}

	public boolean isReallyUnReachableCustom() {

		if (SOSGeometryTools.distance(building.getPositionPoint(), building.model().me().getPositionPoint()) < FireBrigadeAgent.maxDistance)
			return false;
		if (building.model().sosAgent().getVisibleEntities(Building.class).contains(building))//TODO visible hast reachable nist
			return false;
		boolean isUnreachableToRoad = getRoadsShapeInArea().isEmpty() ? true : building.model().sosAgent().move.isReallyUnreachable(getRoadsShapeInArea());
		if (!isUnreachableToRoad)
			return false;

		for (ShapeInArea sh : getBuildingsShapeInArea()) {
			if (building.model().refuges().isEmpty() && ((Building) sh.getArea(building.model())).virtualData[0].isBurning())
				continue;
			if (building.model().sosAgent().move.isReallyUnreachable(sh))
				continue;
			return false;

		}
		return true;
	}

	public long getCostToCustom() {
		if (SOSGeometryTools.distance(building.getPositionPoint(), building.model().me().getPositionPoint()) < FireBrigadeAgent.maxDistance)
			return 0;

		long isUnreachableToRoad = getRoadsShapeInArea().isEmpty() ? MoveConstants.UNREACHABLE_COST : building.model().sosAgent().move.getWeightToLowProcess(getRoadsShapeInArea(), StandardMove.class);
		if (isUnreachableToRoad < MoveConstants.UNREACHABLE_COST)
			return isUnreachableToRoad;

		long min = MoveConstants.UNREACHABLE_COST;
		for (ShapeInArea sh : getBuildingsShapeInArea()) {
			if (building.model().refuges().isEmpty() && ((Building) sh.getArea(building.model())).virtualData[0].isBurning())
				continue;
			ArrayList<ShapeInArea> list = new ArrayList<ShapeInArea>();
			long w = building.model().sosAgent().move.getWeightToLowProcess(list, StandardMove.class);
			//			if (w>MoveConstants.UNREACHABLE_COST)
			//				continue;
			min = Math.min(min, w);
		}
		return min;
	}

	public int getMovingTimeCustom() {
		return building.model().sosAgent().move.getMovingTimeFromMM(getCostToCustom() * MoveConstants.DIVISION_UNIT_FOR_GET);
	}
}
