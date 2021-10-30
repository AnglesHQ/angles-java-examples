package com.github.angleshq.angles;

import com.github.angleshq.angles.api.models.Platform;
import com.github.angleshq.angles.api.models.build.Artifact;
import com.github.angleshq.angles.api.models.screenshot.ImageCompareResponse;
import com.github.angleshq.angles.api.models.screenshot.Screenshot;
import com.github.angleshq.angles.api.models.screenshot.ScreenshotDetails;
import com.github.angleshq.angles.listeners.cucumber.AnglesCucumberAdapter;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExampleSteps extends AnglesCucumberAdapter {

    private RemoteWebDriver browser;
    private Platform platform;
    private final Logger logger = LogManager.getLogger(ExampleSteps.class.getName());

    @Before
    public void initilizeAnglesAdapter() {
        // this before method is required to trigger the Angles setup (even if empty)

        // store the details for the SUT, but you will need your own code to collect the details.
        List<Artifact> artifacts = new ArrayList<Artifact>();
        artifacts.add(new Artifact("com.github.anghleshq", "angles-cucumberjvm-example", "1.0.0"));
        anglesReporter.storeArtifacts(artifacts.toArray(new Artifact[0]));

        browser = new ChromeDriver();
        // resolution is set to ensure the screenshot is the same size
        browser.manage().window().setSize(new Dimension(1280, 976));

        //store platform details (optional, but useful)
        platform = new Platform();
        platform.setBrowserName(browser.getCapabilities().getBrowserName());
        platform.setPlatformName(browser.getCapabilities().getPlatform().name());
        platform.setBrowserVersion(browser.getCapabilities().getVersion());
        anglesReporter.storePlatformDetails(platform);
    }

    @Given("user navigates to github page by opening Chrome")
    public void user_navigates_to_github_page_by_opening_chrome()
    {
        logger.info("Navigating to github page");
        browser.get("https://angleshq.github.io");
    }

    @When("and the user waits for the github link to appear")
    public void user_waits_for_github_link()
    {
        logger.info("Checking for github link");
        WebDriverWait wait = new WebDriverWait(browser, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("forkme_banner")));
     }

    @Then("^the visual mismatch for view (.*) should not exceed (.*) percent$")
    public void direct_to_homepage(String view, Double maxMisMatch) throws Throwable
    {
        Screenshot screenshot = takeScreenshot(view, null);
        anglesReporter.info("Took a screenshot for view [" + view + "]", screenshot.getId());
        compareScreenshotAgainstBaseline(screenshot.getId(), maxMisMatch);
    }

    @After()
    public void closeBrowser()
    {
        browser.quit();
    }

    private Screenshot takeScreenshot(String view, List<String> tags) {
        File getImage = ((TakesScreenshot)browser).getScreenshotAs(OutputType.FILE);
        try {
            String screenShotPath = "target/" + view + "_" + UUID.randomUUID() + "screenshot.jpg";
            FileUtils.copyFile(getImage, new File(screenShotPath));
            ScreenshotDetails details = new ScreenshotDetails();
            details.setPath(screenShotPath);
            details.setView(view);
            if (tags != null)
                details.setTags(tags);
            if (platform != null)
                details.setPlatform(platform);
            return anglesReporter.storeScreenshot(details);
        } catch (IOException e) {
            anglesReporter.error(e.getMessage());
        }
        return null;
    }

    protected void compareScreenshotAgainstBaseline(String screenshotId, double maxMisMatch) throws Exception {
        ImageCompareResponse comparison = anglesReporter.compareScreenshotAgainstBaseline(screenshotId);
        if (comparison != null) {
            // baseline was set and we have a comparison
            anglesReporter.info("Comparing screenshot against baseline and checking if misMatch [" + comparison.getMisMatchPercentage() + "] is greater than acceptable mismatch [" + maxMisMatch + "]");
            doAssert.assertGreaterThan(maxMisMatch, comparison.getMisMatchPercentage());
        } else {
            // no baseline set yet.
            anglesReporter.info("No baseline found, please go into the Angles dashboard and set a baseline");
        }
    }
}
