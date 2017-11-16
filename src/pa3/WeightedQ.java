package pa3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.Map.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.FileIO;

public class WeightedQ {

	public LinkedHashMap<String, Double> queue;
	private ArrayList<String> links;
	private String strWebContent;

	public String getStrWebContent() {
		return strWebContent;
	}

	public void setStrWebContent(String strWebContent) {
		this.strWebContent = strWebContent;
	}

	public LinkedHashMap<String, Double> getQueue() {
		return queue;
	}

	public void setQueue(LinkedHashMap<String, Double> queue) {
		this.queue = queue;
	}

	public ArrayList<String> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}

	public WeightedQ() {
		this.queue = new LinkedHashMap<String, Double>();

	}

	public void add(String key, Double value) {
		this.queue.put(key, value);
		// if (queue.size() > 1) {
		//
		// }

	}

	public Entry<String, Double> extract() {
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(
				this.queue.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Entry tuple = list.get(0);
		String key = (String) tuple.getKey();
		this.queue.remove(key);

		return tuple;
	}

	boolean downloadPagesAndLinks(String myURL) {
		links = new ArrayList<String>();
		BufferedReader br = null;
		try {
			Matcher match;
			Pattern pattern = null;

			String hrefPattern = "/wiki/(?:[A-Za-z0-9-._~!#$&'()*+,;=:@]|%[0-9a-fA-F]{2})*";
			pattern = Pattern.compile(hrefPattern);

			// if (counter % 10 == 0) {
			// Thread.sleep(3000);
			// }
			// counter++;

			// Logging start time of processing the page
			long startTime = System.currentTimeMillis();

			URL urlStream = new URL(myURL);

			InputStream is = urlStream.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			// content = "";
			Boolean isP = false;
			StringBuilder sbContent = new StringBuilder();
			HashSet<String> setLinks = new HashSet<String>();
			while ((line = br.readLine()) != null) {
			//	System.out.println(line);
				sbContent.append(line + "\n");
				if (line.contains("<p>")) {
					isP = true;
				}

				if (isP && line.contains("<a href=")) {

					match = pattern.matcher(line);

					while (match.find()) {
						String address = line.substring(match.start(0),
								match.end(0));
					//	System.out.println(address);
						setLinks.add(address);
					}
				}
			}

			// logging end time of processing the page
			// long endTime = System.currentTimeMillis();
			// long timeTaken = endTime - startTime;
			// totalTime += timeTaken;
			is.close();
			strWebContent = sbContent.toString();
			// System.out.println("Link "+links.size());
			links = new ArrayList<String>();
			for (String strItem : setLinks) {
				links.add(strItem);
			}
			//System.out.println("li: "+links.size());
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

	public int distWordAndWord(String word1, String word2,String urlContent) {
		int result=-1;
		String contentProcess=urlContent;
		ArrayList<Integer> lstIndexWord1=getAllIndexesOfWordInDocument(word1, contentProcess);
		ArrayList<Integer> lstIndexWord2=getAllIndexesOfWordInDocument(word2, contentProcess);
		int maxDistance=1000000;
		int currentDistance=maxDistance;
		
		//System.out.println(lstIndexWord1.size()+"\t"+lstIndexWord2.size());
		
		for(int l=0;l<lstIndexWord1.size();l++){
			for(int k=0;k<lstIndexWord2.size();k++){
				int candidate=Math.abs(lstIndexWord1.get(l)-lstIndexWord2.get(k));
				//System.out.println(candidate);
				if(candidate<=currentDistance){
					currentDistance=candidate;
				}
			}
		}
		result=currentDistance;
		
		return result;
	}

	public int distWordAndListOfWord(String word1, ArrayList<String> lstTopics,String urlContent) {
		int maxDistance=1000000;
		int currentDistance=maxDistance;
		for(int i=0;i<lstTopics.size();i++){
			String wordItem = getOneSpaceContent(lstTopics.get(i).trim()
					.toLowerCase());
			int dist=distWordAndWord(word1, wordItem, urlContent);
			if(dist<=currentDistance){
				currentDistance=dist;
			}
		}
		return currentDistance;
	}

	public ArrayList<String> heuristicComputeWeightedQ(String urlInput,
			String fpInputTopics) {
		ArrayList<String> lstOutput = new ArrayList<String>();
		ArrayList<String> lstWords = new ArrayList<String>();
		lstWords = getLstWordsFromFile(fpInputTopics);
		int indexTried = 0, maxTried = 10;
		int minDistanceAccept=20;
		boolean isDownloadSuccess = false;
		while (!isDownloadSuccess && indexTried < maxTried) {
			isDownloadSuccess = downloadPagesAndLinks(urlInput);
			indexTried++;
		//	System.out.println("try "+indexTried);
		}
		
		String contentProcess=strWebContent.trim().toLowerCase().replaceAll(
				":", " ").replaceAll("_", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").trim();
		contentProcess=getOneSpaceContent(contentProcess);
		if(isDownloadSuccess){
			System.out.println("Success reading base web after "+indexTried+" times");
		} else{
			System.out.println("Failed reading base web after "+indexTried+" times");
		}
		
		for (int i = 0; i < links.size(); i++) {
			//System.out.println(links.get(i));
			//System.out.println(lstWords.toString());
			String strCompareWord=links.get(i);
			if (isContainInURLLink(links.get(i), lstWords)) {				
				add(links.get(i), 1.0);
			} else {
				String[] arrInput = links.get(i).trim().split("/");
				String strLinkContent = arrInput[arrInput.length - 1];
				String strContentLink = getOneSpaceContent(strLinkContent.replaceAll(
						":", " ").replaceAll("_", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").toLowerCase());
				int minDist=distWordAndListOfWord(strContentLink, lstWords, contentProcess);
				double weighti=0;
				//System.out.println(links.get(i)+" "+minDist);
				if(minDist<=minDistanceAccept){
					weighti=1.0/(minDist+2);
					add(links.get(i), weighti);
				} else{
					add(links.get(i), 0.0);
				}
			}
			;
		}

		// get WeightedQ
		lstOutput = new ArrayList<String>();
		int queueSize= queue.keySet().size();
		
		for (int i=0;i<queueSize;i++) {
			Entry<String,Double> item=extract();
			lstOutput.add(item.getKey() + "\t" + item.getValue());
		}

		return lstOutput;
	}

	public String getOneSpaceContent(String input) {
		String strResult = "";
		String[] arr = input.trim().split("\\s+");
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i].isEmpty()) {
				strResult += arr[i] + " ";
			}
		}
		strResult = strResult.trim();
		return strResult;
	}
	
	public ArrayList<Integer> getAllIndexesOfWordInDocument(String word,String urlContent){
		ArrayList<Integer> lstResult=new ArrayList<Integer>();
		int index = urlContent.indexOf(word);
		while(index >= 0) {
		    lstResult.add(index);
			index = urlContent.indexOf(word, index+1);
		    
		}
		return lstResult;
	}

	public boolean isContainInURLLink(String strInput,
			ArrayList<String> lstWords) {
		boolean isContain = false;
		String[] arrInput = strInput.trim().split("/");
		String strLinkContent = arrInput[arrInput.length - 1];
		String strContentSpace = getOneSpaceContent(strLinkContent.replaceAll(
				":", " ").replaceAll("_", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").toLowerCase());
		//System.out.println("content "+strContentSpace);
		for (int i = 0; i < lstWords.size(); i++) {
			String word = getOneSpaceContent(lstWords.get(i).trim()
					.toLowerCase());
			if (strContentSpace.contains(word)) {
				isContain = true;
				break;
			}
		}

		return isContain;
	}

	private ArrayList<String> getLstWordsFromFile(String fpInput) {
		String[] arrWords = FileIO.readStringFromFile(fpInput).trim()
				.split("\n");
		ArrayList<String> lstWords = new ArrayList<String>();
		for (int i = 0; i < arrWords.length; i++) {
			String item = arrWords[i].trim().toLowerCase();
			lstWords.add(item);
			// if()
		}
		return lstWords;
	}

	public static void main(String[] args) {
		String urlPrefix = "https://en.wikipedia.org/";
		String urlStartWikiPage = urlPrefix + "wiki/Tennis";
		String fpSetOfTopic = "data" + File.separator + "pa3" + File.separator
				+ "TopicForWeightedQ.txt";
		String fpOutputText = "data" + File.separator + "pa3" + File.separator
				+ "ResultWeightQ.txt";
		String fpOutputBaseWebContent = "data" + File.separator + "pa3" + File.separator
				+ "ResultBaseWebContent.txt";
		WeightedQ wq = new WeightedQ();
		ArrayList<String> lstOutput = wq.heuristicComputeWeightedQ(
				urlStartWikiPage, fpSetOfTopic);
		StringBuilder sb = new StringBuilder();
		sb.append("Craw from :" + urlStartWikiPage + "\n");
		sb.append("Set of topics :" + wq.getLstWordsFromFile(fpSetOfTopic)
				+ "\n");
		sb.append("Num of results :" +wq.getLinks().size() + "\n");
		for (int i = 0; i < lstOutput.size(); i++) {
			// System.out.println(i+"\t"+lstLinks.get(i));
			sb.append(lstOutput.get(i) + "\n");
		}
		FileIO.writeStringToFile(sb.toString(), fpOutputText);
		FileIO.writeStringToFile(wq.getStrWebContent(), fpOutputBaseWebContent);
		System.out.println("Check the result in " + fpOutputText);

	}

}
