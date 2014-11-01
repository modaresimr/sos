package sos.base.message.structure.blocks;

import sos.base.message.structure.MessageConstants;

/**
 *
 * @author Ali
 *
 */
@Deprecated
public class ReceiveMessageBlock extends AbstractMessageBlock implements MessageConstants {
	/*
	private final StandardEntity sender;
	private final Channel channel;

	public ReceiveMessageBlock(StandardEntity sender, Channel channel, SOSBitArray bitArray, int bitPosition, TimeNamayangar tm4) {
		// tm4.start();
		this.sender = sender;
		this.channel = channel;
		setHeader(bitArray.get(bitPosition, HEADER_SIZE));
		bitPosition += HEADER_SIZE;
		DataArrayList xmlData = ReadXml.blocks.get(header).data();
		data = new DataArrayList(xmlData.size());
		for (int i = 0; i < xmlData.size(); i++) {
			addData(xmlData.getKey(i), bitArray.get(bitPosition, xmlData.getValue(i)));
			bitPosition += xmlData.getValue(i);
		}
		// tm4.stop();

	}

	private void setHeader(int index) {
		setHeader(ReadXml.indexToHeader(index));
	}

	public ReceiveMessageBlock(StandardEntity sender, Channel channel, String header) {
		// super(header);
		setHeader(header);
		this.sender = sender;
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "Header:" + header + " Data:" + getData() + " From:" + sender;
	}

	public StandardEntity getSender() {
		return sender;
	}

	public Channel getChannel() {
		return channel;
	}

	@Override
	public int hashCode() {
		// if (hashCode == 0 && (header.equals(MessageXmlConstant.HEADER_HELP) || header.equals(MessageXmlConstant.HEADER_OUCH) || header.equals(MessageXmlConstant.HEADER_UNKNOWN_OUCH))) {
		// hashCode = ("header" + header + sender).hashCode();
		// }
		return super.hashCode();
	}
	*/
}
