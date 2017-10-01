package pa1;

import java.math.BigInteger;
import java.util.Random;
/**
 * 
 * @author Shruti Sahu
 *
 */
public class BloomFilterRan extends BloomFilter {


	public BloomFilterRan(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		this.filterSize =  pickPrime();
		//System.out.println("filterSize=="+filterSize);
		// TODO Auto-generated constructor stub
	}
	
	public BloomFilterRan(int bitsPerElement) {
		super(bitsPerElement);
		// TODO Auto-generated constructor stub
	}

	protected int pickPrime() {
		int p = bitsPerElements*setSize;
		//System.out.println(p);
		while(true) {
			for(int i = 2; i <= p/2 ; i++) {
				if(p%i==0)
					p++;
				else
					return p;
			}
		}
	}
	
	@Override
	public int[] hashFunction(String s) {
		int[] hashFnVals = new int[numHashes];
		int a = new Random().nextInt(filterSize);
		int b = new Random().nextInt(filterSize);
		//System.out.println("a=="+a+"\tb=="+b);
		for(int i = 0; i < numHashes; i++) {
			hashFnVals[i] = (int) (Math.abs(a*s.hashCode() + b) % filterSize);
		}
		return hashFnVals;
	}

}
