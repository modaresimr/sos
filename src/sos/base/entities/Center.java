package sos.base.entities;

import rescuecore2.worldmodel.EntityID;

/**
 * The AmbulanceCentre object.
 */
public class Center extends Building {
	/* ///////////////////S.O.S instants////////////////// */
	protected short centerIndex;
	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	public Center(Building other) {
		super(other);
	}
	
	// Please don't add any method here!!!!!!
	public Center(EntityID id) {
		super(id);
	}
	
	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "Center[" + getID().getValue() + "]";
	}
	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public void setCenterIndex(short centerIndex) {
		this.centerIndex = centerIndex;
	}
	public short getCenterIndex() {
		return centerIndex;
	}

	/* ////////////////////End of S.O.S/////////////////// */

}
