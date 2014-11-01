package sos.base.util.information_stacker.act;

import sos.base.entities.Human;

public class RescueAction extends AbstractAction {

	private final Human human;

	public RescueAction(Human human) {
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
