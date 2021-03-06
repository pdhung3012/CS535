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

	//
	//
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
			LinkedHashSet<String> setLinks = new LinkedHashSet<String>();
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				sbContent.append(line + "\n");
				if (line.contains("<p>")) {
					isP = true;
				}

				if (isP && line.contains("<a href=")) {

					match = pattern.matcher(line);

					while (match.find()) {
						String address = line.substring(match.start(0),
								match.end(0));
						// System.out.println(address);
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
			// System.out.println("li: "+links.size());
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

	public int distWordAndWord(String word1, String word2, String urlContent) {
		int result = -1;
		String contentProcess = urlContent;
		ArrayList<Integer> lstIndexWord1 = getAllIndexesOfWordInDocument(word1,
				contentProcess);
		ArrayList<Integer> lstIndexWord2 = getAllIndexesOfWordInDocument(word2,
				contentProcess);
		int maxDistance = 1000000;
		int currentDistance = maxDistance;

		System.out.println(word1 + "\t" + lstIndexWord1.toString());
		System.out.println(word2 + "\t" + lstIndexWord2.toString());

		for (int l = 0; l < lstIndexWord1.size(); l++) {
			for (int k = 0; k < lstIndexWord2.size(); k++) {
				int candidate = Math.abs(lstIndexWord1.get(l)
						- lstIndexWord2.get(k));
				// System.out.println(candidate);
				if (candidate <= currentDistance) {
					currentDistance = candidate;
				}
			}
		}
		result = currentDistance;

		return result;
	}

	public int distWordAndListOfWord(String word1, ArrayList<String> lstTopics,
			String urlContent) {
		int maxDistance = 1000000;
		int currentDistance = maxDistance;
		for (int i = 0; i < lstTopics.size(); i++) {
			String wordItem = lstTopics.get(i).trim().toLowerCase();
			// System.out.println(word1+"\t"+wordItem+"\t"+urlContent);
			int dist = distWordAndWord(word1, wordItem, urlContent);
			if (dist <= currentDistance) {
				currentDistance = dist;
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
		int minDistanceAccept = 20;
		boolean isDownloadSuccess = false;
		while (!isDownloadSuccess && indexTried < maxTried) {
			isDownloadSuccess = downloadPagesAndLinks(urlInput);
			indexTried++;
			// System.out.println("try "+indexTried);
		}

		String contentProcess = strWebContent.trim().toLowerCase()
				.replaceAll(":", " ").replaceAll("_", " ")
				.replaceAll("\\(", " ").replaceAll("\\)", " ").trim();
		contentProcess = getOneSpaceContent(contentProcess);
		if (isDownloadSuccess) {
			System.out.println("Success reading base web after " + indexTried
					+ " times");
		} else {
			System.out.println("Failed reading base web after " + indexTried
					+ " times");
		}

		System.out.println(links.size() + " size of link in " + urlInput);
		// for(int j=0;j<links.size();j++){
		// System.out.println("link "+j+": "+links.get(j));
		// }
		queue = new LinkedHashMap<String, Double>();
		for (int i = 0; i < links.size(); i++) {
			// System.out.println(links.get(i));
			// System.out.println(lstWords.toString());
			String strCompareWord = links.get(i);
			if (strCompareWord.contains(":")) {
				continue;
			}
			if (isContainInURLLink(links.get(i), lstWords, contentProcess)) {
				add(links.get(i), 1.0);
			} else {
				String[] arrInput = links.get(i).trim().split("/");
				String strLinkContent = arrInput[arrInput.length - 1];
				String strContentLink = getOneSpaceContent(strLinkContent
						.replaceAll(":", " ").replaceAll("_", " ")
						.replaceAll("\\(", " ").replaceAll("\\)", " ")
						.toLowerCase());
				int minDist = distWordAndListOfWord(strContentLink, lstWords,
						contentProcess);
				double weighti = 0;
				// System.out.println(links.get(i)+" "+minDist);
				if (minDist <= minDistanceAccept) {
					weighti = 1.0 / (minDist + 2);
					add(links.get(i), weighti);
				} else {
					add(links.get(i), 0.0);
				}
			}
			;
		}

		// get WeightedQ
		lstOutput = new ArrayList<String>();
		int queueSize = queue.keySet().size();

		for (int i = 0; i < queueSize; i++) {
			Entry<String, Double> item = extract();
			lstOutput.add(item.getKey() + "\t" + item.getValue());
		}

		return lstOutput;
	}

	public ArrayList<String> heuristicComputeWeightedByUrlListAndTopicAndCurrentPageContent(
			List<String> paramLinksLst, ArrayList<String> lstTopics,
			String strWebContent) {
		ArrayList<String> lstOutput = new ArrayList<String>();

		int minDistanceAccept = 20;
		String contentProcess = strWebContent.trim().toLowerCase()
				.replaceAll(":", " ").replaceAll(">", " > ")
				.replaceAll("<", " < ").replaceAll("\"", " \" ")
				.replaceAll("\\(", " ").replaceAll("\\)", " ").trim();
		contentProcess = getOneSpaceContent(contentProcess);
	//	System.out.println("Done get content");

		LinkedHashSet<String> setLinks = new LinkedHashSet<String>();
		ArrayList<String> paramLstSetLink = new ArrayList<String>();
		for (int i = 0; i < paramLinksLst.size(); i++) {
			setLinks.add(paramLinksLst.get(i));
		}

		for (String strItem : setLinks) {
			
			paramLstSetLink.add(strItem);
		}

		// for (int i = 0; i < paramLinksLst.size(); i++) {
		// String itemI=paramLinksLst.get(i);
		// if(!setChecks.contains(itemI)){
		// setChecks.add(itemI);
		// paramLstSetLink.add(itemI);
		// }
		//
		// }

		System.out.println(paramLstSetLink.toString() + " size of link in ");
		// for(int j=0;j<paramLstSetLink.size();j++){
		// System.out.println("link "+j+": "+paramLstSetLink.get(j));
		// }

		queue = new LinkedHashMap<>();

		for (int i = 0; i < paramLstSetLink.size(); i++) {
			// System.out.println(links.get(i));
			// System.out.println(lstWords.toString());
			String strCompareWord = paramLstSetLink.get(i);
			//System.out.println("compare word "+paramLinksLst.get(i));
			if (strCompareWord.contains(":")) {
				continue;
			}
			if (isContainInURLLink(paramLstSetLink.get(i), lstTopics,
					contentProcess)) {
				add(paramLstSetLink.get(i), 1.0);
			} else {
				String[] arrInput = paramLstSetLink.get(i).trim().split("/");
				String strLinkContent = paramLstSetLink.get(i).trim();//arrInput[arrInput.length - 1];
				String strContentLink = getSourceContentInsideLink(strLinkContent,contentProcess); 
//						getOneSpaceContent(strLinkContent
//						.replaceAll(":", " ").replaceAll(">", " > ")
//						.replaceAll("<", " < ").replaceAll("\"", " \" ")
//						.replaceAll("\\(", " ").replaceAll("\\)", " ")
//						.toLowerCase());
				int minDist = distWordAndListOfWord(strContentLink, lstTopics,
						contentProcess);
				double weighti = 0;
				System.out.println(strCompareWord + " " + minDist);
				if (minDist <= minDistanceAccept) {
					weighti = 1.0 / (minDist + 2);
					add(paramLstSetLink.get(i), weighti);
				} else {
					add(paramLstSetLink.get(i), 0.0);
				}
			}
			;
		}

		// get WeightedQ
		lstOutput = new ArrayList<String>();
		int queueSize = queue.keySet().size();

		for (int i = 0; i < queueSize; i++) {
			Entry<String, Double> item = extract();
			if (item.getValue() > 0) {
				lstOutput.add(item.getKey());
			}

		}

		return lstOutput;
	}

	public String getOneSpaceContent(String input) {
		String strResult = "";
//		String[] arr = input.trim().split("\\s+");
//		for (int i = 0; i < arr.length; i++) {
//			if (!arr[i].isEmpty()) {
//				strResult += arr[i] + " ";
//			}
//		}
		strResult = input.trim();
		return strResult;
	}

	public ArrayList<Integer> getAllIndexesOfWordInDocument(String word,
			String urlContent) {
		ArrayList<Integer> lstResult = new ArrayList<Integer>();
		// int index = urlContent.indexOf(word);

		// while(index >= 0) {
		// lstResult.add(index);
		// index = urlContent.indexOf(word, index+1);
		//
		// }
		String[] arrContents = urlContent.trim().split("\\s+");
		String[] arrWordContent=word.split("\\s+");
		
		//System.out.println(arrWordContent.length+" length word content");
		//System.out.println(arrContents.length+" length content");
		// if word contains url find the string content instead
		int currentIndex=-1;
		int checkInsideTag=0;
		for (int i = 0; i < arrContents.length-arrWordContent.length; i++) {
			
//			if (strRepresentWord.toLowerCase().contains(
//					arrContents[i].toLowerCase())) {
//				lstResult.add(i);
//			}
			
			if(arrContents[i].equals(">")){
				checkInsideTag++;
			} else if (arrContents[i].equals("<")){
				checkInsideTag--;
			}
			
			if(checkInsideTag!=0||arrContents[i].equals(">")){
				continue;
			}
			//System.out.println(arrContents[i]+"\t"+currentIndex);
			currentIndex++;
			boolean checkAppear=true;
			for(int j=0;j<arrWordContent.length;j++){
				if(!arrWordContent[j].trim().toLowerCase().equals(arrContents[i+j].trim().toLowerCase())){
					checkAppear=false;
					break;
				}
			}
			if(checkAppear){
				lstResult.add(currentIndex);
			}
			
		}
		return lstResult;
	}

	private String getSourceContentInsideLink(String strInput,String contentProcess){
		String strResult="";
		String strLinkContent = strInput.trim().toLowerCase();
		String strContentSpace = getOneSpaceContent(strLinkContent);
		// System.out.println("content "+strContentSpace);

		String[] arrContents = contentProcess.split("\\s+");
		String strRepresentWord = "";
		boolean checkFirstAfterA = false;
	//	boolean checkFirstAfterLink = false;
		boolean checkInsideCorrectLink = false;

		for (int i = 0; i < arrContents.length; i++) {
			// System.out.println(arrContents[i]);
			if (arrContents[i].equals(">")&&checkInsideCorrectLink) {
				 checkFirstAfterA=true;
				
				//strRepresentWord += arrContents[i + 1].t+" ";
				//break;
			} else if (i<arrContents.length-1&& arrContents[i].equals("<")&& arrContents[i+1].equals("/a")&&checkInsideCorrectLink) {
				// checkFirstAfterLink=true;
					
				//strRepresentWord += arrContents[i + 1].t+" ";
				checkFirstAfterA = false;
				checkInsideCorrectLink=false;
				break;
			} 
			else if (strContentSpace.toLowerCase().trim().equals(arrContents[i].toLowerCase().trim())) {
				checkInsideCorrectLink=true;
				// lstResult.add(i);
			} else if (checkFirstAfterA&&checkInsideCorrectLink){
				strRepresentWord += arrContents[i].toLowerCase()+" ";
			}
		}
		return strRepresentWord;
	}
	
	public boolean isContainInURLLink(String strInput,
			ArrayList<String> lstWords, String contentProcess) {
		boolean isContain = false;
		//String[] arrInput = strInput.trim().split("/");
		String strLinkContent = strInput.trim().toLowerCase();
		String strContentSpace = getOneSpaceContent(strLinkContent);
		// System.out.println("content "+strContentSpace);

		String[] arrContents = contentProcess.split("\\s+");
		String strRepresentWord = "";
		boolean checkFirstAfterA = false;
	//	boolean checkFirstAfterLink = false;
		boolean checkInsideCorrectLink = false;

		for (int i = 0; i < arrContents.length; i++) {
		//	 System.out.println(arrContents[i]);
			if (arrContents[i].equals(">")&&checkInsideCorrectLink) {
				 checkFirstAfterA=true;
				
				//strRepresentWord += arrContents[i + 1].t+" ";
				//break;
			} else if (i<arrContents.length-1&& arrContents[i].equals("<")&& arrContents[i+1].equals("/a")&&checkInsideCorrectLink) {
				// checkFirstAfterLink=true;
					
				//strRepresentWord += arrContents[i + 1].t+" ";
				checkFirstAfterA = false;
				checkInsideCorrectLink=false;
				break;
			} 
			else if (strContentSpace.toLowerCase().trim().equals(arrContents[i].toLowerCase().trim())) {
				checkInsideCorrectLink=true;
				// lstResult.add(i);
			} else if (checkFirstAfterA&&checkInsideCorrectLink){
				strRepresentWord += arrContents[i].toLowerCase()+" ";
			}
		}
//		System.out.println("represent word "+strRepresentWord+" from "+strContentSpace);
//		Scanner sc=new Scanner(System.in);
//		sc.next();

		for (int i = 0; i < lstWords.size(); i++) {
			String word = getOneSpaceContent(lstWords.get(i).trim()
					.toLowerCase());
			if (strRepresentWord.contains(word)) {
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
		String urlStartWikiPage = urlPrefix + "wiki/Football";
		String fpSetOfTopic = "data" + File.separator + "pa3" + File.separator
				+ "TopicForWeightedQ.txt";
		String fpOutputText = "data" + File.separator + "pa3" + File.separator
				+ "ResultWeightQ.txt";
		String fpOutputBaseWebContent = "data" + File.separator + "pa3"
				+ File.separator + "ResultBaseWebContent.txt";
		WeightedQ wq = new WeightedQ();
		ArrayList<String> lstOutput = wq.heuristicComputeWeightedQ(
				urlStartWikiPage, fpSetOfTopic);
		StringBuilder sb = new StringBuilder();
		sb.append("Craw from :" + urlStartWikiPage + "\n");
		sb.append("Set of topics :" + wq.getLstWordsFromFile(fpSetOfTopic)
				+ "\n");
		sb.append("Num of results :" + wq.getLinks().size() + "\n");
		for (int i = 0; i < lstOutput.size(); i++) {
			// System.out.println(i+"\t"+lstLinks.get(i));
			sb.append(lstOutput.get(i) + "\n");
		}
		FileIO.writeStringToFile(sb.toString(), fpOutputText);
		FileIO.writeStringToFile(wq.getStrWebContent(), fpOutputBaseWebContent);
		System.out.println("Check the result in " + fpOutputText);

	}

}
