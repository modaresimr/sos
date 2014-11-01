package sos.police_v2.state;

import java.awt.Point;
import java.util.ArrayList;
import java.util.PriorityQueue;

import sos.base.entities.Area;
import sos.base.entities.Blockade;
import sos.base.util.SOSActionException;
import sos.police_v2.PoliceForceAgent;

public class DummyPoliceState extends PoliceAbstractState {

	public DummyPoliceState(PoliceForceAgent policeForceAgent) {
		super(policeForceAgent);
	}
	@Override
	public void precompute() {
		
	}

	@Override
	public void act() throws SOSActionException {
		log.info("acting as:"+this.getClass().getSimpleName());
		log.info("LOG IS NOT COMPLETED");
		
		PriorityQueue<Blockade> blockadesInRange = model().getBlockadesInRange(agent.me().getX(), agent.me().getY(),agent.clearDistance);
		Blockade selectedBlock=null;
		if(blockadesInRange.size()>0)
		selectedBlock = blockadesInRange.remove();
		if(selectedBlock!=null)
			clear(new Point(selectedBlock.getX(), selectedBlock.getY()));
		ArrayList<Area> dest=new ArrayList<Area>();
		for (Blockade blockade : model().blockades()) {
			dest.add(blockade.getAreaPosition());
		}
		if(dest.size()>0)
		move(dest);
	}

}
