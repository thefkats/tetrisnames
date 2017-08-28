package names_gatherer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Queue;

public class TakenNamesInfo {
	public static void main(String[] args) {
		new TakenNamesInfo();
	}

	public int threadLimit;
	public int curThreads;
	public Queue<Integer> toCheck;
	public Queue<String[]> toSave;

	public TakenNamesInfo() {
		load();
	}

	private void load() {
		log("loading");
		long time = System.currentTimeMillis();

		threadLimit = 200;
		curThreads = 0;
		toSave = new QQueue<String[]>();
		toCheck = new QQueue<Integer>();

		File f = new File("source info");
		if (!f.isDirectory())
			f.mkdir();
		File[] files = f.listFiles();
		ArrayList<String> names = new ArrayList<String>();

		int max = TakenNames.getMaxId();
		for (int i = 0; i < max; i++)
			toCheck.add(new Integer(i));
		for (File file : files)
			toCheck.remove(new Integer(Integer.parseInt(file.getName())));

		log("finished loading (" + (System.currentTimeMillis() - time) + ")");
		new Thread(new Updater(this)).start();
		new Thread(new Saver(this)).start();
		new Thread(new Saver(this)).start();

		new Thread(new Stats(this)).start();
	}

	private void log(String s) {
		System.out.println(s);
	}

	private class Updater implements Runnable {
		private TakenNamesInfo tni;

		public Updater(TakenNamesInfo tni) {
			this.tni = tni;
		}

		public void run() {
			long time = System.currentTimeMillis();
			int additions = 0;
			while (tni.toCheck.size() != 0) {
				while (tni.curThreads >= tni.threadLimit)
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {

					}
				tni.curThreads++;
				new Thread(new Checker(tni, tni.toCheck.poll().intValue())).start();
				int interval = 5000;
				if (additions % interval == 0 && additions != 0) {
					log("Threads: " + tni.curThreads + " / " + tni.threadLimit);
					log(tni.toCheck.size() + " left (" + ((System.currentTimeMillis() - time) / 1000) + "s)");
					time = System.currentTimeMillis();
				}
				additions++;

			}
			while (tni.curThreads != 0)
				try

				{
					Thread.sleep(2000);
					log("Finishing " + tni.curThreads + " threads...");
				} catch (InterruptedException e) {

				}
		}
	}

	private class Checker implements Runnable {
		private TakenNamesInfo tni;
		private int id;

		public Checker(TakenNamesInfo tni, int id) {
			this.tni = tni;
			this.id = id;
		}

		public void run() {
			String[] a = new String[2];
			a[0] = "" + id;
			String page = TakenNames.getPage("http://www.tetrisfriends.com/users/ajax/profile_stats.php?id=" + id);
			a[1] = (page == null) ? "null" : page;
			tni.toSave.add(a);
			tni.curThreads--;
		}
	}

	private class Saver implements Runnable {
		private TakenNamesInfo tni;

		public Saver(TakenNamesInfo tni) {
			this.tni = tni;
		}

		public void run() {
			while (true) {
				if (tni.toSave.size() == 0)
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {

					}
				int count = 0;
				while (tni.toSave.size() != 0) {
					String[] s = tni.toSave.poll();
					if (s == null)
						break;
					count++;
					new Thread(new Save(s[0], s[1])).start();
					if (count % 200 == 0 && count != 0)
						System.out.println("Save Threads: " + count);
				}

			}
		}
	}

	private class Save implements Runnable {
		private String fileName;
		private String text;

		public Save(String fileName, String text) {
			this.fileName = fileName;
			this.text = text;
		}

		public void run() {
			File f = new File("source info/" + fileName);
			try {
				PrintWriter pw = new PrintWriter(f);
				pw.println(text);
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private class Stats implements Runnable {
		private TakenNamesInfo tni;

		public Stats(TakenNamesInfo tni) {
			this.tni = tni;
		}

		public void run() {
			int start = tni.toCheck.size();
			long time = System.currentTimeMillis();
			while (true) {
				log("[Stats] toCheck: " + tni.toCheck.size() + ", toSave: " + tni.toSave.size() + ", rate: "
						+ ((start - tni.toCheck.size()) / ((System.currentTimeMillis() - time) / 1000.0)) + "p/s");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
