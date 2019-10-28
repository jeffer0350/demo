package banrep.poc.definitions;

import banrep.poc.steps.CreacionCreditosSteps;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import net.thucydides.core.annotations.Steps;

public class CreacionCreditosDefinitions {
	@Steps
	CreacionCreditosSteps CreacionCreditos;
	
	@Given("^Traer Casos Crear Creditos (\\d+)$")
	public void traer_Casos_Crear_Creditos(int Caso) throws Exception {
		CreacionCreditosSteps.traer_Casos_Crear_Creditos(Caso);
	}

	@Given("^Crear el dia (\\d+)$")
	public void crear_el_dia(int Caso) throws Exception {
		CreacionCreditosSteps.crear_El_Dia(Caso);
	}

	@Given("^Ejecutar query de apoyo (\\d+)$")
	public void ejecutar_query_de_apoyo(int Caso) throws Exception {
		CreacionCreditosSteps.ejecutar_Query_Apoyo(Caso);
	}

	@Given("^Limpiar los archivos de los directorios temporales (\\d+)$")
	public void limpiar_los_archivos_de_los_directorios_temporales(int Caso) throws Exception {
		CreacionCreditosSteps.limpiar_Archivos_Directorios_Temporales(Caso);
	}

	@When("^Crear el archivo Frech (\\d+)$")
	public void crear_el_archivo_Frech(int Caso) throws Exception {
		CreacionCreditosSteps.crear_Archivo_Frech(Caso);
	}

	@When("^Abrir la carpeta para tomar evidencia$")
	public void abrir_la_carpeta_para_tomar_evidencia() throws Exception {
		CreacionCreditosSteps.abrir_Carpeta_Evidencia();
	}

	@When("^Lanzar el bat para ejecutar el proceso de Frech (\\d+)$")
	public void lanzar_el_bat_para_ejecutar_el_proceso_de_Frech(int Caso) throws Exception {
		CreacionCreditosSteps.lanzar_Bat_Frech(Caso);
	}

	@When("^Revisar los resultados del procesamiento del archivo (\\d+)$")
	public void revisar_los_resultados_del_procesamiento_del_archivo(int Caso) throws Exception {
		CreacionCreditosSteps.revisar_Resultados(Caso);
	}

	@Then("^Cerrar la carpeta de la evidencia tomada$")
	public void cerrar_la_carpeta_de_la_evidencia_tomada() throws Exception {
		CreacionCreditosSteps.cerrar_Carpeta();
	}
}
