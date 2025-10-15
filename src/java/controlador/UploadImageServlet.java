/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int alumnoId = Integer.parseInt(req.getParameter("alumno_id"));
        Part filePart = req.getPart("imagen");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String uniqueName = System.currentTimeMillis() + "_" + fileName;

        // Directorio absoluto
        String appPath = req.getServletContext().getRealPath("");
        String uploadPath = appPath + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // Escribir el archivo
        filePart.write(uploadPath + File.separator + uniqueName);

        // Guardar en BD
        String dbPath = UPLOAD_DIR + "/" + uniqueName;
        new ImageDAO().guardarImagen(alumnoId, dbPath);

        resp.sendRedirect("albumPadre.jsp");
    }
}
