package pa1;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Random;
/**
 * 
 * @author Shruti Sahu
 *
 */
public class BloomFilterRan extends BloomFilter {

	
	protected int[] a,b;
	public BloomFilterRan(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		this.filterSize =  pickPrime();
		this.filter = new BitSet(filterSize);
		this.a = new int[filterSize];
		this.b = new int[filterSize];
		for(int i = 0; i < filterSize; i++) {
			this.a[i] = new Random().nextInt(filterSize);
			this.b[i] = new Random().nextInt(filterSize);
			//System.out.println(a[i]+"\t"+b[i]);
		}
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
		//System.out.println("a=="+a+"\tb=="+b);
		for(int i = 0; i < numHashes; i++) {
			//System.out.println(i+"\t"+a[i]+"\t");
			int h = Math.abs(this.a[i]*s.hashCode() + this.b[i]);
			hashFnVals[i] =  h % filterSize;
		}
		return hashFnVals;
	}

}
