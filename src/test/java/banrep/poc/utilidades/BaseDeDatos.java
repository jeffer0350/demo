package banrep.poc.utilidades;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * Clase encargada de conexión y consultas a base de datos.
 * @author Chuidiang
 *
 */
public class BaseDeDatos
{
    /** La conexion con la base de datos */
    private Connection conexion = null;

    /** Se establece la conexion con la base de datos */
    public void estableceConexion(String StrConeccion)
    {
        if (conexion != null)
            return;

        try
        {
        	conexion = DriverManager.getConnection(StrConeccion);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Realiza la consulta de personas en la tabla y devuelve el ResultSet
     * correspondiente.
     * @return El resultado de la consulta
     */
    public ResultSet dameListaPersonas(String sql)
    {
        ResultSet rs = null;
        try
        {
        	
			
        	// Se crea un Statement, para realizar la consulta
            Statement s = conexion.createStatement();
            rs = s.executeQuery(sql);	
           
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return rs;
    }
    

    /** Cierra la conexión con la base de datos */
    public void cierraConexion()
    {
        try
        {
            conexion.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	public void ejecutarSentenciaInsert(String insertSql) {

        try
        {
        	// Se crea un Statement, para realizar la consulta
            Statement s = conexion.createStatement();
            s.execute(insertSql);
           
        } catch (Exception e)
        {
            e.printStackTrace();
        }
	}
	
	public void ejecutarSentenciaUpdate(String updateSql) {

        try
        {
        	// Se crea un Statement, para realizar la consulta
            Statement s = conexion.createStatement();
            s.execute(updateSql);
           
        } catch (Exception e)
        {
            e.printStackTrace();
        }
	}
    
}
