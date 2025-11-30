/*
 * SERVLET PARA GESTION DE CALIFICACIONES ACADEMICAS
 * 
 * Funcionalidades: CRUD completo de notas, registro por tarea y alumno
 * Roles: Docente (gestion completa), Padre (consulta de notas de su hijo)
 * Integracion: Relacion con tareas, alumnos, cursos y profesores
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

    // DAO para operaciones con la tabla de notas
    NotaDAO dao = new NotaDAO();

    /**
     * METODO GET - CONSULTAS Y GESTION DE CALIFICACIONES
     * 
     * Acciones soportadas:
     * - listar: Mostrar todas las notas de un curso
     * - nuevo: Formulario para asignar nueva calificacion
     * - editar: Formulario para modificar calificacion existente
     * - eliminar: Eliminar calificacion del sistema
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String accion = request.getParameter("accion");
        int cursoId;

        try {
            // Obtener ID del curso (parametro obligatorio)
            cursoId = Integer.parseInt(request.getParameter("curso_id"));
        } catch (Exception e) {
            // Error: redirigir al dashboard si no hay curso_id
            response.sendRedirect("docenteDashboard.jsp");
            return;
        }

        // Validar que el curso exista
        Curso curso = new CursoDAO().obtenerPorId(cursoId);
        if (curso == null) {
            response.sendRedirect("docenteDashboard.jsp");
            return;
        }

        request.setAttribute("curso", curso);

        // Ejecutar accion segun parametro (valor por defecto: "listar")
        switch (accion == null ? "listar" : accion) {
            case "listar":
                // Listar todas las calificaciones del curso
                request.setAttribute("lista", dao.listarPorCurso(cursoId));
                request.getRequestDispatcher("notasDocente.jsp").forward(request, response);
                break;

            case "nuevo":
                // Formulario para nueva calificacion
                request.setAttribute("tareas", new TareaDAO().listarPorCurso(cursoId));
                request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                request.getRequestDispatcher("notaForm.jsp").forward(request, response);
                break;

            case "editar":
                // Formulario para editar calificacion existente
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Nota notaEditar = dao.obtenerPorId(idEditar);
                request.setAttribute("nota", notaEditar);
                request.setAttribute("tareas", new TareaDAO().listarPorCurso(cursoId));
                request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                request.getRequestDispatcher("notaForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // Eliminar calificacion
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("NotaServlet?curso_id=" + cursoId);
                break;

            default:
                // Redireccion por defecto
                response.sendRedirect("NotaServlet?curso_id=" + cursoId);
        }
    }

    /**
     * METODO POST - GUARDAR CALIFICACIONES
     * 
     * Maneja el envio de formularios para crear nuevas calificaciones
     * y actualizar calificaciones existentes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Determinar si es creacion (id=0) o actualizacion (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // Construir objeto nota con datos del formulario
        Nota n = new Nota();
        n.setCursoId(Integer.parseInt(request.getParameter("curso_id")));
        n.setTareaId(Integer.parseInt(request.getParameter("tarea_id")));
        n.setAlumnoId(Integer.parseInt(request.getParameter("alumno_id")));
        
        // Validar que la nota no este vacia
        String notaStr = request.getParameter("nota");
        if (notaStr == null || notaStr.trim().isEmpty()) {
            response.sendRedirect("NotaServlet?accion=nuevo&curso_id=" + request.getParameter("curso_id"));
            return;
        }
        n.setNota(Double.parseDouble(notaStr.trim()));

        // Ejecutar operacion en base de datos
        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(n); // Nueva calificacion
            System.out.println("Nueva calificacion registrada: " + n.getNota() + " (Alumno: " + n.getAlumnoId() + ")");
        } else {
            n.setId(id);
            resultado = dao.actualizar(n); // Actualizar calificacion
            System.out.println("Calificacion actualizada: " + n.getNota() + " (ID: " + id + ")");
        }

        // Redirigir a la lista de calificaciones del curso
        response.sendRedirect("NotaServlet?curso_id=" + n.getCursoId());
    }
}