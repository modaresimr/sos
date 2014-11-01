package sos.base.entities;

import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.entities.StandardPropertyURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.Property;
import rescuecore2.worldmodel.properties.IntProperty;
import sos.base.sosFireZone.SOSAbstractFireZone;
import sos.tools.Utils;

/**
 * The FireBrigade object.
 */
public class FireBrigade extends Human {
	/* ///////////////////S.O.S instants////////////////// */
	protected short fireIndex; // aramik
	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	/**/private IntProperty water;

	// Please don't add any method here!!!!!!
	/**
	 * Construct a FireBrigade object with entirely undefined values.
	 *
	 * @param id
	 *            The ID of this entity.
	 */
	public FireBrigade(EntityID id) {
		super(id);
		water = new IntProperty(StandardPropertyURN.WATER_QUANTITY);
		registerProperties(water);
	}

	// Please don't add any method here!!!!!!
	/**
	 * FireBrigade copy constructor.
	 *
	 * @param other
	 *            The FireBrigade to copy.
	 */
	public FireBrigade(FireBrigade other) {
		super(other);
		water = new IntProperty(other.water);
		registerProperties(water);
	}

	// Please don't add any method here!!!!!!
	@Override
	protected Entity copyImpl() {
		return new FireBrigade(getID());
	}

	// Please don't add any method here!!!!!!
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.FIRE_BRIGADE;
	}

	// Please don't add any method here!!!!!!
	@Override
	public Property getProperty(String urn) {
		StandardPropertyURN type;
		try {
			type = StandardPropertyURN.fromString(urn);
		} catch (IllegalArgumentException e) {
			return super.getProperty(urn);
		}
		switch (type) {
		case WATER_QUANTITY:
			return water;
		default:
			return super.getProperty(urn);
		}
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the water property.
	 *
	 * @return The water property.
	 */
	public IntProperty getWaterProperty() {
		return water;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the amount of water this fire brigade is carrying.
	 *
	 * @return The water.
	 */
	public int getWater() {
		return water.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the amount of water this fire brigade is carrying.
	 *
	 * @param water
	 *            The new amount of water.
	 */
	public void setWater(int water) {
		this.water.setValue(water);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the water property has been defined.
	 *
	 * @return True if the water property has been defined, false otherwise.
	 */
	public boolean isWaterDefined() {
		return water.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefined the water property.
	 */
	public void undefineWater() {
		water.undefine();
	}

	// Please don't add any method here!!!!!!
	@Override
	protected String getEntityName() {
		return "Fire brigade";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "FireBrigade[" + getID().getValue() + "]";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
		return "FireBrigade[" + getID().getValue() + "] ,water=" + (isWaterDefined() ? getWater() : "-") + " ,hp=" + (isHPDefined() ? getHP() : "-") + " , dmg=" + (isDamageDefined() ? getDamage() : "-") + " , buried=" + (isBuriednessDefined() ? getBuriedness() : "-") + " , stamina=" + (isStaminaDefined() ? getStamina() : "-") + " , dir=" + (isDirectionDefined() ? getDirection() : "-") + " , trv_distance" + (isTravelDistanceDefined() ? getTravelDistance() : "-") + " , position=" + (isPositionDefined() ? getPositionID().getValue() : "-");
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public short getFireIndex() {
		return fireIndex;
	}

	public void setFireIndex(short fireIndex) {
		this.fireIndex = fireIndex;
	}

	private int timeForLastPosition;
	private Area lastPosition;

	public void setLastPosition() {
		if (lastPosition != null && lastPosition.equals(getPositionArea())) {
			timeForLastPosition++;
		} else {
			lastPosition = getPositionArea();
		}
	}

	public int getTimeForLastPositioning() {
		return timeForLastPosition;
	}

	/* ////////////////////End of S.O.S/////////////////// */


	public int distance(SOSAbstractFireZone o1) {
		int near = Integer.MAX_VALUE;
		try{
		if(o1.getOuter().isEmpty()){
			for (Building b : o1.getAllBuildings()) {
				if (near > b.distance(this)) {
					near = b.distance(this);
				}
			}
			return near;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		for (Building b : o1.getOuter()) {
			if (near > b.distance(this)) {
				near = b.distance(this);
			}
		}
		return near;
	}

	public int distance(Area h) {
		return (int) Utils.distance(this.getX(), this.getY(), h.getX(), h.getY());
	}

}