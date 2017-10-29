package pa2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import util.FileIO;

public class LSH {

	private int[][] minHashMatrix;
	private String[] docNames;
	private int bands;
	private MinHash mHash;
	

	public LSH(int[][] minHashMatrix, String[] docNames, int bands){
		this.minHashMatrix=minHashMatrix;
		this.docNames=docNames;
		this.bands=bands;		
	}
	
	public MinHash getmHash() {
		return mHash;
	}

	public void setmHash(MinHash mHash) {
		this.mHash = mHash;
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
		ArrayList<Hashtable<String,ArrayList<String>>> lstHashT=new ArrayList<Hashtable<String,ArrayList<String>>>();
		for(int i=0;i<bands;i++){
			Hashtable<String,ArrayList<String>> hti=new Hashtable<String, ArrayList<String>>();
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
				Hashtable<String,ArrayList<String>> hti=lstHashT.get(i);
				ArrayList<String> lstIndex=hti.get(String.valueOf(hashValue));
				if(lstIndex==null){
					lstIndex=new ArrayList<String>();
					hti.put(String.valueOf(hashValue), lstIndex);
				}
				lstIndex.add(docNames[indexDoc]);	
				//
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
				//System.out.println("aa "+mhil[indexMHIL]);
			}
			int hashValue=computeHash(mhil, T);
			//System.out.println("Hash value "+hashValue);
			Hashtable<String,ArrayList<String>> hti=lstHashT.get(i);
			//System.out.println("size band "+i+": "+hti.size()+" "+hashValue+", table: "+hti.toString());
			String strHashValue=String.valueOf(hashValue);
			
			if(hti.containsKey(strHashValue)){
				//System.out.println("run here");
				setResult.addAll(hti.get(String.valueOf(hashValue)));
			}
		}
		for(String strItem:setResult){
			lstResult.add(strItem);
		}
		
		ArrayList<String> lstSim=new ArrayList<String>();
		ArrayList<Double> lstExactJac=new ArrayList<Double>();
		//filter the array
		for(int i=0;i<lstResult.size();i++){
			if(lstResult.get(i).equals(docName)){
				continue;
			}
			double exactJaccard=getmHash().extractJaccard(docName, lstResult.get(i));
			if(exactJaccard>=simThreshold){
				//find position for sim
				int indexJ=0;
				for(int j=0;j<lstExactJac.size();j++){
					if(lstExactJac.get(j)>exactJaccard){
						indexJ=j;
						continue;
					}
				}
				lstExactJac.add(indexJ, exactJaccard);
				lstSim.add(indexJ,lstResult.get(i));
				
			}
		}
		
		return lstSim;
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
		int[][] minHashMatrix=mh.getArrHashSig();
		String[] arrDocs=mh.getAllDocs();
		LSH lsh=new LSH(minHashMatrix,arrDocs,numberBands);
		ArrayList<String> lstLSH=new ArrayList<String>();
		lsh.setmHash(mh);
		String strTestName="baseball0.txt";
		lstLSH=lsh.nearDuplciateDetector(folderPath, numPermutations, simThreshold,strTestName);
		StringBuilder sbResult=new StringBuilder();
		sbResult.append("Result for "+strTestName+"\n");
		for(int i=0;i<lstLSH.size();i++){
			double exactJaccard=mh.extractJaccard(strTestName, lstLSH.get(i));
			sbResult.append("\t"+lstLSH.get(i)+"\t"+exactJaccard+"\n");			
		}
		String strResult=sbResult.toString();
		FileIO.writeStringToFile(strResult, fpResultDup);
		System.out.println(strResult);
		//System.out.println("Dup of baseball0.txt: "+lstLSH.toString());
		//mha.accuracy(folderPath, numPermutations, errorParam);
		//Dup of baseball0.txt: [hockey789.txt.copy6, space-337.txt.copy4, hockey789.txt.copy3,
		

	}

}
