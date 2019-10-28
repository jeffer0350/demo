/*
 * Copyright (c) 2018, Choucair Testing S.A. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package banrep.poc.utilidades;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Clase que lanza las actividades personalizadas del framework choucair para
 * ser ejecutadas durante el ciclo de vida de construcción de Maven.
 * 
 * @author cmurciag
 * 
 * @version 2018.05.25
 */

public class MainPostBuildActions {

	public static void main(String[] args) {
		actualizarCssEvidencias();
	}

	/**
	 * Actualiza el css principal del reporte Serenity BDD para ajustar las
	 * evidencias personalizadas.
	 * <p>
	 * Nota: La ruta del reporte debe ser {@code target/site/serenity/css/core.css}.
	 */
	private static void actualizarCssEvidencias() {
		System.out.println("[INFO] Agregando estilos para las evidencias anexas al reporte Serenity BDD");

		String file = "target/site/serenity/css/core.css";
		String cssToAdd = "img[alt='evidencia']{ max-width: 100%;width: 300px;}";

		try (FileWriter fw = new FileWriter(file, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print(cssToAdd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
