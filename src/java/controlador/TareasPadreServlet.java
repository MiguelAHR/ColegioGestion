/*
 * SERVLET PARA CONSULTA DE TAREAS DESDE LA VISTA DE PADRES
 * 
 * Funcionalidades: Listar tareas del alumno para vista de padres
 * Roles: Padre
 * Integración: Relación con alumno y cursos
 */
package controlador;

import modelo.Tarea;
import modelo.TareaDAO;
import modelo.Padre;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class TareasPadreServlet extends HttpServlet {

    /**
     * 📖 MÉTODO GET - LISTAR TAREAS DEL ALUMNO (VISTA PADRES)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        // 🔐 VERIFICAR AUTENTICACIÓN Y DATOS DE PADRE
        if (padre == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 📚 OBTENER TAREAS DEL ALUMNO DESDE LA BASE DE DATOS
        TareaDAO dao = new TareaDAO();
        List<Tarea> lista = dao.listarPorAlumno(padre.getAlumnoId());
        request.setAttribute("tareas", lista);

        // 🎯 CARGAR VISTA ESPECÍFICA PARA PADRES
        request.getRequestDispatcher("tareasPadre.jsp").forward(request, response);
    }
}