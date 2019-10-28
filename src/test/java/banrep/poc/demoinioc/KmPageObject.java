package banrep.poc.demoinioc;

import com.borland.silktest.jtf.BrowserBaseState;
import com.borland.silktest.jtf.Desktop;
import com.borland.silktest.jtf.xbrowser.DomTextField;

import net.serenitybdd.core.Serenity;

import com.borland.silktest.jtf.common.BrowserType;
import com.borland.silktest.jtf.common.types.MouseButton;
import com.borland.silktest.jtf.common.types.Point;
import com.borland.silktest.jtf.xbrowser.DomButton;

public class KmPageObject{
	public BrowserBaseState baseState = new BrowserBaseState();
	public static Desktop desktop = new Desktop();

	public void tener_acceso_a_la_plataforma() {
	
		// TODO Auto-generated method stub
		baseState.setUrl("http://km.choucairtesting.com/login/index.php");	
		baseState.setBrowserType(BrowserType.GoogleChrome);
		baseState.execute(desktop);	
		Serenity.takeScreenshot();
		
	}

	public static void tener_Usuario_activo() {
		// TODO Auto-generated method stub
		//start recording 
				desktop.<DomTextField>find("//BrowserApplication//BrowserWindow//input[@id='username']").typeKeys("ocastro");
				desktop.<DomTextField>find("//BrowserApplication//BrowserWindow//input[@id='password']").setText("bogota2016");
				desktop.<DomButton>find("//BrowserApplication//BrowserWindow//input[@id='loginbtn']").click();
				//end recording
				Serenity.takeScreenshot();
	}

	public void ingresar_a_la_Opcion_de_eventos() {
		// TODO Auto-generated method stub
		
	}

	public void crear_un_evento_recurrente_para_el_dia_de_hoy() {
		// TODO Auto-generated method stub
		
	}

	public void verificar_que_se_halla_creado() {
		// TODO Auto-generated method stub
		
	}

}
