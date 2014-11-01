package sos.fire_v2.base.tools;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;

import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Building;
import sos.base.entities.FireBrigade;
import sos.base.entities.Human;
import sos.base.entities.PoliceForce;
import sos.base.entities.Road;
import sos.search_v2.tools.SearchUtils;

public class FireStarZone {
	private ArrayList<Building>[] subZones = null;
	private final int subZoneCount;
	private final int taleAngle;
	private Road gatheringRoad;
	private final double cy;
	private final double cx;
	private final int index;
	private PoliceForce[] polices;
	private AmbulanceTeam[] ambulances;
	private FireBrigade[] fires;
	private double value = 0;
	private int size = -1;
	private HashSet<Building> zoneBuildings = new HashSet<Building>();
	private Area area;

	public FireStarZone(int subZoneCount, double cx, double cy, int index) {
		this.subZoneCount = subZoneCount;
		this.cx = cx;
		this.cy = cy;
		setSubZones(new ArrayList[subZoneCount]);
		taleAngle = 360 / subZoneCount;
		//newing array lists
		for (int i = 0; i < getSubZones().length; i++) {
			getSubZones()[i] = new ArrayList<Building>();
		}
		this.index = index;
	}

	public void createSubZones(ArrayList<Building> zoneBuildings, double cx, double cy) {
		for (Building b : zoneBuildings) {
			int index = ((int) (SearchUtils.getAngle(b, (int) cx, (int) cy) / taleAngle));
			getSubZones()[index].add(b);
			this.getZoneBuildings().add(b);
		}
	}

	public ArrayList<Building>[] getSubZones() {
		return subZones;
	}

	public void setSubZones(ArrayList<Building>[] subZones) {
		this.subZones = subZones;
	}

	public double getCy() {
		return cy;
	}

	public double getCx() {
		return cx;
	}

	public int getSubZoneCount() {
		return subZoneCount;
	}

	public Road getGatheringRoad() {
		return gatheringRoad;
	}

	public void setGatheringRoad(Road gatheringRoad) {
		this.gatheringRoad = gatheringRoad;
	}

	public int getIndex() {
		return index;
	}

	public PoliceForce[] getPolices() {
		return polices;
	}

	public void setPolices(PoliceForce[] polices) {
		this.polices = polices;
	}

	public AmbulanceTeam[] getAmbulances() {
		return ambulances;
	}

	public void setAmbulances(AmbulanceTeam[] ambuances) {
		this.ambulances = ambuances;
	}

	public FireBrigade[] getFires() {
		return fires;
	}

	public void setFires(FireBrigade[] fires) {
		this.fires = fires;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double minDistanceSum(ArrayList<? extends Human> agents) {
		double sum = 0;
		for (Human h : agents) {
			double minDistance = Integer.MAX_VALUE;
			for (ArrayList<Building> buildings : getSubZones()) {
				double md = SearchUtils.minDistanceOf(buildings, h.getX(), h.getY());
				minDistance = Math.min(md, minDistance);
			}
			sum += minDistance;
		}
		return sum;
	}

	public int size() {
		if (size == -1) {
			size = 0;
			for (ArrayList<Building> sb : subZones) {
				size += sb.size();
			}
		}
		return size;
	}

	public HashSet<Building> getZoneBuildings() {
		return zoneBuildings;
	}

	public void setZoneBuildings(HashSet<Building> zoneBuildings) {
		this.zoneBuildings = zoneBuildings;
	}

	public int getNumberOfAgentNeed() {
		return (int) (zoneBuildings.size() / 80d + 1);
	}

	public Shape getBounds() {
		if (area == null) {
			area = new Area();
			for (Building b : getZoneBuildings()) {
				area.add(new Area(b.getShape()));
			}
		}
		return area.getBounds2D();
	}

}
