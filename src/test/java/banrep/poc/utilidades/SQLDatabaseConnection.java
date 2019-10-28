package banrep.poc.utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class SQLDatabaseConnection {
	/*
	 * Jefferson Triana 23-11-2018 Conectar a Base de datos
	 */

	public static Statement ConectarBD(String StrConeccion, String sql) {
		Statement stmt = null;
		ResultSet rs = null;
		DefaultTableModel modelo = new DefaultTableModel();
		
		JTable tabla = new JTable(modelo);
		try (Connection connection = DriverManager.getConnection(StrConeccion);) {

			if (connection != null) {
				System.out.println("Conexión correcta");
				stmt = connection.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					Object[] fila = new Object[120];
					for(int i=0; i<120;i++){
						fila[i] = rs.getObject(i+1);
						}
						//se añade al modelo la fila completa
						modelo.addRow(fila);
					System.out.println(rs.getString("DESCRIPCION_ACIERTO"));
				}
			} else {
				System.out.println("Conexión incorrecta");
			}
		}
		
		

		catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			ResultSetMetaData metaDatos = rs.getMetaData();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		return stmt;
	}

	public static void close(Statement stmt) {
		// TODO Auto-generated method stub
		try {
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}