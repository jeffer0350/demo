package banrep.poc.steps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import banrep.poc.pageobjects.CreacionCreditosPageObjects;
import banrep.poc.utilidades.BaseDeDatos;
import banrep.poc.utilidades.FwEvidenciasSerenity;
import banrep.poc.utilidades.SQLDatabaseConnection;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;

public class CreacionCreditosSteps {

	@Steps
	CreacionCreditosPageObjects CreacionCreditos;

	@Step
	public static void traer_Casos_Crear_Creditos(int Caso) {
		CreacionCreditosPageObjects.traer_Casos_Crear_Creditos(Caso);
	}

	@Step
	public static void crear_El_Dia(int Caso) {
		CreacionCreditosPageObjects.crear_El_Dia(Caso);
	}

	@Step
	public static void ejecutar_Query_Apoyo(int Caso) {
		CreacionCreditosPageObjects.ejecutar_Query_Apoyo(Caso);
	}

	@Step
	public static void limpiar_Archivos_Directorios_Temporales(int Caso) {
		CreacionCreditosPageObjects.limpiar_Archivos_Directorios_Temporales(Caso);
	}

	@Step
	public static void crear_Archivo_Frech(int Caso) {
		CreacionCreditosPageObjects.crear_Archivo_Frech(Caso);
		tomarEvidencia("Crear el archivo Frech " + Caso + "", 1000);
	}

	@Step
	public static void abrir_Carpeta_Evidencia() {

	}

	@Step
	public static void lanzar_Bat_Frech(int Caso) {
		CreacionCreditosPageObjects.lanzar_Bat_Frech(Caso);
		tomarEvidencia("Lanzar el bat para ejecutar el proceso de Frech " + Caso + "", 1000);
	}

	@Step
	public static void revisar_Resultados(int Caso) throws SQLException {
		String link = "";
		link=CreacionCreditosPageObjects.revisar_Resultados(Caso);
		tomarEvidencia("Revisar los resultados del procesamiento del archivo " + Caso + "", 1000, link);
	}

	@Step
	public static void cerrar_Carpeta() {
		CreacionCreditosPageObjects.cerrar_Carpeta();
	}

	@Step
	public static void tomarEvidencia() {
		FwEvidenciasSerenity.tomarEvidencia();
	}

	@Step
	public void tomarEvidencia(String description) {
		FwEvidenciasSerenity.tomarEvidencia(description);
	}

	@Step
	public static void tomarEvidencia(int delay) {
		FwEvidenciasSerenity.tomarEvidencia(delay);
	}

	@Step
	public static void tomarEvidencia(String description, int delay) {
		FwEvidenciasSerenity.tomarEvidencia(description, delay);
	}

	@Step
	public static void tomarEvidencia(String description, int delay, String url) {
		FwEvidenciasSerenity.tomarEvidencia(description, delay, url);
	}

}
