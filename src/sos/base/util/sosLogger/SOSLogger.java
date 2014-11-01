package sos.base.util.sosLogger;

import java.util.ArrayList;

import sos.base.SOSConstant;
import sos.base.SOSConstant.logType;
import sos.base.entities.StandardEntity;
import sos.base.util.sosLogger.SOSLoggerSystem.LogLevel;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;

public class SOSLogger {
	public SOSLoggerSystem messageTransmit;
	public SOSLoggerSystem messageContent;
	public SOSLoggerSystem worldModel;
	public SOSLoggerSystem move;
	public SOSLoggerSystem agent;
	public SOSLoggerSystem base;
	public SOSLoggerSystem search;
	public SOSLoggerSystem noComunication;
	public SOSLoggerSystem blockadeEstimator;
	public ArrayList<SOSLoggerSystem> allLogType = new ArrayList<SOSLoggerSystem>();
	public SOSLoggerSystem act;
	public SOSLoggerSystem reachablity_ExpandArea;
	public SOSLoggerSystem reachablity_ExpandBlock;
	public SOSLoggerSystem reachablity_RoadGraph;
	public SOSLoggerSystem reachablity_ReachablePart;
	public SOSLoggerSystem reachablity_Police;
	public SOSLoggerSystem reachablity_Interface;
	public SOSLoggerSystem reachablity_Merge;
	public SOSLoggerSystem preComputeLog;

	public SOSLogger(StandardEntity me, boolean createFiles, OutputType outputType) {
		messageTransmit = new SOSLoggerSystem(me, logType.MessageTransmit.toString(), true, outputType,false,true);
		allLogType.add(messageTransmit);
		messageContent = new SOSLoggerSystem(me, logType.MessageContent.toString(), SOSConstant.CREATE_BASE_LOGS, outputType,false,true);

		allLogType.add(messageContent);
		worldModel = new SOSLoggerSystem(me, logType.WorldModel.toString(), SOSConstant.CREATE_BASE_LOGS, outputType,false,true);
		allLogType.add(worldModel);
		agent = new SOSLoggerSystem(me, logType.Agent.toString(), createFiles, outputType);
		allLogType.add(agent);
		base = new SOSLoggerSystem(me, logType.Base.toString(), createFiles, outputType,false,true);
		allLogType.add(base);
		move = new SOSLoggerSystem(me, logType.Move.toString(),SOSConstant.CREATE_BASE_LOGS, outputType);
		allLogType.add(move);
		act = new SOSLoggerSystem(me, logType.Act.toString(), createFiles, outputType,false,true);
		allLogType.add(act);
		search = new SOSLoggerSystem(me, logType.Search.toString(), createFiles, outputType,false,true);
		allLogType.add(search);
		reachablity_ExpandArea = new SOSLoggerSystem(me, logType.Reachablity.toString() + "/ExpandArea", SOSConstant.CREATE_BASE_LOGS, outputType);
		allLogType.add(reachablity_ExpandArea);
		reachablity_ExpandBlock = new SOSLoggerSystem(me, logType.Reachablity.toString() + "/ExpandBlok", SOSConstant.CREATE_BASE_LOGS, outputType);
		allLogType.add(reachablity_ExpandBlock);
		reachablity_Interface = new SOSLoggerSystem(me, logType.Reachablity.toString() + "/Interface", SOSConstant.CREATE_BASE_LOGS, outputType);
		allLogType.add(reachablity_Interface);
		reachablity_Merge = new SOSLoggerSystem(me, logType.Reachablity.toString() + "/Merge", SOSConstant.CREATE_BASE_LOGS, outputType);
		allLogType.add(reachablity_Merge);
		reachablity_Police = new SOSLoggerSystem(me, logType.Reachablity.toString() + "/Police", SOSConstant.CREATE_BASE_LOGS, outputType);
		allLogType.add(reachablity_Police);
		reachablity_ReachablePart = new SOSLoggerSystem(me, logType.Reachablity.toString() + "/ReachablePart", SOSConstant.CREATE_BASE_LOGS, outputType);
		allLogType.add(reachablity_ReachablePart);
		reachablity_RoadGraph = new SOSLoggerSystem(me, logType.Reachablity.toString() + "/RoadGraph", SOSConstant.CREATE_BASE_LOGS, outputType);
		allLogType.add(reachablity_RoadGraph);
		noComunication=new SOSLoggerSystem(me, logType.NoComunication+"", createFiles, OutputType.File, true,true);
		allLogType.add(noComunication);
		preComputeLog = new SOSLoggerSystem(me, "preCompute", createFiles, OutputType.File,true);
		allLogType.add(preComputeLog);
		setFullLoggingLevel();
//		loglnToAll("---------------------------- precompute  ----------------------------<cycle time='0'>{");
	}

	public void logCurrentTime(int time) {
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.logCurrentTime(time);
		}
	}

	public void loglnToAll(String log) {
		logToAll(log + "\n");
	}

	public void logToAll(String log) {
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.log(log);
		}
	}

	public void logln(String log) {
		log(log + "\n");
	}

	public void log(String log) {
		agent.log(log);
	}

	public void trace(String log) {
		agent.trace(log);
	}

	public void debug(String log) {
		agent.debug(log);
	}

	public void info(String log) {
		agent.info(log);
	}

	public void error(Object log) {
		boolean isFirst = true;
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.error(log, isFirst);
			isFirst = false;
		}
	}

	public void error(Throwable log) {
		boolean isFirst = true;
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.error(log, isFirst);
			isFirst = false;
		}
	}

	public void fatal(Throwable log) {
		boolean isFirst = true;
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.fatal(log, isFirst);
			isFirst = false;
		}
	}

	public void fatal(Object log) {
		boolean isFirst = true;
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.fatal(log, isFirst);
			isFirst = false;
		}
	}

	public void warn(String log) {
		boolean isFirst = true;
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.warn(log, isFirst);
			isFirst = false;
		}
	}

	public void setFullLoggingLevel() {
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.setFullLoggingLevel();
		}
	}

	public void setLoggingLevel(ArrayList<LogLevel> logLevels) {
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.setLoggingLevel(logLevels);
		}
	}

	public void addLoggingLevel(LogLevel level) {
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.addLoggingLevel(level);
		}
	}

	public void addToAllLogType(SOSLoggerSystem sls) {
		allLogType.add(sls);
	}
	public void timeStepFinished() {
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.timeStepFinished();
		}
	}

	public void shutDown() {
		for (SOSLoggerSystem loggerSystem : allLogType) {
			loggerSystem.shutDown();
		}
	}
}
