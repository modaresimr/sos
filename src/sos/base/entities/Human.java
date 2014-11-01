package sos.base.entities;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import rescuecore2.geometry.Point2D;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardPropertyURN;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.Property;
import rescuecore2.worldmodel.properties.EntityRefProperty;
import rescuecore2.worldmodel.properties.IntArrayProperty;
import rescuecore2.worldmodel.properties.IntProperty;
import sos.ambulance_v2.base.RescueInfo;

/**
 * Abstract base class for Humans.
 */
public abstract class Human extends StandardEntity {
	/* ///////////////////S.O.S instants////////////////// */
	protected short agent_Index = -1;
	protected RescueInfo rescueInfo = null; // aramik
	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
	private static final int RESCUE_AGENT_RADIUS = 500;

	/**/private IntProperty x;
	/**/private IntProperty y;
	/**/protected EntityRefProperty position;
	/**/private IntArrayProperty positionHistory;
	/**/private IntProperty travelDistance;
	/**/private IntProperty direction;
	/**/private IntProperty stamina;
	/**/private IntProperty hp;
	/**/private IntProperty damage;
	/**/private IntProperty buriedness;

	// Please don't add any method here!!!!!!
	/**
	 * Construct a Human object with entirely undefined property values.
	 *
	 * @param id
	 *            The ID of this entity.
	 */
	protected Human(EntityID id) {
		super(id);
		x = new IntProperty(StandardPropertyURN.X);
		y = new IntProperty(StandardPropertyURN.Y);
		travelDistance = new IntProperty(StandardPropertyURN.TRAVEL_DISTANCE);
		position = new EntityRefProperty(StandardPropertyURN.POSITION);
		positionHistory = new IntArrayProperty(StandardPropertyURN.POSITION_HISTORY);
		direction = new IntProperty(StandardPropertyURN.DIRECTION);
		stamina = new IntProperty(StandardPropertyURN.STAMINA);
		hp = new IntProperty(StandardPropertyURN.HP);
		damage = new IntProperty(StandardPropertyURN.DAMAGE);
		buriedness = new IntProperty(StandardPropertyURN.BURIEDNESS);
		registerProperties(x, y, position, positionHistory, travelDistance, direction, stamina, hp, damage, buriedness);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Human copy constructor.
	 *
	 * @param other
	 *            The Human to copy.
	 */
	public Human(Human other) {
		super(other);
		x = new IntProperty(other.x);
		y = new IntProperty(other.y);
		travelDistance = new IntProperty(other.travelDistance);
		position = new EntityRefProperty(other.position);
		positionHistory = new IntArrayProperty(other.positionHistory);
		direction = new IntProperty(other.direction);
		stamina = new IntProperty(other.stamina);
		hp = new IntProperty(other.hp);
		damage = new IntProperty(other.damage);
		buriedness = new IntProperty(other.buriedness);
		registerProperties(x, y, position, positionHistory, travelDistance, direction, stamina, hp, damage, buriedness);
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
		case POSITION:
			return position;
		case POSITION_HISTORY:
			return positionHistory;
		case DIRECTION:
			return direction;
		case STAMINA:
			return stamina;
		case HP:
			return hp;
		case X:
			return x;
		case Y:
			return y;
		case DAMAGE:
			return damage;
		case BURIEDNESS:
			return buriedness;
		case TRAVEL_DISTANCE:
			return travelDistance;
		default:
			return super.getProperty(urn);
		}
	}

	// Please don't add any method here!!!!!!
	@Override
	@Deprecated
	//me().getPositionPoint();
	public Pair<Integer, Integer> getLocation() {
		if (x.isDefined() && y.isDefined()) {
			return new Pair<Integer, Integer>(x.getValue(), y.getValue());
		}
		if (position.isDefined()) {
			StandardEntity e = getPosition();
			return e.getLocation();
		}
		return null;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the position property.
	 *
	 * @return The position property.
	 */
	public EntityRefProperty getPositionProperty() {
		return position;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the entity represented by the position property. The result will be
	 * null if the position property has not been set or if the entity reference
	 * is invalid.
	 *
	 * @param model
	 *            The WorldModel to look up entity references.
	 * @return The entity represented by the position property.
	 */
	public StandardEntity getPosition() {
		if (!position.isDefined()) {
			return null;
		}
		return standardModel().getEntity(position.getValue());
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the position of this human.
	 *
	 * @return The position.
	 */
	public EntityID getPositionID() {
		return position.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the position of this human.
	 *
	 * @param position
	 *            The new position.
	 */
	public void setPosition(EntityID position) {
		this.position.setValue(position);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the position property has been defined.
	 *
	 * @return True if the position property has been defined, false otherwise.
	 */
	public boolean isPositionDefined() {
		return position.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the position property.
	 */
	public void undefinePosition() {
		position.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the position history property.
	 *
	 * @return The position history property.
	 */
	public IntArrayProperty getPositionHistoryProperty() {
		return positionHistory;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the position history.
	 *
	 * @return The position history.
	 */
	public int[] getPositionHistory() {
		return positionHistory.getValue();
	}

	public int getLastCycleTraveledDistance() {
		int dist = 0;
		for (int i = 2; i < getPositionHistory().length; i += 2) {
			dist += Math.hypot(getPositionHistory()[i] - getPositionHistory()[i - 2], getPositionHistory()[i + 1] - getPositionHistory()[i - 1]);
		}
		return dist;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the position history.
	 *
	 * @param history
	 *            The new position history.
	 */
	public void setPositionHistory(int[] history) {
		this.positionHistory.setValue(history);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the position history property has been defined.
	 *
	 * @return True if the position history property has been defined, false
	 *         otherwise.
	 */
	public boolean isPositionHistoryDefined() {
		return positionHistory.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the position history property.
	 */
	public void undefinePositionHistory() {
		positionHistory.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the direction property.
	 *
	 * @return The direction property.
	 */
	public IntProperty getDirectionProperty() {
		return direction;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the direction.
	 *
	 * @return The direction.
	 */
	public int getDirection() {
		return direction.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the direction.
	 *
	 * @param direction
	 *            The new direction.
	 */
	public void setDirection(int direction) {
		this.direction.setValue(direction);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the direction property has been defined.
	 *
	 * @return True if the direction property has been defined, false otherwise.
	 */
	public boolean isDirectionDefined() {
		return direction.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the direction property.
	 */
	public void undefineDirection() {
		direction.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the stamina property.
	 *
	 * @return The stamina property.
	 */
	public IntProperty getStaminaProperty() {
		return stamina;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the stamina of this human.
	 *
	 * @return The stamina.
	 */
	public int getStamina() {
		return stamina.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the stamina of this human.
	 *
	 * @param stamina
	 *            The new stamina.
	 */
	public void setStamina(int stamina) {
		this.stamina.setValue(stamina);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the stamina property has been defined.
	 *
	 * @return True if the stamina property has been defined, false otherwise.
	 */
	public boolean isStaminaDefined() {
		return stamina.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the stamina property.
	 */
	public void undefineStamina() {
		stamina.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the hp property.
	 *
	 * @return The hp property.
	 */
	public IntProperty getHPProperty() {
		return hp;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the hp of this human.
	 *
	 * @return The hp of this human.
	 */
	public int getHP() {
		return hp.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the hp of this human.
	 *
	 * @param newHP
	 *            The new hp.
	 */
	public void setHP(int newHP) {
		this.hp.setValue(newHP);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the hp property has been defined.
	 *
	 * @return True if the hp property has been defined, false otherwise.
	 */
	public boolean isHPDefined() {
		return hp.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the hp property.
	 */
	public void undefineHP() {
		hp.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the damage property.
	 *
	 * @return The damage property.
	 */
	public IntProperty getDamageProperty() {
		return damage;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the damage of this human.
	 *
	 * @return The damage of this human.
	 */
	public int getDamage() {
		return damage.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the damage of this human.
	 *
	 * @param damage
	 *            The new damage.
	 */
	public void setDamage(int damage) {
		this.damage.setValue(damage);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the damage property has been defined.
	 *
	 * @return True if the damage property has been defined, false otherwise.
	 */
	public boolean isDamageDefined() {
		return damage.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the damage property.
	 */
	public void undefineDamage() {
		damage.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the buriedness property.
	 *
	 * @return The buriedness property.
	 */
	public IntProperty getBuriednessProperty() {
		return buriedness;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the buriedness of this human.
	 *
	 * @return The buriedness of this human.
	 */
	public int getBuriedness() {
		return buriedness.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the buriedness of this human.
	 *
	 * @param buriedness
	 *            The new buriedness.
	 */
	public void setBuriedness(int buriedness) {
		this.buriedness.setValue(buriedness);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the buriedness property has been defined.
	 *
	 * @return True if the buriedness property has been defined, false
	 *         otherwise.
	 */
	public boolean isBuriednessDefined() {
		return buriedness.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the buriedness property.
	 */
	public void undefineBuriedness() {
		buriedness.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the X property.
	 *
	 * @return The X property.
	 */
	public IntProperty getXProperty() {
		return x;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the X coordinate of this human.
	 *
	 * @return The x coordinate of this human.
	 */
	public int getX() {
		return x.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the X coordinate of this human.
	 *
	 * @param x
	 *            The new x coordinate.
	 */
	public void setX(int x) {
		this.x.setValue(x);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the x property has been defined.
	 *
	 * @return True if the x property has been defined, false otherwise.
	 */
	public boolean isXDefined() {
		return x.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the X property.
	 */
	public void undefineX() {
		x.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the y property.
	 *
	 * @return The y property.
	 */
	public IntProperty getYProperty() {
		return y;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the y coordinate of this human.
	 *
	 * @return The y coordinate of this human.
	 */
	public int getY() {
		return y.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the y coordinate of this human.
	 *
	 * @param y
	 *            The new y coordinate.
	 */
	public void setY(int y) {
		this.y.setValue(y);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the y property has been defined.
	 *
	 * @return True if the y property has been defined, false otherwise.
	 */
	public boolean isYDefined() {
		return y.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the y property.
	 */
	public void undefineY() {
		y.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the travel distance property.
	 *
	 * @return The travel distance property.
	 */
	public IntProperty getTravelDistanceProperty() {
		return travelDistance;
	}

	// Please don't add any method here!!!!!!
	/**
	 * Get the travel distance.
	 *
	 * @return The travel distance.
	 */
	public int getTravelDistance() {
		return travelDistance.getValue();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the travel distance.
	 *
	 * @param d
	 *            The new travel distance.
	 */
	public void setTravelDistance(int d) {
		this.travelDistance.setValue(d);
	}

	// Please don't add any method here!!!!!!
	/**
	 * Find out if the travel distance property has been defined.
	 *
	 * @return True if the travel distance property has been defined, false
	 *         otherwise.
	 */
	public boolean isTravelDistanceDefined() {
		return travelDistance.isDefined();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Undefine the travel distance property.
	 */
	public void undefineTravelDistance() {
		travelDistance.undefine();
	}

	// Please don't add any method here!!!!!!
	/**
	 * Set the position of this human.
	 *
	 * @param newPosition
	 *            The new position.
	 * @param newX
	 *            The x coordinate of this agent.
	 * @param newY
	 *            The y coordinate if this agent.
	 */
	public void setPosition(EntityID newPosition, int newX, int newY) {
		this.position.setValue(newPosition);
		this.x.setValue(newX);
		this.y.setValue(newY);
	}

	// Please don't add any method here!!!!!!
	@Override
	public String toString() {
		return "Human[" + getID().getValue() + "]";
	}

	// Please don't add any method here!!!!!!
	@Override
	public String fullDescription() {
		return "Human[" + getID().getValue() + "] ,hp=" + (isHPDefined() ? getHP() : "-") + " , dmg=" + (isDamageDefined() ? getDamage() : "-") + " , buried=" + (isBuriednessDefined() ? getBuriedness() : "-") + " , stamina=" + (isStaminaDefined() ? getStamina() : "-") + " , dir=" + (isDirectionDefined() ? getDirection() : "-") + " , trv_distance" + (isTravelDistanceDefined() ? getTravelDistance() : "-") + " , position=" + (isPositionDefined() ? getPositionID().getValue() : "-");
	}

	/* //////////////////End of RESCUECORE//////////////// */
	/* ////////////////////S.O.S Methods////////////////// */
	public short getAgentIndex() {
		return agent_Index;
	}

	public void setAgentIndex(short overall_Agent_Index) {
		this.agent_Index = overall_Agent_Index;
	}

	public RescueInfo getRescueInfo() {// aramik
		if (this.rescueInfo == null)
			this.rescueInfo = new RescueInfo(this);
		return rescueInfo;
	}

	@Override
	public Point2D getPositionPoint() {// aramik
		if (!isXDefined() || !isYDefined())
			return null;
		return new Point2D(getX(), getY());
	}

	@Override
	public Pair<? extends Area, Point2D> getPositionPair() {// aramik
		if (!isPositionDefined())
			return null;
		Point2D po = getPositionPoint();
		if (po == null)
			po = new Point2D(getPositionArea().getX(), getPositionArea().getY());
		Area pos = (getPosition() instanceof Area) ? (Area) getPosition() : (Area) ((Human) getPosition()).getPosition();
		return new Pair<Area, Point2D>(pos, po);
	}

	public Area getPositionArea() {// aramik
		if (!isPositionDefined())
			return null;
		Area pos = (getPosition() instanceof Area) ? (Area) getPosition() : (Area) ((Human) getPosition()).getPosition();
		return pos;
	}

	@Override
	public Shape getShape() {
		Shape shape = new Ellipse2D.Double(getX() - RESCUE_AGENT_RADIUS, getY() - RESCUE_AGENT_RADIUS, RESCUE_AGENT_RADIUS * 2, RESCUE_AGENT_RADIUS * 2);
		return shape;
	}

	///////////////Ali
	public boolean isAlive() {
		return isHPDefined() && getHP() > 0;
	}

	public boolean isBuried() {
		return !(isBuriednessDefined() && getBuriedness() == 0);
	}

	public boolean isReadyToAct() {
		return isAlive() && !isBuried();
	}

	/* ////////////////////End of S.O.S/////////////////// */
	ArrayList<Edge> imReachableToEdges = new ArrayList<Edge>();

	public ArrayList<Edge> getImReachableToEdges() {
		return imReachableToEdges;
	}

	public void addImReachableToEdge(Edge edge) {
		this.imReachableToEdges.add(edge);
	}
}
