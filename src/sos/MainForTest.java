package sos;

import sos.fire_v2.target.Tools;

/**
 * @author Ali
 */

public class MainForTest {

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
	
		System.out.println(Tools.getAngleBetweenTwoVector(-1, 1, 1, -1));
	}

	private static int getSizeOfRegions() {
		int buildingSize = 800;
		int fireSize = 50;
		int cluster = buildingSize / fireSize;
		double coef = Math.max(2, 100 / cluster);
		int db = (int) (coef * buildingSize / fireSize);
		System.out.println("DB " + db + "\t\tCOEF = " + coef);

		int old = (int) Math.ceil(buildingSize / fireSize);
		int best = -1;
		for (int i = fireSize; i > 0; i--) {
			System.out.println("INDEX = " + i + "\t\t" + "OLD = " + old + "\t\t NEW = " + Math.ceil(buildingSize / i));
			if (Math.ceil(buildingSize / i) - old > db) {

				best = i;
				System.out.println("YES\t" + i);
			}
			//			old=(int) Math.ceil(buildingSize / i);
		}
		return best;
	}
}
