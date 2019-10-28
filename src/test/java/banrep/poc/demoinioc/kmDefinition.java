package banrep.poc.demoinioc;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;

public class kmDefinition {

	@Steps
	kmSteps kmSteps;
	
	@Given("^Tener acceso a la plataforma$")
	public void tener_acceso_a_la_plataforma() throws Exception {
	    // Write code here that turns the phrase above into concrete actions
	    kmSteps.tener_acceso_a_la_plataforma();
	}


	@Given("^Tener Usuario activo$")
	public void tener_Usuario_activo() throws Exception {
	    // Write code here that turns the phrase above into concrete actions
	    kmSteps.tener_Usuario_activo();
	}

	@When("^Ingresar a la Opcion de eventos$")
	public void ingresar_a_la_Opcion_de_eventos() throws Exception {
	    // Write code here that turns the phrase above into concrete actions
	    kmSteps.ingresar_a_la_Opcion_de_eventos();
	}

	@When("^Crear un evento recurrente para el dia de hoy$")
	public void crear_un_evento_recurrente_para_el_dia_de_hoy() throws Exception {
	    // Write code here that turns the phrase above into concrete actions
	    kmSteps.crear_un_evento_recurrente_para_el_dia_de_hoy();
	}

	@Then("^Verificar que se halla creado$")
	public void verificar_que_se_halla_creado() throws Exception {
	    // Write code here that turns the phrase above into concrete actions
	    kmSteps.verificar_que_se_halla_creado();
	}
}
