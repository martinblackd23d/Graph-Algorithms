import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// Class DelivB does the work for deliverable DelivB of the Prog340

public class DelivB {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivB( File in, Graph gr ) {
		inputFile = in;
		g = gr;
		
		// Get output file name.
		String inputFileName = inputFile.toString();
		String baseFileName = inputFileName.substring( 0, inputFileName.length()-4 ); // Strip off ".txt"
		String outputFileName = baseFileName.concat( "_out.txt" );
		outputFile = new File( outputFileName );
		if ( outputFile.exists() ) {    // For retests
			outputFile.delete();
		}
		
		try {
			output = new PrintWriter(outputFile);			
		}
		catch (Exception x ) { 
			System.err.format("Exception: %s%n", x);
			System.exit(0);
		}


		// Sort the nodes
		ArrayList<Node> nodeList = g.getNodeList();

		Collections.sort(nodeList, new Comparator<Node>() {
			public int compare(Node n1, Node n2) {
				if (Float.parseFloat(n1.getVal()) < Float.parseFloat(n2.getVal()))
					return -1;
				else if (Float.parseFloat(n1.getVal()) > Float.parseFloat(n2.getVal()))
					return 1;
				else
					return 0;
			}
		});

		// Init arrays for storing b(i, j) and r(i, j)
		int[][] b = new int[nodeList.size()][nodeList.size()];
		int[][] r = new int[nodeList.size()][nodeList.size()];

		// Iterate through i and j to calculate b(i, j) and r(i, j)
		for (int i = 0; i < nodeList.size(); i++) {
			for (int j = i + 1; j < nodeList.size(); j++) {

				// case 0
				if (i == 0 && j == 1) {
					b[i][j] = d(nodeList.get(i), nodeList.get(j));
					r[i][j] = 0;
					continue;
				}

				// case 1
				if (i < j - 1) {
					b[i][j] = b[i][j - 1] + d(nodeList.get(j - 1), nodeList.get(j));
					r[i][j] = j - 1;
					continue;
				}

				// case 2
				if (i + 1 == j) {
					b[i][j] = b[0][j - 1] + d(nodeList.get(0), nodeList.get(j));
					r[i][j] = 0;
					for (int k = 1; k < j - 1; k++) {
						int temp = b[k][j - 1] + d(nodeList.get(k), nodeList.get(j));
						if (temp < b[i][j]) {
							b[i][j] = temp;
							r[i][j] = k;
						}
					}
					continue;
				}
			}
		}

		// Print the length
		int result = b[nodeList.size() - 2][nodeList.size() - 1] + d(nodeList.get(nodeList.size() - 2), nodeList.get(nodeList.size() - 1));
		System.out.println("Shortest bitonic tour has distance " + result);
		output.println("Shortest bitonic tour has distance " + result);

		// Reconstruct the path in one direction
		ArrayList<Node> path = new ArrayList<>();

		path.add(nodeList.get(nodeList.size() - 1));
		int j = nodeList.size() - 1;
		int i = r[nodeList.size() - 2][j];
		int k = r[i][j];
		while (k > 0) {
			if (i > j) {
				i = r[j][i];
				continue;
			}
			k = r[i][j];

			path.add(nodeList.get(k));
			nodeList.remove(k);

			j = k;
		}

		// Add the remaining nodes
		for (Node n : nodeList) {
			path.add(n);
		}

		// Print the path
		System.out.print("Tour is ");
		output.print("Tour is ");
		for (Node n : path) {
			System.out.print(n.getAbbrev() + " ");
			output.print(n.getAbbrev() + " ");
		}
		System.out.println();
		output.println();
		output.flush();
		output.close();
	}

	// Calculate the distance between two nodes
	private int d(Node n1, Node n2) {
		for (Edge e : n1.getOutgoingEdges()) {
			if (e.getHead().equals(n2)) {
				return e.getDist();
			}
		}
		return 0;
	}
}

