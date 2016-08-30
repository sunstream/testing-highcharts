package ui;

import core.TestDataReader;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighchartCrawler {

    private static final By HIGHCHART_IFRAME_LOCATOR = By.cssSelector("iframe[src*='combo-timeline']");
    private static final String CHART_COLOR = "rgba(144,237,125,0.5)";
    private static final By TOOLTIP_LOCATOR = By.cssSelector("g.highcharts-tooltip");
    private static final By TOOLTIP_TEXT_LINE_LOCATOR = By.cssSelector("text tspan");
    private static final By CHART_PATH_LOCATOR = By.cssSelector("path[fill='" + CHART_COLOR + "']");

    private static final Pattern POINT_PATTERN = Pattern.compile("[L]*\\s+([0-9\\.]+)\\s+([0-9\\.]+)");

    private List<Point> pointCoordinatesAsOffsets = new ArrayList<Point>();

    private TestDataReader testDataReader;
    private WebDriver driver;
    private Actions actionBuilder;
    private WebElement employeesChartPath, tooltip;

    public HighchartCrawler(WebDriver driver){
        this.driver = driver;
        actionBuilder = new Actions(driver);
    }

    public void setDataReader(TestDataReader dataReader) {
        testDataReader = dataReader;
    }

    public HighchartCrawler openDemoHighchart(){
        String highchartUrl = driver.findElement(HIGHCHART_IFRAME_LOCATOR).getAttribute("src");
        driver.get(highchartUrl);
        return this;
    }

    public HighchartCrawler verifyEmployeeChartTooltips() {
        getEmployeesChartPoints();
        for (int i = 0; i< pointCoordinatesAsOffsets.size(); i++) {
            Point chartPoint = pointCoordinatesAsOffsets.get(i);
            System.out.println("Point #"+i);
            System.out.println("Coordinates: ("+chartPoint.getX() + ", "+chartPoint.getY() + ")");
            getTooltipForEmployeesChartPoint(chartPoint);
            System.out.println(tooltip.getText());
            verifyTooltipText(i);
        }
        return this;
    }

    private void verifyTooltipText(int index) {
        String[] expectedTooltipLines = testDataReader.getDataFieldValuesByIndex(index + 1);
        List<WebElement> tooltipTextElements = tooltip.findElements(TOOLTIP_TEXT_LINE_LOCATOR);
        List<String> actualTooltipLines = new ArrayList<String>();
        for (WebElement tooltipTextElement : tooltipTextElements) {
            if (!tooltipTextElement.getText().trim().isEmpty()) {
                actualTooltipLines.add(tooltipTextElement.getText().trim());
            }
        }
        Assert.assertEquals(actualTooltipLines.size(), expectedTooltipLines.length, "Invalid tooltip lines count found at point #"+index);
        for (int i = 0; i < expectedTooltipLines.length; i++) {
            String actualTooltipLine = actualTooltipLines.get(i);
            Assert.assertEquals(actualTooltipLine, expectedTooltipLines[i],
                    "Tooltip text mismatch was found at point #"+i+1);
        }
    }

    private void getTooltipForEmployeesChartPoint(Point p) {
        int xOffset = p.getX();
        actionBuilder
                .moveToElement(employeesChartPath, p.getX(), p.getY())
                .build()
                .perform();
        tooltip = driver.findElement(TOOLTIP_LOCATOR);
        while (!tooltip.getText().contains("employee")) {
            xOffset +=5;
            actionBuilder.moveToElement(employeesChartPath, xOffset, p.getY()).build().perform();
            tooltip = driver.findElement(TOOLTIP_LOCATOR);
        }
    }

    private void getEmployeesChartPoints(){
        int minX, minY, maxX, maxY;
        Point currentPoint;

        employeesChartPath = driver.findElement(CHART_PATH_LOCATOR);
        String chartCoordinates = employeesChartPath.getAttribute("d");
        List <Point> pointCoordinatesAsByPath = new ArrayList<Point>();
        Matcher m = POINT_PATTERN.matcher(chartCoordinates);

        if (m.find()) {
            currentPoint = getCoordinatesFromRegExMatch(m);
            minX = maxX = currentPoint.getX();
            minY = maxY = currentPoint.getY();
            pointCoordinatesAsByPath.add(currentPoint);
        } else {
            throw new Error("Cannot proceed: no coordinates were extracted from highchart path attributes. " +
                    "Please fix RegExp or check fetched attribute value.");
        }
        while (m.find()) {
            Point nextPoint = getCoordinatesFromRegExMatch(m);
            if (nextPoint.getX() != currentPoint.getX() && nextPoint.getY() != currentPoint.getY()) {
                currentPoint = nextPoint;
                if (currentPoint.getX() < minX) minX = currentPoint.getX();
                if (currentPoint.getY() < minY) minY = currentPoint.getY();
                if (currentPoint.getX() > maxX) maxX = currentPoint.getX();
                if (currentPoint.getY() > maxY) maxY = currentPoint.getY();
                pointCoordinatesAsByPath.add(currentPoint);
            }
        }

        int xScalingCoefficient = employeesChartPath.getSize().getWidth() / (maxX - minX);
        int yScalingCoefficient = employeesChartPath.getSize().getHeight() / (maxY - minY);

        for (Point p : pointCoordinatesAsByPath) {
            p = new Point(  xScalingCoefficient * (p.getX() - minX),
                            yScalingCoefficient * (p.getY() - minY));
            pointCoordinatesAsOffsets.add(p);
        }
    }

    private Point getCoordinatesFromRegExMatch(Matcher m) {
        return new Point(getCoordinateFromString(m.group(1)), getCoordinateFromString(m.group(2)));
    }

    private int getCoordinateFromString(String coordinateStr){
        return Math.round(Float.parseFloat(coordinateStr));
    }

}
