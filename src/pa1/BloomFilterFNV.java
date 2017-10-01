package pa1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;

import util.FileIO;

public class BloomFilterFNV extends BloomFilter {

	private static final long FNV1_64_INIT = 0xcbf29ce484222325L;
	private static final long FNV1_PRIME_64 = 1099511628211L;

	private HashSet<String> setSource;
	private int bitsPerElement;
	private HashMap<String, Integer> target;
	int numberOfHashFunction = 10;
	private HashSet<String> setFilter;

	public BloomFilterFNV(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		setFilter = new HashSet<String>();
		setSource = new HashSet<String>();
		System.out.println("Filter size "+filterSize);
		for(int i=0;i<filterSize;i++){
			filter[i]=0;
		}
	}

	public long hash64(byte[] data) {
		return hash64(data, data.length);
	}

	public long[] hashMultiple64(byte[] data) {
		long originalResult = hash64(data, data.length);
		long[] arrResult = new long[numberOfHashFunction];
		
		for (int i = 1; i <= numberOfHashFunction; i++) {
			long newValue = originalResult + originalResult % i;
			arrResult[i-1] = newValue;
		}
		return arrResult;

	}

	public long hash64(byte[] data, int length) {
		long hash = FNV1_64_INIT;
		for (int i = 0; i < length; i++) {
			hash ^= (data[i] & 0xff);
			hash *= FNV1_PRIME_64;
		}

		return hash;
	}

	public void add(String s) {

		setSource.add(s);
//		int[] arrResult = hashFunction(s);
//		for (int i = 0; i < arrResult.length; i++) {
//			
//			filter[arrResult[i]]=1;
//			//setFilter.add(String.valueOf(arrResult[i]));
//		}
		
		long[] arrResult = hashMultiple64(s.getBytes());
		for (int i = 0; i < arrResult.length; i++) {
			setFilter.add(String.valueOf(arrResult[i]));
		}

	}

	public boolean appears(String s) {

		setSource.add(s);
		int[] arrResult = hashFunction(s);
		boolean isAppeared = true;

//		for (int i = 0; i < arrResult.length; i++) {
//			if(filter[arrResult[i]]!=1){
//				isAppeared = false;
//				break;				
//			}
//		}
		
		for (int i = 0; i < arrResult.length; i++) {
			if (!setFilter.contains(String.valueOf(arrResult[i]))) {
				isAppeared = false;
				break;

			}
			;
		}
		return isAppeared;
	}

	public int filterSize() {
		return setFilter.size();
	}

	public int numHashes() {
		return numberOfHashFunction;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String strInput = "hello world";
		// long strHashValue = hash64(strInput.getBytes());
		// System.out.println(strInput + "\t" + strHashValue);
		// strHashValue = hash64(strInput.getBytes());
		// System.out.println(strInput + "\t" + strHashValue);

		BloomFilterFNV fnvFilter = new BloomFilterFNV(10, 8);
		String fpInput = "data\\pa1\\Relation2.txt";
		String strInput = FileIO.readStringFromFile(fpInput);
		String[] arrItem = strInput.split("\n");
		boolean checkAppear = false;
		String strConflict = "";
		int indexConflict = -1;
		int numberConflict=0;
		for (int i = 0; i < arrItem.length; i++) {
			String strContent = arrItem[i].split("\\s+")[0];
			checkAppear = fnvFilter.appears(strContent);
			if (checkAppear) {
				strConflict = strContent;
				indexConflict = i + 1;
				numberConflict++;
				System.out.println("Conflict position " + indexConflict + " "
						+ strConflict);
				//break;
			}
			fnvFilter.add(strContent);

		}
		if (numberConflict>0) {
			System.out.println("Number conflict " + numberConflict);
		} else {
			System.out.println("No conflict");
		}

	}

	
	@Override
	public int[] hashFunction(String s) {
		// TODO Auto-generated method stub
		byte[] data=s.getBytes();
		long originalResult = hash64(data, data.length);
		if(originalResult<0){
			originalResult=originalResult*(-1);
		}
		//System.out.println(s+" original long "+originalResult);
		int[] arrResult = new int[numHashes];
		
		for (int i = 1; i <= numHashes; i++) {
			long newValue = originalResult + originalResult % i;
			arrResult[i-1] =(int) newValue;
		}
		return arrResult;
	}

}
