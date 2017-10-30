package pa2;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import util.FileIO;

public class MinHash {

	private String folder;
	private int numPermutations;
	private String[] allDocs;
	// private int[][] arrBinaryMatrix;
	private int[][] arrHashSig;

	private Hashtable<String, BitSet> tableWordForFile;
	private Hashtable<String, HashSet<Integer>> tableIndexForFile;
	private Hashtable<String, Integer> tableVocabIndex;
	HashSet<String> setVocabularies;
	File[] arrFiles;
	int[] a, b, c;

	public int[][] getArrHashSig() {
		return arrHashSig;
	}

	public void setArrHashSig(int[][] arrHashSig) {
		this.arrHashSig = arrHashSig;
	}

	public String printHashPermutation() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numPermutations; i++) {
			// System.out.println("Hash "+(i+1)+": "+a[i]+"\t"+b[i]+"\t"+c[i]);
			sb.append("Hash " + (i + 1) + ": " + a[i] + "\t" + b[i] + "\t"
					+ c[i] + "\n");
		}
		return sb.toString();
	}

	public MinHash(String folder, int numPermutations) {
		this.folder = folder;
		this.numPermutations = numPermutations;

		File fileFolderArticle = new File(folder);
		arrFiles = fileFolderArticle.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.isFile() && pathname.getName().startsWith(".")) {
					return false;
				}
				return true;
			}
		});
		allDocs = new String[arrFiles.length];
		setVocabularies = new HashSet<String>();

		for (int i = 0; i < arrFiles.length; i++) {
			// int index=getIndexOfDoc(arrFiles[i].getName());
			allDocs[i] = arrFiles[i].getName();
			ArrayList<String> lstWords = removeAllStopWords(folder + allDocs[i]);
			for (String strItem : lstWords) {
				setVocabularies.add(strItem);
			}
			// System.out.println(i+" "+arrFiles.length+" vocab size "+setVocabularies.size());
		}
		tableVocabIndex = new Hashtable<>();
		int indexVocab = 0;
		for (String str : setVocabularies) {
			tableVocabIndex.put(str, indexVocab);
			indexVocab++;
		}

		System.out.println("vocab of " + allDocs.length + " doc is "
				+ setVocabularies.size());

		a = new int[numPermutations];
		b = new int[numPermutations];
		for (int i = 1; i <= numPermutations; i++) {
			a[i - 1] =new Random().nextInt(setVocabularies.size()+1);
			b[i - 1] = new Random().nextInt(setVocabularies.size()+1);
		}
		// a=getListFirstPrime();
		// b=getListFirstPrime();
		c = getListFirstPrime();

		System.out
				.println("End generate " + numPermutations + " hash function");

		tableWordForFile = new Hashtable<String, BitSet>();
		tableIndexForFile = new Hashtable<String, HashSet<Integer>>();

		// arrBinaryMatrix=new int[allDocs.length][setVocabularies.size()];
		for (int i = 0; i < allDocs.length; i++) {
			ArrayList<String> lstWordFileI = removeAllStopWords(folder
					+ allDocs[i]);
			int[] vectorFileI = getVectorFromVocabAndFile(setVocabularies,
					lstWordFileI);
			BitSet bi = new BitSet(setVocabularies.size());
			HashSet<Integer> setIndex = new HashSet<Integer>();
			// System.out.println(vectorFileI.length+" length");
			for (int k = 0; k < vectorFileI.length; k++) {
				//System.out.println(vectorFileI[k]+"\t"+k);
				if (vectorFileI[k] == 1) {
					bi.set(k, true);
					setIndex.add(k);
				}
			}
			tableWordForFile.put(allDocs[i], bi);
			tableIndexForFile.put(allDocs[i], setIndex);
		}
		System.out.println("end doc ");
		arrHashSig = new int[numPermutations][allDocs.length];
		for (int i = 0; i < allDocs.length; i++) {

			int[] arrResult = minHashSig(allDocs[i]);
			// int[] arrResult=new int[numPermutations];
			// arrHashSig[i]=arrResult;
			for (int j = 0; j < arrResult.length; j++) {
				arrHashSig[j][i] = arrResult[j];
				// System.out.println(arrHashSig[j][i]+" bit");
				// arrHashSig[j][i]=0;
			}
			System.out.println("end hash doc " + i);

		}
		System.out.println("end hash doc ");

	}

	public long calculateTimeForMinHash() {
		long startTime, endTime;
		startTime = System.currentTimeMillis();
		arrHashSig = new int[numPermutations][allDocs.length];
		for (int i = 0; i < allDocs.length; i++) {

			int[] arrResult = minHashSig(allDocs[i]);
			for (int j = 0; j < arrResult.length; j++) {
				arrHashSig[j][i] = arrResult[j];
				// System.out.println(j+"\t"+i);
			}

		}
		endTime = System.currentTimeMillis();
		return (endTime - startTime);
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

	private int[] getListFirstPrime() {
		int[] arrResult = new int[numPermutations];
		int indexCountPrime = 0;
		int nextCandidate = (setVocabularies.size() + 1);
		arrResult[0] = 1;
		ArrayList<Integer> lstPrimes = new ArrayList<Integer>();
		// while(indexCountPrime<numPermutations ){
		// int countMod=0;
		// nextCandidate--;
		// for(int i=1;i<=nextCandidate;i++){
		// if(nextCandidate%i==0){
		// countMod++;
		// }
		// }
		// if(countMod==2){
		// arrResult[indexCountPrime]=nextCandidate;
		// indexCountPrime++;
		// }
		// }
		int fixPrime = 1;
		for (int i = setVocabularies.size(); i < setVocabularies.size() * 2; i++) {
			if (isPrime(i)) {
				fixPrime = i;
				// lstPrimes.add(i);
				break;
			}
		}
		for (int i = 0; i < numPermutations; i++) {
			arrResult[i] = fixPrime;
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

	private int getIndexOfDoc(String fileName) {
		int result = -1;
		// result=Integer.parseInt(fileName.replaceAll(".txt","").replaceAll("space-",""));
		for (int i = 0; i < allDocs.length; i++) {
			if (fileName.equals(allDocs[i])) {
				result = i;
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
			String[] arrWords = arrItems[i].trim().replaceAll("\\.", " ").replaceAll("\\,", " ").replaceAll("\\;", " ").split("\\s+");
			for (int j = 0; j < arrWords.length; j++) {
				String word = arrWords[j].trim().toLowerCase();

				if (!(word.length() < 3 || word.equals("the"))) {
					lstWords.add(word);

				}
			}
		}
		return lstWords;
	}

	// private HashSet<String> getVocabulary(ArrayList<String> lstFile1,
	// ArrayList<String> lstFile2) {
	// HashSet<String> setResult = new HashSet<String>();
	// for (String strItem : lstFile1) {
	// setResult.add(strItem);
	// }
	// for (String strItem : lstFile2) {
	// setResult.add(strItem);
	// }
	// return setResult;
	// }

	private int[] getVectorFromVocabAndFile(HashSet<String> setVocab,
			ArrayList<String> lstFile) {
		int[] arrResult = null;
		arrResult = new int[setVocab.size()];
		// HashSet<String> setWordInFile = new HashSet<String>();
		for (int i = 0; i < lstFile.size(); i++) {
			String item = lstFile.get(i);
			Integer index = tableVocabIndex.get(item);
			arrResult[index] = 1;
			// if(!setWordInFile.contains(item)){
			//
			// setWordInFile.add(item);
			// }

		}
		// int index = 0;
		// for (String strItemVocab : setVocab) {
		// if (setWordInFile.contains(strItemVocab)) {
		// arrResult[index] = 1;
		// // System.out.println(index+" equal one");
		// } else {
		// arrResult[index] = 0;
		// // System.out.println(index+" equal zero");
		// }
		// index++;
		// }

		return arrResult;
	}

	private int[] bits2Ints(BitSet bs) {
		int[] arrResult = new int[setVocabularies.size()];
		for (int i = 0; i < arrResult.length; i++) {
			arrResult[i] = 0;
			if (bs.get(i)) {
				arrResult[i] = 1;
			}
		}

		return arrResult;
	}

	public double extractJaccard(String file1, String file2) {
		double result = 0;
		// ArrayList<String> lstWordFile1 = removeAllStopWords(folder + file1);
		// ArrayList<String> lstWordFile2 = removeAllStopWords(folder + file2);
		// setVocabularies = getVocabulary(lstWordFile1,
		// lstWordFile2);
		// int[] vectorFile1 = getVectorFromVocabAndFile(setVocabularies,
		// lstWordFile1);
		// int[] vectorFile2 = getVectorFromVocabAndFile(setVocabularies,
		// lstWordFile2);

		// int index1=getIndexOfDoc(file1);
		// int index2=getIndexOfDoc(file2);

		// int[] vectorFile1 = new int[setVocabularies.size()];
		// int[] vectorFile2 = new int[setVocabularies.size()];
		//
		// for(int i=0;i<setVocabularies.size();i++){
		// vectorFile1[i]=arrBinaryMatrix[i][index1];
		// vectorFile2[i]=arrBinaryMatrix[i][index2];
		// }
		int[] vectorFile1 = bits2Ints(tableWordForFile.get(file1));
		int[] vectorFile2 = bits2Ints(tableWordForFile.get(file2));
		double dotProductAB = 0, lASquare = 0, lBSquare = 0;
		for (int i = 0; i < vectorFile1.length; i++) {
			dotProductAB += vectorFile1[i] * vectorFile2[i];
			lASquare += vectorFile1[i] * vectorFile1[i];
			lBSquare += vectorFile2[i] * vectorFile2[i];
		}

		result = dotProductAB / (lASquare+lBSquare-dotProductAB);
//
		return result;
	}

	// try to generate random permutation
	private int getHash(int index, int hashNumber) {
		return ((a[hashNumber - 1] * index + b[hashNumber - 1]) % c[hashNumber - 1]);
		// return ThreadLocalRandom.current().nextInt(1,
		// setVocabularies.size()+1);

	}

	private int[] getRandomPermutation(ArrayList<Integer> lstInput) {

		java.util.Collections.shuffle(lstInput);
		int[] arrPermutation = new int[lstInput.size()];
		for (int i = 0; i < lstInput.size(); i++) {
			// int index= ThreadLocalRandom.current().nextInt(0,
			// lstInput.size());
			arrPermutation[i] = lstInput.get(i);
			// lstInput.remove(index);
		}
		return arrPermutation;
	}

	public int[] minHashSig(String fileName) {
		int[] arrResult = new int[numPermutations];
		int minValue = 10000000;
		HashSet<Integer> setIndex = tableIndexForFile.get(fileName);
		// HashSet<Integer> setInput=new HashSet<Integer>();

//		ArrayList<Integer> lstIndex = new ArrayList<Integer>();
//		for (Integer i : setIndex) {
//			lstIndex.add(i);
//		}
//		System.out.println(setVocabularies.toString());
//		System.out.println(setIndex.size()+" size of "+fileName+"\t"+setIndex.toString());
//		Scanner sc=new Scanner(System.in);
//		sc.next();
		for (int indexHash = 1; indexHash <= numPermutations; indexHash++) {
			minValue = 10000000;
						
			for (Integer i : setIndex) {
				int value = 0;
				value=getHash(i + 1, indexHash)+1;
				//System.out.print(value+" index "+i+" ");
				// System.out.println(value+" hash");
				if (value < minValue) {
					minValue = value;
				}

			}
//			System.out.println("\n\tMin "+minValue);
//			System.out.println("Doc "+fileName+"\t Hash "+indexHash);
			arrResult[indexHash - 1] = minValue;
//			sc.next();
			// System.out.println(fileName+"\t"+lstIndex.size()+"\n"+lstIndex.toString());
		}

		// System.out.println(fileName+"\t"+setIndex.size()+"\n"+setInput.size());
		return arrResult;
	}

	public double approximateJaccard(String file1, String file2) {
		double result = 0;
		int index1 = getIndexOfDoc(file1);
		int index2 = getIndexOfDoc(file2);
		int[] mh1 = new int[numPermutations];
		int[] mh2 = new int[numPermutations];

		for (int i = 0; i < mh1.length; i++) {
			mh1[i] = arrHashSig[i][index1];
			// System.out.println(i+"\t"+index1+"\t"+index2);
			mh2[i] = arrHashSig[i][index2];
			if (mh1[i] == mh2[i]) {
				result++;
			}
		}
		result = (result * 1.0) / numPermutations;
		return result;
	}

	public int[][] minHashMatrix() {
		int[][] arrResult = new int[numPermutations][allDocs.length];
		setVocabularies = new HashSet<>();
		for (int i = 0; i < allDocs.length; i++) {
			ArrayList<String> lstWordFileI = removeAllStopWords(folder
					+ allDocs[i]);
			for (String str : lstWordFileI) {
				setVocabularies.add(str);
			}
		}

		for (int i = 0; i < allDocs.length; i++) {
			int[] mh1 = minHashSig(allDocs[i]);
			for (int j = 0; j < mh1.length; j++) {
				arrResult[j][i] = mh1[j];
			}
		}
		return arrResult;
	}

	public int numTerms() {
		return setVocabularies.size();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderPath = "data" + File.separator + "pa2" + File.separator
				+ "space" + File.separator;
		String fpResultExactJaccard = "data" + File.separator + "pa2"
				+ File.separator + "results" + File.separator
				+ "testExactJaccard.txt";
		String fpResultApproxsJaccard = "data" + File.separator + "pa2"
				+ File.separator + "results" + File.separator
				+ "testApproxJaccard.txt";
		String fpHashPermutation = "data" + File.separator + "pa2"
				+ File.separator + "results" + File.separator
				+ "hashPermutation.txt";

		if (args.length >= 3) {
			folderPath = args[0];
			fpResultExactJaccard = args[1];
			fpResultApproxsJaccard = args[2];
		}

		File folder = new File(folderPath);
		int numOfPermutations = 400;
		System.out.println(folderPath);
		File[] arrFiles = folder.listFiles();
		String strResultExact = "", strResultApprox = "";

		MinHash mh = new MinHash(folderPath, numOfPermutations);
		String fileName1 = "space-994.txt";
		String fileName2 = "space-995.txt";
		double resultExact = mh.extractJaccard(fileName1, fileName2);
		double resultApprox = mh.approximateJaccard(fileName1, fileName2);
		System.out.println(fileName1 + "\t" + fileName2 + "\t" + resultApprox
				+ "\t" + resultExact);
		FileIO.writeStringToFile(mh.printHashPermutation(), fpHashPermutation);
		// for(int i=0;i<arrFiles.length-1;i++){
		// String fileName1 = arrFiles[i].getName();
		// String fileName2 = arrFiles[i+1].getName();
		//
		// //System.out.println(fileName1 + " " + fileName2);
		//
		// double resultExact = mh.extractJaccard(fileName1, fileName2);
		// double resultApprox = mh.approximateJaccard(fileName1, fileName2);
		// if(resultExact>0){
		// System.out.println("Exact\t"+fileName1 + "\t" + fileName2 + "\t" +
		// resultExact);
		// strResultExact+=fileName1 + "\t" + fileName2 + "\t" +
		// resultExact+"\n";
		// }
		// if(resultApprox>0){
		// System.out.println("Approx\t"+fileName1 + "\t" + fileName2 + "\t" +
		// resultApprox);
		// strResultApprox+=fileName1 + "\t" + fileName2 + "\t" +
		// resultApprox+"\n";
		// //break;
		// }
		//
		// }
		FileIO.writeStringToFile(strResultExact, fpResultExactJaccard);
		FileIO.writeStringToFile(strResultApprox, fpResultApproxsJaccard);

	}

}
