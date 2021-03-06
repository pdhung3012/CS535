package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Utility {

	public static void main_old(String[] args) throws IOException {
		BufferedReader bfr = new BufferedReader(new FileReader("data/pa1/query2.txt"));
		BufferedWriter bfw = new BufferedWriter(new FileWriter("data/pa1/words2.txt", true));
		String s ="";
		int count=0;
		while(count != 379){
			s=bfr.readLine();
			bfw.write(s);
			bfw.write("\n");
			count++;
//			String s1[] = s.split("   ");
//			bfw.write(s1[0]);
//			bfw.write("\n");
//			bfw.write(s1[1]);
//			bfw.write("\n");
		}
		System.out.println("Done");
		
	}
	
	public static void main(String[] args) throws IOException {
		RandomString gen = new RandomString(20, ThreadLocalRandom.current());
		int numberHash=500000;
		int sizeSyn=1;
		
		HashSet<String> setData=new HashSet<String>();
		StringBuilder strData=new StringBuilder();
		while(setData.size()<(numberHash/sizeSyn)){
			
			String strOrigin=gen.nextString();
			//System.out.println(strOrigin);
			if(!setData.contains(strOrigin)){
				setData.add(strOrigin);
				if(sizeSyn==1){
					strData.append(strOrigin+"\n");
				} else{
					for(int i=1;i<=sizeSyn;i++){
						String strItem=strOrigin+String.format("%05d", i);
						//System.out.println(strItem);
						strData.append(strItem+"\n");						
					}
				}
				
			}
			
			System.out.println("Data "+setData.size());
		}
		FileIO.writeStringToFile(strData.toString(), "data"+File.pathSeparator+"pa1"+File.pathSeparator+"data_2.txt");
		System.out.println( "Done step 1");
		
		StringBuilder strQuery=new StringBuilder();
		HashSet<String> setQuery=new HashSet<String>();		
		while(setQuery.size()<numberHash/(2*sizeSyn)){
			String strOrigin=gen.nextString();
			

			if(!setData.contains(strOrigin)&&!setQuery.contains(strOrigin)){
				setQuery.add(strOrigin);
				if(sizeSyn==1){
					strQuery.append(strOrigin+"\n");
				} else{
					for(int i=1;i<=sizeSyn;i++){
						String strItem=strOrigin+String.format("%05d", i);
						strQuery.append(strItem+"\n");	

					}
				}
				
			}
						
			System.out.println("Query "+setQuery.size());
		}
		
		for(String strOrigin:setData){
			setQuery.add(strOrigin);
			if(sizeSyn==1){
				strQuery.append(strOrigin+"\n");
			} else{
				for(int i=1;i<=sizeSyn;i++){
					String strItem=strOrigin+String.format("%05d", i);
					strQuery.append(strItem+"\n");	

				}
			}
			
			if(setQuery.size()==numberHash/sizeSyn){
				break;
			}
				
			
		}
		//while(setQuery.size()<numberHash/sizeSyn){
			//String strOrigin=gen.nextString();

		
		FileIO.writeStringToFile(strQuery.toString(), "data"+File.pathSeparator+"pa1"+File.pathSeparator+"q_2.txt");
		
		
		System.out.println("Done");
		
	}

}
