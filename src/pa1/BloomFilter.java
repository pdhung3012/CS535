package pa1;

public abstract class BloomFilter {
	
	protected int filterSize;
	protected int numHashes;
	protected byte[] filter;
	protected int setSize;
	protected int bitsPerElements;
	
	BloomFilter(int setSize, int bitsPerElement){
		this.filterSize = setSize * bitsPerElement;
		this.numHashes = (int) (Math.log(2) * bitsPerElement);
		this.filter = new byte[filterSize];
		this.setSize = setSize;
		this.bitsPerElements = bitsPerElement;
		
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
		
	}
	
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public boolean appears(String s){
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
		return 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public int numHashes(){
		return this.numHashes;
	}
	
}
