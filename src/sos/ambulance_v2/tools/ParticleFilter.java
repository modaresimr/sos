package sos.ambulance_v2.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import sos.base.entities.Human;

/**
 * ParticleFilter by ZJU 2008
 * edited by @r@mik
 * future --> check and expand the size of damage array for more exact prediction
 */

public class ParticleFilter {
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
	private static boolean HAVE_REFUGE;
	private int buriedness;
	private boolean disable;
	// aramik
	public static int HP_PRECISION;
	public static int DAMAGE_PRECISION;
	public static int ARRAY_PRECISION = 100;

	public ParticleFilter(int hpPrecision, int damagePrecision, boolean haveRefuge) {
		m_dmg_ob = 0;
		m_bury = -1;
		m_hp_ob = 10000;
		m_lastUpdate = -1;
		m_particles = null;
		m_particles_time = 0;
		m_deadTime = new int[60];
		m_propertyChanged = false;
		m_time_needRefresh = 50;

		HP_PRECISION = hpPrecision;
		DAMAGE_PRECISION = damagePrecision;
		HAVE_REFUGE = haveRefuge;
	}

	public ParticleFilter(Human hu) {
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
		m_dmg_ob = dmg;
		m_lastUpdate = time;
	}

	public void setHp(int hp, int time) {
		if (hp != m_hp_ob)
			m_propertyChanged = true;
		m_hp_ob = hp;
		m_lastUpdate = time;
		if (hp == 0) {
			disableParticleFilter();
		} else
			disable = false;
	}

	public void disableParticleFilter() {
		disable = true;
		m_particles = null;
		if (m_deadTime == null) {
			m_deadTime = new int[60];
			Arrays.fill(m_deadTime, m_lastUpdate + 10);
		}

	}

	public void setBury(int bury) {
		if (m_bury == -1)
			m_bury = bury;
		buriedness = bury;

	}

	private float[][] initTempParticles() {
		float[][] result = new float[60 * DAMAGE_PRECISION][3];
		float[] hpTable = new float[6];
		hpTable[0] = m_hp_ob - ((HP_PRECISION / 2) - 1);
		hpTable[1] = m_hp_ob - (((HP_PRECISION * 3) / 10) - 1);
		hpTable[2] = m_hp_ob - (HP_PRECISION / 5 - 1);
		hpTable[3] = m_hp_ob + (HP_PRECISION / 5 + 1);
		hpTable[4] = m_hp_ob + (((HP_PRECISION * 3) / 10) + 1);
		hpTable[5] = m_hp_ob + ((HP_PRECISION / 2) - 1);
		//
		float[] dmgTable = new float[DAMAGE_PRECISION];
		float step = 9.98f / 9;
		for (int i = 0; i < DAMAGE_PRECISION; i++) {
			dmgTable[i] = (m_dmg_ob) - (DAMAGE_PRECISION / 2 - 0.01f) + step * i;
		}
		float[] brokenRateTable = new float[10];
		for (int i = 0; i < 10; i++)
			brokenRateTable[i] = 5 + 10 * i;
		//
		for (int i = 0; i < 60 * DAMAGE_PRECISION; i++) {
			int a = i / (10 * DAMAGE_PRECISION);
			int b = (i % (10 * DAMAGE_PRECISION)) / DAMAGE_PRECISION;
			int c = i % 10;
			m_particles[i][0] = hpTable[a];
			if (m_dmg_ob == 0)
				m_particles[i][1] = DAMAGE_PRECISION / 3;
			else
				m_particles[i][1] = dmgTable[b];
			if (m_bury == 0)
				m_particles[i][2] = 100.0f;
			else
				m_particles[i][2] = brokenRateTable[c];
		}
		//
		return result;
	}

	//

	private void initParticles() {
		m_propertyChanged = false;
		m_time_needRefresh = m_lastUpdate;
		m_time_needRefresh += (int) (Math.random() * 20);

		m_time_needRefresh += 30;
		//
		m_particles = new float[60 * DAMAGE_PRECISION][3];
		m_particles_time = m_lastUpdate;
		//
		float[] hpTable = new float[6];
		if (m_hp_ob % HP_PRECISION == 0) {
			hpTable[0] = m_hp_ob - ((HP_PRECISION / 2) - 1);
			hpTable[1] = m_hp_ob - (((HP_PRECISION * 3) / 10) - 1);
			hpTable[2] = m_hp_ob - (HP_PRECISION / 5 - 1);
			hpTable[3] = m_hp_ob + (HP_PRECISION / 5 + 1);
			hpTable[4] = m_hp_ob + (((HP_PRECISION * 3) / 10) + 1);
			hpTable[5] = m_hp_ob + ((HP_PRECISION / 2) - 1);
		} else {
			hpTable[0] = m_hp_ob - 20;
			hpTable[1] = m_hp_ob - 10;
			hpTable[2] = m_hp_ob;
			hpTable[3] = m_hp_ob;
			hpTable[4] = m_hp_ob + 10;
			hpTable[5] = m_hp_ob + 20;
		}
		//
		float[] dmgTable = new float[DAMAGE_PRECISION];
		float step = 9.98f / 9;
		for (int i = 0; i < DAMAGE_PRECISION; i++) {
			dmgTable[i] = (m_dmg_ob) - (DAMAGE_PRECISION / 2 - 0.01f) + step * i;
		}
		//
		float[] brokenRateTable = new float[10];
		for (int i = 0; i < 10; i++) {
			brokenRateTable[i] = 5 + 10 * i;
		}
		//
		for (int i = 0; i < 60 * DAMAGE_PRECISION; i++) {
			int a = i / (10 * DAMAGE_PRECISION);
			int b = (i % (10 * DAMAGE_PRECISION)) / DAMAGE_PRECISION;
			int c = i % 10;
			m_particles[i][0] = hpTable[a];
			if (m_dmg_ob == 0)
				m_particles[i][1] = DAMAGE_PRECISION / 3;
			else
				m_particles[i][1] = dmgTable[b];
			if (m_bury == 0)
				m_particles[i][2] = 100.0f;
			else
				m_particles[i][2] = brokenRateTable[c];
		}

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
			index = (int) (Math.random() * 60 * DAMAGE_PRECISION);

			result.add(calculateDeathTime(m_particles[index]));
		}
		Collections.sort(result);
		int[] finalResult = new int[result.size()];
		for (int i = 0; i < result.size(); i++)
			finalResult[i] = result.get(i);
		return finalResult;
	}

	//
	private boolean checkParticle(float[] status) {
		float hp = m_hp_ob;
		float dmg = m_dmg_ob;
		if (m_hp_ob % HP_PRECISION == 0) {
			return (status[0] > hp - (HP_PRECISION / 2) && status[0] < hp + (HP_PRECISION / 2)
					&& status[1] > dmg - (DAMAGE_PRECISION / 2) && status[1] < dmg + (DAMAGE_PRECISION / 2));
		} else {
			return (status[0] > hp - 26 && status[0] < hp + 26
					&& status[1] > dmg - 5 && status[1] < dmg + 5);
		}
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
		for (int i = 0; i < 60 * DAMAGE_PRECISION; i++) {
			float[] newElement = lifeSpan(m_particles[i], time);
			if (checkParticle(newElement)) {

				//System.out.println("success --> new element="+newElement[0]+" "+newElement[1]+" "+newElement[2]);
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
				index = (int) (Math.random() * 60 * DAMAGE_PRECISION);
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
				index = (int) (Math.random() * 60 * DAMAGE_PRECISION);

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
		m_particles = new float[60 * DAMAGE_PRECISION][3];
		for (int i = 0; i < newParticle.size(); i++)
			m_particles[i] = newParticle.get(i);
		for (int i = newParticle.size(); i < 60 * DAMAGE_PRECISION; i++) {
			int index = 0;
			index = (int) (Math.random() * (newParticle.size()));
			m_particles[i] = newParticle.get(index);
		}
		m_particles_time = m_lastUpdate;
		m_deadTime = calculateDeathTimeAgent();
	}

	//

	public int cycle(int time) {
		if (disable)
			return 1;
		if (m_particles == null && m_lastUpdate > 0) {
			initParticles();
		}
		if (m_propertyChanged == true || time > m_time_needRefresh) {
			updateParticlesAgent(time);
		}
		return m_time_needRefresh;

	}

	public int getProperDeadTime() {
		int index;
		if (HAVE_REFUGE) {
			index = 60 - buriedness;
			if (index < 5)
				index = 5;
			else if (index > 55)
				index = 55;
		} else
			index = 55;

		return getDeadTime()[index];
	}

	public void setParticlesNeedResample() {
		m_particlesNeedResample = true;
	}

}