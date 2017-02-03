package register;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
	private ArrayList<Account> accounts;
	private String path;
	private int user;

	public static void main(String[] args) {
		Main m = new Main();
		m.addAccount("test37");
		m.registerAll();
		System.out.println(m.getAccounts().size());
		m.saveUsers();
	}

	public Main() {
		setup("", 0);
	}

	public Main(String path) {
		setup(path, 0);
	}

	public Main(int user) {
		setup("", user);
	}

	private void setup(String path, int user) {
		accounts = new ArrayList<>();
		this.path = path;
		this.user = user;
		loadUsers();
	}

	public ArrayList<Account> getAccounts() {
		ArrayList<Account> accountsCopy = new ArrayList<>();
		for (Account a : accounts) {
			accountsCopy.add(a.clone());
		}
		
		return accountsCopy;
	}

	public void addAccount(Account a) {
		accounts.add(a);
	}

	public void addAccount(String uname) {
		accounts.add(new Account(uname));
	}

	public boolean registerAll() {
		boolean allRegistered = true;
		for (Account a : accounts) {
			if (!a.isRegistered())
				if (!a.register()) {
					System.out.println("[error: small] Unable to register name: " + a.getUsername());
					allRegistered = false;
				}
		}
		
		return allRegistered;
	}
	
	private boolean loadUsers() {
		File accountFile = new File(path + "accounts.txt");
		if (!accountFile.exists())
			return true;
		try {
			Scanner accountScan = new Scanner(accountFile);
			while (accountScan.hasNextLine()) {
				// TODO add in function that autofills out if line isn't filled
				// (if only a username is there)
				String uname = accountScan.nextLine();
				accounts.add(new Account(uname));
			}
			accountScan.close();
		} catch (FileNotFoundException e) {
			System.out.println("[error: medium] Couldn't load file: " + path + "accounts.txt");
			return false;
		}
		return true;

		// TODO connect with website
	}

	private boolean saveUsers() {
		File accountFile = new File(path + "accounts.txt");
		try {
			PrintWriter accountScan = new PrintWriter(accountFile);

			Set<String> uniqueAccounts = new HashSet<>();
			for (Account a : accounts) {
				if (!uniqueAccounts.contains(a.getUsername())) {
					accountScan.println(a.getUsername());
					uniqueAccounts.add(a.getUsername());
				}
			}
			accountScan.close();
		} catch (FileNotFoundException e) {
			System.out.println("[error: medium] Couldn't save in: " + path + "accounts.txt");
			return false;
		}
		return true;

		// TODO connect with website
	}
}
