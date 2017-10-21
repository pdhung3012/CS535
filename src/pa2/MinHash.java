package pa2;

import java.io.File;

public class MinHash {

	private String folder;
	private int numPermutations;
	private String[] allDocs;
	File[] arrFiles;
	
	
	public MinHash(String folder,int numPermutations){
		this.folder=folder;
		this.numPermutations=numPermutations;
		File fileFolderArticle = new File(folder);
		arrFiles= fileFolderArticle.listFiles();
	}
	
	
	
	public int getNumPermutations() {
		return numPermutations;
	}



	public void setNumPermutations(int numPermutations) {
		this.numPermutations = numPermutations;
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
