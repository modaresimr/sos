package sos.base.util.information_stacker.act;

public class ProblemRestAction extends AbstractAction {

	private final String problem;

	public ProblemRestAction(String problem) {
		this.problem = problem;
		
	}
	public String getProblem() {
		return problem;
	}

	@Override
	public String toString() {
		return super.toString()+":"+problem;
	}
}
