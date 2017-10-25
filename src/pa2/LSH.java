package pa2;

import java.util.ArrayList;

public class LSH {

	private int[][] minHashMatrix;
	private String[] docName;
	private int bands;
	public LSH(int[][] minHashMatrix, String[] docNames, int bands){
		this.minHashMatrix=minHashMatrix;
		this.docName=docNames;
		this.bands=bands;		
	}
	
	public ArrayList<String> nearDuplicatesOf(String docName){
		ArrayList<String> lst=new ArrayList<String>();
		return lst;
	}
	
	public void nearDuplciateDetector(String folder,int numPermutations,double simThreshold,String docName){
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
