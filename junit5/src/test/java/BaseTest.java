import com.github.angleshq.angles.AnglesReporterInterface;
import com.github.angleshq.angles.api.models.Platform;
import com.github.angleshq.angles.api.models.build.Artifact;
import com.github.angleshq.angles.api.models.screenshot.ImageCompareResponse;
import com.github.angleshq.angles.api.models.screenshot.Screenshot;
import com.github.angleshq.angles.api.models.screenshot.ScreenshotDetails;
import com.github.angleshq.angles.assertion.junit.AnglesJUnit5Assert;
import com.github.angleshq.angles.listeners.junit.AnglesJUnit5Extension;
import com.github.angleshq.angles.util.AnglesReporterUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(AnglesJUnit5Extension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    protected AnglesReporterInterface anglesReporter = AnglesReporterUtils.getAnglesReporter();
    protected Platform platform;
    protected RemoteWebDriver browser;
    protected AnglesJUnit5Assert doAssert = new AnglesJUnit5Assert();

    private static final Logger logger = LogManager.getLogger(BaseTest.class.getName());

    @BeforeAll
    public void beforeAll() {
        // store the details for the SUT, but you will need your own code to collect the details.
        List<Artifact> artifacts = new ArrayList<Artifact>();
        artifacts.add(new Artifact("com.github.anghleshq", "angles-testng-example", "1.0.0"));
        anglesReporter.storeArtifacts(artifacts.toArray(new Artifact[0]));
    }

    @BeforeEach
    public void setup() {
        // please ensure the right chromedriver is available in your PATH environment variable.
        anglesReporter.startAction("Setup Browser");
        logger.info("Launching Chrome");
        browser = new ChromeDriver();
        // resolution is set to ensure the screenshot is the same size
        browser.manage().window().setSize(new Dimension(1280, 976));

        //store platform details (optional, but useful)
        logger.info("Storing platform details with the test.");
        platform = new Platform();
        platform.setBrowserName(browser.getCapabilities().getBrowserName());
        platform.setPlatformName(browser.getCapabilities().getPlatform().name());
        platform.setBrowserVersion(browser.getCapabilities().getVersion());
        anglesReporter.storePlatformDetails(platform);
    }

    @AfterEach
    public void teardown() {
        anglesReporter.startAction("Teardown Browser");
        logger.info("Tearing down browser");
        browser.close();
    }

    /**
     * To compare against a baseline you will need to provide a screenshot id (of a screenshot that was stored in angles)
     * And you can then retrieve the ImageComparResponse and use that to do your own assert or as per the example below.
     *
     * @param screenshotId
     * @param maxMisMatch
     */
    protected void compareScreenshotAgainstBaseline(String screenshotId, double maxMisMatch) {
        ImageCompareResponse comparison = anglesReporter.compareScreenshotAgainstBaseline(screenshotId);
        if (comparison != null) {
            // baseline was set and we have a comparison
            if (comparison.getMisMatchPercentage() > 1) {
                anglesReporter.fail("Image Comparison", "Mismatch to be less than " + maxMisMatch, "Mismatch was greater than " + maxMisMatch, "Analysis Time: " + comparison.getAnalysisTime());
            } else {
                anglesReporter.pass("Image Comparison", "Mismatch to be less than " + maxMisMatch, "Mismatch was less than " + maxMisMatch, "Analysis Time: " + comparison.getAnalysisTime());
            }
        } else {
            // no baseline set yet.
            anglesReporter.info("No baseline found, please go into the Angles dashboard and set a baseline");
        }
    }

    /**
     * Simple take screenshot example
     * @param view name for the view you want to store (used for the baseline compare, along with platform details).
     * @param tags any tags you want stored with the screenshot, useful to find it.
     * @return
     */
    protected Screenshot takeScreenshot(String view, List<String> tags) {
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
}
