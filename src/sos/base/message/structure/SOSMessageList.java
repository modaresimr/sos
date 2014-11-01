package sos.base.message.structure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import sos.base.message.structure.blocks.SOSMessageBlock;

/**
 *
 * @author Ali
 *
 * @param <T>
 */
public class SOSMessageList<T extends SOSMessageBlock> {
	// private static int MAX_SIZE = 50;
	private static int DEFAULT_SIZE = 50;
	private final HashMap<Integer, T> hashMap;
	private final LinkedList<Integer> keys;
	private final int priority;
//	private static SOSLoggerSystem sosMessageListlogger= new SOSLoggerSystem(null, "sosMessageListlogger", true, OutputType.File,true);
	@SuppressWarnings("unused")
	private final int size;

	public SOSMessageList(int priority) {
		this(priority, DEFAULT_SIZE);
	}

	public SOSMessageList() {
		this(0);
	}

	public SOSMessageList(int priority, int size) {
		this.priority = priority;
		this.size = size;
		hashMap = new HashMap<Integer, T>(size);
		keys = new LinkedList<Integer>();

	}

	public boolean add(T messageBlock) {
		if (!contains(messageBlock.hashCode())) {
			// if (size() == size)
			// hashMap.remove(keys.removeLast());
			// else if (size() > size)
			// sosMessageListlogger.error("Why Size() is greater tham MAX_SIZE????");
			hashMap.put(messageBlock.hashCode(), messageBlock);
			keys.addFirst(messageBlock.hashCode());

			return true;
		}
		return false;
	}

	public boolean addAll(Collection<? extends T> blocks) {
		for (T messageBlock : blocks) {
			add(messageBlock);
		}
		return true;
	}

	public boolean updateAddAll(Collection<? extends T> blocks) {
		for (T messageBlock : blocks) {
			updateAdd(messageBlock);
		}
		return true;
	}

	public boolean updateAdd(T messageBlock) {
//		if (!contains(messageBlock.hashCode())) {
			// if (keys.size() == size)
			// hashMap.remove(keys.removeLast());
			// else if (size() > size)
			// sosMessageListlogger.error("Why Size() is greater that MAX_SIZE????");
//		}

		if (!contains(messageBlock.hashCode())) {
			keys.addFirst(messageBlock.hashCode());
		}
		hashMap.put(messageBlock.hashCode(), messageBlock);
		return true;
	}

	public void clear() {
		hashMap.clear();
		keys.clear();
	}

	public boolean contains(Integer hashCode) {
		return hashMap.containsKey(hashCode);
	}

	public boolean containsAll(Collection<? extends Integer> c) {
		for (Integer object : c) {
			if (!contains(object))
				return false;
		}
		return true;
	}

	public T get(int hashCode) {
		return hashMap.get(hashCode);
	}

	public boolean isEmpty() {
		return hashMap.isEmpty();
	}

	/*
	 * public Iterator<MessageBlock> iterator() { // TODO Auto-generated method
	 * stub return null; }
	 */
	public T remove(int hashCode) {
		T m = hashMap.remove(hashCode);
		keys.remove(m);
		return m;
	}

	public T removeMessage() {
		try {
			if (size() > 0) {
				return hashMap.remove(keys.removeFirst());
			} else
				return null;
		} catch (Exception e) {// XXX not important
			new Error("message exception").printStackTrace();
			return null;
		}
	}

	/**
	 * It is for saying messages
	 *
	 * @return
	 */
	public T message() {
		if (size() > 0) {
			Integer k = keys.removeFirst();
			keys.addLast(k);
			return hashMap.get(k);
		}
		return null;
	}

	public T seeLastMessage() {
		if (size() > 0) {
			return hashMap.get(keys.getFirst());
		}
		return null;
	}
	public int size() {
		if (keys.size() != hashMap.size()) {
			System.err.println(("KeySize:" + keys.size() + "Keys: " + keys));
			System.err.println(("HashMapSize:" + hashMap.size() + "Map: " + hashMap));
		}
		return hashMap.size();
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOSMessageList<?>) {
			SOSMessageList<? extends SOSMessageBlock> sml2 = (SOSMessageList<?>) obj;
			return sml2.getPriority() == getPriority();
		}
		return false;
	}

	@Override
	public String toString() {
		return hashMap.toString();
	}

	public void shuffle() {
		Collections.shuffle(keys);
	}

//	static public void main(String[] args) {
//		new ReadXml(new XMLTestAgent());
//
//		SOSMessageList<SOSMessageBlock> s = new SOSMessageList<SOSMessageBlock>();
//		while (true) {
//			String d = JOptionPane.showInputDialog("choose remove");
//			if (d.indexOf("r") >= 0)
//				System.out.println(s.removeMessage());
//			else
//				s.add(new MessageBlock(d));
//			if (s.size() == s.keys.size())
//			System.out.println("hash:" + s.size() + "----key:" + s.keys.size());
//			else
//				System.err.println("hash:" + s.size() + "----key:" + s.keys.size());
//		}
//		// s.add()
//	}
}