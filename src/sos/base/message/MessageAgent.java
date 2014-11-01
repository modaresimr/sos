package sos.base.message;

import java.util.ArrayList;

import sos.base.entities.StandardEntity;
import sos.base.message.structure.channel.RadioChannel;

/**
 * 
 * @author Ali
 * 
 */
public class MessageAgent {
	private final ArrayList<RadioChannel> subscribeChannels = new ArrayList<RadioChannel>();
	private final ArrayList<RadioChannel> sendChannels = new ArrayList<RadioChannel>();
	private final StandardEntity me;
	private final int hearLimit;
	private final int index;
	private int weightForSending = 1;
	private boolean isMiddleMan = false;
	private int middleManPriority = 0;
	private boolean unusedAgent=false;
	public MessageAgent(StandardEntity me, int hearLimit, int index) {
		this.me = me;
		setWeightForSending(me.getMessageWeightForSending());
		setMiddleManPriority(me.getMessageMiddleManPriority_forPolice());
		this.hearLimit = hearLimit;
		this.index = index;
		
	}

	public boolean addSubscribeChannels(RadioChannel channel) {
		if (!isHearLimitFull()) {
			channel.addToReceivers(this);
			subscribeChannels.add(channel);
			return true;
		}
		return false;
	}

	public void addSendChannels(RadioChannel channel) {
		channel.addToSenders(this);
		sendChannels.add(channel);
	}

	public ArrayList<RadioChannel> getSubscribeChannels() {
		return subscribeChannels;
	}

	public ArrayList<RadioChannel> getSendChannels() {
		return sendChannels;
	}

	public int getHearLimit() {
		return hearLimit;
	}

	public boolean isHearLimitFull() {
		return hearLimit <= subscribeChannels.size();
	}

	@Override
	public String toString() {
		return getMe().toString();
	}

	public String fullDescribtion() {
		return getMe() + "--> SubScribeChannels:" + getSubscribeChannels() + " SendChannels:" + getSendChannels();
	}

	public StandardEntity getMe() {
		return me;
	}

	public int getIndex() {
		return index;
	}

	public boolean isMiddleMan() {
		return isMiddleMan;
	}

	public void setMiddleMan(boolean isMiddleMan) {
		this.isMiddleMan = isMiddleMan;
	}

	public int getReceiveMessageSize() {
		int sum = 0;
		for (RadioChannel channel : subscribeChannels) {
			sum += channel.getBandwidth();
		}
		return sum;
	}

	public void setWeightForSending(int weightForSending) {
		this.weightForSending = weightForSending;
	}

	public int getWeightForSending() {
		return weightForSending;
	}

	public void setMiddleManPriority(int middleManPriority) {
		this.middleManPriority = middleManPriority;
	}

	public int getMiddleManPriority() {
		return middleManPriority;
	}

	public boolean isUnusedAgent() {
		return unusedAgent;
	}

	public void setUnusedAgent(boolean unusedAgent) {
		this.unusedAgent = unusedAgent;
	}
	
	
}
