package pa2;

public class MinHash {

	private String folder;
	private int numPermutations;
	
	
	
	public MinHash(String folder,int numPermutations){
		this.folder=folder;
		this.numPermutations=numPermutations;
	}
	
	
	
	public int getNumPermutations() {
		return numPermutations;
	}



	public void setNumPermutations(int numPermutations) {
		this.numPermutations = numPermutations;
	}



	public String[] allDocs(){
		return null;
	}
	
	public double extractJaccard(String file1,String file2){
		double result=0;
		return result;
	}
	
	public int[] minHashSig(String fileName){
		return null;
	}
	
	public double approximateJaccard(String file1,String file2){
		double result=0;
		return result;
	}
	
	public int[][] minHashMatrix(){
		return null;
	}
	
	public int numTerms(){
		return 0;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
