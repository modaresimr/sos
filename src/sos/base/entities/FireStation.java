package sos.base.entities;

import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;

/**
 * The FireStation object.
 */
public class FireStation extends Center {
	/* ///////////////////S.O.S instants////////////////// */

	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**
	 * Construct a FireStation object with entirely undefined values.
	 * 
	 * @param id The ID of this entity.
	 */
	public FireStation(EntityID id) {
		super(id);
	}
	
	/**
	 * FireStation copy constructor.
	 * 
	 * @param other The FireStation to copy.
	 */
	public FireStation(FireStation other) {
		super(other);
	}
	
	/**
	 * Create a fire station based on another Building.
	 * 
	 * @param other The Building to copy.
	 */
	public FireStation(Building other) {
		super(other);
	}
	
	@Override
	protected Entity copyImpl() {
		return new FireStation(getID());
	}
	
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.FIRE_STATION;
	}
	
	@Override
	protected String getEntityName() {
		return "Fire station";
	}
	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */

	/* ////////////////////End of S.O.S/////////////////// */

}
