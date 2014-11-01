package sos.search_v2.worldModel;

import sos.base.entities.Area;
import sos.base.entities.Road;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public class SearchRoad {
	private Road realRoad;
	private int score;
	private int notEntranceNeighborCount = -1;//Salim

	public SearchRoad(Road realRoad) {
		this.realRoad = realRoad;
	}

	public Road getRealRoad() {
		return realRoad;
	}

	public void setRealRoad(Road realRoad) {
		this.realRoad = realRoad;
	}

	public int getScore() {
		return score;
	}

	public void addScore(int score) {
		this.score += score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	//Salim
	public int getNotEntranceNeighborCount() {
		if (notEntranceNeighborCount == -1) {
			notEntranceNeighborCount = 0;
			for (Area a : getRealRoad().getNeighbours()) {
				if (a instanceof Road && !((Road) a).isEntrance())
					notEntranceNeighborCount++;
			}
		}
		return notEntranceNeighborCount;
	}
}
