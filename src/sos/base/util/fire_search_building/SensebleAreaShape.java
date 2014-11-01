package sos.base.util.fire_search_building;

import java.awt.Polygon;
import java.awt.Shape;

import sos.base.entities.ShapeableObject;

public class SensebleAreaShape implements ShapeableObject{

	private final Polygon shape;
	private int centerX;
	private int centerY;
	public SensebleAreaShape(int[] apexes) {
		int count = apexes.length / 2;
		int[] xs = new int[count];
		int[] ys = new int[count];
		for (int i = 0; i < count; ++i) {
			xs[i] = apexes[i * 2];
			ys[i] = apexes[i * 2 + 1];
		}
		shape=new Polygon(xs, ys, count);
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
	public int[] getApexes() {
		int[] apexes=new int[shape.npoints*2];
		for (int i = 0; i < shape.npoints; i++) {
			apexes[i*2]=shape.xpoints[i];
			apexes[i*2+1]=shape.ypoints[i];
		}
		
		return apexes;
	}
	
	@Override
	public Shape getShape() {
		return shape;
	}
}
