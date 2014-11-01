package sos.base.util.information_stacker.act;

import sos.base.entities.Blockade;

public class ClearAction extends AbstractAction {

	private final Blockade blockade;

	public ClearAction(Blockade blockade) {
		this.blockade = blockade;
	}

	public Blockade getBlockade() {
		return blockade;
	}
	@Override
	public String toString() {
		return super.toString()+":"+blockade;
	}
}
