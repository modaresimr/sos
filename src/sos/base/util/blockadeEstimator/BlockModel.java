package sos.base.util.blockadeEstimator;

import java.util.HashSet;
import java.util.Set;

import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Blockade;
import sos.base.entities.Road;

/**
 * @author Ali
 */
public class BlockModel {
	//	private ArrayList<SOSBlockade> sosBlockades = new ArrayList<SOSBlockade>();
	//
	//	private ArrayList<SOSBlockade> removedSosBlockades = new ArrayList<SOSBlockade>();
	// private ArrayList<SOSBlockade> maxBlockades = new ArrayList<SOSBlockade>();
	//	private ArrayList<SOSBlockade> middleBlockades=new ArrayList<SOSBlockade>();
	private HashSet<Blockade> removedBlockades = new HashSet<Blockade>();

	private final SOSAgent<?> sosAgent;

	public BlockModel(SOSAgent<?> sosAgent) {
		this.sosAgent = sosAgent;

	}

	/**
	 * @author Ali
	 */
	//	public ArrayList<SOSBlockade> sosBlockades() {
	//		return sosBlockades;
	//	}

	/**
	 * @author Ali
	 * @return
	 */
	//	public ArrayList<SOSBlockade> removedSosBlockades() {
	//		return removedSosBlockades;
	//	}

	/**
	 * @author Ali
	 * @return
	 */
	public Set<Blockade> removedBlockades() {
		return removedBlockades;
	}

	// /**
	// * @author Ali
	// * @return
	// */
	// public void setMaxBlockades(ArrayList<SOSBlockade> maxBlockades) {
	// this.maxBlockades = maxBlockades;
	// }

	// /**
	// * @author Ali
	// * @return
	// */
	// public ArrayList<SOSBlockade> maxBlockades() {
	// return maxBlockades;
	// }

	/**
	 * @author Ali
	 * @return
	 */
	//	public ArrayList<SOSBlockade> middleBlockades() {
	//		return middleBlockades;
	//	}

	//	public void removeSOSBlockade(SOSBlockade sosBlockade) {
	//		if (sosBlockade == null)
	//			return;
	//		// sosBlockade.getOwnerBuilding().getMySosBlock().remove(sosBlockade);
	//		model().block.sosBlockades().remove(sosBlockade);
	//		sosBlockade.getPosition().getSosBlockades().remove(sosBlockade);
	//		model().block.removedSosBlockades().add(sosBlockade);
	//	}

	//	public void updateSosBlockadeIfNeeded(SOSBlockade sosBlockade, int[] apexes) {
	//		if (!AliGeometryTools.areEqual(sosBlockade.getApexes(), apexes, 2))
	//			updateSosBlockade(sosBlockade, apexes);
	//
	//	}

	//	public void updateSosBlockade(SOSBlockade sosBlockade, int[] apexes) {
	//		sosBlockade.setApexes(apexes);
	//		sosBlockade.setClearedTime(sosAgent.blockadeEstimator.findTimeOfCleared(sosBlockade));
	//	}

	//	/**
	//	 * @author Ali
	//	 * @return
	//	 */
	//	public void updateMiddleBlockadeToSOSBlock(SOSBlockade sosBlockade, int[] apexes) {
	//		sosBlockade.getPosition().getSosBlockades().add(sosBlockade);
	//		model().block.sosBlockades().add(sosBlockade);
	//		sosBlockade.setFoggyBlockade(false);
	//		sosBlockade.setApexes(apexes);
	//		sosBlockade.setClearedTime(0);
	//	}

	/**
	 * @author Ali
	 * @return
	 */
	public void newMiddleBlockade(SOSBlockade sosBlockade) {

		//		model().block.middleBlockades().add(sosBlockade);
		sosBlockade.getPosition().getMiddleBlockades().add(sosBlockade);
	}

	//	public void removeMiddleBlockade(SOSBlockade sosBlockade) {
	//		if (sosBlockade == null)
	//			return;
	//		// model().block.middleBlockades().get(sosBlockade.getOwnerBuilding() == null ? NULL_BUILDING : sosBlockade.getOwnerBuilding()).remove(sosBlockade);
	//		sosBlockade.getPosition().getMiddleBlockades().remove(sosBlockade);
	//
	//	}

	// /**
	// * @author Ali
	// * @return
	// */
	// public void newMaxSosBlockade(SOSBlockade sosBlockade) {
	// sosBlockade.setFoggyBlockade(true);
	// model().block.maxBlockades().add(sosBlockade);
	// sosBlockade.getPosition().getMaxBlockades().add(sosBlockade);
	// }

	public SOSWorldModel model() {
		return sosAgent.model();
	}

	//	public void setMiddleBlockades(ArrayList<SOSBlockade> middleBlockades) {
	//		this.middleBlockades = middleBlockades;
	//	}

	public void removeMiddleBlockadesOfRoad(Road oldRd) {
		if (oldRd.getMiddleBlockades() != null)
			oldRd.getMiddleBlockades().clear();
		oldRd.setMiddleBlockadesNull();
	}
}
