package pa3;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.FileIO;

public class WikiCrawler {

	// initialise the queue and visited list
	private LinkedList<GraphNode> queue = new LinkedList<GraphNode>();
	private Set<String> visited = new HashSet<String>();
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
		this.maxGraphNodes = maxGraphNodes-1;
		this.fileName = fileName;
		this.isWeighted = isWeighted;
	}

	void crawl() {

		try {
			excludeRobotTxt();
			
			//All pages are not allowed to download
			if (forbiddenURLS.containsKey("*")) {
				System.out.println("Not allowed to download this page!");
			}
			
			String root = this.seedURL;
			visited.add(root);

			GraphNode.keyWords = this.keyWords.clone();
			GraphNode rootNode = new GraphNode("", root);
			if (!forbiddenURLS.containsKey(root)) {
				rootNode.downloadPagesAndLinks(BASE_URL);
				queue.add(rootNode);
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

			

			StringBuilder sb=new StringBuilder();

			sb.append(visited.size()+"\n");
			for (GraphNode link : crawled) {
				
				sb.append(link.parent + " " + link.child+"\n");
			}
		
			FileIO.writeStringToFile(sb.toString(), this.fileName);
			
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
					//System.out.println("forbidden:"+forbiddenURL);
					forbiddenURLS.put(forbiddenURL, ++i);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	void processingLinks(GraphNode node1) {
		String parent = node1.child;
		final HashMap<String, GraphNode> edgeSet = new HashMap<String, GraphNode>();
		for (String webAdd : node1.links) {

			if (visited.size() > this.maxGraphNodes) {
				break;
			}

			webAdd = webAdd.replace("\"", "");
			if (webAdd != null && !forbiddenURLS.containsKey(webAdd)) {
				if (!webAdd.contains(":") && !webAdd.contains("#")) {
					GraphNode node = new GraphNode(parent, webAdd);

					if (!parent.equalsIgnoreCase(webAdd)) {
						if (!visited.contains(webAdd) /* && !visitedIrrelevant.contains(webAdd)*/) {

							System.out.println("Downloading page #: " + counterDownload++ + " " + webAdd
									+ "  No of Visited nodes: " + visited.size());

							if (node.downloadPagesAndLinks(BASE_URL)) {
								queue.add(node);
								if (!edgeSet.containsKey(webAdd)) {
									edgeSet.put(webAdd, node);
									crawled.add(node);
								}
								visited.add(webAdd);
							} //else {
								//visitedIrrelevant.add(webAdd);
							//}
						}
					}
				}
			}
		}

	}

	void extractEdgesFromatchs(GraphNode node) {

		final HashMap<String, GraphNode> edgeSet = new HashMap<String, GraphNode>();
		for (String address : node.links) {
			address = address.replace("\"", "");
			if (address != null && address.compareToIgnoreCase(node.child) != 0) {// revert
				if ((!address.contains(":")) && (!address.contains("#"))) {
					GraphNode node1 = new GraphNode(node.child, address);

					if (visited.contains(address)) {
						edgeSet.put(address, node1);
					}

				}
			}
		}
		crawled.addAll(edgeSet.values());
	}

	public static void main(String[] arg) {

		String[] keywords = {"Tennis"};

		WikiCrawler cr = new WikiCrawler("/wiki/Tennis", keywords, 100, "data"+File.separator+"pa3"+File.separator+"tenniswiki.txt", false);
		long startTime = System.currentTimeMillis();
		System.out.println("Crawling started at:"+ startTime);
		cr.crawl();
		long endTime = System.currentTimeMillis();
		System.out.println("Crawling ended at:"+ endTime);
		long timeTaken = endTime - startTime;
		System.out.println("Total time taken to crawl :"+ timeTaken);
		System.out.println("Writing to file complete!");

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

			if (counter % 10 == 0) {
				Thread.sleep(3000);
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


	boolean downloadPagesAndLinks(String baseURL) {
		
		BufferedReader br = null;
		try {
			Matcher match;
			Pattern pattern = null;
			
			String hrefPattern = "/wiki/(?:[A-Za-z0-9-._~!#$&'()*+,;=:@]|%[0-9a-fA-F]{2})*";
			pattern = Pattern.compile(hrefPattern);
			
		
			if (counter % 10 == 0) {
				Thread.sleep(3000);
			}
			counter++;

			//Logging start time of processing the page
			long startTime = System.currentTimeMillis();

			URL urlStream = new URL(baseURL + this.child);

			InputStream is = urlStream.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			content = "";
			Boolean isP = false;
			while ((line = br.readLine()) != null) {

				if (line.contains("<p>")) {
					isP = true;
				}

				if (isP && line.contains("<a href=")) {

					match = pattern.matcher(line);

					while (match.find()) {
						String address = line.substring(match.start(0), match.end(0));
						links.add(address);
					}
				}
			}
			//logging end time of processing the page
			long endTime = System.currentTimeMillis();
			long timeTaken = endTime - startTime;
			totalTime += timeTaken;
			is.close();

		} catch (Exception e) {

			e.printStackTrace();

			return false;
		} finally {
			try {
				
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
