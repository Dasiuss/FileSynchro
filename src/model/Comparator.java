package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.google.common.io.Files;

public class Comparator extends Thread{

	private String sourcePath;
	private String destPath;
	private FilesInputStream files;
	private PrintStream logger;
	private int scanned = 0;
	private int copied = 0;
	private List<String> errorsList = new LinkedList<String>();
	private int copiedBack;

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

	@Override
	public void run(){
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
					destFile.getParentFile().mkdirs();//make folders
					Files.copy(sourceFile, destFile);
					destFile.setLastModified(sourceFile.lastModified());
					logger.println("copied");
				} catch (IOException e) {
					errorsList.add(e.getMessage());
					logger.println("error");
					continue;
				}
			}else if(isOlder(sourceFile , destFile)){
				copiedBack++;
				try {
					Files.copy(destFile, sourceFile);
					sourceFile.setLastModified(destFile.lastModified());
					logger.println("copied back");
				} catch (IOException e) {
					errorsList.add(e.getMessage());
					logger.println("error");
					continue;
				}
			}
		}
		logger.println("E R R O R S :");
		for (String error : errorsList){
			logger.println(error);
		}
		logger.println("scanned: " + scanned);
		logger.println("copied: " + copied);
		logger.println("copied back: " + copiedBack);
		logger.println("errors: " + errorsList.size());
		logger.println("no more files found");
		long time = System.currentTimeMillis() - startTime;
		logger.println("That took "+ time/60000 +" min (" + time + "ms)");

	}



	private boolean isOlder(File file, File than) {
		return file.lastModified() < than.lastModified();
	}
}
