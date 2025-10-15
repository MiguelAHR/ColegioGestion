/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Tarea;
import modelo.TareaDAO;
import modelo.Curso;
import modelo.CursoDAO;
import modelo.Profesor;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class TareaServlet extends HttpServlet {

    TareaDAO dao = new TareaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        try {
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            Curso curso = new CursoDAO().obtenerPorId(cursoId);
            request.setAttribute("curso", curso);

            if ("ver".equals(accion)) {
                request.setAttribute("lista", dao.listarPorCurso(cursoId));
                request.getRequestDispatcher("tareasDocente.jsp").forward(request, response);
                return;
            }

            if ("registrar".equals(accion)) {
                request.getRequestDispatcher("tareaForm.jsp").forward(request, response);
                return;
            }

            if ("editar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Tarea tarea = dao.obtenerPorId(id);
                curso = new CursoDAO().obtenerPorId(tarea.getCursoId());
                request.setAttribute("tarea", tarea);
                request.setAttribute("curso", curso);
                request.getRequestDispatcher("tareaForm.jsp").forward(request, response);
                return;
            }

            if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(id);
                response.sendRedirect("TareaServlet?accion=ver&curso_id=" + cursoId);
                return;
            }

            // fallback
            request.setAttribute("lista", dao.listarPorCurso(cursoId));
            request.getRequestDispatcher("tareasDocente.jsp").forward(request, response);

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

        Tarea t = new Tarea();
        t.setNombre(request.getParameter("nombre"));
        t.setDescripcion(request.getParameter("descripcion"));
        t.setFechaEntrega(request.getParameter("fecha_entrega"));
        t.setActivo(Boolean.parseBoolean(request.getParameter("activo")));
        t.setCursoId(Integer.parseInt(request.getParameter("curso_id")));

        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(t);
        } else {
            t.setId(id);
            resultado = dao.actualizar(t);
        }

        response.sendRedirect("TareaServlet?curso_id=" + t.getCursoId());
    }
}
