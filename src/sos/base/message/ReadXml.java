package sos.base.message;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sample.SampleWorldModel;
import sos.base.SOSWorldModel;
import sos.base.entities.StandardWorldModel;
import sos.base.message.structure.MessageConstants;
import sos.base.message.structure.MessageXmlConstant;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.blocks.XmlBlockContent;
import sos.base.message.structure.channel.Channel;
import sos.base.message.structure.channel.VoiceChannel;
import sos.base.util.sosLogger.SOSLoggerSystem;

/**
 * @author Ali
 */
public class ReadXml implements MessageConstants {

	public static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	public static HashMap<String, XmlBlockContent> blocks;
	public static HashMap<Destination, ArrayList<XmlBlockContent>> subBlocks;
//	public static HashMap<String, Integer> headerToIndex = new HashMap<String, Integer>();
//	public static HashMap<Integer, String> indexToHeader = new HashMap<Integer, String>();
	private static HashMap<ChannelSystemType, MessageGroup> validChannelMessages;
	private final StandardWorldModel model;
	private SOSLoggerSystem mcLog;

	public ReadXml(StandardWorldModel model) {
		this.model = model;
		if(model instanceof SOSWorldModel){
			this.mcLog = ((SOSWorldModel) model).sosAgent().sosLogger.messageContent;
		}else if(model instanceof SampleWorldModel){
			mcLog=((SampleWorldModel) model).log();
		}
		if (blocks == null) {
			blocks = new HashMap<String, XmlBlockContent>();
			subBlocks = new HashMap<Destination, ArrayList<XmlBlockContent>>();
			validChannelMessages=new HashMap<MessageConstants.ChannelSystemType, ReadXml.MessageGroup>();
			for (ChannelSystemType channel : ChannelSystemType.values()) {
				validChannelMessages.put(channel, new MessageGroup());
			}
			//				if (sosAgent instanceof AmbulanceTeamAgent)
			//					 parsXML(AMBULANCE_XML_FILE_NAME);
			//				if (sosAgent instanceof FireBrigadeAgent)
			//					 parsXML(FIRE_XML_FILE_NAME);
			//				if (sosAgent instanceof PoliceForceAgent)
			//					 parsXML(POLICE_XML_FILE_NAME);
			//				if (sosAgent instanceof CenterAgent)
//			if(sosAgent.messageSystem.type==Type.LowComunication)
//				parsXML(LowCommunicaion_XML_FILE_NAME);
//			else
				parsXML(CENTER_XML_FILE_NAME);

		}
	}

	public void parsXML(String fileName) {
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xmlFile = db.parse(fileName);
			XmlBlockContent blockContent;

			NodeList destinationList = xmlFile.getElementsByTagName("Destination");
//			int index = 1;
			for (int d = 0; d < destinationList.getLength(); d++) {
				NodeList destinationContent = destinationList.item(d).getChildNodes();
				String dest = ((Element) destinationList.item(d)).getAttribute("name");
				Destination destination = Destination.valueOf(dest);
				subBlocks.put(destination, new ArrayList<XmlBlockContent>());
				NodeList blockList = ((Element) destinationContent).getElementsByTagName("Message");
				for (int i = 0; i < blockList.getLength(); i++) {
					Element messageContent = (Element) blockList.item(i).getChildNodes();

					String header = messageContent.getAttribute("header");

//					headerToIndex.put(header, index);
//					indexToHeader.put(index, header);
//					index++;
					NodeList dataList = messageContent.getElementsByTagName("Data");
					blockContent = new XmlBlockContent(dataList.getLength(), header);
					blockContent.setDestination(destination);
					NodeList channelTypeList = messageContent.getElementsByTagName("ChannelType");
					ChannelSystemType[] channelTypes=new ChannelSystemType[channelTypeList.getLength()];
					for (int j = 0; j < channelTypeList.getLength(); j++) {
						channelTypes[j]= ChannelSystemType.valueOf(channelTypeList.item(j).getFirstChild().getNodeValue());
					}
					blockContent.setValidChannels(channelTypes);
					String maxSize = messageContent.getElementsByTagName("MaxSize").item(0).getFirstChild().getNodeValue();
					blockContent.setMaxSize(Integer.parseInt(maxSize));
					String priority = messageContent.getElementsByTagName("Priority").item(0).getFirstChild().getNodeValue();
					blockContent.setPriority(Integer.parseInt(priority));

					for (int j = 0; j < dataList.getLength(); j++) {
						String data = ((Element) dataList.item(j)).getAttribute("name");
						int value = Integer.parseInt(dataList.item(j).getFirstChild().getNodeValue());
						value=determineExactBits(data,value);

						blockContent.addData(data, value);
						if (j == 0) {
							boolean hasKey = messageContent.getElementsByTagName("HasKey").item(0).getFirstChild().getNodeValue().equalsIgnoreCase("true");
							if (hasKey)
								blockContent.setDataKey(data);
						}
					}

					blocks.put(header, blockContent);
					for (ChannelSystemType validChannel : blockContent.getValidChannels()) {
						validChannelMessages.get(validChannel).addBlock(blockContent);
					}
					subBlocks.get(destination).add(blockContent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mcLog.trace("XML BLOCKS",blocks);
//		mcLog.trace("headerToIndex",headerToIndex);
//		mcLog.trace("indexToHeader",indexToHeader);
		for (ChannelSystemType channelType : ChannelSystemType.values()) {
			mcLog.trace(channelType +"",validChannelMessages.get(channelType));

		}
		if(blocks.size()>32){
			mcLog.warn("Messages xml blocks size("+blocks.size()+") are bigger than 32(5bit)!!!!for some custom header that want to be exact 8x may occure problem");
		}
	}

	private int determineExactBits(String data,int value) {
		if (data.endsWith("index")) {
			if (data.equalsIgnoreCase(MessageXmlConstant.DATA_BUILDING_INDEX)) {
				value = determineNeededBits(model.buildings().size());
				mcLog.trace("Buildings size:" + model.buildings().size() + "-->Needed bits:" + value);
			}
			if (data.equalsIgnoreCase(MessageXmlConstant.DATA_AREA_INDEX)) {
				value = determineNeededBits(model.areas().size());
				mcLog.trace("Areas size:" + model.areas().size() + "-->Needed bits:" + value);
			}
			// if (data.equalsIgnoreCase("RefugeIndex")) {
			// value = determineNeededBits(model.refuges().size());
			// mcLog.trace("Refuges size:" + model.refuges().size() + "-->Needed bits:" + value);
//			}
			if (data.equalsIgnoreCase(MessageXmlConstant.DATA_ROAD_INDEX)) {
				value = determineNeededBits(model.roads().size());
				mcLog.trace("Roads size:" + model.roads().size() + "-->Needed bits:" + value);
			}
			if (data.equalsIgnoreCase(MessageXmlConstant.DATA_POLICE_INDEX)) {
				value = determineNeededBits(model.policeForces().size());
				mcLog.trace("Polices size:" + model.policeForces().size() + "-->Needed bits:" + value);
			}
			if (data.equalsIgnoreCase(MessageXmlConstant.DATA_AMBULANCE_INDEX)) {
				value = determineNeededBits(model.ambulanceTeams().size());
				mcLog.trace("Ambulances size:" + model.ambulanceTeams().size() + "-->Needed bits:" + value);
			}
			if (data.equalsIgnoreCase(MessageXmlConstant.DATA_FIRE_INDEX)) {
				value = determineNeededBits(model.fireBrigades().size());
				mcLog.trace("Fires size:" + model.fireBrigades().size() + "-->Needed bits:" + value);
			}
			if (data.equalsIgnoreCase(MessageXmlConstant.DATA_AGENT_INDEX)) {
				value = determineNeededBits(model.agents().size());
				mcLog.trace("Agents size:" + model.agents().size() + "-->Needed bits:" + value);
			}
		}
//		if (data.equalsIgnoreCase(MessageXmlConstant.DATA_HP)) {
//			value = determineNeededBits((10000-1)/ConfigKey.getPerceptionPrecisionHp()+1);
//			mcLog.trace("Agents size:" + model.agents().size() + "-->Needed bits:" + value);
//		}
//		if (data.equalsIgnoreCase(MessageXmlConstant.DATA_DAMAGE)) {
//			value = determineNeededBits((1200-1)/ConfigKey.getPerceptionPrecisionDamage()+1);
//			mcLog.trace("Agents size:" + model.agents().size() + "-->Needed bits:" + value);
//		}
		return value;
	}

	private static int determineNeededBits(int size) {
		if(size==0)
			return 0;
		return (int) Math.ceil((Math.log(size) / Math.log(2)));
	}

	//	 private static int getStringToIndex(String headerString) {
	//		  Integer temp = headerToIndex.get(headerString);
	//		  if (temp == null)
	//				if (THROW_EXCEPTION)
	//					 throw new Error("Header '" + headerString + "' dosen't initialise yet!");
	//				else {
	//					 System.err.println("Header '" + headerString + "' dosen't initialise yet!");
	//					 return 0;
	//				}
	//		  else
	//				return temp.intValue();
	//	 }



	public static int getBlockSize(String header,ChannelSystemType channelType) {
		int size = getValidChannelMessages(channelType).bitSize();
		if (blocks.get(header) == null){
			new Error(header +" not exist  in blocks???"+blocks).printStackTrace();
			return 0;
		}

		DataArrayList xmlData = ReadXml.blocks.get(header).data();
		for (int i = 0; i < xmlData.size(); i++) {
			size += xmlData.getValue(i);
		}

		return size;
	}

//	public static String indexToHeader(int index) {
//		return indexToHeader.get(index);
//	}
//
//	public static Integer headerToIndex(String header) {
//		return headerToIndex.get(header);
//	}

	public static ArrayList<XmlBlockContent> blocksIn(Destination destination) {
		return subBlocks.get(destination);
	}

	public static class MessageGroup{
		private final HashMap<String, Integer> headerToIndex = new HashMap<String, Integer>();
		private final HashMap<Integer, String> indexToHeader = new HashMap<Integer, String>();
		private final ArrayList<XmlBlockContent> blockContents=new ArrayList<XmlBlockContent>();
		private int neededBits=0;
		public MessageGroup() {
		}
		public void addBlock(XmlBlockContent block){
			blockContents.add(block);
			headerToIndex.put(block.getHeader(), blockContents.size());
			indexToHeader.put(blockContents.size(),block.getHeader());
			neededBits=determineNeededBits(blockContents.size()+1);
		}
		public int size(){
			return blockContents.size();
		}
		public int bitSize(){
			return neededBits;
		}
		public String indexToHeader(int index){
			return indexToHeader.get(index);
		}
		public int headerToIndex(String header){
			return headerToIndex.get(header);
		}

		@Override
		public String toString() {
			return "[MessageGroup]neededbit:" +bitSize()+" size:"+size()+" indexToHeader:"+indexToHeader+" headerToIndex:"+headerToIndex;
		}
	}
	public static ChannelSystemType getChannelSystemType(Channel channel,Type type){
		if(channel instanceof VoiceChannel)
			return ChannelSystemType.Voice;
		if(type==Type.LowComunication)
			return ChannelSystemType.Low;
		return ChannelSystemType.Normal;
	}
	public static MessageGroup getValidChannelMessages(ChannelSystemType channelType){
		return validChannelMessages.get(channelType);
	}
}
