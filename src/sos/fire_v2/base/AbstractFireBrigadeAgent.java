package sos.fire_v2.base;

import java.util.EnumSet;

import javax.xml.soap.SOAPException;

import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.messages.AKExtinguish;
import rescuecore2.worldmodel.EntityID;
import sos.base.PlatoonAgent;
import sos.base.SOSConstant.AgentType;
import sos.base.entities.Building;
import sos.base.entities.FireBrigade;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.util.SOSActionException;
import sos.base.util.information_stacker.act.ExtinguishAct;
import sos.fire_v2.FireBrigadeAgent;
import sos.fire_v2.base.worldmodel.FireWorldModel;

/**
 * SOS fire brigade agent.
 */
public abstract class AbstractFireBrigadeAgent extends PlatoonAgent<FireBrigade> {
	protected static final String MAX_WATER_KEY = "fire.tank.maximum";
	protected static final String MAX_DISTANCE_KEY = "fire.extinguish.max-distance";
	protected static final String MAX_POWER_KEY = "fire.extinguish.max-sum";
	protected static final String VIEW_DISTANCE = "perception.los.max-distance"; // Nima

	public static int maxWater;
	public static int maxDistance;
	public static int maxPower;
	public static int viewDistance; // Nima

	/**
	 * Send an extinguish command to the kernel.
	 * 
	 * @param time
	 *            The current time.
	 * @param target
	 *            The target building.
	 * @param water
	 *            The amount of water to use.
	 * @throws SOSActionException
	 * @throws SOAPException
	 */
	@Deprecated
	protected void extinguish(EntityID target, int water) throws SOSActionException {
		extinguish((Building) model().getEntity(target), water);
	}

	public void extinguish(Building target, int water) throws SOSActionException {
		if (water <= 0) {
			System.err.println("Invalid Extinguish water==0 ::> " + me() + "  " + target);
			return;
		}
		target.virtualData[0].extinguish(water);
		messageBlock = new MessageBlock(HEADER_WATER);
		messageBlock.addData(DATA_BUILDING_INDEX, target.getBuildingIndex());
		messageBlock.addData(DATA_FIRE_INDEX, me().getFireIndex());
		messageBlock.setResendOnNoise(false);
//		messages.add(messageBlock);
		sayMessages.add(messageBlock);
		send(new AKExtinguish(getID(), model().time(), target.getID(), water));
		informationStacker.addInfo(model(), new ExtinguishAct(target, water));
		throw new SOSActionException("Extinguish target: " + target);
	}

	@Override
	protected FireWorldModel createWorldModel() {
		return new FireWorldModel(this);
	}

	@Override
	public FireWorldModel model() {
		return (FireWorldModel) super.model();
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.FIRE_BRIGADE);
	}

	@Override
	public String toString() {
		return "FireBrigadeAgent [" + getID() + "]";
	}

	@Override
	public FireBrigade me() {
		return super.me();
	}

	@Override
	public AgentType type() {
		return AgentType.FireBrigade;
	}

	/**
	 * @author Ali
	 *         This method provide a global access to SOSAgent
	 * @param agentClass
	 *            is type of expected Agent
	 * @return
	 */
	public static FireBrigadeAgent currentAgent() {
		return currentAgent(FireBrigadeAgent.class);
	}

}