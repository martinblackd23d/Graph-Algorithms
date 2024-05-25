import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

// Class DelivD does the work for deliverable DelivD of the Prog340

public class DelivD {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivD( File in, Graph gr ) {
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


		// Init matrices
		ArrayList<Node> nodes = g.getNodeList();

		int[][] dist = new int[nodes.size()][nodes.size()];
		int[][] next = new int[nodes.size()][nodes.size()];
		int[][] pred = new int[nodes.size()][nodes.size()];
		int[][] predNext = new int[nodes.size()][nodes.size()];

		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				dist[i][j] = Integer.MAX_VALUE;
				pred[i][j] = -1;
				// Set distance to self to 0
				if (i == j) {
					dist[i][j] = 0;
					continue;
				}
				// Set distance to direct neighbors
				for (Edge e : nodes.get(i).getOutgoingEdges()) {
					if (e.getHead().equals(nodes.get(j))) {
						dist[i][j] = e.getDist();
						pred[i][j] = i;
						break;
					}
				}
			}
		}

		// Print matrices
		if (nodes.size() < 8)
			print(nodes, dist, pred, 0, output);


		// Floyd-Warshall
		for (int k = 0; k < nodes.size(); k++) {
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = 0; j < nodes.size(); j++) {
					if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE && dist[i][k] + dist[k][j] < dist[i][j]) {
						next[i][j] = dist[i][k] + dist[k][j];
						predNext[i][j] = pred[k][j];
					} else {
						next[i][j] = dist[i][j];
						predNext[i][j] = pred[i][j];
					}
				}
			}
			dist = next;
			pred = predNext;

			// Print matrices
			if (nodes.size() < 8 || k == nodes.size() - 1)
				print(nodes, dist, pred, k + 1, output);
		}

		output.close();
	}

	void print(ArrayList<Node> nodes, int[][] dist, int[][] pred, int k, PrintWriter output) {
		// Headers
		System.out.print("Iteration " + k);
		output.print("Iteration " + k);
		if (k == nodes.size()) {
			System.out.println(" (Final)");
			output.println(" (Final)");
		} else {
			System.out.println();
			output.println();
		}
		System.out.print("d\t");
		output.print("d\t");
		for (int i = 0; i < nodes.size(); i++) {
			System.out.print(nodes.get(i).getAbbrev() + "\t");
			output.print(nodes.get(i).getAbbrev() + "\t");
		}
		// Distance matrix
		System.out.println();
		output.println();
		for (int i = 0; i < nodes.size(); i++) {
			System.out.print(nodes.get(i).getAbbrev() + "\t");
			output.print(nodes.get(i).getAbbrev() + "\t");
			for (int j = 0; j < nodes.size(); j++) {
				if (dist[i][j] == Integer.MAX_VALUE) {
					System.out.print("~\t");
					output.print("~\t");
				} else {
					System.out.print(dist[i][j] + "\t");
					output.print(dist[i][j] + "\t");
				}
			}
			System.out.println();
			output.println();
		}
		System.out.println();
		output.println();

		// Headers
		System.out.print("p\t");
		output.print("p\t");
		for (int i = 0; i < nodes.size(); i++) {
			System.out.print(nodes.get(i).getAbbrev() + "\t");
			output.print(nodes.get(i).getAbbrev() + "\t");
		}
		System.out.println();
		output.println();
		// Predecessor matrix
		for (int i = 0; i < nodes.size(); i++) {
			System.out.print(nodes.get(i).getAbbrev() + "\t");
			output.print(nodes.get(i).getAbbrev() + "\t");
			for (int j = 0; j < nodes.size(); j++) {
				if (pred[i][j] == -1) {
					System.out.print("~\t");
					output.print("~\t");
				} else {
					System.out.print(nodes.get(pred[i][j]).getAbbrev() + "\t");
					output.print(nodes.get(pred[i][j]).getAbbrev() + "\t");
				}
			}
			System.out.println();
			output.println();
		}
		System.out.println();
		output.println();
	}
}


