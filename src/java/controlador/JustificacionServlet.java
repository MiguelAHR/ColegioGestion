package controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Justificacion;
import modelo.JustificacionDAO;
import modelo.Padre;

@WebServlet("/JustificacionServlet")
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
            response.sendRedirect("error.jsp");
        }
    }

    private void mostrarFormJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String alumnoId = request.getParameter("alumno_id");
        request.setAttribute("alumnoId", alumnoId);
        request.getRequestDispatcher("justificarAusencia.jsp").forward(request, response);
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
        
        if (padre != null) {
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
                response.sendRedirect("AsistenciaServlet?accion=verPadre&mensaje=Justificacion enviada");
            } else {
                response.sendRedirect("JustificacionServlet?accion=form&error=Error al enviar justificación");
            }
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    private void aprobarJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int justificacionId = Integer.parseInt(request.getParameter("id"));
        String observaciones = request.getParameter("observaciones");
        
        JustificacionDAO justificacionDAO = new JustificacionDAO();
        boolean exito = justificacionDAO.aprobarJustificacion(justificacionId, 1, observaciones); // 1 = ID del admin/profesor
        
        if (exito) {
            response.sendRedirect("JustificacionServlet?accion=pending&mensaje=Justificacion aprobada");
        } else {
            response.sendRedirect("JustificacionServlet?accion=pending&error=Error al aprobar justificación");
        }
    }

    private void rechazarJustificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int justificacionId = Integer.parseInt(request.getParameter("id"));
        String observaciones = request.getParameter("observaciones");
        
        JustificacionDAO justificacionDAO = new JustificacionDAO();
        boolean exito = justificacionDAO.rechazarJustificacion(justificacionId, 1, observaciones); // 1 = ID del admin/profesor
        
        if (exito) {
            response.sendRedirect("JustificacionServlet?accion=pending&mensaje=Justificacion rechazada");
        } else {
            response.sendRedirect("JustificacionServlet?accion=pending&error=Error al rechazar justificación");
        }
    }
}