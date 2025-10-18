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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "list";
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
            // Usar sesión para evitar problemas de URL
            HttpSession session = request.getSession();
            session.setAttribute("error", "Error interno del sistema");
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "crear";
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
            // Usar sesión para evitar problemas de URL
            HttpSession session = request.getSession();
            session.setAttribute("error", "Error al procesar la solicitud");
            response.sendRedirect("error.jsp");
        }
    }

    private void mostrarFormJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        if (padre == null) {
            System.out.println("❌ ERROR: Padre es null en la sesión");
            session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            System.out.println("🔍 Buscando ausencias para alumno_id: " + padre.getAlumnoId());

            // Cargar las ausencias del alumno para justificar
            AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
            List<Asistencia> ausencias = asistenciaDAO.obtenerAusenciasPorJustificar(padre.getAlumnoId());

            System.out.println("📊 Número de ausencias encontradas: " + ausencias.size());

            request.setAttribute("ausencias", ausencias);
            request.setAttribute("alumnoId", padre.getAlumnoId());
            request.getRequestDispatcher("justificarAusencia.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("❌ ERROR en mostrarFormJustificacion:");
            e.printStackTrace();
            // CORRECCIÓN: Usar la variable session existente, no crear una nueva
            session.setAttribute("error", "Error al cargar las ausencias. Por favor intente nuevamente.");
            response.sendRedirect("asistenciasPadre.jsp");
        }
    }

    private void listarJustificacionesPendientes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JustificacionDAO justificacionDAO = new JustificacionDAO();
        var justificaciones = justificacionDAO.obtenerJustificacionesPendientes();
        request.setAttribute("justificaciones", justificaciones);
        request.getRequestDispatcher("justificacionesPendientes.jsp").forward(request, response);
    }

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

    private void crearJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Padre padre = (Padre) session.getAttribute("padre");

        if (padre == null) {
            session.setAttribute("error", "Sesión expirada. Por favor inicie sesión nuevamente.");
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            int asistenciaId = Integer.parseInt(request.getParameter("asistencia_id"));
            String tipoJustificacion = request.getParameter("tipo_justificacion");
            String descripcion = request.getParameter("descripcion");

            Justificacion justificacion = new Justificacion();
            justificacion.setAsistenciaId(asistenciaId);
            justificacion.setTipoJustificacion(tipoJustificacion);
            justificacion.setDescripcion(descripcion);
            justificacion.setJustificadoPor(padre.getId());

            JustificacionDAO justificacionDAO = new JustificacionDAO();
            boolean exito = justificacionDAO.crearJustificacion(justificacion);

            if (exito) {
                session.setAttribute("mensaje", "Justificación enviada correctamente");
                response.sendRedirect("AsistenciaServlet?accion=verPadre");
            } else {
                session.setAttribute("error", "Error al enviar la justificación");
                response.sendRedirect("JustificacionServlet?accion=form");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Datos inválidos. Por favor verifique la información.");
            response.sendRedirect("JustificacionServlet?accion=form");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error interno al procesar la justificación");
            response.sendRedirect("JustificacionServlet?accion=form");
        }
    }

    private void aprobarJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int justificacionId = Integer.parseInt(request.getParameter("id"));
            String observaciones = request.getParameter("observaciones");

            JustificacionDAO justificacionDAO = new JustificacionDAO();
            boolean exito = justificacionDAO.aprobarJustificacion(justificacionId, 1, observaciones);

            if (exito) {
                session.setAttribute("mensaje", "Justificación aprobada correctamente");
            } else {
                session.setAttribute("error", "Error al aprobar justificación");
            }
            response.sendRedirect("JustificacionServlet?accion=pending");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar la aprobación");
            response.sendRedirect("JustificacionServlet?accion=pending");
        }
    }

    private void rechazarJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int justificacionId = Integer.parseInt(request.getParameter("id"));
            String observaciones = request.getParameter("observaciones");

            JustificacionDAO justificacionDAO = new JustificacionDAO();
            boolean exito = justificacionDAO.rechazarJustificacion(justificacionId, 1, observaciones);

            if (exito) {
                session.setAttribute("mensaje", "Justificación rechazada correctamente");
            } else {
                session.setAttribute("error", "Error al rechazar justificación");
            }
            response.sendRedirect("JustificacionServlet?accion=pending");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar el rechazo");
            response.sendRedirect("JustificacionServlet?accion=pending");
        }
    }
}