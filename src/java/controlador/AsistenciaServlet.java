/*
 * SERVLET PARA GESTION COMPLETA DE ASISTENCIAS ESCOLARES
 * 
 * Funcionalidades:
 * - Registro grupal e individual de asistencias (Docentes)
 * - Consulta de asistencias por curso y fecha (Docentes) 
 * - Visualizacion de asistencias y reportes (Padres)
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
     * METODO GET - MANEJA SOLICITUDES DE CONSULTA Y NAVEGACION
     * 
     * Acciones disponibles segun rol:
     * - Docente: ver cursos, ver asistencias por curso, registrar asistencias
     * - Padre: ver asistencias de su hijo, reportes mensuales
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "ver"; // Accion por defecto: mostrar vista principal
        }

        HttpSession session = request.getSession();
        String rol = (String) session.getAttribute("rol"); // Obtener rol para control de acceso

        try {
            switch (accion) {
                case "ver":
                    // Redirigir segun rol del usuario
                    if ("docente".equals(rol)) {
                        verCursosDocente(request, response); // Vista docente: lista de cursos
                    } else if ("padre".equals(rol)) {
                        verAsistenciasPadre(request, response); // Vista padre: asistencias del hijo
                    }
                    break;
                case "verCurso":
                    verAsistenciasCurso(request, response); // Detalle de asistencias por curso y fecha
                    break;
                case "registrar":
                    mostrarFormRegistro(request, response); // Formulario de registro grupal
                    break;
                case "reportes":
                    mostrarReportes(request, response); // Vista de reportes estadisticos
                    break;
                case "verPadre":
                    verAsistenciasPadreDetalle(request, response); // Vista detallada para padres
                    break;
                default:
                    // Redireccion segura si la accion no es reconocida
                    response.sendRedirect("dashboard.jsp");
            }
        } catch (Exception e) {
            // Manejo centralizado de errores
            e.printStackTrace();
            session.setAttribute("error", "Error en AsistenciaServlet: " + e.getMessage());
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * METODO POST - PROCESA ENVIOS DE FORMULARIOS (REGISTRO DE ASISTENCIAS)
     * 
     * Principalmente maneja el registro grupal de asistencias mediante JSON
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("INICIANDO DO POST ASISTENCIA");
        System.out.println("Accion: " + request.getParameter("accion"));
        System.out.println("Parametros recibidos: " + request.getParameterMap().toString());

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "registrar"; // Valor por defecto
        }

        HttpSession session = request.getSession();

        try {
            switch (accion) {
                case "registrarGrupal":
                    // Registro masivo de asistencias (multiples alumnos)
                    System.out.println("Ejecutando registrarAsistenciaGrupal...");
                    registrarAsistenciaGrupal(request, response);
                    break;
                default:
                    // Accion no reconocida - redirigir a vista principal
                    System.out.println("Accion no reconocida: " + accion);
                    response.sendRedirect("AsistenciaServlet?accion=ver");
            }
        } catch (Exception e) {
            // Manejo de errores en solicitudes POST
            System.out.println("Error en doPost:");
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar asistencia: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    /**
     * MOSTRAR CURSOS ASIGNADOS AL DOCENTE PARA GESTION DE ASISTENCIAS
     * 
     * Carga los cursos del docente desde la base de datos y los envia a la vista
     */
    private void verCursosDocente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        // Verificar que el usuario este autenticado como docente
        if (docente == null) {
            session.setAttribute("error", "Sesion expirada. Por favor inicie sesion nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            System.out.println("Buscando cursos para profesor: " + docente.getNombres() + " " + docente.getApellidos() + " (ID: " + docente.getId() + ")");

            CursoDAO cursoDAO = new CursoDAO();
            // Obtener lista de cursos asignados al docente
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());

            System.out.println("Cursos encontrados: " + (cursos != null ? cursos.size() : 0));

            // Enviar datos a la vista
            request.setAttribute("misCursos", cursos);
            request.getRequestDispatcher("asistenciasDocente.jsp").forward(request, response);

        } catch (Exception e) {
            // Manejo de errores en la carga de cursos
            System.out.println("Error en verCursosDocente:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar los cursos: " + e.getMessage());
            response.sendRedirect("docenteDashboard.jsp");
        }
    }

    /**
     * MOSTRAR ASISTENCIAS DE UN CURSO ESPECIFICO EN FECHA DETERMINADA
     * 
     * Permite a los docentes ver el historial de asistencias por curso y fecha
     */
    private void verAsistenciasCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Obtener parametros de consulta
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            String fecha = request.getParameter("fecha");
            int turnoId = request.getParameter("turno_id") != null
                    ? Integer.parseInt(request.getParameter("turno_id")) : 1; // Turno por defecto: 1

            // Usar fecha actual si no se especifica
            if (fecha == null) {
                fecha = java.time.LocalDate.now().toString();
            }

            System.out.println("Buscando asistencias para curso: " + cursoId + ", fecha: " + fecha);

            // Consultar asistencias en base de datos
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorCursoTurnoFecha(cursoId, turnoId, fecha);

            System.out.println("Asistencias encontradas: " + (asistencias != null ? asistencias.size() : 0));

            // Obtener informacion del curso para mostrar en vista
            CursoDAO cursoDAO = new CursoDAO();
            Curso curso = cursoDAO.obtenerPorId(cursoId);

            // Preparar datos para la vista
            request.setAttribute("asistencias", asistencias);
            request.setAttribute("cursoId", cursoId);
            request.setAttribute("fecha", fecha);
            request.setAttribute("curso", curso);

            request.getRequestDispatcher("asistenciasCurso.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            // Error de formateo en parametros numericos
            System.out.println("Error de formato en verAsistenciasCurso:");
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Parametros invalidos");
            response.sendRedirect("AsistenciaServlet?accion=ver");
        } catch (Exception e) {
            // Error general en la consulta
            System.out.println("Error en verAsistenciasCurso:");
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Error al cargar asistencias: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    /**
     * MOSTRAR ASISTENCIAS DEL ALUMNO PARA VISTA DE PADRES/TUTORES
     * 
     * Incluye resumen mensual y lista detallada de asistencias
     */
    private void verAsistenciasPadre(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        // Verificar autenticacion y datos de padre
        if (padre == null) {
            session.setAttribute("error", "Sesion expirada. Por favor inicie sesion nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            // Obtener parametros de periodo (mes/ano)
            int alumnoId = padre.getAlumnoId();
            int mes = request.getParameter("mes") != null
                    ? Integer.parseInt(request.getParameter("mes")) : java.time.LocalDate.now().getMonthValue();
            int anio = request.getParameter("anio") != null
                    ? Integer.parseInt(request.getParameter("anio")) : java.time.LocalDate.now().getYear();

            System.out.println("Buscando asistencias para alumno: " + alumnoId + ", mes: " + mes + ", ano: " + anio);

            // Consultar asistencias y resumen estadistico
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorAlumnoTurno(alumnoId, 1, mes, anio);
            Map<String, Object> resumen = asistenciaDAO.obtenerResumenAsistenciaAlumnoTurno(alumnoId, 1, mes, anio);

            System.out.println("Asistencias encontradas: " + (asistencias != null ? asistencias.size() : 0));

            // Preparar datos para la vista
            request.setAttribute("asistencias", asistencias);
            request.setAttribute("resumen", resumen);
            request.setAttribute("mes", mes);
            request.setAttribute("anio", anio);

        } catch (Exception e) {
            // Error en la consulta de asistencias
            System.out.println("Error en verAsistenciasPadre:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar asistencias: " + e.getMessage());
        }

        // Cargar vista especifica para padres
        request.getRequestDispatcher("asistenciasPadre.jsp").forward(request, response);
    }

    /**
     * VISTA DETALLADA DE ASISTENCIAS PARA PADRES (ALIAS DE verAsistenciasPadre)
     */
    private void verAsistenciasPadreDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        verAsistenciasPadre(request, response); // Reutilizar logica existente
    }

    /**
     * MOSTRAR FORMULARIO DE REGISTRO DE ASISTENCIAS GRUPALES
     * 
     * Prepara el formulario con lista de cursos y parametros por defecto
     */
    private void mostrarFormRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Profesor docente = (Profesor) session.getAttribute("docente");

        System.out.println("INICIANDO MOSTRAR FORM REGISTRO");
        System.out.println("Docente en sesion: " + (docente != null ? docente.getNombres() + " " + docente.getApellidos() : "NULL"));
        System.out.println("Docente ID: " + (docente != null ? docente.getId() : "NULL"));

        // Verificar que el usuario este autenticado como docente
        if (docente == null) {
            System.out.println("ERROR: No hay docente en sesion");
            session.setAttribute("error", "Sesion expirada. Por favor inicie sesion nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            // Obtener parametros de la URL (filtros)
            String cursoIdParam = request.getParameter("curso_id");
            String fechaParam = request.getParameter("fecha");

            System.out.println("Parametros recibidos:");
            System.out.println("curso_id: " + cursoIdParam);
            System.out.println("fecha: " + fechaParam);

            // Obtener cursos asignados al docente
            CursoDAO cursoDAO = new CursoDAO();
            List<Curso> cursos = cursoDAO.listarPorProfesor(docente.getId());

            System.out.println("Cursos encontrados: " + (cursos != null ? cursos.size() : "null"));

            // Log detallado de cursos encontrados
            if (cursos != null && !cursos.isEmpty()) {
                for (Curso curso : cursos) {
                    System.out.println("Curso: " + curso.getId() + " - " + curso.getNombre() + " - Grado: " + curso.getGradoNombre());
                }
            } else {
                System.out.println("No se encontraron cursos para el profesor");
            }

            // Validar que el docente tenga cursos asignados
            if (cursos == null || cursos.isEmpty()) {
                System.out.println("ERROR: No hay cursos asignados");
                session.setAttribute("error", "No tienes cursos asignados. Contacta con administracion.");
                response.sendRedirect("docenteDashboard.jsp");
                return;
            }

            // Seleccion inteligente de curso por defecto
            if ((cursoIdParam == null || cursoIdParam.isEmpty()) && !cursos.isEmpty()) {
                cursoIdParam = String.valueOf(cursos.get(0).getId());
                System.out.println("Usando primer curso por defecto: " + cursoIdParam);
            }

            // Preparar datos para el formulario JSP
            request.setAttribute("cursos", cursos);
            request.setAttribute("cursoIdParam", cursoIdParam);
            request.setAttribute("fechaParam", fechaParam);

            System.out.println("Datos preparados para el JSP:");
            System.out.println("Cursos: " + cursos.size());
            System.out.println("Curso seleccionado: " + cursoIdParam);
            System.out.println("Fecha: " + fechaParam);

            // Cargar formulario de registro
            request.getRequestDispatcher("registrarAsistencia.jsp").forward(request, response);

        } catch (Exception e) {
            // Error en la carga del formulario
            System.out.println("Error en mostrarFormRegistro:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar cursos: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=ver");
        }
    }

    /**
     * MOSTRAR PAGINA DE REPORTES ESTADISTICOS DE ASISTENCIAS
     */
    private void mostrarReportes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("reportesAsistencia.jsp").forward(request, response);
    }

    /**
     * REGISTRO GRUPAL DE ASISTENCIAS (MULTIPLES ALUMNOS SIMULTANEAMENTE)
     * 
     * Procesa el formulario con datos en formato JSON para registro eficiente
     */
    private void registrarAsistenciaGrupal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        System.out.println("INICIANDO REGISTRO GRUPAL");
        System.out.println("Parametros recibidos:");

        // Log detallado de todos los parametros recibidos
        request.getParameterMap().forEach((key, values) -> {
            if ("alumnos_json".equals(key)) {
                // Mostrar solo parte del JSON por logs (evita saturacion)
                String json = values[0];
                System.out.println(key + ": " + (json.length() > 200 ? json.substring(0, 200) + "..." : json));
            } else {
                System.out.println(key + ": " + String.join(", ", values));
            }
        });

        try {
            // Capturar datos del formulario
            int cursoId = Integer.parseInt(request.getParameter("curso_id"));
            int turnoId = Integer.parseInt(request.getParameter("turno_id"));
            String fecha = request.getParameter("fecha");
            String horaClase = request.getParameter("hora_clase");
            String alumnosJson = request.getParameter("alumnos_json"); // Datos en formato JSON

            // Verificacion robusta de la sesion del docente
            Profesor docente = (Profesor) session.getAttribute("docente");
            if (docente == null) {
                System.out.println("ERROR: No hay docente en sesion");
                // Log detallado de atributos de sesion para depuracion
                System.out.println("Atributos en sesion:");
                java.util.Enumeration<String> sessionAttrs = session.getAttributeNames();
                while (sessionAttrs.hasMoreElements()) {
                    String attrName = sessionAttrs.nextElement();
                    Object attrValue = session.getAttribute(attrName);
                    System.out.println(attrName + ": " + attrValue + " (tipo: "
                            + (attrValue != null ? attrValue.getClass().getName() : "null") + ")");
                }

                session.setAttribute("error", "Sesion expirada. Por favor inicie sesion nuevamente.");
                response.sendRedirect("index.jsp"); // Redirigir al login
                return;
            }

            // Obtener datos del docente para auditoria
            int registradoPor = docente.getId();
            String nombresDocente = docente.getNombres();
            String apellidosDocente = docente.getApellidos();

            System.out.println("Datos procesados:");
            System.out.println("cursoId: " + cursoId);
            System.out.println("turnoId: " + turnoId);
            System.out.println("fecha: " + fecha);
            System.out.println("horaClase: " + horaClase);
            System.out.println("registradoPor: " + registradoPor);
            System.out.println("docente en sesion: " + nombresDocente + " " + apellidosDocente + " (ID: " + registradoPor + ")");

            // Validar datos obligatorios
            if (alumnosJson == null || alumnosJson.isEmpty()) {
                System.out.println("ERROR: alumnos_json esta vacio");
                session.setAttribute("error", "No se recibieron datos de alumnos");
                response.sendRedirect("AsistenciaServlet?accion=registrar");
                return;
            }

            System.out.println("Llamando a AsistenciaDAO...");

            // Ejecutar registro grupal en base de datos
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            boolean resultado = asistenciaDAO.registrarAsistenciaGrupal(cursoId, turnoId, fecha, horaClase, alumnosJson, registradoPor);

            // Mostrar mensaje de resultado
            if (resultado) {
                System.out.println("Asistencias guardadas correctamente en la BD");
                session.setAttribute("mensaje", "Asistencias grupales registradas correctamente");
            } else {
                System.out.println("Error al guardar asistencias en la BD");
                session.setAttribute("error", "Error al registrar las asistencias grupales");
            }

            // Redirigir a la vista de consulta del curso
            System.out.println("Redirigiendo a: AsistenciaServlet?accion=verCurso&curso_id=" + cursoId + "&fecha=" + fecha);
            response.sendRedirect("AsistenciaServlet?accion=verCurso&curso_id=" + cursoId + "&fecha=" + fecha);

        } catch (NumberFormatException e) {
            // Error en el formateo de datos numericos
            System.out.println("ERROR: NumberFormatException en registrarAsistenciaGrupal:");
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error en el formato de los datos: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=registrar");
        } catch (Exception e) {
            // Error general en el procesamiento
            System.out.println("ERROR EXCEPCION en registrarAsistenciaGrupal:");
            System.out.println("Tipo: " + e.getClass().getName());
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error al registrar asistencias grupales: " + e.getMessage());
            response.sendRedirect("AsistenciaServlet?accion=registrar");
        }
    }
}