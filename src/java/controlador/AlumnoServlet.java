/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

import modelo.Alumno;
import modelo.AlumnoDAO;
import modelo.GradoDAO;

public class AlumnoServlet extends HttpServlet {

    AlumnoDAO dao = new AlumnoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("alumnos.jsp").forward(request, response);
            return;
        }

        if (accion.equals("filtrar")) {
            String gradoStr = request.getParameter("grado_id");

            request.setAttribute("grados", new GradoDAO().listar());

            if (gradoStr == null || gradoStr.isEmpty()) {
                request.setAttribute("lista", dao.listar());
            } else {
                int gradoId = Integer.parseInt(gradoStr);
                request.setAttribute("gradoSeleccionado", gradoId);
                request.setAttribute("lista", dao.listarPorGrado(gradoId));
            }

            request.getRequestDispatcher("alumnos.jsp").forward(request, response);
            return;
        }

        if (accion.equals("nuevo")) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.getRequestDispatcher("alumnoForm.jsp").forward(request, response);
            return;
        }

        switch (accion) {
            case "editar":
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Alumno alumno = dao.obtenerPorId(idEditar);
                request.setAttribute("alumno", alumno);
                request.setAttribute("grados", new GradoDAO().listar());
                request.getRequestDispatcher("alumnoForm.jsp").forward(request, response);
                break;

            case "eliminar":
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("AlumnoServlet");
                break;

            default:
                response.sendRedirect("AlumnoServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        Alumno a = new Alumno();
        a.setNombres(request.getParameter("nombres"));
        a.setApellidos(request.getParameter("apellidos"));
        a.setCorreo(request.getParameter("correo"));
        a.setFechaNacimiento(request.getParameter("fecha_nacimiento"));
        a.setGradoId(Integer.parseInt(request.getParameter("grado_id")));

        if (id == 0) {
            dao.agregar(a);
        } else {
            a.setId(id);
            dao.actualizar(a);
        }

        response.sendRedirect("AlumnoServlet");
    }
}
