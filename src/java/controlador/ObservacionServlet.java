/*
 * SERVLET PARA GESTIÓN DE OBSERVACIONES SOBRE ALUMNOS
 * 
 * Funcionalidades: CRUD completo de observaciones, por curso y alumno
 * Roles: Docente (gestión), Padre (consulta)
 * Integración: Relación con cursos, alumnos y profesores
 */
package controlador;

import modelo.Observacion;
import modelo.ObservacionDAO;
import modelo.AlumnoDAO;
import modelo.Curso;
import modelo.CursoDAO;
import modelo.Profesor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ObservacionServlet extends HttpServlet {

    // 📝 DAO PARA OPERACIONES CON LA TABLA DE OBSERVACIONES
    ObservacionDAO dao = new ObservacionDAO();

    /**
     * 📖 MÉTODO GET - CONSULTAS Y GESTIÓN DE OBSERVACIONES
     * 
     * Acciones soportadas:
     * - listar: Listar observaciones de un curso
     * - registrar: Formulario para crear nueva observación
     * - editar: Formulario para modificar observación existente
     * - eliminar: Eliminar observación
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar"; // 🎯 ACCIÓN POR DEFECTO
        }

        try {
            // 📥 OBTENER ID DEL CURSO (PARÁMETRO OBLIGATORIO)
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            Curso curso = new CursoDAO().obtenerPorId(cursoId);
            request.setAttribute("curso", curso);

            // 🎯 EJECUTAR ACCIÓN SEGÚN PARÁMETRO
            switch (accion) {
                case "listar":
                    // 📋 LISTAR OBSERVACIONES DEL CURSO
                    request.setAttribute("lista", dao.listarPorCurso(cursoId));
                    request.getRequestDispatcher("observacionesDocente.jsp").forward(request, response);
                    break;

                case "registrar":
                    // ➕ FORMULARIO PARA NUEVA OBSERVACIÓN
                    request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                    request.getRequestDispatcher("observacionForm.jsp").forward(request, response);
                    break;

                case "editar":
                    // ✏️ FORMULARIO PARA EDITAR OBSERVACIÓN EXISTENTE
                    int idEditar = Integer.parseInt(request.getParameter("id"));
                    Observacion obs = dao.obtenerPorId(idEditar);
                    request.setAttribute("observacion", obs);
                    request.setAttribute("alumnos", new AlumnoDAO().listarPorGrado(curso.getGradoId()));
                    request.getRequestDispatcher("observacionForm.jsp").forward(request, response);
                    break;

                case "eliminar":
                    // 🗑️ ELIMINAR OBSERVACIÓN
                    int idEliminar = Integer.parseInt(request.getParameter("id"));
                    dao.eliminar(idEliminar);
                    response.sendRedirect("ObservacionServlet?accion=listar&curso_id=" + cursoId);
                    break;

                default:
                    // 🔄 REDIRECCIÓN POR DEFECTO
                    response.sendRedirect("docenteDashboard.jsp");
            }

        } catch (Exception e) {
            // 🚨 MANEJO DE ERRORES
            e.printStackTrace();
            response.sendRedirect("docenteDashboard.jsp");
        }
    }

    /**
     * 💾 MÉTODO POST - CREAR Y ACTUALIZAR OBSERVACIONES
     * 
     * Maneja el envío de formularios para crear nuevas observaciones
     * y actualizar observaciones existentes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 DETERMINAR SI ES CREACIÓN (id=0) O ACTUALIZACIÓN (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // 🧩 CONSTRUIR OBJETO OBSERVACIÓN CON DATOS DEL FORMULARIO
        Observacion o = new Observacion();
        o.setCursoId(Integer.parseInt(request.getParameter("curso_id")));
        o.setAlumnoId(Integer.parseInt(request.getParameter("alumno_id")));
        o.setTexto(request.getParameter("texto"));

        // 💾 EJECUTAR OPERACIÓN EN BASE DE DATOS
        boolean resultado;
        if (id == 0) {
            resultado = dao.agregar(o); // 🆕 NUEVA OBSERVACIÓN
            System.out.println("✅ Nueva observación creada para alumno ID: " + o.getAlumnoId());
        } else {
            o.setId(id);
            resultado = dao.actualizar(o); // ✏️ ACTUALIZAR OBSERVACIÓN
            System.out.println("✅ Observación actualizada (ID: " + id + ")");
        }

        // 🔄 REDIRIGIR A LA LISTA DE OBSERVACIONES DEL CURSO
        response.sendRedirect("ObservacionServlet?accion=listar&curso_id=" + o.getCursoId());
    }
}