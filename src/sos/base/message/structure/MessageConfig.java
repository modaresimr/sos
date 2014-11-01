package sos.base.message.structure;

import java.util.HashMap;

import rescuecore2.config.Config;
import sos.base.message.structure.MessageConstants.ChannelType;
import sos.base.message.structure.channel.Channel;
import sos.base.message.structure.channel.RadioChannel;
import sos.base.message.structure.channel.VoiceChannel;

/**
 * 
 * @author Ali
 * 
 */
public class MessageConfig {
	 private int channelCount;
	 private HashMap<Integer, Channel> allChannels = new HashMap<Integer, Channel>();
	 private HashMap<Integer, VoiceChannel> voiceChannels = new HashMap<Integer, VoiceChannel>();
	 private HashMap<Integer, RadioChannel> radioChannels = new HashMap<Integer, RadioChannel>();
	 private int platoonSubScribeLimit;
	 private int centerSubScribeLimit;

	 public MessageConfig(Config config) {
		  try {
				

				channelCount = config.getIntValue("comms.channels.count");

				for (int i = 0; i < channelCount; i++) {
					 String channelKey = "comms.channels." + i;
					 ChannelType type = config.getValue(channelKey + ".type").equalsIgnoreCase("voice") ? ChannelType.Voice : ChannelType.Radio;
					 Channel channel=null;
					 switch (type) {
					 case Voice:
						  int range=config.getIntValue(channelKey + ".range");
						  int messagesSize=config.getIntValue(channelKey + ".messages.size");
						  int messagesMax=config.getIntValue(channelKey + ".messages.max");
						  channel= new VoiceChannel(i, messagesSize, range, messagesMax);
						  voiceChannels.put(i, (VoiceChannel) channel);
						  break;
					 case Radio:
						  int bandwidth=config.getIntValue(channelKey + ".bandwidth");
						  channel=new RadioChannel(i, bandwidth);
						  radioChannels.put(i, (RadioChannel) channel);
						  break;
					 }
					 if(channel!=null)
						 allChannels.put(i, channel);
					 
					 NoiseStructure noise = new NoiseStructure();

					 try {
						  noise.input.dropout.probability = config.getFloatValue(channelKey + ".noise.input.dropout.p");
						  noise.input.dropout.use = config.getBooleanValue(channelKey + ".noise.input.dropout.use");
					 } catch (Exception e) {
					 }

					 try {
						  noise.input.failure.probability = config.getFloatValue(channelKey + ".noise.input.failure.p");
						  noise.input.failure.use = config.getBooleanValue(channelKey + ".noise.input.failure.use");
					 } catch (Exception e) {
					 }

					 try {
						  noise.output.dropout.probability = config.getFloatValue(channelKey + ".noise.output.dropout.p");
						  noise.output.dropout.use = config.getBooleanValue(channelKey + ".noise.output.dropout.use");
					 } catch (Exception e) {
					 }

					 try {
						  noise.output.failure.probability = config.getFloatValue(channelKey + ".noise.output.failure.p");
						  noise.output.failure.use = config.getBooleanValue(channelKey + ".noise.output.failure.use");
					 } catch (Exception e) {
					 }

					 channel.setNoise(noise);
				}
				setPlatoonSubScribeLimit(Math.min(radioChannels.size(),config.getIntValue("comms.channels.max.platoon")));
				setCenterSubScribeLimit(Math.min(radioChannels.size(),config.getIntValue("comms.channels.max.centre")));
		  } catch (Exception e) {
				e.printStackTrace();
		  }
		  
	 }

	 public void setPlatoonSubScribeLimit(int platoonSubScribeLimit) {
		  this.platoonSubScribeLimit = platoonSubScribeLimit;
	 }

	 public int getPlatoonSubScribeLimit() {
		  return platoonSubScribeLimit;
	 }

	 public void setCenterSubScribeLimit(int centerSubScribeLimit) {
		  this.centerSubScribeLimit = centerSubScribeLimit;
	 }

	 public int getCenterSubScribeLimit() {
		  return centerSubScribeLimit;
	 }

	 public HashMap<Integer, RadioChannel> radioChannels() {
		  return radioChannels;
	 }
	 public HashMap<Integer, VoiceChannel> voiceChannels() {
		  return voiceChannels;
	 }
	 @Override
	 public String toString() {
	     return "platoonSubScribeLimit:"+platoonSubScribeLimit+" centerSubScribeLimit:"+centerSubScribeLimit+" RadioChannels:"+radioChannels()+" VoiceChannels:"+voiceChannels();
	 }
//
//	 public int getVoiceChannelCount() {
//		  return voiceChannels.size();
//	 }
//
//	 public int getRadioChannelCount() {
//		  return radioChannels.size();
//	 }

	public void setAllChannels(HashMap<Integer, Channel> allChannels) {
		this.allChannels = allChannels;
	}

	public HashMap<Integer, Channel> getAllChannels() {
		return allChannels;
	}

//	 public int getCentersCount() {
//		  // TODO Auto-generated method stub
//		  return 2;
//	 }
//	 public int getAgentsCount() {
//		  // TODO Auto-generated method stub
//		  return 2;
//	 }
	 
}
