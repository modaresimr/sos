package sos.base.util.information_stacker.act;

import sos.base.move.Path;

public class StockMoveAction extends MoveAction {

	public StockMoveAction(Path path) {
		super(path);
	}
	@Override
	public String toString() {
		return super.toString()+":"+getPath();
	}
}
