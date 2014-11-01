package sos.base.util;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimeNamayangar {
	public static final int LEVEL_HOUR = 5;
	public static final int LEVEL_MIN = 4;
	public static final int LEVEL_SEC = 3;
	public static final int LEVEL_MILI = 2;

	private long passedTime = 0;
	private long t;
	boolean isStoped = false;
	private String name;
	private int refreshTime = 100;// ms
	private MyLabel hour = new MyLabel();
	private MyLabel min = new MyLabel();
	private MyLabel sec = new MyLabel();
	private MyLabel mili = new MyLabel();
	private int startLevel;
	private int endLevel;
	private JFrame timeWindow;
	private boolean isFinished = false;

	public TimeNamayangar() {
		this("");
	}

	public TimeNamayangar(String name) {
		this(name, false);
	}

	public TimeNamayangar(String name, boolean showWindow) {
		this(name, showWindow, true);
	}

	public TimeNamayangar(String name, boolean showWindow, boolean startingOnNew) {
		this(name, showWindow, startingOnNew, LEVEL_HOUR, LEVEL_MILI);
	}

	public TimeNamayangar(String name, boolean showWindow, int refreshTime) {
		this(name, showWindow, true, refreshTime);
	}

	public TimeNamayangar(String name, boolean showWindow, boolean startingOnNew, int refreshTime) {
		this(name, showWindow, startingOnNew);
		this.refreshTime = refreshTime;
	}

	public TimeNamayangar(String name, boolean showWindow, boolean startingOnNew, int startLevel, int endLevel) {
		t = System.currentTimeMillis();
		isStoped = !startingOnNew;
		this.name = name;
		setLevel(startLevel, endLevel);
		if (showWindow)
			showWindow();
	}

	public void setLevel(int startLevel, int endLevel) {
		if (startLevel > endLevel) {
			this.startLevel = endLevel;
			this.endLevel = startLevel;
		} else {
			this.startLevel = startLevel;
			this.endLevel = endLevel;
		}
	}

	JPanel content;

	public JPanel getPanel() {
		if (content != null)
			return content;

		content = new JPanel(new GridLayout(2, 4, 3, 3));
		// content.setBorder(BorderFactory.createLineBorder(Color.green.darker(), 2));
		content.add(new JLabel(" Hour"));
		content.add(new JLabel("Min"));
		content.add(new JLabel("sec"));
		content.add(new JLabel("mili"));
		content.add(hour);
		content.add(min);
		content.add(sec);
		content.add(mili);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!isFinished()) {
					try {
						Thread.sleep(refreshTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// long t1 = System.currentTimeMillis();
					showNewTime();
					// passedTime+=t1-t;
					// t=t1;

				}
			}
		},"Time Namayangar").start();

		return content;

	}

	public void showWindow() {
		timeWindow = new JFrame(name);
		timeWindow.setSize(250, 80);
		timeWindow.add(getPanel());
		timeWindow.setIconImage(new ImageIcon("timenamyangaricon.png").getImage());
		timeWindow.setVisible(true);

	}

	private void showNewTime() {
		if (!isStoped) {
			// synchronized (this) {
			stop();
			// refreshTimeView();
			start();
			// }

		}

	}

	private void refreshTimeView() {
		if (content == null)
			return;
		hour.setText(getHours());
		min.setText(getMins());
		sec.setText(getSeconds());
		String m = "000" + getMiliSeconds();
		mili.setText(m.substring(m.length() - 3, m.length()));
	}

	class MyLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		public MyLabel() {
			setOpaque(true);
			setBackground(Color.white);
			setText("0");
		}

		public void setText(int text) {
			// TODO Auto-generated method stub
			super.setText(text + "");
		}

	}


	public String stop() {

		if (isStoped)
			return toString();
		isStoped = true;
		passedTime += System.currentTimeMillis() - t;
		refreshTimeView();
		return toString();
	}

	private synchronized void passTime() {
		if (isStoped)
			return;
		long t1 = System.currentTimeMillis();
		passedTime += t1 - t;
		t = t1;
	}

	public void start() {
		if (!isStoped)
			return;
		isStoped = false;
		t = System.currentTimeMillis();
	}

	public long getPassedTime() {
		passTime();
		return passedTime;
	}

	public int getHours() {
		return (int) (getPassedTime() / 1000 / 60 / 60);
	}

	public int getMins() {
		return (int) ((getPassedTime() / 1000 / 60) % 60);
	}

	public int getSeconds() {
		return (int) ((getPassedTime() / 1000) % 60);
	}

	public int getMiliSeconds() {
		return (int) ((getPassedTime() % 1000 ));
	}

	@Override
	public String toString() {
		boolean tmpStop = isStoped;
		// stop();
		StringBuffer sb = new StringBuffer(10);
		sb.append("[TimePassed= ");
		switch (endLevel) {
		case LEVEL_HOUR:
			if (startLevel <= LEVEL_HOUR)
				sb.append(getHours() + "h");
			if (startLevel < LEVEL_HOUR)
				sb.append(",");
		case LEVEL_MIN:
			if (startLevel <= LEVEL_MIN)
				sb.append(getMins() + "m");
			if (startLevel < LEVEL_MIN)
				sb.append(",");
		case LEVEL_SEC:
			if (startLevel <= LEVEL_SEC)
				sb.append(getSeconds() + "s");
			if (startLevel < LEVEL_SEC)
				sb.append(",");
		case LEVEL_MILI:
			if (startLevel <= LEVEL_MILI)
				sb.append(getMiliSeconds() + "ms");
		default:
			break;
		}
		if (!tmpStop)
			start();
		return sb.toString() + "]";// "[TimePassed= " + getHours() + "h," +
											// getMins() + "m," + getSeconds() + "s," +
											// getMiliSeconds() + "ms," + getNanoSeconds()
											// + "ns]";
	}

	public static String nanoTimeToString(long miliTime) {
		return miliTimeToString(miliTime, LEVEL_MILI, LEVEL_HOUR);
	}

	public static String miliTimeToString(long miliTime, int startLevel, int endLevel) {
		StringBuffer sb = new StringBuffer(10);

		switch (endLevel) {
		case LEVEL_HOUR:
			if (startLevel <= LEVEL_HOUR)
				sb.append((int) (miliTime / 1000 / 60 / 60) + "h");
			if (startLevel < LEVEL_HOUR)
				sb.append(",");
		case LEVEL_MIN:
			if (startLevel <= LEVEL_MIN)
				sb.append((int) ((miliTime / 1000 / 60) % 60) + "m");
			if (startLevel < LEVEL_MIN)
				sb.append(",");
		case LEVEL_SEC:
			if (startLevel <= LEVEL_SEC)
				sb.append((int) ((miliTime / 1000) % 60) + "s");
			if (startLevel < LEVEL_SEC)
				sb.append(",");
		case LEVEL_MILI:
			if (startLevel <= LEVEL_MILI)
				sb.append((int) ((miliTime % 1000 )) + "ms");
		default:
			break;
		}
		return sb.toString();
	}

//	public static void main(String[] args) {
//		TimeNamayangar tn = new TimeNamayangar("Ali", true, 100);
//		tn.stop();
//		System.out.println(tn);
//		tn.setLevel(LEVEL_HOUR, LEVEL_SEC);
//		System.out.println(tn);
//	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public void finish() {
		this.isFinished = true;
		stop();
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void reset() {
		passedTime=0;
	}
}
