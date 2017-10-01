package pa1;

import java.util.Random;

public class BloomFilterRan extends BloomFilter {

	private int primeNumber;
	public BloomFilterRan(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		// TODO Auto-generated constructor stub
	}
	
	private int pickPrime() {
		int p = bitsPerElements*setSize;
		while(!checkPrime(p)) {
			p++;
		}
		return p;
	}
	
	private boolean checkPrime(int p) {
		for(int i = 1; i <= p/2; i++) {
			if(p%i == 0)
				return false;
			else
				continue;
		}
		return true;
	}
	@Override
	public int[] hashFunction(String s) {
		int[] hashFnVals = new int[numHashes];
		primeNumber = pickPrime();
		int a = new Random().nextInt(primeNumber);
		int b = new Random().nextInt(primeNumber);
		for(int i = 0; i < numHashes; i++) {
			hashFnVals[i] = (a*s.hashCode() + b) % primeNumber;
		}
		return hashFnVals;
	}

}
