package sos.base.message.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Ali
 */
public class MessagePartitioningMakeing {
	private ArrayList<ArrayList<Integer>> messagePartition;
	private ArrayList<String> messageStringPartition;
	private static File f = new File("precompute/message/partitioning.sos");
	
	// public static void main(String[] args) {
	// PrintStream ps = null;
	// try {
	// new File(f.getParent()).mkdir();
	// f.createNewFile();
	// ps = new PrintStream(f);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// if (ps != null) {
	// partitioning();
	// for (ArrayList<Integer> part : messagePartition) {
	// for (int i : part) {
	// ps.append((char) i);
	// }
	// ps.println();
	// }
	// }
	// System.out.println(messagePartition);
	// messagePartition = null;
	// System.err.println(getPartition());
	// }

	public ArrayList<ArrayList<Integer>> getPartition() {
		if (messagePartition != null)
			return messagePartition;
		if (f.exists() && f.canRead()) {
			try {
				messagePartition = new ArrayList<ArrayList<Integer>>();
				BufferedReader br = new BufferedReader(new FileReader(f));
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					ArrayList<Integer> part = new ArrayList<Integer>(line.length());
					for (char c : line.toCharArray()) {
						part.add((int) c);
					}
					messagePartition.add(part);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (isValidPartitionFromFile(messagePartition))
				partitioning();
		} else
			partitioning();
		return messagePartition;
	}

	/**
	 * Validating partitioned read
	 */
	private boolean isValidPartitionFromFile(ArrayList<ArrayList<Integer>> messagePartition2) {
		// TODO Auto-generated method stub
		return false;
	}

	private void partitioning() {
		// sosAgent.sosLogger.base.info("Computing partitioning");
		// sosAgent.sosLogger.messageTransmit.info("Computing partitioning");
		//
		messagePartition = new ArrayList<ArrayList<Integer>>();
		messageStringPartition = new ArrayList<String>();
		// sosAgent.sosLogger.messageTransmit.info("Partitioning number 8 8*2 ... 8*6");
		messagePartition.add(new ArrayList<Integer>(Arrays.asList(0)));// it is 8 % 8
		messageStringPartition.add("0");
		for (int i = 0; i <= 6; i++) {
			partitioning(i * 8);
		}
		Collections.sort(messagePartition, new Comparator<ArrayList<?>>() {
			@Override
			public int compare(ArrayList<?> o1, ArrayList<?> o2) {
				return o1.size() - o2.size();
			}
		});
		// sosAgent.sosLogger.messageTransmit.debug("Partitioned:" + messagePartition);
	}

	private void partitioning(int i) {
		messagePartitioning(i, 7, new ArrayList<Integer>());
	}

	private void messagePartitioning(int n, int max, ArrayList<Integer> part) {
		if (max == 0) {
			Collections.sort(part, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o2 - o1;
				}
			});
			StringBuffer partString = new StringBuffer();
			for (int i = 0; i < part.size(); i++)
				partString.append(part.get(i));
			if (checkStringContains(messageStringPartition, partString.toString()))
				return;
			messageStringPartition.add(partString.toString());
			messagePartition.add(part);
		}
		for (int j = max; j > 0; j--) {
			ArrayList<Integer> cloned = new ArrayList<Integer>(part);
			cloned.add(j);
			messagePartitioning(n - j, Math.min(j, n - j), cloned);
		}
	}

	private boolean checkStringContains(ArrayList<String> Strings, String strContain) {
		for (String s : Strings) {
			String stmp = strContain;
			boolean b = true;
			for (int i = 0; i < s.length(); i++) {
				if (stmp.indexOf(s.charAt(i)) < 0)
					b = false;
				stmp = stmp.substring(stmp.indexOf(s.charAt(i)) + 1);
			}
			if (b)
				return true;
		}
		return false;
	}

}
