/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import java.util.regex.Pattern;

/**
 *
 * @author Jean
 */
public class ValidacionContraseña {
    // Patrones para validar la fortaleza de la contraseña
    private static final String MAYUSCULA_PATTERN = ".*[A-Z].*";
    private static final String MINUSCULA_PATTERN = ".*[a-z].*";
    private static final String DIGITO_PATTERN = ".*\\d.*";
    private static final String CARACTER_ESPECIAL_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";
    
    // Configuración de requisitos (puedes ajustar estos valores)
    private static final int LONGITUD_MINIMA = 8;
    private static final int LONGITUD_MAXIMA = 20;
    
    public static boolean esPasswordFuerte(String password) {
        if (password == null || password.length() < LONGITUD_MINIMA || password.length() > LONGITUD_MAXIMA) {
            return false;
        }
        
        // Verificar cada criterio
        boolean tieneMayuscula = Pattern.compile(MAYUSCULA_PATTERN).matcher(password).matches();
        boolean tieneMinuscula = Pattern.compile(MINUSCULA_PATTERN).matcher(password).matches();
        boolean tieneDigito = Pattern.compile(DIGITO_PATTERN).matcher(password).matches();
        boolean tieneCaracterEspecial = Pattern.compile(CARACTER_ESPECIAL_PATTERN).matcher(password).matches();
        
        // Requerir al menos 3 de los 4 criterios
        int criteriosCumplidos = 0;
        if (tieneMayuscula) criteriosCumplidos++;
        if (tieneMinuscula) criteriosCumplidos++;
        if (tieneDigito) criteriosCumplidos++;
        if (tieneCaracterEspecial) criteriosCumplidos++;
        
        return criteriosCumplidos >= 3;
    }
    
    public static String obtenerRequisitosPassword() {
        return "La contraseña debe tener:\n" +
               "- Mínimo " + LONGITUD_MINIMA + " caracteres\n" +
               "- Máximo " + LONGITUD_MAXIMA + " caracteres\n" +
               "- Al menos una letra mayúscula\n" +
               "- Al menos una letra minúscula\n" +
               "- Al menos un dígito\n" +
               "- Al menos un carácter especial (!@#$%^&* etc.)\n" +
               "- Cumplir al menos 3 de los 4 criterios anteriores";
    }   
    // Método adicional para obtener detalles de validación
    public static String obtenerDetallesValidacion(String password) {
        if (password == null) return "Contraseña nula";
        
        StringBuilder detalles = new StringBuilder();
        detalles.append("Longitud: ").append(password.length()).append("/").append(LONGITUD_MINIMA)
                .append(password.length() >= LONGITUD_MINIMA ? " ✅" : " ❌").append("\n");
        
        boolean mayuscula = Pattern.compile(MAYUSCULA_PATTERN).matcher(password).matches();
        detalles.append("Mayúscula: ").append(mayuscula ? "✅" : "❌").append("\n");
        
        boolean minuscula = Pattern.compile(MINUSCULA_PATTERN).matcher(password).matches();
        detalles.append("Minúscula: ").append(minuscula ? "✅" : "❌").append("\n");
        
        boolean digito = Pattern.compile(DIGITO_PATTERN).matcher(password).matches();
        detalles.append("Dígito: ").append(digito ? "✅" : "❌").append("\n");
        
        boolean especial = Pattern.compile(CARACTER_ESPECIAL_PATTERN).matcher(password).matches();
        detalles.append("Carácter especial: ").append(especial ? "✅" : "❌");
        
        return detalles.toString();
    }
}