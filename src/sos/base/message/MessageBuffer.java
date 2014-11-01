package sos.base.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import sos.base.message.structure.MessageConstants;
import sos.base.message.structure.SOSMessageList;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.system.MessageSystem;
import sos.base.util.sosLogger.SOSLoggerSystem;

/**
 *
 * @author Ali
 *
 */
public class MessageBuffer implements MessageConstants {

	private final HashMap<Integer, SOSMessageList<MessageBlock>> messages = new HashMap<Integer, SOSMessageList<MessageBlock>>();
	private final MessageSystem messageSystem;
	private final SOSLoggerSystem messageContentLog;
	private final SOSLoggerSystem messageTransmitLog;
	private final ChannelSystemType channelType;

	public MessageBuffer(ChannelSystemType channelType,MessageSystem messageSystem) {
		this.messageSystem = messageSystem;
		this.channelType = channelType;
		this.messageContentLog = messageSystem.mcLog;
		this.messageTransmitLog = messageSystem.mtLog;
	}

	/**
	 * <b>IMPORTANT: this methode add new message block or update exiting message block</b>
	 * When a messageBlock add, this part choose the ArrayList depend on message
	 * Priority
	 */
	public void add(MessageBlock messageBlock) {
		if (checkBlock(messageBlock)) {
			messageSystem.noisyMessageDatabase.checkAndRemove(messageBlock);
			int priority = messageBlock.getPriority();
			if (!messages.containsKey(priority))
				messages.put(priority, new SOSMessageList<MessageBlock>(priority));
			if (messages.get(priority).updateAdd(messageBlock)) {
//				messageBlock.toBitArray(getChannelType());
				 messageContentLog.trace(messageBlock + "Added to box " +channelType+" |||caller="+new Error().getStackTrace()[1]);
			} else {
				messageContentLog.debug(messageBlock + "is duplicated in the MessageList " + priority);
			}
		}
	}

	/**
	 * When a messageBlock add, this part choose the ArrayList depend on message
	 * Priority
	 */
	public void addWithoutUpdate(MessageBlock messageBlock) {
		if (checkBlock(messageBlock)) {
			messageSystem.noisyMessageDatabase.checkAndRemove(messageBlock);
			int priority = messageBlock.getPriority();
			if (!messages.containsKey(priority))
				messages.put(priority, new SOSMessageList<MessageBlock>(priority));
			if (messages.get(priority).add(messageBlock)) {
				messageBlock.toBitArray(getChannelType());
				// messageContentLog.trace(messageBlock + "Added to box " +priority);
			} else {
				messageContentLog.debug(messageBlock + "is duplicated in the MessageList " + priority);
			}
		}
	}

	private boolean checkBlock(MessageBlock messageBlock) {

		if (messageBlock.getHeader() == null) {
			messageTransmitLog.error("You must add a header to a block", new Error("You must add a header to a block"));
			return false;
		}
//		if (messageBlock.getHeader().equals(MessageXmlConstant.HEADER_ROAD_STATE))
//			return true;
		DataArrayList xmlData = ReadXml.blocks.get(messageBlock.getHeader()).data();
		for (int i = 0; i < xmlData.size(); i++) {
			if (messageBlock.getData(xmlData.getKey(i)) < 0) {
				messageTransmitLog.error(new Error("You must add all data----" + xmlData.getKey(i) + " didn't fill out in Block " + messageBlock.getHeader() + "!!!!current value="+messageBlock.getData(xmlData.getKey(i)) ));
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		ArrayList<Integer> pkeys = getPriorityKeys();
		for (Integer k : pkeys) {
			sb.append("["+k+":"+messages.get(k)+"],");
		}
		return sb.toString();
	}

	public HashMap<Integer, SOSMessageList<MessageBlock>> getMessages() {
		return messages;
	}

	public ArrayList<Integer> getPriorityKeys() {
		ArrayList<Integer> tmp = new ArrayList<Integer>(messages.keySet());
		Collections.sort(tmp,new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o2-o1;
			}
		});
		return tmp;
	}

	public void clear() {
		for (Entry<Integer, SOSMessageList<MessageBlock>> p_List : messages.entrySet()) {
			p_List.getValue().clear();
		}
//		messages.clear();
	}

	public ChannelSystemType getChannelType() {
		return channelType;
	}
}
