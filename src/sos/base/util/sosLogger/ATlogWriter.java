package sos.base.util.sosLogger;

import java.util.Vector;

import rescuecore2.misc.Pair;
import sos.base.entities.StandardEntity;

public class ATlogWriter extends SOSLoggerSystem {

	public ATlogWriter(StandardEntity me, String logName, boolean createFiles, OutputType OutputType) {
		super(me, logName, createFiles, OutputType);
	}

	public void startLog(String title, String value) {
		if (createFiles)
			ps.println("<" + title + "=   " + value + ">");
		
	}

	public void Info(String info) {
		if (createFiles)
			ps.println("<" + info+"/>");
	}
	public void addLog(String info, String title, String value) {
		if (createFiles){
		startLog(title, value);
		ps.println(info);
		endLog(title);
		}
	}

	public void addLogs(Vector<Pair<String, String>> titleValues, Vector<String> infos) {
		if (!createFiles)
			return;
		
		String parentTitle = titleValues.get(0).first();
		String parentValue = titleValues.get(0).second();
		startLog(parentTitle, parentValue);

		for (int i = 1; i < titleValues.size(); i++) {
			Pair<String, String> p = titleValues.get(i);
			addLog(infos.get(i - 1), p.first(), p.second());
		}
		endLog(parentTitle);

	}
	
	public String addTag(String title,String value){
		return title+"="+value+"      ";
	}

	public void endLog(String title) {
		if (createFiles)
		
		ps.println("</"+title+">");
	}
}
