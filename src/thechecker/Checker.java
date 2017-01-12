package thechecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException {

		/*
		 * TODO: -add timer -add printWriter function to util -add master list
		 * -add word modifier to generator (for invalid)
		 */

		ArrayList<String> settingString = getSettings();

		String mode = settingString.get(0);
		String path = settingString.get(1);
		String gen = settingString.get(2);
		String threads = settingString.get(3);
		Checker check = new Checker(path, Integer.parseInt(gen), Integer.parseInt(threads));

		run(check, Integer.parseInt(mode));
	}

	public static void run(Checker check, int run) throws IOException, InterruptedException {
		String path = check.getPath();

		if (run == 0) {
			if (new File(path).exists()) {
				thechecker.Util.removeFile(new File(path + "data"));
				thechecker.Util.removeFile(new File(path + "finished"));
			}
			thechecker.Util.setup(path);
		}
		if (run >= 16) {
			check.validateAll();
			run -= 16;
		}
		if (run >= 8) {
			check.generateAll();
			run -= 8;
		}
		if (run >= 4) {
			check.splitAll();
			check.checkAll();
			check.mergeAll();
			run -= 4;
		}
		if (run >= 2) {
			check.sortAll();
			run -= 2;
		}
		if (run >= 1) {
			check.formatAll();
			run -= 1;
		}
	}

	public static ArrayList<String> getSettings() throws FileNotFoundException {
		File settingFile = new File("settings.txt");
		if (!settingFile.exists()) {
			makeSettings();
			System.out.println("Generated settings (go edit them now before running program again)");
			System.exit(0);
			return null;
		} else {
			ArrayList<String> settingString = thechecker.Util.getList(new File("settings.txt"), false);

			for (int i = 0; i < settingString.size(); i++) {
				String line = settingString.get(i);
				if (line.contains("#")) {
					settingString.remove(i);
					i--;
				}
			}

			if (settingString.size() != 4) {
				makeSettings();
				return getSettings();
			}

			return settingString;
		}
	}

	public static void makeSettings() throws FileNotFoundException {
		File settingFile = new File("settings.txt");
		PrintWriter writer = new PrintWriter(settingFile);
		writer.print("# Run mode (add modes you want to execute):\n"
				+ "# 0: reset (NOTE: all files except sources will be deleted / recreated)\n" + "# 16: validate\n"
				+ "# 8: generate\n"
				+ "# 4: check (doesn't overwrite finished files - delete folder if you want it to recheck)\n"
				+ "# 2: sort\n" + "# 1: format\n"
				+ "# 31: default (validates all, generates all, checks all, sorts all, formats all)\n" + "#\n"
				+ "# Common modes: 20 (basic check), 28 (basic check and generated values)\n" + "0\n"
				+ "# Output Folder (should end with '/', by default folders generate next to jar file:)\n"
				+ "# Either absolute path or relative path for output file, must use ‘/‘ not ‘\\’\n" + "\n"
				+ "# Generator (add generators you want):\n" + "# 1: Basic substitutions (i - l, o - 0, 3 -> e, 1 -> i/l)\n"
				+ "# 2: Replaces all vowels with underscores\n" + "# 4: Adds an underscore to the end (WIP)\n"
				+ "# 8: Does all combinations of vowels with underscores (WIP)\n" + "3\n" + "# Threads:\n"
				+ "# More is sometimes better (between 10 and 30)\n" + "20\n" + "# How to run: open a command line and type 'java -jar ', then drag this file into the command line and press enter.\n" + "# If that doesn't work, google's your friend.");
		writer.close();
	}
}
