package pa2;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;

import util.FileIO;

public class MinHash {

	private String folder;
	private int numPermutations;
	private String[] allDocs;
	HashSet<String> setVocabularies;
	File[] arrFiles;
	int []a,b,c;

	public MinHash(String folder, int numPermutations) {
		this.folder = folder;
		this.numPermutations = numPermutations;
		File fileFolderArticle = new File(folder);
		arrFiles = fileFolderArticle.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.isFile()) {
					return true;
				}
				return false;
			}
		});
		allDocs = new String[arrFiles.length];
		for (int i = 0; i < arrFiles.length; i++) {
			allDocs[i] = arrFiles[i].getName();
		}
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

	public int getNumPermutations() {
		return numPermutations;
	}

	public void setNumPermutations(int numPermutations) {
		this.numPermutations = numPermutations;
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

	private HashSet<String> getVocabulary(ArrayList<String> lstFile1,
			ArrayList<String> lstFile2) {
		HashSet<String> setResult = new HashSet<String>();
		for (String strItem : lstFile1) {
			setResult.add(strItem);
		}
		for (String strItem : lstFile2) {
			setResult.add(strItem);
		}
		return setResult;
	}

	private int[] getVectorFromVocabAndFile(HashSet<String> setVocab,
			ArrayList<String> lstFile) {
		int[] arrResult = null;
		arrResult = new int[setVocab.size()];
		HashSet<String> setWordInFile = new HashSet<String>();
		for (int i = 0; i < lstFile.size(); i++) {
			setWordInFile.add(lstFile.get(i));
		}
		int index = 0;
		for (String strItemVocab : setVocab) {
			if (setWordInFile.contains(strItemVocab)) {
				arrResult[index] = 1;
			//	System.out.println(index+" equal one");
			} else {
				arrResult[index] = 0;
			//	System.out.println(index+" equal zero");
			}
			index++;
		}

		return arrResult;
	}

	public double extractJaccard(String file1, String file2) {
		double result = 0;
		ArrayList<String> lstWordFile1 = removeAllStopWords(folder + file1);
		ArrayList<String> lstWordFile2 = removeAllStopWords(folder + file2);
		setVocabularies = getVocabulary(lstWordFile1,
				lstWordFile2);
		int[] vectorFile1 = getVectorFromVocabAndFile(setVocabularies,
				lstWordFile1);
		int[] vectorFile2 = getVectorFromVocabAndFile(setVocabularies,
				lstWordFile2);
	//	System.out.println("file length "+vectorFile1.length+" "+vectorFile2.length);
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
		ArrayList<String> lstWordFile1 = removeAllStopWords(folder + fileName);		
		int[] vectorFile1 = getVectorFromVocabAndFile(setVocabularies,
				lstWordFile1);
		int[] arrResult=new int[numPermutations];
		for(int indexHash=1;indexHash<=numPermutations;indexHash++){
			int minValue=10000000;			
			for(int i=0;i<vectorFile1.length;i++){
				if(vectorFile1[i]==1){
					int value=getHash(i+1, indexHash);
					if(value<minValue){
						minValue=value;
					}
				}
			}
			arrResult[indexHash-1]=minValue;
		}
		
		return arrResult;
	}

	public double approximateJaccard(String file1, String file2) {
		double result = 0;		
		a=new int[numPermutations];
		b=new int[numPermutations];
		for(int i=1;i<=numPermutations;i++){
			a[i-1]=i;
			b[i-1]=i+1;
		}
		c=getListFirstPrime();
		ArrayList<String> lstWordFile1 = removeAllStopWords(folder + file1);
		ArrayList<String> lstWordFile2 = removeAllStopWords(folder + file2);
		setVocabularies = getVocabulary(lstWordFile1,
				lstWordFile2);
		int[] mh1 = minHashSig(file1);
		int[] mh2 = minHashSig(file2);
		
		for(int i=0;i<mh1.length;i++){
			if(mh1[i]==mh2[i]){
				result++;
			}
		}
		result=(result*1.0)/numPermutations;
		return result;
	}

	public int[][] minHashMatrix() {
		return null;
	}

	public int numTerms() {
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderPath = "data" + File.separator
				+ "pa2" + File.separator + "articles" + File.separator;
		File folder = new File(folderPath);
		System.out.println(folderPath);
		File[] arrFiles = folder.listFiles();
		String strResultExact="",strResultApprox="";
		String fpResultExactJaccard="data" + File.separator
				+ "pa2" + File.separator + "results" + File.separator+"testExactJaccard.txt";
		String fpResultApproxsJaccard="data" + File.separator
				+ "pa2" + File.separator + "results" + File.separator+"testApproxJaccard.txt";
		for(int i=0;i<arrFiles.length-1;i++){
			String fileName1 = arrFiles[i].getName();
			String fileName2 = arrFiles[i+1].getName();
			int numOfPermutations = 400;
			//System.out.println(fileName1 + " " + fileName2);
			MinHash mh = new MinHash(folderPath, numOfPermutations);
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
