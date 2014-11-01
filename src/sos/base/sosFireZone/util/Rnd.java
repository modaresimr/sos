package sos.base.sosFireZone.util;

import java.util.Random;

public class Rnd {

	private static Random rnd;

	public static void setSeed(long seed) {
		if (seed <= 0)
			rnd = new Random(System.currentTimeMillis());
		else
			rnd = new Random(seed);
	}

	public static double get01() {
		if (rnd == null) {
			rnd = new Random();
		}
		return rnd.nextDouble();
	}

	public static double get(double min, double max) {
		double d = get01();
		return ((max - min) * d) + min;
	}
}
