/*
 * SERVLET PARA GESTIÓN COMPLETA DE CURSOS ACADÉMICOS
 * 
 * Funcionalidades: CRUD completo de cursos, asignación de profesores, filtros por grado
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

    // 📚 DAO PARA OPERACIONES CON LA TABLA DE CURSOS
    CursoDAO dao = new CursoDAO();

    /**
     * 📖 MÉTODO GET - CONSULTAS Y NAVEGACIÓN
     * 
     * Acciones soportadas:
     * - listar: Muestra todos los cursos
     * - filtrar: Filtra por grado específico
     * - nuevo: Formulario de creación
     * - editar: Formulario de edición
     * - eliminar: Elimina curso
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        System.out.println("➡️ Acción recibida: " + accion);

        // 📋 ACCIÓN POR DEFECTO: LISTAR TODOS LOS CURSOS
        if (accion == null || accion.equals("listar")) {
            // 🎯 CARGAR DATOS NECESARIOS PARA LA VISTA
            request.setAttribute("grados", new GradoDAO().listar()); // Para filtros
            request.setAttribute("lista", dao.listar()); // Lista de cursos
            request.getRequestDispatcher("cursos.jsp").forward(request, response);
            return;
        }

        // 🔍 FILTRAR CURSOS POR GRADO ESPECÍFICO
        if (accion.equals("filtrar")) {
            String gradoStr = request.getParameter("grado_id");

            if (gradoStr == null || gradoStr.isEmpty()) {
                // 🎯 SIN FILTRO: MOSTRAR TODOS LOS CURSOS
                request.setAttribute("lista", dao.listar());
            } else {
                // 🎯 CON FILTRO: MOSTRAR CURSOS DEL GRADO SELECCIONADO
                int gradoId = Integer.parseInt(gradoStr);
                request.setAttribute("lista", dao.listarPorGrado(gradoId));
                request.setAttribute("gradoSeleccionado", gradoId); // Mantener selección
            }

            request.setAttribute("grados", new GradoDAO().listar());
            request.getRequestDispatcher("cursos.jsp").forward(request, response);
            return;
        }

        // ➕ MOSTRAR FORMULARIO PARA NUEVO CURSO
        if (accion.equals("nuevo")) {
            // 🎯 CARGAR LISTAS DESPLEGABLES PARA FORMULARIO
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("profesores", new ProfesorDAO().listar());
            request.getRequestDispatcher("cursoForm.jsp").forward(request, response);
            return;
        }

        // ✏️ MOSTRAR FORMULARIO PARA EDITAR CURSO EXISTENTE
        if (accion.equals("editar")) {
            int idEditar = Integer.parseInt(request.getParameter("id"));
            Curso c = dao.obtenerPorId(idEditar); // 📥 OBTENER CURSO DE BD
            request.setAttribute("cursos", c);
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("profesores", new ProfesorDAO().listar());
            request.getRequestDispatcher("cursoForm.jsp").forward(request, response);
            return;
        }

        // 🗑️ ELIMINAR CURSO CON CONFIRMACIÓN
        if (accion.equals("eliminar")) {
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            boolean resultado = dao.eliminar(idEliminar);
            
            // 📢 MOSTRAR MENSAJE DE RESULTADO
            request.getSession().setAttribute("mensajeCurso", resultado
                    ? "Curso eliminado correctamente"
                    : "Error al eliminar el curso");
            response.sendRedirect("CursoServlet?accion=listar");
            return;
        }
    }

    /**
     * 💾 MÉTODO POST - PROCESAMIENTO DE FORMULARIOS
     * 
     * Funcionalidades:
     * - Crear nuevos cursos
     * - Actualizar cursos existentes
     * - Validar integridad de datos
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 DETERMINAR SI ES CREACIÓN O EDICIÓN (ID = 0 → NUEVO)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        Curso c = new Curso();
        c.setNombre(request.getParameter("nombre"));

        try {
            // ✅ VALIDAR DATOS OBLIGATORIOS: GRADO Y PROFESOR
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

        // 📊 MANEJAR CRÉDITOS (CAMPO OPCIONAL)
        try {
            c.setCreditos(Integer.parseInt(request.getParameter("creditos")));
        } catch (NumberFormatException e) {
            c.setCreditos(0); // 🔧 VALOR POR DEFECTO EN CASO DE ERROR
        }

        // 💾 EJECUTAR OPERACIÓN EN BASE DE DATOS
        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(c); // 🆕 CREAR NUEVO REGISTRO
        } else {
            c.setId(id);
            resultado = dao.actualizar(c); // ✏️ ACTUALIZAR REGISTRO EXISTENTE
        }

        // 📢 CONFIGURAR MENSAJE DE RETROALIMENTACIÓN
        request.getSession().setAttribute("mensajeCurso", resultado
                ? "Curso guardado correctamente"
                : "Error al guardar el curso");

        // 🔄 REDIRIGIR A LA LISTA PRINCIPAL
        response.sendRedirect("CursoServlet?accion=listar");
    }
}