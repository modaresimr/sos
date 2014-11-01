package sos.base.sosFireZone;

import java.util.ArrayList;
import java.util.Arrays;

import sos.base.SOSConstant;
import sos.base.entities.Building;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.sosFireZone.util.ConvexHull_arr_New;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

public abstract class SOSAbstractFireZone {
	/**
	 * Indicates the fire zone completely extinguished </br>
	 * True if fire zone extinguished and searched
	 */
	protected boolean isDisable = false;

	/**
	 * Indicates the fire zone extinguished </br>
	 * True if fire zone extinguished
	 */
	private boolean isExtinguishable = true;

	/**
	 * The time that we think fire zone extinguished
	 */
	protected int extinguishedTime = -1;
	/**
	 * The time that fire zone disabled
	 */
	public int disabledTime = -1;

	private boolean isEstimating = true;
	/**
	 * Fire zone ID
	 */
	protected short index;
	/**
	 * Array of buildings which are near the outer </br>
	 * Specially for Pre_Extinguish
	 */
	protected ArrayList<Building> safeBuilding;
	/**
	 * All building of fireZone
	 */
	protected ArrayList<Building> allBuilding;
	/**
	 * The buildings which are burning
	 */
	protected ArrayList<Building> burningBuildings;//TODO
	/**
	 * The buildings which are outside of fire zone </br>
	 * Specially for Extinguish
	 */
	protected ArrayList<Building> outer;
	/**
	 * Indicates the fire zone is reported or not
	 */
	private int reportTime = -1;//For No Comm TODO should examine

	protected ArrayList<Building> dangerBuilding;//TODO

	protected SOSLoggerSystem fireLog;

	public SOSFireZoneManager manager;

	protected SiteSize size = SiteSize.Undefine;;

	/**
	 * Center of fire zone
	 */
	protected int centerX;

	/**
	 * Center of fire zone
	 */
	protected int centerY;

	public static int SMALL_SIZE_BUILDING_COUNT = 10;

	protected ConvexHull_arr_New convex = null;//TODO

	protected ArrayList<Building> convexed;//TODO

	private ConvexHull_arr_New outerConvex = null;//TODO

	protected final int DISABLE_TIME = 20;//TODO chand cycle bad az khamush shodan disale beshe

	public SOSAbstractFireZone(short hashCode, SOSFireZoneManager manager) {
		setIndex(hashCode);
		this.manager = manager;
		safeBuilding = new ArrayList<Building>();
		allBuilding = new ArrayList<Building>();
		outer = new ArrayList<Building>();
		burningBuildings = new ArrayList<Building>();
		dangerBuilding = new ArrayList<Building>();
		fireLog = new SOSLoggerSystem(manager.me.me(), "SOSFireSite/FireSites/" + getIndex(), SOSConstant.CREATE_BASE_LOGS, OutputType.File, true);
		centerX = 0;
		centerY = 0;
		manager.me.sosLogger.addToAllLogType(fireLog);
	}

	/**
	 * update fire zone
	 * 
	 * @param time
	 */
	public abstract void update(int time);

	/**
	 * update just one building according to message or extinguish
	 * 
	 * @param building
	 */
	public abstract void update(Building building);

	/**
	 * expand fire zone according to new fiery building
	 */
	protected abstract void updateSiteOuter();

	protected abstract boolean isAddableToOuter(Building forOuter, Building forSafe);

	protected abstract void computeSafeAndOuter();

	protected abstract boolean checkIland(Building forOuter, Building forSafe);

	public void addFieryBuilding(Building b) {
		allBuilding.add(b);
		updateXY(b, 1);
	}

	@Override
	public String toString() {
		Building b = null;
		if (allBuilding.size() > 0)
			b = allBuilding.get(0);
		return "FireZone(ID=" + index + ", Building=" + b + ", IsDisable=" + isDisable() + ", IsEx=" + isExtinguishable + ")";
	}

	public SiteSize getSize() {
		if (getOuter().size() < SMALL_SIZE_BUILDING_COUNT)
			size = SiteSize.Small;
		else
			size = SiteSize.large;
		return size;
	}

	public void updateXY(Building building, int coef) {
		centerX = ((centerX * (allBuilding.size() - coef)) + coef * building.getX()) / (allBuilding.size() != 0 ? allBuilding.size() : 1);
		centerY = ((centerY * (allBuilding.size() - coef)) + coef * building.getY()) / (allBuilding.size() != 0 ? allBuilding.size() : 1);
	}

	@Override
	public boolean equals(Object f) {
		if (f == null)
			return false;
		if (!(f instanceof SOSAbstractFireZone))
			return false;
		return this.getIndex() == ((SOSAbstractFireZone) f).getIndex();
	}

	public boolean isDisable() {
		return isDisable;
	}

	public void setDisable(boolean isDisable, int time,boolean sendMessage) {//TODO
		fireLog.info("Set disable " + isDisable);
		if (this.isDisable == isDisable)
			return;
		this.isDisable = isDisable;
		if (isDisable) {//TODO message ro begiram
			disabledTime = time;
			if (this instanceof SOSEstimatedFireZone&&sendMessage&&!allBuilding.isEmpty()) {
				try{
				fireLog.error(new Error("XDISABLE=="+this),false);
				manager.me.messageBlock = new MessageBlock(MessageXmlConstant.HEADER_DISABLE_FIRE_SITE);
				manager.me.messageBlock.addData(MessageXmlConstant.DATA_BUILDING_INDEX, this.allBuilding.get(0).getBuildingIndex());
				manager.me.messages.add(manager.me.messageBlock);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} else
			disabledTime = -1;
	}

	public ArrayList<Building> getDangerBuildingForIgnit() {
		return dangerBuilding;
	}

	public abstract void setDangerBuilding();

	public ArrayList<Building> getFires() {
		return burningBuildings;
	}

	public enum SiteSize {
		Small, large, Undefine
	};

	public ArrayList<Building> getAllBuildings() {
		return allBuilding;
	}

	public ConvexHull_arr_New getConvex() {
		if (convex == null)
			convex = new ConvexHull_arr_New(getAllBuildings(), true);
		return convex;
	}

	public void addSafeBuilding(Building b) {
		if (!safeBuilding.contains(b))
			safeBuilding.add(b);
	}

	public boolean isEstimating() {
		return !isDisable && isEstimating;
	}

	/**
	 * Fire zone ID
	 */
	public short getIndex() {
		return index;
	}

	/**
	 * Fire zone ID
	 */
	private void setIndex(short hashCode) {
		this.index = hashCode;

	}

	/**
	 * Array of buildings which are near the outer </br>
	 * Specially for Pre_Extinguish
	 */
	public ArrayList<Building> getSafeBuilding() {
		return safeBuilding;
	}

	/**
	 * The buildings which are outside of fire zone </br>
	 * Specially for Extinguish
	 */
	public ArrayList<Building> getOuter() {
		return outer;
	}

	public void setEstimating(boolean estimating) {
		this.isEstimating = estimating;
	}

	/**
	 * Center of fire zone
	 */
	public int getCenterX() {
		return centerX;
	}

	/**
	 * Center of fire zone
	 */
	public int getCenterY() {
		return centerY;
	}

	/**
	 * Center of fire zone
	 */
	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	/**
	 * Determine the fire zone extinguished </br>
	 * True if fire zone extinguished
	 */
	public boolean isExtinguishable() {
		return isExtinguishable;
	}

	public void setExtinguishable(boolean isExtinguishable) {
		fireLog.info("set extinguishable " + isExtinguishable);
		if (this.isExtinguishable && !isExtinguishable) {
			this.extinguishedTime = manager.me.model().time();
			setDangerBuilding();
		}
		if (isExtinguishable) {
			this.extinguishedTime = -1;
			dangerBuilding.clear();
		}
		this.isExtinguishable = isExtinguishable;

	}

	public ConvexHull_arr_New getOuterConvex() {
		if (outerConvex == null)
			outerConvex = new ConvexHull_arr_New(getOuter());
		return outerConvex;
	}

	public void setOuterConvex(ConvexHull_arr_New outerConvex) {//TODO
		this.outerConvex = outerConvex;
	}

	/**
	 * @return total ground area
	 */
	public int getGroundArea() {
		int temp = 0;
		for (Building b : getAllBuildings()) {
			temp += b.getGroundArea();
		}
		return temp;

	}

	public ConvexHull_arr_New getUsefullConvex() {//TODO
		if (allBuilding.size() == 1)
			return new ConvexHull_arr_New(Arrays.asList(allBuilding.get(0)), false);
		return getConvex();
	}

	/**
	 * indicates id the fire site is reported in noComm strategy.
	 * 
	 * @return
	 */
	public boolean isReported() {
		return reportTime != -1;
	}

	/**
	 * Indicates if the fire site should still be started.
	 * 
	 * @return
	 */
	public boolean shouldBeReported() {
		return (manager.model.time() - reportTime) < 30;
	}

	public int getReportTime() {
		return reportTime;
	}

	public void setReportTime(int time) {
		this.reportTime = time;
	}

}
