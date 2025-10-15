/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import modelo.Curso;
import modelo.CursoDAO;
import modelo.GradoDAO;
import modelo.ProfesorDAO;

@WebServlet("/CursoServlet")
public class CursoServlet extends HttpServlet {

    CursoDAO dao = new CursoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        System.out.println("➡️ Acción recibida: " + accion);

        if (accion == null || accion.equals("listar")) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("cursos.jsp").forward(request, response);
            return;
        }

        if (accion.equals("filtrar")) {
            String gradoStr = request.getParameter("grado_id");

            if (gradoStr == null || gradoStr.isEmpty()) {
                request.setAttribute("lista", dao.listar());
            } else {
                int gradoId = Integer.parseInt(gradoStr);
                request.setAttribute("lista", dao.listarPorGrado(gradoId));
                request.setAttribute("gradoSeleccionado", gradoId);
            }

            request.setAttribute("grados", new GradoDAO().listar());
            request.getRequestDispatcher("cursos.jsp").forward(request, response);
            return;
        }

        if (accion.equals("nuevo")) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("profesores", new ProfesorDAO().listar());
            request.getRequestDispatcher("cursoForm.jsp").forward(request, response);
            return;
        }

        if (accion.equals("editar")) {
            int idEditar = Integer.parseInt(request.getParameter("id"));
            Curso c = dao.obtenerPorId(idEditar);
            request.setAttribute("cursos", c);
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("profesores", new ProfesorDAO().listar());
            request.getRequestDispatcher("cursoForm.jsp").forward(request, response);
            return;
        }

        if (accion.equals("eliminar")) {
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            boolean resultado = dao.eliminar(idEliminar);
            request.getSession().setAttribute("mensajeCurso", resultado
                    ? "Curso eliminado correctamente"
                    : "Error al eliminar el curso");
            response.sendRedirect("CursoServlet?accion=listar");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        Curso c = new Curso();
        c.setNombre(request.getParameter("nombre"));

        try {
            String gradoStr = request.getParameter("grado_id");
            String profesorStr = request.getParameter("profesor_id");

            if (gradoStr == null || gradoStr.isEmpty() || profesorStr == null || profesorStr.isEmpty()) {
                throw new IllegalArgumentException("Grado o profesor no seleccionados");
            }

            c.setGradoId(Integer.parseInt(gradoStr));
            c.setProfesorId(Integer.parseInt(profesorStr));

        } catch (Exception e) {
            System.out.println("❌ ERROR: grado_id o profesor_id inválidos");
            e.printStackTrace();
            request.getSession().setAttribute("mensajeCurso", "Error: Debes seleccionar grado y profesor.");
            response.sendRedirect("CursoServlet?accion=nuevo");
            return;
        }

        try {
            c.setCreditos(Integer.parseInt(request.getParameter("creditos")));
        } catch (NumberFormatException e) {
            c.setCreditos(0);
        }

        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(c);
        } else {
            c.setId(id);
            resultado = dao.actualizar(c);
        }

        request.getSession().setAttribute("mensajeCurso", resultado
                ? "Curso guardado correctamente"
                : "Error al guardar el curso");

        response.sendRedirect("CursoServlet?accion=listar");
    }
}
