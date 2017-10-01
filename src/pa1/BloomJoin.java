package pa1;

import java.util.ArrayList;
import java.util.HashMap;

import util.FileIO;

public class BloomJoin {

	private String[] arrR1,arrR2;
	private HashMap<String,ArrayList<String>> mapFilterR3;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fopPrefix="data\\pa1\\";
		BloomJoin bj=new BloomJoin(fopPrefix+"Relation1.txt",fopPrefix +"Relation2.txt");
		int size = 2000000, bitsPerElement = 8;
		BloomFilterFNV bfr=new BloomFilterFNV(size, bitsPerElement);
		
		bj.doAction(bfr);
		bj.join(fopPrefix +"R3.txt");
		
	}
	
	public BloomJoin(String r1, String r2){
		arrR1=FileIO.readStringFromFile(r1).split("\n");
		arrR2=FileIO.readStringFromFile(r2).split("\n");
	}
	
	public void doAction(BloomFilter bloom){
		
		//create bloom for R1
		for(int i=0;i<arrR1.length;i++){
			String[] arrItemR1=arrR1[i].trim().split("\\s+");
			if(arrItemR1.length==2){
				bloom.add(arrItemR1[1]);
			}			
		}
		
		//r2 check the bloom in r1
		mapFilterR3=new HashMap<String, ArrayList<String>>();
		for(int i=0;i<arrR2.length;i++){
			String[] arrItemR2=arrR2[i].trim().split("\\s+");
			if(arrItemR2.length==2){
				if(bloom.appears(arrItemR2[0])){
					ArrayList<String> lstJoinCol=mapFilterR3.get(arrItemR2[0]);
					if(lstJoinCol==null){
						lstJoinCol=new ArrayList<String>();
						lstJoinCol.add(arrItemR2[1]);
						mapFilterR3.put(arrItemR2[0], lstJoinCol);
					} else{
						lstJoinCol.add(arrItemR2[1]);
					}
				}
			}			
		}
		
		//server 1 get data to join R3 in join function
		
	}

	public void join(String r3){
		String strR3Content="";
		for(int i=0;i<arrR1.length;i++){
			String[] arrItemR1=arrR1[i].trim().split("\\s+");
			if(arrItemR1.length==2){
				ArrayList<String> r3Item=mapFilterR3.get(arrItemR1[0]);
				String first2Col=arrItemR1[0]+"\t"+arrItemR1[1];
				if(r3Item!=null){
					//System.out.println(i+"\t"+arrItemR1[0]);
					for(int j=0;j<r3Item.size();j++){
						strR3Content+=first2Col+"\t"+r3Item.get(j)+"\n";
					}
				}
				
				
			}			
		}
		FileIO.writeStringToFile(strR3Content, r3);
	}
}
