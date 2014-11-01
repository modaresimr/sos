package sos.base.util.sosLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import sos.base.SOSConstant;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;

/**
 * @author Ali
 */
public class SOSLoggerSystem {
	public enum OutputType {
		File, Console, Both, ConsoleErr, NAN
	}

	public enum LogLevel {
		Trace, Info, Debug, Warn, Error, Fatal
	};

	private static final Date STARTING_TIME = new Date(System.currentTimeMillis());
	boolean createFiles = false;
	String path = "";
	String fileType = ".xml";
	protected PrintStream ps = null;
	private OutputType outputType = OutputType.NAN;
	boolean ConsoleLogging = false;
	boolean ConsoleErrLogging = false;
	String logName;
	private final StandardEntity me;
	int currentIndentLevel = 0;
	ArrayList<LogLevel> levels = new ArrayList<LogLevel>();

	/**
	 * @author Ali
	 * @param me
	 *            -->to Have only a file use me=null;
	 * @forExample SOSLoggerSystem(null, "Ali", true, true) {
	 * @param logName
	 * @param createFiles
	 * @param fullLogging
	 * @param fileLogging
	 */
	public SOSLoggerSystem(StandardEntity me, String logName, boolean createFiles, OutputType OutputType) {
		this(me, logName, createFiles, OutputType, false);
	}

	boolean isFirst = true;
	private final boolean xmlLogging;

	public SOSLoggerSystem(StandardEntity me, String logName, boolean createFiles, OutputType OutputType, boolean fullLogging) {
		this(me, logName, createFiles, OutputType, fullLogging, false);
	}

	public SOSLoggerSystem(StandardEntity me, String logName, boolean createFiles, OutputType OutputType, boolean fullLogging, boolean xmlLogging) {
		if (SOSConstant.IS_CHALLENGE_RUNNING || SOSConstant.DONT_CREATE_LOGS)
			createFiles = false;
		this.xmlLogging = false;//xmlLogging;

		this.me = me;
		this.outputType = OutputType;
		if (!createFiles)
			return;
		this.createFiles = true;//createFiles;
		this.logName = logName;
		createFile();
		if (fullLogging)
			setFullLoggingLevel();
	}

	public void increaseIndent() {
		increaseIndent("");
	}

	public void increaseIndent(String title) {
		currentIndentLevel++;
		if (xmlLogging && createFiles) {
			ps.println("<log title='" + title + "'>");
		}
	}

	public void decreaseIndet() {
		currentIndentLevel--;
		if (xmlLogging && createFiles) {
			ps.println("</log>");
		}
	}

	public void logln(Object obj) {
		logln(loggerToString(obj));
	}

	public void log(Object obj) {
		log(loggerToString(obj));
	}

	public void log(String log) {
		log(log, false);
	}

	private void log(String log, boolean consoleLogging) {
		logImp("", log, consoleLogging);
	}

	public void logImp(String title, String log, boolean consoleLogging) {
		if (SOSConstant.NO_ANY_LOG)
			return;

		switch (outputType) {
		case Both:
			System.out.print(log);
		case File:
			if (createFiles)
				try {
					if (isFirst) {
						isFirst = false;
						if (xmlLogging) {
							ps.println("<sos>");
							ps.println("<cycle time='0'>");
						} else
							logln("----------------------------PreCompute----------------------------<cycle time='0'>{");
					}
					if (xmlLogging) {
						ps.println(getSpaceDueToCurrentIndentLevel() + "<log title='" + toValidXMlChars(title) + "'>" + toValidXMlChars(log) + "</log>");
					} else {
						ps.print(getSpaceDueToCurrentIndentLevel() + ((title == null || title.length() == 0) ? "" : title + ":") + log);
					}
					//					ps.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			break;
		case Console:
			System.out.print(log);
			break;
		case ConsoleErr:
			System.err.print(log);
			break;
		default:
			break;
		}
		if (consoleLogging && outputType != OutputType.Both && outputType != OutputType.Console)
			System.out.print(log);
	}

	private String getSpaceDueToCurrentIndentLevel() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < currentIndentLevel; i++) {
			buffer.append("\t");
		}
		return buffer.toString();
	}

	private String toValidXMlChars(String string) {
		return string.replace("&", "&amp;").replace("'", "&apos;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
	}

	public synchronized void loglnImp(String title, String log, boolean consoleLogging) {
		if (SOSConstant.NO_ANY_LOG)
			return;
		//		String consoleEnd=me!=null?" #t"+me.standardModel().time():"";
		String consoleEnd = "";
		switch (outputType) {
		case Both:
			System.out.println(title + ":" + log + consoleEnd);
		case File:
			if (createFiles)
				try {
					if (isFirst) {
						isFirst = false;
						if (xmlLogging) {
							ps.println("<sos>");
							ps.println("<cycle time='0'>");
						} else
							ps.println("----------------------------PreCompute----------------------------<cycle time='0'>{");
					}
					if (xmlLogging) {
						title = title.replace("&", "&amp;").replace("'", "&apos;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
						log = log.replace("&", "&amp;").replace("'", "&apos;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
						ps.println("<log title='" + title + "'>" + log + "</log>");
					} else {
						ps.println(((title == null || title.length() == 0) ? "" : title + ":") + log);
					}
					//					ps.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			break;
		case Console:

			System.out.println(title + ":" + log + consoleEnd);
			break;
		case ConsoleErr:
			System.err.println(title + ":" + log + consoleEnd);
			break;
		default:
			break;
		}
		if (consoleLogging && outputType != OutputType.Both && outputType != OutputType.Console) {
			System.out.println(me + (me == null ? "" : " #t" + me.standardModel().time()) + " " + log + consoleEnd);
		}
	}

	public void logln(String log) {
		logln(log, false);
	}

	private void logln(String log, boolean consoleLogging) {
		loglnImp("", log, consoleLogging);
	}

	public void setOutputType(OutputType OutputType) {
		this.outputType = OutputType;
	}

	public void createFile() {
		String[] s_date = STARTING_TIME.toString().replace(":", ";").split(" ");
		String day = s_date[2];
		String month = s_date[1];
		String time = s_date[3].substring(0, 5);
		path = createDirectory(path + "soslogs/" + day + "-" + month + "-" + time);
		if (logName.indexOf("/") > 0) {
			path = createDirectory(path + "/" + logName.substring(0, logName.lastIndexOf("/")));
			logName = logName.substring(logName.lastIndexOf("/") + 1);
		}
		if (me == null) {
			path += "/" + logName + fileType;
		} else if (!(me instanceof Human)) {
			path = createDirectory(path + "/" + logName + "/Center");
			String s = me.getClass().getName();
			path += "/" + s.substring(s.lastIndexOf(".") + 1) + "-" + me.getID() + fileType;
		} else {
			String s = me.getClass().toString();
			path = createDirectory(path + "/" + logName + "/" + s.substring(s.lastIndexOf(".") + 1));
			path += "/" + me.getID() + fileType;
		}
		createFile(path);
	}

	private void createFile(String name) {
		File file = new File(name);
		try {
			if (!file.exists())
				file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file, true);

			//			new javax.activation.MimetypesFileTypeMap(	fos).addMimeTypes("sos/soslogs");

			ps = new PrintStream(fos);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String createDirectory(String path) {
		File dir = new File(path);
		dir.mkdirs();
		return dir.getPath();
	}

	@Override
	protected void finalize() {
		ps.close();
	}

	public void heavyTrace(Object log) {
		if (levels.contains(LogLevel.Trace))
			logln("[TRACE] " + loggerToString(log));
	}

	public void heavyTrace(String message, Object log) {
		if (levels.contains(LogLevel.Trace))
			loglnImp("[TRACE]" + message, loggerToString(log), false);
	}

	public void trace(Object log) {
		if (levels.contains(LogLevel.Trace))
			loglnImp("[TRACE]", loggerToString(log), false);
	}

	public void trace(String message, Object log) {
		if (levels.contains(LogLevel.Trace))
			loglnImp("[TRACE]" + message, loggerToString(log), false);
	}

	public void debug(Object log) {
		if (levels.contains(LogLevel.Debug))
			loglnImp("[DEBUG]", loggerToString(log), false);
	}

	public void consoleDebug(Object log) {
		if (levels.contains(LogLevel.Debug))
			loglnImp("[DEBUG]", loggerToString(log), true);
	}

	public void debug(String message, Object log) {
		if (levels.contains(LogLevel.Debug))
			loglnImp("[DEBUG] " + message, loggerToString(log), false);
	}

	public void info(Object log) {
		if (levels.contains(LogLevel.Info))
			loglnImp("[INFO]", loggerToString(log), false);
	}

	public void info(String message, Object log) {
		info(message, log, false);
	}

	public void info(String message, Object log, boolean doConsoleLogging) {
		if (levels.contains(LogLevel.Info))
			loglnImp("[INFO] " + message, loggerToString(log), doConsoleLogging);
	}

	public void consoleInfo(Object log) {
		//		if (levels.contains(LogLevel.Info))
		loglnImp("[INFO] ", loggerToString(log), true);
	}

	public void error(Object log) {
		if (levels.contains(LogLevel.Error)) {
			loglnImp("[ERROR] ", loggerToString(log), false);
			System.err.println("[ERROR] " + (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")) + log);
		}
	}

	public void error(Object log, boolean consoleErrLogging) {
		if (levels.contains(LogLevel.Error)) {
			loglnImp("[ERROR] ", loggerToString(log), false);
			if (consoleErrLogging)
				System.err.println("[ERROR] " + (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")) + log);
		}
	}

	public void fatal(Object log) {
		if (levels.contains(LogLevel.Fatal)) {
			System.err.println("[FATAL] " + (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")) + log);

			if (outputType == OutputType.Both || outputType == OutputType.Console || outputType == OutputType.ConsoleErr) {
				OutputType o1 = outputType;
				if (outputType == OutputType.Both || outputType == OutputType.File)
					setOutputType(OutputType.File);
				loglnImp("[FATAL] ", loggerToString(log), false);
				setOutputType(o1);
			}

		}
	}

	public void fatal(Object log, boolean consoleErrLogging) {
		if (levels.contains(LogLevel.Fatal)) {
			if (consoleErrLogging)
				System.err.println("[FATAL] " + (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")) + log);

			if (outputType == OutputType.Both || outputType == OutputType.Console || outputType == OutputType.ConsoleErr) {
				OutputType o1 = outputType;
				if (outputType == OutputType.Both || outputType == OutputType.File)
					setOutputType(OutputType.File);
				loglnImp("[FATAL] ", loggerToString(log), false);
				setOutputType(o1);
			}

		}
	}

	public void fatal(Throwable e, boolean consoleErrLogging) {
		if (levels.contains(LogLevel.Fatal)) {
			loglnImp("[FATAL]", (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")), false);
			handleThrowable(e);
		}
		if (consoleErrLogging) {
			System.err.println("[FATAL]" + (me == null ? "" : (me + " #t" + me.standardModel().time() + " ") + e.getMessage()));
			e.printStackTrace();
		}
	}

	public void error(Throwable e, boolean consoleErrLogging) {
		if (levels.contains(LogLevel.Error)) {
			loglnImp("[ERROR]", (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")), false);
			handleThrowable(e);
		}
		if (consoleErrLogging) {
			System.err.println("[ERROR] " + (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")) + e.getMessage());
			e.printStackTrace();
		}
	}

	public void fatal(Throwable e) {
		fatal(e, true);
	}

	public void error(Throwable e) {
		error(e, true);
	}

	private void handleThrowable(Throwable e) {
		switch (outputType) {
		case Both:
		case File:
			if (createFiles)
				try {
					ps.println("<log title='" + e.getMessage() + "'>");
					e.printStackTrace(ps);
					ps.println("</log>");
					ps.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

		}

	}

	public void warn(Object log) {
		warn(log, true);
	}

	public void warn(Object log, boolean consoleLogging) {
		if (levels.contains(LogLevel.Warn))
			loglnImp("[WARN]", loggerToString(log), consoleLogging);
	}

	public void warn(String message, Object log) {
		warn(message, log, true);
	}

	public void warn(String message, Object log, boolean consoleLogging) {
		if (levels.contains(LogLevel.Warn))
			loglnImp("[WARN] " + message, loggerToString(log), consoleLogging);
	}

	public void addLoggingLevel(LogLevel level) {
		levels.add(level);
	}

	public void setFullLoggingLevel() {
		levels.add(LogLevel.Error);
		levels.add(LogLevel.Fatal);
		levels.add(LogLevel.Trace);
		levels.add(LogLevel.Debug);
		levels.add(LogLevel.Info);
		levels.add(LogLevel.Warn);
	}

	public static String loggerToString(Object obj) {
		if (obj == null)
			return "null";

		if (!obj.getClass().isArray())
			return obj.toString();

		if (obj instanceof Object[]) {
			Object[] a = (Object[]) obj;
			int iMax = a.length - 1;
			if (iMax == -1)
				return "[]";
			StringBuilder b = new StringBuilder();
			b.append('[');
			for (int i = 0;; i++) {
				b.append(loggerToString(a[i]));
				if (i == iMax)
					return b.append(']').toString();
				b.append(", ");
			}
		}

		if ((obj instanceof int[]))
			return Arrays.toString((int[]) obj);

		if ((obj instanceof double[]))
			return Arrays.toString((double[]) obj);

		if ((obj instanceof float[]))
			return Arrays.toString((float[]) obj);

		if ((obj instanceof byte[]))
			return Arrays.toString((byte[]) obj);

		if ((obj instanceof boolean[]))
			return Arrays.toString((boolean[]) obj);

		if ((obj instanceof short[]))
			return Arrays.toString((byte[]) obj);

		if ((obj instanceof char[]))
			return Arrays.toString((char[]) obj);

		if ((obj instanceof long[]))
			return Arrays.toString((long[]) obj);

		return obj.toString();

	}

	public void setLoggingLevel(ArrayList<LogLevel> logLevels) {
		for (LogLevel logLevel : logLevels) {
			addLoggingLevel(logLevel);
		}
	}

	public void error(Object log, Throwable e) {
		if (levels.contains(LogLevel.Error)) {
			loglnImp("[ERROR]", loggerToString(log), false);
			handleThrowable(e);
		}
		if (!(outputType == OutputType.Both || outputType == OutputType.Console || outputType == OutputType.ConsoleErr))
			System.err.println("[ERROR] " + (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")) + loggerToString(log));
		e.printStackTrace();
	}

	public void fatal(Object log, Throwable e) {
		if (levels.contains(LogLevel.Fatal)) {
			loglnImp("[FATAL]", loggerToString(log), false);
			handleThrowable(e);
		}
		if (!(outputType == OutputType.Both || outputType == OutputType.Console || outputType == OutputType.ConsoleErr))
			System.err.println("[FATAL] " + (me == null ? "" : (me + " #t" + me.standardModel().time() + " ")) + loggerToString(log));
		e.printStackTrace();
	}

	public void timeStepFinished() {
		if (createFiles) {
			if (xmlLogging) {
				for (int i = 0; i < currentIndentLevel; i++)
					ps.println("</log>");

				ps.println("</cycle>");
			} else {
				logln("}</cycle>");
			}
			currentIndentLevel = 0;
			ps.flush();
		}
	}

	public void logCurrentTime(int time) {
		if (xmlLogging & createFiles) {
			ps.println("<cycle time='" + time + "'>");
		} else {
			logln("----------------------------" + time + "----------------------------<cycle time='" + time + "'>{");
		}
	}

	public void shutDown() {
		if (createFiles) {
			if (xmlLogging) {
				ps.println("</sos>");
			}
			ps.flush();
		}
	}
}
