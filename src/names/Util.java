package names;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Util {

	public static final String FIRST_LETTERS = "0abcdefghijklmnopqrstuvwxyz";
	public static final String AZ = "abcdefghijklmnopqrstuvwxyz";
	public static final String LETTERS = "abcdefghijklmnopqrstuvwxyz0123456789_";
	public static final String VOWELS = "aeiouy";

	public static ArrayList<String> invalidWords;

	/**
	 * Makes sure all folders are set up and sets path variable from
	 * settings.txt file.
	 * 
	 * @throws FileNotFoundException
	 */
	public static void setup(String path) throws FileNotFoundException {
		ArrayList<File> files = new ArrayList<>();
		files.add(new File(path));
		files.add(new File(path + "sources"));
		files.add(new File(path + "finished"));
		files.add(new File(path + "data"));
		files.add(new File(path + "data/valid"));
		files.add(new File(path + "data/invalid"));
		files.add(new File(path + "data/settings"));
		files.add(new File(path + "data/temp"));
		files.add(new File(path + "data/backup"));
		files.add(new File(path + "finished/masterlists"));
		files.add(new File(path + "finished/masterlists/formatted"));
		files.add(new File(path + "finished/masterlists/interesting"));
		files.add(new File(path + "finished/masterlists/interesting/length"));
		files.add(new File(path + "data/logs"));

		for (File doc : files) {
			if (!doc.exists()) {
				doc.mkdir();
			}
		}

		// Add swears file if it doesn't exist.
		File swears = new File(path + "data/settings/invalidWords.txt");
		if (!swears.exists()) {
			String invalidWords = "admin\nmoderator\nasshole\nballs\nbastard\nbitch\nbutthead\nclit\ncock\ncunt\ndick\nfatso\nfuck\nhomo\njackass\nporn\npussy\nprick\nqueer\nretard\nshit\nslut\nwhore\nnerd";
			PrintWriter document = new PrintWriter(swears);
			document.print(invalidWords);
			document.close();
		} else {
			invalidWords = new ArrayList<>();
			for (String word : getList(new File(path + "data/settings/invalidWords.txt"))) {
				invalidWords.add(word);
			}
		}
	}

	public static ArrayList<String> getList(File doc) throws FileNotFoundException {
		return getList(doc, true);
	}

	public static ArrayList<String> getList(File doc, boolean lower) throws FileNotFoundException {
		ArrayList<String> words = new ArrayList<>();
		Scanner document = new Scanner(doc);
		String word;
		while (document.hasNextLine()) {
			if (lower)
				words.add(document.nextLine().toLowerCase());
			else
				words.add(document.nextLine());

		}
		document.close();

		return words;
	}

	public static String getName(File doc) {
		if (doc.getName().indexOf('.') != -1)
			return doc.getName().substring(0, doc.getName().indexOf('.'));
		return doc.getName();
	}

	/**
	 * Helper method for findFiles(File, ArrayList, Arraylist).
	 * 
	 * @param file
	 *            file to search
	 * @return arraylist of files that match the search
	 */
	public static ArrayList<File> findFiles(File file) {
		ArrayList<String> include = new ArrayList<String>();
		ArrayList<String> exclude = new ArrayList<String>();
		return findFiles(file, include, exclude);
	}

	/**
	 * Helper method for findFiles(ArrayList, File, ArrayList, ArrayList)
	 * 
	 * @param file
	 *            file to search
	 * @param include
	 *            arraylist of strings to include at the end of the filename
	 * @param exclude
	 *            arraylist of strings to exclude at the end of the filename
	 * @return arraylist of files that match the search
	 */
	public static ArrayList<File> findFiles(File file, ArrayList<String> include, ArrayList<String> exclude) {
		ArrayList<File> files = new ArrayList<>();
		exclude.add(".ignore");
		findFiles(files, file, include, exclude);
		return files;
	}

	/**
	 * Finds files with specified parameters. Ignores files starting with a ".".
	 * Ignores ".txt" at the end of filenames.
	 * 
	 * @param docs
	 *            arraylist of documents that are valid
	 * @param doc
	 *            current directory being searched
	 * @param include
	 *            arraylist of strings that must be included at the end of the
	 *            filename
	 * @param exclude
	 *            arraylist of strings that must be excluded at the end of the
	 *            filename
	 */
	private static void findFiles(ArrayList<File> docs, File doc, ArrayList<String> include,
			ArrayList<String> exclude) {
		if (doc.isDirectory()) {
			File[] files = doc.listFiles();
			for (File file : files) {
				findFiles(docs, file, include, exclude);
			}
		} else {
			boolean add = true;
			if (include.size() != 0) {
				add = false;
				for (String in : include) {
					if (doc.getName().endsWith(in) || doc.getName().endsWith(in + ".txt"))
						add = true;
				}
			}

			for (String ex : exclude) {
				if (doc.getName().endsWith(ex) || doc.getName().endsWith(ex + ".txt"))
					add = false;
			}
			// TODO cleanup

			if (doc.getName().startsWith("."))
				add = false;

			if (add)
				docs.add(doc);
		}
	}

	/**
	 * Removes file and all nested files if it is a directory.
	 * 
	 * @param file
	 *            file to remove
	 */
	public static void removeFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File doc : files) {
				removeFile(doc);
			}
			file.delete();
		} else {
			file.delete();
		}
	}

	public static void logPrint(String path, String entry) throws IOException {
		File file = new File(path + "data/logs/log.txt");
		FileWriter fw = new FileWriter(file, true);
		fw.write("\n" + entry);
		fw.close();
	}

	public static void removeDuplicates(File file) throws FileNotFoundException {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File doc : files) {
				removeDuplicates(doc);
			}
		} else {
			ArrayList<String> words = getList(file);

			Set<String> hs = new HashSet<>();
			hs.addAll(words);
			words.clear();
			words.addAll(hs);
			java.util.Collections.sort(words);

			PrintWriter writer = new PrintWriter(file);
			for (String word : words) {
				writer.println(word);
			}
			writer.close();
		}
	}
}
