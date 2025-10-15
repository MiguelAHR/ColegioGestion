/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Observacion;
import modelo.ObservacionDAO;
import modelo.AlumnoDAO;
import modelo.Curso;
import modelo.CursoDAO;
import modelo.Profesor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ObservacionServlet extends HttpServlet {

    ObservacionDAO dao = new ObservacionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        try {
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            Curso curso = new CursoDAO().obtenerPorId(cursoId);
            request.setAttribute("curso", curso);

            switch (accion) {
                case "listar":
                    request.setAttribute("lista", dao.listarPorCurso(cursoId));
                    request.getRequestDispatcher("observacionesDocente.jsp").forward(request, response);
                    break;

                case "registrar":
                    request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                    request.getRequestDispatcher("observacionForm.jsp").forward(request, response);
                    break;

                case "editar":
                    int idEditar = Integer.parseInt(request.getParameter("id"));
                    Observacion obs = dao.obtenerPorId(idEditar);
                    request.setAttribute("observacion", obs);
                    request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                    request.getRequestDispatcher("observacionForm.jsp").forward(request, response);
                    break;

                case "eliminar":
                    int idEliminar = Integer.parseInt(request.getParameter("id"));
                    dao.eliminar(idEliminar);
                    response.sendRedirect("ObservacionServlet?accion=listar&curso_id=" + cursoId);
                    break;

                default:
                    response.sendRedirect("docenteDashboard.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("docenteDashboard.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        Observacion o = new Observacion();
        o.setCursoId(Integer.parseInt(request.getParameter("curso_id")));
        o.setAlumnoId(Integer.parseInt(request.getParameter("alumno_id")));
        o.setTexto(request.getParameter("texto"));

        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(o);
        } else {
            o.setId(id);
            resultado = dao.actualizar(o);
        }

        response.sendRedirect("ObservacionServlet?accion=listar&curso_id=" + o.getCursoId());
    }
}
    
