package pa3;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiCrawler {

	// initialise the queue and visited list
	private LinkedList<GraphNode> queue = new LinkedList<GraphNode>();
	private Set<String> visited = new HashSet<String>();
	private Set<String> visitedIrrelevant = new HashSet<String>();
	private List<GraphNode> crawled = new LinkedList<GraphNode>();
	private HashMap<String, Integer> forbiddenURLS = new HashMap<String, Integer>();

	private String seedURL, fileName;
	private String[] keyWords;
	private static String BASE_URL = "https://en.wikipedia.org";
	private Integer maxGraphNodes;
	private static int counterDownload = 0;
	private boolean isWeighted;

	WikiCrawler(String seedURL, String[] keyWords, Integer maxGraphNodes, String fileName, boolean isWeighted) {
		this.seedURL = seedURL;
		this.keyWords = keyWords;
		for (int i = 0; i < keyWords.length; i++) {
			this.keyWords[i] = this.keyWords[i].toLowerCase();
		}
		this.maxGraphNodes = maxGraphNodes;
		this.fileName = fileName;
		this.isWeighted = isWeighted;
	}

	void crawl() {

		try {
			excludeRobotTxt();
			if (forbiddenURLS.containsKey("*")) {
				System.out.println("Not allowed to download this page!");
			}
			String parent = this.seedURL;
			visited.add(parent);

			GraphNode.keyWords = this.keyWords.clone();
			GraphNode seed = new GraphNode("", parent);
			if (!forbiddenURLS.containsKey(parent)) {
				seed.downloadPagesAndLinks(BASE_URL);
				queue.add(seed);
			}

			while (!queue.isEmpty()) {
				GraphNode node = queue.remove();
				processingLinks(node);

				if (visited.size() > this.maxGraphNodes) {
					break;
				}

			}
			while (!queue.isEmpty()) {
				GraphNode node = queue.remove();

				extractEdgesFromatchs(node);
			}

			PrintWriter writer = new PrintWriter(new FileOutputStream(this.fileName), true);

			writer.println((visited.size()));

			for (GraphNode link : crawled) {
				writer.println(link.parent + " " + link.child);
			}
			writer.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	void excludeRobotTxt() {
		try {
			GraphNode node = new GraphNode(BASE_URL, "/robots.txt");
			node.downloadPage(BASE_URL);
			int i = 1;
			String[] lines = node.content.split("\n");
			for (String line : lines) {
				if (line.startsWith("disallow")) {
					String forbiddenURL = "";
					int startIndex = line.indexOf(":") + 1;
					int endIndex = line.length();
					forbiddenURL = line.substring(startIndex, endIndex).trim();
					System.out.println("forbidden:"+forbiddenURL);
					forbiddenURLS.put(forbiddenURL, ++i);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	void processingLinks(GraphNode node1) {
		String parent = node1.child;
		final HashMap<String, GraphNode> uniqueEdges = new HashMap<String, GraphNode>();
		for (String address : node1.links) {

			if (visited.size() > this.maxGraphNodes) {
				break;
			}

			address = address.replace("\"", "");
			if (address != null && (!forbiddenURLS.containsKey(address))) {
				if (!address.contains(":") && (!address.contains("#"))) {
					GraphNode node = new GraphNode(parent, address);

					if (!parent.equalsIgnoreCase(address)) {
						if (!visited.contains(address) && !visitedIrrelevant.contains(address)) {

							System.out.println("Downloading and processing " + counterDownload++ + " " + address
									+ "  No of Visited nodes -" + visited.size());

							if (node.downloadPagesAndLinks(BASE_URL)) {
								queue.add(node);
								if (!uniqueEdges.containsKey(address)) {
									uniqueEdges.put(address, node);
									crawled.add(node);
								}
								visited.add(address);
							} else {
								visitedIrrelevant.add(address);
							}
						}
					}
				}
			}
		}

	}

	void extractEdgesFromatchs(GraphNode node) {

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

		String[] keywords = {"Tennis"};

		WikiCrawler cr = new WikiCrawler("/wiki/Tennis", keywords, 100, "/data/pa3/tenniswiki.txt", false);

		long startTime = System.currentTimeMillis();
		cr.crawl();
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;
		System.out.println("Data Written to file");

	}
}

class GraphNode {

	private static final int BUFFER_SIZE = 1024;
	String parent;
	String child;
	String content;
	public static String[] keyWords;
	static int counter = 1;

	List<String> links = new LinkedList<String>();
	static double totalTime = 0;

	GraphNode(String parent, String child) {
		this.parent = parent;
		this.child = child;
	}

	boolean downloadPage(String baseURL) {
		URL urlstrm;
		InputStream is = null;
		try {

			if (counter % 200 == 0) {
				Thread.sleep(10000);
			}
			counter++;

			long startDownloadTime = System.currentTimeMillis();
			urlstrm = new URL(baseURL + this.child);
			is = urlstrm.openStream();
			content = getString(is);
			content = content.toLowerCase();
			long endDownloadTime = System.currentTimeMillis();
			long timeTaken = endDownloadTime - startDownloadTime;
			totalTime += timeTaken;

		} catch (Exception e) {

			e.printStackTrace();
			return false;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return true;

	}

	@SuppressWarnings("resource")
	boolean downloadPagesAndLinks(String baseURL) {
		InputStream isr = null;
		BufferedReader br = null;
		try {
			Matcher match;
			Pattern pattern = null;

			String hrefPattern = "/wiki/(?:[A-Za-z0-9-._~!#$&'()*+,;=:@]|%[0-9a-fA-F]{2})*";
			pattern = Pattern.compile(hrefPattern);

			String subChild = child.substring(child.lastIndexOf("/") + 1);
			URL urlraw = new URL(baseURL + "/w/index.php?title=" + subChild + "&action=raw");
			isr = urlraw.openStream();
			br = new BufferedReader(new InputStreamReader(isr));

			String ln, raw = "";
			while ((ln = br.readLine()) != null) {
				raw += ln.toLowerCase();
			}

			for (String str : keyWords) {
				if (!raw.contains(str)) {
					return false;
				}
			}

			if (counter % 100 == 0) {
				Thread.sleep(10000);
			}
			counter++;

			long startTime = System.currentTimeMillis();

			URL urlStream = new URL(baseURL + this.child);

			Set<String> keys = new HashSet<String>();

			InputStream is = urlStream.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			content = "";
			Boolean isP = false;
			while ((line = br.readLine()) != null) {

				if (line.contains("<p>")) { // revert
					isP = true;
				}

				for (String str : keyWords) {
					if (line.contains(str))
						keys.remove(str);
				}
				if (isP && line.contains("<a href=")) {

					match = pattern.matcher(line);

					while (match.find()) {
						String address = line.substring(match.start(0), match.end(0));// match.group(1);
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

			e.printStackTrace();

			return false;
		} finally {
			try {
				isr.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return true;
	}

	public static String getString(InputStream inputStream) throws IOException {
		if (inputStream == null)
			return null;
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		int chars;
		char[] copyBuffer = new char[BUFFER_SIZE];
		StringBuffer sb = new StringBuffer();
		while ((chars = in.read(copyBuffer, 0, BUFFER_SIZE)) != -1)
			sb.append(copyBuffer, 0, chars);
		in.close();
		return sb.toString();
	}
}
