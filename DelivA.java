import java.io.*;
import java.util.*;

// Class DelivA does the work for deliverable DelivA of the Prog340

public class DelivA {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivA( File in, Graph gr ) {
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

		//init variables
		ArrayList<Node> nodeList = g.getNodeList();
		ArrayList<Edge> edgeList;

		String path = "Path";
		int total = 0;
		Node n = null;
		Edge e = null;

		//find first node
		for (int i = 0; i < nodeList.size(); i++) {
			if (Integer.parseInt(nodeList.get(i).getVal()) == 1) {
				n = nodeList.get(i);
				break;
			}
		}
		path += " " + n.getAbbrev();

		//main loop
		for (int i = 1; i <= nodeList.size(); i++) {
			//find next edge
			edgeList = n.getOutgoingEdges();
			e = null;
			for (int j = 0; j < edgeList.size(); j++) {
				if (Integer.parseInt(edgeList.get(j).getHead().getVal()) == Integer.parseInt(n.getVal()) % nodeList.size() + 1) {
					e = edgeList.get(j);
					break;
				}
			}

			//process edge
			n = e.getHead();
			path += " " + n.getAbbrev();
			total += e.getDist();
		}

		//finish up and print results
		path += " has distance " + total + ".";

		System.out.println( "DelivA:");
		System.out.println(path);
		output.println( "DelivA:");
		output.println(path);
		output.flush();
	}

}
