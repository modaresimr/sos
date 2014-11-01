package sos.base.message.structure.blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sos.base.message.structure.MessageConstants.ChannelSystemType;
import sos.base.message.structure.SOSBitArray;

/**
 *
 * @author Ali
 *
 */
public class MessagePackage {
	private final List<MessageBlock> messageBlocks;
	private int bitSize;
	private final ChannelSystemType channelType;

	public MessagePackage(ChannelSystemType channelType,List<MessageBlock> messageBlocks) {
		this.channelType = channelType;
		this.messageBlocks = messageBlocks;
		bitSize=0;
		for (MessageBlock messageBlock : messageBlocks) {
			bitSize+=messageBlock.getBitSize(channelType);
		}
	}
	public MessagePackage(ChannelSystemType channelType,MessageBlock... blocks) {
		this(channelType,Arrays.asList(blocks));
	}

	public SOSBitArray toBitArray() {
		SOSBitArray bitArray=new SOSBitArray(bitSize);
		int bitPosition=0;
		for (MessageBlock messageBlock : messageBlocks) {
			bitArray.set(bitPosition, messageBlock.toBitArray(channelType));
			bitPosition+=messageBlock.getBitSize(channelType);
		}
		return bitArray;
	}
	public byte[] toByteArray() {
		return toBitArray().toByteArray();
	}
	public int getbyteSize() {
		return (int) Math.ceil(((float) bitSize) / Byte.SIZE);
	}
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		for (MessageBlock block : messageBlocks) {
			sb.append(" "+block.getHeader()+"->bitsize:"+block.getBitSize(channelType));
		}
		return "PackageBitSize:"+bitSize+" =>"+sb;
	}

	public String liteDescription() {
		StringBuffer sb=new StringBuffer();
		for (MessageBlock block : messageBlocks) {
			sb.append(block.hashCode()+",");
		}
		return "Package[" + sb + "]";
	}
	public ChannelSystemType getChannelType() {
		return channelType;
	}
	
	public List<MessageBlock> getAllMessageBlocks() {
		return Collections.unmodifiableList(messageBlocks);
	}
}
