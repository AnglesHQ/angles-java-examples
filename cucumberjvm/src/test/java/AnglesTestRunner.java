
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)

@CucumberOptions(
    plugin = { "com.github.angleshq.angles.listeners.cucumber.AnglesCucumberAdapter", "pretty"},
    features = "src/test/resources/angles_example.feature",
    glue={"com.github.angleshq.angles"}
)

public class AnglesTestRunner {
}
