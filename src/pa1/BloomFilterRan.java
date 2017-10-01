package pa1;

public class BloomFilterRan extends BloomFilter {

	private int primeNumber;
	private int N;
	private int t;
	public BloomFilterRan(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		this.N = setSize;
		this.t = bitsPerElement;
		// TODO Auto-generated constructor stub
	}
	
	public int choosePrime() {
		primeNumber = t*N;
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
		// TODO Auto-generated method stub
		return null;
	}

}
