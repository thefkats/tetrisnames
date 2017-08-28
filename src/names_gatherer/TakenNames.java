package names_gatherer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class TakenNames {

	private CharTreeArray cta;
	public int threadsNum;
	public int threadsGoal;

	public TakenNames() {
		load();
	}

	public void setThreads(int num) {
		threadsGoal = num;
	}

	public static int getMaxId() {
		int x = 5000000;
		double inc = Math.pow(2, 20);
		boolean limit = false;
		while (true) {
			String username = getUsername(x);
			if (username == null) {
				inc = inc / 2;
				x -= inc;
				limit = true;
			} else {
				if (limit) {
					inc = inc / 2;
					x += inc;
				} else
					x += inc;
			}
			if (inc <= .5) {
				if (username == null)
					x--;
				break;
			}
		}
		return x;
	}

	/**
	 * 
	 * @param id
	 * @return null if no username, "-" if page is returned null (check later), and
	 *         string if username is found
	 */
	public static String getUsername(int id) {
		String page = getPage("http://www.tetrisfriends.com/users/ajax/profile_stats.php?id=" + id);
		if (page == null)
			return "-";

		String toSearch = "profileViewMPDetails(\"";
		page = page.substring(page.indexOf(toSearch) + toSearch.length());
		page = page.substring(0, page.indexOf("\""));

		return page.length() == 0 ? null : page.trim();
	}

	public void check(int id) {
		cta.add(getUsername(id), id, System.currentTimeMillis(), getStats(id));

	}

	private void load() {
		System.out.print("[Loading...");
		ArrayList<String> names = new ArrayList<String>();
		File f = new File("takenNames.txt");
		try {
			Scanner scan = new Scanner(f);
			String line;
			while (scan.hasNextLine()) {
				line = scan.nextLine();
				names.add(line);
			}
			scan.close();
			cta = new CharTreeArray(names);
		} catch (FileNotFoundException e) {
			cta = new CharTreeArray();
		}
		threadsNum = 0;
		threadsGoal = 50;
		System.out.println(" Done!]");
		new Thread(new Updater(this)).start();
	}

	public void save() {
		File f = new File("takenNames.txt");

		try {
			PrintWriter pw = new PrintWriter(f);
			ArrayList<String> list = new ArrayList<String>(cta.list());
			// java.util.Collections.sort(list);
			for (String s : list)
				pw.println(s);
			pw.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Could not save...");
		}

		f = new File("exportedNames.txt");
		try {
			PrintWriter pw = new PrintWriter(f);
			ArrayList<String> list = new ArrayList<String>(cta.list());
			// java.util.Collections.sort(list);
			for (String s : list)
				if (s.contains(","))
					pw.println(s.substring(0, s.indexOf(",")));
				else
					pw.println(s);
			pw.close();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Could not save...");
		}
	}

	public static boolean nameTaken(String name) {
		String page = getPage("http://www.tetrisfriends.com/users/ajax/user_lookup.php?searchType=2&username=" + name);
		if (page.equals("SUCCESS"))
			return false;
		if (page.equals("Screen Name already taken."))
			return true;
		return false;
	}

	public static String getStats(int id) {
		return getPage("http://www.tetrisfriends.com/users/ajax/profile_stats.php?id=" + id);
	}

	/**
	 * 
	 * @param link
	 * @return returns "http://www.tetrisfriends.com/users/user_not_found.php" if
	 *         page is not found, string of the page, or null if there was an error
	 *         loading
	 */
	public static String getPage(String link) {
		int count = 0;
		while (true) {
			InputStream is = null;
			try {
				URL url = new URL(link);
				is = url.openStream(); // throws an IOException
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String output = "";
				String line;
				while ((line = br.readLine()) != null) {
					output += line;
				}
				if (output.length() != 0)
					return output;
			} catch (MalformedURLException mue) {
				throw new IllegalArgumentException("Invalid Link: " + link);
			} catch (IOException ioe) {
				if (ioe.getMessage().equals("http://www.tetrisfriends.com/users/user_not_found.php"))
					return ioe.getMessage();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException ioe) {
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			count++;
			if (count % 30 == 0 && count != 0) {
				return null;
			}
		}
	}

	private class Checker implements Runnable {
		private int id;
		private TakenNames tn;

		public Checker(int id, TakenNames tn) {
			this.id = id;
			this.tn = tn;
		}

		public void run() {
			tn.check(id);
			tn.threadsNum--;
		}
	}

	private class Updater implements Runnable {
		private TakenNames tn;

		public Updater(TakenNames tn) {
			this.tn = tn;
		}

		public void run() {
			while (true) {
				int max = getMaxId();
				int additions = 0;
				int start = cta.arrSize();
				for (int i = start; i <= max; i++) {
					while (tn.threadsNum >= tn.threadsGoal)
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
						}

					tn.threadsNum++;
					new Thread(new Checker(i, tn)).start();

					additions++;
					if (additions % 1000 == 0 && additions != 0) {
						start = cta.arrSize();
						System.out.println("[Saving: " + (start) + "]");
						tn.save();
					}
					if (additions % 100 == 0)
						System.out.println("[Completed: " + start + " + " + additions + "]\t[Threads: " + threadsNum
								+ "/" + threadsGoal + "]");
				}
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					save();
				}
			}
		}
	}

	public static void main(String[] args) {
		new TakenNames();
		// System.out.println(TakenNames.getPage("http://www.tetrisfriends.com/users/ajax/profile_stats.php?id=54"));
		// check(5422767);
	}
}
