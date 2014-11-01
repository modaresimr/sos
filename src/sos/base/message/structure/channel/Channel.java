package sos.base.message.structure.channel;

import sos.base.message.structure.NoiseStructure;

/**
 * 
 * @author Ali
 * 
 */
public abstract class Channel {
	 private final int channel;
	 public Channel(int channel) {
		  this.channel = channel;
	 }
	 private NoiseStructure noise;
	 public void setNoise(NoiseStructure noise) {
		  this.noise = noise;
	 }

	 public NoiseStructure getNoise() {
		  return noise;
	 }

	 public int getChannelId() {
		  return channel;
	 }

}
