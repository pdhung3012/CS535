package pa1;

import java.io.File;
import java.util.HashSet;

import util.FileIO;

public class TimeAnalysis {

	public long[][] timeAnalysisFalPositive(String fpData,String fpQueries,int timeRun, BloomFilter... arrBf) {
		String strData = FileIO.readStringFromFile(fpData);
		String[] arrItem = strData.split("\n");
		
		String strQueries = FileIO.readStringFromFile(fpQueries);
		String[] arrQueries = strQueries.split("\n");
		
		boolean checkAppear = false;
		String strConflict = "";
		int[] arrNumConflict = new int[arrBf.length];
		HashSet<String> setDataString=new HashSet<String>(); 
		
		long[][] arrTimeResult=new long[arrBf.length][timeRun*2];
		
		for (int i = 0; i < arrNumConflict.length; i++) {
			arrNumConflict[i] = 0;
		}
		
		double[] arrFalsePositiveRate = new double[arrBf.length];
		
		for(int m=0;m<arrBf.length;m++){
			for(int n=0;n<arrBf.length;n++){
				arrTimeResult[m][n]=0;
			}
		}
		
		for(int indexRun=1;indexRun<=timeRun;indexRun++){
									
			for (int i = 0; i < arrItem.length; i++) {
				String strContent = arrItem[i].trim();
				setDataString.add(strContent.toLowerCase());
				for (int j = 0; j < arrBf.length; j++) {
					long startTime = System.currentTimeMillis();
					arrBf[j].add(strContent);
					long endTime   = System.currentTimeMillis();
					long totalTime = endTime - startTime;
					
					arrTimeResult[j][indexRun-1]+=totalTime;
				}
			}
		}
		
		
		int numTruePos=0,numFalsePos=0,numTrueNegative=0,numFalseNegative=0;
		
		for(int indexRun=timeRun+1;indexRun<=timeRun*2;indexRun++){
			for (int i = 0; i < arrQueries.length; i++) {
				String strQuery = arrQueries[i].trim();
				for (int j = 0; j < arrBf.length; j++) {
					long startTime = System.currentTimeMillis();
					checkAppear = arrBf[j].appears(strQuery);
					long endTime   = System.currentTimeMillis();
					long totalTime = endTime - startTime;
					arrTimeResult[j][indexRun-1]+=totalTime;
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
		}
		
	

		for (int i = 0; i < arrBf.length; i++) {
			arrFalsePositiveRate[i] = arrNumConflict[i] * 1.0 / arrQueries.length;
		}

		return arrTimeResult;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fpData = "data"+File.separator+"pa1"+File.separator+"data_2.txt";
		String fpQuery = "data"+File.separator+"pa1"+File.separator+"q_2.txt";
		
		
		if(args.length>=2){
			fpData=args[0];
			fpQuery=args[1];
		}
		
		TimeAnalysis ta = new TimeAnalysis();
		String[] arrContent=FileIO.readStringFromFile(fpQuery).trim().split("\n");
		int dataLength=arrContent.length;
		
		for(int i=1;i<=3;i++){
			// FNV false positive
			
			int size = dataLength; 
			int bitsPerElement =(int) Math.pow(2, 1+i);	
			int timeRun=10;
			
			BloomFilterFNV bfFNV = new BloomFilterFNV(size, bitsPerElement);
			BloomFilterMurmur bfMurMur=new BloomFilterMurmur(size, bitsPerElement);
			BloomFilterRan bfRan=new BloomFilterRan(size, bitsPerElement);
			DynamicFilter bfDyn=new DynamicFilter( bitsPerElement);
			long[][] arrResult = ta.timeAnalysisFalPositive(fpData,fpQuery,timeRun, bfFNV,bfMurMur,bfRan,bfDyn);
			int index = 0;
			
			System.out.println("Bit per element "+bitsPerElement);
			System.out.println("Add");
			for(index=0;index<4;index++){
				double timeForAdd=0;
				for(int indexRun=0;indexRun<timeRun;indexRun++){
					timeForAdd+=arrResult[index][indexRun];
				}
				timeForAdd=timeForAdd/timeRun;
				System.out.println(timeForAdd);
			}
			
			System.out.println("Search");
			for(index=0;index<4;index++){
				double timeForSearch=0;
				for(int indexRun=timeRun;indexRun<timeRun*2;indexRun++){
					timeForSearch+=arrResult[index][indexRun];
				}
				timeForSearch=timeForSearch/timeRun;
				System.out.println(timeForSearch);
			}
			
			
//			System.out.println("bitPerE = "+bitsPerElement+" FNV FP: " + arrResult[index++]);
//			System.out.println("bitPerE = "+bitsPerElement+" Mur mur FP: " + arrResult[index++]);
//			System.out.println("bitPerE = "+bitsPerElement+" Random FP: " + arrResult[index++]);
//			System.out.println("Dynamic false positive rate: " + arrResult[index++]);			
//			System.out.println("Dynamic Filters == "+ bfDyn.dynamicFilters.size());
			
		}
		
		
		
	}

}
