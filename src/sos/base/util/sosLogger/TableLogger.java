package sos.base.util.sosLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import sos.base.SOSConstant;

public class TableLogger {

	HashMap<String, Integer> propertyToIndex = new HashMap<String, Integer>();
	ArrayList<String> propertyOrder = new ArrayList<String>();

	private int currentPropertyIndex = 0;
	private HashMap<String, ScoreRow> idToScoreRow = new HashMap<String, TableLogger.ScoreRow>();
	private final int propertyCount;
	private boolean printNull = true;

	public TableLogger(int propertyCount) {
		this.propertyCount = propertyCount;

	}
	public void addColumn(String name){
		int propertyIndex = currentPropertyIndex++;
		propertyOrder.add(name);
		propertyToIndex.put(name, propertyIndex);
	}

	public void setPrintNull(boolean printNull) {
		this.printNull = printNull;
	}

	public void addScore(String id, String property, Comparable<?> value) {
		if (SOSConstant.IS_CHALLENGE_RUNNING)
			return;
		Integer propertyIndex = propertyToIndex.get(property);
		if (propertyIndex == null) {
			propertyIndex=currentPropertyIndex;
			addColumn(property);
			
		}
		ScoreRow scoreRow = idToScoreRow.get(id);
		if (scoreRow == null) {
			scoreRow = new ScoreRow(id, propertyCount);
			idToScoreRow.put(id, scoreRow);
		}
		scoreRow.AddProperty(propertyIndex, value);

	}

	public String getTablarResult(String sortBy) {
		if (SOSConstant.IS_CHALLENGE_RUNNING)
			return "";
		ArrayList<ScoreRow> scores = new ArrayList<TableLogger.ScoreRow>(idToScoreRow.values());
		final Integer sortPropertyIndex = propertyToIndex.get(sortBy);
		if (sortPropertyIndex == null) {
			new Error("No such Property to sort" + sortBy).printStackTrace();
		} else {
			Collections.sort(scores, new Comparator<ScoreRow>() {

				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public int compare(ScoreRow o1, ScoreRow o2) {
					Comparable p1 = o1.getPropertyScore(sortPropertyIndex);
					Comparable p2 = o2.getPropertyScore(sortPropertyIndex);
					//					return sortPropertyIndex;
					if (p1 == null)
						return p2 == null ? 0 : -1;
					if (p2 == null)
						return p1 == null ? 0 : 1;
					
					return -p1.compareTo(p2);
				}
			});
		}
		int maxIdLength = 0;
		int[] maxColumnLenght = new int[propertyOrder.size()];
		for (ScoreRow scoreRow : scores) {
			maxIdLength = Math.max(maxIdLength, scoreRow.id.length());
			for (int i = 0; i < propertyOrder.size(); i++) {
				maxColumnLenght[i] = Math.max(maxColumnLenght[i], (scoreRow.getPropertyScore(i) + "").length());
			}
		}
		for (int i = 0; i < propertyOrder.size(); i++) {
			maxColumnLenght[i] = Math.max(maxColumnLenght[i], propertyOrder.get(i).length());
		}

		StringBuilder sb = new StringBuilder();
		appendSpace(sb, maxIdLength);
		for (int i = 0; i < propertyOrder.size(); i++) {
			addName(sb, propertyOrder.get(i), maxColumnLenght[i]);

		}
		sb.append("\n");
		int headerLenght = sb.length();
		for (int i = 0; i < headerLenght; i++)
			sb.append("-");

		sb.append("\n");

		for (ScoreRow scoreRow : scores) {
			sb.append(scoreRow.id);
			appendSpace(sb, maxIdLength - scoreRow.id.length());
			for (int i = 0; i < propertyOrder.size(); i++)
				addScore(sb, scoreRow.getPropertyScore(i), maxColumnLenght[i]);
			sb.append("\n");
		}

		return sb.toString();

	}

	private void addName(StringBuilder sb, String propertyName, int maxColumnLenght) {
		sb.append(" | ");
		sb.append(propertyName);

		appendSpace(sb, maxColumnLenght - propertyName.length());
	}

	private void addScore(StringBuilder sb, Comparable<?> score, int maxColumnLenght) {
		sb.append(" | ");
		String scoreString = (score == null) ? (printNull ? "null" : "-") : score + "";
		sb.append(scoreString);

		appendSpace(sb, maxColumnLenght - scoreString.length());
	}

	private void appendSpace(StringBuilder sb, int size) {
		for (int i = 0; i < size; i++)
			sb.append(" ");

	}

	private class ScoreRow {
		private final String id;
		private final Comparable<?>[] scores;

		public ScoreRow(String id, int propertyCount) {
			this.id = id;
			scores = new Comparable<?>[propertyCount];
		}

		public void AddProperty(Integer propertyIndex, Comparable<?> score) {
			if (propertyIndex == null)
				System.err.println("[SCORE TABLE]propertyIndex error");
			scores[propertyIndex] = score;
		}

		public Comparable<?> getPropertyScore(Integer propertyIndex) {
			return scores[propertyIndex];
		}
	}
}
