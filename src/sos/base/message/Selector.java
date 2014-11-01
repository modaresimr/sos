package sos.base.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import sos.base.message.structure.MessageConstants.ChannelSystemType;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.blocks.MessagePackage;
import sos.base.message.system.MessagePartitioningMakeing;
import sos.base.message.system.MessageSystem;

/**
 *
 * @author Ali
 *
 */
public class Selector {
	// private static final int CRITICAL_PACKAGE_BYTE_SIZE = 10000000;

	private final ArrayList<ArrayList<Integer>> messagePartition;
	private final MessageSystem messageSystem;
//	private final ChannelSystemType channelType;


	public Selector(MessageSystem messageSystem) {
		this.messageSystem = messageSystem;
		messagePartition = new MessagePartitioningMakeing().getPartition();
		messageSystem.mtLog.debug("Partitioned:" + messagePartition);
	}

	public ArrayList<MessagePackage> pack(MessageBuffer messageBuffer, int byteSize,LinkedList<MessageBlock> selectedBlocks,boolean doDuplicate) {
		ChannelSystemType channelType=messageBuffer.getChannelType();
		messageSystem.mtLog.info("Packaging messages...(I remained 10%("+(int) (byteSize * .1f)+") of each channel for some important messages)");
		ArrayList<MessagePackage> packages = packageMaker(channelType,selecting(messageBuffer, selectedBlocks, (int) (byteSize * .9f)));// we use .1 of size for duplicating//i keep 10% of each channel for duplicating some important messages for reducing probably of noise
		int remaindedSize = byteSize;
		for (MessagePackage messagePackage : packages) {
			remaindedSize -= messagePackage.getbyteSize();
		}
		messageSystem.mtLog.debug("Remainded message size for duplacating="+remaindedSize);
		messageSystem.noisyMessageDatabase.addAll(selectedBlocks, messageSystem.model.time());
		if(doDuplicate)
			packages.addAll(packageMaker(channelType,selectingForDuplicate(channelType,selectedBlocks, remaindedSize)));
		return packages;
	}

	private HashMap<Integer, LinkedList<MessageBlock>> selectingForDuplicate(ChannelSystemType channelType,LinkedList<MessageBlock> selectedBlocks, int remaindedSize) {
		int remaindedBitSize = remaindedSize * Byte.SIZE;
		HashMap<Integer, LinkedList<MessageBlock>> bitPackList = new HashMap<Integer, LinkedList<MessageBlock>>();
		for (int i = 0; i < 8; i++) {
			bitPackList.put(i, new LinkedList<MessageBlock>());
		}
		for (MessageBlock messageBlock : selectedBlocks) {
			if (remaindedBitSize < messageBlock.getBitSize(channelType))
				break;
			bitPackList.get(messageBlock.modBitSizeTo8(channelType)).add(messageBlock);
			remaindedBitSize -= messageBlock.getBitSize(channelType);
		}
		return bitPackList;
	}

	public ArrayList<MessagePackage> packageMaker(ChannelSystemType channelType,HashMap<Integer, LinkedList<MessageBlock>> bitPackList) {
		ArrayList<MessagePackage> messagePackages = new ArrayList<MessagePackage>();
		for (ArrayList<Integer> part : messagePartition) {
			boolean canPartMakeAPackage = true;
			while (canPartMakeAPackage) {
				for (Integer subpart : part) {
					if (bitPackList.get(subpart).size() < part.lastIndexOf(subpart) - part.indexOf(subpart) + 1) {// "part.lastIndexOf(subpart)-part.indexOf(subpart)+1" give us the count of subpart in part
						canPartMakeAPackage = false;
						break;
					}
				}
				if (canPartMakeAPackage) {
					ArrayList<MessageBlock> messageBlocks = new ArrayList<MessageBlock>();
					// int packageSize = 0;
					for (Integer subpart : part) {
						// packageSize += bitPackList.get(subpart).getLast().getBitSize();
						// if (packageSize <= CRITICAL_PACKAGE_BYTE_SIZE * Byte.SIZE) {
						MessageBlock m = bitPackList.get(subpart).removeLast();
						messageBlocks.add(m);
						// } else
						// break;
					}
					messagePackages.add(new MessagePackage(channelType,messageBlocks));
				}
			}
		}
		ArrayList<MessageBlock> remaindedMessages = new ArrayList<MessageBlock>();
		for (LinkedList<MessageBlock> blocks : bitPackList.values()) {
			for (MessageBlock messageBlock : blocks) {
				remaindedMessages.add(messageBlock);
			}
		}
		messagePackages.add(new MessagePackage(channelType,remaindedMessages));

		return messagePackages;
	}

	private HashMap<Integer, LinkedList<MessageBlock>> selecting(MessageBuffer messageBuffer, LinkedList<MessageBlock> selectedBlocks, int byteSize) {
		ChannelSystemType channelType=messageBuffer.getChannelType();
		HashMap<Integer, LinkedList<MessageBlock>> bitPackList = new HashMap<Integer, LinkedList<MessageBlock>>();
		for (int i = 0; i < 8; i++) {
			bitPackList.put(i, new LinkedList<MessageBlock>());
		}
		int remaindSize = byteSize * Byte.SIZE;
		ArrayList<Integer> priorityKeys = messageBuffer.getPriorityKeys();
		for (Integer element : priorityKeys) {
			while (true) {
				MessageBlock m = messageBuffer.getMessages().get(element).removeMessage();
				if (m == null || remaindSize - m.getBitSize(channelType) < 0)
					break;
				selectedBlocks.add(m);
				remaindSize -= m.getBitSize(channelType);
				bitPackList.get(m.modBitSizeTo8(channelType)).add(m);

			}
		}
		return bitPackList;
	}

	public LinkedList<MessageBlock> selectingForSay(MessageBuffer messageBuffer, int byteSize) {
		ChannelSystemType channelType=messageBuffer.getChannelType();
		LinkedList<MessageBlock> selectedBlocks = new LinkedList<MessageBlock>();
		int remaindSize = byteSize * Byte.SIZE;
		ArrayList<Integer> priorityKeys = messageBuffer.getPriorityKeys();
		for (Integer element : priorityKeys) {
			while (true) {
				MessageBlock m = messageBuffer.getMessages().get(element).seeLastMessage();
				if (m == null || remaindSize - m.getBitSize(channelType) < 0)
					break;
				selectedBlocks.add(messageBuffer.getMessages().get(element).removeMessage());
				remaindSize -= m.getBitSize(channelType);

			}
		}
		return selectedBlocks;
	}
	public MessagePackage selectingForSayPackage(MessageBuffer messageBuffer, int byteSize) {
		ChannelSystemType channelType=messageBuffer.getChannelType();
		LinkedList<MessageBlock> selectedBlocks = new LinkedList<MessageBlock>();
		int remaindSize = byteSize * Byte.SIZE;
		ArrayList<Integer> priorityKeys = messageBuffer.getPriorityKeys();
		for (Integer element : priorityKeys) {
			while (true) {
				MessageBlock m = messageBuffer.getMessages().get(element).seeLastMessage();
				if (m == null || remaindSize - m.getBitSize(channelType) < 0)
					break;
				selectedBlocks.add(messageBuffer.getMessages().get(element).removeMessage());
				remaindSize -= m.getBitSize(channelType);

			}
		}
		
		return new MessagePackage(channelType,selectedBlocks);
	}
}
