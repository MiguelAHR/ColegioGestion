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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");
        
        // Verificar si el usuario está autenticado y es docente
        if (docente == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        try {
            // Cargar los cursos del docente
            CursoDAO cursoDAO = new CursoDAO();
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());
            
            // Poner los cursos en el request para que los use el JSP
            request.setAttribute("misCursos", cursos);
            
            // Redirigir al dashboard del docente
            request.getRequestDispatcher("docenteDashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}