package sos.tools.decisionMaker.definitions;

import java.lang.reflect.InvocationTargetException;

import sos.base.SOSAgent;
import sos.base.entities.Human;
import sos.base.entities.StandardEntity;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.channel.Channel;
import sos.base.util.SOSActionException;
import sos.tools.decisionMaker.definitions.commands.SOSTask;

/**
 * @author Salim
 */
public abstract class SOSAbstractDecisionMaker<E extends SOSInformationModel> {
	protected E infoModel;

	public SOSAbstractDecisionMaker(SOSAgent<? extends Human> agent, Class<? extends SOSInformationModel> infoModelClass) {
		System.out.println();
		try {
			infoModel = (E) infoModelClass.getConstructors()[0].newInstance(new Object[] { agent });
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public abstract SOSTask<?> decide()throws SOSActionException ;

	public abstract void hear(String header, DataArrayList data, SOSBitArray dynamicBitArray, StandardEntity sender, Channel channel);
}
