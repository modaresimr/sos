package sos.fire_v2.base.tools;

public class FireBuildingPP {
	private double point = 0;
	public String st;

	public void setPoint(double point,int time) {
		this.point = point;
		st = "time == "+time+"\n";
	}

	public void addPoint(String reason, double point) {
		this.point += point;
		st += reason + "  = " + point + "\n";
	}

	public double getPoint() {
		return point;
	}
}
