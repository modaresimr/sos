package sos.base.reachablity;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import rescuecore2.geometry.GeometryTools2D;
import rescuecore2.geometry.Line2D;
import rescuecore2.geometry.Point2D;
import rescuecore2.geometry.Vector2D;
import sos.base.entities.Edge;
import sos.base.entities.StandardEntity;
import sos.base.reachablity.tools.ReachablityConstants;
import sos.base.reachablity.tools.SOSArea;
import sos.base.reachablity.tools.Utility;
import sos.base.util.sosLogger.SOSLoggerSystem;

public class ExpandBlockade {
	static SOSLoggerSystem log(StandardEntity entity) {
		return entity.getAgent().sosLogger.reachablity_ExpandBlock;
	}
//	static ShapeDebugFrame debug=new ShapeDebugFrame();
	// Morteza2011*****************************************************************
	public static SOSArea expandBlock(SOSArea blockade, int distance) {
		ArrayList<Edge> verifiedEdges = verifyEdgesForExpand(blockade.getEdges());
		ArrayList<Line2D> expandedLines = setExpandedLines(verifiedEdges, blockade.getShape(), distance);
		ArrayList<Point2D> intersectedPoints = setIntersecedPoints(verifiedEdges,expandedLines);
		ArrayList<Edge> expandedEdges = createNewEdges(intersectedPoints);
		SOSArea finalArea = new SOSArea(expandedEdges, blockade.getID());
		finalArea.setReachablityBlockades(blockade.getReachablityBlockades());
		return finalArea;
	}

	// Morteza2011*****************************************************************
	public static ArrayList<Edge> verifyEdgesForExpand(List<Edge> realEdges) {
		ArrayList<Edge> edges = new ArrayList<Edge>(realEdges);
		for (short i = 0; i < edges.size(); i++) {
			if (GeometryTools2D.getDistance(edges.get(i).getStart(), edges.get(i).getEnd()) < 20) {
				if (i > 0)
					edges.get(i - 1).setEnd(edges.get(i).getStart());
				else
					edges.get(edges.size() - 1).setEnd(edges.get(i).getStart());
				edges.remove(i--);
				continue;
			}
			for (short j = (short) (i + 1); j < edges.size(); j++) {
				if (edges.get(i).edgeEquals(edges.get(j))) {
					edges.remove(j--);
				}
			}
		}
		return edges;
	}

	// Morteza2011*****************************************************************
	private static ArrayList<Line2D> setExpandedLines(List<Edge> blockEdges, Shape shape, int distance) {
		ArrayList<Line2D> expandedLines = new ArrayList<Line2D>();
		ArrayList<Point2D> expandedPointsForTest = null;
		ArrayList<Point2D> expandedPoints = null;
		for (Edge e : blockEdges) {
			expandedPointsForTest = Utility.get2PointsAroundAPointOutOfLine(e.getStart(), e.getEnd(), e.getMidPoint(), ReachablityConstants.TEST_DISTANCE);
			expandedPoints = Utility.get2PointsAroundAPointOutOfLine(e.getStart(), e.getEnd(), e.getMidPoint(), distance);
			Line2D line;
			if (!shape.contains(expandedPointsForTest.get(0).getX(), expandedPointsForTest.get(0).getY()))
				line = new Line2D(expandedPoints.get(0), e.getLine().getDirection());
			else
				line = new Line2D(expandedPoints.get(1), e.getLine().getDirection());
			expandedLines.add(line);
		}
		return expandedLines;
	}

	// Morteza2011*****************************************************************
	private static ArrayList<Point2D> setIntersecedPoints(ArrayList<Edge> verifiedEdges, ArrayList<Line2D> expandedLines) {
		ArrayList<Point2D> intersectedPoints = new ArrayList<Point2D>();
		expandedLines.add(expandedLines.get(0));
		for (short i = 0; i < expandedLines.size() - 1; i++) {
			Point2D p = GeometryTools2D.getIntersectionPoint(expandedLines.get(i), expandedLines.get(i + 1));
			if (p != null){//Added in 2013 by Ali (be khatere inke age sare blockade tiz mibood deraz mishod va natijeye expand eshtebah mibood...)
				Line2D realLine = verifiedEdges.get(i).getLine();
				Point2D nearestPoint = GeometryTools2D.getClosestPointOnSegment(realLine, p);
				if(GeometryTools2D.getDistance(nearestPoint, p)>ReachablityConstants.BLOCK_EXPAND_WIDTH*1.4){

					Vector2D v=new Vector2D(p.getX()-nearestPoint.getX(), p.getY()-nearestPoint.getY());
					v=v.normalised().scale(ReachablityConstants.BLOCK_EXPAND_WIDTH);
					Line2D line=new Line2D(nearestPoint, v);

					ArrayList<Point2D> points2 = Utility.get2PointsAroundAPointOutOfLine(nearestPoint, p, line.getEndPoint(), 10000);
					Line2D newLine=new Line2D(points2.get(0), points2.get(1));

					Line2D expandedLine = expandedLines.get(i);
					Line2D expandedNextLine = expandedLines.get(i + 1);
					Point2D p1 = GeometryTools2D.getIntersectionPoint(expandedLine, newLine);
					Point2D p2 = GeometryTools2D.getIntersectionPoint(expandedNextLine, newLine);
					intersectedPoints.add(p1);
					intersectedPoints.add(p2);
					//////////////debug////////
//
//					Shape blockadeShape = Utility.newShape(verifiedEdges);
//					debug.show("debug", new ShapeDebugFrame.AWTShapeInfo(blockadeShape, "blockade", Color.black, true),
//							new ShapeDebugFrame.Point2DShapeInfo(p, "intersected old point", Color.white, true),
//							new ShapeDebugFrame.Line2DShapeInfo(expandedLines, "expandedLines", Color.yellow, false,false),
//							new Line2DShapeInfo(line, "end to intersected point", Color.blue, false, true),
//							new Line2DShapeInfo(newLine, "Ammod Line", Color.cyan, false, true),
//							new ShapeDebugFrame.DetailInfo("Distance end to intersected point:"+GeometryTools2D.getDistance(nearestPoint, line.getEndPoint())),
//							new Line2DShapeInfo(expandedLine, "expandedLine", Color.red, true, true),
//							new Line2DShapeInfo(expandedNextLine, "expandedNextLine", Color.orange, true, true),
//							new ShapeDebugFrame.Point2DShapeInfo(p1, "P1", Color.green, true),
//							new ShapeDebugFrame.Point2DShapeInfo(p2, "P2", Color.green.darker(), true)
//							);
				}else
					intersectedPoints.add(p);
			}
		}
		return intersectedPoints;
	}

	// Morteza2011*****************************************************************
	private static ArrayList<Edge> createNewEdges(ArrayList<Point2D> intersectedPoints) {
		ArrayList<Edge> createdEdges = new ArrayList<Edge>();
		intersectedPoints.add(intersectedPoints.get(0));
		for (short i = 0; i < intersectedPoints.size() - 1; i++) {
			Edge e = new Edge(intersectedPoints.get(i), intersectedPoints.get(i + 1));
			createdEdges.add(e);
		}
		return createdEdges;
	}

}
