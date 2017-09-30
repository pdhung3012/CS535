package pa1;

import java.util.HashMap;
import java.util.HashSet;

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
		setFilter=new HashSet<String>();
	}

	public long hash64(byte[] data) {
		return hash64(data, data.length);
	}
	
	public long[] hashMultiple64(byte[] data) {
		long originalResult= hash64(data, data.length);
		long[] arrResult=new long[numberOfHashFunction];
		for(int i=0;i<numberOfHashFunction;i++){
			long newValue=originalResult+originalResult%i;
			arrResult[i]=newValue;
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
		long[] arrResult=hashMultiple64(s.getBytes());
		for(int i=0;i<arrResult.length;i++){
			setFilter.add(String.valueOf(arrResult[i]));
		}
		
	}

	public boolean appears(String s) {

		setSource.add(s);
		long[] arrResult=hashMultiple64(s.getBytes());
		boolean isAppeared=true;
		for(int i=0;i<arrResult.length;i++){
			if(!setFilter.contains(String.valueOf(arrResult[i]))){
				isAppeared=false;
				break;
				
			};
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

	}

	

	@Override
	public int[] hashFunction(String s) {
		// TODO Auto-generated method stub
		return null;
	}

}
