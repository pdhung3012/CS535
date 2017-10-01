package pa1;

import java.util.ArrayList;

/**
 * 
 * @author Shruti
 *
 */

public class DynamicFilter extends BloomFilterRan {
	
	ArrayList<DynamicFilter> dynamicFilters = new ArrayList<DynamicFilter>();
	
	public DynamicFilter(int bitsPerElement) {
		super(bitsPerElement);
		
		this.setSize = 1000;
		this.filterSize = setSize * bitsPerElement;
		this.numHashes = (int) (Math.log(2) * bitsPerElement);
		this.filter = new byte[filterSize];
	}

	

	@Override
	public void add(String s) {
		if(dataSize > setSize) {
			setSize *= 2;
			
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
		return false;
	}
}
