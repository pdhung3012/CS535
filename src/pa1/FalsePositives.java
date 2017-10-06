package pa1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import util.FileIO;

public class FalsePositives {

	
	public double[] calculateFalPositive(String fpData,String fpQueries, BloomFilter... arrBf) {
		String strData = FileIO.readStringFromFile(fpData);
		String[] arrItem = strData.split("\n");
		
		String strQueries = FileIO.readStringFromFile(fpQueries);
		String[] arrQueries = strQueries.split("\n");
		
		boolean checkAppear = false;
		String strConflict = "";
		int[] arrNumConflict = new int[arrBf.length];
		HashSet<String> setDataString=new HashSet<String>(); 
		
		for (int i = 0; i < arrNumConflict.length; i++) {
			arrNumConflict[i] = 0;
		}
		
		double[] arrFalsePositiveRate = new double[arrBf.length];
		for (int i = 0; i < arrItem.length; i++) {
			String strContent = arrItem[i].trim();
			setDataString.add(strContent.toLowerCase());
			for (int j = 0; j < arrBf.length; j++) {
				arrBf[j].add(strContent);
			}

		}
		int numTruePos=0,numFalsePos=0,numTrueNegative=0,numFalseNegative=0;
		for (int i = 0; i < arrQueries.length; i++) {
			String strQuery = arrQueries[i].trim();
			for (int j = 0; j < arrBf.length; j++) {
				checkAppear = arrBf[j].appears(strQuery);
				boolean isActuallyAppear=setDataString.contains(strQueries);
				if (checkAppear && !isActuallyAppear) {
					arrNumConflict[j]++;
					numFalsePos++;
				} else if(checkAppear && isActuallyAppear){
					numTruePos++;
				} else if(!checkAppear && !isActuallyAppear){
					numTrueNegative++;
				} else if(!checkAppear && isActuallyAppear){
					numFalseNegative++;
				}
				
			}

		}
	

		for (int i = 0; i < arrBf.length; i++) {
			arrFalsePositiveRate[i] = arrNumConflict[i] * 1.0 / arrQueries.length;
		}

		return arrFalsePositiveRate;
	}
	
	
	
	
	// calculate false poisitive rate of each hash functions
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String fpData = "data\\pa1\\words2.txt";
//		String fpQuery = "data\\pa1\\query.txt";
		String fpData = "data"+File.separator+"pa1"+File.separator+"data_2.txt";
		String fpQuery = "data"+File.separator+"pa1"+File.separator+"q_2.txt";
		String[] arrContent=FileIO.readStringFromFile(fpQuery).trim().split("\n");
		int dataLength=arrContent.length;
		FalsePositives fp = new FalsePositives();

		
		for(int i=1;i<=3;i++){
			// FNV false positive
			
			int size = dataLength; 
			int bitsPerElement =(int) Math.pow(2, 1+i);		
			
			BloomFilterFNV bfFNV = new BloomFilterFNV(size, bitsPerElement);
			BloomFilterMurmur bfMurMur=new BloomFilterMurmur(size, bitsPerElement);
			BloomFilterRan bfRan=new BloomFilterRan(size, bitsPerElement);
			DynamicFilter bfDyn=new DynamicFilter( bitsPerElement);
			double[] arrResult = fp.calculateFalPositive(fpData,fpQuery, bfFNV,bfMurMur,bfRan,bfDyn);
			int index = 0;
			System.out.println("Theoritical FP= "+Math.pow(0.618, bitsPerElement));
			System.out.println("FNV FP: " + arrResult[index++]);
			System.out.println("Mur mur FP: " + arrResult[index++]);
			System.out.println("Random FP: " + arrResult[index++]);
			System.out.println("Dynamic FP: " + arrResult[index++]);
			
			//System.out.println("Dynamic Filters == "+ bfDyn.dynamicFilters.size());
			
		}
		
	}

}
