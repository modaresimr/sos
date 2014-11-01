package sos.base.message.structure.channel;

/**
 * 
 * @author Ali
 * 
 */
public class VoiceChannel extends Channel {
	 private final int messagesSize;
	 private final int range;
	 private final int messagesMax;

	 public VoiceChannel(int channel, int messagesSize, int range, int messagesMax) {
		  super(channel);
		  this.messagesSize = messagesSize;
		  this.range = range;
		  this.messagesMax = messagesMax;
	 }

	 public int getMessagesSize() {
		  return messagesSize;
	 }

	 public int getRange() {
		  return range;
	 }

	 public int getMessagesMax() {
		  return messagesMax;
	 }

	 @Override
	 public String toString() {
		  return "Channel:" + getChannelId() + " Type:Voice " + "MessagesSize:" + getMessagesSize() + " MessageMax=" + getMessagesMax() + " Range:" + getRange() /*+ " Noise:" + getNoise()*/;
	 }

}
