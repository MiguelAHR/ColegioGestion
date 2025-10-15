/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Tarea;
import modelo.TareaDAO;
import modelo.Padre;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class TareasPadreServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        if (padre == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        TareaDAO dao = new TareaDAO();
        List<Tarea> lista = dao.listarPorAlumno(padre.getAlumnoId());
        request.setAttribute("tareas", lista);

        request.getRequestDispatcher("tareasPadre.jsp").forward(request, response);
    }
}

