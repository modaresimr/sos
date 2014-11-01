package sos.base.reachablity;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.blockadeEstimator.AliGeometryTools;
import sos.base.util.blockadeEstimator.BlockadeEstimator;
import sos.base.util.geom.SOSShape;
import sos.base.util.geom.ShapeInArea;
import sos.base.util.sosLogger.SOSLoggerSystem;

public class Reachablity {
	public static enum ReachablityState {
		Open, Close, FoggyOpen, FoggyClose
	}

	static SOSLoggerSystem log(StandardEntity entity) {
		return entity.getAgent().sosLogger.reachablity_Interface;
	}

	public static ReachablityState isReachable(Road area, Point2D start, Point2D end) {
		//log(area).logln("-----------------------------------------------------------------------");
		//log(area).logln("isReachable P2P:\nArea: " + area + "\nStart: " + start + "\nEnd: " + end);
		if (area.isBlockadesDefined() && area.getBlockades().isEmpty() && area.getNeighborBlockades().isEmpty()) {
			//log(area).logln("Blockades size is zero so the road is Open!");
			return getState(area, true);
		}
		if (area.getReachableParts().size() == 0) {
			//log(area).logln("reachableParts.size is zero so the road is full block and unreachable!");
			return getState(area, false);
		}
		else {
			//log(area).logln("reachable parts size: " + area.getReachableParts().size());
		}
		short startIndex = -1, endIndex = -1;
		ArrayList<SOSArea> reachableParts = area.getReachableParts();
		for (short i = 0; i < reachableParts.size(); i++) {
			if (reachableParts.get(i).getShape().contains(start.getX(), start.getY()))
				startIndex = i;
			if (reachableParts.get(i).getShape().contains(end.getX(), end.getY()))
				endIndex = i;
		}
		//log(area).logln("StartPartIndex: " + startIndex + " ,EndPartIndex: " + endIndex);
		if (startIndex == -1) {
			if (area.isBlockadesDefined())
				for (Blockade block : area.getBlockades())
					if (block.getShape().contains(start.toGeomPoint())) {
						//log(area).logln("Start point is in " + block + " so is block");
						return getState(area, false);
					}
			if (area.isBlockadesDefined())
				for (Blockade block : area.getBlockades())
					if (block.getShape().contains(end.toGeomPoint())) {
						//log(area).logln("End point is in " + block + " so is block");
						return getState(area, false);
					}
			int minDist = Integer.MAX_VALUE;
			for (short i = 0; i < reachableParts.size(); i++) {
				int dist = Utility.distanceToSOSArea(start, reachableParts.get(i));
				if (dist < minDist && dist < ReachablityConstants.MinimumDistanceToNearReachablePart) {
					startIndex = i;
					minDist = dist;

				}
			}
			if (startIndex != -1) {
//				log(area).logln("New Start Index: " + startIndex);
//				if (minDist < ReachablityConstants.MinimumDistanceToNearReachablePart) {
//					log(area).logln(area.getAgent() + " , " + area + " P2P selected ReachablePart with distance: " + minDist + " RPS:" + reachableParts.size());
//				} else {
//					log(area).logln("nearest Reachable part is too far: " + minDist);
//				}
			} else if (area.isBlockadesDefined()) {
				log(area).warn(area.getAgent() + " : " + area + " >> distance to reachable parts checked but the point isnot in any of reachableparts yet!!!");
			}

		}
		if (endIndex == -1) {
			//			log(area).warn("end point is not in any of reachable parts or blockades!!!");
			return getState(area, false);
		}
		if (startIndex == endIndex) {
			//log(area).logln("Start and End Reachable parts are the same so is reachable:" + startIndex);
			return getState(area, true);
		}
		//log(area).logln("Start and End points are not in one Reachable Part: " + startIndex + "," + endIndex);
		return getState(area, false);
	}

	// Morteza2011*****************************************************************
	public static ReachablityState isReachable(Road area, Edge start, Edge end) {
		//		log(area).logln("-----------------------------------------------------------------------");
		//		log(area).logln("isReachable E2E:\nArea: " + area + "\nStart: " + start + "\nEnd: " + end);
		if (area.isBlockadesDefined() && area.getBlockades().isEmpty() && area.getNeighborBlockades().isEmpty()) {
			//			log(area).logln("Blockades size is zero so the road is Open!");
			return getState(area, true);
		}
		boolean isReachable = area.getDisjiontSetForEdges().inSameSet(start.getReachablityIndex(), end.getReachablityIndex());
		//		log(area).logln("isReachable: " + isReachable);
		return getState(area, isReachable);
	}

	// Morteza2011*****************************************************************
	public static ReachablityState isReachable(Road area, Point2D start, Edge end) {
		//log(area).logln("-----------------------------------------------------------------------");
		//log(area).logln("isReachable P2E:\nArea: " + area + "\nStart: " + start + "\nEnd: " + end);
		if (area.isBlockadesDefined() && area.getBlockades().isEmpty() && area.getNeighborBlockades().isEmpty()) {
			//log(area).logln("Blockades size is zero so the road is Open!");
			return getState(area, true);
		}
		if (area.getReachableParts().size() == 0) {
			//log(area).logln("reachableParts.size is zero so the road is full block and unreachable!");
			return getState(area, false);
		}
		if (area.isBlockadesDefined())
			for (Blockade block : area.getBlockades())
				if (block.getShape().contains(start.toGeomPoint())) {
					//log(area).logln("Start is in blockade so is block: " + block);
					return getState(area, false);
				}
		short startIndex = -1;
		ArrayList<SOSArea> reachableParts = area.getReachableParts();
		for (short i = 0; i < reachableParts.size(); i++) {
			if (reachableParts.get(i).getShape().contains(start.getX(), start.getY())) {
				startIndex = i;
				break;
			}
		}
		//log(area).logln("Satrt Part Index: " + startIndex);
		//		if (startIndex == -1 && area.isBlockadesDefined()) {
		//			for (Blockade b : area.getBlockades()) {
		//				if (b.getShape().contains(start.getX(), start.getY())) {
		//					//log(area).logln("Start point is in " + b + " so it is unreachable");
		//					return getState(area, false);
		//				}
		//			}
		//		}
		if (startIndex == -1) {
			int minDist = Integer.MAX_VALUE;
			for (short i = 0; i < reachableParts.size(); i++) {
				int dist = Utility.distanceToSOSArea(start, reachableParts.get(i));
				if (dist < minDist && dist < ReachablityConstants.MinimumDistanceToNearReachablePart) {
					startIndex = i;
					minDist = dist;

				}

			}
//			if (startIndex != -1) {
//				log(area).logln("New Start Index: " + startIndex);
//				log(area).error(new Exception("reachablity P2E[Just for Test By Ali]"));
//				if (minDist < ReachablityConstants.MinimumDistanceToNearReachablePart)
//					log(area).logln(area.getAgent() + " , " + area + " P2P selected ReachablePart with distance: " + minDist + " RPS:" + reachableParts.size());
//				else
//					log(area).logln("nearest Reachable part is too far: " + minDist);
//			} else if (area.isBlockadesDefined()) {
//				log(area).debug(area.getAgent() + " : " + area + " >> distance to reachable parts checked but the point isnot in any of reachableparts yet!!! : " + minDist);
//			}
		}
		try {
			if (startIndex > -1) {
				boolean isReachable = area.getDisjiontSetForReachablePartsAndEdges().inSameSet(startIndex, (short) (end.getReachablityIndex() + area.getReachableParts().size()));
				//log(area).logln("isReachable: " + isReachable);
				return getState(area, isReachable);
			} else {
				return getState(area, false);
			}
		} catch (Exception e) {
			log(area).error(e);
			return getState(area, false);

		}
	}

	// Morteza2011*****************************************************************
	public static ReachablityState isReachable(Road area, Edge start,Point2D end) {
		//log(area).logln("-----------------------------------------------------------------------");
		//log(area).logln("isReachable P2E:\nArea: " + area + "\nStart: " + start + "\nEnd: " + end);
		if (area.isBlockadesDefined() && area.getBlockades().isEmpty() && area.getNeighborBlockades().isEmpty()) {
			//log(area).logln("Blockades size is zero so the road is Open!");
			return getState(area, true);
		}
		if (area.getReachableParts().size() == 0) {
			//log(area).logln("reachableParts.size is zero so the road is full block and unreachable!");
			return getState(area, false);
		}
		if (area.isBlockadesDefined())
			for (Blockade block : area.getBlockades())
				if (block.getShape().contains(end.toGeomPoint())) {
					//log(area).logln("Start is in blockade so is block: " + block);
					return getState(area, false);
				}
		short startIndex = -1;
		ArrayList<SOSArea> reachableParts = area.getReachableParts();
		for (short i = 0; i < reachableParts.size(); i++) {
			if (reachableParts.get(i).getShape().contains(end.getX(), end.getY())) {
				startIndex = i;
				break;
			}
		}
		//log(area).logln("Satrt Part Index: " + startIndex);
		//		if (startIndex == -1 && area.isBlockadesDefined()) {
		//			for (Blockade b : area.getBlockades()) {
		//				if (b.getShape().contains(start.getX(), start.getY())) {
		//					//log(area).logln("Start point is in " + b + " so it is unreachable");
		//					return getState(area, false);
		//				}
		//			}
		//		}
		try {
			if (startIndex > -1) {
				boolean isReachable = area.getDisjiontSetForReachablePartsAndEdges().inSameSet(startIndex, (short) (start.getReachablityIndex() + area.getReachableParts().size()));
				//log(area).logln("isReachable: " + isReachable);
				return getState(area, isReachable);
			} else {
				return getState(area, false);
			}
		} catch (Exception e) {
			log(area).error(e);
			return getState(area, false);

		}
	}

	// Morteza2011*****************************************************************
	public static ReachablityState isReachableAgentToEdge(Human h, Road road, Edge end) {
		//		log(road).logln("-----------------------------------------------------------------------");
		//		log(road).logln("isReachableAgentToEdge \nHuman: " + h + "\nRoad: " + road + "\nEnd: " + end);
		if (!h.isXDefined() || !h.isYDefined() || !h.isPositionDefined() || !h.isPositionDefined()) {
			log(road).warn("Some features of human is not defined!!!");
			log(road).error(new Error("Some features of human is not defined!!!"));
			return getState(road, false);
		}
		return isReachable(road, h.getPositionPoint(), end);
	}

	// Morteza2011*****************************************************************
	public static ReachablityState isReachableAgentToPoint(Human h, Road road, Point2D end) {

		//		log(road).logln("-----------------------------------------------------------------------");
		//		log(road).logln("isReachableAgentToPoint \nHuman: " + h + "\nRoad: " + road + "\nEnd: " + end);
		if (!h.isXDefined() || !h.isYDefined() || !h.isPositionDefined() || !h.isPositionDefined()) {
			log(road).warn("Some features of human is not defined!!!");

			return getState(road, false);
		}
		if (!road.isBlockadesDefined())
			return getState(road, true);

		return isReachable(road, h.getPositionPoint(), end);
	}

	// Morteza2011*****************************************************************
	public static ReachablityState getState(Road r, boolean preState) {
		boolean isFogy = !r.isBlockadesDefined();
		boolean isMiddleBlockBiggerThanReal = BlockadeEstimator.isActuallyMiddleBlockadesBiggerThanRealBlockades();
		if (isFogy) {
			if (preState)
				if (isMiddleBlockBiggerThanReal)//////////////////////////////////ALI ADDED!!!!!!!!!!!!
					return ReachablityState.Open;
				else
					return ReachablityState.FoggyOpen;
			else
				return ReachablityState.FoggyClose;
		} else {
			if (preState)
				return ReachablityState.Open;
			else
				return ReachablityState.Close;
		}
	}

	/**
	 * @author ali
	 */
	@SuppressWarnings("unused")
	private static ArrayList<SOSShape> getReachableAreas(Road road, Point2D second, SOSShape sosshape) {
		ArrayList<SOSShape> reachableAreas = new ArrayList<SOSShape>();
		for (SOSArea reachablePart : road.getReachableParts()) {
			if (reachablePart.getShape().contains(second.getX(), second.getY())) {
				Area area = new Area(sosshape);
				area.intersect(new Area(reachablePart.getShape()));
				List<Area> arealist = AliGeometryTools.fix(area);
				for (Area area2 : arealist) {
					int[] apexes = AliGeometryTools.getApexes(area2);
					if (apexes.length >= 6) {
						SOSShape shape = new SOSShape(apexes);
						if (shape.isValid())
							reachableAreas.add(shape);
					}
				}
				break;
			}
		}
		return reachableAreas;
	}

	/**
	 * @author ali
	 */
	@SuppressWarnings("unused")
	private static ArrayList<SOSShape> getReachableAreas(Road road, Edge ed, ShapeInArea sosShape) {

		ArrayList<Integer> listOfReachablePartOfAnEdge = new ArrayList<Integer>();
		for (short i = 0; i < road.getReachableParts().size(); i++) {

			boolean res = road.getDisjiontSetForReachablePartsAndEdges().inSameSet(i, (short) (ed.getReachablityIndex() + road.getReachableParts().size()));
			if (res) {
				listOfReachablePartOfAnEdge.add((int) i);
			}
		}

		ArrayList<SOSShape> reachableAreas = new ArrayList<SOSShape>();
		for (Integer index : listOfReachablePartOfAnEdge) {
			SOSArea reachablePart = road.getReachableParts().get(index);
			Area area = new Area(sosShape);
			area.intersect(new Area(reachablePart.getShape()));
			List<Area> arealist = AliGeometryTools.fix(area);
			for (Area area2 : arealist) {
				int[] apexes = AliGeometryTools.getApexes(area2);
				if (apexes.length >= 6) {
					SOSShape shape = new SOSShape(apexes);
					if (shape.isValid())
						reachableAreas.add(shape);
				}
			}
		}
		return reachableAreas;
	}

	public static ArrayList<Point2D> getReachablePoints(Road road, Point2D from, ShapeInArea to) {
		ArrayList<Point2D> reachableAreas = new ArrayList<Point2D>();
		for (SOSArea reachablePart : road.getReachableParts()) {
			if (reachablePart.getShape().contains(from.getX(), from.getY())) {
				if (reachablePart.getShape().contains(to.getCenterPoint().toGeomPoint())) {
					reachableAreas.add(to.getCenterPoint());
				} else {
					Area area = new Area(to);
					area.intersect(new Area(reachablePart.getShape()));
					List<Area> arealist = AliGeometryTools.fix(area);
					for (Area area2 : arealist) {
						int[] apexes = AliGeometryTools.getApexes(area2);
						if (apexes.length >= 6){
							SOSShape shape = new SOSShape(apexes);
							if(shape.isValid())
							reachableAreas.add(shape.getCenterPoint());
						}
					}
				}
				break;
			}
		}
		return reachableAreas;
	}

	/**
	 * @author ali
	 */
	public static ArrayList<Point2D> getReachablePoints(Road road, Edge ed, ShapeInArea sosShape) {
		ArrayList<Integer> listOfReachablePartOfAnEdge = new ArrayList<Integer>();
		for (short i = 0; i < road.getReachableParts().size(); i++) {

			boolean res = road.getDisjiontSetForReachablePartsAndEdges().inSameSet(i, (short) (ed.getReachablityIndex() + road.getReachableParts().size()));
			if (res) {
				listOfReachablePartOfAnEdge.add((int) i);
			}
		}

		ArrayList<Point2D> reachableAreas = new ArrayList<Point2D>();
		for (Integer index : listOfReachablePartOfAnEdge) {
			SOSArea reachablePart = road.getReachableParts().get(index);
			if (reachablePart.getShape().contains(sosShape.getCenterPoint().toGeomPoint())) {
				reachableAreas.add(sosShape.getCenterPoint());
			} else {
				Area area = new Area(sosShape);
				area.intersect(new Area(reachablePart.getShape()));
				List<Area> arealist = AliGeometryTools.fix(area);
				for (Area area2 : arealist) {
					int[] apexes = AliGeometryTools.getApexes(area2);
					if (apexes.length >= 6){
						SOSShape shape = new SOSShape(apexes);
						if(shape.isValid())
							reachableAreas.add(shape.getCenterPoint());
					}
				}
			}
		}
		return reachableAreas;

	}
}
