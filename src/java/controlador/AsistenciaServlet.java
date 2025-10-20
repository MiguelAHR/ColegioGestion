package controlador;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Asistencia;
import modelo.AsistenciaDAO;
import modelo.Curso;
import modelo.CursoDAO;
import modelo.Profesor;
import modelo.Padre;

public class AsistenciaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "ver";
        }

        HttpSession session = request.getSession();
        String rol = (String) session.getAttribute("rol");

        try {
            switch (accion) {
                case "ver":
                    if ("docente".equals(rol)) {
                        verCursosDocente(request, response);
                    } else if ("padre".equals(rol)) {
                        verAsistenciasPadre(request, response);
                    }
                    break;
                case "verCurso":
                    verAsistenciasCurso(request, response);
                    break;
                case "registrar":
                    mostrarFormRegistro(request, response);
                    break;
                case "reportes":
                    mostrarReportes(request, response);
                    break;
                case "verPadre":
                    verAsistenciasPadreDetalle(request, response);
                    break;
                default:
                    response.sendRedirect("dashboard.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error en AsistenciaServlet: " + e.getMessage());
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== 📨 INICIANDO DO POST ASISTENCIA ===");
        System.out.println("   Accion: " + request.getParameter("accion"));
        System.out.println("   Parámetros recibidos: " + request.getParameterMap().toString());

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "registrar";
        }

        HttpSession session = request.getSession();

        try {
            switch (accion) {
                case "registrarGrupal":
                    System.out.println("🔄 Ejecutando registrarAsistenciaGrupal...");
                    registrarAsistenciaGrupal(request, response);
                    break;
                default:
                    System.out.println("⚠️  Acción no reconocida: " + accion);
                    response.sendRedirect("AsistenciaServlet?accion=ver");
            }
        } catch (Exception e) {
            System.out.println("❌ Error en doPost:");
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar asistencia: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    private void verCursosDocente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        if (docente == null) {
            session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            System.out.println("🔍 Buscando cursos para profesor: " + docente.getNombres() + " " + docente.getApellidos() + " (ID: " + docente.getId() + ")");

            CursoDAO cursoDAO = new CursoDAO();
            // CORREGIDO: Usar listarPorProfesor en lugar de obtenerCursosPorProfesor
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());

            System.out.println("📊 Cursos encontrados: " + (cursos != null ? cursos.size() : 0));

            request.setAttribute("misCursos", cursos);
            request.getRequestDispatcher("asistenciasDocente.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("❌ Error en verCursosDocente:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar los cursos: " + e.getMessage());
            response.sendRedirect("docenteDashboard.jsp");
        }
    }

    private void verAsistenciasCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            String fecha = request.getParameter("fecha");
            int turnoId = request.getParameter("turno_id") != null
                    ? Integer.parseInt(request.getParameter("turno_id")) : 1;

            if (fecha == null) {
                fecha = java.time.LocalDate.now().toString();
            }

            System.out.println("🔍 Buscando asistencias para curso: " + cursoId + ", fecha: " + fecha);

            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorCursoTurnoFecha(cursoId, turnoId, fecha);

            System.out.println("📊 Asistencias encontradas: " + (asistencias != null ? asistencias.size() : 0));

            CursoDAO cursoDAO = new CursoDAO();
            // CORREGIDO: Usar obtenerPorId en lugar de obtenerCursoPorId
            Curso curso = cursoDAO.obtenerPorId(cursoId);

            request.setAttribute("asistencias", asistencias);
            request.setAttribute("cursoId", cursoId);
            request.setAttribute("fecha", fecha);
            request.setAttribute("curso", curso);

            request.getRequestDispatcher("asistenciasCurso.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            System.out.println("❌ Error de formato en verAsistenciasCurso:");
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Parámetros inválidos");
            response.sendRedirect("AsistenciaServlet?accion=ver");
        } catch (Exception e) {
            System.out.println("❌ Error en verAsistenciasCurso:");
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Error al cargar asistencias: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    private void verAsistenciasPadre(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        if (padre == null) {
            session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            int alumnoId = padre.getAlumnoId();
            int mes = request.getParameter("mes") != null
                    ? Integer.parseInt(request.getParameter("mes")) : java.time.LocalDate.now().getMonthValue();
            int anio = request.getParameter("anio") != null
                    ? Integer.parseInt(request.getParameter("anio")) : java.time.LocalDate.now().getYear();

            System.out.println("🔍 Buscando asistencias para alumno: " + alumnoId + ", mes: " + mes + ", año: " + anio);

            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorAlumnoTurno(alumnoId, 1, mes, anio);
            Map<String, Object> resumen = asistenciaDAO.obtenerResumenAsistenciaAlumnoTurno(alumnoId, 1, mes, anio);

            System.out.println("📊 Asistencias encontradas: " + (asistencias != null ? asistencias.size() : 0));

            request.setAttribute("asistencias", asistencias);
            request.setAttribute("resumen", resumen);
            request.setAttribute("mes", mes);
            request.setAttribute("anio", anio);

        } catch (Exception e) {
            System.out.println("❌ Error en verAsistenciasPadre:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar asistencias: " + e.getMessage());
        }

        request.getRequestDispatcher("asistenciasPadre.jsp").forward(request, response);
    }

    private void verAsistenciasPadreDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        verAsistenciasPadre(request, response);
    }

    private void mostrarFormRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        if (docente == null) {
            session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            System.out.println("🔍 Cargando formulario para profesor: " + docente.getNombres() + " " + docente.getApellidos());

            CursoDAO cursoDAO = new CursoDAO();
            // CORREGIDO: Usar listarPorProfesor en lugar de obtenerCursosPorProfesor
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());

            request.setAttribute("cursos", cursos);
            request.getRequestDispatcher("registrarAsistencia.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("❌ Error en mostrarFormRegistro:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar cursos: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    private void mostrarReportes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("reportesAsistencia.jsp").forward(request, response);
    }

    private void registrarAsistencia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int alumnoId = Integer.parseInt(request.getParameter("alumno_id"));
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            int turnoId = Integer.parseInt(request.getParameter("turno_id"));
            String fecha = request.getParameter("fecha");
            String horaClase = request.getParameter("hora_clase");
            String estado = request.getParameter("estado");
            String observaciones = request.getParameter("observaciones");
            
            // ✅ CORREGIDO: Verificar docente antes de obtener ID
            Profesor docente = (Profesor) session.getAttribute("docente");
            if (docente == null) {
                session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
                response.sendRedirect("index.jsp");
                return;
            }
            
            int registradoPor = docente.getId();

            Asistencia asistencia = new Asistencia();
            asistencia.setAlumnoId(alumnoId);
            asistencia.setCursoId(cursoId);
            asistencia.setTurnoId(turnoId);
            asistencia.setFecha(fecha);
            asistencia.setHoraClase(horaClase);
            asistencia.setEstado(estado);
            asistencia.setObservaciones(observaciones);
            asistencia.setRegistradoPor(registradoPor);

            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            boolean resultado = asistenciaDAO.registrarAsistencia(asistencia);

            if (resultado) {
                session.setAttribute("mensaje", "Asistencia registrada correctamente");
            } else {
                session.setAttribute("error", "Error al registrar la asistencia");
            }

            response.sendRedirect("AsistenciaServlet?accion=verCurso&curso_id=" + cursoId + "&fecha=" + fecha);

        } catch (Exception e) {
            System.out.println("❌ Error en registrarAsistencia:");
            e.printStackTrace();
            session.setAttribute("error", "Error al registrar asistencia: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=registrar");
        }
    }

    private void registrarAsistenciaGrupal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        System.out.println("=== 🟡 INICIANDO REGISTRO GRUPAL ===");
        System.out.println("📨 Parámetros recibidos:");

        // Log detallado de todos los parámetros
        request.getParameterMap().forEach((key, values) -> {
            if ("alumnos_json".equals(key)) {
                String json = values[0];
                System.out.println("   " + key + ": " + (json.length() > 200 ? json.substring(0, 200) + "..." : json));
            } else {
                System.out.println("   " + key + ": " + String.join(", ", values));
            }
        });

        try {
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            int turnoId = Integer.parseInt(request.getParameter("turno_id"));
            String fecha = request.getParameter("fecha");
            String horaClase = request.getParameter("hora_clase");
            String alumnosJson = request.getParameter("alumnos_json");

            // ✅ VERIFICACIÓN MÁS ROBUSTA DE LA SESIÓN DEL DOCENTE
            Profesor docente = (Profesor) session.getAttribute("docente");
            if (docente == null) {
                System.out.println("❌ ERROR: No hay docente en sesión");
                System.out.println("   Atributos en sesión:");
                java.util.Enumeration<String> sessionAttrs = session.getAttributeNames();
                while (sessionAttrs.hasMoreElements()) {
                    String attrName = sessionAttrs.nextElement();
                    Object attrValue = session.getAttribute(attrName);
                    System.out.println("   - " + attrName + ": " + attrValue + " (tipo: "
                            + (attrValue != null ? attrValue.getClass().getName() : "null") + ")");
                }

                session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
                response.sendRedirect("index.jsp"); // Redirigir al login
                return;
            }

            // ✅ CORREGIDO: Usar getNombres() y getApellidos() en lugar de getNombre()
            int registradoPor = docente.getId();
            String nombresDocente = docente.getNombres();
            String apellidosDocente = docente.getApellidos();

            System.out.println("🔍 Datos procesados:");
            System.out.println("   cursoId: " + cursoId);
            System.out.println("   turnoId: " + turnoId);
            System.out.println("   fecha: " + fecha);
            System.out.println("   horaClase: " + horaClase);
            System.out.println("   registradoPor: " + registradoPor);
            System.out.println("   docente en sesión: " + nombresDocente + " " + apellidosDocente + " (ID: " + registradoPor + ")");

            if (alumnosJson == null || alumnosJson.isEmpty()) {
                System.out.println("❌ ERROR: alumnos_json está vacío");
                session.setAttribute("error", "No se recibieron datos de alumnos");
                response.sendRedirect("AsistenciaServlet?accion=registrar");
                return;
            }

            System.out.println("🔄 Llamando a AsistenciaDAO...");

            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            boolean resultado = asistenciaDAO.registrarAsistenciaGrupal(cursoId, turnoId, fecha, horaClase, alumnosJson, registradoPor);

            if (resultado) {
                System.out.println("✅ Asistencias guardadas correctamente en la BD");
                session.setAttribute("mensaje", "Asistencias grupales registradas correctamente");
            } else {
                System.out.println("❌ Error al guardar asistencias en la BD");
                session.setAttribute("error", "Error al registrar las asistencias grupales");
            }

            System.out.println("🔄 Redirigiendo a: AsistenciaServlet?accion=verCurso&curso_id=" + cursoId + "&fecha=" + fecha);
            response.sendRedirect("AsistenciaServlet?accion=verCurso&curso_id=" + cursoId + "&fecha=" + fecha);

        } catch (NumberFormatException e) {
            System.out.println("❌ ERROR: NumberFormatException en registrarAsistenciaGrupal:");
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error en el formato de los datos: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=registrar");
        } catch (Exception e) {
            System.out.println("❌ ERROR EXCEPCIÓN en registrarAsistenciaGrupal:");
            System.out.println("   Tipo: " + e.getClass().getName());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error al registrar asistencias grupales: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=registrar");
        }
    }
}