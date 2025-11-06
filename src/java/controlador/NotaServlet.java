/*
 * SERVLET PARA GESTIÓN DE CALIFICACIONES ACADÉMICAS
 * 
 * Funcionalidades: CRUD completo de notas, registro por tarea y alumno
 * Roles: Docente (gestión completa), Padre (consulta de notas de su hijo)
 * Integración: Relación con tareas, alumnos, cursos y profesores
 */
package controlador;

import modelo.Nota;
import modelo.NotaDAO;
import modelo.TareaDAO;
import modelo.AlumnoDAO;
import modelo.Curso;
import modelo.CursoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/NotaServlet")
public class NotaServlet extends HttpServlet {

    // 📊 DAO PARA OPERACIONES CON LA TABLA DE NOTAS
    NotaDAO dao = new NotaDAO();

    /**
     * 📖 MÉTODO GET - CONSULTAS Y GESTIÓN DE CALIFICACIONES
     * 
     * Acciones soportadas:
     * - listar: Mostrar todas las notas de un curso
     * - nuevo: Formulario para asignar nueva calificación
     * - editar: Formulario para modificar calificación existente
     * - eliminar: Eliminar calificación del sistema
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String accion = request.getParameter("accion");
        int cursoId;

        try {
            // 📥 OBTENER ID DEL CURSO (PARÁMETRO OBLIGATORIO)
            cursoId = Integer.parseInt(request.getParameter("curso_id"));
        } catch (Exception e) {
            // 🚨 ERROR: REDIRIGIR AL DASHBOARD SI NO HAY CURSO_ID
            response.sendRedirect("docenteDashboard.jsp");
            return;
        }

        // 🔍 VALIDAR QUE EL CURSO EXISTA
        Curso curso = new CursoDAO().obtenerPorId(cursoId);
        if (curso == null) {
            response.sendRedirect("docenteDashboard.jsp");
            return;
        }

        request.setAttribute("curso", curso);

        // 🎯 EJECUTAR ACCIÓN SEGÚN PARÁMETRO (VALOR POR DEFECTO: "listar")
        switch (accion == null ? "listar" : accion) {
            case "listar":
                // 📋 LISTAR TODAS LAS CALIFICACIONES DEL CURSO
                request.setAttribute("lista", dao.listarPorCurso(cursoId));
                request.getRequestDispatcher("notasDocente.jsp").forward(request, response);
                break;

            case "nuevo":
                // ➕ FORMULARIO PARA NUEVA CALIFICACIÓN
                request.setAttribute("tareas", new TareaDAO().listarPorCurso(cursoId));
                request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                request.getRequestDispatcher("notaForm.jsp").forward(request, response);
                break;

            case "editar":
                // ✏️ FORMULARIO PARA EDITAR CALIFICACIÓN EXISTENTE
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Nota notaEditar = dao.obtenerPorId(idEditar);
                request.setAttribute("nota", notaEditar);
                request.setAttribute("tareas", new TareaDAO().listarPorCurso(cursoId));
                request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                request.getRequestDispatcher("notaForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // 🗑️ ELIMINAR CALIFICACIÓN
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("NotaServlet?curso_id=" + cursoId);
                break;

            default:
                // 🔄 REDIRECCIÓN POR DEFECTO
                response.sendRedirect("NotaServlet?curso_id=" + cursoId);
        }
    }

    /**
     * 💾 MÉTODO POST - GUARDAR CALIFICACIONES
     * 
     * Maneja el envío de formularios para crear nuevas calificaciones
     * y actualizar calificaciones existentes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 DETERMINAR SI ES CREACIÓN (id=0) O ACTUALIZACIÓN (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // 🧩 CONSTRUIR OBJETO NOTA CON DATOS DEL FORMULARIO
        Nota n = new Nota();
        n.setCursoId(Integer.parseInt(request.getParameter("curso_id")));
        n.setTareaId(Integer.parseInt(request.getParameter("tarea_id")));
        n.setAlumnoId(Integer.parseInt(request.getParameter("alumno_id")));
        
        // ✅ VALIDAR QUE LA NOTA NO ESTÉ VACÍA
        String notaStr = request.getParameter("nota");
        if (notaStr == null || notaStr.trim().isEmpty()) {
            response.sendRedirect("NotaServlet?accion=nuevo&curso_id=" + request.getParameter("curso_id"));
            return;
        }
        n.setNota(Double.parseDouble(notaStr.trim()));

        // 💾 EJECUTAR OPERACIÓN EN BASE DE DATOS
        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(n); // 🆕 NUEVA CALIFICACIÓN
            System.out.println("✅ Nueva calificación registrada: " + n.getNota() + " (Alumno: " + n.getAlumnoId() + ")");
        } else {
            n.setId(id);
            resultado = dao.actualizar(n); // ✏️ ACTUALIZAR CALIFICACIÓN
            System.out.println("✅ Calificación actualizada: " + n.getNota() + " (ID: " + id + ")");
        }

        // 🔄 REDIRIGIR A LA LISTA DE CALIFICACIONES DEL CURSO
        response.sendRedirect("NotaServlet?curso_id=" + n.getCursoId());
    }
}