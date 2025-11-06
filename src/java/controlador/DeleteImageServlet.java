/*
 * SERVLET PARA ELIMINACIÓN DE IMÁGENES DEL ÁLBUM
 * 
 * Funcionalidades: Eliminar imágenes del sistema de archivos y BD
 * Roles: Padre
 * Integración: Relación con alumno y sistema de archivos
 */
package controlador;

import modelo.ImageDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/DeleteImageServlet")
public class DeleteImageServlet extends HttpServlet {
    
    /**
     * 🗑️ MÉTODO POST - ELIMINAR IMAGEN
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        int imgId = idParam != null ? Integer.parseInt(idParam) : 0;

        // 📁 OBTENER RUTA ABSOLUTA AL DIRECTORIO DE LA APLICACIÓN
        String contextPath = getServletContext().getRealPath("/");

        // 🗑️ ELIMINAR IMAGEN (ARCHIVO Y REGISTRO BD)
        boolean ok = new ImageDAO().eliminarImagen(imgId, contextPath);
        
        // 🔄 REDIRIGIR AL ÁLBUM
        response.sendRedirect("albumPadre.jsp");
    }
}