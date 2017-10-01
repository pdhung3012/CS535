package pa1;

import util.FileIO;

public class FalsePositives {

	public double calculateFalPositive(BloomFilter bf,String fpInput){
		String strInput = FileIO.readStringFromFile(fpInput);
		String[] arrItem = strInput.split("\n");
		boolean checkAppear = false;
		String strConflict = "";
		int indexConflict = -1;
		int numberConflict=0;
		for (int i = 0; i < arrItem.length; i++) {
			String strContent = arrItem[i].split("\\s+")[0];
			checkAppear = bf.appears(strContent);
			if (checkAppear) {
				strConflict = strContent;
				indexConflict = i + 1;
				numberConflict++;
				System.out.println("Conflict position " + indexConflict + " "
						+ strConflict);
				//break;
			}
			bf.add(strContent);
		}
		if (numberConflict>0) {
			System.out.println("Number conflict " + numberConflict);
		} else {
			System.out.println("No conflict");
		}
		return numberConflict*1.0/arrItem.length;
	}
	//calculate false poisitive rate of each hash functions
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int size=2000000,bitsPerElement=8;
		String fpData="data\\pa1\\Relation2.txt";
		FalsePositives fp=new FalsePositives();
		
		//mur mur false positive		
		BloomFilterMurmur bfMurMur=new BloomFilterMurmur(size, 8);
		System.out.println("Mur mur false positive rate: "+fp.calculateFalPositive(bfMurMur, fpData));
		
	}

}
