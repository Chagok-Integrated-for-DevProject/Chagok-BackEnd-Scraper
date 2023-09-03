package com.project.chagok.backend.scraper.batch.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class SeleniumConfig {

    @Bean
    public WebDriver chromeWebDriver() throws MalformedURLException {

        ChromeOptions chromeOptions = new ChromeOptions();
        WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444"), chromeOptions);
        return driver;
    }
}
