package sos.base.message.structure;

/**
 *
 * @author Ali
 *
 */
public interface MessageConstants {
	public static final int UNUSED_AGENT_SEND_BYTE = 6;
	static final String AMBULANCE_XML_FILE_NAME = "xml/ambulance.xml", FIRE_XML_FILE_NAME = "xml/fire.xml", POLICE_XML_FILE_NAME = "xml/police.xml", CENTER_XML_FILE_NAME = "xml/center.xml", LowCommunicaion_XML_FILE_NAME = "xml/centerlow.xml";

//	final static int HEADER_SIZE = 5;
	final static boolean THROW_EXCEPTION = true;

	enum Destination {
		All, Fire, Police, Ambulance, FirePolice, FireAmbulance, PoliceAmbulance
	}

	enum MessageCenterType {
		FullCenter, TwoCenter, OneCenter, NoCenterA, NoCenterB, NoComunication
	}

	enum ChannelType {
		Voice, Radio
	}

	enum Noise {
		Input, Output
	}

	enum Type {
		WithMiddleMan,NoMiddleMan,NoComunication,LowComunication,CenteralMiddleMan
	}
	public enum ChannelSystemType{
		Voice,Low,Normal
	}

}
