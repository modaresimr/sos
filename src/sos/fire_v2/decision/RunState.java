package sos.fire_v2.decision;

public enum RunState {
		state0(0),
		state1(1),
		state2(2),
		state3(3),
		center(4),
		state5(5),
		state6(6),
		state7(7),
		state8(8),
		NONE(10);
		private int stateNumber;

		private RunState(int stateNumber) {
			this.stateNumber = stateNumber;
		}
		public int getStateNumber() {
			return stateNumber;
		}
	}

