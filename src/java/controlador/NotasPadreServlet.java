/*
 * SERVLET PARA CONSULTA DE NOTAS DESDE LA VISTA DE PADRES
 * 
 * Funcionalidades: Listar notas del alumno para vista de padres
 * Roles: Padre
 * Integración: Relación con alumno, cursos y tareas
 */
package controlador;

import modelo.Nota;
import modelo.NotaDAO;
import modelo.Padre;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class NotasPadreServlet extends HttpServlet {

    /**
     * 📖 MÉTODO GET - LISTAR NOTAS DEL ALUMNO (VISTA PADRES)
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

        // 📊 OBTENER NOTAS DEL ALUMNO DESDE LA BASE DE DATOS
        NotaDAO dao = new NotaDAO();
        List<Nota> lista = dao.listarPorAlumno(padre.getAlumnoId());
        request.setAttribute("notas", lista);

        // 🎯 CARGAR VISTA ESPECÍFICA PARA PADRES
        request.getRequestDispatcher("notasPadre.jsp").forward(request, response);
    }
}