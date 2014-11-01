package sos.base.entities;

import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;

/**
 * The AmbulanceCentre object.
 */
public class AmbulanceCenter extends Center {
	/* ///////////////////S.O.S instants////////////////// */

	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**
	 * Construct a AmbulanceCentre object with entirely undefined values.
	 * 
	 * @param id The ID of this entity.
	 */
	public AmbulanceCenter(EntityID id) {
		super(id);
	}
	
	/**
	 * AmbulanceCentre copy constructor.
	 * 
	 * @param other The AmbulanceCentre to copy.
	 */
	public AmbulanceCenter(AmbulanceCenter other) {
		super(other);
	}
	
	/**
	 * Create an ambulance centre based on another Building.
	 * 
	 * @param other The Building to copy.
	 */
	public AmbulanceCenter(Building other) {
		super(other);
	}
	
	@Override
	protected Entity copyImpl() {
		return new AmbulanceCenter(getID());
	}
	
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.AMBULANCE_CENTRE;
	}
	
	@Override
	protected String getEntityName() {
		return "Ambulance Center";
	}
	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */

	/* ////////////////////End of S.O.S/////////////////// */

}
