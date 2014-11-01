package sos.ambulance_v2.tools;

public class SimpleDeathTime {
	/**
	 * a simple method to find life time added by Aramik
	 *
	 * @param hp
	 * @param dmg
	 * @param time
	 * @return
	 */
	public static int getEasyLifeTime_OLD(int hp, int dmg, int time) {
		if (dmg <= 0)
			return Integer.MAX_VALUE;
		if (hp <= 0)
			return 0;
		double alpha = 0;
		double newAlpha = 0.01;
		while (java.lang.Math.abs(alpha - newAlpha) > 1E-10) {

			alpha = newAlpha;
			double tmp = java.lang.Math.exp(-alpha * time);
			newAlpha = ((alpha * time + 1) * tmp - 1) /
								(time * tmp - (double) (10000 - hp) / dmg);
		}

		if (alpha > 0)
			return (int) (java.lang.Math.ceil((7.0 / 8) *
								java.lang.Math.log(alpha * hp / dmg + 1) / alpha));
		else
			return hp / dmg;
	}
	/**
	 * a simple method to find life time added by Aramik
	 *
	 * @param hp
	 * @param dmg
	 * @param time
	 * @return
	 */
	public static int getEasyLifeTime(int hp, int dmg, int time) {
		return estimatedDeathTime(hp, dmg, time);
//		return getEasyLifeTime_OLD(hp, dmg, time);
	}
	public static int estimatedDeathTime(int hp, double dmg,int updatetime) {

		int agenttime=1000;
		int count = agenttime - updatetime;
		if (count <= 0 || dmg == 0)
			return hp;

		double kbury = 0.000035;
		double kcollapse = 0.00025;
		double darsadbury = -0.0014 * updatetime + 0.64;
		double burydamage = dmg * darsadbury;
		double collapsedamage = dmg - burydamage;

		while (count > 0) {
			int time = agenttime - count;
//			System.out.print("cycle:"+time+" bury:"+burydamage+" collapse:"+collapsedamage+" dmg:"+dmg);
			burydamage += kbury * burydamage * burydamage + 0.11 ;
			collapsedamage += kcollapse * collapsedamage * collapsedamage+0.11 ;
			dmg=burydamage+collapsedamage;
			count--;
			hp -= dmg;
//			System.out.print("cycle:"+time+" darsad:"+darsadbury);
//			System.out.println(" hp:"+hp+" damge:"+dmg);
			if (hp <= 0)
				return time;
		}
		return 1000;
	}
	private static int calculateDeathTime(float hp,float dmg,float buriedness,int time) {

		float brokenDmg = dmg * buriedness/ 100.0f;
		float buryDmg = dmg - brokenDmg;

		while (hp > 0) {
			buryDmg += buryDmg * buryDmg * 0.000035;
			buryDmg += 0.01;
			brokenDmg += brokenDmg * brokenDmg * 0.00025;
			brokenDmg += 0.01;
			hp -= buryDmg;
			hp -= brokenDmg;
			time++;
			if (time > 1000)
				return 1000;
		}
		return  time;
	}

	public static int getEstimatedDamage(int hp, int time) {
		return (10000 - hp) / time;
	}

	public static void main(String[] args) {
		System.out.println(getEasyLifeTime(9982,30, 100));
		System.out.println(getEasyLifeTime_OLD(9982,30, 100));
		System.out.println(calculateDeathTime(9982,30, 20,100));
//		System.out.println(getEasyLifeTime_OLD(10000,20, 8));
	}
}
