/*
 * SERVLET PARA GESTIÓN DE TAREAS Y ACTIVIDADES ACADÉMICAS
 * 
 * Funcionalidades: CRUD completo de tareas, asignación por curso, fechas de entrega
 * Roles: Docente (gestión completa), Padre (consulta de tareas de su hijo)
 * Integración: Relación con cursos, alumnos y calificaciones
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

    // 📝 DAO PARA OPERACIONES CON LA TABLA DE TAREAS
    TareaDAO dao = new TareaDAO();

    /**
     * 📖 MÉTODO GET - CONSULTAS Y GESTIÓN DE TAREAS
     * 
     * Acciones soportadas:
     * - ver: Listar tareas de un curso específico
     * - registrar: Formulario para crear nueva tarea
     * - editar: Formulario para modificar tarea existente
     * - eliminar: Eliminar tarea del sistema
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        try {
            // 📥 OBTENER ID DEL CURSO (PARÁMETRO OBLIGATORIO PARA LA MAYORÍA DE ACCIONES)
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            Curso curso = new CursoDAO().obtenerPorId(cursoId);
            request.setAttribute("curso", curso);

            // 🎯 EJECUTAR ACCIÓN SEGÚN PARÁMETRO
            if ("ver".equals(accion)) {
                // 📋 LISTAR TODAS LAS TAREAS DEL CURSO
                request.setAttribute("lista", dao.listarPorCurso(cursoId));
                request.getRequestDispatcher("tareasDocente.jsp").forward(request, response);
                return;
            }

            if ("registrar".equals(accion)) {
                // ➕ MOSTRAR FORMULARIO PARA NUEVA TAREA
                request.getRequestDispatcher("tareaForm.jsp").forward(request, response);
                return;
            }

            if ("editar".equals(accion)) {
                // ✏️ CARGAR FORMULARIO DE EDICIÓN DE TAREA
                int id = Integer.parseInt(request.getParameter("id"));
                Tarea tarea = dao.obtenerPorId(id);
                curso = new CursoDAO().obtenerPorId(tarea.getCursoId());
                request.setAttribute("tarea", tarea);
                request.setAttribute("curso", curso);
                request.getRequestDispatcher("tareaForm.jsp").forward(request, response);
                return;
            }

            if ("eliminar".equals(accion)) {
                // 🗑️ ELIMINAR TAREA DEL SISTEMA
                int id = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(id);
                response.sendRedirect("TareaServlet?accion=ver&curso_id=" + cursoId);
                return;
            }

            // 🔄 FALLBACK: SI NO HAY ACCIÓN ESPECÍFICA, LISTAR TAREAS
            request.setAttribute("lista", dao.listarPorCurso(cursoId));
            request.getRequestDispatcher("tareasDocente.jsp").forward(request, response);

        } catch (Exception e) {
            // 🚨 MANEJO DE ERRORES - REDIRIGIR AL DASHBOARD
            e.printStackTrace();
            response.sendRedirect("docenteDashboard.jsp");
        }
    }

    /**
     * 💾 MÉTODO POST - CREAR Y ACTUALIZAR TAREAS
     * 
     * Maneja el envío de formularios para crear nuevas tareas
     * y actualizar tareas existentes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 DETERMINAR SI ES CREACIÓN (id=0) O ACTUALIZACIÓN (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // 🧩 CONSTRUIR OBJETO TAREA CON DATOS DEL FORMULARIO
        Tarea t = new Tarea();
        t.setNombre(request.getParameter("nombre"));
        t.setDescripcion(request.getParameter("descripcion"));
        t.setFechaEntrega(request.getParameter("fecha_entrega"));
        t.setActivo(Boolean.parseBoolean(request.getParameter("activo")));
        t.setCursoId(Integer.parseInt(request.getParameter("curso_id")));

        // 💾 EJECUTAR OPERACIÓN EN BASE DE DATOS
        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(t); // 🆕 CREAR NUEVA TAREA
            System.out.println("✅ Nueva tarea creada: " + t.getNombre() + " (Curso: " + t.getCursoId() + ")");
        } else {
            t.setId(id);
            resultado = dao.actualizar(t); // ✏️ ACTUALIZAR TAREA EXISTENTE
            System.out.println("✅ Tarea actualizada: " + t.getNombre() + " (ID: " + id + ")");
        }

        // 🔄 REDIRIGIR A LA LISTA DE TAREAS DEL CURSO
        response.sendRedirect("TareaServlet?curso_id=" + t.getCursoId());
    }
}