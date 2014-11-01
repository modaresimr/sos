package sos.base.sosFireZone.util;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;

import sos.base.entities.Area;
import sos.base.util.blockadeEstimator.AliGeometryTools;

public class ConvexHull_arr_New {

	private int[][] convexpoints;

	/**
	 * @author MasHouD
	 *         bar asase X hame sort mishan ,,,, bad az un LeftMost va RightMost
	 *         ro joda mionim ... va baraye do ghesmate paeen va balaye khate
	 *         sakhte shode ba an do harkat mikonim va noghat ra yeki yeki ezafe
	 *         mikonim va age didim moshkel ijad mikonan hazf mishan
	 */

	public ConvexHull_arr_New(Collection<? extends Area> aList) {
		this(aList, false);
	}

	/**
	 * FOR MERGE TWO CONVEX INTO ONE
	 */
	public ConvexHull_arr_New(ConvexHull_arr_New c1, ConvexHull_arr_New c2) {

		int firstLength = c1.convexpoints[0].length;
		int[][] points = new int[2][(c1.convexpoints[0].length) + (c2.convexpoints[0].length)];
		for (int i = 0; i < c1.convexpoints[0].length; i++) {
			points[0][i] = c1.convexpoints[0][i];
			points[1][i] = c1.convexpoints[1][i];
		}
		for (int i = 0; i < c2.convexpoints[0].length; i++) {
			points[0][i + firstLength] = c2.convexpoints[0][i];
			points[1][i + firstLength] = c2.convexpoints[1][i];
		}
		convexpoints = getConvexHull(points);
	}

	public ConvexHull_arr_New(Collection<? extends Area> aList, boolean withCenterpoints) {
		if (withCenterpoints)
			convexpoints = getConvex(aList);
		else
			convexpoints = getConvexFullApexes(aList);
	}

	private ConvexHull_arr_New(int[][] convexpoints2) {
		convexpoints = new int[convexpoints2.length][];

		for (int i = 0; i < convexpoints2.length; i++) {
			convexpoints[i] = new int[convexpoints2[i].length];
			for (int j = 0; j < convexpoints2[i].length; j++) {
				convexpoints[i][j] = convexpoints2[i][j] + 0;
			}
		}
		//		convexpoints = convexpoints2;
	}

	@Override
	public ConvexHull_arr_New clone() {
		return new ConvexHull_arr_New(convexpoints);
	}

	public static QuickSortX2D sorter = new QuickSortX2D();
	private int[] lowerIndex, upperIndex;
	private int lowerSize, upperSize;
	private int leftMostIndex, rightMostIndex;

	public int[][] getConvex(Collection<? extends Area> aList) {

		int[][] points = new int[2][aList.size()];
		int i = 0;
		for (Area area : aList) {
			points[0][i] = area.getX();
			points[1][i] = area.getY();
			i++;
		}
		return getConvexHull(points);
	}

	/**
	 * GetConvex2 creates a convex using all apexes
	 *
	 * @param aList
	 * @return
	 */
	private int[][] getConvexFullApexes(Collection<? extends Area> aList) {
		ArrayList<Integer> x = new ArrayList<Integer>(100);
		ArrayList<Integer> y = new ArrayList<Integer>(100);
		for (Area area : aList) {
			for (int i = 0; i < area.getApexList().length; i += 2) {
				x.add(area.getApexList()[i]);
				y.add(area.getApexList()[i + 1]);
			}
		}
		int[][] points = new int[2][x.size()];
		for (int i = 0; i < x.size(); i++) {
			points[0][i] = x.get(i);
			points[1][i] = y.get(i);
		}
		return getConvexHull(points);
	}

	/**
	 * @author MasHouD
	 * @param points
	 *            deghat kon voroodi inja tagheer mikone ha .. badan estefade
	 *            nakon azash
	 */
	public int[][] getConvexHull(int[][] points) {

		if (points[0].length <= 3)
			return points.clone();

		sorter.quickSort(points);

		rightMostIndex = points[0].length - 1;
		leftMostIndex = 0;

		if (lowerIndex == null || lowerIndex.length < points[0].length)
			lowerIndex = new int[points[0].length];
		if (upperIndex == null || upperIndex.length < points[0].length)
			upperIndex = new int[points[0].length];

		upperSize = lowerSize = 0;

		for (int i = 1; i < points[0].length - 1; i++) {
			if (direction(points[0][leftMostIndex], points[1][leftMostIndex], points[0][rightMostIndex], points[1][rightMostIndex], points[0][i], points[1][i]) < 0) {
				upperIndex[upperSize] = i;
				upperSize++;
			} else {
				lowerIndex[lowerSize] = i;
				lowerSize++;
			}
		}

		int[] newL = getHalfHull(points, lowerIndex, lowerSize, 1);
		int[] newU = getHalfHull(points, upperIndex, upperSize, -1);

		int newLSize, newUSize;
		for (int i = 0;; i++)
			if (newL[i] == points[0].length - 1) {
				newLSize = i + 1;
				break;
			}
		for (int i = 0;; i++)
			if (newU[i] == points[0].length - 1) {
				newUSize = i + 1;
				break;
			}

		int[][] result = new int[2][newLSize + newUSize - 2];
		for (int i = 0; i < newLSize; i++) {
			result[0][i] = points[0][newL[i]];
			result[1][i] = points[1][newL[i]];
		}
		for (int i = 1; i < newUSize - 1; i++) {
			result[0][newLSize + i - 1] = points[0][newU[newUSize - 1 - i]];
			result[1][newLSize + i - 1] = points[1][newU[newUSize - 1 - i]];
		}

		return result;
	}

	/**
	 * @param points
	 * @param index
	 * @param size
	 * @param factor
	 * @return
	 */
	private int[] getHalfHull(int[][] points, int[] index, int size, int factor) {
		int newSize = 0;
		int[] outputIndex = new int[size + 2];
		outputIndex[0] = 0;
		newSize = 1;

		index[size] = points[0].length - 1;
		size++;

		int i = 0;
		while (i < size) {
			outputIndex[newSize] = index[i];
			newSize++;
			while (newSize >= 3) {

				int leftIndex = outputIndex[newSize - 3];
				int rightIndex = outputIndex[newSize - 1];
				int targetIndex = outputIndex[newSize - 2];

				if (direction(points[0][leftIndex], points[1][leftIndex], points[0][rightIndex], points[1][rightIndex], points[0][targetIndex], points[1][targetIndex]) * factor <= 0) {
					newSize--;
					outputIndex[newSize - 1] = outputIndex[newSize];
				} else
					break;
			}
			i++;
		}

		return outputIndex;
	}

	private static double direction(int lx, int ly, int rx, int ry, int px, int py) {
		return ((double) (lx - rx) * (py - ry)) - ((double) (px - rx) * (ly - ry));

	}

	public boolean contains(int targetX, int targetY) {
		return getShape().contains(targetX, targetY);
	}

	public boolean contains2(int targetX, int targetY) {
		int[][] convex = convexpoints;
		int size = convexpoints[0].length;
		int up1, up2, down1, down2;
		int[][] arr = new int[2][size];
		for (int i = 0; i < size; i++) {
			arr[0][i] = convex[0][i];
			arr[1][i] = convex[1][i];
		}

		sorter.quickSort(arr);
		int leftIndex = 0;
		int rightIndex = (size - 1);
		up2 = down2 = 0;
		for (int i = 1; i < arr[0].length - 1; i++) {
			if (direction(arr[0][leftIndex], arr[1][leftIndex], arr[0][rightIndex], arr[1][rightIndex], arr[0][i], arr[1][i]) < 0) {
				up1 = up2;
				up2 = i;
				if (direction(arr[0][up1], arr[1][up1], arr[0][up2], arr[1][up2], targetX, targetY) < 0)
					return false;
			} else {
				down1 = down2;
				down2 = i;
				if (direction(arr[0][down1], arr[1][down1], arr[0][down2], arr[1][down2], targetX, targetY) > 0)
					return false;
			}
		}
		up1 = up2;
		up2 = rightIndex;
		if (direction(arr[0][up1], arr[1][up1], arr[0][up2], arr[1][up2], targetX, targetY) < 0)
			return false;
		down1 = down2;
		down2 = rightIndex;
		if (direction(arr[0][down1], arr[1][down1], arr[0][down2], arr[1][down2], targetX, targetY) > 0)
			return false;

		return true;
	}

	public boolean isInScaledConvex(float scale, int targetX, int targetY) {

		if (convexpoints == null)
			return false;
		int size = convexpoints[0].length;
		int up1, up2, down1, down2;
		int[][] arr = new int[2][size];
		for (int i = 0; i < size; i++) {
			arr[0][i] = convexpoints[0][i];
			arr[1][i] = convexpoints[1][i];
		}

		sorter.quickSort(arr);

		scaleConvex(arr, scale);
		int leftIndex = 0;
		int rightIndex = (size - 1);
		up2 = down2 = 0;
		for (int i = 1; i < arr[0].length - 1; i++) {
			if (direction(arr[0][leftIndex], arr[1][leftIndex], arr[0][rightIndex], arr[1][rightIndex], arr[0][i], arr[1][i]) < 0) {
				up1 = up2;
				up2 = i;
				if (direction(arr[0][up1], arr[1][up1], arr[0][up2], arr[1][up2], targetX, targetY) < 0)
					return false;
			} else {
				down1 = down2;
				down2 = i;
				if (direction(arr[0][down1], arr[1][down1], arr[0][down2], arr[1][down2], targetX, targetY) > 0)
					return false;
			}
		}
		up1 = up2;
		up2 = rightIndex;
		if (direction(arr[0][up1], arr[1][up1], arr[0][up2], arr[1][up2], targetX, targetY) < 0)
			return false;
		down1 = down2;
		down2 = rightIndex;
		if (direction(arr[0][down1], arr[1][down1], arr[0][down2], arr[1][down2], targetX, targetY) > 0)
			return false;

		return true;
	}

	public ConvexHull_arr_New getScaleConvex(float scale) {
		ConvexHull_arr_New newconvex = clone();
		scaleConvex(newconvex.convexpoints, scale);
		return newconvex;
	}

	private static void scaleConvex(int[][] convex, float scale) {
		int size = convex[0].length;
		double Cx, Cy;
		Cx = Cy = 0d;
		for (int i = 0; i < size; i++) {
			Cx += convex[0][i];
			Cy += convex[1][i];
		}
		Cx /= size;
		Cy /= size;

		for (int i = 0; i < size; i++) {
			convex[0][i] = (int) ((convex[0][i] - Cx) * scale + Cx);
			convex[1][i] = (int) ((convex[1][i] - Cy) * scale + Cy);
		}
	}

	Shape shape = null;

	public int[] getApexes() {
		int[] apexes = new int[convexpoints[0].length * 2];
		for (int i = 0; i < convexpoints[0].length; i++) {
			apexes[2 * i] = convexpoints[0][i];
			apexes[2 * i + 1] = convexpoints[1][i];
		}
		return apexes;
	}

	public Shape getShape() {
		if (shape == null) {
			//		sorter.quickSort(this.convexpoints);

			shape = AliGeometryTools.getShape(getApexes());
		}
		return shape;
	}
	//	public void addPointToCompleteConvex(int[][] convex, int x, int y) {
	//		//TODO benvisesh dg ... un rahe ghabli error dasht
	//	}

}
