package names_gatherer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TakenNamesDb {
	public static void main(String[] args) {
//		new TakenNamesId();
	}

	public int threadLimit;
	public int curThreads;
	public String[] users;

	public TakenNamesDb() {
		load();
	}

	private void load() {
		log("Started Loading");
		threadLimit = 100;
		curThreads = 0;
		users = new String[TakenNames.getMaxId()];

		File f = new File("id-names");
		try {
			Scanner s = new Scanner(f);
			int lineNum = 0;
			while (s.hasNextLine()) {
				users[lineNum] = s.nextLine();
				lineNum++;
			}
			s.close();
		} catch (FileNotFoundException e) {
		}
		log("Finished Loading");
//		new Thread(new Updater(this)).start();
	}

	private void save() {
		log("Started Saving");
		File f = new File("id-names");
		try {
			PrintWriter pw = new PrintWriter(f);
			for (int i = 0; i < users.length; i++)
				pw.println(users[i]);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		log("Finished Saving");
	}

	private void log(String s) {
		System.out.println(s);
	}

	private class Updater implements Runnable {
		private TakenNamesId tni;

		public Updater(TakenNamesId tni) {
			this.tni = tni;
		}

		public void run() {
			int start = 0;
			while (true) {
				int max = TakenNames.getMaxId() + 1;
				int additions = 0;
				for (int i = start; i < max; i++) {
					if (users[i] == null || users[i].equals("null")) {
						while (tni.curThreads >= tni.threadLimit)
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {

							}
						tni.curThreads++;
						new Thread(new Checker(tni, i)).start();
						if (additions % 5000 == 0 && additions != 0) {
							log("Threads: " + tni.curThreads + " / " + tni.threadLimit);
							log("Save " + i);
							save();
						}
						additions++;
					} else {
						System.out.println(i);
					}
				}
				start = max;

				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class Checker implements Runnable {
		private TakenNamesId tni;
		private int id;

		public Checker(TakenNamesId tni, int id) {
			this.tni = tni;
			this.id = id;
		}

		public void run() {
			tni.users[id] = id + " " + TakenNames.getUsername(id);
			tni.curThreads--;
		}
	}
}
