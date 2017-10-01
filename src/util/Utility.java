package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Utility {

	public static void main(String[] args) throws IOException {
		BufferedReader bfr = new BufferedReader(new FileReader("data/pa1/query2.txt"));
		BufferedWriter bfw = new BufferedWriter(new FileWriter("data/pa1/words2.txt", true));
		String s ="";
		int count=0;
		while(count != 379){
			s=bfr.readLine();
			bfw.write(s);
			bfw.write("\n");
			count++;
//			String s1[] = s.split("   ");
//			bfw.write(s1[0]);
//			bfw.write("\n");
//			bfw.write(s1[1]);
//			bfw.write("\n");
		}
		System.out.println("Done");
		
	}

}
