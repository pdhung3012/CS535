package pa3;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MyWikiRanker {

	public static void main(String[] arg) {

		String[] keywords = { "Data Structure", "Computer Science", "Algorithm" };
		String seedUrl = "/wiki/Computer Science";
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
	
		error = 0.005;
		pr = new PageRank(file, error);

		System.out.println("For epsilon= " + error + "the results are: " + pr.getNumOfIterations());
		System.out.println("Top 10 pages with highest page rank: \n " + Arrays.toString(pr.topKPageRank(10)) + "\n");
	
	}



}
