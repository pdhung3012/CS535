package pa1;

public abstract class BloomFilter {
	
	protected int filterSize;
	protected int numHashes;
	protected byte[] filter;
	
	BloomFilter(int setSize, int bitsPerElement){
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
		s = s.toLowerCase();
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
