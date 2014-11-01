package sos.base.message.structure.blocks;

import sos.base.SOSAgent;
import sos.base.entities.Road;
import sos.base.entities.StandardWorldModel;
import sos.base.message.ReadXml;
import sos.base.message.ReadXml.MessageGroup;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.SOSBitArray;

public class DynamicSizeMessageBlock extends MessageBlock {

	protected final SOSBitArray dynamicBits;


	public DynamicSizeMessageBlock(String header, SOSBitArray dynamicBits) {
		super(header);
		this.dynamicBits = dynamicBits;
	}

	public DynamicSizeMessageBlock(String header, DataArrayList data, SOSBitArray dynamicBitArray) {
		super(header,data);
		this.dynamicBits = dynamicBitArray;

	}

	@Override
	public int getBitSize(ChannelSystemType channelType) {
		return super.getBitSize(channelType)+dynamicBits.length();
	}
	@Override
	public SOSBitArray toBitArray(ChannelSystemType channelType) {
		if (messageBitArray != null){
			if(lastChannelType==channelType)
				return messageBitArray;
			SOSAgent.currentAgent().sosLogger.messageContent.debug("lastchanneltype is different!!!!!"+lastChannelType+" new chaneltype"+channelType);
		}
		lastChannelType=channelType;
		MessageGroup messageGroup = ReadXml.getValidChannelMessages(channelType);
		
		messageBitArray = new SOSBitArray(getBitSize(channelType));
		int tempBitPosition = 0;

		messageBitArray.set(tempBitPosition, SOSBitArray.makeBit(messageGroup.headerToIndex(header), messageGroup.bitSize()));
		tempBitPosition += messageGroup.bitSize();

		DataArrayList xmlData = ReadXml.blocks.get(header).data();

		for (int i = 0; i < xmlData.size(); i++) {
			if(xmlData.getValue(i)<0){
				new Error("value is negetive").printStackTrace();
			}
			messageBitArray.set(tempBitPosition, SOSBitArray.makeBit(getData(xmlData.getKey(i)), xmlData.getValue(i)));
			tempBitPosition += xmlData.getValue(i);
		}

		messageBitArray.set(tempBitPosition, dynamicBits);
		return messageBitArray;
	}


	public static int getDynamicBitSize(String header,DataArrayList datas,StandardWorldModel model){
		if(header.equalsIgnoreCase(MessageXmlConstant.HEADER_ROAD_STATE)){
			Road road=model.roads().get(datas.get(MessageXmlConstant.DATA_ROAD_INDEX));
			return road.getWorldGraphEdgesSize();
		}
		if(header.equalsIgnoreCase(MessageXmlConstant.HEADER_AGENT_TO_EDGES_REACHABLITY_STATE)){
			Road road=model.roads().get(datas.get(MessageXmlConstant.DATA_ROAD_INDEX));
			return road.getPassableEdges().length;
		}
		return 0;
	}
}
