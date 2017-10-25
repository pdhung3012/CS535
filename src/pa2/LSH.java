package pa2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class LSH {

	private int[][] minHashMatrix;
	private String[] docNames;
	private int bands;
	public LSH(int[][] minHashMatrix, String[] docNames, int bands){
		this.minHashMatrix=minHashMatrix;
		this.docNames=docNames;
		this.bands=bands;		
	}
	
	public ArrayList<String> nearDuplicatesOf(String docName){
		ArrayList<String> lst=new ArrayList<String>();
		return lst;
	}
	
	private int getIndexOfDoc(String fileName){
		int result=-1;
		//result=Integer.parseInt(fileName.replaceAll(".txt","").replaceAll("space-",""));
		for(int i=0;i<docNames.length;i++){
			if(fileName.equals(docNames[i])){
				result=i;
				break;
			}
		}
		return result;
	}
	
	private int computeHash(int[] mhil,int T){
		int result=0;
		for(int i=0;i<mhil.length;i++){
			result+=mhil[i]*i;
		}
		result=result%T;
		return result;
	}
	
	public ArrayList<String> nearDuplciateDetector(String folder,int numPermutations,double simThreshold,String docName){
		ArrayList<String> lstResult=new ArrayList<String>();
		int T=3*docName.length();
		int c=T;
		int r=minHashMatrix.length/bands;
		ArrayList<Hashtable<Integer,ArrayList<String>>> lstHashT=new ArrayList<Hashtable<Integer,ArrayList<String>>>();
		for(int i=0;i<bands;i++){
			Hashtable<Integer,ArrayList<String>> hti=new Hashtable<Integer, ArrayList<String>>();
			lstHashT.add(hti);
		}
		int indexDoc=0;
		
		for(indexDoc=0;indexDoc<docNames.length;indexDoc++){
			for(int i=0;i<bands;i++){
				int[] mhil=new int[r];
				for(int j=i*r;j<(i+1)*r;j++){
					int indexMHIL=(j%r);
					mhil[indexMHIL]=minHashMatrix[j][indexDoc];
				}
				int hashValue=computeHash(mhil, T);
				Hashtable<Integer,ArrayList<String>> hti=lstHashT.get(i);
				ArrayList<String> lstIndex=hti.get(new Integer(hashValue));
				if(lstIndex==null){
					lstIndex=new ArrayList<String>();
					hti.put(hashValue, lstIndex);
				}
				lstIndex.add(docNames[indexDoc]);				
			}
		}
		
		//
		indexDoc=getIndexOfDoc(docName);
		System.out.println(docName+"\t"+docNames.length);
		//int countBBucket=0;
		HashSet<String> setResult=new HashSet<String>();
		for(int i=0;i<bands;i++){
			int[] mhil=new int[r];
			for(int j=i*r;j<(i+1)*r;j++){
				int indexMHIL=(j%r);
				mhil[indexMHIL]=minHashMatrix[j][indexDoc];
			}
			int hashValue=computeHash(mhil, T);
			Hashtable<Integer,ArrayList<String>> hti=lstHashT.get(i);
			
			if(hti.contains(hashValue)){
				setResult.addAll(hti.get(hashValue));
			}
		}
		for(String strItem:setResult){
			lstResult.add(strItem);
		}
		
		return lstResult;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderPath = "data" + File.separator
				+ "pa2" + File.separator + "F17PA2" + File.separator;
		String fpResultDup="data" + File.separator
				+ "pa2" + File.separator + "results" + File.separator+"nearDuplicateResults.txt";
		int numPermutations=800;
		double simThreshold=0.5;
		int numberBands=20;
		MinHash mh=new MinHash(folderPath,numPermutations);
		int[][] minHashMatrix=mh.minHashMatrix();
		String[] arrDocs=mh.getAllDocs();
		LSH lsh=new LSH(minHashMatrix,arrDocs,numberBands);
		ArrayList<String> lstLSH=new ArrayList<String>();
		lstLSH=lsh.nearDuplciateDetector(folderPath, numPermutations, simThreshold,"baseball0.txt");
		System.out.println("Dup of baseball0.txt: "+lstLSH.toString());
		//mha.accuracy(folderPath, numPermutations, errorParam);
	}

}
