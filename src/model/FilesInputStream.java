package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FilesInputStream {

	private List<File> foundFiles = new LinkedList<File>();
	private List<File> dirsToScan = new LinkedList<File>();
	private List<String> ignoredFiles = new LinkedList<>();
	private File file;

	public FilesInputStream(String dir) throws FileNotFoundException {
		file = new File(dir);
		if (!file.isDirectory()) {
			System.err.println(file.getPath() + " is not a directory");
			throw new FileNotFoundException();
		}
		dirsToScan.add(file);
		addIgnoredFiles();
	}

	public FilesInputStream(File dir) throws FileNotFoundException {
		file = dir;
		if (!file.isDirectory())
			throw new FileNotFoundException();
		dirsToScan.add(file);
		addIgnoredFiles();
	}

	private void addIgnoredFiles() {
		ignoredFiles.add("desktop.ini");
		ignoredFiles.add("Thumbs.db");
	}

	/**
	 * returns next file
	 * 
	 * @return File
	 */
	public File read() {
		while (foundFiles.isEmpty()) {
			if (dirsToScan.isEmpty())
				return null;
			refill();
		}
		file = foundFiles.get(0);
		foundFiles.remove(0);
		return file;
	}

	private void refill() {
		scan(dirsToScan.get(0));
		dirsToScan.remove(0);
	}

	private void scan(File file) {
		File[] files = file.listFiles();

		for (File f : files) {
			if (f.isDirectory()) {
				dirsToScan.add(f);
			} else if (f.isFile()) {
				if (!ignoredFiles.contains(f.getName()))
					foundFiles.add(f);
			}
		}
	}

	public String getPath() throws IOException {
		return file.getCanonicalPath();
	}
}
