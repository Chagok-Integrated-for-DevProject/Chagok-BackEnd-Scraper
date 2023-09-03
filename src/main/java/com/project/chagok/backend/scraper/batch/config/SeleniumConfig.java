package com.project.chagok.backend.scraper.batch.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    @Bean
    public WebDriver chromeWebDriver() {
        // chrome driver setup
        WebDriverManager.chromedriver().setup();

        // chrom driver options
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless=new");

        return new ChromeDriver(chromeOptions);
    }
}
