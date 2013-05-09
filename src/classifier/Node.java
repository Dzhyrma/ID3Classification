package classifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/** The class Node helps to build a tree for the ID3 algorithm
 * 
 * @author Andrii Dzhyrma */
public class Node {
	private String attributeName;
	private Vector<Instance> currentInstances;
	private Map<String, Node> nodes;

	/** Initializes the node with the instances those store correspondent attribute
	 * values.
	 * 
	 * @param currentInstances - the vector of instances */
	public Node(Vector<Instance> currentInstances) {
		this.currentInstances = currentInstances;
		this.nodes = new HashMap<String, Node>();
	}

	/* prints the tree */
	private void print(String offset, int goalAttribute) {
		if (this.nodes != null && !this.nodes.isEmpty()) {
			System.out.println();
			Set<String> keys = this.nodes.keySet();
			for (String key : keys) {
				System.out.print(offset + attributeName + " = " + key);
				this.nodes.get(key).print(offset + "|  ", goalAttribute);
			}
		} else
			System.out.println(": " + getPrediction(goalAttribute));
	}

	/** Adds a new node to the current node.
	 * 
	 * @param value - the value of the attribute on the branch
	 * @param node - the node to be added */
	public void addNode(String value, Node node) {
		nodes.put(value, node);
	}

	/** @return the name of the attribute on this node */
	public String getAttributeName() {
		return attributeName;
	}

	/** @return the vector of the instances on this node */
	public final Vector<Instance> getCurrentInstances() {
		return this.currentInstances;
	}

	/** Returns an entropy value of the current node.
	 * 
	 * @param attributeIndex - the attribute for which the entropy will be
	 *          calculated
	 * @param possibleValues - the possible values of this attribute
	 * @return the entropy value */
	public double getEntropy(Integer attributeIndex, Set<String> possibleValues) {
		double total = this.currentInstances.size();
		double result = 0;
		if (possibleValues == null || attributeIndex == null) {
			System.err.println("Entropy ERROR!");
			return 0;
		}
		// calculate entropy value using a general formula
		for (String value : possibleValues) {
			double probability = 0;
			for (Instance instance : this.currentInstances) {
				if (instance.getValue(attributeIndex).equals(value))
					probability++;
			}
			if (probability > 0) {
				probability /= total;
				probability *= Math.log(probability);
				result -= probability;
			}
		}
		return result;
	}

	/** Returns number of instances where the given attribute has the given value.
	 * 
	 * @param attributeIndex - the attribute index
	 * @param value - the attribute value
	 * @return the number of instances */
	public int getInstancesAmountWithValue(int attributeIndex, String value) {
		int result = 0;
		for (Instance instance : this.currentInstances) {
			if (instance.getValue(attributeIndex).equals(value))
				result++;
		}
		return result;
	}

	/** @return the amount of the current instances on this node */
	public int getInstancesLength() {
		if (this.currentInstances == null)
			return 0;
		return this.currentInstances.size();
	}

	/** Returns a node on the branch with the given value
	 * 
	 * @param value - the value of the attribute on the current attribute
	 * @return the node */
	public Node getNode(String value) {
		return nodes.get(value);
	}

	/** Returns the prediction for the attribute with the given index calculating
	 * instances and finding the maximum time occurred value.
	 * 
	 * @param attributeIndex - the attribute index
	 * @return the predicted value */
	public String getPrediction(int attributeIndex) {
		if (this.currentInstances.size() == 0)
			return null;
		// this variable will store amount of occurrence for each value
		Map<String, Integer> counter = new HashMap<String, Integer>();
		for (Instance instance : this.currentInstances) {
			String value = instance.getValue(attributeIndex);
			if (value != null) {
				if (counter.containsKey(value))
					counter.put(value, counter.get(value) + 1);
				else
					counter.put(value, 1);
			}
		}
		if (counter.isEmpty())
			return null;
		// find the maximum one
		Entry<String, Integer> max = null;
		for (Entry<String, Integer> entry : counter.entrySet()) {
			if (entry.getValue() != null
			    && (max == null || max.getValue() == null || max.getValue() < entry
			        .getValue()))
				max = entry;
		}
		return max.getKey();
	}

	/** For the given instance, attribute map, current goal attribute index and the
	 * index of the goal attribute on building predicts the value goes through all
	 * the tree.
	 * 
	 * @param instance - the instance to predict
	 * @param map - the map between attribute names and their indexes in the
	 *          instance
	 * @param currentGoalAttributeIndex - the index of the goal attribute in the
	 *          instance
	 * @param previousGoalAttributeIndex - the index of the goal attribute in the
	 *          classifier built */
	public void predict(Instance instance, Map<String, Integer> map,
	    Integer currentGoalAttributeIndex, Integer previousGoalAttributeIndex) {
		if (map == null || instance == null)
			return;
		if (map.containsKey(this.attributeName)) {
			Integer attributeIndex = map.get(this.attributeName);
			if (attributeIndex != null) {
				String value = instance.getValue(attributeIndex);
				if (this.nodes.containsKey(value)) {
					Node node = this.nodes.get(value);
					if (node != null) {
						// if we have this attribute, value and node in the tree exists,
						// then go deeper
						node.predict(instance, map, currentGoalAttributeIndex,
						    previousGoalAttributeIndex);
						return;
					}
				}
			}
		}
		// assign value if we cannot go deeper
		instance.setValue(currentGoalAttributeIndex,
		    getPrediction(previousGoalAttributeIndex));
	}

	/** Prints the built tree.
	 * 
	 * @param goalAttributeIndex - the goal attribute index */
	public void print(int goalAttributeIndex) {
		this.print("", goalAttributeIndex);
	}

	/** Sets the name of the attribute for the current node.
	 * 
	 * @param attributeName - the attribute name */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
}
