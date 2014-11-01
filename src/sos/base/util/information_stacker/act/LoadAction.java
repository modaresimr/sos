package sos.base.util.information_stacker.act;

import sos.base.entities.Human;

public class LoadAction extends AbstractAction {

	private final Human human;

	public LoadAction(Human human) {
		this.human = human;
	}
	public Human getHuman() {
		return human;
	}
	@Override
	public String toString() {
		return super.toString()+":"+human;
	}
}
