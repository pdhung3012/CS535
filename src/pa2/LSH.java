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
	private int numberNeedCompare;
	private int numberSSimilarity;
	private int r,indexDoc,c,T;
	ArrayList<Hashtable<String,ArrayList<String>>> lstHashT;

	public LSH(int[][] minHashMatrix, String[] docNames, int bands){
		this.minHashMatrix=minHashMatrix;
		this.docNames=docNames;
		this.bands=bands;		
	}
	
	
	
	public int getNumberNeedCompare() {
		return numberNeedCompare;
	}



	public void setNumberNeedCompare(int numberNeedCompare) {
		this.numberNeedCompare = numberNeedCompare;
	}



	public int getNumberSSimilarity() {
		return numberSSimilarity;
	}



	public void setNumberSSimilarity(int numberSSimilarity) {
		this.numberSSimilarity = numberSSimilarity;
	}



	public MinHash getmHash() {
		return mHash;
	}

	public void setmHash(MinHash mHash) {
		this.mHash = mHash;
	}
	
	public ArrayList<String> nearDuplicatesOf(String docName){
		ArrayList<String> lstResult=new ArrayList<String>();
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
		return lstResult;
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
		T=3*docName.length();
		c=T;
		r=minHashMatrix.length/bands;
		lstHashT=new ArrayList<Hashtable<String,ArrayList<String>>>();
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
		lstResult=nearDuplicatesOf(docName);
		
		ArrayList<String> lstSim=new ArrayList<String>();
		ArrayList<Double> lstApproxJac=new ArrayList<Double>();
		//filter the array
		numberNeedCompare=lstResult.size()-1;
		
		for(int i=0;i<lstResult.size();i++){
			if(lstResult.get(i).equals(docName)){
				continue;
			}
			double approximateJaccard=getmHash().approximateJaccard(docName, lstResult.get(i));
			//double exactJaccard=getmHash().extractJaccard(docName, lstResult.get(i));
			if(approximateJaccard>=simThreshold){
				//find position for sim
				int indexJ=0;
				for(int j=0;j<lstApproxJac.size();j++){
					if(lstApproxJac.get(j)>=approximateJaccard){
						indexJ=j;
						break;
					}
				}
				lstApproxJac.add(indexJ, approximateJaccard);
				lstSim.add(indexJ,lstResult.get(i));
				
			}
		}
		numberSSimilarity=lstSim.size();
		
		return lstSim;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String folderPath = "data" + File.separator
				+ "pa2" + File.separator + "F17PA2" + File.separator;
		String fpResultDup="data" + File.separator
				+ "pa2" + File.separator + "results" + File.separator+"nearDuplicateResults.txt";
		String fpFileQuery="data" + File.separator
				+ "pa2" + File.separator + "results" + File.separator+"listDocForDuplicateDetector.txt";
		
		int numPermutations=800;
		double simThreshold=0.5;
		int numberBands=20;
		
		if(args.length>=6){
			folderPath=args[0];
			fpResultDup=args[1];
			fpFileQuery=args[2];
			numPermutations=Integer.parseInt(args[3]);
			simThreshold=Double.parseDouble(args[4]);
			numberBands=Integer.parseInt(args[5]);
		}
		

		String[] arrQueries=FileIO.readStringFromFile(fpFileQuery).trim().split("\n");
		
		MinHash mh=new MinHash(folderPath,numPermutations);
		int[][] minHashMatrix=mh.getArrHashSig();
		String[] arrDocs=mh.getAllDocs();
		LSH lsh=new LSH(minHashMatrix,arrDocs,numberBands);
		ArrayList<String> lstLSH=new ArrayList<String>();
		lsh.setmHash(mh);
		FileIO.writeStringToFile("", fpResultDup);
		for(int index=0;index<arrQueries.length;index++){
			String strTestName=arrQueries[index].trim();
			lstLSH=lsh.nearDuplciateDetector(folderPath, numPermutations, simThreshold,strTestName);
			StringBuilder sbResult=new StringBuilder();
			sbResult.append((index+1)+") Result for "+strTestName+" ("+lsh.getNumberSSimilarity()+"/"+lsh.getNumberNeedCompare()+"="+(lsh.getNumberSSimilarity()*1.0/lsh.getNumberNeedCompare())+")\n");
			sbResult.append("\tFileName\tapproxJaccard\texactJaccard\n");
			for(int i=0;i<lstLSH.size();i++){
				double approxJaccard=mh.approximateJaccard(strTestName, lstLSH.get(i));
				double exactJaccard=mh.extractJaccard(strTestName, lstLSH.get(i));
				sbResult.append("\t"+lstLSH.get(i)+"\t"+approxJaccard+"\t"+exactJaccard+"\n");			
			}
			String strResult=sbResult.toString();
			FileIO.appendStringToFile(strResult, fpResultDup);
			System.out.println(strResult);
		}
		
		
		//System.out.println("Dup of baseball0.txt: "+lstLSH.toString());
		//mha.accuracy(folderPath, numPermutations, errorParam);
		//Dup of baseball0.txt: [hockey789.txt.copy6, space-337.txt.copy4, hockey789.txt.copy3,
		

	}

}
