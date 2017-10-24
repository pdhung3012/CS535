package pa2;

import java.io.File;

public class MinHashTime {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderPath = "data" + File.separator
				+ "pa2" + File.separator + "space" + File.separator;
		int numPermutations=600;
		MinHashTime mht=new MinHashTime();
		mht.timer(folderPath, numPermutations);
	}
	public void timer(String folder, int numPermutations){
		MinHash mh=new MinHash(folder,numPermutations);
		System.out.println("finish Constructor");
		
		
		String[] docs=mh.getAllDocs();
		long startTime = System.currentTimeMillis();		
		for(int i=0;i<docs.length-1;i++){
			for(int j=i+1;j<docs.length;j++){
				double exactJaccard=mh.extractJaccard(docs[i],docs[j]);
				//double approxJaccard=mh.approximateJaccard(docs[i],docs[j]);
			}
		}		
		long endTime   = System.currentTimeMillis();
		long exactJaccardTime = endTime - startTime;
		
		
		long constructMinHashTime = mh.calculateTimeForMinHash();
		
		startTime = System.currentTimeMillis();
		for(int i=0;i<docs.length-1;i++){
			for(int j=i+1;j<docs.length;j++){
				//double exactJaccard=mh.extractJaccard(docs[i],docs[j]);
				double approxJaccard=mh.approximateJaccard(docs[i],docs[j]);
			}
		}		
		endTime   = System.currentTimeMillis();
		long approxJaccardTime = endTime - startTime;		
		System.out.println("Exact jaccard time: "+exactJaccardTime);
		System.out.println("Construct minhash time: "+constructMinHashTime);
		System.out.println("Approx jaccard time: "+approxJaccardTime);
		System.out.println("Total Approx time: "+(approxJaccardTime+constructMinHashTime));
	}

}
