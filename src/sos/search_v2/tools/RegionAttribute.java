package sos.search_v2.tools;

public class RegionAttribute {
	private String attName;
	private String value;

	public RegionAttribute(String name, String value) {
		this.value = value;
		this.attName = name;
	}

	public String getAttributeName() {
		return attName;
	}

	public void setAttributeName(String name) {
		this.attName = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
