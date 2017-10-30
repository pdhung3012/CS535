package pa2;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Hashtable;

import util.FileIO;

public class MinHash {

	private String folder;
	private int numPermutations;
	private String[] allDocs;
	//private int[][] arrBinaryMatrix;
	private int[][] arrHashSig;
	

	private Hashtable<String,BitSet> tableWordForFile;
	private Hashtable<String,HashSet<Integer>> tableIndexForFile;
	private Hashtable<String,Integer> tableVocabIndex;
	HashSet<String> setVocabularies;
	File[] arrFiles;
	int []a,b,c;

	public int[][] getArrHashSig() {
		return arrHashSig;
	}

	public void setArrHashSig(int[][] arrHashSig) {
		this.arrHashSig = arrHashSig;
	}
	
	public MinHash(String folder, int numPermutations) {
		this.folder = folder;
		this.numPermutations = numPermutations;
		
		a=new int[numPermutations];
		b=new int[numPermutations];
		for(int i=1;i<=numPermutations;i++){
			a[i-1]=i;
			b[i-1]=i+1;
		}
		c=getListFirstPrime();
		
		
		File fileFolderArticle = new File(folder);
		arrFiles = fileFolderArticle.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.isFile()&&pathname.getName().startsWith(".")) {
					return false;
				}
				return true;
			}
		});
		allDocs = new String[arrFiles.length];
		setVocabularies=new HashSet<String>();
		
		for (int i = 0; i < arrFiles.length; i++) {
			//int index=getIndexOfDoc(arrFiles[i].getName());
			allDocs[i] = arrFiles[i].getName();
			ArrayList<String> lstWords=removeAllStopWords(folder+allDocs[i]);
			for (String strItem : lstWords) {
				setVocabularies.add(strItem);
			}
			//System.out.println(i+" "+arrFiles.length+" vocab size "+setVocabularies.size());
		}
		tableVocabIndex=new Hashtable<>();
		int indexVocab=0;
		for(String str:setVocabularies){
			tableVocabIndex.put(str, indexVocab);
			indexVocab++;
		}
		
		System.out.println("vocab of "+allDocs.length+" doc is "+setVocabularies.size());
		
		tableWordForFile=new Hashtable<String, BitSet>();
		tableIndexForFile=new Hashtable<String, HashSet<Integer>>();
		
//		arrBinaryMatrix=new int[allDocs.length][setVocabularies.size()];
		for (int i = 0; i < allDocs.length; i++) {
			ArrayList<String> lstWordFileI = removeAllStopWords(folder + allDocs[i]);			
			int[] vectorFileI = getVectorFromVocabAndFile(setVocabularies,
					lstWordFileI);
			BitSet bi=new BitSet(setVocabularies.size());
			HashSet<Integer> setIndex=new HashSet<Integer>();
			//System.out.println(vectorFileI.length+" length");
			for(int k=0;k<vectorFileI.length;k++){
				if(vectorFileI[k]==1){
					bi.set(k, true);
					setIndex.add(k);
				}
				
			}
			tableWordForFile.put(allDocs[i], bi);
			tableIndexForFile.put(allDocs[i], setIndex);
			
//			int index=getIndexOfDoc(allDocs[i]);
//			arrBinaryMatrix[index]=vectorFileI;
//			for(int j=0;j<vectorFileI.length;j++){
//				arrBinaryMatrix[j][i]=vectorFileI[j];
//			}
			//System.out.println("end doc "+i);
		}
		System.out.println("end doc ");
		arrHashSig=new int[numPermutations][allDocs.length];
		for (int i = 0; i < allDocs.length; i++) {
			
			int[] arrResult=minHashSig(allDocs[i]);
			//int[] arrResult=new int[numPermutations];
			//arrHashSig[i]=arrResult;
			for(int j=0;j<arrResult.length;j++){
				arrHashSig[j][i]=arrResult[j];
				//arrHashSig[j][i]=0;
			}
			System.out.println("end hash doc "+i);
		
		}
		System.out.println("end hash doc ");
		
	}
	
	public long calculateTimeForMinHash(){
		long startTime,endTime;
		startTime=System.currentTimeMillis();
		arrHashSig=new int[allDocs.length][numPermutations];
		for (int i = 0; i < allDocs.length; i++) {
			
			int[] arrResult=minHashSig(allDocs[i]);
			arrHashSig[i]=arrResult;		
		}
		endTime=System.currentTimeMillis();
		return (endTime-startTime);
	}
	
	private int[] getListFirstPrime(){
		int[] arrResult=new int[numPermutations];
		int indexCountPrime=1;
		int nextCandidate=1;
		arrResult[0]=1;
		while(indexCountPrime<numPermutations ){
			int countMod=0;
			nextCandidate++;
			for(int i=1;i<=nextCandidate;i++){
				if(nextCandidate%i==0){
					countMod++;
				}
			}
			if(countMod==2){
				arrResult[indexCountPrime]=nextCandidate;
				indexCountPrime++;
			}			
		}
		return arrResult;
	}
	
	public String[] getAllDocs() {
		return allDocs;
	}

	public void setAllDocs(String[] allDocs) {
		this.allDocs = allDocs;
	}


	public int getNumPermutations() {
		return numPermutations;
	}

	public void setNumPermutations(int numPermutations) {
		this.numPermutations = numPermutations;
	}

	private int getIndexOfDoc(String fileName){
		int result=-1;
		//result=Integer.parseInt(fileName.replaceAll(".txt","").replaceAll("space-",""));
		for(int i=0;i<allDocs.length;i++){
			if(fileName.equals(allDocs[i])){
				result=i;
				break;
			}
		}
		return result;
	}
	
	private ArrayList<String> removeAllStopWords(String filePath) {
		String content = FileIO.readStringFromFile(filePath);
		
		String[] arrItems = content.split("\n");
		ArrayList<String> lstWords = new ArrayList<String>();
		for (int i = 0; i < arrItems.length; i++) {
			String[] arrWords = arrItems[i].trim().split("\\s+");
			for (int j = 0; j < arrWords.length; j++) {
				String word = arrWords[j].trim().toLowerCase();
				
				if (!(word.length() < 3 || word.equals("the"))) {
					lstWords.add(word);
					
				}
			}
		}
		return lstWords;
	}

//	private HashSet<String> getVocabulary(ArrayList<String> lstFile1,
//			ArrayList<String> lstFile2) {
//		HashSet<String> setResult = new HashSet<String>();
//		for (String strItem : lstFile1) {
//			setResult.add(strItem);
//		}
//		for (String strItem : lstFile2) {
//			setResult.add(strItem);
//		}
//		return setResult;
//	}

	private int[] getVectorFromVocabAndFile(HashSet<String> setVocab,
			ArrayList<String> lstFile) {
		int[] arrResult = null;
		arrResult = new int[setVocab.size()];
		//HashSet<String> setWordInFile = new HashSet<String>();
		for (int i = 0; i < lstFile.size(); i++) {
			String item=lstFile.get(i);
			Integer index=tableVocabIndex.get(item);
			arrResult[index]=1;
//			if(!setWordInFile.contains(item)){
//				
//				setWordInFile.add(item);
//			}
			
			
		}
	//	int index = 0;
//		for (String strItemVocab : setVocab) {
//			if (setWordInFile.contains(strItemVocab)) {
//				arrResult[index] = 1;
//			//	System.out.println(index+" equal one");
//			} else {
//				arrResult[index] = 0;
//			//	System.out.println(index+" equal zero");
//			}
//			index++;
//		}

		return arrResult;
	}
	
	 private int[] bits2Ints(BitSet bs) {
		    int[] arrResult=new int[setVocabularies.size()];
		 	for(int i=0;i<arrResult.length;i++){
		 		arrResult[i]=0;
		 		if(bs.get(i)){
		 			arrResult[i]=1;
		 		}
		 	}

		    return arrResult;
		  }

	public double extractJaccard(String file1, String file2) {
		double result = 0;
//		ArrayList<String> lstWordFile1 = removeAllStopWords(folder + file1);
//		ArrayList<String> lstWordFile2 = removeAllStopWords(folder + file2);
//		setVocabularies = getVocabulary(lstWordFile1,
//				lstWordFile2);
//		int[] vectorFile1 = getVectorFromVocabAndFile(setVocabularies,
//				lstWordFile1);
//		int[] vectorFile2 = getVectorFromVocabAndFile(setVocabularies,
//				lstWordFile2);
		
//		int index1=getIndexOfDoc(file1);
//		int index2=getIndexOfDoc(file2);
		
//		int[] vectorFile1 = new int[setVocabularies.size()];
//		int[] vectorFile2 = new int[setVocabularies.size()];
//		
//		for(int i=0;i<setVocabularies.size();i++){
//			vectorFile1[i]=arrBinaryMatrix[i][index1];
//			vectorFile2[i]=arrBinaryMatrix[i][index2];
//		}
		int[] vectorFile1 =bits2Ints(tableWordForFile.get(file1));
		int[] vectorFile2 =bits2Ints( tableWordForFile.get(file2));
		double dotProductAB = 0, lASquare = 0, lBSquare = 0;
		for (int i = 0; i < vectorFile1.length; i++) {
			dotProductAB += vectorFile1[i] * vectorFile2[i];
			lASquare += vectorFile1[i] * vectorFile1[i];
			lBSquare += vectorFile2[i] * vectorFile2[i];
		}

		result = dotProductAB / (Math.sqrt(lASquare) * Math.sqrt(lBSquare));

		return result;
	}
	
	private int getHash(int index,int hashNumber){
		return ((a[hashNumber-1]*index+b[hashNumber-1])%c[hashNumber-1]);
	}

	public int[] minHashSig(String fileName) {
		//ArrayList<String> lstWordFile1 = removeAllStopWords(folder + fileName);		
		//int indexI=getIndexOfDoc(fileName);
		
		
		//BitSet bitVectorFileI = tableWordForFile.get(fileName);//arrBinaryMatrix[indexI];
		//System.out.println(vectorFileI.length+" aaa");
//		for(int i=0;i<setVocabularies.size();i++){
//			vectorFileI[i]=arrBinaryMatrix[i][indexI];
//		}
		//System.out.println("length vector"+vectorFileI.length);
		int[] arrResult=new int[numPermutations];
		int minValue=10000000;	
		HashSet<Integer> setIndex=tableIndexForFile.get(fileName);
		for(int indexHash=1;indexHash<=numPermutations;indexHash++){
			minValue=10000000;
//			bitVectorFileI.
//			for(int i=0;i<vectorFileI.length;i++){
//				if(vectorFileI[i]==1){
//					int value=0;
//					value=getHash(i+1, indexHash);
//					if(value<minValue){
//						minValue=value;
//					}
//				}
//			}
			
			for(Integer i:setIndex){
				int value=0;
				value=getHash(i+1, indexHash);
				if(value<minValue){
					minValue=value;
				}
				
			}
			arrResult[indexHash-1]=minValue;
		}
		
		return arrResult;
	}

	public double approximateJaccard(String file1, String file2) {
		double result = 0;		
		int index1=getIndexOfDoc(file1);
		int index2=getIndexOfDoc(file2);
		int[] mh1 =new int[numPermutations];
		int[] mh2 =new int[numPermutations];
		
		for(int i=0;i<mh1.length;i++){
			mh1[i]=arrHashSig[i][index1];
			mh2[i]=arrHashSig[i][index2];
			if(mh1[i]==mh2[i]){
				result++;
			}
		}
		result=(result*1.0)/numPermutations;
		return result;
	}

	public int[][] minHashMatrix() {
		int[][] arrResult=new int[numPermutations][allDocs.length];
		setVocabularies=new HashSet<>();
		for(int i=0;i<allDocs.length;i++){
			ArrayList<String> lstWordFileI=removeAllStopWords(folder+allDocs[i]);
			for(String str:lstWordFileI){
				setVocabularies.add(str);
			}
		}
		
		for(int i=0;i<allDocs.length;i++){
			int[] mh1=minHashSig(allDocs[i]);
			for(int j=0;j<mh1.length;j++){
				arrResult[j][i]=mh1[j];
			}
		}
		return arrResult;
	}

	public int numTerms() {
		return setVocabularies.size();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderPath = "data" + File.separator
				+ "pa2" + File.separator + "articles" + File.separator;
		String fpResultExactJaccard="data" + File.separator
				+ "pa2" + File.separator + "results" + File.separator+"testExactJaccard.txt";
		String fpResultApproxsJaccard="data" + File.separator
				+ "pa2" + File.separator + "results" + File.separator+"testApproxJaccard.txt";
		
		if(args.length>=3){
			folderPath=args[0];
			fpResultExactJaccard=args[1];
			fpResultApproxsJaccard=args[2];
		}
		
		File folder = new File(folderPath);
		int numOfPermutations = 400;
		System.out.println(folderPath);
		File[] arrFiles = folder.listFiles();
		String strResultExact="",strResultApprox="";
		
		
		
		
		MinHash mh = new MinHash(folderPath, numOfPermutations);
		for(int i=0;i<arrFiles.length-1;i++){
			String fileName1 = arrFiles[i].getName();
			String fileName2 = arrFiles[i+1].getName();
			
			//System.out.println(fileName1 + " " + fileName2);
			
			double resultExact = mh.extractJaccard(fileName1, fileName2);
			double resultApprox = mh.approximateJaccard(fileName1, fileName2);
			if(resultExact>0){
				System.out.println("Exact\t"+fileName1 + "\t" + fileName2 + "\t" + resultExact);
				strResultExact+=fileName1 + "\t" + fileName2 + "\t" + resultExact+"\n";
			}
			if(resultApprox>0){
				System.out.println("Approx\t"+fileName1 + "\t" + fileName2 + "\t" + resultApprox);
				strResultApprox+=fileName1 + "\t" + fileName2 + "\t" + resultApprox+"\n";
				//break;
			}
			
		}
		FileIO.writeStringToFile(strResultExact,fpResultExactJaccard);
		FileIO.writeStringToFile(strResultApprox,fpResultApproxsJaccard);
		
	}

}
