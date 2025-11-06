/*
 * SERVLET PARA GESTIÓN COMPLETA DE ASISTENCIAS ESCOLARES
 * 
 * Funcionalidades:
 * - Registro grupal e individual de asistencias (Docentes)
 * - Consulta de asistencias por curso y fecha (Docentes) 
 * - Visualización de asistencias y reportes (Padres)
 * - Control de sesiones y permisos por rol
 * 
 * Roles: 
 * - Docente: Registro y consulta de asistencias
 * - Padre: Solo consulta de asistencias de su hijo
 */
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

    /**
     * 📖 MÉTODO GET - MANEJA SOLICITUDES DE CONSULTA Y NAVEGACIÓN
     * 
     * Acciones disponibles según rol:
     * - Docente: ver cursos, ver asistencias por curso, registrar asistencias
     * - Padre: ver asistencias de su hijo, reportes mensuales
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "ver"; // 🎯 ACCIÓN POR DEFECTO: MOSTRAR VISTA PRINCIPAL
        }

        HttpSession session = request.getSession();
        String rol = (String) session.getAttribute("rol"); // 🔐 OBTENER ROL PARA CONTROL DE ACCESO

        try {
            switch (accion) {
                case "ver":
                    // 🎯 REDIRIGIR SEGÚN ROL DEL USUARIO
                    if ("docente".equals(rol)) {
                        verCursosDocente(request, response); // 👨‍🏫 VISTA DOCENTE: LISTA DE CURSOS
                    } else if ("padre".equals(rol)) {
                        verAsistenciasPadre(request, response); // 👨‍👧‍👦 VISTA PADRE: ASISTENCIAS DEL HIJO
                    }
                    break;
                case "verCurso":
                    verAsistenciasCurso(request, response); // 📊 DETALLE DE ASISTENCIAS POR CURSO Y FECHA
                    break;
                case "registrar":
                    mostrarFormRegistro(request, response); // 📝 FORMULARIO DE REGISTRO GRUPAL
                    break;
                case "reportes":
                    mostrarReportes(request, response); // 📈 VISTA DE REPORTES ESTADÍSTICOS
                    break;
                case "verPadre":
                    verAsistenciasPadreDetalle(request, response); // 🔍 VISTA DETALLADA PARA PADRES
                    break;
                default:
                    // 🏠 REDIRECCIÓN SEGURA SI LA ACCIÓN NO ES RECONOCIDA
                    response.sendRedirect("dashboard.jsp");
            }
        } catch (Exception e) {
            // 🚨 MANEJO CENTRALIZADO DE ERRORES
            e.printStackTrace();
            session.setAttribute("error", "Error en AsistenciaServlet: " + e.getMessage());
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * 💾 MÉTODO POST - PROCESA ENVÍOS DE FORMULARIOS (REGISTRO DE ASISTENCIAS)
     * 
     * Principalmente maneja el registro grupal de asistencias mediante JSON
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🎯 LOG DE INICIO PARA DEPURACIÓN
        System.out.println("=== 📨 INICIANDO DO POST ASISTENCIA ===");
        System.out.println("   Accion: " + request.getParameter("accion"));
        System.out.println("   Parámetros recibidos: " + request.getParameterMap().toString());

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "registrar"; // 🎯 VALOR POR DEFECTO
        }

        HttpSession session = request.getSession();

        try {
            switch (accion) {
                case "registrarGrupal":
                    // 👥 REGISTRO MASIVO DE ASISTENCIAS (MÚLTIPLES ALUMNOS)
                    System.out.println("🔄 Ejecutando registrarAsistenciaGrupal...");
                    registrarAsistenciaGrupal(request, response);
                    break;
                default:
                    // ⚠️ ACCIÓN NO RECONOCIDA - REDIRIGIR A VISTA PRINCIPAL
                    System.out.println("⚠️  Acción no reconocida: " + accion);
                    response.sendRedirect("AsistenciaServlet?accion=ver");
            }
        } catch (Exception e) {
            // 🚨 MANEJO DE ERRORES EN SOLICITUDES POST
            System.out.println("❌ Error en doPost:");
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar asistencia: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    /**
     * 👨‍🏫 MOSTRAR CURSOS ASIGNADOS AL DOCENTE PARA GESTIÓN DE ASISTENCIAS
     * 
     * Carga los cursos del docente desde la base de datos y los envía a la vista
     */
    private void verCursosDocente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        // 🔐 VERIFICAR QUE EL USUARIO ESTÉ AUTENTICADO COMO DOCENTE
        if (docente == null) {
            session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            // 📝 LOG INFORMATIVO
            System.out.println("🔍 Buscando cursos para profesor: " + docente.getNombres() + " " + docente.getApellidos() + " (ID: " + docente.getId() + ")");

            CursoDAO cursoDAO = new CursoDAO();
            // 📚 OBTENER LISTA DE CURSOS ASIGNADOS AL DOCENTE
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());

            System.out.println("📊 Cursos encontrados: " + (cursos != null ? cursos.size() : 0));

            // 📤 ENVIAR DATOS A LA VISTA
            request.setAttribute("misCursos", cursos);
            request.getRequestDispatcher("asistenciasDocente.jsp").forward(request, response);

        } catch (Exception e) {
            // 🚨 MANEJO DE ERRORES EN LA CARGA DE CURSOS
            System.out.println("❌ Error en verCursosDocente:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar los cursos: " + e.getMessage());
            response.sendRedirect("docenteDashboard.jsp");
        }
    }

    /**
     * 📊 MOSTRAR ASISTENCIAS DE UN CURSO ESPECÍFICO EN FECHA DETERMINADA
     * 
     * Permite a los docentes ver el historial de asistencias por curso y fecha
     */
    private void verAsistenciasCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 📥 OBTENER PARÁMETROS DE CONSULTA
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            String fecha = request.getParameter("fecha");
            int turnoId = request.getParameter("turno_id") != null
                    ? Integer.parseInt(request.getParameter("turno_id")) : 1; // 🎯 TURNO POR DEFECTO: 1

            // 📅 USAR FECHA ACTUAL SI NO SE ESPECIFICA
            if (fecha == null) {
                fecha = java.time.LocalDate.now().toString();
            }

            System.out.println("🔍 Buscando asistencias para curso: " + cursoId + ", fecha: " + fecha);

            // 📊 CONSULTAR ASISTENCIAS EN BASE DE DATOS
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorCursoTurnoFecha(cursoId, turnoId, fecha);

            System.out.println("📊 Asistencias encontradas: " + (asistencias != null ? asistencias.size() : 0));

            // 🔍 OBTENER INFORMACIÓN DEL CURSO PARA MOSTRAR EN VISTA
            CursoDAO cursoDAO = new CursoDAO();
            Curso curso = cursoDAO.obtenerPorId(cursoId);

            // 📤 PREPARAR DATOS PARA LA VISTA
            request.setAttribute("asistencias", asistencias);
            request.setAttribute("cursoId", cursoId);
            request.setAttribute("fecha", fecha);
            request.setAttribute("curso", curso);

            request.getRequestDispatcher("asistenciasCurso.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            // 🚨 ERROR DE FORMATEO EN PARÁMETROS NUMÉRICOS
            System.out.println("❌ Error de formato en verAsistenciasCurso:");
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Parámetros inválidos");
            response.sendRedirect("AsistenciaServlet?accion=ver");
        } catch (Exception e) {
            // 🚨 ERROR GENERAL EN LA CONSULTA
            System.out.println("❌ Error en verAsistenciasCurso:");
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Error al cargar asistencias: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    /**
     * 👨‍👧‍👦 MOSTRAR ASISTENCIAS DEL ALUMNO PARA VISTA DE PADRES/TUTORES
     * 
     * Incluye resumen mensual y lista detallada de asistencias
     */
    private void verAsistenciasPadre(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        // 🔐 VERIFICAR AUTENTICACIÓN Y DATOS DE PADRE
        if (padre == null) {
            session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            // 📥 OBTENER PARÁMETROS DE PERÍODO (MES/AÑO)
            int alumnoId = padre.getAlumnoId();
            int mes = request.getParameter("mes") != null
                    ? Integer.parseInt(request.getParameter("mes")) : java.time.LocalDate.now().getMonthValue();
            int anio = request.getParameter("anio") != null
                    ? Integer.parseInt(request.getParameter("anio")) : java.time.LocalDate.now().getYear();

            System.out.println("🔍 Buscando asistencias para alumno: " + alumnoId + ", mes: " + mes + ", año: " + anio);

            // 📊 CONSULTAR ASISTENCIAS Y RESUMEN ESTADÍSTICO
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorAlumnoTurno(alumnoId, 1, mes, anio);
            Map<String, Object> resumen = asistenciaDAO.obtenerResumenAsistenciaAlumnoTurno(alumnoId, 1, mes, anio);

            System.out.println("📊 Asistencias encontradas: " + (asistencias != null ? asistencias.size() : 0));

            // 📤 PREPARAR DATOS PARA LA VISTA
            request.setAttribute("asistencias", asistencias);
            request.setAttribute("resumen", resumen);
            request.setAttribute("mes", mes);
            request.setAttribute("anio", anio);

        } catch (Exception e) {
            // 🚨 ERROR EN LA CONSULTA DE ASISTENCIAS
            System.out.println("❌ Error en verAsistenciasPadre:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar asistencias: " + e.getMessage());
        }

        // 🎯 CARGAR VISTA ESPECÍFICA PARA PADRES
        request.getRequestDispatcher("asistenciasPadre.jsp").forward(request, response);
    }

    /**
     * 🔍 VISTA DETALLADA DE ASISTENCIAS PARA PADRES (ALIAS DE verAsistenciasPadre)
     */
    private void verAsistenciasPadreDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        verAsistenciasPadre(request, response); // 🔄 REUTILIZAR LÓGICA EXISTENTE
    }

    /**
     * 📝 MOSTRAR FORMULARIO DE REGISTRO DE ASISTENCIAS GRUPALES
     * 
     * Prepara el formulario con lista de cursos y parámetros por defecto
     */
    private void mostrarFormRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        // 🎯 LOG DETALLADO PARA DEPURACIÓN
        System.out.println("=== 🔍 INICIANDO MOSTRAR FORM REGISTRO ===");
        System.out.println("   Docente en sesión: " + (docente != null ? docente.getNombres() + " " + docente.getApellidos() : "NULL"));
        System.out.println("   Docente ID: " + (docente != null ? docente.getId() : "NULL"));

        // 🔐 VERIFICAR QUE EL USUARIO ESTÉ AUTENTICADO COMO DOCENTE
        if (docente == null) {
            System.out.println("❌ ERROR: No hay docente en sesión");
            session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            // 📥 OBTENER PARÁMETROS DE LA URL (FILTROS)
            String cursoIdParam = request.getParameter("curso_id");
            String fechaParam = request.getParameter("fecha");

            System.out.println("📌 Parámetros recibidos:");
            System.out.println("   curso_id: " + cursoIdParam);
            System.out.println("   fecha: " + fechaParam);

            // 📚 OBTENER CURSOS ASIGNADOS AL DOCENTE
            CursoDAO cursoDAO = new CursoDAO();
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());

            System.out.println("📊 Cursos encontrados: " + (cursos != null ? cursos.size() : "null"));

            // 📝 LOG DETALLADO DE CURSOS ENCONTRADOS
            if (cursos != null && !cursos.isEmpty()) {
                for (Curso curso : cursos) {
                    System.out.println("   - Curso: " + curso.getId() + " - " + curso.getNombre() + " - Grado: " + curso.getGradoNombre());
                }
            } else {
                System.out.println("⚠️  No se encontraron cursos para el profesor");
            }

            // 🚨 VALIDAR QUE EL DOCENTE TENGA CURSOS ASIGNADOS
            if (cursos == null || cursos.isEmpty()) {
                System.out.println("❌ ERROR: No hay cursos asignados");
                session.setAttribute("error", "No tienes cursos asignados. Contacta con administración.");
                response.sendRedirect("docenteDashboard.jsp");
                return;
            }

            // 🎯 SELECCIÓN INTELIGENTE DE CURSO POR DEFECTO
            if ((cursoIdParam == null || cursoIdParam.isEmpty()) && !cursos.isEmpty()) {
                cursoIdParam = String.valueOf(cursos.get(0).getId());
                System.out.println("🔄 Usando primer curso por defecto: " + cursoIdParam);
            }

            // 📤 PREPARAR DATOS PARA EL FORMULARIO JSP
            request.setAttribute("cursos", cursos);
            request.setAttribute("cursoIdParam", cursoIdParam);
            request.setAttribute("fechaParam", fechaParam);

            System.out.println("✅ Datos preparados para el JSP:");
            System.out.println("   - Cursos: " + cursos.size());
            System.out.println("   - Curso seleccionado: " + cursoIdParam);
            System.out.println("   - Fecha: " + fechaParam);

            // 🎯 CARGAR FORMULARIO DE REGISTRO
            request.getRequestDispatcher("registrarAsistencia.jsp").forward(request, response);

        } catch (Exception e) {
            // 🚨 ERROR EN LA CARGA DEL FORMULARIO
            System.out.println("❌ Error en mostrarFormRegistro:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar cursos: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    /**
     * 📈 MOSTRAR PÁGINA DE REPORTES ESTADÍSTICOS DE ASISTENCIAS
     */
    private void mostrarReportes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("reportesAsistencia.jsp").forward(request, response);
    }

    /**
     * 👥 REGISTRO GRUPAL DE ASISTENCIAS (MÚLTIPLES ALUMNOS SIMULTÁNEAMENTE)
     * 
     * Procesa el formulario con datos en formato JSON para registro eficiente
     */
    private void registrarAsistenciaGrupal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        System.out.println("=== 🟡 INICIANDO REGISTRO GRUPAL ===");
        System.out.println("📨 Parámetros recibidos:");

        // 📝 LOG DETALLADO DE TODOS LOS PARÁMETROS RECIBIDOS
        request.getParameterMap().forEach((key, values) -> {
            if ("alumnos_json".equals(key)) {
                // 📋 MOSTRAR SOLO PARTE DEL JSON POR LOGS (EVITA SATURACIÓN)
                String json = values[0];
                System.out.println("   " + key + ": " + (json.length() > 200 ? json.substring(0, 200) + "..." : json));
            } else {
                System.out.println("   " + key + ": " + String.join(", ", values));
            }
        });

        try {
            // 📥 CAPTURAR DATOS DEL FORMULARIO
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            int turnoId = Integer.parseInt(request.getParameter("turno_id"));
            String fecha = request.getParameter("fecha");
            String horaClase = request.getParameter("hora_clase");
            String alumnosJson = request.getParameter("alumnos_json"); // 📋 DATOS EN FORMATO JSON

            // 🔐 VERIFICACIÓN ROBUSTA DE LA SESIÓN DEL DOCENTE
            Profesor docente = (Profesor) session.getAttribute("docente");
            if (docente == null) {
                System.out.println("❌ ERROR: No hay docente en sesión");
                // 📊 LOG DETALLADO DE ATRIBUTOS DE SESIÓN PARA DEPURACIÓN
                System.out.println("   Atributos en sesión:");
                java.util.Enumeration<String> sessionAttrs = session.getAttributeNames();
                while (sessionAttrs.hasMoreElements()) {
                    String attrName = sessionAttrs.nextElement();
                    Object attrValue = session.getAttribute(attrName);
                    System.out.println("   - " + attrName + ": " + attrValue + " (tipo: "
                            + (attrValue != null ? attrValue.getClass().getName() : "null") + ")");
                }

                session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
                response.sendRedirect("index.jsp"); // 🔄 REDIRIGIR AL LOGIN
                return;
            }

            // 👤 OBTENER DATOS DEL DOCENTE PARA AUDITORÍA
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

            // 🚨 VALIDAR DATOS OBLIGATORIOS
            if (alumnosJson == null || alumnosJson.isEmpty()) {
                System.out.println("❌ ERROR: alumnos_json está vacío");
                session.setAttribute("error", "No se recibieron datos de alumnos");
                response.sendRedirect("AsistenciaServlet?accion=registrar");
                return;
            }

            System.out.println("🔄 Llamando a AsistenciaDAO...");

            // 💾 EJECUTAR REGISTRO GRUPAL EN BASE DE DATOS
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            boolean resultado = asistenciaDAO.registrarAsistenciaGrupal(cursoId, turnoId, fecha, horaClase, alumnosJson, registradoPor);

            // 📢 MOSTRAR MENSAJE DE RESULTADO
            if (resultado) {
                System.out.println("✅ Asistencias guardadas correctamente en la BD");
                session.setAttribute("mensaje", "Asistencias grupales registradas correctamente");
            } else {
                System.out.println("❌ Error al guardar asistencias en la BD");
                session.setAttribute("error", "Error al registrar las asistencias grupales");
            }

            // 🔄 REDIRIGIR A LA VISTA DE CONSULTA DEL CURSO
            System.out.println("🔄 Redirigiendo a: AsistenciaServlet?accion=verCurso&curso_id=" + cursoId + "&fecha=" + fecha);
            response.sendRedirect("AsistenciaServlet?accion=verCurso&curso_id=" + cursoId + "&fecha=" + fecha);

        } catch (NumberFormatException e) {
            // 🚨 ERROR EN EL FORMATEO DE DATOS NUMÉRICOS
            System.out.println("❌ ERROR: NumberFormatException en registrarAsistenciaGrupal:");
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error en el formato de los datos: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=registrar");
        } catch (Exception e) {
            // 🚨 ERROR GENERAL EN EL PROCESAMIENTO
            System.out.println("❌ ERROR EXCEPCIÓN en registrarAsistenciaGrupal:");
            System.out.println("   Tipo: " + e.getClass().getName());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error al registrar asistencias grupales: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=registrar");
        }
    }
}