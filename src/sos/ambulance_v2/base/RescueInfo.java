package sos.ambulance_v2.base;

import java.security.InvalidParameterException;
import java.util.TreeSet;

import sos.ambulance_v2.AmbulanceUtils;
import sos.ambulance_v2.tools.FireDeathTime;
import sos.ambulance_v2.tools.ParticleFilter;
import sos.ambulance_v2.tools.SimpleDeathTime;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.Human;
import sos.base.entities.Refuge;
import sos.fire_v2.FireBrigadeAgent;
import sos.police_v2.PoliceForceAgent;

public class RescueInfo {
	public enum IgnoreReason {
		Unreachable, NotIgnored, NoRefuge, UnloadInRoad, HaveEnoughAT, FinishMessageReceived, IsNotMyMissionTarget, ShouldCheck_Unknown, NoComunicationAndFinished
		, IgnoredTargetMessageReceived, TargetOutOfMyAction, NotReachableToTarget, FinishedWorkOnTarget, ImNotLoader, CantLoad, InRefuge, NotReachableToRefuge, NoPosition
		, CenterAssignMoreImportantTarget, DeadHuman, FiryCivilian, InAmbulance, WillDie,LockInBlockade, StuckInUnreachableMode, IwasExtraAT, InvalidOldTarget, TargetLoadedByAnotherAT
	}

	public RescueInfo(Human hu) {
		this.me = hu;
	}

	private final Human me;
	private short fireDeathTime = 1000;
	private short injuryDeathTime = 999;

	private ParticleFilter pf;
	//	private ZJUParticleFilter zjuPf;

	private boolean m_lifeTimeLocker = false;

	private short ATneedToBeRescued = 0;
	private TreeSet<Integer> nowWorkingOnMe = null;

	private Refuge bestRefuge = null;
	private short timeToRefuge = 0;
	private short refugeCalculatedTime = 1;
	private short assignedTime;

	private long rescuePriority = 0;
	private int ignoredUntil = 0;
	private boolean longLife = false;
	private IgnoreReason ignoreReason = IgnoreReason.NotIgnored;

	public short getRefugeCalculatedTime() {
		return refugeCalculatedTime;
	}

	public void setRefugeCalculatedTime(int refugeCalculatedTime) {
		this.refugeCalculatedTime = (short) refugeCalculatedTime;
	}

	public boolean isM_lifeTimeLocker() {
		return m_lifeTimeLocker;
	}

	public void setM_lifeTimeLocker(boolean m_lifeTimeLocker) {
		this.m_lifeTimeLocker = m_lifeTimeLocker;
	}

	private ParticleFilter getPartileFilter() {
		if (pf == null)
			pf = new ParticleFilter(me);
		return this.pf;
	}

	public void setLongLife(boolean longLife) {
		this.longLife = longLife;
	}

	public boolean longLife() {
		return this.longLife;
	}

	public short getTimeToRefuge() {
		return timeToRefuge;
	}

	public void setTimeToRefuge(int timeToRefuge) {
		this.timeToRefuge = (short) timeToRefuge;
	}

	public short getAssignedTime() {
		return assignedTime;
	}

	public long getRescuePriority() {
		return rescuePriority;
	}

	public void setRescuePriority(long rescuePriority) {
		this.rescuePriority = rescuePriority;
	}

	public void setIgnoredUntil(IgnoreReason reason, int ignoredUntil) {
		this.ignoreReason = reason;
		this.ignoredUntil = ignoredUntil;
	}

	public void setNotIgnored() {
		setIgnoredUntil(IgnoreReason.NotIgnored, 2);
	}

	public boolean isIgnored() {
		return this.ignoreReason != IgnoreReason.NotIgnored;
	}

	public int getIgnoredUntil() {
		if (!isIgnored())
			return 2;
		return ignoredUntil;
	}

	public IgnoreReason getIgnoreReason() {
		return ignoreReason;
	}

	public void setAssignedTime(int assignedTime) {
		this.assignedTime = (short) assignedTime;
	}

	public int getDeathTime() {
		return Math.min(getFireDeathTime(), getInjuryDeathTime());
	}

	public short getFireDeathTime() {
		if (lastFireDeathTimeCalculate != me.model().time()) {
			fireDeathTime = (short) FireDeathTime.getFireDeathTime(me);
			if (me.model().refuges().isEmpty() && AmbulanceUtils.isHumaninFireBuilding(me))
				fireDeathTime = (short) me.model().time();

			lastFireDeathTimeCalculate = me.model().time();
		}
		return fireDeathTime;
	}

	public short getInjuryDeathTime() {
		int deathtimeEasy = SimpleDeathTime.getEasyLifeTime(me.getHP(), me.getDamage(), me.updatedtime());
		if (pf == null) {
			return (short) deathtimeEasy;
		}
		if (lastInjuryTimeCalculate != me.updatedtime()) {

			int deathtimePF = getPartileFilter().getProperDeadTime();
			//			int deathtimeZjuPF = zjuPf.getDeadTime()[index];
			injuryDeathTime = (short) deathtimePF;
			//			if (Math.abs(deathtimePF - deathtimeZjuPF) > 10)
			//				me.getAgent().sosLogger.worldModel.warn(me + " deathtime:" + deathtimePF + " but ZJU say:" + deathtimeZjuPF);

			if (deathtimePF < deathtimeEasy - 100 || deathtimePF > deathtimeEasy + 50) {
				me.getAgent().sosLogger.worldModel.debug("Estimated death time(" + deathtimePF + ") for " + me + " seem to not be currect! using easy death time(" + deathtimeEasy + ")");
				injuryDeathTime = (short) deathtimeEasy;
			}
			String humanInfoLog = me + " (hp:" + me.getHP() + ",damage:" + me.getDamage() + ",time:" + me.updatedtime() + "), buried=" + me.getBuriedness() + ",PartileDT:" + deathtimePF + ",EasyDT:" + deathtimeEasy + ",FireDT:" + getFireDeathTime() + " pos=" + me.getPosition();
			me.getAgent().sosLogger.worldModel.trace(humanInfoLog);
			lastInjuryTimeCalculate = me.updatedtime();
		}
		return injuryDeathTime;
	}

	private int lastFireDeathTimeCalculate = 0;
	private int lastInjuryTimeCalculate = 0;

	public short getATneedToBeRescued() {
		return ATneedToBeRescued;
	}

	public void setATneedToBeRescued(int aTneedToBeRescued) {
		ATneedToBeRescued = (short) aTneedToBeRescued;
	}

	public void addAT(AmbulanceTeam at) {
		if (at == null)
			throw new InvalidParameterException("AmbulanceTeam: " + at);

		if (this.nowWorkingOnMe == null)
			this.nowWorkingOnMe = new TreeSet<Integer>();

		nowWorkingOnMe.add(at.getID().getValue());
	}

	public void removeAT(AmbulanceTeam at) {
		if (this.nowWorkingOnMe == null)
			this.nowWorkingOnMe = new TreeSet<Integer>();
		nowWorkingOnMe.remove(at.getID().getValue());
	}

	public TreeSet<Integer> getNowWorkingOnMe() {
		if (this.nowWorkingOnMe == null)
			this.nowWorkingOnMe = new TreeSet<Integer>();
		return nowWorkingOnMe;
	}

	public void setBestRefuge(Refuge bestRefuge) {
		this.bestRefuge = bestRefuge;
	}

	public Refuge getBestRefuge() {
		return bestRefuge;
	}

	public int estimatedDamage() {
		int count = me.getAgent().time() - me.updatedtime();
		if (count <= 0)
			return me.getDamage();
		double dmg = me.getDamage();
		double k = 0.00025;
		while (count > 0) {
			dmg = dmg + k * dmg * dmg;
			count--;
		}
		return (int) Math.round(dmg);
	}

	public int estimatedHp() {
		int count = me.getAgent().time() - me.updatedtime();
		if (count <= 0 || me.getDamage() == 0)
			return me.getHP();
		double hp = me.getHP();
		double dmg = me.getDamage();
		double k = 0.00025;
		while (count > 0) {
			dmg = dmg + k * dmg * dmg;
			count--;
			hp -= dmg;
		}
		if (hp <= 0)
			return 0;
		return (int) Math.round(hp);
	}

	@Override
	public String toString() {
		return "RescueInfo[death=" + getDeathTime() + " ignored=" + isIgnored() + " ignoredUntil=" +
				ignoredUntil + " ignoreReason:" + ignoreReason + " need=" + ATneedToBeRescued + " AT=" + nowWorkingOnMe + " refuge=" + bestRefuge + " refTime=" + timeToRefuge + " long=" + longLife() + "]";
	}

	public void checkIsLongTime() {
		int longTimePeriod = 200;
		int time = me.model().time();
		if (time > 80)
			longTimePeriod = 150;
		//		if (time > 160)
		//			longTimePeriod = 100;

		if (me instanceof Civilian && getDeathTime() >= time + longTimePeriod)
			setLongLife(true);
		else
			setLongLife(false);
	}

	public void updateProperties() {
		//		if (!(me.getAgent() instanceof AmbulanceTeamAgent))
		//			return;
		if (me.getAgent() instanceof FireBrigadeAgent)
			return;
		if (me.getAgent() instanceof PoliceForceAgent)
			return;
		if(me.getDamage()==0)
			return;
		
		getPartileFilter().setDmg(me.getDamage(), me.updatedtime());
		getPartileFilter().setBury(me.getBuriedness());
		getPartileFilter().setHp(me.getHP(), me.updatedtime());

		// locking free situations
		if (isM_lifeTimeLocker()) {//TODO WTF?
			if (!(me.getPosition() instanceof Building))
				return;
			Building b = (Building) me.getPosition();
			if (b.isOnFire())
				return;
			setM_lifeTimeLocker(false);
			getPartileFilter().setParticlesNeedResample();
		}
		getPartileFilter().cycle(me.updatedtime());

		if (AmbulanceUtils.isBurning(me)) {
			setM_lifeTimeLocker(true);//TODO  chie?
		}
		checkIsLongTime();
	}

	public void disableDeathTimesIfNeeded() {
		if (pf == null)
			return;
		if (me.getAreaPosition() instanceof Refuge)
			getPartileFilter().disableParticleFilter();

		if (getPartileFilter().getDeadTime()[55] < me.model().time() - 10)
			getPartileFilter().disableParticleFilter();
	}
}