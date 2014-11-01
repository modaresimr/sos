package sos.base.message.structure.blocks;

import sos.base.message.structure.MessageConstants.Destination;
import sos.base.message.structure.MessageConstants.ChannelSystemType;

/**
 *
 * @author Ali
 *
 */
public class XmlBlockContent {
	 private int priority;
	private final DataArrayList data;
	 private String header;
	 private String dataKey;
	 private int maxSize;
	 private Destination destination;
	private ChannelSystemType[] validChannels;
	public XmlBlockContent(int size, String header) {
		data = new DataArrayList(size);
		  this.setHeader(header);
	 }

	 public void setPriority(int priority) {
		  this.priority = priority;
	 }

	 public int getPriority() {
		  return priority;
	 }

	public DataArrayList data() {
		  return data;
	 }

	 public void setHeader(String header) {
		  this.header = header;
	 }

	 public String getHeader() {
		  return header;
	 }

	 @Override
	public String toString() {
		  return "Header:[" + header + "] Destination:["+destination+"] MaxSize:'" + maxSize + "' Priority:'" + priority +"' Key:["+getDataKey()+ "] Data:'" + data + "'\n";
	 }


	 public void addData(String name, int value) {
		  data.put(name, value);
	 }

	 public void setMaxSize(int maxSize) {
		  this.maxSize = maxSize;
	 }

	 public int getMaxSize() {
		  return maxSize;
	 }

	 public void setDataKey(String dataKey) {
		  this.dataKey = dataKey;
	 }

	 public String getDataKey() {
		  return dataKey;
	 }

	 public void setDestination(Destination destination) {
		  this.destination = destination;
	 }

	 public Destination getDestination() {
		  return destination;
	 }

	public ChannelSystemType[] getValidChannels() {
		return validChannels;
	}

	public void setValidChannels(ChannelSystemType[] validChannels) {
		this.validChannels = validChannels;

	}

}
