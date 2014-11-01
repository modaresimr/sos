package sos.base.entities;

import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import sos.ambulance_v2.base.WorkingInfo;

/**
 * The AmbulanceTeam object.
 */
public class AmbulanceTeam extends Human {
	/* ///////////////////S.O.S instants////////////////// */
	protected short ambIndex; // aramik
	protected WorkingInfo work = null;// aramik
	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**
	 * Construct a AmbulanceTeam object with entirely undefined values.
	 *
	 * @param id The ID of this entity.
	 */
	public AmbulanceTeam(EntityID id) {
		super(id);
	}

	// Please don't add any method here!!!!!!
	/**
	 * AmbulanceTeam copy constructor.
	 *
	 * @param other The AmbulanceTeam to copy.
	 */
	public AmbulanceTeam(AmbulanceTeam other) {
		super(other);
	}

	// Please don't add any method here!!!!!!
	@Override
	protected Entity copyImpl() {
		return new AmbulanceTeam(getID());
	}

	// Please don't add any method here!!!!!!
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.AMBULANCE_TEAM;
	}

	// Please don't add any method here!!!!!!
	@Override
	protected String getEntityName() {
		return "Ambulance team";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "AmbulanceTeam[" + getID().getValue() + "]";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
		return "AmbulanceTeam[" + getID().getValue() + "] ,hp=" + (isHPDefined() ? getHP() : "-") +
				" , dmg=" + (isDamageDefined() ? getDamage() : "-") + " , buried=" + (isBuriednessDefined() ? getBuriedness() : "-") +
				" , stamina=" + (isStaminaDefined() ? getStamina() : "-") + " , dir=" + (isDirectionDefined() ? getDirection() : "-") +
				" , trv_distance" + (isTravelDistanceDefined() ? getTravelDistance() : "-") +
				" , position=" + (isPositionDefined() ? getPositionID().getValue() : "-");
	}
	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public short getAmbIndex() {
		return ambIndex;
	}
	public void setAmbIndex(short ambIndex) {
		this.ambIndex = ambIndex;
	}

	public WorkingInfo getWork() {
		if (this.work == null)
			this.work = new WorkingInfo(this);
		return work;
	}

	@Override
	public int getMessageWeightForSending() {
		if(model().time()>0)
			getAgent().sosLogger.error(new Error("MessageWeightForSending can be use only in precompute"));
		for (int i = 0; i <= getAmbIndex(); i++) {
			AmbulanceTeam amb = standardModel().ambulanceTeams().get(i);
			if (amb.isPositionDefined()	&& !(amb.getPositionArea() instanceof Building)) {
				if (i == getAmbIndex())
					return 3;
				break;
			}
		}
		//TODO FIXME XXX if hame too building boodan chi kar kone?????
		return super.getMessageWeightForSending();
	}
	/* ////////////////////End of S.O.S/////////////////// */

}