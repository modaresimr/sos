package sos.base.precompute;

import sos.base.SOSAgent;
import sos.base.SOSWorldModel;
import sos.base.entities.StandardEntity;

public abstract class AbstractPreCompute {

	public void execute() {
		if (!setFromFile()) {
			compute();
			writeToFile();
		}
	}


	protected abstract void compute();

	protected void writeToFile() {
		String precomputeFile = PreCompute.getPreComputeFile(getClass().getSimpleName() + "/");
		PreComputeFile content = getPreComputeFileContent();
		FileOperations.Write(precomputeFile, content);
	}

	protected abstract PreComputeFile getPreComputeFileContent();

	/**
	 * if reading from file is success and no problem occured during
	 * setting from file @return true
	 */
	protected boolean setFromFile() {
		try {
			String precomputeFile = PreCompute.getPreComputeFile(getClass().getSimpleName() + "/");
			PreComputeFile content = FileOperations.Read(precomputeFile, PreComputeFile.class);
			if (content != null) {
				setFromFile(content);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected abstract void setFromFile(PreComputeFile content);

	protected static SOSWorldModel model() {
		return agent().model();
	}
	protected static SOSAgent<? extends StandardEntity> agent() {
		return SOSAgent.currentAgent();
	}
	
}
