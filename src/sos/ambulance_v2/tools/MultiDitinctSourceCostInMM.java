package sos.ambulance_v2.tools;

import java.util.ArrayList;
import java.util.HashMap;

import rescuecore2.geometry.Vector2D;
import sos.base.SOSWorldModel;
import sos.base.entities.Area;
import sos.base.message.system.MessageSystem;
import sos.base.move.MoveConstants;
import sos.base.move.types.StandardMove;
import sos.base.util.blockadeEstimator.AliGeometryTools;

public class MultiDitinctSourceCostInMM {

	private final HashMap<Area,CostFromInMM> distinctSrc_cost;
	private final SOSWorldModel model;

	public MultiDitinctSourceCostInMM(SOSWorldModel model,ArrayList<Area> distinctSrc) {
		this.model = model;
		distinctSrc_cost=new HashMap<Area, CostFromInMM>(distinctSrc.size());
		long t1 = System.currentTimeMillis();
		if(shouldCheckExact(model))
		for (Area area : distinctSrc) 
			distinctSrc_cost.put(area, new CostFromInMM(model, area));
		
		model.sosAgent().sosLogger.base.trace("performing ambulacnce dijkstra took:"+(System.currentTimeMillis()-t1)+" ms");
	}
	
	public long getCostFromTo(Area src,Area des) {
		if(distinctSrc_cost.get(src)!=null)
			return distinctSrc_cost.get(src).getCostTo(des);
		return getFoolCost(src,des);
//		return getFoolCostFromTo(src, des);
		
	}
	private long getFoolCost(Area src, Area des) {
		Area firstpo = model.me().getAreaPosition();

		long moveToLastPo = model.sosAgent().move.getWeightTo(src, StandardMove.class);
		if (moveToLastPo >= MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING)
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;

		long moveToTarget = model.sosAgent().move.getWeightTo(des, StandardMove.class);
		if (moveToTarget >= MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING)
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		Vector2D v1 = new Vector2D(src.getX() - firstpo.getX(), src.getY() - firstpo.getY());
		Vector2D v2 = new Vector2D(des.getX() - firstpo.getX(), des.getY() - firstpo.getY());
		double angle = AliGeometryTools.getAngleInRadian(v1, v2);
		long moveFromLastPosToTargetX = (long) (moveToLastPo - moveToTarget * Math.cos(angle));
		long moveFromLastPosToTargety = (long) (Math.sin(angle));

		double len = Math.hypot(moveFromLastPosToTargetX, moveFromLastPosToTargety);
		if (len > MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING)
			return MoveConstants.UNREACHABLE_COST_FOR_GRAPH_WEIGTHING;
		return (int) len;
	}

	public static int getFoolCostFromTo(Area from, Area to) {//TODO maybe change to dis=(x-x')+(y-y')
		int dis = (Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY()))*3/2;
		return dis;
	}

	private boolean shouldCheckExact(SOSWorldModel model){
		return !(model.sosAgent().messageSystem.type== MessageSystem.Type.NoComunication
				||model.sosAgent().messageSystem.type== MessageSystem.Type.LowComunication);
	}
}
