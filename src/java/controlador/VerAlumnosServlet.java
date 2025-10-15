/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Alumno;
import modelo.AlumnoDAO;
import modelo.Grado;
import modelo.GradoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/VerAlumnosServlet")
public class VerAlumnosServlet extends HttpServlet {

    AlumnoDAO alumnoDAO = new AlumnoDAO();
    GradoDAO gradoDAO = new GradoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gradoIdParam = request.getParameter("grado");

        List<Grado> grados = gradoDAO.listar();
        request.setAttribute("grados", grados);

        if (gradoIdParam != null && !gradoIdParam.isEmpty()) {
            int gradoId = Integer.parseInt(gradoIdParam);
            List<Alumno> alumnos = alumnoDAO.listarPorGrado(gradoId);
            request.setAttribute("alumnos", alumnos);
            request.setAttribute("gradoSeleccionado", gradoId);
        }

        request.getRequestDispatcher("verAlumnos.jsp").forward(request, response);
    }
}

