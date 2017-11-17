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
	private WeightedQ weightedQ;
	private ArrayList<String> lstKeyWords;


	WikiCrawler(String seedURL, String[] keyWords, Integer maxGraphNodes, String fileName, boolean isWeighted) {
		this.seedURL = seedURL;
		this.keyWords = keyWords;
		this.lstKeyWords=new ArrayList<String>();
		for (int i = 0; i < keyWords.length; i++) {
			this.keyWords[i] = this.keyWords[i].toLowerCase();
			this.lstKeyWords.add(this.keyWords[i]);
		}
		this.maxGraphNodes = maxGraphNodes - 1;
		this.fileName = fileName;
		this.isWeighted = isWeighted;
		weightedQ=new WeightedQ();
	}

	void crawl() {

		try {
			excludeRobotTxt();

			// All pages are not allowed to download
			if (forbiddenURLS.containsKey("*")) {
				System.out.println("Not allowed to download this page!");
			}

			String root = this.seedURL;
			visited.add(root);

			GraphNode.keyWords = this.keyWords.clone();
			GraphNode rootNode = new GraphNode("", root);
			rootNode.setWq(weightedQ);
			rootNode.setLstKeyWords(lstKeyWords);
			if (!forbiddenURLS.containsKey(root)) {
				rootNode.downloadPagesAndLinks(BASE_URL);
				queue.addLast(rootNode);
			}

			while (!queue.isEmpty()) {
				GraphNode node = queue.removeFirst();
				processingLinks(node);

				if (visited.size() > this.maxGraphNodes) {
					break;
				}

			}
			while (!queue.isEmpty()) {
				GraphNode node = queue.removeFirst();
				getEdges(node);
			}

			StringBuilder sb = new StringBuilder();

			sb.append(visited.size() + "\n");
			for (GraphNode link : crawled) {

				sb.append(link.parent + " " + link.child + "\n");
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
					node.setLstKeyWords(lstKeyWords);
					node.setWq(weightedQ);
					if (!parent.equalsIgnoreCase(webAdd)) {
						if (!visited.contains(webAdd) ) {

							System.out.println("Downloading page #: " + counterDownload++ + " " + webAdd
									+ "  No of Visited nodes: " + visited.size());

							if (node.downloadPagesAndLinks(BASE_URL)) {
								queue.add(node);
								if (!edgeSet.containsKey(webAdd)) {
									edgeSet.put(webAdd, node);
									crawled.add(node);
								}
								visited.add(webAdd);
							} 
						}
					}
				}
			}
		}

	}

	void getEdges(GraphNode node) {

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

		String[] keywords = { "Tennis" };
		boolean isWeighted = false;
		WikiCrawler cr = new WikiCrawler("/wiki/Tennis", keywords, 100,
				"data" + File.separator + "pa3" + File.separator + "tenniswiki.txt", isWeighted);
		long startTime = System.currentTimeMillis();
		System.out.println("Crawling started at:" + startTime);
		cr.crawl();
		long endTime = System.currentTimeMillis();
		System.out.println("Crawling ended at:" + endTime);
		long timeTaken = endTime - startTime;
		System.out.println("Total time taken to crawl :" + timeTaken);
		System.out.println("Writing to file complete!");
		//
	}
}

class GraphNode {

	private static final int BUFFER_SIZE = 1024;
	String parent;
	String child;
	String content;
	public static String[] keyWords;
	static int counter = 1;
	private WeightedQ wq;
	private ArrayList<String> lstKeyWords;
	
	
	

	

	public ArrayList<String> getLstKeyWords() {
		return lstKeyWords;
	}

	public void setLstKeyWords(ArrayList<String> lstKeyWords) {
		this.lstKeyWords = lstKeyWords;
	}

	public WeightedQ getWq() {
		return wq;
	}

	public void setWq(WeightedQ wq) {
		this.wq = wq;
	}



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
		links=new ArrayList<String>();
		try {
			Matcher match;
			Pattern pattern = null;

			String hrefPattern = "/wiki/(?:[A-Za-z0-9-._~!#$&'()*+,;=:@]|%[0-9a-fA-F]{2})*";
			pattern = Pattern.compile(hrefPattern);

			

			if (counter % 10 == 0) {
				Thread.sleep(3000);
			}
			counter++;

			// Logging start time of processing the page
			long startTime = System.currentTimeMillis();

			URL urlStream = new URL(baseURL + this.child);

			InputStream is = urlStream.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			content = "";
			Boolean isP = false;
			int ctr=0;
			while ((line = br.readLine()) != null) {

				if (line.contains("<p>")) {
					isP = true;
				}

				if (isP && line.contains("<a href=")) {
					//System.out.println(line);
					//System.exit(1);
					match = pattern.matcher(line);

					while (match.find()) {
						String address = line.substring(match.start(0), match.end(0));
					//	System.out.println(address);
						links.add(address);
					}
					//System.exit(1);
				}
				ctr++;
			}
			
			ArrayList<String> weightedLinks=new ArrayList<String>();
//			System.out.println(links.size()+ " size of link in "+baseURL);
////			for(int j=0;j<links.size();j++){
////				System.out.println("link "+j+": "+links.get(j));
////			}
			weightedLinks=wq.heuristicComputeWeightedByUrlListAndTopicAndCurrentPageContent(links, lstKeyWords, content);			
			links=weightedLinks;
			//System.out.println("Number of links"+ctr);
			// logging end time of processing the page
			long endTime = System.currentTimeMillis();
			long timeTaken = endTime - startTime;
			totalTime += timeTaken;
			is.close();
			br.close();

		} catch (Exception e) {

			e.printStackTrace();

			return false;
		} finally {
			try {
				if(br!=null)
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
