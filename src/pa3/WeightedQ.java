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

	public Map<String, Double> queue;
	private ArrayList<String> links;
	private String strWebContent;
	
	
	
	public String getStrWebContent() {
		return strWebContent;
	}

	public void setStrWebContent(String strWebContent) {
		this.strWebContent = strWebContent;
	}

	public Map<String, Double> getQueue() {
		return queue;
	}

	public void setQueue(Map<String, Double> queue) {
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
//		if (queue.size() > 1) {
//
//		}

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
		links=new ArrayList<String>();
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
			StringBuilder sbContent=new StringBuilder();
			HashSet<String> setLinks=new HashSet<String>();
			while ((line = br.readLine()) != null) {

				sbContent.append(line+"\n");
				if (line.contains("<p>")) {
					isP = true;
				}

				if (isP && line.contains("<a href=")) {

					match = pattern.matcher(line);

					while (match.find()) {
						String address = line.substring(match.start(0),
								match.end(0));
						setLinks.add(address);
					}
				}
			}
			
			// logging end time of processing the page
//			long endTime = System.currentTimeMillis();
//			long timeTaken = endTime - startTime;
			// totalTime += timeTaken;
			is.close();
			strWebContent=sbContent.toString();
			links=new ArrayList<String>();
			for(String strItem:setLinks){
				links.add(strItem);
			}
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

	public void distWordAndWord(String word1, String word2) {

	}

	public void distWordAndSet(String word1, String word2) {

	}
	
	public ArrayList<String> heuristicComputeWeightedQ(String urlInput,String fpInputTopics){
		ArrayList<String> lstOutput=new ArrayList<String>();
		ArrayList<String> lstWords=new ArrayList<String>();
		lstWords=getLstWordsFromFile(fpInputTopics);
		downloadPagesAndLinks(urlInput);
		 
		for(int i=0;i<links.size();i++){
			if(isContainInURLLink(urlInput, lstWords)){
				add(urlInput, 1.0);				
			} else{
				
			};
		}
		
		//get WeightedQ
		lstOutput=new ArrayList<String>();
		for(String str:queue.keySet()){
			Double val=queue.get(str);
			lstOutput.add(str+"\t"+val);
		}
		
		
		return lstOutput;
	}
	
	public String getOneSpaceContent(String input){
		String strResult="";
		String[] arr=input.trim().split("\\s+");
		for(int i=0;i<arr.length;i++){
			if(!arr[i].isEmpty()){
				strResult+=arr[i]+" ";
			}
		}
		strResult=strResult.trim();
		return strResult;
	}
	
	public boolean isContainInURLLink(String strInput,ArrayList<String> lstWords){
		boolean isContain=false;
		String[] arrInput=strInput.trim().split("/");
		String strLinkContent=arrInput[arrInput.length-1];
		String strContentSpace=getOneSpaceContent(strLinkContent.replaceAll(":"," ").replaceAll("_"," "));
				
		for(int i=0;i<lstWords.size();i++){
			String word=getOneSpaceContent(lstWords.get(i).trim().toLowerCase());
			if(strContentSpace.contains(word)){
				isContain=true;
				break;
			}
		}
		
		
		return isContain;
	}
	
	private ArrayList<String> getLstWordsFromFile(String fpInput){
		String[] arrWords=FileIO.readStringFromFile(fpInput).trim().split("\n");
		ArrayList<String> lstWords=new ArrayList<String>();
		for(int i=0;i<arrWords.length;i++){
			String item=arrWords[i].trim().toLowerCase();
			lstWords.add(item);
			//if()
		}
		return lstWords;
	}

	public static void main(String[] args) {
		String urlPrefix = "https://en.wikipedia.org/";
		String urlStartWikiPage = urlPrefix + "wiki/Tennis";
		String fpSetOfTopic = "data" + File.separator + "pa3" + File.separator
				+ "TopicForWeightedQ.txt";
		String fpOutputText="data" + File.separator + "pa3" + File.separator
				+ "ResultWeightQ.txt";
		WeightedQ wq=new WeightedQ();
		ArrayList<String> lstOutput=wq.heuristicComputeWeightedQ(urlStartWikiPage, fpSetOfTopic);
		StringBuilder sb=new StringBuilder();
		sb.append("Craw from :"+urlStartWikiPage+"\n");
		sb.append("Set of topics :"+wq.getLstWordsFromFile(fpSetOfTopic)+"\n");
		sb.append("Num of results :"+lstOutput.size()+"\n");
		for(int i=0;i<lstOutput.size();i++){
			//System.out.println(i+"\t"+lstLinks.get(i));
		}
		

	}

}
