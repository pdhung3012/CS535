package pa3;

import java.io.File;

public class MyWikiRanker {

	public static void main(String[] arg) {

		String[] keywords = { "Computer", "Computer Science", "Algorithms", ""};
		String seedUrl = "/wiki/Computer";

		WikiCrawler cr = new WikiCrawler(seedUrl, keywords, 100,
				"data" + File.separator + "pa3" + File.separator + "tenniswiki.txt", false);
		long startTime = System.currentTimeMillis();
		System.out.println("Crawling started at:" + startTime);
		cr.crawl();
		long endTime = System.currentTimeMillis();
		System.out.println("Crawling ended at:" + endTime);
		long timeTaken = endTime - startTime;
		System.out.println("Total time taken to crawl :" + timeTaken);
		System.out.println("Writing to file complete!");

	}

}
