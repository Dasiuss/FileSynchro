package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import com.google.common.io.Files;

public class Comparator {

	private String sourcePath;
	private String destPath;
	private FilesInputStream files;
	private PrintStream logger;
	private int scanned = 0;
	private int copied = 0;

	public Comparator(String source, String destination) throws FileNotFoundException{
		this.sourcePath = source;
		this.destPath = destination;

		this.files = new FilesInputStream(source);
		logger = System.out;
	}

	public Comparator(String source, String destination, PrintStream logger) throws FileNotFoundException{
		this.sourcePath = source;
		this.destPath = destination;
		this.logger = logger;
		this.files = new FilesInputStream(source);
	}


	public void start(){
		long startTime = System.currentTimeMillis();
		
		File sourceFile, destFile;
		int pathLen = sourcePath.length();
		String relPath;
		while ((sourceFile = files.read())!=null){
			scanned++;
			logger.println("Processing: "+sourceFile.getAbsolutePath());
			relPath = sourceFile.getAbsolutePath().substring(pathLen);
			destFile = new File(destPath + relPath);
			if (!destFile.exists() || isOlder(destFile, sourceFile)){
				copied++;
				try {
					destFile.getParentFile().mkdirs();
					Files.copy(sourceFile, destFile);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		logger.println("scanned: " + scanned);
		logger.println("copied: " + copied);
		logger.println("no more files found");
		long time = System.currentTimeMillis() - startTime;
		logger.println("That took "+ time/60000 +" min (" + time + "ms)");

	}



	private boolean isOlder(File file, File than) {
		return file.lastModified() < than.lastModified();
	}
}
