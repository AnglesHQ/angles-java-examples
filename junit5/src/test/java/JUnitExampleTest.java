import com.github.angleshq.angles.api.models.screenshot.Screenshot;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class JUnitExampleTest extends BaseTest {


    @Test
    public void jUnitExampleTest() {
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
