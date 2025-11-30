/*
 * SERVLET PARA GESTION COMPLETA DE ESTUDIANTES/ALUMNOS
 * 
 * Funcionalidades: CRUD completo, filtrado por grado, integracion con cursos
 * Roles: Administrador (gestion), Docente (consulta), Padre (consulta limitada)
 * Integracion: Relacion con grados, cursos, asistencias y calificaciones
 */
package controlador;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import modelo.Alumno;
import modelo.AlumnoDAO;
import modelo.GradoDAO;

public class AlumnoServlet extends HttpServlet {

    // DAO para operaciones con la tabla de alumnos
    AlumnoDAO dao = new AlumnoDAO();

    /**
     * METODO GET - CONSULTAS Y NAVEGACION DE ALUMNOS
     * 
     * Acciones soportadas:
     * - listar: Mostrar todos los alumnos (accion por defecto)
     * - filtrar: Filtrar alumnos por grado especifico
     * - nuevo: Formulario para crear nuevo alumno
     * - editar: Formulario para modificar alumno existente
     * - eliminar: Eliminar alumno del sistema
     * - obtenerPorCurso: Endpoint AJAX para obtener alumnos por curso
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // Accion por defecto: listar todos los alumnos con filtros de grado
        if (accion == null) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("alumnos.jsp").forward(request, response);
            return;
        }

        // Filtrar alumnos por grado especifico
        if (accion.equals("filtrar")) {
            String gradoStr = request.getParameter("grado_id");

            request.setAttribute("grados", new GradoDAO().listar());

            if (gradoStr == null || gradoStr.isEmpty()) {
                // Sin filtro: mostrar todos los alumnos
                request.setAttribute("lista", dao.listar());
            } else {
                // Con filtro: mostrar alumnos del grado seleccionado
                int gradoId = Integer.parseInt(gradoStr);
                request.setAttribute("gradoSeleccionado", gradoId);
                request.setAttribute("lista", dao.listarPorGrado(gradoId));
            }

            request.getRequestDispatcher("alumnos.jsp").forward(request, response);
            return;
        }

        // Endpoint AJAX: obtener alumnos por curso (para registro de asistencias/notas)
        if (accion.equals("obtenerPorCurso")) {
            obtenerAlumnosPorCurso(request, response);
            return;
        }

        // Mostrar formulario para nuevo alumno
        if (accion.equals("nuevo")) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.getRequestDispatcher("alumnoForm.jsp").forward(request, response);
            return;
        }

        // Procesar acciones restantes
        switch (accion) {
            case "editar":
                // Cargar formulario de edicion de alumno
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Alumno alumno = dao.obtenerPorId(idEditar);
                request.setAttribute("alumno", alumno);
                request.setAttribute("grados", new GradoDAO().listar());
                request.getRequestDispatcher("alumnoForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // Eliminar alumno del sistema
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("AlumnoServlet");
                break;

            default:
                // Redireccion por defecto
                response.sendRedirect("AlumnoServlet");
        }
    }

    /**
     * METODO POST - CREAR Y ACTUALIZAR ALUMNOS
     * 
     * Maneja el envio de formularios para crear nuevos alumnos
     * y actualizar informacion de alumnos existentes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Determinar si es creacion (id=0) o actualizacion (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // Construir objeto alumno con datos del formulario
        Alumno a = new Alumno();
        a.setNombres(request.getParameter("nombres"));
        a.setApellidos(request.getParameter("apellidos"));
        a.setCorreo(request.getParameter("correo"));
        a.setFechaNacimiento(request.getParameter("fecha_nacimiento"));
        a.setGradoId(Integer.parseInt(request.getParameter("grado_id")));

        // Ejecutar operacion en base de datos
        if (id == 0) {
            dao.agregar(a);
            System.out.println("Nuevo alumno creado: " + a.getNombres() + " " + a.getApellidos());
        } else {
            a.setId(id);
            dao.actualizar(a);
            System.out.println("Alumno actualizado: " + a.getNombres() + " " + a.getApellidos() + " (ID: " + id + ")");
        }

        // Redirigir a la lista principal de alumnos
        response.sendRedirect("AlumnoServlet");
    }

    /**
     * ENDPOINT AJAX - OBTENER ALUMNOS POR CURSO (JSON)
     * 
     * Proposito: Proveer datos para interfaces dinamicas como:
     * - Registro de asistencias por curso
     * - Asignacion de calificaciones
     * - Listas de estudiantes por clase
     * 
     * @return JSON array con datos de alumnos
     */
    private void obtenerAlumnosPorCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        System.out.println("INICIANDO DEBUG obtenerAlumnosPorCurso");

        try {
            // Capturar y validar parametro curso_id
            String cursoIdParam = request.getParameter("curso_id");
            System.out.println("Parametro curso_id recibido: '" + cursoIdParam + "'");
            System.out.println("Todos los parametros: " + request.getParameterMap().toString());

            // Validar parametro obligatorio
            if (cursoIdParam == null || cursoIdParam.isEmpty()) {
                System.out.println("ERROR: curso_id es nulo o vacio");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"error\": \"Parametro curso_id requerido\"}");
                return;
            }

            // Convertir y ejecutar consulta
            int cursoId = Integer.parseInt(cursoIdParam);
            System.out.println("Buscando alumnos para curso ID: " + cursoId);

            List<Alumno> alumnos = dao.obtenerAlumnosPorCurso(cursoId);

            System.out.println("Alumnos encontrados: " + alumnos.size());

            // Log detallado de alumnos encontrados
            for (Alumno alumno : alumnos) {
                System.out.println("Alumno: " + alumno.getId() + " - " + alumno.getNombres() + " " + alumno.getApellidos());
            }

            // Convertir resultados a JSON y enviar respuesta
            String json = convertirAlumnosAJson(alumnos);
            System.out.println("JSON a enviar: " + json);

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();

            System.out.println("FIN DEBUG - Respuesta enviada");

        } catch (NumberFormatException e) {
            // Error en formato de parametro
            System.out.println("ERROR: curso_id no es un numero valido");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\": \"ID de curso invalido: debe ser un numero\"}");
        } catch (Exception e) {
            // Error general en el procesamiento
            System.out.println("ERROR inesperado:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}");
        }
    }

    /**
     * METODO AUXILIAR - CONVERTIR LISTA DE ALUMNOS A JSON MANUALMENTE
     * 
     * Proposito: Generar JSON sin dependencias externas
     * Formato: Array de objetos alumno con todos sus atributos
     */
    private String convertirAlumnosAJson(List<Alumno> alumnos) {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < alumnos.size(); i++) {
            Alumno a = alumnos.get(i);
            json.append("{")
                    .append("\"id\":").append(a.getId()).append(",")
                    .append("\"nombres\":\"").append(escapeJson(a.getNombres())).append("\",")
                    .append("\"apellidos\":\"").append(escapeJson(a.getApellidos())).append("\",")
                    .append("\"correo\":\"").append(escapeJson(a.getCorreo())).append("\",")
                    .append("\"fechaNacimiento\":\"").append(escapeJson(a.getFechaNacimiento())).append("\",")
                    .append("\"gradoId\":").append(a.getGradoId())
                    .append("}");

            // Agregar coma entre elementos (excepto ultimo)
            if (i < alumnos.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        return json.toString();
    }

    /**
     * METODO AUXILIAR - ESCAPAR CARACTERES ESPECIALES EN JSON
     * 
     * Proposito: Prevenir errores de sintaxis JSON y ataques de inyeccion
     * Caracteres escapados: comillas, barras invertidas, saltos de linea, etc.
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}