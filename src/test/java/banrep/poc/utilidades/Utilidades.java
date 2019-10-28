package banrep.poc.utilidades;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.sql.Statement;
import java.sql.ResultSet;

public class Utilidades{
	
	public static StringBuilder queryStrDriven = new StringBuilder();
	public static Connection conexion;
	public static Statement st = null;
	public static ResultSet rs = null;
	
	public static String crearElDia() {
		
		try {
			
			SimpleDateFormat formateador = new SimpleDateFormat("yyyyMMdd");
			Date fechaDate = new Date(0);
			String strFecha = formateador.format(fechaDate);
			System.out.println(strFecha);
			//int intRecords = 0;
					
			//conexion = ActualizarCreditos.conexionFrechAutomatizacion();
			queryStrDriven.setLength(0);
			queryStrDriven.append("SELECT [fecha] FROM [Frech_Automatizacion].[Frech].[fechas_procesamiento] where fecha = '"+strFecha+"'");
			st = conexion.createStatement();
			rs = st.executeQuery(queryStrDriven.toString());
			
			while (rs.next()) {
				queryStrDriven.setLength(0);
				//Inserta la fecha en la tabla fechas_procesamiento
				queryStrDriven.append("INSERT INTO [Frech_Automatizacion].[Frech].[fechas_procesamiento] ([fecha]) VALUES ('"+strFecha+"')");
			}
			st.executeUpdate(queryStrDriven.toString());
			rs.close();
			
			
		} catch (Exception e) {
			
		}
		
		return null;
		
	}
	
}