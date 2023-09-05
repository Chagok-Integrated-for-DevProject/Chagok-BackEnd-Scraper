package com.project.chagok.backend.scraper.batch.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class WebDriverFactory {

    @Value("${spring.profiles.active}")
    private String activeProfile;


    public WebDriver getChromeDriver() throws MalformedURLException {
        if (activeProfile.equals("dev")) {

            // chrome driver setup
            WebDriverManager.chromedriver().setup();

            // chrom driver options
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless=new");

            return new ChromeDriver(chromeOptions);
        } else if (activeProfile.equals("deploy")) {
            ChromeOptions chromeOptions = new ChromeOptions();

            return new RemoteWebDriver(new URL("http://localhost:4444"), chromeOptions);
        }

        throw new RuntimeException("cannot create web driver");
    }
}
