package sos.base.sosFireEstimator;


public class SOSIsolatedFireSite {
//	private SOSFireEstimatorWorldModel esModel;
//	private SOSWorldModel model;
//	private SOSFireEstimatorWorldModel newEsModel;
//	private SOSFireEstimator newFireEstimator;
//	SOSLoggerSystem log = new SOSLoggerSystem(null, "Isole/isole", true, OutputType.File, true);
//
//	public SOSIsolatedFireSite(SOSFireEstimatorWorldModel esModel, SOSWorldModel model) {
//		this.model = model;
//		this.esModel = esModel;
//		newEsModel = esModel.clone();
//		newFireEstimator = new SOSFireEstimator(model, newEsModel, null, 1, (short) -1);
//		model.sosAgent().sosLogger.addToAllLogType(log);
//	}
//
//	public boolean isIsole(SOSFireSite fireSite, int estimateTime) {
//		log.info("estimate fire site for time=" + estimateTime);
//		long t1 = System.currentTimeMillis();
//		log.info(fireSite + " started to checking for isole");
//		initialize(fireSite.getEstimatorBuilding());
//		newFireEstimator.setEstimatorBuildings(fireSite.getEstimatorBuilding());
//		for (int i = 0; i < estimateTime; i++) {
//			newFireEstimator.step(i, fireSite);
//		}
//		log.info("time for isole " + (System.currentTimeMillis() - t1));
//		return false;
//	}
//	private void initialize(ArrayList<Building> estimatorBuilding) {
//		for (Building b : estimatorBuilding) {
//			if (b.virtualData[1] == null)
//				b.virtualData[1] = new VirtualData(b);
//				b.virtualData[1].setRealBuildingProperty();
//			//			log.info(b + " initialized ");
//
//		}
//	}

}
