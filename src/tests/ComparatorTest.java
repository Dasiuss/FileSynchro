package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import model.Comparator;

import org.junit.Test;

public class ComparatorTest {
	Comparator cp;
	private PrintStream fakePrintStream;

	@Test
	public void testStart() {//time test!
		int howManyTimes = 20;
		fakePrintStream = new PrintStream(System.out){
			public void println(String str){
			}
		};
		
		long startTime;
		long[] times = new long[howManyTimes];
		long avg=0;
		
		try {
			for (int i = 0 ; i < howManyTimes ; i ++){
				startTime = System.currentTimeMillis();
				cp = new Comparator("d:/desktop", "d:/desktop"+i, fakePrintStream);
				cp.start();
				times[i] = System.currentTimeMillis() - startTime;
				System.out.println(times[i]);
				avg+=times[i];
				new File("d:/desktop"+i).deleteOnExit();
			}
			avg /= howManyTimes;
			System.out.println(avg);
			
			
		} catch (FileNotFoundException e) {
			System.out.println("wrong paths");
		};
		
	}

}
