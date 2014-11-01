package sos.fire_v2;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTextArea;

import sos.base.SOSConstant;
import sos.base.entities.FireBrigade;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.fire_v2.base.AbstractFireBrigadeAgent;
import sos.fire_v2.base.worldmodel.FirePrecompute;
import sos.fire_v2.decision.FBFeedbackFactory;
import sos.fire_v2.decision.FireDecisionMaker;
import sos.fire_v2.position.Positioing;
import sos.search_v2.agentSearch.FireBrigadeSearch;
import sos.search_v2.tools.cluster.ClusterData;
import sos.search_v2.worldModel.SearchWorldModel;
import sos.tools.decisionMaker.definitions.commands.SOSTask;

public class FireBrigadeAgent extends AbstractFireBrigadeAgent {
	private ArrayList<ClusterData> clusters= new ArrayList<ClusterData>();
	public FireDecisionMaker FDK;

	public JTextArea mySelectHistory = new JTextArea() {
		private static final long serialVersionUID = 1L;

		@Override
		public void append(String str) {
			if (!SOSConstant.IS_CHALLENGE_RUNNING)
				super.append(str);
		};
	};

	private void newThink() throws SOSActionException {
		FDK.lastAct = "NONE";
		FDK.lastState = "NONE";
		SOSTask<?> decide = FDK.decide();
		if (decide != null) {
			FDK.lastAct = decide.getClass().getSimpleName();
			decide.execute(this);
		}
		FDK.lastAct = "SEARCH";
		search();
	}

	public Positioing positioning;

	@Override
	protected void prepareForThink() {
		super.prepareForThink();
	}

	@Override
	protected void think() throws SOSActionException {
		super.think();
		newThink();
	}

	public void log(String st) {
		sosLogger.agent.info(st);
	}

	@Override
	protected void finalizeThink() {
		super.finalizeThink();
	}

	@Override
	protected void preCompute() {
		super.preCompute();

		mySelectHistory.setFont(new Font("serif", Font.ITALIC, 12));
		maxWater = config.getIntValue(MAX_WATER_KEY);
		maxDistance = config.getIntValue(MAX_DISTANCE_KEY);
		maxPower = config.getIntValue(MAX_POWER_KEY);
		viewDistance = config.getIntValue(VIEW_DISTANCE);
	
		model().setOwner(this);

		FirePrecompute preCompute = new FirePrecompute(model());

		model().setMapSides();
		model().setSideIslands();
		preCompute.execute();

		positioning = new Positioing(this, model());

		log("MAP NAME ::::> " + getMapInfo().getRealMapName());

		/////SEARCH////////////////////////
		SearchWorldModel<FireBrigade> ss = new SearchWorldModel<FireBrigade>(model());
		model().searchWorldModel = ss;
		newSearch = new FireBrigadeSearch(this);

		////////////SELECT//////
		model().estimatedModel.world = model();
		FDK = new FireDecisionMaker(this, new FBFeedbackFactory());
	}

	@Override
	public void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel) {
		super.hear(header, data, dynamicBitArray, sender, channel);
	}

	@Override
	protected void thinkAfterExceptionOccured() throws SOSActionException {
		try {
			search();
		} catch (SOSActionException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		randomWalk(true);
	}
	public ArrayList<ClusterData> getcluster() {
		return clusters;

	}

}
