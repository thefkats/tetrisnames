package register;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Account {

	private String email;
	private String username;
	private String password;
	private boolean isRegistered;
	private boolean isValidated;

	public Account(String username) {
		setup(username, "asdfpass", getEmail(username), isRegistered(username), isValidated(username, "asdfpass"));
	}

	public Account(String username, String password, String email) {
		setup(username, password, email, isRegistered(username), isValidated(username, password));
	}

	private void setup(String uname, String pword, String email, boolean registered, boolean validated) {
		username = uname;
		password = pword;
		isRegistered = registered;
		isValidated = validated;
		this.email = email;
	}

	public void register() {
		// TODO
	}

	public void validate() {
		// TODO
	}

	public void play() {
		// TODO
	}
	
	public void changeEmail(String email) {
		// TODO
	}
	
	public void changePassword(String password) {
		// TODO
	}
	
	public void forgotPassword(String email) {
		// TODO
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getUsername() {
		return username;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	private String getEmail(String username) {
		return username.replaceAll("_", ".u") + "@grr.la";
	}

	public static boolean isValidated(String userName, String passWord) {
		// TODO
		return false;
	}

	// TODO add checker class that checks using (inline) threads
	private static boolean isRegistered(String userName) {
		int count = 0;
		while (count < 1000) {
			URL site;
			try {
				site = new URL(
						"http://www.tetrisfriends.com/users/ajax/user_lookup.php?searchType=2&username=" + userName);

				BufferedReader in;
				try {
					in = new BufferedReader(new InputStreamReader(site.openStream()));

					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						if (inputLine.contains("Screen Name already taken."))
							return true;
					}

					in.close();
					return false;
				} catch (IOException e) {
					System.out.println("[error: bad] Problem with BufferedReader in Account.isTaken");
				}

			} catch (MalformedURLException e) {
				System.out.println("[error: ?] Bad url in Account.istaken");
			}

		}
		System.out.println("[error: serious] Unchecked username in Account.userName");
		return false;
	}
}
