package sos.search_v2.agentSearch;

import sos.base.SOSAgent;
import sos.base.entities.PoliceForce;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.search_v2.tools.cluster.BuridBlockSearchCluster;
import sos.search_v2.tools.searchScore.PoliceSearchScore;

/**
 * @author Yoosef Golshahi
 * @param <E>
 */
public class PoliceSearch extends AgentSearch<PoliceForce> {

	public PoliceSearch(SOSAgent<PoliceForce> me) {
		super(me, new BuridBlockSearchCluster<PoliceForce>(me, me.model().policeForces()), PoliceSearchScore.class);

	}

	@Override
	public void hear(String header, DataArrayList data, StandardEntity sender, Channel channel) {
		super.hear(header, data, sender, channel);
		////////////////////////////////////////////////////////

	}

	@Override
	public void initSearchOrder() {
		searchTypes.add(strategyChooser.dummySearch);
	}

}
