package pa1;

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
		for (int i = 0; i < arrNumConflict.length; i++) {
			arrNumConflict[i] = 0;
		}
		double[] arrFalsePositiveRate = new double[arrBf.length];
		for (int i = 0; i < arrItem.length; i++) {
			String strContent = arrItem[i].trim();
			for (int j = 0; j < arrBf.length; j++) {
				arrBf[j].add(strContent);
			}

		}
		
		for (int i = 0; i < arrQueries.length; i++) {
			String strQuery = arrQueries[i].trim();
			for (int j = 0; j < arrBf.length; j++) {
				checkAppear = arrBf[j].appears(strQuery);
				if (checkAppear) {
					arrNumConflict[j]++;
					
				}
				
			}

		}
	

		for (int i = 0; i < arrBf.length; i++) {
			arrFalsePositiveRate[i] = arrNumConflict[i] * 1.0 / arrQueries.length;
		}

		return arrFalsePositiveRate;
	}
	
	public double[] calculateFalPositive(String fpInput, BloomFilter... arrBf) {
		String strInput = FileIO.readStringFromFile(fpInput);
		String[] arrItem = strInput.split("\n");
		boolean checkAppear = false;
		String strConflict = "";
		int indexConflict = -1;
		int numberConflict = 0;
		int[] arrNumConflict = new int[arrBf.length];
		for (int i = 0; i < arrNumConflict.length; i++) {
			arrNumConflict[i] = 0;
		}
		double[] arrFalsePositiveRate = new double[arrBf.length];
		for (int i = 0; i < arrItem.length; i++) {
			String strContent = arrItem[i].split("\\s+")[0];
			for (int j = 0; j < arrBf.length; j++) {
				checkAppear = arrBf[j].appears(strContent);
				if (checkAppear) {
					// strConflict = strContent;
					// indexConflict = i + 1;
					arrNumConflict[j]++;
					// System.out.println("Conflict position " + indexConflict +
					// " "
					// + strConflict);
				}
				arrBf[j].add(strContent);
			}

		}
		// if (numberConflict>0) {
		// System.out.println("Number conflict " + numberConflict);
		// } else {
		// System.out.println("No conflict");
		// }

		for (int i = 0; i < arrBf.length; i++) {
			arrFalsePositiveRate[i] = arrNumConflict[i] * 1.0 / arrItem.length;
		}

		return arrFalsePositiveRate;
	}

	// calculate false poisitive rate of each hash functions
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String fpData = "data\\pa1\\words2.txt";
//		String fpQuery = "data\\pa1\\query.txt";
		String fpData = "data/pa1/words2.txt";
		String fpQuery = "data/pa1/query.txt";
		FalsePositives fp = new FalsePositives();

		
		for(int i=1;i<=3;i++){
			// FNV false positive
			
			int size = 500000; 
			int bitsPerElement =(int) Math.pow(2, 1+i);		
			
			BloomFilterFNV bfFNV = new BloomFilterFNV(size, bitsPerElement);
			BloomFilterMurmur bfMurMur=new BloomFilterMurmur(size, bitsPerElement);
			BloomFilterRan bfRan=new BloomFilterRan(size, bitsPerElement);
			DynamicFilter bfDyn=new DynamicFilter( bitsPerElement);
			double[] arrResult = fp.calculateFalPositive(fpData,fpQuery, bfFNV,bfMurMur,bfDyn);
			int index = 0;
			System.out.println("bitPerE = "+bitsPerElement+" FNV FP: " + arrResult[index++]);
			System.out.println("bitPerE = "+bitsPerElement+" Mur mur FP: " + arrResult[index++]);
			//System.out.println("bitPerE = "+bitsPerElement+" Random FP: " + arrResult[index++]);
			System.out.println("Dynamic false positive rate: " + arrResult[index++]);

		}
		
	}

}
