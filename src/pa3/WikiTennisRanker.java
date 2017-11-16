package pa3;

import java.util.Set;

import java.util.*;

public class WikiTennisRanker {

	public static void main(String[] args) throws Exception {
		WikiTennisRanker wtr = new WikiTennisRanker();
		wtr.computePageRank(0.01);
		wtr.computePageRank(0.005);
		//
	}

	public void computePageRank(double error) throws Exception {
		String file = "data/pa3/wikiTennis.txt";
		PageRank pr = new PageRank(file, error);

		System.out.println("For epsilon= " + error + "the results are: " + pr.getNumOfIterations());
		System.out.println("Top 10 pages with highest page rank: \n " + Arrays.toString(pr.topKPageRank(10)) + "\n");
		System.out.println("Top 10 pages with highest in-degree: \n" + Arrays.toString(pr.topKInDegree(10)) + "\n");
		System.out.println("Top 10 pages with highest out-degree:\n " + Arrays.toString(pr.topKOutDegree(10)) + "\n");

		Set<String> top100RanksSet = new HashSet<String>(Arrays.asList(pr.topKPageRank(100)));
		Set<String> top100InDegreeSet = new HashSet<String>(Arrays.asList(pr.topKInDegree(100)));
		Set<String> top100OutDegreeSet = new HashSet<String>(Arrays.asList(pr.topKOutDegree(100)));
		
		WikiTennisRanker wtr = new WikiTennisRanker();
		
		System.out.println(
				"The Jaccard similarity between the top  pages in ranking and top  pages with highest indegree is: "
						+ wtr.computeJaccSim(top100RanksSet, top100InDegreeSet));
		System.out.println(
				"The Jaccard similarity between the top  pages with highest outdegree and top  pages with highest indegree is: "
						+ wtr.computeJaccSim(top100InDegreeSet, top100OutDegreeSet));
		System.out.println(
				"The Jaccard similarity between the top  pages in ranking and top  pages with highest outdegree is: "
						+ wtr.computeJaccSim(top100RanksSet, top100OutDegreeSet));
	}

	public double computeJaccSim(Set<String> file1, Set<String> file2) throws Exception {

		int T1 = file1.size();
		int T2 = file2.size();
		file1.retainAll(file2);// computing intersection
		int intersection = file1.size();
		double ratio = (double) intersection / (double) (T1 + T2 - intersection);

		return ratio;
	}
}
