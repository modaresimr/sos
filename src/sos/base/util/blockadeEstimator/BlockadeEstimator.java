package sos.base.util.blockadeEstimator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.entities.StandardWorldModel;
import sos.base.precompute.PreCompute;
import sos.base.util.mapRecognition.MapRecognition.MapName;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

@SuppressWarnings("unused")
public class BlockadeEstimator{
	private static final double MAX_COLLAPSE = 100;// From Kernel
	private static final int FLOOR_HEIGHT = 7 * 1000;// From Config
	private static float MIDDLE_COLLAPSE_PERCENT = 0.5f;
	private StandardWorldModel model;
	// private static HashMap<EntityID, Area> staticMaxBlock = new HashMap<EntityID, Area>();
	// private static HashMap<EntityID, Area> staticMiddleBlock = new HashMap<EntityID, Area>();

	// private static HashMap<Building, HashMap<Road, List<SOSBlockade>>> staticMiddleBlock_road_sosBlock = new HashMap<Building, HashMap<Road, List<SOSBlockade>>>();
	// private static ShapeDebugFrame debug = new ShapeDebugFrame();
	public SOSLoggerSystem beLog;
	private String middleBlocksPreComputeFile;

	public enum BlockEstimate {
		BuildingDetectionError, TheRoadIsFullOfBlock, EstimateComplete

	}

	// private final SOSAgent<E> sosAgent;

	public BlockadeEstimator(StandardWorldModel model) {
		this.model = model;
		beLog = new SOSLoggerSystem(model().me(), "BlockadeEstimator", true, OutputType.File);
		model().sosAgent().sosLogger.addToAllLogType(beLog);
		beLog.setFullLoggingLevel();

		if ((int) model().getBounds().getWidth() > 800000) {
			beLog.debug("Map Width > 800000 so we collpase buildings full!!!");
			MIDDLE_COLLAPSE_PERCENT = 1f;
		}
		beLog.debug("MIDDLE_COLLAPSE_PERCENT=" + MIDDLE_COLLAPSE_PERCENT);
		beLog.debug("isActuallyMiddleBlockadesBiggerThanRealBlockades=" + isActuallyMiddleBlockadesBiggerThanRealBlockades());

		middleBlocksPreComputeFile=PreCompute.getPreComputeFile("MiddleBlockade");

	}

	public static boolean isActuallyMiddleBlockadesBiggerThanRealBlockades() {
		return MIDDLE_COLLAPSE_PERCENT > .9f;
	}

	/*
	 * public BlockEstimate estimateSOSBlockade(Blockade blockade, TreeSet<Road> changedRoadsReachability) {
	 * long startTime = System.currentTimeMillis();
	 * // Area shape1 = AliGeometryTools.areaToGeomArea(blockade.getPosition());
	 * /*
	 * blockade = unclearBlockade(blockade, findTimeOfCleared(blockade));
	 * // Area shape2 = AliGeometryTools.sosareaToGeomArea(blockade.getCustomExpandedBlock(10));
	 * Road road = blockade.getPosition();
	 * List<Point2D> nonBreakedBlockPoints = new ArrayList<Point2D>();
	 * for (Edge e1 : blockade.getEdges()) {
	 * if (!isBreakedEdge(e1, road.getEdges(), blockade, road)) {
	 * nonBreakedBlockPoints.add(e1.getStart());
	 * nonBreakedBlockPoints.add(e1.getEnd());
	 * }
	 * }
	 * // shape1.subtract(shape2);
	 * double d = -2;
	 * beLog.logln("");
	 * if (nonBreakedBlockPoints.size() == 0) {
	 * // beLog.error("Can't Estimate " + blockade + " because it doesn't have enough nonBreakedEdge! NOT HANDLED YET");
	 * beLog.warn(blockade.getPosition() + " is full of blockade ---> Blockade Estimator can't estimate full of blocks roads!");
	 * // TODO WHAT?
	 * beLog.info("BlockEstimator usage time: " + (System.currentTimeMillis() - startTime));
	 * return BlockEstimate.TheRoadIsFullOfBlock;
	 * // beLog.debug(blockade.getPosition() + " is full of blockade ---> Blockade Estimator can't estimate full of blocks roads!");
	 * } else {
	 * Building building;
	 * if (nonBreakedBlockPoints.size() < 4) {
	 * beLog.info("the blockade have not enough non breaked edge(Point) so it try to choose a building if it sure that's correct ");
	 * building = lowBreakPointOwnerBuilding(blockade, nonBreakedBlockPoints);
	 * } else {
	 * building = ownerBuilding(blockade, nonBreakedBlockPoints);
	 * }
	 * if (building != null) {
	 * // if (AliGeometryTools.havecorrectDirection(building)) {
	 * // d = AliGeometryTools.getClosestDistance( AliGeometryTools.areaToGeomArea(building),GeometryTools2D.vertexArrayToPoints(AliGeometryTools.getApexes(shape1)));
	 * // // List<Line2D> lines = AliGeometryTools.getLines(shape1);
	 * // // double dSalim = Integer.MAX_VALUE;
	 * // // for (Edge e : building.getEdges()) {
	 * // // dSalim = Math.min(dSalim, SOSGeometryTools.distance(e, lines));
	 * // // }
	 * // beLog.debug(blockade + " owner" + building + "(have Correct direction) block size---> d=" + d + " ,dsalim=" + dSalim );
	 * // } else {
	 * // d = findSizeOfBlockForAbnormalBlockade(blockade,building);
	 * // beLog.debug(blockade + " owner" + building + "(have INCorrect direction) block size---> d=" + d + " ,dsalim=" + dSalim );
	 * // }
	 * d = findSizeOfBlockade(blockade, nonBreakedBlockPoints, building);
	 * beLog.debug(blockade + " owner" + building + " block size---> d=" + d);
	 * // if (building.getID().getValue() == 11302||building.getID().getValue() ==36897) {
	 * // ArrayList<ShapeDebugFrame.ShapeInfo> a=new ArrayList<ShapeInfo>();
	 * // a.add(new AWTShapeInfo(blockade.getPosition().getShape(), "Road of the blockade" + blockade.getPosition(), Color.RED.brighter(), true));
	 * // a.add(new AWTShapeInfo(blockade.getShape(), blockade.toString(), Color.green, false));
	 * // a.add(new AWTShapeInfo(shape2, "CustomExpandedBlock(20)", Color.GRAY, false));
	 * // a.add(new AWTShapeInfo(building.getShape(), "Owner " + building.toString(), Color.BLUE, false));
	 * // a.add(new AWTShapeInfo(shape1, "intersect", Color.blue, false));
	 * // a.add(new ShapeDebugFrame.DetailInfo("Block Size=" + d));
	 * // List<Point2D> ps = GeometryTools2D.vertexArrayToPoints(building.getApexList());
	 * // for (Point2D p : ps) {
	 * // a.add(new ShapeDebugFrame.Point2DShapeInfo(p, p.toString(), Color.blue, false));
	 * // }
	 * // debug.show("estimateBlockadeRandomNumber", a
	 * //
	 * // );
	 * // }
	 * //
	 * findOrCreateSOSBlockadesOfBuilding(building, d, changedRoadsReachability);
	 * beLog.info("BlockEstimator usage time: " + (System.currentTimeMillis() - startTime));
	 * return BlockEstimate.EstimateComplete;
	 * } else {
	 * // beLog.warn("Building detection error for [" + blockade + "]");
	 * beLog.info("BE Can't find the currect Owner Building of " + blockade + " now the blockade will add manual without owner building");
	 * SOSBlockade newManualSOSBlockade = new SOSBlockade(new Area(blockade.getShape()), blockade.getPosition(), null);
	 * newManualSOSBlockade.setReferedBlockade(blockade);
	 * blockade.setReferedSosBlockade(newManualSOSBlockade);
	 * model().block.newMiddleBlockade(newManualSOSBlockade);
	 * model().block.updateMiddleBlockadeToSOSBlock(newManualSOSBlockade, blockade.getApexes());
	 * beLog.info("BlockEstimator usage time: " + (System.currentTimeMillis() - startTime));
	 * return BlockEstimate.EstimateComplete;
	 * // }
	 * // }
	 * }
	 */
	//	private double findSizeOfBlockade(Blockade blockade, List<Point2D> nonBreakedBlockPoints, Building building) {
	//		int blockSize = AliGeometryTools.getClosestDistance(nonBreakedBlockPoints, GeometryTools2D.vertexArrayToPoints((building.getApexList())));
	//		// debug.show("blocksize",
	//		// new ShapeDebugFrame.AWTShapeInfo(blockade.getShape(),"Blockade" + blockade, Color.darkGray, false),
	//		// new AWTShapeInfo(road.getShape(), road.toString(), Color.magenta, false),
	//		// new ShapeDebugFrame.Line2DShapeInfo(lbreak, "BlockadeEdges", Color.white, true, true),
	//		// new AWTShapeInfo(building.getShape(), building.toString(), Color.orange, false)
	//		// new ShapeDebugFrame.DetailInfo("threshold: " + threshold)
	//		// );
	//		return blockSize;
	//	}

	public int findTimeOfCleared(BlockadeInterface blockade) {// FIXME BUG DARE ye RAHE hal mikham
		beLog.info("finding the time of cleared " + blockade);
		if (blockade == null) {
			beLog.debug("the block is null ---> returning -1");
			return -1;
		}
		// debug.setBackground(new AWTShapeInfo(blockade.getPosition().getShape(), blockade.getPosition().toString(), Color.red.brighter(), true),
		// new AWTShapeInfo(blockade.getShape(), blockade.toString(), Color.gray, false),
		// new ShapeDebugFrame.Point2DShapeInfo(blockade.getCenteroid(), "block Centeroid", Color.black, true));

		int i;
		FOR: for (i = 0;; i++) {
			SOSBlockade sb = unclearBlockadeInterFace(blockade, i);

			List<Point2D> bpoints = GeometryTools2D.vertexArrayToPoints(sb.getApexes());
			for (Point2D p : bpoints) {
				if (!blockade.getPosition().getShape().contains(p.toGeomPoint())) {
					// if (model().time() > 2)
					// debug.show("InitialBlockade", new AWTShapeInfo(sb.getShape(), "UnCleared " + i + " time Block", Color.BLUE, false));
					break FOR;
				}
			}
			for (Edge e1 : sb.getEdges()) {
				if (isBreakedEdge(e1, blockade.getPosition().getEdges(), sb, blockade.getPosition())) {
					// if (model().time() > 2)
					// debug.show("InitialBlockade", new AWTShapeInfo(sb.getShape(), "UnCleared " + i + " time Block", Color.BLUE, false));
					break FOR;
				}
			}
		}
		if (blockade instanceof SOSBlockade) {
			SOSBlockade sosBlockade = (SOSBlockade) blockade;
			int lastClearTime = sosBlockade.getClearedTime();
			if (lastClearTime > i) {
				i = lastClearTime + 1;
			}
		}
		// beLog.debug(blockade + " cleared Time=" + i + " cost:" + blockade.getRepairCost() + " ---> initial Cost= " + (blockade.getRepairCost() + i * SOSAgent.BLOCKADE_REPAIR_RATE));
		// if (i != 0)
		// System.out.println((blockade + " cleared Time=" + i + " cost:" + blockade.getRepairCost() + " ---> initial Cost= " + blockade.getRepairCost() + i * SOSAgent.BLOCKADE_REPAIR_RATE));
		return i;

	}

	//
	//	public Building estimateBlockadeForBuilding(Blockade blockade) {
	//		EntityID e = new EntityID(Integer.parseInt(JOptionPane.showInputDialog("buliding ID for :" + blockade)));
	//		Building b = ((sos.base.entities.Building) model().getEntity(e));
	//		ArrayList<ShapeInfo> infos = new ArrayList<ShapeDebugFrame.ShapeInfo>();
	//		for (Blockade block : model().blockades()) {
	//			infos.add(new AWTShapeInfo(block.getShape(), block.toString(), Color.gray, false));
	//		}
	//		/*
	//		 * Area fullArea = expandBuildingForCollapsingBlockade(b, getMaximumBlockadeSize(b));
	//		 * debug.setBackground(infos);
	//		 * debug.show("estimateBlockadeRandomNumber", new AWTShapeInfo(blockade.getPosition().getShape(), "Road of the blockade" + blockade.getPosition(), Color.RED.brighter(), true),
	//		 * new AWTShapeInfo(blockade.getShape(), blockade.toString(), Color.green, false),
	//		 * new AWTShapeInfo(b.getShape(), b.toString(), Color.green, false),
	//		 * new AWTShapeInfo(fullArea, "fullarea", Color.blue, false));
	//		 */
	//		return b;
	//
	//	}
	//
	public final class SortComparator implements java.util.Comparator<SOSBlockade>, java.io.Serializable {
		private static final long serialVersionUID = -123456789123525L;

		@Override
		public int compare(SOSBlockade ro1, SOSBlockade ro2) {
			if (ro1.getCenteroid().getX() > ro2.getCenteroid().getX() || ro1.getCenteroid().getX() == ro2.getCenteroid().getX() && ro1.getCenteroid().getY() > ro2.getCenteroid().getY())
				return 1;
			return -1;
		}
	}

	//	public void findOrCreateSOSBlockadesOfBuilding(Building building, double blockadeSize, TreeSet<Road> changedRoadsReachability) {
	//		if (!building.isSosblockCreated()) {
	//			createBlockades(building, blockadeSize, changedRoadsReachability);
	//			ArrayList<SOSBlockade> blocks = model().block.middleBlockades().get(building);
	//			for (int i = 0; i < blocks.size(); i++) {
	//				changedRoadsReachability.add(blocks.get(i).getPosition());
	//				if (blocks.get(i).isFoggyBlockade()) {
	//					model().block.removeMiddleBlockade(blocks.get(i));
	//					blocks.remove(i);
	//					i--;
	//				}
	//			}
	//			Collections.sort(blocks, new SortComparator());
	//			// for (int i = 0; i < blocks.size(); i++) {
	//			// blocks.get(i).setBuildingBlockadeIndex(i);
	//			// }
	//		} else {
	//			beLog.warn("Blockades of " + building + " has been created so it will not recreate");
	//		}
	//	}
	/*
	 * private ArrayList<SOSBlockade> createBlockades(Building building, double blockadeSize, TreeSet<Road> changedRoadsReachability) {
	 * beLog.logln("");
	 * long startTime = System.currentTimeMillis();
	 * ArrayList<SOSBlockade> sosBlockadeList = new ArrayList<SOSBlockade>();
	 * // if (building == null) {
	 * // beLog.error("Null Building??? why used Create blockade? ", new NullPointerException());
	 * // return sosBlockadeList;
	 * // }
	 * building.setIsSosblockCreated(true);
	 * building.setBlockSize((int) blockadeSize);
	 * if (blockadeSize <= 0) {
	 * beLog.debug("blockadeSize <= 0 so no blockade is exist!", new NullPointerException());
	 * return sosBlockadeList;
	 * }
	 * long startTime1 = System.currentTimeMillis();
	 * java.awt.geom.Area fullArea = expandBuildingForCollapsingBlockade(building, blockadeSize);
	 * beLog.trace("--expandBuildingForCollapsingBlockade time usage: " + (System.currentTimeMillis() - startTime1));
	 * // fullArea.subtract(new Area(building.getShape()));
	 * // Find existing blockade areas
	 * // java.awt.geom.Area existing = new java.awt.geom.Area();
	 * for (StandardEntity e : model().getEntitiesOfType(StandardEntityURN.BLOCKADE)) {
	 * Blockade blockade = (Blockade) e;
	 * existing.add(blockadeToArea(blockade));
	 * }
	 * // // Intersect wall areas with roads
	 * HashMap<Road, List<SOSBlockade>> blockadeAreas = createRoadBlockades(fullArea, building);
	 * for (Entry<Road, List<SOSBlockade>> block : blockadeAreas.entrySet()) {
	 * for (SOSBlockade sosBlock : block.getValue()) {
	 * sosBlockFullArea.add(blockArea); // forLog
	 * SOSBlockade relevanceMiddleBlock;
	 * if (block.getValue().size() > 1) {
	 * relevanceMiddleBlock = findRelevanceMiddleBlockForAbnormalBlocks(sosBlock);
	 * } else {
	 * relevanceMiddleBlock = findRelevanceMiddleBlock(sosBlock);
	 * }
	 * beLog.info("updating MiddleBlockadeToSOSBlock");
	 * model().block.updateMiddleBlockadeToSOSBlock(relevanceMiddleBlock, sosBlock.getApexes());
	 * sosBlockadeList.add(relevanceMiddleBlock);
	 * }
	 * changedRoadsReachability.add(block.getKey());
	 * }
	 * beLog.info("createBlockades(for " + building + " size:" + blockadeSize + ") time usage: " + (System.currentTimeMillis() - startTime));
	 * // if (building.getID().getValue() == 298) {
	 * // java.awt.geom.Area sosBlockFullArea = new java.awt.geom.Area();
	 * // ArrayList<ShapeInfo> front = new ArrayList<ShapeDebugFrame.ShapeInfo>();
	 * // front.add(new ShapeDebugFrame.AWTShapeInfo(building.getShape(), "Original building area" + building, Color.RED, true));
	 * // front.add(new ShapeDebugFrame.AWTShapeInfo(fullArea, "Expanded building area (d = " + blockadeSize + ")", Color.BLACK, false));
	 * // front.add(new ShapeDebugFrame.AWTShapeInfo(sosBlockFullArea, "sosBlockFullArea", Color.green, false));
	 * // front.add(new ShapeDebugFrame.DetailInfo("blockadeAreas: " + blockadeAreas));
	 * // ArrayList<ShapeInfo> back = new ArrayList<ShapeDebugFrame.ShapeInfo>();
	 * // for (SOSBlockade sosBlockade : sosBlockadeList) {
	 * // front.add(new ShapeDebugFrame.AWTShapeInfo(sosBlockade.getShape(), sosBlockade.toString(), Color.green, false));
	 * // back.add(new AWTShapeInfo(sosBlockade.getPosition().getShape(), sosBlockade.getPosition().toString(), Color.blue, false));
	 * // }
	 * // for (Blockade blockade : model().blockades()) {
	 * // back.add(new AWTShapeInfo(blockade.getShape(), blockade.toString(), Color.lightGray, false));
	 * // }
	 * // debug.setBackground(back);
	 * // debug.show("Collapsed building", front);
	 * // }
	 * return sosBlockadeList;
	 * }
	 */
	//	private SOSBlockade findRelevanceMiddleBlockForAbnormalBlocks(SOSBlockade sosBlock) {
	//		beLog.info("findRelevanceMiddleBlockForAbnormalBlocks(" + sosBlock + ")");
	//		ArrayList<SOSBlockade> probableRelevanceblockList = new ArrayList<SOSBlockade>();
	//
	//		for (SOSBlockade middleBlock : sosBlock.getPosition().getMiddleBlockades()) {
	//			if (middleBlock.getOwnerBuilding() == sosBlock.getOwnerBuilding() && middleBlock.isFoggyBlockade())
	//				probableRelevanceblockList.add(middleBlock);
	//		}
	//
	//		if (probableRelevanceblockList.isEmpty()) {
	//			model().block.newMiddleBlockade(sosBlock);
	//			beLog.debug("that was newMiddleBlockade and added to world model(" + sosBlock + ")");
	//			return sosBlock;
	//		} else {
	//			beLog.debug("probableRelevanceblockList:" + probableRelevanceblockList + " and choosed the first:");
	//			return probableRelevanceblockList.get(0);
	//		}
	//	}

	//	private SOSBlockade findRelevanceMiddleBlock(SOSBlockade sosBlock) {
	//		beLog.info("findRelevanceMiddleBlock(" + sosBlock + ")");
	//		for (SOSBlockade middleBlock : sosBlock.getPosition().getMiddleBlockades()) {
	//			if (middleBlock.getOwnerBuilding() == sosBlock.getOwnerBuilding()) {
	//				beLog.debug("RelevanceMiddleBlock:" + middleBlock);
	//				return middleBlock;
	//			}
	//		}
	//		beLog.debug("that was newMiddleBlockade and added to world model(" + sosBlock + ")");
	//		model().block.newMiddleBlockade(sosBlock);
	//		return sosBlock;
	//	}

	/*
	 * public void findBuildingMayCreateBlockadesOnRoad(Building building) {
	 * long startTime = System.currentTimeMillis();
	 * int blockadeSize = (int) getMaximumBlockadeSize(building);
	 * if (blockadeSize <= 0 || building == null)
	 * return;
	 * java.awt.geom.Area fullArea = staticMaxBlock.get(building.getID());
	 * if (fullArea == null) {
	 * fullArea = building.getMaxBlockadeArea();
	 * staticMaxBlock.put(building.getID(), fullArea);
	 * }
	 * HashMap<Road, List<SOSBlockade>> blockadeAreas = createRoadBlockades(fullArea, building);
	 * for (Road roadOfMaxBlock : blockadeAreas.keySet()) {
	 * roadOfMaxBlock.addNearBuildings(building);
	 * }
	 * beLog.info("createMaxBlockades(for " + building + ") time usage: " + (System.currentTimeMillis() - startTime));
	 * }
	 */
	public HashMap<Road, List<SOSBlockade>> createMiddleBlockades(Building building) {
		long startTime = System.currentTimeMillis();
		// HashSet<SOSBlockade> sosBlockadeList = new HashSet<SOSBlockade>();

		int blockadeSize = (int) (getMaximumBlockadeSize(building) * getColapsePercent());
		Area fullArea = expandBuildingForCollapsingMiddleBlockade(building, blockadeSize);
		HashMap<Road, List<SOSBlockade>> blockadeAreas = createRoadBlockades(fullArea, building);
		for (Entry<Road, List<SOSBlockade>> block : blockadeAreas.entrySet()) {
			for (SOSBlockade sosBlock : block.getValue()) {
				model().block.newMiddleBlockade(sosBlock);
				/* sosBlockadeList.add(sosBlock); */
			}

		}
		beLog.info("createMiddleBlockades(for " + building + ") time usage: " + (System.currentTimeMillis() - startTime));
		return blockadeAreas;
		// return sosBlockadeList;
	}

	private double getColapsePercent() {
		double percent = MIDDLE_COLLAPSE_PERCENT;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Eindhoven)
		//			percent=Rnd.get01()*0.60;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Mexico)
		//			percent=Rnd.get01()*0.10;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Berlin)
		//			percent=Rnd.get01()*0.75;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Paris)
		//			percent=Rnd.get01()*0.75;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Kobe)
		//			percent=Rnd.get01()*0.75;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.VC)
		//			percent=Rnd.get01()*0.75;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Istanbul)
		//			percent=Rnd.get01()*0.75;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Big)
		//			percent=Rnd.get01()*0.75;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Medium)
		//			percent=Rnd.get01()*0.75;
		//		if(model.me().getAgent().getMapInfo().getRealMapName()==MapName.Small)
		//			percent=Rnd.get01()*0.75;
		return percent;
	}

	private Area expandBuildingForCollapsingMiddleBlockade_NEW(Building building, int blockadeSize) {
		java.awt.geom.Area fullArea = new Area(building.getShape());

		fullArea.transform(AffineTransform.getScaleInstance(1.5, 1.5));
		return fullArea;
	}

	private Area expandBuildingForCollapsingMiddleBlockade(Building b, int blockSize) {
		java.awt.geom.Area fullArea;
		Path2D path = new Path2D.Double();

		//if (AliGeometryTools.havecorrectDirection(b)) {
		for (Edge edge : b.getEdges()) {
			projectWallPathForMiddleBlockade(edge, blockSize, b, path);
		}
		fullArea = new Area(path);
		//} else {
		//fullArea = new Area(b.getShape());
		//}
		if (AliGeometryTools.havecorrectDirection(b))
			fullArea.add(new Area(b.getShape()));
		// fullArea.add(projectApex_New(GeometryTools2D.vertexArrayToPoints(b.getApexList()), blockSize, fullArea));
		projectApexForMiddleBlockade(GeometryTools2D.vertexArrayToPoints(b.getApexList()), blockSize, fullArea);
		// debug.show("b",
		// new AWTShapeInfo(b.getShape(), b.toString(), Color.red.brighter(), true),
		// new AWTShapeInfo(fullArea, "fullArea", Color.green, false),
		// // new ShapeDebugFrame.Point2DShapeInfo(firstP, firstP.toString(), Color.white, true),
		// // new ShapeDebugFrame.Point2DShapeInfo(pos.get(0), "pos.get(0)" + pos.get(0).toString(), Color.BLACK, true),
		// // new ShapeDebugFrame.Point2DShapeInfo(pos.get(i), " current" + pos.get(i).toString(), Color.yellow, true),
		// new AWTShapeInfo(ellipsePath, "ellipsePath", Color.blue, false));
		// fullArea.add(new Area(ellipsePath));
		return fullArea;

	}

	private void projectApexForMiddleBlockade(List<Point2D> vertexes, int blockSize, Area fullArea) {
		for (Point2D p : vertexes) {
			Rectangle2D ellipse1 = new Rectangle2D.Double(p.getX() - blockSize, p.getY() - blockSize, blockSize * 2, blockSize * 2);
			fullArea.add(new java.awt.geom.Area(/* AliGeometryTools.getShape(AliGeometryTools.getApexes */(ellipse1))/* ) */);
		}
	}

	private void projectWallPathForMiddleBlockade(Edge edge, int blockSize, Building b, Path2D path) {
		Line2D wallLine = edge.getLine();// new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());
		Vector2D offset = wallLine.getDirection().getNormal().normalised().scale(-blockSize);
		Point2D fourth = wallLine.getOrigin().plus(offset);
		Point2D third = wallLine.getEndPoint().plus(offset);

		if (path.getCurrentPoint() == null)
			path.moveTo(fourth.getX(), fourth.getY());
		else
			path.lineTo(fourth.getX(), fourth.getY());

		path.lineTo(third.getX(), third.getY());

	}

	// private HashMap<Road, List<SOSBlockade>> getBlockadeAreasFromSavedPlace(Building building) {
	// // HashMap<Road, List<SOSBlockade>> st = staticMiddleBlock_road_sosBlock.get(building);
	// if(st==null)
	// return null;
	//
	// HashMap<Road, List<SOSBlockade>> result=new HashMap<Road, List<SOSBlockade>>();
	// for (Entry<Road, List<SOSBlockade>> road_Blocklist : st.entrySet()) {
	// ArrayList<SOSBlockade> newList=new ArrayList<SOSBlockade>(road_Blocklist.getValue().size());
	// for (SOSBlockade savedBlock : road_Blocklist.getValue()) {
	// SOSBlockade s = new SOSBlockade(null, savedBlock.getPosition(), savedBlock.getOwnerBuilding());
	// s.setApexes(savedBlock.getApexes().clone());
	// newList.add(s);
	// }
	// result.put(road_Blocklist.getKey(), newList);
	// }
	// return result;
	// }

	public static java.awt.geom.Area expandBuildingForCollapsingBlockade(Building b, double blockSize) {
		java.awt.geom.Area fullArea;
		Path2D path = new Path2D.Double();

		if (AliGeometryTools.havecorrectDirection(b)) {
			for (Edge edge : b.getEdges()) {
				projectWallPath(edge, blockSize, b, path);
			}
			fullArea = new Area(path);
		} else {
			fullArea = new Area(b.getShape());
		}
		// fullArea.add(projectApex_New(GeometryTools2D.vertexArrayToPoints(b.getApexList()), blockSize, fullArea));
		projectApex(GeometryTools2D.vertexArrayToPoints(b.getApexList()), blockSize, fullArea);
		// debug.show("b",
		// new AWTShapeInfo(b.getShape(), b.toString(), Color.red.brighter(), true),
		// new AWTShapeInfo(fullArea, "fullArea", Color.green, false),
		// // new ShapeDebugFrame.Point2DShapeInfo(firstP, firstP.toString(), Color.white, true),
		// // new ShapeDebugFrame.Point2DShapeInfo(pos.get(0), "pos.get(0)" + pos.get(0).toString(), Color.BLACK, true),
		// // new ShapeDebugFrame.Point2DShapeInfo(pos.get(i), " current" + pos.get(i).toString(), Color.yellow, true),
		// new AWTShapeInfo(ellipsePath, "ellipsePath", Color.blue, false));
		// fullArea.add(new Area(ellipsePath));
		return fullArea;
	}

	private static void projectApex(List<Point2D> vertexes, double blockSize, Area fullArea) {
		for (Point2D p : vertexes) {
			Ellipse2D ellipse1 = new Ellipse2D.Double(p.getX() - blockSize, p.getY() - blockSize, blockSize * 2, blockSize * 2);
			fullArea.add(new java.awt.geom.Area(/* AliGeometryTools.getShape(AliGeometryTools.getApexes */(ellipse1))/* ) */);
		}
	}

	private static void projectWallPath(Edge edge, double blockSize, Building b, Path2D path) {
		Line2D wallLine = edge.getLine();// new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());
		Vector2D offset = wallLine.getDirection().getNormal().normalised().scale(-blockSize);
		Point2D fourth = wallLine.getOrigin().plus(offset);
		Point2D third = wallLine.getEndPoint().plus(offset);

		if (path.getCurrentPoint() == null)
			path.moveTo(fourth.getX(), fourth.getY());
		else
			path.lineTo(fourth.getX(), fourth.getY());

		path.lineTo(third.getX(), third.getY());

	}

	private static Area projectApex_New(List<Point2D> vertexes, double blockSize, Building b, Area fullArea) {
		Path2D ellipsePath = new Path2D.Double();

		for (Point2D p : vertexes) {
			Ellipse2D ellipse1 = new Ellipse2D.Double(p.getX() - blockSize, p.getY() - blockSize, blockSize * 2, blockSize * 2);
			// AliGeometryTools.getShape(AliGeometryTools.getApexes(ellipse1));
			int[] apx = AliGeometryTools.getApexes(ellipse1);
			List<Point2D> pos = GeometryTools2D.vertexArrayToPoints(apx);
			Point2D firstP = null;
			for (int i = 0; i < pos.size(); i++) {
				if (i + 1 < pos.size() && fullArea.contains(pos.get(i).toGeomPoint()) && fullArea.contains(pos.get((i + 1)).toGeomPoint()))
					if (!(i > 0 && !fullArea.contains(pos.get(i - 1).toGeomPoint()) && fullArea.contains(pos.get((i)).toGeomPoint())))
						continue;

				if (firstP == null) {
					ellipsePath.moveTo(pos.get(i).getX(), pos.get(i).getY());
					firstP = pos.get(i);
				} else
					ellipsePath.lineTo(pos.get(i).getX(), pos.get(i).getY());
				// ellipsePath.lineTo(pos.get(i + 1).getX(), pos.get(i + 1).getY());

			}
			if (firstP != null)
				ellipsePath.lineTo(firstP.getX(), firstP.getY());
		}
		// debug.show("b",
		// new AWTShapeInfo(b.getShape(), b.toString(), Color.red.brighter(), true),
		// new AWTShapeInfo(fullArea, "fullArea", Color.green, false),
		// // // new ShapeDebugFrame.Point2DShapeInfo(firstP, firstP.toString(), Color.white, true),
		// // // new ShapeDebugFrame.Point2DShapeInfo(pos.get(0), "pos.get(0)" + pos.get(0).toString(), Color.BLACK, true),
		// // // new ShapeDebugFrame.Point2DShapeInfo(pos.get(i), " current" + pos.get(i).toString(), Color.yellow, true),
		// new AWTShapeInfo(ellipsePath, "ellipsePath", Color.blue, false));
		return new Area(ellipsePath);
	}

	private static Area projectWall(Edge edge, double d, Building b) {

		java.awt.geom.Area projectedArea = new java.awt.geom.Area();
		Line2D wallLine = edge.getLine();// new Line2D(edge.getStartX(), edge.getStartY(), edge.getEndX() - edge.getStartX(), edge.getEndY() - edge.getStartY());
		Vector2D offset = wallLine.getDirection().getNormal().normalised().scale(-d);

		Path2D path = new Path2D.Double();
		Point2D first = wallLine.getOrigin();
		Point2D second = wallLine.getEndPoint();
		Point2D third = second.plus(offset);
		Point2D fourth = first.plus(offset);

		path.moveTo(first.getX(), first.getY());
		path.lineTo(second.getX(), second.getY());
		path.lineTo(third.getX(), third.getY());
		path.lineTo(fourth.getX(), fourth.getY());
		java.awt.geom.Area wallArea = new java.awt.geom.Area(path);
		//
		// int[] x = { (int) first.getX(), (int) second.getX(), (int) third.getX(), (int) fourth.getX() };
		// int[] y = { (int) first.getY(), (int) second.getY(), (int) third.getY(), (int) fourth.getY() };
		// Polygon r = new Polygon(x, y, 4);
		// projectedArea.add(new Area(r));
		projectedArea.add(wallArea);
		// Also add circles at each corner
		// double radius = offset.getLength();

		// Point2D pppp = new Point2D((third.getX() + fourth.getX()) / 2, (third.getY() + fourth.getY()) / 2);
		// debug.show("Collapsed building",
		// new ShapeDebugFrame.AWTShapeInfo(b.getShape(), "Original building area", Color.RED, true),
		// new ShapeDebugFrame.Line2DShapeInfo(wallLine, "Wall edge", Color.WHITE, true, true),
		// new ShapeDebugFrame.AWTShapeInfo(r, "Wall area (d = " + d + ")", Color.GREEN, false),
		// // new ShapeDebugFrame.AWTShapeInfo(ellipse1, "Ellipse 1", Color.BLUE, false),
		// // new ShapeDebugFrame.AWTShapeInfo(ellipse2, "Ellipse 2", Color.ORANGE, false),
		// new ShapeDebugFrame.Point2DShapeInfo(pppp, "pppp", Color.LIGHT_GRAY, true)
		// );

		return projectedArea;
	}

	private HashMap<Road, List<SOSBlockade>> createRoadBlockades(java.awt.geom.Area buildingArea/* , java.awt.geom.Area existing */, Building building) {
		long startTime = System.currentTimeMillis();
		HashMap<Road, List<SOSBlockade>> blockadeAreas = new HashMap<Road, List<SOSBlockade>>();
		Collection<Road> roads = model().getObjectsInRectangle(buildingArea.getBounds(), Road.class);
		for (Road r : roads) {
			java.awt.geom.Area intersection = r.getGeomArea();
			intersection.intersect(buildingArea);
			// intersection.subtract(existing);
			if (intersection.isEmpty()) {
				continue;
			}
			List<Area> blockAreas = AliGeometryTools.fix(intersection);
			ArrayList<SOSBlockade> sosBlockList = new ArrayList<SOSBlockade>();
			for (Area blockArea : blockAreas) {
				int[] apexes = AliGeometryTools.getApexes(blockArea);
				if (apexes.length >= 6) {
					SOSBlockade sosBlock = new SOSBlockade(apexes, r);
					if (sosBlock.getRepairCost() > 0) {
						sosBlockList.add(sosBlock);
					}
				}
				blockadeAreas.put(r, sosBlockList);
			}
			/*
			 * debug.show("Road blockage",
			 * new ShapeDebugFrame.AWTShapeInfo(buildingArea, "Building area", Color.BLACK, false),
			 * new ShapeDebugFrame.AWTShapeInfo(roadArea, "Road area", Color.BLUE, false),
			 * new ShapeDebugFrame.AWTShapeInfo(intersection, "Intersection", Color.GREEN, true)
			 * );
			 */
		}
		beLog.info("----createRoadBlockades time usage: " + (System.currentTimeMillis() - startTime));
		return blockadeAreas;
	}

	//	/**
	//	 * @author1 Salim
	//	 * @author2 Ali
	//	 * @param blockade
	//	 * @param nonBreakedBlockPoints
	//	 * @return
	//	 */
	//	private Building ownerBuilding(Blockade blockade, List<Point2D> nonBreakedBlockPoints) {
	//		beLog.info("find owner building for " + blockade);
	//		Road road = blockade.getPosition();
	//		ArrayList<Building> buildings = road.collapseNearBuildings();
	//		Pair<Building, Double> choosedBuilding_Threshold = new Pair<Building, Double>(null, Double.MAX_VALUE);
	//		for (Building building : buildings) {
	//			double threshold = thresholdSteadyDistance(blockade, nonBreakedBlockPoints, building.getMaxBlockadeArea(), road, building);
	//			if (threshold >= 0 && choosedBuilding_Threshold.second() >= threshold) {
	//				choosedBuilding_Threshold = new Pair<Building, Double>(building, threshold);
	//			}
	//
	//		}
	//		beLog.debug("choosed Owner Building[" + choosedBuilding_Threshold.first() + "] Threshold=" + choosedBuilding_Threshold.second());
	//		return choosedBuilding_Threshold.first();
	//	}
	//
	//	private Building lowBreakPointOwnerBuilding(Blockade blockade, List<Point2D> nonBreakedBlockPoints) {
	//		beLog.info("find owner building for low breaked edge " + blockade);
	//		Road road = blockade.getPosition();
	//		int i = 0;
	//		ArrayList<Building> buildings = road.collapseNearBuildings();
	//		Pair<Building, Double> choosedBuilding_Threshold = new Pair<Building, Double>(null, Double.MAX_VALUE);
	//		for (Building building : buildings) {
	//			double threshold = thresholdSteadyDistance(blockade, nonBreakedBlockPoints, building.getMaxBlockadeArea(), road, building);
	//			if (threshold >= 0 && threshold < 100 && choosedBuilding_Threshold.second() >= threshold) {
	//				i++;
	//				choosedBuilding_Threshold = new Pair<Building, Double>(building, threshold);
	//			}
	//
	//		}
	//		if (i == 1) {
	//			beLog.debug("choosed Owner Building[" + choosedBuilding_Threshold.first() + "] Threshold=" + choosedBuilding_Threshold.second());
	//			return choosedBuilding_Threshold.first();
	//		} else {
	//			beLog.trace(" current NOT choosed building is [" + choosedBuilding_Threshold.first() + ", Threshold:" + choosedBuilding_Threshold.second() + "] Threshold=" + choosedBuilding_Threshold.second());
	//			beLog.debug("No Building choosed because BE found '" + i + "' possible building");
	//			return null;
	//		}
	//	}

	//	/**
	//	 * @param nonBreakedBlockPoints
	//	 * @author1 Salim
	//	 * @author2 Ali
	//	 * @param blockadeEdges
	//	 * @param maxBlockadeEdges
	//	 * @param road
	//	 * @param building
	//	 * @return
	//	 */
	//	private double thresholdSteadyDistance(Blockade blockade, List<Point2D> nonBreakedBlockPoints, Area maxBlockade, Road road, Building building) {
	//		if (!AliGeometryTools.containsAll(maxBlockade, nonBreakedBlockPoints))
	//			return -1;
	//		List<Line2D> maxBlockadeEdges = AliGeometryTools.getLines(maxBlockade);
	//
	//		double threshold = -1;
	//		double minD = Double.MAX_VALUE, maxD = -1;
	//		double dCenteroid = AliGeometryTools.getClosestDistance(maxBlockadeEdges, blockade.getCenteroid());
	//		for (Point2D point2d : nonBreakedBlockPoints) {
	//			double dtmp = AliGeometryTools.getClosestDistance(maxBlockadeEdges, point2d);
	//			minD = Math.min(minD, dtmp);
	//			maxD = Math.max(maxD, dtmp);
	//		}
	//		if (minD > dCenteroid) {
	//			minD = Double.MAX_VALUE;
	//			maxD = -1;
	//		}
	//		threshold = AliGeometryTools.computeThreshold(minD, maxD);
	//		// debug.activate();
	//
	//		// if (blockade.getID().getValue() == 1054) {
	//		// debug.show("haveSteadyDistance",
	//		// new ShapeDebugFrame.Line2DShapeInfo(maxBlockadeEdges, "maxBlockLine", Color.blue, false, true),
	//		// new ShapeDebugFrame.AWTShapeInfo(blockade.getShape(), "Blockade" + blockade, Color.darkGray, false),
	//		// new AWTShapeInfo(road.getShape(), road.toString(), Color.magenta, false),
	//		// new ShapeDebugFrame.Points2DShapeInfo(nonBreakedBlockPoints, "BlockadeEdges", Color.white, true),
	//		// new ShapeDebugFrame.Line2DShapeInfo(GeometryTools2D.pointsToLines(nonBreakedBlockPoints, false), "BlockadeEdgesLine", Color.white, false, true),
	//		// new AWTShapeInfo(building.getShape(), building.toString(), Color.orange, false),
	//		// new ShapeDebugFrame.DetailInfo("threshold: " + threshold)
	//		// );
	//		// }
	//		//
	//
	//		return threshold;
	//	}

	//	/**
	//	 * @author1 Salim
	//	 * @author2 Ali
	//	 * @param e
	//	 * @param roadedges
	//	 * @param road
	//	 * @param blockade
	//	 * @return
	//	 */
	private boolean isBreakedEdge(Edge e, List<Edge> roadedges, BlockadeInterface blockade, Road road) {
		// if (e.getLine().getLength() < 50)????XXX is needed?
		// return false;
		for (Edge edge : roadedges) {
			if (AliGeometryTools.haveAccordance(edge.getLine(), e.getLine(), 10) || (!blockade.getPosition().getShape().contains(e.getMidPoint().toGeomPoint())))
				return true;
		}
		return false;
	}

	private SOSWorldModel model() {
		return (SOSWorldModel) model;
	}

	//	public void clearSOSBlockade(SOSBlockade sosBlockade, int clearTime) {
	//		/* Shape oldBlockShape = sosBlockade.getShape(); */
	//		beLog.info("Clearing " + sosBlockade + " clear time:" + clearTime);
	//		double original = sosBlockade.getRepairCost();
	//		double current = original - SOSAgent.BLOCKADE_REPAIR_RATE * clearTime;
	//		if (clearTime <= 0)
	//			return;
	//		if (current <= 0) {
	//			beLog.debug("clearing cause removing the SOSblockade and middleBlockade");
	//			model().block.removeSOSBlockade(sosBlockade);
	//			model().block.removeMiddleBlockade(sosBlockade);
	//			sosBlockade.setClearedTime(sosBlockade.getClearedTime() + 1);
	//			return;
	//		}
	//		double d = current / original;
	//		int[] apexes = sosBlockade.getApexes();
	//		double cx = sosBlockade.getX();
	//		double cy = sosBlockade.getY();
	//		for (int i = 0; i < apexes.length; i += 2) {
	//			double x = apexes[i];
	//			double y = apexes[i + 1];
	//			double dx = x - cx;
	//			double dy = y - cy;
	//			// Shift both x and y so they are now d * dx from the center
	//			double newX = cx + (dx * d);
	//			double newY = cy + (dy * d);
	//			apexes[i] = (int) newX;
	//			apexes[i + 1] = (int) newY;
	//			/*
	//			 * debug.show("shirniking", new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "old point", Color.magenta, true)
	//			 * , new ShapeDebugFrame.Point2DShapeInfo(new Point2D(newX, newY), "new point", Color.orange, true));
	//			 */
	//		}
	//		sosBlockade.setApexes(apexes);
	//		/*
	//		 * debug.activate();
	//		 * debug.show("clearSOSBlockade", new AWTShapeInfo(oldBlockShape, "before clear " + sosBlockade, Color.white, true),
	//		 * new AWTShapeInfo(sosBlockade.getShape(), "new Blockade" + sosBlockade, Color.orange, false));
	//		 */
	//	}

	public Blockade unclearBlockade(Blockade blockade, int unClearTime) {
		beLog.info("unclearBlockade " + blockade + " unClearTime:" + unClearTime);
		Blockade newBlock = (Blockade) blockade.copy();
		int[] apexes = blockade.getApexes();
		int[] newApexes = new int[apexes.length];
		/* Shape oldBlockShape = new Area(blockade.getShape()); */
		if (unClearTime > 0) {
			double original = blockade.getRepairCost();
			double current = original + SOSAgent.BLOCKADE_REPAIR_RATE * unClearTime;
			double d = current / original;
			double cx = newBlock.getX();
			double cy = newBlock.getY();
			for (int i = 0; i < apexes.length; i += 2) {
				double x = apexes[i];
				double y = apexes[i + 1];
				double dx = x - cx;
				double dy = y - cy;
				// Shift both x and y so they are now d * dx from the centre
				double newX = cx + (dx * d);
				double newY = cy + (dy * d);
				apexes[i] = (int) newX;
				apexes[i + 1] = (int) newY;
				/*
				 * debug.show("shirniking", new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "old point", Color.magenta, true)
				 * , new ShapeDebugFrame.Point2DShapeInfo(new Point2D(newX, newY), "new point", Color.orange, true));
				 */
			}
		} else {
			newApexes = apexes;
		}
		newBlock.setApexes(newApexes);
		/*
		 * debug.activate();
		 * if (clearTime != 0) {
		 * debug.show("unclearBlockade", new AWTShapeInfo(oldBlockShape, "before unclear " + blockade, Color.white, true),
		 * new AWTShapeInfo(newBlock.getShape(), "new Blockade" + blockade, Color.orange, false));
		 * }
		 */
		return newBlock;
	}

	public SOSBlockade unclearBlockadeInterFace(BlockadeInterface blockade, int unClearTime) {
		int[] apexes = blockade.getApexes();
		int[] newApexes = new int[apexes.length];
		/* Shape oldBlockShape = new Area(blockade.getShape()); */
		if (unClearTime > 0) {
			double original = blockade.getRepairCost();
			double current = original + SOSAgent.BLOCKADE_REPAIR_RATE * unClearTime;
			double d = current / original;
			double cx = blockade.getX();
			double cy = blockade.getY();
			for (int i = 0; i < apexes.length; i += 2) {
				double x = apexes[i];
				double y = apexes[i + 1];
				double dx = x - cx;
				double dy = y - cy;
				// Shift both x and y so they are now d * dx from the centre
				double newX = cx + (dx * d);
				double newY = cy + (dy * d);
				newApexes[i] = (int) newX;
				newApexes[i + 1] = (int) newY;
				/*
				 * debug.show("shirniking", new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "old point", Color.magenta, true)
				 * , new ShapeDebugFrame.Point2DShapeInfo(new Point2D(newX, newY), "new point", Color.orange, true));
				 */
			}
		} else {
			newApexes = apexes;
		}
		SOSBlockade sb = new SOSBlockade(newApexes, blockade.getPosition());
		// sb.setJustApexes(newApexes);
		/*
		 * debug.activate();
		 * if (clearTime != 0) {
		 * debug.show("unclearBlockade", new AWTShapeInfo(oldBlockShape, "before unclear " + blockade, Color.white, true),
		 * new AWTShapeInfo(newBlock.getShape(), "new Blockade" + blockade, Color.orange, false));
		 * }
		 */
		return sb;
	}

	// public int computeNewCostByd(BlockadeInterface blockade, double d) {
	// int[] apexes = blockade.getApexes();
	// int[] newApexes = new int[apexes.length];
	// double cx = blockade.getX();
	// double cy = blockade.getY();
	// for (int i = 0; i < apexes.length; i += 2) {
	// double x = apexes[i];
	// double y = apexes[i + 1];
	// double dx = x - cx;
	// double dy = y - cy;
	// // Shift both x and y so they are now d * dx from the centre
	// double newX = cx + (dx * d);
	// double newY = cy + (dy * d);
	// newApexes[i] = (int) newX;
	// newApexes[i + 1] = (int) newY;
	//
	// }
	// SOSBlockade sb = new SOSBlockade(null, null, null);
	// sb.setJustApexes(newApexes);
	// // debug.show("findTimeOfClear", new AWTShapeInfo(sb.getShape(), "EXPANDED BLOCKADE", Color.magenta, false));
	// return (int) (GeometryTools2D.computeArea(GeometryTools2D.vertexArrayToPoints(newApexes)) * SOSBlockade.REPAIR_COST_FACTOR);
	// }

	public void preCompute() {

		if (doFastPrecompute(model().sosAgent().getMapInfo().getRealMapName())) {
			for (Road r : model().roads()) {
				SOSBlockade sosBlock = new SOSBlockade(new Area(r.getShape()), r);
				if (sosBlock.getApexes().length >= 6 && sosBlock.getRepairCost() > 0) {
					model().block.newMiddleBlockade(sosBlock);
				}
			}
		} else {

			for (Building building : model().buildings()) {
				/*HashMap<Road, List<SOSBlockade>> tmp =*/ createMiddleBlockades(building);


			}
		//	FileOperations.Write(middleBlocksPreComputeFile, preComputeContent);
		}
		//			if (!SOSConstant.IS_CHALLENGE_RUNNING)
		//				writeMiddleBlockadeToFile(middleBlocksFile, buildingMiddleBlock);
		//		}
	}

	public static boolean doFastPrecompute(MapName mapName) {
		switch (mapName) {
		case Berlin:
		case Istanbul:
		case Kobe:
		case Paris:
		case Small:
		case VC:
			return false;

		case Big:
		case Medium:
		case Eindhoven:
		case Mexico:
		case MexicoSmall:
		case Unknown:
		default:
			return true;
		}

	}

	public Blockade clearBlockade(Blockade blockade, int clearTime) {
		Blockade b = (Blockade) blockade.copy();
		/* Shape oldBlockShape = blockade.getShape(); */
		double original = blockade.getRepairCost();

		double current = original - SOSAgent.BLOCKADE_REPAIR_RATE * clearTime;
		if (current <= 0)
			return null;
		else {
			double d = current / original;
			int[] apexes = b.getApexes();
			double cx = b.getX();
			double cy = b.getY();
			for (int i = 0; i < apexes.length; i += 2) {
				double x = apexes[i];
				double y = apexes[i + 1];
				double dx = x - cx;
				double dy = y - cy;
				// Shift both x and y so they are now d * dx from the centre
				double newX = cx + (dx * d);
				double newY = cy + (dy * d);
				apexes[i] = (int) newX;
				apexes[i + 1] = (int) newY;
				/*
				 * debug.show("shirniking", new ShapeDebugFrame.Point2DShapeInfo(new Point2D(x, y), "old point", Color.magenta, true)
				 * , new ShapeDebugFrame.Point2DShapeInfo(new Point2D(newX, newY), "new point", Color.orange, true));
				 */
			}
			b.setApexes(apexes);
			b.setRepairCost((int) (GeometryTools2D.computeArea(GeometryTools2D.vertexArrayToPoints(b.getApexes())) * SOSBlockade.REPAIR_COST_FACTOR));
			// debug.show("clearSOSBlockade", new AWTShapeInfo(oldBlockShape, "before clear " + blockade, Color.white, true),
			// new AWTShapeInfo(b.getShape(), "new Blockade" + blockade, Color.orange, false));

			return b;
		}
	}

	public static double getMaximumBlockadeSize(Building b) {
		return b.isBlockadesDefined() ? FLOOR_HEIGHT * b.getFloors() * (b.getBrokenness() / MAX_COLLAPSE) : FLOOR_HEIGHT * b.getFloors() * 1;
	}

}
