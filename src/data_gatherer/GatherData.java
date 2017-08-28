package data_gatherer;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class GatherData {
	private static ChromeDriver driver;

	public static void main(String[] args) {
		String exePath = "chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", exePath);
		System.setProperty("webdriver.chrome.silentOutput", "true");
		
		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File("Adblock-Plus_v1.12.4.crx"));
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);

		driver = new ChromeDriver(capabilities);

		driver.get("https://www.tetrisfriends.com/users/login.php");

		WebElement email = driver.findElement(By.name("email"));
		WebElement pw = driver.findElement(By.name("password"));
		email.sendKeys("fkatleader3@gmail.com");
		pw.sendKeys("werewolf00");
		pw.submit();

		driver.get("http://www.tetrisfriends.com/games/Live/game.php");
		
		// TODO wait until page is loaded, and check if the background fails to load
		driver.manage().window().setSize(new Dimension(850, 500));

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,300)", "");
		
		try {
			Robot robot = new Robot();
			robot.mouseMove(1250, 500);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
//		driver.close();
	}

}