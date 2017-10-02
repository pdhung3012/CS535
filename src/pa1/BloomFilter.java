package pa1;
/**
 * 
 * @author Shruti sahu
 * @author Hung
 */

public abstract class BloomFilter {
	
	protected int filterSize;
	protected int numHashes;
	protected byte[] filter;
	protected int setSize;
	protected int bitsPerElements;
	protected int dataSize;
	
	/**
	 * Constructor with only one parameter i.e bitsPerElement
	 * @param bitsPerElement
	 */
	BloomFilter(int bitsPerElement){
		this.bitsPerElements = bitsPerElement;
	}
	
	/**
	 * Constructor with 2 parameters
	 * @param setSize
	 * @param bitsPerElement
	 */
	BloomFilter(int setSize, int bitsPerElement){
		this.setSize = setSize;
		this.bitsPerElements = bitsPerElement;
		this.filterSize = setSize * bitsPerElement;
		this.numHashes = (int) (Math.log(2) * bitsPerElement);
		this.filter = new byte[filterSize];
		
	}
	/**
	 * Creating the k hash functions and computing the values of each hash function
	 * @param s
	 * @return int[]
	 */
	
	public abstract int[] hashFunction(String s);
	
	/**
	 * Adding a string to the bloom filter
	 * @param s
	 */
	public void add(String s){
		int[] hashValues = hashFunction(s);
		s = s.toLowerCase();
		for(int k = 0; k < numHashes; k++) {
			 filter[hashValues[k]] = 1;
		}
		dataSize++;
	}
	
	
/**
 * To check if the String s is in the bloom filter or not
 * @param s
 * @return boolean
 */
	public boolean appears(String s){
		s = s.toLowerCase();
		int[] hashResult = hashFunction(s);
		for(int i = 0; i < hashResult.length; i++) {
			if(filter[hashResult[i]] == 0) 
				return false;	
		}
		return true;
	}
	
	
	/**
	 * Returns the filter size
	 * @return
	 */
	public int filterSize(){
		return this.filterSize; 
	}
	
	
	/**
	 * Returns the data size
	 * @return
	 */
	public int dataSize(){
		return dataSize;
	}
	
	/**
	 * Returns the number of hash functions
	 * @return
	 */
	public int numHashes(){
		return this.numHashes;
	}
	
}
