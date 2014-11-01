package sos.base.util.information_stacker.act;

import sos.base.entities.Building;

public class ExtinguishAct extends AbstractAction {


	private final Building target;
	private final int water;

	public ExtinguishAct(Building target,int water) {
		this.target = target;
		this.water = water;
	}
	
	public Building getTarget() {
		return target;
	}
	public int getWater() {
		return water;
	}
	@Override
	public String toString() {
		return super.toString()+":"+target;
	}
}
