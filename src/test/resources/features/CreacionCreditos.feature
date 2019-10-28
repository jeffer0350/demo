@tag
Feature: Creacion de creditos Frech

  @tag1
  Scenario Outline: Crear Creditos
    Given Traer Casos Crear Creditos <Caso>
    And Crear el dia <Caso>
    And Ejecutar query de apoyo <Caso>
    And Limpiar los archivos de los directorios temporales <Caso>
    When Crear el archivo Frech <Caso>
    And Lanzar el bat para ejecutar el proceso de Frech <Caso>
    And Revisar los resultados del procesamiento del archivo <Caso>
    And Ejecutar query de apoyo <Caso>
    Then Cerrar la carpeta de la evidencia tomada

    Examples: 
      | Caso | Descripción                                                                                                     |
      |    1 | Crear crédito exitosamente                                                                                      |
      |   38 | Crear un crédito utilizando el ultimo cupo de la elegibilidad A                                                 |
      |    3 | Validar longitud máxima del cifin (7)                                                                           |
      |    6 | Validar que la identificación no tenga ceros a la derecha                                                       |
      |   21 | Validar un crédito con el campo desembolso en pesos mayor al valor del inmueble en pesos- se rechaza el crédito |
