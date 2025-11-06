/*
 * SERVLET PARA SUBIDA DE IMÁGENES AL ÁLBUM DEL ALUMNO
 * 
 * Funcionalidades: Subir imágenes, almacenar en sistema de archivos y BD
 * Roles: Padre
 * Integración: Relación con alumno y sistema de archivos
 */
package controlador;

import modelo.ImageDAO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/UploadImageServlet")
@MultipartConfig(
  fileSizeThreshold = 1024 * 1024,    // 1 MB
  maxFileSize = 5 * 1024 * 1024,      // 5 MB
  maxRequestSize = 6 * 1024 * 1024    // 6 MB
)
public class UploadImageServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";

    /**
     * 💾 MÉTODO POST - SUBIR IMAGEN AL SERVIDOR
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 📥 OBTENER DATOS DEL FORMULARIO
        int alumnoId = Integer.parseInt(req.getParameter("alumno_id"));
        Part filePart = req.getPart("imagen");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String uniqueName = System.currentTimeMillis() + "_" + fileName; // 🕒 NOMBRE ÚNICO

        // 📁 CREAR DIRECTORIO DE SUBIDAS SI NO EXISTE
        String appPath = req.getServletContext().getRealPath("");
        String uploadPath = appPath + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // 💾 ESCRIBIR ARCHIVO EN EL SERVIDOR
        filePart.write(uploadPath + File.separator + uniqueName);

        // 💾 GUARDAR RUTA EN BASE DE DATOS
        String dbPath = UPLOAD_DIR + "/" + uniqueName;
        new ImageDAO().guardarImagen(alumnoId, dbPath);

        // 🔄 REDIRIGIR AL ÁLBUM DEL PADRE
        resp.sendRedirect("albumPadre.jsp");
    }
}