package pa3;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MyWikiRanker {

	public static void main(String[] arg) {

		String[] keywords = { "Computer", "Computer Science", "Algorithms" };
		String seedUrl = "/wiki/Computer";
		String file = "data" + File.separator + "pa3" + File.separator + "ComputerScienceGraphs.txt";

		WikiCrawler cr = new WikiCrawler(seedUrl, keywords, 100, file, false);
		long startTime = System.currentTimeMillis();
		System.out.println("Crawling started at:" + startTime);
		cr.crawl();
		long endTime = System.currentTimeMillis();
		System.out.println("Crawling ended at:" + endTime);
		long timeTaken = endTime - startTime;
		System.out.println("Total time taken to crawl :" + timeTaken);
		System.out.println("Writing to file complete!");

		double error = 0.01;
		PageRank pr = new PageRank(file, error);

		System.out.println("For epsilon= " + error + "the results are: " + pr.getNumOfIterations());
		System.out.println("Top 10 pages with highest page rank: \n " + Arrays.toString(pr.topKPageRank(10)) + "\n");
		System.out.println("Top 10 pages with highest in-degree: \n" + Arrays.toString(pr.topKInDegree(10)) + "\n");
		System.out.println("Top 10 pages with highest out-degree:\n " + Arrays.toString(pr.topKOutDegree(10)) + "\n");

		Set<String> top10RanksSet = new HashSet<String>(Arrays.asList(pr.topKPageRank(10)));
		Set<String> top10InDegreeSet = new HashSet<String>(Arrays.asList(pr.topKInDegree(10)));
		Set<String> top10OutDegreeSet = new HashSet<String>(Arrays.asList(pr.topKOutDegree(10)));

		MyWikiRanker wtr = new MyWikiRanker();

		try {
			System.out.println(
					"The Jaccard similarity between the top  pages in ranking and top  pages with highest indegree is: "
							+ computeJaccSim(top10RanksSet, top10InDegreeSet));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println(
					"The Jaccard similarity between the top  pages with highest outdegree and top  pages with highest indegree is: "
							+ computeJaccSim(top10InDegreeSet, top10OutDegreeSet));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println(
					"The Jaccard similarity between the top  pages in ranking and top  pages with highest outdegree is: "
							+ computeJaccSim(top10RanksSet, top10OutDegreeSet));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double computeJaccSim(Set<String> file1, Set<String> file2) throws Exception {

		int T1 = file1.size();
		int T2 = file2.size();
		file1.retainAll(file2);// computing intersection
		int intersection = file1.size();
		double ratio = (double) intersection / (double) (T1 + T2 - intersection);

		return ratio;
	}

}
