package test.sos.base.util.sosLogger;

import sos.ambulance_v2.base.RescueInfo.IgnoreReason;
import sos.base.util.sosLogger.TableLogger;

public class TableLoggerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TableLogger st=new TableLogger(10);
		st.addScore("ali", "T1", 10);
		st.addScore("ali", "TTTTTTTTTTTTT2", "Asghar");
		st.addScore("ali", "TTTTTTTTTTTTT3", 14);

		st.addScore("gholooooom", "T1", 1);
		st.addScore("gholooooom", "TTTTTTTTTTTTT2", IgnoreReason.CantLoad);
		st.addScore("gholooooom", "TTTTTTTTTTTTT3", Integer.MAX_VALUE);

		st.addScore("mori", "T1", 100);
		st.addScore("mori", "TTTTTTTTTTTTT2", "Ali  sdhds kadgf kdjsgafkjdsgf");
		st.addScore("mori", "TTTTTTTTTTTTT3", 10);

		st.addScore("mori2", "T1", Integer.MAX_VALUE);
		st.addScore("mori2", "TTTTTTTTTTTTT2", Integer.MAX_VALUE);
		st.addScore("mori2", "TTTTTTTTTTTTT3", 10);

		System.out.println(st.getTablarResult("T1"));

	}

}
