package co.edu.unbosque.ElecSys.cuentaPorPagar.archivo;

import java.math.BigDecimal;

/**
 * Clase de utilidad encargada de convertir valores numéricos (BigDecimal) a su
 * representación textual en español, siguiendo el formato legal para moneda colombiana.
 */
public class CalcularValorLetras {


    /**
     * Punto de entrada principal para la conversión de moneda a letras.
     * @param numero El valor monetario a convertir.
     * @return Una cadena de texto con el valor en letras, incluyendo pesos y centavos (ej: "DIEZ PESOS M/CTE").
     */
    public String convertir(BigDecimal numero) {

        long parteEntera = numero.longValue();
        int centavos = numero
                .remainder(BigDecimal.ONE)
                .movePointRight(2)
                .abs()
                .intValue();

        String letras = convertirNumero(parteEntera);

        if (centavos > 0) {
            return letras + " PESOS CON " + convertirNumero(centavos) + " CENTAVOS M/CTE";
        }

        return letras + " PESOS M/CTE";
    }

    /**
     * Realiza la conversión recursiva de números largos a palabras.
     * @param numero Valor numérico de tipo long.
     * @return Representación textual del número.
     */
    private static String convertirNumero(long numero) {

        if (numero == 0) return "CERO";
        if (numero < 100) return decenas((int) numero);
        if (numero < 1000) return centenas((int) numero);
        if (numero < 1000000)
            return convertirNumero(numero / 1000) + " MIL " + convertirNumero(numero % 1000);
        if (numero < 1000000000) {
            long millones = numero / 1000000;
            long resto = numero % 1000000;

            if (millones == 1) {
                return "UN MILLON" + (resto > 0 ? " " + convertirNumero(resto) : "");
            } else {
                return convertirNumero(millones) + " MILLONES" + (resto > 0 ? " " + convertirNumero(resto) : "");
            }
        }

        return "";
    }

    /**
     * Convierte valores en el rango de las centenas (100-999) a texto.
     * @param numero Valor entero a convertir.
     * @return Texto correspondiente a la centena.
     */
    private static String centenas(int numero) {

        if (numero == 100) return "CIEN";

        String[] centenas = {
                "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS",
                "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS",
                "OCHOCIENTOS", "NOVECIENTOS"
        };

        return centenas[numero / 100] + " " + decenas(numero % 100);
    }

    /**
     * Convierte valores en el rango de las decenas y unidades (0-99) a texto.
     * @param numero Valor entero a convertir.
     * @return Texto correspondiente a la decena/unidad.
     */
    private static String decenas(int numero) {

        String[] especiales = {
                "DIEZ","ONCE","DOCE","TRECE","CATORCE","QUINCE",
                "DIECISEIS","DIECISIETE","DIECIOCHO","DIECINUEVE"
        };

        String[] decenas = {
                "","DIEZ","VEINTE","TREINTA","CUARENTA",
                "CINCUENTA","SESENTA","SETENTA","OCHENTA","NOVENTA"
        };

        String[] unidades = {
                "","UNO","DOS","TRES","CUATRO",
                "CINCO","SEIS","SIETE","OCHO","NUEVE"
        };

        if (numero < 10) return unidades[numero];
        if (numero >= 10 && numero < 20) return especiales[numero - 10];
        if (numero < 100) {
            if (numero % 10 == 0) return decenas[numero / 10];
            return decenas[numero / 10] + " Y " + unidades[numero % 10];
        }

        return "";
    }
}
