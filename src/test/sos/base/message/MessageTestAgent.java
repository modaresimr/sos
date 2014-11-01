package test.sos.base.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;

import rescuecore2.standard.entities.StandardEntityURN;
import sos.base.PlatoonAgent;
import sos.base.SOSConstant.AgentType;
import sos.base.SOSConstant.GraphEdgeState;
import sos.base.entities.Human;
import sos.base.entities.Road;
import sos.base.message.ReadXml;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.AbstractMessageBlock;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.blocks.DynamicSizeMessageBlock;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.blocks.MessagePackage;
import sos.base.message.structure.channel.Channel;
import sos.base.precompute.PreCompute;
import sos.base.util.SOSActionException;
import sos.base.util.sosLogger.SOSLoggerSystem;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;
import sos.base.worldGraph.WorldGraphEdge;
import sos.tools.GraphEdge;

public class MessageTestAgent extends PlatoonAgent<Human> implements MessageXmlConstant {

	private SOSLoggerSystem mcLog;
	private SOSLoggerSystem mtLog;

	@Override
	protected void preCompute() {
		//super.preCompute();
		simplePreCompute();

		mcLog = sosLogger.messageContent;
		mcLog.setOutputType(OutputType.Both);
		mtLog = sosLogger.messageTransmit;
		mtLog.setOutputType(OutputType.Both);

		LinkedList<MessageBlock> messageBlocks = makeBlocks2();
		Channel channel = messageSystem.getMessageConfig().getAllChannels().get(0);
		ChannelSystemType channelType = ReadXml.getChannelSystemType(channel, Type.CenteralMiddleMan);
		messageSystem.speak(channel,new MessagePackage(channelType, messageBlocks));
//		messageSystem.chackCorrectnessEncoding(new MessagePackage(channelType, messageBlocks));
//		SOSBitArray bitArray = encode(messageBlocks, channelType);
//		SOSBitArray bitArray = encode2(messageBlocks, channelType);
//		mcLog.trace(bitArray);
//		byte[] b = bitArray.toByteArray();
//		ArrayList<MessageBlock> received = decode(b, channelType);
//		isEqual(received,messageBlocks);
	}

	private SOSBitArray encode2(LinkedList<MessageBlock> messageBlocks, ChannelSystemType channelType) {

		return new MessagePackage(channelType, messageBlocks).toBitArray();
	}

	private void isEqual(ArrayList<MessageBlock> received, LinkedList<MessageBlock> messageBlocks) {
		if(!received.containsAll(messageBlocks))
			throw new AssertionError("not contains all messages");
		mtLog.consoleInfo("Test Success...");
	}

	private SOSBitArray encode(LinkedList<MessageBlock> messageBlocks, ChannelSystemType channelType) {
		int bitSize = 0;
		mcLog.trace("sayed message", messageBlocks);
		StringBuffer sb = new StringBuffer();
		for (MessageBlock messageBlock : messageBlocks) {
			sb.append(messageBlock.hashCode());
		}
		mcLog.debug("sayed message", sb);
		for (MessageBlock messageBlock : messageBlocks) {
			bitSize += messageBlock.getBitSize(channelType);
		}
		SOSBitArray bitArray = new SOSBitArray(bitSize);
		int bitPosition = 0;
		for (MessageBlock messageBlock : messageBlocks) {
			bitArray.set(bitPosition, messageBlock.toBitArray(channelType));
			bitPosition += messageBlock.getBitSize(channelType);
		}
		return bitArray;
	}
	private LinkedList<MessageBlock> makeBlocks2() {
		LinkedList<MessageBlock> messageBlocks = new LinkedList<MessageBlock>();
		for (int i = 0; i < 100; i++) {

		MessageBlock messageBlock = new MessageBlock(HEADER_FIRE);
		messageBlock.addData(DATA_BUILDING_INDEX,619 );
		messageBlock.addData(DATA_FIERYNESS, 1);
		messageBlock.addData(DATA_HEAT, 37);
		messageBlock.addData(DATA_TIME, 4);
		messageBlocks.add(messageBlock);

		messageBlock = new MessageBlock(HEADER_FIRE);
		messageBlock.addData(DATA_BUILDING_INDEX,588);
		messageBlock.addData(DATA_FIERYNESS, 1);
		messageBlock.addData(DATA_HEAT, 40);
		messageBlock.addData(DATA_TIME, 4);
		messageBlocks.add(messageBlock);

		messageBlock = new MessageBlock(HEADER_FIRE);
		messageBlock.addData(DATA_BUILDING_INDEX,580 );
		messageBlock.addData(DATA_FIERYNESS, 0);
		messageBlock.addData(DATA_HEAT, 1);
		messageBlock.addData(DATA_TIME, 5);
		messageBlocks.add(messageBlock);

		}

		return messageBlocks;
	}
	private LinkedList<MessageBlock> makeBlocks() {
		LinkedList<MessageBlock> messageBlocks = new LinkedList<MessageBlock>();
		for (Road rd : model().roads()) {
			boolean isAllOpen = true;
			for (Short ind : rd.getGraphEdges()) {
				GraphEdge ge = model().graphEdges().get(ind);
				if (ge instanceof WorldGraphEdge && ge.getState() != GraphEdgeState.Open) {
					isAllOpen = false;
					break;
				}
			}
			if (isAllOpen) {
				MessageBlock messageBlock = new MessageBlock(HEADER_OPEN_ROAD);
				messageBlock.addData(DATA_ROAD_INDEX, rd.getRoadIndex());
				if(checkBlock(messageBlock))
					messageBlocks.add(messageBlock);
			} else {
				SOSBitArray states = new SOSBitArray(rd.getWorldGraphEdgesSize());
				for (int i = 0; i < rd.getWorldGraphEdgesSize(); i++) {
					states.set(i, random.nextBoolean());
				}
				MessageBlock messageBlock = new DynamicSizeMessageBlock(HEADER_ROAD_STATE, states);
				messageBlock.addData(DATA_ROAD_INDEX, rd.getRoadIndex());
				if(checkBlock(messageBlock))
					messageBlocks.add(messageBlock);
			}
		}
		for (int i = 0; i < 1000; i++) {

			messageBlock = new MessageBlock(HEADER_SENSED_CIVILIAN);
			messageBlock.addData(DATA_ID, random.nextInt(1<<30));
			messageBlock.addData(DATA_AREA_INDEX, random.nextInt(model().areas().size()));
			messageBlock.addData(DATA_HP, random.nextInt(10000) / 322);
			int damage = random.nextInt(1500);
			if (damage > 1200)
				damage = 1200;
			messageBlock.addData(DATA_DAMAGE, damage / 10);
			int buried = random.nextInt(128);
			if (buried > 126)
				buried = 126;
			messageBlock.addData(DATA_BURIEDNESS, buried);
			messageBlock.addData(DATA_TIME, random.nextInt(300));
			boolean isReallyReachable;
			isReallyReachable = false;

			messageBlock.addData(DATA_IS_REALLY_REACHABLE, isReallyReachable ? 1 : 0);
			if(checkBlock(messageBlock))
			messageBlocks.add(messageBlock);
		}
		for (int i = 0; i < 100; i++) {
			messageBlock = new MessageBlock(HEADER_IM_HEALTHY_AND_CAN_ACT);
			messageBlock.addData(DATA_AGENT_INDEX, random.nextInt(model().agents().size()));
			if(checkBlock(messageBlock))
			messageBlocks.add(messageBlock);
		}
		Collections.shuffle(messageBlocks,random);
		return messageBlocks;
	}
	private boolean checkBlock(MessageBlock messageBlock) {

		if (messageBlock.getHeader() == null) {
			mtLog.error("You must add a header to a block", new Error("You must add a header to a block"));
			return false;
		}
//		if (messageBlock.getHeader().equals(MessageXmlConstant.HEADER_ROAD_STATE))
//			return true;
		DataArrayList xmlData = ReadXml.blocks.get(messageBlock.getHeader()).data();
		for (int i = 0; i < xmlData.size(); i++) {
			if (messageBlock.getData(xmlData.getKey(i)) < 0) {
				mtLog.error(new Error("You must add all data----" + xmlData.getKey(i) + " didn't fill out in Block " + messageBlock.getHeader() + "!!!!current value="+messageBlock.getData(xmlData.getKey(i)) ));
				return false;
			}
		}

		return true;
	}


	private ArrayList<MessageBlock> decode(byte[] content, ChannelSystemType channelType) {
		ArrayList<MessageBlock> received=new ArrayList<MessageBlock>();
		SOSBitArray bitArray = new SOSBitArray(content);
		int bitPosition = 0;
		int headerSize = ReadXml.getValidChannelMessages(channelType).bitSize();
		while (bitPosition + headerSize <= bitArray.length()) {
			int headerIndex = bitArray.get(bitPosition, headerSize);
			if (headerIndex == 0) {
				//if(bitArray.get(bitPosition, bitArray.length()-bitPosition)==0)
				break;
			}
			String header = ReadXml.getValidChannelMessages(channelType).indexToHeader(headerIndex);
			if (header == null)
				break;
			//			bitMessageLog.debug("Header:" + header + "(" + headerIndex + ")");
			bitPosition += headerSize;

			DataArrayList xmlData = ReadXml.blocks.get(header).data();
			DataArrayList data = new DataArrayList(xmlData.size());
			mtLog.trace("Header:" + header + "(" + headerIndex + ") has this setting:" + "(" + xmlData.size() + ")" + xmlData);
			//			bitMessageLog.trace("Header:" + header + "(" + headerIndex + ") has this setting:" + "(" + xmlData.size() + ")" + xmlData);

			for (int i = 0; i < xmlData.size(); i++) {
				data.put(xmlData.getKey(i), bitArray.get(bitPosition, xmlData.getValue(i)));
				//				bitMessageLog.heavyTrace("i:" + i + " key:" + xmlData.getKey(i) + "=" + bitArray.get(bitPosition, xmlData.getValue(i)) + " bitsize=" + xmlData.getValue(i));
				bitPosition += xmlData.getValue(i);
			}
			int messageBlockHashcode = AbstractMessageBlock.getHash(header, data);
			SOSBitArray dynamicBitArray = null;
			int dynamicBitSize = DynamicSizeMessageBlock.getDynamicBitSize(header, data, model());
			if (dynamicBitSize > 0) {
				dynamicBitArray = bitArray.getBit(bitPosition, dynamicBitSize);
				//				bitMessageLog.heavyTrace("dynamic bit size:" + dynamicBitSize + " bitarray:" + dynamicBitArray);
			}
			received.add(new DynamicSizeMessageBlock(header,data,bitArray));
			bitPosition += dynamicBitSize;
			mcLog.trace("hash:" + messageBlockHashcode + " header:" + header + " data:" + data + " dynamicBitArray:" + dynamicBitArray);
		}
		return received;
	}

	private void simplePreCompute() {
		sosLogger.info("ID=" + getID()); // Ali
		model().precompute();
		PreCompute<Human> precompute = new PreCompute<Human>(this);
		precompute.splashLogStart("Started Precompute...");
		precompute.mapRecognizer();
		precompute.messagePrecompute();
		precompute.findMaxBlockadesForBuilding();//get 1m in VC//3mb in berlin
		precompute.setReachablityPrecomputes();//get 4.5m in VC//5mb in berlin
		precompute.computeRoadsBasePoint();
		precompute.computeEdgesNewProperties();
		precompute.createWorldGraphNodes();
		precompute.createWorldGraphEdges();
		precompute.createVirtualGraphEdges();
		precompute.splashLogEnd("Finished Precompute...");
	}

	@Override
	protected void think() throws SOSActionException {
		super.think();
	}

	@Override
	protected void thinkAfterExceptionOccured() throws SOSActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public AgentType type() {
		// TODO Auto-generated method stub
		return AgentType.AmbulanceTeam;
	}

	@Override
	protected EnumSet<StandardEntityURN> getRequestedEntityURNsEnum() {
		return EnumSet.of(StandardEntityURN.POLICE_FORCE, StandardEntityURN.AMBULANCE_TEAM, StandardEntityURN.FIRE_BRIGADE);
	}

}
