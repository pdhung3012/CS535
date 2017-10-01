package pa1;

public class BloomFilterRan extends BloomFilter {

	private int primeNumber;
	public BloomFilterRan(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		// TODO Auto-generated constructor stub
	}
	
	public int choosePrime() {
		primeNumber = bitsPerElements*setSize;
		while(!checkPrime(primeNumber)) {
			primeNumber++;
		}
		return primeNumber;
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
		
		return null;
	}

}
