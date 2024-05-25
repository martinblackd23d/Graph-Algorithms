import java.io.File;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

// Class DelivC does the work for deliverable DelivC of the Prog340

public class DelivC {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivC( File in, Graph gr ) {
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
		

		// Find start node
		Node start = null;
		for (Node n : g.getNodeList()) {
			if (n.getVal().equals("S")) {
				start = n;
				break;
			}
		}

		int bound = Integer.MAX_VALUE;
		Stack<Node> frontier = new Stack<Node>();
		Stack<Node> path = new Stack<Node>(); // Current branch
		Stack<Integer> pathDist = new Stack<Integer>(); // Distance on current branch
		Stack<Node> pathPred = new Stack<Node>(); // Predecessor on current branch for backtracking
		String pathString = ""; // Entire branching history
		ArrayList<Node> finalPath = null; // Shortest path found

		HashMap<Node, Integer> distances = new HashMap<Node, Integer>(); // Shortest distance from start, also indicates visited nodes

		frontier.push(start);
		pathDist.push(0);
		pathPred.push(null);
		distances.put(start, 0);

		while (!frontier.isEmpty()) {
			// Get next node
			Node current = frontier.pop();
			int dist = pathDist.pop();
			Node pred = pathPred.pop();

			// If next node is out of bounds, ignore it (in case the bound has shrunk since it was added to the frontier)
			// If a shorter path has already been found, ignore it
			if (dist >= bound || (distances.containsKey(current) && dist > distances.get(current))) {
				continue;
			}

			// Print backtracking if new branch is started
			boolean flag = true;
			while (!path.isEmpty() && !pred.equals(path.peek())) {
				// Skip the last node in the previous branch
				if (flag) {
					flag = false;
					path.pop();
					continue;
				}
				pathString += "(" + path.pop().getAbbrev() + ") ";
			}
			// Print the node where the new branch starts
			if (!flag) {
				pathString += "(" + path.peek().getAbbrev() + ") ";
			}

			// Print current node
			if (current.getVal().equals("G")) {
				pathString += current.getAbbrev() + "@" + dist + " ";
			} else {
				pathString += current.getAbbrev() + " ";
			}

			// Add current node to path
			path.push(current);

			// If goal found, set bound and save path
			if (current.getVal().equals("G")) {
				bound = dist;
				finalPath = new ArrayList<Node>(path);
				continue;
			}

			// Sort edges in revers order of how the Nodes need to be processed
			ArrayList<Edge> edges = current.getOutgoingEdges();
			Collections.sort(edges, new Comparator<Edge>() {
				public int compare(Edge e1, Edge e2) {
					int d = e2.getDist() - e1.getDist();
					if (d != 0) {
						return d;
					}
					return e2.getHead().getVal().compareTo(e1.getHead().getVal());
				}
			});

			// Add next nodes to frontier, as well as their distances from the start and predecessors
			for (Edge e : current.getOutgoingEdges()) {
				Node next = e.getHead();
				int nextDistance = dist + e.getDist();
				// Add to frontier if it has not been visited or if the new path is shorter
				if (nextDistance < bound && (!distances.containsKey(next) || nextDistance < distances.get(next))) {
					frontier.push(next);
					pathDist.push(nextDistance);
					pathPred.push(current);
					distances.put(next, nextDistance);
				}
			}
		}

		// do the final backtracking
		path.pop();
		while (!path.isEmpty()) {
			pathString += "(" + path.pop().getAbbrev() + ") ";
		}

		// Print the path
		System.out.println(pathString);
		output.println(pathString);

		if (finalPath == null) {
			System.out.println("No path found.");
			output.println("No path found.");
			output.flush();
			output.close();
			return;
		}
		System.out.print("Shortest path is ");
		output.print("Shortest path is ");
		for (Node n : finalPath) {
			System.out.print(n.getAbbrev() + " ");
			output.print(n.getAbbrev() + " ");
		}
		System.out.println("with dist " + bound + ".");
		output.println("with dist " + bound + ".");
		output.flush();
		output.close();
	}
}


