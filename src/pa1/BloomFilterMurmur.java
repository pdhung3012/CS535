package pa1;
/**
 * BloomFilterMurmur 
 * part 1.2 of Programming assignment 1
 * 
 * @author Shruti
 *
 */

public class BloomFilterMurmur extends BloomFilter{
	
	/**
	 * BloomFilterMurmur creates a bloom filter that 
	 * can store a Set S of cardinality setSize using
	 * murmur hash functions
	 * @param setSize 
	 * @param bitsPerElement
	 */

	BloomFilterMurmur(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashFunction() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}
