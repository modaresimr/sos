package sos.base.util;



public class ConsoleProcessBar {
	
	private final int maxSize;
	private int current = 0;
	private int currentWidth = 0;
	private String lineSeparator;
	private final int width;
	
	public ConsoleProcessBar(int width, int maxSize) {
		this.width = width;
		this.maxSize = maxSize;
		lineSeparator = "\n";
	}
	
	public String progress() {
		current++;
		if (current == maxSize)
			return lineSeparator;
		int tmpWidth = (int) (((double) current / maxSize) * width);
		if (tmpWidth > currentWidth) {
			currentWidth = tmpWidth;
			return (char) 1 + "";
		}
		return "";
	}
	
	public int getCurrentPercent() {
		return (int) (((double) current / maxSize) * 100);
	}
}
