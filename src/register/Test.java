package register;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Test {
	public static void main(String[] args) throws IOException {
		URL oracle = new URL("https://www.tetrisfriends.com/users/ajax/user_lookup.php?searchType=2&username=hii");
		BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
		
		//Account a = new Account("hii");
	}
}