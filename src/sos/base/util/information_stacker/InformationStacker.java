package sos.base.util.information_stacker;
 
 import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.move.Path;
import sos.base.util.information_stacker.act.AbstractAction;
import sos.base.util.information_stacker.act.MoveAction;

public class InformationStacker {
	ArrayList<CycleInformations> infos;
//	private int last = -1;
	
	public InformationStacker(int size) {
		infos = new ArrayList<CycleInformations>(size);
	}
	
	/**
	 * @param timeAgo can't be 0
	 * @return
	 */
	public CycleInformations getInformations(int timeAgo) {
	
		if (timeAgo > infos.size() || timeAgo==0)
			return null;
//		int pos = (last + (infos.length - timeAgo) + 1) % infos.length;
//		return infos[pos];
		return infos.get(infos.size()-timeAgo);
	}
	
	
	public void addInfo(int time,AbstractAction action,Pair<? extends Area, Point2D> position) {
		addInfo(new CycleInformations(time, action, position));
	}
	public void addInfo(CycleInformations info) {
//		int pos = (++last) % infos.length;
//		infos[pos] = info;
		infos.add(info);
	}

	public Path getLastMovePath() {
		for (int i = 1; i <= infos.size(); i++) {
			CycleInformations ci = getInformations(i);//TODO BUG
			if (ci.getAct() instanceof MoveAction) {
				return ((MoveAction) ci.getAct()).getPath();
			}
		}
		return null;
	}

	public void addInfo(SOSWorldModel model, AbstractAction act) {
		addInfo(model.time(), act, model.me().getPositionPair());
	}

	public boolean hasInformation(int timeAgo) {
		return !(timeAgo > infos.size() || timeAgo==0);
	}


}
