import com.github.angleshq.angles.api.models.screenshot.Screenshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class TestNGExampleTest extends BaseTest {

    private static final Logger logger = LogManager.getLogger(TestNGExampleTest.class.getName());

    @Test
    public void testNGExampleTest() {
        anglesReporter.startAction("Navigate to github page and check link");
        browser.get("https://angleshq.github.io/");
        WebElement href = browser.findElement(By.id("forkme_banner"));
        doAssert.assertEquals(href.getText(), "View on GitHub");

        // take a screenshot
        Screenshot screenshot = takeScreenshot("angles_github_page", null);
        // link the screenshot to a line in Angles (e.g. info, fail or error).
        anglesReporter.info("Took a screenshot of the github page", screenshot.getId());
        // set the baseline in the angles dashboard and then assert on the mismatch
        compareScreenshotAgainstBaseline(screenshot.getId(), 1);

    }

}
