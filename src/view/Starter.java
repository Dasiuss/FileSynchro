package view;

import java.io.FileNotFoundException;

import model.*;

public class Starter {

	public static void main(String[] args) {

		String sourcePath = "D:/Desktop/";
		String destPath = "D:/Desktop6/";

		try {
			Comparator cp = new Comparator(sourcePath,destPath);
			cp.start();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
}