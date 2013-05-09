package classifier;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

/** The class Instances stores, manages, reads and prints instances.
 * 
 * @author Andrii Dzhyrma */
public class Instances {
	// the map between attribute names and their indexes in the instances
	private Map<String, Integer> attributes;
	// the goal attribute
	private String goalAttribute = null;
	private Integer goalAttributeIndex = null;
	// instances
	private Vector<Instance> instances;
	// possible values for all attributes
	private Vector<Set<String>> possibleValues;

	/** Initializes instances and attributes. */
	public Instances() {
		this.attributes = new HashMap<String, Integer>();
		this.possibleValues = new Vector<Set<String>>();
		this.instances = new Vector<Instance>();
	}

	/** Initializes instances and attributes by reading them from the file.
	 * 
	 * @param fileName - the file path */
	public Instances(String fileName) {
		this();
		readInstances(fileName);
	}

	/** Returns the index of the attribute given.
	 * 
	 * @param attribute - the attribute
	 * @return the index of the attribute. Null - if the given attribute does not
	 *         exist */
	public Integer getAttributeIndex(String attribute) {
		if (this.attributes.keySet().contains(attribute))
			return this.attributes.get(attribute);
		return null;
	}

	/** Returns the map of attributes of the instances and their indexes.
	 * 
	 * @return the map of attributes and their indexes */
	public Map<String, Integer> getAttributeMap() {
		return this.attributes;
	}

	/** Returns the set of all attributes of the instances.
	 * 
	 * @return the set of all attributes */
	public Set<String> getAttributes() {
		return new HashSet<String>(this.attributes.keySet());
	}

	/** Returns the goal attribute.
	 * 
	 * @return the goal attribute */
	public String getGoalAttribute() {
		return this.goalAttribute;
	}

	/** Returns the goal attribute index.
	 * 
	 * @return the goal attribute index */
	public Integer getGoalAttributeIndex() {
		return this.goalAttributeIndex;
	}

	/** Returns the vector of all read instances.
	 * 
	 * @return the vector of an Instance type objects */
	public Vector<Instance> getInstances() {
		return this.instances;
	}

	/** Returns all the possible values for the input attribute.
	 * 
	 * @param attributeIndex- the attribute index
	 * @return all the possible values for the input attribute index. Null is the
	 *         index is out of range */
	public Set<String> getPossibleValues(Integer attributeIndex) {
		if (attributeIndex != null && attributeIndex >= 0
		    && attributeIndex < this.possibleValues.size())
			return this.possibleValues.get(attributeIndex);
		return null;
	}

	/** Prints all the instances. */
	public void print() {
		for (Instance instance : this.instances) {
			System.out.println(instance);
		}
	}

	/** Reads instances from the file.
	 * 
	 * @param fileName - the file path. */
	public void readInstances(String fileName) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(fileName);
			Scanner scanner = new Scanner(fileReader);
			// read attributes using scanner
			if (!scanner.hasNextLine()) {
				// no text data in the file
				System.err.println("No attributes were found...");
				scanner.close();
				fileReader.close();
				return;
			}
			String attributesString = scanner.nextLine();
			String delimiters = "[,]";
			String[] attributes = attributesString.split(delimiters);
			if (attributes.length == 0) {
				// no attributes were found
				System.err.println("Should be at least one attribute in the data.");
				scanner.close();
				fileReader.close();
				return;
			}
			this.attributes = new HashMap<String, Integer>();
			// checks whether all attribute names are different
			int i = 0;
			for (; i < attributes.length
			    && !this.attributes.containsKey(attributes[i]); i++)
				this.attributes.put(attributes[i], i);
			if (i < attributes.length) {
				System.err.println("Some attribute names are the same: "
				    + attributes[i]);
				scanner.close();
				fileReader.close();
				return;
			}
			// set the last attribute as a goal
			setGoalAttribute(attributes[attributes.length - 1]);
			// read instances values
			int row = 1;
			this.instances = new Vector<Instance>();
			this.possibleValues = new Vector<Set<String>>();
			for (int j = 0; j < attributes.length; j++)
				this.possibleValues.add(new HashSet<String>());
			while (scanner.hasNextLine()) {
				row++;
				String valuesString = scanner.nextLine();
				String[] values = valuesString.split(delimiters);
				// amount of values and attributes are not the same
				if (values.length != attributes.length) {
					System.err.println(row + "# row should has " + attributes.length
					    + " values.");
					scanner.close();
					fileReader.close();
					return;
				}
				this.instances.add(new Instance(values));
				for (int j = 0; j < values.length; j++)
					if (!this.possibleValues.get(j).contains(values[j]))
						this.possibleValues.get(j).add(values[j]);
			}
			scanner.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
		} catch (IOException e) {
			System.err.println("File not closed.");
		}
	}

	/** Sets the goal attribute.
	 * 
	 * @param goalAttribute - the goal attribute */
	public void setGoalAttribute(String goalAttribute) {
		if (this.attributes.keySet().contains(goalAttribute)) {
			this.goalAttribute = goalAttribute;
			this.goalAttributeIndex = this.attributes.get(goalAttribute);
		}
	}
}
