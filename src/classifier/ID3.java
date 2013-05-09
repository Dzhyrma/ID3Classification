package classifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/** The class ID3 builds and prints the tree. Also has method to predict given
 * instances.
 * 
 * @author Andrii Dzhyrma */
public class ID3 {
	private static final double EPSILON = 0.00000001;
	// root node of the tree
	private Node rootNode;
	// training set of instances to build a tree
	private Instances trainingSet;

	public ID3() {
	}

	/** Generates the tree recursively.
	 * 
	 * @param node - the node for which we should built branches
	 * @param notUsedAttributes - the not used attributes on this path
	 * @param isLogging - the log parameter. If true, we will print algorithm of
	 *          building on the screen */
	private void generateBranches(Node node, Set<String> notUsedAttributes,
	    boolean isLogging) {
		// calculate the current entropy
		double currentEntropy = node.getEntropy(
		    trainingSet.getGoalAttributeIndex(),
		    trainingSet.getPossibleValues(trainingSet.getGoalAttributeIndex()));
		if (isLogging)
			System.out.println("Go deeper.\nEntropy of the current node is "
			    + currentEntropy);
		// if it is zero, do not go deeper
		if (Math.abs(currentEntropy) < EPSILON) {
			if (isLogging)
				System.out
				    .println("Do not need to branch this node.\nGo back to the parent node.");
			return;
		}
		// otherwise branch this node
		if (isLogging)
			System.out.println("Branch this node.");
		// create a result variable
		Map<String, Node> result = null;
		double maxInfoGain = 0;
		// calculate for each not used attribute it's infogain and store the maximum
		// one in the result variable
		for (String notUsedAttribute : notUsedAttributes) {
			if (isLogging)
				System.out.println("Check the attribute: '" + notUsedAttribute + "'.");
			double infoGain = 0;
			Set<String> newFreeAttributes = new HashSet<String>(notUsedAttributes);
			newFreeAttributes.remove(notUsedAttribute);
			Integer attributeIndex = trainingSet.getAttributeIndex(notUsedAttribute);
			if (attributeIndex == null)
				continue;
			Map<String, Node> branches = new HashMap<String, Node>();
			Set<String> possibleValues = trainingSet
			    .getPossibleValues(attributeIndex);
			if (possibleValues == null)
				continue;
			// calculate the current infogain using entropy
			for (String value : possibleValues) {
				Vector<Instance> newInstances = new Vector<Instance>();
				Vector<Instance> currentInstances = node.getCurrentInstances();
				for (Instance instance : currentInstances) {
					if (instance.getValue(attributeIndex).equals(value))
						newInstances.add(instance);
				}
				if (newInstances.size() > 0) {
					Node newNode = new Node(newInstances);
					branches.put(value, newNode);
					infoGain -= newNode.getInstancesLength()
					    * newNode.getEntropy(trainingSet.getGoalAttributeIndex(),
					        trainingSet.getPossibleValues(trainingSet
					            .getGoalAttributeIndex()));
				}
			}
			// if we had possibility to branch and infogain is bigger than maximum,
			// assign found branches to the result
			if (!branches.isEmpty()) {
				infoGain /= node.getInstancesLength();
				infoGain += currentEntropy;
				if (isLogging)
					System.out.println("Infogain of this attribute is: " + infoGain);
				if (infoGain > maxInfoGain) {
					if (isLogging)
						System.out.println("The best attribute now is '" + notUsedAttribute
						    + "'.");
					maxInfoGain = infoGain;
					result = branches;
					node.setAttributeName(notUsedAttribute);
				}
			} else {
				if (isLogging)
					System.out.println("This attribute does not have branches...");
			}
		}
		// for each branch recursively call this method to generate their branches
		// also
		Set<String> newFreeAttributes = new HashSet<>(notUsedAttributes);
		newFreeAttributes.remove(node.getAttributeName());
		if (result != null) {
			Set<String> values = result.keySet();
			for (String value : values) {
				Node newNode = result.get(value);
				node.addNode(value, newNode);
				if (isLogging)
					System.out.println("Check the path with the attribute '"
					    + node.getAttributeName() + "' and its value '" + value + "'.");
				generateBranches(newNode, newFreeAttributes, isLogging);
			}
		}
		if (isLogging)
			System.out.println("Go back to the parent node.");
	}

	/** For the given instances builds a prediction tree.
	 * 
	 * @param instances - the instances */
	public void build(Instances instances) {
		this.trainingSet = instances;
		Set<String> freeAttributes = instances.getAttributes();
		freeAttributes.remove(instances.getGoalAttribute());
		this.rootNode = new Node(instances.getInstances());
		generateBranches(this.rootNode, freeAttributes, true);
	}

	/** Predicts the CLASS values using the built tree.
	 * 
	 * @param instances - the instances to predict */
	public void predict(Instances instances) {
		instances.setGoalAttribute(this.trainingSet.getGoalAttribute());
		Vector<Instance> instancesToPredict = instances.getInstances();
		for (Instance instance : instancesToPredict) {
			rootNode.predict(instance, instances.getAttributeMap(),
			    instances.getGoalAttributeIndex(),
			    this.trainingSet.getGoalAttributeIndex());
		}
	}

	/** Prints the built tree on the screen. */
	public void printTree() {
		rootNode.print(this.trainingSet.getGoalAttributeIndex());
	}

}
