package sos.base.reachablity.tools;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import sos.base.entities.Blockade;
import sos.base.entities.Edge;
import sos.base.entities.Road;
import sos.base.entities.ShapeableObject;

public class SOSArea implements ShapeableObject {
	private ArrayList<Edge> edges;// Morteza2011
	private Shape shape = null;// Morteza2011
	private ArrayList<SOSArea> holes = null;
	/*
	 * points was commented because it was only used inside a function. saving
	 * it and keeping it for later was not useful because it was never used
	 * anywhere again. private ArrayList<ArrayList<Pair<Point2D, Point2D>>>
	 * points = new ArrayList<ArrayList<Pair<Point2D, Point2D>>>();//
	 * Morteza2011
	 */
	private int ID;// Morteza2011
	private ArrayList<Blockade> reachablityblockades = null;// Morteza2011
	// private Point2D center = null; deleted by Salim on March 27 2011 because
	// every one simply used it's X and Y separately except one place in Search
	// which could be changed.
	private Road road = null;

	// Morteza2011*****************************************************************
	public SOSArea(List<Edge> list, Shape polygon) {
		this.edges = new ArrayList<Edge>(list);
		this.shape = polygon;
	}

	// Morteza2011*****************************************************************
	public SOSArea(List<Edge> edges, int ID) {
		this.edges = new ArrayList<Edge>(edges);
		this.ID = ID;
	}

	// Morteza2011*****************************************************************
	public SOSArea(ArrayList<Edge> edges) {
		this.edges = new ArrayList<Edge>(edges);
	}

	// Morteza2011*****************************************************************
	public SOSArea(List<Edge> edges) {
		this.edges = new ArrayList<Edge>(edges);
	}

	// Morteza2011*****************************************************************
	public int getID() {
		return ID;
	}

	// Morteza2011*****************************************************************
	public void setID(int ID) {
		this.ID = ID;
	}

	// Morteza2011*****************************************************************
	public SOSArea() {
		edges = new ArrayList<Edge>();
	}

	// Morteza2011*****************************************************************
	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
		shape = null;
	}

	// Morteza2011*****************************************************************
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	// Morteza2011*****************************************************************
	@Override
	public Shape getShape() {
		if (shape == null)
			shape = Utility.newShape(edges);
		return shape;
	}

	/*
	 * //
	 * Morteza2011************************************************************
	 * ***** public void setPoints(ArrayList<ArrayList<Pair<Point2D, Point2D>>>
	 * points) { this.points = points; }
	 * //
	 * Morteza2011************************************************************
	 * ***** public ArrayList<ArrayList<Pair<Point2D, Point2D>>> getPoints() {
	 * return points; }
	 * //
	 * Morteza2011************************************************************
	 * ***** public void addPoint(int edgeIndex, Pair<Point2D, Point2D> point) {
	 * points.get(edgeIndex).add(point); }
	 */

	// Morteza2011*****************************************************************
	public void setReachablityBlockades(ArrayList<Blockade> blockades) {
		if (reachablityblockades == null)
			reachablityblockades = new ArrayList<Blockade>();
		else
			reachablityblockades.clear();
		reachablityblockades.addAll(blockades);
	}

	// Morteza2011*****************************************************************
	public void addReachablityBlockades(ArrayList<Blockade> blockades) {
		if (reachablityblockades == null)
			reachablityblockades = new ArrayList<Blockade>();
		reachablityblockades.addAll(blockades);
	}

	// Morteza2011*****************************************************************
	public ArrayList<Blockade> getReachablityBlockades() {
		if (reachablityblockades == null)
			reachablityblockades = new ArrayList<Blockade>();
		return reachablityblockades;
	}

	// Since the center pointed was deleted it was not needed any more
	// except I added two function to get X and Y of the center of the Area
	// seperatly
	// //
	// Morteza2011*****************************************************************
	// public Point2D getCenter() {
	// if (center == null) {
	// double x = 0;
	// double y = 0;
	// for (int i = 0; i < edges.size(); i++) {
	// x = x + edges.get(i).getMidPoint().getX();
	// y = y + edges.get(i).getMidPoint().getY();
	// }
	// center = new Point2D(x / edges.size(), y / edges.size());
	// }
	// return center;
	// }
	//	private void computeCenter() {
	//		centerX = 0;
	//		centerY = 0;
	//		for (int i = 0; i < edges.size(); i++) {
	//			centerX = centerX + edges.get(i).getMidPoint().getX();
	//			centerY = centerY + edges.get(i).getMidPoint().getY();
	//		}
	//		centerX /= edges.size();
	//		centerY /= edges.size();
	//	}

	/**
	 * @author Salim
	 * @date March 27 2011
	 */
	public double getCenterY() {
		double centerY = 0;
		for (int i = 0; i < edges.size(); i++) {
			centerY = centerY + edges.get(i).getMidPoint().getY();
		}
		centerY /= edges.size();
		return centerY;
	}

	/**
	 * @author Salim
	 * @date March 27 2011
	 */
	public double getCenterX() {
		double centerX = 0;
		for (int i = 0; i < edges.size(); i++) {
			centerX = centerX + edges.get(i).getMidPoint().getX();
		}
		centerX /= edges.size();
		return centerX;
	}

	// Morteza2011*****************************************************************
	public void setRoad(Road r) {
		this.road = r;
	}

	// Morteza2011*****************************************************************
	public Road getRoad() {
		return road;
	}

	public int[] getApexes() {
		int[] apexList = new int[getEdges().size() * 2];
		int i = 0;
		for (Edge next : getEdges()) {
			apexList[i++] = next.getStartX();
			apexList[i++] = next.getStartY();
		}
		return apexList;
	}

	public ArrayList<SOSArea> getHoles() {
		return holes;
	}

	public void addHole(SOSArea hole) {
		if (holes == null)
			holes = new ArrayList<SOSArea>();
		holes.add(hole);
	}

	// Morteza2011*****************************************************************
	public Rectangle getBounds() {
		int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
		for (Edge e : edges) {
			if (e.getStart().getIntX() < minX) {
				minX = e.getStart().getIntX();
			}
			if (e.getStart().getIntX() > maxX) {
				maxX = e.getStart().getIntX();
			}
			if (e.getStart().getIntY() < minY) {
				minY = e.getStart().getIntY();
			}
			if (e.getStart().getIntY() > maxY) {
				maxY = e.getStart().getIntY();
			}
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName()+"[id="+ID+"]";
	}
}
