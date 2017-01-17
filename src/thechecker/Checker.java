package thechecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Checker {

	/**
	 * Path to folder to setup this program in.
	 */
	private String path;

	/**
	 * Number of threads to do at once.
	 */
	private int threadNum;

	private int gen;

	/**
	 * Constructs a new checker with specified path.
	 * 
	 * @param path
	 *            path of the directory to build this program in
	 * @throws FileNotFoundException
	 */
	public Checker(String path) throws FileNotFoundException {
		thechecker.Util.setup(path);
		this.path = path;
		threadNum = 1;
		gen = 3;
	}

	/**
	 * Constructs a new checker with specified path and number of threads.
	 * 
	 * @param path
	 *            path of the directory to build this program in
	 * @param threads
	 *            number of threads to run at once
	 * @throws FileNotFoundException
	 */
	public Checker(String path, int threads) throws FileNotFoundException {
		thechecker.Util.setup(path);
		this.path = path;
		threadNum = threads;
		gen = 3;
	}

	public Checker(String path, int gen, int threads) throws FileNotFoundException {
		System.out.print("Setting up...");
		thechecker.Util.setup(path);
		this.path = path;
		threadNum = threads;
		this.gen = Math.max(0, Math.min(gen, 3));
		System.out.println(" Done!");
	}

	/**
	 * Runs the checker and checks sources in source folder.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void run() throws InterruptedException, IOException {
		// validateAll();
		// generateAll();
		// splitAll();
		// checkAll();
		// mergeAll();
		// sortAll();
		formatAll();
		// TODO
	}

	/**
	 * Gets the path of this checker. Set to "" by default.
	 * 
	 * @return path of this checker
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path for this checker to build files in.
	 * 
	 * @param path
	 *            path to update this checker with
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public void validateAll() throws FileNotFoundException {
		ArrayList<File> documents = thechecker.Util.findFiles(new File(path + "sources"));

		for (File file : documents) {
			validate(file);
		}
	}

	public void validate(File doc) throws FileNotFoundException {
		System.out.print("-Validating: " + doc.getName() + "...");
		long startTime = System.currentTimeMillis();

		ArrayList<String> words = thechecker.Util.getList(doc);
		ArrayList<String> valid = new ArrayList<>();
		ArrayList<String> invalid = new ArrayList<>();

		for (String word : words) {
			if (validate(word)) {
				valid.add(word);
			} else {
				invalid.add(word);
			}
		}

		java.util.Collections.sort(valid);
		java.util.Collections.sort(invalid);

		File outputValid = new File(path + "data/valid/" + thechecker.Util.getName(doc) + ".valid.txt");
		File outputInvalid = new File(path + "data/invalid/" + thechecker.Util.getName(doc) + ".invalid.txt");
		PrintWriter writer = new PrintWriter(outputValid);
		for (String word : valid) {
			writer.println(word);
		}
		writer.close();
		writer = new PrintWriter(outputInvalid);
		for (String word : invalid) {
			writer.println(word);
		}
		writer.close();

		thechecker.Util.removeDuplicates(outputValid);
		thechecker.Util.removeDuplicates(outputInvalid);

		long endTime = System.currentTimeMillis();
		System.out.println(
				" Done! (" + (doc.length() / 1000.0) + " kb in " + ((endTime - startTime) / 1000.0) + " seconds)");
	}

	public boolean validate(String word) {
		if (word.length() < 3 || word.length() > 16)
			return false;

		for (String swear : thechecker.Util.invalidWords) {
			if (word.contains(swear))
				return false;
		}

		if (!thechecker.Util.FIRST_LETTERS.contains("" + word.charAt(0)))
			if (word.charAt(0) == '0') {
				if (!thechecker.Util.FIRST_LETTERS.contains("" + word.charAt(1)))
					return false;
			} else {
				return false;
			}
		for (int i = 1; i < word.length(); i++) {
			if (!thechecker.Util.LETTERS.contains("" + word.charAt(i)))
				return false;
		}

		return true;
	}

	public void generateAll() throws FileNotFoundException {
		ArrayList<String> include = new ArrayList<>();
		ArrayList<String> exclude = new ArrayList<>();
		include.add(".valid");
		ArrayList<File> documents = thechecker.Util.findFiles(new File(path + "/data/valid"), include, exclude);

		for (File file : documents) {
			generate(file);
		}
	}

	public void generate(File doc) throws FileNotFoundException {
		System.out.print("-Generating: " + doc.getName() + "...");
		long startTime = System.currentTimeMillis();

		ArrayList<String> words = thechecker.Util.getList(doc);
		ArrayList<String> generatedWords = new ArrayList<>();
		// TODO make this a general function

		for (String word : words) {
			int tempGen = gen;
			if (tempGen >= 4) {
				System.out.println("What???");
				tempGen -= 4;
			}
			if (tempGen >= 2) {
				generatedWords.addAll(generate1(word));
				tempGen -= 2;
			}
			if (tempGen >= 1) {
				generatedWords.addAll(generate2(word));
				tempGen -= 1;
			}
		}

		for (int i = 0; i < generatedWords.size(); i++) {
			if (!validate(generatedWords.get(i)))
				generatedWords.remove(i);
		}

		java.util.Collections.sort(generatedWords);

		File output = new File(path + "data/valid/" + thechecker.Util.getName(doc) + "gen" + gen + ".txt");
		PrintWriter writer = new PrintWriter(output);
		for (String word : generatedWords) {
			writer.println(word);
		}
		writer.close();

		thechecker.Util.removeDuplicates(output);
		long endTime = System.currentTimeMillis();
		System.out.println(
				" Done! (" + (output.length() / 1000.0) + " kb in " + ((endTime - startTime) / 1000.0) + " seconds)");
	}

	public ArrayList<String> generate1(String word) {

		ArrayList<String> words = new ArrayList<>();
		ArrayList<String> wordParts = new ArrayList<>();

		char[] possible;
		possible = generate1Rules(word.charAt(0), true);
		for (char c : possible) {
			wordParts.add("" + c);
		}
		for (int i = 1; i < word.length(); i++) {
			possible = generate1Rules(word.charAt(i), false);
			ArrayList<String> temp;
			temp = new ArrayList<>();
			for (String s : wordParts) {
				for (char c : possible) {
					temp.add(s + c);
				}
			}
			wordParts = temp;
		}

		return wordParts;
	}

	public ArrayList<String> generate2(String word) {
		ArrayList<String> withoutVowels = new ArrayList<>();
		String newWord = "" + word.charAt(0);

		for (int i = 1; i < word.length(); i++) {
			if (thechecker.Util.VOWELS.contains("" + word.charAt(i))) {
				newWord += "_";
			} else {
				newWord += word.charAt(i);
			}
		}

		withoutVowels.add(newWord);
		return withoutVowels;
	}

	private char[] generate1Rules(char letter, boolean isFirst) {
		char[] possible = new char[20];
		int count = 1;
		possible[0] = letter;

		if (letter == '0') {
			possible[count] = 'o';
			count++;
		} else if (letter == 'o') {
			possible[count] = '0';
			count++;
		} else if (letter == '3') {
			possible[count] = 'e';
			count++;
		} else if (letter == 'i') {
			possible[count] = 'l';
			count++;
		} else if (letter == 'l') {
			possible[count] = 'i';
			count++;
		} else if (letter == '1') {
			possible[count] = 'l';
			count++;
			possible[count] = 'i';
			count++;
		} else if (letter == '_' || letter == ' ' || letter == '-') {
			possible[count] = 0;
			count++;
		}

		if (!isFirst) {
			if (letter == '-' || letter == ' ' || letter == '\'') {
				possible[count] = '_';
				count++;
			}
		}

		char[] total = new char[count];
		for (int i = 0; i < count; i++) {
			total[i] = possible[i];
		}
		return total;
	}

	public void splitAll() throws FileNotFoundException {
		ArrayList<String> include = new ArrayList<>();
		ArrayList<String> exclude = new ArrayList<>();
		include.add(".valid");
		include.add(".gen" + gen);
		ArrayList<File> documents = thechecker.Util.findFiles(new File(path + "data/valid"), include, exclude);

		include = new ArrayList<>();
		exclude = new ArrayList<>();
		include.add("checked");
		ArrayList<File> documentsDone = thechecker.Util.findFiles(new File(path + "finished"), include, exclude);
		ArrayList<String> finishedNames = new ArrayList<>();

		for (File file : documentsDone) {
			finishedNames.add(thechecker.Util.getName(file));
		}

		for (File file : documents) {
			if (!finishedNames.contains(thechecker.Util.getName(file)))
				split(file);
		}
	}

	public void split(File doc) throws FileNotFoundException {
		System.out.print("-Splitting: " + doc.getName() + "...");
		long startTime = System.currentTimeMillis();

		ArrayList<String> words = Util.getList(doc);
		String path = this.path + "data/temp/" + thechecker.Util.getName(doc);
		if (new File(path).exists()) {
			thechecker.Util.removeFile(new File(path));
		}

		new File(path).mkdir();
		path += "/";

		int place = 0;

		for (int i = 0; i < thechecker.Util.FIRST_LETTERS.length(); i++) {
			new File(path + thechecker.Util.FIRST_LETTERS.charAt(i)).mkdir();

			ArrayList<String> letter = new ArrayList<>();
			while (place < words.size() && words.get(place).charAt(0) == Util.FIRST_LETTERS.charAt(i)) {
				letter.add(words.get(place));
				place++;
			}

			for (int in = 0; in < letter.size() / 500 + 1; in++) {
				File file = new File(path + Util.FIRST_LETTERS.charAt(i) + "/part " + in);
				PrintWriter document = new PrintWriter(file);
				for (int index = 0; index < 500 && index + in * 500 < letter.size(); index++) {
					document.println(letter.get(index + in * 500));
				}
				document.close();
			}

		}

		// TODO make clock
		long endTime = System.currentTimeMillis();
		System.out.println(
				" Done! (" + (doc.length() / 1000.0) + " kb in " + ((endTime - startTime) / 1000.0) + " seconds)");
	}

	public void checkAll() throws FileNotFoundException, InterruptedException {
		ArrayList<String> include = new ArrayList<>();
		ArrayList<String> exclude = new ArrayList<>();
		exclude.add(".c");
		exclude.add(".checked");
		ArrayList<File> documents = thechecker.Util.findFiles(new File(path + "data/temp"), include, exclude);

		for (File file : documents) {
			if (!new File(file.getName() + ".checked").exists())
				check(file);
		}
	}

	public void check(File doc) throws InterruptedException, FileNotFoundException {
		System.out.print("-Checking: " + doc.getParentFile().getParentFile().getName() + "/"
				+ doc.getParentFile().getName() + "/" + doc.getName() + "...");
		long startTime = System.currentTimeMillis();

		ArrayList<String> names = thechecker.Util.getList(doc);
		ArrayList<ArrayList<String>> wordList = new ArrayList<>();
		for (int i = 0; i < threadNum; i++) {
			wordList.add(new ArrayList<String>());
		}
		for (int i = 0; i < names.size(); i++) {
			wordList.get(i % threadNum).add(names.get(i));
		}

		ArrayList<Name> words = new ArrayList<>();
		for (int i = 0; i < threadNum; i++) {
			words.add(new Name(wordList.get(i), path));
			words.get(i).start();
		}
		for (int i = 0; i < threadNum; i++) {
			words.get(i).join();
		}

		File out = new File(doc.getAbsolutePath() + ".c");
		PrintWriter output = new PrintWriter(out);
		File outTaken = new File(doc.getAbsolutePath() + ".t");
		PrintWriter outputTaken = new PrintWriter(outTaken);

		for (Name word : words) {
			for (String validName : word.getUntaken()) {
				output.println(validName);
			}
			for (String invalidName : word.getTaken()) {
				outputTaken.println(invalidName);
			}
		}

		outputTaken.close();
		output.close();
		File finished = new File(out.getAbsolutePath() + "hecked.txt");
		out.renameTo(finished);
		File finishedTaken = new File(outTaken.getAbsolutePath() + "aken.txt");
		outTaken.renameTo(finishedTaken);

		long endTime = System.currentTimeMillis();
		System.out.println(
				" Done! (" + (doc.length() / 1000.0) + " kb in " + ((endTime - startTime) / 1000.0) + " seconds)");
	}

	public void mergeAll() throws IOException {
		File[] files = new File(path + "data/temp").listFiles();
		for (File file : files) {
			if (!file.getName().startsWith(".") && !file.getName().endsWith(".ignore")
					&& !file.getName().endsWith(".ignore.txt"))
				merge(file);
		}
	}

	public void merge(File doc) throws IOException {
		System.out.print("-Merging: " + doc.getName() + "...");
		long startTime = System.currentTimeMillis();

		ArrayList<ArrayList<File>> sortingLists = new ArrayList<>();

		ArrayList<String> include = new ArrayList<>();
		ArrayList<String> exclude = new ArrayList<>();
		include.add("checked");
		ArrayList<File> untaken = thechecker.Util.findFiles(doc, include, exclude);
		sortingLists.add(untaken);

		include = new ArrayList<>();
		include.add("taken");
		ArrayList<File> taken = thechecker.Util.findFiles(doc, include, exclude);
		sortingLists.add(taken);

		for (ArrayList<File> files : sortingLists) {
			ArrayList<String> words = new ArrayList<>();
			String fileName = "error";

			for (File file : files) {
				words.addAll(thechecker.Util.getList(file));
				fileName = file.getName();
			}
			thechecker.Util.logPrint(path, "Sorting " + doc.getName());

			File dir = new File(path + "finished/" + thechecker.Util.getName(doc));
			if (!dir.exists())
				dir.mkdir();

			File combined = new File(path + "finished/" + thechecker.Util.getName(doc) + "/"
					+ fileName.substring(fileName.indexOf('.') + 1));
			PrintWriter writer = new PrintWriter(combined);
			for (String word : words) {
				writer.println(word);
			}
			writer.close();
			thechecker.Util.removeDuplicates(combined);
		}

		thechecker.Util.removeFile(doc);

		long endTime = System.currentTimeMillis();
		System.out.println(
				" Done! (" + (doc.length() / 1000.0) + " kb in " + ((endTime - startTime) / 1000.0) + " seconds)");
	}

	public static void updateMaster() {
		// TODO make master lists (length, letters)
	}

	public void sortAll() throws FileNotFoundException {
		ArrayList<String> include = new ArrayList<>();
		ArrayList<String> exclude = new ArrayList<>();
		include.add("checked");
		ArrayList<File> documents = thechecker.Util.findFiles(new File(path + "finished"), include, exclude);

		for (File file : documents) {
			sort(file);
		}
	}

	public void sort(File doc) throws FileNotFoundException {
		System.out.print("-Sorting: " + doc.getParentFile().getName() + "...");
		long startTime = System.currentTimeMillis();

		new File(doc.getParent() + "/sorted").mkdir();
		new File(doc.getParent() + "/sorted/length").mkdir();
		ArrayList<String> onlyLetters = new ArrayList<>();

		// Create lists to hold the words.
		ArrayList<ArrayList<String>> length = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			length.add(new ArrayList<String>());
		}

		// Sort all words by length.
		for (String word : thechecker.Util.getList(doc)) {
			boolean isOnlyLetters = true;
			for (int i = 0; i < word.length(); i++) {
				if (!thechecker.Util.AZ.contains("" + word.charAt(i))) {
					isOnlyLetters = false;
					break;
				}
			}
			if (isOnlyLetters)
				onlyLetters.add(word);

			length.get(Math.min(16, word.length()) - 3).add(word);
		}

		PrintWriter writer = new PrintWriter(new File(doc.getParent() + "/sorted/onlyLetters.txt"));
		for (String word : onlyLetters) {
			writer.println(word);
		}
		writer.close();

		// Sort all the lengths alphabetically and stores in a file.
		int count = 3;
		for (ArrayList<String> arr : length) {
			java.util.Collections.sort(arr);
			writer = new PrintWriter(new File(doc.getParent() + "/sorted/length/" + count + " letters.txt"));
			for (String name : arr) {
				writer.println(name);
			}
			writer.close();
			count++;
		}

		long endTime = System.currentTimeMillis();
		System.out.println(
				" Done! (" + (doc.length() / 1000.0) + " kb in " + ((endTime - startTime) / 1000.0) + " seconds)");
	}

	public void formatAll() throws FileNotFoundException {
		File doc = new File(path + "finished");
		ArrayList<String> include = new ArrayList<>();
		ArrayList<String> exclude = new ArrayList<>();

		exclude.add("formatted");
		ArrayList<File> files = thechecker.Util.findFiles(doc, include, exclude);

		for (File file : files) {
			format(file);
		}
	}

	public void format(File doc) throws FileNotFoundException {
		System.out.print("-Formatting: " + doc.getParentFile().getName() + "...");
		long startTime = System.currentTimeMillis();

		ArrayList<String> names = thechecker.Util.getList(doc);

		File out = new File(doc.getParentFile().getPath() + "/" + thechecker.Util.getName(doc) + ".formatted.txt");
		PrintWriter output = new PrintWriter(out);

		ArrayList<String> a = new ArrayList<>();
		ArrayList<String> b = new ArrayList<>();
		ArrayList<String> c = new ArrayList<>();
		ArrayList<String> d = new ArrayList<>();
		ArrayList<String> e = new ArrayList<>();
		ArrayList<String> f = new ArrayList<>();
		ArrayList<String> g = new ArrayList<>();
		ArrayList<String> h = new ArrayList<>();
		ArrayList<String> i = new ArrayList<>();
		ArrayList<String> j = new ArrayList<>();
		ArrayList<String> k = new ArrayList<>();
		ArrayList<String> l = new ArrayList<>();
		ArrayList<String> m = new ArrayList<>();
		ArrayList<String> n = new ArrayList<>();
		ArrayList<String> o = new ArrayList<>();
		ArrayList<String> p = new ArrayList<>();
		ArrayList<String> q = new ArrayList<>();
		ArrayList<String> r = new ArrayList<>();
		ArrayList<String> s = new ArrayList<>();
		ArrayList<String> t = new ArrayList<>();
		ArrayList<String> u = new ArrayList<>();
		ArrayList<String> v = new ArrayList<>();
		ArrayList<String> w = new ArrayList<>();
		ArrayList<String> x = new ArrayList<>();
		ArrayList<String> y = new ArrayList<>();
		ArrayList<String> z = new ArrayList<>();
		ArrayList<String> misc = new ArrayList<>();

		for (String name : names) {
			String lName = name;
			lName.toLowerCase();
			if (lName.charAt(0) == 'a')
				a.add(name);
			else if (lName.charAt(0) == 'b')
				b.add(name);
			else if (lName.charAt(0) == 'c')
				c.add(name);
			else if (lName.charAt(0) == 'd')
				d.add(name);
			else if (lName.charAt(0) == 'e')
				e.add(name);
			else if (lName.charAt(0) == 'f')
				f.add(name);
			else if (lName.charAt(0) == 'g')
				g.add(name);
			else if (lName.charAt(0) == 'h')
				h.add(name);
			else if (lName.charAt(0) == 'i')
				i.add(name);
			else if (lName.charAt(0) == 'j')
				j.add(name);
			else if (lName.charAt(0) == 'k')
				k.add(name);
			else if (lName.charAt(0) == 'l')
				l.add(name);
			else if (lName.charAt(0) == 'm')
				m.add(name);
			else if (lName.charAt(0) == 'n')
				n.add(name);
			else if (lName.charAt(0) == 'o')
				o.add(name);
			else if (lName.charAt(0) == 'p')
				p.add(name);
			else if (lName.charAt(0) == 'q')
				q.add(name);
			else if (lName.charAt(0) == 'r')
				r.add(name);
			else if (lName.charAt(0) == 's')
				s.add(name);
			else if (lName.charAt(0) == 't')
				t.add(name);
			else if (lName.charAt(0) == 'u')
				u.add(name);
			else if (lName.charAt(0) == 'v')
				v.add(name);
			else if (lName.charAt(0) == 'w')
				w.add(name);
			else if (lName.charAt(0) == 'x')
				x.add(name);
			else if (lName.charAt(0) == 'y')
				y.add(name);
			else if (lName.charAt(0) == 'z')
				z.add(name);
			else
				misc.add(name);
		}

		int max = Math.max(Math.max(Math.max(a.size(), b.size()), Math.max(c.size(), d.size())),
				Math.max(Math.max(e.size(), f.size()), Math.max(g.size(), h.size())));
		max = Math.max(Math.max(Math.max(max, i.size()), Math.max(j.size(), k.size())),
				Math.max(Math.max(l.size(), m.size()), Math.max(n.size(), o.size())));
		max = Math.max(Math.max(Math.max(max, p.size()), Math.max(q.size(), r.size())),
				Math.max(Math.max(s.size(), t.size()), Math.max(u.size(), v.size())));
		max = Math.max(Math.max(Math.max(max, w.size()), Math.max(x.size(), y.size())),
				Math.max(z.size(), misc.size()));

		for (int in = 0; in < max; in++) {
			if (in < a.size()) {
				output.print(a.get(in));
			}
			output.print("\t");
			if (in < b.size()) {
				output.print(b.get(in));
			}
			output.print("\t");
			if (in < c.size()) {
				output.print(c.get(in));
			}
			output.print("\t");
			if (in < d.size()) {
				output.print(d.get(in));
			}
			output.print("\t");
			if (in < e.size()) {
				output.print(e.get(in));
			}
			output.print("\t");
			if (in < f.size()) {
				output.print(f.get(in));
			}
			output.print("\t");
			if (in < g.size()) {
				output.print(g.get(in));
			}
			output.print("\t");
			if (in < h.size()) {
				output.print(h.get(in));
			}
			output.print("\t");
			if (in < i.size()) {
				output.print(i.get(in));
			}
			output.print("\t");
			if (in < j.size()) {
				output.print(j.get(in));
			}
			output.print("\t");
			if (in < k.size()) {
				output.print(k.get(in));
			}
			output.print("\t");
			if (in < l.size()) {
				output.print(l.get(in));
			}
			output.print("\t");
			if (in < m.size()) {
				output.print(m.get(in));
			}
			output.print("\t");
			if (in < n.size()) {
				output.print(n.get(in));
			}
			output.print("\t");
			if (in < o.size()) {
				output.print(o.get(in));
			}
			output.print("\t");
			if (in < p.size()) {
				output.print(p.get(in));
			}
			output.print("\t");
			if (in < q.size()) {
				output.print(q.get(in));
			}
			output.print("\t");
			if (in < r.size()) {
				output.print(r.get(in));
			}
			output.print("\t");
			if (in < s.size()) {
				output.print(s.get(in));
			}
			output.print("\t");
			if (in < t.size()) {
				output.print(t.get(in));
			}
			output.print("\t");
			if (in < u.size()) {
				output.print(u.get(in));
			}
			output.print("\t");
			if (in < v.size()) {
				output.print(v.get(in));
			}
			output.print("\t");
			if (in < w.size()) {
				output.print(w.get(in));
			}
			output.print("\t");
			if (in < x.size()) {
				output.print(x.get(in));
			}
			output.print("\t");
			if (in < y.size()) {
				output.print(y.get(in));
			}
			output.print("\t");
			if (in < z.size()) {
				output.print(z.get(in));
			}
			output.print("\t");
			if (in < misc.size()) {
				output.print(misc.get(in));
			}
			output.println();
		}

		output.close();

		long endTime = System.currentTimeMillis();
		System.out.println(
				" Done! (" + (doc.length() / 1000.0) + " kb in " + ((endTime - startTime) / 1000.0) + " seconds)");
	}
}
