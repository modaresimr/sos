package sos.search_v2.agentSearch;

import sos.base.SOSAgent;
import sos.base.entities.FireBrigade;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.search_v2.tools.cluster.BuridBlockSearchCluster;
import sos.search_v2.tools.searchScore.FireBrigadeSearchScore;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public class FireBrigadeSearch extends AgentSearch<FireBrigade> {
	public FireBrigadeSearch(SOSAgent<FireBrigade> me) {
		super(me, new BuridBlockSearchCluster<FireBrigade>(me, me.model().fireBrigades()), FireBrigadeSearchScore.class);
	}

	@Override
	public void hear(String header, DataArrayList data, StandardEntity sender, Channel channel) {
		super.hear(header, data, sender, channel);
	}

	@Override
	public void initSearchOrder() {
		searchTypes.add(strategyChooser.dummySearch);
	}

}
