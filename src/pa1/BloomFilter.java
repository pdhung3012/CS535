package pa1;
/**
 * 
 * @author Shruti
 * @author Hung
 */

public abstract class BloomFilter {
	
	protected int filterSize;
	protected int numHashes;
	protected byte[] filter;
	protected int setSize;
	protected int bitsPerElements;
	protected int dataSize;
	
	BloomFilter(int bitsPerElement){
		this.bitsPerElements = bitsPerElement;
	}
	
	BloomFilter(int setSize, int bitsPerElement){
		this.setSize = setSize;
		this.bitsPerElements = bitsPerElement;
		this.filterSize = setSize * bitsPerElement;
		this.numHashes = (int) (Math.log(2) * bitsPerElement);
		this.filter = new byte[filterSize];
	}
	/**
	 * 
	 * @param s
	 * @return 
	 */
	
	public abstract int[] hashFunction(String s);
	
	public void add(String s){
		int[] hashValues = hashFunction(s);
		s = s.toLowerCase();
		for(int k = 0; k < numHashes; k++) {
			 filter[hashValues[k]] = 1;
		}
		dataSize++;
	}
	
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public boolean appears(String s){
		s = s.toLowerCase();
		return true;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int filterSize(){
		return this.filterSize; 
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int dataSize(){
		return dataSize;
	}
	
	/**
	 * 
	 * @return
	 */
	public int numHashes(){
		return this.numHashes;
	}
	
}
