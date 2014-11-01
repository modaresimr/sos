package sos.base.precompute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileAlreadyExistsException;

import sos.LaunchAgents;
import sos.base.SOSAgent;
import sos.base.SOSConstant;
import sos.base.util.sosLogger.SOSLoggerSystem;

public class FileOperations {

	public static void Write(String outputFilePath, PreComputeFile objectToWrite) {
		if (!SOSConstant.isCreatingPreComputeFiles())
			return;
		try {
			File outputFile = new File(outputFilePath);
			outputFile.getParentFile().mkdirs();
			if(outputFile.exists())
				throw new FileAlreadyExistsException(outputFilePath);
			outputFile.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile));
			oos.writeObject(objectToWrite);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			getProperLog().error(e);
		}catch (FileAlreadyExistsException e) {
//			getProperLog().error(e);
		} catch (IOException e) {
			getProperLog().error(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends PreComputeFile> T Read(String inputFilePath, Class<T> outputType) {
		if (!SOSConstant.isCreatingPreComputeFiles())
			return null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFilePath));
			T content = (T) ois.readObject();
			if(content.isValid())
				return content;
			getProperLog().error(new Error(inputFilePath+" has invalid contents!"));
		} catch (FileNotFoundException e) {
//			getProperLog().error(e);
		} catch (IOException e) {
			getProperLog().error(e);
		} catch (ClassNotFoundException e) {
			getProperLog().error(e);
		}
		return null;

	}

	private static SOSLoggerSystem getProperLog() {
		try {
			return SOSAgent.currentAgent().sosLogger.preComputeLog;
		} catch (Exception e) {
			return LaunchAgents.sosLogger;
		}
	}
}
