package sos.base.entities;

import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import sos.tools.Utils;

/**
 * The PoliceForce object.
 */
public class PoliceForce extends Human {
	/* ///////////////////S.O.S instants////////////////// */
	protected short policeIndex; // aramik

	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**
	 * Construct a PoliceForce object with entirely undefined values.
	 *
	 * @param id The ID of this entity.
	 */
	public PoliceForce(EntityID id) {
		super(id);
	}

	// Please don't add any method here!!!!!!
	/**
	 * PoliceForce copy constructor.
	 *
	 * @param other The PoliceForce to copy.
	 */
	public PoliceForce(PoliceForce other) {
		super(other);
	}

	// Please don't add any method here!!!!!!
	@Override
	protected Entity copyImpl() {
		return new PoliceForce(getID());
	}

	// Please don't add any method here!!!!!!
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.POLICE_FORCE;
	}

	// Please don't add any method here!!!!!!
	@Override
	protected String getEntityName() {
		return "Police force";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "PoliceForce[" + getID().getValue() + "]";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
		return "PoliceForce[" + getID().getValue() + "] ,hp=" + (isHPDefined() ? getHP() : "-") + " , dmg=" + (isDamageDefined() ? getDamage() : "-") + " , buried=" + (isBuriednessDefined() ? getBuriedness() : "-") + " , stamina=" + (isStaminaDefined() ? getStamina() : "-") + " , dir=" + (isDirectionDefined() ? getDirection() : "-") + " , trv_distance" + (isTravelDistanceDefined() ? getTravelDistance() : "-") + " , position=" + (isPositionDefined() ? getPositionID().getValue() : "-");
	}
	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public short getPoliceIndex() {
		return policeIndex;
	}

	public void setPoliceIndex(short policeIndex) {
		this.policeIndex = policeIndex;
	}

	public int distance(Area road) {
		return (int) Utils.distance(getX(), getY(), road.getX(),road.getY());
	}
	/* ////////////////////End of S.O.S/////////////////// */

}