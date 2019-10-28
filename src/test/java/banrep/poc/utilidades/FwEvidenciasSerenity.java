/*
 * Copyright (c) 2018, Choucair Testing S.A. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package banrep.poc.utilidades;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.thucydides.core.steps.StepEventBus;

/**
 * Esta clase consiste únicamente en métodos estáticos que permiten anexar una
 * evidencia en el reporte de Serenity BDD.
 * <p>
 * El core de Serenity BDD usa el {@code WebDriver} para tomar las evidencias,
 * por lo que otro tipo de evidencias o descripciones no pueden ser añadidas
 * libremente. En esta clase se implementan métodos que permitan anexar
 * evidencia propia a los reportes finales de Serenity.
 * <p>
 * Para tomar las evidencias y anexarlas al proyecto se deben tener las
 * siguientes configuraciones y consideraciones:
 * <ol>
 * <li>Configuración POM
 * <li>Clases
 * <li>Actualización de CSS
 * </ol>
 * <p>
 * <b>Ejemplos de uso:</b> evidencia
 * 
 * @author cmurciag
 * @version 2018.05.26
 */

public class FwEvidenciasSerenity {

	
	/**
	 * Toma una captura de pantalla y reemplaza el nombre del paso marcado como
	 * {@code @Step} con la evidencia. La imagen es guardada en la carpeta del
	 * reporte: {@code target/site/serenity/images/}.
	 * 
	 * @param description
	 *            Descripción de la evidencia visible en el reporte
	 */
	
	static Logger logger = LoggerFactory.getLogger(FwEvidenciasSerenity.class);	
	public static void tomarEvidencia(String description) {
		String path = "target/site/serenity/";
		String folder = "images/";
		File dir = new File(path + folder);
		if (!dir.exists()) {
			dir.mkdir();
		}

		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String fileName = "scrn" + timeStamp + ".png";

		try {
			BufferedImage image = new Robot()
					.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ImageIO.write(image, "png", new File(dir + "/" + fileName));
		} catch (Exception e) {
			logger.info("Exception"+ e);
		}
		String arr[];
		arr=description.split("@");
		String text="";
		if(arr.length>1) {
			text = arr[0] + "\r\n [![Dar click en Revisar Resultados]](" + arr[1] + ")";
		}else {
			text = description + "\r\n" + "\r\n[![evidencia](" + folder + fileName + " \"Evidencia " + description
					+ "\")](" + folder + fileName + ")";
		}
		StepEventBus.getEventBus().updateCurrentStepTitle(text);
	}

	/**
	 * Toma una captura de pantalla y reemplaza el nombre del paso marcado como
	 * {@code @Step} con la evidencia. La imagen es guardada en la carpeta del
	 * reporte: {@code target/site/serenity/images/aux/}. Usa el método
	 * {@link #tomarEvidencia(String)} ingresando una evidencia sin descripción.
	 * 
	 * @see #tomarEvidencia(String)
	 * 
	 * @author cmurciag
	 * @since 2018.05.25
	 */
	public static void tomarEvidencia() {
		tomarEvidencia("");
	}

	/**
	 * Toma una captura de pantalla luego de un tiempo de espera <tt>delay</tt> en
	 * <tt>ms</tt>.
	 * 
	 * @param delay
	 *            Tiempo de espera en <tt>ms</tt> antes de tomar la captura.
	 * 
	 * @see #tomarEvidencia(String)
	 * @see #tomarEvidencia(String, int)
	 * 
	 * @author cmurciag
	 * @since 2018.05.25
	 */
	public static void tomarEvidencia(int delay) {
		tomarEvidencia("", delay);
	}

	/**
	 * Toma una captura de pantalla luego de un tiempo de espera <tt>delay</tt> en
	 * <tt>ms</tt> y añade una descripción <tt>description</tt>.
	 * 
	 * @param description
	 *            Descripción de la evidencia visible en el reporte
	 * @param delay
	 *            Tiempo de espera en <tt>ms</tt> antes de tomar la captura.
	 * 
	 * @see #tomarEvidencia(String)
	 * 
	 * @author cmurciag
	 * @since 2018.05.25
	 */
	public static void tomarEvidencia(String description, int delay, String urldoc) {
		try {
			Thread.sleep(delay);
		} catch (Exception e) {
			logger.info("Exception"+ e);
		}
		tomarEvidencia(description+"@"+urldoc);
	}
	/**
	 * Toma una captura de pantalla luego de un tiempo de espera <tt>delay</tt> en
	 * <tt>ms</tt> y añade una descripción <tt>description</tt>.
	 * 
	 * @param description
	 *            Descripción de la evidencia visible en el reporte
	 * @param delay
	 *            Tiempo de espera en <tt>ms</tt> antes de tomar la captura.
	 * 
	 * @see #tomarEvidencia(String)
	 * 
	 * @author cmurciag
	 * @since 2018.05.25
	 */
	public static void tomarEvidencia(String description, int delay) {
		try {
			Thread.sleep(delay);
		} catch (Exception e) {
			logger.info("Exception"+ e);
		}
		tomarEvidencia(description);
	}	
}