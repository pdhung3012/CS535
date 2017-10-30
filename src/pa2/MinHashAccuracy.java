package pa2;

import java.io.File;

import util.FileIO;

public class MinHashAccuracy {

	private String fpResultExactJaccard;
	
	
	
	
	public String getFpResultExactJaccard() {
		return fpResultExactJaccard;
	}

	public void setFpResultExactJaccard(String fpResultExactJaccard) {
		this.fpResultExactJaccard = fpResultExactJaccard;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderPath = "data" + File.separator
				+ "pa2" + File.separator + "space" + File.separator;
		String fpResultExactJaccard="data" + File.separator
				+ "pa2" + File.separator + "results" + File.separator+"comparePairToPair.txt";		
		int numPermutations=400;
		double errorParam=0.04;
		
		if(args.length>=4){
			folderPath=args[0];
			fpResultExactJaccard=args[1];
			numPermutations=Integer.parseInt(args[2]);
			errorParam=Double.parseDouble(args[3]);
		}
		
		MinHashAccuracy mha=new MinHashAccuracy();
		mha.setFpResultExactJaccard(fpResultExactJaccard);
		mha.accuracy(folderPath, numPermutations, errorParam);
		
	}
	
	public double accuracy(String folder, int numPermutations,double errorParameter){
		double result=0;
		
		//File folder = new File(folderPath);
		System.out.println(folder);
		StringBuilder strPairToPairCompare=new StringBuilder();
		MinHash mh=new MinHash(folder, numPermutations);
		System.out.println("Finish constructor");
		String[] docs=mh.getAllDocs();
		int numExceed=0,numCompare=0;
		for(int i=0;i<docs.length-1;i++){
			for(int j=i+1;j<docs.length;j++){
				double exactJaccard=mh.extractJaccard(docs[i],docs[j]);
				double approxJaccard=mh.approximateJaccard(docs[i],docs[j]);
				double offset=Math.abs(exactJaccard-approxJaccard);
				numCompare++;
				boolean isExceed=false;
				if(offset>errorParameter){
					numExceed++;
					isExceed=true;
				}
				strPairToPairCompare.append(docs[i]+"\t"+docs[j]+"\t"+exactJaccard+"\t"+approxJaccard+"\t"+isExceed+"\n");
				
			}
			System.out.println(docs[i]+"\t"+i);
			System.out.println("Percentage exceed: "+numExceed+"/"+numCompare+"="+(numExceed*1.0/numCompare));
		}
		strPairToPairCompare.append("Percentage exceed: "+numExceed+"/"+numCompare+"="+(numExceed*1.0/numCompare)+"\n");
		
		FileIO.writeStringToFile(strPairToPairCompare.toString(), fpResultExactJaccard);
		return result;
	}

}
