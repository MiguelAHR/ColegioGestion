package controlador;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import modelo.Alumno;
import modelo.AlumnoDAO;
import modelo.GradoDAO;

// ❌ ELIMINA ESTA LÍNEA: @WebServlet("/AlumnoServlet")s
public class AlumnoServlet extends HttpServlet {

    AlumnoDAO dao = new AlumnoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.setAttribute("lista", dao.listar());
            request.getRequestDispatcher("alumnos.jsp").forward(request, response);
            return;
        }

        if (accion.equals("filtrar")) {
            String gradoStr = request.getParameter("grado_id");

            request.setAttribute("grados", new GradoDAO().listar());

            if (gradoStr == null || gradoStr.isEmpty()) {
                request.setAttribute("lista", dao.listar());
            } else {
                int gradoId = Integer.parseInt(gradoStr);
                request.setAttribute("gradoSeleccionado", gradoId);
                request.setAttribute("lista", dao.listarPorGrado(gradoId));
            }

            request.getRequestDispatcher("alumnos.jsp").forward(request, response);
            return;
        }

        // ✅ NUEVA FUNCIONALIDAD: Obtener alumnos por curso (para AJAX)
        if (accion.equals("obtenerPorCurso")) {
            obtenerAlumnosPorCurso(request, response);
            return;
        }

        if (accion.equals("nuevo")) {
            request.setAttribute("grados", new GradoDAO().listar());
            request.getRequestDispatcher("alumnoForm.jsp").forward(request, response);
            return;
        }

        switch (accion) {
            case "editar":
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Alumno alumno = dao.obtenerPorId(idEditar);
                request.setAttribute("alumno", alumno);
                request.setAttribute("grados", new GradoDAO().listar());
                request.getRequestDispatcher("alumnoForm.jsp").forward(request, response);
                break;

            case "eliminar":
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("AlumnoServlet");
                break;

            default:
                response.sendRedirect("AlumnoServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty()
                ? Integer.parseInt(request.getParameter("id")) : 0;

        Alumno a = new Alumno();
        a.setNombres(request.getParameter("nombres"));
        a.setApellidos(request.getParameter("apellidos"));
        a.setCorreo(request.getParameter("correo"));
        a.setFechaNacimiento(request.getParameter("fecha_nacimiento"));
        a.setGradoId(Integer.parseInt(request.getParameter("grado_id")));

        if (id == 0) {
            dao.agregar(a);
        } else {
            a.setId(id);
            dao.actualizar(a);
        }

        response.sendRedirect("AlumnoServlet");
    }

    // ✅ NUEVO MÉTODO: Obtener alumnos por curso (AJAX)
    private void obtenerAlumnosPorCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        System.out.println("=== 🔍 INICIANDO DEBUG obtenerAlumnosPorCurso ===");

        try {
            // Debug del parámetro recibido
            String cursoIdParam = request.getParameter("curso_id");
            System.out.println("📥 Parámetro curso_id recibido: '" + cursoIdParam + "'");
            System.out.println("📥 Todos los parámetros: " + request.getParameterMap().toString());

            if (cursoIdParam == null || cursoIdParam.isEmpty()) {
                System.out.println("❌ ERROR: curso_id es nulo o vacío");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"error\": \"Parámetro curso_id requerido\"}");
                return;
            }

            int cursoId = Integer.parseInt(cursoIdParam);
            System.out.println("🔍 Buscando alumnos para curso ID: " + cursoId);

            List<Alumno> alumnos = dao.obtenerAlumnosPorCurso(cursoId);

            System.out.println("📊 Alumnos encontrados: " + alumnos.size());

            // Debug detallado de alumnos
            for (Alumno alumno : alumnos) {
                System.out.println("   👤 " + alumno.getId() + " - " + alumno.getNombres() + " " + alumno.getApellidos());
            }

            // Convertir a JSON
            String json = convertirAlumnosAJson(alumnos);
            System.out.println("📦 JSON a enviar: " + json);

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();

            System.out.println("=== ✅ FIN DEBUG - Respuesta enviada ===");

        } catch (NumberFormatException e) {
            System.out.println("❌ ERROR: curso_id no es un número válido");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\": \"ID de curso inválido: debe ser un número\"}");
        } catch (Exception e) {
            System.out.println("❌ ERROR inesperado:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}");
        }
    }

    // ✅ MÉTODO PARA CONVERTIR ALUMNOS A JSON MANUALMENTE
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

            if (i < alumnos.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        return json.toString();
    }

    // ✅ MÉTODO PARA ESCAPAR CARACTERES ESPECIALES EN JSON
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
