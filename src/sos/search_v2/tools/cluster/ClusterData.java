package sos.search_v2.tools.cluster;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import sos.base.SOSAgent;
import sos.base.entities.Building;
import sos.base.sosFireZone.util.ConvexHull_arr_New;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public class ClusterData {
	private HashSet<Building> buildings;
	private int hashCode;
	private double x;
	private double y;
	private int timeOfRJS = -1;//Salim
	private double remainingJobScore;//Salim
	private final SOSAgent<?> me;//Salim
	private final int index;//Salim
	private Rectangle area = null;
	private Area clusterArea; //sinash & Taghva
	private Building centerBuilding = null;
	private boolean isCoverer = false;
	private int diagonal = 0;

	public ClusterData(int hashCode, HashSet<Building> clusterBuilding, SOSAgent<?> me, int index) {
		this.me = me;
		this.index = index;
		this.setHashCode(hashCode);
		this.setBuildings(clusterBuilding);

		//sinash & Taghva
		ConvexHull_arr_New clusterConvex = new ConvexHull_arr_New(new ArrayList<Building>(buildings)).getScaleConvex(1.2F);
		clusterArea = new java.awt.geom.Area(clusterConvex.getShape());
		//end sinash & Taghva
	}

	public ClusterData(HashSet<Building> clusterBuilding, int index) {
		this.setBuildings(clusterBuilding);
		me = null;
		this.index = index;
	}

	public ClusterData(int hashCode, StarZone starZone, SOSAgent<?> me, int index) {
		this(hashCode, starZone.getZoneBuildings(), me, index);
	}

	public ClusterData(ClusterData clusterData) {
		this(clusterData.getHashCode(), new HashSet<Building>(clusterData.buildings), clusterData.me, clusterData.index);
	}

	public HashSet<Building> getBuildings() {
		return buildings;
	}

	public Iterator<Building> getBuildingsIterator() {
		return buildings.iterator();
	}

	public void setBuildings(HashSet<Building> buildings) {
		this.buildings = buildings;
		//Salim
		int cx = 0, cy = 0;
		for (Building b : getBuildings()) {
			cx += b.getX();
			cy += b.getY();
		}
		cx /= getBuildings().size();
		cy /= getBuildings().size();
		setX(cx);
		setY(cy);
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public double getX() {//Salim
		return x;
	}

	public void setX(double x) {//Salim
		this.x = x;
	}

	public double getY() {//Salim
		return y;
	}

	public void setY(double y) {//Salim
		this.y = y;
	}

	public int getIndex() {//Salim
		return index;
	}

	public Rectangle getBounds() {
		if (area == null) {
			area = new Rectangle((int) getX(), (int) getY(), 0, 0);
			for (Building b : getBuildings()) {
				area.add(b.getShape().getBounds());
			}
		}
		return area;
	}

	public Shape getConvexShape() {
		ConvexHull_arr_New convex = new ConvexHull_arr_New(getBuildings());
		return convex.getShape();
	}

	/**
	 * @author sinash
	 */
	public Area getClusterArea() {
		return clusterArea;
	}

	@Override
	public String toString() {
		return "(id=" + getIndex() + ", X=" + getX() + "  Y=" + getY() + "  Hash=" + hashCode + " Building=" + buildings.iterator().next() + ")";
	}

	/**
	 * @author Hesam akbary
	 */

	public Building getNearestBuildingToCenter() {
		Building select = null;
		if (centerBuilding != null)
			return centerBuilding;
		else {
			int dis = Integer.MAX_VALUE;
			for (Building building : getBuildings()) {
				int temp = (int) Point.distance(getX(), getY(), building.getX(), building.getY());
				if (select == null || dis > temp) {
					select = building;
					dis = temp;
				}
			}
		}
		return select;

	}

	public void setCoverer(boolean isCoverer) {
		this.isCoverer = isCoverer;
	}

	public boolean isCoverer() {
		return isCoverer;
	}

	public int getDiagonalOfCluster() {
		if (diagonal == 0) {
			diagonal = (int) Point.distance(0, 0, getBounds().getWidth(), getBounds().getHeight());
		}
		return diagonal;
	}
}
