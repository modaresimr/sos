package sos.base.entities;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.Property;

/**
 * The Civilian object.
 */
public class Civilian extends Human {
	/* ///////////////////S.O.S instants////////////////// */
	private EntityID firstPosition; //Addes By Salim
	private int foundTime;//Addes By Salim

	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	private static final int CIVILIAN_RADIUS = 200;

	/**
	 * Construct a Civilian object with entirely undefined values.
	 *
	 * @param id
	 *            The ID of this entity.
	 */
	public Civilian(EntityID id) {
		super(id);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Civilian copy constructor.
	 *
	 * @param other
	 *            The Civilian to copy.
	 */
	public Civilian(Civilian other) {
		super(other);
	}

	// Please don't add any method here!!!!!!
	@Override
	protected Entity copyImpl() {
		return new Civilian(getID());
	}

	// Please don't add any method here!!!!!!
	@Override
	public StandardEntityURN getStandardURN() {
		return StandardEntityURN.CIVILIAN;
	}

	// Please don't add any method here!!!!!!
	@Override
	protected String getEntityName() {
		return "Civilian";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
		if (!isUnkonwnCivilian()) {
			return "Civilian[" + getID() + "] ,hp=" + (isHPDefined() ? getHP() : "-") + " , dmg=" + (isDamageDefined() ? getDamage() : "-") + " , buried=" + (isBuriednessDefined() ? getBuriedness() : "-") + " , stamina=" + (isStaminaDefined() ? getStamina() : "-") + " , dir=" + (isDirectionDefined() ? getDirection() : "-") + " , trv_distance" + (isTravelDistanceDefined() ? getTravelDistance() : "-") + " , position=" + (isPositionDefined() ? getPositionID().getValue() : "-");
		} else {
			return "UnknownCivilian[" + getID() + "] ,hp=" + (isHPDefined() ? getHP() : "-") + " , dmg=" + (isDamageDefined() ? getDamage() : "-") + " , buried=" + (isBuriednessDefined() ? getBuriedness() : "-") + " , stamina=" + (isStaminaDefined() ? getStamina() : "-") + " , dir=" + (isDirectionDefined() ? getDirection() : "-") + " , trv_distance" + (isTravelDistanceDefined() ? getTravelDistance() : "-") + " , position=" + (isPositionDefined() ? getPositionID().getValue() : "-");
		}
	}

	// Please don't add any method here!!!!!!
	public boolean isUnkonwnCivilian() {
		return !isPositionDefined();
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		if (isUnkonwnCivilian())
			return "UnknownCivilian[" + getID() + "]";
		return "Civilian[" + getID() + "]";
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	@Override
	public Shape getShape() {
		Shape shape = new Ellipse2D.Double(getX() - CIVILIAN_RADIUS, getY() - CIVILIAN_RADIUS, CIVILIAN_RADIUS * 2, CIVILIAN_RADIUS * 2);
		return shape;
	}

	/* ////////////////////End of S.O.S/////////////////// */
	@Override
	protected void firePropertyChanged(Property p, Object oldValue, Object newValue) {//Salim
		super.firePropertyChanged(p, oldValue, newValue);
		if (p.getURN().equals(position.getURN()))
			if (getFirstPosition() == null) {
				setFirstPosition((EntityID) p.getValue());
				if (standardModel() != null)
					foundTime = standardModel().time();
			}
	}

	public EntityID getFirstPosition() {
		return firstPosition;
	}

	public void setFirstPosition(EntityID firstPosition) {
		this.firstPosition = firstPosition;
	}

	public int getFoundTime() {
		return foundTime;
	}

}