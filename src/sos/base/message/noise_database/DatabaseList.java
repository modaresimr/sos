package sos.base.message.noise_database;

import java.util.ArrayList;
import java.util.HashMap;

import sos.base.message.structure.blocks.MessageBlock;

/**
 * 
 * @author Ali
 * 
 */
public class DatabaseList {
	HashMap<Integer, MessageBlock> map = new HashMap<Integer, MessageBlock>();

	public DatabaseList() {
	}

	public void add(MessageBlock messageBlock) {
		map.put(messageBlock.hashCode(), messageBlock);
	}

	public MessageBlock getBlock(int hashCode) {
		return map.get(hashCode);
	}

	public boolean containsBlock(int hashCode) {
		return map.containsKey(hashCode);
	}

	public boolean removeBlock(int hashCode) {
		return removeMessageBlock(hashCode) != null;
	}

	public MessageBlock removeMessageBlock(int hashCode) {
		return map.remove(hashCode);
	}
	public ArrayList<MessageBlock> getRemainingBlocksAndRemove(){
		ArrayList<MessageBlock> blocks=new ArrayList<MessageBlock>(map.values());
		map.clear();
		return blocks;
	}
}
