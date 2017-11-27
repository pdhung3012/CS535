package pa2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;

import util.FileIO;

public class NearDuplicates {

	private LSH lsh;
	//private String[] docNames;
	private int bands;
	private MinHash mHash;
	private int numberNeedCompare;
	private int numberSSimilarity;
	private int r,indexDoc,c,T;
	ArrayList<Hashtable<Integer,ArrayList<String>>> lstHashT;
	
	public NearDuplicates(String folder, int numPermutations,int numberBands){
		this.bands=numberBands;
		mHash=new MinHash(folder,numPermutations);
		int[][] minHashMatrix=mHash.getArrHashSig();
		String[] arrDocs=mHash.getAllDocs();
		lsh=new LSH(minHashMatrix,arrDocs,numberBands);
		lsh.setmHash(mHash);
		
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



	public LSH getLsh() {
		return lsh;
	}



	public void setLsh(LSH lsh) {
		this.lsh = lsh;
	}



	public MinHash getmHash() {
		return mHash;
	}



	public void setmHash(MinHash mHash) {
		this.mHash = mHash;
	}



	boolean isPrime(int n) {
		// check if n is a multiple of 2
		if (n % 2 == 0)
			return false;
		// if not, then just check the odds
		for (int i = 3; i * i <= n; i += 2) {
			if (n % i == 0)
				return false;
		}
		return true;
	}
	
	private int getIndexOfDoc(String fileName){
		int result=-1;
		//result=Integer.parseInt(fileName.replaceAll(".txt","").replaceAll("space-",""));
		for(int i=0;i<mHash.getAllDocs().length;i++){
			if(fileName.equals(mHash.getAllDocs()[i])){
				result=i;
				break;
			}
		}
		return result;
	}
	

	
	
	public ArrayList<String> nearDuplciateDetector(String folder,int numPermutations,double simThreshold,String docName){
		int N=mHash.getAllDocs().length;
		
		T=20000*N;
		while(!isPrime(T)){
			T++;
		}
		
		//System.out.println("T equals "+T);
//		Scanner sc=new Scanner(System.in);
//		sc.next();
		//T=3*docNames.length;
		c=T;
		r=numPermutations/bands;
		lstHashT=new ArrayList<Hashtable<Integer,ArrayList<String>>>();
		for(int i=0;i<bands;i++){
			Hashtable<Integer,ArrayList<String>> hti=new Hashtable<Integer, ArrayList<String>>();
			lstHashT.add(hti);
		}
		
		lsh.setLstHashT(lstHashT);
		int indexDoc=0;
		
		for(indexDoc=0;indexDoc<mHash.getAllDocs().length;indexDoc++){
			for(int i=0;i<bands;i++){
				int[] mhil=new int[r];
				for(int j=i*r;j<(i+1)*r;j++){
					int indexMHIL=(j%r);
					mhil[indexMHIL]=lsh.getMinHashMatrix()[j][indexDoc];
				}
				int hashValue=lsh.computeHash(mhil, T);
				Hashtable<Integer,ArrayList<String>> hti=lstHashT.get(i);
				ArrayList<String> lstIndex=hti.get(hashValue);
				if(lstIndex==null){
					lstIndex=new ArrayList<String>();
					hti.put(hashValue, lstIndex);
				}
				lstIndex.add(mHash.getAllDocs()[indexDoc]);	
				//
			}
		}
		
		//
		indexDoc=getIndexOfDoc(docName);
		System.out.println(docName+"\t"+mHash.getAllDocs().length);
		//int countBBucket=0;
		//lstResult=lsh.nearDuplicatesOf(docName);
		
		ArrayList<String> lstSim=nearDuplicates(simThreshold,docName);
		return lstSim;
	}
	
	public ArrayList<String> nearDuplicates(double simThreshold,String docName){
		ArrayList<String> lstResult=lsh.nearDuplicatesOf(docName);
		ArrayList<String> lstSim=new ArrayList<String>();
		ArrayList<Double> lstApproxJac=new ArrayList<Double>();
		//filter the array
		numberNeedCompare=lstResult.size()-1;
//		System.out.println("Lst result: "+lstResult.size());
//		Scanner sc=new Scanner(System.in);
//		sc.next();
		for(int i=0;i<lstResult.size();i++){
			if(lstResult.get(i).equals(docName)){
				continue;
			}
			double approximateJaccard=lsh.getmHash().approximateJaccard(docName, lstResult.get(i));
			double exactJaccard=lsh.getmHash().extractJaccard(docName, lstResult.get(i));
			if(approximateJaccard>=simThreshold){
				//find position for sim
				int indexJ=0;
				for(int j=0;j<lstApproxJac.size();j++){
					if(lstApproxJac.get(j)<=approximateJaccard){
						indexJ=j;
						break;
					}
					indexJ++;
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
		
		int numPermutations=600;
		double simThreshold=0.8;
		int numberBands=30;
		
		if(args.length>=6){
			folderPath=args[0];
			fpResultDup=args[1];
			fpFileQuery=args[2];
			numPermutations=Integer.parseInt(args[3]);
			simThreshold=Double.parseDouble(args[4]);
			numberBands=Integer.parseInt(args[5]);
		}
		

		String[] arrQueries=FileIO.readStringFromFile(fpFileQuery).trim().split("\n");
		
//		MinHash mh=new MinHash(folderPath,numPermutations);
//		int[][] minHashMatrix=mh.getArrHashSig();
//		String[] arrDocs=mh.getAllDocs();
//		LSH lsh=new LSH(minHashMatrix,arrDocs,numberBands);
//		lsh.setmHash(mh);
		ArrayList<String> lstLSH=new ArrayList<String>();

		NearDuplicates nearDup=new NearDuplicates(folderPath, numPermutations, numberBands);
		
		FileIO.writeStringToFile("", fpResultDup);
		for(int index=0;index<arrQueries.length;index++){
			String strTestName=arrQueries[index].trim();
			lstLSH=nearDup.nearDuplciateDetector(folderPath, numPermutations, simThreshold,strTestName);
			StringBuilder sbResult=new StringBuilder();
			sbResult.append((index+1)+") Result for "+strTestName+" ("+nearDup.getNumberSSimilarity()+"/"+nearDup.getNumberNeedCompare()+"="+(nearDup.getNumberSSimilarity()*1.0/nearDup.getNumberNeedCompare())+")\n");
			sbResult.append("\tFileName\tapproxJaccard\texactJaccard\n");
			for(int i=0;i<lstLSH.size();i++){
				double approxJaccard=nearDup.getLsh().getmHash().approximateJaccard(strTestName, lstLSH.get(i));
				double exactJaccard=nearDup.getLsh().getmHash().extractJaccard(strTestName, lstLSH.get(i));
				sbResult.append("\t"+lstLSH.get(i)+"\t"+approxJaccard+"\t"+exactJaccard+"\n");			
			}
			String strResult=sbResult.toString();
			FileIO.appendStringToFile(strResult, fpResultDup);
			System.out.println(strResult);
		}
	}

}
