package util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class FileIO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void writeStringToFile(String string, String outputFile) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(string);
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			/*e.printStackTrace();
			System.exit(0);*/
			System.err.println(e.getMessage());
		}
	}
	
	public static void appendStringToFile(String string, String outputFile) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile,true));
			writer.write(string);
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			/*e.printStackTrace();
			System.exit(0);*/
			System.err.println(e.getMessage());
		}
	}
	
	public static String readStringFromFile(String inputFile) {
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
			byte[] bytes = new byte[(int) new File(inputFile).length()];
			in.read(bytes);
			in.close();
			return new String(bytes);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
