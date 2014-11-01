package sos.base.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import sos.base.message.structure.MessageConstants.ChannelSystemType;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.blocks.MessagePackage;

/**
 *
 * @author Ali
 *
 */
public class oldSelector {
	private static ArrayList<ArrayList<Integer>> messagePartition;
	private static ArrayList<StringBuffer> messageStringPartition;

	public oldSelector() {
		if (messagePartition == null) {
			partitioning();
		}
	}

	public ArrayList<MessagePackage> pack(ChannelSystemType channelType,MessageBuffer messageBuffer, int byteSize) {
		return packSelection(channelType,selecting(channelType,messageBuffer, byteSize));

	}

	private ArrayList<MessagePackage> packSelection(ChannelSystemType channelType,HashMap<Integer, LinkedList<MessageBlock>> bitPackList) {
		ArrayList<MessagePackage> messagePackages = new ArrayList<MessagePackage>();
		for (ArrayList<Integer> part : messagePartition) {
			boolean canPartMakeAPackage = true;
			while (canPartMakeAPackage) {
				for (Integer subpart : part) {
					if (bitPackList.get(subpart).size() < part.lastIndexOf(subpart) - part.indexOf(subpart) + 1) {//"part.lastIndexOf(subpart)-part.indexOf(subpart)+1" give us the count of subpart in part
						canPartMakeAPackage = false;
						break;
					}
				}
				if (canPartMakeAPackage) {
					ArrayList<MessageBlock> messageBlocks = new ArrayList<MessageBlock>();
					for (Integer subpart : part) {
						messageBlocks.add(bitPackList.get(subpart).removeLast());
					}
					messagePackages.add(new MessagePackage(channelType,messageBlocks));
				}
			}
		}
		return messagePackages;
	}

	private HashMap<Integer, LinkedList<MessageBlock>> selecting(ChannelSystemType channelType,MessageBuffer messageBuffer, int byteSize) {
		HashMap<Integer, LinkedList<MessageBlock>> bitPackList = new HashMap<Integer, LinkedList<MessageBlock>>();
		for (int i = 0; i < 8; i++) {
			bitPackList.put(i, new LinkedList<MessageBlock>());
		}
		//		ArrayList<MessageBlock> selectingBlocks = new ArrayList<MessageBlock>();
		int remaindSize = byteSize * Byte.SIZE;
		for (Integer element : messageBuffer.getPriorityKeys()) {
			while (true) {
				MessageBlock m = messageBuffer.getMessages().get(element).removeMessage();
				if (m == null || remaindSize - m.getBitSize(channelType) < 0)
					break;
				remaindSize -= m.getBitSize(channelType);
				bitPackList.get(m.modBitSizeTo8(channelType)).add(m);

			}
		}
		return bitPackList;
	}

	private static void partitioning() {
		messagePartition = new ArrayList<ArrayList<Integer>>();
		messagePartition.add(new ArrayList<Integer>(Arrays.asList(0)));//it is 8 % 8
		messagePartitioning(8, 7, new ArrayList<Integer>());
//		Collections.sort(messagePartition, new Comparator<ArrayList<?>>() {
//			@Override
//			public int compare(ArrayList<?> o1, ArrayList<?> o2) {
//				return o1.size() - o2.size();
//			}
//		});
	}

	private static void messagePartitioning(int n, int max, ArrayList<Integer> part) {
		if (max == 0) {
			if(!messagePartition.contains(part)){
				StringBuffer partString=new StringBuffer();
				for (int i = 0; i < part.size(); i++)
					partString.append(part.get(i));

				int sum=0;
				for (int i = 0; i < part.size()-1; i++) {
					sum+=part.get(i);
					if(sum==8)
						return;
				}

				for (int i = 0; i < part.size()-1; i++) {
					sum+=part.get(i);
					if(sum==8)
						return;
				}

				messageStringPartition.add(partString);
				messagePartition.add(part);
			}
			return;
		}
		for (int j = max; j > 0; j--) {
			ArrayList<Integer> cloned = new ArrayList<Integer>(part){
				private static final long serialVersionUID = 1L;
				@Override
				public boolean equals(Object o) {
					if (o instanceof ArrayList<?>) {
						ArrayList<?> o2 = (ArrayList<?>) o;
						if(size()!=o2.size())
							return false;
						for (Integer integer : this) {
							if(!o2.contains(integer))
								return false;
						}
						return true;
					}
					return false;
				}
			};
			cloned.add(j%8);
			if(j%8!=0)
				messagePartitioning(n - j, Math.min(j, n - j), cloned);
		}
	}
}
