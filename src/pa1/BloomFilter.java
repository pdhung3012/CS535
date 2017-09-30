package pa1;

public abstract class BloomFilter {
	
	
	BloomFilter(){
		
	}
	/**
	 * 
	 * @param s
	 */
	
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
		
		return 0;
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
		return 0;
	}
	
}
