package pa1;

/**
 * BloomFilterMurmur part 1.2 of Programming assignment 1
 * 
 * @author Shruti
 *
 */

public class BloomFilterMurmur extends BloomFilter {

	/**
	 * BloomFilterMurmur creates a bloom filter that can store a Set S of
	 * cardinality setSize using murmur hash functions
	 * 
	 * @param setSize
	 * @param bitsPerElement
	 */

	BloomFilterMurmur(int setSize, int bitsPerElement) {
		super(setSize, bitsPerElement);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int[] hashFunction(String s) {
		int[] hashFnVals = new int[numHashes];

		final long m = 0xc6a4a7935bd1e995L;
		final int r = 47;
		
		for (int j = 0; j < numHashes; j++) {
			int seed = 0xe17a1465^j;
			byte[] data = s.getBytes();
			int length = data.length;
			long h = (seed & 0xffffffffl) ^ (length * m);

			int length8 = length / 8;

			for (int i = 0; i < length8; i++) {
				final int i8 = i * 8 ;
				long k = ((long) data[i8 + 0] & 0xff) + (((long) data[i8 + 1] & 0xff) << 8)
						+ (((long) data[i8 + 2] & 0xff) << 16) + (((long) data[i8 + 3] & 0xff) << 24)
						+ (((long) data[i8 + 4] & 0xff) << 32) + (((long) data[i8 + 5] & 0xff) << 40)
						+ (((long) data[i8 + 6] & 0xff) << 48) + (((long) data[i8 + 7] & 0xff) << 56);

				k *= m;
				k ^= k >>> r;
				k *= m;

				h ^= k;
				h *= m;
			}

			switch (length % 8) {
			case 7:
				h ^= (long) (data[(length & ~7) + 6] & 0xff) << 48;
			case 6:
				h ^= (long) (data[(length & ~7) + 5] & 0xff) << 40;
			case 5:
				h ^= (long) (data[(length & ~7) + 4] & 0xff) << 32;
			case 4:
				h ^= (long) (data[(length & ~7) + 3] & 0xff) << 24;
			case 3:
				h ^= (long) (data[(length & ~7) + 2] & 0xff) << 16;
			case 2:
				h ^= (long) (data[(length & ~7) + 1] & 0xff) << 8;
			case 1:
				h ^= (long) (data[length & ~7] & 0xff);
				h *= m;
			}
			;

			h ^= h >>> r;
			h *= m;
			h ^= h >>> r;
			
			hashFnVals[j] = (int) h % numHashes;
		}
		return hashFnVals;
	}

}
