package pa1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import util.FileIO;

public class BloomFilterFNV extends BloomFilter {

	

	private static final long FNV_64_INIT = 0xcbf29ce484222325L;
	private static final long FNV_64_PRIME = 0x100000001b3L;

	private int bitsPerElement;
	
	public BloomFilterFNV(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		System.out.println("Filter size " + filterSize);
		System.out.println("Number of hash " + numHashes);
//		for (int i = 0; i < filterSize; i++) {
//			filter[i] = 0;
//		}
	}

	private long hash64(byte[] data) {
		return hash64(data, data.length);
	}

	

	private long hash64(byte[] data, int length) {
		long rv = FNV_64_INIT;
		for (int i = 0; i < length; i++) {
			rv ^= data[i];
			rv *= FNV_64_PRIME;
		}
		return rv;
	}

	private long hash64(final String k) {
		long rv = FNV_64_INIT;
		final int len = k.length();
		for (int i = 0; i < len; i++) {
			rv ^= k.charAt(i);
			rv *= FNV_64_PRIME;
		}
		return rv;
	}

	@Override
	public void add(String s) {
		s=s.toLowerCase();
		int[] arrResult = hashFunction(s);
		for (int i = 0; i < arrResult.length; i++) {
			try {
				filter.set(arrResult[i],true);
			} catch (Exception ex) {
				System.out.println(arrResult[i] + " err");
				ex.printStackTrace();
				throw new ArrayIndexOutOfBoundsException(ex.getMessage());

			}

		
		}


	}

	@Override
	public boolean appears(String s) {

		int[] arrResult = hashFunction(s);
		boolean isAppeared = true;

		for (int i = 0; i < arrResult.length; i++) {
			try {
				if (!filter.get(arrResult[i])) {
					isAppeared = false;
					break;
				}
			} catch (Exception ex) {
				System.out.println(s + " " + i + " " + arrResult[i] + " error");
				ex.printStackTrace();
				throw new ArrayIndexOutOfBoundsException(ex.getMessage());
			}

		}

	
		return isAppeared;
	}

	@Override
	public int filterSize() {
		return filterSize;
	}

	

	

	@Override
	public int[] hashFunction(String s) {
		// TODO Auto-generated method stub

		// System.out.println(s + " original long " + originalResult);
		int[] arrResult = new int[numHashes];

		for (int i = 1; i <= numHashes; i++) {
			byte[] data = null;
			if (i == 1) {
				data = (s).getBytes();
			} else {
				data = (String.format("%02d", i)+""+s).getBytes();
			}

			long originalResult = hash64(data);
			originalResult = Math.abs(originalResult);
			// long newValue = originalResult % (filterSize-i*100);
			// System.out.println("Original result "+originalResult);
			arrResult[i - 1] = (int) (originalResult % filterSize);
			// arrResult[i - 1] = Math.abs(((int) originalResult) % filterSize);
			// System.out.println("Original result "+originalResult+" "+arrResult[i
			// - 1]);
		}
		return arrResult;
	}

}
