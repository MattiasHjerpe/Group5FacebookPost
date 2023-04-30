package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        //Enter what you want to post
        String postContentString = "This is a post";

        Logger logger = LoggerFactory.getLogger(Main.class);
        WebDriver driver = null;
        String email = "";
        String password = "";

        // Read the JSON file
        try {
            logger.info("Import json file");
            File jsonFile = new File("C:\\temp\\facebook.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonFile);

            // Extract the username and password values
            email = jsonNode.get("facebookCredentials").get("email").asText();
            password = jsonNode.get("facebookCredentials").get("password").asText();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        // Create ChromeOptions instance and adds arguments
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");

        //Start by assigning chromedriver to the users local location
        logger.info("Assigning Chromedriver");
        try {
            // Set the path to the ChromeDriver executable
            System.setProperty("webdriver.chrome.driver", "src/main/java/chromedriver.exe");
            //Initialize the driver
            driver = new ChromeDriver(options);
            // Navigate to the Facebook login page
            driver.get("https://sv-se.facebook.com/");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // Accept only necessary cookies
        logger.info("Handling cookies window, allow only necessary cookies");
        try {
            assert driver != null;
            WebElement button = driver.findElement(By.xpath("//button[text()='Neka valfria cookies']"));
            button.click();
        } catch (Exception e) {
            logger.error(e.getMessage());
            try {
                WebElement button = driver.findElement(By.xpath("//button[text()='Tillåt endast nödvändiga cookies']"));
                button.click();
            } catch (Exception error) {
                logger.error(e.getMessage());
            }
        }

        // Enter the email address and password, and click login
        logger.info("Using WebElements to input username and password, and click login");
        try {
            WebElement emailInput = driver.findElement(By.id("email"));
            emailInput.sendKeys(email);

            WebElement passwordInput = driver.findElement(By.id("pass"));
            passwordInput.sendKeys(password);

            WebElement loginButton = driver.findElement(By.name("login"));
            loginButton.click();
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
        }

        Thread.sleep(4000);

        //Click on top left Facebook symbol
        logger.info("Load profile");
        try {
            WebElement goToProfile = driver.findElement(By.cssSelector("path[class='xe3v8dz']"));
            goToProfile.click();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Thread.sleep(4000);

        // Click in posting field
        logger.info("Click in posting field");
        try {
            WebElement postField = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div/div[3]/div/div/div/div[1]/div[1]/div/div[2]/div/div/div/div[3]/div/div[2]/div/div/div/div[1]/div/div[1]/span"));
            postField.click();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Thread.sleep(4000);

        // Write a post
        logger.info("Writing post");
        try {
            WebElement writePost = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div/div[4]/div/div/div[1]/div/div[2]/div/div/div/form/div/div[1]/div/div/div/div[2]/div[1]/div[1]/div[1]/div/div/div[1]/p"));
            writePost.sendKeys(postContentString);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        // Post
        logger.info("Clicking post button");
        try {
            WebElement postButton = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div/div[4]/div/div/div[1]/div/div[2]/div/div/div/form/div/div[1]/div/div/div/div[3]/div[2]/div/div/div[1]"));
            postButton.click();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Thread.sleep(4000);
        
        // Verifies that the expected text was posted
        WebElement post = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div/div[3]/div/div/div/div[1]/div[1]/div/div[2]/div/div/div/div[3]/div/div[3]/div/div/div[1]/div/div/div/div/div/div/div/div/div/div/div/div/div[2]/div/div/div[3]/div/div/div/div"));
        String postContent = post.getText();

        if (postContent.equals(postContentString)) {
            logger.info("Test Passed");
        } else {
            logger.info("Test Failed");
        }

        // Close the browser
        driver.quit();
    }
}
