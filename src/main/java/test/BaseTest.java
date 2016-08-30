package test;

import core.TestDataReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BaseTest {
    private static final String CONFIG_FILE_NAME = "config.properties";
    protected Properties config = new Properties();
    protected WebDriver driver;
    protected TestDataReader dataReader;

    @BeforeClass
    void setupDriver(){
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    }

    @BeforeClass
    void setupConfig() {
        FileInputStream propFile;
        try {
            propFile = new FileInputStream(CONFIG_FILE_NAME);
            config.load(propFile);
        } catch (IOException e) {
            throw new Error("Failed to read configuration properties file '" + CONFIG_FILE_NAME+"': "+e.getMessage(), e);
        }
    }

    @AfterClass(alwaysRun = true)
    void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }

    void setupTestData(String testFieldName) {
        String testDataFileName = config.getProperty("testDataFile");
        dataReader = new TestDataReader(testDataFileName);
        dataReader.setDataFieldName(testFieldName);
    }
}
