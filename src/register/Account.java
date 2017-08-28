package register;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import captcha.Cracker;

public class Account {
	public String path;
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
		path = "tetrisnames data/";
	}

	public boolean register() {
		try {
			isRegistered = register(username, email, password);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean validate() {
		// TODO
		return false;
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

	public Account clone() {
		return new Account(username, password, email);
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

	private boolean register(String uname, String pword, String eMail) throws IOException {
		String exePath = "chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", exePath);
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.tetrisfriends.com/users/register.php?saveGame=true&linkTag=nav");

		WebElement email = driver.findElement(By.id("email"));
		WebElement uName = driver.findElement(By.id("username"));
		WebElement pWord1 = driver.findElement(By.id("password"));
		WebElement pWord2 = driver.findElement(By.id("password_dup"));
		Select month = new Select(driver.findElement(By.id("month")));
		Select day = new Select(driver.findElement(By.id("day")));
		Select year = new Select(driver.findElement(By.id("year")));
		WebElement pBDay = driver.findElement(By.name("is_private_birth_date"));
		Select country = new Select(driver.findElement(By.name("country")));
		WebElement pLoc = driver.findElement(By.name("is_private_location"));
		Select state = new Select(driver.findElement(By.id("stateSelect")));

		WebElement img = driver.findElement(By.name("captcha_img"));

		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		// FileUtils.copyFile(screen, new
		// File("C:/Users/James/Documents/programming/java/tfregister/captchas/toBeSorted/unsplit/temp2.png"));

		int width = img.getSize().getWidth();
		int height = img.getSize().getHeight();
		BufferedImage bImg = ImageIO.read(screen);

		// System.out.println(img.getLocation().getX() + "," +
		// img.getLocation().getY());
		// BufferedImage dest = bImg.getSubimage(img.getLocation().getX(),
		// img.getLocation().getY(), width, height);
		BufferedImage dest = bImg.getSubimage(315, 542, 250, 62);

		int w = dest.getWidth();
		int h = dest.getHeight();
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(0.8, 0.8);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(dest, after);

		ImageIO.write(after, "png", screen);
		File file = new File(path + "test.jpg");
		FileUtils.copyFile(screen, file);
		// File file2 = new File(path + "omg.png");
		// FileUtils.copyFile(screen, file2);

		KeyCracker c = new KeyCracker("test.jpg");
		String keyText = c.getKey();

		WebElement key = driver.findElement(By.name("captcha"));

		email.sendKeys("email@emaail.com");
		uName.sendKeys("a19487");
		pWord1.sendKeys("asdf123p");
		pWord2.sendKeys("asdfpass");
		month.selectByValue("7");
		day.selectByValue("3");
		year.selectByValue("1997");
		country.selectByValue("US");

		state.selectByValue("NJ");
		pBDay.click();
		pLoc.click();
		key.sendKeys(keyText);

		return false;
	}
}
