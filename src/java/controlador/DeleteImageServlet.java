/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// src/java/controlador/DeleteImageServlet.java
package controlador;

import modelo.ImageDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/DeleteImageServlet")
public class DeleteImageServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    String idParam = request.getParameter("id");
    int imgId = idParam != null ? Integer.parseInt(idParam) : 0;

    // contextPath = ruta absoluta al webapp en disco
    String contextPath = getServletContext().getRealPath("/");

    boolean ok = new ImageDAO().eliminarImagen(imgId, contextPath);
    // Después de borrar volvemos al dashboard del padre
    response.sendRedirect("albumPadre.jsp");
  }
}
