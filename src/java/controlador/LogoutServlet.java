/*
 * SERVLET PARA CIERRE SEGURO DE SESIONES DE USUARIO
 * 
 * Propósito: Invalidar sesiones de manera segura y prevenir acceso no autorizado
 * Características: Eliminación completa de datos de sesión, headers de cache
 * Seguridad: Previene ataques de replay y acceso con sesiones expiradas
 */
package controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    /**
     * 📤 MÉTODO GET - PROCESA SOLICITUDES DE CERRAR SESIÓN
     * 
     * Flujo de cierre de sesión:
     * 1. Invalidar sesión actual del usuario
     * 2. Eliminar cookies y datos de sesión
     * 3. Configurar headers para prevenir cache
     * 4. Redirigir al login
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔐 OBTENER SESIÓN ACTUAL SIN CREAR UNA NUEVA (false = no crear nueva)
        HttpSession session = request.getSession(false);
        
        // 🗑️ INVALIDAR SESIÓN EXISTENTE - ELIMINA TODOS LOS DATOS DE SESIÓN
        if (session != null) {
            session.invalidate(); // 🧨 DESTRUYE COMPLETAMENTE LA SESIÓN
            System.out.println("✅ Sesión invalidada correctamente");
        } else {
            System.out.println("ℹ️  No había sesión activa para invalidar");
        }

        // 🔒 CONFIGURAR HEADERS DE SEGURIDAD - PREVIENE USO DE CACHE
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // FECHA DE EXPIRACIÓN EN EL PASADO

        System.out.println("🔒 Headers de seguridad configurados - Cache deshabilitado");

        // 🏠 REDIRIGIR AL LOGIN CON INTERFAZ LIMPIA
        response.sendRedirect("index.jsp");
        System.out.println("➡️ Usuario redirigido a página de login");
    }
}