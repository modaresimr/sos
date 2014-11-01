package sos.base.reachablity;

import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.police_v2.PoliceUtils;

public class PoliceReachablity {

	static SOSLoggerSystem log(StandardEntity entity) {
		return entity.getAgent().sosLogger.reachablity_Police;
	}

	// Morteza2012*****************************************************************
	public static ArrayList<Blockade> clearableBlockades2(Road area, Point2D start, Point2D end) {
//		log(area).info("clearableBlockades1:P2P has been called...");
//		log(area).info(area);
		ArrayList<SOSArea> reachableParts = area.getReachableParts();
		ArrayList<Blockade> clearableBlockades = new ArrayList<Blockade>();
		short startPartIndex = -1, endPartIndex = -1;
		for (short i = 0; i < reachableParts.size(); i++) {
			if (reachableParts.get(i).getShape().contains(start.getX(), start.getY()))
				startPartIndex = i;
			if (reachableParts.get(i).getShape().contains(end.getX(), end.getY()))
				endPartIndex = i;
		}
//		log(area).info("startPartIndex: " + startPartIndex + " EndPartIndex: " + endPartIndex);
		ArrayList<Blockade> allBlockades = new ArrayList<Blockade>();
		allBlockades.addAll(area.getBlockades());
		allBlockades.addAll(area.getNeighborBlockades());
		if (startPartIndex == -1) {
//			log(area).debug("start point is not in reachableParts!!! ID: " + area.getID() + " Start point: " + start);
			for (Blockade a : allBlockades) {
				if (a.getExpandedBlock().getShape().contains(start.getX(), start.getY())) {
					clearableBlockades.add(a);
					return clearableBlockades;
				}
			}
			int minDist = Integer.MAX_VALUE;
			for (short i = 0; i < reachableParts.size(); i++) {
				int dist = Utility.distanceToSOSArea(start, reachableParts.get(i));
				if (dist < minDist) {
					startPartIndex = i;
					minDist = dist;

				}
			}
			if (startPartIndex != -1) {
//				log(area).info("selected ReachablePart with distance: " + minDist);
				//System.err.println("[Reachablity/isReachableP2E]selected ReachablePart with distance: " + minDist);
			} else {
				log(area).error("start point is not in area!!! \nID: " + area.getID() + " Start point: " + start + "\nThe points should not be out of area");
				clearableBlockades.addAll(allBlockades);
				return clearableBlockades;
			}
		}
		if (endPartIndex == -1)
			if (!area.getShape().contains(end.getX(), end.getY())) {
				log(area).error("end point is not in area!!! \nID: " + area.getID() + " End point: " + end + "\nThe points should not be out of area");
				return clearableBlockades;
			}
		if (startPartIndex == endPartIndex && startPartIndex != -1) {
//			log(area).info("start and end points are in one part!");
			return clearableBlockades;
		}
		ArrayList<SOSArea> blocks = new ArrayList<SOSArea>();
		for (Blockade blockade : allBlockades) {
			if (!PoliceUtils.isValid(blockade))
				continue;
			blocks.clear();
			blocks.add(blockade.getExpandedBlock());
			blocks = CreateReachableParts.createReachableAreaParts(area,area.getExpandedArea(), blocks);
			for (short i = 0; i < blocks.size(); i++) {
				if (blocks.get(i).getShape().contains(start.getX(), start.getY()))
					startPartIndex = i;
				if (blocks.get(i).getShape().contains(end.getX(), end.getY()))
					endPartIndex = i;
			}
			if (startPartIndex != endPartIndex) {
				clearableBlockades.add(blockade);
//				log(area).info("Found a blockade that blocked the road alone >> " + blockade);
				return clearableBlockades;
			}
		}
		for (Blockade blockade : allBlockades) {
			if (!PoliceUtils.isValid(blockade))
				continue;
			blocks.clear();
			for (Blockade b : allBlockades) {
				if (b.getID().getValue() != blockade.getID().getValue())
					blocks.add(b.getExpandedBlock());
			}
			blocks = MergeBlockades.mergeBlockades(area, blocks);
			blocks = CreateReachableParts.createReachableAreaParts(area,area.getExpandedArea(), blocks);
			for (short i = 0; i < blocks.size(); i++) {
				if (blocks.get(i).getShape().contains(start.getX(), start.getY()))
					startPartIndex = i;
				if (blocks.get(i).getShape().contains(end.getX(), end.getY()))
					endPartIndex = i;
			}
			if (startPartIndex == endPartIndex && startPartIndex != -1) {
				clearableBlockades.add(blockade);
//				log(blockade).info("Found a blockade that if be cleard the road will be open!!! >> " + blockade);
				return clearableBlockades;
			}
		}
		Edge e = new Edge(start, end);
		for (Blockade b : allBlockades) {
			if (!PoliceUtils.isValid(b))
				continue;
			for (Edge e1 : b.getExpandedBlock().getEdges()) {
				if (Utility.getIntersect(e1, e) != null) {
					clearableBlockades.add(b);
					break;
				}
			}
		}
		if (clearableBlockades.size() == 0 && area.isBlockadesDefined() && allBlockades.size() > 0)
			clearableBlockades.addAll(allBlockades);
		return clearableBlockades;

	}

	// Morteza2012*****************************************************************
	public static ArrayList<Blockade> clearableBlockades(Road area, Point2D start, Point2D end) {
		ArrayList<Blockade> bs = new ArrayList<Blockade>();
		bs = clearableBlockades2(area, start, end);
//		log(area).debug("Blockades: " + bs);
		return bs;
	}
	/*
	 * // Morteza2011*****************************************************************
	 * public static ArrayList<Blockade> clearableBlockades(Road area, Point2D start, Point2D end) {
	 * ArrayList<SOSArea> reachableParts = area.getReachableParts();
	 * ArrayList<Blockade> clearableBlockades = new ArrayList<Blockade>();
	 * short startPartIndex = -1, endPartIndex = -1;
	 * for (short i = 0; i < reachableParts.size(); i++) {
	 * if (reachableParts.get(i).getShape().contains(start.getX(), start.getY()))
	 * startPartIndex = i;
	 * if (reachableParts.get(i).getShape().contains(end.getX(), end.getY()))
	 * endPartIndex = i;
	 * }
	 * if (startPartIndex == -1) {
	 * for (Blockade a : area.getBlockades()) {
	 * if (a.getExpandedBlock().getShape().contains(start.getX(), start.getY())) {
	 * clearableBlockades.add(a);
	 * return clearableBlockades;
	 * }
	 * }
	 * logPR.error("start point is not in area!!! \nID: " + area.getID() + " Start point: " + start + "\nThe points should not be out of area");
	 * return clearableBlockades;
	 * }
	 * if (endPartIndex == -1)
	 * if (!area.getShape().contains(end.getX(), end.getY())) {
	 * logPR.error("end point is not in area!!! \nID: " + area.getID() + " End point: " + end + "\nThe points should not be out of area");
	 * return clearableBlockades;
	 * }
	 * if (startPartIndex == endPartIndex) {
	 * return clearableBlockades;
	 * }
	 * for (short i = 0; i < area.getMergedBlockades().size(); i++) {
	 * SOSArea a = area.getMergedBlockades().get(i);
	 * if (a.getShape().contains(end.getX(), end.getY())) {
	 * for (Edge e : reachableParts.get(startPartIndex).getEdges()) {
	 * if (e.getReachablityIndex() == -i - 2) {
	 * for (Blockade blockade : a.getReachablityBlockades()) {
	 * if (blockade.getExpandedBlock().getShape().contains(end.getX(), end.getY())) {
	 * clearableBlockades.add(blockade);
	 * return clearableBlockades;
	 * }
	 * }
	 * logPR.warn("End is in merged blockades but is not in expanded blockades!!!");
	 * clearableBlockades.addAll(a.getReachablityBlockades());
	 * return clearableBlockades;
	 * }
	 * }
	 * break;
	 * }
	 * }
	 * ArrayList<Short> reachablePartsEdges = new ArrayList<Short>();
	 * for (Edge e : reachableParts.get(startPartIndex).getEdges()) {
	 * short index = e.getReachablityIndex();
	 * if (index < -1 && !reachablePartsEdges.contains(index)) {
	 * reachablePartsEdges.add(index);
	 * }
	 * }
	 * for (short i = 0; i < reachablePartsEdges.size(); i++) {
	 * for (short j = 0; j < reachableParts.size(); j++) {
	 * if (j == startPartIndex)
	 * continue;
	 * for (Edge edge : reachableParts.get(j).getEdges()) {
	 * if (edge.getReachablityIndex() == reachablePartsEdges.get(i)) {
	 * return getSpecialBlockades(area, -reachablePartsEdges.get(i) - 2, start, end);
	 * }
	 * }
	 * }
	 * }
	 * logPR.error("Each of Edges has not been repeated in two parts!!!");
	 * return clearableBlockades;
	 * }
	 * // Morteza2011*****************************************************************
	 * public static ArrayList<Blockade> clearableBlockades(Road area, Point2D start, Edge end) {
	 * ArrayList<SOSArea> reachableParts = area.getReachableParts();
	 * ArrayList<Blockade> clearableBlockades = new ArrayList<Blockade>();
	 * ArrayList<Short> endPartIndexes = new ArrayList<Short>();
	 * short startPartIndex = -1;
	 * for (short i = 0; i < reachableParts.size(); i++) {
	 * if (reachableParts.get(i).getShape().contains(start.getX(), start.getY()))
	 * startPartIndex = i;
	 * for (Edge e : reachableParts.get(i).getEdges()) {
	 * if (e.getReachablityIndex() < 0)
	 * continue;
	 * if (end.edgeEquals(area.getEdges().get(e.getReachablityIndex())))
	 * endPartIndexes.add(i);
	 * if (end.getTwin() != null && end.getTwin().edgeEquals(area.getEdges().get(e.getReachablityIndex())))
	 * endPartIndexes.add(i);
	 * }
	 * }
	 * if (startPartIndex == -1) {
	 * for (Blockade a : area.getBlockades()) {
	 * if (a.getExpandedBlock().getShape().contains(start.getX(), start.getY())) {
	 * clearableBlockades.add(a);
	 * return clearableBlockades;
	 * }
	 * }
	 * logPR.warn("Traffic Simulator BUG: point is not in reachable area!!! \nID: " + area.getID() + " Start point: " + start + "\nThe point should not be out of area");
	 * clearableBlockades.addAll(area.getBlockades());
	 * return clearableBlockades;
	 * }
	 * boolean endIsInArea = false;
	 * for (Edge e : area.getEdges()) {
	 * if (e.isPassable() && e.edgeEquals(end)) {
	 * endIsInArea = true;
	 * break;
	 * }
	 * if (end.getTwin() != null && e.edgeEquals(end.getTwin())) {
	 * endIsInArea = true;
	 * end = end.getTwin();
	 * }
	 * }
	 * if (!endIsInArea) {
	 * logPR.error("end Edge is not in area or is not passable!!! \nID: " + area.getID() + " End point: " + end + "\nThe edge should not be out of area");
	 * return clearableBlockades;
	 * }
	 * for (short j = 0; j < endPartIndexes.size(); j++) {
	 * if (startPartIndex == endPartIndexes.get(j)) {
	 * return clearableBlockades;
	 * }
	 * }
	 * for (int i = 0; i < area.getMergedBlockades().size(); i++) {
	 * SOSArea a = area.getMergedBlockades().get(i);
	 * if (a.getShape().contains(end.getMidPoint().getX(), end.getMidPoint().getY())) {
	 * for (Edge e : reachableParts.get(startPartIndex).getEdges()) {
	 * if (e.getReachablityIndex() == -i - 2) {
	 * for (Blockade blockade : a.getReachablityBlockades()) {
	 * if (blockade.getExpandedBlock().getShape().contains(end.getMidPoint().getX(), end.getMidPoint().getY())) {
	 * clearableBlockades.add(blockade);
	 * return clearableBlockades;
	 * }
	 * }
	 * logPR.warn("End is in merged blockades but is not in expanded blockades!!!");
	 * clearableBlockades.addAll(a.getReachablityBlockades());
	 * return clearableBlockades;
	 * }
	 * }
	 * break;
	 * }
	 * }
	 * ArrayList<Short> reachablePartsEdges = new ArrayList<Short>();
	 * for (Edge e : reachableParts.get(startPartIndex).getEdges()) {
	 * short index = e.getReachablityIndex();
	 * if (!reachablePartsEdges.contains(index) && index < -1)
	 * reachablePartsEdges.add(index);
	 * }
	 * for (short i = 0; i < reachablePartsEdges.size(); i++) {
	 * for (short j = 0; j < reachableParts.size(); j++) {
	 * if (j == startPartIndex)
	 * continue;
	 * for (Edge edge : reachableParts.get(j).getEdges()) {
	 * if (edge.getReachablityIndex() == reachablePartsEdges.get(i)) {
	 * return getSpecialBlockades(area, -edge.getReachablityIndex() - 2, start, end.getMidPoint());
	 * }
	 * }
	 * }
	 * }
	 * logPR.error("Each of Edges has not been repeated in two parts!!!");
	 * return clearableBlockades;
	 * }
	 * // Morteza2011*****************************************************************
	 * //TODO MORTEZA-->> WHAT IS THE DIFFERENT OF THIS METHOD AND up method???
	 * public static ArrayList<Blockade> clearableBlockades(Road area, Edge start, Point2D end) {
	 * ArrayList<SOSArea> reachableParts = area.getReachableParts();
	 * ArrayList<Blockade> clearableBlockades = new ArrayList<Blockade>();
	 * ArrayList<Short> startPartIndexes = new ArrayList<Short>();
	 * boolean startIsInArea = false;
	 * short endPartIndex = -1;
	 * for (Edge e : area.getEdges()) {
	 * if (e.edgeEquals(start))
	 * startIsInArea = true;
	 * if (start.getTwin() != null && e.edgeEquals(start.getTwin())) {
	 * startIsInArea = true;
	 * start = start.getTwin();
	 * }
	 * }
	 * if (!startIsInArea) {
	 * logPR.error("start edge is not in area!!! \nID: " + area.getID() + " Start Edge: " + start + "\nThe edges should not be out of area");
	 * return clearableBlockades;
	 * }
	 * for (short i = 0; i < reachableParts.size(); i++) {
	 * for (Edge e : reachableParts.get(i).getEdges()) {
	 * if (e.getReachablityIndex() < 0)
	 * continue;
	 * if (start.edgeEquals(area.getEdges().get(e.getReachablityIndex())))
	 * startPartIndexes.add(i);
	 * }
	 * if (reachableParts.get(i).getShape().contains(end.getX(), end.getY()))
	 * endPartIndex = i;
	 * }
	 * if (endPartIndex == -1)
	 * if (!area.getShape().contains(end.getX(), end.getY())) {
	 * logPR.error("EndPoint is not in area!!! \nID: " + area.getID() + " End Point: " + end + "\nThe points should not be out of area");
	 * return clearableBlockades;
	 * }
	 * for (short i = 0; i < startPartIndexes.size(); i++) {
	 * if (startPartIndexes.get(i) == endPartIndex) {
	 * return clearableBlockades;
	 * }
	 * }
	 * if (startPartIndexes.size() == 0) {
	 * for (Blockade blockade : area.getBlockades()) {
	 * if (blockade.getExpandedBlock().getShape().contains(start.getMidPoint().getX(), start.getMidPoint().getY())) {
	 * clearableBlockades.add(blockade);
	 * return clearableBlockades;
	 * }
	 * }
	 * logPR.error("startpartIndexes.Size=0 but start Edge is in Area and is not in blockade!!!\nArea ID: " + area.getID() + " startEdge: " + start);
	 * return clearableBlockades;
	 * }
	 * for (short i = 0; i < area.getMergedBlockades().size(); i++) {
	 * SOSArea a = area.getMergedBlockades().get(i);
	 * if (a.getShape().contains(end.getX(), end.getY())) {
	 * for (Edge e : reachableParts.get(startPartIndexes.get(0)).getEdges()) {
	 * if (e.getReachablityIndex() == -i - 2) {
	 * for (Blockade blockade : a.getReachablityBlockades()) {
	 * if (blockade.getExpandedBlock().getShape().contains(end.getX(), end.getY())) {
	 * clearableBlockades.add(blockade);
	 * return clearableBlockades;
	 * }
	 * }
	 * logPR.warn("End is in merged blockades but is not in expanded blockades!!!");
	 * clearableBlockades.addAll(a.getReachablityBlockades());
	 * return clearableBlockades;
	 * }
	 * }
	 * break;
	 * }
	 * }
	 * ArrayList<Short> startReachablePartsEdges = new ArrayList<Short>();
	 * for (Edge e : reachableParts.get(startPartIndexes.get(0)).getEdges()) {
	 * if (!startReachablePartsEdges.contains(e.getReachablityIndex()) && e.getReachablityIndex() < -1)
	 * startReachablePartsEdges.add(e.getReachablityIndex());
	 * }
	 * for (short i = (short) (startReachablePartsEdges.size() - 1); i >= 0; i--) {
	 * for (short j = 0; j < reachableParts.size(); j++) {
	 * if (j == startPartIndexes.get(0))
	 * continue;
	 * for (Edge e : reachableParts.get(j).getEdges()) {
	 * if (e.getReachablityIndex() == startReachablePartsEdges.get(i)) {
	 * return getSpecialBlockades(area, -e.getReachablityIndex() - 2, start.getMidPoint(), end);
	 * }
	 * }
	 * }
	 * }
	 * logPR.error("Each of Edges has not been repeated in two parts!!!");
	 * return clearableBlockades;
	 * }
	 * // Morteza2011*****************************************************************
	 * public static ArrayList<Blockade> clearableBlockades(Road area, Edge start, Edge end) {
	 * ArrayList<SOSArea> reachableParts = area.getReachableParts();
	 * ArrayList<Blockade> clearableBlockades = new ArrayList<Blockade>();
	 * ArrayList<Short> startPartIndexes = new ArrayList<Short>(), endPartIndexes = new ArrayList<Short>();
	 * boolean startIsInArea = false, endIsInArea = false;
	 * for (Edge e : area.getEdges()) {
	 * if (e.edgeEquals(start))
	 * startIsInArea = true;
	 * if (e.edgeEquals(end))
	 * endIsInArea = true;
	 * if (end.getTwin() != null && e.edgeEquals(end.getTwin())) {
	 * endIsInArea = true;
	 * end = end.getTwin();
	 * }
	 * if (start.getTwin() != null && e.edgeEquals(start.getTwin())) {
	 * startIsInArea = true;
	 * start = start.getTwin();
	 * }
	 * }
	 * if (!startIsInArea) {
	 * return clearableBlockades;
	 * }
	 * if (!endIsInArea) {
	 * return clearableBlockades;
	 * }
	 * for (short i = 0; i < reachableParts.size(); i++) {
	 * for (Edge e : reachableParts.get(i).getEdges()) {
	 * if (e.getReachablityIndex() < 0)
	 * continue;
	 * if (start.edgeEquals(area.getEdges().get(e.getReachablityIndex()))) {
	 * startPartIndexes.add(i);
	 * }
	 * if (end.edgeEquals(area.getEdges().get(e.getReachablityIndex())))
	 * endPartIndexes.add(i);
	 * }
	 * }
	 * for (short i = 0; i < startPartIndexes.size(); i++) {
	 * for (short j = 0; j < endPartIndexes.size(); j++) {
	 * if (startPartIndexes.get(i) == endPartIndexes.get(j)) {
	 * return clearableBlockades;
	 * }
	 * }
	 * }
	 * if (startPartIndexes.size() == 0) {
	 * for (Blockade blockade : area.getBlockades()) {
	 * if (blockade.getExpandedBlock().getShape().contains(start.getMidPoint().getX(), start.getMidPoint().getY())) {
	 * clearableBlockades.add(blockade);
	 * return clearableBlockades;
	 * }
	 * }
	 * logPR.error("startpartIndexes.Size=0 but start Edge is in Area and is not in blockade!!!\nArea ID: " + area.getID() + " startEdge: " + start);
	 * return clearableBlockades;
	 * }
	 * ArrayList<Short> startReachablePartsEdges = new ArrayList<Short>();
	 * for (Edge e : reachableParts.get(startPartIndexes.get(0)).getEdges()) {
	 * if (!startReachablePartsEdges.contains(e.getReachablityIndex()) && e.getReachablityIndex() < -1)
	 * startReachablePartsEdges.add(e.getReachablityIndex());
	 * }
	 * for (short i = (short) (startReachablePartsEdges.size() - 1); i >= 0; i--) {
	 * for (short j = 0; j < reachableParts.size(); j++) {
	 * if (j == startPartIndexes.get(0))
	 * continue;
	 * for (Edge e : reachableParts.get(j).getEdges()) {
	 * if (e.getReachablityIndex() == startReachablePartsEdges.get(i)) {
	 * return getSpecialBlockades(area, -startReachablePartsEdges.get(i) - 2, start.getMidPoint(), end.getMidPoint());
	 * }
	 * }
	 * }
	 * }
	 * logPR.warn("Each of Edges has not been repeated in two parts!!!");
	 * return clearableBlockades;
	 * }
	 * // Morteza2011*****************************************************************
	 * private static ArrayList<Blockade> getSpecialBlockades(Road area, int mb, Point2D start, Point2D end) {
	 * ArrayList<Blockade> specialBlockades = new ArrayList<Blockade>();
	 * if (area.getMergedBlockades().get(mb).getReachablityBlockades().size() == 1) {
	 * specialBlockades.add(area.getMergedBlockades().get(mb).getReachablityBlockades().get(0));
	 * return specialBlockades;
	 * }
	 * SOSArea clearRP = area.getExpandedArea();
	 * ArrayList<Blockade> blockades = area.getMergedBlockades().get(mb).getReachablityBlockades();
	 * if (blockades.size() == 2) {
	 * ArrayList<SOSArea> blocks = new ArrayList<SOSArea>();
	 * blocks.add(blockades.get(0).getExpandedBlock());
	 * ArrayList<SOSArea> rps = CreateReachableParts.createReachableAreaParts(clearRP, blocks);
	 * if (rps.size() == 1) {
	 * specialBlockades.add(blockades.get(1));
	 * return specialBlockades;
	 * } else {
	 * specialBlockades.add(blockades.get(0));
	 * return specialBlockades;
	 * }
	 * }
	 * ArrayList<SOSArea> blocks = new ArrayList<SOSArea>();
	 * for (int i = 0; i < blockades.size(); i++) {
	 * blocks.add(blockades.get(i).getExpandedBlock());
	 * ArrayList<SOSArea> rps = CreateReachableParts.createReachableAreaParts(clearRP, blocks);
	 * if (rps.size() == 2) {
	 * specialBlockades.add(blockades.get(i));
	 * return specialBlockades;
	 * }
	 * blocks.clear();
	 * }
	 * ArrayList<Blockade> preSpecialsBlockades = new ArrayList<Blockade>();
	 * for (int i = 0; i < blockades.size(); i++) {
	 * for (int j = 0; j < blockades.size(); j++) {
	 * if (i != j)
	 * blocks.add(blockades.get(j).getExpandedBlock());
	 * }
	 * ArrayList<SOSArea> rps = CreateReachableParts.createReachableAreaParts(clearRP, blocks);
	 * if (rps.size() == 1)
	 * preSpecialsBlockades.add(blockades.get(i));
	 * blocks.clear();
	 * }
	 * if (preSpecialsBlockades.size() == 0) {
	 * Edge line = new Edge(start, end);
	 * for (Blockade blockade : area.getMergedBlockades().get(mb).getReachablityBlockades()) {
	 * for (Edge e : blockade.getExpandedBlock().getEdges()) {
	 * if (Utility.getIntersect(line, e) != null) {
	 * specialBlockades.add(blockade);
	 * break;
	 * }
	 * }
	 * }
	 * if (specialBlockades.size() == 0) {
	 * specialBlockades.addAll(area.getMergedBlockades().get(mb).getReachablityBlockades());
	 * }
	 * return specialBlockades;
	 * }
	 * int minCost = 0;
	 * for (int i = 0; i < preSpecialsBlockades.size(); i++) {
	 * if (preSpecialsBlockades.get(i).getRepairCost() < preSpecialsBlockades.get(minCost).getRepairCost())
	 * minCost = i;
	 * }
	 * specialBlockades.add(preSpecialsBlockades.get(minCost));
	 * return specialBlockades;
	 * }
	 */

}
