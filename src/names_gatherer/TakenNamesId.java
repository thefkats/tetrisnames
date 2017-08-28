package names_gatherer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TakenNamesId {
	public static void main(String[] args) {
		int max = TakenNames.getMaxId();
		for (int i = 0; i < max / 100000; i++) {
			TakenNamesId tni = new TakenNamesId(i * 100000, (i + 1) * 100000 - 1);
			while (!tni.finished)
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
		}
	}
	
	public int threadLimit;
	public int curThreads;
	public String[] users;
	public int begin;
	public int end;
	public boolean finished;
	public static final String PATH = "data/id-names/";
	
	public TakenNamesId(int begin, int end) {
		finished = false;
		this.begin = begin;
		this.end = end;
		load();
	}

	private void load() {
		log("Started Loading " + begin + "-" + end);
		threadLimit = 200;
		curThreads = 0;
		users = new String[end - begin];

		File f = new File(PATH + "id-names " + begin + "-" + end);
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
		new Thread(new Updater(this)).start();
	}

	private void save() {
		log("Started Saving " + begin + "-" + end);
		File f = new File(PATH + "id-names " + begin + "-" + end);
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
			int additions = 0;
			long time = System.currentTimeMillis();
			for (int i = 0; i < end - begin; i++) {
				if (users[i] == null || users[i].equals("null")) {
					while (tni.curThreads >= tni.threadLimit)
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {

						}
					tni.curThreads++;
					new Thread(new Checker(tni, i + tni.begin)).start();
					int interval = 5000;
					if (additions % interval == 0 && additions != 0) {
						log("Threads: " + tni.curThreads + " / " + tni.threadLimit);
						log("Saved " + interval + " / " + i + " (" + ((int) (((i * 1.0) / (end - begin)) * 1000) / 10.0)
								+ "%) in " + ((System.currentTimeMillis() - time) / 1000) + "s");
						time = System.currentTimeMillis();
						save();
					}
					additions++;
				}
			}
			if (additions != 0)
				save();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
			}

			int count = 0;
			while (tni.curThreads != 0)
				try {
					Thread.sleep(2000);
					log("Finishing " + tni.curThreads + " threads... (" + (60 - count) + ")");
					if (count % 60 == 0) {
						save();
						new Updater(tni).run();
						break;
					}
					count++;
				} catch (InterruptedException e) {

				}
			if (additions != 0)
				save();
			log("[Finished] " + (begin / 100000) + " / 60");
			finished = true;
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
			String username = TakenNames.getUsername(id);
			if (username == null || !username.equals("-"))
				tni.users[id - tni.begin] = id + " " + username;
			tni.curThreads--;
		}
	}

}
