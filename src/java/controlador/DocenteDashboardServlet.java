/*
 * SERVLET PARA CARGAR EL DASHBOARD ESPECÍFICO DE DOCENTES
 * 
 * Funcionalidades: Cargar cursos del docente y redirigir a dashboard
 * Roles: Docente
 * Integración: Relación con cursos y profesores
 */
package controlador;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Profesor;
import modelo.Curso;
import modelo.CursoDAO;

@WebServlet("/DocenteDashboardServlet")
public class DocenteDashboardServlet extends HttpServlet {

    /**
     * 📖 MÉTODO GET - CARGAR DASHBOARD DEL DOCENTE
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");
        
        // 🔐 VERIFICAR QUE EL USUARIO ESTÉ AUTENTICADO COMO DOCENTE
        if (docente == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            System.out.println("🔍 Cargando cursos para profesor ID: " + docente.getId());
            
            // 📚 CARGAR LOS CURSOS DEL DOCENTE
            CursoDAO cursoDAO = new CursoDAO();
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());
            
            System.out.println("📊 Cursos encontrados: " + (cursos != null ? cursos.size() : 0));
            
            // 📝 LOG DETALLADO DE CURSOS
            if (cursos != null) {
                for (Curso curso : cursos) {
                    System.out.println("   - " + curso.getNombre() + " (Grado: " + curso.getGradoNombre() + ")");
                }
            }
            
            // 📤 PONER LOS CURSOS EN EL REQUEST PARA QUE LOS USE EL JSP
            request.setAttribute("misCursos", cursos);
            
            // 🎯 REDIRIGIR AL DASHBOARD DEL DOCENTE
            request.getRequestDispatcher("docenteDashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.out.println("❌ Error en DocenteDashboardServlet:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar los cursos: " + e.getMessage());
            response.sendRedirect("error.jsp");
        }
    }
}