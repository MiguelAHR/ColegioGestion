/*
 * SERVLET PARA CONSULTA DE ALUMNOS POR GRADO (VISTA PÚBLICA/ADMIN)
 * 
 * Funcionalidades: Listar alumnos con filtro por grado
 * Roles: Admin, Docente (posiblemente)
 * Integración: Relación con grados
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

    // 🎓 DAO PARA OPERACIONES CON ALUMNOS Y GRADOS
    AlumnoDAO alumnoDAO = new AlumnoDAO();
    GradoDAO gradoDAO = new GradoDAO();

    /**
     * 📖 MÉTODO GET - LISTAR ALUMNOS CON FILTRO POR GRADO
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 OBTENER PARÁMETRO DE FILTRO (OPCIONAL)
        String gradoIdParam = request.getParameter("grado");

        // 🎯 CARGAR LISTA DE GRADOS PARA EL FORMULARIO
        List<Grado> grados = gradoDAO.listar();
        request.setAttribute("grados", grados);

        // 🔍 APLICAR FILTRO SI SE ESPECIFICÓ UN GRADO
        if (gradoIdParam != null && !gradoIdParam.isEmpty()) {
            int gradoId = Integer.parseInt(gradoIdParam);
            List<Alumno> alumnos = alumnoDAO.listarPorGrado(gradoId);
            request.setAttribute("alumnos", alumnos);
            request.setAttribute("gradoSeleccionado", gradoId);
        }

        // 🎯 CARGAR VISTA DE LISTA DE ALUMNOS
        request.getRequestDispatcher("verAlumnos.jsp").forward(request, response);
    }
}