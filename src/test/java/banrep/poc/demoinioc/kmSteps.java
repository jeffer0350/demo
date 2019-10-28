package banrep.poc.demoinioc;


import net.thucydides.core.annotations.Step;


public class kmSteps {

	KmPageObject obj=new KmPageObject();
	@Step
	public void tener_acceso_a_la_plataforma() {
		obj.tener_acceso_a_la_plataforma();
		//tomarEvidencia();
	}
	@Step
	public void tener_Usuario_activo() {
		KmPageObject.tener_Usuario_activo();
		
	}
	@Step
	public void ingresar_a_la_Opcion_de_eventos() {
		//KmPageObject.ingresar_a_la_Opcion_de_eventos();
		
	}
	@Step
	public void crear_un_evento_recurrente_para_el_dia_de_hoy() {
		//KmPageObject.crear_un_evento_recurrente_para_el_dia_de_hoy();
		
	}
	@Step
	public void verificar_que_se_halla_creado() {
		//KmPageObject.verificar_que_se_halla_creado();
		
	}
	@Step
	public void tomarEvidencia() { 
		//FW_EvidenciasSerenity.tomarEvidencia();
		
	}
	/*
	@Step
	public void tomarEvidencia(String description) {FW_EvidenciasSerenity.tomarEvidencia(description);}
	
	@Step
	public void tomarEvidencia(int delay) {FW_EvidenciasSerenity.tomarEvidencia(delay);}
	
	@Step
	public void tomarEvidencia(String description, int delay) {FW_EvidenciasSerenity.tomarEvidencia(description,delay);}
*/
}
