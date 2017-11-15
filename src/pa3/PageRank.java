package pa3;

import java.util.*;
import java.io.*;

public class PageRank {

	private String fileName;
	private double epsilon;
	private Map<String, Double> pageRankMap = new HashMap<String, Double>();
	private Map<String, List<String>> outDegreeList = new HashMap<String, List<String>>();
	private Map<String, List<String>> inDegreeList = new HashMap<String, List<String>>();
	private int edgesInGraph = 0;
	private Set<String> nodesInGraph = new HashSet<String>();
	private int numOfNodes;
	private int numOfIterations;
	private static final double BETA = 0.85;

	PageRank(String fileName, double epsilon) {
		this.fileName = fileName;
		this.epsilon = epsilon;
		processGraph();
		System.out.println("Processed graph");
		this.numOfNodes = nodesInGraph.size();
		System.out.println("Number of nodes in the graph:" + numOfNodes);
		pageRankMap = computePageRanks();
	}
	// Returns the rank of a particular page
	public double pageRankOf(String vertex) {
		vertex = vertex.toLowerCase();
		return pageRankMap.get(vertex);
	}

	public int outDegreeOf(String vertex) {
		vertex = vertex.toLowerCase();
		return outDegreeList.get(vertex).size();
	}

	public int inDegreeOf(String vertex) {
		vertex = vertex.toLowerCase();
		return inDegreeList.get(vertex).size();
	}

	public int numEdges() {
		return this.edgesInGraph;
	}

	public String[] topKPageRank(int k) {
		List<String> list = new ArrayList<String>(pageRankMap.keySet());

		Collections.sort(list, new Comparator<String>() {
			public int compare(String s1, String s2) {
				if (pageRankOf(s2) > pageRankOf(s1))
					return (1);
				else if (pageRankOf(s2) < pageRankOf(s1))
					return (-1);
				else
					return (0);
			}
		});
		String[] topK = new String[k];

		for (int i = 0; i < k; i++) {
			topK[i] = list.get(i);
		}
		return (topK);
	}

	public String[] topKInDegree(int k) {
		List<String> list = new ArrayList<String>(inDegreeList.keySet());

		Collections.sort(list, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return (inDegreeOf(s2) - inDegreeOf(s1));
			}
		});
		String[] topK = new String[k];

		for (int i = 0; i < k; i++) {
			topK[i] = list.get(i);
		}
		return (topK);
	}

	public String[] topKOutDegree(int k) {
		List<String> list = new ArrayList<String>(outDegreeList.keySet());

		Collections.sort(list, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return (outDegreeOf(s2) - outDegreeOf(s1));
			}
		});
		String[] topK = new String[k];

		for (int i = 0; i < k; i++) {
			topK[i] = list.get(i);
		}
		return (topK);
	}

	public void processGraph() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.fileName));
			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				String[] links = line.split(" ");
				edgesInGraph++;
				links[0] = links[0].toLowerCase();
				links[1] = links[1].toLowerCase();
				if (!nodesInGraph.contains(links[0]))
					nodesInGraph.add(links[0]);
				if (!nodesInGraph.contains(links[1]))
					nodesInGraph.add(links[1]);
				if (!outDegreeList.containsKey(links[0])) {
					outDegreeList.put(links[0], new ArrayList<String>());
				}
				outDegreeList.get(links[0]).add(links[1]);
				if (!inDegreeList.containsKey(links[1])) {
					inDegreeList.put(links[1], new ArrayList<String>());
				}
				inDegreeList.get(links[1]).add(links[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Map<String, Double> computePageRanks() {
		Map<String, Double> p0 = new HashMap<String, Double>();
		for (String s : nodesInGraph) {
			p0.put(s, 1.0 / numOfNodes);
		}
		this.numOfIterations = 0;
		boolean convereged = false;
		double norm = Double.MAX_VALUE;
		Map<String, Double> p1 = p0;
		while (!convereged) {
			p1 = computeNextP(p0);
			norm = computeNorm(p1, p0);
			// System.out.println("Norm:"+norm);
			if (norm <= epsilon)
				convereged = true;
			this.numOfIterations++;
			// System.out.println("Iteration number:"+numOfIterations);
			p0 = p1;
		}

		return (p1);
	}

	public Map<String, Double> computeNextP(Map<String, Double> p0) {
		Map<String, Double> p1 = new HashMap<String, Double>();
		for (String s : nodesInGraph) {
			p1.put(s, (1 - BETA) / numOfNodes);
		}

		double P;
		List<String> out;
		for (String s : nodesInGraph) {
			if (!outDegreeList.containsKey(s)) {
				for (String t : nodesInGraph) {
					P = p1.get(t) + BETA * p0.get(s) / numOfNodes;
					p1.put(t, P);
				}
			} else { // outgoing links
				out = outDegreeList.get(s);
				for (String t : out) {
					P = p1.get(t) + BETA * p0.get(s) / out.size();
					p1.put(t, P);
				}
			}
		}
		return (p1);
	}

	public double computeNorm(Map<String, Double> p1, Map<String, Double> p0) {
		double sum = 0;
		for (String s : nodesInGraph) {
			sum += Math.abs(p1.get(s) - p0.get(s));
		}
		return (Math.abs(sum));
	}

	public double getNumOfIterations() {
		return this.numOfIterations;
	}
}
