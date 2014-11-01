package sos.base.sosFireEstimator;

import java.util.ArrayList;

import org.uncommons.maths.random.GaussianGenerator;

import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

public class SOSFireEstimatorWorldModel {
	public SOSWorldModel world;
	//////////////////////////////////////////////////////////////////////////
	private double[][] airTemp;
	public static final int SAMPLE_SIZE = 5000;
	public static final float AIR_CAPACITY = 0.2f;
	public static final float AIR_HEIGHT = 30;
	public final int CAPACITY;
//	public CellData[][] worldCells;
	private ArrayList<Building> fieryBuildings = new ArrayList<Building>();
	private  SOSLoggerSystem log;

	/////////////////////////////////////////////////////////////////////////
	public SOSFireEstimatorWorldModel(SOSWorldModel world) {
		this.world = world;
		CAPACITY = (int) (SAMPLE_SIZE * SAMPLE_SIZE * AIR_HEIGHT * AIR_CAPACITY) / 1000000;
		world.estimatedModel = this;
		java.util.Random random = new java.util.Random(23);
		VirtualData.burnRate = new GaussianGenerator(0.15, 0.02, random);
		log = new SOSLoggerSystem(world.me(), "SOSFireEstimator/WorldModel", true, OutputType.File, true);
		log.info("Estimator WorldModel Created in time = " + world.time());
		initialize();

	}

	@SuppressWarnings("unchecked")
	public SOSFireEstimatorWorldModel(SOSFireEstimatorWorldModel esModel) {
		this.world = esModel.world;
		CAPACITY = esModel.CAPACITY;
		world.estimatedModel = this;
		log = new SOSLoggerSystem(world.me(), "SOSFireEstimator/WorldModel(-1)", true, OutputType.File, true);
		world.sosAgent().sosLogger.addToAllLogType(log);
		this.fieryBuildings = (ArrayList<Building>) esModel.fieryBuildings.clone();
		this.airTemp = esModel.airTemp.clone();
//		this.worldCells = esModel.worldCells.clone();
		log.info("Estimator WorldModel Created in time = " + world.time());

	}

	@Override
	public SOSFireEstimatorWorldModel clone() {
		return new SOSFireEstimatorWorldModel(this);
	}

	public void addFieryBuilding(Building b) {
		log.info(b + " added to fieryBuilding  time =" + world.time());
		fieryBuildings.add(b);
	}

	public ArrayList<Building> getFieryBuildings() {
		return fieryBuildings;
	}

	public void initialize() {
		log.info("initialize Estimator Data time = " + world.time());
		for (Building b : world.buildings()) {
			b.virtualData[0] = new VirtualData(b);
			b.virtualData[0].initialize(this);
		}
		initializeAir();
	}

	private void initializeAir() {
		log.info("initialize airTemp  cellSize = " + SAMPLE_SIZE + "   time = " + world.time());
		int xSamples = 1 + (getMaxX() - getMinX()) / SAMPLE_SIZE;
		int ySamples = 1 + (getMaxY() - getMinY()) / SAMPLE_SIZE;
		airTemp = new double[xSamples][ySamples];
//		worldCells = new CellData[xSamples][ySamples];
		log.info("Number Of Cells = " + xSamples + " * " + ySamples + " = " + (xSamples * ySamples));
		for (int x = 0; x < airTemp.length; x++)
			for (int y = 0; y < airTemp[x].length; y++) {
				airTemp[x][y] = 0;
//				worldCells[x][y] = new CellData(0d, new ArrayList<Pair<Building, Double>>(), new ArrayList<Building>(), x * SAMPLE_SIZE + getMinX(), y * SAMPLE_SIZE + getMinY(), x, y);
			}

		for (Building b : world.buildings()) {
			b.virtualData[0].findCells(this);
		}
	}

	public double[][] getAirTemp() {
		return airTemp;
	}

	public void setAirTemp(double[][] a) {
		airTemp = a;
	}

	public void setAirCellTemp(int x, int y, double temp) {
		airTemp[x][y] = temp;
	}

	public double getAirCellTemp(int x, int y) {
		return airTemp[x][y];
	}

	public int getMaxY() {
		return (int) world.getBounds().getMaxY();
	}

	public int getMinX() {
		return (int) world.getBounds().getMinX();
	}

	public int getMinY() {
		return (int) world.getBounds().getMinY();
	}

	public int getMaxX() {
		return (int) world.getBounds().getMaxX();
	}

}
