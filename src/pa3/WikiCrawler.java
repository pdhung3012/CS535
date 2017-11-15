package pa3;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiCrawler {

	// initialise the queue and visited list
	Queue<GraphNode> queue = new LinkedList<GraphNode>();
	Set<String> visited = new HashSet<String>();
	Set<String> visitedIrrelevant = new HashSet<String>();
	List<GraphNode> crawled = new LinkedList<GraphNode>();
	HashMap<String, Integer> CrawlerForbiddenURL = new HashMap<String, Integer>();

	public String seedURL, fileName;
	public String[] keyWords;
	public static String BASE_URL = "https://en.wikipedia.org";
	public Integer maxGraphNodes;
	public static int countDownload = 0;
	private boolean isWeighted;

	WikiCrawler(String seedURL, String[] keyWords, Integer maxGraphNodes, String fileName, boolean isWeighted) {
		this.seedURL = seedURL;
		this.keyWords = keyWords;
		for (int i = 0; i < keyWords.length; i++) {
			this.keyWords[i] = this.keyWords[i].toLowerCase();
		}
		this.maxGraphNodes = maxGraphNodes - 1;
		this.fileName = fileName;
		this.isWeighted = isWeighted;
	}

	void crawl() {

		try {

			// initialse page to seedURL

			excludeRobotTxt();
			if (CrawlerForbiddenURL.containsKey("*")) {
				System.out.println("The site forbids download of all pages through crawlers...hence exiting");
			}
			URL url = new URL(BASE_URL + this.seedURL);
			String parent = this.seedURL, graph = "";
			int isRoot = 1;
			visited.add(parent);

			GraphNode.keyWords = this.keyWords.clone();
			GraphNode seed = new GraphNode("", parent);
			if (!CrawlerForbiddenURL.containsKey(parent)) {
				seed.downloadPagesAndLinks(BASE_URL);
				queue.add(seed);
			}
			
			while (!queue.isEmpty()) {
				GraphNode node = queue.remove();
				ProcessLinks(node);

				if (visited.size() > this.maxGraphNodes) {
					break;
				}

			}
			while (!queue.isEmpty()) {
				GraphNode node = queue.remove();

				extractEdgesFromLinks(node);
			}

			PrintWriter writer = new PrintWriter(this.fileName);

			writer.println((visited.size()));

			for (GraphNode link : crawled) {
				writer.println(link.parent + " " + link.child);
			}
			writer.close();

		} catch (Exception e) {

			// e.printStackTrace();
			System.out.println(" Exception " + e.getLocalizedMessage());
		}
	}

	void excludeRobotTxt() {
		try {
			GraphNode node = new GraphNode(BASE_URL, "/robots.txt");
			node.downloadPage(BASE_URL);
			int index = 0, i = 1;
			String[] lines = node.content.split("\n");
			for (String line : lines) {
				if (line.startsWith("disallow")) {

					String forbiddenURL = "";
					int start = line.indexOf(":") + 1;
					int end = line.length();
					forbiddenURL = line.substring(start, end).trim();
					CrawlerForbiddenURL.put(forbiddenURL, i);
					i++;
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	void ProcessLinks(GraphNode node1) {
		String parent = node1.child;
		// final List<GraphNode> links = new LinkedList<GraphNode>();
		// final List<GraphNode> edges = new LinkedList<GraphNode>();
		final HashMap<String, GraphNode> uniqueEdges = new HashMap<String, GraphNode>();
		int irrelevant = 0, relavent = 0;
		for (String address : node1.links) {

			if (visited.size() > this.maxGraphNodes) {
				break;
			}

			address = address.replace("\"", "");
			if (address != null && (!CrawlerForbiddenURL.containsKey(address))) {
				// System.out.println(address); //revert
				if (!address.contains(":") && (!address.contains("#"))) {
					// System.out.println(parent + " " + address);
					GraphNode node = new GraphNode(parent, address);

					if (!parent.equalsIgnoreCase(address)) {
						if (!visited.contains(address) && !visitedIrrelevant.contains(address)) {

							System.out.println("Downloading and processing " + countDownload++ + " " + address
									+ "  No of Visited nodes -" + visited.size());

							if (node.downloadPagesAndLinks(BASE_URL)) {
								relavent++;
								queue.add(node);
								if (!uniqueEdges.containsKey(address)) {
									uniqueEdges.put(address, node);
									crawled.add(node);
								}
								visited.add(address);
							} else {
								visitedIrrelevant.add(address);
								irrelevant++;

							}
						}
					}
				}
			}
		}
		// queue.addAll(links);
		// crawled.addAll(edges);
	}

	void extractEdgesFromLinks(GraphNode node) {

		final HashMap<String, GraphNode> uniqueEdges = new HashMap<String, GraphNode>();
		for (String address : node.links) {
			address = address.replace("\"", "");
			if (address != null && address.compareToIgnoreCase(node.child) != 0) {// revert
				if ((!address.contains(":")) && (!address.contains("#"))) {
					GraphNode node1 = new GraphNode(node.child, address);

					if (visited.contains(address)) {
						uniqueEdges.put(address, node1);
					}

				}
			}
		}
		crawled.addAll(uniqueEdges.values());
	}

	public static void main(String[] arg) {

		String[] keywords = { "friends" };

		WikiCrawler cr = new WikiCrawler("/wiki/Friends", keywords, 1000, "/friendswiki.txt", false);

		long startTime = System.currentTimeMillis();
		cr.crawl();
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;
		// System.out.println("time taken "+timeTaken/1000+" download
		// time"+GraphNode.totalTime/1000);

		System.out.println("Data Written to file");

	}
}

class GraphNode {

	private static final int BUFFER_SIZE = 1024;
	public static String[] keyWords;
	static int count = 1;
	String parent, child, content;

	List<String> links = new LinkedList<String>();
	static double totalTime = 0;

	GraphNode(String src, String link) {
		parent = src;
		child = link;
	}

	boolean downloadPagesAndLinks(String baseURL) {

		try {
			Matcher mLink;
			Pattern pLink = null;

			// String HTML_HREF_TAG_PATTERN =
			// "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
			String hrefPattern = "/wiki/(?:[A-Za-z0-9-._~!#$&'()*+,;=:@]|%[0-9a-fA-F]{2})*";
			pLink = Pattern.compile(hrefPattern);

			String subChild = child.substring(child.lastIndexOf("/") + 1);
			URL urlraw = new URL(baseURL + "/w/index.php?title=" + subChild + "&action=raw");
			InputStream isr = urlraw.openStream();
			BufferedReader rbr = new BufferedReader(new InputStreamReader(isr));

			String ln, raw = "";
			while ((ln = rbr.readLine()) != null) {
				raw += ln.toLowerCase();
			}
			isr.close();
			rbr.close();
			for (String str : keyWords) {
				if (!raw.contains(str)) {
					return false;
				}
			}

			if (count % 100 == 0) {
				Thread.sleep(5000);
			}
			count++;

			long startTime = System.currentTimeMillis();

			URL urlStream = new URL(baseURL + this.child);

			Set<String> keys = new HashSet<String>();
			/*
			 * for(String str: keyWords){ keys.add(str); }
			 */
			// System.out.println("Downloading "+count);
			InputStream is = urlStream.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			content = "";
			Boolean foundp = false;
			while ((line = br.readLine()) != null) {

				// line = line;

				if (line.contains("<p>")) { // revert
					foundp = true;
				}

				for (String str : keyWords) {
					if (line.contains(str))
						keys.remove(str);
				}
				if (foundp && line.contains("<a href=")) {

					mLink = pLink.matcher(line);

					while (mLink.find()) {
						String address = line.substring(mLink.start(0), mLink.end(0));// mLink.group(1);
						links.add(address);
					}
				}
			}
			long endTime = System.currentTimeMillis();
			long timeTaken = endTime - startTime;
			totalTime += timeTaken;
			is.close();

			/*
			 * if(!keys.isEmpty()){ return false; }
			 */
		} catch (Exception e) {

			// e.printStackTrace();
			System.out.println("Exception during file download " + e.getLocalizedMessage());
			return false;
		}

		return true;
	}

	Boolean downloadPage(String baseURL) {
		try {

			if (count % 100 == 0) {
				Thread.sleep(5000);
			}
			count++;

			long startTime = System.currentTimeMillis();

			URL urlStream = new URL(baseURL + this.child);

			// System.out.println("Downloading "+count);
			InputStream is = urlStream.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			content = getString(is);
			content = content.toLowerCase();
			long endTime = System.currentTimeMillis();
			long timeTaken = endTime - startTime;
			totalTime += timeTaken;

			is.close();

		} catch (Exception e) {

			System.out.println("Exception during file download " + e.getLocalizedMessage());
			return false;
		}

		return true;

	}

	public static String getString(InputStream inputStream) throws IOException {
		if (inputStream == null)
			return null;
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream), BUFFER_SIZE);
		int charsRead;
		char[] copyBuffer = new char[BUFFER_SIZE];
		StringBuffer sb = new StringBuffer();
		while ((charsRead = in.read(copyBuffer, 0, BUFFER_SIZE)) != -1)
			sb.append(copyBuffer, 0, charsRead);
		in.close();
		return sb.toString();
	}
}

