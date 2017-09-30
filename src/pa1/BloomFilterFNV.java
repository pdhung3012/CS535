package pa1;

import java.util.HashMap;
import java.util.HashSet;

public class BloomFilterFNV extends BloomFilter{

	private static final long FNV1_64_INIT = 0xcbf29ce484222325L;
	private static final long FNV1_PRIME_64 = 1099511628211L;

	private HashSet<String> setSource;
	private int bitsPerElement;
	private HashMap<String,Integer> target;
	
	

	public BloomFilterFNV(int setSize, int bitsPerElement) {
		super(setSize,bitsPerElement);
		
	}

	public static long hash64(byte[] data) {
		return hash64(data, data.length);
	}

	public static long hash64(byte[] data, int length) {
		long hash = FNV1_64_INIT;
		for (int i = 0; i < length; i++) {
			hash ^= (data[i] & 0xff);
			hash *= FNV1_PRIME_64;
		}

		return hash;
	}

	public void add(String s) {
		setSource.add(s);
	}

	public boolean appears(String s) {
		
		//
		return true;
	}

	public int filterSize() {
		return 0;
	}

	public int numHashes() {
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String strInput = "hello world";
//		long strHashValue = hash64(strInput.getBytes());
//		System.out.println(strInput + "\t" + strHashValue);
//		strHashValue = hash64(strInput.getBytes());
//		System.out.println(strInput + "\t" + strHashValue);
		
		BloomFilterFNV fnvFilter=new BloomFilterFNV(10, 8);
		
	}

	@Override
	public int hashFunction() {
		// TODO Auto-generated method stub
		return 0;
	}

}
