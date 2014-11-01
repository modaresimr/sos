package sos.base.sosFireEstimator;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Map.Entry;

import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.entities.Building;
import sos.base.sosFireZone.SOSEstimatedFireZone;
import sos.base.sosFireZone.SOSRealFireZone;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

public class SOSFireEstimator {
	private SOSFireEstimatorWorldModel esModel;
	private SOSWorldModel world;

	public static float GAMMA = 0.2f;
	public static float AIR_TO_AIR_COEFFICIENT = 0.8f;//1.0f;
	public static float AIR_TO_BUILDING_COEFFICIENT = 0.0015f;//0.0015
	public static float WATER_COEFFICIENT = 30f;
	public static float ENERGY_LOSS = 0.86f;
	public static float WIND_DIRECTION = 0.9f;
	public static float WIND_RANDOM = 0f;
	public static int WIND_SPEED = 0;
	public static float RADIATION_COEFFICENT = 0.011f;
	public static float TIME_STEP_LENGTH = 1f;//
	public static float WEIGHT_GRID = 0.2f;//
	public static float AIR_CELL_HEAT_CAPACITY = 0.004f;
	private ArrayList<Building> estimatorBuildings;
	private ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> fireSites;

	public void setEstimatorBuildings(ArrayList<Building> estimatorBuildings) {

	}

	private int index = -1;
	//	private ArrayList<Building> arrays = new ArrayList<Building>();
	private short hashCode;
	private SOSLoggerSystem log;

	public SOSFireEstimator(SOSWorldModel world, SOSFireEstimatorWorldModel esModel, ArrayList<Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone>> fireSites, int index, short hash) {
		this.world = world;
		this.esModel = esModel;
		//		this.estimatorBuildings = estimatorBuildings;
		this.estimatorBuildings = new ArrayList<Building>();
		this.index = index;
		this.fireSites = fireSites;
		setHashCode(hash);
		log = new SOSLoggerSystem(world.me(), "SOSFireEstimator/FireEstimator(" + hashCode + ")", true, OutputType.File, true);

	}

	public void step(int timestep) {
		estimatorBuildings.clear();
		for (Pair<ArrayList<SOSRealFireZone>, SOSEstimatedFireZone> pfs : fireSites) {
			SOSEstimatedFireZone fs = pfs.second();
			if ((fs).isEstimating())
				for (Building b : fs.getEstimatorBuilding())
					if (!estimatorBuildings.contains(b)) {
						estimatorBuildings.add(b);

					}
		}
		//		int i=0;
		log.info("-----------------------------------" + timestep + "-------------------------------------------");
		log.info("fire site  " + fireSites);
		log.info("buildings " + estimatorBuildings);
		long t1 = System.currentTimeMillis();
		//		update(timestep);
		t1 = System.currentTimeMillis();
		burn(timestep);
		t1 = System.currentTimeMillis();

		updateGrid();
		log.info("updateGrid  " + (System.currentTimeMillis() - t1) + " ms ");
		t1 = System.currentTimeMillis();
		exchangeBuilding();
		log.info("exchange  " + (System.currentTimeMillis() - t1) + " ms ");
		//		cool();
		for (Building b : estimatorBuildings) {
			b.virtualData[index].waterCooling();
			if (b.virtualData[index].getTemperature() >= b.virtualData[index].getIgnitionPoint() && b.virtualData[index].fuel > 0 && b.virtualData[index].isInflamable())
				if (b.virtualData[index].isIgnition() == false) {
					b.virtualData[index].ignite(timestep);
					esModel.addFieryBuilding(b);
				}
		}
	}

	public void step(int timestep, SOSEstimatedFireZone fs) {
		estimatorBuildings.clear();
		//		for (AbstractFireSite fs : fs) {
		//			if ((fs).isEstimating())
		//				for (Building b : fs.getEstimatorBuilding())
		//					if (!estimatorBuildings.contains(b)) {
		estimatorBuildings.addAll(fs.getEstimatorBuilding());
		//
		//					}
		//		}
		//		//		int i=0;
		log.info("-----------------------------------" + timestep + "-------------------------------------------");
		log.info("fire site  " + fireSites);
		log.info("buildings " + estimatorBuildings);
		long t1 = System.currentTimeMillis();
		//		update(timestep);
		t1 = System.currentTimeMillis();
		burn(timestep);
		t1 = System.currentTimeMillis();

		updateGrid();
		log.info("updateGrid  " + (System.currentTimeMillis() - t1) + " ms ");
		t1 = System.currentTimeMillis();
		exchangeBuilding();
		log.info("exchange  " + (System.currentTimeMillis() - t1) + " ms ");
		//		cool();
		for (Building b : estimatorBuildings) {
			b.virtualData[index].waterCooling();
			if (b.virtualData[index].getTemperature() >= b.virtualData[index].getIgnitionPoint() && b.virtualData[index].fuel > 0 && b.virtualData[index].isInflamable())
				if (b.virtualData[index].isIgnition() == false) {
					b.virtualData[index].ignite(timestep);
					esModel.addFieryBuilding(b);
				}
		}
	}

	//
	//	private void update(int time) {
	//		for (Building b : estimatorBuildings) {
	//			if (b.virtualData[index].isShouldUpdate()) {
	//				//log.info(b + " updated ");
	//				b.virtualData[index].update();
	//				b.virtualData[index].setShouldUpdate(false);
	//			}
	//		}
	//	}

	private void burn(int time) {
		for (Building b : estimatorBuildings) {
			if (b.virtualData[index].getTemperature() >= b.virtualData[index].getIgnitionPoint() && b.virtualData[index].fuel > 0 && b.virtualData[index].isInflamable()) {
				if (b.virtualData[index].isIgnition() == false) {
					b.virtualData[index].ignite(time);
					esModel.addFieryBuilding(b);
				}
				float consumed = b.virtualData[index].getConsum();
				if (consumed > b.virtualData[index].fuel) {
					consumed = b.virtualData[index].fuel;
				}
//				if (b.id() == 23545)
//					log.info(b + " burn ==>  fuel = " + b.virtualData[index].fuel + "  consumed=" + consumed + "  oldEnergy " + b.virtualData[index].getEnergy() + "  new Energy " + (b.virtualData[index].getEnergy() + consumed));
				b.virtualData[index].setEnergy(b.virtualData[index].getEnergy() + consumed);
				b.virtualData[index].fuel -= consumed;
//				if (b.id() == 23545)
//					log.info("new fuel " + b.virtualData[index].fuel);

			}
		}
	}

	private void exchangeBuilding() {
		double[] radiation = new double[estimatorBuildings.size()];
		for (int i = 0; i < estimatorBuildings.size(); i++) {
			Building b = estimatorBuildings.get(i);
			exchangeWithAir(b);
			//			double radEn = b.virtualData[index].getRadiationEnergy();
			//			radiation[i] = radEn;
			//			log.info(b+" information "+b.virtualData[index].Rad());
			//			log.info(b + " radiation = " + radEn);
		}

		for (int i = 0; i < estimatorBuildings.size(); i++) {
			Building b = estimatorBuildings.get(i);
			//			exchangeWithAir(b);
			double radEn = b.virtualData[index].getRadiationEnergy();
			radiation[i] = radEn;
			//			log.info(b+" information "+b.virtualData[index].Rad());
			//			log.info(b + " radiation = " + radEn);
		}

		for (int i = 0; i < estimatorBuildings.size(); i++) {
			Building b = estimatorBuildings.get(i);
			double radEn = radiation[i];
			if (radEn == 0)
				continue;
			//			log.info("------------>Radiation Energy from " + b + "  Energy= " + radEn);
			for (Entry<Short, Float> bv : b.real_neighbors_BuildValue().entrySet()) {
				//				log.info("-----");

				Building bs = world.buildings().get(bv.getKey());
				if (bs.virtualData[index] == null && index == 1)
					continue;// for isole
				//				log.info("Radiation Energy To " + bs + " Old Energy " + bs.virtualData[index].getEnergy());
				//				log.info("connected Value " + bv.f);
				double oldEnergy = bs.virtualData[index].getEnergy();
				double newEnergy = oldEnergy + radEn * (bv.getValue()) * 0.5f;// * bv.f * 4.5;//TODO
				bs.virtualData[index].setEnergy(newEnergy);
				//				log.info("Transfered Energy =" + (radEn * bv.f) + "   new Energy " + newEnergy);
			}
			b.virtualData[index].setEnergy(b.virtualData[index].getEnergy() - radEn);
			//			log.info("<------------END Radiation Energy from " + b + "  new Energy= " + b.virtualData[index].getEnergy());
		}
	}

	private void exchangeWithAir(Building b) {
		double oldEnergy = b.virtualData[index].getEnergy();
		double energyDelta = 0;
		for (int[] nextCell : b.virtualData[index].cells) {
			int cellX = nextCell[0];
			int cellY = nextCell[1];
			double cellCover = nextCell[2] / 100d;
			double cellTemp = esModel.getAirCellTemp(cellX, cellY);
			double dT = cellTemp - b.virtualData[index].getTemperature();
			double energyTransferToBuilding = dT * AIR_TO_BUILDING_COEFFICIENT * TIME_STEP_LENGTH * cellCover * SOSFireEstimatorWorldModel.SAMPLE_SIZE;
			energyDelta += energyTransferToBuilding;
			double newCellTemp = cellTemp - energyTransferToBuilding / (AIR_CELL_HEAT_CAPACITY * SOSFireEstimatorWorldModel.SAMPLE_SIZE);
			esModel.setAirCellTemp(cellX, cellY, newCellTemp);
		}
		b.virtualData[index].setEnergy(oldEnergy + energyDelta);
	}

	private void updateGrid() {
		double[][] airtemp = esModel.getAirTemp();
		double[][] newairtemp = new double[airtemp.length][airtemp[0].length];

		//		int mini =0;
		//		int maxi=esModel.getAirTemp().length;
		//		int minj =0;
		//		int maxj=esModel.getAirTemp()[0].length;

		Rectangle2D bound = getBounds2D();
		int mini = (int) ((bound.getMinX() - esModel.getMinX()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE);
		int minj = (int) ((bound.getMinY() - esModel.getMinY()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE);
		int maxi = (int) ((bound.getMaxX() - esModel.getMinX()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE) + 1;
		int maxj = (int) ((bound.getMaxY() - esModel.getMinY()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE) + 1;
		if (mini < 0)
			mini = 0;
		if (minj < 0)
			minj = 0;
		if (maxi > esModel.getAirTemp().length)
			maxi = esModel.getAirTemp().length;
		if (maxj > esModel.getAirTemp()[0].length)
			maxi = esModel.getAirTemp()[0].length;

		for (int x = mini; x < maxi; x++) {
			for (int y = minj; y < maxj; y++) {
				double dt = (averageTemp(x, y) - airtemp[x][y]);
				double change = (dt * (AIR_TO_AIR_COEFFICIENT) * TIME_STEP_LENGTH);
				newairtemp[x][y] = relTemp(airtemp[x][y] + change);
				//				if (newairtemp[x][y] != 0)
				//					//log.info("update Grid oldCellTem" + airtemp[x][y] + " newCellTemp = " + newairtemp[x][y]);
				if (!(newairtemp[x][y] > -Double.MAX_VALUE && newairtemp[x][y] < Double.MAX_VALUE)) {
					newairtemp[x][y] = Double.MAX_VALUE * 0.75;
				}
			}
		}
		esModel.setAirTemp(newairtemp);
	}

	private Rectangle2D getBounds2D() {
		Rectangle r = new Rectangle();
		for (Building b : estimatorBuildings) {
			r.add(b.getShape().getBounds());
		}
		return r;
	}

	private double relTemp(double deltaT) {
		return Math.max(0, deltaT * ENERGY_LOSS * TIME_STEP_LENGTH);
	}

	private double averageTemp(int x, int y) {
		double rv = neighbourCellAverage(x, y) / weightSummCells(x, y);
		return rv;
	}

	private double neighbourCellAverage(int x, int y) {
		double total = getTempAt(x + 1, y - 1);
		total += getTempAt(x + 1, y);
		total += getTempAt(x + 1, y + 1);
		total += getTempAt(x, y - 1);
		total += getTempAt(x, y + 1);
		total += getTempAt(x - 1, y - 1);
		total += getTempAt(x - 1, y);
		total += getTempAt(x - 1, y + 1);
		return total * WEIGHT_GRID;
	}

	private float weightSummCells(int x, int y) {
		return 8 * WEIGHT_GRID;
	}

	protected double getTempAt(int x, int y) {
		if (x < 0 || y < 0 || x >= esModel.getAirTemp().length || y >= esModel.getAirTemp()[0].length)
			return 0;
		return esModel.getAirTemp()[x][y];
	}

	//	private void waterCooling(Building b) {
	//		double lWATER_COEFFICIENT = (b.virtualData[index].getFieryness() > 0 && b.virtualData[index].getFieryness() < 4 ? WATER_COEFFICIENT : WATER_COEFFICIENT * GAMMA);
	//		if (b.virtualData[index].getWaterQuantity() > 0) {
	//			double dE = b.virtualData[index].getEnergy();
	//			if (dE <= 0) {
	//				return;
	//			}
	//			double effect = b.virtualData[index].getWaterQuantity() * lWATER_COEFFICIENT;
	//			int consumed = b.virtualData[index].getWaterQuantity();
	//			if (effect > dE) {
	//				double pc = 1 - ((effect - dE) / effect);
	//				effect *= pc;
	//				consumed *= pc;
	//			}
	//			b.virtualData[index].setWaterQuantity(b.virtualData[index].getWaterQuantity() - consumed);
	//			b.virtualData[index].setEnergy(b.virtualData[index].getEnergy() - effect);
	//		}
	//	}

	//	private void cool() {
	//		for (Iterator<Building> i = estimatorBuildings.iterator(); i.hasNext();) {
	//			Building b = i.next();
	//			waterCooling(b);
	//		}
	//	}

	public short getHashCode() {
		return hashCode;
	}

	public void setHashCode(short hashCode) {
		this.hashCode = hashCode;
	}

}
