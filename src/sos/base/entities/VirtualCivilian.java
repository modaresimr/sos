package sos.base.entities;

public class VirtualCivilian {

	private final Area position;
	private final int burridness;
	private final int deathTime;
	private boolean isReallyReachable;


	private boolean isIgnored = false ;
	private int ATneedToBeRescued = 1;
	private int timeToRefuge = 1;
	private int rescuePriority = 0;
	private boolean isAgent = false;
	private int agentType = 0; // 0 == AT, 1==Police, 2==Fire

	public VirtualCivilian(Area position, int burridness, int deathTime, boolean isReallyReachable) {
		this.position = position;
		this.burridness = burridness;
		this.deathTime = deathTime;
		this.isReallyReachable = isReallyReachable;
	}

	public Area getPosition() {
		return position;
	}

	public int getBuridness() {
		return burridness;
	}

	public int getDeathTime() {
		return deathTime;
	}

	public boolean isReallyReachable() {
		return isReallyReachable;
	}

	@Override
	public String toString() {
		return "[VirtualCivilian("+position+") b:"+burridness+", dt:"+deathTime+" r:"+isReallyReachable+"]";
	}

	public boolean isPositionDefined() {
		if(position.isXDefined() && position.isYDefined())
			return true;
		return false;
	}

	public int getATneedToBeRescued() {
		return ATneedToBeRescued;
	}

	public void setATneedToBeRescued(int aTneedToBeRescued) {
		ATneedToBeRescued = aTneedToBeRescued;
	}

	public int getTimeToRefuge() {
		return timeToRefuge;
	}

	public void setTimeToRefuge(int timeToRefuge) {
		this.timeToRefuge = timeToRefuge;
	}

	public int getRescuePriority() {
		return rescuePriority;
	}

	public void setRescuePriority(int rescuePriority) {
		this.rescuePriority = rescuePriority;
	}

	public boolean isAgent() {
		return isAgent;
	}

	public void setAgent(boolean isAgent) {
		this.isAgent = isAgent;
	}

	public int getAgentType() {
		return agentType;
	}

	public void setAgentType(int i) {
		this.agentType = i;
	}

	public boolean isIgnored() {
		return isIgnored;
	}

	public void setIgnored(boolean isIgnored) {
		this.isIgnored = isIgnored;
	}

	public void setReallyReachable(boolean isReallyReachable) {
		this.isReallyReachable = isReallyReachable;
	}


}
