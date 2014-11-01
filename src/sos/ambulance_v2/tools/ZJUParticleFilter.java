package sos.ambulance_v2.tools;

import java.util.ArrayList;
import java.util.Collections;

public class ZJUParticleFilter {

	private int m_dmg_ob;
	private int m_hp_ob;
	private int m_bury;
	private int m_lastUpdate;
	private double[][] m_particles;
	private int m_particles_time;
	private int[] m_deadTime;
	private boolean m_particlesNeedResample = false;

	private boolean m_propertyChanged;
	private int m_time_needRefresh;

	// modified from 08Base
	private ArrayList<Integer> hpHistory;
	private ArrayList<Integer> dmgHistory;

	/**
	 * 构造函数
	 */
	public ZJUParticleFilter() {
		m_dmg_ob = 0;
		m_bury = -1;
		m_hp_ob = 10000;
		m_lastUpdate = -1;
		m_particles = null;
		m_particles_time = 0;
		m_deadTime = new int[60];
		m_propertyChanged = false;
		m_time_needRefresh = 50;
		hpHistory = new ArrayList<Integer>();
		dmgHistory = new ArrayList<Integer>();
	}

	/**
	 * @return dead time
	 *
	 */
	public int[] getDeadTime() {

		return m_deadTime;
	}

	/**
	 * 如果和原来的dmg不相等，那么设置m_propertyChanged = true;
	 *
	 * @param dmg
	 *            m_dmg_ob=dmg
	 * @param time
	 *            m_lastUpdate=time
	 *
	 */
	public void setDmg(int dmg, int time) {
		// if(time <= m_lastUpdate)
		// return;
		if (dmg != m_dmg_ob)
			m_propertyChanged = true;
		m_dmg_ob = dmg;
		m_lastUpdate = time;
		dmgHistory.add(dmg);
		/**
		 * 如果开始的时候第一个dmg=0，后面修正这个值，如果发现了dmg>100，重新进行初始化 modified from 08Base
		 */
		if (dmgHistory.get(0) == 0) {
			if (dmg >= 100) {
				initParticles();
			}
		}
	}

	/**
	 * 如果和原来的hp不相等，那么设置m_propertyChanged = true;
	 *
	 * @param hp
	 *            m_hp_ob=hp
	 * @param time
	 *            m_lastUpdate=time
	 *
	 */
	public void setHp(int hp, int time) {
		// if(time <= m_lastUpdate)
		// return;
		if (hp != m_hp_ob)
			m_propertyChanged = true;
		m_hp_ob = hp;
		m_lastUpdate = time;
		/**
		 * 每一次设置hp，我们都将这个添加到hpHistory队列中去
		 */
		hpHistory.add(hp);
	}

	/**
	 * 设置被掩埋的程度
	 *
	 * @param bury
	 */
	public void setBury(int bury) {

		if (m_bury == -1)
			m_bury = bury;
	}

	public void setParticlesNeedResample(){
		this.m_particlesNeedResample = true;
	}

	/**
	 * 初始化的粒子为600个 hpTable[6],建立一个在m_hp_ob附近的值以200为梯度，-499,-299,-99,101,301,499
	 * </br> dmgTable[10],建立一个m_dmg_ob 附件的合法值，分为10份，9.98/9为步长 </br>
	 * brokenRateTable[10] ，建立一个5至95以10为步长的表 </br> 可以很容易发现6*10*10=600； </br> int
	 * a = i/100;</br> int b = (i%100)/10;</br> int c = i%10;</br>
	 * 用上面的三个区分，并且初始化result，这样建立了600个hp，dmg，brokenRate不同的粒子 </br>
	 *
	 * @return result(double[600][3]);
	 */
	private double[][] initTempParticles() {
		double[][] result = new double[600][3];
		double[] hpTable = new double[6];
		hpTable[0] = m_hp_ob - 499;
		hpTable[1] = m_hp_ob - 299;
		hpTable[2] = m_hp_ob - 99;
		hpTable[3] = m_hp_ob + 101;
		hpTable[4] = m_hp_ob + 301;
		hpTable[5] = m_hp_ob + 499;
		// six possible case for near the m_hp_ob
		double[] dmgTable = new double[10];
		double step = 9.98 / 9;
		for (int i = 0; i < 10; i++)
			dmgTable[i] = m_dmg_ob - 4.99 + step * i;
		//
		double[] brokenRateTable = new double[10];
		for (int i = 0; i < 10; i++)
			brokenRateTable[i] = 5 + 10 * i;
		//
		for (int i = 0; i < 600; i++) {
			int a = i / 100;
			int b = (i % 100) / 10;
			int c = i % 10;
			result[i][0] = hpTable[a];
			if (m_dmg_ob == 0)
				result[i][1] = 3.0;
			else
				result[i][1] = dmgTable[b];
			if (m_bury == 0)
				result[i][2] = 100.0;
			else
				result[i][2] = brokenRateTable[c];
		}
		//
		return result;
	}

	/**
	 *
	 * 初始化的粒子为600个 hpTable[6],建立一个在m_hp_ob附近的值以200为梯度，-499,-299,-99,101,301,499
	 * </br> dmgTable[10],建立一个m_dmg_ob 附件的合法值，分为10份，9.98/9为步长 </br>
	 * brokenRateTable[10] ，建立一个5至95以10为步长的表 </br> 可以很容易发现6*10*10=600； </br> int
	 * a = i/100;</br> int b = (i%100)/10;</br> int c = i%10;</br>
	 * 用上面的三个区分，并且初始化result，这样建立了600个hp，dmg，brokenRate不同的粒子 </br>
	 *
	 * m_deadTime = calculateDeadTime(agent);
	 *
	 */
	private void initParticles() {
		m_propertyChanged = false;
		m_time_needRefresh = m_lastUpdate;
		m_time_needRefresh += (int) (Math.random() * 20);
		m_time_needRefresh += 30;
		//
		m_particles = new double[600][3];
		m_particles_time = m_lastUpdate;
		//
		double[] hpTable = new double[6];
		if (m_hp_ob % 1000 == 0) {
			hpTable[0] = m_hp_ob - 499;
			hpTable[1] = m_hp_ob - 299;
			hpTable[2] = m_hp_ob - 99;
			hpTable[3] = m_hp_ob + 101;
			hpTable[4] = m_hp_ob + 301;
			hpTable[5] = m_hp_ob + 499;
		} else {
			hpTable[0] = m_hp_ob - 20;
			hpTable[1] = m_hp_ob - 10;
			hpTable[2] = m_hp_ob;
			hpTable[3] = m_hp_ob;
			hpTable[4] = m_hp_ob + 10;
			hpTable[5] = m_hp_ob + 20;
		}
		//
		double[] dmgTable = new double[10];
		double step = 9.98 / 9;
		for (int i = 0; i < 10; i++)
			dmgTable[i] = m_dmg_ob - 4.99 + step * i;
		//
		double[] brokenRateTable = new double[10];
		for (int i = 0; i < 10; i++)
			brokenRateTable[i] = 5 + 10 * i;
		//
		for (int i = 0; i < 600; i++) {
			int a = i / 100;
			int b = (i % 100) / 10;
			int c = i % 10;
			m_particles[i][0] = hpTable[a];
			if (m_dmg_ob == 0)
				m_particles[i][1] = 3.0;
			else
				m_particles[i][1] = dmgTable[b];
			if (m_bury == 0)
				m_particles[i][2] = 100.0;
			else
				m_particles[i][2] = brokenRateTable[c];
		}

		m_deadTime = calculateDeadTime();
	}

	private double[] lifeSpan(double[] status, int time) {
		double[] result = new double[3];
		double hp = status[0];
		double dmg = status[1];
		double brokenDmg = dmg * status[2] / 100.0;
		double buryDmg = dmg - brokenDmg;
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

	public static void main(String[] args) {
		double[] status = new double[3];
		status[0] = 10000;
		status[1] = 00;
		status[2] = 60;
		System.out.println(calculate(status));
	}

	private static int calculate(double[] status) {
		double hp = status[0];
		double dmg = status[1];
		double brokenDmg = dmg * status[2] / 100.0;
		double buryDmg = dmg - brokenDmg;
		int time = 0;
		while (hp > 0) {
			buryDmg += buryDmg * buryDmg * 0.000035;
			buryDmg += 0.01;
			brokenDmg += brokenDmg * brokenDmg * 0.00025;
			brokenDmg += 0.01;
			hp -= buryDmg;
			hp -= brokenDmg;
			time++;
			if (time > 400)
				return 400;
		}

		return time;
	}

	//
	/**
	 * 通过一个公式来计算。死亡时间
	 *
	 * @param status
	 *            这个是一个 status[3]的数组
	 * @return 计算得到的死亡时间结果
	 */
	private int calculateDeadTime(double[] status) {
		double hp = status[0];
		double dmg = status[1];
		double brokenDmg = dmg * status[2] / 100.0;
		double buryDmg = dmg - brokenDmg;
		int time = 0;
		while (hp > 0) {
			buryDmg += buryDmg * buryDmg * 0.000035;
			buryDmg += 0.01;
			brokenDmg += brokenDmg * brokenDmg * 0.00025;
			brokenDmg += 0.01;
			hp -= buryDmg;
			hp -= brokenDmg;
			time++;
			if (time > 400)
				return m_particles_time + 400;
		}
		// if(m_particles_time + time <= 30)
		// System.out.println("!!!!!!!"+status[0]+"  "+status[1]+"  "+status[2]);
		return m_particles_time + time;
	}

	//
	/**
	 * 通过公式计算死亡时间
	 *
	 * @param status
	 *            这个是一个 status[3]的数组
	 * @param baseTime
	 *            基准时间
	 * @return 死亡时间
	 */
	private int calculateDeadTime(double[] status, int baseTime) {
		double hp = status[0];
		double dmg = status[1];
		double brokenDmg = dmg * status[2] / 100.0;
		double buryDmg = dmg - brokenDmg;
		int time = 0;
		while (hp > 0) {
			buryDmg += buryDmg * buryDmg * 0.000035;
			buryDmg += 0.01;
			brokenDmg += brokenDmg * brokenDmg * 0.00025;
			brokenDmg += 0.01;
			hp -= buryDmg;
			hp -= brokenDmg;
			time++;
			if (time > 400)
				return baseTime + 400;
		}
		// if(m_particles_time + time <= 30)
		// System.out.println("!!!!!!!"+status[0]+"  "+status[1]+"  "+status[2]);
		return baseTime + time;
	}

	//
	/**
	 *
	 * @return 计算出来的60个死亡时间的结果
	 */
	private int[] calculateDeadTime() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int[] finalResult = new int[60];
		if (m_dmg_ob != 0) {
			for (int i = 0; i < 60; i++) {
				int index = 0;
				index = (int) (Math.random() * 600);
				result.add(calculateDeadTime(m_particles[index]));
			}
			Collections.sort(result);

			for (int i = 0; i < result.size(); i++)
				finalResult[i] = result.get(i);
		} else if (m_dmg_ob == 0) {
			// modified from 08Base
			int j = 0;
			// 获得最开始的hp值
			int last = hpHistory.get(hpHistory.size() - 1);
			for (int i = 0; i < 60; i++) {
				if (last < 10000) {
					finalResult[i] = (m_lastUpdate) * 10000 / (10000 - last);
				} else {
					/**
					 * 410的意思只是指这个数很大，超过了300
					 */
					finalResult[i] = 410;
				}

			}
		}
		return finalResult;
	}

	private boolean checkParticle(double[] status) {
		double hp = m_hp_ob;
		double dmg = m_dmg_ob;
		if (m_hp_ob % 1000 == 0) {
			return (status[0] > hp - 500 && status[0] < hp + 500
					&& status[1] > dmg - 5 && status[1] < dmg + 5);
		} else {
			return (status[0] > hp - 26 && status[0] < hp + 26
					&& status[1] > dmg - 5 && status[1] < dmg + 5);
		}
	}

	private void updateParticles(int timeNow) {
		int time = m_lastUpdate - m_particles_time;
		if (time <= 0)
			return;
		// System.out.println("update time: "+time);
		boolean propertyChanged = m_propertyChanged;
		m_propertyChanged = false;
		ArrayList<double[]> newParticle = new ArrayList<double[]>();
		for (int i = 0; i < 600; i++) {
			double[] newElement = lifeSpan(m_particles[i], time);
			if (checkParticle(newElement)) {
				// System.out.println("success");
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
			// // check if resample is needed ///
			double[][] tmpParticle = initTempParticles();
			ArrayList<Integer> deadTime = new ArrayList<Integer>();
			int total = 0;
			for (int i = 0; i < 60; i++) {
				int index = 0;
				index = (int) (Math.random() * 600);
				int t = calculateDeadTime(tmpParticle[index], m_lastUpdate);
				deadTime.add(t);
				total += t;
			}
			int avg = total / 60;
			total = 0;
			for (int i = 0; i < 60; i++)
				total += Math.abs(deadTime.get(i) - avg);
			int scatterNew = total;
			//
			deadTime = new ArrayList<Integer>();
			total = 0;
			for (int i = 0; i < 60; i++) {
				int index = 0;
				index = (int) (Math.random() * 600);
				int t = calculateDeadTime(m_particles[index]);
				deadTime.add(t);
				total += t;
			}
			avg = total / 60;
			total = 0;
			for (int i = 0; i < 60; i++)
				total += Math.abs(deadTime.get(i) - avg);
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
		// System.out.println("success");
		//
		m_time_needRefresh = timeNow + 50;
		m_particles = new double[600][3];
		for (int i = 0; i < newParticle.size(); i++)
			m_particles[i] = newParticle.get(i);
		for (int i = newParticle.size(); i < 600; i++) {
			int index = 0;
			index = (int) (Math.random() * newParticle.size());
			m_particles[i] = newParticle.get(index);
		}
		m_particles_time = m_lastUpdate;
		m_deadTime = calculateDeadTime();
	}

	/**
	 * 将当前的时间作为参数传入其中
	 *
	 * @param time
	 *            timenow
	 *
	 */
	public void cycle(int time) {
		if (m_particles == null && m_lastUpdate > 0)
			initParticles();
		if (m_propertyChanged == true || time > m_time_needRefresh)
			updateParticles(time);
	}

	@Override
	public String toString() {
		return "Hp:" + this.m_hp_ob + " Dmg:" + this.m_dmg_ob + " Bury:"
				+ this.m_bury;
	}
}