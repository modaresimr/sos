package sos.police_v2.base.worldModel;

import java.util.PriorityQueue;

import rescuecore2.geometry.Point2D;
import rescuecore2.worldmodel.EntityID;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Blockade;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.police_v2.PoliceForceAgent;
import sos.police_v2.PoliceUtils;

import com.infomatiq.jsi.IntProcedure;
import com.infomatiq.jsi.Rectangle;

public class PoliceWorldModel extends SOSWorldModel {
	public PoliceWorldModel(SOSAgent<? extends StandardEntity> sosAgent) {
		super(sosAgent);
	}

	@Override
	public void precompute() {
		super.precompute();
	}


	public Point2D getCeterOfMap() {
		int x = getWorldBounds().first().first() + getWorldBounds().second().first();
		int y = getWorldBounds().first().second() + getWorldBounds().second().second();
		return new Point2D(x, y);
	}

	public PriorityQueue<Blockade> getBlockadesInRange(int x, int y, int range) {
		if (!indexed) {
			index();
		}
		final PriorityQueue<Blockade> result = new PriorityQueue<Blockade>(20, new DistanceComparator());
		Rectangle r = new Rectangle(x - range, y - range, x + range, y + range);
		index.intersects(r, new IntProcedure() {
			@Override
			public boolean execute(int id) {
				StandardEntity e = getEntity(new EntityID(id));
				if (e != null && e instanceof Road && ((Road) e).isBlockadesDefined()) {
					for (Blockade blockade : ((Road) e).getBlockades()) {
						if (PoliceUtils.isValid(blockade))
							result.add(blockade);
					}
				}
				return true;
			}
		});
		return result;
	}


	private class DistanceComparator implements java.util.Comparator<Blockade> {
		@Override
		public int compare(Blockade c1, Blockade c2) {
			return (int) (PoliceUtils.getBlockadeDistance(c1) - PoliceUtils.getBlockadeDistance(c2));
		}
	}

	@Override
	public PoliceForceAgent sosAgent() {
		return (PoliceForceAgent) super.sosAgent();
	}

}
