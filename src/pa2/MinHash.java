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
	File[] arrFiles;
	
	
	public MinHash(String folder,int numPermutations){
		this.folder=folder;
		this.numPermutations=numPermutations;
		File fileFolderArticle = new File(folder);
		arrFiles= fileFolderArticle.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if(pathname.isFile()){
					return true;
				}
				return false;
			}
		});
		allDocs=new String[arrFiles.length];
		for(int i=0;i<arrFiles.length;i++){
			allDocs[i]=arrFiles[i].getName();
		}
	}
	
	
	
	public int getNumPermutations() {
		return numPermutations;
	}



	public void setNumPermutations(int numPermutations) {
		this.numPermutations = numPermutations;
	}



	private ArrayList<String> removeAllStopWords(String filePath){
		String content=FileIO.readStringFromFile(filePath);
		String[] arrItems=content.split("\n");
		ArrayList<String> lstWords=new ArrayList<String>();
		for(int i=0;i<arrItems.length;i++){
			String[] arrWords=arrItems[i].trim().split("\\s+");
			for(int j=0;j<arrWords.length;j++){
				String word=arrWords[j].trim().toLowerCase();
				if(!(word.length()<3||word.equals("the"))){
					lstWords.add(word);
					
				}
			}
		}
		return lstWords;
	}
	
	private HashSet<String> getVocabulary(ArrayList<String> lstFile1,ArrayList<String> lstFile2){
		HashSet<String> setResult=new HashSet<String>();
		for(String strItem:lstFile1){
			setResult.add(strItem);
		}
		for(String strItem:lstFile2){
			setResult.add(strItem);
		}
		return setResult;
	}
	
	private int[] getVectorFromVocabAndFile(HashSet<String> setVocab,ArrayList<String> lstFile){
		int[] arrResult=null;
		arrResult=new int[setVocab.size()];
		HashSet<String> setWordInFile=new HashSet<String>();
		for(int i=0;i<lstFile.size();i++){
			setWordInFile.add(lstFile.get(i));
		}
		int index=0;
		for(String strItemVocab:setVocab){
			if(setWordInFile.contains(strItemVocab)){
				arrResult[index]=1;
			}else{
				arrResult[index]=0;
			}
			index++;
		}
		
		return arrResult;
	}
	
	public double extractJaccard(String file1,String file2){
		double result=0;
		ArrayList<String> lstWordFile1=removeAllStopWords(folder+file1);
		ArrayList<String> lstWordFile2=removeAllStopWords(folder+file2);
		HashSet<String> setVocabularies=getVocabulary(lstWordFile1, lstWordFile2);
		int[] vectorFile1=getVectorFromVocabAndFile(setVocabularies, lstWordFile1);
		int[] vectorFile2=getVectorFromVocabAndFile(setVocabularies, lstWordFile2);
		double dotProductAB=0,lASquare=0,lBSquare=0;
		for(int i=0;i<vectorFile1.length;i++){
			dotProductAB=vectorFile1[i]*vectorFile2[i];
			lASquare+=vectorFile1[i]*vectorFile1[i];
			lBSquare+=vectorFile2[i]*vectorFile2[i];
		}
		
		result=dotProductAB/(Math.sqrt(lASquare)*Math.sqrt(lBSquare));
		
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
		String folderPath= File.pathSeparator+"data"+File.pathSeparator+"pa2"+File.pathSeparator+"articles"+File.pathSeparator;
		
	}

}
