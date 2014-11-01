package sos.base.message.noise_database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import sos.base.message.structure.blocks.AbstractMessageBlock;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.system.MessageSystem;
import sos.base.util.sosLogger.SOSLoggerSystem;

/**
 * 
 * @author Ali
 * 
 */
public class NoisyMessageDatabase implements MessageDatabase {
	final static int NUM_CYCLE_SAVED_MESSAGES = 4;
	HashMap<Integer, DatabaseList> timeDataList = new HashMap<Integer, DatabaseList>();
	private final MessageSystem messageSystem;
	private final SOSLoggerSystem mtLog;
	
	public NoisyMessageDatabase(MessageSystem messageSystem) {
		this.messageSystem = messageSystem;
		this.mtLog = messageSystem.mtLog;
		for (int i = 0; i < NUM_CYCLE_SAVED_MESSAGES; i++) {
			timeDataList.put(i, new DatabaseList());
		}
	}

	@Override
	public void add(MessageBlock messageBlock, int time) {
		if (messageBlock.isResendOnNoise()){
			int delay = messageSystem.getNormalMessageDelay();
			timeDataList.get((time + (NUM_CYCLE_SAVED_MESSAGES-(2-delay) - messageBlock.getPriorityLevel())) % NUM_CYCLE_SAVED_MESSAGES).add(messageBlock);
		}
	}

	@Override
	public boolean checkAndRemove(AbstractMessageBlock messageBlock) {

		for (int i = 0; i < NUM_CYCLE_SAVED_MESSAGES; i++) {
			try {
				if (timeDataList.get(i).removeBlock(messageBlock.hashCode()))
					return true;
			} catch (Exception e) {// XXX No need!!! but kar az mohkam kari eib nemikone!!!!!!!
				mtLog .fatal("message database exception", e);
				mtLog .debug("timeDataList:" + timeDataList + " messageBlock:" + messageBlock);
			}
		}
		return false;
	}

	public boolean checkAndRemove(int messageBlockHashCode) {
		
		for (int i = 0; i < NUM_CYCLE_SAVED_MESSAGES; i++) {
			try {
				if (timeDataList.get(i).removeBlock(messageBlockHashCode))
					return true;
			} catch (Exception e) {// XXX No need!!! but kar az mohkam kari eib nemikone!!!!!!!
				mtLog .fatal("message database exception", e);
			}
		}
		return false;
	}

	@Override
	public ArrayList<MessageBlock> getAndRemoveNoisyMessages(int time) {
		return timeDataList.get(time % NUM_CYCLE_SAVED_MESSAGES).getRemainingBlocksAndRemove();
	}

	public void addAll(Collection<MessageBlock> blocks, int time) {
		for (MessageBlock messageBlock : blocks) {
			add(messageBlock, time);
		}
	}

}
