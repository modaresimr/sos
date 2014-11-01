package sos.ambulance_v2.tools;

import java.util.ArrayList;
import java.util.Collections;

import sos.base.entities.Human;

/**
 * ParticleFilter by ZJU 2008
 * edited by @r@mik ali
 * future --> check and expand the size of damage array for more exact prediction
 */

public class SOSParticleFilter {
	private int m_dmg_ob;
	private int m_hp_ob;
	private int m_bury;
	private int m_lastUpdate;
	private float[][] m_particles;
	private int m_particles_time;
	public boolean m_particlesNeedResample = false;
	private int[] m_deadTime;
	//
	private boolean m_propertyChanged;
	private int m_time_needRefresh;
	private int buriedness;
	private static boolean HAVE_REFUGE;
	// aramik
	public static int HP_PRECISION;
	public static int ARRAY_PRECISION;
	public static int DAMAGE_PRECISION;
	private static float[] brokenRateTable = new float[10];
	static {
		for (int i = 0; i < 10; i++) {
			brokenRateTable[i] = 5 + 10 * i;
		}
	}

	public SOSParticleFilter(int hpPrecision, int damagePrecision, boolean haveRefuge) {
		m_dmg_ob = 0;
		m_bury = -1;
		m_hp_ob = 10000;
		m_lastUpdate = -1;
		m_particles = null;
		m_particles_time = 0;
		m_deadTime = new int[60];
		m_propertyChanged = false;
		m_time_needRefresh = 50;
		ARRAY_PRECISION = 100;
		HP_PRECISION = hpPrecision;
		DAMAGE_PRECISION = damagePrecision;
		HAVE_REFUGE = haveRefuge;
	}

	public SOSParticleFilter(Human hu) {
		this(hu.getAgent().getConfig().getIntValue("perception.los.precision.hp"),
				hu.getAgent().getConfig().getIntValue("perception.los.precision.damage"),
				!hu.getAgent().model().refuges().isEmpty());
	}

	public int[] getDeadTime() {
		return m_deadTime;
	}

	public void setDmg(int dmg, int time) {
		if (dmg != m_dmg_ob)
			m_propertyChanged = true;
		//		if (dmg % DAMAGE_PRECISION < DAMAGE_PRECISION/2)
		//			dmg = dmg - (dmg % DAMAGE_PRECISION);
		//		else
		//			dmg = dmg - (dmg % DAMAGE_PRECISION) + DAMAGE_PRECISION;

		m_dmg_ob = dmg;
		m_lastUpdate = time;
	}

	public void setHp(int hp, int time) {
		if (hp != m_hp_ob)
			m_propertyChanged = true;
		m_hp_ob = hp;
		m_lastUpdate = time;
	}

	public void setBury(int bury) {
		if (m_bury == -1)
			m_bury = bury;
		buriedness = bury;
	}

	private float[][] initTempParticles() {
		float[][] result = new float[60 * ARRAY_PRECISION][3];
		int[] hpTable = new int[6];
		int hp = getRealSensedValue(m_hp_ob, HP_PRECISION);
		int dmg = getRealSensedValue(m_dmg_ob, DAMAGE_PRECISION);
		hpTable[0] = hp - ((HP_PRECISION / 2) - 1);
		hpTable[1] = hp - (((HP_PRECISION * 3) / 10) - 1);
		hpTable[2] = hp - (HP_PRECISION / 5 - 1);
		hpTable[3] = hp + (HP_PRECISION / 5 + 1);
		hpTable[4] = hp + (((HP_PRECISION * 3) / 10) + 1);
		hpTable[5] = hp + ((HP_PRECISION / 2) - 1);
		//

		int worseDamage = getDamage(hpTable[0], m_dmg_ob, m_lastUpdate);
		int lessDamage = getDamage(hpTable[5], m_dmg_ob, m_lastUpdate);
		float[] dmgTable = new float[ARRAY_PRECISION];
		float step = (worseDamage - lessDamage) / ARRAY_PRECISION;

		for (int i = 0; i < ARRAY_PRECISION; i++) {
			dmgTable[i] = lessDamage + step * i;
		}
		//
		for (int i = 0; i < 60 * ARRAY_PRECISION; i++) {
			int a = i / (10 * ARRAY_PRECISION);
			int b = (i % (10 * ARRAY_PRECISION)) / ARRAY_PRECISION;
			int c = i % 10;
			result[i][0] = hpTable[a];
			if (dmg == 0)
				result[i][1] = (worseDamage + lessDamage) / 2;
			else
				result[i][1] = dmgTable[b];

			if (m_bury == 0)
				result[i][2] = 100.0f;
			else
				result[i][2] = brokenRateTable[c];
		}
		//
		return result;
	}

	private static int getDamage(int hp, int damage, int time) {
		int estimatedDamage = SimpleDeathTime.getEstimatedDamage(hp, time);
		damage = getRealSensedValue(damage, DAMAGE_PRECISION);

		return Math.min(Math.max(damage-DAMAGE_PRECISION/2, estimatedDamage), damage + DAMAGE_PRECISION/2);
	}

	//

	private void initParticles() {
		m_propertyChanged = false;
		m_time_needRefresh = m_lastUpdate;
		m_time_needRefresh += (int) (Math.random() * 20);

		m_time_needRefresh += 30;
		//
		m_particles = initTempParticles();
		m_particles_time = m_lastUpdate;
		//

		m_deadTime = calculateDeathTimeAgent();
	}
	//
	private float[] lifeSpan(float[] status, int time) {
		float[] result = new float[3];
		float hp = status[0];
		float dmg = status[1];
		float brokenDmg = dmg * status[2] / 100.0f;
		float buryDmg = dmg - brokenDmg;
		for (int i = 0; i < time; i++) {
			buryDmg += buryDmg * buryDmg * 0.000035;
			buryDmg += 0.01;
			brokenDmg += brokenDmg * brokenDmg * 0.00025;
			brokenDmg += 0.01;
			hp -= buryDmg;
			hp -= brokenDmg;
		}
		result[0] = hp;
		result[1] = buryDmg + brokenDmg;
		result[2] = brokenDmg / result[1] * 100;
		return result;
	}

	//
	private int calculateDeathTime(float[] status) {
		float hp = status[0];
		float dmg = status[1];
		float brokenDmg = dmg * status[2] / 100.0f;
		float buryDmg = dmg - brokenDmg;
		int time = 0;
		while (hp > 0) {
			buryDmg += buryDmg * buryDmg * 0.000035;
			buryDmg += 0.01;
			brokenDmg += brokenDmg * brokenDmg * 0.00025;
			brokenDmg += 0.01;
			hp -= buryDmg;
			hp -= brokenDmg;
			time++;
			if (time > 1000)
				return m_particles_time + 1000;
		}
		return m_particles_time + time;
	}

	//
	private int calculateDeathTime(float[] status, int baseTime) {
		float hp = status[0];
		float dmg = status[1];
		float brokenDmg = dmg * status[2] / 100.0f;
		float buryDmg = dmg - brokenDmg;
		int time = 0;
		while (hp > 0) {
			buryDmg += buryDmg * buryDmg * 0.000035;
			buryDmg += 0.01;
			brokenDmg += brokenDmg * brokenDmg * 0.00025;
			brokenDmg += 0.01;
			hp -= buryDmg;
			hp -= brokenDmg;
			time++;
			if (time > 1000)
				return baseTime + 1000;
		}
		return baseTime + time;
	}

	//
	private int[] calculateDeathTimeAgent() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < 60; i++) {
			int index = 0;
			index = (int) (Math.random() * 60 * ARRAY_PRECISION);

			result.add(calculateDeathTime(m_particles[index]));
		}
		Collections.sort(result);
		int[] finalResult = new int[result.size()];
		for (int i = 0; i < result.size(); i++)
			finalResult[i] = result.get(i);
		return finalResult;
	}

	public static int getRealSensedValue(int value, int preception) {
		if (value % preception < preception / 2)
			return value - (value % preception);
		else
			return value - (value % preception) + preception;
	}

	//
	private boolean checkParticle(float[] status) {
		if(status[0]>10000)
			return false;
		if(status[1]<0)
			return false;
		float hp = getRealSensedValue(m_hp_ob, HP_PRECISION);
		float dmg = getRealSensedValue(m_dmg_ob, DAMAGE_PRECISION);
		boolean isChecked = (status[0] > hp - (HP_PRECISION / 2) && status[0] < hp + (HP_PRECISION / 2)
				&& status[1] > dmg - (DAMAGE_PRECISION / 2) && status[1] < dmg + (DAMAGE_PRECISION / 2));
		System.out.println("checkParticle real hp="+hp+" real dmg="+dmg+" s[0]"+status[0]+" s[1]"+status[1]+" , isChecked?"+isChecked);
		return isChecked;
	}

	//
	private void updateParticlesAgent(int timeNow) {
		int time = m_lastUpdate - m_particles_time;
		if (time <= 0)
			return;
		//System.out.println("update time: "+time);
		boolean propertyChanged = m_propertyChanged;
		m_propertyChanged = false;
		ArrayList<float[]> newParticle = new ArrayList<float[]>();
		for (int i = 0; i < 60 * ARRAY_PRECISION; i++) {
			float[] newElement = lifeSpan(m_particles[i], time);
			if (checkParticle(newElement)) {
				newParticle.add(newElement);
			}
		}
		//
		if (newParticle.size() == 0) {

			if (propertyChanged && m_particlesNeedResample) {
				m_particlesNeedResample = false;
				initParticles();
				return;
			}
			//// check if resample is needed ///
			float[][] tmpParticle = initTempParticles();
			ArrayList<Integer> deadTime = new ArrayList<Integer>();
			int total = 0;
			for (int i = 0; i < 60; i++) {
				int index = 0;
				index = (int) (Math.random() * 60 * ARRAY_PRECISION);
				int t = calculateDeathTime(tmpParticle[index], m_lastUpdate);
				deadTime.add(t);
				total += t;
			}
			int death_avg = total / 60;
			total = 0;

			for (int i = 0; i < 60; i++) {
				total += Math.abs(deadTime.get(i) - death_avg);
			}
			int scatterNew = total;
			//
			deadTime = new ArrayList<Integer>();

			total = 0;
			for (int i = 0; i < 60; i++) {
				int index = 0;
				index = (int) (Math.random() * 60 * ARRAY_PRECISION);

				int t = calculateDeathTime(m_particles[index]);
				deadTime.add(t);
				total += t;
			}
			death_avg = total / 60;
			total = 0;

			for (int i = 0; i < 60; i++) {
				total += Math.abs(deadTime.get(i) - death_avg);
			}
			int scatterOld = total;
			//
			if (scatterNew < scatterOld) {
				initParticles();
				return;
			}
			//
			m_time_needRefresh = timeNow + 15;
			return;
		}
		//
		m_time_needRefresh = timeNow + 50;
		m_particles = new float[60 * ARRAY_PRECISION][3];
		for (int i = 0; i < newParticle.size(); i++)
			m_particles[i] = newParticle.get(i);
		for (int i = newParticle.size(); i < 60 * ARRAY_PRECISION; i++) {
			int index = 0;
			index = (int) (Math.random() * (newParticle.size()));
			m_particles[i] = newParticle.get(index);
		}
		m_particles_time = m_lastUpdate;
		m_deadTime = calculateDeathTimeAgent();
		System.out.println("D");
	}

	//

	public int cycle(int time) {
		if (m_particles == null && m_lastUpdate > 0) {
			initParticles();
		}
		if (m_propertyChanged == true || time > m_time_needRefresh) {
			updateParticlesAgent(time);
		}
		return m_time_needRefresh;

	}

	public void setParticlesNeedResample() {
		this.m_particlesNeedResample = true;
	}


	public int getProperDeadTime() {
		int index;
		if (HAVE_REFUGE) {
			index = 60 - m_bury;
			if (index < 5)
				index = 5;
			else if (index > 55)
				index = 55;
		} else
			index = 55;

		return getDeadTime()[index];
	}
	public static void main(String[] args) {
		SOSParticleFilter pf = new SOSParticleFilter(1000, 100, true);
//		System.out.println(getDamage(8500, 100, 20));
//		System.out.println(getDamage(9500, 100, 20));

		//		pf.setHp(8000, 50);
		//		pf.setDmg(70, 50);
		//		pf.cycle(50);

		//		int hp = 6668, damage = 45, time =110;
		//		int hp2 = 3552, damage2 = 67, time2 =166;

		//		int hp = 7000, damage = 30, time =110;
		//		int hp2 = 4000, damage2 = 100, time2 =166;
		//		System.out.println(SimpleDeathTime.getEstimatedDamage(hp, time));

		//		int hp = 8000, damage = 100, time =20;
		//		int hp2 = 5000, damage2 = 100, time2 =46,buriedness=60;
//		for (int i = 0; i < 49; i++) {
//			int hp = 10000, damage =33, time = i, buriedness = 60;
//			pf.setHp(hp, time);
//			pf.setDmg(damage, time);
//			pf.setBury(buriedness);
//			pf.cycle(time);
//		}
		int hp = 6000, damage =100, time = 165, buriedness = 60;
//		int hp2 = 5000, damage2 = 100, time2 = 46;
		//
		pf.setHp(hp, time);
		pf.setDmg(damage, time);
		pf.setBury(buriedness);
		pf.cycle(time);
		//
		//		pf.setHp(hp2, time2);
		//		pf.setDmg(damage2, time2);
		//		pf.cycle(time2);

		int index = 60 - buriedness;
		if (index < 5)
			index = 5;
		else if (index > 55)
			index = 55;
		System.out.println("pf "+index+"==>dt:"+pf.getDeadTime()[index]);

		System.out.println(SimpleDeathTime.getEasyLifeTime(hp, damage, time));
//		System.out.println(SimpleDeathTime.getEasyLifeTime(hp2, damage2, time2));
	}
}