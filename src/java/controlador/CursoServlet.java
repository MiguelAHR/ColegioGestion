/*
 * SERVLET PARA GESTION COMPLETA DE CURSOS ACADEMICOS
 * 
 * Funcionalidades: CRUD completo de cursos, asignacion de profesores, filtros por grado
 * Roles: Administrador
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

    // DAO para operaciones con la tabla de cursos
    CursoDAO dao = new CursoDAO();

    /**
     * METODO GET - CONSULTAS Y NAVEGACION
     * 
     * Acciones soportadas:
     * - listar: Muestra todos los cursos
     * - filtrar: Filtra por grado especifico
     * - nuevo: Formulario de creacion
     * - editar: Formulario de edicion
     * - eliminar: Elimina curso
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        System.out.println("Accion recibida: " + accion);

        // Accion por defecto: listar todos los cursos
        if (accion == null || accion.equals("listar")) {
            // Cargar datos necesarios para la vista
            request.setAttribute("grados", new GradoDAO().listar()); // Para filtros
            request.setAttribute("lista", dao.listar()); // Lista de cursos
            request.getRequestDispatcher("cursos.jsp").forward(request, response);
            return;
        }

        // Filtrar cursos por grado especifico
        if (accion.equals("filtrar")) {
            String gradoStr = request.getParameter("grado_id");

            if (gradoStr == null || gradoStr.isEmpty()) {
                // Sin filtro: mostrar todos los cursos
                request.setAttribute("lista", dao.listar());
            } else {
                // Con filtro: mostrar cursos del grado seleccionado
                int gradoId = Integer.parseInt(gradoStr);
                request.setAttribute("lista", dao.listarPorGrado(gradoId));
                request.setAttribute("gradoSeleccionado", gradoId); // Mantener seleccion
            }

            request.setAttribute("grados", new GradoDAO().listar());
            request.getRequestDispatcher("cursos.jsp").forward(request, response);
            return;
        }

        // Mostrar formulario para nuevo curso
        if (accion.equals("nuevo")) {
            // Cargar listas desplegables para formulario
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("profesores", new ProfesorDAO().listar());
            request.getRequestDispatcher("cursoForm.jsp").forward(request, response);
            return;
        }

        // Mostrar formulario para editar curso existente
        if (accion.equals("editar")) {
            int idEditar = Integer.parseInt(request.getParameter("id"));
            Curso c = dao.obtenerPorId(idEditar); // Obtener curso de BD
            request.setAttribute("cursos", c);
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("profesores", new ProfesorDAO().listar());
            request.getRequestDispatcher("cursoForm.jsp").forward(request, response);
            return;
        }

        // Eliminar curso con confirmacion
        if (accion.equals("eliminar")) {
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            boolean resultado = dao.eliminar(idEliminar);
            
            // Mostrar mensaje de resultado
            request.getSession().setAttribute("mensajeCurso", resultado
                    ? "Curso eliminado correctamente"
                    : "Error al eliminar el curso");
            response.sendRedirect("CursoServlet?accion=listar");
            return;
        }
    }

    /**
     * METODO POST - PROCESAMIENTO DE FORMULARIOS
     * 
     * Funcionalidades:
     * - Crear nuevos cursos
     * - Actualizar cursos existentes
     * - Validar integridad de datos
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Determinar si es creacion o edicion (ID = 0 -> NUEVO)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        Curso c = new Curso();
        c.setNombre(request.getParameter("nombre"));

        try {
            // Validar datos obligatorios: grado y profesor
            String gradoStr = request.getParameter("grado_id");
            String profesorStr = request.getParameter("profesor_id");

            if (gradoStr == null || gradoStr.isEmpty() || profesorStr == null || profesorStr.isEmpty()) {
                throw new IllegalArgumentException("Grado o profesor no seleccionados");
            }

            c.setGradoId(Integer.parseInt(gradoStr));
            c.setProfesorId(Integer.parseInt(profesorStr));

        } catch (Exception e) {
            System.out.println("ERROR: grado_id o profesor_id invalidos");
            e.printStackTrace();
            request.getSession().setAttribute("mensajeCurso", "Error: Debes seleccionar grado y profesor.");
            response.sendRedirect("CursoServlet?accion=nuevo");
            return;
        }

        // Manejar creditos (campo opcional)
        try {
            c.setCreditos(Integer.parseInt(request.getParameter("creditos")));
        } catch (NumberFormatException e) {
            c.setCreditos(0); // Valor por defecto en caso de error
        }

        // Ejecutar operacion en base de datos
        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(c); // Crear nuevo registro
        } else {
            c.setId(id);
            resultado = dao.actualizar(c); // Actualizar registro existente
        }

        // Configurar mensaje de retroalimentacion
        request.getSession().setAttribute("mensajeCurso", resultado
                ? "Curso guardado correctamente"
                : "Error al guardar el curso");

        // Redirigir a la lista principal
        response.sendRedirect("CursoServlet?accion=listar");
    }
}