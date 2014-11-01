package sos.base.util.fire_search_building;

import sos.base.entities.Road;

public class SensebleAreaOfRoadArea extends SensebleAreaShape{

	private final Road road;
	public SensebleAreaOfRoadArea(int[] apexes,Road road) {
		super(apexes);
		this.road = road;
	}
	public Road getRoad() {
		return road;
	}
	/*private int centerX;
	private int centerY;

	public SensebleAreaOfRoadArea(int[] apexes,Road road) {
		this.road = road;
		computeCenter(apexes);
	}

	
	private void computeCenter(int[] apexes) {
		centerX = 0;
		centerY = 0;
		for (int i = 0; i < apexes.length; i+=2) {
			centerX = centerX + apexes[i];
			centerY = centerY + apexes[i+1];
		}
		centerX /= apexes.length/2;
		centerY /= apexes.length/2;
	}
	public int getCenterX() {
		return centerX;
	}
	public int getCenterY() {
		return centerY;
	}
	*/
}
