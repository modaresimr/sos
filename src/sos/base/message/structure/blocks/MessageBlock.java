package sos.base.message.structure.blocks;

import sos.base.SOSAgent;
import sos.base.message.ReadXml;
import sos.base.message.ReadXml.MessageGroup;
import sos.base.message.structure.MessageConstants;
import sos.base.message.structure.SOSBitArray;

/**
 *
 * @author Ali
 *
 */
public class MessageBlock extends AbstractMessageBlock implements MessageConstants {
	private int priority = 0;
	public static final int PRIORITY_LEVEL_MAX = 2;
	public static final int PRIORITY_LEVEL_MIDDEL = 1;
	public static final int PRIORITY_LEVEL_MIN = 0;

	protected SOSBitArray messageBitArray;
	private boolean resendOnNoise = true;

	public MessageBlock(String header) {
		super(header);
		setPriority(ReadXml.blocks.get(header).getPriority());
	}

	/*
	 * public MessageBlock(String header, int... datas) {
	 * data = new HashMap<String, Integer>();
	 * setHeader(header);
	 * Iterator<String> s = ReadXml.blocks.get(header).data().keySet().iterator();
	 * for (int d : datas) {
	 * if (s.hasNext())
	 * addData(s.next(), d);
	 * else
	 * throw new Error("Data does not match with the XML");
	 * }
	 * if (s.hasNext())
	 * throw new Error("Data does not match with the XML");
	 * }
	 */

	public MessageBlock(AbstractMessageBlock messageBlock) {
		super(messageBlock);
		setPriority(ReadXml.blocks.get(header).getPriority());
	}

	public MessageBlock(String header, DataArrayList data) {
		super(header, data);
		setPriority(ReadXml.blocks.get(header).getPriority());
	}

	@Override
	public void setHeader(String header) {
		this.header = header;
		if (ReadXml.blocks.get(header) == null) {
			throw new Error(header + " is not in message xml---> check it please");
		}
		// DataArrayList xmlData = ReadXml.blocks.get(header).data();
		// for (int i = 0; i < xmlData.size(); i++) {
		// addData(xmlData.getKey(i), -1);
		// }
		data.fillList(-1);

	}

	public int getBitSize(ChannelSystemType channelType) {
		return ReadXml.getBlockSize(header,channelType);
	}
	protected ChannelSystemType lastChannelType;
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
				new Error("value is negetive!!!!!!!!!!!![TO BIT ARRAY]").printStackTrace();
				messageBitArray.set(tempBitPosition, SOSBitArray.makeBit(0, xmlData.getValue(i)));
			}else
				messageBitArray.set(tempBitPosition, SOSBitArray.makeBit(getData(xmlData.getKey(i)), xmlData.getValue(i)));
			tempBitPosition += xmlData.getValue(i);
		}
		return messageBitArray;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public int modBitSizeTo8(ChannelSystemType channelType) {
		return getBitSize(channelType) % 8;
	}

	public void setResendOnNoise(boolean resendOnNoise) {
		this.resendOnNoise = resendOnNoise;
	}

	public boolean isResendOnNoise() {
		return resendOnNoise;
	}

	/**
	 *
	 * @return
	 *         PRIORITY_LEVEL_MIN=0 if less than 5
	 *         PRIORITY_LEVEL_MIDDEL=1 if between 5 and 20
	 *         PRIORITY_LEVEL_MAX=2 if more than 20
	 */
	public int getPriorityLevel() {
		if (priority < 5)
			return PRIORITY_LEVEL_MIN;
		if (priority < 20)
			return PRIORITY_LEVEL_MIDDEL;

		return PRIORITY_LEVEL_MAX;
	}
}
