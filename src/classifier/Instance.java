package classifier;

import java.util.Vector;

/** The class Instance stores, sets and gets String values.
 * 
 * @author Andrii Dzhyrma */
public class Instance {
	private Vector<String> values;

	/** Initializes the instance using an array of Strings.
	 * 
	 * @param values - the array of Strings */
	public Instance(String[] values) {
		this.values = new Vector<String>();
		for (String value : values) {
			this.values.add(value);
		}
	}

	/** Returns value for the input attribute.
	 * 
	 * @param attributeIndex - the attribute index
	 * @return the value */
	public String getValue(Integer attributeIndex) {
		if (attributeIndex != null && attributeIndex >= 0
		    && attributeIndex < this.values.size())
			return this.values.get(attributeIndex);
		return null;
	}

	/** Sets value for the input attribute.
	 * 
	 * @param attributeIndex - the attribute index
	 * @param value - the value to be assigned */
	public void setValue(Integer attributeIndex, String value) {
		if (attributeIndex != null && attributeIndex >= 0
		    && attributeIndex < this.values.size())
			values.set(attributeIndex, value);
	}

	@Override
	public String toString() {
		return "[values=" + this.values + "]";
	}

}
