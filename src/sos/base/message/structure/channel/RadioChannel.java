package sos.base.message.structure.channel;

import java.util.ArrayList;

import sos.base.message.MessageAgent;
import sos.base.message.structure.MessageConstants;

/**
 * 
 * @author Ali
 * 
 */
public class RadioChannel extends Channel {
	private final int bandwidth;
	private final ArrayList<MessageAgent> senders = new ArrayList<MessageAgent>();
	private final ArrayList<MessageAgent> recievers = new ArrayList<MessageAgent>();
	private int agentSendNum = 0;

	public RadioChannel(int channel, int bandwidth/* ,AgentType receiverType */) {
		super(channel);
		this.bandwidth = bandwidth;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	@Override
	public String toString() {
		return "{Channel:" + getChannelId() + " Bandwidth:" + getBandwidth() + "}";
	}

	public String fullDescribtion() {
		return "Channel:" + getChannelId() + " Type:Radio" + " Bandwidth:" + getBandwidth() + " Noise:" + getNoise();
	}

	public ArrayList<MessageAgent> getSenders() {
		return senders;
	}

	public void addToSenders(MessageAgent messageAgent) {
		getSenders().add(messageAgent);
	}

	public void addToReceivers(MessageAgent messageAgent) {
		getRecievers().add(messageAgent);
	}

	public ArrayList<MessageAgent> getRecievers() {
		return recievers;
	}

	public void setAgentSendNum(int agentSendNum) {
		this.agentSendNum = agentSendNum;
	}

	public int getAgentSendNum() {
		return agentSendNum;
	}

	public int getSendByteLimit(MessageAgent mine) {
		try {
			if (senders.size() == 0)
				throw new Error("Can not getAgentBandwidthForSend");
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
		int bytesReceiveFromAgentsWithWeight = 0;
		for (MessageAgent messageAgent : getSenders()) {
			bytesReceiveFromAgentsWithWeight += messageAgent.getWeightForSending() * messageAgent.getReceiveMessageSize();
		}
		
		
		int bandwidth2 = bandwidth-MessageConstants.UNUSED_AGENT_SEND_BYTE;

		if(mine.isUnusedAgent())
			return MessageConstants.UNUSED_AGENT_SEND_BYTE; 

		return (int) (((float) (mine.getWeightForSending() * mine.getReceiveMessageSize()) / (float) bytesReceiveFromAgentsWithWeight) * bandwidth2);
	}

}
