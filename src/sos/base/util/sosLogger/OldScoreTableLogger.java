package sos.base.util.sosLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class OldScoreTableLogger {

	HashMap<String, Integer> propertyToIndex = new HashMap<String, Integer>();
	ArrayList<String> propertyOrder = new ArrayList<String>();

	private int currentPropertyIndex = 0;
	private HashMap<String, ScoreRow> idToScoreRow = new HashMap<String, OldScoreTableLogger.ScoreRow>();
	private final int propertyCount;

	public OldScoreTableLogger(int propertyCount) {
		this.propertyCount = propertyCount;

	}

	public void addScore(String id, String property, int score) {
		Integer propertyIndex = propertyToIndex.get(property);
		if (propertyIndex == null) {
			propertyIndex = currentPropertyIndex++;
			propertyOrder.add(property);
			propertyToIndex.put(property, propertyIndex);
		}
		ScoreRow scoreRow = idToScoreRow.get(id);
		if (scoreRow == null) {
			scoreRow = new ScoreRow(id, propertyCount);
			idToScoreRow.put(id, scoreRow);
		}
		scoreRow.AddProperty(propertyIndex, score);

	}

	public String getTablarResult(String sortBy) {
		ArrayList<ScoreRow> scores = new ArrayList<OldScoreTableLogger.ScoreRow>(idToScoreRow.values());
		final Integer sortPropertyIndex = propertyToIndex.get(sortBy);
		if (sortPropertyIndex == null) {
			new Error("No such Property to sort" + sortBy).printStackTrace();
		} else {
			Collections.sort(scores, new Comparator<ScoreRow>() {

				@Override
				public int compare(ScoreRow o1, ScoreRow o2) {
					return -(o1.getPropertyScore(sortPropertyIndex) - o2.getPropertyScore(sortPropertyIndex));
				}
			});
		}
		int maxIdLength = 0;
		for (ScoreRow scoreRow : scores)
			maxIdLength = Math.max(maxIdLength, scoreRow.id.length());

		StringBuilder sb = new StringBuilder();
		appendSpace(sb, maxIdLength);

		for (String order : propertyOrder)
			addName(sb, order);

		sb.append("\n");
		int headerLenght = sb.length();
		for (int i = 0; i < headerLenght; i++)
			sb.append("-");

		sb.append("\n");

		for (ScoreRow scoreRow : scores){
			sb.append(scoreRow.id);
			appendSpace(sb, maxIdLength-scoreRow.id.length());
			for (String order : propertyOrder)
				addScore(sb, order, scoreRow.getPropertyScore(propertyToIndex.get(order)));
			sb.append("\n");
		}

		return sb.toString();

	}
	private void addName(StringBuilder sb, String propertyName) {
		sb.append(" | ");
		sb.append(propertyName);

		if (propertyName.length() < 10)
			appendSpace(sb, 10 - propertyName.length());
	}
	private void addScore(StringBuilder sb, String propertyName,int score) {
		sb.append(" | ");
		String scoreString = Integer.toString(score);
		sb.append(scoreString);
		int propertyLength = Math.max(10, propertyName.length());
		if (scoreString.length()<propertyLength)
			appendSpace(sb, propertyLength-scoreString.length());
	}
	private void appendSpace(StringBuilder sb, int size) {
		for (int i = 0; i < size; i++) {
			sb.append(" ");
		}
	}

	private class ScoreRow {
		private final String id;
		private final int[] scores;

		public ScoreRow(String id, int propertyCount) {
			this.id = id;
			scores = new int[propertyCount];
		}

		public void AddProperty(Integer propertyIndex, int score) {
			if (propertyIndex == null)
				System.err.println("[SCORE TABLE]propertyIndex error");
			scores[propertyIndex] = score;
		}

		public int getPropertyScore(Integer propertyIndex) {
			return scores[propertyIndex];
		}
	}
}
