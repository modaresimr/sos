package sos.base.util.namayangar.standard.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import rescuecore2.config.Config;
import rescuecore2.geometry.Point2D;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.Human;
import sos.base.util.namayangar.standard.misc.AgentPath;

/**
 * A view layer that animates human movements.
 */
public class AnimatedHumanLayer extends HumanLayer {
	private Set<EntityID> humanIDs;
	
	private Map<EntityID, Queue<Point2D>> frames;
	private boolean animationDone;
	
	/**
	 * Construct an animated human view layer.
	 */
	public AnimatedHumanLayer() {
		humanIDs = new HashSet<EntityID>();
		frames = new HashMap<EntityID, Queue<Point2D>>();
		animationDone = true;
	}
	
	@Override
	public void initialise(Config config) {
		super.initialise(config);
		humanIDs.clear();
		synchronized (this) {
			frames.clear();
			animationDone = true;
		}
	}
	
	@Override
	public String getName() {
		return "Humans (animated)";
	}
	
	/**
	 * Increase the frame number.
	 * 
	 * @return True if a new frame is actually required.
	 */
	public boolean nextFrame() {
		synchronized (this) {
			if (animationDone) {
				return false;
			}
			animationDone = true;
			for (Queue<Point2D> next : frames.values()) {
				if (next.size() > 0) {
					next.remove();
					animationDone = false;
				}
			}
			return !animationDone;
		}
	}
	
	@Override
	protected Point2D getLocation(Human h) {
		synchronized (this) {
			Queue<Point2D> agentFrames = frames.get(h.getID());
			if (agentFrames != null && !agentFrames.isEmpty()) {
				return agentFrames.peek();
			}
		}
		return h.getPositionPoint();
	}
	
	@Override
	protected void preView() {
		super.preView();
		humanIDs.clear();
	}
	
	@Override
	protected void viewObject(Object o) {
		super.viewObject(o);
		if (o instanceof Human) {
			humanIDs.add(((Human) o).getID());
		}
	}
	
	/**
	 * Compute the animation frames.
	 * 
	 * @param frameCount The number of animation frames to compute.
	 */
	public void computeAnimation(int frameCount) {
		synchronized (this) {
			frames.clear();
			// Compute animation
			double step = 1.0 / (frameCount - 1.0);
			for (EntityID next : humanIDs) {
				Queue<Point2D> result = new LinkedList<Point2D>();
				Human human = (Human) world.getEntity(next);
				if (human == null) {
					continue;
				}
				AgentPath path = null;
				try {
					path = AgentPath.computePath(human, world);
				} catch (Exception e) {
				}
				if (path == null) {
					continue;
				}
				for (int i = 0; i < frameCount; ++i) {
					Point2D nextPoint = path.getPointOnPath(i * step);
					result.add(nextPoint);
				}
				frames.put(next, result);
			}
			animationDone = false;
		}
	}
}
