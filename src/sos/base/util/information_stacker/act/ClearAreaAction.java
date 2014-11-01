package sos.base.util.information_stacker.act;

import java.awt.Point;

public class ClearAreaAction extends AbstractAction {

	private final Point target;

	public ClearAreaAction(Point target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return super.toString() + ": " + target.x + "_" + target.y;
	}

	public Point getTarget() {
		return target;
	}
}
