package sos.base.util.information_stacker.act;

import sos.base.move.Path;

public class MoveAction extends AbstractAction {

	private final Path path;

	public MoveAction(Path path) {
		this.path = path;
	}
	
	public Path getPath() {
		return path;
	}
	@Override
	public String toString() {
		return super.toString()+":"+path.getDestination();
	}
}
