package pa1;

import util.FileIO;

public class FalsePositives {

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
		int size = 2000000, bitsPerElement = 8;
		String fpData = "data\\pa1\\Relation2.txt";
		FalsePositives fp = new FalsePositives();

		// FNV false positive
		BloomFilterFNV bfFNV = new BloomFilterFNV(size, bitsPerElement);
		// BloomFilterMurmur bfMurMur=new BloomFilterMurmur(size,
		// bitsPerElement);
		// BloomFilterRan bfRan=new BloomFilterRan(size, bitsPerElement);

		// double[] arrResult=
		// fp.calculateFalPositive(fpData,bfFNV,bfMurMur,bfRan);
		double[] arrResult = fp.calculateFalPositive(fpData, bfFNV);

		int index = 0;
		System.out.println("FNV false positive rate: " + arrResult[index++]);
//		System.out
//				.println("Mur mur false positive rate: " + arrResult[index++]);
//		System.out.println("Random false positive rate: " + arrResult[index++]);

	}

}
