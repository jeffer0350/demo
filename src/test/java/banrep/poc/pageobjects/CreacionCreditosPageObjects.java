package banrep.poc.pageobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import banrep.poc.utilidades.BaseDeDatos;
import banrep.poc.utilidades.ConversorResultSetADefaultTableModel;
import banrep.poc.utilidades.FwEvidenciasSerenity;
import banrep.poc.utilidades.SQLDatabaseConnection;
import net.thucydides.core.annotations.Step;

import org.apache.tools.ant.taskdefs.Javadoc.Html;
import org.joda.time.DateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CreacionCreditosPageObjects {

	private static BaseDeDatos bd;
	/** Clase donde se guardan los datos leidos de base de datos */
	private static DefaultTableModel modelo;

	static SQLDatabaseConnection objconection = new SQLDatabaseConnection();
	public static Statement stmt = null;
	public static ResultSet rs = null;
	static String strSearch = "<SEARCH>";

	public static void traer_Casos_Crear_Creditos(int Caso) {
		try {
			// Se instancian las clases necesarias
			bd = new BaseDeDatos();
			modelo = new DefaultTableModel();

			bd.estableceConexion(
					"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
			ResultSet rs = bd.dameListaPersonas(
					"SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ESTADO_CASO = 'Activo' and ID_CASO='" + Caso
							+ "'");
			ConversorResultSetADefaultTableModel.rellena(rs, modelo);
			Thread.sleep(1000);
			bd.cierraConexion();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int cols = modelo.getColumnCount();
		int fils = modelo.getRowCount();
		for (int i = 0; i < fils; i++) {
			for (int j = 0; j < cols; j++)
				System.out.print(modelo.getValueAt(i, j));
			System.out.println();
		}
	}

	public static void crear_El_Dia(int Caso) {

		try {
			bd = new BaseDeDatos();
			bd.estableceConexion(
					"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
			ResultSet rs = bd.dameListaPersonas(
					"SELECT FRECH_NUM FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ESTADO_CASO = 'Activo'  and ID_CASO='"
							+ Caso + "'");
			while (rs.next()) {
				String FrechType = rs.getString("FRECH_NUM");

				LocalDate date = LocalDate.now();
				DateTimeFormatter strFecha = DateTimeFormatter.ofPattern("yyyyLLdd");
				String formattedStrFecha = date.format(strFecha);

				String selectSql = "SELECT [fecha] FROM [Frech_Automatizacion].[" + GetFrechSchema(FrechType)
						+ "].[fechas_procesamiento] where fecha = '" + formattedStrFecha + "'";

				bd.estableceConexion(
						"jdbc:sqlserver://WDESSQL20141D;database=Frech_Automatizacion;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
				ResultSet rs1 = bd.dameListaPersonas(selectSql);

				if (!rs1.next()) {
					String insertSql = "INSERT INTO [Frech_Automatizacion].[" + GetFrechSchema(FrechType)
							+ "].[fechas_procesamiento] ([fecha]) VALUES ('" + formattedStrFecha + "')";
					bd.ejecutarSentenciaInsert(insertSql);
				}
			}
			bd.cierraConexion();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String GetFrechSchema(String strFrechNumber) {

		String sFrechSchema = "";
		switch (strFrechNumber.toLowerCase().toString()) {
		case "1":
			sFrechSchema = "Frech";
			break;
		case "2":
			sFrechSchema = "Frech2";
			break;
		case "3":
			sFrechSchema = "Frech3";
			break;
		case "4":
			sFrechSchema = "Frech4";
			break;
		case "5":
			sFrechSchema = "Frech5";
			break;
		case "6":
			sFrechSchema = "FRECH6";
			break;
		default:
			sFrechSchema = "ERROR";
			break;
		}
		return sFrechSchema;
	}

	public static void ejecutar_Query_Apoyo(int Caso) {

		try {
			bd = new BaseDeDatos();
			bd.estableceConexion(
					"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
			ResultSet rs = bd.dameListaPersonas(
					"SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ESTADO_CASO = 'Activo' and ID_CASO = '"
							+ Caso + "'");
			int intUtilizado = 0;

			String strQuery = null;
			// String strIdCaso = null;
			String strDataRestore = null;
			String strValueFromDB = null;

			while (rs.next()) {
				strQuery = rs.getString("PRE_QUERY");
				strDataRestore = rs.getString("DATA_TO_RESTORE");
				strValueFromDB = rs.getString("REEMPLAZO_QUERY").toLowerCase().toString();
				intUtilizado = rs.getInt("UTILIZADO");

				if (intUtilizado == 0) {
					if (strDataRestore != "<IGNORE>") {
						if (strDataRestore == "<SET>") {
							strQuery = strQuery.replace("<REEMPLAZAR>", strValueFromDB).replace(",", ".").toString();
						} else {
							String[] strDataToRestoreArray;
							String strOriginalValue = null;
							String strUpdateQuery;
							strDataToRestoreArray = strDataRestore.split("\\|");

							for (String arreglo : strDataToRestoreArray) {
								if (arreglo.contains("GET")) {
									ResultSet rs1 = bd.dameListaPersonas(strDataToRestoreArray[0]);
									while (rs1.next()) {
										strOriginalValue = rs1.getString("numero_elegibles");
									}

									strUpdateQuery = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D SET REEMPLAZO_QUERY = "
											+ strOriginalValue + " WHERE ID_CASO = " + Caso + "";
									bd.ejecutarSentenciaUpdate(strUpdateQuery);
								}
							}
						}
					}
					String strUpdateQuery = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D SET UTILIZADO = '-1' WHERE ID_CASO = "
							+ Caso + "";
					bd.ejecutarSentenciaUpdate(strUpdateQuery);
				}
				String strUpdateQuery = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D SET UTILIZADO = '0' WHERE ID_CASO = "
						+ Caso + "";
				bd.ejecutarSentenciaUpdate(strUpdateQuery);
			}

			bd.cierraConexion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void limpiar_Archivos_Directorios_Temporales(int Caso) {
		try {
			int intUtilizado = 0;
			bd = new BaseDeDatos();
			bd.estableceConexion(
					"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
			ResultSet rs = bd.dameListaPersonas(
					"SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ESTADO_CASO = 'Activo'and ID_CASO='" + Caso
							+ "'");
			while (rs.next()) {
				intUtilizado = rs.getInt("UTILIZADO");
				String strInfolder = rs.getString("INFOLDER");
				String strTempIn = strInfolder + "\\" + "tempIn";
				funcion_Eliminar_Archivos(new File(strInfolder));
				funcion_Eliminar_Archivos(new File(strTempIn));

				if (intUtilizado == 0) {
					String strUpdateQuery = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D "
							+ "SET [RESULTADO_BD_FRECH] ='',	" + "[NOMBRE_ARCHIVO] = '' ," + "[OUTPUT_FILE] = '' ,"
							+ "[RESPUESTA_ENCONTRADA] = 'False' ," + "[HORA_PROCESAMIENTO] = '' ,"
							+ "[VALUES_SAVED] = '' ," + "[FIELDS_TO_SAVE] = '' " + "WHERE ID_CASO = " + Caso + "";
					bd.ejecutarSentenciaUpdate(strUpdateQuery);
					String strUpdateQuery1 = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D SET UTILIZADO = '-1' WHERE ID_CASO = "
							+ Caso + "";
					bd.ejecutarSentenciaUpdate(strUpdateQuery1);
				}
				String strUpdateQuery = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D SET UTILIZADO = '0' WHERE ID_CASO = "
						+ Caso + "";
				bd.ejecutarSentenciaUpdate(strUpdateQuery);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void funcion_Eliminar_Archivos(File pArchivo) {
		if (!pArchivo.exists()) {
			return;
		}
		if (pArchivo.isDirectory()) {
			for (File f : pArchivo.listFiles()) {
				f.delete();
			}
		}
	}

	public static void crear_Archivo_Frech(int Caso) {

		String strNitEntidad;
		String strNombreArchIndicador;
		String strNombreArchFormato;
		String strNombreArchCodSebra;
		String strNombreArchAnio_Mes;
		String strValidaEstructura;
		String strTotalRegistros;
		int intTotalRegistros;
		int intUtilizado = 0;
		String StrHeader = "";
		String StrBody = "";
		String strFileName;
		String strFormato;
		String strFrechNo;
		String strPath;
		String[] strHeaderFields = null;
		String[] strBodyFields = null;
		// String strIdCaso;
		String strSearchFields = "";
		String strSearchValues = "";

		try {
			bd = new BaseDeDatos();
			bd.estableceConexion(
					"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
			ResultSet rs = bd.dameListaPersonas(
					"SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ESTADO_CASO = 'Activo' and ID_CASO = '"
							+ Caso + "'" + " AND UTILIZADO = '0'");
			while (rs.next()) {
				intUtilizado = rs.getInt("UTILIZADO");
				strFormato = rs.getString("FORMATO").toString();
				strFrechNo = rs.getString("FRECH_NUM").toString();
				strPath = rs.getString("INFOLDER").toString();
				strNombreArchIndicador = rs.getString("NOMBRE_ARCH_INDICADOR").toString();
				strNombreArchFormato = rs.getString("NOMBRE_ARCH_FORMATO").toString();
				strNombreArchCodSebra = rs.getString("NOMBRE_ARCH_COD_SEBRA").toString();
				strNombreArchAnio_Mes = rs.getString("NOMBRE_ARCH_AÑO_MES").toString();
				strNitEntidad = rs.getString("NIT_ESTABLECIMIENTO").toString();
				strValidaEstructura = rs.getString("VALIDAESTRUCTURA").toString();
				strTotalRegistros = rs.getString("TOTAL_REGISTROS");
				intTotalRegistros = Integer.parseInt(strTotalRegistros);

				ResultSet rs1 = bd.dameListaPersonas("SELECT * FROM TBL_ARCHIVOS_FRECH " + "WHERE FRECH_NUM = '"
						+ strFrechNo.trim() + "' AND FORMATO = '" + strFormato.trim() + "'");

				while (rs1.next()) {
					String headerCampos = rs1.getString("HEADER_CAMPOS").toString();
					String campos = rs1.getString("CAMPOS").toString();
					String campos2 = rs1.getString("CAMPOS2").toString();

					strHeaderFields = headerCampos.split("\\|");
					strBodyFields = campos.split("\\|");
					campos2.split("\\|");
				}
				strFileName = ObtenerIndicadorArchivo(strFrechNo, strNombreArchIndicador) + ""
						+ ObtenerFormato(strFormato, strNombreArchFormato) + ""
						+ ObtenerCodigoSebra(strNitEntidad, strFrechNo, strNombreArchCodSebra) + ""
						+ ObtenerAnio_Mes(strNombreArchAnio_Mes, strFormato, Caso);
				String strSeparador = "\\";
				strPath = strPath + "" + strSeparador + "" + strFileName;

				for (String HeadF : strHeaderFields) {

					String strSQL = "SELECT " + HeadF + " FROM dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = "
							+ Caso + "" + " AND UTILIZADO = '0' AND ESTADO_CASO = 'Activo'";
					ResultSet rs2 = bd.dameListaPersonas(strSQL);
					while (rs2.next()) {
						String Value = rs2.getString(HeadF);

						if (Value.equals(strSearch)) {
							switch (HeadF.trim()) {
							case "FECHA_CORTE_LIQUIDACION_CRED":
								Value = ObtenerFechaLiquidHead(Caso);
								break;
							case "SUM_CAMPO_COBERTURA_T":
								Value = ObtenerSumCampoCobertura(strFrechNo, Caso);
								break;
							default:
								Value = "ERROR";
							}
							strSearchFields = strSearchFields + "" + HeadF.trim() + "" + "|";
							strSearchValues = strSearchValues + "" + Value.toString().trim() + "" + "|";
						}
						StrHeader = StrHeader + "" + Value + "" + ";";
					}
				}
				StrHeader = StrHeader.substring(0, StrHeader.length() - 1);

				for (String strBodyF : strBodyFields) {

					String strSQL = "SELECT " + strBodyF + " FROM dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = "
							+ Caso + "" + " AND UTILIZADO = '0' AND ESTADO_CASO = 'Activo'";

					ResultSet rs2 = bd.dameListaPersonas(strSQL);
					while (rs2.next()) {
						String Value = "";
						Value = rs2.getString(strBodyF);

						if (Value.equals(strSearch)) {
							switch (strBodyF.trim()) {
							case "NO_CIFIN":
								Value = ObtenerNuevoCifin();
								break;
							case "ID_DEUDOR_PPAL":
								Value = ObtenerNuevoIdDeudor();
								break;
							case "NUMERO_CREDITO":
								Value = ObtenerNuevoNoCredito();
								break;
							case "APELLIDOS_DEUDOR_PPAL":
								Value = ObtenerNuevosApellidos();
								break;
							case "NOMBRES_DEUDOR_PPAL":
								Value = ObtenerNuevosNombres();
								break;
							case "NO_CIFIN_CREDITO_CEDIDO":
								Value = ObtenerCifinCedido(Caso);
								break;
							case "NO_CREDITO_CEDIDO":
								Value = ObtenerNoCreditoCedido(strFrechNo, Caso);
								break;
							case "VALOR_VENTA_PESOS_CREDITO_CEDIDO":
								Value = ObtenerValorCOPCredito(Caso);
								break;
							case "FECHA_CORTE_LIQUID_CREDITO":
								Value = ObtenerFechaLiquidCredito(Caso);
								break;
							case "SALDO_CAPITAL_VIGENTE_NO_VENCIDO":
								Value = ObtenerSaldoCapVigente(Caso);
								break;
							case "TASA_COBERTURA_CREDITO_CEDIDO":
								Value = ObtenerTasaCoberturaCredCedido(Caso);
								break;
							case "VALOR_INMUEBLE_SMMLV_CREDITO_CEDIDO":
								Value = ObtenerValorSMMLVCredito(Caso);
								break;
							case "VALOR_DE_VENTA_INMUEBLE_EN_SMMLV":
								Value = ObtenerValorVentaInmuebleSMMLV(strFrechNo, Caso);
								break;
							case "FECHA_TRANSMISION_FRECH_INFO_CAMPO_CIFIN":
								Value = ObtenerFechaTransmisionCifin();
								break;
							case "FECHA_DESEMBOLSO_CREDITO_CEDIDO":
								Value = ObtenerFechaDesembCredCedido(Caso);
								break;
							case "NO_CIFIN_NOV":
								Value = ObtenerNuevoCifin();
								break;
							case "NO_CIFIN_INICIAL":
								Value = ObtenerCifinInicial(Caso);
								break;
							case "DENOM_CREDITO_CEDIDO":
								Value = ObtenerDenomCredCedido(Caso);
								break;
							case "SISTEMA_AMORTIZACION_CREDITO_CEDIDO":
								Value = ObtenerSistAmortCredCedido(Caso);
								break;
							case "MONTO_CREDITO_CEDIDO_UVR":
								Value = ObtenerMontoCredCedidoUVR(Caso);
								break;
							case "PLAZO_CREDITO_CEDIDO_MESES":
								Value = ObtenerPlazoCreditoMeses(Caso);
								break;
							case "DEST_COBERTURA_CREDITO_CEDIDO":
								Value = ObtenerDestCoberturaCredCedido(Caso);
								break;
							case "MONTO_CREDITO_CEDIDO_PESOS":
								Value = ObtenerMontoCredCedidoCOP(Caso);
								break;
							case "INTERES_T":
								Value = ObtenerInteresT(strFrechNo, Caso);
								break;
							case "COBERTURA_T":
								Value = ObtenerCobertura(strFrechNo, Caso);
								break;
							case "MONTO_CREDITO":
								Value = ObtenerMontoCredito(strFrechNo, Caso);
								break;
							case "COBERTURA_CREDITO_PERIODO_LIQ":
								Value = ObtenerCobertCredPeriodoLiquidado(strFrechNo, Caso);
								break;
							default:
								Value = "ERROR";
							}
							strSearchFields = strSearchFields + "" + strBodyF.trim() + "" + "|";
							strSearchValues = strSearchValues + "" + Value.toString().trim() + "" + "|";
						}
						StrBody = StrBody + "" + Value + "" + ";";
					}
				}
				StrBody = StrBody.substring(0, StrBody.length() - 1);
				if (strValidaEstructura != "<IGNORE>") {
					StrBody = StrBody + "" + ";";
				}
				if (strSearchFields.contains("|")) {
					strSearchFields = strSearchFields.substring(0, strSearchFields.length() - 1);
					strSearchValues = strSearchValues.substring(0, strSearchValues.length() - 1);
				}
				String strUpQuery = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D " + "SET NOMBRE_ARCHIVO = '"
						+ strFileName.trim() + "', " + "FIELDS_TO_SAVE = '" + strSearchFields.trim() + "', "
						+ "VALUES_SAVED = '" + strSearchValues.trim() + "' " + "WHERE ID_CASO = " + Caso + "";
				bd.ejecutarSentenciaUpdate(strUpQuery);

				try {
					String ruta1 = strPath;
					String strH = StrHeader;
					String strB = StrBody;

					File file = new File(ruta1);

					// Si el archivo no existe, es creado
					if (file.exists()) {
						file.delete();
						file.createNewFile();
					} else {
						if (!file.exists()) {
							file.createNewFile();
						}
					}

					FileOutputStream fos = new FileOutputStream(file);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
					for (int i = 0; i < intTotalRegistros - 1; i++) {
						bw.write(strH);
						bw.newLine();
						bw.write(strB);
						Runtime app = Runtime.getRuntime();
						app.exec("cmd.exe /c start /max notepad " + ruta1);
						String timeStamp = new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date());
						String strUpQuery1 = "";
						strUpQuery1 = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D " + "SET HORA = '"
								+ timeStamp.toString() + "' " + "WHERE ID_CASO = " + Caso + " ";
						bd.ejecutarSentenciaUpdate(strUpQuery1);
					}
					bw.close();
					StrHeader = "";
					StrBody = "";
					strSearchFields = "";
					strSearchValues = "";

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void abrir_Carpeta_Evidencia() {

	}

	public static void lanzar_Bat_Frech(int Caso) {
		String strUpQuery = "";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date());
		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd.dameListaPersonas(
				"SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ESTADO_CASO = 'Activo' and ID_CASO = '" + Caso
						+ "'");
		try {
			while (rs.next()) {
				String strInfolder = rs.getString("INFOLDER");

				strUpQuery = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D " + "SET HORA_PROCESAMIENTO = '"
						+ timeStamp.toString() + "' " + "WHERE ID_CASO = " + Caso + " ";
				bd.ejecutarSentenciaUpdate(strUpQuery);
				Runtime app = Runtime.getRuntime();
				try {
					strInfolder = strInfolder.substring(0, strInfolder.length() - 3);
					String ruta = strInfolder + "\\" + "run.bat";
					ruta = ruta.replace("\\", "/");
					app.exec("cmd.exe /C start " + ruta);
					app.exec("cmd /c taskkill /f /im cmd.exe ");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String revisar_Resultados(int Caso) {
		String strqueryString;
		String strFecha[];
		String strPart1;
		String strPart2;
		String strFechaA;
		String strFechaArchivo;
		String strFechaProcesamiento;
		String strFrechInPath;
		String strNombreArchivo;
		String strResultadoEsperado;
		String Bd_Resultados;
		String strUpdateQry;
		String strPosQry;
		String strCasoFeliz;
		String FrechType;
		String strNumeroErrores;
		String strNumeroRegistros;
		String strRegistrosAceptados;
		String strCobrosAceptados;
		String strCobrosRechazados;
		String link = "";
		String link1 = "";

		int intErrores = 0;
		boolean boolCasoFeliz;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd.dameListaPersonas(
				"SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ESTADO_CASO = 'Activo' and ID_CASO = '" + Caso
						+ "'");
		try {
			while (rs.next()) {
				int intUtilizado = rs.getInt("UTILIZADO");
				strFechaProcesamiento = rs.getString("HORA_PROCESAMIENTO").toString();
				strNombreArchivo = rs.getString("NOMBRE_ARCHIVO").toString();
				strFechaArchivo = rs.getString("HORA").toString();
				strResultadoEsperado = rs.getString("RESPUESTA_ESPERADA").toString();
				strPosQry = rs.getString("POS_QUERY").toString();
				strFrechInPath = rs.getString("INFOLDER").toString();
				FrechType = rs.getString("FRECH_NUM");
				strCasoFeliz = rs.getString("CASO_FELIZ").toString();
				boolCasoFeliz = Boolean.parseBoolean(strCasoFeliz);

				strFecha = strFechaArchivo.split(" ");
				strPart1 = strFecha[0].replaceAll("-", "");
				strPart2 = strFecha[1].replaceAll(":", "");
				strFechaA = strNombreArchivo + "_" + strPart1 + strPart2;

				strqueryString = "SELECT * FROM [Frech_Automatizacion].[" + GetFrechSchema(FrechType) + "].[archivos] "
						+ "WHERE [fecha_archivo] > '" + strFechaProcesamiento + "' " + "AND nombre_archivo = '"
						+ strNombreArchivo + "'";
				bd.estableceConexion(
						"jdbc:sqlserver://WDESSQL20141D;database=Frech_Automatizacion;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
				ResultSet rs1 = bd.dameListaPersonas(strqueryString);

				while (rs1.next()) {
					strNumeroErrores = rs1.getString("numero_errores").toString();
					strNumeroRegistros = rs1.getString("numero_registros").toString();
					strRegistrosAceptados = rs1.getString("registros_aceptados").toString();
					strCobrosAceptados = rs1.getString("cobros_aceptados").toString();
					strCobrosRechazados = rs1.getString("cobros_rechazados").toString();
					Bd_Resultados = "N° Errores: " + "" + strNumeroErrores + " N° Registros: " + "" + strNumeroRegistros
							+ " Registros aceptados: " + "" + strRegistrosAceptados + " Cobros Aceptados: " + ""
							+ strCobrosAceptados + " Cobros Rechazados: " + "" + strCobrosRechazados;
					intErrores = Integer.parseInt(strNumeroErrores);

					link = "D:\\FRECH\\Automatizacion\\FRECH\\FrechFiles\\out\\" + strFechaA + "";
					link1 = "<a href=\"" + link + "\">" + strFechaA + "</a>";

					strUpdateQry = "UPDATE dbo.UNI_FRECH1_CREACION_CREDITO_D " + "SET RESULTADO_BD_FRECH = '"
							+ Bd_Resultados + "', " + "OUTPUT_FILE = '" + link1 + "' " + "WHERE ID_CASO = " + Caso
							+ " ";
					bd.ejecutarSentenciaUpdate(strUpdateQry);

					if (boolCasoFeliz) {
						if (intErrores == 0) {
							System.out.println("Caso Feliz no está generando error en el procesamiento del archivo");
						}
					} else {
						System.out.println("Caso Feliz (False) está generando error en el procesamiento del archivo: "
								+ link + "");
						assertFalse(
								"Caso Feliz (False) está generando error en el procesamiento del archivo: " + link + "",
								true);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return link;
	}

	public static void cerrar_Carpeta() {
		Runtime app = Runtime.getRuntime();
		try {
			app.exec("cmd /c taskkill /f /im notepad.exe ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String ObtenerCobertCredPeriodoLiquidado(String strFrechNo, int caso) {
		String strReturnValue = "";
		String strDenomCredito = "";
		String strMontoCreditoUVR = "";
		String strMontoCreditoCOP = "";
		String strfechaDesembCredito = "";
		String strPerLiquid2 = "";
		String strCantRegistros = "";
		String strTasaCobertura = "";
		String strQuery = "";
		String strUVRValue = "";
		String strCobertura2 = "";
		String strDiasCobertura = "";
		String strDiasCobertura2 = "";
		Double decMontoCreditoUVR = (double) 0;
		Double decMontoCreditoCOP = (double) 0;
		Double decTasaCobertura = (double) 0;
		Double decUVRValue = (double) 0;
		Double decCobertura = (double) 0;
		Double decDiasCobertura = (double) 0;
		DateTime dtFechaDesem;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strDenomCredito = rs.getString("DENOM_CREDITO").toString();
				strMontoCreditoUVR = rs.getString("MONTO_DESEMBOLSO_CREDITO_UVR").toString();
				strMontoCreditoCOP = rs.getString("MONTO_CREDITO_EN_PESOS").toString();
				strTasaCobertura = rs.getString("TASA_COBERTURA").toString();
				strCantRegistros = rs.getString("TOTAL_REGISTROS").toString();
				strfechaDesembCredito = rs.getString("FECHA_CALC_COBERTURA").toString();
				// strCobertura2 = rs.getString("COBERTURA_T2").toString();
				strPerLiquid2 = rs.getString("COBERTURA_CREDITO_PERIODO_LIQ2").toString();
				strDiasCobertura = rs.getString("NO_DIAS_PERIODO_LIQUIDADO").toString();
				strDiasCobertura2 = rs.getString("NO_DIAS_PERIODO_LIQUIDADO2").toString();
				decDiasCobertura = Double.parseDouble(strDiasCobertura);
				if ("NO_DIAS_PERIODO_LIQUIDADO2".toString() != "<IGNORE>") {
					Double.parseDouble(strDiasCobertura2);
				}
				dtFechaDesem = DateTime.parse(strfechaDesembCredito);
				strfechaDesembCredito = dtFechaDesem.toString("yyyyMMdd");
				strQuery = "SELECT [uvr] FROM [Frech_Automatizacion].[" + GetFrechSchema(strFrechNo)
						+ "].[uvr] where fecha_uvr ='" + strfechaDesembCredito + "'";
				bd.estableceConexion(
						"jdbc:sqlserver://WDESSQL20141D;database=Frech_Automatizacion;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
				ResultSet rs1 = bd.dameListaPersonas(strQuery);
				while (rs1.next()) {
					strUVRValue = rs1.getString("uvr").toString();
				}
				if (strUVRValue.equals("")) {
					strUVRValue = "100.000";
				}
				decUVRValue = Double.parseDouble(strUVRValue.replace(".", ","));
				if (strDenomCredito.equals("UVR")) {
					decMontoCreditoUVR = Double.parseDouble(strMontoCreditoUVR.replace(".", ","));
					decMontoCreditoUVR = decMontoCreditoUVR - (decMontoCreditoUVR / 10);
					decMontoCreditoUVR = (Math.floor(decMontoCreditoUVR * 10000)) / 10000;
					decTasaCobertura = Double.parseDouble(strTasaCobertura.replace(".", ","));

					if (strCantRegistros.equals("2")) {
						decCobertura = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaCobertura), (1 / 12))) - 1))
								* decUVRValue;
					} else {
						decCobertura = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaCobertura), (1 / 12))) - 1))
								* (decDiasCobertura / 30) * decUVRValue;
						if (strPerLiquid2.equals(strSearch)) {

						} else {
							if (strCobertura2 != "<IGNORE>") {
								decCobertura = decCobertura + Double.parseDouble(strCobertura2.replace(".", ","));
							}
						}
					}
				} else {
					if (strDenomCredito.equals("COP")) {
						decMontoCreditoCOP = Double.parseDouble(strMontoCreditoCOP.replace(".", ","));
						decMontoCreditoCOP = decMontoCreditoCOP - (decMontoCreditoCOP / 10);
						strReturnValue = decMontoCreditoCOP.toString().replace(",", ".");
					} else {
						strReturnValue = "ERROR";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerMontoCredito(String strFrechNo, int caso) {
		String strReturnValue = "";
		String strDenomCredito = "";
		String strMontoCreditoUVR = "";
		String strMontoCreditoCOP = "";
		String strfechaDesembCredito = "";
		String strCantRegistros = "";
		String strTasaCobertura = "";
		String strQuery = "";
		String strUVRValue = "";
		String strDiasCobertura = "";
		String strTasaPactada = "";
		Double decMonto = (double) 0;
		Double decMontoCreditoUVR = (double) 0;
		Double decMontoCreditoCOP = (double) 0;
		Double decTasaCobertura = (double) 0;
		Double decUVRValue = (double) 0;
		Double decCobertura = (double) 0;
		Double decDiasCobertura = (double) 0;
		Double decInteresT = (double) 0;
		Double decTasaPactada = (double) 0;
		DateTime dtFechaDesem;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strDenomCredito = rs.getString("DENOM_CREDITO").toString();
				strMontoCreditoUVR = rs.getString("MONTO_DESEMBOLSO_CREDITO_UVR").toString();
				strMontoCreditoCOP = rs.getString("MONTO_CREDITO_EN_PESOS").toString();
				strTasaCobertura = rs.getString("TASA_COBERTURA").toString();
				strCantRegistros = rs.getString("TOTAL_REGISTROS").toString();
				strfechaDesembCredito = rs.getString("FECHA_CALC_COBERTURA").toString();
				strTasaPactada = rs.getString("TASA_PACTADA").toString();
				strDiasCobertura = rs.getString("NO_DIAS_PERIODO_LIQUIDADO").toString();
				decDiasCobertura = Double.parseDouble(strDiasCobertura);
				dtFechaDesem = DateTime.parse(strfechaDesembCredito);
				strfechaDesembCredito = dtFechaDesem.toString("yyyyMMdd");
				strQuery = "SELECT [uvr] FROM [Frech_Automatizacion].[" + GetFrechSchema(strFrechNo)
						+ "].[uvr] where fecha_uvr ='" + strfechaDesembCredito + "'";
				bd.estableceConexion(
						"jdbc:sqlserver://WDESSQL20141D;database=Frech_Automatizacion;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
				ResultSet rs1 = bd.dameListaPersonas(strQuery);
				while (rs1.next()) {
					strUVRValue = rs1.getString("uvr").toString();
				}
				if (strUVRValue.equals("")) {
					strUVRValue = "100.000";
				}
				decUVRValue = Double.parseDouble(strUVRValue.replace(".", ","));
				if (strDenomCredito.equals("UVR")) {
					decMontoCreditoUVR = Double.parseDouble(strMontoCreditoUVR.replace(".", ","));
					decMontoCreditoUVR = decMontoCreditoUVR - (decMontoCreditoUVR / 10);
					decMontoCreditoUVR = (Math.floor(decMontoCreditoUVR * 10000)) / 10000;
					decTasaPactada = Double.parseDouble(strTasaPactada.replace(".", ","));
					decTasaCobertura = Double.parseDouble(strTasaCobertura.replace(".", ","));

					if (strCantRegistros.equals("2")) {
						decCobertura = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaCobertura), (1 / 12))) - 1))
								* decUVRValue;
						decInteresT = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaPactada), (1 / 12))) - 1))
								* decUVRValue;
					} else {
						decCobertura = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaCobertura), (1 / 12))) - 1))
								* (decDiasCobertura / 30) * decUVRValue;
						decInteresT = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaPactada), (1 / 12))) - 1))
								* (decDiasCobertura / 30) * decUVRValue;
					}
					decMonto = decInteresT - decCobertura;
					decMonto = Math.floor(decMonto);
					strReturnValue = decMonto.toString().replace(",", ".");
				} else {
					if (strDenomCredito.equals("COP")) {
						decMontoCreditoCOP = Double.parseDouble(strMontoCreditoCOP.replace(".", ","));
						decMontoCreditoCOP = decMontoCreditoCOP - (decMontoCreditoCOP / 10);
						strReturnValue = decMontoCreditoCOP.toString().replace(",", ".");
					} else {
						strReturnValue = "ERROR";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return strReturnValue;
	}

	private static String ObtenerCobertura(String strFrechNo, int caso) {
		String strReturnValue = "";
		String strDenomCredito = "";
		String strMontoCreditoUVR = "";
		String strMontoCreditoCOP = "";
		String strfechaDesembCredito = "";
		String strCantRegistros = "";
		String strTasaCobertura = "";
		String strQuery = "";
		String strUVRValue = "";
		String strDiasCobertura = "";
		Double decMontoCreditoUVR = (double) 0;
		Double decMontoCreditoCOP = (double) 0;
		Double decTasaCobertura = (double) 0;
		Double decUVRValue = (double) 0;
		Double decCobertura = (double) 0;
		Double decDiasCobertura = (double) 0;
		DateTime dtFechaDesem;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strDenomCredito = rs.getString("DENOM_CREDITO").toString();
				strMontoCreditoUVR = rs.getString("MONTO_DESEMBOLSO_CREDITO_UVR").toString();
				strMontoCreditoCOP = rs.getString("MONTO_CREDITO_EN_PESOS").toString();
				strTasaCobertura = rs.getString("TASA_COBERTURA").toString();
				strCantRegistros = rs.getString("TOTAL_REGISTROS").toString();
				strfechaDesembCredito = rs.getString("FECHA_CALC_COBERTURA").toString();
				strDiasCobertura = rs.getString("NO_DIAS_PERIODO_LIQUIDADO").toString();
				decDiasCobertura = Double.parseDouble(strDiasCobertura);
				dtFechaDesem = DateTime.parse(strfechaDesembCredito);
				strfechaDesembCredito = dtFechaDesem.toString("yyyyMMdd");
				strQuery = "SELECT [uvr] FROM [Frech_Automatizacion].[" + GetFrechSchema(strFrechNo)
						+ "].[uvr] where fecha_uvr ='" + strfechaDesembCredito + "'";
				bd.estableceConexion(
						"jdbc:sqlserver://WDESSQL20141D;database=Frech_Automatizacion;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
				ResultSet rs1 = bd.dameListaPersonas(strQuery);
				while (rs1.next()) {
					strUVRValue = rs1.getString("uvr").toString();
				}
				if (strUVRValue.equals("")) {
					strUVRValue = "100.000";
				}
				decUVRValue = Double.parseDouble(strUVRValue.replace(".", ","));
				if (strDenomCredito.equals("UVR")) {
					decMontoCreditoUVR = Double.parseDouble(strMontoCreditoUVR.replace(".", ","));
					decMontoCreditoUVR = decMontoCreditoUVR - (decMontoCreditoUVR / 10);
					decMontoCreditoUVR = (Math.floor(decMontoCreditoUVR * 10000)) / 10000;
					decTasaCobertura = Double.parseDouble(strTasaCobertura.replace(".", ","));
					decCobertura = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaCobertura), (1 / 12))) - 1))
							* decUVRValue;

					if (strCantRegistros.equals("2")) {
						decCobertura = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaCobertura), (1 / 12))) - 1))
								* decUVRValue;
					} else {
						decCobertura = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaCobertura), (1 / 12))) - 1))
								* (decDiasCobertura / 30) * decUVRValue;
					}
					decCobertura = Math.floor(decCobertura);
					strReturnValue = decCobertura.toString().replace(",", ".");
				} else {
					if (strDenomCredito.equals("COP")) {
						decMontoCreditoCOP = Double.parseDouble(strMontoCreditoCOP.replace(".", ","));
						decMontoCreditoCOP = decMontoCreditoCOP - (decMontoCreditoCOP / 10);
						strReturnValue = decMontoCreditoCOP.toString().replace(",", ".");
					} else {
						strReturnValue = "ERROR";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return strReturnValue;
	}

	private static String ObtenerInteresT(String strFrechNo, int caso) {
		String strReturnValue = "";
		String strDenomCredito = "";
		String strMontoCreditoUVR = "";
		String strMontoCreditoCOP = "";
		String strfechaDesembCredito = "";
		String strCantRegistros = "";
		String strQuery = "";
		String strUVRValue = "";
		String strTasaPactada = "";
		String strDiasCobertura = "";
		Double decMontoCreditoUVR = (double) 0;
		Double decMontoCreditoCOP = (double) 0;
		Double decUVRValue = (double) 0;
		Double decDiasCobertura = (double) 0;
		Double decInteresT = (double) 0;
		Double decTasaPactada = (double) 0;
		DateTime dtFechaDesem;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strDenomCredito = rs.getString("DENOM_CREDITO").toString();
				strMontoCreditoUVR = rs.getString("MONTO_DESEMBOLSO_CREDITO_UVR").toString();
				strMontoCreditoCOP = rs.getString("MONTO_CREDITO_EN_PESOS").toString();
				strCantRegistros = rs.getString("TOTAL_REGISTROS").toString();
				strfechaDesembCredito = rs.getString("FECHA_CALC_COBERTURA").toString();
				strTasaPactada = rs.getString("TASA_PACTADA").toString();
				strDiasCobertura = rs.getString("NO_DIAS_PERIODO_LIQUIDADO").toString();
				decDiasCobertura = Double.parseDouble(strDiasCobertura);
				dtFechaDesem = DateTime.parse(strfechaDesembCredito);
				strfechaDesembCredito = dtFechaDesem.toString("yyyyMMdd");
				strQuery = "SELECT [uvr] FROM [Frech_Automatizacion].[" + GetFrechSchema(strFrechNo)
						+ "].[uvr] where fecha_uvr ='" + strfechaDesembCredito + "'";
				bd.estableceConexion(
						"jdbc:sqlserver://WDESSQL20141D;database=Frech_Automatizacion;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
				ResultSet rs1 = bd.dameListaPersonas(strQuery);
				while (rs1.next()) {
					strUVRValue = rs1.getString("uvr").toString();
				}
				if (strUVRValue.equals("")) {
					strUVRValue = "100.000";
				}
				decUVRValue = Double.parseDouble(strUVRValue.replace(".", ","));
				if (strDenomCredito.equals("UVR")) {
					decMontoCreditoUVR = Double.parseDouble(strMontoCreditoUVR.replace(".", ","));
					decMontoCreditoUVR = decMontoCreditoUVR - (decMontoCreditoUVR / 10);
					decMontoCreditoUVR = (Math.floor(decMontoCreditoUVR * 10000)) / 10000;
					decTasaPactada = Double.parseDouble(strTasaPactada.replace(".", ","));
					if (strCantRegistros.equals("2")) {
						decInteresT = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaPactada), (1 / 12))) - 1))
								* decUVRValue;
					} else {
						decInteresT = (decMontoCreditoUVR * (((int) Math.pow((1 + decTasaPactada), (1 / 12))) - 1))
								* (decDiasCobertura / 30) * decUVRValue;
					}
					decInteresT = Math.floor(decInteresT);
					strReturnValue = decInteresT.toString().replace(",", ".");
				} else {
					if (strDenomCredito.equals("COP")) {
						decMontoCreditoCOP = Double.parseDouble(strMontoCreditoCOP.replace(".", ","));
						decMontoCreditoCOP = decMontoCreditoCOP - (decMontoCreditoCOP / 10);
						strReturnValue = decMontoCreditoCOP.toString().replace(",", ".");
					} else {
						strReturnValue = "ERROR";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return strReturnValue;
	}

	private static String ObtenerDestCoberturaCredCedido(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("DEST_COBERTURA").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("DEST_COBERTURA".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerMontoCredCedidoCOP(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("MONTO_CREDITO_EN_PESOS").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("MONTO_CREDITO_EN_PESOS".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerPlazoCreditoMeses(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("PLAZO_MESES").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("PLAZO_MESES".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerMontoCredCedidoUVR(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("MONTO_DESEMBOLSO_CREDITO_UVR").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("MONTO_DESEMBOLSO_CREDITO_UVR".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerSistAmortCredCedido(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("SISTEMA_AMORTIZACION").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("SISTEMA_AMORTIZACION".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerDenomCredCedido(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("DENOM_CREDITO").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("DENOM_CREDITO".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerCifinInicial(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("NO_CIFIN").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("NO_CIFIN".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerFechaDesembCredCedido(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("FECHA_DESEMBOLSO_CREDITO").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("FECHA_DESEMBOLSO_CREDITO".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerFechaTransmisionCifin() {
		DateTime dtFecha;
		String strFecha = "";

		dtFecha = DateTime.now();
		strFecha = dtFecha.toString("yyyyMMdd");

		return strFecha;
	}

	private static String ObtenerValorVentaInmuebleSMMLV(String strFrechNo, int caso) {
		String strReturnValue = "";
		String strqueryString;
		String strAnio;
		String strValorVentaInm;
		String strFieldsToSave;
		String strValuesSaved;
		String strDecimales;
		String strSMMLV;
		int intReturnValue = 0;
		int intDecimales = 0;
		int intValorVentaInm;
		int SMMLV = 0;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("VALOR_DE_VENTA_INMUEBLE_EN_SMMLV").toString().trim();
				strValorVentaInm = rs.getString("VALOR_VENTA_EN_PESOS").toString();
				intValorVentaInm = Integer.parseInt(strValorVentaInm);
				strAnio = rs.getString("FECHA_DESEMBOLSO_CREDITOS").toString().trim().substring(0, 4);

				if (strReturnValue.equals(strSearch)) {
					strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
					strValuesSaved = rs.getString("VALUES_SAVED").toString();
					strqueryString = "SELECT * FROM [Frech_Automatizacion].['" + GetFrechSchema(strFrechNo)
							+ "'].[parametros]" + "WHERE [id_parametro] = 'numDecSMMLV'";
					bd.estableceConexion(
							"jdbc:sqlserver://WDESSQL20141D;database=Frech_Automatizacion;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
					ResultSet rs1 = bd.dameListaPersonas(strqueryString);
					while (rs1.next()) {
						strDecimales = rs1.getString("valor").toString();
						intDecimales = Integer.parseInt(strDecimales);
					}
					strqueryString = "SELECT * FROM [Frech_Automatizacion].['" + GetFrechSchema(strFrechNo)
							+ "'].[parametros]" + "WHERE [id_parametro] = 'Smlv" + strAnio + "'";
					ResultSet rs2 = bd.dameListaPersonas(strqueryString);
					while (rs2.next()) {
						strSMMLV = rs2.getString("valor").toString();
						SMMLV = Integer.parseInt(strSMMLV);
					}
					intReturnValue = Math.floorDiv(intValorVentaInm / SMMLV, intDecimales);
					strReturnValue = Integer.toString(intReturnValue).replace(",", ".");
					while (strReturnValue.length() <= intDecimales + 4) {
						strReturnValue = strReturnValue + "1";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return strReturnValue;
	}

	private static String ObtenerValorSMMLVCredito(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("VALOR_DE_VENTA_INMUEBLE_EN_SMMLV").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("VALOR_DE_VENTA_INMUEBLE_EN_SMMLV".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerTasaCoberturaCredCedido(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("TASA_COBERTURA").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("TASA_COBERTURA".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerSaldoCapVigente(int caso) {
		String strReturnValue = "";
		String strDenomCredito = "";
		String strMontoCreditoUVR = "";
		String strMontoCreditoCOP = "";
		String strNumeroNuevo;
		double decMontoCreditoUVR;
		double decMontoCreditoCOP;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strDenomCredito = rs.getString("DENOM_CREDITO").toString();
				strMontoCreditoUVR = rs.getString("MONTO_DESEMBOLSO_CREDITO_UVR").toString();
				strMontoCreditoCOP = rs.getString("MONTO_CREDITO_EN_PESOS").toString();
				if (strDenomCredito.equals("UVR")) {
					strNumeroNuevo = strMontoCreditoUVR.replace(".", ".");
					decMontoCreditoUVR = Double.parseDouble(strNumeroNuevo);
					System.out.println(decMontoCreditoUVR);
					decMontoCreditoUVR = decMontoCreditoUVR - (decMontoCreditoUVR / 10);
					decMontoCreditoUVR = ((Math.floor(decMontoCreditoUVR * 10000)) / 10000);
					strReturnValue = Double.toString(decMontoCreditoUVR).replace(",", ".");
					System.out.println(strReturnValue);
				} else {
					if (strDenomCredito.equals("COP")) {
						strNumeroNuevo = strMontoCreditoCOP.replace(".", ".");
						decMontoCreditoCOP = Double.parseDouble(strNumeroNuevo);
						decMontoCreditoCOP = decMontoCreditoCOP - (decMontoCreditoCOP / 10);
						strReturnValue = Double.toString(decMontoCreditoCOP).replace(",", ".");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return strReturnValue;
	}

	private static String ObtenerFechaLiquidCredito(int caso) {
		String strReturnValue = "";
		String strDesemCredito = "";
		DateTime dtFechaDesem;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strDesemCredito = rs.getString("FECHA_DESEMBOLSO_CREDITO").toString();
				dtFechaDesem = DateTime.parse(strDesemCredito);
				dtFechaDesem = dtFechaDesem.plusMonths(1);
				strReturnValue = dtFechaDesem.toString("yyyyMM");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return strReturnValue;
	}

	private static String ObtenerValorCOPCredito(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("VALOR_VENTA_EN_PESOS").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("VALOR_VENTA_EN_PESOS".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerNoCreditoCedido(String strFrechNo, int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("NUMERO_CREDITO").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("NUMERO_CREDITO".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
				String strSearch2 = "<NCES2SEARCH>";
				if (strReturnValue.equals(strSearch2)) {
					strReturnValue = ObtenerNuevoNoCredito();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerCifinCedido(int caso) {
		String strReturnValue = "";
		String strFieldsToSave;
		String strValuesSaved;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				strReturnValue = rs.getString("NO_CIFIN").toString().trim();
				strFieldsToSave = rs.getString("FIELDS_TO_SAVE").toString();
				strValuesSaved = rs.getString("VALUES_SAVED").toString();
				if (strReturnValue.equals(strSearch)) {
					String[] strFieldsToSave1;
					String[] strValuesSaved1;
					strFieldsToSave1 = strFieldsToSave.split("|");
					strValuesSaved1 = strValuesSaved.split("|");

					for (int i = 0; i < strFieldsToSave1.length - 1; i++) {
						if ("NO_CIFIN".equals(strFieldsToSave1[i].trim())) {
							strReturnValue = strValuesSaved1[i].trim();
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerNuevosNombres() throws ParseException {
		String strReturnValue = "";
		double aleatorio = 0;

		String[] nombres = { "GUILLERMO", "LUCAS", "DAVID", "ROGER", "BRUNO", "ISABEL", "ALEX", "CARLOS", "MARIBEL",
				"ALISON", "FERNANDA", "JHOANA ", "NATALY ", "MICHELL ", "PAULINA ", "GRACE", "JOSE ", "JESUS ",
				"CARLOS ", "PEDRO ", "JAVIER ", "MARIA ", "OSCAR ", "CARLA ", "ANA ", "RAUL ", "FERNANDO", "FRANCISCO",
				"MANUEL ", "SOFIA ", "EDGAR ", "DIANA ", "GUADALUPE", "CARLOS ANTONIO", "MARIA CAMILA",
				"PAULA CRISTINA", "MARIA FERNANDA", "JAIRO ANDRES", "CRISTIAN ALEXANDER", "DIEGO ANDRES",
				"ANA GABRIELA", "JHON ALEXANDER" };

		aleatorio = Math.floor(Math.random() * (nombres.length));
		strReturnValue = nombres[(int) aleatorio];
		return strReturnValue;
	}

	private static String ObtenerNuevosApellidos() throws ParseException {
		String strReturnValue = "";
		double aleatorio = 0;

		String[] apellidos = { "ALVAREZ	ALONSO", "AGUILAR", "ARIAS AGUIRRE", "BLANCO", "BRAVO BENITEZ", "BELTRAN",
				"BERNAL", "CASTRO", "CASTILLO CRUZ", "CAMPOS", "CANO", "DIAZ DOMINGUEZ", "DELGADO", "DIEZ", "DURAN",
				"ESTEBAN", "ESPINOSA", "ESCUDERO", "ESTEVEZ", "ESCOBAR", "FERNANDEZ FLORES", "FERNANDEZ",
				"FERNANDEZ GARCIA", "FERRER", "GARCIA", "GONZALEZ", "GOMEZ", "GARCIA", "GARCIA GUTIERREZ", "HERNANDEZ",
				"HERRERA", "HERRERO", "HIDALGO", "HERNANDEZ GARCIA", "IGLESIAS", "IBANEZ", "IZQUIERDO", "IBARRA",
				"INFANTE", "JIMENEZ", "JIMENEZ GARCIA", "JOSE", "JUAREZ", "LOPEZ", "LOPEZ LOPEZ", "PEREZ", "LUNA",
				"MARTINEZ", "MARTIN	MORENO", "MUNOZ", "MORALES", "NAVARRO", "NUNEZ", "NIETO", "NARANJO", "NAVAS",
				"ORTIZ", "ORTEGA", "OTERO", "OLIVARES", "ORDONEZ", "PEREZ LOPEZ", "PEREZ GARCIA", "PENA", "QUINTANA",
				"QUESADA QUINTERO", "QUIROGA QUINTANILLA", "RODRIGUEZ RUIZ", "ROMERO	RAMIREZ", "RAMOS", "SANCHEZ",
				"SANZ", "SUAREZ	SANCHEZ", "SANCHEZ SERRANO", "TORRES	TRUJILLO", "TOLEDO", "TAPIA", "URIBE",
				"URBANO	URRUTIA", "UBEDA", "URIARTE", "VAZQUEZ", "VEGA", "VIDAL", "VARGAS", "VERA", "WILLIAMS", "YEPES",
				"ZAMORA", "ZAPATA", "ZAMBRANO", "ZARAGOZA", "ZAFRA" };

		aleatorio = Math.floor(Math.random() * (apellidos.length));
		strReturnValue = apellidos[(int) aleatorio];
		return strReturnValue;
	}

	private static String ObtenerNuevoNoCredito() {
		String strqueryString;
		String strReturnValue = "";
		Integer intNoCredito;
		Integer intMaxNoCredito = 2147483000;
		Integer intMinNoCredito = 0;
		Random random = new Random();

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		intNoCredito = random.nextInt((intMaxNoCredito - intMinNoCredito) + 1) + intMinNoCredito;
		strqueryString = "SELECT [numero_credito]"
				+ "FROM [Frech_Automatizacion].[Frech].[creditos] where numero_credito = '"
				+ intNoCredito.toString().trim() + "'" + "UNION ALL Select [numero_credito]"
				+ "FROM [Frech_Automatizacion].[Frech2].[creditos] where numero_credito = '"
				+ intNoCredito.toString().trim() + "'" + "UNION ALL Select [numero_credito]"
				+ "FROM [Frech_Automatizacion].[Frech3].[creditos] where numero_credito = '"
				+ intNoCredito.toString().trim() + "'" + "UNION ALL Select [numero_credito]"
				+ "FROM [Frech_Automatizacion].[Frech4].[creditos] where numero_credito = '"
				+ intNoCredito.toString().trim() + "'" + "UNION ALL Select [numero_credito]"
				+ "FROM [Frech_Automatizacion].[Frech5].[creditos] where numero_credito = '"
				+ intNoCredito.toString().trim() + "'" + "UNION ALL Select [numero_credito]"
				+ "FROM [Frech_Automatizacion].[Frech6].[creditos] where numero_credito = '"
				+ intNoCredito.toString().trim() + "'";
		ResultSet rs = bd.dameListaPersonas(strqueryString);

		try {
			if (!rs.next()) {
				strReturnValue = intNoCredito.toString().trim();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerNuevoIdDeudor() {
		String strqueryString;
		String strReturnValue = "";
		Integer intId;
		Integer intMaxId = 2147483000;
		Integer intMinId = 0;
		Random random = new Random();

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		intId = random.nextInt((intMaxId - intMinId) + 1) + intMinId;
		strqueryString = "SELECT [identificacion_deudor]"
				+ "FROM [Frech_Automatizacion].[Frech].[creditos] where identificacion_deudor = 'C"
				+ intId.toString().trim() + "'" + "UNION ALL Select [identificacion_deudor]"
				+ "FROM [Frech_Automatizacion].[Frech2].[creditos] where identificacion_deudor = 'C"
				+ intId.toString().trim() + "'" + "UNION ALL Select [identificacion_deudor]"
				+ "FROM [Frech_Automatizacion].[Frech3].[creditos] where identificacion_deudor = 'C"
				+ intId.toString().trim() + "'" + "UNION ALL Select [identificacion_deudor]"
				+ "FROM [Frech_Automatizacion].[Frech4].[creditos] where identificacion_deudor = 'C"
				+ intId.toString().trim() + "'" + "UNION ALL Select [identificacion_deudor]"
				+ "FROM [Frech_Automatizacion].[Frech5].[creditos] where identificacion_deudor = 'C"
				+ intId.toString().trim() + "'" + "UNION ALL Select [identificacion_deudor]"
				+ "FROM [Frech_Automatizacion].[Frech6].[creditos] where identificacion_deudor = 'C"
				+ intId.toString().trim() + "'";
		ResultSet rs = bd.dameListaPersonas(strqueryString);

		try {
			if (!rs.next()) {
				strReturnValue = "C" + "" + intId.toString().trim();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerNuevoCifin() {
		String strqueryString;
		String strReturnValue = "";
		Integer intCifin;
		Integer intMaxCifin = 999999;
		Integer intMinCifin = 0;
		Random random = new Random();

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		intCifin = random.nextInt((intMaxCifin - intMinCifin) + 1) + intMinCifin;

		strqueryString = "SELECT [numero_cifin]"
				+ "FROM [Frech_Automatizacion].[Frech].[creditos] where numero_cifin = '" + intCifin.toString().trim()
				+ "'" + "UNION ALL Select [numero_cifin]"
				+ "FROM [Frech_Automatizacion].[Frech2].[creditos] where numero_cifin = '" + intCifin.toString().trim()
				+ "'" + "UNION ALL Select [numero_cifin]"
				+ "FROM [Frech_Automatizacion].[Frech3].[creditos] where numero_cifin = '" + intCifin.toString().trim()
				+ "'" + "UNION ALL Select [numero_cifin]"
				+ "FROM [Frech_Automatizacion].[Frech4].[creditos] where numero_cifin = '" + intCifin.toString().trim()
				+ "'" + "UNION ALL Select [numero_cifin]"
				+ "FROM [Frech_Automatizacion].[Frech5].[creditos] where numero_cifin = '" + intCifin.toString().trim()
				+ "'" + "UNION ALL Select [numero_cifin]"
				+ "FROM [Frech_Automatizacion].[Frech6].[creditos] where numero_cifin = '" + intCifin.toString().trim()
				+ "'";
		ResultSet rs = bd.dameListaPersonas(strqueryString);
		try {
			if (!rs.next()) {
				strReturnValue = intCifin.toString().trim();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerFechaLiquidHead(int caso) {

		String strReturnValue = "";
		String strDesemCredito = "";
		Date dtFechaDesem;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");
		try {
			while (rs.next()) {
				strDesemCredito = rs.getString("FECHA_DESEMBOLSO_CREDITO").toString();
				SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMM");

				try {
					dtFechaDesem = fromUser.parse(strDesemCredito); // Parse it to the exisitng date pattern and return
																	// Date type
					strReturnValue = myFormat.format(dtFechaDesem); // format it to the date pattern you prefer
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}

	private static String ObtenerSumCampoCobertura(String strFrechNo, int caso) {

		String CoberturaT1 = "";
		String CoberturaT2 = "";
		String ReturnValue = "";
		Integer intCoberturaT1 = 0;
		Integer intCoberturaT2 = 0;
		Integer Total = 0;

		bd.estableceConexion(
				"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
		ResultSet rs = bd
				.dameListaPersonas("SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");

		try {
			while (rs.next()) {
				CoberturaT1 = rs.getString("COBERTURA_T").toString();
				// CoberturaT2 = rs.getString("COBERTURA_T2").toString();
				/*
				 * if (CoberturaT1.equals("<SEARCH>")) {
				 * 
				 * } CoberturaT2 = rs.getString("COBERTURA_T2").toString(); if
				 * (CoberturaT2.equals("<SEARCH>")) {
				 * 
				 * }
				 */

				if (CoberturaT1.equals("<IGNORE>") || CoberturaT1.equals("") || CoberturaT1.equals("<SEARCH>")) {
					intCoberturaT1 = 0;
				}

				/*
				 * if (CoberturaT2.equals("<IGNORE>") || CoberturaT2.equals("") ||
				 * CoberturaT2.equals("<SEARCH>") ) { intCoberturaT2= 0; }
				 */
			}
			Total = (intCoberturaT1 + intCoberturaT2);
			ReturnValue = Total.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ReturnValue;
	}

	private static String ObtenerAnio_Mes(String strNombreArchAnio_Mes, String strFormato, int caso)
			throws SQLException {

		String strAnioyMes = "";
		String strDesemCredito = "";
		DateTime dtFechaDesem;

		if (strNombreArchAnio_Mes != "<IGNORE>") {
			switch (strFormato.trim()) {
			case "1":
				strAnioyMes = "201604";
				break;
			case "2":
				bd.estableceConexion(
						"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
				ResultSet rs = bd.dameListaPersonas(
						"SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");
				while (rs.next()) {
					strDesemCredito = rs.getString("FECHA_DESEMBOLSO_CREDITO").toString();
					dtFechaDesem = DateTime.parse(strDesemCredito);
					dtFechaDesem = dtFechaDesem.plusMonths(1);
					strAnioyMes = dtFechaDesem.toString("yyyyMM");
				}
				break;
			case "3":
				strAnioyMes = "201606";
				break;
			case "4":
				strAnioyMes = "201605";
				break;
			case "5":
				strAnioyMes = "201606";
				break;
			case "6":
				bd.estableceConexion(
						"jdbc:sqlserver://WDESSQL20141D;database=BR_FRECH;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
				ResultSet rs1 = bd.dameListaPersonas(
						"SELECT * FROM  dbo.UNI_FRECH1_CREACION_CREDITO_D WHERE ID_CASO = '" + caso + "'");
				while (rs1.next()) {
					strDesemCredito = rs1.getString("FECHA_DESEMBOLSO_CREDITO").toString();
					dtFechaDesem = DateTime.parse(strDesemCredito);
					dtFechaDesem = dtFechaDesem.plusMonths(1);
					strAnioyMes = dtFechaDesem.toString("yyyyMM");
				}
				break;
			default:
				strAnioyMes = "ERROR";
			}
		}
		return strAnioyMes;
	}

	private static String ObtenerCodigoSebra(String strNitEntidad, String strFrechNo, String strNombreArchCodSebra)
			throws SQLException {

		String strqueryString;
		String strCodSebra = "";

		if (strNombreArchCodSebra != "<IGNORE>") {
			strqueryString = "SELECT * FROM [Frech_Automatizacion].[" + GetFrechSchema(strFrechNo) + "].[entidades]"
					+ "WHERE [nit_entidad] = '" + strNitEntidad + "'";
			bd.estableceConexion(
					"jdbc:sqlserver://WDESSQL20141D;database=Frech_Automatizacion;user=autopruebas;password=choucair;trustServerCertificate=false;loginTimeout=30;");
			ResultSet rs = bd.dameListaPersonas(strqueryString);
			while (rs.next()) {
				strCodSebra = rs.getString("cod_sebra").toString();
			}
			if (strCodSebra == "") {
				System.out.println("NIT Entidad no encontrado");
			}
		} else {
			strCodSebra = strNombreArchCodSebra;
		}
		return strCodSebra;
	}

	private static String ObtenerFormato(String strFormato, String strNombreArchFormato) {

		String sFormat;
		if (strNombreArchFormato != "<IGNORE>") {
			sFormat = strFormato;
		} else {
			sFormat = strNombreArchFormato;
		}
		return sFormat;
	}

	private static String ObtenerIndicadorArchivo(String strFrechNo, String strNombreArchIndicador) {

		String sFrechFileChar = "";
		if (strNombreArchIndicador != "<IGNORE>") {
			switch (strFrechNo.trim()) {
			case "1":
				sFrechFileChar = "F";
				break;
			case "2":
				sFrechFileChar = "R";
				break;
			case "3":
				sFrechFileChar = "C";
				break;
			case "4":
				sFrechFileChar = "V";
				break;
			case "5":
				sFrechFileChar = "M";
				break;
			case "6":
				sFrechFileChar = "P";
				break;
			default:
				sFrechFileChar = "ERROR";
				break;
			}
		} else {
			sFrechFileChar = strNombreArchIndicador;
		}
		return sFrechFileChar;
	}

}