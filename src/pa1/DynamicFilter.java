package pa1;

import java.util.ArrayList;

/**
 * 
 * @author Shruti
 *
 */

public class DynamicFilter extends BloomFilterRan {
	
	ArrayList<byte[]> dynamicFilters = new ArrayList<byte[]>();
	
	public DynamicFilter(int bitsPerElement) {
		super(bitsPerElement);
		
		this.setSize = 1000;
		this.filterSize = setSize * bitsPerElement;
		this.numHashes = (int) (Math.log(2) * bitsPerElement);
		this.filter = new byte[filterSize];
		dynamicFilters.add(this.filter);
	}

	

	@Override
	public void add(String s) {
		if(dataSize > setSize) {
			this.filterSize *= 2;
			filter = new byte[filterSize];
			dynamicFilters.add(filter);
		}
		int[] hashValues = hashFunction(s);
		s = s.toLowerCase();
		for(int k = 0; k < numHashes; k++) {
			 filter[hashValues[k]] = 1;
		}
		dataSize++;
	}
	
	
	@Override
	public boolean appears(String s) {
		int numFilters = dynamicFilters.size();
		int[] hashRes = hashFunction(s);
		for(int i = 0; i < numFilters; i++) {
			int count = 0;
			byte[] filterAtI = dynamicFilters.get(i);
			for(int j = 0; j < hashRes.length; j++) {
				if(filterAtI[hashRes[j]] == 0)
					return false;
			}
		}
		return false;
	}
}
