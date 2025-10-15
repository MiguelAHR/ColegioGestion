/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Observacion;
import modelo.ObservacionDAO;
import modelo.Padre;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ObservacionesPadreServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        if (padre == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        ObservacionDAO dao = new ObservacionDAO();
        List<Observacion> lista = dao.listarPorAlumno(padre.getAlumnoId());
        request.setAttribute("observaciones", lista);

        request.getRequestDispatcher("observacionesPadre.jsp").forward(request, response);
    }
}

