package register;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Account {

	private String userName;
	private boolean isRegistered;

	public Account(String userName) {
		this.userName = userName;
		BufferedReader hi;
		try {
			URL oracle = new URL("https://www.tetrisfriends.com/users/ajax/user_lookup.php?searchType=2&username=hii");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// isRegistered = //
		// "https://www.tetrisfriends.com/users/ajax/user_lookup.php?searchType=2&username="
		// + userName
	}

	public void register() {

	}

	private boolean isRegistered() {
		return isRegistered;
	}
}
