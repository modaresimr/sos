package sos.base.message.structure.blocks;


/**
 * 
 * @author Ali
 * 
 */
public interface SOSMessageBlock {
	public void setHeader(String header);
	
	public String getHeader();
	public void addData(String key, int value);
	public int getData(String key);
	
	// public Set<Entry<String, Integer>> getAllData();
	public DataArrayList getData();
	
	public Integer getKeyData();
	@Override
	public int hashCode();
}
