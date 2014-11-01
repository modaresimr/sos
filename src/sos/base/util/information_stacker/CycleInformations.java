package sos.base.util.information_stacker;

 import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.entities.Area;
import sos.base.util.information_stacker.act.AbstractAction;

public class CycleInformations {
	private final int time;
	private final AbstractAction act;
	private final Pair<? extends Area, Point2D> positionPair;
	
	public CycleInformations(int time,  AbstractAction act, Pair<? extends Area, Point2D> positionPair) {
		this.time = time;
		this.act = act;
		this.positionPair = positionPair;
	}
	
	public int time() {
		return time;
	}
	
	public Pair<? extends Area, Point2D> getPositionPair() {
		return positionPair;
	}
	
	public AbstractAction getAct() {
		return act;
	}
	@Override
	public String toString() {
		return "time:"+time+", act:"+act+", pos:"+positionPair;
	}
}
