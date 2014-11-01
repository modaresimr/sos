package sos.base.util.blockadeEstimator;

import java.awt.Shape;

import rescuecore2.geometry.Point2D;
import sos.base.entities.Road;

public interface BlockadeInterface {
	public int getX();

	public int getY();

	public int[] getApexes();

	public void setApexes(int[] apexes);

	public Road getPosition();


	public int getRepairCost();

	public Shape getShape();
	public Point2D getCenteroid();

}
