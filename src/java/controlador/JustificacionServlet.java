/*
 * SERVLET PARA GESTION DE JUSTIFICACIONES DE AUSENCIAS
 * 
 * Funcionalidades: Crear justificaciones, aprobar/rechazar (admin/docente), consulta padres
 * Roles: Padre (crear), Admin/Docente (aprobar/rechazar), Padre (consulta)
 * Integracion: Relacion con asistencias, alumnos y padres
 */
package controlador;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Justificacion;
import modelo.JustificacionDAO;
import modelo.Padre;
import modelo.Asistencia;
import modelo.AsistenciaDAO;

public class JustificacionServlet extends HttpServlet {

    /**
     * METODO GET - CONSULTAS Y NAVEGACION DE JUSTIFICACIONES
     * 
     * Acciones soportadas:
     * - form: Formulario para crear justificacion (padres)
     * - pending: Listar justificaciones pendientes (admin/docente)
     * - list: Listar justificaciones del alumno (padres)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "list"; // Accion por defecto: listar
        }

        try {
            switch (accion) {
                case "form":
                    mostrarFormJustificacion(request, response);
                    break;
                case "pending":
                    listarJustificacionesPendientes(request, response);
                    break;
                case "list":
                    listarJustificaciones(request, response);
                    break;
                default:
                    response.sendRedirect("dashboard.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Error interno del sistema: " + e.getMessage());
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * METODO POST - CREAR Y GESTIONAR JUSTIFICACIONES
     * 
     * Acciones soportadas:
     * - crear: Crear nueva justificacion (padres)
     * - aprobar: Aprobar justificacion (admin/docente)
     * - rechazar: Rechazar justificacion (admin/docente)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "crear"; // Accion por defecto: crear
        }

        try {
            switch (accion) {
                case "crear":
                    crearJustificacion(request, response);
                    break;
                case "aprobar":
                    aprobarJustificacion(request, response);
                    break;
                case "rechazar":
                    rechazarJustificacion(request, response);
                    break;
                default:
                    response.sendRedirect("JustificacionServlet?accion=list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * MOSTRAR FORMULARIO PARA CREAR JUSTIFICACION
     * 
     * Carga las ausencias del alumno que pueden ser justificadas
     */
    private void mostrarFormJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        if (padre == null) {
            System.out.println("ERROR: Padre es null en la sesion");
            session.setAttribute("error", "Sesion expirada. Por favor inicie sesion nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            System.out.println("Buscando ausencias para alumno_id: " + padre.getAlumnoId());
            System.out.println("Padre (username): " + padre.getUsername());
            System.out.println("Alumno: " + padre.getAlumnoNombre());

            // Obtener ausencias del alumno que pueden ser justificadas
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> ausencias = asistenciaDAO.obtenerAusenciasPorJustificar(padre.getAlumnoId());

            System.out.println("Numero de ausencias encontradas: " + ausencias.size());
            
            // Log detallado de ausencias
            for (Asistencia a : ausencias) {
                System.out.println("Ausencia: ID=" + a.getId() + 
                                 ", Fecha=" + a.getFecha() + 
                                 ", Curso=" + a.getCursoNombre() + 
                                 ", Estado=" + a.getEstado());
            }

            request.setAttribute("ausencias", ausencias);
            request.setAttribute("alumnoId", padre.getAlumnoId());
            request.getRequestDispatcher("justificarAusencia.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("ERROR en mostrarFormJustificacion:");
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar las ausencias: " + e.getMessage());
            response.sendRedirect("asistenciasPadre.jsp");
        }
    }

    /**
     * LISTAR JUSTIFICACIONES PENDIENTES (PARA ADMIN/DOCENTE)
     */
    private void listarJustificacionesPendientes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JustificacionDAO justificacionDAO = new JustificacionDAO();
        var justificaciones = justificacionDAO.obtenerJustificacionesPendientes();
        request.setAttribute("justificaciones", justificaciones);
        request.getRequestDispatcher("justificacionesPendientes.jsp").forward(request, response);
    }

    /**
     * LISTAR JUSTIFICACIONES DEL ALUMNO (PARA PADRES)
     */
    private void listarJustificaciones(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        if (padre != null) {
            JustificacionDAO justificacionDAO = new JustificacionDAO();
            var justificaciones = justificacionDAO.obtenerJustificacionesPorAlumno(padre.getAlumnoId());
            request.setAttribute("justificaciones", justificaciones);
        }

        request.getRequestDispatcher("justificacionesPadre.jsp").forward(request, response);
    }

    /**
     * CREAR NUEVA JUSTIFICACION
     */
    private void crearJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        if (padre == null) {
            session.setAttribute("error", "Sesion expirada. Por favor inicie sesion nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            int asistenciaId = Integer.parseInt(request.getParameter("asistencia_id"));
            String tipoJustificacion = request.getParameter("tipo_justificacion");
            String descripcion = request.getParameter("descripcion");

            System.out.println("Creando justificacion:");
            System.out.println("   Asistencia ID: " + asistenciaId);
            System.out.println("   Tipo: " + tipoJustificacion);
            System.out.println("   Descripcion: " + descripcion);
            System.out.println("   Justificado por: " + padre.getId());
            System.out.println("   Padre username: " + padre.getUsername());

            Justificacion justificacion = new Justificacion();
            justificacion.setAsistenciaId(asistenciaId);
            justificacion.setTipoJustificacion(tipoJustificacion);
            justificacion.setDescripcion(descripcion);
            justificacion.setJustificadoPor(padre.getId());

            JustificacionDAO justificacionDAO = new JustificacionDAO();
            boolean exito = justificacionDAO.crearJustificacion(justificacion);

            if (exito) {
                System.out.println("Justificacion creada exitosamente");
                session.setAttribute("mensaje", "Justificacion enviada correctamente");
                response.sendRedirect("AsistenciaServlet?accion=verPadre");
            } else {
                System.out.println("Error al crear justificacion");
                session.setAttribute("error", "Error al enviar la justificacion");
                response.sendRedirect("JustificacionServlet?accion=form");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Datos invalidos. Por favor verifique la informacion.");
            response.sendRedirect("JustificacionServlet?accion=form");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error interno al procesar la justificacion: " + e.getMessage());
            response.sendRedirect("JustificacionServlet?accion=form");
        }
    }

    /**
     * APROBAR JUSTIFICACION
     */
    private void aprobarJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int justificacionId = Integer.parseInt(request.getParameter("id"));
            String observaciones = request.getParameter("observaciones");

            JustificacionDAO justificacionDAO = new JustificacionDAO();
            boolean exito = justificacionDAO.aprobarJustificacion(justificacionId, 1, observaciones);

            if (exito) {
                session.setAttribute("mensaje", "Justificacion aprobada correctamente");
            } else {
                session.setAttribute("error", "Error al aprobar justificacion");
            }
            response.sendRedirect("JustificacionServlet?accion=pending");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar la aprobacion: " + e.getMessage());
            response.sendRedirect("JustificacionServlet?accion=pending");
        }
    }

    /**
     * RECHAZAR JUSTIFICACION
     */
    private void rechazarJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int justificacionId = Integer.parseInt(request.getParameter("id"));
            String observaciones = request.getParameter("observaciones");

            JustificacionDAO justificacionDAO = new JustificacionDAO();
            boolean exito = justificacionDAO.rechazarJustificacion(justificacionId, 1, observaciones);

            if (exito) {
                session.setAttribute("mensaje", "Justificacion rechazada correctamente");
            } else {
                session.setAttribute("error", "Error al rechazar justificacion");
            }
            response.sendRedirect("JustificacionServlet?accion=pending");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar el rechazo: " + e.getMessage());
            response.sendRedirect("JustificacionServlet?accion=pending");
        }
    }
}