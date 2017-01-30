package names;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Name extends Thread {

	private ArrayList<String> words;
	
	private ArrayList<String> untaken;
	
	private ArrayList<String> taken;
	
	private String path;
	
	public Name(ArrayList<String> words, String path) {
		this.path = path;
		this.words = words;
		untaken = new ArrayList<>();
		taken = new ArrayList<>();
	}
	
	@Override
	public void run() {
		for (String word : words) {
			checkWord(word);
		}
	}
	
	private void checkWord(String word) {
		URL site;
		try {
			site = new URL("http://www.tetrisfriends.com/friends/ajax/friend_search_result.php?name=" + word);
			BufferedReader in;
			BufferedWriter writer;
			int count = 0;
			
			while (true) {
				count++;
				try {
					in = new BufferedReader(new InputStreamReader(site.openStream()));
					writer = new BufferedWriter(new FileWriter(path + "data/temp/000.ignore"));

					String inputLine;
					if ((inputLine = in.readLine()) != null) {
						if (inputLine.contains("No results found.")) {
							untaken.add(word);
							in.close();
							writer.close();
							return;
						} else {
							taken.add(word);
							in.close();
							writer.close();
							return;
						}
					} else {
						new Throwable("Page didn't load");
					}

					in.close();
					writer.close();
				} catch (IOException e) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e1) {
						System.out.println("ERROR: sleep was interrupted, didn't check " + word);
					}
					System.out.println(word + " is sleeping " + count + " seconds...");
				}
			}
		} catch (MalformedURLException e1) {
			System.out.println("ERROR: programmer messed up or tetrisfriends changed url or internet is down");
		}
	}
	
	public ArrayList<String> getUntaken() {
		return untaken;
	}
	
	public ArrayList<String> getTaken() {
		return taken;
	}
}
