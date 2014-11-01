package sos.fire_v2.decision;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.util.geom.ShapeInArea;
import sos.fire_v2.FireBrigadeAgent;
/**
 *
 *
 * @author Pegah
 *
 */

public class RunPlace {
	ArrayList<Building> buildings = new ArrayList<Building>();
	RunState place;
	Building nearestToCenter;
	double X1;
	double Y1;
	double X2;
	double Y2;
	double Xsize;
	double Ysize;
	double cX;
	double cY;
	private boolean checkedCenter = false;
	private int centerTime;
	private int enterTime;
	SOSWorldModel worldModel;

	public RunPlace(RunState r, double minX, double minY, double maxX, double maxY, double Xsize, double Ysize, SOSWorldModel model) {
		this.place = r;
		this.X1 = minX;
		this.Y1 = minY;
		this.X2 = maxX;
		this.Y2 = maxY;
		this.Xsize = Xsize;
		this.Ysize = Ysize;
		this.worldModel = model;
		cX = (minX + Xsize) / 2;
		cY = (minY + Ysize) / 2;
		buildings = (ArrayList<Building>) worldModel.getObjectsInRectangle(new Rectangle((int) minX, (int) minY, (int) maxX, (int) maxY), Building.class);
		nearestToCenter = buildings.get(0);
		double min = getDistance(nearestToCenter, cX, cY);
		for (Building b : buildings) {
			if (distance(b, cX, cY) < min) {
				min = getDistance(b, cX, cY);
				nearestToCenter = b;

			}
		}
		Collections.sort(buildings, new buildingCompare());

	}

	public RunPlace(RunState R) {
		this.place = R;
	}

	public RunPlace(ArrayList<Building> buildings, RunState r, double minX, double minY, double maxX, double maxY, double Xsize, double Ysize, SOSWorldModel model) {

		this.buildings = buildings;
		this.X1 = minX;
		this.Y1 = minY;
		this.X2 = maxX;
		this.Y2 = maxY;
		this.Xsize = Xsize;
		this.Ysize = Ysize;
		cX = (minX + Xsize) / 2;
		cY = (minY + Ysize) / 2;
		this.place = r;
		this.worldModel = model;
		nearestToCenter = buildings.get(0);
		double min = getDistance(nearestToCenter, cX, cY);
		for (Building b : buildings) {
			if (distance(b, cX, cY) < min) {
				min = getDistance(b, cX, cY);
				nearestToCenter = b;

			}
		}
		Collections.sort(buildings, new buildingCompare());

	}

	public RunPlace(ArrayList<Building> buildings, RunState r, double minX, double minY, double maxX, double maxY, SOSWorldModel model) {

		this.buildings = buildings;
		this.X1 = minX;
		this.Y1 = minY;
		this.X2 = maxX;
		this.Y2 = maxY;
		this.Xsize = X2 - X1;
		this.Ysize = Y2 - Y1;
		cX = (minX + Xsize) / 2;
		cY = (minY + Ysize) / 2;
		this.place = r;
		this.worldModel = model;
		nearestToCenter = buildings.get(0);
		double min = getDistance(nearestToCenter, cX, cY);
		for (Building b : buildings) {
			if (distance(b, cX, cY) < min) {
				min = getDistance(b, cX, cY);
				nearestToCenter = b;

			}
		}
		Collections.sort(buildings, new buildingCompare());
	}

	public RunPlace(RunPlace nextPlace, int time) {
		this.buildings = nextPlace.buildings;
		this.nearestToCenter = nextPlace.nearestToCenter;
		//		this.centerTime=null;
		this.enterTime = time;
		this.place = nextPlace.place;
		this.worldModel = nextPlace.worldModel;
		this.X1 = nextPlace.X1;
		this.X2 = nextPlace.X2;
		this.Y1 = nextPlace.Y1;
		this.Y2 = nextPlace.Y2;
		this.cX = nextPlace.cY;
		this.checkedCenter = false;
		this.Xsize = nextPlace.Xsize;
		this.Ysize = nextPlace.Ysize;
		Collections.sort(buildings, new buildingCompare());

	}

	//	@Override
	public String toStirng() {
		return place.toString();
	}

	public boolean HaveCheckedCenter(FireBrigadeAgent agent) {
		if (checkedCenter)
			return true;
		for (ShapeInArea shape : nearestToCenter.getSearchAreas()) {
			if (shape.contains(agent.me().getX(), agent.me().getY())) {
				checkedCenter = true;
				setCenterTime(agent.time());
				return true;
			}
		}
		return false;

	}

	public void setPlace(RunState state) {
		this.place = state;
	}

	public boolean contains(double x, double y) {
		return (x < X2 && x > X1 && y < Y1 && y > Y2);
	}

	public boolean contains(FireBrigadeAgent agent) {
		if (contains(agent.me().getX(), agent.me().getY())) {
			if (enterTime > agent.time())
				enterTime = agent.time();
			return true;
		}
		return false;

	}

	public boolean contains(Building b) {
		return buildings.contains(b);
	}

	private double distance(Building build, double d, double e) {
		return Math.sqrt(Math.pow(build.getX() - d, 2) + Math.pow(build.getY() - e, 2));
	}

	public double getDistance(Building build, double d, double e) {
		return Math.sqrt(Math.pow(build.getX() - d, 2) + Math.pow(build.getY() - e, 2));
	}

	public int getCenterTime() {
		return centerTime;
	}

	private void setCenterTime(int centerTime) {
		this.centerTime = centerTime;
	}

	public int getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(int enterTime) {
		this.enterTime = enterTime;
	}

	private class buildingCompare implements Comparator<Building> {

		@Override
		public int compare(Building o1, Building o2) {
			// TODO Auto-generated method stub
			return o1.distance(nearestToCenter) - o2.distance(nearestToCenter);
		}

	}

}