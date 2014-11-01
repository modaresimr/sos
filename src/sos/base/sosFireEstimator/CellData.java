package sos.base.sosFireEstimator;


public class CellData {
//	private double temprature;
//	private boolean isChanged;
//	private ArrayList<Pair<Building, Double>> bldgsPer;
//	private ArrayList<Building> neighboors;
//	private int xCenter;
//	private int yCenter;
//	private int x;
//	private int y;
//
//	public CellData(double temprature, ArrayList<Pair<Building, Double>> bldgsPer, ArrayList<Building> neighboors, int xCenter, int yCenter, int x, int y) {
//		this.setTemprature(temprature);
//		this.setBldgsPer(bldgsPer);
//		this.setNeighboors(neighboors);
//		this.xCenter = xCenter;
//		this.yCenter = yCenter;
//		this.setX(x);
//		this.setY(y);
//		if (temprature == 0)
//			setChanged(false);
//		else
//			setChanged(true);
//	}
//
//	public void setTemprature(double temprature) {
//		if (temprature != getTemprature()) {
//			this.temprature = temprature;
//			setChanged(true);
//		} else
//			setChanged(false);
//	}
//
//	public double getTemprature() {
//		return temprature;
//	}
//
//	public void setChanged(boolean isChanged) {
//		this.isChanged = isChanged;
//	}
//
//	public boolean isChanged() {
//		return isChanged;
//	}
//
//	public void setBldgsPer(ArrayList<Pair<Building, Double>> bldgsPer) {
//		this.bldgsPer = bldgsPer;
//	}
//
//	public ArrayList<Pair<Building, Double>> getBldgsPer() {
//		return bldgsPer;
//	}
//
//	public void setNeighboors(ArrayList<Building> neighboors) {
//		this.neighboors = neighboors;
//	}
//
//	public ArrayList<Building> getNeighboors() {
//		return neighboors;
//	}
//
//	public CellData clone(double temprature) {
//		CellData c = new CellData(temprature, getBldgsPer(), getNeighboors(), getxCenter(), getyCenter(), getX(), getY());
//		return c;
//	}
//
//	public void addPair(Pair<Building, Double> pair) {
//		bldgsPer.add(pair);
//	}
//
//	@Override
//	public String toString() {
//		String s = getAveragePer() + "," + getMinFieryness() + "," + getAverageNeigh() + "," + temprature;
//		return s;
//	}
//
//	private double getAverageNeigh() {
//		double sum = 0;
//		for (Building b : getNeighboors()) {
//			if (b.getFieryness() > 0 && b.getFieryness() < 4) {
//				double dis = Math.sqrt(Math.pow(xCenter - b.getX(), 2) + Math.pow(yCenter - b.getY(), 2));
//				sum += b.getTemperature() * (4 - b.getFieryness()) * dis;
//			}
//		}
//		return sum;
//	}
//
//	private double getAveragePer() {
//		double sum = 0;
//		double c = 0;
//		for (Pair<Building, Double> p : getBldgsPer()) {
//			sum += p.first().getTemperature() * p.second();
//			c += p.second();
//		}
//		return sum / c;
//	}
//
//	private int getMinFieryness() {
//		int min = 0;
//		for (Pair<Building, Double> p : getBldgsPer()) {
//			if (min > p.first().getFieryness() && p.first().getFieryness() > 0 && p.first().getFieryness() < 4) {
//				min = p.first().getFieryness();
//			}
//		}
//		return min;
//	}
//
//	public void addNeigh(Building b2) {
//		if (!neighboors.contains(b2))
//			neighboors.add(b2);
//	}
//
//	public int getxCenter() {
//		return xCenter;
//	}
//
//	public void setxCenter(int xCenter) {
//		this.xCenter = xCenter;
//	}
//
//	public int getyCenter() {
//		return yCenter;
//	}
//
//	public void setyCenter(int yCenter) {
//		this.yCenter = yCenter;
//	}
//
//	public void setX(int x) {
//		this.x = x;
//	}
//
//	public int getX() {
//		return x;
//	}
//
//	public void setY(int y) {
//		this.y = y;
//	}
//
//	Shape shape;
//
//	public Shape getShape() {
//		if (shape == null) {
//			int x1 = getxCenter();
//			int y1 = getyCenter();
//			int arrx[] = new int[] { x1, x1 + 5000, x1 + 5000, x1, x1 };
//			int arry[] = new int[] { y1, y1, y1 + 5000, y1 + 5000, y1 };
//			shape= new Polygon(arrx, arry, 5);
//		}
//		return shape;
//	}
//
//	public int getY() {
//		return y;
//	}
//
}
