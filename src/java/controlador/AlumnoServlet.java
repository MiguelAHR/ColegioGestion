/*
 * SERVLET PARA GESTIÓN COMPLETA DE ESTUDIANTES/ALUMNOS
 * 
 * Funcionalidades: CRUD completo, filtrado por grado, integración con cursos
 * Roles: Administrador (gestión), Docente (consulta), Padre (consulta limitada)
 * Integración: Relación con grados, cursos, asistencias y calificaciones
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

// ❌ NOTA: Anotación @WebServlet eliminada para configuración en web.xml
public class AlumnoServlet extends HttpServlet {

    // 🎓 DAO PARA OPERACIONES CON LA TABLA DE ALUMNOS
    AlumnoDAO dao = new AlumnoDAO();

    /**
     * 📖 MÉTODO GET - CONSULTAS Y NAVEGACIÓN DE ALUMNOS
     * 
     * Acciones soportadas:
     * - listar: Mostrar todos los alumnos (acción por defecto)
     * - filtrar: Filtrar alumnos por grado específico
     * - nuevo: Formulario para crear nuevo alumno
     * - editar: Formulario para modificar alumno existente
     * - eliminar: Eliminar alumno del sistema
     * - obtenerPorCurso: Endpoint AJAX para obtener alumnos por curso
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // 📋 ACCIÓN POR DEFECTO: LISTAR TODOS LOS ALUMNOS CON FILTROS DE GRADO
        if (accion == null) {
            request.setAttribute("grados", new GradoDAO().listar()); // 🎯 CARGAR GRADOS PARA FILTROS
            request.setAttribute("lista", dao.listar()); // 📚 CARGAR TODOS LOS ALUMNOS
            request.getRequestDispatcher("alumnos.jsp").forward(request, response);
            return;
        }

        // 🔍 FILTRAR ALUMNOS POR GRADO ESPECÍFICO
        if (accion.equals("filtrar")) {
            String gradoStr = request.getParameter("grado_id");

            // 🎯 CARGAR LISTA DE GRADOS PARA EL FORMULARIO
            request.setAttribute("grados", new GradoDAO().listar());

            if (gradoStr == null || gradoStr.isEmpty()) {
                // 📋 SIN FILTRO: MOSTRAR TODOS LOS ALUMNOS
                request.setAttribute("lista", dao.listar());
            } else {
                // 🎯 CON FILTRO: MOSTRAR ALUMNOS DEL GRADO SELECCIONADO
                int gradoId = Integer.parseInt(gradoStr);
                request.setAttribute("gradoSeleccionado", gradoId); // 💾 GUARDAR SELECCIÓN
                request.setAttribute("lista", dao.listarPorGrado(gradoId));
            }

            request.getRequestDispatcher("alumnos.jsp").forward(request, response);
            return;
        }

        // 🔄 ENDPOINT AJAX: OBTENER ALUMNOS POR CURSO (PARA REGISTRO DE ASISTENCIAS/NOTAS)
        if (accion.equals("obtenerPorCurso")) {
            obtenerAlumnosPorCurso(request, response);
            return;
        }

        // ➕ MOSTRAR FORMULARIO PARA NUEVO ALUMNO
        if (accion.equals("nuevo")) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.getRequestDispatcher("alumnoForm.jsp").forward(request, response);
            return;
        }

        // 🎯 PROCESAR ACCIONES RESTANTES
        switch (accion) {
            case "editar":
                // ✏️ CARGAR FORMULARIO DE EDICIÓN DE ALUMNO
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Alumno alumno = dao.obtenerPorId(idEditar);
                request.setAttribute("alumno", alumno);
                request.setAttribute("grados", new GradoDAO().listar());
                request.getRequestDispatcher("alumnoForm.jsp").forward(request, response);
                break;

            case "eliminar":
                // 🗑️ ELIMINAR ALUMNO DEL SISTEMA
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("AlumnoServlet");
                break;

            default:
                // 🔄 REDIRECCIÓN POR DEFECTO
                response.sendRedirect("AlumnoServlet");
        }
    }

    /**
     * 💾 MÉTODO POST - CREAR Y ACTUALIZAR ALUMNOS
     * 
     * Maneja el envío de formularios para crear nuevos alumnos
     * y actualizar información de alumnos existentes
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 📥 DETERMINAR SI ES CREACIÓN (id=0) O ACTUALIZACIÓN (id>0)
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        // 🧩 CONSTRUIR OBJETO ALUMNO CON DATOS DEL FORMULARIO
        Alumno a = new Alumno();
        a.setNombres(request.getParameter("nombres"));
        a.setApellidos(request.getParameter("apellidos"));
        a.setCorreo(request.getParameter("correo"));
        a.setFechaNacimiento(request.getParameter("fecha_nacimiento"));
        a.setGradoId(Integer.parseInt(request.getParameter("grado_id")));

        // 💾 EJECUTAR OPERACIÓN EN BASE DE DATOS
        if (id == 0) {
            dao.agregar(a); // 🆕 CREAR NUEVO ALUMNO
            System.out.println("✅ Nuevo alumno creado: " + a.getNombres() + " " + a.getApellidos());
        } else {
            a.setId(id);
            dao.actualizar(a); // ✏️ ACTUALIZAR ALUMNO EXISTENTE
            System.out.println("✅ Alumno actualizado: " + a.getNombres() + " " + a.getApellidos() + " (ID: " + id + ")");
        }

        // 🔄 REDIRIGIR A LA LISTA PRINCIPAL DE ALUMNOS
        response.sendRedirect("AlumnoServlet");
    }

    /**
     * 🔄 ENDPOINT AJAX - OBTENER ALUMNOS POR CURSO (JSON)
     * 
     * Propósito: Proveer datos para interfaces dinámicas como:
     * - Registro de asistencias por curso
     * - Asignación de calificaciones
     * - Listas de estudiantes por clase
     * 
     * @return JSON array con datos de alumnos
     */
    private void obtenerAlumnosPorCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🎯 CONFIGURAR RESPUESTA COMO JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        System.out.println("=== 🔍 INICIANDO DEBUG obtenerAlumnosPorCurso ===");

        try {
            // 📥 CAPTURAR Y VALIDAR PARÁMETRO CURSO_ID
            String cursoIdParam = request.getParameter("curso_id");
            System.out.println("📥 Parámetro curso_id recibido: '" + cursoIdParam + "'");
            System.out.println("📥 Todos los parámetros: " + request.getParameterMap().toString());

            // 🚨 VALIDAR PARÁMETRO OBLIGATORIO
            if (cursoIdParam == null || cursoIdParam.isEmpty()) {
                System.out.println("❌ ERROR: curso_id es nulo o vacío");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"error\": \"Parámetro curso_id requerido\"}");
                return;
            }

            // 🔢 CONVERTIR Y EJECUTAR CONSULTA
            int cursoId = Integer.parseInt(cursoIdParam);
            System.out.println("🔍 Buscando alumnos para curso ID: " + cursoId);

            List<Alumno> alumnos = dao.obtenerAlumnosPorCurso(cursoId);

            System.out.println("📊 Alumnos encontrados: " + alumnos.size());

            // 📝 LOG DETALLADO DE ALUMNOS ENCONTRADOS
            for (Alumno alumno : alumnos) {
                System.out.println("   👤 " + alumno.getId() + " - " + alumno.getNombres() + " " + alumno.getApellidos());
            }

            // 📦 CONVERTIR RESULTADOS A JSON Y ENVIAR RESPUESTA
            String json = convertirAlumnosAJson(alumnos);
            System.out.println("📦 JSON a enviar: " + json);

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();

            System.out.println("=== ✅ FIN DEBUG - Respuesta enviada ===");

        } catch (NumberFormatException e) {
            // 🚨 ERROR EN FORMATO DE PARÁMETRO
            System.out.println("❌ ERROR: curso_id no es un número válido");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\": \"ID de curso inválido: debe ser un número\"}");
        } catch (Exception e) {
            // 🚨 ERROR GENERAL EN EL PROCESAMIENTO
            System.out.println("❌ ERROR inesperado:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}");
        }
    }

    /**
     * 🛠️ MÉTODO AUXILIAR - CONVERTIR LISTA DE ALUMNOS A JSON MANUALMENTE
     * 
     * Propósito: Generar JSON sin dependencias externas
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

            // 🔄 AGREGAR COMA ENTRE ELEMENTOS (EXCEPTO ÚLTIMO)
            if (i < alumnos.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        return json.toString();
    }

    /**
     * 🛡️ MÉTODO AUXILIAR - ESCAPAR CARACTERES ESPECIALES EN JSON
     * 
     * Propósito: Prevenir errores de sintaxis JSON y ataques de inyección
     * Caracteres escapados: comillas, barras invertidas, saltos de línea, etc.
     */
    private String escapeJson(String text) {
        if (text == null) {
            return ""; // 🔄 VALOR POR DEFECTO PARA NULL
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