package sos.base.message.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import rescuecore2.KernelConstants;
import rescuecore2.standard.components.StandardAgent;
import sample.SampleWorldModel;
import sos.ambulance_v2.decision.AmbulanceCenterActivity;
import sos.base.AbstractSOSAgent;
import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.Center;
import sos.base.entities.Human;
import sos.base.entities.PoliceForce;
import sos.base.entities.StandardEntity;
import sos.base.entities.StandardWorldModel;
import sos.base.message.MessageAgent;
import sos.base.message.MessageBuffer;
import sos.base.message.ReadXml;
import sos.base.message.Selector;
import sos.base.message.noise_database.NoisyMessageDatabase;
import sos.base.message.structure.MessageConfig;
import sos.base.message.structure.MessageConstants;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.blocks.MessagePackage;
import sos.base.message.structure.channel.Channel;
import sos.base.message.structure.channel.RadioChannel;
import sos.base.message.structure.channel.VoiceChannel;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.fire_v2.FireStationActivity;
import sos.police_v2.PoliceOfficeActivity;

/**
 * @author Ali
 */
public class MessageSystem implements MessageConstants {
	private static final int NORMAL_BANDWIDTH_FOR_EACH_AGENT = 30;
	private static final float USEFUL_PRECENT_FOR_RECEIVE_PER_SEND = .8f;

	public Type type;
	private final MessageConfig messageConfig;
	private final ArrayList<MessageAgent> messageAgents = new ArrayList<MessageAgent>();
	private final ArrayList<MessageAgent> messageMiddleManAgents = new ArrayList<MessageAgent>();
	private final ArrayList<MessageAgent> messageNormalAgents = new ArrayList<MessageAgent>();
	private final ArrayList<MessageAgent> messageCenters = new ArrayList<MessageAgent>();
	private final ArrayList<MessageAgent> unusedAgent = new ArrayList<MessageAgent>();

	public final NoisyMessageDatabase noisyMessageDatabase;
	private MessageAgent mine;
	int messageCenterNum = 0;
	private final Selector selector;
	public SOSLoggerSystem mtLog;
	public SOSLoggerSystem mcLog;
	public final StandardWorldModel model;
	private final StandardAgent<?> agent;

	/**
	 * noisy database only used for nonMiddleMan Agents
	 * 
	 * @param sosAgent
	 */
	public MessageSystem(StandardAgent<?> agent) {
		this.agent = agent;
		this.model = agent.model();
		if (model instanceof SOSWorldModel) {
			this.mtLog = ((SOSWorldModel) model).sosAgent().sosLogger.messageTransmit;
			this.mcLog = ((SOSWorldModel) model).sosAgent().sosLogger.messageContent;
		} else if (model instanceof SampleWorldModel) {
			mtLog = ((SampleWorldModel) model).log();
			mcLog = ((SampleWorldModel) model).log();
		}

		noisyMessageDatabase = new NoisyMessageDatabase(this);
		selector = new Selector(this);
		messageConfig = new MessageConfig(agent.getConfig());
		mtLog.debug(getMessageConfig().toString());
		convertAgents();
		if (messageConfig.getPlatoonSubScribeLimit() == 0)
			type = Type.NoComunication;
		else {
			determineChannelsDistribution();
			assignAgentSendChannels();
			checkCenteralMiddleMan();
		}
		removeUnusefulCenters();
		mtLog.debug("MessageComunicationType:" + type);
		fullDebug();
	}

	private void removeUnusefulCenters() {
		mtLog.info("removeUnusefulCenters....");

		HashSet<RadioChannel> remainedchannels = new HashSet<RadioChannel>();
		FOR: for (RadioChannel radioChannel : getMessageConfig().radioChannels().values()) {
			for (MessageAgent s : radioChannel.getSenders()) {
				if (messageMiddleManAgents.contains(s))
					continue FOR;
			}
			if (radioChannel.getRecievers().size() > 0) {
				remainedchannels.add(radioChannel);
			}
		}
		mtLog.debug("Channels that will used for unused centers:" + remainedchannels);

		ArrayList<MessageAgent> shouldRemove = new ArrayList<MessageAgent>();
		HashSet<MessageAgent> malist = new HashSet<MessageAgent>(messageMiddleManAgents);
		malist.addAll(messageCenters);
		for (MessageAgent mmma : malist) {
			if (mmma.getSubscribeChannels().size() == 0) {
				shouldRemove.add(mmma);
				mmma.setUnusedAgent(true);
				unusedAgent.add(mmma);
				//for unused center
				for (RadioChannel radioChannel : remainedchannels) {
					mmma.addSendChannels(radioChannel);
					mtLog.debug(mmma.fullDescribtion());

				}

			}

		}
		messageMiddleManAgents.removeAll(shouldRemove);
		//		if(mine.getMe() instanceof Center&&!messageMiddleManAgents.contains(mine))
		//			mine=null;
	}

	private void checkLowCommunication(ArrayList<RadioChannel> remainedchannels) {
		int countReceived = 0;
		for (int i = 0; i < remainedchannels.size(); i++) {
			if (messageConfig.getPlatoonSubScribeLimit() > i) {
				countReceived += remainedchannels.get(i).getBandwidth();
			}
		}
		if (countReceived < 800)
			type = Type.LowComunication;
		//		else if (countReceived / (double) countSend > 3) {
		//			type = Type.LowComunication;
		//		}//TODO

	}

	private void convertAgents() {
		int index = 0;
		//		if (isUsefulToHaveCenter()) {
		for (Center center : model.centers()) {
			MessageAgent messageAgent = new MessageAgent(center, Math.min(getMessageConfig().getCenterSubScribeLimit(), getMessageConfig().radioChannels().size()), index);
			if (model.me().getID().equals(center.getID()))
				mine = messageAgent;
			messageAgent.setMiddleMan(true);
			messageAgents.add(messageAgent);
			messageMiddleManAgents.add(messageAgent);
			messageCenters.add(messageAgent);
			/*
			 * if (index == 0) { sosAgent.centerActivities.add(new AmbulanceCenterActivity(sosAgent)); sosAgent.centerActivities.add(new FireStationActivity(sosAgent)); sosAgent.centerActivities.add(new PoliceOfficeActivity(sosAgent)); }
			 */
			index++;
		}
		//		}
		for (Human agent : model.fireBrigades()) {
			MessageAgent messageAgent = new MessageAgent(agent, Math.min(getMessageConfig().getPlatoonSubScribeLimit(), getMessageConfig().radioChannels().size()), index);
			if (model.me().getID().equals(agent.getID()))
				mine = messageAgent;
			messageAgents.add(messageAgent);
			index++;
		}
		for (Human agent : model.ambulanceTeams()) {
			MessageAgent messageAgent = new MessageAgent(agent, Math.min(getMessageConfig().getPlatoonSubScribeLimit(), getMessageConfig().radioChannels().size()), index);
			if (model.me().getID().equals(agent.getID()))
				mine = messageAgent;
			messageAgents.add(messageAgent);
			index++;
		}
		ArrayList<MessageAgent> tmpPoliceMessageList = new ArrayList<MessageAgent>();
		for (PoliceForce agent : model.policeForces()) {
			MessageAgent messageAgent = new MessageAgent(agent, Math.min(getMessageConfig().getPlatoonSubScribeLimit(), getMessageConfig().radioChannels().size()), index);
			if (model.me().getID().equals(agent.getID()))
				mine = messageAgent;
			tmpPoliceMessageList.add(messageAgent);
			// messageAgent.setMiddleManPriority(agent.getMiddleManPriority());
			index++;
		}
		Collections.sort(tmpPoliceMessageList, new Comparator<MessageAgent>() {
			@Override
			public int compare(MessageAgent o1, MessageAgent o2) {
				return o2.getMiddleManPriority() - o1.getMiddleManPriority();
			}
		});
		messageAgents.addAll(tmpPoliceMessageList);
	}

	private void determineChannelsDistribution() {
		mtLog.info("determining Channels Distribution...");

		ArrayList<RadioChannel> remainedchannels = new ArrayList<RadioChannel>(messageConfig.radioChannels().values());
		sortChannels(remainedchannels);
		type = Type.WithMiddleMan;
		checkLowCommunication(remainedchannels);
		if (type == Type.LowComunication) {
			mtLog.debug("Message system is Low Communication...");
			mtLog.warn("Low Communication is under construction");
			takeMiddleManAndNormalAgents(remainedchannels);
			messageMiddleManAgents.clear();
			messageCenters.clear();
			setNoMiddleManMessageSystem(remainedchannels);
			type = Type.LowComunication;
		} else {
			takeMiddleManAndNormalAgents(remainedchannels);
			if (messageMiddleManAgents.size() > 0) {
				setWithMiddleMenMessageSystem(remainedchannels);
			} else /* if (messageMiddleManAgents.size() == 0) */{
				setNoMiddleManMessageSystem(remainedchannels);
			}
		}
	}

	private void checkCenteralMiddleMan() {
		mtLog.info("checking CenteralMiddleMan...");
		int numberOfUsedMiddleMan = 0;
		for (MessageAgent messageMiddleManAgent : messageMiddleManAgents) {
			int sumSendMessage = 0;
			for (RadioChannel radioChannel : messageMiddleManAgent.getSendChannels()) {
				sumSendMessage += radioChannel.getSendByteLimit(messageMiddleManAgent);
			}
			if (sumSendMessage != 0)
				if (numberOfUsedMiddleMan++ > 1)
					break;
		}
		if (numberOfUsedMiddleMan == 1) {
			mtLog.debug("Yeah! it is centeral middle man!");
			type = Type.CenteralMiddleMan;
			if (mine.isMiddleMan() && mine.getSubscribeChannels().size() > 0) {
				mtLog.info("setting the centers activity");
				if (agent instanceof SOSAgent<?>) {
					((AbstractSOSAgent<? extends StandardEntity>) agent).addCenterActivity(new AmbulanceCenterActivity((SOSAgent<? extends StandardEntity>) agent));
					((AbstractSOSAgent<? extends StandardEntity>) agent).addCenterActivity(new FireStationActivity((SOSAgent<?>) agent));
					((AbstractSOSAgent<? extends StandardEntity>) agent).addCenterActivity(new PoliceOfficeActivity((SOSAgent<?>) agent));
				}
			}
		} else {
			mtLog.info("No! it is not centeral middleMan");
		}
	}

	private void assignAgentSendChannels() {
		mtLog.info("Assigning AgentSendChannels...");

		ArrayList<RadioChannel> remainedchannels = new ArrayList<RadioChannel>();
		int totalBandwidthByteSize = 0;
		for (RadioChannel radioChannel : getMessageConfig().radioChannels().values()) {
			if (radioChannel.getSenders().size() == 0 & radioChannel.getRecievers().size() > 0) {
				remainedchannels.add(radioChannel);
				totalBandwidthByteSize += radioChannel.getBandwidth();
			}
		}
		sortChannels(remainedchannels);
		mtLog.debug("Channels that not assigned to agent yet:" + remainedchannels);

		mtLog.info("Determining number of agents for each remainded channels...");
		// int allWeight=0;
		// for (MessageAgent messageAgent : messageNormalAgents) {
		// allWeight+=messageAgent.getWeightForSending();
		// }//TODO Check it if it can be useful

		int remaindAgentNum = messageNormalAgents.size();
		for (int i = 0; i < remainedchannels.size(); i++) {
			int agentSendNum = (int) Math.ceil(messageNormalAgents.size() * remainedchannels.get(i).getBandwidth() / (float) totalBandwidthByteSize);
			if (i == remainedchannels.size() - 1)
				agentSendNum = remaindAgentNum;
			remainedchannels.get(i).setAgentSendNum(agentSendNum);
			remaindAgentNum -= agentSendNum;
			mtLog.debug(agentSendNum + " of agents assigned send for " + remainedchannels.get(i));
		}

		mtLog.info("set agents to remained channels...");
		int i = 0;
		for (RadioChannel radioChannel : remainedchannels) {
			int lastI = i;
			for (; i < radioChannel.getAgentSendNum() + lastI && i < messageNormalAgents.size(); i++) {
				messageNormalAgents.get(i).addSendChannels(radioChannel);
				mtLog.debug(messageNormalAgents.get(i).fullDescribtion());
			}
		}

	}

	//	private boolean isUsefulToHaveCenter() {
	//		return messageConfig.getCenterSubScribeLimit() >= messageConfig.getPlatoonSubScribeLimit();
	//	}

	private void sortChannels(ArrayList<RadioChannel> channels) {
		mtLog.info("sorting channels by their bandwidth");
		Collections.sort(channels, new Comparator<RadioChannel>() {
			@Override
			public int compare(RadioChannel r1, RadioChannel r2) {
				return r2.getBandwidth() - r1.getBandwidth();
			}
		});
		mtLog.info("sorted channels", channels);
	}

	private void setNoMiddleManMessageSystem(ArrayList<RadioChannel> remainedchannels) {
		mtLog.info("No MiddleMan Choosed");
		mtLog.debug("all remainded channel: " + remainedchannels);
		type = Type.NoMiddleMan;
		int assignChannelCount = Math.min(remainedchannels.size(), messageConfig.getPlatoonSubScribeLimit());
		mtLog.info("assign channel count=" + assignChannelCount);
		for (MessageAgent messageHuman : messageNormalAgents) {
			for (int i = 0; i < assignChannelCount; i++) {
				messageHuman.addSubscribeChannels(remainedchannels.get(i));
				messageHuman.addSendChannels(remainedchannels.get(i));
			}
		}
		for (int i = 0; i < assignChannelCount; i++) {
			remainedchannels.remove(0);
		}
		// debug
		mtLog.debug("all remainded channel after disturbing channels: " + remainedchannels);
		for (MessageAgent middleManAgent : messageMiddleManAgents) {
			mtLog.debug(middleManAgent.fullDescribtion());
		}
	}

	private void setWithMiddleMenMessageSystem(ArrayList<RadioChannel> remainedchannels) {
		type = Type.WithMiddleMan;
		mtLog.debug(messageMiddleManAgents.size() + " MiddleMan and center Choosed");
		mtLog.info("set channels receiver and sender for MiddleMan Agents");
		for (MessageAgent middleManAgent : messageMiddleManAgents) {
			for (int j = 0; j < messageConfig.getPlatoonSubScribeLimit(); j++) {
				middleManAgent.addSendChannels(remainedchannels.get(j));
			}
		}
		for (MessageAgent messageHuman : messageNormalAgents) {
			for (int j = 0; j < messageConfig.getPlatoonSubScribeLimit(); j++) {
				messageHuman.addSubscribeChannels(remainedchannels.get(j));
			}
		}
		for (int j = 0; j < messageConfig.getPlatoonSubScribeLimit(); j++) {
			remainedchannels.remove(0);
		}
		mtLog.debug("current remainded channels", remainedchannels);
		mtLog.info("Set remainded channels to MiddleMans");
		for (MessageAgent middleManAgent : messageMiddleManAgents) {
			while (remainedchannels.size() > 0)
				if (middleManAgent.addSubscribeChannels(remainedchannels.get(0)))
					remainedchannels.remove(0);
				else
					break;
		}
		// debug
		mtLog.info("Seting remainded channels to MiddleMans is finished");
		mtLog.debug("all remainded channel after disturbing channels: " + remainedchannels);
		for (MessageAgent middleManAgent : messageMiddleManAgents) {
			mtLog.debug(middleManAgent.fullDescribtion());
		}
	}

	private void takeMiddleManAndNormalAgents(ArrayList<RadioChannel> remainedchannels) {
		mtLog.info("taking MiddleMan and Normal Agents");
		int maxMiddleManSupportedChannel = messageCenters.size() * messageConfig.getCenterSubScribeLimit();
		mtLog.debug("centers supported channels count: " + maxMiddleManSupportedChannel);
		int remaindedChannelCount = messageConfig.radioChannels().size() - maxMiddleManSupportedChannel - messageConfig.getPlatoonSubScribeLimit();

		if (messageConfig.getPlatoonSubScribeLimit() > 1) {
			if (maxMiddleManSupportedChannel == 0) {
				for (int i = 0; i < Math.min(2, remaindedChannelCount / messageConfig.getPlatoonSubScribeLimit()); i++) {// 2 MiddleMan will be take
					MessageAgent messageAgent = messageAgents.get(messageAgents.size() - i - 1);
					messageAgent.setMiddleMan(true);
					messageMiddleManAgents.add(messageAgent);
					remaindedChannelCount -= messageAgent.getHearLimit();
					maxMiddleManSupportedChannel += messageAgent.getHearLimit();
				}
			} else if (remaindedChannelCount > 2 * messageConfig.getPlatoonSubScribeLimit()) {
				MessageAgent messageAgent = messageAgents.get(messageAgents.size() - 1);
				messageAgent.setMiddleMan(true);
				messageMiddleManAgents.add(messageAgent);
				remaindedChannelCount -= messageAgent.getHearLimit();
				maxMiddleManSupportedChannel += messageAgent.getHearLimit();
			}
		}
		if (isItUsefulToHaveNoMiddleMan(remainedchannels, remaindedChannelCount, maxMiddleManSupportedChannel)) {
			for (MessageAgent m : messageMiddleManAgents) {
				m.setMiddleMan(false);
			}
			messageMiddleManAgents.clear();
		}
		for (MessageAgent messageAgent : messageAgents) {
			if (!messageAgent.isMiddleMan() && !(messageAgent.getMe() instanceof Center))
				messageNormalAgents.add(messageAgent);
		}
		// debug
		mtLog.debug("MiddleMen:" + messageMiddleManAgents);
		mtLog.debug("Normal Agents:" + messageNormalAgents);
	}

	private boolean isItUsefulToHaveNoMiddleMan(ArrayList<RadioChannel> remainedchannels, int remaindedChannelCountForAgentToCenter, int maxCenterSupportedChannel) {
		mtLog.info("Checking if having NoMiddleMen is useful ");
		if (messageAgents.size() < 5) {
			mtLog.debug("NoMiddleMen!!!Because the number of agents are less than 5");
			return true;
		} else if (messageConfig.radioChannels().size() <= messageConfig.getPlatoonSubScribeLimit()) {
			mtLog.debug("NoMiddleMen!!!Because size of radioChannels <= platoonSubScribeLimit");
			return true;
		} else if (messageConfig.radioChannels().size() < 3) {
			mtLog.debug("NoMiddleMen!!!Because size of radioChannels <=2 ");
			return true;
		}
		if (canAgentHaveNormalCommunicationWithOutMiddleMan(remainedchannels))
			return true;
		if (isMiddleManJustABroker(remainedchannels, remaindedChannelCountForAgentToCenter, maxCenterSupportedChannel))
			return true;

		mtLog.debug("Messaging System needs atleast a middleMan ");
		return false;
	}

	private boolean isMiddleManJustABroker(ArrayList<RadioChannel> remainedchannels, int remaindedChannelCountForAgentToCenter, int maxCenterSupportedChannel) {
		mtLog.info("Checking if MiddleMen is just a broker (is middle man waste time?)");
		int sendFormMiddleMan = 0, receiveFormMiddleMan = 0;
		int j;
		for (j = 0; j < messageConfig.getPlatoonSubScribeLimit(); j++) {
			sendFormMiddleMan += remainedchannels.get(j).getBandwidth();
		}
		for (; maxCenterSupportedChannel > 0 && j < remainedchannels.size(); j++) {
			receiveFormMiddleMan += remainedchannels.get(j).getBandwidth();
			maxCenterSupportedChannel--;
		}
		mtLog.debug("MiddleMan receive Message size:" + receiveFormMiddleMan + " MiddleMan send Message size:" + sendFormMiddleMan);
		if (receiveFormMiddleMan * USEFUL_PRECENT_FOR_RECEIVE_PER_SEND < sendFormMiddleMan) {
			mtLog.debug("NoMiddleMen!!!Because size of receiveFormMiddleMan*USEFUL_PRECENT_FOR_RECEIVE_PER_SEND'" + USEFUL_PRECENT_FOR_RECEIVE_PER_SEND + "'(" + receiveFormMiddleMan * USEFUL_PRECENT_FOR_RECEIVE_PER_SEND + ")  < sendFormMiddleMan(" + sendFormMiddleMan + ")");
			return true;
		}
		mtLog.info("checking 'isMiddleManJustABroker?' false");
		return false;
	}

	private boolean canAgentHaveNormalCommunicationWithOutMiddleMan(ArrayList<RadioChannel> remainedchannels) {
		mtLog.info("Checking if having agents can have a normal comunication without MiddleMen (each agent can send atleast '" + NORMAL_BANDWIDTH_FOR_EACH_AGENT + "'byte...");
		int assignChannelCount = Math.min(remainedchannels.size(), messageConfig.getPlatoonSubScribeLimit());
		int agentWeightCount = 0;
		int noMiddleManAgentBandwidth = 0;
		for (MessageAgent messageHuman : messageAgents) {
			if (!(messageHuman.getMe() instanceof Center)) {
				agentWeightCount += messageHuman.getWeightForSending();
			}
		}
		for (int i = 0; i < assignChannelCount; i++) {
			noMiddleManAgentBandwidth += remainedchannels.get(i).getBandwidth();
		}
		if (noMiddleManAgentBandwidth / agentWeightCount > NORMAL_BANDWIDTH_FOR_EACH_AGENT) {
			mtLog.debug("NoMiddleMen!!!Because each agent can send '" + noMiddleManAgentBandwidth / agentWeightCount + "' byte if dosen't have any middle man");
			return true;
		}
		mtLog.debug("checking 'canAgentHaveNormalCommunicationWithOutMiddleMan' false");
		return false;
	}

	private void fullDebug() {
		for (MessageAgent messageAgent : messageAgents) {
			for (RadioChannel radioChannel : messageAgent.getSendChannels()) {
				mtLog.debug(messageAgent + " can send '" + radioChannel.getSendByteLimit(messageAgent) + "' bytes on channel:'" + radioChannel + "'");
			}
		}
	}

	public MessageConfig getMessageConfig() {
		return messageConfig;
	}

	public MessageAgent getMine() {
		return mine;
	}

	public void sendMessage(MessageBuffer messages) {

		if (model.time() < agent.getConfig().getIntValue(KernelConstants.IGNORE_AGENT_COMMANDS_KEY))
			return;
		int idx = unusedAgent.indexOf(mine);
		if (idx >= 0 && model.time() % unusedAgent.size() != idx)
			return;

		// Add Noisy messages
		LinkedList<MessageBlock> selectedMessages = new LinkedList<MessageBlock>();
		if (!getMine().isMiddleMan())
			addNoisyMessages(messages);
		for (RadioChannel radioChannel : mine.getSendChannels()) {
			mcLog.debug("want to send in " + radioChannel + " Messages in the Message buffer " + "(size:" + messages.getMessages().values().size() + ") " + messages);
			if (type == Type.LowComunication)
				speak(radioChannel, selector.pack(messages, radioChannel.getBandwidth(), selectedMessages, false));
			else
				speak(radioChannel, selector.pack(messages, radioChannel.getSendByteLimit(mine), selectedMessages, true));
		}

	}

	private void addNoisyMessages(MessageBuffer messages) {
		ArrayList<MessageBlock> noisyMessages = noisyMessageDatabase.getAndRemoveNoisyMessages(model.time());
		mcLog.debug("Noisy Messages:" + noisyMessages);
		for (MessageBlock messageBlock : noisyMessages) {
			// messageBlock.setPriority(messageBlock.getPriority());//messages
			messages.addWithoutUpdate(messageBlock);
		}
	}

	public void sayMessage(MessageBuffer messages) {
		mcLog.trace("say MessageBuffer:",messages);
		for (VoiceChannel voiceChannel : getMessageConfig().voiceChannels().values()) {
			speak(voiceChannel, selector.selectingForSayPackage(messages, voiceChannel.getMessagesSize()));
		}
	}

//	private void speak(VoiceChannel voiceChannel, LinkedList<MessageBlock> messageBlocks) {
//		int bitSize = 0;
//		mcLog.trace("sayed message", messageBlocks);
//		StringBuffer sb = new StringBuffer();
//		for (MessageBlock messageBlock : messageBlocks) {
//			sb.append(messageBlock.hashCode());
//		}
//		mcLog.debug("sayed message", sb);
//		ChannelSystemType channelType = ReadXml.getChannelSystemType(voiceChannel, type);
//		for (MessageBlock messageBlock : messageBlocks) {
//			bitSize += messageBlock.getBitSize(channelType);
//		}
//		SOSBitArray bitArray = new SOSBitArray(bitSize);
//		int bitPosition = 0;
//		for (MessageBlock messageBlock : messageBlocks) {
//			bitArray.set(bitPosition, messageBlock.toBitArray(channelType));
//			bitPosition += messageBlock.getBitSize(channelType);
//		}
//		if (bitSize > 0)
//			speak(voiceChannel, bitArray.toByteArray());
//
//	}

	public void speak(Channel channel, MessagePackage... packages) {
		speak(channel, Arrays.asList(packages));
	}

	private void speak(Channel channel, List<MessagePackage> packages) {
		if (packages.size() > 0) {
//			mcLog.trace("spoke packages to " + channel + " packagesSize=" + packages.size() + " :" + packages);
			mtLog.trace("spoke packages to " + channel + " packagesSize=" + packages.size() + " :" + packages);
			int messageSize = 0;
			StringBuffer sb = new StringBuffer();
			for (MessagePackage messagePackage : packages) {
				sb.append(messagePackage.liteDescription());
				messageSize += messagePackage.getbyteSize();
			}
//			mcLog.debug("spoke packages('" + messageSize + "byte') to " + channel, sb);
//			mtLog.debug("spoke packages('" + messageSize + "byte') to " + channel, sb);
			for (MessagePackage messagePackage : packages) {
//				if (chackCorrectnessEncoding(messagePackage))
					speak(channel, messagePackage.toByteArray());
			}
			mtLog.debug("Sent " + messageSize + " byte to " + channel);
		}
	}

	public boolean chackCorrectnessEncoding(MessagePackage messagePackage) {
		boolean isvalid = false;
		ArrayList<MessageBlock> decodedMessage = null;
		ChannelSystemType channelType=messagePackage.getChannelType();
		mcLog.trace("channel type:"+channelType+" "+messagePackage);
		mcLog.heavyTrace("header size:"+ReadXml.getValidChannelMessages(channelType).bitSize());
		try {
			SOSBitArray bits = messagePackage.toBitArray();
			mcLog.heavyTrace(bits);
			byte[] content = bits.toByteArray();
			decodedMessage = SOSAgent.currentAgent().messageHandler.decode(content, channelType);
			isvalid = decodedMessage.containsAll(messagePackage.getAllMessageBlocks());
		} catch (Exception e) {
			mcLog.error(e);
		}
		if (!isvalid) {
			mcLog.error("Failed encoding....");
			mcLog.trace(messagePackage.getAllMessageBlocks().toString());
			mcLog.heavyTrace("====bits===========");
			for (MessageBlock b : messagePackage.getAllMessageBlocks()) {
				mcLog.heavyTrace(b+":   "+b.toBitArray(channelType));
			}
			mcLog.trace("==========decoded message");
			mcLog.trace(decodedMessage);
			mcLog.trace("==========diffrent messages");
			ArrayList<MessageBlock> list = new ArrayList<MessageBlock>( messagePackage.getAllMessageBlocks());
			list.removeAll(decodedMessage);
			mcLog.trace(list);
		}
		return isvalid;
	}

	private void speak(Channel channel, byte[] message) {
		if (message.length > 0) {
			agent.speak(channel.getChannelId(), message);
			// mtLog.debug("+Sending " + message.length + " bytes on channel:" + channel);
		}
	}

	public void setChannels() {
		if (model.time() >= agent.getConfig().getIntValue(KernelConstants.IGNORE_AGENT_COMMANDS_KEY)) {
			subscribe(getMine().getSubscribeChannels());
		}
	}

	private void subscribe(ArrayList<RadioChannel> subscribeChannels) {
		if (subscribeChannels.size() == 0) {
			mtLog.debug("No channel Subscribed");
			return;
		}
		int[] channels = new int[subscribeChannels.size()];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = subscribeChannels.get(i).getChannelId();
		}
		agent.subscribe(channels);

		mtLog.debug("Subscribed channels:");
		mtLog.logln(channels);
	}

	public int getNormalMessageDelay() {
		switch (type) {
		case NoComunication:
		case LowComunication:
			return -1;
		case NoMiddleMan:
			return 1;
		}
		if (mine.isMiddleMan())
			return 1;
		return 2;
	}

	public boolean isUsefulCenter(Center center) {
		for (MessageAgent element : messageMiddleManAgents) {
			if (element.getMe().equals(center) && !element.isUnusedAgent())
				return true;

		}
		return false;

	}

}
