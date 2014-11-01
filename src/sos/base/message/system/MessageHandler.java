/**
 * @author Ali
 */
package sos.base.message.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import rescuecore2.messages.Command;
import rescuecore2.standard.components.StandardAgent;
import rescuecore2.standard.messages.AKSpeak;
import sample.SOSAbstractSampleAgent;
import sos.base.SOSAgent;
import sos.base.entities.Civilian;
import sos.base.entities.StandardEntity;
import sos.base.message.MessageBuffer;
import sos.base.message.ReadXml;
import sos.base.message.structure.MessageConstants;
import sos.base.message.structure.SOSBitArray;
import sos.base.message.structure.blocks.AbstractMessageBlock;
import sos.base.message.structure.blocks.DataArrayList;
import sos.base.message.structure.blocks.DynamicSizeMessageBlock;
import sos.base.message.structure.blocks.MessageBlock;
import sos.base.message.structure.channel.Channel;
import sos.base.util.TimeNamayangar;
import sos.base.util.sosLogger.SOSLoggerSystem;

public class MessageHandler implements MessageConstants {
//	private SOSLoggerSystem bitMessageLog;
	private final StandardAgent<?> agent;
	private SOSLoggerSystem mcLog;
	private SOSLoggerSystem mtLog;
	private MessageSystem messageSystem;
	private MessageBuffer messages;

	//	private final SOSAgent<? extends StandardEntity> sosAgent;

	public MessageHandler(StandardAgent<?> agent) {
		this.agent = agent;
		if(agent instanceof SOSAgent<?>){
			messages=((SOSAgent<?>)agent).messages;
			messageSystem= ((SOSAgent<?>)agent).messageSystem;
			this.mtLog = ((SOSAgent<?>)agent).sosLogger.messageTransmit;
			this.mcLog = ((SOSAgent<?>)agent).sosLogger.messageContent;
		}else if(agent instanceof SOSAbstractSampleAgent<?>){
			messages=((SOSAbstractSampleAgent<?>)agent).messages;
			messageSystem= ((SOSAbstractSampleAgent<?>)agent).messageSystem;
			mtLog=((SOSAbstractSampleAgent<?>)agent).sosLogger;
			mcLog=((SOSAbstractSampleAgent<?>)agent).sosLogger;
		}
		if (agent instanceof SOSAgent<?>) {
//			SOSAgent<?> sosAgent = (SOSAgent<?>) agent;
//			bitMessageLog = new SOSLoggerSystem(sosAgent.me(), "bitmessage", true, OutputType.File, true);
//			sosAgent.sosLogger.addToAllLogType(bitMessageLog);
		}
	}


	public void handleReceive(Collection<Command> heard) {
		// mcLog.setOutputType(OutputType.Both);

		TimeNamayangar messageSystemHandleTime = new TimeNamayangar("Time 1", false, false);
		TimeNamayangar hearTime = new TimeNamayangar("Time 2", false, false);
		HashSet<Integer> recivedMessages = new HashSet<Integer>();
		int receiveSize = 0;
		int usefulReceiveSize = 0;
		HashMap<String, Integer> timePerHeader=new HashMap<String, Integer>();
		HashMap<String, Integer> countPerHeader=new HashMap<String, Integer>();
		StringBuffer allRecivedMessagesLog = new StringBuffer(1000);
		try {
			messageSystemHandleTime.start();
			for (Command command : heard) {
				try {
					if (command instanceof AKSpeak) {
						AKSpeak speak = (AKSpeak) command;
						Channel channel = messageSystem.getMessageConfig().getAllChannels().get(speak.getChannel());
						ChannelSystemType channelType = ReadXml.getChannelSystemType(channel, messageSystem.type);
						StandardEntity sender = agent.model().getEntity(command.getAgentID());
//						StringBuilder logBuffer=new StringBuilder();
						// mtLog.debug("-Received " + speak.getContent().length + " byte from " + sender);
						if (!(sender instanceof Civilian || sender == null)) {
							SOSBitArray bitArray = new SOSBitArray(speak.getContent());
//							bitMessageLog.trace("Get " + speak.getContent().length + " byte from " + sender, bitArray);

							if (bitArray.length() == 0)
								mtLog.heavyTrace("Dropout Noise Detected! Form '" + sender + "'");
							
							
							int bitPosition = 0;
							int headerSize = ReadXml.getValidChannelMessages(channelType).bitSize();
							while (bitPosition + headerSize<= bitArray.length()) {
								int headerIndex = bitArray.get(bitPosition, headerSize);
								if (headerIndex == 0){
									//if(bitArray.get(bitPosition, bitArray.length()-bitPosition)==0)
										break;
								}
								String header = ReadXml.getValidChannelMessages(channelType).indexToHeader(headerIndex);
								if (header == null)
									break;
//								bitMessageLog.debug("Header:" + header + "(" + headerIndex + ")");
								bitPosition += headerSize;

								DataArrayList xmlData = ReadXml.blocks.get(header).data();
								DataArrayList data = new DataArrayList(xmlData.size());
//								mtLog.trace("Header:" + header + "(" + headerIndex + ") has this setting:" + "(" + xmlData.size() + ")" + xmlData);
//								bitMessageLog.trace("Header:" + header + "(" + headerIndex + ") has this setting:" + "(" + xmlData.size() + ")" + xmlData);

								for (int i = 0; i < xmlData.size(); i++) {
									data.put(xmlData.getKey(i), bitArray.get(bitPosition, xmlData.getValue(i)));
//									bitMessageLog.heavyTrace("i:" + i + " key:" + xmlData.getKey(i) + "=" + bitArray.get(bitPosition, xmlData.getValue(i)) + " bitsize=" + xmlData.getValue(i));
									bitPosition += xmlData.getValue(i);
								}
								int messageBlockHashcode = AbstractMessageBlock.getHash(header, data);
								SOSBitArray dynamicBitArray = null;
								int dynamicBitSize = DynamicSizeMessageBlock.getDynamicBitSize(header, data, agent.model());
								if (dynamicBitSize > 0) {

									dynamicBitArray = bitArray.getBit(bitPosition, dynamicBitSize);
//									bitMessageLog.heavyTrace("dynamic bit size:" + dynamicBitSize + " bitarray:" + dynamicBitArray);
								}
								messageSystemHandleTime.stop();
								bitPosition += dynamicBitSize;
								// ReceiveMessageBlock rmb = new ReceiveMessageBlock(sender, channel, bitArray, bitPosition, tm4);
								if (recivedMessages.add(messageBlockHashcode)) {
									usefulReceiveSize++;
									allRecivedMessagesLog.append("Header:(" + headerIndex + ")" + header + " data:" + data + " dynamicBits:" + dynamicBitArray + " sender:" + sender + " Channel=" + channel + "Hashcode=" + messageBlockHashcode + " ,");
									messageSystemHandleTime.stop();
									hearTime.start();
									long startHear = System.currentTimeMillis();
									agent.hear(header, data, dynamicBitArray, sender, channel);
									long endHear = System.currentTimeMillis();
									Integer lastTime = timePerHeader.get(header);
									Integer lastCount = countPerHeader.get(header);
									if(lastTime==null)lastTime=0;
									if(lastCount==null)lastCount=0;
									timePerHeader.put(header, (int) (lastTime+(endHear-startHear)));
									countPerHeader.put(header, lastCount+1);
									hearTime.stop();
									messageSystemHandleTime.start();
									if (messageSystem.getMine().isMiddleMan()) {
										if (dynamicBitArray == null)
											messages.add(new MessageBlock(header, data));
										else
											messages.add(new DynamicSizeMessageBlock(header, data, dynamicBitArray));
									} else
										// noisy database only used for nonMiddleMan Agents
										messageSystem.noisyMessageDatabase.checkAndRemove(messageBlockHashcode);
								}
								receiveSize++;
								// bitPosition += ReadXml.getBlockSize(header);
								// receiveMessageList.add(rmb);
							}
						} else {
							if (sender == null) {
								Civilian c = new Civilian(speak.getAgentID());
								agent.model().addEntity(c);
								sender = c;
							}
							agent.hear(new String(speak.getContent(), "UTF-8"), new DataArrayList(0), null, sender, channel);
						}
					} else
						mtLog.error(new Error("UnExcepted Hear " + command));
				} catch (Exception e) {
					mtLog.error(e);
				}
			}
			messageSystemHandleTime.stop();
			mcLog.debug("Received " + receiveSize + " block. usefulReceive block=" + usefulReceiveSize);
			if (receiveSize == 0 && messageSystem.type != Type.NoComunication && agent.model().time() > agent.FREEZE_TIME + messageSystem.getNormalMessageDelay() && !messageSystem.getMine() .isUnusedAgent()) {
				mtLog.warn("No Message Received!!!! it may a problem in subscribe handle in kernel");
			}
			mtLog.trace("MessageSystemHandleTime= " + messageSystemHandleTime);
			mtLog.trace("HearTime= " + hearTime);
			SOSAgent.currentAgent().sosLogger.act.trace("MessageSystemHandleTime= " + messageSystemHandleTime);
			SOSAgent.currentAgent().sosLogger.act.trace("HearTime= " + hearTime);
			mtLog.trace("CountPerHeader= " + countPerHeader);
			mtLog.trace("HearTimePerHeader= " + timePerHeader);
		} catch (Exception e) {
			mtLog.error(e);
		}

		mcLog.debug("Receive messages(size=" + usefulReceiveSize + ")", allRecivedMessagesLog);
		mtLog.debug("Receive messages(size=" + usefulReceiveSize + ")", allRecivedMessagesLog);

	}







	/////////////////////test/////////////

	public ArrayList<MessageBlock> decode(byte[] content, ChannelSystemType channelType) {
		ArrayList<MessageBlock> received=new ArrayList<MessageBlock>();
		SOSBitArray bitArray = new SOSBitArray(content);
		int bitPosition = 0;
		int headerSize = ReadXml.getValidChannelMessages(channelType).bitSize();
		mcLog.trace("decode message handeler:", "header size:"+headerSize+" channeltype:"+channelType);
		try{
		while (bitPosition + headerSize <= bitArray.length()) {
			int headerIndex = bitArray.get(bitPosition, headerSize);
			if (headerIndex == 0) {
				//if(bitArray.get(bitPosition, bitArray.length()-bitPosition)==0)
				break;
			}

			String header = ReadXml.getValidChannelMessages(channelType).indexToHeader(headerIndex);
			if (header == null)
				break;
			//			bitMessageLog.debug("Header:" + header + "(" + headerIndex + ")");
			bitPosition += headerSize;

			DataArrayList xmlData = ReadXml.blocks.get(header).data();
			DataArrayList data = new DataArrayList(xmlData.size());
			mtLog.trace("Header:" + header + "(" + headerIndex + ") has this setting:" + "(" + xmlData.size() + ")" + xmlData);
			mcLog.trace("Header:" + header + "(" + headerIndex + ") has this setting:" + "(" + xmlData.size() + ")" + xmlData);
			//			bitMessageLog.trace("Header:" + header + "(" + headerIndex + ") has this setting:" + "(" + xmlData.size() + ")" + xmlData);

			for (int i = 0; i < xmlData.size(); i++) {
				data.put(xmlData.getKey(i), bitArray.get(bitPosition, xmlData.getValue(i)));
				//				bitMessageLog.heavyTrace("i:" + i + " key:" + xmlData.getKey(i) + "=" + bitArray.get(bitPosition, xmlData.getValue(i)) + " bitsize=" + xmlData.getValue(i));
				bitPosition += xmlData.getValue(i);
			}
			int messageBlockHashcode = AbstractMessageBlock.getHash(header, data);
			SOSBitArray dynamicBitArray = null;
			int dynamicBitSize = DynamicSizeMessageBlock.getDynamicBitSize(header, data, agent.model());
			if (dynamicBitSize > 0) {
				dynamicBitArray = bitArray.getBit(bitPosition, dynamicBitSize);
				//				bitMessageLog.heavyTrace("dynamic bit size:" + dynamicBitSize + " bitarray:" + dynamicBitArray);
			}
			received.add(new DynamicSizeMessageBlock(header,data,bitArray));
			bitPosition += dynamicBitSize;

			mcLog.trace("hash:" + messageBlockHashcode + " header:" + header + " data:" + data + " dynamicBitArray:" + dynamicBitArray);
		}
		}catch (Exception e) {
			mcLog.error(e);
		}
		return received;
	}


}
