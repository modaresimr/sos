package sos.base.message.noise_database;

import java.util.ArrayList;

import sos.base.message.structure.blocks.AbstractMessageBlock;
import sos.base.message.structure.blocks.MessageBlock;

/**
 * 
 * @author Ali
 * 
 */
public interface MessageDatabase {
	public void add(MessageBlock messageBlock,int time);
	public boolean checkAndRemove(AbstractMessageBlock messageBlock);
	public ArrayList<MessageBlock> getAndRemoveNoisyMessages(int time);
}
