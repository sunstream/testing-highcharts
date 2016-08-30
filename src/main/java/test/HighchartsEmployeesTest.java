package test;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import test.BaseTest;
import ui.HighchartCrawler;

public class HighchartsEmployeesTest extends BaseTest {

    private static final String TEST_FIELD_NAME = "employees";

    private HighchartCrawler highchartPage;
    private String startUrl;

    @BeforeClass
    void setup() {
        super.setupTestData(TEST_FIELD_NAME);
        startUrl = config.getProperty("startUrl");
    }

    @Test
    public void testEmployeesChart() {
        driver.get(startUrl);
        highchartPage = new HighchartCrawler(driver);
        highchartPage.setDataReader(dataReader);
        highchartPage
                .openDemoHighchart()
                .verifyEmployeeChartTooltips();
    }
}