/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Nota;
import modelo.NotaDAO;
import modelo.TareaDAO;
import modelo.AlumnoDAO;
import modelo.Curso;
import modelo.CursoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/NotaServlet")
public class NotaServlet extends HttpServlet {

    NotaDAO dao = new NotaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String accion = request.getParameter("accion");
        int cursoId;

        try {
            cursoId = Integer.parseInt(request.getParameter("curso_id"));
        } catch (Exception e) {
            response.sendRedirect("docenteDashboard.jsp");
            return;
        }

        Curso curso = new CursoDAO().obtenerPorId(cursoId);
        if (curso == null) {
            response.sendRedirect("docenteDashboard.jsp");
            return;
        }

        request.setAttribute("curso", curso);

        switch (accion == null ? "listar" : accion) {
            case "listar":
                request.setAttribute("lista", dao.listarPorCurso(cursoId));
                request.getRequestDispatcher("notasDocente.jsp").forward(request, response);
                break;

            case "nuevo":
                request.setAttribute("tareas", new TareaDAO().listarPorCurso(cursoId));
                request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                request.getRequestDispatcher("notaForm.jsp").forward(request, response);
                break;

            case "editar":
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Nota notaEditar = dao.obtenerPorId(idEditar);
                request.setAttribute("nota", notaEditar);
                request.setAttribute("tareas", new TareaDAO().listarPorCurso(cursoId));
                request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                request.getRequestDispatcher("notaForm.jsp").forward(request, response);
                break;

            case "eliminar":
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("NotaServlet?curso_id=" + cursoId);
                break;

            default:
                response.sendRedirect("NotaServlet?curso_id=" + cursoId);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        Nota n = new Nota();
        n.setCursoId(Integer.parseInt(request.getParameter("curso_id")));
        n.setTareaId(Integer.parseInt(request.getParameter("tarea_id")));
        n.setAlumnoId(Integer.parseInt(request.getParameter("alumno_id")));
        String notaStr = request.getParameter("nota");
        if (notaStr == null || notaStr.trim().isEmpty()) {
            response.sendRedirect("NotaServlet?accion=nuevo&curso_id=" + request.getParameter("curso_id"));
            return;
        }
        n.setNota(Double.parseDouble(notaStr.trim()));

        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(n);
        } else {
            n.setId(id);
            resultado = dao.actualizar(n);
        }

        response.sendRedirect("NotaServlet?curso_id=" + n.getCursoId());
    }
}
