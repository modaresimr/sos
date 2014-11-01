package sos.base.sosFireEstimator;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.uncommons.maths.number.NumberGenerator;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.entities.Center;

/**
 * @author Yoosef
 */
public class VirtualData {
	/////////////////////////////////////////////////////////////////////////////////
	private int waterQuantity = 0;
	public static final double STEFAN_BOLTZMANN_CONSTANT = 0.000000056704;
	public static float woodSpeed = 0.05f;
	public static float steelSpeed = 0.05f;
	public static float concreteSpeed = 0.05f;
	public int ignitionTime = -1;
	public static NumberGenerator<Double> burnRate;
	private float initFuel;
	public float volume;
	public float capacity = -1;
	public static final int FLOOR_HEIGHT = 3;
	public static float woodCapacity = 1.1f;
	public static float steelCapacity = 1;
	public static float concreteCapacity = 1.5f;
	public static float woodIgnition = 47;
	public static float steelIgnition = 47;
	public static float concreteIgnition = 47;
	public static float woodEnergie = 2400;
	public static float steelEnergie = 800;
	public static float concreteEnergie = 350;
	public static float woodBurning = 800;
	public static float steelBurning = 850;
	public static float concreteBurning = 800;

	public static final int NORMAL = 0;
	public static final int HEATING = 1;
	public static final int BURNING = 2;
	public static final int COOLING_DOWN = 3;
	public static final int EXTINGUISHED = 5;
	public static final int BURNED_DOWN = 4;
	private static final int WATER_COEFFICIENT = 30;
	private static final float GAMMA = 0.5f;
	//////////////////////////////////////////////////////
	public int[][] cells;
	////////////////////////////////////////////////////////////////////////////////
	private boolean shouldUpdate = false;
	public float fuel;
	private double energy = 0;
	private Building self;
	private boolean ignition;
	private boolean inflamable = true;
	private boolean reallyIgnited;

	/////////////////////////////Estimator\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public VirtualData(Building self) {
		this.setSelf(self);
		setReallyIgnited(false);
	}

	public VirtualData(VirtualData other) {
		//java.util.Random random = new java.util.Random(23);
		//burnRate = new GaussianGenerator(0.15, 0.02, random);
		this.self = other.self;
		this.initFuel = other.initFuel;
		this.volume = other.volume;
		this.capacity = other.capacity;
		this.cells = other.cells.clone();
		this.fuel = other.fuel;
		this.energy = other.energy;
		this.ignition = other.ignition;
		//		this.polygon = other.polygon;
		this.inflamable = other.inflamable;
		this.reallyIgnited = other.reallyIgnited;
	}
	
	public void setRealBuildingProperty(){
		setOriginalTemprature(self.isTemperatureDefined()?self.getTemperature():0);
		setOriginalFieryness(self.isFierynessDefined()?self.getFieryness():0);
		this.initFuel = self.virtualData[0].initFuel;
		this.volume = self.virtualData[0].volume;
		this.capacity = self.virtualData[0].capacity;
		this.cells = self.virtualData[0].cells.clone();
		this.ignition = self.virtualData[0].ignition;
		//		this.polygon = other.polygon;
		this.inflamable = self.virtualData[0].inflamable;
	}

	public boolean isReallyIgnited() {
		return reallyIgnited;
	}

	public void setReallyIgnited(boolean reallyIgnited) {
		this.reallyIgnited = reallyIgnited;
	}

	@Override
	public VirtualData clone() {
		return new VirtualData(this);
	}

	public boolean isInflamable() {
		return inflamable;
	}

	public void setInflamable(boolean inflamable) {
		this.inflamable = inflamable;
	}

	public float getInitialFuel() {
		if (initFuel <= 0) {
			volume = self.getGroundArea() * self.getFloors() * FLOOR_HEIGHT;
			initFuel = (getFuelDensity() * volume);
		}
		return initFuel;
	}

	public float getCapacity() {
		if (capacity <= 0) {
			volume = self.getGroundArea() * self.getFloors() * FLOOR_HEIGHT;
			setCapacity(volume * getThermoCapacity());
		}
		return capacity;
	}

	public void setCapacity(float f) {
		capacity = f;
	}

	public double getEnergy() {
		if (energy == Double.NaN || energy == Double.POSITIVE_INFINITY || energy == Double.NEGATIVE_INFINITY)
			energy = Double.MAX_VALUE * 0.75d;
		return energy;
	}

	public float getConsum() {
		if (fuel == 0) {
			return 0;
		}
		float tf = (float) (getTemperature() / 1000f);
		float lf = getFuel() / getInitialFuel();
		float f = (float) (tf * lf * burnRate.nextValue());//*0.75f;
		if (f < 0.005f)
			f = 0.005f;
		return getInitialFuel() * f;

	}

	public void ignite(int time) {
		energy = getCapacity() * getIgnitionPoint() * 1.5;
		ignitionTime = time;
		setIgnition(true);
	}

	public void setTemprature(int temprature) {
		energy = getCapacity() * temprature;
	}

	public void setSelf(Building self) {
		this.self = self;
	}

	public Building getSelf() {
		return self;
	}

	public void setIgnition(boolean iginition) {
		this.ignition = iginition;
	}

	public boolean isIgnition() {
		return ignition;
	}

	public void findCells(SOSFireEstimatorWorldModel w) {
		LinkedList<Integer> tmp = new LinkedList<Integer>();
		Rectangle2D bound = self.getShape().getBounds2D();
		int mini = (int) ((bound.getMinX() - w.getMinX()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE);
		int minj = (int) ((bound.getMinY() - w.getMinY()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE);
		int maxi = (int) ((bound.getMaxX() - w.getMinX()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE) + 1;
		int maxj = (int) ((bound.getMaxY() - w.getMinY()) / SOSFireEstimatorWorldModel.SAMPLE_SIZE) + 1;
		if (mini < 0)
			mini = 0;
		if (minj < 0)
			minj = 0;
		if (maxi > w.getAirTemp().length)
			maxi = w.getAirTemp().length;

		if (maxj > w.getAirTemp()[0].length)
			maxi = w.getAirTemp()[0].length;

		//		int mini = 0;
		//		int minj = 0;
		//		int maxi = w.getAirTemp().length;
		//		int maxj = w.getAirTemp()[0].length;
		for (int x = mini; x < maxi; x++)
			for (int y = minj; y < maxj; y++) {
				int xv = x * SOSFireEstimatorWorldModel.SAMPLE_SIZE + w.getMinX();
				int yv = y * SOSFireEstimatorWorldModel.SAMPLE_SIZE + w.getMinY();
				//				if (Geometry.boundingTest(self.getShape(), xv, yv, SOSFireEstimatorWorldModel.SAMPLE_SIZE, SOSFireEstimatorWorldModel.SAMPLE_SIZE)) {
				int pc = Geometry.percent(xv, yv, SOSFireEstimatorWorldModel.SAMPLE_SIZE, SOSFireEstimatorWorldModel.SAMPLE_SIZE, self.getShape());
				if (pc > 0) {
					tmp.add(x);
					tmp.add(y);
					tmp.add(pc);
				}
				//				}
			}
		if (tmp.size() > 0) {
			cells = new int[tmp.size() / 3][3];
			Iterator<Integer> i = tmp.iterator();
			for (int c = 0; c < cells.length; c++) {
				cells[c][0] = i.next();
				cells[c][1] = i.next();
				cells[c][2] = i.next();
			}
		} /*
		   * else {
		   * for (int x = 0; x < w.getAirTemp().length; x++) {
		   * for (int y = 0; y < w.getAirTemp()[0].length; y++) {
		   * int xv = x * SOSFireEstimatorWorldModel.SAMPLE_SIZE + w.getMinX();
		   * int yv = y * SOSFireEstimatorWorldModel.SAMPLE_SIZE + w.getMinY();
		   * if (Geometry.boundingTest(self.getShape(), xv, yv, SOSFireEstimatorWorldModel.SAMPLE_SIZE, SOSFireEstimatorWorldModel.SAMPLE_SIZE)) {
		   * int counter = 0;
		   * double dx = SOSFireEstimatorWorldModel.SAMPLE_SIZE / 100;
		   * double dy = SOSFireEstimatorWorldModel.SAMPLE_SIZE / 100;
		   * for (int i = 0; i < 100; i++) {
		   * for (int j = 0; j < 100; j++) {
		   * double testX = dx * i + xv;
		   * double testY = dy * j + yv;
		   * if (self.getShape().contains(dx * i + xv, dy * j + yv)) {
		   * counter++;
		   * }
		   * }
		   * }
		   * }
		   * }
		   * }
		   * }
		   */
	}

	public double getTemperature() {
		double rv = getEnergy() / getCapacity();
		if (Double.isNaN(rv)) {
			new RuntimeException().printStackTrace();
		}
		if (rv == Double.NaN || rv == Double.POSITIVE_INFINITY || rv == Double.NEGATIVE_INFINITY)
			rv = Double.MAX_VALUE * 0.75;
		return rv;
	}

	public void initialize(SOSFireEstimatorWorldModel sosFireEstimatorWorldModel) {
		volume = self.getGroundArea() * self.getFloors() * FLOOR_HEIGHT;
		setCapacity(volume * getThermoCapacity());
		energy = 0;
		initFuel = -1;
		fuel = getInitialFuel();
		if (self instanceof Center)//TODO
			inflamable = false;

	}

	public double getRadiationEnergy() {
		double t = getTemperature() + 273d;
		double radEn = (t * t * t * t) * self.totalWallArea * SOSFireEstimator.RADIATION_COEFFICENT * STEFAN_BOLTZMANN_CONSTANT;
		if (radEn == Double.NaN || radEn == Double.POSITIVE_INFINITY || radEn == Double.NEGATIVE_INFINITY)
			radEn = Double.MAX_VALUE * 0.75;
		if (radEn > getEnergy()) {
			radEn = getEnergy();
		}
		return radEn;
	}

	public String Rad() {
		return "temprature = " + (getTemperature() + 293) + " selfWall = " + self.totalWallArea + " con1 = " + SOSFireEstimator.RADIATION_COEFFICENT + " con2 =" + STEFAN_BOLTZMANN_CONSTANT + "===== " + getRadiationEnergy();
	}

	private float getFuelDensity() {
		switch (self.getBuildingCode()) {
		case 0:
			return woodEnergie;
		case 1:
			return steelEnergie;
		default:
			return concreteEnergie;
		}
	}

	private float getThermoCapacity() {
		switch (self.getBuildingCode()) {
		case 0:
			return woodCapacity;
		case 1:
			return steelCapacity;
		default:
			return concreteCapacity;
		}
	}

	public float getBurningTemp() {
		switch (self.getBuildingCode()) {
		case 0:
			return woodBurning;
		case 1:
			return steelBurning;
		default:
			return concreteBurning;
		}
	}

	public float getIgnitionPoint() {
		switch (self.getBuildingCode()) {
		case 0:
			return woodIgnition;
		case 1:
			return steelIgnition;
		default:
			return concreteIgnition;
		}
	}

	public int getWaterQuantity() {
		return waterQuantity;
	}

	public void setWaterQuantity(int i) {
		waterQuantity = i;
	}

	public float getFuel() {
		return fuel;
	}

	public void setEnergy(double d) {

		this.energy = d;
	}

	////////////////////////////////////Tools\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	public void setOriginalFieryness(int fieryness) {
		//TODO
		if(fieryness>0 && (self instanceof Center))
			inflamable=true;
		if (fieryness == 4)
			wasEverWatered = true;
		if (getFieryness() != fieryness) {
			if ((fieryness) == 0 || (fieryness) == 4)
				fuel = getInitialFuel();
			else {
				if ((fieryness) == 1 || (fieryness) == 5)
					fuel = (float) (getInitialFuel() * 0.66);
				else {
					if ((fieryness) == 2 || (fieryness) == 6)
						fuel = (float) (getInitialFuel() * 0.33);
					else {
						if ((fieryness) == 3 || (fieryness) == 7) {
							fuel = (float) (getInitialFuel() * 0.23);
						} else {
							fuel =  0;
						}
					}
				}
			}
		}
		if (fieryness == 0 || (fieryness > 3 && fieryness < 8))
			ignition = false;

	}

	//	public int getFieryness() {
	//		if (!isInflamable())
	//			return 0;
	//
	//		if (getTemperature() >= getIgnitionPoint()) {
	//			if (fuel >= getInitialFuel() * 0.66)
	//				return 1; // burning, slightly damaged
	//			if (fuel >= getInitialFuel() * 0.33)
	//				return 2; // burning, more damaged
	//			if (fuel > 0)
	//				return 3; // burning, severly damaged
	//		}
	//
	//		if (fuel == getInitialFuel())
	//			if (wasEverWatered)
	//				return 4; // not burnt, but watered-damaged
	//			else
	//				return 0; // not burnt, no water damage
	//		if (fuel >= getInitialFuel() * 0.66)
	//			return 5; // extinguished, slightly damaged
	//		if (fuel >= getInitialFuel() * 0.33)
	//			return 6; // extinguished, more damaged
	//		if (fuel > 0)
	//			return 7; // extinguished, severely damaged
	//		return 8; // completely burnt down
	//
	//	}

	public void setOriginalTemprature(int temperature) {
		if(temperature>0)
			setWaterQuantity(0);
		setTemprature(temperature);
		if (temperature > getTemperature()) {
			for (int[] nextCell : cells) {
				int cellX = nextCell[0];
				int cellY = nextCell[1];
				self.model().estimatedModel.setAirCellTemp(cellX, cellY, temperature);
			}
		}
		if (getTemperature() >= getIgnitionPoint()) {
			setIgnition(true);
			//			ignitionTime = -1;
		} else {
			//			ignitionTime = 0;
			setIgnition(false);
		}
	}

	//	public int getFierynessOfterExtinguish(int water) {
	//		if (!self.virtualData.isInflamable())
	//			return 0;
	//		if (getTemeratureOfterExtinguish(water) >= getIgnitionPoint()) {
	//			if (fuel >= getInitialFuel() * 0.66)
	//				return 1; // burning, slightly damaged
	//			if (fuel >= getInitialFuel() * 0.33)
	//				return 2; // burning, more damaged
	//			if (fuel > 0)
	//				return 3; // burning, severly damaged
	//		}
	//
	//		if (fuel == getInitialFuel())
	//			return 4; // not burnt, but watered-damaged
	//		if (fuel >= getInitialFuel() * 0.66)
	//			return 5; // extinguished, slightly damaged
	//		if (fuel >= getInitialFuel() * 0.33)
	//			return 6; // extinguished, more damaged
	//		if (fuel > 0)
	//			return 7; // extinguished, severely damaged
	//		return 8; // completely burnt down
	//
	//	}

	public boolean wasEverWatered=false;

	public int getFieryness() {
		if (!isInflamable())
			return 0;

		if (getTemperature() >= getIgnitionPoint()) {
			if (fuel >= getInitialFuel() * 0.66)
				return 1; // burning, slightly damaged
			if (fuel >= getInitialFuel() * 0.33)
				return 2; // burning, more damaged
			if (fuel > 0)
				return 3; // burning, severly damaged
		}

		if (fuel == getInitialFuel())
			if (wasEverWatered)
				return 4; // not burnt, but watered-damaged
			else
				return 0; // not burnt, no water damage
		if (fuel >= getInitialFuel() * 0.66)
			return 5; // extinguished, slightly damaged
		if (fuel >= getInitialFuel() * 0.33)
			return 6; // extinguished, more damaged
		if (fuel > 0)
			return 7; // extinguished, severely damaged
		return 8; // completely burnt down

	}

	//	public int getWaterForExtinguish() {
	//		if (getFieryness() == 0 || getFieryness() > 3)
	//			return 0;
	//		double minEnergy = getIgnitionPoint() * getCapacity();
	//		double dE = getEnergy() - minEnergy;
	//		dE = dE / SOSFireEstimator.WATER_COEFFICIENT;
	//		if (dE < 0)
	//			return 0;
	//		else
	//			return (int) dE;
	//	}

	//	public double getEnergyTransferRateToOut() {
	//		return getRadiationEnergy();
	//	}
	//
	//	public boolean willIgniteInNextCycle() {
	//		return getTemperature() > 30 && getTemperature() <= 47;
	//	}
	//
	//	public double getEnergyTransferRateToSelf() {
	//		double enery = 0;
	//		for (int i = 0; i < connectedValues.length; i++) {
	//			enery += connectedBuilding[i].virtualData.getRadiationEnergy() * connectedValues[i];
	//		}
	//		return enery;
	//	}
	//
	//	public double getHowMuchCanStopTheFire(int maxWater) {
	//		double newTemprature = (getEnergy() - maxWater * SOSFireEstimator.WATER_COEFFICIENT) / getCapacity();
	//		double radE = getRadiationEnergy(newTemprature);
	//		if (newTemprature < 0)
	//			radE = -radE;
	//		return (getRadiationEnergy() - radE);
	//
	//	}
	public ArrayList<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();

	public ArrayList<Pair<String, String>> getData() {
		ArrayList<Pair<String, String>> datas = new ArrayList<Pair<String, String>>();
		datas.add(new Pair<String, String>("Energy ", getEnergy() + ""));
		datas.add(new Pair<String, String>("Temprature ", getTemperature() + ""));
		datas.add(new Pair<String, String>("Fuel ", fuel + ""));
		datas.add(new Pair<String, String>("Initial Fuel ", getInitialFuel() + ""));
		datas.add(new Pair<String, String>("Fieryness ", getFieryness() + ""));
		return datas;

	}

	public boolean isBurning() {
		return getFieryness() > 0 && getFieryness() < 4;
	}

	//	public boolean canVisit() {
	//		// TODO Auto-generated method stub
	//		return false;
	//	}

	public void setShouldUpdate(boolean shouldUpdate) {
		this.shouldUpdate = shouldUpdate;
	}

	public boolean isShouldUpdate() {
		return shouldUpdate;
	}

	public boolean update() {
		boolean temp = false;
		if (getTemperature() > 47 && self.getTemperature() < 47)
			temp = true;
		//		if (((int) getTemperature()) == 0 && self.isTemperatureDefined() && self.getTemperature() > 0) {
		//			self.setFireProbability(true);
		//		}
		setOriginalFieryness(self.getFieryness());
		setOriginalTemprature(self.getTemperature());
		waterQuantity=0;
		return temp;

	}

	public boolean isIgniteInNextCycle() {
		return getTemperature() > 35 && !isIgnition();
	}

	public void reset(SOSFireEstimatorWorldModel es) {
		setOriginalFieryness(self.getFieryness());
		setOriginalTemprature(self.getTemperature());

		for (int[] nextCell : cells) {
			int cellX = nextCell[0];
			int cellY = nextCell[1];
			es.setAirCellTemp(cellX, cellY, 0);
		}
	}

	public void extinguish(int water) {
		wasEverWatered = true;
		setWaterQuantity(getWaterQuantity() + water);
//		energy -= water * 30;
		waterCooling();
		//		setTemprature((int) getTemperature());
	}

	public void waterCooling() {
		double lWATER_COEFFICIENT = (self.getFieryness() > 0 && self.getFieryness() < 4 ? WATER_COEFFICIENT : WATER_COEFFICIENT * GAMMA);
		if (getWaterQuantity() > 0) {
			double dE = getTemperature() * getCapacity();
			if (dE <= 0) {
				return;
			}
			double effect = getWaterQuantity() * lWATER_COEFFICIENT;
			int consumed = getWaterQuantity();
			if (effect > dE) {
				double pc = 1 - ((effect - dE) / effect);
				effect *= pc;
				consumed *= pc;
			}
			setWaterQuantity(getWaterQuantity() - consumed);
			setEnergy(getEnergy() - effect);
		}
	}

	public boolean isExtinguishableInOneCycle(int water) {
		double dE = ((getTemperature() - 40) * getCapacity());
		return (dE / 25) <= water;

	}

	boolean AA = false;

	public void artificialFire(int b) {
		if (AA)
			return;
		AA = true;
		if (b>0) {
			setOriginalFieryness(1);
			setOriginalTemprature(Math.min(3, b)*50);
			ignite(self.updatedtime());
			for (int[] nextCell : cells) {
				int cellX = nextCell[0];
				int cellY = nextCell[1];
				self.model().estimatedModel.setAirCellTemp(cellX, cellY, 100);
			}
		} else {
			setOriginalFieryness(0);
			setOriginalTemprature(30);
			for (int[] nextCell : cells) {
				int cellX = nextCell[0];
				int cellY = nextCell[1];
				self.model().estimatedModel.setAirCellTemp(cellX, cellY, 60);
			}
		}
	}

	public boolean isRealBurning() {
		// TODO Auto-generated method stub
		return false;
	}

	public void disable() {
//		energy=0;
		
	}
}
