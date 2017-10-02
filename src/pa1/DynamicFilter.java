package pa1;

import java.util.ArrayList;

/**
 * 
 * @author Shruti sahu
 *
 */

public class DynamicFilter extends BloomFilterRan {
	
	public ArrayList<byte[]> dynamicFilters = new ArrayList<byte[]>();
	
	public DynamicFilter(int bitsPerElement) {
		super(bitsPerElement);
		
		this.setSize = 1000;
		this.bitsPerElements = bitsPerElement;
		this.filterSize = setSize * bitsPerElement;
		this.numHashes = (int) (Math.log(2) * bitsPerElement);
		this.filter = new byte[filterSize];
		this.dynamicFilters.add(this.filter);
	}

	
	
	@Override
	public void add(String s) {
		//System.out.println("DataSize==="+dataSize+"\t Filter Size==="+filterSize +"\t check=="+((int)dataSize > (int)setSize));
		if(dataSize > setSize) {
			this.setSize*=2;
			this.filterSize = setSize * this.bitsPerElements;
			//System.out.println(filterSize);
			filter = new byte[filterSize];
			this.dynamicFilters.add(filter);
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
		int numFilters = this.dynamicFilters.size();
		int[] hashRes = hashFunction(s);
		int count = 0;
		for(int i = 0; i < numFilters; i++) {
			byte[] filterAtI = dynamicFilters.get(i);
			for(int j = 0; j < hashRes.length; j++) {
				if(hashRes[j] > filterAtI.length)
					break;
				else if(filterAtI[hashRes[j]] == 0)
					break;
				else
					count++;
			}
		}
		if(count == hashRes.length)
			return true;
		else return false;
	}
}
