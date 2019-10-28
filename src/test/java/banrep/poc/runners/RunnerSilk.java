package banrep.poc.runners;

import org.junit.runner.RunWith;

import banrep.poc.utilidades.BeforeSuite;
import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;

@CucumberOptions(features = "src/test/resources/features/CreacionCreditos.feature", glue="banrep.poc.definitions")
@RunWith(CucumberWithSerenity.class)

public class RunnerSilk {
	@BeforeSuite
	public static void test(){
	System.out.print("entra");
	}
}