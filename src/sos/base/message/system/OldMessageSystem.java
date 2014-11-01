package sos.base.message.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import rescuecore2.KernelConstants;
import sos.ambulance_v2.decision.AmbulanceCenterActivity;
import sos.base.SOSAgent;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Center;
import sos.base.entities.FireBrigade;
import sos.base.entities.Human;
import sos.base.entities.PoliceForce;
import sos.base.entities.StandardEntity;
import sos.base.message.MessageAgent;
import sos.base.message.MessageBuffer;
import sos.base.message.ReadXml;
import sos.base.message.Selector;
import sos.base.message.structure.MessageConfig;
import sos.base.message.structure.MessageConstants;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.blocks.MessagePackage;
import sos.base.message.structure.channel.Channel;
import sos.base.message.structure.channel.RadioChannel;
import sos.base.message.structure.channel.VoiceChannel;
import sos.fire_v2.FireStationActivity;
import sos.police_v2.PoliceOfficeActivity;

/**
 *
 * @author Ali
 *
 */
public class OldMessageSystem implements MessageConstants {
	public Type type;
	private SOSAgent<? extends StandardEntity> sosAgent;
	private MessageConfig messageConfig;
	private ArrayList<MessageAgent> messageHumans = new ArrayList<MessageAgent>();
	private ArrayList<MessageAgent> messageCentres = new ArrayList<MessageAgent>();
	private MessageAgent mine;
	int messageCenterNum = 0;
	private Selector selector;

	public OldMessageSystem(SOSAgent<? extends StandardEntity> sosAgent) {
		this.sosAgent = sosAgent;
		//		  sosAgent.sosLogger.messageTransmit.setOutputType(OutputType.Both);
		selector = new Selector(sosAgent.messageSystem);
		messageConfig = new MessageConfig(sosAgent.getConfig());
		sosAgent.sosLogger.messageTransmit.debug(getMessageConfig().toString());
		convertAgents();
		if (messageConfig.getPlatoonSubScribeLimit() == 0)
			type = Type.NoComunication;
		else {
			determineChannelsDistribution();
			optimizeChannelsDistribution();
			assignAgentSendChannels();
		}
	}

	private void assignAgentSendChannels() {
		Collections.sort(messageHumans, new Comparator<MessageAgent>() {
			@Override
			public int compare(MessageAgent o1, MessageAgent o2) {// we define that fire have more priority
				int o1p = 0, o2p = 0;
				if (o1.getMe() instanceof FireBrigade)
					o1p = o1.getMe().getID().getValue() + 200;
				if (o1.getMe() instanceof AmbulanceTeam)
					o1p = o1.getMe().getID().getValue() + 100;
				if (o1.getMe() instanceof PoliceForce)
					o1p = o1.getMe().getID().getValue() + 1;

				if (o2.getMe() instanceof FireBrigade)
					o2p = o2.getMe().getID().getValue() + 200;
				if (o2.getMe() instanceof AmbulanceTeam)
					o2p = o2.getMe().getID().getValue() + 100;
				if (o2.getMe() instanceof PoliceForce)
					o2p = o2.getMe().getID().getValue() + 1;

				return o2p - o1p;
			}
		});
		sosAgent.sosLogger.messageTransmit.trace("Sorted Message Agents:" + messageHumans);
		ArrayList<RadioChannel> remainedchannels = new ArrayList<RadioChannel>();
		int bandwidthByteSize = 0;
		for (RadioChannel radioChannel : getMessageConfig().radioChannels().values()) {
			if (radioChannel.getSenders().size() == 0 & radioChannel.getRecievers().size() > 0) {
				remainedchannels.add(radioChannel);
				bandwidthByteSize += radioChannel.getBandwidth();
			}
		}
		Collections.sort(remainedchannels, new Comparator<RadioChannel>() {
			@Override
			public int compare(RadioChannel r1, RadioChannel r2) {
				for (MessageAgent agent : r1.getRecievers()) {
					if (agent.getIndex() == 0)
						return -1;
				}
				for (MessageAgent agent : r2.getRecievers()) {
					if (agent.getIndex() == 0)
						return 1;
				}
				return r2.getBandwidth() - r1.getBandwidth();
			}
		});
		sosAgent.sosLogger.messageTransmit.debug("Channels that not assigned to agent yet:" + remainedchannels);
		sosAgent.sosLogger.messageTransmit.info("Determining number of agents for each remained channels...");
		int remaindAgentNum = sosAgent.model().agents().size();
		for (int i = 0; i < remainedchannels.size(); i++) {
			int agentSendNum = Math.round(messageHumans.size() * remainedchannels.get(i).getBandwidth() / (float) bandwidthByteSize);
			if (i == remainedchannels.size() - 1)
				agentSendNum = remaindAgentNum;
			remainedchannels.get(i).setAgentSendNum(agentSendNum);
			remaindAgentNum -= agentSendNum;
			sosAgent.sosLogger.messageTransmit.debug(agentSendNum + " of agents assigned send for " + remainedchannels.get(i));
		}
		sosAgent.sosLogger.messageTransmit.info("set agents to remained channels...");
		int i = 0;
		for (RadioChannel radioChannel : remainedchannels) {
			int lastI = i;
			for (; i < radioChannel.getAgentSendNum() + lastI && i < messageHumans.size(); i++) {
				messageHumans.get(i).addSendChannels(radioChannel);
				sosAgent.sosLogger.messageTransmit.debug(messageHumans.get(i).fullDescribtion());
			}
		}
		//		  debug();
	}

	private void optimizeChannelsDistribution() {
		// TODO Optimizing message Distribution
	}

	private void convertAgents() {
		int index = 0;
		for (Center centre : sosAgent.model().centers()) {
			MessageAgent messageAgent = new MessageAgent(centre, getMessageConfig().getCenterSubScribeLimit(), index);
			if (sosAgent.me().getID().equals(centre.getID()))
				mine = messageAgent;
			messageCentres.add(messageAgent);
			messageAgent.setMiddleMan(true);
			if (index == 0) {
				sosAgent.addCenterActivity(new AmbulanceCenterActivity(sosAgent));
				sosAgent.addCenterActivity(new FireStationActivity(sosAgent));
				sosAgent.addCenterActivity(new PoliceOfficeActivity(sosAgent));
			}
			index++;
		}
		for (Human agent : sosAgent.model().agents()) {
			MessageAgent messageAgent = new MessageAgent(agent, getMessageConfig().getPlatoonSubScribeLimit(), index);
			if (sosAgent.me().getID().equals(agent.getID()))
				mine = messageAgent;
			messageHumans.add(messageAgent);
			index++;
		}
	}

	public void sendMessage(MessageBuffer messages) {
		LinkedList<MessageBlock> selectedMessages = new LinkedList<MessageBlock>();
		for (RadioChannel radioChannel : getMine().getSendChannels()) {
			speak(radioChannel, selector.pack(messages, radioChannel.getSendByteLimit(mine),selectedMessages,true));
		}
	}

	public void sayMessage(MessageBuffer messages) {
		for (VoiceChannel voiceChannel : getMessageConfig().voiceChannels().values()) {
			speak(voiceChannel, selector.selectingForSay(messages, voiceChannel.getMessagesSize()));
		}
	}

	private void speak(VoiceChannel voiceChannel, LinkedList<MessageBlock> messageBlocks) {
		int bitSize = 0;
		ChannelSystemType channelType = ReadXml.getChannelSystemType(voiceChannel, type);
		for (MessageBlock messageBlock : messageBlocks) {
			bitSize += messageBlock.getBitSize(channelType);
		}
		SOSBitArray bitArray = new SOSBitArray(bitSize);
		int bitPosition = 0;
		for (MessageBlock messageBlock : messageBlocks) {
			bitArray.set(bitPosition, messageBlock.toBitArray(channelType));
			bitPosition += messageBlock.getBitSize(channelType);
		}
		speak(voiceChannel, bitArray.toByteArray());
	}

	private void speak(RadioChannel radioChannel, ArrayList<MessagePackage> packages) {
		for (MessagePackage messagePackage : packages) {
			speak(radioChannel, messagePackage.toByteArray());
		}
	}

	public void setChannels() {
		if (sosAgent.time() == sosAgent.getConfig().getIntValue(KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
			subscribe(getMine().getSubscribeChannels());
		}

	}

	private void subscribe(ArrayList<RadioChannel> subscribeChannels) {
		int[] channels = new int[subscribeChannels.size()];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = subscribeChannels.get(i).getChannelId();
		}
	}

	private void determineChannelsDistribution() {
		messageCenterNum = estimateMessageCenterType();
		sosAgent.sosLogger.messageTransmit.debug("First determining message centre need:" + messageCenterNum);
		if (getMessageConfig().getCenterSubScribeLimit() >= 4 && getMessageConfig().getPlatoonSubScribeLimit() >= 2) {
			ArrayList<RadioChannel> remainedchannels = new ArrayList<RadioChannel>(getMessageConfig().radioChannels().values());

			if (messageCenterNum != 0) {// if we have center
				// at the first we set 2 channels for all agents to subscribe
//				type = Type.WithCenter;
				sosAgent.sosLogger.messageTransmit.info("sorting channels by their bandwidth");

				Collections.sort(remainedchannels, new Comparator<RadioChannel>() {
					@Override
					public int compare(RadioChannel r1, RadioChannel r2) {
						return r2.getBandwidth() - r1.getBandwidth();
					}
				});

				sosAgent.sosLogger.messageTransmit.info("set channels recivers for HumanAgents");
				for (MessageAgent messageHuman : messageHumans) {
					messageHuman.addSubscribeChannels(remainedchannels.get(0));
					messageHuman.addSubscribeChannels(remainedchannels.get(1));
				}

				sosAgent.sosLogger.messageTransmit.info("set the communication between Centers");
				messageCentres.get(0).addSendChannels(remainedchannels.remove(0));// Centeral center index=0
				messageCentres.get(0).addSendChannels(remainedchannels.remove(0));// Centeral center index=0

				for (int i = 0; i < messageCenterNum; i++) {//at first we define free channels for centers to have more channel bandwidth
					messageCentres.get(i).addSubscribeChannels(remainedchannels.remove(0));
					if (!(i == 0 && messageCenterNum == 3))
						messageCentres.get(i).addSubscribeChannels(remainedchannels.remove(remainedchannels.size() - 1));//beacuse we want to make a EQUILIBRIUM in the channels for center
				}

				messageCentres.get(0).addSubscribeChannels(remainedchannels.get(0));
				messageCentres.get(0).addSubscribeChannels(remainedchannels.get(1));

				switch (messageCenterNum) {
				case 3:
					messageCentres.get(0).addSubscribeChannels(remainedchannels.get(2));
					messageCentres.get(1).addSendChannels(remainedchannels.get(0));

					messageCentres.get(2).addSendChannels(remainedchannels.remove(0));
					messageCentres.get(1).addSendChannels(remainedchannels.remove(0));
					messageCentres.get(2).addSendChannels(remainedchannels.remove(0));
					break;
				case 2:
					messageCentres.get(1).addSendChannels(remainedchannels.remove(0));
					messageCentres.get(1).addSendChannels(remainedchannels.remove(0));
					break;
				case 1:
					remainedchannels.remove(0);
					remainedchannels.remove(0);
					break;
				}
				sosAgent.sosLogger.messageTransmit.info("Distributing remained channels between centers and agents");
				boolean haveChanged = true;
				while (0 < remainedchannels.size() && haveChanged) {
					haveChanged = false;
					if (messageCentres.get(0).getSendChannels().size() < getMessageConfig().getPlatoonSubScribeLimit()) {
						for (MessageAgent messageHuman : messageHumans)
							haveChanged |= messageHuman.addSubscribeChannels(remainedchannels.get(0));
						messageCentres.get(0).addSendChannels(remainedchannels.remove(0));
					}
					if (remainedchannels.size() == 0)
						break;
					for (int j = 0; j < messageCenterNum && remainedchannels.size() > 0; j++) {
						if (messageCentres.get(j).addSubscribeChannels(remainedchannels.get(0))) {
							haveChanged |= true;
							remainedchannels.remove(0);
						}
					}
					if (remainedchannels.size() == 0)
						break;
					if (messageCenterNum > 1) {
						if (messageCentres.get(0).addSubscribeChannels(remainedchannels.get(0))) {
							haveChanged |= true;
							switch (messageCenterNum) {
							case 2:
								messageCentres.get(1).addSendChannels(remainedchannels.remove(0));
								break;
							case 3:
								messageCentres.get(1).addSendChannels(remainedchannels.get(0));
								messageCentres.get(2).addSendChannels(remainedchannels.remove(0));
								break;
							}
						}
					}
				}
				//debug();
				for (MessageAgent messageCentre : messageCentres) {
					sosAgent.sosLogger.messageTransmit.debug(messageCentre.fullDescribtion());
				}
				//sosAgent.sosLogger.messageTransmit.debug(messageHumans + "---" + messageCentres);
				sosAgent.sosLogger.messageTransmit.debug("not assigned channels:" + remainedchannels);
			} else /* if(messageCenterNum==0) */{
//				type = Type.NoCenter;
			}
		} else {
		}
	}

	private int estimateMessageCenterType() {
		int centerHear = getMessageConfig().getCenterSubScribeLimit();
		int platoonHear = getMessageConfig().getPlatoonSubScribeLimit();

		if (getMessageConfig().radioChannels().size() > centerHear + centerHear + platoonHear)//centerHear+centerHear/2+centerHear/2+platoonHear
			return Math.min(Math.min(centerHear, 3), sosAgent.model().centers().size());
		if (getMessageConfig().radioChannels().size() > centerHear + Math.round(centerHear / 2f) + platoonHear)
			return Math.min(Math.min(centerHear, 2), sosAgent.model().centers().size());
		if (getMessageConfig().radioChannels().size() > Math.max(Math.round(centerHear / 2f), platoonHear) + platoonHear)//
			return Math.min(Math.min(centerHear, 1), sosAgent.model().centers().size());
		return 0;
	}

	private void speak(Channel channel, byte[] message) {
		sosAgent.speak(channel.getChannelId(), message);
	}

	/*
	 * private void debug() {
	 * for (MessageAgent mAgent : messageHumans) {
	 * StringBuffer mb = new StringBuffer();
	 * mb.append(mAgent.getMe() + "\n\t");
	 * mb.append("subscribe:" + mAgent.getSubscribeChannels() + "\n\t");
	 * mb.append("Send:" + mAgent.getSendChannels());
	 * sosAgent.sosLogger.messageTransmit.debug(mb.toString());
	 * }
	 * for (MessageAgent mAgent : messageCentres) {
	 * StringBuffer mb = new StringBuffer();
	 * mb.append(mAgent.getMe() + "\n\t");
	 * mb.append("subscribe:" + mAgent.getSubscribeChannels() + "\n\t");
	 * mb.append("Send:" + mAgent.getSendChannels());
	 * sosAgent.sosLogger.messageTransmit.debug(mb.toString());
	 * }
	 * }
	 */

	public MessageConfig getMessageConfig() {
		return messageConfig;
	}

	public MessageAgent getMine() {
		return mine;
	}
}
