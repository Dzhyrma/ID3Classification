package main;

import classifier.ID3;
import classifier.Instances;

public class Main {

	/** @param args */
	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			System.out.println("Sorry... No arguments found.");
			return;
		}
		ID3 classifier = new ID3();
		Instances trainSet = new Instances(args[0]);
		classifier.build(trainSet);
		System.out.println("\nThe ID3 tree:");
		classifier.printTree();
		if (args.length > 1) {
			Instances predictSet = new Instances(args[1]);
			classifier.predict(predictSet);
			System.out.println("\nPredictions:");
			predictSet.print();
		}
	}

}
