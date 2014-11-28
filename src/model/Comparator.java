package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.google.common.io.Files;

public class Comparator extends Thread {

	private final String sourcePath;
	private final String destPath;
	private final FilesInputStream files;
	private final PrintStream logger;
	private int scanned = 0;
	private int copied = 0;
	private final List<String> errorsList = new LinkedList<String>();
	private int copiedBack;
	private long startTime;
	private boolean isStopped = false;

	public Comparator(String source, String destination) throws FileNotFoundException {
		this.sourcePath = source;
		this.destPath = destination;

		this.files = new FilesInputStream(source);
		logger = System.out;
	}

	public Comparator(String source, String destination, PrintStream logger) throws FileNotFoundException {
		this.sourcePath = source;
		this.destPath = destination;
		this.logger = logger;
		this.files = new FilesInputStream(source);
	}

	@Override
	public void run() {
		startTime = System.currentTimeMillis();

		File sourceFile, destFile;
		int pathLen = sourcePath.length();
		String relPath;
		while ((sourceFile = files.read()) != null && !isStopped) {

			Thread.yield();
			scanned++;
			logger.println("Processing: " + sourceFile.getAbsolutePath());
			relPath = sourceFile.getAbsolutePath().substring(pathLen);
			destFile = new File(destPath + relPath);
			if (!destFile.exists() || isOlder(destFile, sourceFile)) {
				copied++;
				try {
					copyFile(sourceFile, destFile);
					logger.println("copied");
				} catch (IOException e) {
					errorsList.add(e.getMessage());
					logger.println("error");
					continue;
				}
			} else if (isOlder(sourceFile, destFile)) {
				copiedBack++;
				try {
					copyFile(destFile, sourceFile);
					logger.println("copied back");
				} catch (IOException e) {
					errorsList.add(e.getMessage());
					logger.println("error");
					continue;
				}
			}
		}
		logResults();
	}

	/**
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	private void copyFile(File sourceFile, File destFile) throws IOException {
		destFile.getParentFile().mkdirs();// make folders
		Files.copy(sourceFile, destFile);
		destFile.setLastModified(sourceFile.lastModified());
	}

	private void logResults() {
		if (!errorsList.isEmpty()) {
			logger.println("E R R O R S :");
			for (String error : errorsList) {
				logger.println(error);
			}
		}
		logger.println("scanned: " + scanned);
		logger.println("copied: " + copied);
		logger.println("copied back: " + copiedBack);
		logger.println("errors: " + errorsList.size());
		logger.println("no more files found");
		long time = System.currentTimeMillis() - startTime;
		logger.println("That took " + time / 60000 + " min (" + time + "ms)");
	}

	private boolean isOlder(File file, File than) {
		return file.lastModified() < than.lastModified();
	}

	public void stopMe() {
		this.isStopped = true;
	}

}
