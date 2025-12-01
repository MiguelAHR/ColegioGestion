package controlador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import modelo.Curso;
import modelo.CursoDAO;
import modelo.GradoDAO;
import modelo.ProfesorDAO;
import modelo.Profesor;

@WebServlet("/CursoServlet")
public class CursoServlet extends HttpServlet {

    CursoDAO dao = new CursoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String rol = (String) session.getAttribute("rol");
        String accion = request.getParameter("accion");

        // VALIDACIÓN: Solo admin puede gestionar cursos (crear, editar, eliminar)
        if (("nuevo".equals(accion) || "editar".equals(accion) || "eliminar".equals(accion)) && !"admin".equals(rol)) {
            response.sendRedirect("acceso_denegado.jsp");
            return;
        }

        // Para docentes que quieren ver cursos, validar ownership
        if ("docente".equals(rol) && ("editar".equals(accion) || "eliminar".equals(accion))) {
            Profesor docente = (Profesor) session.getAttribute("docente");
            if (docente != null) {
                try {
                    int cursoId = Integer.parseInt(request.getParameter("id"));
                    if (!isCursoAssignedToProfesor(cursoId, docente.getId())) {
                        session.setAttribute("error", "No tienes permisos para acceder a este curso.");
                        response.sendRedirect("acceso_denegado.jsp");
                        return;
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "ID de curso inválido.");
                    response.sendRedirect("cursos.jsp");
                    return;
                }
            }
        }

        System.out.println("Accion recibida: " + accion);

        if (accion == null || accion.equals("listar")) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("cursos.jsp").forward(request, response);
            return;
        }

        if (accion.equals("filtrar")) {
            String gradoStr = request.getParameter("grado_id");
            request.setAttribute("grados", new GradoDAO().listar());

            if (gradoStr == null || gradoStr.isEmpty()) {
                request.setAttribute("lista", dao.listar());
            } else {
                int gradoId = Integer.parseInt(gradoStr);
                request.setAttribute("lista", dao.listarPorGrado(gradoId));
                request.setAttribute("gradoSeleccionado", gradoId);
            }

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
            if (c == null) {
                session.setAttribute("error", "Curso no encontrado.");
                response.sendRedirect("CursoServlet");
                return;
            }
            request.setAttribute("cursos", c);
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("profesores", new ProfesorDAO().listar());
            request.getRequestDispatcher("cursoForm.jsp").forward(request, response);
            return;
        }

        if (accion.equals("eliminar")) {
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            boolean resultado = dao.eliminar(idEliminar);
            
            session.setAttribute("mensajeCurso", resultado
                    ? "Curso eliminado correctamente"
                    : "Error al eliminar el curso");
            response.sendRedirect("CursoServlet?accion=listar");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String rol = (String) session.getAttribute("rol");
        
        // Solo admin puede crear/editar cursos
        if (!"admin".equals(rol)) {
            response.sendRedirect("acceso_denegado.jsp");
            return;
        }

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
            System.out.println("ERROR: grado_id o profesor_id invalidos");
            session.setAttribute("error", "Error: Debes seleccionar grado y profesor.");
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

        session.setAttribute("mensajeCurso", resultado
                ? "Curso guardado correctamente"
                : "Error al guardar el curso");

        response.sendRedirect("CursoServlet?accion=listar");
    }

    /**
     * METODO AUXILIAR PARA VERIFICAR ASIGNACIÓN CURSO-PROFESOR
     */
    private boolean isCursoAssignedToProfesor(int cursoId, int profesorId) {
        String sql = "SELECT COUNT(*) as count FROM cursos WHERE id = ? AND profesor_id = ?";
        
        try (java.sql.Connection con = conexion.Conexion.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, cursoId);
            ps.setInt(2, profesorId);
            java.sql.ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (Exception e) {
            System.out.println("Error al verificar asignación curso-profesor: " + e.getMessage());
        }
        
        return false;
    }
}